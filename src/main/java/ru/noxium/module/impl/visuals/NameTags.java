package ru.noxium.module.impl.visuals;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.Vec3d;
import net.minecraft.text.Text;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.util.math.MathHelper;
import ru.noxium.Noxium;
import ru.noxium.event.EventInit;
import ru.noxium.event.impl.EventChangeWorld;
import ru.noxium.event.impl.EventScreen;
import ru.noxium.event.render.EventRender3D;
import ru.noxium.module.api.Category;
import ru.noxium.module.api.IModule;
import ru.noxium.module.api.Module;
import ru.noxium.module.api.setting.Setting;
import ru.noxium.module.api.setting.impl.BooleanSetting;
import ru.noxium.module.api.setting.impl.MultiBooleanSetting;
import ru.noxium.util.other.Mathf;
import ru.noxium.util.render.core.Renderer2D;
import ru.noxium.util.render.text.FontRegistry;

@IModule(name = "NameTags", description = "Отображает теги над сущностями", category = Category.Visuals, bind = -1)
@Environment(EnvType.CLIENT)
public class NameTags extends Module {
   public static Map<PlayerEntity, double[]> entityPositions = new HashMap<>();
   public static Map<ItemEntity, double[]> itemPositions = new HashMap<>();
   private static final List<NameTags.PendingItemRender> pendingItems = new ArrayList<>();
   public MultiBooleanSetting entityType = new MultiBooleanSetting(
         "Отображать", new BooleanSetting("Player", true), new BooleanSetting("Mobs", false),
         new BooleanSetting("Item", true));
   public BooleanSetting armor = new BooleanSetting("Отображать броню", true)
         .hidden(() -> !this.entityType.get("Player"));
   public BooleanSetting showMainHand = new BooleanSetting("Правая рука", true)
         .hidden(() -> !this.entityType.get("Player"));
   public BooleanSetting showOffHand = new BooleanSetting("Левая рука", true)
         .hidden(() -> !this.entityType.get("Player"));

   public NameTags() {
      this.addSettings(new Setting[] { this.entityType, this.armor, this.showMainHand, this.showOffHand });
   }

   @EventInit
   public void onWorldLoad(EventChangeWorld e) {
      entityPositions.clear();
      itemPositions.clear();
   }

   @EventInit
   public void onRender3D(EventRender3D event) {
      this.updatePositions(event.getTickDelta());
   }

   private void updatePositions(float tickDelta) {
      entityPositions.clear();
      if (mc.world != null && mc.player != null) {
         for (PlayerEntity p : mc.world.getPlayers()) {
            if (p != mc.player) {
               double x = MathHelper.lerp(tickDelta, p.lastRenderX, p.getX());
               double y = MathHelper.lerp(tickDelta, p.lastRenderY, p.getY());
               double z = MathHelper.lerp(tickDelta, p.lastRenderZ, p.getZ());
               Vec3d headPos = new Vec3d(x, y + p.getHeight() + 0.35, z);
               Vec3d legsPos = new Vec3d(x, y - 0.35, z);
               Vec3d headScreen = Mathf.worldSpaceToScreenSpace(headPos);
               Vec3d legsScreen = Mathf.worldSpaceToScreenSpace(legsPos);
               if (headScreen != null
                     && legsScreen != null
                     && headScreen.z >= 0.0
                     && headScreen.z <= 1.0
                     && legsScreen.z >= 0.0
                     && legsScreen.z <= 1.0) {
                  entityPositions.put(
                        p,
                        new double[] {
                              headScreen.x,
                              headScreen.y,
                              headScreen.z,
                              legsScreen.x,
                              legsScreen.y,
                              legsScreen.z
                        });
               }
            }
         }
      }
   }

