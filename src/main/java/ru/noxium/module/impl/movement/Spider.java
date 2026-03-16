package ru.noxium.module.impl.movement;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket;
import net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket.Mode;
import net.minecraft.network.packet.c2s.play.UpdateSelectedSlotC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerInteractItemC2SPacket;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.hit.BlockHitResult;

import ru.noxium.event.EventInit;
import ru.noxium.event.impl.EventUpdate;
import ru.noxium.event.player.EventMotion;
import ru.noxium.module.api.Category;
import ru.noxium.module.api.IModule;
import ru.noxium.module.api.Module;
import ru.noxium.module.api.setting.Setting;
import ru.noxium.module.api.setting.impl.ModeSetting;
import ru.noxium.module.api.setting.impl.SliderSetting;
import ru.noxium.module.impl.combat.auraProcess.rotationProcess.impl.FreeLookUtil;
import ru.noxium.module.impl.combat.auraProcess.rotationProcess.impl.Rotation;
import ru.noxium.module.impl.combat.auraProcess.rotationProcess.impl.RotationProcess;
import ru.noxium.util.other.TimerUtil;

import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.util.stream.IntStream;

@IModule(name = "Spider", category = Category.Movement, description = "Позволяет лазить по стенам, используя блоки или ведро с лавой", bind = -1)
@Environment(EnvType.CLIENT)
public class Spider extends Module {

    private final ModeSetting mode = new ModeSetting("Режим", "Blocks", "Blocks", "Lava", "Matrix", "SpookyTime");
    private final ModeSetting modeRotate = new ModeSetting("Ротация", "Packet", "Packet", "Client")
            .hidden(() -> !mode.is("Blocks"));
    private final SliderSetting spiderSpeed = new SliderSetting("Speed", 2.0f, 1.0f, 10.0f, 0.05f, false)
            .hidden(() -> !mode.is("Matrix"));

    private int prevSlot = -1;
    private final TimerUtil stopWatch = new TimerUtil();
    private Timer timer = new Timer();
    private boolean canUse = true;
    private long lastBucketUse = 0L;
    private final Random random = new Random();

    private double startY = -1;
    private int blocksPassed = 0;
    private boolean motionPaused = false;
    private long pauseEndTime = 0;
    private int bucketPlaceCount = 0;

    public Spider() {
        this.addSettings(new Setting[] { mode, modeRotate, spiderSpeed });
    }

    @Override
    public void onEnable() {
        super.onEnable();
        if (this.mode.is("SpookyTime")) {
            mc.options.sneakKey.setPressed(false);
        }
    }

    @Override
    public void onDisable() {
        super.onDisable();
        if (this.mode.is("SpookyTime")) {
            this.timer.cancel();
            this.timer = new Timer();
            this.canUse = true;
            this.bucketPlaceCount = 0;
            this.startY = -1;
            this.blocksPassed = 0;
            this.motionPaused = false;
            this.pauseEndTime = 0;
            if (mc.options != null) {
                mc.options.sneakKey.setPressed(false);
                mc.options.jumpKey.setPressed(false);
            }
        }
    }

    private int locateLavaBucket() {
        for (int i = 0; i < 9; i++) {
            ItemStack stack = mc.player.getInventory().getStack(i);
            if (!stack.isEmpty() && stack.getItem() == Items.LAVA_BUCKET)
                return i;
        }
        return -1;
    }

    private void performWallBoost(int bucketSlot) {
        mc.options.jumpKey.setPressed(true);
        mc.options.sneakKey.setPressed(true);

        Rotation aimed = new Rotation(mc.player.getYaw(), 0);
        RotationProcess.update(aimed, 180.0F, 180.0F, 1, 5);

        if (System.currentTimeMillis() - lastBucketUse < getCooldownByHeight())
            return;

        swapAndUseBucket(bucketSlot);

        lastBucketUse = System.currentTimeMillis();
    }

    private void swapAndUseBucket(int slot) {
        int previousSlot = mc.player.getInventory().getSelectedSlot();

        if (slot != previousSlot)
            mc.player.networkHandler.sendPacket(new UpdateSelectedSlotC2SPacket(slot));

        mc.player.networkHandler.sendPacket(
                new PlayerInteractItemC2SPacket(Hand.MAIN_HAND, 0, mc.player.getYaw(), mc.player.getPitch()));

        Vec3d vel = mc.player.getVelocity();
        mc.player.setVelocity(vel.x, 0.42D + (random.nextDouble() * 0.03D), vel.z);

        if (slot != previousSlot)
            mc.player.networkHandler.sendPacket(new UpdateSelectedSlotC2SPacket(previousSlot));
    }

    private long getCooldownByHeight() {
        double distance = computeHeightGap();
        return distance <= 100.0 ? 300L : 15L + random.nextInt(2);
    }

    private double computeHeightGap() {
        if (mc.player == null || mc.world == null)
            return 0.0;

        double currentY = mc.player.getY();
        for (double y = currentY; y > 0.0; y -= 0.1) {
            BlockPos pos = BlockPos.ofFloored(mc.player.getX(), y, mc.player.getZ());
            if (!mc.world.getBlockState(pos).isAir()) {
                return Math.max(currentY - (y + 1), 0.0);
            }
        }
        return 0.0;
    }

