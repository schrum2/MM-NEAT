package edu.southwestern.tasks.gvgai.zelda.level;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import edu.southwestern.util.random.RandomNumbers;

public class GenerateGrammar<T extends Grammar> {
	private HashMap<T, List<T[]>> rules;
	
	public GenerateGrammar() {
		this.rules = new HashMap<>();
	}
	
	public void addRule(T symbol, T[] next) {
		if(!symbol.isSymbol())
			throw new IllegalArgumentException("Invalid symbol");
		
		if(!rules.containsKey(symbol))
			rules.put(symbol, new LinkedList<>());
		
		rules.get(symbol).add(next);
	}
	
	public List<T> generate(T symbol) {
		List<T> generated = new LinkedList<>();
		if(!symbol.isSymbol())
			throw new IllegalArgumentException("Invalid symbol");
		
		generate(symbol, generated);
		
		return generated;
		
	}
	
	public void generate(T grammar, List<T> generated) {
		if(grammar.isSymbol()) {
			List<T[]> replacements = rules.get(grammar);
			T[] rule = replacements.get(RandomNumbers.randomGenerator.nextInt(replacements.size()));
			for(T g : rule)
				generate(g, generated);
		} else {
			generated.add(grammar);
		}

	}
	
	public static void main(String[] args) {
		GenerateGrammar<ZeldaGrammar> grammar = new GenerateGrammar<>();
		
		grammar.addRule(ZeldaGrammar.OBSTACLE_S, new ZeldaGrammar[] {ZeldaGrammar.ROOM});
		grammar.addRule(ZeldaGrammar.OBSTACLE_S, new ZeldaGrammar[] {ZeldaGrammar.MONSTER, ZeldaGrammar.OBSTACLE_S});
		grammar.addRule(ZeldaGrammar.OBSTACLE_S, new ZeldaGrammar[] {ZeldaGrammar.KEY, ZeldaGrammar.OBSTACLE_S, ZeldaGrammar.LOCK, ZeldaGrammar.OBSTACLE_S});
		
		grammar.addRule(ZeldaGrammar.DUNGEON_S, new ZeldaGrammar[] {ZeldaGrammar.START, ZeldaGrammar.OBSTACLE_S, ZeldaGrammar.TREASURE});
		
		List<ZeldaGrammar> generated = grammar.generate(ZeldaGrammar.DUNGEON_S);
		
		for(ZeldaGrammar z : generated) {
			System.out.print(z.getLabelName() + " ");
		}
		
	}
}
