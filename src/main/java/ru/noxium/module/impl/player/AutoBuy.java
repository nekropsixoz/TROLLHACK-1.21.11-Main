package ru.noxium.module.impl.player;

import java.awt.Color;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.ingame.GenericContainerScreen;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.LoreComponent;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.c2s.play.UpdateSelectedSlotC2SPacket;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import ru.noxium.event.EventInit;
import ru.noxium.event.impl.EventScreen;
import ru.noxium.event.impl.EventUpdate;
import ru.noxium.event.input.MouseButtonEvent;
import ru.noxium.module.api.Category;
import ru.noxium.module.api.IModule;
import ru.noxium.module.api.Module;
import ru.noxium.module.api.setting.Setting;
import ru.noxium.module.api.setting.impl.BooleanSetting;
import ru.noxium.module.api.setting.impl.SliderSetting;
import ru.noxium.util.other.StopWatchShadow;
import ru.noxium.util.render.core.Renderer2D;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@IModule(name = "AutoBuy", category = Category.Player, description = "Автоматическая покупка и перепродажа на аукционе", bind = -1)
@Environment(EnvType.CLIENT)
public class AutoBuy extends Module {

    public BooleanSetting swapAnarchy = new BooleanSetting("Свапать анархии", true);
    public BooleanSetting autoResell = new BooleanSetting("Авто перевыставление", true);
    public BooleanSetting autoSnipe = new BooleanSetting("Авто скупка", true);
    public SliderSetting snipeDelay = new SliderSetting("Задержка обновления (мс)", 100, 10, 1000, 10, false);
    public SliderSetting resellPercent = new SliderSetting("На сколько % ниже аука", 12, 1, 100, 1, false);
    public SliderSetting parseInterval = new SliderSetting("Парсер (сек)", 300, 10, 600, 1, false);
    public BooleanSetting onlyTopItems = new BooleanSetting("Только топ вещи", true);

    private final StopWatchShadow anarchyTimer = new StopWatchShadow();
    private final StopWatchShadow auctionTimer = new StopWatchShadow();
    private final StopWatchShadow refreshTimer = new StopWatchShadow();
    private final StopWatchShadow resellCommandTimer = new StopWatchShadow();
    private final Random random = new Random();

    private final String[] anarchyCommands = {
            "an104", "an105", "an106", "an107", "an108",
            "an202", "an203", "an204", "an205", "an206", "an207", "an208", "an209", "an210", "an211", "an212", "an213",
            "an214", "an215", "an216", "an217",
            "an301", "an302", "an303", "an304",
            "an401", "an402",
            "an502", "an503", "an504",
            "an602", "an603"
    };

    private final String[] targetItems = {
            "Меч крушителя",
            "Нагрудник Крушителя",
            "Шлем Крушителя",
            "Поножи Крушителя",
            "Ботинки Крушителя",
            "Ареса",
            "Титана",
            "Ярости",
            "Карателя",
            "талисман Крушителя",
            "Сфера",
            "Драконий скин",
            "Трапка",
            "Божья аура",
            "Снежок заморозка",
            "незеритовый слиток"
    };

    private record ItemIdentity(Item item, String name, List<String> lore) {
    }

    private record SimplifiedIdentity(Item item, String name, boolean isTop) {
    }

    private final Map<SimplifiedIdentity, Long> itemPrices = new HashMap<>();
    private final List<Purchase> purchaseHistory = new ArrayList<>();
    private int currentItemIndex = -1;
    private State state = State.IDLE;
    private long lastActionTime = 0;

    private float buttonX, buttonY, buttonW, buttonH;
    private boolean wasBoughtOnThisPage = false;

    private enum State {
        IDLE,
        PARSING,
        SNIPING,
        RESELLING
    }

    private static class Purchase {
        ItemStack stack;
        long price;

        Purchase(ItemStack stack, long price) {
            this.stack = stack.copy();
            this.price = price;
        }
    }

    private static class ResellTask {
        SimplifiedIdentity identity;
        long price;
        long addedTime;
        int ticksInHand = 0;

        ResellTask(SimplifiedIdentity identity, long price) {
            this.identity = identity;
            this.price = price;
            this.addedTime = System.currentTimeMillis();
        }
    }

    private final List<ResellTask> resellQueue = new ArrayList<>();

    public AutoBuy() {
        this.addSettings(
                new Setting[] { swapAnarchy, autoResell, autoSnipe, snipeDelay, resellPercent, parseInterval });
    }

