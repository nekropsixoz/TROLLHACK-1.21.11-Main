package lol.ethane.feature.scripting.wrapper.impl;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.opengl.GlStateManager;
import net.minecraft.class_4587;
import net.minecraft.class_310;
import net.minecraft.class_332;
import net.minecraft.class_327;
import net.minecraft.class_3532;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;
import org.luaj.vm2.lib.VarArgFunction;
import org.lwjgl.opengl.GL11;

/**
 * Lua Render Wrapper - API для 2D рендеринга в Minecraft
 * Предоставляет методы для рисования прямоугольников, текста, линий и границ
 */
public class LuaRenderWrapper extends DynamicLuaWrapper {

   private static final int ARGB_ALPHA_MASK = 0xFF000000;
   private static final int ARGB_RED_MASK = 0x00FF0000;
   private static final int ARGB_GREEN_MASK = 0x0000FF00;
   private static final int ARGB_BLUE_MASK = 0x000000FF;

   public LuaRenderWrapper() {
      this.registerRenderMethods();
      this.registerInputMethods();
   }

   /**
    * Регистрирует все методы для рендеринга
    */
   private void registerRenderMethods() {

      // draw_rect(x, y, width, height, color_argb)
      // Рисует заполненный прямоугольник
      this.registerMethod("draw_rect", new VarArgFunction() {
         public LuaValue invoke(Varargs args) {
            int start = getStart(args);
            double x = args.checkdouble(start);
            double y = args.checkdouble(start + 1);
            double width = args.checkdouble(start + 2);
            double height = args.checkdouble(start + 3);
            int color = (int)args.checklong(start + 4);

            drawFilledRect(x, y, width, height, color);
            return LuaValue.NIL;
         }
      });

      // draw_outline(x, y, width, height, color_argb, thickness)
      // Рисует границу прямоугольника
      this.registerMethod("draw_outline", new VarArgFunction() {
         public LuaValue invoke(Varargs args) {
            int start = getStart(args);
            double x = args.checkdouble(start);
            double y = args.checkdouble(start + 1);
            double width = args.checkdouble(start + 2);
            double height = args.checkdouble(start + 3);
            int color = (int)args.checklong(start + 4);
            double thickness = args.optdouble(start + 5, 1.0);

            drawOutlineRect(x, y, width, height, color, thickness);
            return LuaValue.NIL;
         }
      });

      // draw_text(text, x, y, color_argb)
      // Рисует текст на экране
      this.registerMethod("draw_text", new VarArgFunction() {
         public LuaValue invoke(Varargs args) {
            int start = getStart(args);
            String text = args.checkjstring(start);
            double x = args.checkdouble(start + 1);
            double y = args.checkdouble(start + 2);
            int color = (int)args.checklong(start + 3);
            double scale = args.optdouble(start + 4, 1.0);

            drawText(text, x, y, color, scale);
            return LuaValue.NIL;
         }
      });

      // draw_text_centered(text, x, y, color_argb)
      // Рисует текст по центру
      this.registerMethod("draw_text_centered", new VarArgFunction() {
         public LuaValue invoke(Varargs args) {
            int start = getStart(args);
            String text = args.checkjstring(start);
            double x = args.checkdouble(start + 1);
            double y = args.checkdouble(start + 2);
            int color = (int)args.checklong(start + 3);

            drawTextCentered(text, x, y, color);
            return LuaValue.NIL;
         }
      });

      // draw_line(x1, y1, x2, y2, color_argb)
      // Рисует линию между двумя точками
      this.registerMethod("draw_line", new VarArgFunction() {
         public LuaValue invoke(Varargs args) {
            int start = getStart(args);
            double x1 = args.checkdouble(start);
            double y1 = args.checkdouble(start + 1);
            double x2 = args.checkdouble(start + 2);
            double y2 = args.checkdouble(start + 3);
            int color = (int)args.checklong(start + 4);
            double thickness = args.optdouble(start + 5, 1.0);

            drawLine(x1, y1, x2, y2, color, thickness);
            return LuaValue.NIL;
         }
      });

      // draw_circle(x, y, radius, color_argb)
      // Рисует заполненный круг
      this.registerMethod("draw_circle", new VarArgFunction() {
         public LuaValue invoke(Varargs args) {
            int start = getStart(args);
            double x = args.checkdouble(start);
            double y = args.checkdouble(start + 1);
            double radius = args.checkdouble(start + 2);
            int color = (int)args.checklong(start + 3);

            drawCircle(x, y, radius, color);
            return LuaValue.NIL;
         }
      });

      // draw_circle_outline(x, y, radius, color_argb)
      // Рисует граница круга
      this.registerMethod("draw_circle_outline", new VarArgFunction() {
         public LuaValue invoke(Varargs args) {
            int start = getStart(args);
            double x = args.checkdouble(start);
            double y = args.checkdouble(start + 1);
            double radius = args.checkdouble(start + 2);
            int color = (int)args.checklong(start + 3);
            double thickness = args.optdouble(start + 4, 1.0);

            drawCircleOutline(x, y, radius, color, thickness);
            return LuaValue.NIL;
         }
      });

      // draw_gradient(x, y, width, height, color1_argb, color2_argb)
      // Рисует прямоугольник с градиентом
      this.registerMethod("draw_gradient", new VarArgFunction() {
         public LuaValue invoke(Varargs args) {
            int start = getStart(args);
            double x = args.checkdouble(start);
            double y = args.checkdouble(start + 1);
            double width = args.checkdouble(start + 2);
            double height = args.checkdouble(start + 3);
            int color1 = (int)args.checklong(start + 4);
            int color2 = (int)args.checklong(start + 5);

            drawGradientRect(x, y, width, height, color1, color2);
            return LuaValue.NIL;
         }
      });

      // draw_triangle(x1, y1, x2, y2, x3, y3, color_argb)
      // Рисует заполненный треугольник
      this.registerMethod("draw_triangle", new VarArgFunction() {
         public LuaValue invoke(Varargs args) {
            int start = getStart(args);
            double x1 = args.checkdouble(start);
            double y1 = args.checkdouble(start + 1);
            double x2 = args.checkdouble(start + 2);
            double y2 = args.checkdouble(start + 3);
            double x3 = args.checkdouble(start + 4);
            double y3 = args.checkdouble(start + 5);
            int color = (int)args.checklong(start + 6);

            drawTriangle(x1, y1, x2, y2, x3, y3, color);
            return LuaValue.NIL;
         }
      });

      // get_screen_width()
      // Возвращает ширину экрана
      this.register("get_screen_width", () -> {
         if (class_310.method_1551() != null && class_310.method_1551().method_22683() != null) {
            return LuaValue.valueOf(class_310.method_1551().method_22683().method_4480());
         }
         return LuaValue.ZERO;
      });

      // get_screen_height()
      // Возвращает высоту экрана
      this.register("get_screen_height", () -> {
         if (class_310.method_1551() != null && class_310.method_1551().method_22683() != null) {
            return LuaValue.valueOf(class_310.method_1551().method_22683().method_4507());
         }
         return LuaValue.ZERO;
      });
   }

