package edu.utexas.cs.nn.logs;

import cz.cuni.amis.pogamut.base.agent.IAgentId;
import cz.cuni.amis.pogamut.base.agent.impl.AgentId;
import cz.cuni.amis.pogamut.base.communication.messages.InfoMessage;
import cz.cuni.amis.pogamut.base.communication.parser.exception.ParserEOFException;
import cz.cuni.amis.pogamut.base.communication.parser.impl.yylex.IYylexObserver;
import cz.cuni.amis.pogamut.base.component.bus.ComponentBus;
import cz.cuni.amis.pogamut.base.component.bus.IComponentBus;
import cz.cuni.amis.pogamut.base.utils.logging.AgentLogger;
import cz.cuni.amis.pogamut.base.utils.logging.IAgentLogger;
import cz.cuni.amis.pogamut.ut2004.communication.messages.UnrealIdTranslator;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.Yylex;
import cz.cuni.amis.pogamut.ut2004.communication.parser.UT2004Parser;
import cz.cuni.amis.pogamut.ut2004.communication.translator.itemdescriptor.ItemTranslator;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.regex.Pattern;

/**
 * This class process event logs and convers them to a timed sequence of the
 * Pogamut message objects o the right kind.
 *
 * An example line in the log file looks like this:
 *
 * ScriptLog: nnrgAPI@146.20:DM-GoatswoodPlay.ObservedPlayer3:player0: DAM {Damage 52} {DamageType XWeapons.DamTypeRedeemer} {Flaming False} {CausedByWorld False} {DirectDamage True} {BulletHit False} {VehicleHit False}
 *
 * We want to convert it into a bunch of entries that have a time, a player
 * (id and name), and a parsed message. We use a regexp to pre-parse the line
 * into these three parts, and then the Pogamut parser to parse the actual
 * Message.
 * @author Igor Karpov (ikarpov@cs.utexas.edu)
 */
public class LogProcessor {
    public static Pattern logLinePattern = Pattern.compile("ScriptLog: nnrgAPI@([^:]+):([^:]+):([^:]+):(.*)");

    public List<LogEntry> parseLogEntries(BufferedReader reader)
            throws IOException
    {
        IAgentId agentId = new AgentId(LogProcessor.class.getName() + ".parseLogEntries");
        IAgentLogger logger = new AgentLogger(agentId);
        logger.setLevel(Level.ALL);
        IComponentBus bus = new ComponentBus(logger);
        List<LogEntry> events = new LinkedList<LogEntry>();
        LogReaderProvider readerProvider = new LogReaderProvider(logger, bus, reader);
        IYylexObserver yylexObserver = new YylexObserver();
        Yylex yylex = new Yylex();
        ItemTranslator itemTranslator = new ItemTranslator();
        UnrealIdTranslator uidTranslator = new UnrealIdTranslator();

        UT2004Parser parser = new UT2004Parser(uidTranslator, itemTranslator,
                readerProvider, yylex, yylexObserver, bus, logger);
        readerProvider.getController().manualStart("Manual start...");
        try {
            InfoMessage message = parser.parse();
            while (message != null) {
                LogEntry entry = readerProvider.getNextEntry();
                entry.setMessage(message);
                events.add(entry);
                message = parser.parse();
            }
        } catch (ParserEOFException eof) {
            System.out.println("EOF reached");
        } finally {
            readerProvider.getController().manualStop("Manual stop...");
        }
        return events;
    }

    public class YylexObserver implements IYylexObserver {

        @Override
        public void exception(Exception e, String info) {
            System.err.println(info);
            e.printStackTrace(System.err);
        }

        @Override
        public void warning(String info) {
            // ignore warnings
        }
        
    }

}
