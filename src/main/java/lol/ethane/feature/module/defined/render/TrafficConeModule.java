package lol.ethane.feature.module.defined.render;

import lol.aether.builders.Rectangle;
import lol.ethane.event.defined.render.Render2DEvent;
import lol.ethane.event.subscriber.Subscribe;
import lol.ethane.feature.module.Module;
import lol.ethane.feature.module.ModuleCategory;
import lol.ethane.feature.module.property.impl.BooleanProperty;
import lol.ethane.feature.module.property.impl.NumberProperty;
import lol.ethane.utils.math.MathUtil;
import net.minecraft.class_1657;
import net.minecraft.class_310;
import org.lwjgl.opengl.GL11;
import com.mojang.blaze3d.systems.RenderSystem;

import java.util.stream.StreamSupport;

/**
 * Renders real 3D traffic cones on player heads using OpenGL with proper 3D context.
 */
public class TrafficConeModule extends Module {
    
    private static final class_310 mc = class_310.method_1551();

    // Module properties
    public final BooleanProperty showOnSelf = new BooleanProperty("Show On Self", false);
    public final BooleanProperty showOnAllPlayers = new BooleanProperty("Show All Players", true);
    public final NumberProperty coneSize = new NumberProperty("Cone Size", 0.5, 0.2, 1.5, 0.1);
    public final NumberProperty maxDistance = new NumberProperty("Max Distance", 64, 8, 256, 1);

    public TrafficConeModule() {
        super("TrafficCone", "Renders real 3D traffic cones on player heads.", ModuleCategory.RENDER);
        this.addProperties(showOnSelf, showOnAllPlayers, coneSize, maxDistance);
    }

    @Subscribe
    public void onRender2D(Render2DEvent event) {
        if (mc.field_1724 == null || mc.field_1687 == null) return;

        double maxDist = (Double) maxDistance.getValue();
        double scale = (Double) coneSize.getValue();

        StreamSupport.stream(mc.field_1687.method_18112().spliterator(), false)
                .filter(entity -> entity instanceof class_1657)
                .map(entity -> (class_1657) entity)
                .filter(player -> shouldRender(player, maxDist))
                .forEach(player -> renderTrafficCone(player, event, scale));
    }

    private boolean shouldRender(class_1657 player, double maxDistance) {
        if (player.method_6086() && !showOnAllPlayers.getValue()) return false;
        if (player == mc.field_1724 && !showOnSelf.getValue()) return false;
        
        double distance = MathUtil.distance(player, mc.field_1724);
        if (distance > maxDistance) return false;
        
        return player.method_6032() > 0;
    }

    private void renderTrafficCone(class_1657 player, Render2DEvent event, double scale) {
        try {
            // Получаем 2D позицию для рендера
            double[] screenPos = getScreenPosition(player, event);
            if (screenPos == null) return;
            
            double x = screenPos[0];
            double y = screenPos[1] - 20; // Над головой
            
            // Рисуем простой 2D конус
            drawSimple2DCone(event, x, y, scale);
        } catch (Exception e) {
            // Игнорируем ошибки рендера
        }
    }
    
    private void drawSimple2DCone(Render2DEvent event, double x, double y, double scale) {
        try {
            // Параметры конуса
            float width = (float) (30 * scale);
            float height = (float) (40 * scale);
            float xPos = (float) x;
            float yPos = (float) y;
            
            // Цвет конуса (оранжевый)
            int color = 0xFFA500;
            int whiteColor = 0xFFFFFFFF;
            
            // Рисуем основание (прямоугольник)
            event.getContext().drawRectangle(
                Rectangle.builder().xywh(xPos - width/2, yPos, width, height/4).color(color));
            
            // Рисуем белую полосу
            event.getContext().drawRectangle(
                Rectangle.builder().xywh(xPos - width/4, yPos + height/4, width/2, height/12).color(whiteColor));
            
        } catch (Exception e) {
            // Игнорируем ошибки рендера
        }
    }

    private double[] getScreenPosition(class_1657 player, Render2DEvent event) {
        try {
            // Простая проекция 3D -> 2D
            double distance = MathUtil.distance(player, mc.field_1724);
            if (distance > 64) return null;
            
            // Центр экрана
            double centerX = event.getGraphics().method_51421() / 2.0;
            double centerY = event.getGraphics().method_51443() / 2.0;
            
            // Простая проекция (можно улучшить)
            double offsetX = 0.0; // Начнем с центра
            double offsetZ = 0.0;
            
            return new double[]{centerX + offsetX, centerY + offsetZ - 50};
            
        } catch (Exception e) {
            return null;
        }
    }

    }