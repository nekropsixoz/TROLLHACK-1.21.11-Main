package ru.noxium.module.impl.combat;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.packet.s2c.play.EntityVelocityUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.ExplosionS2CPacket;
import ru.noxium.event.EventInit;
import ru.noxium.event.impl.EventPacket;
import ru.noxium.module.api.Category;
import ru.noxium.module.api.IModule;
import ru.noxium.module.api.Module;
import ru.noxium.module.api.setting.Setting;
import ru.noxium.module.api.setting.impl.ModeSetting;
import ru.noxium.module.api.setting.impl.MultiBooleanSetting;
import ru.noxium.module.api.setting.impl.BooleanSetting;

@IModule(name = "Velocity", description = "Отключает отбрасывание от игрока и т.д", category = Category.Combat, bind = -1)
@Environment(EnvType.CLIENT)
public class Velocity extends Module {
    public static final Velocity INSTANCE = new Velocity();
    
    // mode
    public static ModeSetting mode = new ModeSetting("Моды", "Cancel", "Cancel");
    
    // setting
    public static MultiBooleanSetting SettingVelocity = new MultiBooleanSetting(
        "Настройки", 
        new BooleanSetting("Игнорировать взрывы", true)
    );

    public Velocity() {
        this.addSettings(new Setting[] { mode, SettingVelocity });
    }

    @EventInit
    public void AntiKnockBack(EventPacket e) {
        // mode
        if (mode.is("Cancel")) {
            if (e.getPacket() instanceof EntityVelocityUpdateS2CPacket velPacket) {
                if (mc.player != null && velPacket.getEntityId() == mc.player.getId()) {
                    e.cancel();
                }
            }
        }
        // boolean
        if (SettingVelocity.get("Игнорировать взрывы")) {
            if (e.getPacket() instanceof ExplosionS2CPacket) {
                e.cancel();
            }
        }
    }

    @Override
    public void onDisable() {
        super.onDisable();
    }
}
