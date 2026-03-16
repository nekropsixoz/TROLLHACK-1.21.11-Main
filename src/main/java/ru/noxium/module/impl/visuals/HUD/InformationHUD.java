package ru.noxium.module.impl.visuals.HUD;

import java.awt.Color;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ChatScreen;
import ru.noxium.module.impl.visuals.Hud;
import ru.noxium.util.render.animation.util.Easings;
import ru.noxium.util.render.core.Renderer2D;
import ru.noxium.util.render.math.ScaledResolution;
import ru.noxium.util.render.text.FontRegistry;

@Environment(EnvType.CLIENT)
public class InformationHUD {
   public static MinecraftClient mc = MinecraftClient.getInstance();
   private static double prevX = 0.0;
   private static double prevY = 0.0;
   private static double prevZ = 0.0;
   private static long prevTime = 0L;
   private static float bps = 0.0F;

   public static void information(Renderer2D r2) {
      Hud.animC.update();
      int bgColor = Renderer2D.ColorUtil.rgba(18, 18, 18, 80);
      int outlineColor = Renderer2D.ColorUtil.rgba(33, 33, 33, 255);
      int textColor = Renderer2D.ColorUtil.rgba(160, 216, 255, 255);
      int iconColor = Renderer2D.ColorUtil.rgba(160, 216, 255, 200);
      
      ScaledResolution sr = new ScaledResolution(mc);
      boolean chat = mc.currentScreen instanceof ChatScreen;
      Hud.animC.run(chat ? 1.0 : 0.0, 0.8F, Easings.CIRC_OUT, false);
      float x = 20.0F;
      float y = sr.getHeight() * 2 - 75.0F + (20.0F + -20.0F * Hud.animC.get());
      long currentTime = System.currentTimeMillis();
      if (prevTime != 0L && currentTime - prevTime >= 50L) {
         double dx = mc.player.getX() - prevX;
         double dy = mc.player.getY() - prevY;
         double dz = mc.player.getZ() - prevZ;
         double distance = Math.sqrt(dx * dx + dy * dy + dz * dz);
         double timeDelta = (currentTime - prevTime) / 1000.0;
         if (timeDelta > 0.0) {
            bps = (float)(distance / timeDelta);
         }

         prevX = mc.player.getX();
         prevY = mc.player.getY();
         prevZ = mc.player.getZ();
         prevTime = currentTime;
      } else if (prevTime == 0L) {
         prevX = mc.player.getX();
         prevY = mc.player.getY();
         prevZ = mc.player.getZ();
         prevTime = currentTime;
      }

      if (ru.noxium.module.impl.visuals.Hud.blur.get()) {
         r2.prepareBlur(30.0F);
         r2.blur(x, y, 40.64F, 40.64F, 10.0F, 1.0F);
      }
      
      r2.rect(x, y, 40.64F, 40.64F, 10.0F, bgColor);
      r2.rectOutline(x, y, 40.64F, 40.64F, 10.0F, outlineColor, 2.0F);
      r2.text(FontRegistry.ICONS, x + 11.0F, y + 30.0F, 36.0F, "0", iconColor);
      int playerX = (int)mc.player.getX();
      int playerY = (int)mc.player.getY();
      int playerZ = (int)mc.player.getZ();
      String xStr = String.valueOf(playerX);
      String yStr = String.valueOf(playerY);
      String zStr = String.valueOf(playerZ);
      float fontSize = 28.0F;
      float textX = x + 92.0F;
      float textY = y + 25.0F;
      float coordsWidth = r2.measureText(FontRegistry.INTER_MEDIUM, xStr, fontSize).width
         + r2.measureText(FontRegistry.INTER_MEDIUM, "x ", fontSize).width
         + r2.measureText(FontRegistry.INTER_MEDIUM, yStr, fontSize).width
         + r2.measureText(FontRegistry.INTER_MEDIUM, "y ", fontSize).width
         + r2.measureText(FontRegistry.INTER_MEDIUM, zStr, fontSize).width
         + r2.measureText(FontRegistry.INTER_MEDIUM, "z", fontSize).width;
      String bpsValue = String.format("%.1f", bps);
      float bpsWidth = r2.measureText(FontRegistry.INTER_MEDIUM, bpsValue, 28.0F).width + r2.measureText(FontRegistry.INTER_MEDIUM, "b/s", 28.0F).width;
      
      if (ru.noxium.module.impl.visuals.Hud.blur.get()) {
         r2.prepareBlur(30.0F);
         r2.blur(x + 49.6F, y, coordsWidth + bpsWidth + 100.0F, 40.64F, 10.0F, 1.0F);
      }
      
      r2.rect(x + 49.6F, y, coordsWidth + bpsWidth + 100.0F, 40.64F, 10.0F, bgColor);
      r2.rectOutline(x + 49.6F, y, coordsWidth + bpsWidth + 100.0F, 40.64F, 10.0F, outlineColor, 2.0F);
      r2.text(FontRegistry.ICONS, x + 66.56F, y + 28.0F, 32.0F, "1", iconColor);
      r2.text(FontRegistry.INTER_MEDIUM, textX, textY, fontSize, xStr, textColor);
      textX += r2.measureText(FontRegistry.INTER_MEDIUM, xStr, fontSize).width;
      r2.text(FontRegistry.INTER_MEDIUM, textX, textY, fontSize, "x ", iconColor);
      textX += r2.measureText(FontRegistry.INTER_MEDIUM, "x ", fontSize).width;
      r2.text(FontRegistry.INTER_MEDIUM, textX, textY, fontSize, yStr, textColor);
      textX += r2.measureText(FontRegistry.INTER_MEDIUM, yStr, fontSize).width;
      r2.text(FontRegistry.INTER_MEDIUM, textX, textY, fontSize, "y ", iconColor);
      textX += r2.measureText(FontRegistry.INTER_MEDIUM, "y ", fontSize).width;
      r2.text(FontRegistry.INTER_MEDIUM, textX, textY, fontSize, zStr, textColor);
      textX += r2.measureText(FontRegistry.INTER_MEDIUM, zStr, fontSize).width;
      r2.text(FontRegistry.INTER_MEDIUM, textX, textY, fontSize, "z", iconColor);
      textX += r2.measureText(FontRegistry.INTER_MEDIUM, "z", fontSize).width;
      r2.rect(x + coordsWidth + 98.0F, y + 15.0F, 2.34F, 11.21F, 4.0F, Renderer2D.ColorUtil.rgba(33, 33, 33, 100));
      r2.text(FontRegistry.ICONS, x + coordsWidth + 108.0F, y + 28.0F, 32.0F, "2", iconColor);
      float bpsTextX = x + coordsWidth + 132.0F;
      r2.text(FontRegistry.INTER_MEDIUM, bpsTextX, y + 25.0F, fontSize, bpsValue, textColor);
      bpsTextX += r2.measureText(FontRegistry.INTER_MEDIUM, bpsValue, fontSize).width;
      r2.text(FontRegistry.INTER_MEDIUM, bpsTextX, y + 25.0F, fontSize, "b/s", iconColor);
   }
}
