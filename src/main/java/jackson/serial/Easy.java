package jackson.serial;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.module.jaxb.JaxbAnnotationModule;

/**
 * This used to use the wox serialization library, but that library
 * depends on non-API method calls incompatible with the latest versions
 * of Java. Therefore, I replaced the serialization with Jackson.
 */
public class Easy {
    public static void save(Object ob, String filename) {
        try {
        	ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.registerModule(new JaxbAnnotationModule());
            objectMapper.setVisibility(PropertyAccessor.FIELD, Visibility.ANY);
            objectMapper.writeValue(new File(filename), ob);
            System.out.println("Saved object to " + filename);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static <T> T load(String filename, Class<T> c) {
        try {
            File file = new File(filename);
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.setVisibility(PropertyAccessor.FIELD, Visibility.ANY);
            objectMapper.registerModule(new JaxbAnnotationModule());
            String json = inputStreamToString(new FileInputStream(file));
            //System.out.println(xml);
            T value = objectMapper.readValue(json, c);
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