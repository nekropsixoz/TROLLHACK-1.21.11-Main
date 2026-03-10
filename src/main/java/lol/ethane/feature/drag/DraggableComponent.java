package lol.ethane.feature.drag;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import lol.aether.builders.Rectangle;
import lol.aether.shader.MgfxContext;
import lol.ethane.Ethane;
import lol.ethane.utils.data.SaveUtil;
import lombok.Generated;
import net.minecraft.class_310;
import net.minecraft.class_332;
import org.lwjgl.glfw.GLFW;

public abstract class DraggableComponent {
   private final String name;
   private float x;
   private float y;
   private float width;
   private float height;
   private float roundness;
   private float renderX;
   private float renderY;
   private boolean dragging;
   private float dragOffsetX;
   private float dragOffsetY;
   private final List<Float> verticalSnaps = new ArrayList();
   private final List<Float> horizontalSnaps = new ArrayList();

   public DraggableComponent(String name, float x, float y, float width, float height, float roundness) {
      this.name = name;
      this.x = x;
      this.y = y;
      this.width = width;
      this.height = height;
      this.roundness = roundness;
      this.renderX = x;
      this.renderY = y;
   }

   public void render(class_332 graphics, int mouseX, int mouseY, float partialTicks) {
      this.handleDragging(mouseX, mouseY);
      float lerpSpeed = 0.2F;
      this.renderX += (this.x - this.renderX) * lerpSpeed;
      this.renderY += (this.y - this.renderY) * lerpSpeed;
      class_310 mc = class_310.method_1551();
      float screenWidth = (float)mc.method_22683().method_4486();
      float screenHeight = (float)mc.method_22683().method_4502();
      if (this.renderX < 0.0F) {
         this.renderX = 0.0F;
      }

      if (this.renderY < 0.0F) {
         this.renderY = 0.0F;
      }

      if (this.renderX + this.width > screenWidth) {
         this.renderX = screenWidth - this.width;
      }

      if (this.renderY + this.height > screenHeight) {
         this.renderY = screenHeight - this.height;
      }

      this.x = Math.max(0.0F, Math.min(this.x, screenWidth - this.width));
      this.y = Math.max(0.0F, Math.min(this.y, screenHeight - this.height));
      MgfxContext context = Ethane.getInstance().getRenderer().getContext();
      this.onRender(context, graphics, this.renderX, this.renderY, this.width, this.height);
      if (this.dragging && (!this.verticalSnaps.isEmpty() || !this.horizontalSnaps.isEmpty())) {
         Color snapColor = new Color(255, 255, 255, 120);
         int color = snapColor.getRGB();
         Iterator var12 = this.verticalSnaps.iterator();

         float snapY;
         while(var12.hasNext()) {
            snapY = (Float)var12.next();
            context.drawRectangle(Rectangle.builder().xywh(snapY - 0.5F, 0.0F, 0.75F, screenHeight).color(color));
         }

         var12 = this.horizontalSnaps.iterator();

         while(var12.hasNext()) {
            snapY = (Float)var12.next();
            context.drawRectangle(Rectangle.builder().xywh(0.0F, snapY - 0.5F, screenWidth, 0.75F).color(color));
         }
      }

   }

   public void renderBloom(class_332 graphics, float partialTicks) {
      this.onRenderBloom(graphics, this.renderX, this.renderY, this.width, this.height);
   }

   protected abstract void onRender(MgfxContext var1, class_332 var2, float var3, float var4, float var5, float var6);

   protected void onRenderBloom(class_332 graphics, float x, float y, float w, float h) {
   }

