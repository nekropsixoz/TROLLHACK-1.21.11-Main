package ru.noxium.module.api;

import java.util.ArrayList;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import ru.noxium.Noxium;
import ru.noxium.module.impl.combat.AimHelper;
import ru.noxium.module.impl.combat.AntiAim;
import ru.noxium.module.impl.combat.AutoGapple;
import ru.noxium.module.impl.combat.AutoSwap;
import ru.noxium.module.impl.combat.AutoTotem;
import ru.noxium.module.impl.combat.AutoTNT;
import ru.noxium.module.impl.combat.AutoClicker;
import ru.noxium.module.impl.combat.BowHelper;
import ru.noxium.module.impl.combat.HitAura;
import ru.noxium.module.impl.combat.HitBox;
import ru.noxium.module.impl.combat.NoFriendDamage;
import ru.noxium.module.impl.combat.TNTMinecart;
import ru.noxium.module.impl.combat.TrashTalk;
import ru.noxium.module.impl.combat.Velocity;
import ru.noxium.module.impl.misc.ItemScroller;
import ru.noxium.module.impl.misc.STHelper;
import ru.noxium.module.impl.movement.GuiMove;
import ru.noxium.module.impl.movement.LongJump;
import ru.noxium.module.impl.movement.NoSlow;
import ru.noxium.module.impl.movement.NoWeb;
import ru.noxium.module.impl.movement.Speed;
import ru.noxium.module.impl.movement.Spider;
import ru.noxium.module.impl.movement.Sprint;
import ru.noxium.module.impl.movement.Strafe;
import ru.noxium.module.impl.movement.TargetStrafe;
import ru.noxium.module.impl.player.AhHelper;
import ru.noxium.module.impl.player.AutoBuy;
import ru.noxium.module.impl.player.AutoTool;
import ru.noxium.module.impl.player.ChestStealer;
import ru.noxium.module.impl.player.MiddleClick;
import ru.noxium.module.impl.player.NoDelay;
import ru.noxium.module.impl.player.NoPush;
import ru.noxium.module.impl.player.SPJoiner;
import ru.noxium.module.impl.visuals.Arrows;
import ru.noxium.module.impl.visuals.AspectRation;
import ru.noxium.module.impl.visuals.ClickGUI;
import ru.noxium.module.impl.visuals.CustomWorld;
import ru.noxium.module.impl.visuals.ESP;
import ru.noxium.module.impl.visuals.Hat;
import ru.noxium.module.impl.visuals.Hud;
import ru.noxium.module.impl.visuals.ItemESP;
import ru.noxium.module.impl.visuals.JumpCircle;
import ru.noxium.module.impl.render.JumpEffect;
import ru.noxium.module.impl.visuals.NameTags;
import ru.noxium.module.impl.visuals.NightVision;
import ru.noxium.module.impl.visuals.NoRender;
import ru.noxium.module.impl.visuals.Particles;
import ru.noxium.module.impl.visuals.RTXSounds;
import ru.noxium.module.impl.visuals.SkinManager;
import ru.noxium.module.impl.visuals.Svetych;
import ru.noxium.module.impl.visuals.SwingAnimation;
import ru.noxium.module.impl.visuals.SmoothCamera;
import ru.noxium.module.impl.visuals.TargetESP;
import ru.noxium.module.impl.visuals.Test;
import ru.noxium.module.impl.visuals.Trails;
import ru.noxium.module.impl.render.LineGlyphs;
import ru.noxium.module.impl.render.Charms;

@Environment(EnvType.CLIENT)
public class Manager {
   public ArrayList<Module> module = new ArrayList<>();

   public Manager() {
      this.module.add(new HitAura());
      this.module.add(new AntiAim());
      this.module.add(new Velocity());
      this.module.add(new TNTMinecart());
      this.module.add(new TrashTalk());
      this.module.add(new AutoGapple());
      this.module.add(new AutoSwap());
      this.module.add(new AutoTotem());
      this.module.add(new AutoTNT());
      this.module.add(new AutoClicker());
      this.module.add(new BowHelper());
      this.module.add(new HitBox());
      this.module.add(new NoFriendDamage());
      this.module.add(new AimHelper());
      this.module.add(new Sprint());
      this.module.add(new Speed());
      this.module.add(new NoWeb());
      this.module.add(new NoSlow());
      this.module.add(new GuiMove());
      this.module.add(new LongJump());
      this.module.add(new Spider());
      this.module.add(new Strafe());
      this.module.add(new TargetStrafe());
      this.module.add(new Test());
      this.module.add(new ESP());
      this.module.add(new JumpCircle());
      this.module.add(new JumpEffect());
      this.module.add(new Trails());
      this.module.add(new Hud());
      this.module.add(new Arrows());
      this.module.add(new ClickGUI());
      this.module.add(new ItemESP());
      this.module.add(new Svetych());
      this.module.add(new Particles());
      this.module.add(new NoRender());
      this.module.add(new SwingAnimation());
      this.module.add(new SmoothCamera());
      this.module.add(new Hat());
      this.module.add(new TargetESP());
      this.module.add(new SkinManager());
      this.module.add(new CustomWorld());
      this.module.add(new NightVision());
      this.module.add(new AspectRation());
      this.module.add(new RTXSounds());
      this.module.add(new NameTags());
      this.module.add(new NoPush());
      this.module.add(new NoDelay());
      this.module.add(new SPJoiner());
      this.module.add(new MiddleClick());
      this.module.add(new ItemScroller());
      this.module.add(new STHelper());
      this.module.add(new AhHelper());
      this.module.add(new AutoBuy());
      this.module.add(new AutoTool());
      this.module.add(new ChestStealer());
      this.module.add(new LineGlyphs());
      this.module.add(new Charms());
   }

   public ArrayList<Module> getModules() {
      return this.module;
   }

   public <T extends Module> T get(Class<T> clazz) {
      return this.module.stream().filter(module -> clazz.isAssignableFrom(module.getClass())).map(clazz::cast)
            .findFirst().orElse(null);
   }

   public Module getModule(Class<?> class1) {
      for (Module module1 : this.module) {
         if (module1.getClass() == class1) {
            return module1;
         }
      }

      return null;
   }

   public ArrayList<Module> getType(Category category) {
      ArrayList<Module> modules = new ArrayList<>();

      for (Module module1 : this.module) {
         if (module1.category == category) {
            modules.add(module1);
         }
      }

      return modules;
   }

   public Module[] getBind(int bind) {
      return Noxium.get.manager.module.stream().filter(module -> module.bind == bind).toArray(Module[]::new);
   }
}
