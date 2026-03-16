package ru.noxium.ui.gui.component.render;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.util.math.MatrixStack;
import ru.noxium.Noxium;
import ru.noxium.module.api.Category;
import ru.noxium.module.api.Module;
import ru.noxium.ui.gui.GuiScreen;
import ru.noxium.util.color.ColorUtil;
import ru.noxium.util.render.core.Renderer2D;
import ru.noxium.util.render.text.FontRegistry;
import ru.noxium.util.render.math.ScrollUtil;
import java.util.List;

@Environment(EnvType.CLIENT)
public class GuiRenderNew extends GuiScreen {
        public static void renderNew(Renderer2D renderer2D, MatrixStack pose, int mouseX, int mouseY, float mainAlpha) {
                // Compact Layout parameters
                float columnWidth = 110.0F;
                float spacing = 15.0F;
                float totalWidth = (columnWidth * 5) + (spacing * 4);

                float screenWidth = mc.getWindow().getScaledWidth();
                float screenHeight = mc.getWindow().getScaledHeight();

                float startX = screenWidth / 2.0F - totalWidth / 2.0F;
                float startY = screenHeight / 2.0F - 100.0F;

                float rounding = 8.0F;
                float headerHeight = 26.0F;
                float moduleHeight = 14.0F;
                float padding = 8.0F;
                int maxModules = 10;

                int themeColor = Renderer2D.ColorUtil.getMainColor(1, 1);
                Category[] categories = Category.values();

                for (int i = 0; i < categories.length; i++) {
                        Category category = categories[i];
                        float cx = startX + i * (columnWidth + spacing);
                        float cy = startY;

                        List<Module> modules = Noxium.get.manager.getType(category);
                        float listHeight = Math.min(modules.size(), maxModules) * moduleHeight;
                        float totalHeight = headerHeight + listHeight + padding;
                        float fullContentHeight = headerHeight + modules.size() * moduleHeight + padding;

                        ScrollUtil scroll = getCategoryScroll(category);
                        scroll.update();
                        scroll.setMax(fullContentHeight, totalHeight);

                        // 1. Deep Shadow (Atmospheric)
                        renderer2D.shadow(cx, cy, columnWidth, totalHeight, rounding, 20.0F, 1.0F,
                                        Renderer2D.ColorUtil.rgba(0, 0, 0, (int) (180 * mainAlpha)));

                        // 2. Dark Liquid Glass Body (No Blur)
                        int glassBgTop = Renderer2D.ColorUtil.rgba(18, 18, 24, (int) (170 * mainAlpha));
                        int glassBgBot = Renderer2D.ColorUtil.rgba(8, 8, 12, (int) (140 * mainAlpha));
                        renderer2D.verticalGradient(cx, cy, columnWidth, totalHeight, rounding, glassBgTop, glassBgBot);

                        // 4. Subtle Inner Theme Pulse
                        float pulse = (float) (0.5 + 0.5 * Math.sin(System.currentTimeMillis() / 800.0));
                        int pulseCol = Renderer2D.ColorUtil.replAlpha(themeColor, (int) (15 * pulse * mainAlpha));
                        renderer2D.rect(cx + 1.0F, cy + 1.0F, columnWidth - 2.0F, totalHeight - 2.0F, rounding,
                                        pulseCol);

                        // 5. Category Title
                        String catName = category.getName().toUpperCase();
                        float tw = renderer2D.measureText(FontRegistry.INTER_SEMIBOLD, catName, 11.0F).width;
                        renderer2D.text(FontRegistry.INTER_SEMIBOLD, cx + columnWidth / 2.0F - tw / 2.0F, cy + 13.0F,
                                        11.0F, catName,
                                        Renderer2D.ColorUtil.rgba(255, 255, 255, (int) (230 * mainAlpha)));

                        // 6. MODULES
                        renderer2D.pushRoundedClipRect(cx, cy + headerHeight, columnWidth, listHeight + padding, 0, 0,
                                        rounding, rounding);

                        float my = cy + headerHeight + scroll.getScroll();
                        for (Module module : modules) {
                                if (my + moduleHeight < cy + headerHeight || my > cy + totalHeight) {
                                        my += moduleHeight;
                                        continue;
                                }

                                boolean hovered = GuiRenderMain.isHovered(mouseX, mouseY, cx + 4.0F, my - 1.0F,
                                                columnWidth - 8.0F, moduleHeight - 1.0F);
                                if (!GuiRenderMain.isHovered(mouseX, mouseY, cx, cy + headerHeight, columnWidth,
                                                listHeight + padding)) {
                                        hovered = false;
                                }

                                module.animation.update();
                                double anim = module.animation.get();

                                if (anim > 0.005 || hovered) {
                                        float hAnim = (float) (hovered ? 1.0 : anim);
                                        int hb = Renderer2D.ColorUtil.replAlpha(themeColor,
                                                        (int) (45 * hAnim * mainAlpha));
                                        // Module Background Highlight
                                        renderer2D.rect(cx + 6.0F, my - 1.0F, columnWidth - 12.0F, moduleHeight - 2.0F,
                                                        4.0F, hb);

                                        if (anim > 0.005) {
                                                // Side Accent
                                                renderer2D.rect(cx + 4.0F, my + 3.0F, 1.5F, moduleHeight - 8.0F, 1.0F,
                                                                Renderer2D.ColorUtil.replAlpha(themeColor,
                                                                                (int) (255 * mainAlpha)));
                                        }
                                }

                                int textColor = ColorUtil.overCol(
                                                Renderer2D.ColorUtil.rgba(160, 160, 170, (int) (180 * mainAlpha)),
                                                Renderer2D.ColorUtil.rgba(255, 255, 255, (int) (255 * mainAlpha)),
                                                (float) anim);

                                renderer2D.text(FontRegistry.INTER_MEDIUM, cx + 15.0F, my + 7.5F, 10.5F, module.name,
                                                textColor);
                                my += moduleHeight;
                        }
                        renderer2D.popClipRect();
                }
        }
}
