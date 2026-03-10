package lol.ethane.utils.math;

import lol.ethane.utils.misc.Stopwatch;
import org.apache.logging.log4j.Logger;

public class Measurement {
   public static void begin(Runnable runnable, Logger logger, String format) {
      Stopwatch stopwatch = new Stopwatch();
      runnable.run();
      long time = stopwatch.elapsedTime();
      logger.info(format, time + "ms");
   }
}
