package lol.aether.helper;

import com.mojang.blaze3d.systems.GpuDevice;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.textures.GpuTexture;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import net.minecraft.class_10865;
import net.minecraft.class_10868;
import net.minecraft.class_10874;
import net.minecraft.class_276;
import net.minecraft.class_2960;
import net.minecraft.class_3298;
import net.minecraft.class_3300;
import org.apache.commons.io.IOUtils;
import org.lwjgl.opengl.GL20;

public class MgfxShaderHelper {
   public static int createShader(class_3300 manager, String fragmentPath, String vertexPath) {
      int program = GL20.glCreateProgram();
      int vertexId = createShader(manager, vertexPath, 35633);
      int fragmentId = createShader(manager, fragmentPath, 35632);
      GL20.glAttachShader(program, vertexId);
      GL20.glAttachShader(program, fragmentId);
      GL20.glLinkProgram(program);
      if (GL20.glGetProgrami(program, 35714) == 0) {
         String error = GL20.glGetProgramInfoLog(program, 1024);
         System.err.println("Failed to link shader program: " + error);
         GL20.glDeleteProgram(program);
         return 0;
      } else {
         GL20.glValidateProgram(program);
         if (GL20.glGetProgrami(program, 35715) == 0) {
            System.err.println("Failed to validate shader program: " + GL20.glGetProgramInfoLog(program, 1024));
         }

         GL20.glDetachShader(program, vertexId);
         GL20.glDetachShader(program, fragmentId);
         GL20.glDeleteShader(vertexId);
         GL20.glDeleteShader(fragmentId);
         return program;
      }
   }

   private static int createShader(class_3300 manager, String path, int type) {
      int shaderId = GL20.glCreateShader(type);

      String error;
      try {
         error = readResource(manager, path);
         GL20.glShaderSource(shaderId, error);
         GL20.glCompileShader(shaderId);
      } catch (Exception var5) {
         GL20.glDeleteShader(shaderId);
         throw var5;
      }

      if (GL20.glGetShaderi(shaderId, 35713) == 0) {
         error = GL20.glGetShaderInfoLog(shaderId, 1024);
         System.err.println("Failed to compile shader (" + path + "): " + error);
         GL20.glDeleteShader(shaderId);
         throw new RuntimeException("Shader compilation failed: " + path);
      } else {
         return shaderId;
      }
   }

   public static int framebufferId(class_276 target) {
      GpuTexture texture = target.method_30277();
      if (texture instanceof class_10868) {
         class_10868 glTexture = (class_10868)texture;
         GpuDevice device = RenderSystem.getDevice();
         if (device instanceof class_10865) {
            class_10865 backend = (class_10865)device;
            class_10874 buffers = backend.method_68401();
            return glTexture.method_68426(buffers, target.method_30278());
         } else {
            throw new IllegalStateException("Only OpenGL backend is supported");
         }
      } else {
         throw new IllegalStateException("Expected GL texture attachment");
      }
   }

   private static String readResource(class_3300 manager, String path) {
      class_2960 location;
      if (path.contains(":")) {
         location = class_2960.method_60654(path);
      } else {
         location = class_2960.method_60655("ethane", path);
      }

      Optional<class_3298> resource = manager.method_14486(location);
      if (resource.isEmpty()) {
         throw new RuntimeException("Could not find shader resource: " + String.valueOf(location));
      } else {
         try {
            InputStream stream = ((class_3298)resource.get()).method_14482();

            String var5;
            try {
               var5 = IOUtils.toString(stream, StandardCharsets.UTF_8);
            } catch (Throwable var8) {
               if (stream != null) {
                  try {
                     stream.close();
                  } catch (Throwable var7) {
                     var8.addSuppressed(var7);
                  }
               }

               throw var8;
            }

            if (stream != null) {
               stream.close();
            }

            return var5;
         } catch (IOException var9) {
            throw new RuntimeException("Failed to read shader resource: " + String.valueOf(location), var9);
         }
      }
   }
}