    @Override
    public void onEnable() {
        super.onEnable();
        anarchyTimer.reset();
        auctionTimer.reset();
        resellCommandTimer.reset();
        state = State.IDLE;
        currentItemIndex = -1;
        wasBoughtOnThisPage = false;
    }

    @EventInit
    public void onUpdate(EventUpdate event) {
        if (mc.player == null || mc.world == null)
            return;

        // Anarchy Swap logic
        if (swapAnarchy.get() && anarchyTimer.hasTimeElapsed(180000L, true)) {
            String command = anarchyCommands[random.nextInt(anarchyCommands.length)];
            mc.getNetworkHandler().sendChatCommand(command);
        }

        // Auction logic
        switch (state) {
            case IDLE -> {
                if (autoResell.get() && !resellQueue.isEmpty()) {
                    state = State.RESELLING;
                } else if (auctionTimer.hasTimeElapsed((long) parseInterval.current * 1000L) || itemPrices.isEmpty()) {
                    startParsing();
                } else if (autoSnipe.get()) {
                    startSniping();
                }
            }
            case PARSING -> handleParsing();
            case SNIPING -> handleSniping();
            case RESELLING -> {
                if (resellQueue.isEmpty()) {
                    state = State.IDLE;
                } else {
                    resellQueue.removeIf(task -> {
                        if (System.currentTimeMillis() - task.addedTime > 20000L) {
                            return true;
                        }
                        return attemptResell(task);
                    });
                }
            }
        }

        // Auto /ah resell logic
        if (autoResell.get() && resellCommandTimer.hasTimeElapsed(70000L, true)) {
            mc.getNetworkHandler().sendChatCommand("ah resell");
        }
    }

    private void startParsing() {
        itemPrices.clear();
        currentItemIndex = 0;
        state = State.PARSING;
        searchNextItem();
    }

    private void searchNextItem() {
        if (currentItemIndex >= targetItems.length) {
            state = State.IDLE;
            auctionTimer.reset();
            if (autoSnipe.get())
                startSniping();
            return;
        }

        String itemName = targetItems[currentItemIndex];
        mc.getNetworkHandler().sendChatCommand("ah search " + itemName);
        lastActionTime = System.currentTimeMillis();
    }

    private void handleParsing() {
        if (System.currentTimeMillis() - lastActionTime < 1000)
            return;

        if (mc.currentScreen instanceof GenericContainerScreen chestScreen) {
            String title = chestScreen.getTitle().getString();
            if (title.contains("Поиск:") || title.contains("Аукцион")) {
                boolean itemsLoaded = false;
                for (int i = 0; i < 45; i++) {
                    if (!chestScreen.getScreenHandler().getSlot(i).getStack().isEmpty()) {
                        itemsLoaded = true;
                        break;
                    }
                }

                if (itemsLoaded) {
                    parseAndStorePrice(chestScreen);
                    currentItemIndex++;
                    mc.player.closeHandledScreen();
                    searchNextItem();
                } else if (System.currentTimeMillis() - lastActionTime > 4000L) {
                    mc.player.sendMessage(
                            Text.literal("§c[AutoBuy] §7Нет результатов для: §f" + targetItems[currentItemIndex]),
                            false);
                    currentItemIndex++;
                    mc.player.closeHandledScreen();
                    searchNextItem();
                }
            }
        } else if (System.currentTimeMillis() - lastActionTime > 5000L) {
            currentItemIndex++;
            searchNextItem();
        }
    }

