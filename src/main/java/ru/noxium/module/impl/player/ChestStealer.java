package ru.noxium.module.impl.player;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.screen.GenericContainerScreenHandler;
import net.minecraft.screen.slot.SlotActionType;
import ru.noxium.event.EventInit;
import ru.noxium.event.impl.EventUpdate;
import ru.noxium.module.api.Category;
import ru.noxium.module.api.IModule;
import ru.noxium.module.api.Module;
import ru.noxium.module.api.setting.Setting;
import ru.noxium.module.api.setting.impl.BooleanSetting;
import ru.noxium.module.api.setting.impl.ModeSetting;
import ru.noxium.module.api.setting.impl.MultiBooleanSetting;
import ru.noxium.module.api.setting.impl.SliderSetting;
import ru.noxium.util.other.TimerUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@IModule(
    name = "ChestStealer",
    description = "Автоматически забирает предметы из сундуков",
    category = Category.Player,
    bind = -1
)
@Environment(EnvType.CLIENT)
public class ChestStealer extends Module {
    
    private final ModeSetting mode = new ModeSetting("Режим", "Умный", "Умный", "Всё");
    private final BooleanSetting chestClose = new BooleanSetting("Закрывать при полном", true);
    private final SliderSetting stealDelay = new SliderSetting("Задержка", 100.0F, 0.0F, 1000.0F, 10.0F, false);
    private final BooleanSetting filterLootToggle = new BooleanSetting("Фильтр лута", false)
        .hidden(() -> !mode.is("Умный"));
    private final MultiBooleanSetting filterLoot = new MultiBooleanSetting(
        "Лут",
        new BooleanSetting("Руды", true),
        new BooleanSetting("Головы", false),
        new BooleanSetting("Незеритовый слиток", false),
        new BooleanSetting("Зачарованная книга", false),
        new BooleanSetting("Тотемы", false),
        new BooleanSetting("Зелья", false)
    ).hidden(() -> !mode.is("Умный") || !filterLootToggle.get());
    private final SliderSetting itemLimit = new SliderSetting("Лимит кол-ва", 12.0F, 1.0F, 64.0F, 1.0F, false)
        .hidden(() -> !mode.is("Умный"));
    private final SliderSetting missPercent = new SliderSetting("Миссать %", 50.0F, 0.0F, 100.0F, 1.0F, true)
        .hidden(() -> !mode.is("Умный"));
    
    private final TimerUtil timerUtil = new TimerUtil();
    private final Random random = new Random();
    
    public ChestStealer() {
        addSettings(mode, chestClose, stealDelay, filterLootToggle, filterLoot, itemLimit, missPercent);
    }
    
    @EventInit
    public void onUpdate(EventUpdate event) {
        if (mc.player == null || mc.world == null) return;
        
        if (mode.is("Умный")) {
            handleSmartMode();
        } else if (mode.is("Всё")) {
            handleAllMode();
        }
    }
    
    private void handleSmartMode() {
        if (!(mc.player.currentScreenHandler instanceof GenericContainerScreenHandler)) {
            return;
        }
        
        GenericContainerScreenHandler container = (GenericContainerScreenHandler) mc.player.currentScreenHandler;
        
        // Получаем размер инвентаря сундука (без инвентаря игрока)
        int chestSize = container.getRows() * 9;
        
        List<Integer> validSlots = new ArrayList<>();
        
        for (int i = 0; i < chestSize; i++) {
            if (container.slots.size() <= i) continue;
            
            Item item = container.slots.get(i).getStack().getItem();
            int count = container.slots.get(i).getStack().getCount();
            
            if (item != Items.AIR && count <= (int)itemLimit.get() && filterItem(item)) {
                validSlots.add(i);
            }
        }
        
        if (!validSlots.isEmpty() && timerUtil.hasReached((long)stealDelay.get())) {
            int randomIndex = random.nextInt(validSlots.size());
            int slotToSteal = validSlots.get(randomIndex);
            
            // Миссаем с заданным процентом
            if (random.nextInt(100) >= (int)missPercent.get()) {
                mc.interactionManager.clickSlot(
                    container.syncId,
                    slotToSteal,
                    0,
                    SlotActionType.QUICK_MOVE,
                    mc.player
                );
            }
            
            timerUtil.reset();
        }
        
        // Закрываем сундук если он пустой
        if (chestClose.get() && isChestEmpty(container, chestSize)) {
            mc.player.closeHandledScreen();
        }
    }
    
