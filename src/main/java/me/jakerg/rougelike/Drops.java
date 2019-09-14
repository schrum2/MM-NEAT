package me.jakerg.rougelike;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import edu.southwestern.util.random.RandomNumbers;

public class Drops<T>{

	private class DropItem{
		T data;
		double weight;
	}
	
	private List<DropItem> items;
	private double totalWeight;
	private Random random = RandomNumbers.randomGenerator;
	
	public Drops() {
		items = new ArrayList<>();
		totalWeight = 0;
	}
	
	public void addEntry(T data, double weight) {
		totalWeight += weight;
		DropItem item = new DropItem();
		item.data = data;
		item.weight = weight;
		items.add(item);
	}
	
	public T getItem() {
		double r = random.nextDouble() * totalWeight;
		
		for(DropItem item : items)
			if(item.weight >= r)
				return item.data;
		
		
		return null;
	}
	
}
