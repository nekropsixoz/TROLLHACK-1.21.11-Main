package ru.noxium.module.impl.visuals.HUD;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.entity.player.ItemCooldownManager;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.client.gui.screen.ChatScreen;
import ru.noxium.module.impl.visuals.Hud;
import ru.noxium.ui.draggable.DraggableManager;
import ru.noxium.util.color.ColorUtil;
import ru.noxium.util.render.animation.util.Animation;
import ru.noxium.util.render.animation.util.Easings;
import ru.noxium.util.render.core.Renderer2D;
import ru.noxium.util.render.text.FontRegistry;

@Environment(EnvType.CLIENT)
public class CoolDowns {
    public static MinecraftClient mc = MinecraftClient.getInstance();
    private static final Animation widthAnimation = new Animation();
    private static final Animation heightAnimation = new Animation();
    public static Animation anim = new Animation();

    public static void render(Renderer2D r2, DrawContext drawContext) {
        if (mc.player == null)
            return;

        ItemCooldownManager cooldownManager = mc.player.getItemCooldownManager();
        List<CooldownEntry> activeCooldowns = getActiveCooldowns(cooldownManager);

        // Sort by remaining time (longest first)
        activeCooldowns.sort(Comparator.comparingDouble(CooldownEntry::getRemainingTime).reversed());

        anim.update();
        boolean expand = !activeCooldowns.isEmpty();
        String headerName = "CoolDowns";
        float headerTextWidth = r2.measureText(FontRegistry.INTER_MEDIUM, headerName, 28.0F).width;

        float minWidth = 46.0F + headerTextWidth + 10.0F;
        float calculatedWidth = minWidth;

        if (expand) {
            for (CooldownEntry entry : activeCooldowns) {
                String name = getRussianName(entry.item);
                String time = formatTime(entry.remainingTime);

                float nameWidth = r2.measureText(FontRegistry.INTER_MEDIUM, name, 28.0F).width;
                float timeWidth = r2.measureText(FontRegistry.INTER_MEDIUM, time, 28.0F).width;

                // name + space + bar (30) + space + time + icon
                float totalWidth = 10.0F + nameWidth + 10.0F + 30.0F + 10.0F + timeWidth + 5.0F + 1.0F + 5.0F + 12.0F
                        + 10.0F;
                calculatedWidth = Math.max(calculatedWidth, totalWidth);
            }
        }

        calculatedWidth = Math.max(calculatedWidth, minWidth);
        float headerHeight = 16.0F;
        float calculatedHeight = headerHeight;
        if (expand) {
            calculatedHeight = 50.0F + headerHeight + (activeCooldowns.size() * 20.0F) + 5.0F;
        }

        calculatedHeight = Math.max(calculatedHeight, 55.0F);

        boolean isEmpty = activeCooldowns.isEmpty();
        boolean closeCondition = isEmpty && !(mc.currentScreen instanceof ChatScreen);
        anim.run(closeCondition ? 0.0 : 1.0, 0.15F, Easings.QUAD_OUT);

        widthAnimation.update();
        heightAnimation.update();
        widthAnimation.run(calculatedWidth, 0.15, Easings.QUART_OUT);
        heightAnimation.run(calculatedHeight, 0.15, Easings.QUART_OUT);

        float animatedWidth = widthAnimation.get();
        float animatedHeight = heightAnimation.get();

        if (anim.get() > 0.01F) {
            float preferredX = 20.0F;
            float preferredY = 300.0F;
            DraggableManager.DragSession session = DraggableManager.getInstance().beginDrag("cooldowns", preferredX,
                    preferredY, animatedWidth, animatedHeight);
            float x = session.positionX();
            float y = session.positionY() - 40.0F + 40.0F * anim.get();

            Hud.drawClientRect(r2, x, y, animatedWidth, animatedHeight, 13.0F, 1.0F * anim.get(), 1.0F);

            // Icon for the header
            r2.text(FontRegistry.ICONS, x + 10.0F, y + 30.0F, 36.0F, "F",
                    ColorUtil.replAlpha(0xA0D8FF, anim.get()));

            r2.shadow(x + 14.0F, y + 20.0F, 0.3F, 0.3F, 10.0F, 7.5F, 0.5F,
                    Renderer2D.ColorUtil.replAlpha(0xA0D8FF,
                            (int) (75.0F * anim.get())));

            r2.rect(x + 35.0F, y + 15.5F, 1.0F, 11.0F,
                    Renderer2D.ColorUtil.replAlpha(0xA0D8FF,
                            (int) (51.0F * anim.get())));

            r2.text(FontRegistry.INTER_MEDIUM, x + 42.0F, y + 26.0F, 28.0F, headerName,
                    ColorUtil.replAlpha(0xA0D8FF, anim.get()));

            r2.rect(x, y + 39.52F, animatedWidth, 1.0F,
                    Renderer2D.ColorUtil.replAlpha(0xA0D8FF,
                            (int) (25.0F * anim.get())));

            if (expand) {
                float offset = 0.0F;
                float guiScale = (float) mc.getWindow().getScaleFactor();

                for (CooldownEntry entry : activeCooldowns) {
                    float lineY = y + 50.0F + headerHeight + offset;
                    float addx = 40.0F - 40.0F * anim.get();

                    String name = getRussianName(entry.item);
                    String time = formatTime(entry.remainingTime);

                    int textColor = Renderer2D.ColorUtil.replAlpha(0xA0D8FF,
                            (int) (anim.get() * 255.0F));
                    int mainColor = Renderer2D.ColorUtil.replAlpha(0xA0D8FF,
                            (int) (anim.get() * 255.0F));

                    // Draw Item Name
                    r2.text(FontRegistry.INTER_MEDIUM, x + 10.0F + addx, lineY, 28.0F, name, textColor);

                    float nameWidth = r2.measureText(FontRegistry.INTER_MEDIUM, name, 28.0F).width;
                    float barX = x + 10.0F + nameWidth + 5.0F + addx;
                    float barY = lineY - 4.0F;

                    // Progress Bar from logic snippet
                    r2.rect(barX, barY, 30.0F, 2.0F, 1.0F, Renderer2D.ColorUtil.replAlpha(0x000000, 100));
                    r2.rect(barX, barY, 30.0F * entry.progress, 2.0F, 1.0F, mainColor);

                    float timeWidth = r2.measureText(FontRegistry.INTER_MEDIUM, time, 28.0F).width;
                    float iconAreaWidth = 12.0F + 5.0F + 1.0F + 5.0F + 10.0F;
                    float timeX = x + animatedWidth - timeWidth - iconAreaWidth + addx;

                    // Draw Time
                    r2.text(FontRegistry.INTER_MEDIUM, timeX, lineY, 28.0F, time, mainColor);

                    float separatorX = x + animatedWidth - 12.0F - 10.0F - 5.0F - 1.0F + addx;
                    r2.rect(separatorX, lineY - 8.0F, 1.0F, 10.0F,
                            Renderer2D.ColorUtil.replAlpha(mainColor, (int) (51.0F * anim.get())));

                    float scale = 0.75f;
                    float iconX = (x + animatedWidth - 12.0F - 10.0F + addx) / guiScale;
                    float iconY = (lineY - 9.0F) / guiScale;

                    // Draw Item Icon
                    pendingItems.add(new PendingItemRender(new ItemStack(entry.item), iconX, iconY, scale, 0));

                    offset += 20.0F;
                }
            }
            DraggableManager.getInstance().endDrag(session);
        }
    }