    private void parseAndStorePrice(GenericContainerScreen screen) {
        String searchTarget = targetItems[currentItemIndex].toLowerCase();
        List<Long> prices = new ArrayList<>();

        for (Slot slot : screen.getScreenHandler().slots) {
            if (slot.id < 45) {
                ItemStack stack = slot.getStack();
                if (!stack.isEmpty()) {
                    String cleanName = Formatting.strip(stack.getName().getString()).toLowerCase();
                    if (cleanName.contains(searchTarget)) {
                        ItemIdentity identity = createIdentity(stack);
                        boolean isTop = satisfiesStrictRequirements(identity);

                        if (onlyTopItems.get() && !isTop) {
                            continue;
                        }

                        Long price = getPriceFromStack(stack);
                        if (price != null && price > 0) {
                            prices.add(price);
                        }
                    }
                }
            }
        }

        if (!prices.isEmpty()) {
            prices.sort(Long::compare);
            // Use the minimum as the absolute base, but we'll show the snipe limit based on
            // it
            long marketPrice = prices.get(0);

            // If we have multiple items, use the 2nd one as a more 'stable' market price
            // to avoid being tricked by a single very low 'bait' price.
            if (prices.size() > 1 && prices.get(0) < prices.get(1) * 0.7) {
                marketPrice = prices.get(1);
            }

            // Store for each item found (if they have slightly different names but same
            // base)
            for (Slot slot : screen.getScreenHandler().slots) {
                if (slot.id < 45) {
                    ItemStack stack = slot.getStack();
                    if (!stack.isEmpty()) {
                        String cleanName = Formatting.strip(stack.getName().getString()).toLowerCase();
                        if (cleanName.contains(searchTarget)) {
                            ItemIdentity identity = createIdentity(stack);
                            boolean isTop = satisfiesStrictRequirements(identity);
                            SimplifiedIdentity simplified = new SimplifiedIdentity(stack.getItem(), cleanName, isTop);

                            long existing = itemPrices.getOrDefault(simplified, Long.MAX_VALUE);
                            if (marketPrice < existing) {
                                itemPrices.put(simplified, marketPrice);
                            }
                        }
                    }
                }
            }

            long snipeLimit = (long) (marketPrice * (1.0 - resellPercent.current / 100.0));
            mc.player.sendMessage(
                    Text.literal("§a[AutoBuy] §7Рынок для §f" + targetItems[currentItemIndex] + "§7: §e"
                            + String.format("%,d", marketPrice) + "$ §7(Скупка до: §b"
                            + String.format("%,d", snipeLimit) + "$§7)"),
                    false);
        } else {
            mc.player.sendMessage(Text.literal("§c[AutoBuy] §7Цены не найдены для: §f" + targetItems[currentItemIndex]),
                    false);
        }
    }

    private void startSniping() {
        state = State.SNIPING;
        wasBoughtOnThisPage = false;
        mc.getNetworkHandler().sendChatCommand("ah");
        lastActionTime = System.currentTimeMillis();
    }

    private void handleSniping() {
        if (System.currentTimeMillis() - lastActionTime < 1000)
            return;

        if (!(mc.currentScreen instanceof GenericContainerScreen chestScreen)) {
            // If we have items to resell, let the RESELLING state take over
            if (autoResell.get() && !resellQueue.isEmpty()) {
                state = State.IDLE;
                return;
            }

            if (System.currentTimeMillis() - lastActionTime > 2000L) {
                startSniping();
            }
            return;
        }

        String title = chestScreen.getTitle().getString();
        if (!title.contains("Аукцион"))
            return;

        // Scan items
        boolean bought = false;
        if (!wasBoughtOnThisPage) {
            for (Slot slot : chestScreen.getScreenHandler().slots) {
                if (slot.id < 45) {
                    ItemStack stack = slot.getStack();
                    if (stack.isEmpty())
                        continue;

                    ItemIdentity identity = createIdentity(stack);
                    boolean isTop = satisfiesStrictRequirements(identity);

                    if (onlyTopItems.get() && !isTop) {
                        continue;
                    }

                    Long price = getPriceFromStack(stack);
                    String cleanName = Formatting.strip(stack.getName().getString()).toLowerCase();
                    SimplifiedIdentity simplified = new SimplifiedIdentity(stack.getItem(), cleanName, isTop);
                    Long marketPrice = itemPrices.get(simplified);

                    if (price != null && marketPrice != null
                            && price <= (long) (marketPrice * (1.0 - resellPercent.current / 100.0))) {
                        // Snipe!
                        snipeItem(chestScreen, slot.id, identity, marketPrice);
                        bought = true;
                        break;
                    }
                    if (bought)
                        break;
                }
            }
        }

        if (!bought) {
            // Check if items from resellQueue have arrived in inventory
            boolean itemsArrived = false;
            for (ResellTask task : resellQueue) {
                if (isItemInInventory(task.identity)) {
                    itemsArrived = true;
                    break;
                }
            }

            // If we just bought something, wait until it arrives or fallback after 5s
            long waitTime = wasBoughtOnThisPage ? 5000L : 1500L;
            boolean timeout = System.currentTimeMillis() - lastActionTime > waitTime;

            // Prioritize reselling ONLY if items are actually in inventory
            if (autoResell.get() && itemsArrived && timeout) {
                mc.player.closeHandledScreen();
                state = State.RESELLING;
                wasBoughtOnThisPage = false; // Reset purchase flag
                return;
            }

            // Fallback: If we were waiting for a purchase that didn't arrive, continue
            // sniping
            if (wasBoughtOnThisPage && timeout) {
                wasBoughtOnThisPage = false;
            }

            // Refresh logic
            if (refreshTimer.hasTimeElapsed((long) snipeDelay.current)
                    && System.currentTimeMillis() - lastActionTime > 2000L && !wasBoughtOnThisPage) {
                // Nether Star is usually in slot 49
                mc.interactionManager.clickSlot(chestScreen.getScreenHandler().syncId, 49, 0, SlotActionType.PICKUP,
                        mc.player);
                refreshTimer.reset();
                wasBoughtOnThisPage = false;
            }
        }

        // Periodically re-parse prices
        if (auctionTimer.hasTimeElapsed((long) parseInterval.current * 1000L)) {
            mc.player.closeHandledScreen();
            startParsing();
        }
    }

