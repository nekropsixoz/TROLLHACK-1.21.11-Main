package ru.noxium.module.impl.combat;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import ru.noxium.event.EventInit;
import ru.noxium.event.impl.EventUpdate;
import ru.noxium.module.api.Category;
import ru.noxium.module.api.IModule;
import ru.noxium.module.api.Module;
import ru.noxium.module.api.setting.Setting;
import ru.noxium.module.api.setting.impl.BooleanSetting;
import ru.noxium.module.api.setting.impl.ModeSetting;
import ru.noxium.module.api.setting.impl.SliderSetting;

@IModule(name = "AntiAim", description = "Скрывает реальные углы поворота", category = Category.Combat, bind = -1)
@Environment(EnvType.CLIENT)
public class AntiAim extends Module {
    
    private final BooleanSetting realBoolean = new BooleanSetting("Менять хитбокс", false);
    private final BooleanSetting randomReal = new BooleanSetting("Анти-Брутфорс", true);
    private final BooleanSetting fakeBoolean = new BooleanSetting("Визуальные АА", true);
    private final ModeSetting fakeModeYaw = new ModeSetting("Менять Yaw", "Jitter", "Jitter", "Static", "Random", "Defense");
    private final SliderSetting yawSlider = new SliderSetting("Угол Yaw", 60.0F, 1.0F, 70.0F, 1.0F, false);
    private final ModeSetting fakeModePitch = new ModeSetting("Менять Pitch", "Defense", "Defense", "Custom");
    private final SliderSetting pitchSlider = new SliderSetting("Угол Pitch", 65.0F, 0.0F, 90.0F, 1.0F, false);
    private final BooleanSetting zeroPitch = new BooleanSetting("Zero pitch on land", false);
    private final BooleanSetting chivoBlyat = new BooleanSetting("Отображать у всех", false);

    public AntiAim() {
        this.addSettings(new Setting[] { realBoolean, randomReal, fakeBoolean, fakeModeYaw, fakeModePitch, zeroPitch, yawSlider, pitchSlider, chivoBlyat });
    }
    
    float yaw = 0;
    float pitch = 0;
    long timeLanded = 0;
    int delayTime = 500;
    boolean can = true;
    
    @EventInit
    public void onUpdate(EventUpdate e) {
        if (HitAura.target == null) {
            if (fakeBoolean.get()) {
                
                if (mc.options.useKey.isPressed() || mc.options.attackKey.isPressed() || mc.currentScreen != null) {
                    can = false;
                    return;
                } else {
                    can = true;
                }
            
                // yaw
                if (fakeModeYaw.is("Jitter")) {
                    if (mc.player.age % 2 == 0) {
                        yaw = mc.player.getYaw() + yawSlider.get() + 180;
                        mc.player.setHeadYaw(yaw);
                        mc.player.bodyYaw = yaw;
                    } else {
                        yaw = mc.player.getYaw() - yawSlider.get() + 180;
                        mc.player.setHeadYaw(yaw);
                        mc.player.bodyYaw = yaw;
                    }
                }
                
                if (fakeModeYaw.is("Static")) {
                    yaw = mc.player.getYaw() + 180;
                    mc.player.setHeadYaw(yaw);
                    mc.player.bodyYaw = yaw;
                }
                
                if (fakeModeYaw.is("Defense")) {
                    yaw = mc.player.getYaw() + 180;
                    mc.player.setHeadYaw(yaw);
                    mc.player.bodyYaw = yaw;
                    if (mc.player.age % (int)randomizeFloat(2, 6) == 0) {
                        yaw = mc.player.getYaw() + (int)randomizeFloat(12, 60) + 200;
                        mc.player.setHeadYaw(yaw);
                        mc.player.bodyYaw = yaw;
                    } else {
                        yaw = mc.player.getYaw() - (int)randomizeFloat(12, 60) + 200;
                        mc.player.setHeadYaw(yaw);
                        mc.player.bodyYaw = yaw;
                    }
                }
                
                if (fakeModeYaw.is("Random")) {
                    int i = (int)randomizeFloat(1, 180);
                    if ((int)randomizeFloat(1, 2) == 1) {
                        yaw = mc.player.getYaw() + 180 + i;
                        mc.player.setHeadYaw(yaw);
                        mc.player.bodyYaw = yaw;
                    } else {
                        yaw = mc.player.getYaw() + 180 - i;
                        mc.player.setHeadYaw(yaw);
                        mc.player.bodyYaw = yaw;
                    }
                }
                
                // pitch
                if (fakeModePitch.is("Custom")) {
                    pitch = pitchSlider.get();
                    mc.player.setPitch(pitch);
                }
                
                if (fakeModePitch.is("Defense")) {
                    pitch = pitchSlider.get();
                    mc.player.setPitch(pitch);
                    if (mc.player.age % (int)randomizeFloat(4, 12) == 0) {
                        pitch = -65;
                        mc.player.setPitch(pitch);
                    }
                }
                
                if (zeroPitch.get()) {
                    if (mc.player.isOnGround()) {
                        if (timeLanded == 0) {
                            timeLanded = System.currentTimeMillis();
                        }
                        if (System.currentTimeMillis() - timeLanded <= delayTime) {
                            pitch = 0;
                            mc.player.setPitch(pitch);
                        }
                    } else {
                        timeLanded = 0;
                    }
                }
                
                if (chivoBlyat.get()) {           
                    if (can) {
                        mc.player.setYaw(yaw);
                        mc.player.setPitch(pitch);
                    }
                }
            }
        }
        
        if (realBoolean.get()) {
            if (mc.player.isSubmergedInWater() || mc.options.useKey.isPressed() || mc.options.attackKey.isPressed() || mc.currentScreen != null) {
                mc.player.setVelocity(mc.player.getVelocity().x, mc.player.getVelocity().y, mc.player.getVelocity().z);
                return;
            }
            
            int i = 4;
            
            if (randomReal.get()) {
                i = (int)randomizeFloat(4, 8);
            }
            
            if (mc.player.age % i == 0) {
                mc.player.setVelocity(mc.player.getVelocity().x, 0.1, mc.player.getVelocity().z);
            } else {
                mc.player.setVelocity(mc.player.getVelocity().x, mc.player.getVelocity().y, mc.player.getVelocity().z);
            }
        }
    }
    
    private float randomizeFloat(float min, float max) {
        return (float) (Math.random() * (max - min) + min);
    }
    
    public void reset() {
        if (mc.player != null) {
            yaw = mc.player.getYaw();
            pitch = mc.player.getPitch();
        }
    }
    
    @Override
    public void onDisable() {
        reset();
        super.onDisable();
    }
}
