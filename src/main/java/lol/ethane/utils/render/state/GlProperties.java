package lol.ethane.utils.render.state;

public final class GlProperties {
   private final int[] lastActiveTexture = new int[1];
   private final int[] lastProgram = new int[1];
   private final int[] lastTexture = new int[1];
   private final int[] lastSampler = new int[1];
   private final int[] lastArrayBuffer = new int[1];
   private final int[] lastVertexArrayObject = new int[1];
   private final int[] lastPolygonMode = new int[2];
   private final int[] lastViewport = new int[4];
   private final int[] lastScissorBox = new int[4];
   private final int[] lastBlendSrcRgb = new int[1];
   private final int[] lastBlendDstRgb = new int[1];
   private final int[] lastBlendSrcAlpha = new int[1];
   private final int[] lastBlendDstAlpha = new int[1];
   private final int[] lastBlendEquationRgb = new int[1];
   private final int[] lastBlendEquationAlpha = new int[1];
   private final int[] lastPixelUnpackBufferBinding = new int[1];
   private final int[] lastUnpackAlignment = new int[1];
   private final int[] lastUnpackRowLength = new int[1];
   private final int[] lastUnpackSkipPixels = new int[1];
   private final int[] lastUnpackSkipRows = new int[1];
   private final int[] lastPackSwapBytes = new int[1];
   private final int[] lastPackLsbFirst = new int[1];
   private final int[] lastPackRowLength = new int[1];
   private final int[] lastPackImageHeight = new int[1];
   private final int[] lastPackSkipPixels = new int[1];
   private final int[] lastPackSkipRows = new int[1];
   private final int[] lastPackSkipImages = new int[1];
   private final int[] lastPackAlignment = new int[1];
   private final int[] lastUnpackSwapBytes = new int[1];
   private final int[] lastUnpackLsbFirst = new int[1];
   private final int[] lastUnpackImageHeight = new int[1];
   private final int[] lastUnpackSkipImages = new int[1];
   private boolean lastEnableBlend = false;
   private boolean lastEnableCullFace = false;
   private boolean lastEnableDepthTest = false;
   private boolean lastEnableStencilTest = false;
   private boolean lastEnableScissorTest = false;
   private boolean lastEnablePrimitiveRestart = false;
   private boolean lastDepthMask;

   public int[] lastVertexArrayObject() {
      return this.lastVertexArrayObject;
   }

   public int[] lastArrayBuffer() {
      return this.lastArrayBuffer;
   }

   public int[] lastSampler() {
      return this.lastSampler;
   }

   public int[] lastTexture() {
      return this.lastTexture;
   }

   public int[] lastProgram() {
      return this.lastProgram;
   }

   public int[] lastActiveTexture() {
      return this.lastActiveTexture;
   }

   public int[] lastPolygonMode() {
      return this.lastPolygonMode;
   }

   public int[] lastViewport() {
      return this.lastViewport;
   }

   public int[] lastScissorBox() {
      return this.lastScissorBox;
   }

   public int[] lastBlendSrcRgb() {
      return this.lastBlendSrcRgb;
   }

   public int[] lastBlendDstRgb() {
      return this.lastBlendDstRgb;
   }

   public int[] lastBlendSrcAlpha() {
      return this.lastBlendSrcAlpha;
   }

   public int[] lastBlendDstAlpha() {
      return this.lastBlendDstAlpha;
   }

   public int[] lastBlendEquationRgb() {
      return this.lastBlendEquationRgb;
   }

   public int[] lastBlendEquationAlpha() {
      return this.lastBlendEquationAlpha;
   }

   public int[] lastPixelUnpackBufferBinding() {
      return this.lastPixelUnpackBufferBinding;
   }

   public int[] lastUnpackAlignment() {
      return this.lastUnpackAlignment;
   }

   public int[] lastUnpackRowLength() {
      return this.lastUnpackRowLength;
   }

   public int[] lastUnpackSkipPixels() {
      return this.lastUnpackSkipPixels;
   }

   public int[] lastUnpackSkipRows() {
      return this.lastUnpackSkipRows;
   }

   public int[] lastPackSwapBytes() {
      return this.lastPackSwapBytes;
   }

   public int[] lastPackLsbFirst() {
      return this.lastPackLsbFirst;
   }

   public int[] lastPackRowLength() {
      return this.lastPackRowLength;
   }

   public int[] lastPackImageHeight() {
      return this.lastPackImageHeight;
   }

   public int[] lastPackSkipPixels() {
      return this.lastPackSkipPixels;
   }

   public int[] lastPackSkipRows() {
      return this.lastPackSkipRows;
   }

   public int[] lastPackSkipImages() {
      return this.lastPackSkipImages;
   }

   public int[] lastPackAlignment() {
      return this.lastPackAlignment;
   }

   public int[] lastUnpackSwapBytes() {
      return this.lastUnpackSwapBytes;
   }

   public int[] lastUnpackLsbFirst() {
      return this.lastUnpackLsbFirst;
   }

   public int[] lastUnpackImageHeight() {
      return this.lastUnpackImageHeight;
   }

   public int[] lastUnpackSkipImages() {
      return this.lastUnpackSkipImages;
   }

   public boolean lastEnableBlend() {
      return this.lastEnableBlend;
   }

   public void lastEnableBlend(boolean lastEnableBlend) {
      this.lastEnableBlend = lastEnableBlend;
   }

   public boolean lastEnableCullFace() {
      return this.lastEnableCullFace;
   }

   public void lastEnableCullFace(boolean lastEnableCullFace) {
      this.lastEnableCullFace = lastEnableCullFace;
   }

   public boolean lastEnableDepthTest() {
      return this.lastEnableDepthTest;
   }

   public void lastEnableDepthTest(boolean lastEnableDepthTest) {
      this.lastEnableDepthTest = lastEnableDepthTest;
   }

   public boolean lastEnableStencilTest() {
      return this.lastEnableStencilTest;
   }

   public void lastEnableStencilTest(boolean lastEnableStencilTest) {
      this.lastEnableStencilTest = lastEnableStencilTest;
   }

   public boolean lastEnableScissorTest() {
      return this.lastEnableScissorTest;
   }

   public void lastEnableScissorTest(boolean lastEnableScissorTest) {
      this.lastEnableScissorTest = lastEnableScissorTest;
   }

   public boolean lastEnablePrimitiveRestart() {
      return this.lastEnablePrimitiveRestart;
   }

   public void lastEnablePrimitiveRestart(boolean lastEnablePrimitiveRestart) {
      this.lastEnablePrimitiveRestart = lastEnablePrimitiveRestart;
   }

   public boolean lastDepthMask() {
      return this.lastDepthMask;
   }

   public void lastDepthMask(boolean lastDepthMask) {
      this.lastDepthMask = lastDepthMask;
   }
}
