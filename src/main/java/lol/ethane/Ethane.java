package lol.ethane;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import lol.aether.MgfxRenderer;
import lol.ethane.feature.binding.repository.BindRepository;
import lol.ethane.feature.command.defined.ConfigCommand;
import lol.ethane.feature.command.defined.ScriptsCommand;
import lol.ethane.feature.command.defined.module.BindCommand;
import lol.ethane.feature.command.defined.module.HideCommand;
import lol.ethane.feature.command.defined.module.ToggleCommand;
import lol.ethane.feature.command.repository.CommandRepository;
import lol.ethane.feature.drag.DraggableRepository;
import lol.ethane.feature.helper.impl.ServerObserver;
import lol.ethane.feature.helper.impl.notification.NotificationHelper;
import lol.ethane.feature.helper.impl.player.timer.TimerHelper;
import lol.ethane.feature.module.defined.combat.*;
import lol.ethane.feature.module.defined.combat.criticals.CriticalsModule;
import lol.ethane.feature.module.defined.combat.velocity.VelocityModule;
import lol.ethane.feature.module.defined.movement.FastStopModule;
import lol.ethane.feature.module.defined.movement.MovementFixModule;
import lol.ethane.feature.module.defined.movement.SprintModule;
import lol.ethane.feature.module.defined.movement.flight.FlightModule;
import lol.ethane.feature.module.defined.movement.noSlow.NoSlowModule;
import lol.ethane.feature.module.defined.movement.noWeb.NoWebModule;
import lol.ethane.feature.module.defined.movement.speed.SpeedModule;
import lol.ethane.feature.module.defined.other.AntiCheatSnifferModule;
import lol.ethane.feature.module.defined.other.AntiLogBypassModule;
import lol.ethane.feature.module.defined.other.CrystalOptimizerModule;
import lol.ethane.feature.module.defined.other.disabler.DisablerModule;
import lol.ethane.feature.module.defined.player.LegitScaffoldModule;
import lol.ethane.feature.module.defined.player.nofall.NoFallModule;
import lol.ethane.feature.module.defined.player.ScaffoldModule;
import lol.ethane.feature.module.defined.render.AnimationsModule;
import lol.ethane.feature.module.defined.render.ArrayListModule;
import lol.ethane.feature.module.defined.render.CameraClipModule;
import lol.ethane.feature.module.defined.render.ChatModule;
import lol.ethane.feature.module.defined.render.ClickGUIModule;
import lol.ethane.feature.module.defined.render.InterfaceModule;
import lol.ethane.feature.module.defined.render.NameTagsModule;
import lol.ethane.feature.module.defined.render.TargetESPModule;
import lol.ethane.feature.module.defined.render.TrafficConeModule;
import lol.ethane.feature.module.repository.ModuleRepository;
import lol.ethane.feature.scripting.registry.ScriptRepository;
import lol.ethane.utils.data.SaveUtil;
import lol.ethane.utils.simulation.PlayerSimulationCache;
import lombok.Generated;
import net.fabricmc.api.ModInitializer;

public class Ethane implements ModInitializer {
   private static Ethane instance;
   private final BindRepository bindRepository;
   private final DraggableRepository draggableRepository;
   private ModuleRepository moduleRepository;
   private CommandRepository commandRepository;
   private ScriptRepository scriptRepository;
   private MgfxRenderer renderer;
   private final ScheduledExecutorService autoSaveExecutor = Executors.newSingleThreadScheduledExecutor();

   public Ethane() {
      instance = this;
      this.bindRepository = new BindRepository();
      this.draggableRepository = new DraggableRepository();
   }

   public static Ethane getInstance() {
      return instance;
   }

   public void runInitializations() {
      System.setProperty("java.net.preferIPv4Stack", "true");
      ServerObserver.setInstance();
      NotificationHelper.setInstance();
      TimerHelper.setInstance();
      PlayerSimulationCache.getInstance();
      if (this.moduleRepository == null) {
         this.moduleRepository = ModuleRepository.from(new AuraModule(),new NameTagsModule(),new KillAura(),new AutoTotemModule(), new SpearTargetModule(), new CriticalsModule(), new VelocityModule(), new AutoShieldBreakModule(), new SuperKnockbackModule(), new NoFallModule(), new ScaffoldModule(), new LegitScaffoldModule(), new SprintModule(), new FastStopModule(), new NoWebModule(), new NoSlowModule(), new SpeedModule(), new FlightModule(), new MovementFixModule(), new ArrayListModule(), new CameraClipModule(), new AnimationsModule(), new ChatModule(), new ClickGUIModule(), new InterfaceModule(), new TargetESPModule(), new TrafficConeModule(), new AntiCheatSnifferModule(), new AntiLogBypassModule(), new DisablerModule(), new CrystalOptimizerModule());
      }

      SaveUtil.loadBindings();
      
      if (this.commandRepository == null) {
         this.commandRepository = CommandRepository.builder().put(new ToggleCommand(), new BindCommand(), new HideCommand(), new ScriptsCommand(), new ConfigCommand()).build();
      }

      if (this.scriptRepository == null) {
         this.scriptRepository = new ScriptRepository();
         this.scriptRepository.init();
      }

      SaveUtil.loadConfig();
      SaveUtil.loadDraggables();

      this.renderer = new MgfxRenderer();
      this.autoSaveExecutor.scheduleAtFixedRate(SaveUtil::saveAllAsync, 1L, 1L, TimeUnit.MINUTES);
   }

   public void shutdown() {
      if (this.moduleRepository != null) {
         ((ClickGUIModule)this.moduleRepository.getModule(ClickGUIModule.class)).setEnabled(false);
      }

      SaveUtil.saveAll();
      if (this.scriptRepository != null) {
         this.scriptRepository.unloadScripts();
      }

   }

   public static void setInstance() {
      instance = new Ethane();
   }

   @Override
   public void onInitialize() {
      setInstance();
      runInitializations();
   }

   @Generated
   public BindRepository getBindRepository() {
      return this.bindRepository;
   }

   @Generated
   public DraggableRepository getDraggableRepository() {
      return this.draggableRepository;
   }

   @Generated
   public ModuleRepository getModuleRepository() {
      return this.moduleRepository;
   }

   @Generated
   public CommandRepository getCommandRepository() {
      return this.commandRepository;
   }

   @Generated
   public ScriptRepository getScriptRepository() {
      return this.scriptRepository;
   }

   @Generated
   public MgfxRenderer getRenderer() {
      return this.renderer;
   }
}