   /**
    * Регистрирует методы для работы с вводом (мышь, клавиатура)
    */
   private void registerInputMethods() {

      // get_mouse_x()
      // Возвращает X позицию мыши
      this.register("get_mouse_x", () -> {
         return LuaValue.valueOf(getMouseX());
      });

      // get_mouse_y()
      // Возвращает Y позицию мыши
      this.register("get_mouse_y", () -> {
         return LuaValue.valueOf(getMouseY());
      });

      // get_mouse_pos()
      // Возвращает позицию мыши как таблицу {x, y}
      this.registerMethod("get_mouse_pos", new VarArgFunction() {
         public LuaValue invoke(Varargs args) {
            LuaValue table = new org.luaj.vm2.LuaTable();
            table.set("x", LuaValue.valueOf(getMouseX()));
            table.set("y", LuaValue.valueOf(getMouseY()));
            return table;
         }
      });
   }

   // ===== ВСПОМОГАТЕЛЬНЫЕ МЕТОДЫ =====

   private int getStart(Varargs args) {
      return args.arg(1) instanceof LuaRenderWrapper ? 2 : 1;
   }

   private double getMouseX() {
      if (class_310.method_1551() == null) return 0;
      // TODO: Получить актуальную X позицию мыши из окна
      return 0; // Placeholder
   }

   private double getMouseY() {
      if (class_310.method_1551() == null) return 0;
      // TODO: Получить актуальную Y позицию мыши из окна
      return 0; // Placeholder
   }

