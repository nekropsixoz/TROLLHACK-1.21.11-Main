package ru.noxium;

import java.io.File;
import lombok.Generated;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import ru.noxium.cfg.ConfigManager;
import ru.noxium.commands.CommandBootstrap;
import ru.noxium.config.GuiManager;
import ru.noxium.config.friend.FriendManager;
import ru.noxium.commands.macros.MacroManager;
import ru.noxium.event.EventManager;
import ru.noxium.event.RenderHandler;
import ru.noxium.event.render.RenderEvent;
import ru.noxium.module.api.Manager;
import ru.noxium.module.bind.BindingManager;
import ru.noxium.module.impl.combat.auraProcess.rotationProcess.ComponentManager;
import ru.noxium.rpc.RPC;
import ru.noxium.sound.SoundMixFilter;
import ru.noxium.ui.draggable.DraggableManager;
import ru.noxium.ui.gui.GuiClient;
import ru.noxium.ui.gui.GuiScreen;
import ru.noxium.util.render.animation.AnimationSystem;
import ru.noxium.util.render.backends.gl.GlBackend;
import ru.noxium.util.render.backends.gl.GlState;
import ru.noxium.util.render.core.Renderer2D;
import ru.noxium.util.render.text.FontObject;
import ru.noxium.util.render.text.FontRegistry;
import xks.accounts.Managment;
import xks.accounts.Profile;
import xks.accounts.User;

@Environment(EnvType.CLIENT)
public class Noxium implements ClientModInitializer {
   public static Noxium get;
   public Manager manager;
   public final String name = "noxiumDLC";
   public final String version = "v1";
   public final String title = "1.21.4";
   public final File preRoot = new File("C:\\", "noxiumDLC\\");
   public final File root = new File(this.preRoot, "noxiumDLC");
   public final String rootRes = "noxium";
   public static SoundMixFilter rtx;
   public GuiManager guiManager;
   public ComponentManager componentManager;
   public ConfigManager configManager;
   public FriendManager friendManager;
   public MacroManager macroManager;
   public GuiClient guiClient;
   private final RPC rpc = new RPC();
   private static GlBackend backend;
   private static Renderer2D renderer;
   private static FontObject uiFont;
   private static volatile boolean initialized = false;
   private static volatile boolean modInitialized = false;

   public static Renderer2D getRenderer() {
      ensureRendererInitialized();
      return renderer;
   }

   public static boolean isModInitialized() {
      return modInitialized;
   }

   public void onInitializeClient() {
      /***
       * Гайд:
       * 1. При билде (когда билдишь джарку и кидаешь мне) ты разкоменти
        "Auth.init();"
       * 2. Если ты просто в интелке то закоменти Auth.init(); и разкоментируй:
       * Managment.USER_PROFILE = new Profile(
       * "Nick",
       * 1,
       * "Owner"
       * );
       *
       * User.name = "sekynda";
       * User.role = "Owenr";
       * User.uid = "1";
       * User.client = "Fantom";
       *
       * там ты можешь менять уид и ник и роль а вот в билде нужен Auth.init; тк там
       * уже реальная проверка с сайта и защита клиента (главное не забудь поменять в
       * билде)
       *
       * Использование:
       * Получить ник - User.name(); (Тип String, полный путь:
       * xks.accounts.User.name(); )
       * Получить уид - User.uid(); (Тип String, полный путь: xks.accounts.User.uid();
       * )
       * Получить роль - User.role(); (Тип String, полный путь:
       * xks.accounts.User.role(); )
       * Получить версию - User.ver(); (Тип String, полный путь:
       * xks.accounts.User.ver(); )
       * Хвид:
       * С хвидом все довольно сложно Но ты можешь юзать: xks.go.XKS__N__LD.b(); Тип
       * String (оно берет с нативки поэтому в интелке оно работать не будет поэтому
       * сделай проверку на null тк в релизе все будет работать а в инетлке нет)
       */

       //Auth.init(); //ЭТО ПРИ БИЛДЕ

      //suka ti eblan

      Managment.USER_PROFILE = new Profile(// ЭТО КОГДА В ИНТЕЛКЕ
            "Nick", // ЭТО КОГДА В ИНТЕЛКЕ
            1, // ЭТО КОГДА В ИНТЕЛКЕ
            "Owner" // ЭТО КОГДА В ИНТЕЛКЕ
      ); // ЭТО КОГДА В ИНТЕЛКЕ
         // ЭТО КОГДА В ИНТЕЛКЕ
      User.name = "hatekdev"; // ЭТО КОГДА В ИНТЕЛКЕ
      User.role = "Owenr"; // ЭТО КОГДА В ИНТЕЛКЕ
      User.uid = "1"; // ЭТО КОГДА В ИНТЕЛКЕ
      User.client = "NoxiumClient"; // ЭТО КОГДА В ИНТЕЛКЕ

      System.out.println("[Noxium] onInitializeClient() START");
      get = this;
      this.manager = new Manager();
      this.friendManager = new FriendManager();
      this.macroManager = new MacroManager();
      this.macroManager.init();
      this.configManager = new ConfigManager();
      this.guiManager = new GuiManager();
      this.componentManager = new ComponentManager();
      this.componentManager.init();
      this.guiManager.init();
      GuiScreen.selectedTheme = this.guiManager.getCurrentTheme();
      GuiScreen.preSelectedTheme = this.guiManager.getCurrentTheme();
      GuiScreen.selectedCategories = this.guiManager.getCurrentCategory();
      rtx = SoundMixFilter.makeDistorterMixer();
      rtx.init();
      this.rpc.startRpc();
      CommandBootstrap.initialize();
      BindingManager.getInstance().initialize();
      if (this.configManager != null) {
         this.configManager.load();
         if (this.configManager.findConfig("default") != null) {
            this.configManager.loadConfig("default");
         }
      }

      this.guiClient = new GuiClient();
      ru.noxium.proxy.ProxyManager.load();
      RenderHandler.register();
      EventManager.register(this);
      modInitialized = true;
      System.out.println("[Noxium] onInitializeClient() COMPLETE - modInitialized=" + modInitialized);
   }

