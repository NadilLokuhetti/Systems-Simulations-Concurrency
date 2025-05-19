package util;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.*;

public class LoggerUtil {
    // Singleton logger instance to ensure only one logger is used across the application
    private static Logger logger;

    // Default log file name where logs will be stored
    private static final String DEFAULT_LOG_FILE = "bathroom_stalls.log";

    // Flag to enable or disable console logging. Set to false to disable console output.
    private static final boolean ENABLE_CONSOLE_LOGGING = true;

    // ANSI escape codes for colored console output
    public static final String RESET = "\u001B[0m";  // Resets text color to default
    public static final String GREEN = "\u001B[32m"; // Green color for "enter" actions
    public static final String RED = "\u001B[31m";   // Red color for "leave" actions or errors
    public static final String YELLOW = "\u001B[33m"; // Yellow color for warnings
    public static final String BLUE = "\u001B[34m";  // Blue color for fine-grained logs

    // Private constructor to prevent instantiation of the utility class
    private LoggerUtil() {
        // Prevent instantiation
    }

    /**
     * Returns a singleton logger instance. If the logger is not initialized,
     * it sets up the logger with the specified log file name or the default file name.
     *
     * @param logFileName the name of the log file (can be null to use the default)
     * @return the logger instance
     */
    public static synchronized Logger getLogger(String logFileName) {
        if (logger == null) {
            setupLogger(logFileName != null ? logFileName : DEFAULT_LOG_FILE);
        }
        return logger;
    }

    /**
     * Sets up the logger with a FileHandler for file logging and optionally
     * a ConsoleHandler for console logging.
     *
     * @param logFileName the name of the log file
     */
    private static void setupLogger(String logFileName) {
        logger = Logger.getLogger(" ");
        logger.setUseParentHandlers(false); // Disable default console logging

        try {
            // Create a FileHandler to write logs to the specified file
            FileHandler fileHandler = new FileHandler(logFileName, false); // Overwrite existing logs
            fileHandler.setFormatter(new CustomLogFormatter(false)); // File logs without color
            fileHandler.setLevel(Level.ALL); // Log all levels
            logger.addHandler(fileHandler);

            // Optional: Add ConsoleHandler for console logging if enabled
            if (ENABLE_CONSOLE_LOGGING) {
                ConsoleHandler consoleHandler = new ConsoleHandler();
                consoleHandler.setFormatter(new CustomLogFormatter(true)); // Console logs with color
                consoleHandler.setLevel(Level.ALL); // Log all levels
                logger.addHandler(consoleHandler);
            }

            logger.setLevel(Level.ALL); // Log all levels
        } catch (IOException e) {
            System.err.println("Failed to initialize logger: " + e.getMessage());
        }
    }

    /**
     * Custom log formatter that formats log records with optional color for console output.
     */
    static class CustomLogFormatter extends Formatter {
        private static final String PATTERN = "yyyy-MM-dd HH:mm:ss"; // Timestamp format
        private final boolean useColor; // Flag to enable or disable color

        public CustomLogFormatter(boolean useColor) {
            this.useColor = useColor;
        }

        @Override
        public String format(LogRecord record) {
            // Format the timestamp using the specified pattern
            String timestamp = new SimpleDateFormat(PATTERN).format(new Date(record.getMillis()));

            // Get the color for the log level
            String levelColor = getColor(record.getLevel());
            String resetColor = useColor ? RESET : ""; // Reset color if using color

            // Format the log message with optional color
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

        /**
         * Returns the ANSI color code for the given log level.
         *
         * @param level the log level
         * @return the ANSI color code
         */
        private String getColor(Level level) {
            if (!useColor) {
                return ""; // No color
            }
            if (level == Level.SEVERE) {
                return RED; // Red for severe logs
            } else if (level == Level.WARNING) {
                return YELLOW; // Yellow for warnings
            } else if (level == Level.INFO) {
                return GREEN; // Green for info logs
            } else if (level == Level.FINE || level == Level.FINER || level == Level.FINEST) {
                return BLUE; // Blue for fine-grained logs
            }
            return RESET; // Default color
        }
    }

    /**
     * Utility method to log messages with a timestamp, thread name, and formatted message.
     * Includes a color parameter for customization of color for the log message.
     *
     * @param level      the log level (e.g., Level.INFO, Level.WARNING)
     * @param threadName the name of the thread
     * @param message    the log message
     * @param color      the color for the log message (e.g., GREEN, RED, RESET)
     */
    public static void log(Level level, String threadName, String message, String color) {
        // Format the timestamp
        String timestamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());

        // Apply color if passed
        String colorCode = color != null ? color : RESET;

        // Format the log message with a structured layout and optional color
        String formattedMessage = String.format(
                "%s\n" +
                        "Timestamp   : %s\n" +
                        "Thread Name : %s\n" +
                        "Action      : %s\n" +
                        "------------------------",
                colorCode, // Apply color
                timestamp,
                threadName,
                message,
                RESET // Reset color after the message
        );

        // Log the formatted message
        getLogger(null).log(level, formattedMessage);
    }
}