    private void handleAllMode() {
        if (!(mc.player.currentScreenHandler instanceof GenericContainerScreenHandler)) {
            return;
        }
        
        GenericContainerScreenHandler container = (GenericContainerScreenHandler) mc.player.currentScreenHandler;
        int chestSize = container.getRows() * 9;
        
        for (int i = 0; i < chestSize; i++) {
            if (container.slots.size() <= i) continue;
            
            Item item = container.slots.get(i).getStack().getItem();
            
            if (item != Items.AIR && timerUtil.hasReached((long)stealDelay.get())) {
                mc.interactionManager.clickSlot(
                    container.syncId,
                    i,
                    0,
                    SlotActionType.QUICK_MOVE,
                    mc.player
                );
                timerUtil.reset();
                break; // Берем по одному предмету за раз
            }
        }
        
        // Закрываем сундук если он пустой
        if (chestClose.get() && isChestEmpty(container, chestSize)) {
            mc.player.closeHandledScreen();
        }
    }
    
    private boolean isChestEmpty(GenericContainerScreenHandler container, int chestSize) {
        for (int i = 0; i < chestSize; i++) {
            if (container.slots.size() <= i) continue;
            if (container.slots.get(i).getStack().getItem() != Items.AIR) {
                return false;
            }
        }
        return true;
    }
    
    private boolean filterItem(Item item) {
        if (!filterLootToggle.get()) {
            return true;
        }
        
        boolean filterOres = filterLoot.get("Руды");
        boolean filterHeads = filterLoot.get("Головы");
        boolean filterNetherite = filterLoot.get("Незеритовый слиток");
        boolean filterEnchantedBooks = filterLoot.get("Зачарованная книга");
        boolean filterTotems = filterLoot.get("Тотемы");
        boolean filterPotions = filterLoot.get("Зелья");
        
        if (filterOres && (item == Items.DIAMOND_ORE ||
                           item == Items.DEEPSLATE_DIAMOND_ORE ||
                           item == Items.EMERALD_ORE ||
                           item == Items.DEEPSLATE_EMERALD_ORE ||
                           item == Items.IRON_ORE ||
                           item == Items.DEEPSLATE_IRON_ORE ||
                           item == Items.GOLD_ORE ||
                           item == Items.DEEPSLATE_GOLD_ORE ||
                           item == Items.COAL_ORE ||
                           item == Items.DEEPSLATE_COAL_ORE ||
                           item == Items.DIAMOND ||
                           item == Items.EMERALD ||
                           item == Items.IRON_INGOT ||
                           item == Items.GOLD_INGOT ||
                           item == Items.COAL)) {
            return true;
        }
        
        if (filterHeads && (item == Items.PLAYER_HEAD ||
                            item == Items.ZOMBIE_HEAD ||
                            item == Items.CREEPER_HEAD ||
                            item == Items.SKELETON_SKULL ||
                            item == Items.WITHER_SKELETON_SKULL ||
                            item == Items.DRAGON_HEAD)) {
            return true;
        }
        
        if (filterNetherite && (item == Items.NETHERITE_INGOT ||
                                item == Items.NETHERITE_SCRAP ||
                                item == Items.ANCIENT_DEBRIS)) {
            return true;
        }
        
        if (filterEnchantedBooks && item == Items.ENCHANTED_BOOK) {
            return true;
        }
        
        if (filterTotems && item == Items.TOTEM_OF_UNDYING) {
            return true;
        }
        
        if (filterPotions && (item == Items.POTION ||
                              item == Items.SPLASH_POTION ||
                              item == Items.LINGERING_POTION)) {
            return true;
        }
        
        return false;
    }
}