   public static void ensureRendererInitialized() {
      if (!initialized) {
         onInit();
      }
   }

   private static synchronized void onInit() {
      if (!initialized) {
         backend = new GlBackend();
         renderer = new Renderer2D(backend);
         FontRegistry.initialize(backend, renderer);
         uiFont = FontRegistry.INTER_MEDIUM;
         initialized = true;
      }
   }

   public static void onRender() {
      if (modInitialized) {
         GlState.Snapshot snapshot = GlState.push();

         try {
            if (!initialized) {
               onInit();
            }

            MinecraftClient client = MinecraftClient.getInstance();
            if (client == null || client.getWindow() == null) {
               return;
            }

            int width = client.getWindow().getFramebufferWidth();
            int height = client.getWindow().getFramebufferHeight();
            if (width <= 0 || height <= 0) {
               return;
            }

            AnimationSystem.getInstance().tick();
            DraggableManager draggableManager = DraggableManager.getInstance();
            draggableManager.beginFrame(client, renderer, width, height);
            boolean rendererBegun = false;

            try {
               renderer.begin(width, height);
               rendererBegun = true;

               try {
                  EventManager.call(new RenderEvent(client, renderer, uiFont, width, height));
               } finally {
                  if (rendererBegun) {
                     renderer.end();
                  }
               }
            } finally {
               draggableManager.endFrame();
            }
         } finally {
            GlState.pop(snapshot);
         }
      }
   }

   @Generated
   public Manager getManager() {
      return this.manager;
   }

   @Generated
   public String getName() {
      return "noxiumDLC";
   }

   @Generated
   public String getVersion() {
      return "v1";
   }

   @Generated
   public String getTitle() {
      return "1.21.4";
   }

   @Generated
   public File getPreRoot() {
      return this.preRoot;
   }

   @Generated
   public File getRoot() {
      return this.root;
   }

   @Generated
   public String getRootRes() {
      return "noxium";
   }

   @Generated
   public GuiManager getGuiManager() {
      return this.guiManager;
   }

   @Generated
   public ComponentManager getComponentManager() {
      return this.componentManager;
   }

   @Generated
   public ConfigManager getConfigManager() {
      return this.configManager;
   }

   @Generated
   public FriendManager getFriendManager() {
      return this.friendManager;
   }

   @Generated
   public MacroManager getMacroManager() {
      return this.macroManager;
   }

   @Generated
   public GuiClient getGuiClient() {
      return this.guiClient;
   }

   @Generated
   public RPC getRpc() {
      return this.rpc;
   }

   @Generated
   public void setManager(Manager manager) {
      this.manager = manager;
   }

   @Generated
   public void setGuiManager(GuiManager guiManager) {
      this.guiManager = guiManager;
   }

   @Generated
   public void setComponentManager(ComponentManager componentManager) {
      this.componentManager = componentManager;
   }

   @Generated
   public void setConfigManager(ConfigManager configManager) {
      this.configManager = configManager;
   }

   @Generated
   public void setFriendManager(FriendManager friendManager) {
      this.friendManager = friendManager;
   }

   @Generated
   public void setMacroManager(MacroManager macroManager) {
      this.macroManager = macroManager;
   }

   @Generated
   public void setGuiClient(GuiClient guiClient) {
      this.guiClient = guiClient;
   }
}
