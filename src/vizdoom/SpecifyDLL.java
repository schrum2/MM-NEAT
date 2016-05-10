
package vizdoom;

import java.lang.reflect.Field;

/**
 * In order to avoid the need to specify a java.library.path
 * when launching from the command line, the specifyDLLPath()
 * method can be called to set the library path to the vizdoom.dll.
 * 
 * @author Jacob Schrum
 */
public class SpecifyDLL {
    /**
     * Sets java.library.path to standard location of vizdoom.dll
     */
    public static void specifyDLLPath() {
        specifyDLLPath("vizdoom\\bin\\java");
    }

    /**
     * Sets java.library.path to whatever path is supplied.
     * Numerous exceptions are possible, and are handled fairly
     * crudely with a very general try-catch.
     * @param path Path from working directory to vizdoom.dll
     */
    public static void specifyDLLPath(String path) {
        System.setProperty( "java.library.path", path );
        try {
            Field fieldSysPath = ClassLoader.class.getDeclaredField( "sys_paths" );
            fieldSysPath.setAccessible( true );
            fieldSysPath.set( null, null );
        } catch(Exception e) {
            System.out.println(e);
            System.out.println("Could not find vizdoom.dll: " + path);
        }
    }
}
