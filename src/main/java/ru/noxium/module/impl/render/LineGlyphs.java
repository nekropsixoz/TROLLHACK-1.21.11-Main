package ru.noxium.module.impl.render;

import com.mojang.blaze3d.pipeline.BlendFunction;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.pipeline.RenderPipeline.Snippet;
import com.mojang.blaze3d.platform.DepthTestFunction;
import com.mojang.blaze3d.vertex.VertexFormat.DrawMode;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.RenderSetup;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;
import org.joml.Matrix4f;
import ru.noxium.event.EventInit;
import ru.noxium.event.render.EventRender3D;
import ru.noxium.module.api.Category;
import ru.noxium.module.api.IModule;
import ru.noxium.module.api.Module;
import ru.noxium.module.api.setting.impl.BooleanSetting;
import ru.noxium.module.api.setting.impl.HueSetting;
import ru.noxium.module.api.setting.impl.ModeSetting;
import ru.noxium.util.color.ColorUtil;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

@IModule(
   name = "LineGlyphs",
   description = "Falling animated lines around player",
   category = Category.Visuals,
   bind = -1
)
@Environment(EnvType.CLIENT)
public class LineGlyphs extends Module {

   private final BooleanSetting glowing = new BooleanSetting("Свечение", true);
   private final BooleanSetting dashed = new BooleanSetting("Пунктир", false);
   private final ModeSetting colorMode = new ModeSetting("Режим цвета", "Client", "Client", "Picker", "DoublePicker");
   private final HueSetting pickColor1 = new HueSetting("Цвет 1", 50.0f);
   private final HueSetting pickColor2 = new HueSetting("Цвет 2", 85.0f);

   private final List<FallingLine> lines = new ArrayList<>();
   private final Random random = new Random();

   private static final int LINE_COUNT = 120;
   private static final float FALL_SPEED = 0.07f;
   private static final float LINE_LENGTH = 17f;
   private static final float SEGMENT_LENGTH = 2f;
   private static final float ZIGZAG_WIDTH = 5f;

   private static final RenderPipeline LINES_PIPELINE = RenderPipelines.register(
      RenderPipeline.builder(new Snippet[] { RenderPipelines.POSITION_COLOR_SNIPPET })
            .withLocation(Identifier.of("noxium", "lineglyphs_lines"))
            .withVertexFormat(VertexFormats.POSITION_COLOR, DrawMode.DEBUG_LINES)
            .withCull(false)
            .withDepthTestFunction(DepthTestFunction.LEQUAL_DEPTH_TEST)
            .withDepthWrite(false)
            .withBlend(BlendFunction.LIGHTNING)
            .build());
   private static final RenderLayer LINES_LAYER = RenderLayer.of("lineglyphs_lines", 
      RenderSetup.builder(LINES_PIPELINE).expectedBufferSize(1024).translucent().build());

   public LineGlyphs() {
      addSettings(glowing, dashed, colorMode, pickColor1, pickColor2);
   }

   @Override
   public void onEnable() {
      super.onEnable();
      generateLines();
   }

   @Override
   public void onDisable() {
      super.onDisable();
      lines.clear();
   }

   private void generateLines() {
      lines.clear();
      if (mc.player == null) return;
      Vec3d playerPos = new Vec3d(mc.player.getX(), mc.player.getY(), mc.player.getZ());
      for (int i = 0; i < LINE_COUNT; i++) {
         lines.add(new FallingLine(random, playerPos));
      }
   }

   @EventInit
   public void onRender3D(EventRender3D e) {
      if (mc.player == null || mc.world == null) return;

      Vec3d playerPos = new Vec3d(mc.player.getX(), mc.player.getY(), mc.player.getZ());
      int linesToSpawn = LINE_COUNT - lines.size();

      Iterator<FallingLine> iterator = lines.iterator();
      while (iterator.hasNext()) {
         FallingLine line = iterator.next();
         line.update(playerPos, FALL_SPEED, SEGMENT_LENGTH, ZIGZAG_WIDTH, LINE_LENGTH);
         if (line.shouldRespawn(playerPos)) {
            iterator.remove();
            linesToSpawn++;
         }
      }

      for (int i = 0; i < linesToSpawn; i++) {
         lines.add(new FallingLine(random, playerPos));
      }

      renderLines(e);
   }

