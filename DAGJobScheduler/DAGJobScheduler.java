import java.util.Scanner;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.PriorityQueue;
import java.util.ListIterator;
import java.io.File;

/**
*	This program is a code exercise I implemented in order to brush up on my algorithmic $killz.
*	It takes a file representation of a Directed Acyclic Graph (DAG), builds a data-structure
* 	representation of the graph, and gives you some information about the DAG (currently only the 
*	minimum completion time of all of the nodes in the graph).
*
*	@AUTHOR kroy (github.com/kroy)
*
**/
public class DAGJobScheduler{

	private class Vertex{
		int key; 			//key into the adjacency list for this vertex
		double weight; 		//cost of performing the action at this vertex
		double distance; 	//distance from the start along the shortest path.  Equivalent to the completion time of a particular node
		LinkedList <Vertex> adjacencyListIn;
		LinkedList <Vertex> adjacencyListOut;
		boolean visited;

		public Vertex(int key){
			this.key = key;
			this.weight = -1;
			this.distance = 0;
			adjacencyListOut = new LinkedList <Vertex> ();
			adjacencyListIn = new LinkedList <Vertex> ();
			visited = false;
		}

		public String toString(){
			return ""+this.key;
		}

		public boolean equals(Vertex v){
			return this.key == v.key;
		}
	}

	/**
	*	The DAG class. Each node u represents a task that needs to be completed. An edge between
	*	two nodes (u,v) indicates that u must be completed before v. Nodes must have non-negative
	* 	completion times and the graph should be acyclic, because that's in the name.
	*
	* 	The graph itself is simply a hash of vertices. I chose a hash because lookups are fast, and,
	* 	for this application, we never need to list all of the nodes in the graph. However, if 
	* 	space is a concern I may switch to a different data structure.  The "start" vertex is
	* 	simply the first vertex listed in the file representation of the graph, which is a problem.
	* 	The end vertex is a dummy sink with 0 weight, created in order to catch all flow from nodes
	* 	without successors.
	*	
	**/

	private class DAG{
		HashMap<Integer, Vertex> vertexList;//hashmap of all of the vertices in the graph
		Vertex start; 		//start vertex of the DAG, could also be represented as an int
		Vertex end;			//end vertex of the DAG.  An artificial sink, whose key is -1

		public DAG(String fname){
			vertexList = new HashMap <Integer,Vertex> ();
			start = null;
			end = new Vertex(-1);
			end.weight = 0;
			buildVertices(fname);
		}

		/**
		*	buildVertices(fname) takes in the name of a file containing a representation of the
		* 	adjacency list, then builds our adjacency list and initializes the start and
		*	end vertices. The end vertex is an artificial sink with weight 0 in order to collect
		*	completion times for all nodes which do not have successors.
		*
		*	Each line in the file should be in the format:
		*	
		*	<vertex key>, <vertex weight>: <ancestor vertex key 1>, <ancestor vertex key 2>,...,<ancestor vertex key n>
		*	#denotes a comment line
		*
		**/

