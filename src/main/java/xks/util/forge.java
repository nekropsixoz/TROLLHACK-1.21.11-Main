package xks.util;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

public class forge {
    private static final List<String> ALLOWED_PATTERNS = List.of(
            //"1","2"
    );

    private static final PrintStream originalOut = System.out;
    private static final PrintStream originalErr = System.err;
    private static final InputStream originalIn = System.in;

    //     ()
    private static final List<String> hiddenLogs = new ArrayList<>();

    public static void start() {
        try {
            //  System.out
            System.setOut(new PrintStream(new OutputStream() {
                @Override
                public void write(int b) {
                    //    -  
                }

                @Override
                public void write(byte[] b, int off, int len) {
                    String message = new String(b, off, len);
                    if (shouldShow(message)) {
                        originalOut.write(b, off, len);
                    } else {
                        hiddenLogs.add("[HIDDEN] " + message.trim());
                    }
                }
            }));

            //  System.err
            System.setErr(new PrintStream(new OutputStream() {
                @Override
                public void write(int b) {
                    //    -  
                }

                @Override
                public void write(byte[] b, int off, int len) {
                    String message = new String(b, off, len);
                    if (shouldShow(message)) {
                        originalErr.write(b, off, len);
                    } else {
                        hiddenLogs.add("[HIDDEN_ERROR] " + message.trim());
                    }
                }
            }));

            //  System.in ()
            System.setIn(new InputStream() {
                @Override
                public int read() {
                    return -1; // EOF -  
                }
            });

            //   Log4j
            suppressLog4j();

            //  java.util.logging
            suppressJavaUtilLogging();

        } catch (Exception e) {
            //    
        }
    }

    private static boolean shouldShow(String message) {
        if (message == null || message.trim().isEmpty()) {
            return false;
        }

        String lowerMessage = message.toLowerCase();

        //    
        for (String pattern : ALLOWED_PATTERNS) {
            if (lowerMessage.contains(pattern)) {
                return true;
            }
        }

        return false;
    }

    private static void suppressLog4j() {
        try {
            //   Log4j
            org.apache.logging.log4j.core.LoggerContext ctx =
                    (org.apache.logging.log4j.core.LoggerContext) org.apache.logging.log4j.LogManager.getContext(false);
            org.apache.logging.log4j.core.config.Configuration config = ctx.getConfiguration();
            org.apache.logging.log4j.core.config.LoggerConfig loggerConfig = config.getLoggerConfig(org.apache.logging.log4j.LogManager.ROOT_LOGGER_NAME);
            loggerConfig.setLevel(org.apache.logging.log4j.Level.OFF);
            ctx.updateLoggers();
        } catch (Exception e) {
            // Ignore
        }
    }

    private static void suppressJavaUtilLogging() {
        try {
            java.util.logging.Logger rootLogger = java.util.logging.LogManager.getLogManager().getLogger("");
            for (java.util.logging.Handler handler : rootLogger.getHandlers()) {
                rootLogger.removeHandler(handler);
            }
        } catch (Exception e) {
            // Ignore
        }
    }

    //     ()
    public static void restoreConsole() {
        try {
            System.setOut(originalOut);
            System.setErr(originalErr);
            System.setIn(originalIn);
        } catch (Exception e) {
            // Ignore
        }
    }

    //    ( )
    public static List<String> getHiddenLogs() {
        return new ArrayList<>(hiddenLogs);
    }

    //   
    public static void clearHiddenLogs() {
        hiddenLogs.clear();
    }
}