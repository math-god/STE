/*
package log;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class FileLogger {

    private static final Map<String, FileHandler> handlerMap = new HashMap<>();

    public static Logger getFileLogger(String className, String fileName) {
        initializeIfNeed(fileName);

        var logger = Logger.getLogger(className);
        logger.addHandler(handlerMap.get(fileName));
        logger.setUseParentHandlers(false);

        return logger;
    }

    private static void initializeIfNeed(String fileName) {
        if (handlerMap.get(fileName) == null) {
            try {
                var fileHandler = new FileHandler(fileName);
                fileHandler.setFormatter(new SimpleFormatter());

                handlerMap.put(fileName, fileHandler);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
*/
