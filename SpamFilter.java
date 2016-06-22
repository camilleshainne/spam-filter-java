//import java.io.*;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Hashtable;

public class SpamFilter{
	
	BagOfWords spam;
	BagOfWords ham;
	Hashtable<String, Double> message = new Hashtable<String, Double>();
	Hashtable<String, String> type = new Hashtable<String, String>();
	int totalSize, totalLines, k;

	public SpamFilter(){
		spam = new BagOfWords("spam.txt");
		ham = new BagOfWords("ham.txt");
		totalSize = this.getDictionarySize();
		totalLines = spam.lines + ham.lines;
	}

	public void filterSpam(File file){
		ArrayList<String> input = this.loadFile(file);

		double
			pWs = 1.0, 		// P(message|spam)
			pWh = 1.0, 		// P(message|ham)		
			pSpam = (double)(spam.lines + this.k) / (double)(totalLines + (2*k)),		// P(spam)
			pHam = (double)(ham.lines + this.k) / (double)(totalLines + (2*k)),			// P(ham)
			pSm = 0.0; 		// P(spam|message)

		for(String msg : input){
			int num = 0;
			String[] string = msg.split(" ");		// split message

			for(String m : string){					// checks if word is new
				if(!spam.bagOfWords.containsKey(m) && !ham.bagOfWords.containsKey(m)) num++; 
			}

			for(String m : string){
				int s, h;

				if(spam.bagOfWords.get(m) == null) s=0;
				else s = spam.bagOfWords.get(m);
				pWs *= (double)(s + this.k) / (double)(spam.totalWords + (this.k * (totalSize + num))); // P(message|spam)

				if(ham.bagOfWords.get(m) == null) h=0;
				else h = ham.bagOfWords.get(m);
				pWh *= (double)(h + this.k) / (double)(ham.totalWords + (this.k * (totalSize + num)));	// P(message|ham)
			}

			pSm = (pWs * pSpam) / ((pWs * pSpam) + (pWh * pHam));	// P(spam|message)
			message.put(msg, pSm);
		}

	}

	public int getDictionarySize(){
		int dictionarySize = spam.dictionarySize;

		for(String key : ham.bagOfWords.keySet()){
			if(!spam.bagOfWords.containsKey(key)){
				dictionarySize++;
			}
		}

		return dictionarySize;
	}

	public ArrayList<String> loadFile(File file){
		ArrayList<String> msg = new ArrayList<String>();

		try{
			FileReader read = new FileReader(file);
			BufferedReader buffer = new BufferedReader(read);
			String word = buffer.readLine();
			this.k = Integer.parseInt(word);

			while(word != null){
				word = buffer.readLine();
				if(word == null) break;
				word = word.replaceAll("[^a-zA-Z0-9 ]", "");
				msg.add(word.toLowerCase());
			}

			buffer.close();

		} catch(Exception e){
			e.printStackTrace();
			return null;
		}

		return msg;
	}

	public void filter(){
		for(String key : this.message.keySet()){
			if(this.message.get(key) > 0.5) this.type.put(key, "SPAM");
			else this.type.put(key, "HAM");
		}
	}

	public boolean saveOutput(){
		try{
			File file = new File("output.txt");
			if(!file.exists()) file.createNewFile();

			FileWriter writer = new FileWriter(file.getAbsoluteFile());
			BufferedWriter buffer = new BufferedWriter(writer);

			for(String key : this.type.keySet()){
				buffer.write(key + " " + this.type.get(key) + "\n");
			}

			buffer.close();
			return true;
		} catch(Exception e){
			e.printStackTrace();
			return false;
		}
	}

	public static void main(String[] args){
		SpamFilter spamfilter = new SpamFilter();
		spamfilter.filterSpam(new File(args[0])); // input file
		spamfilter.filter();
		spamfilter.saveOutput();
	}

}
