package lol.ethane.utils.render.state;

import java.util.Stack;
import org.lwjgl.opengl.GL30;

public class GlStates {
   private static final Stack<GlState> GL_STATES = new Stack();

   public static void push() {
      GL_STATES.push((new GlState(getGLVersion())).push());
   }

   public static void pop() {
      if (!GL_STATES.empty()) {
         ((GlState)GL_STATES.pop()).pop();
      }

   }

   public static int getGLVersion() {
      int[] major = new int[1];
      int[] minor = new int[1];
      GL30.glGetIntegerv(33307, major);
      GL30.glGetIntegerv(33308, minor);
      return major[0] * 100 + minor[0] * 10;
   }
}
