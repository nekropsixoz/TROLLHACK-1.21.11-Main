package lol.ethane.event;

public class EventCancellable {
   private boolean cancelled;

   public void setCancelled() {
      this.cancelled = true;
   }

   public boolean isCancelled() {
      return this.cancelled;
   }
}
