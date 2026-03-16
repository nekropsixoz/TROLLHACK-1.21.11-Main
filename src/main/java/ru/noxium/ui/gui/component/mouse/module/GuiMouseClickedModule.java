package ru.noxium.ui.gui.component.mouse.module;

import java.util.List;
import java.util.stream.Collectors;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import ru.noxium.module.api.Module;
import ru.noxium.module.api.setting.Setting;
import ru.noxium.module.api.setting.impl.HueSetting;
import ru.noxium.ui.gui.GuiScreen;
import ru.noxium.ui.gui.component.mouse.setting.GuiMouseClickedSetting;
import ru.noxium.ui.gui.component.render.GuiRenderMain;
import ru.noxium.ui.gui.component.setting.GuiRenderSetting;
import ru.noxium.util.render.core.Renderer2D;
import ru.noxium.util.render.math.animation.Direction;
import ru.noxium.util.render.math.animation.anim.util.Easings;

@Environment(EnvType.CLIENT)
public class GuiMouseClickedModule extends GuiScreen {
   public static boolean mouseClickedModule(Renderer2D renderer2D, int mouseX, int mouseY, int pButton) {
      float x1 = GuiScreen.x;
      float y1 = GuiScreen.y + 34.025F;
      float rectWidth = GuiScreen.width;
      float rectHeight = GuiScreen.height - 34.025F - 40.0F;

      float clipX = x1;
      float clipY = y1;
      float clipWidth = rectWidth;
      float clipHeight = rectHeight;

      if (!GuiRenderMain.isHovered(mouseX, mouseY, clipX, clipY, clipWidth, clipHeight)) {
         return false;
      } else {
         List<Module> filteredModules = GuiScreen.modules;
         if (GuiScreen.activeSearch && !GuiScreen.searchText.isEmpty()) {
            String searchLower = GuiScreen.searchText.toLowerCase().trim();
            filteredModules = GuiScreen.modules.stream()
                  .filter(modulex -> modulex.name.toLowerCase().contains(searchLower)).collect(Collectors.toList());
         }

         // Column Calcs matching Render
         float moduleWidth = 121.47F;
         float gapX = 20.0F;
         float gapY = 10.0F;
         float totalContentWidth = (moduleWidth * 2) + gapX;
         float startMargin = (rectWidth - totalContentWidth) / 2.0F;

         float col1X = GuiScreen.x + startMargin;
         float col2X = GuiScreen.x + startMargin + moduleWidth + gapX;

         float scrollY = GuiScreen.getScrollUtil().getScroll();
         float currentY_Col1 = scrollY;
         float currentY_Col2 = scrollY;

         int index = 0;
         for (Module module : filteredModules) {
            // Calculate Settings Height (logic must match render)
            float settingsHeight = 12.0F;
            float fullSettingsHeight = 12.0F;
            float settingsAnim = GuiScreen.getModuleSettingsAnimation(module).get();
            float settingsAlphaAnim = GuiScreen.getModuleSettingsAlphaAnimation(module).get();
            boolean settingsOpen = GuiScreen.openSettingsModules.contains(module) || settingsAnim > 0.0F
                  || settingsAlphaAnim > 0.0F;

            if (settingsOpen) {
               for (Setting setting : module.getSettingsForGUI()) {
                  fullSettingsHeight += GuiRenderSetting.getSettingHeight(renderer2D, setting);
               }
               fullSettingsHeight = Math.max(fullSettingsHeight, 20.0F);
               settingsHeight = 12.0F + (fullSettingsHeight - 12.0F) * settingsAnim;
            } else {
               settingsHeight = 0.0F;
            }

            float moduleBaseHeight = 21.325F;
            float currentModuleTotalHeight = moduleBaseHeight + settingsHeight;

            boolean isCol2 = (index % 2 != 0);
            float checkX = isCol2 ? col2X : col1X;
            float checkY = isCol2 ? currentY_Col2 : currentY_Col1;

            float moduleRenderY = GuiScreen.y + 43.365F + checkY;

            // Interaction Logic
            if (GuiScreen.openSettingsModules.contains(module) && pButton == 0) {
               float settingY = GuiScreen.y + 64.69F + checkY + 4.0F;
               float settingX = checkX + 9.0F;
               float settingWidth = 105.47F;
               float totalSettingsHeight = 0.0F;

               for (Setting setting : module.getSettingsForGUI()) {
                  float actualSettingY = settingY + totalSettingsHeight;
                  if (GuiMouseClickedSetting.handleSettingClick(renderer2D, setting, settingX, actualSettingY,
                        settingWidth, mouseX, mouseY, pButton)) {
                     return true;
                  }
                  // Match render increment
                  totalSettingsHeight += GuiRenderSetting.getSettingHeight(renderer2D, setting) * settingsAlphaAnim;
               }
            }

            // Click Module
            if (GuiRenderMain.isHovered(mouseX, mouseY, checkX, moduleRenderY, moduleWidth, moduleBaseHeight)) {
               if (pButton == 0) {
                  module.toggle();
               } else if (pButton == 1 && !module.getSettingsForGUI().isEmpty()) {
                  if (GuiScreen.openSettingsModules.contains(module)) {
                     GuiScreen.openSettingsModules.remove(module);
                     GuiScreen.getModuleSettingsAnimation(module).run(0.0, 0.6F, Easings.QUART_OUT);
                     GuiScreen.getModuleSettingsAlphaAnimation(module).run(0.0, 0.16F, Easings.SINE_OUT);
                     if (GuiScreen.activeColorPicker != null
                           && module.getSettingsForGUI().contains(GuiScreen.activeColorPicker)) {
                        GuiScreen.animation15.setDirection(Direction.BACKWARDS);
                        GuiScreen.activeColorPicker = null;
                     }
                  } else {
                     GuiScreen.openSettingsModules.add(module);
                     GuiScreen.getModuleSettingsAlphaAnimation(module).run(1.0, 0.16F, Easings.SINE_OUT);
                     GuiScreen.getModuleSettingsAnimation(module).run(1.0, 0.6F, Easings.QUART_OUT);
                  }
               } else if (pButton == 2) {
                  if (module.binding) {
                     module.binding = false;
                     GuiScreen.activeModuleBind = null;
                     GuiScreen.getModuleBindAnimation(module).run(0.0, 0.2F, Easings.SINE_OUT);
                  } else {
                     if (GuiScreen.activeModuleBind != null) {
                        GuiScreen.activeModuleBind.binding = false;
                        GuiScreen.getModuleBindAnimation(GuiScreen.activeModuleBind).run(0.0, 0.2F, Easings.SINE_OUT);
                     }
                     GuiScreen.activeModuleBind = module;
                     module.binding = true;
                     GuiScreen.getModuleBindAnimation(module).run(1.0, 0.2F, Easings.SINE_OUT);
                  }
                  return true;
               }
            }

            // Increment
            if (isCol2) {
               currentY_Col2 += currentModuleTotalHeight + gapY;
            } else {
               currentY_Col1 += currentModuleTotalHeight + gapY;
            }

            index++;
         }

         return false;
      }
   }

