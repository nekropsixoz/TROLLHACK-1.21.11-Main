package lol.ethane.feature.module.defined.render;

import java.util.Comparator;
import java.util.stream.StreamSupport;
import lol.aether.builders.Rectangle;
import lol.ethane.event.defined.render.Render2DEvent;
import lol.ethane.event.subscriber.Subscribe;
import lol.ethane.feature.module.Module;
import lol.ethane.feature.module.ModuleCategory;
import lol.ethane.feature.module.property.impl.NumberProperty;
import lol.ethane.utils.math.MathUtil;
import net.minecraft.class_1657;
import net.minecraft.class_310;

/**
 * TargetESP Jello — рисует цель (ближайший игрок) в стиле Jello:
 * скруглённый прямоугольник, имя, здоровье.
 */
public class TargetESPModule extends Module {

    private final NumberProperty range = new NumberProperty("Range", 64.0, 8.0, 128.0, 4.0);

    private class_1657 currentTarget;
    private double targetDistance;

    public TargetESPModule() {
        super("Target ESP", "Jello-style target indicator.", ModuleCategory.RENDER);
        this.addProperties(range);
    }

    @Subscribe
    private void onRender2D(Render2DEvent e) {
        class_310 mc = class_310.method_1551();
        if (mc.field_1724 == null || mc.field_1687 == null) return;

        double maxRange = (Double) range.getValue();
        class_1657 target = findNearestPlayer(maxRange);
        if (target == null) {
            currentTarget = null;
            return;
        }

        currentTarget = target;
        targetDistance = MathUtil.distance(target, mc.field_1724);

        float w = 140f;
        float h = 44f;
        float x = (e.getGraphics().method_51421() - w) / 2f;
        float y = 12f;
        float r = 8f;

        int bg = 0xE01a1a1a;
        int border = 0xFF00D4FF;
        int text = 0xFFFFFFFF;

        e.getContext().drawRectangle(
            Rectangle.builder().xywh(x, y, w, h).color(bg).radius(r));
        e.getContext().drawRectangle(
            Rectangle.builder().xywh(x, y, w, 1.5f).color(border).radius(r, r, 0, 0));
        e.getContext().drawRectangle(
            Rectangle.builder().xywh(x, y + h - 1.5f, w, 1.5f).color(border).radius(0, 0, r, r));
        e.getContext().drawRectangle(
            Rectangle.builder().xywh(x, y, 1.5f, h).color(border));
        e.getContext().drawRectangle(
            Rectangle.builder().xywh(x + w - 1.5f, y, 1.5f, h).color(border));

        String name = target.method_5477().getString();
        float health = target.method_6032();
        float maxHp = target.method_6063();
        String info = String.format("%s %.1f/%.1f | %.1fm", name, health, maxHp, targetDistance);

        if (mc.field_1772 != null) {
            e.getGraphics().method_51433(mc.field_1772, info, (int) (x + 6), (int) (y + 6), text, true);
            float barW = w - 12f;
            float barY = y + h - 14f;
            e.getContext().drawRectangle(
                Rectangle.builder().xywh(x + 6, barY, barW, 6).color(0xFF4a4a4a).radius(3f));
            float fill = Math.max(0, Math.min(1, health / maxHp));
            int green = (int) (255 * fill);
            int red = 255 - green;
            int barColor = 0xFF000000 | (red << 16) | (green << 8);
            e.getContext().drawRectangle(
                Rectangle.builder().xywh(x + 6, barY, barW * fill, 6).color(barColor).radius(3f));
        }
    }

    private class_1657 findNearestPlayer(double maxRange) {
        class_310 mc = class_310.method_1551();
        if (mc.field_1724 == null || mc.field_1687 == null) return null;

        return StreamSupport.stream(mc.field_1687.method_18112().spliterator(), false)
            .filter(e -> e instanceof class_1657)
            .map(e -> (class_1657) e)
            .filter(e -> e != mc.field_1724 && e.method_5805())
            .filter(e -> MathUtil.distance(e, mc.field_1724) <= maxRange)
            .min(Comparator.comparingDouble(e -> MathUtil.distance(e, mc.field_1724)))
            .orElse(null);
    }
}
