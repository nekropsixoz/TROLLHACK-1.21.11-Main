package lol.ethane.feature.helper.impl.notification;

import lol.ethane.utils.render.animation.Animation;
import lol.ethane.utils.render.animation.Easing;

public class Notification {
   public final String title;
   public final String description;
   public final long expiration;
   public float width;
   public final Animation xAnimation;
   public final Animation yAnimation;

   public Notification(String title, String description, long expiration) {
      this.xAnimation = new Animation(Easing.OUT_QUART, 400L);
      this.yAnimation = new Animation(Easing.OUT_QUART, 400L);
      this.title = title;
      this.description = description;
      this.expiration = System.currentTimeMillis() + expiration;
   }
}
