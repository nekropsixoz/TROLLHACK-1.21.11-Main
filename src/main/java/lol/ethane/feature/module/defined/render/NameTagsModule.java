package lol.ethane.feature.module.defined.render;

import lol.aether.builders.Rectangle;
import lol.ethane.event.defined.render.Render2DEvent;
import lol.ethane.event.subscriber.Subscribe;
import lol.ethane.feature.module.Module;
import lol.ethane.feature.module.ModuleCategory;
import lol.ethane.feature.module.property.impl.BooleanProperty;
import lol.ethane.feature.module.property.impl.mode.ModeProperty;
import lol.ethane.feature.module.property.impl.NumberProperty;
import lol.ethane.utils.math.MathUtil;
import net.minecraft.class_1657;
import net.minecraft.class_243;
import net.minecraft.class_310;

import java.util.stream.StreamSupport;

/**
 * Renders custom name tags above players.
 */
public class NameTagsModule extends Module {
    
    private static final class_310 mc = class_310.method_1551();
    
    // Module properties
    public final ModeProperty<RenderMode> renderMode = new ModeProperty<>("Render Mode", RenderMode.SIMPLE);
    public final BooleanProperty showHealth = new BooleanProperty("Show Health", true);
    public final BooleanProperty showDistance = new BooleanProperty("Show Distance", true);
    public final BooleanProperty showArmor = new BooleanProperty("Show Armor", true);
    public final BooleanProperty showInvisibles = new BooleanProperty("Show Invisibles", false);
    public final NumberProperty scale = new NumberProperty("Scale", 1.0, 0.5, 3.0, 0.1);
    public final NumberProperty maxDistance = new NumberProperty("Max Distance", 64, 8, 256, 1);
    
    public NameTagsModule() {
        super("NameTags", "Renders custom name tags above players.", ModuleCategory.RENDER);
        this.addProperties(renderMode, showHealth, showDistance, showArmor, showInvisibles, scale, maxDistance);
    }
    
    @Subscribe
    public void onRender2D(Render2DEvent event) {
        if (mc.field_1724 == null || mc.field_1687 == null) return;
        
        StreamSupport.stream(mc.field_1687.method_18112().spliterator(), false)
            .filter(entity -> entity instanceof class_1657)
            .map(entity -> (class_1657) entity)
            .filter(player -> shouldRender(player))
            .forEach(player -> renderNameTag(player, event));
    }
    
    private boolean shouldRender(class_1657 player) {
        if (player == mc.field_1724) return false;
        if (!showInvisibles.getValue() && player.method_5805()) return false;
        
        double distance = MathUtil.distance(player, mc.field_1724);
        return distance <= (Double) maxDistance.getValue();
    }
    
    private void renderNameTag(class_1657 player, Render2DEvent event) {
        float scaleValue = ((Double) scale.getValue()).floatValue();
        
        // Get 3D world position and project to 2D screen
        class_243 worldPos = new class_243(
            player.method_23317(), 
            player.method_23318() + 2.2, // Above head
            player.method_23321()
        );
        
        double[] screenPos = getScreenPosition(worldPos);
        if (screenPos == null) return;
        
        float x = (float) screenPos[0];
        float y = (float) screenPos[1];
        
        switch (renderMode.getValue()) {
            case SIMPLE:
                renderSimpleNameTag(player, x, y, scaleValue, event);
                break;
            case DETAILED:
                renderDetailedNameTag(player, x, y, scaleValue, event);
                break;
            case MODERN:
                renderModernNameTag(player, x, y, scaleValue, event);
                break;
        }
    }
    
    private double[] getScreenPosition(class_243 worldPos) {
        try {
            if (mc.method_22683() == null) return null;
            float screenWidth = mc.method_22683().method_4486();
            float screenHeight = mc.method_22683().method_4502();
            
            class_243 cameraPos = mc.field_1724.method_33571();
            float yaw = mc.field_1724.method_36454();
            float pitch = mc.field_1724.method_36455();
            
            double dx = worldPos.field_1352 - cameraPos.field_1352;
            double dy = worldPos.field_1351 - cameraPos.field_1351;
            double dz = worldPos.field_1350 - cameraPos.field_1350;
            
            double yawRad = Math.toRadians(-yaw);
            double pitchRad = Math.toRadians(-pitch);
            
            double rotatedX = dx * Math.cos(yawRad) - dz * Math.sin(yawRad);
            double rotatedZ = dx * Math.sin(yawRad) + dz * Math.cos(yawRad);
            double finalY = dy * Math.cos(pitchRad) - rotatedZ * Math.sin(pitchRad);
            double finalZ = dy * Math.sin(pitchRad) + rotatedZ * Math.cos(pitchRad);
            
            if (finalZ <= 0) return null;
            
            double fov = 70.0; // Default FOV
            double fovRad = Math.toRadians(fov);
            
            double screenX = screenWidth / 2.0 + (rotatedX / finalZ) * (screenHeight / (2.0 * Math.tan(fovRad / 2.0)));
            double screenY = screenHeight / 2.0 - (finalY / finalZ) * (screenHeight / (2.0 * Math.tan(fovRad / 2.0)));
            
            if (screenX < 0 || screenX > screenWidth || screenY < 0 || screenY > screenHeight) return null;
            
            return new double[]{screenX, screenY};
        } catch (Exception e) {
            return null;
        }
    }
    
