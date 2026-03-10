package lol.ethane.utils.simulation;

import net.minecraft.class_243;

public class CachedPlayerSimulation implements PlayerSimulation {
   private final SimulatedPlayerCache simulatedPlayerCache;
   private int ticks = 0;

   public CachedPlayerSimulation(SimulatedPlayerCache simulatedPlayerCache) {
      this.simulatedPlayerCache = simulatedPlayerCache;
   }

   public class_243 getPos() {
      return this.simulatedPlayerCache.getSnapshotAt(this.ticks).pos();
   }

   public void tick() {
      ++this.ticks;
   }
}
