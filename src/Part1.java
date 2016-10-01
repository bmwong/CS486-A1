import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Locale;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;

public class Part1 {
	static HashMap<String, Node> masterMap;

	// For parsing
	static class Edge {
		String prevPos;
		Node node;
		double probability;
		
		public Edge(String prevPos, Node node, double probability) {
			this.prevPos = prevPos;
			this.node = node;
			this.probability = probability;
		}
		
		public String getPrevPos() { return this.prevPos; }
		public Node getNode() { return this.node; }
		public double getProbability() { return this.probability; }
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
		
		public String getWord() { return this.word; }
		public ArrayList<Edge> getEdges(String pos) {
			return posMap.get(pos);
		}

		public void addEdge(String pos1, String pos2, Node node, double probability) {
			Edge edge = new Edge(pos1, node, probability);
			posMap.get(pos2).add(edge);
		}

		// For debugging purposes
		public String toString() {
			String output = word + ": ";
			for (ArrayList<Edge> edges : posMap.values()) {
				for (Edge edge : edges) {
					output += "(" + edge.getPrevPos() + " - " + edge.getProbability() + " - " + edge.getNode().getWord() + "), ";
				}
			}
			return output;
		}
		
	}
	
	// For searching
	static class Word {
		String text;
		String pos;
		double probability;

		public Word(String text, String pos, double probability) {
			this.text = text;
			this.pos = pos;
			this.probability = probability;
		}

		public String getText() { return this.text; }
		public String getPos() { return this.pos; }
		public double getProbability() { return this.probability; }

	}
	static class Sequence {
		ArrayList<Word> words;

		public Sequence() {
			this.words = new ArrayList<Word>();
		}
		private Sequence(ArrayList<Word> words) {
			this.words = new ArrayList<Word>(words);
		}

		public Sequence copy() {
			return new Sequence(this.words);
		}

		public int size() {
			return words.size();
		}

		public Word getLastWord() {
			return words.get(words.size() - 1);
		}

		public String getSentence(){
			ArrayList<String> texts = new ArrayList<String>();
			for (Word word : words) {
				texts.add(word.getText());
			}
			return String.join(" ", texts);
		}

		public double getTotalProbability() {
			double probability = 1;
			for (Word word : words) {
				probability *= word.getProbability();
			}
			return probability;
		}

		public boolean addWord(String word, String pos, double probability, ArrayList<String> sentenceSpec) {
			if (words.size() < sentenceSpec.size() && sentenceSpec.get(words.size()).equals(pos)) {
				this.words.add(new Word(word, pos, probability));
				return true;
			}
			return false;
		}
	}
	
	private static Node getNodeFromMaster(String word, ArrayList<String> sentenceSpec) {
		if (masterMap.get(word) == null) {
			Node newNode = new Node(word, sentenceSpec);
			masterMap.put(word, newNode);
			return newNode;			
		} 
		else { return masterMap.get(word); }
	}
	
	public static String bfs(Node root, ArrayList<String> sentenceSpec) {
		ArrayList<Sequence> validSequences = new ArrayList<Sequence>();
		int nodesConsidered = 0;
		Sequence rootSeq = new Sequence();
		rootSeq.addWord(root.getWord(), sentenceSpec.get(0), 1, sentenceSpec);
		Queue queue = new LinkedList();
		queue.add(rootSeq);

		while(!queue.isEmpty()) {
			Sequence seq = (Sequence)queue.remove();
			if (seq.size() == sentenceSpec.size()) {
				validSequences.add(seq);
				continue;
			}

			Word word = seq.getLastWord();
			Node node = masterMap.get(word.getText());
			String nextPos = sentenceSpec.get(seq.size());
			for (Edge edge : node.getEdges(nextPos)) {
				nodesConsidered++;
				if (edge.getPrevPos().equals(word.getPos())) {
					Sequence newSeq = seq.copy();
					Node nextNode = edge.getNode();
					if (newSeq.addWord(nextNode.getWord(), nextPos, edge.getProbability(), sentenceSpec)){
						queue.add(newSeq);
					}
				}
			}
		}
		
		double maxProbability = 0;
		Sequence maxProbabilitySeq = null;
		for (Sequence seq : validSequences){
			if (seq.getTotalProbability() > maxProbability) {
				maxProbability = seq.getTotalProbability();
				maxProbabilitySeq = seq;
			}
		}

		DecimalFormat df = new DecimalFormat("0", DecimalFormatSymbols.getInstance(Locale.ENGLISH));
		df.setMaximumFractionDigits(340);

		return "\"" + maxProbabilitySeq.getSentence() + "\" with probability " + df.format(maxProbability) + "\nTotal nodes considered: " + nodesConsidered;
	}
	
	/*
	 * startingWord - starting word
	 * sentenceSpec - list of parts-of-speech, i.e. ["NNP", "VBD", "DT", "NN"]
	 * graph - text of input.txt
	 */
	public static String generate(String startingWord, ArrayList<String> sentenceSpec, String graph) {
		masterMap = new HashMap<String, Node>();
		// Parse input
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
		return bfs(getNodeFromMaster(startingWord, sentenceSpec), sentenceSpec);
	}
	
	public static void main(String[] args) {
		try {
			byte[] encoded = Files.readAllBytes(Paths.get("input.txt"));
			
			String startingWord = "hans";
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
