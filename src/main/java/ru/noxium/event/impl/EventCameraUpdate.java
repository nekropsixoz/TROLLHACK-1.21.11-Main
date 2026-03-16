package ru.noxium.event.impl;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import ru.noxium.event.Event;

@Environment(EnvType.CLIENT)
public class EventCameraUpdate extends Event {
   private double x;
   private double y;
   private double z;

   public EventCameraUpdate(double x, double y, double z) {
      this.x = x;
      this.y = y;
      this.z = z;
   }

   public double getX() {
      return x;
   }

   public void setX(double x) {
      this.x = x;
   }

   public double getY() {
      return y;
   }

   public void setY(double y) {
      this.y = y;
   }

   public double getZ() {
      return z;
   }

   public void setZ(double z) {
      this.z = z;
   }
}
