package examples.StarterNNPacMan;

/**
 * Created by piers on 27/10/16.
 */
public class BuildClusterFile {
    public static void main(String[] args) {
        // generations, start, end, start, end
        for(int hiddenLayers = Integer.parseInt(args[1]); hiddenLayers <= Integer.parseInt(args[2]); hiddenLayers++){
            for(int neurons = Integer.parseInt(args[3]); neurons <= Integer.parseInt(args[4]); neurons++){
                System.out.println(args[0] + "," + hiddenLayers + "," + neurons);
            }
        }
    }
}
