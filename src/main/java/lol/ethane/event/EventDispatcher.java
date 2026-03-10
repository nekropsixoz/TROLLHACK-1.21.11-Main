package lol.ethane.event;

import lol.ethane.event.registry.EventRegistry;

public final class EventDispatcher {
   private static final EventRegistry eventRegistry = new EventRegistry();

   public static void subscribe(Object subscriber) {
      eventRegistry.subscribe(subscriber);
   }

   public static void unsubscribe(Object subscriber) {
      eventRegistry.unsubscribe(subscriber);
   }

   public static void dispatch(Object event) {
      eventRegistry.dispatch(event);
   }
}
