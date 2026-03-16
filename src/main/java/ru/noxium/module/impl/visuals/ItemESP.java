package ru.noxium.module.impl.visuals;

import java.util.Map;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.util.math.Vec3d;
import ru.noxium.event.EventInit;
import ru.noxium.event.render.EventRender3D;
import ru.noxium.module.api.Category;
import ru.noxium.module.api.IModule;
import ru.noxium.module.api.Module;

@IModule(name = "Item ESP", description = " ", category = Category.Visuals, bind = -1)
@Environment(EnvType.CLIENT)
public class ItemESP extends Module {
      private final Map<ItemEntity, Vec3d> itemPositions = new java.util.HashMap<>();

      @EventInit
      public void onRender3D(EventRender3D event) {
            itemPositions.clear();
            if (mc.world != null) {
                  for (Entity entity : mc.world.getEntities()) {
                        if (entity instanceof ItemEntity itemEntity) {
                              double x = net.minecraft.util.math.MathHelper.lerp(event.getTickDelta(),
                                          itemEntity.lastRenderX, itemEntity.getX());
                              double y = net.minecraft.util.math.MathHelper.lerp(event.getTickDelta(),
                                          itemEntity.lastRenderY, itemEntity.getY());
                              double z = net.minecraft.util.math.MathHelper.lerp(event.getTickDelta(),
                                          itemEntity.lastRenderZ, itemEntity.getZ());

                              // Adjust Y to be above the item
                              Vec3d pos = new Vec3d(x, y + 0.75, z);
                              Vec3d screenPos = ru.noxium.util.other.Mathf.worldSpaceToScreenSpace(pos);

                              if (screenPos != null && screenPos.z >= 0.0 && screenPos.z <= 1.0) {
                                    itemPositions.put(itemEntity, screenPos);
                              }
                        }
                  }
            }
      }

      @EventInit
      public void onRender2D(ru.noxium.event.impl.EventScreen event) {
            if (!itemPositions.isEmpty()) {
                  ru.noxium.util.render.core.Renderer2D renderer = event.renderer();
                  Vec3d cameraPos = mc.gameRenderer.getCamera().getCameraPos();

                  for (java.util.Map.Entry<ItemEntity, Vec3d> entry : itemPositions.entrySet()) {
                        ItemEntity entity = entry.getKey();
                        Vec3d screenPos = entry.getValue();

                        String name = entity.getStack().getName().getString();
                        int count = entity.getStack().getCount();
                        String text = name + (count > 1 ? " x" + count : "");

                        double distance = cameraPos.distanceTo(new Vec3d(entity.getX(), entity.getY(), entity.getZ()));
                        float scale = (float) (5.0 / Math.max(distance, 5.0));
                        scale = net.minecraft.util.math.MathHelper.clamp(scale, 0.5f, 1.5f);

                        float fontSize = 16.0f;
                        float fontHeight = fontSize;
                        float width = renderer.measureText(ru.noxium.util.render.text.FontRegistry.INTER_MEDIUM, text,
                                    fontSize).width;
                        float padding = 6.0f;

                        renderer.pushTranslation((float) screenPos.x * 2.0f, (float) screenPos.y * 2.0f);
                        renderer.pushScale(scale, scale);

                        // Draw background like in NameTags
                        float rectWidth = width + padding * 2.0f;
                        float rectHeight = fontHeight + 8.0f;
                        Hud.drawClientRect(renderer, -rectWidth / 2.0f, -rectHeight / 2.0f, rectWidth, rectHeight, 5.0f,
                                    1.0f, 1.0f);

                        // Center text
                        renderer.text(ru.noxium.util.render.text.FontRegistry.INTER_MEDIUM, -width / 2.0f,
                                    -fontSize / 4.0f, fontSize, text, -1);

                        renderer.popScale();
                        renderer.popTransform();
                  }
            }
      }
}
