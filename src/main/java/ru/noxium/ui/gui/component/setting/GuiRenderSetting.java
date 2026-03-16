package ru.noxium.ui.gui.component.setting;

import java.awt.Color;
import java.util.HashMap;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import ru.noxium.module.api.setting.Setting;
import ru.noxium.module.api.setting.impl.BindSettings;
import ru.noxium.module.api.setting.impl.BooleanSetting;
import ru.noxium.module.api.setting.impl.HueSetting;
import ru.noxium.module.api.setting.impl.ModeSetting;
import ru.noxium.module.api.setting.impl.MultiBooleanSetting;
import ru.noxium.module.api.setting.impl.NoneSetting;
import ru.noxium.module.api.setting.impl.SliderSetting;
import ru.noxium.module.api.setting.impl.StringSetting;
import ru.noxium.ui.gui.GuiScreen;
import ru.noxium.util.color.ColorUtil;
import ru.noxium.util.render.animation.util.Animation;
import ru.noxium.util.render.animation.util.Easings;
import ru.noxium.util.render.core.Renderer2D;
import ru.noxium.util.render.math.MathHelper;
import ru.noxium.util.render.math.animation.AnimationMath;
import ru.noxium.util.render.math.animation.anim.util.Animation2;
import ru.noxium.util.render.text.FontRegistry;
import ru.noxium.util.render.utils.KeyUtil;

@Environment(EnvType.CLIENT)
public class GuiRenderSetting {
   public static Animation multiAnim = new Animation();
   public static HashMap<String, Float> animation = new HashMap<>();
   public static HashMap<String, Float> animation2 = new HashMap<>();
   public static HashMap<String, Float> multiBooleanAnimation = new HashMap<>();

   public static float getSettingHeight(Renderer2D renderer2D, Setting setting) {
      if (setting instanceof NoneSetting) {
         return ((NoneSetting) setting).get();
      } else if (setting instanceof BooleanSetting) {
         return 11.0F;
      } else if (setting instanceof SliderSetting) {
         return 20.0F;
      } else if (setting instanceof ModeSetting modeSetting) {
         float currentModeButtonHeight = 10.075F;
         float baseHeight = currentModeButtonHeight + 13.0F;
         float settingWidth = 105.47F;
         
         if (modeSetting.dropdownAnim.get() > 0.01F) {
               float modeSpacing = 2.0F;
               float modeHeight = 10.075F;
               float padding = 3.0F;
               float verticalSpacing = -2.0F;
               float calcX = padding;
               float calcY = 0.0F;

               for (String mode : modeSetting.modes) {
                  if (!mode.equals(modeSetting.currentMode)) {
                     float modeWidth = renderer2D.measureText(FontRegistry.INTER_MEDIUM, mode, 12.0F).width + padding * 2.0F;
                     if (calcX + modeWidth > settingWidth && calcX > padding) {
                        calcX = padding;
                        calcY += modeHeight + verticalSpacing;
                     }
                     calcX += modeWidth + modeSpacing;
                  }
               }

               float dropdownHeight = (calcY + modeHeight) * modeSetting.dropdownAnim.get();
               return baseHeight + dropdownHeight + 2.0F;
         }
         
         return baseHeight;
      } else if (setting instanceof BindSettings) {
         return 14.0F;
      } else if (setting instanceof StringSetting) {
         return 16.0F;
      } else if (setting instanceof HueSetting) {
         return 16.0F;
      } else if (setting instanceof MultiBooleanSetting multiBooleanSetting) {
         float height = 0.0F;
         float startY = 10.0F;
         float currentX = 0.0F;
         float currentY = startY;
         float spacing = 3.0F;
         float boolHeight = 10.0F;
         float padding = 4.0F;
         float width = 105.47F;

         for (BooleanSetting boolSetting : multiBooleanSetting.settings) {
            float nameWidth = renderer2D.measureText(FontRegistry.INTER_MEDIUM, boolSetting.name, 12.0F).width;
            float boolWidth = nameWidth + padding * 2.0F;
            if (currentX + boolWidth > 0.0F + width) {
               currentX = 0.0F;
               currentY += boolHeight + spacing;
            }

            currentX += boolWidth + spacing;
         }

         return currentY - 0.0F + boolHeight + 1.0F;
      } else {
         return 16.0F;
      }
   }

