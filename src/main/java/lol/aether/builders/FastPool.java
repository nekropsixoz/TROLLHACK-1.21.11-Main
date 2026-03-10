package lol.aether.builders;

import java.util.function.Supplier;

public class FastPool<T> {
   private final T[] pool;
   private int index = 0;
   private final Supplier<T> factory;

   @SuppressWarnings("unchecked")
   public FastPool(int size, Supplier<T> factory) {
      this.pool = (T[])new Object[size];

      for(int i = 0; i < size; ++i) {
         this.pool[i] = factory.get();
      }

      this.factory = factory;
   }

   public T obtain() {
      if (this.index >= this.pool.length) {
         System.err.println("Overflow! Pool size " + this.pool.length + " exceeded.");
         return this.factory.get();
      } else {
         return this.pool[this.index++];
      }
   }

   public void flush() {
      this.index = 0;
   }
}
