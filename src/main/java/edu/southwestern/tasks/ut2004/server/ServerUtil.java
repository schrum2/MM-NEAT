package edu.southwestern.tasks.ut2004.server;

import cz.cuni.amis.pogamut.unreal.bot.impl.NativeUnrealBotAdapter;
import cz.cuni.amis.pogamut.ut2004.bot.IUT2004Bot;
import cz.cuni.amis.pogamut.ut2004.server.IUT2004Server;
import cz.cuni.amis.pogamut.ut2004.utils.UCCWrapper;
import cz.cuni.amis.utils.collections.ObservableCollection;
import java.util.HashMap;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Manages the servers running games, either adding them to the list to be tracked, or deleting them.
 * @author Jacob Schrum
 */
public class ServerUtil {

	public static final int MIN_AVAILABLE_PORT = 3000;
	public static final int MAX_AVAILABLE_PORT = 3500;
	public static int nextPort = MIN_AVAILABLE_PORT;
	public static final boolean PERIODIC_CLEANING = true;
	private static ReentrantLock runningServersLock = new ReentrantLock();
	private static HashMap<Integer, UCCWrapper> runningServers = new HashMap<Integer, UCCWrapper>();
	private static int tickets = 0;

	/**
	 * finds an available port with which to connect the server
	 * @return returns the port number to connect to
	 */
	public static int getAvailablePort() {
		runningServersLock.lock();
		int port;
		try {
			port = nextPort++;
			System.out.println("Port " + port + " claimed");
			if (nextPort > MAX_AVAILABLE_PORT) {
				nextPort = MIN_AVAILABLE_PORT;
			}
		} finally {
			runningServersLock.unlock();
		}
		return port;
	}

	/**
	 * stops the server running, along with anything associated with it
	 * @param ucc (what class will manage the server)
	 * @param killAll (whether or not to stop all the servers)
	 */
	public static void destroyServer(UCCWrapper ucc, boolean killAll) {
		if (ucc != null) {
			IUT2004Server server = ucc.getUTServer();
			if (server != null) {
				killServerAgents(server, killAll); // See if shutting down
													// server without killing
													// GBBots is enough
				ServerKiller.killServer(server);
			}
			ucc.stop();
			try {
				Thread.sleep(4000);
			} catch (InterruptedException ex) {
				System.out.println("Post-server-kill Thread sleep interrupted");
			}
		}
	}

	/**
	 * kills the agents in a given server
	 * @param server (server with agents be killed)
	 * @param all (whether to kill all the agents)
	 */
	public static void killServerAgents(IUT2004Server server, boolean all) {
		final ObservableCollection<? extends NativeUnrealBotAdapter> nativeAgents = server.getNativeAgents();
		synchronized (nativeAgents) {
			NativeUnrealBotAdapter[] agents = new NativeUnrealBotAdapter[0];
			nativeAgents.toArray(agents);
			for (NativeUnrealBotAdapter agent : agents) {
				System.out.println("Kill native bot " + agent);
				agent.stop();
			}
		}
		if (all) {
			final ObservableCollection<IUT2004Bot> bots = server.getAgents();
			synchronized (bots) {
				for (IUT2004Bot gbbot : bots) {
					System.out.println("Kill GBBot " + gbbot);
					BotKiller.killBot(gbbot);
				}
			}
		}
	}

	/**
	 * adds a server to the list of ones to be monitored
	 * @param newUCC (what class will manage the server)
	 * @return returns the ticket number for the server
	 */
	public static int addServer(UCCWrapper newUCC) {
		runningServersLock.lock();
		int ticket = -1;
		try {
			runningServers.put(tickets, newUCC);
			ticket = tickets++;
		} finally {
			runningServersLock.unlock();
		}
		return ticket;
	}

	/**
	 * destroys a server, and removes it from the list of serfers being monitored
	 * @param ticket (the number of the server to be removed)
	 * @return returns which server was removed
	 */
	public static UCCWrapper removeServer(int ticket) {
		runningServersLock.lock();
		UCCWrapper removed = null;
		try {
			System.out.println("Removing server with ticket: " + ticket);
			removed = runningServers.remove(ticket);
			destroyServer(removed, false);
		} finally {
			runningServersLock.unlock();
		}
		return removed;
	}
}