   private void handleDragging(int mouseX, int mouseY) {
      if (this.dragging) {
         if (GLFW.glfwGetMouseButton(class_310.method_1551().method_22683().method_4490(), 0) == 0) {
            this.dragging = false;
            this.verticalSnaps.clear();
            this.horizontalSnaps.clear();
            return;
         }

         float rawX = (float)mouseX - this.dragOffsetX;
         float rawY = (float)mouseY - this.dragOffsetY;
         float threshold = 4.0F;
         class_310 mc = class_310.method_1551();
         float screenWidth = (float)mc.method_22683().method_4486();
         float screenHeight = (float)mc.method_22683().method_4502();
         this.verticalSnaps.clear();
         this.horizontalSnaps.clear();
         float snappedX = rawX;
         float snappedY = rawY;
         float centerX = rawX + this.width / 2.0F;
         if (Math.abs(centerX - screenWidth / 2.0F) < 4.0F) {
            snappedX = screenWidth / 2.0F - this.width / 2.0F;
            this.verticalSnaps.add(screenWidth / 2.0F);
         }

         float centerY = rawY + this.height / 2.0F;
         if (Math.abs(centerY - screenHeight / 2.0F) < 4.0F) {
            snappedY = screenHeight / 2.0F - this.height / 2.0F;
            this.horizontalSnaps.add(screenHeight / 2.0F);
         }

         Iterator var13 = Ethane.getInstance().getDraggableRepository().getDraggables().iterator();

         while(var13.hasNext()) {
            DraggableComponent other = (DraggableComponent)var13.next();
            if (other != this) {
               this.checkSnapX(rawX, other.getX(), 4.0F, () -> {
                  this.verticalSnaps.add(other.getX());
               });
               this.checkSnapX(rawX, other.getX() + other.getWidth(), 4.0F, () -> {
                  this.verticalSnaps.add(other.getX() + other.getWidth());
               });
               this.checkSnapX(rawX + this.width, other.getX(), 4.0F, () -> {
                  this.verticalSnaps.add(other.getX());
               });
               this.checkSnapX(rawX + this.width, other.getX() + other.getWidth(), 4.0F, () -> {
                  this.verticalSnaps.add(other.getX() + other.getWidth());
               });
               this.checkSnapX(rawX + this.width / 2.0F, other.getX() + other.getWidth() / 2.0F, 4.0F, () -> {
                  this.verticalSnaps.add(other.getX() + other.getWidth() / 2.0F);
               });
               this.checkSnapY(rawY, other.getY(), 4.0F, () -> {
                  this.horizontalSnaps.add(other.getY());
               });
               this.checkSnapY(rawY, other.getY() + other.getHeight(), 4.0F, () -> {
                  this.horizontalSnaps.add(other.getY() + other.getHeight());
               });
               this.checkSnapY(rawY + this.height, other.getY(), 4.0F, () -> {
                  this.horizontalSnaps.add(other.getY());
               });
               this.checkSnapY(rawY + this.height, other.getY() + other.getHeight(), 4.0F, () -> {
                  this.horizontalSnaps.add(other.getY() + other.getHeight());
               });
               this.checkSnapY(rawY + this.height / 2.0F, other.getY() + other.getHeight() / 2.0F, 4.0F, () -> {
                  this.horizontalSnaps.add(other.getY() + other.getHeight() / 2.0F);
               });
            }
         }

         Iterator var15;
         float hSnap;
         float diffT;
         float diffB;
         float diffC;
         float minDiff;
         float finalY;
         if (!this.verticalSnaps.isEmpty()) {
            minDiff = Float.MAX_VALUE;
            finalY = rawX;
            var15 = this.verticalSnaps.iterator();

            while(var15.hasNext()) {
               hSnap = (Float)var15.next();
               diffT = Math.abs(rawX - hSnap);
               diffB = Math.abs(rawX + this.width - hSnap);
               diffC = Math.abs(rawX + this.width / 2.0F - hSnap);
               if (diffT < 4.0F && diffT < minDiff) {
                  minDiff = diffT;
                  finalY = hSnap;
               }

               if (diffB < 4.0F && diffB < minDiff) {
                  minDiff = diffB;
                  finalY = hSnap - this.width;
               }

               if (diffC < 4.0F && diffC < minDiff) {
                  minDiff = diffC;
                  finalY = hSnap - this.width / 2.0F;
               }
            }

            snappedX = finalY;
         }

         if (!this.horizontalSnaps.isEmpty()) {
            minDiff = Float.MAX_VALUE;
            finalY = rawY;
            var15 = this.horizontalSnaps.iterator();

            while(var15.hasNext()) {
               hSnap = (Float)var15.next();
               diffT = Math.abs(rawY - hSnap);
               diffB = Math.abs(rawY + this.height - hSnap);
               diffC = Math.abs(rawY + this.height / 2.0F - hSnap);
               if (diffT < 4.0F && diffT < minDiff) {
                  minDiff = diffT;
                  finalY = hSnap;
               }

               if (diffB < 4.0F && diffB < minDiff) {
                  minDiff = diffB;
                  finalY = hSnap - this.height;
               }

               if (diffC < 4.0F && diffC < minDiff) {
                  minDiff = diffC;
                  finalY = hSnap - this.height / 2.0F;
               }
            }

            snappedY = finalY;
         }

         this.x = snappedX;
         this.y = snappedY;
         SaveUtil.markDirty();
      }

   }

