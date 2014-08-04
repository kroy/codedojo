import java.util.HashMap;
import java.util.Scanner;
import java.util.HashSet;
import java.util.Iterator;

public class AnagramFinder{
	public static void main(String[] args){
		if (args.length < 1)
			printAnagrams("Resistance");
		else{
			//first check that the string is alpha-numeric
			printAnagrams(args[0]);
		}
	}

	private static void printAnagrams(String base){
		HashSet <String> anagrams = findAnagrams(base);
		try{
			Iterator <String> i = anagrams.iterator();
			while(i.hasNext())
				System.out.println(i);
		}
		catch (Exception e){
			System.err.println(e.getMessage());
		}
	}

	private static HashSet <String> findAnagrams(String base){
		int [] available = sortString(base); //an array of the count of each letter found in base
		// for(int i = 0; i < available.length; i++)
		// 	System.out.println(available[i]);
		return null;
	}

	private static int [] sortString(String s){
		int [] buckets = new int [26];
		for(int i=0; i < s.length(); i++){
			//for each character, increment the appropriate slot by one
			//ignores spaces and punctuations
			//need to modify this in order to handle utf strings
			char c = s.charAt(i);
			if(c>=65 && c<=90){ //c is an uppercase ASCII char
				c-=65;	//normalize c to a number in [0,25]
			}
			if(c>=97 && c<=122){ //c is a lowercase ASCII char
				c-=97;	//normalize c to a number in [0,25]
			}
			buckets[c]++;
		}
		return buckets;
	}
}