    private void renderSimpleNameTag(class_1657 player, float x, float y, float scale, Render2DEvent event) {
        String name = player.method_5477().getString();
        
        // Render name
        if (mc.field_1772 != null) {
            event.getGraphics().method_51433(mc.field_1772, name, (int) x, (int) y, 0xFFFFFFFF, true);
        }
        
        // Render health
        if (showHealth.getValue()) {
            float health = player.method_6032();
            float maxHealth = player.method_6063();
            String healthText = String.format("%.1f/%.1f", health, maxHealth);
            
            if (mc.field_1772 != null) {
                event.getGraphics().method_51433(mc.field_1772, healthText, (int) x, (int) (y + 15 * scale), 0xFFFF0000, true);
            }
        }
    }
    
    private void renderDetailedNameTag(class_1657 player, float x, float y, float scale, Render2DEvent event) {
        String name = player.method_5477().getString();
        
        // Background
        float nameWidth = mc.field_1772 != null ? mc.field_1772.method_1727(name) : 60;
        float bgWidth = nameWidth + 8;
        float bgHeight = 20;
        
        event.getContext().drawRectangle(
            Rectangle.builder().xywh(x - bgWidth/2, y - 2, bgWidth, bgHeight).color(0xE0000000));
        event.getContext().drawRectangle(
            Rectangle.builder().xywh(x - bgWidth/2, y - 2, bgWidth, 1).color(0xFF333333));
        
        // Name
        if (mc.field_1772 != null) {
            event.getGraphics().method_51433(mc.field_1772, name, (int) x, (int) y, 0xFFFFFFFF, true);
        }
        
        float yOffset = 15;
        
        // Health bar
        if (showHealth.getValue()) {
            float health = player.method_6032();
            float maxHealth = player.method_6063();
            float healthPercent = health / maxHealth;
            
            // Health bar background
            event.getContext().drawRectangle(
                Rectangle.builder().xywh(x - 20, y + yOffset, 40, 3).color(0xFF000000));
            
            // Health bar fill
            int healthColor = healthPercent > 0.6 ? 0xFF00FF00 : 
                            healthPercent > 0.3 ? 0xFFFFFF00 : 0xFFFF0000;
            event.getContext().drawRectangle(
                Rectangle.builder().xywh(x - 20, y + yOffset, 40 * healthPercent, 3).color(healthColor));
            
            // Health text
            String healthText = String.format("%.1f/%.1f", health, maxHealth);
            if (mc.field_1772 != null) {
                event.getGraphics().method_51433(mc.field_1772, healthText, (int) x, (int) (y + yOffset + 5), 0xFFFFFFFF, true);
            }
            yOffset += 15;
        }
        
        // Distance
        if (showDistance.getValue()) {
            String distanceText = String.format("Distance: %.1fm", MathUtil.distance(player, mc.field_1724));
            if (mc.field_1772 != null) {
                event.getGraphics().method_51433(mc.field_1772, distanceText, (int) x, (int) (y + yOffset), 0xFFAAAAAA, true);
            }
        }
    }
    
    private void renderModernNameTag(class_1657 player, float x, float y, float scale, Render2DEvent event) {
        String name = player.method_5477().getString();
        
        // Modern background with rounded corners
        float nameWidth = mc.field_1772 != null ? mc.field_1772.method_1727(name) : 60;
        float bgWidth = nameWidth + 12;
        float bgHeight = 25;
        
        event.getContext().drawRectangle(
            Rectangle.builder().xywh(x - bgWidth/2, y - 3, bgWidth, bgHeight).color(0xE0141414).radius(4));
        event.getContext().drawRectangle(
            Rectangle.builder().xywh(x - bgWidth/2 + 1, y - 2, bgWidth - 2, 1).color(0xFF666666).radius(3, 3, 0, 0));
        
        // Name with shadow
        if (mc.field_1772 != null) {
            event.getGraphics().method_51433(mc.field_1772, name, (int) (x + 1), (int) (y + 1), 0xFF000000, true);
            event.getGraphics().method_51433(mc.field_1772, name, (int) x, (int) y, 0xFFFFFFFF, true);
        }
        
        float yOffset = 18;
        
        // Modern health bar
        if (showHealth.getValue()) {
            float health = player.method_6032();
            float maxHealth = player.method_6063();
            float healthPercent = Math.min(health / maxHealth, 1.0f);
            
            // Health bar background
            event.getContext().drawRectangle(
                Rectangle.builder().xywh(x - 25, y + yOffset, 50, 4).color(0xE0282828).radius(2));
            
            // Health bar fill
            int healthColor = getHealthColor(healthPercent);
            event.getContext().drawRectangle(
                Rectangle.builder().xywh(x - 25, y + yOffset, 50 * healthPercent, 4).color(healthColor).radius(2));
            
            // Health percentage
            String healthPercentText = String.format("%d%%", (int)(healthPercent * 100));
            if (mc.field_1772 != null) {
                event.getGraphics().method_51433(mc.field_1772, healthPercentText, (int) x, (int) (y + yOffset + 6), healthColor, true);
            }
            yOffset += 12;
        }
        
        // Distance indicator
        if (showDistance.getValue()) {
            String distanceText = String.format("%.1fm", MathUtil.distance(player, mc.field_1724));
            if (mc.field_1772 != null) {
                event.getGraphics().method_51433(mc.field_1772, distanceText, (int) (x + nameWidth/2 + 10), (int) y, 0xFFFFFF00, true);
            }
        }
    }
    
    private int getHealthColor(float percent) {
        if (percent > 0.6) return 0xFF00FF00;
        if (percent > 0.3) return 0xFFFFFF00;
        return 0xFFFF0000;
    }
    
    public enum RenderMode {
        SIMPLE("Simple"),
        DETAILED("Detailed"), 
        MODERN("Modern");
        
        private final String name;
        
        RenderMode(String name) {
            this.name = name;
        }
        
        @Override
        public String toString() {
            return name;
        }
    }
}
