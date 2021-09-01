package gson.serial;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import com.google.gson.Gson;

import edu.southwestern.util.MiscUtil;

/**
 * Gson based alternative to the wox library which uses deprecated non-API classes.
 * @author schrum2
 *
 */
public class Easy {
    public static void save(Object ob, String filename) {
        try {
        	Gson gson = new Gson();
        	String json = gson.toJson(ob);  
        	System.out.println(json);
        	MiscUtil.waitForReadStringAndEnterKeyPress();
            FileWriter file = new FileWriter(filename);
            file.write(json);
            file.close();
            System.out.println("Saved object to " + filename);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static <T> T load(String filename, Class<T> c) {
        try {
            File file = new File(filename);
            String json = inputStreamToString(new FileInputStream(file));
        	Gson gson = new Gson();
            T value = gson.fromJson(json, c);
            return value;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    /**
     * Utility method from:
     * https://www.baeldung.com/jackson-xml-serialization-and-deserialization
     * 
     * @param is Input stream
     * @return String in the stream
     * @throws IOException
     */
    public static String inputStreamToString(InputStream is) throws IOException {
        StringBuilder sb = new StringBuilder();
        String line;
        BufferedReader br = new BufferedReader(new InputStreamReader(is));
        while ((line = br.readLine()) != null) {
            sb.append(line);
        }
        br.close();
        return sb.toString();
    }
}
