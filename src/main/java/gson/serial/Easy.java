package gson.serial;

import java.io.File;
import java.io.FileWriter;
import java.util.Scanner;

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

    public static Object load(String filename) {
        try {
            Scanner s = new Scanner(new File(filename));
            String json = s.nextLine();
        	Gson gson = new Gson();
        	return gson.fromJson(json, Object.class); 
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
