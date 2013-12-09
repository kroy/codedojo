public class GraphTester{
	public static void main(String [] args){
		Graph g = new Graph();
		Integer u = new Integer(5);
		Integer v = new Integer(6);
		g.addVertex(u);
		g.addEdge(u,v,12);
		g.addEdge(u, new Integer(4), 4);
		g.addEdge(new Integer(4), v, 3);
		System.out.println(g.shortestPath(u,v));
	}
}