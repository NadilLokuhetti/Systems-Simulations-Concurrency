import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.*;

public class LoggerUtil {
    private static Logger logger;
    private static final String DEFAULT_LOG_FILE = "coffee_shop.log";
    private static final boolean ENABLE_CONSOLE_LOGGING = true; // Set to false to disable console logging

    // ANSI escape codes for colors
    private static final String RESET = "\u001B[0m";
    private static final String RED = "\u001B[31m";
    private static final String GREEN = "\u001B[32m";
    private static final String YELLOW = "\u001B[33m";
    private static final String BLUE = "\u001B[34m";

    private LoggerUtil() {
        // Prevent instantiation
    }

    public static synchronized Logger getLogger(String logFileName) {
        if (logger == null) {
            setupLogger(logFileName != null ? logFileName : DEFAULT_LOG_FILE);
        }
        return logger;
    }

    private static void setupLogger(String logFileName) {
        logger = Logger.getLogger("CoffeeShopLogger");
        logger.setUseParentHandlers(false); // Disable default console logging

        try {
            // Create a FileHandler for the log file
            FileHandler fileHandler = new FileHandler(logFileName, false); // Overwrite existing logs
            fileHandler.setFormatter(new CustomLogFormatter(false)); // File logs without color
            fileHandler.setLevel(Level.ALL);
            logger.addHandler(fileHandler);

            // Optional: Add ConsoleHandler for console logging
            if (ENABLE_CONSOLE_LOGGING) {
                ConsoleHandler consoleHandler = new ConsoleHandler();
                consoleHandler.setFormatter(new CustomLogFormatter(true)); // Console logs with color
                consoleHandler.setLevel(Level.ALL);
                logger.addHandler(consoleHandler);
            }

            logger.setLevel(Level.ALL);
        } catch (IOException e) {
            System.err.println("Failed to initialize logger: " + e.getMessage());
        }
    }

    // Custom log formatter with optional color
    static class CustomLogFormatter extends Formatter {
        private static final String PATTERN = "yyyy-MM-dd HH:mm:ss";
        private final boolean useColor;

        public CustomLogFormatter(boolean useColor) {
            this.useColor = useColor;
        }

        @Override
        public String format(LogRecord record) {
            String timestamp = new SimpleDateFormat(PATTERN).format(new Date(record.getMillis()));
            String levelColor = getColor(record.getLevel());
            String resetColor = useColor ? RESET : "";

            // Full-color formatting for the entire log message
            return String.format("%s [%s%s%s] %s: %s%s%s%n",
                    timestamp,
                    useColor ? levelColor : "",
                    record.getLevel(),
                    resetColor,
                    useColor ? levelColor : "",
                    record.getLoggerName(),
                    record.getMessage(),
                    resetColor);
        }

        private String getColor(Level level) {
            if (!useColor) {
                return ""; // No color
            }
            if (level == Level.SEVERE) {
                return RED;
            } else if (level == Level.WARNING) {
                return YELLOW;
            } else if (level == Level.INFO) {
                return GREEN;
            } else if (level == Level.FINE || level == Level.FINER || level == Level.FINEST) {
                return BLUE;
            }
            return RESET; // Default color
        }
    }

    // Utility method to log with dynamic thread name and message
    public static void log(Level level, String threadName, String message) {
        String formattedMessage = String.format("%s: %s", threadName, message);
        getLogger(null).log(level, formattedMessage);
    }
}
