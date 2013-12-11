import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.PriorityQueue;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

public class Graph{

	private class Vertex implements Comparable{
		Object node;	//the object represented by this vertex
						//an object should be represented by exactly one vertex. Object equality is determined by the equals() method
						//should add generics support
		LinkedList <Edge> incident;	//List of all edges incident to this vertex
		double distance;	//the distance to this node along the shortest path
		Vertex predecessor;	//predecessor vertex along the shortest path

		private Vertex(Object node){
			this.node = node;
			this.incident = new LinkedList <Edge> ();
			this.distance = Double.POSITIVE_INFINITY;
			this.predecessor = null;
		}

		private Vertex(Object node, LinkedList <Edge> incident){
			this(node);
			this.incident = incident;
		}

		private void addEdge(Edge e){
			incident.add(e);
		}

		private boolean equals(Vertex v){
			return this.node.equals(v.node);
		}

		public String toString(){
			return node.toString();
		}

		public int compareTo(Object v){
			if (!(v instanceof Vertex))	//this is so wrong, ya turkey
				return -1;
			if(this.distance < ((Vertex)v).distance)
				return -1;
			else if (this.distance == ((Vertex)v).distance)
				return 0;
			else
				return 1;
		}
	}

	private class Edge{
		Vertex u,v;
		double weight;

		private Edge(Vertex u, Vertex v, double weight){
			this.u = u;
			this.v = v;
			this.weight = weight;
		}

		private boolean equals(Edge k){
			return (this.u.equals(k.u) && this.v.equals(k.v)) || (this.v.equals(k.u) && this.u.equals(k.v));
		}

		private Vertex otherVertex(Vertex u){
			if(this.u.equals(u))
				return this.v;
			else if(this.v.equals(u))
				return this.u;
			else 
				return null;
		}

		public String toString(){
			return "("+u.toString()+", "+v.toString()+")";
		}
	}

	private HashMap <Object,Vertex> vertexList;	//turn this into a hashset
	private HashSet <Edge> edgeList;
	private int numVertices, numEdges;
	private boolean negativeEdges;

	public Graph(){
		vertexList = new HashMap <Object, Vertex> (100, .75f);
		edgeList = new HashSet <Edge> (40, .75f);
		numVertices = numEdges = 0;
		negativeEdges = false;
	}

	public boolean addVertex(Object node){
		Vertex u = vertexList.get(node);
		if(u == null){
			u = new Vertex(node);
			vertexList.put(node, u);
			numVertices++;
			return true;
		}
		return false;	//a vertex for that node exists already
	}

	public boolean addEdge(Object u, Object v, double weight){
		addVertex(u);
		addVertex(v);
		Vertex s = vertexList.get(u);
		Vertex t = vertexList.get(v);
		Edge e = new Edge(s,t,weight);
		if (edgeList.add(e)){
			s.addEdge(e);
			t.addEdge(e);
			numEdges++;
			return true;
		}
		else
			return false;
	}

	private Map <Object, Object []> dijkstras(Object s, Object t){
		HashSet <Vertex> unknown = new HashSet <Vertex> (vertexList.values());
		for(Iterator <Vertex> i = unknown.iterator(); i.hasNext();){
			Vertex v = i.next();
			v.distance = Double.POSITIVE_INFINITY;
			v.predecessor = null;
		}
		HashSet <Vertex> known = new HashSet <Vertex> ();
		Vertex start = vertexList.get(s);
		Vertex end = null;
		// if (t == null){
		// 	return null;
		// }
		if(t!=null)
			end = vertexList.get(t);
		start.distance = 0;
		PriorityQueue <Vertex> met = new PriorityQueue <Vertex> ();	//nodes we've met, but we don't have the shortest path to yet
		met.add(start);
		Vertex last = start;
		while((unknown.size()!=0 && met.size()!=0)){	//check for when last is null, and whether .equals(null) will throw an exception
			Vertex next = met.poll();
			for(Iterator <Edge> i = next.incident.iterator(); i.hasNext();){
				Edge e = i.next();
				Vertex v = e.otherVertex(next);
				if(v == null)
					return null;
				double candidateDist = next.distance + e.weight;
				if(candidateDist < v.distance){
					v.distance = candidateDist;
					v.predecessor = next;
					met.add(v);
				}
			}
			unknown.remove(next);
			known.add(next);
			last = next;
		}
		Map <Object, Object []> paths = new HashMap <Object, Object []> ();
		for(Iterator j = vertexList.values().iterator(); j.hasNext();){
			LinkedList <Object> shortPath = new LinkedList <Object> ();
			Object [] pathAndValue = new Object [2];
			last = (Vertex)j.next();
			pathAndValue[1] = last.distance;
			Vertex next = last;
			while(next != null){
				shortPath.addFirst(next.node);
				next = next.predecessor;
			}
			pathAndValue[0] = shortPath;
			paths.put(last.node, pathAndValue);
		}

		return paths;
	}

	public Object [] shortestPath(Object s, Object t){
		if(negativeEdges){
			return null;
		}
		return dijkstras(s, null).get(t);
	}

	public Map <Object, Object []> shortestPath(Object s){
		if(negativeEdges){
			return null;
		}
		else {
			return dijkstras(s, null);
		}
	}
}