    private void snipeItem(GenericContainerScreen screen, int slotId, ItemIdentity identity, long marketPrice) {
        ItemStack stack = screen.getScreenHandler().getSlot(slotId).getStack();
        Long price = getPriceFromStack(stack);
        String cleanName = Formatting.strip(stack.getName().getString()).toLowerCase();
        boolean isTop = satisfiesStrictRequirements(identity);

        // Record history
        if (price != null) {
            purchaseHistory.add(0, new Purchase(stack, price));
            if (purchaseHistory.size() > 10)
                purchaseHistory.remove(purchaseHistory.size() - 1);
        }

        // Hold shift and click 3 times
        for (int i = 0; i < 3; i++) {
            mc.interactionManager.clickSlot(screen.getScreenHandler().syncId, slotId, 0, SlotActionType.QUICK_MOVE,
                    mc.player);
        }
        lastActionTime = System.currentTimeMillis(); // Delay refresh after buy
        wasBoughtOnThisPage = true;

        mc.player.sendMessage(
                Text.literal("§6[AutoBuy] §7Покупаю: §f" + identity.name + " §7за §e" + price + " §7(рынок: "
                        + marketPrice + ")"),
                false);

        if (autoResell.get()) {
            SimplifiedIdentity simplified = new SimplifiedIdentity(stack.getItem(), cleanName, isTop);
            resellItem(simplified, marketPrice);
        }
    }

    private boolean isItemInInventory(SimplifiedIdentity target) {
        if (mc.player == null)
            return false;
        for (int i = 0; i < 36; i++) {
            ItemStack stack = mc.player.getInventory().getStack(i);
            if (stack.isEmpty())
                continue;
            String cleanName = Formatting.strip(stack.getName().getString()).toLowerCase();
            if (stack.getItem() == target.item
                    && (cleanName.contains(target.name) || target.name.contains(cleanName))) {
                return true;
            }
        }
        return false;
    }

    @EventInit
    public void onScreenRender(EventScreen event) {
        if (!(mc.currentScreen instanceof GenericContainerScreen chestScreen))
            return;
        String title = chestScreen.getTitle().getString();
        if (!title.contains("Аукцион") && !title.contains("Поиск:"))
            return;

        Renderer2D renderer = event.renderer();
        int width = event.viewportWidth();
        int height = event.viewportHeight();

        // Button dimensions (Centered above the chest)
        buttonW = 100;
        buttonH = 20;
        buttonX = width / 2f - buttonW / 2f;
        buttonY = height / 2f - 130; // Roughly above the container

        // Draw Button
        int btnColor = autoSnipe.get() ? new Color(50, 200, 50, 200).getRGB() : new Color(200, 50, 50, 200).getRGB();
        renderer.rect(buttonX, buttonY, buttonW, buttonH, 4, btnColor);
        renderer.rectOutline(buttonX, buttonY, buttonW, buttonH, 4, -1, 1);

        String btnText = "AutoBuy: " + (autoSnipe.get() ? "ON" : "OFF");
        renderer.text(event.defaultFont(), buttonX + buttonW / 2f, buttonY + buttonH / 2f - 4, 16, btnText, -1,
                "center");

        // Purchase History Panel (Right side)
        float historyX = width / 2f + 100;
        float historyY = height / 2f - 100;
        float historyW = 120;
        float historyH = 200;

        renderer.rect(historyX, historyY, historyW, historyH, 4, new Color(0, 0, 0, 150).getRGB());
        renderer.rectOutline(historyX, historyY, historyW, historyH, 4, -1, 1);
        renderer.text(event.defaultFont(), historyX + 5, historyY + 5, 14, "История покупок:", -1);

        float itemY = historyY + 25;
        for (Purchase p : purchaseHistory) {
            // Draw item icon
            event.drawContext().drawItem(p.stack, (int) historyX + 5, (int) itemY);

            String name = Formatting.strip(p.stack.getName().getString());
            if (name.length() > 15)
                name = name.substring(0, 12) + "...";
            renderer.text(event.defaultFont(), historyX + 25, itemY, 12, name, -1);
            renderer.text(event.defaultFont(), historyX + 25, itemY + 8, 12, "§e$" + p.price, -1);

            itemY += 22;
        }
    }

