package edu.utexas.cs.nn.logs;

import cz.cuni.amis.pogamut.base.communication.connection.IWorldReaderProvider;
import cz.cuni.amis.pogamut.base.communication.connection.WorldReader;
import cz.cuni.amis.pogamut.base.communication.exception.CommunicationException;
import cz.cuni.amis.pogamut.base.component.bus.IComponentBus;
import cz.cuni.amis.pogamut.base.utils.logging.IAgentLogger;
import cz.cuni.amis.pogamut.ut2004.component.ComponentStub;
import cz.cuni.amis.utils.token.IToken;
import cz.cuni.amis.utils.token.Tokens;
import java.io.BufferedReader;
import java.io.IOException;

public class LogReaderProvider extends ComponentStub implements IWorldReaderProvider {

    private BufferedReader lineReader;
    private LogReader logReader;

    public LogReaderProvider(IAgentLogger logger, IComponentBus bus, BufferedReader lineReader) throws IOException {
        super(logger, bus);
        this.lineReader = lineReader;
        this.logReader = new LogReader(this.lineReader);
    }

    @Override
    public IToken getComponentId() {
        return Tokens.get("ReaderProvider");
    }

    public WorldReader getReader() throws CommunicationException {
        return this.logReader;
    }

    public LogEntry getNextEntry() {
        return logReader.getNextEntry();
    }
}
