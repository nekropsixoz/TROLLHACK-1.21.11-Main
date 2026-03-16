package ru.noxium.ui.gui;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import ru.noxium.module.api.Category;
import ru.noxium.module.api.Module;
import ru.noxium.module.api.Theme;
import ru.noxium.module.api.setting.impl.BindSettings;
import ru.noxium.module.api.setting.impl.BooleanSetting;
import ru.noxium.module.api.setting.impl.HueSetting;
import ru.noxium.module.api.setting.impl.SliderSetting;
import ru.noxium.module.api.setting.impl.StringSetting;
import ru.noxium.util.render.math.ScrollUtil;
import ru.noxium.util.render.math.animation.Animation;
import ru.noxium.util.render.math.animation.anim.util.Animation2;
import ru.noxium.util.render.math.animation.anim2.Easing;
import ru.noxium.util.render.math.animation.impl.EaseInOutQuad;

@Environment(EnvType.CLIENT)
public class GuiScreen {
   public static MinecraftClient mc = MinecraftClient.getInstance();
   public static Animation mainAnimation = new EaseInOutQuad(200, 1.0);
   public static Animation categoryAnimation = new EaseInOutQuad(500, 1.0);
   public static Animation moduleAnimation = new EaseInOutQuad(500, 1.0);
   public static Animation animation14 = new EaseInOutQuad(500, 1.0);
   public static Animation animation15 = new EaseInOutQuad(1000, 1.0);
   public static ru.noxium.util.render.math.animation.anim2.Animation alpha = new ru.noxium.util.render.math.animation.anim2.Animation(
         Easing.EASE_OUT_SINE, 1500L);
   public static ru.noxium.util.render.math.animation.anim.Animation animation = new ru.noxium.util.render.math.animation.anim.Animation();
   public static Animation2 alphaPC = new Animation2();
   public static Animation2 settingPC = new Animation2();
   public static Animation2 alphaPC2 = new Animation2();
   public static Animation2 alphaPC3 = new Animation2();
   public static HueSetting activeColorPicker = null;
   public static float colorPickerX = 0.0F;
   public static float colorPickerY = 0.0F;
   public static boolean pickingSaturationBrightness = false;
   public static boolean pickingHue = false;
   public static boolean pickingAlpha = false;
   public static BindSettings activeBindSetting = null;
   public static StringSetting activeStringSetting = null;
   public static SliderSetting activeSliderSetting = null;
   public static Module activeModuleBind = null;
   public static float sliderX = 0.0F;
   public static float sliderY = 0.0F;
   public static float sliderWidth = 0.0F;
   public static String searchText = "";
   public static boolean activeSearch = false;
   public static long lastBackspaceTime = 0L;
   public static boolean backspaceHeld = false;
   public static long firstBackspacePressTime = 0L;
   public static boolean showClientSettingsPopup = false;
   public static BooleanSetting clientBlurSetting = new BooleanSetting("Блюр?", true);
   public static boolean exit = false;
   public static float x;
   public static float y;
   public static float width;
   public static float height;
   public static int currentMouseX = 0;
   public static int currentMouseY = 0;
   public static Category[] categories;
   public static Theme selectedTheme;
   public static Theme preSelectedTheme;
   public static Theme[] themes;
   public static Category selectedCategories;
   public static List<Module> modules;
   private static ScrollUtil scrollUtil;
   public static Set<Module> openSettingsModules = new HashSet<>();
   public static Map<Module, Animation2> moduleSettingsAnimations = new HashMap<>();
   public static Map<Module, Animation2> moduleSettingsAlphaAnimations = new HashMap<>();
   public static Map<Module, Animation2> moduleBindAnimations = new HashMap<>();
   public static Map<SliderSetting, Animation2> sliderAnimations = new HashMap<>();
   public static Map<Category, ScrollUtil> categoryScrolls = new HashMap<>();

   public static ScrollUtil getCategoryScroll(Category category) {
      return categoryScrolls.computeIfAbsent(category, k -> new ScrollUtil());
   }

   public static ScrollUtil getScrollUtil() {
      if (scrollUtil == null) {
         scrollUtil = new ScrollUtil();
      }

      return scrollUtil;
   }

   public static Animation2 getModuleSettingsAnimation(Module module) {
      return moduleSettingsAnimations.computeIfAbsent(module, k -> new Animation2());
   }

   public static Animation2 getModuleSettingsAlphaAnimation(Module module) {
      return moduleSettingsAlphaAnimations.computeIfAbsent(module, k -> new Animation2());
   }

   public static Animation2 getModuleBindAnimation(Module module) {
      Animation2 anim = moduleBindAnimations.computeIfAbsent(module, k -> new Animation2());
      if (module.bind != -1 && anim.getDuration() == 0.0 && anim.getValue() == 0.0) {
         anim.setValue(1.0);
      }

      return anim;
   }

   public static Animation2 getSliderAnimation(SliderSetting slider) {
      return sliderAnimations.computeIfAbsent(slider, k -> {
         Animation2 newAnim = new Animation2();
         float targetProgress = (slider.current - slider.minimum) / (slider.maximum - slider.minimum);
         newAnim.setValue(targetProgress);
         return newAnim;
      });
   }
}
