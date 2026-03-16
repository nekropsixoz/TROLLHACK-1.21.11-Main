package ru.noxium.ui.gui.component.render;

import java.awt.Color;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.util.math.MatrixStack;
import org.joml.Vector4f;
import ru.noxium.ui.gui.GuiScreen;

import ru.noxium.util.render.backends.gl.StencilHelper;
import ru.noxium.util.render.core.Renderer2D;
import ru.noxium.util.render.text.FontRegistry;

@Environment(EnvType.CLIENT)
public class GuiRenderUpPanel extends GuiScreen {
      public static void renderUpPanel(Renderer2D renderer2D, MatrixStack pose, float mainAlpha) {
            int bgColor = Renderer2D.ColorUtil.rgba(18, 18, 18, (int)(80.0F * mainAlpha));
            int outlineColor = Renderer2D.ColorUtil.rgba(33, 33, 33, (int)(255.0F * mainAlpha));
            int textColor = Renderer2D.ColorUtil.rgba(160, 216, 255, (int)(255.0F * mainAlpha));
            int textColorDim = Renderer2D.ColorUtil.rgba(160, 216, 255, (int)(150.0F * mainAlpha));
            int mainColor = Renderer2D.ColorUtil.replAlpha(Renderer2D.ColorUtil.getMainColor(1, 1), (int)(255.0F * mainAlpha));
            
            renderer2D.text(FontRegistry.INTER_MEDIUM, GuiScreen.x + 15.0F, GuiScreen.y + 12.595F + 7.0F, 14.0F, "TROLLHACK", textColor);
            renderer2D.text(FontRegistry.INTER_MEDIUM, GuiScreen.x + 65.0F, GuiScreen.y + 12.595F + 7.0F, 14.0F, "1.21", textColorDim);
            
            renderer2D.rect(GuiScreen.x + 111.885F, GuiScreen.y + 6.185F, 124.04F, 21.325F, 10.0F, bgColor);
            renderer2D.text(FontRegistry.ICONS, GuiScreen.x + 119.87F, GuiScreen.y + 12.335F - 1.0F + 9.75F, 17.5F, "C", mainColor);
            
            float searchTextX = GuiScreen.x + 134.775F;
            float searchTextY = GuiScreen.y + 12.595F + 7.0F - 0.4F;
            String searchDisplayText;
            if (GuiScreen.activeSearch) {
                  searchDisplayText = GuiScreen.searchText.isEmpty() ? "" : GuiScreen.searchText;
            } else {
                  searchDisplayText = "Search";
            }

            renderer2D.text(FontRegistry.INTER_MEDIUM, searchTextX, searchTextY, 14.0F, searchDisplayText, textColorDim);
            
            if (GuiScreen.activeSearch) {
                  long currentTime = System.currentTimeMillis();
                  boolean showCursor = currentTime / 500L % 2L == 0L;
                  if (showCursor) {
                        float textWidth = renderer2D.measureText(FontRegistry.INTER_MEDIUM, searchDisplayText, 14.0F).width;
                        float cursorX = searchTextX + textWidth;
                        float cursorY = searchTextY - 0.5F;
                        renderer2D.rect(cursorX, cursorY - 5.0F, 1.0F, 7.0F, 0.5F, mainColor);
                  }
            }
      }

}
