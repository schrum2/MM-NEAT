package me.jakerg.rougelike;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import asciiPanel.AsciiPanel;

public class TitleUtil {
	public static List<String> loadTitleFromFile(String string) throws IOException {
		return Files.readAllLines(Paths.get(string), Charset.forName("Cp1252"));
	}

	public static int getCenterAligned(int size, AsciiPanel terminal) {
		return (terminal.getHeightInCharacters() / 2) - (size / 2);
	}
}
