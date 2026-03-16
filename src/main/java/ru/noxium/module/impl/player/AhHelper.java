package ru.noxium.module.impl.player;

import lombok.Getter;
import lombok.Setter;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.ingame.GenericContainerScreen;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.LoreComponent;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import ru.noxium.event.EventInit;
import ru.noxium.event.impl.EventUpdate;
import ru.noxium.event.impl.KeyPressEvent;
import ru.noxium.event.player.EventInput;
import ru.noxium.module.api.Category;
import ru.noxium.module.api.IModule;
import ru.noxium.module.api.Module;
import ru.noxium.module.api.setting.Setting;
import ru.noxium.module.api.setting.impl.BindSettings;
import ru.noxium.module.api.setting.impl.BooleanSetting;
import ru.noxium.module.api.setting.impl.ModeSetting;
import ru.noxium.util.other.StopWatch;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Getter
@Setter
@IModule(name = "AhHelper", category = Category.Player, description = "Помощник на ауке", bind = -1)
@Environment(EnvType.CLIENT)
public class AhHelper extends Module {
    public BooleanSetting three = new BooleanSetting("Самое дешевое", true);
    public BooleanSetting showPricePerItem = new BooleanSetting("Показывать цену за штуку", true);
    public BindSettings searchBind = new BindSettings("Поиск по бинду", -1);
    public BooleanSetting autoRelist = new BooleanSetting("Автоперевыставление", false);
    public ModeSetting autoRelistMode = new ModeSetting("Режим автоперевыставления", "элита", "элита", "игрок");

    private boolean price = false;
    float x = 0.0F;
    float y = 0.0F;
    float x2 = 0.0F;
    float y2 = 0.0F;
    float x3 = 0.0F;
    float y3 = 0.0F;

    private PriceInfo priceInfo1 = null;
    private PriceInfo priceInfo2 = null;
    private PriceInfo priceInfo3 = null;

    private StopWatch eliteTimer = new StopWatch();
    private boolean eliteMovingForward = true;
    private boolean eliteActive = false;
    private boolean eliteMoved2Blocks = false;
    private double eliteStartX = 0;
    private double eliteStartZ = 0;

    private StopWatch playerTimer = new StopWatch();

    public AhHelper() {
        this.autoRelistMode.hidden = () -> !autoRelist.get();
        this.addSettings(new Setting[] { three, showPricePerItem, autoRelist, autoRelistMode, searchBind });
    }

    @Getter
    @Setter
    public static class PriceInfo {
        private int totalPrice;
        private int count;
        private int pricePerItem;

        public PriceInfo(int totalPrice, int count) {
            this.totalPrice = totalPrice;
            this.count = count;
            this.pricePerItem = count > 0 ? totalPrice / count : 0;
        }
    }

    @Override
    public void onDisable() {
        eliteActive = false;
        eliteMoved2Blocks = false;
        eliteStartX = 0;
        eliteStartZ = 0;
        eliteTimer.reset();
        playerTimer.reset();
        super.onDisable();
    }

    @EventInit
    private void onKey(KeyPressEvent e) {
        if (e.getKey() == searchBind.get() && searchBind.get() != -1) {
            if (mc.player == null)
                return;

            ItemStack heldItem = mc.player.getMainHandStack();
            if (heldItem.isEmpty()) {
                heldItem = mc.player.getOffHandStack();
            }

            if (!heldItem.isEmpty()) {
                String itemName = Formatting.strip(heldItem.getName().getString());
                if (itemName != null && !itemName.isEmpty()) {
                    itemName = itemName.replaceAll("^\\[.*?\\]\\s*", "").trim();
                    itemName = itemName.replaceAll("^.{1,3}\\s+", "");
                    itemName = itemName.replaceAll("\\s+.{1,3}$", "");
                    itemName = itemName.trim();

                    if (!itemName.isEmpty()) {
                        String command = "ah search " + itemName;
                        if (mc.getNetworkHandler() != null) {
                            mc.getNetworkHandler().sendChatCommand(command);
                        }
                    }
                }
            }
        }
    }

    @EventInit
    public void onInput(EventInput event) {
        if (!this.enable)
            return;

        if (autoRelist.get() && autoRelistMode.is("игрок") && mc.player != null) {
            if (mc.currentScreen instanceof GenericContainerScreen) {
                if (mc.player.isOnGround()) {
                    event.setJump(true);
                }
            }
        }

        if (autoRelist.get() && autoRelistMode.is("элита") && mc.player != null) {
            if (eliteActive) {
                if (eliteMoved2Blocks) {
                    event.setForward(0.0f);
                    event.setStrafe(0.0f);

                    if (mc.getNetworkHandler() != null) {
                        mc.getNetworkHandler().sendChatCommand("ah resell");
                    }

                    eliteActive = false;
                    eliteMoved2Blocks = false;
                    eliteStartX = 0;
                    eliteStartZ = 0;
                    eliteTimer.reset();
                    return;
                }

                if (!eliteMoved2Blocks) {
                    if (eliteMovingForward) {
                        event.setForward(1.0f);
                    } else {
                        event.setForward(-1.0f);
                    }
                    event.setStrafe(0.0f);

                    if (eliteStartX == 0 && eliteStartZ == 0) {
                        eliteStartX = mc.player.getX();
                        eliteStartZ = mc.player.getZ();
                    }

                    if (mc.player.isOnGround()) {
                        event.setJump(true);
                    }

                    if (eliteStartX != 0 && eliteStartZ != 0) {
                        double currentX = mc.player.getX();
                        double currentZ = mc.player.getZ();
                        double distanceX = currentX - eliteStartX;
                        double distanceZ = currentZ - eliteStartZ;
                        double totalDistance = Math.sqrt(distanceX * distanceX + distanceZ * distanceZ);

                        if (totalDistance >= 2.0) {
                            eliteMoved2Blocks = true;
                            event.setForward(0.0f);
                            event.setStrafe(0.0f);
                        }
                    }
                }
            }
        }
    }

