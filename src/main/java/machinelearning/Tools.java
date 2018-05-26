package machinelearning;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.StringTokenizer;

/**
* Created by IntelliJ IDEA.
* User: julian
* Date: Oct 21, 2008
* Time: 5:05:05 PM
*/
public class Tools {

   public static int drawIndex (double[] array) {
       // assumption: members of the array sum to 1
       double r = Math.random ();
       double sum = 0;
       //int winner = 0;
       for (int i = 0; i < array.length; i++) {
           sum += array[i];
           if (sum >= r) {
               return i;
           }
       }
       throw new RuntimeException ("Winner not found " + arrayToString (array));
   }


   public static int argmax(double[] array) {
       int index = 0;
       double highest = array[0];
       for (int i = 0; i < array.length; i++) {
           if (array[i] > highest) {
               index = i;
               highest = array[i];
           }
       }
       return index;
   }


   public static void bound(double array[], double maxval) {
       for (int i = 0; i < array.length; i++) {
           if (array[i] > maxval) {
               array[i] = maxval;
           }
           else if (array[i] < -maxval) {
               array[i] = -maxval;
           }
       }
   }


   public static void setLength(double array[], double l) {
       double sum = 0.0;
       for (int i = 0; i < array.length; i++) {
           sum += array[i]*array[i];
       }
       sum = Math.sqrt(sum);
       for (int i = 0; i < array.length; i++) {
           array[i] = l * array[i] / sum;
       }
   }


   public static void absbound(double array[], double maxval) {
       for (int i = 0; i < array.length; i++) {
           if (Math.abs(array[i]) > maxval) {
               if (array[i] < 0.0) {
                   array[i] = -maxval;
               }
               else
                   array[i] = maxval;
           }
       }
   }

   public static void normmax(double array[], double maxval) {
       double m = Math.abs(array[0]);
       for (int i = 1; i < array.length; i++) {
           if (Math.abs(array[i]) > m) {
               m = Math.abs(array[i]);
           }
       }
       for (int i = 0; i < array.length; i++) {
           array[i] = maxval * array[i] / m;
       }
   }


   public static void constant(double array[], double maxval) {
           for (int i = 1; i < array.length; i++) {
               if (Math.abs(array[i]) > 0.0) {
                   array[i] = maxval;
               }
               if (Math.abs(array[i]) < 0.0) {
                   array[i] = -maxval;
               }
           }
       }


   public static void minmax(double array[], double minval, double maxval) {
       double ma = Math.abs(array[0]);
       for (int i = 1; i < array.length; i++) {
           if (Math.abs(array[i]) > ma) {
               ma = Math.abs(array[i]);
           }
       }
       if (ma < minval) {
           for (int i = 0; i < array.length; i++) {
               array[i] *= minval / ma;
           }
       }
       if (ma > maxval) {
           for (int i = 0; i < array.length; i++) {
               array[i] = array[i] * maxval / ma;
           }

       }
   }


   public static double maximum(double array[]) {
       double best = array[0];
       for (int i = 1; i < array.length; i++) {
           if (array[i] > best) {
               best = array[i];
           }
       }
       return best;
   }

   public static String arrayToString(double[] array) {
       StringBuffer sb = new StringBuffer ();
       for (int i = 0; i < array.length; i++) {
           sb.append(array[i] + " ");
       }
       return sb.toString ();
   }

   public static String arrayToShortString (double[] array) {
       //StringBuffer sb = new StringBuffer ();
       ByteArrayOutputStream out = new ByteArrayOutputStream();
       for (int i = 0; i < array.length; i++) {
           PrintStream ps = new PrintStream (out);


           ps.printf("%.4f ",array[i]);
       }
       return out.toString();
   }


   public static String arrayToShortString(double[][] array) {
       ByteArrayOutputStream out = new ByteArrayOutputStream();
       for (int i = 0; i < array.length; i++) {
           PrintStream ps = new PrintStream (out);
           for (int j = 0; j < array[i].length; j++) {
               ps.printf("%.4f ",array[i][j]);
           }
           ps.printf(" ____ ");
       }
       return out.toString();
   }

   public static double safeExp(double x) {
       if(Math.abs(x) < 7.0)
           return Math.exp(x);
       if(x > 0.0)
           return Math.exp(7.0);
       else
           return Math.exp(-7.0);
   }

   public static double sigmoid(double x) {
       return 1.0/(1.0 + safeExp(-x));
   }

   public static double sigmoidprime(double x) {
       return sigmoid(x)*(1.0 - sigmoid(x));
   }

   public static double tanh(double x) {
       return Math.tanh(x);
   }

   public static double tanhprime(double x) {
       return 1.0 - tanh(x)*tanh(x);
   }

   public static double[] parseDoubleArray (String string, int length) {
       int count = 0;
       double[] array = new double[length];
       StringTokenizer st = new StringTokenizer (string, " ");
       while (st.hasMoreTokens ()) {
           array[count++] = Double.parseDouble (st.nextToken());
       }
       if (count < length)
           throw new RuntimeException ("Too few items in string - not enough to fill the array! ("
                   + length + " wanted, " + count + " found)");
       return null;
   }

   public static int draw (double[] distribution) {
       return draw (distribution, Math.random ());
   }

   public static int draw (double[] distribution, double p) {
       double probabilitySum = 0;
       for (int index = 0; index < distribution.length; index++) {
           probabilitySum += distribution[index];
           if (p <= probabilitySum)
               return index;
       }
       throw new RuntimeException ("Can't draw any index! probabilitysum=" + probabilitySum + ", p=" + p);
   }


    public static double[][] copyArray(double[][] original) {
        double[][] copy = new double[original.length][original[0].length];
        for (int i = 0; i < original.length; i++) {
            for (int j = 0; j < original[i].length; j++) {
                copy[i][j] = original[i][j];
            }
        }
        return copy;
    }

}