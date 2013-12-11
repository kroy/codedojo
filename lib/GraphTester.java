public class GraphTester{
	public static void main(String [] args){
		Graph g = new Graph();
		String s = "s";
		String t = "t";
		g.addVertex(s);
		g.addEdge(s,t,12);
		g.addEdge(s, "u", 4);
		g.addEdge("u", t, 3);
		g.addEdge("u", "v", 1);
		g.addEdge("v", t, 1);
		System.out.println(g.shortestPath("u",t).values());
	}
}