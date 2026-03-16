package ru.noxium.module.impl.visuals.HUD;

import java.util.ArrayList;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ItemStack;
import ru.noxium.module.impl.visuals.Hud;
import ru.noxium.ui.draggable.DraggableManager;
import ru.noxium.util.render.animation.util.Animation;
import ru.noxium.util.render.animation.util.Easings;
import ru.noxium.util.render.core.Renderer2D;

@Environment(EnvType.CLIENT)
public class ArmorHUD {
    private static final MinecraftClient mc = MinecraftClient.getInstance();
    private static final Animation widthAnimation = new Animation();
    private static final Animation heightAnimation = new Animation();
    public static Animation anim2 = new Animation();
    private static final List<PendingItemRender> pendingItems = new ArrayList<>();

    // Используем константы из HotBarHUD для идентичного вида
    private static final float HOTBAR_HEIGHT = 35.0F;
    private static final float SLOT_WIDTH = 35.0F;

    public static void armor(Renderer2D r2, DrawContext drawContext) {
        if (mc.player == null || mc.world == null)
            return;

        anim2.update();

        ItemStack head = mc.player.getEquippedStack(EquipmentSlot.HEAD);
        ItemStack chest = mc.player.getEquippedStack(EquipmentSlot.CHEST);
        ItemStack legs = mc.player.getEquippedStack(EquipmentSlot.LEGS);
        ItemStack feet = mc.player.getEquippedStack(EquipmentSlot.FEET);

        boolean hasArmor = !head.isEmpty() || !chest.isEmpty() || !legs.isEmpty() || !feet.isEmpty();

        // Ширина на 4 слота
        float width = SLOT_WIDTH * 4;
        float height = HOTBAR_HEIGHT;

        boolean isEmpty = !hasArmor;
        boolean closeCondition = isEmpty && !(mc.currentScreen instanceof ChatScreen);
        anim2.run(closeCondition ? 0.0 : 1.0, 0.15F, Easings.QUAD_OUT);

        widthAnimation.update();
        heightAnimation.update();
        widthAnimation.run(width, 0.15, Easings.QUART_OUT);
        heightAnimation.run(height, 0.15, Easings.QUART_OUT);

        float animatedWidth = widthAnimation.get();
        float animatedHeight = heightAnimation.get();

        if (!closeCondition || anim2.get() != 0.0F) {
            // Позиция по умолчанию — чуть выше хотбара
            float preferredX = (mc.getWindow().getWidth() - animatedWidth) / 2.0F;
            float preferredY = mc.getWindow().getHeight() - 110.0F;

            DraggableManager.DragSession session = DraggableManager.getInstance().beginDrag("armor", preferredX,
                    preferredY, animatedWidth, animatedHeight);
            float x = session.positionX();
            float y = session.positionY();

            // Рисуем одну общую панель, как в хотбаре
            // Используем радиус 8.0F для меньшего размера
            Hud.drawClientRect(r2, x, y, animatedWidth, animatedHeight, 8.0F, anim2.get(), 1.0F);

            ItemStack[] armor = { head, chest, legs, feet };
            float guiScale = (float) mc.getWindow().getScaleFactor();

            for (int i = 0; i < 4; i++) {
                ItemStack stack = armor[i];
                if (!stack.isEmpty()) {
                    // Центрируем предмет в его "слоте" шириной 51.5F
                    float slotCenterX = x + (i * SLOT_WIDTH) + (SLOT_WIDTH / 2.0F);
                    float itemRenderSize = 16.0F;

                    float itemX = (slotCenterX - itemRenderSize / 2.0F) / guiScale;
                    float itemY = (y + height / 2.0F - itemRenderSize / 2.0F) / guiScale;

                    // Добавляем те же смещения (-3.5F), что и в HotBarHUD для идеального
                    // выравнивания
                    pendingItems.add(new PendingItemRender(stack, itemX - 3.5F, itemY - 3.5F, i, 1.0F));
                }
            }

            DraggableManager.getInstance().endDrag(session);
        }

        renderPendingItems(drawContext);
    }

    public static void renderPendingItems(DrawContext drawContext) {
        if (!pendingItems.isEmpty()) {
            for (PendingItemRender item : pendingItems) {
                drawContext.getMatrices().pushMatrix();
                drawContext.getMatrices().translate(item.x, item.y);
                drawContext.getMatrices().scale(item.scale, item.scale);
                drawContext.drawItem(item.stack, 0, 0, item.seed);
                drawContext.drawStackOverlay(mc.textRenderer, item.stack, 0, 0);
                drawContext.getMatrices().popMatrix();
            }
            pendingItems.clear();
        }
    }

    @Environment(EnvType.CLIENT)
    private static class PendingItemRender {
        ItemStack stack;
        float x;
        float y;
        int seed;
        float scale;

        PendingItemRender(ItemStack stack, float x, float y, int seed, float scale) {
            this.stack = stack;
            this.x = x;
            this.y = y;
            this.seed = seed;
            this.scale = scale;
        }
    }
}
