package lol.ethane.event.listener;

import java.lang.invoke.CallSite;
import java.lang.invoke.MethodHandle;
import lol.ethane.event.EventCancellable;
import lol.ethane.event.subscriber.IEventSubscriber;
import lombok.Generated;

public final class ListenerMethod {
   private final int priority;
   private final CallSite callSite;
   private final IEventSubscriber subscriber;
   private final MethodHandle dynamicInvoker;

   public ListenerMethod(int priority, CallSite callSite, IEventSubscriber subscriber) {
      this.priority = -priority;
      this.callSite = callSite;
      this.subscriber = subscriber;
      this.dynamicInvoker = callSite.dynamicInvoker();
   }

   public boolean invoke(Object event) {
      if (this.subscriber.isHandlingEvents()) {
         try {
            this.dynamicInvoker.invoke(this.subscriber, event);
         } catch (Throwable var3) {
            var3.printStackTrace();
            throw new RuntimeException("Error invoking event", var3);
         }

         boolean var10000;
         if (event instanceof EventCancellable) {
            EventCancellable cancellable = (EventCancellable)event;
            if (cancellable.isCancelled()) {
               var10000 = true;
               return var10000;
            }
         }

         var10000 = false;
         return var10000;
      } else {
         return false;
      }
   }

   @Generated
   public int getPriority() {
      return this.priority;
   }

   @Generated
   public CallSite getCallSite() {
      return this.callSite;
   }

   @Generated
   public IEventSubscriber getSubscriber() {
      return this.subscriber;
   }
}
