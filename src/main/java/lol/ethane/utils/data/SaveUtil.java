package lol.ethane.utils.data;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.gson.internal.LinkedTreeMap;
import com.ibm.icu.impl.Pair;
import java.awt.Color;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import lol.ethane.Ethane;
import lol.ethane.feature.binding.BindingService;
import lol.ethane.feature.binding.IBindable;
import lol.ethane.feature.binding.type.InputType;
import lol.ethane.feature.drag.DraggableComponent;
import lol.ethane.feature.module.Module;
import lol.ethane.feature.module.UnknownModuleException;
import lol.ethane.feature.module.property.Property;
import net.minecraft.class_310;

public final class SaveUtil {
   private static final ExecutorService SAVE_EXECUTOR = Executors.newSingleThreadExecutor((r) -> {
      Thread thread = new Thread(r, "Ethane-Save-Thread");
      thread.setDaemon(true);
      return thread;
   });
   public static final File DIRECTORY;
   private static final Gson GSON;
   private static volatile boolean dirty;
   private static final BindingService BINDING_SERVICE;

   public static void markDirty() {
      dirty = true;
   }

   public static boolean isDirty() {
      return dirty;
   }

   public static void saveAll() {
      if (isDirty()) {
         saveConfig();
         saveBindings();
         saveDraggables();
         dirty = false;
      }
   }

   public static void saveAllAsync() {
      if (isDirty()) {
         SAVE_EXECUTOR.execute(SaveUtil::saveAll);
      }
   }

   public static void saveBindings() {
      try {
         if (!DIRECTORY.exists()) {
            DIRECTORY.mkdir();
         }

         File file = new File(DIRECTORY, "bindings.json");
         JsonArray bindingsArray = new JsonArray();
         Iterator var2 = BINDING_SERVICE.getBindingMap().keySet().iterator();

         while(var2.hasNext()) {
            Pair<Integer, InputType> binding = (Pair)var2.next();
            JsonObject bindingJson = new JsonObject();
            bindingJson.addProperty("keyCode", (Number)binding.first);
            JsonArray bindablesArray = new JsonArray();
            Iterator var6 = BINDING_SERVICE.getBindingMap().get(binding).iterator();

            while(var6.hasNext()) {
               IBindable bindable = (IBindable)var6.next();
               if (bindable instanceof Module) {
                  Module module = (Module)bindable;
                  JsonObject moduleJson = new JsonObject();
                  moduleJson.addProperty("module", module.getId());
                  bindablesArray.add(moduleJson);
               }
            }

            bindingJson.add("bindables", bindablesArray);
            bindingsArray.add(bindingJson);
         }

         Files.writeString(file.toPath(), GSON.toJson(bindingsArray), new OpenOption[0]);
      } catch (Exception var10) {
         var10.printStackTrace();
      }

   }

   public static void loadBindings() {
      if (Ethane.getInstance() == null || Ethane.getInstance().getModuleRepository() == null) {
         return;
      }
      try {
         FileReader reader = new FileReader(new File(DIRECTORY, "bindings.json"));

         try {
            JsonArray bindingsArray = JsonParser.parseReader(reader).getAsJsonArray();
            Iterator var2 = bindingsArray.iterator();

            while(var2.hasNext()) {
               JsonElement bindingElement = (JsonElement)var2.next();
               JsonObject bindingJson = bindingElement.getAsJsonObject();
               int keyCode = bindingJson.get("keyCode").getAsInt();
               InputType inputType = keyCode < 10 ? InputType.MOUSE : InputType.KEYBOARD;
               JsonArray bindablesArray = bindingJson.getAsJsonArray("bindables");
               Iterator var8 = bindablesArray.iterator();

               while(var8.hasNext()) {
                  JsonElement bindableElement = (JsonElement)var8.next();
                  JsonObject bindableJson = bindableElement.getAsJsonObject();
                  if (bindableJson.has("module")) {
                     String moduleID = bindableJson.get("module").getAsString();
                     Module module = Ethane.getInstance().getModuleRepository().getModule(moduleID);
                     BINDING_SERVICE.register(keyCode, module, inputType);
                  }
               }
            }
         } catch (Throwable var14) {
            try {
               reader.close();
            } catch (Throwable var13) {
               var14.addSuppressed(var13);
            }

            throw var14;
         }

         reader.close();
      } catch (UnknownModuleException | IOException var15) {
         var15.printStackTrace();
      }

   }

   public static void saveConfig() {
      try {
         if (!DIRECTORY.exists()) {
            DIRECTORY.mkdir();
         }

         File file = new File(DIRECTORY, "config.json");
         Files.writeString(file.toPath(), GSON.toJson(Ethane.getInstance().getModuleRepository().getModules()), new OpenOption[0]);
      } catch (Exception var1) {
         var1.printStackTrace();
      }

   }