   /**
    * Рисует заполненный прямоугольник
    */
   private static void drawFilledRect(double x, double y, double width, double height, int color) {
      try {
         float alpha = ((color >> 24) & 0xFF) / 255.0f;
         float red = ((color >> 16) & 0xFF) / 255.0f;
         float green = ((color >> 8) & 0xFF) / 255.0f;
         float blue = (color & 0xFF) / 255.0f;

         GlStateManager._enableBlend();
         GlStateManager._blendFuncSeparate(770, 771, 1, 0);
         GlStateManager._colorMask(true, true, true, true);

         // Используем GuiComponent для рисования (если доступен)
         // GuiComponent.fill(x, y, x + width, y + height, color);

         // Если GuiComponent недоступен, рисуем используя Tessellator
         drawRectTessellator(x, y, x + width, y + height, color);

         GlStateManager._disableBlend();
      } catch (Exception e) {
         e.printStackTrace();
      }
   }

   /**
    * Рисует граничу прямоугольника
    */
   private static void drawOutlineRect(double x, double y, double width, double height, int color, double thickness) {
      try {
         // Верхняя линия
         drawLine(x, y, x + width, y, color, thickness);
         // Нижняя линия
         drawLine(x, y + height, x + width, y + height, color, thickness);
         // Левая линия
         drawLine(x, y, x, y + height, color, thickness);
         // Правая линия
         drawLine(x + width, y, x + width, y + height, color, thickness);
      } catch (Exception e) {
         e.printStackTrace();
      }
   }

   /**
    * Рисует текст на экране
    */
   private static void drawText(String text, double x, double y, int color, double scale) {
      try {
         if (class_310.method_1551() == null) return;

         class_4587 poseStack = new class_4587();
         poseStack.method_22903();
         poseStack.method_46416((float)x, (float)y, 0.0f);
         poseStack.method_22905((float)scale, (float)scale, 1.0f);

         // Получаем Font из клиента
         // Font font = class_310.method_1551().field_1820;
         // MultiBufferSource.BufferSource buffer = MultiBufferSource.immediate(Tessellator.getInstance().getBuilder());
         // font.drawInBatch(text, 0, 0, color, false, poseStack.last().pose(), buffer, true, 0, 15728880);
         // buffer.endBatch();

         poseStack.method_22909();
      } catch (Exception e) {
         e.printStackTrace();
      }
   }

   /**
    * Рисует текст по центру
    */
   private static void drawTextCentered(String text, double x, double y, int color) {
      try {
         if (class_310.method_1551() == null) return;

         // Font font = class_310.method_1551().field_1820;
         // int textWidth = font.width(text);
         // drawText(text, x - textWidth / 2.0, y, color, 1.0);
      } catch (Exception e) {
         e.printStackTrace();
      }
   }

   /**
    * Рисует линию между двумя точками
    */
   private static void drawLine(double x1, double y1, double x2, double y2, int color, double thickness) {
      try {
         float alpha = ((color >> 24) & 0xFF) / 255.0f;
         float red = ((color >> 16) & 0xFF) / 255.0f;
         float green = ((color >> 8) & 0xFF) / 255.0f;
         float blue = (color & 0xFF) / 255.0f;

         GlStateManager._enableBlend();
         GlStateManager._blendFuncSeparate(770, 771, 1, 0);
         GlStateManager._colorMask(true, true, true, true);
         GL11.glLineWidth((float)thickness);

         // Рисуем линию используя Tessellator
         drawLineTessellator(x1, y1, x2, y2);

         GL11.glLineWidth(1.0f);
         GlStateManager._disableBlend();
      } catch (Exception e) {
         e.printStackTrace();
      }
   }

   /**
    * Рисует заполненный круг
    */
   private static void drawCircle(double x, double y, double radius, int color) {
      try {
         int segments = (int)(radius * 2);
         segments = Math.max(segments, 8);

         float alpha = ((color >> 24) & 0xFF) / 255.0f;
         float red = ((color >> 16) & 0xFF) / 255.0f;
         float green = ((color >> 8) & 0xFF) / 255.0f;
         float blue = (color & 0xFF) / 255.0f;

         GlStateManager._enableBlend();
         GlStateManager._blendFuncSeparate(770, 771, 1, 0);
         GlStateManager._colorMask(true, true, true, true);

         double angle = 0;
         double step = 360.0 / segments;

         for (int i = 0; i < segments; i++) {
            double angle1 = angle;
            double angle2 = angle + step;

            double x1 = x + Math.cos(Math.toRadians(angle1)) * radius;
            double y1 = y + Math.sin(Math.toRadians(angle1)) * radius;
            double x2 = x + Math.cos(Math.toRadians(angle2)) * radius;
            double y2 = y + Math.sin(Math.toRadians(angle2)) * radius;

            drawTriangle(x, y, x1, y1, x2, y2, color);

            angle = angle2;
         }

         GlStateManager._disableBlend();
      } catch (Exception e) {
         e.printStackTrace();
      }
   }

