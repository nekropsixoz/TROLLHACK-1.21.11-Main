package lol.ethane.feature.module.defined.combat;

import lol.ethane.event.defined.game.PreGameTickEvent;
import lol.ethane.event.subscriber.Subscribe;
import lol.ethane.feature.module.Module;
import lol.ethane.feature.module.ModuleCategory;
import lol.ethane.feature.module.property.impl.BooleanProperty;
import lol.ethane.feature.module.property.impl.NumberProperty;
import net.minecraft.class_1297;
import net.minecraft.class_1511;
import net.minecraft.class_1541;
import net.minecraft.class_1542;
import net.minecraft.class_1713;
import net.minecraft.class_1799;
import net.minecraft.class_1802;
import net.minecraft.class_2338;
import net.minecraft.class_2350;
import net.minecraft.class_2846;
import net.minecraft.class_2868;

public class AutoTotemModule extends Module {
    private final BooleanProperty elytraHealthCheck = new BooleanProperty("Elytra Health", true);
    private final BooleanProperty tntCheck = new BooleanProperty("TNT", true);
    private final BooleanProperty fallCheck = new BooleanProperty("Fall", false);
    private final BooleanProperty crystalCheck = new BooleanProperty("Crystal", false);
    
    private final NumberProperty health = new NumberProperty("Health", 4.0, 1.0, 20.0, 0.5);
    private final NumberProperty elytraHealth = new NumberProperty("Elytra Health Threshold", 9.0, 0.0, 20.0, 0.5);
    private final NumberProperty crystalDistance = new NumberProperty("Crystal Distance", 4.0, 1.0, 10.0, 1.0);
    private final NumberProperty tntDistance = new NumberProperty("TNT Distance", 30.0, 3.0, 50.0, 1.0);

    public AutoTotemModule() {
        super("Auto Totem", "Automatically swaps to totem.", ModuleCategory.COMBAT);
        this.addProperties(elytraHealthCheck, tntCheck, fallCheck, crystalCheck, health, elytraHealth, crystalDistance, tntDistance);
    }

    @Subscribe
    private void onTick(PreGameTickEvent event) {
        if (mc.field_1724 == null || mc.field_1687 == null) return;

        int invSlot = findTotemSlot();
        if (invSlot == -1) return;

        if (!isHoldingTotem() && shouldSwap(invSlot)) {
            swapToTotem(invSlot);
        }
    }

    /**
     * Решение о свопе в тотем.
     * Всегда ставит тотем, если он есть в инвентаре и не держится в руках,
     * а также учитывает опасные ситуации (хп, кристаллы, тнт, падение, элитры).
     */
    private boolean shouldSwap(int invSlot) {
        // Уже держим тотем – смысла свопать нет
        if (isHoldingTotem()) return false;

        double currentHealth = mc.field_1724.method_6032() + mc.field_1724.method_6067();
        if (currentHealth <= (Double) health.getValue()) return true;
        
        if (elytraHealthCheck.getValue()) {
            class_1799 chest = mc.field_1724.method_31548().method_5438(38);
            if (chest.method_7909() == net.minecraft.class_1802.field_8204 &&
                currentHealth <= (Double) elytraHealth.getValue()) {
                return true;
            }
        }

        if (fallCheck.getValue() && mc.field_1724.field_6017 > 2.0) return true;

        if (crystalCheck.getValue()) {
            for (class_1297 entity : mc.field_1687.method_18112()) {
                if (entity instanceof class_1511 && mc.field_1724.method_5739(entity) <= crystalDistance.getValue()) {
                    return true;
                }
            }
        }

        if (tntCheck.getValue()) {
            for (class_1297 entity : mc.field_1687.method_18112()) {
                if ((entity instanceof class_1541 || entity instanceof class_1542) && mc.field_1724.method_5739(entity) <= tntDistance.getValue()) {
                    return true;
                }
            }
        }

        // Если до сюда дошли – явной опасности нет,
        // но тотем в инвентаре есть и в руках его нет → все равно ставим.
        return true;
    }

    private int findTotemSlot() {
        // Ищем тотем в основном инвентаре (0..35)
        for (int i = 0; i < 36; i++) {
            class_1799 stack = mc.field_1724.method_31548().method_5438(i);
            if (stack.method_7909() == class_1802.field_8281) {
                return i;
            }
        }
        return -1;
    }

    private boolean isHoldingTotem() {
        return mc.field_1724.method_6047().method_7909() == class_1802.field_8281 || 
               mc.field_1724.method_6079().method_7909() == class_1802.field_8281;
    }

    /**
     * Кладём тотем в левую руку (offhand).
     * Если тотем в хотбаре — выбираем слот и шлём SWAP_ITEM_WITH_OFFHAND.
     * Иначе — клик по слоту контейнера (swap с offhand).
     */
    private void swapToTotem(int invSlot) {
        if (invSlot < 9) {
            // Тотем в хотбаре: выбираем слот и свапаем с оффхендом (как клавиша F)
            mc.field_1724.field_3944.method_52787(new class_2868(invSlot));
            mc.field_1724.field_3944.method_52787(
                new class_2846(class_2846.class_2847.field_12969, class_2338.field_10980, class_2350.field_11036)
            );
        } else {
            // Тотем в основном инвентаре (9–35): swap через контейнер
            int containerSlot = invSlot;
            int offhandSlot = 40; // PlayerScreenHandler offhand slot
            mc.field_1761.method_2906(
                    mc.field_1724.field_7512.field_7763,
                    containerSlot,
                    offhandSlot,
                    class_1713.field_7794,
                    mc.field_1724
            );
        }
    }
}
