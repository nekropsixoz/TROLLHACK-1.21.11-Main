package lol.ethane.feature.module.repository;

import com.google.common.collect.ClassToInstanceMap;
import com.google.common.collect.ImmutableClassToInstanceMap;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.MutableClassToInstanceMap;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import lol.ethane.event.EventDispatcher;
import lol.ethane.feature.module.Module;
import lol.ethane.feature.module.ModuleCategory;
import lol.ethane.feature.module.UnknownModuleException;

public class ModuleRepository {
   private final ClassToInstanceMap<Module> classToInstanceMap = MutableClassToInstanceMap.create();
   private final Map<String, Module> idToInstanceMap;

   private ModuleRepository(ClassToInstanceMap<Module> classToInstanceMap, Map<String, Module> idToInstanceMap) {
      this.classToInstanceMap.putAll(classToInstanceMap);
      this.idToInstanceMap = new HashMap(idToInstanceMap);
   }

   public void register(Module module) {
      this.classToInstanceMap.put(module.getClass(), module);
      this.idToInstanceMap.put(module.getId(), module);
   }

   public void unregister(Module module) {
      module.setEnabled(false);
      this.classToInstanceMap.remove(module.getClass(), module);
      this.idToInstanceMap.remove(module.getId(), module);
      EventDispatcher.unsubscribe(module);
   }

   public void findModule(String id, Consumer<Module> moduleConsumer, Consumer<UnknownModuleException> exceptionHandler) {
      try {
         moduleConsumer.accept(this.getModule(id));
      } catch (UnknownModuleException var5) {
         exceptionHandler.accept(var5);
      }

   }

   @SuppressWarnings("unchecked")
   public <T extends Module> T getModule(Class<T> moduleClass) {
      return (T)this.classToInstanceMap.getInstance(moduleClass);
   }

   public Module getModule(String id) throws UnknownModuleException {
      Module module = (Module)this.idToInstanceMap.get(id);
      if (module == null) {
         throw new UnknownModuleException(id);
      } else {
         return module;
      }
   }

   public Collection<Module> getModules() {
      return this.idToInstanceMap.values();
   }

   public Collection<Module> getModulesInCategory(ModuleCategory category) {
      return (Collection)this.idToInstanceMap.values().stream().filter((module) -> {
         return category.equals(module.getCategory());
      }).collect(Collectors.toList());
   }

   public static ModuleRepository from(Module... modules) {
      ModuleRepository.Builder builder = builder();
      Module[] var2 = modules;
      int var3 = modules.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         Module module = var2[var4];
         builder.register(module);
      }

      return builder.build();
   }

   public static ModuleRepository.Builder builder() {
      return new ModuleRepository.Builder();
   }

   public static class Builder {
      private final com.google.common.collect.ImmutableClassToInstanceMap.Builder<Module> classToInstanceMapBuilder = ImmutableClassToInstanceMap.builder();
      private final com.google.common.collect.ImmutableMap.Builder<String, Module> idToInstanceMapBuilder = ImmutableMap.builder();

      private Builder() {
      }

      @SuppressWarnings("unchecked")
      public void register(Module module) {
         this.classToInstanceMapBuilder.put((Class<Module>)module.getClass(), module);
         this.idToInstanceMapBuilder.put(module.getId(), module);
      }

      public ModuleRepository build() {
         return new ModuleRepository(this.classToInstanceMapBuilder.build(), this.idToInstanceMapBuilder.build());
      }
   }
}
