package edu.utexas.cs.nn.tasks.ut2004.server;

import cz.cuni.amis.pogamut.ut2004.bot.IUT2004Bot;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbcommands.DisconnectBot;
import java.util.concurrent.*;

/**
 *
 * @author Jacob Schrum
 */
public class BotKiller implements Callable<Boolean> {

    public static void killBot(IUT2004Bot bot) {
        ExecutorService pond = null;
        try {
            pond = Executors.newFixedThreadPool(1);
            Future<Boolean> future = pond.submit(new BotKiller(bot));
            Boolean result = future.get();
        } catch (InterruptedException ex) {
            System.out.println("Bot kill interrupted");
        } catch (ExecutionException ex) {
            System.out.println("ExecutionException in bot kill");
        } finally {
            if (pond != null) {
                pond.shutdown();
            }
        }
    }
    private final IUT2004Bot b;

    public BotKiller(IUT2004Bot b) {
        this.b = b;
    }

    @Override
    public Boolean call() {
        Thread.currentThread().setName("BotKiller-" + b.getName());
        //System.out.println(b.getName() + ": Killing " + b);
        try {
            b.getAct().act(new DisconnectBot());
            //b.bot.kill();
            b.stop();
        } catch (Exception e) {
            System.out.println("Exception on kill: " + e);
            e.printStackTrace();
        }
        //System.out.println(b.getName() + ": Done killing " + b);
        return true;
    }
}