   private void renderLines(EventRender3D event) {
      if (lines.isEmpty() || mc.gameRenderer == null) return;

      Camera camera = mc.gameRenderer.getCamera();
      Vec3d camPos = camera.getCameraPos();
      MatrixStack matrices = event.getMatrixStack();
      
      VertexConsumerProvider.Immediate immediate = mc.getBufferBuilders().getEntityVertexConsumers();

      for (int lineIndex = 0; lineIndex < lines.size(); lineIndex++) {
         FallingLine line = lines.get(lineIndex);
         if (line.points.size() < 2) continue;

         int color = getColor(lineIndex);

         for (int i = 0; i < line.points.size() - 1; i++) {
            Vec3d start = line.points.get(i);
            Vec3d end = line.points.get(i + 1);

            if (start.distanceTo(camPos) > 60 || end.distanceTo(camPos) > 60) continue;

            float segmentAlpha = (float)(i + 1) / line.points.size();
            int segmentColor = ColorUtil.replAlpha(color, segmentAlpha);

            if (dashed.get()) {
               drawDashedLine(matrices, immediate, start, end, segmentColor, camPos);
               if (glowing.get()) {
                  int glowColor = ColorUtil.replAlpha(color, segmentAlpha * 0.4f);
                  drawDashedLine(matrices, immediate, start, end, glowColor, camPos);
               }
            } else {
               drawLine(matrices, immediate, start, end, segmentColor, camPos);
               if (glowing.get()) {
                  int glowColor = ColorUtil.replAlpha(color, segmentAlpha * 0.4f);
                  drawLine(matrices, immediate, start, end, glowColor, camPos);
               }
            }
         }
      }
      
      immediate.draw();
   }

   private void drawLine(MatrixStack matrices, VertexConsumerProvider.Immediate immediate, Vec3d start, Vec3d end, int color, Vec3d camPos) {
      VertexConsumer buffer = immediate.getBuffer(LINES_LAYER);
      Matrix4f matrix = matrices.peek().getPositionMatrix();
      
      int r = (color >> 16) & 0xFF;
      int g = (color >> 8) & 0xFF;
      int b = color & 0xFF;
      int a = (color >> 24) & 0xFF;
      
      float x1 = (float)(start.x - camPos.x);
      float y1 = (float)(start.y - camPos.y);
      float z1 = (float)(start.z - camPos.z);
      float x2 = (float)(end.x - camPos.x);
      float y2 = (float)(end.y - camPos.y);
      float z2 = (float)(end.z - camPos.z);
      
      buffer.vertex(matrix, x1, y1, z1).color(r, g, b, a);
      buffer.vertex(matrix, x2, y2, z2).color(r, g, b, a);
   }

   private void drawDashedLine(MatrixStack matrices, VertexConsumerProvider.Immediate immediate, Vec3d start, Vec3d end, int color, Vec3d camPos) {
      double dashLength = 0.15;
      double gapLength = 0.1;
      double totalPattern = dashLength + gapLength;

      Vec3d direction = end.subtract(start);
      double lineLength = direction.length();
      Vec3d normalizedDir = direction.normalize();

      double currentDistance = 0;
      while (currentDistance < lineLength) {
         double dashEnd = Math.min(currentDistance + dashLength, lineLength);
         Vec3d dashStart = start.add(normalizedDir.multiply(currentDistance));
         Vec3d dashEndPos = start.add(normalizedDir.multiply(dashEnd));
         drawLine(matrices, immediate, dashStart, dashEndPos, color, camPos);
         currentDistance += totalPattern;
      }
   }

   private int getColor(int index) {
      return switch (colorMode.get()) {
         case "Picker" -> pickColor1.getRGB();
         case "DoublePicker" -> {
            int c1 = pickColor1.getRGB();
            int c2 = pickColor2.getRGB();
            float t = (index % 10) / 10.0f;
            int r1 = (c1 >> 16) & 0xFF;
            int g1 = (c1 >> 8) & 0xFF;
            int b1 = c1 & 0xFF;
            int r2 = (c2 >> 16) & 0xFF;
            int g2 = (c2 >> 8) & 0xFF;
            int b2 = c2 & 0xFF;
            int r = (int)(r1 + (r2 - r1) * t);
            int g = (int)(g1 + (g2 - g1) * t);
            int b = (int)(b1 + (b2 - b1) * t);
            yield 0xFF000000 | (r << 16) | (g << 8) | b;
         }
         default -> ColorUtil.fade();
      };
   }

   private class FallingLine {
      List<Vec3d> points = new ArrayList<>();
      List<Vec3d> turnPoints = new ArrayList<>();
      Vec3d currentDirection;
      double distanceTraveled = 0;
      double currentSegmentLength;
      Random random = new Random();

