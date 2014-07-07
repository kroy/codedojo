import java.io.*;
import java.util.HashSet;
import java.util.HashMap;
import java.util.Scanner;
import java.util.ArrayList;

/**
* 	KeywordFinder.java -- A primitive keyword finding java program that parses a text file
*		and returns the keywords for that file, ignoring a specific set of words.
*		
*		Right now, "keywords" are defined as the set of words which occurs most frequently.
*		Eventually I'd like to refine the notion of keyword.
*
*		@author kroy
*
**/

public class KeywordFinder{
	private String filename; 	//could be the name of a file on the filesystem, or a url
	private File file;
	private Scanner fin;		//scanner for the file
	//want to add support for opening and reading urls

	private HashMap <String, Integer> wordFrequency; //a hashmap of the frequency of each word in the file	
	private int numTopWords;		//The number of keywords to return
	private int currTopWords;		//The number of keywords currently being tracked in the top words array 
	private String [] topWords;		//An array of the top numTopWords words in order

	private int numKeywords;
	private HashSet <String> ignore;
	private ArrayList <String> keywords;

	public static final String DEFAULT_IGNORE = "and the but or if can to a I of my as at in that"; 

	/**
	* Main constructor for Keyword Finders. Uses the default ignore list, and returns the default number of keywords (10).
	* Initializes the file and the scanner on that file.
	*
	* 	TODO: Factor out some things
	*		transform filename into a proper uri or filepath
	* 		allow variable number of arguments
	*
	* 	@param filename - the name of the file to process
	**/

	public KeywordFinder(String filename) throws FileNotFoundException{
		this.filename = filename;
		initializeFile();

		ignore = new HashSet <String> ();
		buildIgnore(DEFAULT_IGNORE);//make this into a regex maybe

		numKeywords = 10;
		numTopWords = numKeywords;			//factor out
		currTopWords = 0;
		topWords = new String [numTopWords]; 	//factor out
		wordFrequency = new HashMap <String,Integer> ();
	}

	/**
	* Constructor which allows the user to provide a custom list of words to ignore.
	*
	* @param filename - the name of the file to process
	* @param ignoreWords - a space separated list of words which the keyword processor should ignore.
	**/

	public KeywordFinder(String filename, String ignoreWords) throws FileNotFoundException{
		this(filename);
		ignore = new HashSet <String> ();
		buildIgnore(ignoreWords);
	}

	/**
	* This method processes the full file and returns an array of the top keywords in the file
	* in descending order of precedence (in this case, number of occurences of each word)
	*
	* @return an array of keywords in descending order of precedence
	**/

	public String[] process(){
		//process the full file
		// TODO: check for errors, special cases, ignore punctuations at the end of tokens
		
		while(fin.hasNext()){	//this needs to be refined so that tokens don't include punctuation/quotation marks
			String token = fin.next();
			if(!ignore.contains(token)){
				updateFrequency(token); //may want to add error handling here
			}
		}
		return topWords;
	}

	public String[] process(int count){
		/* TODO */
		return null;
	}

	private void initializeFile() throws FileNotFoundException{
		//Create File object, initialize Scanner
		//might want to check for errors here too
		file = new File(filename);
		fin = new Scanner(file);
	}

	private void buildIgnore(String ignoreWords){
		//Create the HashSet of words to ignore
		String [] words = ignoreWords.split(" ");
		for(int i=0; i < words.length; i++){
			if(words[i] != "")
				ignore.add(words[i]);
		}
	}

	/**
	* updateFrequency does all the work to update the count of each word, as well as maintain the array of 
	* top words.
	*
	* TODO: make this less brittle
	**/

	private void updateFrequency(String token){
		int currCount = 0;
		if(wordFrequency.get(token)!= null){//word is contained in the frequency list already
			currCount = wordFrequency.get(token);
		}
		wordFrequency.put(token, ++currCount);

		int pivot = currTopWords;	//the index of the top words array after which all words are guaranteed to stay in place (after insertion of token)
		int correctSlot = currTopWords; // correct index for token
		while(correctSlot > 0){//find the correct place to put token in the top words list
			if(topWords[correctSlot] != null && currCount < wordFrequency.get(topWords[correctSlot-1])) //doesn't work cause last slot will always get replaced
				break;
			if(topWords[correctSlot] != null && topWords[correctSlot-1].equals(token))
				pivot = correctSlot-1;
			correctSlot--;
		}
		if(pivot == currTopWords && currTopWords < 9)
			currTopWords++;

		while(pivot > correctSlot){
			topWords[pivot] = topWords[--pivot];
		}

		topWords[correctSlot] = token;
	}

}