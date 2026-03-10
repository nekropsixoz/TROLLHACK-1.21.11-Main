package lol.ethane.feature.binding.repository;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import lol.ethane.event.EventDispatcher;
import lol.ethane.event.defined.press.KeyPressEvent;
import lol.ethane.event.subscriber.IEventSubscriber;
import lol.ethane.event.subscriber.Subscribe;
import lol.ethane.feature.binding.BindingService;
import lol.ethane.feature.binding.type.InputType;
import lombok.Generated;
import org.lwjgl.glfw.GLFW;

public class BindRepository implements IEventSubscriber {
   private final BindingService bindingService = new BindingService();
   private final Map<String, Integer> namedBindingMap = new HashMap();
   public static final String GLFW_KEY_PREFIX = "GLFW_KEY_";

   public BindRepository() {
      try {
         Field[] var1 = GLFW.class.getDeclaredFields();
         int var2 = var1.length;

         for(int var3 = 0; var3 < var2; ++var3) {
            Field field = var1[var3];
            if (field.getName().startsWith("GLFW_KEY_")) {
               this.namedBindingMap.put(field.getName().substring("GLFW_KEY_".length()), field.getInt((Object)null));
            }
         }

         for(int i = 0; i < 10; ++i) {
            this.namedBindingMap.put("MOUSE_" + i, i);
         }

         this.namedBindingMap.put("CLEAR", -1);
      } catch (IllegalAccessException var5) {
         var5.printStackTrace();
      }

      EventDispatcher.subscribe(this);
   }

   @Subscribe
   public void onKeyPress(KeyPressEvent event) {
      this.bindingService.dispatch(event.getInteractionCode(), InputType.KEYBOARD);
   }

   @Generated
   public BindingService getBindingService() {
      return this.bindingService;
   }

   @Generated
   public Map<String, Integer> getNamedBindingMap() {
      return this.namedBindingMap;
   }
}
