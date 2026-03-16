package ru.noxium.module.impl.render;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.BufferAllocator;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.Vec3d;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import ru.noxium.event.EventInit;
import ru.noxium.event.render.EventRender3D;
import ru.noxium.module.api.Category;
import ru.noxium.module.api.IModule;
import ru.noxium.module.api.Module;
import ru.noxium.module.api.setting.impl.BooleanSetting;
import ru.noxium.module.api.setting.impl.HueSetting;
import ru.noxium.module.api.setting.impl.ModeSetting;
import ru.noxium.module.api.setting.impl.SliderSetting;
import ru.noxium.util.color.ColorUtil;
import ru.noxium.util.render.world.WorldRenderLayers;

@IModule(
    name = "Charms",
    description = "3D объекты возле игрока",
    category = Category.Visuals,
    bind = -1
)
@Environment(EnvType.CLIENT)
public class Charms extends Module {
    
    private final ModeSetting shape = new ModeSetting("Форма", "Куб", "Куб", "Октаэдр", "Икосаэдр");
    private final BooleanSetting glow = new BooleanSetting("Свечение", true);
    private final SliderSetting glowIntensity = new SliderSetting("Интенсивность", 2.0F, 0.5F, 5.0F, 0.1F, false)
        .hidden(() -> !glow.get());
    private final SliderSetting size = new SliderSetting("Размер", 0.3F, 0.1F, 1.0F, 0.05F, false);
    private final SliderSetting distance = new SliderSetting("Дистанция", 1.2F, 0.5F, 3.0F, 0.1F, false);
    private final SliderSetting rotationSpeed = new SliderSetting("Скорость вращения", 1.0F, 0.1F, 5.0F, 0.1F, false);
    private final ModeSetting colorMode = new ModeSetting("Режим цвета", "Client", "Client", "Picker");
    private final HueSetting pickColor = new HueSetting("Цвет", 50.0f).hidden(() -> !colorMode.is("Picker"));
    
    private float rotation = 0.0F;
    
    public Charms() {
        addSettings(shape, glow, glowIntensity, size, distance, rotationSpeed, colorMode, pickColor);
    }
    
