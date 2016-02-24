package pacman.game.util;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;

/**
 * This class handles the I/O for the controllers. It has two static methods
 * that allow controller to load files and to write those files. All these files
 * need to be in the directory 'myData': when running locally, this directory
 * needs to be at the same level as the 'src' directory. In the competition,
 * this directory should be included with the source files, at the same level.
 *
 * If you use I/O in your controller, you must make use of this class. The
 * reason for this is as follows: the competition runs differently to how the
 * software is used in a local setup. There are also security concerns to be
 * taken into account (as well as file sizes). This makes it more difficult to
 * use I/O in general and hence we provide this class for all I/O. We
 * subsequently use a different class with the same signature for the actual
 * competition so that all controllers making use of this class can read and
 * write files in the actual competition.
 */
public class IO {

    public static final String DIRECTORY = "myData/";

    /**
     * Used to save data, represented as a string, in a file called with name
     * specified.
     *
     * @param fileName the name of the file (including extension)
     * @param data the data to be saved
     * @return whether the data was saved successfully
     */
    public static boolean saveFile(String fileName, String data, boolean append) {
        try {
            FileOutputStream outS = new FileOutputStream(DIRECTORY + fileName, append);
            PrintWriter pw = new PrintWriter(outS);

            pw.println(data);
            pw.flush();
            outS.close();

        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

    /**
     * Loads data from a file as specified by the file name supplied.
     *
     * @param fileName the file to be loaded
     * @return the data contained in the file specified
     */
    public static String loadFile(String fileName) {
        StringBuffer data = new StringBuffer();

        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(DIRECTORY + fileName)));
            String input = br.readLine();

            while (input != null) {
                if (!input.equals("")) {
                    data.append(input + "\n");
                }

                input = br.readLine();
            }
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }

        return data.toString();
    }
}