      public FallingLine(Random random, Vec3d playerPos) {
         if (mc.gameRenderer == null || mc.player == null) {
            double range = 15;
            double x = playerPos.x + (random.nextDouble() - 0.5) * range;
            double y = playerPos.y + random.nextDouble() * 14;
            double z = playerPos.z + (random.nextDouble() - 0.5) * range;
            points.add(new Vec3d(x, y, z));
         } else {
            float yaw = mc.player.getYaw();
            float pitch = mc.player.getPitch();
            double yawRad = Math.toRadians(yaw);
            double pitchRad = Math.toRadians(pitch);

            double distance = 2 + random.nextDouble() * 6;

            double dirX = -Math.sin(yawRad) * Math.cos(pitchRad);
            double dirY = -Math.sin(pitchRad);
            double dirZ = Math.cos(yawRad) * Math.cos(pitchRad);

            Vec3d lookDir = new Vec3d(dirX, dirY, dirZ);
            Vec3d worldUp = new Vec3d(0, 1, 0);
            Vec3d right = lookDir.crossProduct(worldUp).normalize();
            Vec3d up = right.crossProduct(lookDir).normalize();

            double horizontalSpread = (random.nextDouble() - 0.5) * 24;
            double verticalSpread = (random.nextDouble() - 0.5) * 18;

            double x = playerPos.x + dirX * distance;
            double y = playerPos.y + dirY * distance;
            double z = playerPos.z + dirZ * distance;

            x += right.x * horizontalSpread + up.x * verticalSpread;
            y += right.y * horizontalSpread + up.y * verticalSpread;
            z += right.z * horizontalSpread + up.z * verticalSpread;

            points.add(new Vec3d(x, y, z));
         }

         currentDirection = getRandomDirection();
         currentSegmentLength = 0.5 + random.nextDouble() * 1.5;
      }

      public void update(Vec3d playerPos, float fallSpeed, float segmentLengthSetting, float zigzagWidth, float maxLineLength) {
         if (points.isEmpty()) return;

         Vec3d lastPoint = points.get(points.size() - 1);
         Vec3d movement = currentDirection.multiply(fallSpeed);
         Vec3d newPoint = lastPoint.add(movement);
         points.add(newPoint);
         distanceTraveled += fallSpeed;

         if (distanceTraveled >= currentSegmentLength) {
            turnPoints.add(newPoint);
            makeRandomTurn(zigzagWidth);
            distanceTraveled = 0;
            currentSegmentLength = 0.5 + random.nextDouble() * 1.5;
         }

         double totalLength = calculateTotalLength();
         while (totalLength > maxLineLength && points.size() > 2) {
            Vec3d removedPoint = points.remove(0);
            turnPoints.removeIf(turnPoint -> turnPoint.equals(removedPoint));
            totalLength = calculateTotalLength();
         }

         if (!points.isEmpty()) {
            Vec3d firstPoint = points.get(0);
            turnPoints.removeIf(turnPoint -> {
               boolean isInPoints = points.stream().anyMatch(p -> p.distanceTo(turnPoint) < 0.1);
               return !isInPoints && turnPoint.distanceTo(firstPoint) > maxLineLength;
            });
         }
      }

      private double calculateTotalLength() {
         double length = 0;
         for (int i = 0; i < points.size() - 1; i++) {
            length += points.get(i).distanceTo(points.get(i + 1));
         }
         return length;
      }

      private void makeRandomTurn(float zigzagWidth) {
         currentDirection = getRandomDirection();
      }

      private Vec3d getRandomDirection() {
         int directionType = random.nextInt(6);
         return switch (directionType) {
            case 0 -> new Vec3d(1, 0, 0);
            case 1 -> new Vec3d(-1, 0, 0);
            case 2 -> new Vec3d(0, 1, 0);
            case 3 -> new Vec3d(0, -1, 0);
            case 4 -> new Vec3d(0, 0, 1);
            case 5 -> new Vec3d(0, 0, -1);
            default -> new Vec3d(0, -1, 0);
         };
      }

      public boolean shouldRespawn(Vec3d playerPos) {
         if (points.isEmpty()) return true;

         Vec3d lastPoint = points.get(points.size() - 1);
         double distance = lastPoint.distanceTo(playerPos);
         if (distance > 18) return true;

         if (mc.gameRenderer != null) {
            Camera camera = mc.gameRenderer.getCamera();
            Vec3d cameraPos = camera.getCameraPos();
            Vec3d cameraDir = Vec3d.fromPolar(camera.getPitch(), camera.getYaw());
            Vec3d toLine = lastPoint.subtract(cameraPos).normalize();
            double dotProduct = cameraDir.dotProduct(toLine);
            if (dotProduct < 0.5) return true;
         }

         return false;
      }
   }
}
