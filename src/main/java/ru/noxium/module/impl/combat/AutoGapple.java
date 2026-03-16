package ru.noxium.module.impl.combat;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import ru.noxium.event.EventInit;
import ru.noxium.event.impl.EventUpdate;
import ru.noxium.module.api.Category;
import ru.noxium.module.api.IModule;
import ru.noxium.module.api.Module;
import ru.noxium.module.api.setting.Setting;
import ru.noxium.module.api.setting.impl.SliderSetting;

@IModule(name = "AutoGapple", description = "Авто поедание золотого яблока", category = Category.Combat, bind = -1)
@Environment(EnvType.CLIENT)
public class AutoGapple extends Module {
    public static final AutoGapple INSTANCE = new AutoGapple();

    private final SliderSetting HP = new SliderSetting("HEART", 3.0F, 1.0F, 36.0F, 0.5F, false);

    public AutoGapple() {
        this.addSettings(new Setting[] { HP });
    }

    @EventInit
    public void onUpdate(EventUpdate e) {
        if (mc.player == null || mc.world == null) return;

        if (mc.player.getHealth() <= HP.get()) {
            ItemStack offhandItem = mc.player.getOffHandStack();
            if (offhandItem.getItem() == Items.GOLDEN_APPLE || offhandItem.getItem() == Items.ENCHANTED_GOLDEN_APPLE) {
                mc.options.useKey.setPressed(true);
            }
        }
        else if (mc.player.getHealth() >= HP.get()) {
            mc.options.useKey.setPressed(false);
        }
    }

    @Override
    public void onDisable(){
        super.onDisable();
        mc.options.useKey.setPressed(false);
    }
}
