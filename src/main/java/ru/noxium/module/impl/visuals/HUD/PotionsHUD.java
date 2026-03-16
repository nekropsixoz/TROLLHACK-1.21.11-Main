package ru.noxium.module.impl.visuals.HUD;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.texture.AbstractTexture;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.util.Identifier;
import net.minecraft.util.Language;
import ru.noxium.module.impl.visuals.Hud;
import ru.noxium.ui.draggable.DraggableManager;
import ru.noxium.util.color.ColorUtil;
import ru.noxium.util.keyboard.ScaledResolution;
import ru.noxium.util.render.animation.util.Animation;
import ru.noxium.util.render.animation.util.Easings;
import ru.noxium.util.render.core.Renderer2D;
import ru.noxium.util.render.text.FontRegistry;

@Environment(EnvType.CLIENT)
public class PotionsHUD {
      public static MinecraftClient mc = MinecraftClient.getInstance();
      private static final Animation widthAnimation = new Animation();
      private static final Animation heightAnimation = new Animation();
      public static Animation anim2 = new Animation();

      public static void potions(Renderer2D r2, DrawContext drawContext) {
            if (mc.player == null)
                  return;

            ScaledResolution sr = new ScaledResolution(mc);
            List<StatusEffectInstance> activeEffects = new ArrayList<>(mc.player.getStatusEffects());
            activeEffects.sort(Comparator.comparingInt(StatusEffectInstance::getDuration).reversed());

            anim2.update();
            boolean expand = !activeEffects.isEmpty();
            String headerName = "Potions";
            float headerTextWidth = r2.measureText(FontRegistry.INTER_MEDIUM, headerName, 28.0F).width;

            float minWidth = 46.0F + headerTextWidth + 10.0F;
            float calculatedWidth = minWidth;

            if (expand) {
                  for (StatusEffectInstance effect : activeEffects) {
                        String rawName = effect.getTranslationKey().replace("effect.minecraft.", "");
                        String effectName = Language.getInstance().get(effect.getTranslationKey());
                        if (effect.getAmplifier() > 0) {
                              effectName += " " + (effect.getAmplifier() + 1);
                        }
                        String durationText = formatDuration(effect.getDuration());

                        float nameWidth = r2.measureText(FontRegistry.INTER_MEDIUM, effectName, 28.0F).width;
                        float durationWidth = r2.measureText(FontRegistry.INTER_MEDIUM, durationText, 28.0F).width;

                        float totalWidth = 10.0F + nameWidth + 10.0F + durationWidth + 5.0F + 1.0F + 5.0F + 12.0F
                                    + 10.0F;
                        calculatedWidth = Math.max(calculatedWidth, totalWidth);
                  }
            }

            calculatedWidth = Math.max(calculatedWidth, minWidth);
            float headerHeight = 16.0F;
            float calculatedHeight = headerHeight;
            if (expand) {
                  calculatedHeight = 50.0F + headerHeight + (activeEffects.size() * 20.0F) + 5.0F;
            }

            calculatedHeight = Math.max(calculatedHeight, 55.0F);

            boolean isEmpty = activeEffects.isEmpty();
            boolean closeCondition = isEmpty && !(mc.currentScreen instanceof ChatScreen);
            anim2.run(closeCondition ? 0.0 : 1.0, 0.15F, Easings.QUAD_OUT);

            widthAnimation.update();
            heightAnimation.update();
            widthAnimation.run(calculatedWidth, 0.15, Easings.QUART_OUT);
            heightAnimation.run(calculatedHeight, 0.15, Easings.QUART_OUT);

            float animatedWidth = widthAnimation.get();
            float animatedHeight = heightAnimation.get();

            if (!closeCondition || anim2.get() != 0.0F) {
                  float preferredX = 20.0F;
                  float preferredY = 474.0F;
                  DraggableManager.DragSession session = DraggableManager.getInstance().beginDrag("potions", preferredX,
                              preferredY, animatedWidth, animatedHeight);
                  float x = session.positionX();
                  float y = session.positionY() - 40.0F + 40.0F * anim2.get();

                  Hud.drawClientRect(r2, x, y, animatedWidth, animatedHeight, 13.0F, 1.0F * anim2.get(), 1.0F);

                  r2.text(FontRegistry.ICONS, x + 10.0F, y + 30.0F, 36.0F, "f",
                              ColorUtil.replAlpha(0xA0D8FF, anim2.get()));

                  r2.shadow(
                              x + 14.0F,
                              y + 20.0F,
                              0.3F,
                              0.3F,
                              10.0F,
                              7.5F,
                              0.5F,
                              Renderer2D.ColorUtil.replAlpha(0xA0D8FF,
                                          (int) (75.0F * anim2.get())));

                  r2.rect(x + 35.0F, y + 15.5F, 1.0F, 11.0F,
                              Renderer2D.ColorUtil.replAlpha(0xA0D8FF,
                                          (int) (51.0F * anim2.get())));

                  r2.text(FontRegistry.INTER_MEDIUM, x + 42.0F, y + 26.0F, 28.0F, headerName,
                              ColorUtil.replAlpha(0xA0D8FF, anim2.get()));

                  r2.rect(x, y + 39.52F, animatedWidth, 1.0F,
                              Renderer2D.ColorUtil.replAlpha(0xA0D8FF,
                                          (int) (25.0F * anim2.get())));

                  if (expand) {
                        float offset = 0.0F;
                        for (StatusEffectInstance effect : activeEffects) {
                              String rawName = effect.getTranslationKey().replace("effect.minecraft.", "");
                              String effectName = Language.getInstance().get(effect.getTranslationKey());
                              if (effect.getAmplifier() > 0) {
                                    effectName += " " + (effect.getAmplifier() + 1);
                              }
                              String durationText = formatDuration(effect.getDuration());
                              float durationWidth = r2.measureText(FontRegistry.INTER_MEDIUM, durationText,
                                          28.0F).width;

                              int textColor = Renderer2D.ColorUtil.replAlpha(0xA0D8FF,
                                          (int) (anim2.get() * 255.0F));
                              int mainColor = Renderer2D.ColorUtil.replAlpha(0xA0D8FF,
                                          (int) (anim2.get() * 255.0F));

                              float addx = 40.0F - 40.0F * anim2.get();
                              float lineY = y + 50.0F + headerHeight + offset;

                              r2.text(FontRegistry.INTER_MEDIUM, x + 10.0F + addx, lineY, 28.0F,
                                          effectName, textColor);

                              float iconAreaWidth = 12.0F + 5.0F + 1.0F + 5.0F + 10.0F;

                              float durationX = x + animatedWidth - durationWidth - iconAreaWidth + addx;

                              r2.text(
                                          FontRegistry.INTER_MEDIUM, durationX,
                                          lineY, 28.0F, durationText, mainColor);

                              float separatorX = x + animatedWidth - 12.0F - 10.0F - 5.0F - 1.0F + addx;
                              r2.rect(separatorX, lineY - 8.0F, 1.0F, 10.0F,
                                          Renderer2D.ColorUtil.replAlpha(mainColor, (int) (51.0F * anim2.get())));

                              float iconX = x + animatedWidth - 12.0F - 10.0F + addx;
                              float iconY = lineY - 9.0F;

                              try {
                                    Identifier textureId = Identifier.of("minecraft",
                                                "textures/mob_effect/" + rawName + ".png");
                                    AbstractTexture texture = mc.getTextureManager().getTexture(textureId);

                                    if (texture != null) {
                                          int glId = -1;
                                          try {
                                                Method getGlId = texture.getClass().getMethod("getGlId");
                                                glId = (int) getGlId.invoke(texture);
                                          } catch (Exception e) {
                                                try {
                                                      Method getGlTexture = texture.getClass()
                                                                  .getMethod("getGlTexture");
                                                      Object internalTexture = getGlTexture.invoke(texture);
                                                      if (internalTexture != null) {
                                                            Method internalGetId = internalTexture.getClass()
                                                                        .getMethod("getGlId");
                                                            glId = (int) internalGetId.invoke(internalTexture);
                                                      }
                                                } catch (Exception ex) {
                                                }
                                          }

                                          if (glId > 0) {
                                                r2.pushAlpha(anim2.get());
                                                r2.drawRgbaTexture(glId, iconX, iconY, 12.0F, 12.0F);
                                                r2.popAlpha();
                                          } else {
                                                throw new Exception("No GL ID");
                                          }
                                    } else {
                                          throw new Exception("Texture not found");
                                    }
                              } catch (Throwable e) {
                                    int effectColor = effect.getEffectType().value().getColor();
                                    r2.rect(iconX, iconY + 1.0F, 12.0F, 12.0F, 4.0F,
                                                Renderer2D.ColorUtil.replAlpha(effectColor | 0xFF000000,
                                                            (int) (255.0F * anim2.get())));
                              }

                              offset += 20.0F;
                        }
                  }
                  DraggableManager.getInstance().endDrag(session);
            }
      }

      private static String formatDuration(int ticks) {
            int seconds = ticks / 20;
            int minutes = seconds / 60;
            int remainingSeconds = seconds % 60;
            return String.format("%02d:%02d", minutes, remainingSeconds);
      }
}