    private static final List<PendingItemRender> pendingItems = new ArrayList<>();

    public static void renderPendingItems(DrawContext drawContext) {
        if (!pendingItems.isEmpty()) {
            for (PendingItemRender item : pendingItems) {
                drawContext.getMatrices().pushMatrix();
                drawContext.getMatrices().translate(item.x, item.y);
                drawContext.getMatrices().scale(item.scale, item.scale);
                drawContext.drawItem(item.stack, 0, 0, item.seed);
                drawContext.getMatrices().popMatrix();
            }
            pendingItems.clear();
        }
    }

    private static class PendingItemRender {
        ItemStack stack;
        float x;
        float y;
        float scale;
        int seed;

        PendingItemRender(ItemStack stack, float x, float y, float scale, int seed) {
            this.stack = stack;
            this.x = x;
            this.y = y;
            this.scale = scale;
            this.seed = seed;
        }
    }

    private static String getRussianName(Item item) {
        if (item == Items.ENCHANTED_GOLDEN_APPLE)
            return "Чарка";
        if (item == Items.ENDER_EYE)
            return "Дезорент";
        if (item == Items.NETHERITE_SCRAP)
            return "Трапка";
        if (item == Items.SUGAR)
            return "Явная пыль";
        if (item == Items.ENDER_PEARL)
            return "Эндер-перл";
        if (item == Items.GOLDEN_APPLE)
            return "Гэпл";
        if (item == Items.DRIED_KELP)
            return "Пласт";
        return item.getName().getString();
    }

