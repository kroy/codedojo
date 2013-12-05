//import kiron.lib.*;

public class BSTTester{
	public static void main(String [] args){
		Integer init = new Integer(10);
		BinarySearchTree tester = new BinarySearchTree(init);
		Integer j = new Integer(14);
		tester.insert(j);
		Integer k = new Integer(5);
		// tester.insert(k);
		// for(int i = 0; i<= 20; i++){
		// 	j = new Integer(i);
		// 	tester.insert(j);
		// }

		System.out.println(tester.delete(k));
		System.out.println(tester);
	}
}