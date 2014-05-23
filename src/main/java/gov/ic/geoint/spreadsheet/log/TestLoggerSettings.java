
package gov.ic.geoint.spreadsheet.log;

import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 */
public class TestLoggerSettings {

    public TestLoggerSettings() {
        ConsoleHandler ch = new ConsoleHandler();
        ch.setLevel(Level.ALL);
        Logger govLogger = Logger.getLogger("gov");
        govLogger.setLevel(Level.ALL);
        govLogger.addHandler(ch);
        Logger javaLogger = Logger.getLogger("java");
        javaLogger.setLevel(Level.ALL);
        javaLogger.addHandler(ch);
    }

    
}
