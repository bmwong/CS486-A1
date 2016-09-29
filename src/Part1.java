import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Queue;

public class Part1 {
	static HashMap<String, Node> masterMap;
	
	// For Making graph
	static class Edge {
		String beforePos;
		Node node;
		double probability;
		
		public Edge(String beforePos, Node node, double probability) {
			this.beforePos = beforePos;
			this.node = node;
			this.probability = probability;
		}
		
		public String getBeforePos() {
			return this.beforePos;
		}
		public Node getNode() {
			return this.node;
		}
		public double getProbability() {
			return this.probability;
		}
	}
	static class Node {
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
		public void addEdge(String pos1, String pos2, Node node, double probability) {
			Edge edge = new Edge(pos1, node, probability);
			posMap.get(pos2).add(edge);
		}
		
		public String toString() {
			
			String output = word + "\n";
			for (ArrayList<Edge> edges : posMap.values()) {
				for (Edge edge : edges) {
					output += "(" + edge.getProbability() + " - " + edge.getNode().getWord() + "), ";
				}
			}
			return output;
		}
		
	}
	
	boolean isValidSequence(Sequence sequence, ArrayList<String> sentenceSpec) {
		
		return false;
	}
	
	// For doing search
	static class Sequence {
		ArrayList<Word> words;

		public Sequence() {
			this.words = new ArrayList<Word>();
		}
		private Sequence(ArrayList<Word> words) {
			this.words = new ArrayList<Word>(words);
		}
		
		public int size() {
			return words.size();
		}
		public Word getLastWord() {
			return words.get(words.size() - 1);
		}
		public boolean addWord(String word, String pos, double probability, ArrayList<String> sentenceSpec) {
			if (sentenceSpec.size() > words.size() || sentenceSpec.get(words.size()).equals(pos)) {
				this.words.add(new Word(word, pos, probability));
				return true;
			}
			return false;
		}
		public Sequence copy() {
			return new Sequence(this.words);
		}		
		public double getTotalProbability() {
			return 0;
		}
	}
	static class Word {
		String text;
		String pos;
		double probability;
		
		public Word(String text, String pos, double probability) {
			this.text = text;
			this.pos = pos;
			this.probability = probability;
		}
		
		public String getText() {
			return this.text;
		}
		public String getPos() {
			return this.pos;
		}
		public double getProbability() {
			return this.probability;
		}
		
	}
	
	private static Node getNodeFromMaster(String word, ArrayList<String> sentenceSpec) {
		if (masterMap.get(word) == null) {
			Node newNode = new Node(word, sentenceSpec);
			masterMap.put(word, newNode);
			return newNode;			
		} else return masterMap.get(word);
	}
	
	public static String bfs(Node root, ArrayList<String> sentenceSpec) {
		Sequence rootSeq = new Sequence();
		rootSeq.addWord(root.getWord(), sentenceSpec.get(0), 1, sentenceSpec);
		Queue queue = new LinkedList();
		queue.add(rootSeq);
		while(!queue.isEmpty()) {
			Sequence seq = (Sequence)queue.remove();
			Word word = seq.getLastWord();
			Node node = masterMap.get(word.getText());
			
			for (Edge edge : node.getEdges(sentenceSpec.get(seq.size()))) {
				// HERE
			}
		}
		return "";
	}
	
	/*
	 * startingWord - starting word
	 * sentenceSpec - list of parts-of-speech, i.e. ["NNP", "VBD", "DT", "NN"]
	 * graph - text of input.txt
	 */
	public static String generate(String startingWord, ArrayList<String> sentenceSpec, String graph) {
		masterMap = new HashMap<String, Node>();
				
		for (String line : graph.split("\n")) {
			String[] parts = line.split("//");
			String[] firstTag = parts[0].split("/");
			String[] secondTag = parts[1].split("/");
			double probability = Double.parseDouble(parts[2]);
			
			Node n1 = getNodeFromMaster(firstTag[0], sentenceSpec);
			Node n2 = getNodeFromMaster(secondTag[0], sentenceSpec);
			String pos1 = firstTag[1];
			String pos2 = secondTag[1];
			
			if (sentenceSpec.contains(pos1) && sentenceSpec.contains(pos2)) {
				n1.addEdge(pos1, pos2, n2, probability);
			}
		}
		return masterMap.get(startingWord).toString();
	}
	
	public static void main(String[] args) {
		try {
			byte[] encoded = Files.readAllBytes(Paths.get("test.txt"));
			
			String startingWord = "benjamin";
			String graph = new String(encoded, StandardCharsets.UTF_8);
			ArrayList<String> sentenceSpec = new ArrayList<String>() {{
				add("NNP");
				add("VBD");
				add("DT");
				add("NN");
			}};
			
			System.out.println(generate(startingWord, sentenceSpec, graph));
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	
}
