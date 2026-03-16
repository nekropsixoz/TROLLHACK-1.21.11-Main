package ru.noxium.ui.gui.component.mouse.category;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import ru.noxium.Noxium;
import ru.noxium.module.api.Category;
import ru.noxium.ui.gui.GuiScreen;
import ru.noxium.ui.gui.component.render.GuiRenderMain;
import ru.noxium.util.render.math.animation.Direction;

@Environment(EnvType.CLIENT)
public class GuiMouseClickedCategory extends GuiScreen {
   // Custom Order: Combat -> Movement -> Visuals -> Player -> Misc
   public static Category[] sortedCategories = { Category.Combat, Category.Movement, Category.Visuals, Category.Player,
         Category.Misc };

   public static void mouseClickedCategory(int mouseX, int mouseY) {
      float iconSize = 24.0F;
      float spacing = 15.0F;
      int categoryCount = sortedCategories.length;
      float totalWidth = (categoryCount * iconSize) + ((categoryCount - 1) * spacing);
      float startX = GuiScreen.x + (GuiScreen.width - totalWidth) / 2.0F;
      float startY = GuiScreen.y + GuiScreen.height - 35.0F;

      float currentX = startX;
      for (Category category : sortedCategories) {
         if (GuiRenderMain.isHovered(mouseX, mouseY, currentX, startY, iconSize, iconSize)
               && GuiScreen.selectedCategories != category) {
            GuiScreen.animation15.setDirection(Direction.BACKWARDS);
            GuiScreen.activeColorPicker = null;
            GuiScreen.selectedCategories = category;
            GuiScreen.modules = Noxium.get.manager.getType(GuiScreen.selectedCategories);
            GuiScreen.categoryAnimation.reset();
            GuiScreen.moduleAnimation.reset();
            GuiScreen.getScrollUtil().reset();
            Noxium.get.guiManager.setGuiCategory(category);
         }
         currentX += iconSize + spacing;
      }
   }
}
