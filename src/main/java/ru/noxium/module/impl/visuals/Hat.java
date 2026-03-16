package ru.noxium.module.impl.visuals;

import com.mojang.blaze3d.pipeline.BlendFunction;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.pipeline.RenderPipeline.Snippet;
import com.mojang.blaze3d.platform.DepthTestFunction;
import com.mojang.blaze3d.vertex.VertexFormat.DrawMode;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.RenderSetup;
import net.minecraft.util.math.Vec3d;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.util.Identifier;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.option.Perspective;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.client.util.BufferAllocator;
import net.minecraft.client.render.VertexConsumerProvider.Immediate;
import org.joml.Matrix4f;
import ru.noxium.event.EventInit;
import ru.noxium.event.render.EventRender3D;
import ru.noxium.module.api.Category;
import ru.noxium.module.api.IModule;
import ru.noxium.module.api.Module;
import ru.noxium.module.api.setting.Setting;
import ru.noxium.module.api.setting.impl.BooleanSetting;
import ru.noxium.module.api.setting.impl.ModeSetting;
import ru.noxium.util.color.ColorUtil;

@IModule(name = "Hat", description = " ", category = Category.Visuals, bind = -1)
@Environment(EnvType.CLIENT)
public class Hat extends Module {
   private static final int BUFFER_SIZE = 65536;
   public static ModeSetting mode = new ModeSetting("Mode", "China Hat", "China Hat", "Nimb");
   public static BooleanSetting fdg = new BooleanSetting("Прикреп к бошке", true);
   private static final RenderPipeline HAT_FILL_PIPELINE = RenderPipelines.register(
         RenderPipeline.builder(new Snippet[] { RenderPipelines.POSITION_COLOR_SNIPPET })
               .withLocation(Identifier.of("noxium", "hat_fill"))
               .withVertexFormat(VertexFormats.POSITION_COLOR, DrawMode.TRIANGLES)
               .withCull(false)
               .withDepthTestFunction(DepthTestFunction.LEQUAL_DEPTH_TEST)
               .withDepthWrite(false)
               .withBlend(BlendFunction.LIGHTNING)
               .build());
   private static RenderPipeline HAT_LINE_PIPELINE = RenderPipelines.register(
         RenderPipeline.builder(new Snippet[] { RenderPipelines.POSITION_COLOR_SNIPPET })
               .withLocation(Identifier.of("noxium", "hat_line"))
               .withVertexFormat(VertexFormats.POSITION_COLOR, DrawMode.DEBUG_LINES)
               .withCull(false)
               .withDepthTestFunction(DepthTestFunction.LEQUAL_DEPTH_TEST)
               .withDepthWrite(false)
               .withBlend(BlendFunction.LIGHTNING)
               .build());

   public Hat() {
      this.addSettings(new Setting[] { mode, fdg });
   }