    @EventInit
    public void onMotion(EventMotion e) {
        if (mode.is("Lava")) {
            if (mc.player.isInLava()) {
                Vec3d vel = mc.player.getVelocity();
                mc.player.setVelocity(vel.x, 0.30, vel.z);
            }
            handleLavaBucketMode(e);
        }

        if (mode.is("Matrix")) {
            if (!mc.player.horizontalCollision) {
                return;
            }
            long speed = (long) MathHelper.clamp(500 - (spiderSpeed.get() / 2 * 100), 0, 500);
            if (stopWatch.isReached(speed)) {
                e.setOnGround(true);
                mc.player.setOnGround(true);
                mc.player.jump();
                stopWatch.reset();
            }
        }

        if (mode.is("SpookyTime")) {
            handleSpookytimeBETA(e);
        }
    }

    private void handleLavaBucketMode(EventMotion e) {
        if (mc.player == null || mc.player.isInLava()) {
            return;
        }
        Rotation aimed = new Rotation(mc.player.getYaw(), 76);
        RotationProcess.update(aimed, 180.0F, 180.0F, 1, 1488);

        int lavaSlot = locateLavaBucket();
        if (lavaSlot == -1) {
            return;
        }

        if (canUse) {
            int currentSlot = mc.player.getInventory().getSelectedSlot();

            if (lavaSlot != currentSlot) {
                mc.player.networkHandler.sendPacket(new UpdateSelectedSlotC2SPacket(lavaSlot));
            }

            mc.player.networkHandler.sendPacket(
                    new PlayerInteractItemC2SPacket(Hand.MAIN_HAND, 0, mc.player.getYaw(), mc.player.getPitch()));

            if (lavaSlot != currentSlot) {
                mc.player.networkHandler.sendPacket(new UpdateSelectedSlotC2SPacket(currentSlot));
            }

            double yBoost = 0.25D + (random.nextDouble() * 0.07D);
            Vec3d vel = mc.player.getVelocity();
            mc.player.setVelocity(vel.x, yBoost, vel.z);

            canUse = false;
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    canUse = true;
                }
            }, getAppliedDelayMs());
        }

        mc.options.sneakKey.setPressed(true);
    }

    private int getAppliedDelayMs() {
        float base = 0.25F;
        int ms = (int) (base * 1000.0F);
        int offset = random.nextInt(120) - 30;
        return Math.max(150, ms + offset);
    }

    private void handleSpookytimeBETA(EventMotion motion) {
        if (motionPaused) {
            if (System.currentTimeMillis() >= pauseEndTime) {
                motionPaused = false;
                startY = mc.player.getY();
                blocksPassed = 0;
            } else {
                mc.player.setVelocity(mc.player.getVelocity().x, -0.01, mc.player.getVelocity().z);
                return;
            }
        }

        if (startY == -1) {
            startY = mc.player.getY();
        }

        double deltaY = Math.abs(mc.player.getY() - startY);
        if (deltaY >= blocksPassed + 1) {
            blocksPassed++;
        }

        // Reduced pause frequency to avoid suspicious stops, but kept for long climbs
        if (blocksPassed > 0 && blocksPassed % 4 == 0 && !motionPaused) {
            motionPaused = true;
            pauseEndTime = System.currentTimeMillis() + 150L + random.nextInt(100);
            return;
        }

        if (mc.player.isInLava()) {
            // Slower, more natural swim speed
            Vec3d vel = mc.player.getVelocity();
            mc.player.setVelocity(vel.x, 0.18D + (random.nextDouble() * 0.04D), vel.z);
            return;
        }

        int lavaSlot = locateLavaBucket();
        if (lavaSlot == -1)
            return;

        if (mc.player.horizontalCollision) {
            // First jump: Just jump normally, don't place lava yet
            if (mc.player.isOnGround()) {
                mc.player.jump();
                // Ensure sneak is off
                if (mc.options.sneakKey.isPressed()) {
                    mc.options.sneakKey.setPressed(false);
                }
                return;
            }

            // In air: Only place lava and boost if we start falling
            if (mc.player.getVelocity().y < -0.01) {
                if (this.canUse) {
                    int clientSlot = mc.player.getInventory().getSelectedSlot();

                    // Slower rotation to look less automated
                    RotationProcess.update(new Rotation(FreeLookUtil.freeYaw, 80.0F), 180, 180, 1, 10);

                    if (lavaSlot != clientSlot) {
                        mc.player.networkHandler.sendPacket(new UpdateSelectedSlotC2SPacket(lavaSlot));
                    }

                    // Added distinct sequence for interaction
                    mc.player.networkHandler.sendPacket(
                            new PlayerInteractItemC2SPacket(Hand.MAIN_HAND, 0, mc.player.getYaw(),
                                    mc.player.getPitch()));

                    if (lavaSlot != clientSlot) {
                        mc.player.networkHandler.sendPacket(new UpdateSelectedSlotC2SPacket(clientSlot));
                    }

                    // Boost upwards
                    double yBoost = 0.42D + (random.nextDouble() * 0.02D);
                    Vec3d vel = mc.player.getVelocity();
                    mc.player.setVelocity(vel.x, yBoost, vel.z);

                    this.canUse = false;
                    this.bucketPlaceCount++;

                    // Slightly longer delay to allow the full arc of the jump
                    this.timer.schedule(new TimerTask() {
                        @Override
                        public void run() {
                            Spider.this.canUse = true;
                        }
                    }, getAppliedDelayMsForBETA());
                }
            }

            // Remove sneaking to allow the player to fall and trigger the catch mechanism
            if (mc.options.sneakKey.isPressed()) {
                mc.options.sneakKey.setPressed(false);
            }
        } else {
            // Not colliding (e.g., crested the wall or in air) in SpookyTime
            if (mc.options.sneakKey.isPressed()) {
                mc.options.sneakKey.setPressed(false);
            }

            this.bucketPlaceCount = 0;
            this.blocksPassed = 0;
            this.startY = -1;
        }
    }

    private long getAppliedDelayMsForBETA() {
        // Dynamic delay based on recent usage to simulate cooldown
        long base = 250L;
        if (bucketPlaceCount > 2)
            base += 50L;
        if (bucketPlaceCount > 4)
            base += 100L;
        return base + random.nextInt(80);
    }

    @EventInit
    public void onUpdate(EventUpdate e) {
        if (mode.is("Blocks")) {
            int slotId = findBlockSlotId();
            if (slotId != -1) {
                Hand hand = (mc.player.getOffHandStack().getItem() instanceof BlockItem bi
                        && bi.getBlock().getDefaultState().isFullCube(mc.world, mc.player.getBlockPos()))
                                ? Hand.OFF_HAND
                                : Hand.MAIN_HAND;
                ItemStack itemStack = hand.equals(Hand.OFF_HAND) ? mc.player.getOffHandStack()
                        : mc.player.getInventory().getStack(slotId);
                BlockPos pos = findPos(-1);
                if (canPlace(itemStack) && !pos.equals(BlockPos.ORIGIN)) {
                    Vec3d vec = Vec3d.ofCenter(pos);
                    double diffX = vec.x - mc.player.getX();
                    double diffY = vec.y - (mc.player.getY() + mc.player.getStandingEyeHeight());
                    double diffZ = vec.z - mc.player.getZ();
                    double diffXZ = Math.sqrt(diffX * diffX + diffZ * diffZ);
                    float yaw = (float) Math.toDegrees(Math.atan2(diffZ, diffX)) - 90F;
                    float pitch = (float) -Math.toDegrees(Math.atan2(diffY, diffXZ));

                    if (hand.equals(Hand.MAIN_HAND)) {
                        mc.player.networkHandler.sendPacket(new UpdateSelectedSlotC2SPacket(slotId));
                        prevSlot = mc.player.getInventory().getSelectedSlot();
                        mc.player.getInventory().setSelectedSlot(slotId);
                    }

                    if (!modeRotate.is("Packet")) {
                        RotationProcess.update(new Rotation(yaw, pitch), 360, 360, 0, 5);
                    }

                    if (!mc.player.isSneaking()) {
                        mc.player.networkHandler.sendPacket(
                                new ClientCommandC2SPacket(mc.player, Mode.values()[0])); // PRESS_SHIFT_KEY /
                                                                                          // START_SNEAKING
                    }

                    mc.interactionManager.interactBlock(mc.player, hand,
                            new BlockHitResult(vec, Direction.UP, pos, false));

                    if (!mc.player.isSneaking()) {
                        mc.player.networkHandler.sendPacket(
                                new ClientCommandC2SPacket(mc.player, Mode.values()[1])); // RELEASE_SHIFT_KEY /
                                                                                          // STOP_SNEAKING
                    }

                    if (hand.equals(Hand.MAIN_HAND)) {
                        mc.player.networkHandler.sendPacket(new UpdateSelectedSlotC2SPacket(prevSlot));
                        mc.player.getInventory().setSelectedSlot(prevSlot);
                    }
                }
            }
        }
    }

    private int findBlockSlotId() {
        return IntStream.range(0, 9).filter(i -> mc.player.getInventory().getStack(i).getItem() instanceof BlockItem)
                .findFirst().orElse(-1);
    }

    private boolean canPlace(ItemStack stack) {
        return mc.player.getVelocity().y < 0 && mc.world.getBlockState(mc.player.getBlockPos().add(0, -1, 0)).isAir();
    }

    private BlockPos findPos(int yOffset) {
        BlockPos blockPos = mc.player.getBlockPos().add(0, yOffset, 0);
        List<BlockPos> list = List.of(blockPos.west(), blockPos.east(), blockPos.south(), blockPos.north());
        return list.stream().filter(pos -> !mc.world.getBlockState(pos).isAir()).findFirst().orElse(BlockPos.ORIGIN);
    }
}
