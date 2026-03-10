package lol.ethane.feature.module.defined.player;

import lol.ethane.utils.math.MovementUtil;
import net.minecraft.class_310;

/**
 * Handles tower logic for scaffold module.
 * Separated from main module for better code organization.
 */
public class ScaffoldTowerHandler {
    
    private static final class_310 mc = class_310.method_1551();
    
    // Telly bridge counters
    private int tellyTicksUntilJump = 0;
    private int tellyJumpTicks = 0;
    
    public void handleTower(ScaffoldModule.TowerMode towerMode) {
        if (mc.field_1690.field_1903.method_1434() && !MovementUtil.isMoving()) {
            switch (towerMode) {
                case VANILLA:
                    mc.field_1724.method_18800(0, 0.42, 0);
                    break;
                case EXTRA:
                    mc.field_1724.method_18800(0, 0.6, 0);
                    break;
                case TELLY:
                    mc.field_1724.method_18800(0, 0.42, 0);
                    break;
                case NONE:
                default:
                    break;
            }
        }
    }
    
    public void updateTellyLogic(ScaffoldModule.TowerMode towerMode, boolean hasBlocks) {
        if (towerMode == ScaffoldModule.TowerMode.TELLY) {
            if (mc.field_1724.method_24828()) {
                tellyTicksUntilJump++;
            } else {
                tellyTicksUntilJump = 0;
                tellyJumpTicks = 2 + (int) (Math.random() * 3);
            }
        } else {
            tellyTicksUntilJump = 0;
        }
    }
    
    public boolean shouldTellyJump(ScaffoldModule.TowerMode towerMode, boolean hasBlocks) {
        return towerMode == ScaffoldModule.TowerMode.TELLY
                && mc.field_1724 != null
                && mc.field_1724.method_24828()
                && hasBlocks
                && MovementUtil.isMoving()
                && tellyTicksUntilJump >= tellyJumpTicks;
    }
    
    public void reset() {
        tellyTicksUntilJump = 0;
        tellyJumpTicks = 2 + (int) (Math.random() * 3);
    }
}
