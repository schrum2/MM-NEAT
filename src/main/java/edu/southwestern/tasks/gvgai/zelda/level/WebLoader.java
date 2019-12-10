package edu.southwestern.tasks.gvgai.zelda.level;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import edu.southwestern.parameters.Parameters;
import edu.southwestern.tasks.mario.gan.reader.JsonReader;

public class WebLoader implements LevelLoader{
	
	
	URL url;
	HttpURLConnection con;
	public WebLoader() {
		Parameters.parameters.setBoolean("zeldaGANUsesOriginalEncoding", false);
		try {
			url = new URL("https://zelda-level-api.herokuapp.com/api/get-level");
			
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public List<List<List<Integer>>> getLevels() {

		try {
			con = (HttpURLConnection) url.openConnection();
			con.setRequestMethod("GET");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		con.setRequestProperty("Content-Type", "application/json");
		String inputLine;
		StringBuffer content = new StringBuffer();
		try {
			BufferedReader in = new BufferedReader(
			  new InputStreamReader(con.getInputStream()));
			while ((inputLine = in.readLine()) != null) {
			    content.append(inputLine);
			}
			in.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println(content.toString());
		List<List<List<Integer>>> roomInList = JsonReader.JsonToInt("[" + content.toString() + "]");
		List<List<Integer>> r = roomInList.get(0);
		List<List<List<Integer>>> ret = new ArrayList<>();
		ret.add(r);
		return ret;
	}

}
