package ru.noxium.ui.gui.theme;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.DrawContext;
import org.joml.Vector4f;
import ru.noxium.Noxium;
import ru.noxium.module.api.Theme;
import ru.noxium.ui.gui.GuiScreen;
import ru.noxium.ui.gui.component.render.RenderUtil;
import ru.noxium.util.render.core.Renderer2D;
import ru.noxium.util.render.math.MathHelper;
import ru.noxium.util.render.math.ScaleHelper;
import ru.noxium.util.render.math.animation.Direction;

@Environment(EnvType.CLIENT)
public class ThemeScreen extends GuiScreen {
   public static void renderTheme(Renderer2D renderer2D, DrawContext drawContext, int mouseX1, int mouseY1) {
      float mainAnim = (float)GuiScreen.alphaPC.getValue();
      int color255 = (int)(255.0F * mainAnim);
      int color20 = (int)(90.0F * mainAnim);
      float centerX = mc.getWindow().getScaledWidth() / 2.0F;
      float y = 16 - (15.0F - 15.0F * mainAnim);
      int count = GuiScreen.themes.length;
      float offset = 18.0F;
      float totalWidth = count * offset;
      float startX = centerX - totalWidth / 2.0F;
      
      int bgColor = Renderer2D.ColorUtil.rgba(18, 18, 18, (int)(80.0F * mainAnim));
      int outlineColor = Renderer2D.ColorUtil.rgba(33, 33, 33, (int)(255.0F * mainAnim));
      
      if (GuiScreen.clientBlurSetting.get()) {
         renderer2D.prepareBlur(30.0F);
         renderer2D.blur(startX - 9.0F + 1.0F, y - 5.0F, totalWidth + 9.0F - 1.0F, 21.25F, 10.0F, mainAnim);
      }

      renderer2D.rect(startX - 9.0F + 1.0F, y - 5.0F, totalWidth + 9.0F - 1.0F, 21.25F, 10.0F, bgColor);
      renderer2D.rectOutline(startX - 9.0F + 1.0F, y - 5.0F, totalWidth + 9.0F - 1.0F, 25.0F, 10.0F, outlineColor, 2.0F);
      
      float x = startX;

      for (Theme theme : GuiScreen.themes) {
         theme.animation.setDirection(theme == GuiScreen.selectedTheme ? Direction.FORWARDS : Direction.BACKWARDS);
         renderer2D.shadow(x + 4.5F, y + 4.76F + 0.5F, 0.1F, 0.1F, 10.0F, 6.0F, 0.1F, Renderer2D.ColorUtil.setAlpha(theme.getMain(), (int)(color20 * theme.animation.getOutput())).getRGB());
         renderer2D.rect(x, y + 0.76F, 9.25F, 9.25F, 10.0F, Renderer2D.ColorUtil.setAlpha(theme.getMain(), color255).getRGB());
         x += offset;
      }
   }

   public static void mouseClickedTheme(double mouseX1, double mouseY1, int button) {
      int mouseX = (int) ScaleHelper.calc((float) mouseX1, (float) mouseY1)[0];
      int mouseY = (int) ScaleHelper.calc((float) mouseX1, (float) mouseY1)[1];
      float centerX = mc.getWindow().getScaledWidth() / 2.0F;
      float y = 16;
      int count = GuiScreen.themes.length;
      float offset = 18.0F;
      float totalWidth = count * offset;
      float startX = centerX - totalWidth / 2.0F;
      float x = startX;

      for (Theme theme : GuiScreen.themes) {
         if (MathHelper.isHovered(mouseX, mouseY, x, y, 16.0F, 16.0F)) {
            GuiScreen.animation14.reset();
            GuiScreen.preSelectedTheme = GuiScreen.selectedTheme;
            GuiScreen.selectedTheme = theme;
            Noxium.get.guiManager.setGuiTheme(theme);
         }

         x += offset;
      }
   }
}
