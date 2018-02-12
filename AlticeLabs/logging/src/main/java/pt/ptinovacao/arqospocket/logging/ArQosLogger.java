package pt.ptinovacao.arqospocket.logging;

import android.util.Log;

import org.slf4j.helpers.FormattingTuple;
import org.slf4j.helpers.MarkerIgnoringBase;
import org.slf4j.helpers.MessageFormatter;

/**
 * SLF4J implementation that sends logs to the Android logging framework.
 * <p>
 * Created by Emílio Simões on 06-04-2017.
 */
public class ArQosLogger extends MarkerIgnoringBase {

    private static final long serialVersionUID = -1227274521521287937L;

    private static LogLevel currentLogLevel = LogLevel.INFO;

    ArQosLogger(final String name) {
        this.name = name;
    }

    @Override
    public boolean isTraceEnabled() {
        return isLevelEnabled(LogLevel.TRACE);
    }

    @Override
    public void trace(final String message) {
        log(LogLevel.TRACE, message, null);
    }

    @Override
    public void trace(final String format, final Object parameter) {
        formatAndLog(LogLevel.TRACE, format, parameter, null);
    }

    @Override
    public void trace(final String format, final Object parameter1, final Object parameter2) {
        formatAndLog(LogLevel.TRACE, format, parameter1, parameter2);
    }

    @Override
    public void trace(final String format, final Object... arguments) {
        formatAndLog(LogLevel.TRACE, format, arguments);
    }

    @Override
    public void trace(final String message, final Throwable throwable) {
        log(LogLevel.TRACE, message, throwable);
    }

    @Override
    public boolean isDebugEnabled() {
        return isLevelEnabled(LogLevel.DEBUG);
    }

    @Override
    public void debug(final String message) {
        log(LogLevel.DEBUG, message, null);
    }

    @Override
    public void debug(final String format, final Object parameter) {
        formatAndLog(LogLevel.DEBUG, format, parameter, null);
    }

    @Override
    public void debug(final String format, final Object parameter1, final Object parameter2) {
        formatAndLog(LogLevel.DEBUG, format, parameter1, parameter2);
    }

    @Override
    public void debug(final String format, final Object... arguments) {
        formatAndLog(LogLevel.DEBUG, format, arguments);
    }

    @Override
    public void debug(final String message, final Throwable throwable) {
        log(LogLevel.DEBUG, message, throwable);
    }

    @Override
    public boolean isInfoEnabled() {
        return isLevelEnabled(LogLevel.INFO);
    }

    @Override
    public void info(final String message) {
        log(LogLevel.INFO, message, null);
    }

    @Override
    public void info(final String format, final Object parameter) {
        formatAndLog(LogLevel.INFO, format, parameter, null);
    }

    @Override
    public void info(final String format, final Object parameter1, final Object parameter2) {
        formatAndLog(LogLevel.INFO, format, parameter1, parameter2);
    }

    @Override
    public void info(final String format, final Object... arguments) {
        formatAndLog(LogLevel.INFO, format, arguments);
    }

    @Override
    public void info(final String message, final Throwable throwable) {
        log(LogLevel.INFO, message, throwable);
    }

    @Override
    public boolean isWarnEnabled() {
        return isLevelEnabled(LogLevel.WARN);
    }

    @Override
    public void warn(final String message) {
        log(LogLevel.WARN, message, null);
    }

    @Override
    public void warn(final String format, final Object parameter) {
        formatAndLog(LogLevel.WARN, format, parameter, null);
    }

    @Override
    public void warn(final String format, final Object parameter1, final Object parameter2) {
        formatAndLog(LogLevel.WARN, format, parameter1, parameter2);
    }

    @Override
    public void warn(final String format, final Object... arguments) {
        formatAndLog(LogLevel.WARN, format, arguments);
    }

    @Override
    public void warn(final String message, final Throwable throwable) {
        log(LogLevel.WARN, message, throwable);
    }

    @Override
    public boolean isErrorEnabled() {
        return isLevelEnabled(LogLevel.ERROR);
    }

    @Override
    public void error(final String message) {
        log(LogLevel.ERROR, message, null);
    }

    @Override
    public void error(final String format, final Object parameter) {
        formatAndLog(LogLevel.ERROR, format, parameter, null);
    }

    @Override
    public void error(final String format, final Object parameter1, final Object parameter2) {
        formatAndLog(LogLevel.ERROR, format, parameter1, parameter2);
    }

    @Override
    public void error(final String format, final Object... arguments) {
        formatAndLog(LogLevel.ERROR, format, arguments);
    }

    @Override
    public void error(final String message, final Throwable throwable) {
        log(LogLevel.ERROR, message, throwable);
    }

    private boolean isLevelEnabled(LogLevel level) {
        return level.ordinal() >= currentLogLevel.ordinal();
    }

    private void formatAndLog(LogLevel level, String format, Object parameter, Object parameter2) {
        if (!isLevelEnabled(level)) {
            return;
        }
        FormattingTuple tuple = MessageFormatter.format(format, parameter, parameter2);
        log(level, tuple.getMessage(), tuple.getThrowable());
    }

    private void formatAndLog(LogLevel level, String format, Object... arguments) {
        if (!isLevelEnabled(level)) {
            return;
        }
        FormattingTuple tuple = MessageFormatter.arrayFormat(format, arguments);
        log(level, tuple.getMessage(), tuple.getThrowable());
    }

    private void log(LogLevel level, String message, Throwable throwable) {
        if (!isLevelEnabled(level)) {
            return;
        }

        switch (level) {
            case TRACE:
                Log.v(name, message, throwable);
                break;
            case DEBUG:
                Log.d(name, message, throwable);
                break;
            case INFO:
                Log.i(name, message, throwable);
                break;
            case WARN:
                Log.w(name, message, throwable);
                break;
            case ERROR:
                Log.e(name, message, throwable);
                break;
        }

    }

    /**
     * Log levels.
     */
    public enum LogLevel {
        TRACE, DEBUG, INFO, WARN, ERROR
    }

    /**
     * Returns the current log level.
     * The default log level is INFO.
     *
     * @return log level
     */
    public static LogLevel getCurrentLogLevel() {
        return currentLogLevel;
    }

    /**
     * Sets the current log level.
     *
     * @param level new log level to set
     */
    public static void setCurrentLogLevel(LogLevel level) {
        currentLogLevel = level;
    }
}
