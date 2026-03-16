package ru.noxium.ui.gui.component.render;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.util.math.MatrixStack;
import ru.noxium.ui.gui.GuiScreen;
import ru.noxium.util.render.core.Renderer2D;

@Environment(EnvType.CLIENT)
public class GuiRenderLines extends GuiScreen {
      public static void renderLines(Renderer2D renderer2D, MatrixStack pose, float mainAlpha) {
            int outlineColor = Renderer2D.ColorUtil.replAlpha(Renderer2D.ColorUtil.getOutLineColor(1, 1),
                        (int) (20.4F * mainAlpha));
            int outlineColor2 = Renderer2D.ColorUtil.replAlpha(Renderer2D.ColorUtil.getOutLineColor(1, 1),
                        (int) (16.0F * mainAlpha));
            renderer2D.rect(GuiScreen.x, GuiScreen.y + 33.7F, 366.475F, 0.7F, outlineColor);
            renderer2D.rect(GuiScreen.x, GuiScreen.y + 203.19F, 104.34F, 0.7F, outlineColor2);
      }
}
