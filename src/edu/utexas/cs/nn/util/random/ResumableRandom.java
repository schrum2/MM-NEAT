/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.utexas.cs.nn.util.random;

import java.lang.reflect.Field;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;

/**
 *
 * @author Jacob
 */
public class ResumableRandom extends Random {

    public ResumableRandom(long seed) {
        super(seed);
    }

    public ResumableRandom() {
        super();
    }
    
//    @Override
//    public int nextInt(int x) {
//        int result = super.nextInt(x);
//        System.out.println("Random int " + result);
//        return result;
//    }
//    
//    @Override
//    public double nextDouble(){
//        double result = super.nextDouble();
//        System.out.println("Random double " + result);
//        return result;
//    }

    public void hardSetSeed(long newSeed) {
        //System.out.println("Hard set: " + newSeed);
        try {
            Field field = this.getClass().getSuperclass().getDeclaredField("seed");
            field.setAccessible(true);
            AtomicLong seed = (AtomicLong) field.get(this);
            seed.set(newSeed);
        } catch (IllegalArgumentException ex) {
            ex.printStackTrace();
            System.exit(1);
        } catch (IllegalAccessException ex) {
            ex.printStackTrace();
            System.exit(1);
        } catch (NoSuchFieldException ex) {
            ex.printStackTrace();
            System.exit(1);
        } catch (SecurityException ex) {
            ex.printStackTrace();
            System.exit(1);
        }
    }

    public long getSeed() {
        try {
//            System.out.println(Arrays.toString(this.getClass().getFields()));
//            System.out.println(Arrays.toString(this.getClass().getSuperclass().getFields()));
            Field field = this.getClass().getSuperclass().getDeclaredField("seed");
            field.setAccessible(true);
            AtomicLong seed = (AtomicLong) field.get(this);
            return seed.get();
        } catch (IllegalArgumentException ex) {
            ex.printStackTrace();
            System.exit(1);
        } catch (IllegalAccessException ex) {
            ex.printStackTrace();
            System.exit(1);
        } catch (NoSuchFieldException ex) {
            ex.printStackTrace();
            System.exit(1);
        } catch (SecurityException ex) {
            ex.printStackTrace();
            System.exit(1);
        }
        return 0;
    }

    public static void main(String[] args) {
        ResumableRandom r1 = new ResumableRandom(0);

        System.out.println(r1.nextInt(100) + ":" + r1.getSeed());
        System.out.println(r1.nextInt(100) + ":" + r1.getSeed());
        System.out.println(r1.nextInt(100) + ":" + r1.getSeed());
        System.out.println(r1.nextInt(100) + ":" + r1.getSeed());
        System.out.println(r1.nextInt(100) + ":" + r1.getSeed());
        System.out.println("----------------------");

        ResumableRandom r2 = new ResumableRandom(0);

        System.out.println(r2.nextInt(100) + ":" + r2.getSeed());
        System.out.println(r2.nextInt(100) + ":" + r2.getSeed());
        System.out.println(r2.nextInt(100) + ":" + r2.getSeed());
        System.out.println(r2.nextInt(100) + ":" + r2.getSeed());
        System.out.println(r2.nextInt(100) + ":" + r2.getSeed());
        System.out.println("----------------------");

        r1 = new ResumableRandom(0);

        System.out.println(r1.nextInt(100) + ":" + r1.getSeed());
        long seed = r1.getSeed();
        r2.hardSetSeed(seed);
        System.out.println(r2.nextInt(100) + ":" + r2.getSeed());
        seed = r2.getSeed();
        r1.hardSetSeed(seed);
        System.out.println(r1.nextInt(100) + ":" + r1.getSeed());
        seed = r1.getSeed();
        r2.hardSetSeed(seed);
        System.out.println(r2.nextInt(100) + ":" + r2.getSeed());
        seed = r2.getSeed();
        r1.hardSetSeed(seed);
        System.out.println(r1.nextInt(100) + ":" + r1.getSeed());
        System.out.println("----------------------");

    }
}
