package ru.noxium.module.impl.visuals.HUD;

import java.util.ArrayList;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import ru.noxium.Noxium;
import ru.noxium.module.api.Category;
import ru.noxium.module.api.Module;
import ru.noxium.util.other.ScaleUtil;
import ru.noxium.util.render.core.Renderer2D;
import ru.noxium.util.render.math.animation.Translate;
import ru.noxium.util.render.text.FontRegistry;

@Environment(EnvType.CLIENT)
public class ArrayListHUD {
   public static ArrayList<Module> modules = new ArrayList<>();

   public static float deltaTime() {
      return MinecraftClient.getInstance().getCurrentFps() > 0.0F ? 1.0F / MinecraftClient.getInstance().getCurrentFps() : 1.0F;
   }

   public static void arraylist(Renderer2D r2) {
      modules.clear();
      if (Noxium.get != null && Noxium.get.manager != null) {
         for (Module m : Noxium.get.manager.module) {
            if (m.category != Category.Visuals && m.enable) {
               modules.add(m);
            }
         }
      }

      if (modules.isEmpty()) {
         ScaleUtil.scale_post(r2);
      } else {
         ScaleUtil.scale_pre(r2);
         modules.sort(
            (f1, f2) -> r2.measureText(FontRegistry.INTER_MEDIUM, f1.getDisplayName(), 32.0F).width
                  > r2.measureText(FontRegistry.INTER_MEDIUM, f2.getDisplayName(), 32.0F).width
               ? -1
               : 1
         );
         int count = 0;
         int x = 10;
         int y = 120;

         for (Module mx : modules) {
            Translate translate = mx.a;
            float offset = count * 24;
            translate.interpolate(10.0F, offset, 15.0F * deltaTime());
            float posX = translate.getX() + x;
            mx.isRender = posX > -10.0F;
            if (translate.getX() <= 0.1F && translate.getX() >= -0.1F) {
               mx.isRender = true;
            }

            count++;
         }

         count = 0;

         for (Module mx : modules) {
            Translate translate = mx.a;
            if (mx.isRender) {
               count++;
            }
         }

         count = 0;

         for (Module mxx : modules) {
            Translate translate = mxx.a;
            if (mxx.isRender) {
               count++;
            }
         }

         count = 0;

         for (Module mxxx : modules) {
            Translate translate = mxxx.a;
            if (mxxx.isRender) {
               int bgColor = Renderer2D.ColorUtil.rgba(18, 18, 18, 80);
               int outlineColor = Renderer2D.ColorUtil.rgba(33, 33, 33, 255);
               float moduleWidth = r2.measureText(FontRegistry.INTER_MEDIUM, mxxx.name, 32.0F).width + 12.0F;
               
               if (ru.noxium.module.impl.visuals.Hud.blur.get()) {
                  r2.prepareBlur(30.0F);
                  r2.blur(translate.getX() + x, translate.getY() + y + 5.0F, moduleWidth, 34.0F, 10.0F, 1.0F);
               }
               
               r2.rect(translate.getX() + x, translate.getY() + y + 5.0F, moduleWidth, 34.0F, 10.0F, bgColor);
               r2.rectOutline(translate.getX() + x, translate.getY() + y + 5.0F, moduleWidth, 34.0F, 10.0F, outlineColor, 2.0F);
               count++;
            }
         }

         count = 0;

         for (Module mxxxx : modules) {
            Translate translate = mxxxx.a;
            if (mxxxx.isRender) {
               float widthName = 0.0F;
               float rectY = translate.getY() + y + 5.0F;
               float rectHeight = 34.0F;
               float fontSize = 32.0F;
               float textHeight = r2.measureText(FontRegistry.INTER_MEDIUM, mxxxx.name, fontSize).height;
               float rectCenterY = rectY + rectHeight / 2.0F;
               float textY = rectCenterY - textHeight * 0.05F + 5.0F;
               int textColorNormal = Renderer2D.ColorUtil.rgba(160, 216, 255, 255);
               int textColorBright = Renderer2D.ColorUtil.rgba(180, 236, 255, 255);
               double timeSeconds = System.currentTimeMillis() / 1000.0;
               double animationSpeed = 0.5;
               double animationPhase = timeSeconds * animationSpeed % 1.0;
               int nameLength = mxxxx.name.length();

               for (int charIndex = 0; charIndex < nameLength; charIndex++) {
                  char c = mxxxx.name.charAt(charIndex);
                  double charPosition = nameLength > 1 ? (double)charIndex / (nameLength - 1) : 0.5;
                  double gradientPosition = (charPosition - animationPhase + 1.0) % 1.0;
                  double smoothFactor = Math.sin(gradientPosition * Math.PI * 2.0) * 0.5 + 0.5;
                  
                  int red1 = textColorNormal >> 16 & 0xFF;
                  int green1 = textColorNormal >> 8 & 0xFF;
                  int blue1 = textColorNormal & 0xFF;
                  int red2 = textColorBright >> 16 & 0xFF;
                  int green2 = textColorBright >> 8 & 0xFF;
                  int blue2 = textColorBright & 0xFF;
                  
                  int finalR = (int)(red1 + (red2 - red1) * smoothFactor);
                  int finalG = (int)(green1 + (green2 - green1) * smoothFactor);
                  int finalB = (int)(blue1 + (blue2 - blue1) * smoothFactor);
                  int colorModuleText = 0xFF000000 | finalR << 16 | finalG << 8 | finalB;
                  
                  r2.text(FontRegistry.INTER_MEDIUM, translate.getX() + x + 6.0F + widthName, textY, fontSize, String.valueOf(c), colorModuleText);
                  widthName += r2.measureText(FontRegistry.INTER_MEDIUM, String.valueOf(c), fontSize).width;
               }

               count++;
            }
         }

         ScaleUtil.scale_post(r2);
         r2.popScale();
      }
   }
}
