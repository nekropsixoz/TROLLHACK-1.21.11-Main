package ru.noxium.module.impl.render;

import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import ru.noxium.event.EventInit;
import ru.noxium.event.impl.EventUpdate;
import ru.noxium.event.player.EventMotion;
import ru.noxium.module.api.Category;
import ru.noxium.module.api.IModule;
import ru.noxium.module.api.Module;
import ru.noxium.module.api.setting.impl.SliderSetting;
import ru.noxium.util.color.ColorUtil;

import java.util.*;

@IModule(
   name = "JumpEffect",
   description = "Creates a wave effect when jumping",
   category = Category.Visuals,
   bind = -1
)
public class JumpEffect extends Module {

   private final List<WaveEffect> waveEffects = Collections.synchronizedList(new ArrayList<>());

   private final SliderSetting radius = new SliderSetting("Radius", 4.0f, 2.0f, 8.0f, 0.1f, false);
   private final SliderSetting speed = new SliderSetting("Speed", 800.0f, 300.0f, 2000.0f, 50.0f, false);

   public JumpEffect() {
      addSettings(radius, speed);
   }

   @EventInit
   public void onJump(EventMotion event) {
      if (!this.enable) return;
      if (mc.player == null) return;

      // Check if player is jumping
      if (mc.player.getVelocity().y > 0) {
         BlockPos pos = mc.player.getBlockPos().down();
         waveEffects.add(new WaveEffect(pos, System.currentTimeMillis()));
      }
   }

   @EventInit
   public void onWorldRender(EventUpdate event) {
      if (!this.enable) return;
      if (waveEffects.isEmpty() || mc.world == null) return;

      Iterator<WaveEffect> iterator = waveEffects.iterator();
      while (iterator.hasNext()) {
         WaveEffect wave = iterator.next();
         if (wave.isExpired()) {
            iterator.remove();
            continue;
         }
         wave.render();
      }
   }

   private class WaveEffect {
      private final BlockPos centerPos;
      private final long startTime;
      private final long duration;
      private final int maxRadius;

      public WaveEffect(BlockPos centerPos, long startTime) {
         this.centerPos = centerPos;
         this.startTime = startTime;
         this.duration = (long) speed.get();
         this.maxRadius = (int) Math.ceil(radius.get());
      }

      public boolean isExpired() {
         return System.currentTimeMillis() - startTime > duration;
      }

      public void render() {
         if (mc.world == null) return;

         long elapsed = System.currentTimeMillis() - startTime;
         float progress = (float) elapsed / duration;
         
         float currentRadius = easeOutCubic(progress) * maxRadius;
         
         float fadeInDuration = 0.15f;
         float fadeOutStart = 0.75f;
         float globalAlpha;

         if (progress < fadeInDuration) {
            globalAlpha = progress / fadeInDuration;
         } else if (progress >= fadeOutStart) {
            float fadeOutProgress = (progress - fadeOutStart) / (1f - fadeOutStart);
            globalAlpha = 1f - easeInCubic(fadeOutProgress);
         } else {
            globalAlpha = 1f;
         }

         int rendered = 0;
         int maxPerFrame = 400;

         for (int x = -maxRadius; x <= maxRadius; x++) {
            for (int z = -maxRadius; z <= maxRadius; z++) {
               if (rendered >= maxPerFrame) break;

               BlockPos blockPos = centerPos.add(x, 0, z);
               
               double distanceFromCenter = Math.sqrt(x * x + z * z);
               
               if (distanceFromCenter > currentRadius + 0.5f) continue;
               if (distanceFromCenter < currentRadius - 2.5f) continue;

               BlockState state = mc.world.getBlockState(blockPos);
               if (state.isAir()) continue;

               VoxelShape shape = state.getOutlineShape(mc.world, blockPos);
               if (shape.isEmpty()) continue;

               rendered++;

               float waveProgress = (float) (distanceFromCenter / maxRadius);
               
               float localAlpha = 1.0f - Math.abs((float)distanceFromCenter - currentRadius) / 2.5f;
               localAlpha = Math.max(0, Math.min(1, localAlpha));
               
               float pulseOffset = waveProgress * 2f;
               float pulse = (float) Math.sin((progress * Math.PI * 4) - pulseOffset);
               pulse = (pulse + 1f) / 2f;
               localAlpha *= (0.5f + pulse * 0.5f);
               
               localAlpha *= globalAlpha;

               if (localAlpha > 0.02f) {
                  int color1 = ColorUtil.getColor(100, 200, 255, 255);
                  int color2 = ColorUtil.getColor(255, 100, 200, 255);
                  int gradientColor = getGradientColor(color1, color2, waveProgress);
                  int finalColor = (gradientColor & 0x00FFFFFF) | ((int) (localAlpha * 180) << 24);

                  try {
                     // Use a simple rendering approach since Render3D might not be available
                     // This would need to be adapted based on the client's rendering system
                     // For now, we'll leave this as a placeholder
                  } catch (Exception ignored) {
                  }
               }
            }
         }
      }

      private int getGradientColor(int c1, int c2, float t) {
         float smoothT = (float) (Math.sin((t - 0.5f) * Math.PI) * 0.5f + 0.5f);
         int r1 = (c1 >> 16) & 0xFF;
         int g1 = (c1 >> 8) & 0xFF;
         int b1 = c1 & 0xFF;
         
         int r2 = (c2 >> 16) & 0xFF;
         int g2 = (c2 >> 8) & 0xFF;
         int b2 = c2 & 0xFF;
         
         int r = (int) (r1 + (r2 - r1) * smoothT);
         int g = (int) (g1 + (g2 - g1) * smoothT);
         int b = (int) (b1 + (b2 - b1) * smoothT);
         
         return (r << 16) | (g << 8) | b;
      }

      private float easeOutCubic(float x) {
         return 1f - (float) Math.pow(1f - x, 3);
      }

      private float easeInCubic(float x) {
         return x * x * x;
      }
   }
}
