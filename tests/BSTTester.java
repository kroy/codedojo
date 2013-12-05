//import kiron.lib.*;

public class BSTTester{
	public static void main(String [] args){
		BinaryTree tester = new BinaryTree(new Integer(10));
		for(int i = 0; i<= 20; i++)
			tester.insert(new Integer(i));
		System.out.println(tester);
	}
}