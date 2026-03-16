package ru.noxium.ui.gui.component.main;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import ru.noxium.Noxium;
import ru.noxium.module.api.setting.impl.HueSetting;
import ru.noxium.module.api.setting.impl.SliderSetting;
import ru.noxium.ui.gui.GuiScreen;
import ru.noxium.util.render.math.ScaleHelper;

@Environment(EnvType.CLIENT)
public class GuiMouseDragged extends GuiScreen {
   public static boolean mouseDragged(double pMouseX, double pMouseY, int pButton, double pDragX, double pDragY) {
      int mouseX = (int) ScaleHelper.calc((float) pMouseX, (float) pMouseY)[0];
      int mouseY = (int) ScaleHelper.calc((float) pMouseX, (float) pMouseY)[1];
      if (GuiScreen.activeColorPicker != null && GuiScreen.activeColorPicker instanceof HueSetting) {
         HueSetting hueSetting = GuiScreen.activeColorPicker;
         float pickerX = GuiScreen.colorPickerX;
         float pickerY = GuiScreen.colorPickerY;
         if (pickerX != 0.0F || pickerY != 0.0F) {
            float paletteWidth = 63.92F;
            float paletteHeight = 47.02F;
            float paletteX = pickerX + 5.0F;
            float paletteY = pickerY + 5.0F;
            if (GuiScreen.pickingSaturationBrightness) {
               float x = Math.max(0.0F, Math.min(mouseX - paletteX, paletteWidth));
               float y = Math.max(0.0F, Math.min(mouseY - paletteY, paletteHeight));
               hueSetting.saturation = x / paletteWidth;
               hueSetting.brightness = 1.0F - y / paletteHeight;
               if (Noxium.get.configManager != null) {
                  Noxium.get.configManager.autoSave();
               }

               return true;
            }

            if (GuiScreen.pickingHue) {
               float hueSliderWidth = 64.0F;
               float hueSliderHeight = 2.59F;
               float hueSliderX = pickerX + 5.0F;
               float hueSliderY = paletteY + paletteHeight + 5.0F;
               float huePos = Math.max(0.0F, Math.min(mouseX - hueSliderX, hueSliderWidth));
               hueSetting.current = huePos / hueSliderWidth * 106.0F;
               if (Noxium.get.configManager != null) {
                  Noxium.get.configManager.autoSave();
               }

               return true;
            }
         }
      }

      if (GuiScreen.activeSliderSetting != null) {
         SliderSetting sliderSetting = GuiScreen.activeSliderSetting;
         float progress = (mouseX - GuiScreen.sliderX) / GuiScreen.sliderWidth;
         progress = Math.max(0.0F, Math.min(1.0F, progress));

         float newValue = sliderSetting.minimum + (sliderSetting.maximum - sliderSetting.minimum) * progress;

         // Snap to increment if it's set
         if (sliderSetting.increment > 0) {
            newValue = Math.round(newValue / sliderSetting.increment) * sliderSetting.increment;
            newValue = Math.max(sliderSetting.minimum, Math.min(sliderSetting.maximum, newValue));
         }

         sliderSetting.current = newValue;

         if (Noxium.get.configManager != null) {
            Noxium.get.configManager.autoSave();
         }

         return true;
      } else {
         return false;
      }
   }
}
