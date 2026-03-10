package lol.ethane.feature.helper.impl.notification;

import java.util.ArrayList;
import java.util.List;
import lol.ethane.event.EventDispatcher;
import lol.ethane.event.defined.render.Render2DEvent;
import lol.ethane.event.defined.render.RenderBloomEvent;
import lol.ethane.event.subscriber.IEventSubscriber;
import lol.ethane.event.subscriber.Subscribe;
import lombok.Generated;
import net.minecraft.class_310;
import net.minecraft.class_408;

public class NotificationHelper implements IEventSubscriber {
   private final class_310 mc = class_310.method_1551();
   private final List<Notification> notificationList = new ArrayList();
   private static NotificationHelper instance;

   private NotificationHelper() {
   }

   private void render(boolean bloom) {
   }

   @Subscribe
   private void onRenderBloom(RenderBloomEvent event) {
      this.render(true);
   }

   @Subscribe
   private void onRender2D(Render2DEvent event) {
      this.render(false);
   }

   public void queue(Notification notification) {
      float height = 27.5F;
      float margin = 7.5F;
      float chatOffset = this.mc.field_1755 instanceof class_408 ? 18.0F : 0.0F;
      float targetY = (float)this.mc.method_22683().method_4502() - (height + margin) - chatOffset;
      notification.yAnimation.setValue((double)targetY);
      notification.yAnimation.setStartPoint((double)targetY);
      this.notificationList.add(notification);
   }

   public static void setInstance() {
      instance = new NotificationHelper();
      EventDispatcher.subscribe(instance);
   }

   @Generated
   public static NotificationHelper getInstance() {
      return instance;
   }
}
