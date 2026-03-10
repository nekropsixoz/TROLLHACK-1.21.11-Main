package lol.ethane.event.registry;

import java.lang.invoke.CallSite;
import java.lang.invoke.ConstantCallSite;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.invoke.MethodHandles.Lookup;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import lol.ethane.event.listener.ListenerMethod;
import lol.ethane.event.subscriber.IEventSubscriber;
import lol.ethane.event.subscriber.Subscribe;

public final class EventRegistry {
   private final Map<Class<?>, List<ListenerMethod>> subscriberMap = new HashMap();
   private static final Lookup LOOKUP = MethodHandles.lookup();

   private void subscribe(Object instance, Class<?> clazzOwner) {
      IEventSubscriber listener = (IEventSubscriber)instance;
      Method[] var4 = clazzOwner.getDeclaredMethods();
      int var5 = var4.length;

      for(int var6 = 0; var6 < var5; ++var6) {
         Method method = var4[var6];
         Subscribe subscribe = (Subscribe)method.getDeclaredAnnotation(Subscribe.class);
         if (subscribe != null && !Modifier.isStatic(method.getModifiers())) {
            Class<?> type = method.getParameterTypes()[0];
            MethodType methodType = MethodType.methodType(Void.TYPE, type);

            try {
               Lookup privateLookup = Modifier.isPrivate(method.getModifiers()) ? MethodHandles.privateLookupIn(clazzOwner, LOOKUP) : LOOKUP;
               MethodHandle methodHandle = privateLookup.findVirtual(clazzOwner, method.getName(), methodType);
               CallSite site = new ConstantCallSite(methodHandle);
               ListenerMethod listenerMethod = new ListenerMethod(subscribe.priority(), site, listener);
               ((List)this.subscriberMap.computeIfAbsent(type, (x) -> {
                  return new ArrayList();
               })).add(listenerMethod);
            } catch (NoSuchMethodException | IllegalAccessException var15) {
               throw new RuntimeException("Error subscribing event: " + method.getName(), var15);
            }
         }
      }

   }

   public void subscribe(Object subscriber) {
      this.subscribe(subscriber, subscriber.getClass());

      for(Class parent = subscriber.getClass().getSuperclass(); parent != Object.class; parent = parent.getSuperclass()) {
         this.subscribe(subscriber, parent);
      }

      this.sortSubscribers();
   }

   public void unsubscribe(Object subscriber) {
      Iterator var2 = this.subscriberMap.values().iterator();

      while(var2.hasNext()) {
         List<ListenerMethod> listenerMethods = (List)var2.next();
         listenerMethods.removeIf((listenerMethod) -> {
            return listenerMethod.getSubscriber() == subscriber;
         });
      }

   }

   private void sortSubscribers() {
      Iterator var1 = this.subscriberMap.values().iterator();

      while(var1.hasNext()) {
         List<ListenerMethod> callsiteList = (List)var1.next();
         callsiteList.sort(Comparator.comparingInt(ListenerMethod::getPriority));
      }

   }

   public void dispatch(Object event) {
      List<ListenerMethod> listenerMethods = (List)this.subscriberMap.get(event.getClass());
      if (listenerMethods != null) {
         for(int i = 0; i < listenerMethods.size(); ++i) {
            ListenerMethod listenerMethod = (ListenerMethod)listenerMethods.get(i);
            if (listenerMethod.invoke(event)) {
               break;
            }
         }
      }

   }
}
