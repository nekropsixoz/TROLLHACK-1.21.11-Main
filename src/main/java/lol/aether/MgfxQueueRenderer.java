package lol.aether;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class MgfxQueueRenderer {
   private static final Queue<Runnable> callbackQueue = new ConcurrentLinkedQueue();

   public static void request(Runnable callback) {
      if (callback != null) {
         callbackQueue.offer(callback);
      }

   }

   public static void flush() {
      if (!callbackQueue.isEmpty()) {
         try {
            Runnable[] callbackArray = (Runnable[])callbackQueue.toArray(new Runnable[0]);
            callbackQueue.clear();
            Runnable[] var1 = callbackArray;
            int var2 = callbackArray.length;

            for(int var3 = 0; var3 < var2; ++var3) {
               Runnable callback = var1[var3];

               try {
                  callback.run();
               } catch (Exception var6) {
                  var6.printStackTrace();
               }
            }
         } catch (Throwable var7) {
            var7.printStackTrace();
         }

      }
   }
}
