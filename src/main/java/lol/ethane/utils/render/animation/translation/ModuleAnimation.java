package lol.ethane.utils.render.animation.translation;

import lol.ethane.utils.render.animation.Animation;
import lol.ethane.utils.render.animation.Easing;
import lombok.Generated;

public final class ModuleAnimation {
   private final Animation xAnimation;
   private final Animation yAnimation;

   public ModuleAnimation(double x, double y) {
      this.xAnimation = new Animation(Easing.OUT_EXPO, 650L);
      this.yAnimation = new Animation(Easing.ELASTIC_BOUNCE, 1200L);
      this.xAnimation.setValue(x);
      this.xAnimation.setStartPoint(x);
      this.xAnimation.setEndPoint(x);
      this.yAnimation.setValue(y);
      this.yAnimation.setStartPoint(y);
      this.yAnimation.setEndPoint(y);
   }

   public void animate(double newX, double newY) {
      this.xAnimation.setDuration(650L);
      this.yAnimation.setDuration(750L);
      this.yAnimation.funny = true;
      this.xAnimation.process(newX);
      this.yAnimation.process(newY);
   }

   public double getX() {
      return this.xAnimation.getValue();
   }

   public double getY() {
      return this.yAnimation.getValue();
   }

   public void setX(double x) {
      this.xAnimation.setValue(x);
      this.xAnimation.setStartPoint(x);
      this.xAnimation.setEndPoint(x);
   }

   public void setY(double y) {
      this.yAnimation.setValue(y);
      this.yAnimation.setStartPoint(y);
      this.yAnimation.setEndPoint(y);
   }

   @Generated
   public Animation getXAnimation() {
      return this.xAnimation;
   }

   @Generated
   public Animation getYAnimation() {
      return this.yAnimation;
   }
}
