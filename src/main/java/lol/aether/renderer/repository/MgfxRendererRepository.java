package lol.aether.renderer.repository;

import lol.aether.renderer.batch.MgfxFontRenderer;
import lol.aether.renderer.batch.MgfxRectangleRenderer;
import lol.aether.renderer.sandbox.MgfxSandboxRenderer;
import lol.aether.shader.MgfxContext;
import lombok.Generated;
import net.minecraft.class_3300;

public class MgfxRendererRepository {
   private final MgfxSandboxRenderer sandboxRenderer;
   private final MgfxRectangleRenderer rectangleRenderer;
   private final MgfxFontRenderer fontRenderer;
   private final MgfxContext context;

   public MgfxRendererRepository(class_3300 manager, MgfxContext context) {
      this.context = context;
      this.sandboxRenderer = new MgfxSandboxRenderer(manager, context);
      this.rectangleRenderer = new MgfxRectangleRenderer(manager, context);
      this.fontRenderer = new MgfxFontRenderer(manager, context);
   }

   public void dispose() {
      this.sandboxRenderer.cleanup();
      this.rectangleRenderer.cleanup();
      this.fontRenderer.cleanup();
   }

   @Generated
   public MgfxSandboxRenderer getSandboxRenderer() {
      return this.sandboxRenderer;
   }

   @Generated
   public MgfxRectangleRenderer getRectangleRenderer() {
      return this.rectangleRenderer;
   }

   @Generated
   public MgfxFontRenderer getFontRenderer() {
      return this.fontRenderer;
   }

   @Generated
   public MgfxContext getContext() {
      return this.context;
   }
}