		private void buildVertices(String fname){
			File f = null;
			Scanner fin = null;
			try{
				f = new File(fname);		//add some inspection of the filename
				fin = new Scanner(f);
			}
			catch(Exception e){
				System.out.println("Failed to open input file. Failed with error: " + e.getMessage());
				System.exit(0);
			}

			while(fin.hasNextLine()){
				String line = fin.nextLine();
				line = line.trim();
				if (line.charAt(0) == '#'){
					//do nothing, this is a comment line
				}
				else{ //this is a line representing a vertex and 
					try {
						String [] pieces = line.split(":");
						String [] newVertex = pieces[0].split(",");
						Vertex u = vertexList.get(Integer.parseInt(newVertex[0].trim()));
						if(u==null){ 	//if the vertex doesn't already exist, create it
							u = new Vertex(Integer.parseInt(newVertex[0].trim()));
							vertexList.put(Integer.parseInt(newVertex[0].trim()),u);
						}
						u.weight = Integer.parseInt(newVertex[1].trim());	//add a check for negative weights
						if(u.key==0){//if this is the first vertex, set it as the start and set its distance to its weight
							start = u;
							u.distance = u.weight;
						}
						if(pieces.length>1){ //if there are successor vertices
							String [] adjacentVertices = pieces[1].split(",");
							for(int i=0; i<adjacentVertices.length; i++){
								Integer outNode = Integer.valueOf(adjacentVertices[i].trim());
								Vertex outVertex = vertexList.get(outNode); 	//try to find a vertex representation of this successor
								if(outVertex == null){	// if there isn't a vtx representation of the successor, create one
									outVertex = new Vertex((int)outNode);
									vertexList.put(outNode, outVertex);
								}
								u.adjacencyListOut.add(outVertex); //add the successor to the successor list of u
								outVertex.adjacencyListIn.add(u);	//add u to the parent list of outvertex
							}
						}
						else{
							u.adjacencyListOut.add(end);
							end.adjacencyListIn.add(u);
						}
						// System.out.println("In vertices: " + u.adjacencyListIn);
						// System.out.println("Out vertices: " + u.adjacencyListOut);
					}
					catch(Exception e){
						System.out.println("File parse failed with error: " + e);
					}
				}
			}
			
		}

		/**
		*	topoSort() performs a topological sort on the DAG.  This can only be performed once per
		*	graph instance because vertices' inbound adjacency lists are destroyed by the sort.
		* 	The basic logic of the sort is to start with the "start" node s and slice all outbound edges
		*  	 (s,v) from it and add s to the sorted list. For each v that no longer has any inbound edges, 
		*	we repeat this process until there are no more vertices to sort, or until we find a cycle.
		*
		**/

		private LinkedList <Vertex> topoSort(){
			LinkedList <Vertex> sourceless = new LinkedList <Vertex> ();	//a queue of all of the element whose predecessors have already been sorted
			LinkedList <Vertex> sorted = new LinkedList <Vertex> ();
			sourceless.add(start);
			Vertex u = sourceless.poll();
			while(u!=null){
				if(u.visited){
					System.out.println("Sorry, this graph has a cycle.  Exiting");
					System.exit(0);
				}
				sorted.add(u);
				u.visited = true;
				//System.out.println("Considering vertex: " + u);
				ListIterator <Vertex> i = u.adjacencyListOut.listIterator(0);
				while(i.hasNext()){
					Vertex v = i.next();
					//System.out.println("Looking at connection: " + u + ", " + v );
					v.adjacencyListIn.remove(u);
					if(v.adjacencyListIn.isEmpty())
						sourceless.add(v);
				}
				u=sourceless.poll();	
			}

			return sorted;
		}

		/**
		*	minCompletionTime() analyzes the DAG and returns the minimum completion time of the tasks
		*	in the graph, assuming we have unlimited workers working in parallel. First a topological sort is
		* 	performed on the graph.  The sorted list of vertices is then stepped through.  For each vertex u,
		* 	each outbound edge from u, (u,v), is examined and the completion time for node v is set to the
		* 	maximum of the current distance to v and the distance to u plus the weight of v.
		*
		* 	@return end.distance: The time it takes to complete all of the tasks in the graph
		*
		**/

		private double minCompletionTime(){
			LinkedList <Vertex> sorted = topoSort();
			System.out.println(sorted);
			//u.dstance = u.weight;	//the completion time of the first vertex is the cost of the task itself
			while(sorted.peek()!= null){
				Vertex u = sorted.poll();
				if(u.weight<0){
					System.out.println("Vertex " + u + " was not initialized properly");
					System.exit(0);
				}
				ListIterator <Vertex> i = u.adjacencyListOut.listIterator(0);
				while(i.hasNext()){
					Vertex v = i.next();
					// System.out.println(v);
					// System.out.println("u: " + u.distance + " v: " + v.distance);
					v.distance = Math.max(v.distance, u.distance+v.weight);
				}
			}
			return end.distance;
		}
	}

	public static void main(String [] args){
		DAGJobScheduler tester = new DAGJobScheduler();
		DAGJobScheduler.DAG test = tester.new DAG("test.in");
		//System.out.println(test.topoSort());
		System.out.println(test.minCompletionTime());
	}



}