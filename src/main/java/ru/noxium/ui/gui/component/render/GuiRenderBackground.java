package ru.noxium.ui.gui.component.render;

import java.awt.Color;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.util.math.MatrixStack;
import ru.noxium.ui.gui.GuiScreen;
import ru.noxium.util.render.core.Renderer2D;

@Environment(EnvType.CLIENT)
public class GuiRenderBackground extends GuiScreen {
   public static void renderBackground(Renderer2D renderer2D, MatrixStack pose, float mainAlpha) {
      int bgColor = Renderer2D.ColorUtil.rgba(18, 18, 18, (int)(80.0F * mainAlpha));
      int outlineColor = Renderer2D.ColorUtil.rgba(33, 33, 33, (int)(255.0F * mainAlpha));
      
      if (mainAlpha > 0.1F && GuiScreen.clientBlurSetting.get()) {
         renderer2D.prepareBlur(30.0F);
         renderer2D.blur(GuiScreen.x, GuiScreen.y, GuiScreen.width, GuiScreen.height, 10.0F, mainAlpha);
      }

      renderer2D.rect(GuiScreen.x, GuiScreen.y, GuiScreen.width, GuiScreen.height, 10.0F, bgColor);
      renderer2D.rectOutline(GuiScreen.x, GuiScreen.y, GuiScreen.width, GuiScreen.height, 10.0F, outlineColor, 2.0F);
   }
}