    @EventInit
    public void onMouseClick(MouseButtonEvent event) {
        if (!event.isPress() || event.button() != 0)
            return;
        if (!(mc.currentScreen instanceof GenericContainerScreen chestScreen))
            return;
        String title = chestScreen.getTitle().getString();
        if (!title.contains("Аукцион") && !title.contains("Поиск:"))
            return;

        double mouseX = event.cursorX();
        double mouseY = event.cursorY();

        if (mouseX >= buttonX && mouseX <= buttonX + buttonW && mouseY >= buttonY && mouseY <= buttonY + buttonH) {
            autoSnipe.set(!autoSnipe.get());
            mc.player.sendMessage(Text.literal("§6[AutoBuy] §7Снайпер: " + (autoSnipe.get() ? "§aВКЛ" : "§cВЫКЛ")),
                    true);
        }
    }

    private void resellItem(SimplifiedIdentity identity, long price) {
        resellQueue.add(new ResellTask(identity, price));
    }

    private boolean attemptResell(ResellTask task) {
        if (mc.player == null)
            return false;

        SimplifiedIdentity targetIdentity = task.identity;
        long price = task.price;

        for (int i = 0; i < 36; i++) {
            ItemStack stack = mc.player.getInventory().getStack(i);
            if (stack.isEmpty())
                continue;

            String cleanName = Formatting.strip(stack.getName().getString()).toLowerCase();

            // Very lenient check: item type must match and names must be similar
            boolean nameMatch = cleanName.equals(targetIdentity.name) ||
                    cleanName.contains(targetIdentity.name) ||
                    targetIdentity.name.contains(cleanName);

            if (stack.getItem() == targetIdentity.item && nameMatch) {
                if (i < 9) {
                    // In hotbar
                    if (mc.player.getInventory().getSelectedSlot() != i) {
                        mc.player.getInventory().setSelectedSlot(i);
                        mc.getNetworkHandler().sendPacket(new UpdateSelectedSlotC2SPacket(i));
                        task.ticksInHand = 0;
                        mc.player.sendMessage(Text.literal("§6[AutoBuy] §7Выбираем слот..."), true);
                        return false;
                    }

                    // Verify item is in hand
                    ItemStack handStack = mc.player.getMainHandStack();
                    String handCleanName = Formatting.strip(handStack.getName().getString()).toLowerCase();
                    boolean handNameMatch = handCleanName.contains(targetIdentity.name) ||
                            targetIdentity.name.contains(handCleanName);

                    if (handStack.getItem() == targetIdentity.item && handNameMatch) {
                        task.ticksInHand++;
                        if (task.ticksInHand == 1) {
                            mc.player.sendMessage(
                                    Text.literal("§6[AutoBuy] §7Выставляем §f" + targetIdentity.name + " §7за §e"
                                            + String.format("%,d", price) + "$"),
                                    true);
                        }
                        if (task.ticksInHand >= 15) { // Wait longer for server sync (approx 0.75s)
                            mc.getNetworkHandler().sendChatCommand("ah sell " + price);
                            mc.player.sendMessage(Text.literal("§6[AutoBuy] §7Предмет выставлен!"), true);
                            return true;
                        }
                    } else {
                        task.ticksInHand = 0;
                    }
                    return false;
                } else {
                    // In main inventory, move to hotbar
                    mc.player.sendMessage(Text.literal("§6[AutoBuy] §7Перемещаем предмет в хотбар..."), true);
                    int targetHotbarSlot = mc.player.getInventory().getSelectedSlot();
                    mc.interactionManager.clickSlot(mc.player.playerScreenHandler.syncId, i,
                            targetHotbarSlot, SlotActionType.SWAP, mc.player);
                    return false;
                }
            }
        }
        return false;
    }

