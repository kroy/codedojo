import java.io.*;

public class ParseFile {
	public static void main(String[] args) throws FileNotFoundException{
		KeywordFinder k = new KeywordFinder("test.in", "the but or if can to a I of my as at in that");
		String [] keywords = k.process();
		for(int i=0; i<keywords.length; i++)
			System.out.print(keywords[i] + ", ");
	}
}