   @EventInit
   public void onRender2D(EventScreen event) {
      if (mc.world != null && mc.player != null) {
         Renderer2D renderer = event.renderer();
         DrawContext drawContext = event.drawContext();
         Vec3d cameraPos = mc.gameRenderer.getCamera().getCameraPos();

         for (PlayerEntity entity : entityPositions.keySet()) {
            if (entity != mc.player) {
               double[] pos = entityPositions.get(entity);
               if (pos != null && !(pos[2] < 0.0) && !(pos[2] > 1.0)) {
                  Vec3d entityPos = new Vec3d(entity.getX(), entity.getY(), entity.getZ());
                  double distance = cameraPos.distanceTo(entityPos);
                  double baseDistance = 5.0;
                  float scale = (float) (baseDistance / Math.max(distance, 5.0));
                  scale = MathHelper.clamp(scale, 0.5F, 2.0F);
                  int color = Noxium.get.friendManager.isFriend(entity.getName().getString())
                        ? Renderer2D.ColorUtil.getColor(40, 255, 40, 150)
                        : Renderer2D.ColorUtil.getColor(0, 0, 0, 150);
                  renderer.pushTranslation((float) pos[0] * 2.0F, (float) pos[1] * 2.0F);
                  renderer.pushScale(scale, scale);
                  String name = entity.getName().getString();
                  float hp = entity.getHealth();
                  String hpText = "[" + String.format("%.1f", hp) + "]";
                  boolean isFriend = Noxium.get.friendManager.isFriend(name);
                  String friendTag = isFriend ? "[F] " : "";
                  float fontSize = 28.0F;
                  float friendTagWidth = isFriend
                        ? renderer.measureText(FontRegistry.INTER_MEDIUM, friendTag, fontSize).width
                        : 0.0F;
                  float nameWidth = renderer.measureText(FontRegistry.INTER_MEDIUM, name, fontSize).width;
                  float spaceWidth = renderer.measureText(FontRegistry.INTER_MEDIUM, " ", fontSize).width;
                  float hpWidth = renderer.measureText(FontRegistry.INTER_MEDIUM, hpText, fontSize).width;
                  float totalWidth = friendTagWidth + nameWidth + spaceWidth + hpWidth;
                  float width = totalWidth + 12.0F;
                  Hud.drawClientRect(renderer, -width / 2.0F - 2.0F, -12.0F, width + 4.0F, 24.0F, 7.0F, 1.0F, 1.0F);
                  float textX = -width / 2.0F + 6.0F;
                  if (isFriend) {
                     renderer.text(FontRegistry.INTER_MEDIUM, textX, 4.75F, fontSize, "[F]",
                           Renderer2D.ColorUtil.getColor(40, 255, 40, 255));
                     textX += friendTagWidth;
                  }

                  renderer.text(FontRegistry.INTER_MEDIUM, textX, 4.75F, fontSize, name,
                        Renderer2D.ColorUtil.getTextColor(1, 1));
                  textX += nameWidth + spaceWidth;
                  renderer.text(FontRegistry.INTER_MEDIUM, textX, 4.75F, fontSize, hpText,
                        Renderer2D.ColorUtil.getMainColor(1, 1));
                  renderer.popScale();
                  renderer.popTransform();
                  if (this.armor.get()) {
                     renderer.pushTranslation((float) pos[0] * 2.0F, (float) pos[1] * 2.0F);
                     renderer.pushScale(scale, scale);
                     float nameRectWidth = width + 4.0F;
                     this.drawArmor(renderer, drawContext, entity, (float) pos[0] * 2.0F, (float) pos[1] * 2.0F, scale,
                           nameRectWidth);
                     renderer.popScale();
                     renderer.popTransform();
                  }

                  renderer.pushTranslation((float) pos[3] * 2.0F, (float) pos[4] * 2.0F);
                  renderer.pushScale(scale, scale);
                  this.drawPlayerItems(renderer, entity, 0.0F, -12.0F, 14.0F, color);
                  renderer.popScale();
                  renderer.popTransform();
               }
            }
         }

         this.renderPendingItems(drawContext);
      }
   }

