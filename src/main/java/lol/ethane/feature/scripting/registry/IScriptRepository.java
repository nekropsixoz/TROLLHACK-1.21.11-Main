package lol.ethane.feature.scripting.registry;

import java.util.Collection;

public interface IScriptRepository<T> {
   void init();

   void add(T... var1);

   void remove(T... var1);

   int size();

   Collection<T> values();
}
