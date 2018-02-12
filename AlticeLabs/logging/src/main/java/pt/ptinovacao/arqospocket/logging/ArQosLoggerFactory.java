package pt.ptinovacao.arqospocket.logging;

import org.slf4j.ILoggerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * An implementation of {@link ILoggerFactory}.
 * <p>
 * Created by Emílio Simões on 06-04-2017.
 */
public class ArQosLoggerFactory implements ILoggerFactory {

    private final Map<String, ArQosLogger> loggerMap;

    public ArQosLoggerFactory() {
        loggerMap = new HashMap<>();
    }

    @Override
    public ArQosLogger getLogger(final String name) {
        String loggerName = name.substring(name.lastIndexOf('.') + 1);

        ArQosLogger logger;
        synchronized (this) {
            logger = loggerMap.get(name);
            if (logger == null) {
                logger = new ArQosLogger(loggerName);
                loggerMap.put(name, logger);
            }
        }
        return logger;
    }
}