    private ItemIdentity createIdentity(ItemStack stack) {
        if (stack.isEmpty())
            return null;
        Item item = stack.getItem();
        String name = Formatting.strip(stack.getName().getString()).toLowerCase();
        List<String> cleanedLore = new ArrayList<>();
        LoreComponent loreComponent = stack.get(DataComponentTypes.LORE);
        if (loreComponent != null) {
            for (Text line : loreComponent.lines()) {
                String text = line.getString();
                String stripped = Formatting.strip(text);
                if (stripped.startsWith("---") || stripped.isEmpty())
                    continue;

                String lower = stripped.toLowerCase();
                // Common dynamic lore mapping that shouldn't define identity
                if (lower.contains("цена") || lower.contains("price") || lower.contains("$") ||
                        lower.contains("продавец") || lower.contains("seller") || lower.contains("владелец") ||
                        lower.contains("owner") || lower.contains("время") || lower.contains("time") ||
                        lower.contains("прочность") || lower.contains("durability")) {
                    continue;
                }
                cleanedLore.add(stripped.trim());
            }
        }
        return new ItemIdentity(item, name, cleanedLore);
    }

    private boolean satisfiesStrictRequirements(ItemIdentity identity) {
        if (identity == null)
            return false;
        String name = identity.name.toLowerCase();

        // Pass simple items (skin, traps, ingots, etc.)
        if (name.contains("скин") || name.contains("трапка") || name.contains("аура") ||
                name.contains("снежок") || name.contains("слиток")) {
            return true;
        }

        // Check for "Original Item" tag
        boolean isOriginal = identity.lore.stream()
                .anyMatch(l -> l.contains("Оригинальный предмет") || l.contains("Оригинальный"));
        if (isOriginal)
            return true;

        // Check by item type first to avoid keyword overlap (e.g. Armor of Ares vs
        // Sword of Ares)
        if (name.contains("меч") || name.contains("топор")) {
            return identity.lore.stream().anyMatch(l -> l.contains("Острота VII") || l.contains("Острота 7") ||
                    l.contains("Острота VI") || l.contains("Острота 6"));
        }

        if (name.contains("нагрудник") || name.contains("шлем") || name.contains("поножи")
                || name.contains("ботинки")) {
            return identity.lore.stream().anyMatch(l -> l.contains("Защита VII") || l.contains("Защита 7") ||
                    l.contains("Защита VI") || l.contains("Защита 6"));
        }

        // Generic check for Talismans, Spheres, and other special items
        return identity.lore.stream().anyMatch(l -> l.contains("VII") || l.contains("VI") ||
                l.contains("Легендарный") || l.contains("Урон +") || l.contains("Броня +") ||
                l.contains("Уровень II") || l.contains("Уровень III"));
    }

    private Long getPriceFromStack(ItemStack stack) {
        LoreComponent lore = stack.get(DataComponentTypes.LORE);
        if (lore == null)
            return null;

        for (Text line : lore.lines()) {
            Long price = findPriceInText(line.getString());
            if (price != null)
                return price;
        }
        return null;
    }

    private Long findPriceInText(String text) {
        if (text == null || text.isEmpty())
            return null;

        String stripped = Formatting.strip(text);
        // Supports space, comma, dot as thousands separators
        Pattern numberPattern = Pattern.compile("\\d{1,3}(?:[.,\\s]?\\d{3})*");
        Matcher matcher = numberPattern.matcher(stripped);

        Long largestPrice = null;
        long maxValue = 0;

        while (matcher.find()) {
            String numberStr = matcher.group();
            String cleanedNumber = numberStr.replaceAll("[.,\\s]", "");

            try {
                long value = Long.parseLong(cleanedNumber);
                String lowerContext = stripped.toLowerCase();

                // Keywords to identify price lines
                if ((lowerContext.contains("цена") || lowerContext.contains("стоимость") ||
                        lowerContext.contains("купить") || lowerContext.contains("price") ||
                        lowerContext.contains("скидка") || lowerContext.contains("$"))
                        && value > maxValue && value >= 100) {
                    maxValue = value;
                    largestPrice = value;
                }
            } catch (NumberFormatException ignored) {
            }
        }

        return largestPrice;
    }
}
