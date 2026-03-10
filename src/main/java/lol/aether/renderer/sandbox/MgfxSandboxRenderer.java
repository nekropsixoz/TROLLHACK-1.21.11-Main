package lol.aether.renderer.sandbox;

import lol.aether.helper.MgfxStateHelper;
import lol.aether.shader.MgfxContext;
import lol.aether.shader.MgfxShaderProgram;
import net.minecraft.class_3300;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL14;

public class MgfxSandboxRenderer extends MgfxShaderProgram {
   public MgfxSandboxRenderer(class_3300 resourceManager, MgfxContext context) {
      super(resourceManager, context, "shaders/sandbox/shader.vert", "shaders/sandbox/shader.frag");
   }

   protected void init() {
      this.register("time");
      this.register("resolution");
   }

   public void render() {
      int windowWidth = this.getContext().getWidth();
      int windowHeight = this.getContext().getHeight();
      MgfxStateHelper ignored = MgfxStateHelper.capture(0);

      try {
         this.bind();
         GL11.glDisable(3089);
         GL11.glViewport(0, 0, windowWidth, windowHeight);
         this.setFloat("time", (float)(System.currentTimeMillis() % 1000000L) / 1000.0F);
         this.setVec2("resolution", (float)windowWidth, (float)windowHeight);
         GL11.glEnable(3042);
         GL14.glBlendFuncSeparate(770, 771, 1, 0);
         GL11.glColorMask(true, true, true, true);
         GL11.glDisable(2929);
         GL11.glDisable(2884);
         this.renderFullScreen();
         this.unbind();
      } catch (Throwable var7) {
         if (ignored != null) {
            try {
               ignored.close();
            } catch (Throwable var6) {
               var7.addSuppressed(var6);
            }
         }

         throw var7;
      }

      if (ignored != null) {
         ignored.close();
      }

   }
}
