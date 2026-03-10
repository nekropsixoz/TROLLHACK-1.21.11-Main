package lol.ethane.utils.simulation;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class SimulatedPlayerCache {
   final SimulatedPlayer simulatedPlayer;
   private int currentSimulationStep = 0;
   private final List<SimulatedPlayerSnapshot> simulationSteps = new ArrayList();
   private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();

   public SimulatedPlayerCache(SimulatedPlayer simulatedPlayer) {
      this.simulatedPlayer = simulatedPlayer;
      this.simulationSteps.add(new SimulatedPlayerSnapshot(simulatedPlayer));
   }

   public void simulateUntil(int ticks) {
      if (ticks < 0) {
         throw new IllegalArgumentException("ticks may not be negative");
      } else if (this.currentSimulationStep < ticks) {
         this.lock.writeLock().lock();

         try {
            while(this.currentSimulationStep < ticks) {
               this.simulatedPlayer.tick();
               this.simulationSteps.add(new SimulatedPlayerSnapshot(this.simulatedPlayer));
               ++this.currentSimulationStep;
            }
         } finally {
            this.lock.writeLock().unlock();
         }

      }
   }

   public SimulatedPlayerSnapshot getSnapshotAt(int ticks) {
      this.simulateUntil(ticks);
      this.lock.readLock().lock();

      SimulatedPlayerSnapshot var2;
      try {
         var2 = (SimulatedPlayerSnapshot)this.simulationSteps.get(ticks);
      } finally {
         this.lock.readLock().unlock();
      }

      return var2;
   }

   public Stream<SimulatedPlayerSnapshot> simulate() {
      return IntStream.iterate(0, (i) -> {
         return i + 1;
      }).mapToObj(this::getSnapshotAt);
   }

   public List<SimulatedPlayerSnapshot> getSnapshotsBetween(int start, int end) {
      if (end >= 1200) {
         throw new IllegalArgumentException("tried to simulate a player for more than a minute!");
      } else {
         this.simulateUntil(end + 1);
         this.lock.readLock().lock();

         ArrayList var3;
         try {
            var3 = new ArrayList(this.simulationSteps.subList(start, end + 1));
         } finally {
            this.lock.readLock().unlock();
         }

         return var3;
      }
   }

   public Stream<SimulatedPlayerSnapshot> simulateBetween(int start, int end) {
      if (end >= 1200) {
         throw new IllegalArgumentException("tried to simulate a player for more than a minute!");
      } else {
         this.simulateUntil(end + 1);
         return IntStream.rangeClosed(start, end).mapToObj(this::getSnapshotAt);
      }
   }
}
