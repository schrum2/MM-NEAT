package edu.southwestern.tasks.gvgai.zelda.level;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

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
			Random r = new Random();
			T[] rule = replacements.get(r.nextInt(replacements.size()));
			for(T g : rule)
				generate(g, generated);
		} else {
			generated.add(grammar);
		}

	}
	
	public static void main(String[] args) {
		GenerateGrammar<ZeldaGrammar> grammar = new GenerateGrammar<>();
		
		grammar.addRule(ZeldaGrammar.OBSTACLE, new ZeldaGrammar[] {ZeldaGrammar.ROOM});
		grammar.addRule(ZeldaGrammar.OBSTACLE, new ZeldaGrammar[] {ZeldaGrammar.MONSTER, ZeldaGrammar.OBSTACLE});
		grammar.addRule(ZeldaGrammar.OBSTACLE, new ZeldaGrammar[] {ZeldaGrammar.KEY, ZeldaGrammar.OBSTACLE, ZeldaGrammar.LOCK, ZeldaGrammar.OBSTACLE});
		
		grammar.addRule(ZeldaGrammar.DUNGEON, new ZeldaGrammar[] {ZeldaGrammar.START, ZeldaGrammar.OBSTACLE, ZeldaGrammar.TREASURE});
		
		List<ZeldaGrammar> generated = grammar.generate(ZeldaGrammar.DUNGEON);
		
		for(ZeldaGrammar z : generated) {
			System.out.print(z.getLabelName() + " ");
		}
		
	}
}
