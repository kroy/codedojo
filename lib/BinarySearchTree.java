//package kiron.lib;

public class BinarySearchTree{

	private Comparable root;	//should implement comparable and override the equals method in order for inserts/deletes/finds to make sense
	private BinarySearchTree left, right, parent;

	public BinarySearchTree(){
		root = new Integer(0);
		left = null;
		right = null;
		parent = null;
	}

	public BinarySearchTree(Comparable root){
		this.root = root;
		left = null;
		right = null;
		parent = null;
	}

	public BinarySearchTree(Comparable root, BinarySearchTree left, BinarySearchTree right){
		this(root);
		this.left = left;
		this.right = right;
	}

	/**
	*	Insert method. Inserts a binary tree into the curret binary tree at the apropriate position, except that 
	*	this doesn't really work. The tree we are inserting needs to be checked in order to ensure that all of its
	* 	child nodes are in the correct place in the parent trees.  For  now, t can only be a single-node tree.
	*
	*	@TODO make sure all elements in t are in the proper place in the parent tree (loop through all the subelements?)
	**/

	private void insert(BinarySearchTree t){
		if(t.root.compareTo(this.root) <= 0){//we're looking at the left subtree
			if(isLeaf()){
				this.left = t;
				t.parent = this;
			}
			else if(left==null)
				this.left = t;
			else
				this.left.insert(t);
		}

		else{
			if(isLeaf()){
				this.right = t;
				t.parent = this;
			}
			else if(this.right == null)
				this.right = t;
			else
				this.right.insert(t);
		}
	}

	/**
	*	Another insert method, in case I want to be able to hide the need to create a binary tree representing
	*	every node in the tree.
	*
	*	@return a boolean representing whether the insert was successful or not.  Just in case I want to actually add error checks
	**/
	public boolean insert(Comparable root){
		BinarySearchTree t = new BinarySearchTree(root);
		this.insert(t);
		return true;
	}

	/**
	*	@Args Comparable obj: the object to be deleted
	*
	*	The delete method checks if the object we are trying to delete should be in the left or right subtree, and, 
	*	if the proper subtree exists, sends a request to delete the object from that subtrees. In our first base
	*	case, the object we are trying to delete is the root of the tree we are currently examining. We need
	*	to determine whether the current root is a leaf, has one child, or has two children. If it is a leaf, we can go
	* 	ahead and delete the node by setting parent's reference to it to null. If it has one child, we can replace
	*	the node with its child.  If it has two, we need to do a rotation by finding the immediate in-order predecessor
	* 	and replacing the current node with it.
	*
	*	@return the object deleted or null if the object was not found
	*
	**/

	public Comparable delete(Comparable obj){
		Comparable deleted = null;
		if(obj.compareTo(this.root)<0 && this.left != null){	//this logic is a little janky.  Maybe fix it
			deleted = this.left.delete(obj);
		}
		else if(obj.compareTo(this.root)>0 && this.right != null){
			deleted = this.right.delete(obj);
		}
		else if (obj.compareTo(this.root) == 0){
			if(this.isLeaf()){
				if(this.parent != null){//this is not the root for the tree
					parent.replaceChild(this, null);
					deleted = this.root;
					this.parent = null;
				}
				else{//this is the root node for the entire tree
					deleted = this.root;
				}
			}
			else if(this.left == null){
				deleted = this.root;
				parent.replaceChild(this, this.right);
			}
			else if(this.right == null){
				deleted = this.root;
				parent.replaceChild(this, this.left);
			}
			else{//otherwise this has two children
				BinarySearchTree predecessor = this.left.findMaxTree();
				deleted = this.root;
				this.root = predecessor.root;
				predecessor.delete(predecessor.root);	//delete the predecessor from the tree rooted at predecessor (in case it has a left sub-tree)

			}
		}
		return deleted;
	}

	public Comparable get(Comparable seeking){
		Comparable sought = null;
		if(this.root.compareTo(seeking) == 0)
			sought = this.root;
		else if(this.root.compareTo(seeking) < 0 && this.left != null)
			sought = this.left.get(seeking);
		else if(this.root.compareTo(seeking)>0 && this.right!=null)
			sought = this.right.get(seeking);
		return sought;
	}

	private BinarySearchTree findMinTree(){
		BinarySearchTree curr = this;
		while(!curr.isLeaf()){
			curr = curr.getLeft();
		}
		return curr;
	}

	private BinarySearchTree findMaxTree(){
		BinarySearchTree curr = this;
		while(!curr.isLeaf()){
			curr = curr.getRight();
		}
		return curr;
	}

	public Comparable findMin(){
		return this.findMinTree().root;
	}

	public Comparable findMax(){
		return this.findMaxTree().root;
	}

	/**
	*	Helper Methods
	**/

	/**
	*	@Args succ: the curent child of the tree we are looking at.
	*	@Args newSucc: the new child we want to replace succ with.  Can be null.
	*
	*/

	private void replaceChild(BinarySearchTree succ, BinarySearchTree newSucc){
		if(succ.root.compareTo(this.root) <0){
			this.left = newSucc;
		}
		else{
			this.right = newSucc;
		}
	}

	private boolean isLeaf(){
		return this.left == null && this.right == null;
	}

	private void restructure(){

	}

	/**
	*	Mutator methods blargh
	*
	*/

	public void setLeft(BinarySearchTree t){
		this.left = t;
	}

	public void setRight(BinarySearchTree t){
		this.right = t;
	}

	public BinarySearchTree getLeft(){
		return this.left;
	}

	public BinarySearchTree getRight(){
		return this.right;
	}

	/**
	*	Returns a string listing all the nodes in the tree in order of their value.
	*	Could get quite messy with complicated node members.
	*
	*/

	public String inOrder(){
		if(this.isLeaf())
			return this.root.toString();
		else if (this.left == null)
			return this.root.toString() + " " + right.inOrder();
		else if (this.right == null)
			return this.left.inOrder() + " " + this.root.toString();
		return this.left.inOrder() + " " + this.root.toString() + " " + this.right.inOrder();
	}

	public String toString(){ //make this prettier
		return this.inOrder();
	}
}