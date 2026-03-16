package ru.noxium.ui.gui;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.text.Text;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.Click;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.input.CharInput;
import net.minecraft.client.input.KeyInput;
import org.lwjgl.glfw.GLFW;
import ru.noxium.Noxium;
import ru.noxium.config.GuiManager;
import ru.noxium.event.EventInit;
import ru.noxium.event.EventManager;
import ru.noxium.event.render.RenderEvent;
import ru.noxium.module.api.Category;
import ru.noxium.module.api.Manager;
import ru.noxium.module.api.Theme;
import ru.noxium.ui.gui.component.main.GuiCharTyped;
import ru.noxium.ui.gui.component.main.GuiInit;
import ru.noxium.ui.gui.component.main.GuiKeyPressed;
import ru.noxium.ui.gui.component.main.GuiMouseDragged;
import ru.noxium.ui.gui.component.main.GuiMouseReleased;
import ru.noxium.ui.gui.component.main.GuiMouseScrolled;
import ru.noxium.ui.gui.component.main.GuiShouldCloseOnEsc;
import ru.noxium.ui.gui.component.mouse.GuiMouseClicked;
import ru.noxium.ui.gui.component.render.GuiRender;
import ru.noxium.ui.gui.theme.ThemeScreen;
import ru.noxium.util.player.MovementManager;
import ru.noxium.util.render.core.Renderer2D;

@Environment(EnvType.CLIENT)
public class GuiClient extends Screen {
   public ThemeScreen themeScreen;
   public MinecraftClient mc = MinecraftClient.getInstance();
   private static volatile boolean eventsRegistered = false;

   public GuiClient() {
      super(Text.literal("Gui"));
   }

   public static void registerEventHandlers() {
      if (!eventsRegistered) {
         eventsRegistered = true;
         EventManager.register(new Object() {
            @EventInit
            public void onRender(RenderEvent event) {
               MinecraftClient client = event.client();
               if (client != null && client.currentScreen instanceof GuiClient) {
                  double[] mouseX = new double[1];
                  double[] mouseY = new double[1];
                  if (client.getWindow() != null) {
                     GLFW.glfwGetCursorPos(client.getWindow().getHandle(), mouseX, mouseY);
                     if (client.mouse != null) {
                        client.mouse.unlockCursor();
                     }
                  }

                  int mouseXInt = (int)mouseX[0];
                  int mouseYInt = (int)mouseY[0];
                  DrawContext drawContext = null;
                  GuiRender.render(event.renderer(), drawContext, mouseXInt, mouseYInt, client.getRenderTickCounter().getDynamicDeltaTicks());
               }
            }
         });
      }
   }

   public void render(DrawContext context, int mouseX, int mouseY, float deltaTicks) {
   }

   public void renderBackground(DrawContext context, int mouseX, int mouseY, float delta) {
   }

   public void renderInGameBackground(DrawContext context) {
   }

   @Override
   public boolean mouseClicked(Click click, boolean bl) {
      Renderer2D renderer = Noxium.getRenderer();
      return renderer != null && GuiMouseClicked.mouseClicked(renderer, click.x(), click.y(), click.button()) ? true : super.mouseClicked(click, bl);
   }

   @Override
   public boolean mouseReleased(Click click) {
      GuiMouseReleased.mouseReleased();
      return super.mouseReleased(click);
   }

   @Override
   public boolean mouseDragged(Click click, double pDragX, double pDragY) {
      return GuiMouseDragged.mouseDragged(click.x(), click.y(), click.button(), pDragX, pDragY) ? true : super.mouseDragged(click, pDragX, pDragY);
   }

   public boolean mouseScrolled(double pMouseX, double pMouseY, double pScrollX, double pScrollY) {
      return GuiMouseScrolled.mouseScrolled(pMouseX, pMouseY, pScrollX, pScrollY) ? true : super.mouseScrolled(pMouseX, pMouseY, pScrollX, pScrollY);
   }

   @Override
   public boolean keyPressed(KeyInput input) {
      return GuiKeyPressed.keyPressed(input.key(), input.scancode(), input.modifiers()) ? true : super.keyPressed(input);
   }

   @Override
   public boolean charTyped(CharInput input) {
      return GuiCharTyped.charTyped((char)input.codepoint(), input.modifiers()) ? true : super.charTyped(input);
   }

   public boolean shouldCloseOnEsc() {
      return GuiShouldCloseOnEsc.shouldCloseOnEsc();
   }

   public void close() {
      MovementManager.getInstance().unlockMovement("Search");
      GuiScreen.activeSearch = false;
      GuiScreen.searchText = "";
      Noxium.get.guiManager.setGuiCategory(GuiScreen.selectedCategories);
      super.close();
   }

   public void tick() {
      super.tick();
      if (GuiScreen.exit && GuiScreen.alphaPC.isFinished()) {
         this.close();
         GuiScreen.exit = false;
      }
   }

   public boolean shouldPause() {
      return false;
   }

   public void init() {
      super.init();
      this.themeScreen = new ThemeScreen();
      GuiInit.init();
      MinecraftClient client = MinecraftClient.getInstance();
      if (client != null && client.mouse != null) {
         client.mouse.unlockCursor();
      }

      GuiScreen.categories = Category.values();
      GuiScreen.themes = Theme.values();
      GuiScreen.width = 366.475F;
      GuiScreen.height = 238.805F;
      GuiScreen.x = 480.0F - GuiScreen.width / 2.0F;
      GuiScreen.y = 260.0F - GuiScreen.height / 2.0F;
      GuiScreen.mainAnimation.reset();
      if (Noxium.get.guiManager == null) {
         Noxium.get.guiManager = new GuiManager();
         Noxium.get.guiManager.init();
      }

      GuiScreen.selectedTheme = Noxium.get.guiManager.getCurrentTheme();
      GuiScreen.preSelectedTheme = Noxium.get.guiManager.getCurrentTheme();
      GuiScreen.selectedCategories = Noxium.get.guiManager.getCurrentCategory();
      if (Noxium.get.manager == null) {
         Noxium.get.manager = new Manager();
      }

      GuiScreen.modules = Noxium.get.manager.getType(GuiScreen.selectedCategories);
   }
}
