package lol.ethane.feature.scripting.wrapper.event;

import it.unimi.dsi.fastutil.ints.IntListIterator;
import java.util.Iterator;
import java.util.Set;
import lol.ethane.event.EventCancellable;
import lol.ethane.feature.scripting.wrapper.impl.DynamicLuaWrapper;
import net.minecraft.class_10182;
import net.minecraft.class_10185;
import net.minecraft.class_1799;
import net.minecraft.class_2561;
import net.minecraft.class_2568;
import net.minecraft.class_2596;
import net.minecraft.class_2604;
import net.minecraft.class_2708;
import net.minecraft.class_2709;
import net.minecraft.class_2716;
import net.minecraft.class_2743;
import net.minecraft.class_2797;
import net.minecraft.class_2828;
import net.minecraft.class_2851;
import net.minecraft.class_7438;
import net.minecraft.class_7439;
import net.minecraft.class_7827;
import net.minecraft.class_2568.class_10611;
import net.minecraft.class_2568.class_10612;
import net.minecraft.class_2568.class_10613;
import net.minecraft.class_2568.class_5248;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.ZeroArgFunction;

public class LuaPacketEvent extends DynamicLuaWrapper {
   public LuaPacketEvent(EventCancellable event, class_2596<?> packet) {
      this.register("packet_name", () -> {
         return LuaValue.valueOf(packet.getClass().getSimpleName());
      });
      this.register("packet_type", () -> {
         return LuaValue.valueOf(packet.method_65080().comp_2231().toString());
      });
      if (packet instanceof class_2743) {
         class_2743 p = (class_2743)packet;
         this.register("id", () -> {
            return LuaValue.valueOf(p.method_11818());
         });
         this.register("motion_x", () -> {
            return LuaValue.valueOf(p.method_73085().field_1352);
         });
         this.register("motion_y", () -> {
            return LuaValue.valueOf(p.method_73085().field_1351);
         });
         this.register("motion_z", () -> {
            return LuaValue.valueOf(p.method_73085().field_1350);
         });
      } else if (packet instanceof class_2828) {
         class_2828 p = (class_2828)packet;
         this.register("x", () -> {
            return LuaValue.valueOf(p.method_12269(0.0D));
         });
         this.register("y", () -> {
            return LuaValue.valueOf(p.method_12268(0.0D));
         });
         this.register("z", () -> {
            return LuaValue.valueOf(p.method_12274(0.0D));
         });
         this.register("yaw", () -> {
            return LuaValue.valueOf((double)p.method_12271(0.0F));
         });
         this.register("pitch", () -> {
            return LuaValue.valueOf((double)p.method_12270(0.0F));
         });
         this.register("on_ground", () -> {
            return LuaValue.valueOf(p.method_12273());
         });
      } else if (packet instanceof class_2797) {
         class_2797 p = (class_2797)packet;
         this.register("message", () -> {
            return LuaValue.valueOf(p.comp_945());
         });
      } else if (packet instanceof class_7439) {
         class_7439 p = (class_7439)packet;
         this.register("content", () -> {
            return LuaValue.valueOf(p.comp_763().getString());
         });
         this.register("hover_texts", () -> {
            LuaTable table = new LuaTable();
            this.collectHoverTexts(p.comp_763(), table);
            return table;
         });
      } else if (packet instanceof class_7438) {
         class_7438 p = (class_7438)packet;
         this.register("content", () -> {
            class_2561 c = p.comp_1103();
            if (c == null) {
               c = class_2561.method_43470(p.comp_1102().comp_1090());
            }

            return LuaValue.valueOf(((class_2561)c).getString());
         });
         this.register("hover_texts", () -> {
            LuaTable table = new LuaTable();
            class_2561 c = p.comp_1103();
            if (c != null) {
               this.collectHoverTexts(c, table);
            }

            return table;
         });
      } else if (packet instanceof class_7827) {
         class_7827 p = (class_7827)packet;
         this.register("content", () -> {
            return LuaValue.valueOf(p.comp_1097().getString());
         });
         this.register("hover_texts", () -> {
            LuaTable table = new LuaTable();
            this.collectHoverTexts(p.comp_1097(), table);
            return table;
         });
      } else if (packet instanceof class_2604) {
         class_2604 p = (class_2604)packet;
         this.register("id", () -> {
            return LuaValue.valueOf(p.method_11167());
         });
         this.register("uuid", () -> {
            return LuaValue.valueOf(p.method_11164().toString());
         });
         this.register("x", () -> {
            return LuaValue.valueOf(p.method_11175());
         });
         this.register("y", () -> {
            return LuaValue.valueOf(p.method_11174());
         });
         this.register("z", () -> {
            return LuaValue.valueOf(p.method_11176());
         });
         this.register("yaw", () -> {
            return LuaValue.valueOf((double)p.method_11168());
         });
         this.register("pitch", () -> {
            return LuaValue.valueOf((double)p.method_11171());
         });
      } else {
         label92: {
            boolean var10001;
            Throwable var23;
            if (packet instanceof class_2851) {
               label84: {
                  class_2851 var10 = (class_2851)packet;
                  class_2851 var10000 = var10;

                  class_10185 var24;
                  try {
                     var24 = var10000.comp_3139();
                  } catch (Throwable var18) {
                     var23 = var18;
                     var10001 = false;
                     break label84;
                  }

                  class_10185 var17 = var24;
                  this.register("forward", () -> {
                     return LuaValue.valueOf(var17.comp_3159());
                  });
                  this.register("backward", () -> {
                     return LuaValue.valueOf(var17.comp_3160());
                  });
                  this.register("left", () -> {
                     return LuaValue.valueOf(var17.comp_3161());
                  });
                  this.register("right", () -> {
                     return LuaValue.valueOf(var17.comp_3162());
                  });
                  this.register("jump", () -> {
                     return LuaValue.valueOf(var17.comp_3163());
                  });
                  this.register("shift", () -> {
                     return LuaValue.valueOf(var17.comp_3164());
                  });
                  break label92;
               }
            } else {
               label85: {
                  if (packet instanceof class_2716) {
                     class_2716 p = (class_2716)packet;
                     this.register("ids", () -> {
                        LuaTable ids = new LuaTable();
                        int i = 1;
                        IntListIterator var3 = p.method_36548().iterator();

                        while(var3.hasNext()) {
                           int id = (Integer)var3.next();
                           ids.set(i++, LuaValue.valueOf(id));
                        }

                        return ids;
                     });
                     break label92;
                  }

                  if (!(packet instanceof class_2708)) {
                     break label92;
                  }

                  class_2708 var13 = (class_2708)packet;
                  class_2708 var25 = var13;

                  int var27;
                  try {
                     var27 = var25.comp_3133();
                  } catch (Throwable var21) {
                     var23 = var21;
                     var10001 = false;
                     break label85;
                  }

                  int var26 = var27;
                  int id = var26;
                  var25 = var13;

                  class_10182 var30;
                  try {
                     var30 = var25.comp_3228();
                  } catch (Throwable var20) {
                     var23 = var20;
                     var10001 = false;
                     break label85;
                  }

                  class_10182 var28 = var30;
                  class_10182 change = var28;
                  var25 = var13;

                  Set var31;
                  try {
                     var31 = var25.comp_3229();
                  } catch (Throwable var19) {
                     var23 = var19;
                     var10001 = false;
                     break label85;
                  }

                  Set var29 = var31;
                  this.register("id", () -> {
                     return LuaValue.valueOf(id);
                  });
                  this.register("x", () -> {
                     return LuaValue.valueOf(change.comp_3148().field_1352);
                  });
                  this.register("y", () -> {
                     return LuaValue.valueOf(change.comp_3148().field_1351);
                  });
                  this.register("z", () -> {
                     return LuaValue.valueOf(change.comp_3148().field_1350);
                  });
                  this.register("delta_x", () -> {
                     return LuaValue.valueOf(change.comp_3149().field_1352);
                  });
                  this.register("delta_y", () -> {
                     return LuaValue.valueOf(change.comp_3149().field_1351);
                  });
                  this.register("delta_z", () -> {
                     return LuaValue.valueOf(change.comp_3149().field_1350);
                  });
                  this.register("yaw", () -> {
                     return LuaValue.valueOf((double)change.comp_3150());
                  });
                  this.register("pitch", () -> {
                     return LuaValue.valueOf((double)change.comp_3151());
                  });
                  this.register("relatives", () -> {
                     LuaTable relatives = new LuaTable();
                     int i = 1;
                     Iterator var3 = var29.iterator();

                     while(var3.hasNext()) {
                        class_2709 relative = (class_2709)var3.next();
                        relatives.set(i++, LuaValue.valueOf(relative.name()));
                     }

                     return relatives;
                  });
                  break label92;
               }
            }

            Throwable var3 = var23;
            throw new MatchException(var3.toString(), var3);
         }
      }

      this.registerMethod("cancel", new ZeroArgFunction() {
         public LuaValue call() {
            event.setCancelled();
            return LuaValue.NIL;
         }
      });
   }

