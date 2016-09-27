import java.util.ArrayList;
import java.util.HashMap;

public class Part1 {
	HashMap<String, Node> masterMap;
	
	// For Making graph
	private class Edge {
		Node node;
		double probability;
		
		public Edge(Node node, double probability) {
			this.node = node;
			this.probability = probability;
		}
		
		public Node getNode() {
			return this.node;
		}
		public double getProbability() {
			return this.probability;
		}
	}
	private class Node {
		String word;
		HashMap<String, ArrayList<Edge>> posMap;
		
		public Node(String word, ArrayList<String> sentenceSpec) {
			this.word = word;
			this.posMap = new HashMap<String, ArrayList<Edge>>();
			
			for (String pos : sentenceSpec) {
				posMap.put(pos, new ArrayList<Edge>());
			}			
		}
		
		public String getWord() {
			return this.word;
		}
		public ArrayList<Edge> getEdges(String pos) {
			return posMap.get(pos);
		}
	}
	
	// For doing search
	private class Sequence {
		
	}
	private class Word {
		
	}
	
	private Node getNodeFromMaster(String word, ArrayList<String> sentenceSpec) {
		if (masterMap.get(word) == null) {
			Node newNode = new Node(word, sentenceSpec);
			masterMap.put(word, newNode);
			return newNode;			
		} else return masterMap.get(word);
	}
	
	/*
	 * startingWord - starting word
	 * sentenceSpec - list of parts-of-speech, i.e. ["NNP", "VBD", "DT", "NN"]
	 * graph - text of input.txt
	 */
	public String generate(String startingWord, ArrayList<String> sentenceSpec, String graph) {
		masterMap = new HashMap<String, Node>();
		
		ArrayList<String> lines = new ArrayList<String>();
		for (String line : lines) {
			String[] parts = line.split("//");
			String[] firstTag = parts[0].split("/");
			String[] secondTag = parts[1].split("/");
			double probability = Double.parseDouble(parts[3]);
			
			

		}
		
		return "";
	}
	
	public static void main(String[] args) {
		
	}
	
}
