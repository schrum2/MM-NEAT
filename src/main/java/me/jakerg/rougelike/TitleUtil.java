package me.jakerg.rougelike;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class TitleUtil {
	public static List<String> loadTitleFromFile(String string) throws IOException {
		return Files.readAllLines(Paths.get(string), Charset.forName("Cp1252"));
	}
}
