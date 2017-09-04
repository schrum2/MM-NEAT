package edu.utexas.cs.nn.tasks.ut2004.server;

import cz.cuni.amis.pogamut.unreal.bot.impl.NativeUnrealBotAdapter;
import cz.cuni.amis.pogamut.ut2004.bot.IUT2004Bot;
import cz.cuni.amis.pogamut.ut2004.server.IUT2004Server;
import cz.cuni.amis.pogamut.ut2004.utils.UCCWrapper;
import cz.cuni.amis.utils.collections.ObservableCollection;
import java.util.HashMap;
import java.util.concurrent.locks.ReentrantLock;

/**
 *
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
