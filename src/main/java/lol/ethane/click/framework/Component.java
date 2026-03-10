package lol.ethane.click.framework;

import lol.aether.shader.MgfxContext;
import lombok.Generated;
import net.minecraft.class_11909;

public class Component {
   public boolean expanded;
   public boolean last;
   public float x;
   public float y;
   public float width;
   public float height;

   public void render(MgfxContext context, int mouseX, int mouseY) {
   }

   public float getTotalHeight() {
      return 0.0F;
   }

   public void setBounds(float x, float y, float width, float height) {
      this.x = x;
      this.y = y;
      this.width = width;
      this.height = height;
   }

   public boolean isHovered(double mouseX, double mouseY) {
      return mouseX > (double)this.x && mouseX < (double)(this.x + this.width) && mouseY > (double)this.y && mouseY < (double)(this.y + this.height);
   }

   public boolean isHovered(double x, double y, double width, double height, double mouseX, double mouseY) {
      return mouseX > x && mouseX < x + width && mouseY > y && mouseY < y + height;
   }

   public void mouseClicked(class_11909 event) {
   }

   public void mouseReleased(class_11909 event) {
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
}
