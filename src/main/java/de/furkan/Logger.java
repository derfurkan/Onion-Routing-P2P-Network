package de.furkan;

import java.util.logging.Level;

public class Logger {

  private final java.util.logging.Logger logger;

  public Logger(Class<?> clazz) {
    logger = java.util.logging.Logger.getLogger(clazz.getName());
  }

  public void info(String message) {
    logger.log(Level.INFO, formatMessage("INFO", message));
  }

  public void warn(String message) {
    logger.log(Level.WARNING, formatMessage("WARN", message));
  }

  public void error(String message) {
    logger.log(Level.SEVERE, formatMessage("ERROR", message));
  }

  public void debug(String message) {
    logger.log(Level.FINE, formatMessage("DEBUG", message));
  }

  private String formatMessage(String severity, String message) {
    return "[" + logger.getName() + "] [" + severity + "]: " + message;
  }
}