    @EventInit
    public void onUpdate(EventUpdate update) {
        if (!this.enable)
            return;

        if (autoRelist.get() && autoRelistMode.is("элита") && mc.player != null) {
            if (eliteTimer.elapsedTime() >= 65000L) {
                eliteActive = true;
                eliteMoved2Blocks = false;
                eliteStartX = 0;
                eliteStartZ = 0;
                eliteMovingForward = !eliteMovingForward;
                eliteTimer.reset();
            }
        }

        if (autoRelist.get() && autoRelistMode.is("игрок") && mc.player != null) {
            if (mc.currentScreen instanceof GenericContainerScreen chestScreen) {
                if (playerTimer.elapsedTime() >= 65000L) {
                    for (Slot slot : chestScreen.getScreenHandler().slots) {
                        if (slot != null && slot.hasStack()) {
                            ItemStack stack = slot.getStack();
                            if (stack.isOf(Items.CLOCK)) {
                                if (mc.interactionManager != null) {
                                    mc.interactionManager.clickSlot(chestScreen.getScreenHandler().syncId, slot.id, 0,
                                            SlotActionType.PICKUP, mc.player);
                                }
                                playerTimer.reset();
                                break;
                            }
                        }
                    }
                }
            }
        }

        if (mc.currentScreen instanceof GenericContainerScreen e) {
            String title = e.getTitle().getString();
            if (!title.contains("Аукцион") && !title.contains("Поиск:")) {
                resetPrices();
            } else {
                int fsPrice = Integer.MAX_VALUE;
                int medPrice = Integer.MAX_VALUE;
                int thPrice = Integer.MAX_VALUE;

                Slot slot1 = null;
                Slot slot2 = null;
                Slot slot3 = null;

                for (Slot slot : e.getScreenHandler().slots) {
                    if (slot.id <= 44) {
                        PriceInfo currentPriceInfo = this.extractPriceInfoFromStack(slot.getStack());
                        if (currentPriceInfo != null) {
                            int currentPrice = currentPriceInfo.getPricePerItem();
                            if (currentPrice != -1 && currentPrice < fsPrice) {
                                fsPrice = currentPrice;
                                slot1 = slot;
                                priceInfo1 = currentPriceInfo;
                            }

                            if (three.get()) {
                                if (currentPrice != -1 && currentPrice < medPrice && currentPrice > fsPrice) {
                                    medPrice = currentPrice;
                                    slot2 = slot;
                                    priceInfo2 = currentPriceInfo;
                                }

                                if (currentPrice != -1 && currentPrice < thPrice && currentPrice > medPrice) {
                                    thPrice = currentPrice;
                                    slot3 = slot;
                                    priceInfo3 = currentPriceInfo;
                                }
                            } else {
                                this.x2 = 0.0F;
                                this.x3 = 0.0F;
                                priceInfo2 = null;
                                priceInfo3 = null;
                            }
                        }
                    }
                }

                if (slot1 != null) {
                    this.x = (float) slot1.x;
                    this.y = (float) slot1.y;
                } else {
                    priceInfo1 = null;
                }

                if (slot2 != null) {
                    this.x2 = (float) slot2.x;
                    this.y2 = (float) slot2.y;
                } else {
                    priceInfo2 = null;
                }

                if (slot3 != null) {
                    this.x3 = (float) slot3.x;
                    this.y3 = (float) slot3.y;
                } else {
                    priceInfo3 = null;
                }
            }
        } else {
            resetPrices();
        }
    }

    private void resetPrices() {
        this.x = 0.0F;
        this.x2 = 0.0F;
        this.x3 = 0.0F;
        this.price = false;
        priceInfo1 = null;
        priceInfo2 = null;
        priceInfo3 = null;
    }

    public PriceInfo extractPriceInfoFromStack(ItemStack stack) {
        if (stack == null || stack.isEmpty()) {
            return null;
        }

        LoreComponent lore = stack.get(DataComponentTypes.LORE);
        if (lore == null) {
            return null;
        }

        int count = stack.getCount();
        if (count <= 0) {
            return null;
        }

        for (Text line : lore.lines()) {
            String combinedText = line.getString();
            Integer totalPrice = findPriceInText(combinedText);

            if (totalPrice != null && totalPrice > 0) {
                return new PriceInfo(totalPrice, count);
            }
        }

        return null;
    }

    private Integer findPriceInText(String text) {
        if (text == null || text.isEmpty()) {
            return null;
        }

        Pattern numberPattern = Pattern.compile("\\d{1,3}(?:[,\\s]?\\d{3})*");
        Matcher matcher = numberPattern.matcher(text);

        Integer largestPrice = null;
        int maxValue = 0;

        while (matcher.find()) {
            String numberStr = matcher.group();
            String cleanedNumber = numberStr.replaceAll("[,\\s]", "");

            try {
                int value = Integer.parseInt(cleanedNumber);

                int startPos = matcher.start();
                int endPos = matcher.end();

                int contextStart = Math.max(0, startPos - 50);
                int contextEnd = Math.min(text.length(), endPos + 50);
                String context = text.substring(contextStart, contextEnd).toLowerCase();

                if ((context.contains("цена") || context.contains("price") || context.contains("$"))
                        && value > maxValue && value >= 100) {
                    maxValue = value;
                    largestPrice = value;
                }
            } catch (NumberFormatException e) {
            }
        }

        return largestPrice;
    }
}
