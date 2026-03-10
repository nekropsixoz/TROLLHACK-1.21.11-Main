package lol.ethane.feature.helper.impl;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;
import lol.ethane.event.EventDispatcher;
import lol.ethane.event.defined.network.ReceivePacketEvent;
import lol.ethane.event.subscriber.Subscribe;
import lol.ethane.feature.helper.IHelper;
import net.minecraft.class_2596;
import net.minecraft.class_2678;
import net.minecraft.class_6373;

public final class ServerObserver implements IHelper {
   private final List<Integer> transactions = new CopyOnWriteArrayList();
   private boolean sniffTransactions;
   private static ServerObserver instance;

   private ServerObserver() {
   }

   @Subscribe
   private void onPacketReceive(ReceivePacketEvent event) {
      class_2596 packet = event.getPacket();
      Objects.requireNonNull(packet);
      
      if (packet instanceof class_6373 ping) {
         if (this.sniffTransactions) {
            this.transactions.add(ping.method_36950());
            if (this.transactions.size() >= 5) {
               this.sniffTransactions = false;
            }
         }
      } else if (packet instanceof class_2678) {
         this.transactions.clear();
         this.sniffTransactions = true;
      }
   }

   public String guessAntiCheat() {
      if (this.transactions.size() < 5) {
         return "Failed (too few samples)";
      } else {
         List<Integer> diffs = new ArrayList();

         int first;
         for(first = 0; first < this.transactions.size() - 1; ++first) {
            diffs.add((Integer)this.transactions.get(first + 1) - (Integer)this.transactions.get(first));
         }

         first = (Integer)this.transactions.getFirst();
         boolean allDiffsEqual = true;
         if (!diffs.isEmpty()) {
            int firstDiff = (Integer)diffs.getFirst();
            Iterator var5 = diffs.iterator();

            while(var5.hasNext()) {
               Integer diff = (Integer)var5.next();
               if (!Objects.equals(diff, firstDiff)) {
                  allDiffsEqual = false;
                  break;
               }
            }

            if (allDiffsEqual) {
               int constantDiff = (Integer)diffs.getFirst();
               if (constantDiff == 1) {
                  if (first >= -23772 && first <= -23762) {
                     return "Vulcan";
                  }

                  if (first >= 95 && first <= 105 || first >= -20005 && first <= -19995) {
                     return "Matrix";
                  }

                  if (first >= -32773 && first <= -32762) {
                     return "Grizzly";
                  }

                  return "Verus";
               }

               if (constantDiff == -1) {
                  if (first >= -8287 && first <= -8280) {
                     return "Errata";
                  }

                  if (first < -3000) {
                     return "Intave";
                  }

                  if (first >= -5 && first <= 0) {
                     return "Grim";
                  }

                  if (first >= -3000 && first <= -2995) {
                     return "Karhu";
                  }

                  return "Polar";
               }

               return "Failed";
            }
         }

         boolean verusStart = ((Integer)this.transactions.get(0)).equals(this.transactions.get(1));
         boolean verusRest = true;

         for(int i = 2; i < this.transactions.size() - 1; ++i) {
            if ((Integer)this.transactions.get(i + 1) - (Integer)this.transactions.get(i) != 1) {
               verusRest = false;
               break;
            }
         }

         if (verusStart && verusRest) {
            return "Verus";
         } else {
            boolean vulcanRest;
            int i;
            boolean vulcanStart;
            if (diffs.size() >= 2) {
               vulcanStart = (Integer)diffs.get(0) >= 100 && (Integer)diffs.get(1) == -1;
               vulcanRest = true;

               for(i = 2; i < diffs.size(); ++i) {
                  if ((Integer)diffs.get(i) != -1) {
                     vulcanRest = false;
                     break;
                  }
               }

               if (vulcanStart && vulcanRest) {
                  return "Polar";
               }
            }

            if (first < -3000 && this.transactions.contains(0)) {
               return "Intave";
            } else {
               if (this.transactions.size() >= 3) {
                  vulcanStart = (Integer)this.transactions.get(0) == -30767 && (Integer)this.transactions.get(1) == -30766 && (Integer)this.transactions.get(2) == -25767;
                  vulcanRest = true;

                  for(i = 3; i < this.transactions.size() - 1; ++i) {
                     if ((Integer)this.transactions.get(i + 1) - (Integer)this.transactions.get(i) != 1) {
                        vulcanRest = false;
                        break;
                     }
                  }

                  if (vulcanStart && vulcanRest) {
                     return "Old Vulcan";
                  }
               }

               return "Unknown";
            }
         }
      }
   }

   public static ServerObserver get() {
      return instance;
   }

   public static void setInstance() {
      instance = new ServerObserver();
      EventDispatcher.subscribe(instance);
   }
}
