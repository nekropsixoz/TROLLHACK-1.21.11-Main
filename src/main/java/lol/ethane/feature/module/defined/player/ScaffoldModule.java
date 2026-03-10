package lol.ethane.feature.module.defined.player;

import lol.ethane.event.defined.player.PlayerMovementTickEvent;
import lol.ethane.event.defined.player.PlayerSafeWalkEvent;
import lol.ethane.event.defined.press.MoveInputEvent;
import lol.ethane.event.subscriber.Subscribe;
import lol.ethane.feature.helper.impl.player.rotation.RotationHelper;
import lol.ethane.feature.module.Module;
import lol.ethane.feature.module.ModuleCategory;
import lol.ethane.feature.module.property.impl.BooleanProperty;
import lol.ethane.feature.module.property.impl.NumberProperty;
import lol.ethane.feature.module.property.impl.mode.ModeProperty;
import net.minecraft.class_241;
import net.minecraft.class_310;

/**
 * Automatically places blocks under you.
 * Refactored for better code organization and maintainability.
 */
public class ScaffoldModule extends Module {
    private static final class_310 mc = class_310.method_1551();
    
    // Helper classes for separated concerns
    private final ScaffoldRotationHandler rotationHandler = new ScaffoldRotationHandler();
    private final ScaffoldTowerHandler towerHandler = new ScaffoldTowerHandler();
    private final ScaffoldBlockHandler blockHandler = new ScaffoldBlockHandler();
    
    private int startY = 256;

    // Module properties
    public final ModeProperty<RotationMode> rotationMode = new ModeProperty<>("Rotations", RotationMode.BACKWARDS);
    public final ModeProperty<SprintMode> sprintMode = new ModeProperty<>("Sprint", SprintMode.NONE);
    public final NumberProperty groundMotion = new NumberProperty("Ground Motion", 100, 0, 100, 1);
    public final NumberProperty airMotion = new NumberProperty("Air Motion", 100, 0, 100, 1);
    public final ModeProperty<TowerMode> tower = new ModeProperty<>("Tower", TowerMode.NONE);
    public final ModeProperty<KeepYMode> keepY = new ModeProperty<>("Keep Y", KeepYMode.NONE);
    public final BooleanProperty safeWalk = new BooleanProperty("Safe Walk", true);
    public final BooleanProperty swing = new BooleanProperty("Swing", true);

    public ScaffoldModule() {
        super("Scaffold", "Automatically places blocks under you.", ModuleCategory.PLAYER);
        this.addProperties(rotationMode, sprintMode, groundMotion, airMotion, tower, keepY, safeWalk, swing);
    }

    @Override
    protected void onEnable() {
        if (mc.field_1724 != null) {
            startY = (int) mc.field_1724.method_23318();
        }

        // Reset helper states
        rotationHandler.reset();
        towerHandler.reset();
        blockHandler.reset();

        super.onEnable();
    }

    @Subscribe
    private void onMovementTick(PlayerMovementTickEvent event) {
        if (event.getState() != PlayerMovementTickEvent.State.PRE) return;
        if (mc.field_1724 == null || mc.field_1687 == null) return;

        // Update block management
        blockHandler.updateBlockCount();
        if (!blockHandler.hasBlocks()) {
            blockHandler.findBlockSlot();
        }

        // Get block data and place if available
        ScaffoldBlockHandler.BlockData blockData = blockHandler.getBlockData(startY, keepY.getValue());
        if (blockData != null) {
            rotationHandler.updateRotations(rotationMode.getValue());
            RotationHelper.getClientHandler().setRotation(new class_241(
                rotationHandler.getCurrentYaw(), 
                rotationHandler.getCurrentPitch()
            ));
            
            blockHandler.placeBlock(blockData, swing.getValue());
        }

        // Update tower logic
        towerHandler.updateTellyLogic(tower.getValue(), blockHandler.hasBlocks());
        towerHandler.handleTower(tower.getValue());
    }

    @Subscribe
    private void onSafeWalk(PlayerSafeWalkEvent event) {
        if (safeWalk.getValue()) {
            event.setSafeWalk(true);
        }
    }

    @Subscribe
    private void onMoveInput(MoveInputEvent event) {
        // Handle sprint mode
        if (sprintMode.is(SprintMode.NONE)) {
            event.setSprint(false);
        }

        // Handle telly bridge auto-jumps
        if (towerHandler.shouldTellyJump(tower.getValue(), blockHandler.hasBlocks())) {
            event.setJump(true);
        }
    }

    // Enums for module modes
    public enum RotationMode {
        NONE, DEFAULT, BACKWARDS, SIDEWAYS, GODBRIDGE, SMOOTH
    }

    public enum SprintMode {
        NONE, VANILLA
    }

    public enum TowerMode {
        NONE, VANILLA, EXTRA, TELLY
    }

    public enum KeepYMode {
        NONE, VANILLA, EXTRA, TELLY
    }
}