   public static void loadConfig() {
      loadConfig("config.json");
   }

   public static void loadConfig(String jsonString) {
      try {
         if (!jsonString.trim().startsWith("{") && !jsonString.trim().startsWith("[")) {
            File file = new File(DIRECTORY, jsonString);
            if (file.exists()) {
               jsonString = Files.readString(file.toPath());
            }
         }

         List<?> jsonModules = (List)GSON.fromJson(jsonString, List.class);
         Iterator var2 = jsonModules.iterator();

         while(var2.hasNext()) {
            Object jsonModuleObj = var2.next();
            LinkedTreeMap<?, ?> jsonModule = (LinkedTreeMap)jsonModuleObj;
            String jsonModuleID = (String)jsonModule.get("name");
            Boolean jsonEnabled = (Boolean)jsonModule.get("enabled");
            Boolean jsonVisible = (Boolean)jsonModule.get("visible");
            List<?> jsonProperties = (List)jsonModule.get("properties");
            
            if (Ethane.getInstance().getModuleRepository() == null) {
               continue;
            }
            
            Iterator var9 = Ethane.getInstance().getModuleRepository().getModules().iterator();

            while(var9.hasNext()) {
               Module clientModule = (Module)var9.next();
               if (jsonModuleID.equals(clientModule.getId())) {
                  if (jsonEnabled != null && jsonEnabled != clientModule.isEnabled()) {
                     clientModule.setEnabled(jsonEnabled);
                  }

                  if (jsonVisible != null && jsonVisible != clientModule.isVisible()) {
                     clientModule.setVisible(jsonVisible);
                  }

                  Iterator var11 = jsonProperties.iterator();

                  while(var11.hasNext()) {
                     Object jsonPropertyObj = var11.next();
                     LinkedTreeMap<?, ?> jsonProperty = (LinkedTreeMap)jsonPropertyObj;
                     String propertyName = (String)jsonProperty.get("name");
                     Object propertyValue = jsonProperty.get("value");
                     Iterator var16 = clientModule.getPropertyList().iterator();

                     while(var16.hasNext()) {
                        Property<?> clientProperty = (Property)var16.next();
                        if (propertyName.equals(clientProperty.getId())) {
                           clientProperty.applyValue(propertyValue);
                        }
                     }
                  }
               }
            }
         }
      } catch (Exception var18) {
         var18.printStackTrace();
      }

   }

   public static void saveDraggables() {
      try {
         if (!DIRECTORY.exists()) {
            DIRECTORY.mkdir();
         }

         File file = new File(DIRECTORY, "draggables.json");
         JsonArray array = new JsonArray();
         Iterator var2 = Ethane.getInstance().getDraggableRepository().getDraggables().iterator();

         while(var2.hasNext()) {
            DraggableComponent draggable = (DraggableComponent)var2.next();
            JsonObject json = new JsonObject();
            json.addProperty("name", draggable.getName());
            json.addProperty("x", draggable.getX());
            json.addProperty("y", draggable.getY());
            array.add(json);
         }

         Files.writeString(file.toPath(), GSON.toJson(array), new OpenOption[0]);
      } catch (Exception var5) {
         var5.printStackTrace();
      }

   }

   public static void loadDraggables() {
      try {
         File file = new File(DIRECTORY, "draggables.json");
         if (!file.exists()) {
            return;
         }

         JsonArray array = JsonParser.parseString(Files.readString(file.toPath())).getAsJsonArray();
         Iterator var2 = array.iterator();

         while(var2.hasNext()) {
            JsonElement element = (JsonElement)var2.next();
            JsonObject json = element.getAsJsonObject();
            String name = json.get("name").getAsString();
            DraggableComponent draggable = Ethane.getInstance().getDraggableRepository().get(name);
            if (draggable != null) {
               if (json.has("x")) {
                  draggable.setX(json.get("x").getAsFloat());
               }

               if (json.has("y")) {
                  draggable.setY(json.get("y").getAsFloat());
               }
            }
         }
      } catch (Exception var7) {
         var7.printStackTrace();
      }

   }

   static {
      DIRECTORY = new File(class_310.method_1551().field_1697, File.separator + "ethane" + File.separator);
      GSON = (new GsonBuilder()).registerTypeAdapter(Color.class, new JsonSerializer<Color>() {
         public JsonElement serialize(Color src, Type typeOfSrc, JsonSerializationContext context) {
            return new JsonPrimitive(src.getRGB());
         }
      }).registerTypeAdapter(Color.class, new JsonDeserializer<Color>() {
         public Color deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            return new Color(json.getAsInt(), true);
         }
      }).excludeFieldsWithoutExposeAnnotation().create();
      dirty = false;
      BINDING_SERVICE = Ethane.getInstance().getBindRepository().getBindingService();
   }
}
