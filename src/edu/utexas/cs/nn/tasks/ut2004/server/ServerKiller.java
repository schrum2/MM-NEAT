/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.utexas.cs.nn.tasks.ut2004.server;

import cz.cuni.amis.pogamut.base.agent.state.level1.IAgentStateDown;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbcommands.DisconnectBot;
import cz.cuni.amis.pogamut.ut2004.server.IUT2004Server;
import java.util.concurrent.*;

/**
 *
 * @author Jacob Schrum
 */
public class ServerKiller implements Callable<Boolean> {

    public static void killServer(IUT2004Server server) {
        ExecutorService pond = null;
        try {
            pond = Executors.newFixedThreadPool(1);
            Future<Boolean> future = pond.submit(new ServerKiller(server));
            Boolean result = future.get();
        } catch (InterruptedException ex) {
            System.out.println("Server kill interrupted");
        } catch (ExecutionException ex) {
            System.out.println("ExecutionException in server kill");
        } finally {
            if (pond != null) {
                pond.shutdown();
            }
        }
    }
    private final IUT2004Server server;

    public ServerKiller(IUT2004Server server) {
        this.server = server;
    }

    public Boolean call() throws InterruptedException {
        Thread.currentThread().setName("ServerKiller");
        server.getAct().act(new DisconnectBot());
        try {
            server.stop();
        } catch (Exception e) {
            server.kill();
        }
        if (server.getState().getFlag().isNotState(IAgentStateDown.class)) {
            System.out.println("Server wasn't dead yet: " + server.getState().getFlag());
            Thread.sleep(4000);
            if (server.getState().getFlag().isNotState(IAgentStateDown.class)) {
                System.out.println("Server still not dead, so kill: " + server.getState().getFlag());
                server.kill();
            }
        }
        return true;
    }
}
