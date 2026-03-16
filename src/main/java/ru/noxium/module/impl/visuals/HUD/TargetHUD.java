package ru.noxium.module.impl.visuals.HUD;

import com.mojang.blaze3d.opengl.GlStateManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.client.texture.AbstractTexture;
import net.minecraft.client.texture.GlTexture;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Items;
import net.minecraft.item.Item;
import net.minecraft.util.Identifier;
import ru.noxium.module.impl.combat.HitAura;
import ru.noxium.module.impl.visuals.Hud;
import ru.noxium.ui.draggable.DraggableManager;
import ru.noxium.util.render.animation.util.Animation;
import ru.noxium.util.render.animation.util.Easings;
import ru.noxium.util.render.core.Renderer2D;
import ru.noxium.util.render.math.ScaledResolution;
import ru.noxium.util.render.math.animation.AnimationMath;
import ru.noxium.util.render.text.FontRegistry;

import java.awt.*;

@Environment(EnvType.CLIENT)
public class TargetHUD {

   private static final MinecraftClient mc = MinecraftClient.getInstance();
   private static final Animation openAnim = new Animation();
   private static float animatedHpWidth = 0;

   public static void targetHUD(Renderer2D r2, DrawContext ctx) {
      Entity target = HitAura.target;
      boolean chat = mc.currentScreen instanceof ChatScreen;

      boolean show = target != null || (chat && mc.player != null);
      if (target == null && chat) target = mc.player;

      openAnim.update();
      openAnim.run(show ? 2 : 0, 0.6f, Easings.QUAD_OUT);
      float alpha = openAnim.get();

      if (alpha < 0.01f || !(target instanceof LivingEntity living)) {
         animatedHpWidth = 0;
         return;
      }

      float width = 170;
      float height = 56;

      ScaledResolution sr = new ScaledResolution(mc);
      DraggableManager.DragSession drag = DraggableManager.getInstance()
              .beginDrag("targethud",
                      (sr.getWidth() - width) / 2f,
                      (sr.getHeight() - height) / 2f,
                      width,
                      height);

      float x = drag.positionX();
      float y = drag.positionY();

      // ФОН с улучшенным дизайном
      Hud.drawClientRect(r2, x, y, width, height, 8f, alpha, 2f);

      // ГОЛОВА
      if (living instanceof PlayerEntity player && mc.getNetworkHandler() != null) {
         PlayerListEntry entry = mc.getNetworkHandler().getPlayerListEntry(player.getUuid());
         if (entry != null) {
            drawHead(r2, entry.getSkinTextures().body().id(), x + 8, y + 8, 40, alpha);
         }
      }

      // ОСНОВНАЯ ИНФОРМАЦИЯ
      drawMainInfo(r2, living, x, y, alpha);

      DraggableManager.getInstance().endDrag(drag);
   }

   private static void drawMainInfo(Renderer2D r2, LivingEntity living, float x, float y, float alpha) {
      r2.pushAlpha(alpha);
      
      // ИМЯ - позиция справа от головы
      String name = living.getName().getString();
      r2.text(FontRegistry.INTER_MEDIUM, x + 55, y + 12, 20, name, -1);

      float hp = living.getHealth();
      float maxHp = living.getMaxHealth();
      float abs = living.getAbsorptionAmount();

      // HP ТЕКСТ - под именем
      String hpText = String.format("%.0f HP", hp + abs);
      r2.text(FontRegistry.INTER_MEDIUM, x + 56, y + 27, 13, hpText, new Color(165, 160, 160).getRGB());

      // HP BAR - светло-голубой как на скриншоте
      float barX = x + 55;
      float barY = y + 44;
      float barW = 95;
      float barH = 4;

      float targetW = Math.min((hp + abs) / maxHp * barW, barW);
      animatedHpWidth = AnimationMath.animation(animatedHpWidth, targetW, 0.25f);

      // Фон HP бара
      r2.rect(barX, barY, barW, barH, 2f, new Color(40, 40, 40, 200).getRGB());
      
      // Светло-голубой HP бар как на скриншоте
      r2.rect(barX, barY, animatedHpWidth, barH, 2f, new Color(100, 200, 255).getRGB());

      r2.popAlpha();
   }

   // ===== ГОЛОВА ИГРОКА =====
   private static void drawHead(Renderer2D r2, Identifier skin, float x, float y, float size, float alpha) {
      try {
         TextureManager tm = mc.getTextureManager();
         AbstractTexture tex = tm.getTexture(skin);
         if (!(tex.getGlTexture() instanceof GlTexture gl)) return;

         int id = gl.getGlId();
         if (id <= 0) return;

         GlStateManager._bindTexture(id);
         r2.pushAlpha(alpha);
         r2.drawRgbaTextureWithUVRounded(id, x, y, size, size,
                 0.125f, 0.125f, 0.25f, 0.25f, 4);
         r2.drawRgbaTextureWithUVRounded(id, x, y, size, size,
                 0.625f, 0.125f, 0.75f, 0.25f, 4);
         r2.popAlpha();
      } catch (Exception ignored) {}
   }

   public static void renderPendingItems(DrawContext drawContext) {
      // Empty implementation - no pending items in new TargetHUD
   }
}
