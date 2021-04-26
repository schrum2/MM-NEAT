package jackson.serial;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.module.paranamer.ParanamerModule;

/**
 * This used to use the wox serialization library, but that library
 * depends on non-API method calls incompatible with the latest versions
 * of Java. Therefore, I replaced the serialization with Jackson.
 */
public class Easy {
    public static void save(Object ob, String filename) {
        try {
        	XmlMapper xmlMapper = new XmlMapper();
        	xmlMapper.setVisibility(PropertyAccessor.FIELD, Visibility.ANY);
        	xmlMapper.registerModule(new ParanamerModule());
            xmlMapper.writeValue(new File(filename), ob);
            System.out.println("Saved object to " + filename);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static <T> T load(String filename, Class<T> c) {
        try {
            File file = new File(filename);
            XmlMapper xmlMapper = new XmlMapper();
            xmlMapper.setVisibility(PropertyAccessor.FIELD, Visibility.ANY);
            xmlMapper.registerModule(new ParanamerModule());
            String xml = inputStreamToString(new FileInputStream(file));
            //System.out.println(xml);
            T value = xmlMapper.readValue(xml, c);
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