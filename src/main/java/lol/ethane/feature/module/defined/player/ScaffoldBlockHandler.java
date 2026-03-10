package lol.ethane.feature.module.defined.player;

import net.minecraft.class_1268;
import net.minecraft.class_1747;
import net.minecraft.class_1799;
import net.minecraft.class_2338;
import net.minecraft.class_2350;
import net.minecraft.class_243;
import net.minecraft.class_2868;
import net.minecraft.class_2879;
import net.minecraft.class_310;
import net.minecraft.class_3965;

/**
 * Handles block management and placement for scaffold module.
 * Separated from main module for better code organization.
 */
public class ScaffoldBlockHandler {
    
    private static final class_310 mc = class_310.method_1551();
    
    private int blockCount = -1;
    
    public void updateBlockCount() {
        if (mc.field_1724 == null) {
            blockCount = 0;
            return;
        }
        
        class_1799 stack = mc.field_1724.method_6047();
        if (isBlock(stack)) {
            blockCount = stack.method_7947();
        } else {
            blockCount = 0;
        }
    }
    
    public boolean hasBlocks() {
        return blockCount > 0;
    }
    
    public void findBlockSlot() {
        if (mc.field_1724 == null) return;
        
        for (int i = 0; i < 9; i++) {
            class_1799 stack = mc.field_1724.method_31548().method_5438(i);
            if (isBlock(stack)) {
                mc.field_1724.field_3944.method_52787(new class_2868(i));
                blockCount = stack.method_7947();
                return;
            }
        }
    }
    
    public void placeBlock(BlockData data, boolean shouldSwing) {
        class_243 hitVec = new class_243(data.pos.method_10263() + 0.5, data.pos.method_10264() + 0.5, data.pos.method_10260() + 0.5);
        class_3965 hitResult = new class_3965(hitVec, data.facing, data.pos, false);

        mc.field_1761.method_2896(mc.field_1724, class_1268.field_5808, hitResult);
        
        if (shouldSwing) {
            mc.field_1724.method_6104(class_1268.field_5808);
        } else {
            mc.field_1724.field_3944.method_52787(new class_2879(class_1268.field_5808));
        }
    }
    
    public BlockData getBlockData(int startY, ScaffoldModule.KeepYMode keepYMode) {
        if (mc.field_1724 == null || mc.field_1687 == null) {
            return null;
        }
        
        class_2338 playerPos = mc.field_1724.method_24515().method_10069(0, -1, 0);

        if (keepYMode != ScaffoldModule.KeepYMode.NONE) {
            int targetY = startY - 1;
            playerPos = new class_2338(playerPos.method_10263(), targetY, playerPos.method_10260());
        }

        if (mc.field_1687.method_22347(playerPos)) {
            for (class_2350 facing : class_2350.values()) {
                class_2338 neighbor = playerPos.method_10093(facing);
                if (!mc.field_1687.method_22347(neighbor)) {
                    return new BlockData(neighbor, facing.method_10153());
                }
            }
        }
        return null;
    }
    
    private boolean isBlock(class_1799 stack) {
        return !stack.method_7960() && stack.method_7909() instanceof class_1747;
    }
    
    public void reset() {
        blockCount = -1;
    }
    
    /**
     * Data class for block placement information.
     */
    public static class BlockData {
        public final class_2338 pos;
        public final class_2350 facing;

        public BlockData(class_2338 pos, class_2350 facing) {
            this.pos = pos;
            this.facing = facing;
        }
    }
}