   private void checkSnapX(float val, float target, float threshold, Runnable onSnap) {
      if (Math.abs(val - target) < threshold) {
         onSnap.run();
      }

   }

   private void checkSnapY(float val, float target, float threshold, Runnable onSnap) {
      if (Math.abs(val - target) < threshold) {
         onSnap.run();
      }

   }

   public void mouseClicked(double mouseX, double mouseY, int button) {
      if (button == 0 && this.isHovered(mouseX, mouseY)) {
         this.dragging = true;
         this.dragOffsetX = (float)(mouseX - (double)this.x);
         this.dragOffsetY = (float)(mouseY - (double)this.y);
      }

   }

   public boolean isHovered(double mouseX, double mouseY) {
      return mouseX >= (double)this.renderX && mouseX <= (double)(this.renderX + this.width) && mouseY >= (double)this.renderY && mouseY <= (double)(this.renderY + this.height);
   }

   @Generated
   public String getName() {
      return this.name;
   }

   @Generated
   public float getX() {
      return this.x;
   }

   @Generated
   public float getY() {
      return this.y;
   }

   @Generated
   public float getWidth() {
      return this.width;
   }

   @Generated
   public float getHeight() {
      return this.height;
   }

   @Generated
   public float getRoundness() {
      return this.roundness;
   }

   @Generated
   public float getRenderX() {
      return this.renderX;
   }

   @Generated
   public float getRenderY() {
      return this.renderY;
   }

   @Generated
   public boolean isDragging() {
      return this.dragging;
   }

   @Generated
   public float getDragOffsetX() {
      return this.dragOffsetX;
   }

   @Generated
   public float getDragOffsetY() {
      return this.dragOffsetY;
   }

   @Generated
   public List<Float> getVerticalSnaps() {
      return this.verticalSnaps;
   }

   @Generated
   public List<Float> getHorizontalSnaps() {
      return this.horizontalSnaps;
   }

   @Generated
   public void setX(float x) {
      this.x = x;
   }

   @Generated
   public void setY(float y) {
      this.y = y;
   }

   @Generated
   public void setWidth(float width) {
      this.width = width;
   }

   @Generated
   public void setHeight(float height) {
      this.height = height;
   }

   @Generated
   public void setRoundness(float roundness) {
      this.roundness = roundness;
   }

   @Generated
   public void setRenderX(float renderX) {
      this.renderX = renderX;
   }

   @Generated
   public void setRenderY(float renderY) {
      this.renderY = renderY;
   }

   @Generated
   public void setDragging(boolean dragging) {
      this.dragging = dragging;
   }

   @Generated
   public void setDragOffsetX(float dragOffsetX) {
      this.dragOffsetX = dragOffsetX;
   }

   @Generated
   public void setDragOffsetY(float dragOffsetY) {
      this.dragOffsetY = dragOffsetY;
   }
}
