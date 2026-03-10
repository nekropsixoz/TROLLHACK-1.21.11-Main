package lol.ethane.event.subscriber;

public interface IEventSubscriber {
   default boolean isHandlingEvents() {
      return true;
   }
}