   private void collectHoverTexts(class_2561 component, LuaTable table) {
      label60: {
         class_2568 hoverEvent = component.method_10866().method_10969();
         boolean var10001;
         Throwable var20;
         if (hoverEvent instanceof class_10613) {
            label63: {
               class_10613 var4 = (class_10613)hoverEvent;
               class_10613 var10000 = var4;

               class_2561 var21;
               try {
                  var21 = var10000.comp_3510();
               } catch (Throwable var12) {
                  var20 = var12;
                  var10001 = false;
                  break label63;
               }

               class_2561 var10 = var21;
               table.set(table.length() + 1, LuaValue.valueOf(var10.getString()));
               break label60;
            }
         } else if (hoverEvent instanceof class_10611) {
            label64: {
               class_10611 var6 = (class_10611)hoverEvent;
               class_10611 var22 = var6;

               class_5248 var23;
               try {
                  var23 = var22.comp_3508();
               } catch (Throwable var13) {
                  var20 = var13;
                  var10001 = false;
                  break label64;
               }

               class_5248 var17 = var23;
               Iterator var18 = var17.method_27682().iterator();

               while(true) {
                  if (!var18.hasNext()) {
                     break label60;
                  }

                  class_2561 line = (class_2561)var18.next();
                  table.set(table.length() + 1, LuaValue.valueOf(line.getString()));
               }
            }
         } else {
            label65: {
               if (!(hoverEvent instanceof class_10612)) {
                  break label60;
               }

               class_10612 var8 = (class_10612)hoverEvent;
               class_10612 var24 = var8;

               class_1799 var25;
               try {
                  var25 = var24.comp_3509();
               } catch (Throwable var14) {
                  var20 = var14;
                  var10001 = false;
                  break label65;
               }

               class_1799 var19 = var25;
               table.set(table.length() + 1, LuaValue.valueOf(var19.method_7964().getString()));
               break label60;
            }
         }

         Throwable var15 = var20;
         throw new MatchException(var15.toString(), var15);
      }

      Iterator var16 = component.method_10855().iterator();

      while(var16.hasNext()) {
         class_2561 sibling = (class_2561)var16.next();
         this.collectHoverTexts(sibling, table);
      }

   }
}
