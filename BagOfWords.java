//import java.io.*;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.File;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.Scanner;

public class BagOfWords{

	Hashtable<String, Integer> bagOfWords = new Hashtable<String, Integer>();	// word, freq
	ArrayList<String> words = new ArrayList<String>();							// list
	int lines, dictionarySize, totalWords;


	public BagOfWords(String input){
		try{
			loadWords(new File(input));
		} catch(Exception e){
			e.printStackTrace();
		}

		bagOfWords = listWords(words);
		dictionarySize = bagOfWords.keySet().size();
		totalWords = words.size();
	}

	public boolean loadWords(File file){
		try{
			Scanner scanner = new Scanner(file);
			
			while(scanner.hasNext()){
				String addWord = scanner.next();
				addWord = addWord.replaceAll("[^a-zA-Z0-9 ]", "");
				words.add(addWord.toLowerCase());
			}

			scanner.close();
		} catch(Exception e){
			e.printStackTrace();
			return false;
		}

		try{
			FileReader read = new FileReader(file);
			BufferedReader buffer = new BufferedReader(read);

			while(buffer.readLine() != null){					// while file is not empty
				this.lines++;
			}

			buffer.close();
		} catch(Exception e){
			e.printStackTrace();
			return false;
		}

		return true;

	}

	public Hashtable<String, Integer> listWords(ArrayList<String> w){
		Hashtable<String, Integer> table = new Hashtable<String, Integer>();
		
		for(String s: w){
			if(table.containsKey(s)){
				table.put(s, table.get(s)+1); // new word
			} else{
				table.put(s, 1);
			}
		}

		return table;
	}
		
}
