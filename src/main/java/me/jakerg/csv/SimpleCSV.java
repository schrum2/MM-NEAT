package me.jakerg.csv;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Scanner;
import java.util.Set;

public class SimpleCSV<T> {
	
	private T data;
	
	public SimpleCSV(T data) {
		this.data = data;
	}
	
	public void saveToCSV(boolean append, File file) throws Exception {
		
		if(!file.exists())
			file.createNewFile();
		
		String[] headers = new String[]{};
		HashMap<String, String> values = new HashMap<>();
		if(append) {
			headers = getFileHeaders(file);
			if(headers.length == 0) {
				append = false;
			} else {
				try {
					values = getFieldValues(headers);
				} catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e) {
					e.printStackTrace();
					return;
				}
			}
			
		}
		
		if(!append) {
			try {
				values = getFieldHeaders();
			} catch (IllegalArgumentException | IllegalAccessException e) {
				e.printStackTrace();
				return;
			}
		}
		
		if(values.size() == 0) throw new Exception("No values found in class");
		
		FileWriter fw = new FileWriter(file, append);
		
		if(!append) {
			String h = "";
			int i = 0;
			headers = new String[values.size()];
			for(String key : values.keySet()) {
				h += key;
				headers[i] = key;
				if(i != values.size() - 1)
					h += ',';
				else
					h += '\n';
				i++;
			}
			fw.append(h);
		}
		
		String h = "";
		for(int i = 0; i < headers.length; i++) {
			String fieldData = values.get(headers[i]);
			h += fieldData;
			if(i == headers.length - 1)
				h += '\n';
			else
				h += ',';
		}
		fw.append(h);
		
		fw.close();
		
	}
	
	public void saveToTxt(File file) throws IOException, IllegalArgumentException, IllegalAccessException {
		if(!file.exists())
			file.createNewFile();
		
		FileWriter fw = new FileWriter(file);
		HashMap<String, String> values = getFieldHeaders();
	
		for(Entry<String, String> entry : values.entrySet())
			fw.append(entry.getKey() + "=" + entry.getValue() + "\n");
		
		fw.close();
		
	}

	private HashMap<String, String> getFieldValues(String[] headers) throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
		HashMap<String, String> headerToData = new HashMap<>();
		Class<? extends Object> clazz = data.getClass();
		
		for(String header : headers) {
			Field field = clazz.getDeclaredField(header);
			String fieldData = String.valueOf(field.get(data));
			headerToData.put(header, fieldData);
		}
		return headerToData;
	}

	private String[] getFileHeaders(File file) throws IOException {
		FileReader fr = new FileReader(file);
		Scanner scanner = new Scanner(fr);
		
		if(scanner.hasNextLine()) {
			String head = scanner.nextLine();
			head = head.replaceAll("\n", "");
			String[] headers = head.split(",");
			scanner.close();
			return headers;
		}
		scanner.close();
		return new String[] {};
	}
	
	private HashMap<String, String> getFieldHeaders() throws IllegalArgumentException, IllegalAccessException {
		HashMap<String, String> fieldToValue = new HashMap<>();
		Class<? extends Object> clazz = data.getClass();
		Field[] fields = clazz.getDeclaredFields();
		
		for(Field field : fields) {
			Annotation annotation = field.getAnnotation(CSVField.class);
			if(annotation instanceof CSVField) {
				String fieldName = field.getName();
				String fieldData = String.valueOf(field.get(data));
				fieldToValue.put(fieldName, fieldData);
			}
		}
		
		return fieldToValue;
	}
}
   