    private static String formatTime(float seconds) {
        if (seconds >= 60) {
            int mins = (int) (seconds / 60);
            int secs = (int) (seconds % 60);
            return String.format("%d:%02d", mins, secs);
        } else {
            return String.format("%.1f", seconds);
        }
    }

    @SuppressWarnings("unchecked")
    private static List<CooldownEntry> getActiveCooldowns(ItemCooldownManager manager) {
        List<CooldownEntry> entries = new ArrayList<>();
        if (mc.player == null)
            return entries;

        try {
            Field entriesField = null;
            for (Field f : ItemCooldownManager.class.getDeclaredFields()) {
                if (Map.class.isAssignableFrom(f.getType())) {
                    f.setAccessible(true);
                    entriesField = f;
                    break;
                }
            }

            if (entriesField != null) {
                Map<Item, Object> internalMap = (Map<Item, Object>) entriesField.get(manager);
                for (Map.Entry<Item, Object> entry : internalMap.entrySet()) {
                    Item item = entry.getKey();
                    Object cooldownObj = entry.getValue();

                    float progress = manager.getCooldownProgress(new ItemStack(item),
                            mc.getRenderTickCounter().getTickProgress(true));

                    if (progress > 0.001f) {
                        int startTick = 0;
                        int endTick = 0;

                        for (Field f : cooldownObj.getClass().getDeclaredFields()) {
                            f.setAccessible(true);
                            if (f.getType() == int.class) {
                                String name = f.getName();
                                if (name.toLowerCase().contains("start")) {
                                    startTick = f.getInt(cooldownObj);
                                } else if (name.toLowerCase().contains("end")) {
                                    endTick = f.getInt(cooldownObj);
                                }
                            }
                        }

                        if (startTick == 0 && endTick == 0) {
                            int count = 0;
                            for (Field f : cooldownObj.getClass().getDeclaredFields()) {
                                f.setAccessible(true);
                                if (f.getType() == int.class) {
                                    if (count == 0)
                                        startTick = f.getInt(cooldownObj);
                                    else if (count == 1)
                                        endTick = f.getInt(cooldownObj);
                                    count++;
                                }
                            }
                        }

                        int currentTick = 0;
                        for (Field f : ItemCooldownManager.class.getDeclaredFields()) {
                            if (f.getType() == int.class) {
                                f.setAccessible(true);
                                currentTick = f.getInt(manager);
                                break;
                            }
                        }

                        float remainingSeconds = (endTick - currentTick) / 20.0f;
                        if (remainingSeconds > 0) {
                            entries.add(new CooldownEntry(item, remainingSeconds, progress));
                        }
                    }
                }
                if (!entries.isEmpty())
                    return entries;
            }
        } catch (Exception ignored) {
        }

        // Fallback: check all inventory items
        for (int i = 0; i < mc.player.getInventory().size(); i++) {
            ItemStack stack = mc.player.getInventory().getStack(i);
            if (!stack.isEmpty()) {
                Item item = stack.getItem();
                float progress = manager.getCooldownProgress(new ItemStack(item),
                        mc.getRenderTickCounter().getTickProgress(true));
                if (progress > 0.001f) {
                    boolean found = false;
                    for (CooldownEntry e : entries)
                        if (e.item == item)
                            found = true;
                    if (!found)
                        entries.add(new CooldownEntry(item, progress * 20.0f, progress));
                }
            }
        }

        ItemStack offhand = mc.player.getOffHandStack();
        if (!offhand.isEmpty()) {
            Item item = offhand.getItem();
            float progress = manager.getCooldownProgress(new ItemStack(item),
                    mc.getRenderTickCounter().getTickProgress(true));
            if (progress > 0.001f) {
                boolean found = false;
                for (CooldownEntry e : entries)
                    if (e.item == item)
                        found = true;
                if (!found)
                    entries.add(new CooldownEntry(item, progress * 20.0f, progress));
            }
        }

        return entries;
    }

    private static class CooldownEntry {
        Item item;
        float remainingTime;
        float progress;

        public CooldownEntry(Item item, float remainingTime, float progress) {
            this.item = item;
            this.remainingTime = remainingTime;
            this.progress = progress;
        }

        public float getRemainingTime() {
            return remainingTime;
        }
    }
}
