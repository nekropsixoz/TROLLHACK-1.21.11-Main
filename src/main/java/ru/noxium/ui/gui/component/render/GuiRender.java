package ru.noxium.ui.gui.component.render;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.util.math.MatrixStack;
import ru.noxium.module.api.Module;
import ru.noxium.module.impl.visuals.ClickGUI;
import ru.noxium.ui.gui.GuiScreen;
import ru.noxium.ui.gui.theme.ThemeScreen;
import ru.noxium.util.render.core.Renderer2D;
import ru.noxium.util.render.math.ScaleHelper;

@Environment(EnvType.CLIENT)
public class GuiRender extends GuiScreen {
   public static void render(Renderer2D renderer2D, DrawContext p_281549_, int p_281550_, int p_282878_,
         float p_282465_) {
      MinecraftClient client = MinecraftClient.getInstance();
      if (client != null && client.getWindow() != null) {
         int viewportWidth = client.getWindow().getFramebufferWidth();
         int viewportHeight = client.getWindow().getFramebufferHeight();
         if (viewportWidth > 0 && viewportHeight > 0) {
            int mouseX = (int) (p_281550_ / ScaleHelper.size);
            int mouseY = (int) (p_282878_ / ScaleHelper.size);
            GuiScreen.currentMouseX = mouseX;
            GuiScreen.currentMouseY = mouseY;
            GuiScreen.alphaPC.update();
            GuiScreen.settingPC.update();
            GuiScreen.alphaPC2.update();
            GuiScreen.alphaPC3.update();
            if (GuiScreen.modules != null) {
               for (Module module : GuiScreen.modules) {
                  GuiScreen.getModuleSettingsAnimation(module).update();
                  GuiScreen.getModuleSettingsAlphaAnimation(module).update();
                  GuiScreen.getModuleBindAnimation(module).update();
               }
            }

            GuiScreen.alpha.run(1.0);
            float mainAlpha = GuiScreen.alphaPC.get();
            if (!(mainAlpha <= 0.001F)) {
               float scaledWidth = client.getWindow().getScaledWidth();
               float scaledHeight = client.getWindow().getScaledHeight();

               MatrixStack pose = new MatrixStack();
               float scale = (float) client.getWindow().getFramebufferWidth() / client.getWindow().getScaledWidth();
               renderer2D.pushScale(scale);

               try {
                  // Проверяем выбранный стиль GUI
                  if (ClickGUI.guiStyle.is("Классический")) {
                     // Классический стиль - колонки с категориями
                     GuiRenderClassic.renderClassic(renderer2D, pose, mouseX, mouseY, mainAlpha);
                  } else {
                     // Современный стиль - оригинальный дизайн
                     GuiScreen.x = scaledWidth / 2.0F - GuiScreen.width / 2.0F;
                     GuiScreen.y = scaledHeight / 2.0F - GuiScreen.height / 2.0F - (80.0F - 80.0F * mainAlpha);
                     
                     GuiRenderBackground.renderBackground(renderer2D, pose, mainAlpha);
                     GuiRenderLeftPanel.renderLeftPanel(renderer2D, pose, mainAlpha);
                     GuiRenderLines.renderLines(renderer2D, pose, mainAlpha);
                     GuiRenderMain.renderMain(renderer2D, pose, mouseX, mouseY, mainAlpha);
                     GuiRenderUpPanel.renderUpPanel(renderer2D, pose, mainAlpha);

                     ThemeScreen.renderTheme(renderer2D, p_281549_, mouseX, mouseY);
                  }
               } finally {
                  renderer2D.popTransform();
                  renderer2D.popTransform();
               }
            }
         }
      }
   }
}