    @EventInit
    public void onRender3D(EventRender3D event) {
        if (mc.player == null || mc.world == null || mc.gameRenderer == null) return;
        
        rotation += rotationSpeed.get();
        if (rotation >= 360.0F) rotation -= 360.0F;
        
        Vec3d playerPos = new Vec3d(mc.player.getX(), mc.player.getY(), mc.player.getZ());
        Vec3d cameraPos = mc.gameRenderer.getCamera().getCameraPos();
        
        // Позиция чарма относительно игрока (слева от игрока)
        float yaw = mc.player.bodyYaw;
        double angleRad = Math.toRadians(yaw + 90); // Слева от игрока
        double offsetX = Math.cos(angleRad) * distance.get();
        double offsetZ = Math.sin(angleRad) * distance.get();
        
        double charmX = playerPos.x + offsetX - cameraPos.x;
        double charmY = playerPos.y + mc.player.getHeight() * 0.6 - cameraPos.y;
        double charmZ = playerPos.z + offsetZ - cameraPos.z;
        
        MatrixStack matrices = event.getMatrixStack();
        BufferAllocator allocator = new BufferAllocator(262144);
        VertexConsumerProvider.Immediate immediate = VertexConsumerProvider.immediate(allocator);
        
        try {
            matrices.push();
            matrices.translate(charmX, charmY, charmZ);
            matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(rotation));
            matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(rotation * 0.7F));
            matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(rotation * 0.5F));
            
            int color = colorMode.is("Picker") ? pickColor.getRGB() : ColorUtil.fade();
            
            // Рендерим основную форму
            renderShape(matrices, immediate, color, size.get(), false);
            
            // Рендерим глоу если включено
            if (glow.get()) {
                float glowSize = size.get() * (1.0F + glowIntensity.get() * 0.2F);
                int glowColor = ColorUtil.replAlpha(color, 0.3F);
                renderShape(matrices, immediate, glowColor, glowSize, true);
            }
            
            matrices.pop();
            immediate.draw();
        } finally {
            allocator.close();
        }
    }
    
    private void renderShape(MatrixStack matrices, VertexConsumerProvider.Immediate immediate, 
                            int color, float shapeSize, boolean isGlow) {
        switch (shape.get()) {
            case "Куб":
                renderCube(matrices, immediate, color, shapeSize, isGlow);
                break;
            case "Октаэдр":
                renderOctahedron(matrices, immediate, color, shapeSize, isGlow);
                break;
            case "Икосаэдр":
                renderIcosahedron(matrices, immediate, color, shapeSize, isGlow);
                break;
        }
    }
    
    private void renderCube(MatrixStack matrices, VertexConsumerProvider.Immediate immediate, 
                           int color, float size, boolean isGlow) {
        RenderLayer layer = isGlow ? 
            WorldRenderLayers.POSITION_COLOR_QUADS_NO_DEPTH_BLEND() : 
            WorldRenderLayers.POSITION_COLOR_QUADS_NO_DEPTH();
        VertexConsumer buffer = immediate.getBuffer(layer);
        Matrix4f matrix = matrices.peek().getPositionMatrix();
        
        int r = (color >> 16) & 0xFF;
        int g = (color >> 8) & 0xFF;
        int b = color & 0xFF;
        int a = (color >> 24) & 0xFF;
        
        float s = size / 2.0F;
        
        // Передняя грань
        buffer.vertex(matrix, -s, -s, s).color(r, g, b, a);
        buffer.vertex(matrix, s, -s, s).color(r, g, b, a);
        buffer.vertex(matrix, s, s, s).color(r, g, b, a);
        buffer.vertex(matrix, -s, s, s).color(r, g, b, a);
        
        // Задняя грань
        buffer.vertex(matrix, s, -s, -s).color(r, g, b, a);
        buffer.vertex(matrix, -s, -s, -s).color(r, g, b, a);
        buffer.vertex(matrix, -s, s, -s).color(r, g, b, a);
        buffer.vertex(matrix, s, s, -s).color(r, g, b, a);
        
        // Верхняя грань
        buffer.vertex(matrix, -s, s, s).color(r, g, b, a);
        buffer.vertex(matrix, s, s, s).color(r, g, b, a);
        buffer.vertex(matrix, s, s, -s).color(r, g, b, a);
        buffer.vertex(matrix, -s, s, -s).color(r, g, b, a);
        
        // Нижняя грань
        buffer.vertex(matrix, -s, -s, -s).color(r, g, b, a);
        buffer.vertex(matrix, s, -s, -s).color(r, g, b, a);
        buffer.vertex(matrix, s, -s, s).color(r, g, b, a);
        buffer.vertex(matrix, -s, -s, s).color(r, g, b, a);
        
        // Правая грань
        buffer.vertex(matrix, s, -s, s).color(r, g, b, a);
        buffer.vertex(matrix, s, -s, -s).color(r, g, b, a);
        buffer.vertex(matrix, s, s, -s).color(r, g, b, a);
        buffer.vertex(matrix, s, s, s).color(r, g, b, a);
        
        // Левая грань
        buffer.vertex(matrix, -s, -s, -s).color(r, g, b, a);
        buffer.vertex(matrix, -s, -s, s).color(r, g, b, a);
        buffer.vertex(matrix, -s, s, s).color(r, g, b, a);
        buffer.vertex(matrix, -s, s, -s).color(r, g, b, a);
    }
    
    private void renderOctahedron(MatrixStack matrices, VertexConsumerProvider.Immediate immediate, 
                                  int color, float size, boolean isGlow) {
        RenderLayer layer = isGlow ? 
            WorldRenderLayers.POSITION_COLOR_QUADS_NO_DEPTH_BLEND() : 
            WorldRenderLayers.POSITION_COLOR_QUADS_NO_DEPTH();
        VertexConsumer buffer = immediate.getBuffer(layer);
        Matrix4f matrix = matrices.peek().getPositionMatrix();
        
        int r = (color >> 16) & 0xFF;
        int g = (color >> 8) & 0xFF;
        int b = color & 0xFF;
        int a = (color >> 24) & 0xFF;
        
        float s = size;
        
        // Вершины октаэдра
        float[][] vertices = {
            {0, s, 0},      // Верх
            {s, 0, 0},      // Право
            {0, 0, s},      // Перед
            {-s, 0, 0},     // Лево
            {0, 0, -s},     // Зад
            {0, -s, 0}      // Низ
        };
        
        // Грани октаэдра (треугольники, но рисуем как квады дублируя вершины)
        int[][] faces = {
            {0, 1, 2, 2}, {0, 2, 3, 3}, {0, 3, 4, 4}, {0, 4, 1, 1},
            {5, 2, 1, 1}, {5, 3, 2, 2}, {5, 4, 3, 3}, {5, 1, 4, 4}
        };
        
        for (int[] face : faces) {
            buffer.vertex(matrix, vertices[face[0]][0], vertices[face[0]][1], vertices[face[0]][2]).color(r, g, b, a);
            buffer.vertex(matrix, vertices[face[1]][0], vertices[face[1]][1], vertices[face[1]][2]).color(r, g, b, a);
            buffer.vertex(matrix, vertices[face[2]][0], vertices[face[2]][1], vertices[face[2]][2]).color(r, g, b, a);
            buffer.vertex(matrix, vertices[face[3]][0], vertices[face[3]][1], vertices[face[3]][2]).color(r, g, b, a);
        }
    }
    
    private void renderIcosahedron(MatrixStack matrices, VertexConsumerProvider.Immediate immediate, 
                                   int color, float size, boolean isGlow) {
        RenderLayer layer = isGlow ? 
            WorldRenderLayers.POSITION_COLOR_QUADS_NO_DEPTH_BLEND() : 
            WorldRenderLayers.POSITION_COLOR_QUADS_NO_DEPTH();
        VertexConsumer buffer = immediate.getBuffer(layer);
        Matrix4f matrix = matrices.peek().getPositionMatrix();
        
        int r = (color >> 16) & 0xFF;
        int g = (color >> 8) & 0xFF;
        int b = color & 0xFF;
        int a = (color >> 24) & 0xFF;
        
        float t = (float)((1.0 + Math.sqrt(5.0)) / 2.0); // Золотое сечение
        float s = size * 0.5F;
        
        // Вершины икосаэдра (12 вершин)
        float[][] vertices = {
            {-s, t*s, 0}, {s, t*s, 0}, {-s, -t*s, 0}, {s, -t*s, 0},
            {0, -s, t*s}, {0, s, t*s}, {0, -s, -t*s}, {0, s, -t*s},
            {t*s, 0, -s}, {t*s, 0, s}, {-t*s, 0, -s}, {-t*s, 0, s}
        };
        
        // Грани икосаэдра (20 треугольников, рисуем как квады дублируя последнюю вершину)
        int[][] faces = {
            {0, 11, 5, 5}, {0, 5, 1, 1}, {0, 1, 7, 7}, {0, 7, 10, 10}, {0, 10, 11, 11},
            {1, 5, 9, 9}, {5, 11, 4, 4}, {11, 10, 2, 2}, {10, 7, 6, 6}, {7, 1, 8, 8},
            {3, 9, 4, 4}, {3, 4, 2, 2}, {3, 2, 6, 6}, {3, 6, 8, 8}, {3, 8, 9, 9},
            {4, 9, 5, 5}, {2, 4, 11, 11}, {6, 2, 10, 10}, {8, 6, 7, 7}, {9, 8, 1, 1}
        };
        
        for (int[] face : faces) {
            buffer.vertex(matrix, vertices[face[0]][0], vertices[face[0]][1], vertices[face[0]][2]).color(r, g, b, a);
            buffer.vertex(matrix, vertices[face[1]][0], vertices[face[1]][1], vertices[face[1]][2]).color(r, g, b, a);
            buffer.vertex(matrix, vertices[face[2]][0], vertices[face[2]][1], vertices[face[2]][2]).color(r, g, b, a);
            buffer.vertex(matrix, vertices[face[3]][0], vertices[face[3]][1], vertices[face[3]][2]).color(r, g, b, a);
        }
    }
}