   @EventInit
   public void onRender(EventRender3D event) {
      if (mc.world != null && mc.player != null) {
         if (mc.options.getPerspective() != Perspective.FIRST_PERSON) {
            BufferAllocator allocator = new BufferAllocator(65536);
            Immediate immediate = VertexConsumerProvider.immediate(allocator);
            MatrixStack matrices = event.getMatrixStack();
            float partialTicks = event.getTickDelta();

            try {
               Vec3d cameraPos = mc.gameRenderer.getCamera().getCameraPos();
               double x = mc.player.lastRenderX + (mc.player.getX() - mc.player.lastRenderX) * partialTicks;
               double y = mc.player.lastRenderY + (mc.player.getY() - mc.player.lastRenderY) * partialTicks;
               double z = mc.player.lastRenderZ + (mc.player.getZ() - mc.player.lastRenderZ) * partialTicks;
               double hatY = y + mc.player.getHeight() - (mc.player.isSneaking() ? 0.25F : 0.05F);
               matrices.push();
               matrices.translate(x - cameraPos.x, hatY - cameraPos.y, z - cameraPos.z);
               if (fdg.get()) {
                  float yaw = mc.player.getYaw(partialTicks);
                  float pitch = mc.player.getPitch(partialTicks);
                  matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(-yaw));
                  matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(pitch));
                  float pitchAbs = Math.abs(pitch);
                  float compensationY = pitchAbs / 90.0F * 0.3F;
                  matrices.translate(0.0F, compensationY, 0.05F);
               }

               Matrix4f matrix = matrices.peek().getPositionMatrix();
               if (mode.is("China Hat")) {
                  this.renderChinaHat(immediate, matrix);
               } else if (mode.is("Nimb")) {
                  this.renderNimb(immediate, matrix);
               }

               matrices.pop();
               immediate.draw();
            } finally {
               allocator.close();
            }
         }
      }
   }

   private void renderChinaHat(Immediate immediate, Matrix4f matrix) {
      int segments = 120;
      float radius = 0.55F;
      float height = 0.25F;
      float alpha = 0.7058824F;
      VertexConsumer fillBuffer = immediate.getBuffer(this.getHatFillLayer());

      for (int i = 0; i < segments; i++) {
         float angle1 = (float) Math.toRadians(i * (360.0 / segments));
         float angle2 = (float) Math.toRadians((i + 1) * (360.0 / segments));
         int c1 = ColorUtil.multAlpha(ColorUtil.fade(i), alpha);
         int c2 = ColorUtil.multAlpha(ColorUtil.fade(i + 1), alpha);
         fillBuffer.vertex(matrix, 0.0F, height, 0.0F).color(c1);
         fillBuffer.vertex(matrix, (float) Math.cos(angle1) * radius, 0.0F, (float) Math.sin(angle1) * radius)
               .color(c1);
         fillBuffer.vertex(matrix, (float) Math.cos(angle2) * radius, 0.0F, (float) Math.sin(angle2) * radius)
               .color(c2);
      }

      VertexConsumer lineBuffer = immediate.getBuffer(this.getHatLineLayer());
      int lineCol = ColorUtil.replAlpha(ColorUtil.fade(), 255);

      for (int i = 0; i < segments; i++) {
         float angle1 = (float) Math.toRadians(i * (360.0 / segments));
         float angle2 = (float) Math.toRadians((i + 1) * (360.0 / segments));
         lineBuffer.vertex(matrix, (float) Math.cos(angle1) * radius, 0.0F, (float) Math.sin(angle1) * radius)
               .color(lineCol);
         lineBuffer.vertex(matrix, (float) Math.cos(angle2) * radius, 0.0F, (float) Math.sin(angle2) * radius)
               .color(lineCol);
      }
   }

   private void renderNimb(Immediate immediate, Matrix4f matrix) {
      int segments = 120;
      float radius = 0.4F;
      VertexConsumer lineBuffer = immediate.getBuffer(this.getHatLineLayer());
      int color = ColorUtil.replAlpha(ColorUtil.fade(), 255);

      for (int i = 0; i < segments; i++) {
         float angle1 = (float) Math.toRadians(i * (360.0 / segments));
         float angle2 = (float) Math.toRadians((i + 1) * (360.0 / segments));
         lineBuffer.vertex(matrix, (float) Math.cos(angle1) * radius, 0.1F, (float) Math.sin(angle1) * radius)
               .color(color);
         lineBuffer.vertex(matrix, (float) Math.cos(angle2) * radius, 0.1F, (float) Math.sin(angle2) * radius)
               .color(color);
      }
   }

   private RenderLayer getHatFillLayer() {
      return RenderLayer.of("noxium_hat_fill",
            RenderSetup.builder(HAT_FILL_PIPELINE)
                  .expectedBufferSize(65536)
                  .translucent()
                  .build());
   }

   private RenderLayer getHatLineLayer() {
      return RenderLayer.of("noxium_hat_line",
            RenderSetup.builder(HAT_LINE_PIPELINE)
                  .expectedBufferSize(65536)
                  .translucent()
                  .build());
   }
}