   public static float renderSetting(
         Renderer2D renderer2D,
         Setting setting,
         float x,
         float y,
         float width,
         int mouseX,
         int mouseY,
         int outlineColor,
         int mainColor,
         int mainColor6,
         int mainColor40,
         int textColor,
         float mainAlpha) {
      float height = 0.0F;
      if (setting instanceof NoneSetting) {
         height = ((NoneSetting) setting).get();
      } else if (setting instanceof BooleanSetting boolSetting) {
         boolean value = boolSetting.get();
         float checkBoxSize = 12.0F;
         float checkBoxX = x + width - checkBoxSize - 3.0F;
         float checkBoxY = y + 1.0F;
         
         boolSetting.anim.update();
         boolSetting.anim.run(value ? 1.0 : 0.0, 0.15F, Easings.SINE_OUT);
         
         int bgColor = Renderer2D.ColorUtil.rgba(18, 18, 18, (int)(80.0F * mainAlpha));
         int textColorNormal = Renderer2D.ColorUtil.rgba(160, 216, 255, (int)(255.0F * mainAlpha));
         
         renderer2D.rect(checkBoxX, checkBoxY, checkBoxSize, checkBoxSize, 10.0F, bgColor);
         
         if (boolSetting.anim.get() > 0.01F) {
               float circleSize = 6.0F * boolSetting.anim.get();
               float circleX = checkBoxX + (checkBoxSize - circleSize) / 2.0F;
               float circleY = checkBoxY + (checkBoxSize - circleSize) / 2.0F;
               renderer2D.circle(circleX + circleSize / 2.0F, circleY + circleSize / 2.0F, circleSize / 2.0F, 0.0F, 1.0F, textColorNormal);
         }
         
         renderer2D.text(FontRegistry.INTER_MEDIUM, x, y + 3.0F + 5.0F, 13.0F, setting.name, mainColor40);
         height = 10.0F;
      } else if (setting instanceof SliderSetting sliderSetting) {
         float sliderHeight = 4.0F;
         float sliderY = y + 10.0F;
         float sliderWidth = width - 2.5F;
         
         Animation2 sliderAnim = GuiScreen.getSliderAnimation(sliderSetting);
         float targetProgress = (sliderSetting.current - sliderSetting.minimum) / (sliderSetting.maximum - sliderSetting.minimum);
         sliderAnim.update();
         sliderAnim.run(targetProgress, 0.24F, ru.noxium.util.render.math.animation.anim.util.Easings.QUART_OUT);
         float progress = (float)sliderAnim.getValue();
         float progressWidth = sliderWidth * progress;
         
         int trackColor = Renderer2D.ColorUtil.rgba(255, 255, 255, (int)(255.0F * mainAlpha));
         int thumbColor = Renderer2D.ColorUtil.rgba(176, 176, 176, (int)(255.0F * mainAlpha));
         
         renderer2D.rect(x, sliderY + 2.0F, sliderWidth, sliderHeight, 2.0F, trackColor);
         
         float thumbSize = 8.0F;
         float thumbX = x + progressWidth - thumbSize / 2.0F;
         float thumbY = sliderY + 2.0F - (thumbSize - sliderHeight) / 2.0F;
         renderer2D.circle(thumbX + thumbSize / 2.0F, thumbY + thumbSize / 2.0F, thumbSize / 2.0F, 0.0F, 1.0F, thumbColor);
         renderer2D.shadow(thumbX, thumbY, thumbSize, thumbSize, thumbSize / 2.0F, 3.0F, 0.5F, Renderer2D.ColorUtil.rgba(176, 176, 176, (int)(100.0F * mainAlpha)));
         
         String valueText = sliderSetting.percent ? String.format("%.1f%%", sliderSetting.current) : String.format("%.1f / %.1f", sliderSetting.current, sliderSetting.maximum);
         renderer2D.text(FontRegistry.INTER_MEDIUM, x, y + 1.0F + 7.0F, 13.0F, setting.name, mainColor40);
         renderer2D.text(FontRegistry.INTER_MEDIUM, x + sliderWidth - renderer2D.measureText(FontRegistry.INTER_MEDIUM, valueText, 13.0F).width - 2.0F, y + 7.0F, 13.0F, valueText, mainColor);
         height = 19.0F;
      } else if (setting instanceof ModeSetting modeSetting) {
         for (String s : modeSetting.modes) {
            animation.putIfAbsent(s, 0.0F);
         }

         int bgColor = Renderer2D.ColorUtil.rgba(18, 18, 18, (int)(80.0F * mainAlpha));
         int bgColorDark = Renderer2D.ColorUtil.rgba(26, 26, 26, (int)(80.0F * mainAlpha));
         int textColorNormal = Renderer2D.ColorUtil.rgba(160, 216, 255, (int)(255.0F * mainAlpha));
         
         renderer2D.text(FontRegistry.INTER_MEDIUM, x, y + 7.0F, 13.0F, setting.name, mainColor40);
         
         float currentModeButtonHeight = 10.075F;
         float currentModeButtonY = y + 10.0F;
         float currentModeWidth = renderer2D.measureText(FontRegistry.INTER_MEDIUM, modeSetting.currentMode, 12.0F).width + 12.0F;
         
         renderer2D.rect(x, currentModeButtonY, currentModeWidth, currentModeButtonHeight, 10.0F, bgColor);
         renderer2D.text(FontRegistry.INTER_MEDIUM, x + 6.0F, currentModeButtonY + 1.5F + 5.7F, 12.0F, modeSetting.currentMode, textColorNormal);
         
         modeSetting.dropdownAnim.update();
         modeSetting.dropdownAnim.run(modeSetting.opened ? 1.0 : 0.0, 0.2F, Easings.QUART_OUT);
         float dropdownAnimValue = modeSetting.dropdownAnim.get();
         
         if (dropdownAnimValue > 0.01F) {
               float modeSpacing = 2.0F;
               float modeHeight = 10.075F;
               float padding = 3.0F;
               float verticalSpacing = -2.0F;
               float calcX = padding;
               float calcY = 0.0F;

               for (String mode : modeSetting.modes) {
                  if (!mode.equals(modeSetting.currentMode)) {
                     float modeWidth = renderer2D.measureText(FontRegistry.INTER_MEDIUM, mode, 12.0F).width + padding * 2.0F;
                     if (calcX + modeWidth > width && calcX > padding) {
                        calcX = padding;
                        calcY += modeHeight + verticalSpacing;
                     }
                     calcX += modeWidth + modeSpacing;
                  }
               }

               float dropdownHeight = (calcY + modeHeight) * dropdownAnimValue;
               float dropdownY = currentModeButtonY + currentModeButtonHeight + 2.0F;
               
               renderer2D.pushRoundedClipRect(x, dropdownY, width, dropdownHeight, 0.0F, 0.0F, 10.0F, 10.0F);
               renderer2D.rect(x, dropdownY, width, dropdownHeight, 10.0F, bgColorDark);
               
               float currentX = padding;
               float currentY = 1.5F;

               for (String mode : modeSetting.modes) {
                  if (!mode.equals(modeSetting.currentMode)) {
                     boolean isSelected = mode.equals(modeSetting.currentMode);
                     float modeWidth = renderer2D.measureText(FontRegistry.INTER_MEDIUM, mode, 12.0F).width + padding * 2.0F;
                     if (currentX + modeWidth > width && currentX > padding) {
                        currentX = padding;
                        currentY += modeHeight + verticalSpacing;
                     }

                     float anim = animation.get(mode);
                     float target = isSelected ? 1.0F : 0.0F;
                     anim = AnimationMath.fast(anim, target, 10.0F);
                     animation.put(mode, anim);
                     int modeColor = ColorUtil.overCol(mainColor40, textColorNormal, anim);
                     renderer2D.text(FontRegistry.INTER_MEDIUM, x + currentX, dropdownY + currentY + 5.5F, 12.0F, mode, ColorUtil.multAlpha(modeColor, dropdownAnimValue));
                     currentX += modeWidth + modeSpacing;
                  }
               }
               
               renderer2D.popClipRect();
               height = currentModeButtonHeight + 12.0F + dropdownHeight + 2.0F;
         } else {
               height = currentModeButtonHeight + 12.0F;
         }
      } else if (setting instanceof BindSettings bindSetting) {
         float bindHeight = 10.075F;
         String displayName = setting.name != null && !setting.name.isEmpty() ? setting.name : "KEY";
         String keyText = bindSetting.active ? "..." : KeyUtil.getKey(bindSetting.key);
         float keyTextWidth = renderer2D.measureText(FontRegistry.INTER_MEDIUM, keyText, 12.0F).width;
         float minButtonWidth = 16.055F;
         float buttonWidth = Math.max(minButtonWidth, keyTextWidth + 8.0F);
         float bindButtonX = x + width - buttonWidth - 2.0F;
         if (bindButtonX < x) {
            bindButtonX = x;
            buttonWidth = width - 2.0F;
         }

         renderer2D.text(FontRegistry.INTER_MEDIUM, x, y + 1.0F + 6.8F, 13.0F, displayName, mainColor40);
         float backgroundX = bindButtonX - 6.0F;
         float backgroundWidth = buttonWidth + 2.0F;
         if (backgroundX < x) {
            backgroundWidth = backgroundX + backgroundWidth - x;
            backgroundX = x;
         }

         renderer2D.rectOutline(backgroundX, y, backgroundWidth, bindHeight, 3.0F, outlineColor, 0.1F);
         renderer2D.rect(backgroundX, y, backgroundWidth, bindHeight, 3.0F, mainColor6);
         renderer2D.text(
               FontRegistry.INTER_MEDIUM,
               backgroundX + backgroundWidth / 2.0F - keyTextWidth / 2.0F,
               y + 1.5F + 5.7F,
               12.0F,
               keyText,
               bindSetting.active ? mainColor : mainColor40);
         height = 13.0F;
      } else if (setting instanceof StringSetting stringSetting) {
         float textFieldHeight = 10.075F;
         float textFieldWidth = 63.56F;
         float textFieldX = x + 42.0F;
         float textX = textFieldX + 5.0F;
         float textY = y + 1.5F;
         renderer2D.text(FontRegistry.INTER_MEDIUM, x, y + 1.0F + 6.5F, 13.0F, setting.name, mainColor40);
         renderer2D.rectOutline(textFieldX, y, textFieldWidth, textFieldHeight, 3.0F, outlineColor, 0.1F);
         renderer2D.rect(textFieldX, y, textFieldWidth, textFieldHeight, 3.0F, mainColor6);
         String inputText = stringSetting.input;
         boolean isEmpty = inputText.isEmpty();
         float cursorX = textX;
         if (isEmpty) {
            renderer2D.text(FontRegistry.INTER_MEDIUM, textX - 2.0F, textY - 0.5F + 6.1F, 12.0F, "Enter text",
                  mainColor40);
         } else {
            float currentX = textX;
            float maxX = textFieldX + textFieldWidth - 5.0F;
            float gradientStartX = textX;
            float gradientEndX = textFieldX + textFieldWidth - 5.0F;

            for (int i = 0; i < inputText.length(); i++) {
               char c = inputText.charAt(i);
               String charStr = String.valueOf(c);
               float charWidth = renderer2D.measureText(FontRegistry.INTER_MEDIUM, charStr, 12.0F).width;
               if (currentX + charWidth > maxX) {
                  cursorX = currentX;
                  break;
               }

               textColor = mainColor40;
               if (i >= 16) {
                  float fadeStartX = gradientStartX
                        + renderer2D.measureText(FontRegistry.INTER_MEDIUM, inputText.substring(0, 16), 12.0F).width;
                  float gradientWidth = Math.min(30.0F, gradientEndX - fadeStartX);
                  if (gradientWidth > 0.0F) {
                     float fadeProgress = (currentX - fadeStartX) / gradientWidth;
                     fadeProgress = MathHelper.clamp(fadeProgress, 0.0F, 1.0F);
                     int alpha = mainColor40 >> 24 & 0xFF;
                     alpha = (int) (alpha * (1.0F - fadeProgress));
                     textColor = Renderer2D.ColorUtil.replAlpha(mainColor40, alpha);
                  } else {
                     textColor = Renderer2D.ColorUtil.replAlpha(mainColor40, 0);
                  }
               }

               renderer2D.text(FontRegistry.INTER_MEDIUM, currentX - 2.0F, textY - 0.5F + 6.1F, 12.0F, charStr,
                     textColor);
               currentX += charWidth;
               cursorX = currentX;
            }
         }

         boolean isActive = GuiScreen.activeStringSetting == stringSetting && stringSetting.active;
         if (isActive) {
            long currentTime = System.currentTimeMillis();
            boolean showCursor = currentTime / 500L % 2L == 0L;
            if (showCursor) {
               renderer2D.rect(cursorX - 3.0F, textY - 0.5F, 1.0F, 8.0F, 0.5F, mainColor);
            }
         }

         height = 15.0F;
      } else if (setting instanceof HueSetting hueSetting) {
         float colorHeight = 12.0F;
         float colorWidth = 40.0F;
         float colorX = x + width - colorWidth - 2.0F;
         renderer2D.text(FontRegistry.INTER_MEDIUM, x, y + 1.0F + 7.0F, 13.0F, setting.name, mainColor40);
         Color hueColor = hueSetting.getColor();
         renderer2D.rectOutline(colorX - 10.0F, y, 46.48F, 10.075F, 3.0F, outlineColor, 0.1F);
         renderer2D.rect(colorX - 10.0F, y, 46.48F, 10.075F, 3.0F, mainColor6);
         renderer2D.rect(
               colorX + 32.0F - 10.0F,
               y + 0.8F,
               13.285F,
               8.315F,
               0.0F,
               3.0F,
               3.0F,
               0.0F,
               Renderer2D.ColorUtil.replAlpha(hueColor.getRGB(), (int) (255.0F * mainAlpha)));
         String hexText = String.format("#%02X%02X%02X", hueColor.getRed(), hueColor.getGreen(), hueColor.getBlue());
         renderer2D.text(
               FontRegistry.INTER_MEDIUM,
               colorX + colorWidth / 2.0F
                     - renderer2D.measureText(FontRegistry.INTER_MEDIUM, hexText, 12.0F).width / 2.0F - 14.0F,
               y + 1.5F + 5.7F,
               12.0F,
               hexText,
               mainColor40);
         height = 15.0F;
      } else if (setting instanceof MultiBooleanSetting multiBooleanSetting) {
         renderer2D.text(FontRegistry.INTER_MEDIUM, x, y + 7.0F, 13.0F, setting.name, mainColor40);
         float startY = y + 10.0F;
         float currentX = x;
         float currentY = startY;
         float spacing = 3.0F;
         float boolHeight = 10.0F;
         float padding = 4.0F;

         for (BooleanSetting boolSetting : multiBooleanSetting.settings) {
            float nameWidth = renderer2D.measureText(FontRegistry.INTER_MEDIUM, boolSetting.name, 12.0F).width;
            float boolWidth = nameWidth + padding * 2.0F;
            if (currentX + boolWidth > x + width) {
               currentX = x;
               currentY += boolHeight + spacing;
            }

            renderer2D.rectOutline(currentX, currentY, boolWidth, boolHeight, 3.0F, outlineColor, 0.1F);
            renderer2D.rect(currentX, currentY, boolWidth, boolHeight, 3.0F, mainColor6);
            String animKey = setting.name + "_" + boolSetting.name;
            multiBooleanAnimation.putIfAbsent(animKey, boolSetting.get() ? 1.0F : 0.0F);
            float anim = multiBooleanAnimation.get(animKey);
            float target = boolSetting.get() ? 1.0F : 0.0F;
            anim = AnimationMath.fast(anim, target, 10.0F);
            multiBooleanAnimation.put(animKey, anim);
            textColor = ColorUtil.overCol(mainColor40, mainColor, anim);
            renderer2D.text(FontRegistry.INTER_MEDIUM, currentX + padding, currentY + 3.0F - 1.0F + 5.0F, 12.0F,
                  boolSetting.name, textColor);
            currentX += boolWidth + spacing;
         }

         height = currentY - y + boolHeight;
      }

      return height + 1.0F;
   }
}