   private void drawArmor(Renderer2D renderer, DrawContext drawContext, PlayerEntity player, float baseX, float baseY,
         float scale, float nameRectWidth) {
      if (player.getArmor() != 0) {
         EquipmentSlot[] slots = new EquipmentSlot[] { EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS,
               EquipmentSlot.FEET };
         float slotSize = 12.0F;
         float slotSpacing = 12.0F;
         int slotCount = 4;
         float distanceBetweenCenters = (slotCount - 1) * slotSpacing;
         float firstSlotCenterX = -distanceBetweenCenters / 2.0F;
         float armorY = -28.0F;
         float guiScale = mc.getWindow().getScaleFactor();

         for (int i = 0; i < slotCount; i++) {
            float slotCenterX = firstSlotCenterX + i * slotSpacing;
            float slotX = slotCenterX - slotSize / 2.0F;
            int slotCol = Renderer2D.ColorUtil.replAlpha(Renderer2D.ColorUtil.getMainColor(1, 1), 40);
            ItemStack stack = player.getEquippedStack(slots[i]);
            if (!stack.isEmpty()) {
               float centerY = armorY + slotSize / 2.0F;
               float screenX = (baseX / 2.0F + slotCenterX * scale) / guiScale * 2.0F;
               float screenY = (baseY / 2.0F + centerY * scale) / guiScale * 2.0F;
               pendingItems.add(new NameTags.PendingItemRender(player, stack, screenX, screenY, i, 0.5F));
            }
         }
      }
   }

   private float transformX(float[] matrix, float px, float py) {
      return matrix != null && matrix.length >= 9 ? matrix[0] * px + matrix[1] * py + matrix[2] : px;
   }

   private float transformY(float[] matrix, float px, float py) {
      return matrix != null && matrix.length >= 9 ? matrix[3] * px + matrix[4] * py + matrix[5] : py;
   }

   private void renderPendingItems(DrawContext drawContext) {
      if (!pendingItems.isEmpty()) {
         for (NameTags.PendingItemRender item : pendingItems) {
            drawContext.getMatrices().pushMatrix();
            drawContext.getMatrices().translate(item.x, item.y);
            drawContext.getMatrices().scale(item.scale, item.scale);
            drawContext.drawItem(item.player, item.stack, -4, -4, item.seed);
            drawContext.getMatrices().popMatrix();
         }

         pendingItems.clear();
      }
   }

   private void drawPlayerItems(Renderer2D renderer, PlayerEntity player, float centerX, float startY, float fontHeight,
         int color) {
      List<String> items = new ArrayList<>();
      if (this.showMainHand.get() && !player.getMainHandStack().isEmpty()) {
         ItemStack stack = player.getMainHandStack();
         String name = stack.getName().getString();
         if (stack.getCount() > 1) {
            name += " x" + stack.getCount();
         }
         items.add(name);
      }

      if (this.showOffHand.get() && !player.getOffHandStack().isEmpty()) {
         ItemStack stack = player.getOffHandStack();
         String name = stack.getName().getString();
         if (stack.getCount() > 1) {
            name += " x" + stack.getCount();
         }
         items.add(name);
      }

      float y = startY;

      for (String item : items) {
         float w = renderer.measureText(FontRegistry.INTER_MEDIUM, item, fontHeight * 2.0F).width + 12.0F;
         Hud.drawClientRect(renderer, -w / 2.0F, y - 1.0F, w, fontHeight + 10.0F, 7.0F, 1.0F, 1.0F);
         renderer.text(FontRegistry.INTER_MEDIUM, -w / 2.0F + 6.0F, y + 15.0F, fontHeight * 2.0F, item,
               Renderer2D.ColorUtil.getTextColor(1, 1));
         y += fontHeight + 12.0F;
      }
   }

   @Environment(EnvType.CLIENT)
   private static class PendingItemRender {
      PlayerEntity player;
      ItemStack stack;
      float x;
      float y;
      int seed;
      float scale;

      PendingItemRender(PlayerEntity player, ItemStack stack, float x, float y, int seed, float scale) {
         this.player = player;
         this.stack = stack;
         this.x = x;
         this.y = y;
         this.seed = seed;
         this.scale = scale;
      }
   }
}
