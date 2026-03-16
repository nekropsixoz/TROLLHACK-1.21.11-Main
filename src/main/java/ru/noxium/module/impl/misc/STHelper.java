package ru.noxium.module.impl.misc;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerInteractItemC2SPacket;
import net.minecraft.network.packet.c2s.play.UpdateSelectedSlotC2SPacket;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.LoreComponent;
import net.minecraft.util.math.Vec3d;
import org.lwjgl.glfw.GLFW;
import ru.noxium.event.EventInit;
import ru.noxium.event.render.EventRender3D;
import ru.noxium.event.impl.EventUpdate;
import ru.noxium.module.api.Category;
import ru.noxium.module.api.IModule;
import ru.noxium.module.api.Module;
import ru.noxium.module.api.setting.Setting;
import ru.noxium.module.api.setting.impl.BindSettings;
import ru.noxium.module.api.setting.impl.ModeSetting;
import ru.noxium.util.other.StopWatchShadow;

import java.awt.Color;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@IModule(name = "STHelper", description = "Помощник для серверов", category = Category.Misc, bind = -1)
@Getter
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class STHelper extends Module {

    public static ModeSetting mode = new ModeSetting("Тип сервера", "FunTime", "FunTime");

    List<KeyBind> keyBindings = new ArrayList<>();

    List<String> potionQueue = new ArrayList<>();
    StopWatchShadow potionTimer = new StopWatchShadow();
    Map<String, ItemInfo> itemConfig = new HashMap<>();
    Map<String, Boolean> lastKeyStates = new HashMap<>();
    Map<String, Boolean> keyPressedThisTick = new HashMap<>();

    @NonFinal
    int originalSlot = -1;
    @NonFinal
    int targetSlot = -1;
    @NonFinal
    ActionState actionState = ActionState.IDLE;
    @NonFinal
    long actionTimer = 0;
    @NonFinal
    String pendingItemKey = null;
    @NonFinal
    int originalSourceSlot = -1;

    private enum ActionState {
        IDLE, SWAP_TO_ITEM, USE_ITEM, SWAP_BACK
    }

    private static class ItemInfo {
        List<String> loreKeywords;
        String nameFallback;
        Item item;
        String displayName;
        boolean funTimeOnly;
        // EffectRequirement logic removed as it requires implementation or dependencies
        // not provided fully in noxium utils
        // List<EffectRequirement> effectRequirements;

        ItemInfo(List<String> loreKeywords, String nameFallback, Item item, String displayName, boolean funTimeOnly) {
            this.loreKeywords = loreKeywords;
            this.nameFallback = nameFallback;
            this.item = item;
            this.displayName = displayName;
            this.funTimeOnly = funTimeOnly;
        }

        ItemInfo(String nameFallback, Item item, String displayName) {
            this.loreKeywords = null;
            this.nameFallback = nameFallback;
            this.item = item;
            this.displayName = displayName;
            this.funTimeOnly = false;
        }
    }

    public STHelper() {
        initialize();
    }

    public void initialize() {
        addSettings(new Setting[] { mode });

        // Only FunTime items
        keyBindings.add(new KeyBind(Items.SNOWBALL, new BindSettings("Снежок заморозка", -1)
                .hidden(() -> !mode.is("FunTime")), 7));
        keyBindings.add(new KeyBind(Items.PHANTOM_MEMBRANE, new BindSettings("Божья аура", -1)
                .hidden(() -> !mode.is("FunTime")), 2));
        keyBindings.add(new KeyBind(Items.NETHERITE_SCRAP, new BindSettings("Трапка", -1)
                .hidden(() -> !mode.is("FunTime")), 0));
        keyBindings.add(new KeyBind(Items.DRIED_KELP, new BindSettings("Пласт", -1)
                .hidden(() -> !mode.is("FunTime")), 0));
        keyBindings.add(new KeyBind(Items.SUGAR, new BindSettings("Явная пыль", -1)
                .hidden(() -> !mode.is("FunTime")), 10));
        keyBindings.add(new KeyBind(Items.FIRE_CHARGE, new BindSettings("Огненный смерч", -1)
                .hidden(() -> !mode.is("FunTime")), 10));
        keyBindings.add(new KeyBind(Items.ENDER_EYE, new BindSettings("Дезориентация", -1)
                .hidden(() -> !mode.is("FunTime")), 10));

        keyBindings.add(new KeyBind(Items.SPLASH_POTION, new BindSettings("Хлопушка", -1)
                .hidden(() -> !mode.is("FunTime")), 0));
        keyBindings.add(new KeyBind(Items.SPLASH_POTION, new BindSettings("Святая вода", -1)
                .hidden(() -> !mode.is("FunTime")), 0));
        keyBindings.add(new KeyBind(Items.SPLASH_POTION, new BindSettings("Зелье Гнева", -1)
                .hidden(() -> !mode.is("FunTime")), 0));
        keyBindings.add(new KeyBind(Items.SPLASH_POTION, new BindSettings("Зелье Палладина", -1)
                .hidden(() -> !mode.is("FunTime")), 0));
        keyBindings.add(new KeyBind(Items.SPLASH_POTION, new BindSettings("Зелье Ассасина", -1)
                .hidden(() -> !mode.is("FunTime")), 0));
        keyBindings.add(new KeyBind(Items.SPLASH_POTION, new BindSettings("Зелье Радиации", -1)
                .hidden(() -> !mode.is("FunTime")), 0));
        keyBindings.add(new KeyBind(Items.SPLASH_POTION, new BindSettings("Снотворное", -1)
                .hidden(() -> !mode.is("FunTime")), 0));

        keyBindings.forEach(bind -> addSettings(new Setting[] { bind.setting() }));

        itemConfig.put("sugar", new ItemInfo(
                List.of("световая вспышка", "радиус: 10 блоков", "свечение", "слепота"),
                "явная пыль", Items.SUGAR, "Явная пыль", true));

        itemConfig.put("disorientation", new ItemInfo(
                List.of("чем ближе цель, тем дольше длительность эффектов"),
                "дезориентация", Items.ENDER_EYE, "Дезориентация", true));

        itemConfig.put("trap", new ItemInfo(
                List.of("нерушимая клетка", "длительность: 15 секунд"),
                "трапка", Items.NETHERITE_SCRAP, "Трапка", true));

        itemConfig.put("plast", new ItemInfo(
                List.of("нерушимая стена", "вертикальный:", "горизонтальный:"),
                "пласт", Items.DRIED_KELP, "Пласт", true));

        itemConfig.put("fireSwirl", new ItemInfo(
                List.of("огненная волна", "радиус: 10 блоков", "поджог"),
                "огненный смерч", Items.FIRE_CHARGE, "Огненный смерч", true));

        itemConfig.put("snow", new ItemInfo(
                List.of("ледяная сфера", "радиус: 7 блоков", "заморозка", "слабость"),
                "снежок заморозка", Items.SNOWBALL, "Снежок заморозка", true));

        itemConfig.put("bojaura", new ItemInfo(
                List.of("божественная аура", "радиус: 2 блока", "снятие всех эффектов", "невидимость"),
                "божья аура", Items.PHANTOM_MEMBRANE, "Божья аура", true));

        // Potion naming logic kept simple as EffectRequirement class is removed to
        // avoid complexity/missing classes
        itemConfig.put("hlopushka", new ItemInfo(null, "хлопушка", Items.SPLASH_POTION, "Хлопушка", true));
        itemConfig.put("holywater", new ItemInfo(null, "святая вода", Items.SPLASH_POTION, "Святая вода", true));
        itemConfig.put("gnev", new ItemInfo(null, "зелье гнева", Items.SPLASH_POTION, "Зелье Гнева", true));
        itemConfig.put("paladin", new ItemInfo(null, "зелье палладина", Items.SPLASH_POTION, "Зелье Палладина", true));
        itemConfig.put("assassin", new ItemInfo(null, "зелье ассасина", Items.SPLASH_POTION, "Зелье Ассасина", true));
        itemConfig.put("radiation", new ItemInfo(null, "зелье радиации", Items.SPLASH_POTION, "Зелье Радиации", true));
        itemConfig.put("snotvornoe", new ItemInfo(null, "снотворное", Items.SPLASH_POTION, "Снотворное", true));

        itemConfig.keySet().forEach(key -> {
            lastKeyStates.put(key, false);
            keyPressedThisTick.put(key, false);
        });
    }

    @Override
    public void onEnable() {
        super.onEnable();
        potionQueue.clear();
        potionTimer.reset();
        lastKeyStates.replaceAll((k, v) -> false);
        keyPressedThisTick.replaceAll((k, v) -> false);
        actionState = ActionState.IDLE;
        originalSlot = -1;
        targetSlot = -1;
        originalSourceSlot = -1;
        pendingItemKey = null;
    }

    @Override
    public void onDisable() {
        super.onDisable();
        lastKeyStates.replaceAll((k, v) -> false);
        keyPressedThisTick.replaceAll((k, v) -> false);
        potionQueue.clear();
        potionTimer.reset();
        actionState = ActionState.IDLE;
        originalSlot = -1;
        targetSlot = -1;
        originalSourceSlot = -1;
        pendingItemKey = null;
    }

    @EventInit
    public void onUpdate(EventUpdate e) {
        if (mc.player == null || mc.world == null || mc.currentScreen != null)
            return;

        processKeyBindings();

        if (actionState != ActionState.IDLE) {
            processItemAction();
        }

        processItemQueue();
    }

    private void processKeyBindings() {
        for (KeyBind bind : keyBindings) {
            String key = getKeyFromBinding(bind.setting().name);
            if (key != null && !bind.setting().hidden.get()) {
                boolean currentKey = bind.setting().isKeyDown(bind.setting().get());
                boolean wasPressedLastTick = lastKeyStates.getOrDefault(key, false);

                if (currentKey && !wasPressedLastTick) {
                    ItemInfo info = itemConfig.get(key);
                    if (info != null) {
                        int slotIndex = findSlotByItem(info);
                        if (slotIndex != -1) {
                            ItemStack stack = mc.player.getInventory().getStack(slotIndex);
                            if (!mc.player.getItemCooldownManager().isCoolingDown(stack)) {
                                if (!potionQueue.contains(key)) {
                                    potionQueue.add(key);
                                }
                            }
                        } else {
                            ru.noxium.Noxium.get.manager.get(ru.noxium.module.impl.visuals.Hud.class).showNotification(
                                    "warn", "Предмет " + info.displayName + " не найден!", 1500L,
                                    ru.noxium.util.render.core.Renderer2D.ColorUtil.getTextTwoColor(1, 1));
                        }
                    }
                }
                lastKeyStates.put(key, currentKey);
                keyPressedThisTick.put(key, currentKey);
            }
        }
    }

    private void processItemQueue() {
        if (actionState == ActionState.IDLE && !potionQueue.isEmpty() && potionTimer.hasTimeElapsed(50)) {
            String potionKey = potionQueue.remove(0);
            ItemInfo info = itemConfig.get(potionKey);
            int slotIndex = findSlotByItem(info);
            if (slotIndex != -1) {
                ItemStack stack = mc.player.getInventory().getStack(slotIndex);
                if (!mc.player.getItemCooldownManager().isCoolingDown(stack)) {
                    startItemUse(slotIndex, info);
                }
            }
            potionTimer.reset();
        }
    }

    private void startItemUse(int slotIndex, ItemInfo info) {
        originalSlot = mc.player.getInventory().getSelectedSlot();
        originalSourceSlot = slotIndex;
        targetSlot = slotIndex;
        pendingItemKey = info.displayName;

        actionState = ActionState.SWAP_TO_ITEM;
        actionTimer = System.currentTimeMillis();
    }

    private void processItemAction() {
        long currentTime = System.currentTimeMillis();
        long elapsed = currentTime - actionTimer;

        switch (actionState) {
            case SWAP_TO_ITEM -> {
                performSwapToItem();
                actionState = ActionState.USE_ITEM;
                actionTimer = currentTime;
            }
            case USE_ITEM -> {
                if (elapsed >= 25) {
                    performUseItem();
                    actionState = ActionState.SWAP_BACK;
                    actionTimer = currentTime;
                }
            }
            case SWAP_BACK -> {
                if (elapsed >= 25) {
                    performSwapBack();
                    actionState = ActionState.IDLE;
                    originalSlot = -1;
                    targetSlot = -1;
                    originalSourceSlot = -1;
                    pendingItemKey = null;
                }
            }
        }
    }

    private void performSwapToItem() {
        if (targetSlot >= 0 && targetSlot < 9) {
            if (targetSlot != originalSlot) {
                mc.getNetworkHandler().sendPacket(new UpdateSelectedSlotC2SPacket(targetSlot));
                mc.player.getInventory().setSelectedSlot(targetSlot);
            }
        } else {
            // Swap item from inventory directly into the current hotbar slot
            boolean sprinting = mc.player.isSprinting();
            if (sprinting) {
                mc.getNetworkHandler()
                        .sendPacket(new ClientCommandC2SPacket(mc.player, ClientCommandC2SPacket.Mode.STOP_SPRINTING));
            }

            mc.interactionManager.clickSlot(mc.player.currentScreenHandler.syncId, targetSlot, originalSlot,
                    SlotActionType.SWAP, mc.player);

            if (sprinting) {
                mc.getNetworkHandler()
                        .sendPacket(new ClientCommandC2SPacket(mc.player, ClientCommandC2SPacket.Mode.START_SPRINTING));
            }
            targetSlot = originalSlot;
        }
    }

    private void performUseItem() {
        boolean sprinting = mc.player.isSprinting();
        if (sprinting) {
            mc.getNetworkHandler()
                    .sendPacket(new ClientCommandC2SPacket(mc.player, ClientCommandC2SPacket.Mode.STOP_SPRINTING));
        }

        mc.interactionManager.interactItem(mc.player, Hand.MAIN_HAND);

        if (sprinting) {
            mc.getNetworkHandler()
                    .sendPacket(new ClientCommandC2SPacket(mc.player, ClientCommandC2SPacket.Mode.START_SPRINTING));
        }
    }

    private void performSwapBack() {
        boolean wasFromInventory = !(originalSourceSlot >= 0 && originalSourceSlot < 9)
                && !(originalSourceSlot >= 36 && originalSourceSlot < 45);

        if (wasFromInventory) {
            boolean sprinting = mc.player.isSprinting();
            if (sprinting) {
                mc.getNetworkHandler()
                        .sendPacket(new ClientCommandC2SPacket(mc.player, ClientCommandC2SPacket.Mode.STOP_SPRINTING));
            }

            mc.interactionManager.clickSlot(mc.player.currentScreenHandler.syncId, originalSourceSlot, originalSlot,
                    SlotActionType.SWAP, mc.player);

            if (sprinting) {
                mc.getNetworkHandler()
                        .sendPacket(new ClientCommandC2SPacket(mc.player, ClientCommandC2SPacket.Mode.START_SPRINTING));
            }
        } else {
            if (originalSourceSlot >= 36 && originalSourceSlot < 45) {
                int hotbarSlot = originalSourceSlot - 36;
                if (targetSlot != hotbarSlot) {
                    mc.getNetworkHandler().sendPacket(new UpdateSelectedSlotC2SPacket(hotbarSlot));
                    mc.player.getInventory().setSelectedSlot(hotbarSlot);
                }
            } else if (originalSourceSlot >= 0 && originalSourceSlot < 9) {
                if (targetSlot != originalSourceSlot) {
                    mc.getNetworkHandler().sendPacket(new UpdateSelectedSlotC2SPacket(originalSourceSlot));
                    mc.player.getInventory().setSelectedSlot(originalSourceSlot);
                }
            }
        }

        if (mc.player.getInventory().getSelectedSlot() != originalSlot) {
            mc.getNetworkHandler().sendPacket(new UpdateSelectedSlotC2SPacket(originalSlot));
            mc.player.getInventory().setSelectedSlot(originalSlot);
        }
    }

    private int findSlotByItem(ItemInfo info) {
        for (int i = 0; i < 36; i++) {
            ItemStack stack = mc.player.getInventory().getStack(i);
            if (stack.isEmpty() || !stack.getItem().equals(info.item))
                continue;

            if (info.loreKeywords != null && !info.loreKeywords.isEmpty()) {
                if (matchesLore(stack, info.loreKeywords))
                    return i;
            }

            if (info.nameFallback != null && !info.nameFallback.isEmpty()) {
                if (getCleanName(stack.getName()).contains(info.nameFallback.toLowerCase()))
                    return i;
            }
        }
        return -1;
    }

    private List<String> getLore(ItemStack stack) {
        List<String> lore = new ArrayList<>();
        if (stack == null || stack.isEmpty())
            return lore;
        try {
            LoreComponent loreComponent = stack.get(DataComponentTypes.LORE);
            if (loreComponent != null) {
                for (Text text : loreComponent.lines()) {
                    String line = getCleanName(text);
                    if (!line.isEmpty())
                        lore.add(line);
                }
            }
        } catch (Exception ignored) {
        }
        return lore;
    }

    private boolean matchesLore(ItemStack stack, List<String> keywords) {
        if (keywords == null || keywords.isEmpty())
            return false;
        List<String> lore = getLore(stack);
        if (lore.isEmpty())
            return false;
        String fullLore = String.join(" ", lore).toLowerCase();
        int matchCount = 0;
        for (String keyword : keywords) {
            if (fullLore.contains(keyword.toLowerCase()))
                matchCount++;
        }
        return matchCount >= 1;
    }

    private String getCleanName(Text name) {
        return name.getString().toLowerCase().replaceAll("§[0-9a-fk-or]", "");
    }

    private String getKeyFromBinding(String bindingName) {
        return switch (bindingName) {
            case "Снежок заморозка" -> "snow";
            case "Божья аура" -> "bojaura";
            case "Трапка" -> "trap";
            case "Пласт" -> "plast";
            case "Явная пыль" -> "sugar";
            case "Огненный смерч" -> "fireSwirl";
            case "Дезориентация" -> "disorientation";
            case "Хлопушка" -> "hlopushka";
            case "Святая вода" -> "holywater";
            case "Зелье Гнева" -> "gnev";
            case "Зелье Палладина" -> "paladin";
            case "Зелье Ассасина" -> "assassin";
            case "Зелье Радиации" -> "radiation";
            case "Снотворное" -> "snotvornoe";
            default -> null;
        };
    }

    public record KeyBind(Item item, BindSettings setting, float distance) {
    }
}
