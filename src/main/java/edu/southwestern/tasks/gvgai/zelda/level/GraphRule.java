package edu.southwestern.tasks.gvgai.zelda.level;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import edu.southwestern.util.datastructures.Graph;

public class GraphRule<T extends Grammar> {
	private T symbolStart;
	// Dr. Schrum: Why are these not used?
	//private T inBetween;
	//private T toStart;
	private T symbolEnd;
	private GraphGrammar<T> grammar;
	
	public GraphRule(T symbolStart) {
		this.grammar = new GraphGrammar<>();
		this.symbolStart = symbolStart;
	}
	
	public GraphRule(T symbolStart, T symbolEnd) {
		this.grammar = new GraphGrammar<>();
		this.symbolStart = symbolStart;
		this.symbolEnd = symbolEnd;
	}
	
	public GraphRule(File file) {
		try {
			loadFromFile(file);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public GraphGrammar<T> grammar(){
		return this.grammar;
	}
	
	public void setStart(T data) {
		grammar.setStart(data);
	}
	
	public void setEnd(T data) {
		grammar.setEnd(data);
	}
	
	public T getSymbolStart() {
		return this.symbolStart;
	}
	
	public T getSymbolEnd() {
		return this.symbolEnd;
	}

	public Graph<T> getGraph() {
		return grammar.getGraph();
	}

	public Graph<T>.Node getStart() {
		return grammar.getGraphStart();
	}
	public List<Graph<T>.Node> getNodesToStart(){
		return grammar.getNodesToStart();
	}
	public List<Graph<T>.Node> getNodesBetween(){
		return grammar.getNodesBetween();
	}
	public Graph<T>.Node getEnd(){
		return grammar.getGraphEnd();
	}
	
	/**
	 * Save the rule to a file with the name based on the index and start and end nodes
	 * @param index Rule number
	 * @param directory Directory to save the file to
	 * @throws IOException
	 */
	public void saveToFile(int index, File directory) throws IOException {
		String fileName = index + "_" + symbolStart.getLevelType();
		if(symbolEnd != null)
			fileName += "-" + symbolEnd.getLevelType();
		fileName += ".dot";
		File save = directory.toPath().resolve(fileName).toFile();
		BufferedWriter w = new BufferedWriter(new FileWriter(save.getAbsolutePath()));
		w.write(grammar.getDOTString());
		w.close();
	}
	
	/**
	 * Load a Graph Rule from a file
	 * @param file File to load from
	 * @throws FileNotFoundException
	 */
	@SuppressWarnings("unchecked")
	public void loadFromFile(File file) throws FileNotFoundException {
		String n = file.getName();
		n = n.substring(n.indexOf('_') + 1, n.indexOf("."));
		System.out.println(n);
		String[] vals = n.split("-");
		for(String v : vals) {
			System.out.print(v);
		}
		System.out.println();
		try {
			System.out.println(ZeldaGrammar.getByType(vals[0]));
			this.symbolStart = (T) ZeldaGrammar.getByType(vals[0]);
			System.out.println(symbolStart.getLevelType());
			if(vals.length > 1)
				this.symbolEnd = (T) ZeldaGrammar.getByType(vals[1]);
			this.grammar = new GraphGrammar<T>(file);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
}
