package lol.ethane.utils.render;

import java.util.LinkedList;
import java.util.Queue;

public final class MinecraftRenderer {
   private static final Queue<Runnable> RENDER_QUEUE = new LinkedList();

   public static void addToQueue(Runnable runnable) {
      RENDER_QUEUE.add(runnable);
   }

   public static void render() {
      while(!RENDER_QUEUE.isEmpty()) {
         ((Runnable)RENDER_QUEUE.poll()).run();
      }

   }
}