   /**
    * Рисует граница круга
    */
   private static void drawCircleOutline(double x, double y, double radius, int color, double thickness) {
      try {
         int segments = (int)(radius * 2);
         segments = Math.max(segments, 8);

         double angle = 0;
         double step = 360.0 / segments;

         for (int i = 0; i < segments; i++) {
            double angle1 = angle;
            double angle2 = angle + step;

            double x1 = x + Math.cos(Math.toRadians(angle1)) * radius;
            double y1 = y + Math.sin(Math.toRadians(angle1)) * radius;
            double x2 = x + Math.cos(Math.toRadians(angle2)) * radius;
            double y2 = y + Math.sin(Math.toRadians(angle2)) * radius;

            drawLine(x1, y1, x2, y2, color, thickness);

            angle = angle2;
         }
      } catch (Exception e) {
         e.printStackTrace();
      }
   }

   /**
    * Рисует прямоугольник с градиентом
    */
   private static void drawGradientRect(double x, double y, double width, double height, int color1, int color2) {
      try {
         // Разбиваем градиент на несколько слоев
         int steps = 10;
         for (int i = 0; i < steps; i++) {
            float progress = (float)i / steps;
            int blendedColor = blendColors(color1, color2, progress);

            double stepHeight = height / steps;
            drawFilledRect(x, y + (stepHeight * i), width, stepHeight, blendedColor);
         }
      } catch (Exception e) {
         e.printStackTrace();
      }
   }

   /**
    * Рисует заполненный треугольник
    */
   private static void drawTriangle(double x1, double y1, double x2, double y2, double x3, double y3, int color) {
      try {
         float alpha = ((color >> 24) & 0xFF) / 255.0f;
         float red = ((color >> 16) & 0xFF) / 255.0f;
         float green = ((color >> 8) & 0xFF) / 255.0f;
         float blue = (color & 0xFF) / 255.0f;

         GlStateManager._enableBlend();
         GlStateManager._blendFuncSeparate(770, 771, 1, 0);
         GlStateManager._colorMask(true, true, true, true);

         // Рисуем треугольник используя Tessellator
         drawTriangleTessellator(x1, y1, x2, y2, x3, y3);

         GlStateManager._disableBlend();
      } catch (Exception e) {
         e.printStackTrace();
      }
   }

   // ===== TESSELLATOR МЕТОДЫ =====

   /**
    * Рисует прямоугольник используя Tessellator
    */
   private static void drawRectTessellator(double x1, double y1, double x2, double y2, int color) {
      // TODO: Реализовать рисование прямоугольника через Tessellator
      // Это требует доступа к BufferBuilder и правильной установки матриц
   }

   /**
    * Рисует линию используя Tessellator
    */
   private static void drawLineTessellator(double x1, double y1, double x2, double y2) {
      // TODO: Реализовать рисование линии через Tessellator
   }

   /**
    * Рисует треугольник используя Tessellator
    */
   private static void drawTriangleTessellator(double x1, double y1, double x2, double y2, double x3, double y3) {
      // TODO: Реализовать рисование треугольника через Tessellator
   }

   // ===== УТИЛИТЫ ДЛЯ ЦВЕТОВ =====

   /**
    * Смешивает два цвета
    */
   private static int blendColors(int color1, int color2, float progress) {
      int a1 = (color1 >> 24) & 0xFF;
      int r1 = (color1 >> 16) & 0xFF;
      int g1 = (color1 >> 8) & 0xFF;
      int b1 = color1 & 0xFF;

      int a2 = (color2 >> 24) & 0xFF;
      int r2 = (color2 >> 16) & 0xFF;
      int g2 = (color2 >> 8) & 0xFF;
      int b2 = color2 & 0xFF;

      int a = (int)(a1 + (a2 - a1) * progress);
      int r = (int)(r1 + (r2 - r1) * progress);
      int g = (int)(g1 + (g2 - g1) * progress);
      int b = (int)(b1 + (b2 - b1) * progress);

      return (a << 24) | (r << 16) | (g << 8) | b;
   }

   /**
    * Преобразует RGB в ARGB
    */
   public static int rgbToArgb(int r, int g, int b, int a) {
      return (a << 24) | (r << 16) | (g << 8) | b;
   }
}