   public static float[] findColorPickerPosition(Renderer2D renderer2D, HueSetting hueSetting) {
      if (hueSetting == null) {
         return null;
      } else {
         float rectWidth = GuiScreen.width;
         float moduleWidth = 121.47F;
         float gapX = 20.0F;
         float gapY = 10.0F;
         float totalContentWidth = (moduleWidth * 2) + gapX;
         float startMargin = (rectWidth - totalContentWidth) / 2.0F;

         float col1X = GuiScreen.x + startMargin;
         float col2X = GuiScreen.x + startMargin + moduleWidth + gapX;

         float scrollY = GuiScreen.getScrollUtil().getScroll();
         float currentY_Col1 = scrollY;
         float currentY_Col2 = scrollY;

         int index = 0;
         for (Module module : GuiScreen.modules) {
            float settingsHeight = 12.0F;
            float fullSettingsHeight = 12.0F;
            float settingsAnim = GuiScreen.getModuleSettingsAnimation(module).get();
            float settingsAlphaAnim = GuiScreen.getModuleSettingsAlphaAnimation(module).get();
            boolean settingsOpen = GuiScreen.openSettingsModules.contains(module) || settingsAnim > 0.0F
                  || settingsAlphaAnim > 0.0F;

            if (settingsOpen) {
               for (Setting setting : module.getSettingsForGUI()) {
                  fullSettingsHeight += GuiRenderSetting.getSettingHeight(renderer2D, setting);
               }
               fullSettingsHeight = Math.max(fullSettingsHeight, 20.0F);
               settingsHeight = 12.0F + (fullSettingsHeight - 12.0F) * settingsAnim;
            } else {
               settingsHeight = 0.0F;
            }

            float moduleBaseHeight = 21.325F;
            float currentModuleTotalHeight = moduleBaseHeight + settingsHeight;

            boolean isCol2 = (index % 2 != 0);
            float checkX = isCol2 ? col2X : col1X;
            float checkY = isCol2 ? currentY_Col2 : currentY_Col1;

            if (GuiScreen.openSettingsModules.contains(module)) {
               float settingY = GuiScreen.y + 64.69F + checkY + 4.0F;
               float settingX = checkX + 9.0F;
               float settingWidth = 105.47F;
               float totalSettingsHeight = 0.0F;

               for (Setting setting : module.getSettingsForGUI()) {
                  if (setting == hueSetting) {
                     float pickerX = settingX + settingWidth - 15.0F;
                     float pickerY = settingY + totalSettingsHeight - 5.0F;
                     return new float[] { pickerX, pickerY };
                  }
                  totalSettingsHeight += GuiRenderSetting.getSettingHeight(renderer2D, setting) * settingsAlphaAnim;
               }
            }

            if (isCol2) {
               currentY_Col2 += currentModuleTotalHeight + gapY;
            } else {
               currentY_Col1 += currentModuleTotalHeight + gapY;
            }

            index++;
         }

         return null;
      }
   }
}
