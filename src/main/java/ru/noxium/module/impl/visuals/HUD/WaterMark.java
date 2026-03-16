package ru.noxium.module.impl.visuals.HUD;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import org.lwjgl.glfw.GLFW;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.client.texture.AbstractTexture;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.util.Identifier;
import ru.noxium.api.UserData;
import ru.noxium.util.render.core.Renderer2D;
import ru.noxium.util.render.text.FontRegistry;
import ru.noxium.util.render.animation.util.Easings;

@Environment(EnvType.CLIENT)
public class WaterMark {
      public static MinecraftClient mc = MinecraftClient.getInstance();

      private enum ElementType {
            LOGO,
            LOGIN,
            FPS,
            SERVER,
            PING
      }

      // Context Menu State
      private static boolean menuOpen = false;
      private static final ru.noxium.util.render.animation.util.Animation menuAnimation = new ru.noxium.util.render.animation.util.Animation();
      private static float menuX = 0, menuY = 0;
      private static boolean wasRightMouseDown = false;

      // This list defines the current order
      private static final List<ElementType> elements = new ArrayList<>(Arrays.asList(
                  ElementType.LOGO,
                  ElementType.LOGIN,
                  ElementType.SERVER,
                  ElementType.PING,
                  ElementType.FPS));

      // Visual State
      private static final Map<ElementType, Float> currentXPositions = new EnumMap<>(ElementType.class);
      private static float currentBgWidth = 0.0F;
      private static long lastFrameTime = System.currentTimeMillis();

      // Drag Interaction
      private static ElementType draggingElement = null; // The element currently being dragged
      private static float dragOffset = 0.0F; // Offset from the element's distinct X to the mouse X
      private static boolean wasMouseDown = false;
      private static final float ANIMATION_SPEED = 0.35F; // Responsiveness of the re-layout animation

      public static void waterMark(Renderer2D r2) {
            // --- 1. Update Delta Time ---
            long now = System.currentTimeMillis();
            float delta = Math.min((now - lastFrameTime) / 20.0F, 1.0F);
            lastFrameTime = now;

            // --- 2. Check Settings ---
            boolean showPing = ru.noxium.module.impl.visuals.Hud.waterMarkElements.get("Пинг");
            boolean showServer = ru.noxium.module.impl.visuals.Hud.waterMarkElements.get("Сервер");
            boolean showLogin = ru.noxium.module.impl.visuals.Hud.waterMarkElements.get("Логин");
            boolean showFps = ru.noxium.module.impl.visuals.Hud.waterMarkElements.get("ФПС");
            boolean useFullIp = ru.noxium.module.impl.visuals.Hud.fullIp.get();

            // --- 3. Gather Data & Measurements ---
            float startX = 20.0F;
            float startY = 20.0F;
            float height = 40.64F;

            // Prepare texts
            String serverText = "ST";
            if (mc.getCurrentServerEntry() != null) {
                  if (useFullIp) {
                        serverText = mc.getCurrentServerEntry().address;
                  } else {
                        String ip = mc.getCurrentServerEntry().address.toLowerCase();
                        if (ip.contains("spookytime"))
                              serverText = "ST";
                        else
                              serverText = "Server";
                  }
            } else {
                  serverText = "Singleplayer";
            }

            String pingValue = "0";
            if (mc.getNetworkHandler() != null && mc.player != null) {
                  PlayerListEntry entry = mc.getNetworkHandler().getPlayerListEntry(mc.player.getUuid());
                  if (entry != null)
                        pingValue = String.valueOf(entry.getLatency());
            }

            String fpsValue = String.valueOf(mc.getCurrentFps());
            String playerName = UserData.LOGIN;
            String loginIcon = "g"; // User Icon
            String serverIcon = "1";
            String pingIcon = "0";
            String fpsIcon = "2"; // Trying character '2' for the performance icon

            // Layout Constants
            float paddingX = 10.0F;
            float iconGap = 6.0F;
            float separatorWidth = 1.0F;
            float separatorGap = 8.0F;
            float sepTotalW = separatorGap + separatorWidth + separatorGap;
            float headSize = 32.0F;
            float spaceW = r2.measureText(FontRegistry.INTER_SEMIBOLD, " ", 28.0F).width; // " "

            // Measure widths of ALL potential elements
            Map<ElementType, Float> widths = new EnumMap<>(ElementType.class);
            widths.put(ElementType.LOGO, r2.measureText(FontRegistry.INTER_SEMIBOLD, "TROLL", 32.0F).width);

            float lIconW = r2.measureText(FontRegistry.ICONS, loginIcon, 32.0F).width;
            float lTextW = r2.measureText(FontRegistry.INTER_SEMIBOLD, playerName, 28.0F).width;
            widths.put(ElementType.LOGIN, lIconW + iconGap + lTextW);

            float sIconW = r2.measureText(FontRegistry.ICONS, serverIcon, 32.0F).width;
            float sTextW = r2.measureText(FontRegistry.INTER_SEMIBOLD, serverText, 28.0F).width;
            widths.put(ElementType.SERVER, sIconW + iconGap + sTextW);

            float pIconW = r2.measureText(FontRegistry.ICONS, pingIcon, 32.0F).width;
            float pTextW = r2.measureText(FontRegistry.INTER_SEMIBOLD, pingValue, 28.0F).width;
            float msW = r2.measureText(FontRegistry.INTER_SEMIBOLD, "ms", 28.0F).width;
            widths.put(ElementType.PING, pIconW + iconGap + pTextW + spaceW + msW);

            float fIconW = r2.measureText(FontRegistry.ICONS, fpsIcon, 32.0F).width;
            float fTextW = r2.measureText(FontRegistry.INTER_SEMIBOLD, fpsValue, 28.0F).width;
            float fpsWText = r2.measureText(FontRegistry.INTER_SEMIBOLD, "fps", 28.0F).width;
            widths.put(ElementType.FPS, fIconW + iconGap + fTextW + spaceW + fpsWText);

            // --- 4. Input Handling ---
            boolean mouseDown = GLFW.glfwGetMouseButton(mc.getWindow().getHandle(),
                        GLFW.GLFW_MOUSE_BUTTON_LEFT) == GLFW.GLFW_PRESS;
            boolean rightMouseDown = GLFW.glfwGetMouseButton(mc.getWindow().getHandle(),
                        GLFW.GLFW_MOUSE_BUTTON_RIGHT) == GLFW.GLFW_PRESS;
            double mouseX = mc.mouse.getX();
            double mouseY = mc.mouse.getY();

            // Right Click Logic (Open Menu)
            if (rightMouseDown && !wasRightMouseDown) {
                  if (mouseX >= startX && mouseX <= startX + currentBgWidth && mouseY >= startY
                              && mouseY <= startY + height) {
                        menuOpen = !menuOpen;
                        if (menuOpen) {
                              menuX = (float) mouseX;
                              menuY = (float) mouseY;
                        }
                  }
            }
            wasRightMouseDown = rightMouseDown;

            // Drag Start
            if (mouseDown && !wasMouseDown) {
                  // If menu is open and we click, let's handle setting toggles
                  if (menuOpen) {
                        float mAlpha = menuAnimation.get();
                        if (mAlpha > 0.5F) {
                              float mWidth = 150.0F; // Increased width
                              float itemH = 30.0F; // Increased item height
                              float mHeight = itemH * 5 + 10.0F; // Dynamic height for 5 items
                              if (mouseX >= menuX && mouseX <= menuX + mWidth && mouseY >= menuY
                                          && mouseY <= menuY + mHeight) {
                                    float relativeY = (float) mouseY - (menuY + 5.0F);
                                    if (relativeY >= 0 && relativeY < itemH) {
                                          ru.noxium.module.impl.visuals.Hud.waterMarkElements.settings.get(0)
                                                      .set(!showPing);
                                    } else if (relativeY >= itemH && relativeY < itemH * 2) {
                                          ru.noxium.module.impl.visuals.Hud.waterMarkElements.settings.get(1)
                                                      .set(!showServer);
                                    } else if (relativeY >= itemH * 2 && relativeY < itemH * 3) {
                                          ru.noxium.module.impl.visuals.Hud.waterMarkElements.settings.get(2)
                                                      .set(!showFps);
                                    } else if (relativeY >= itemH * 3 && relativeY < itemH * 4) {
                                          ru.noxium.module.impl.visuals.Hud.waterMarkElements.settings.get(3)
                                                      .set(!showLogin);
                                    } else if (relativeY >= itemH * 4 && relativeY < itemH * 5) {
                                          ru.noxium.module.impl.visuals.Hud.fullIp.set(!useFullIp);
                                    }
                              } else {
                                    menuOpen = false;
                              }
                        }
                  } else {
                        // Check Hitboxes for dragging
                        for (ElementType type : elements) {
                              if (shouldHide(type, showPing, showServer, showLogin, showFps))
                                    continue;

                              Float currentX = currentXPositions.get(type);
                              if (currentX == null)
                                    continue;

                              float w = widths.get(type);
                              float hitX = currentX - (sepTotalW / 2.0F);
                              float hitW = w + sepTotalW;

                              if (mouseX >= hitX && mouseX <= hitX + hitW && mouseY >= startY
                                          && mouseY <= startY + height) {
                                    draggingElement = type;
                                    dragOffset = (float) mouseX - currentX;
                                    break;
                              }
                        }
                  }
            }

            // Drag End
            if (!mouseDown) {
                  draggingElement = null;
            }
            wasMouseDown = mouseDown;

            // --- 5. Logic: Reorder List Based on Drag ---
            if (draggingElement != null) {
                  float dragX = (float) mouseX - dragOffset;
                  float dragCenterX = dragX + widths.get(draggingElement) / 2.0F;

                  List<ElementType> others = new ArrayList<>(elements);
                  others.remove(draggingElement);

                  int bestIndex = 0;
                  float minDistance = Float.MAX_VALUE;
                  float cursor = startX + paddingX;

                  for (int i = 0; i <= others.size(); i++) {
                        float targetXForSlot = cursor;
                        float w_drag = widths.get(draggingElement);
                        float slotCenter = targetXForSlot + w_drag / 2.0F;
                        float dist = Math.abs(dragCenterX - slotCenter);

                        if (dist < minDistance) {
                              minDistance = dist;
                              bestIndex = i;
                        }

                        if (i < others.size()) {
                              ElementType other = others.get(i);
                              if (!shouldHide(other, showPing, showServer, showLogin, showFps)) {
                                    float w_other = widths.get(other);
                                    cursor += w_other + sepTotalW;
                              }
                        }
                  }
                  int newIndex = bestIndex;

                  elements.clear();
                  elements.addAll(others);
                  elements.add(newIndex, draggingElement);
            }

            // --- 6. Calculate Target Layout & Animate ---
            float layoutCursor = startX + paddingX;
            float totalLayoutWidth = paddingX * 2;

            for (int i = 0; i < elements.size(); i++) {
                  ElementType type = elements.get(i);

                  if (shouldHide(type, showPing, showServer, showLogin, showFps)) {
                        continue;
                  }

                  float w = widths.get(type);
                  float targetX = layoutCursor;

                  Float current = currentXPositions.get(type);
                  if (current == null)
                        current = targetX;

                  if (draggingElement == type) {
                        float visualDragX = (float) mouseX - dragOffset;
                        currentXPositions.put(type, visualDragX);
                  } else {
                        float nextVal = lerp(current, targetX, delta * ANIMATION_SPEED);
                        currentXPositions.put(type, nextVal);
                  }

                  layoutCursor += w;
                  totalLayoutWidth += w;

                  boolean hasNextVisible = false;
                  for (int j = i + 1; j < elements.size(); j++) {
                        if (!shouldHide(elements.get(j), showPing, showServer, showLogin, showFps)) {
                              hasNextVisible = true;
                              break;
                        }
                  }

                  if (hasNextVisible) {
                        layoutCursor += sepTotalW;
                        totalLayoutWidth += sepTotalW;
                  }
            }

            // Animate BG Width
            currentBgWidth = lerp(currentBgWidth, totalLayoutWidth, delta * ANIMATION_SPEED);

            // --- 7. Rendering ---

            int bgColor = Renderer2D.ColorUtil.rgba(18, 18, 18, (int)(80.0F));
            int outlineColor = Renderer2D.ColorUtil.rgba(33, 33, 33, 255);
            int textColor = Renderer2D.ColorUtil.rgba(160, 216, 255, 255);
            int textColorDim = Renderer2D.ColorUtil.rgba(160, 216, 255, 150);
            
            if (ru.noxium.module.impl.visuals.Hud.blur.get()) {
                  r2.prepareBlur(30.0F);
                  r2.blur(startX, startY, currentBgWidth, height, 10.0F, 1.0F);
            }
            
            r2.rect(startX, startY, currentBgWidth, height, 10.0F, bgColor);
            r2.rectOutline(startX, startY, currentBgWidth, height, 10.0F, outlineColor, 2.0F);

            // B. Elements
            List<ElementType> renderOrder = new ArrayList<>(elements);
            if (draggingElement != null) {
                  renderOrder.remove(draggingElement);
                  renderOrder.add(draggingElement);
            }

            for (ElementType type : renderOrder) {
                  if (shouldHide(type, showPing, showServer, showLogin, showFps))
                        continue;

                  Float xVal = currentXPositions.get(type);
                  if (xVal == null)
                        continue;

                  float cx = xVal;
                  float w = widths.get(type);

                  boolean hasNext = false;
                  int myIndex = elements.indexOf(type);
                  for (int k = myIndex + 1; k < elements.size(); k++) {
                        if (!shouldHide(elements.get(k), showPing, showServer, showLogin, showFps)) {
                              hasNext = true;
                              break;
                        }
                  }

                  if (hasNext) {
                        float sepX = cx + w + separatorGap;
                        int sepColor = Renderer2D.ColorUtil.rgba(33, 33, 33, 100);
                        r2.rect(sepX, startY + 12.0F, separatorWidth, 16.0F, sepColor);
                  }

                  if (draggingElement == type) {
                        r2.rect(cx - 2, startY + 2, w + 4, height - 4, 4.0F,
                                    new java.awt.Color(255, 255, 255, 50).getRGB());
                  } else if (draggingElement == null) {
                        float hitX = cx - (sepTotalW / 2.0F);
                        float hitW = w + sepTotalW;
                        boolean hov = (mouseX >= hitX && mouseX <= hitX + hitW && mouseY >= startY
                                    && mouseY <= startY + height);
                        if (hov) {
                              r2.rect(cx - 2, startY + 2, w + 4, height - 4, 4.0F,
                                          new java.awt.Color(255, 255, 255, 30).getRGB());
                        }
                  }

                  int menuTextColor = Renderer2D.ColorUtil.rgba(160, 216, 255, 255);
                  int mainColor = Renderer2D.ColorUtil.rgba(160, 216, 255, 200);

                  switch (type) {
                        case LOGO:
                              r2.text(FontRegistry.INTER_SEMIBOLD, cx + 2.0F, startY + 25.5F, 32.0F, "TROLL", menuTextColor);
                              break;
                        case LOGIN:
                              r2.text(FontRegistry.ICONS, cx, startY + 28.0F, 32.0F, loginIcon, mainColor);
                              r2.text(FontRegistry.INTER_SEMIBOLD, cx + lIconW + iconGap, startY + 25.5F, 28.0F,
                                          playerName, menuTextColor);
                              break;
                        case SERVER:
                              r2.text(FontRegistry.ICONS, cx, startY + 28.0F, 32.0F, serverIcon, mainColor);
                              r2.text(FontRegistry.INTER_SEMIBOLD, cx + sIconW + iconGap, startY + 25.5F, 28.0F,
                                          serverText, menuTextColor);
                              break;
                        case PING:
                              r2.text(FontRegistry.ICONS, cx, startY + 28.0F, 32.0F, pingIcon, mainColor);
                              r2.text(FontRegistry.INTER_SEMIBOLD, cx + pIconW + iconGap, startY + 25.5F, 28.0F,
                                          pingValue, menuTextColor);
                              r2.text(FontRegistry.INTER_SEMIBOLD, cx + pIconW + iconGap + pTextW + spaceW,
                                          startY + 25.5F, 28.0F, "ms", mainColor);
                              break;
                        case FPS:
                              r2.text(FontRegistry.ICONS, cx, startY + 28.0F, 32.0F, fpsIcon, mainColor);
                              r2.text(FontRegistry.INTER_SEMIBOLD, cx + fIconW + iconGap, startY + 25.5F, 28.0F,
                                          fpsValue, menuTextColor);
                              r2.text(FontRegistry.INTER_SEMIBOLD, cx + fIconW + iconGap + fTextW + spaceW,
                                          startY + 25.5F, 28.0F, "fps", mainColor);
                              break;
                  }
            }

            // --- 8. Context Menu Rendering ---
            menuAnimation.update();
            menuAnimation.run(menuOpen ? 1.0 : 0.0, 0.15F, Easings.SINE_OUT);
            float mAlpha = menuAnimation.get();
            if (mAlpha > 0.001F) {
                  float mWidth = 150.0F;
                  float itemH = 30.0F;
                  float mHeight = itemH * 5 + 10.0F;
                  r2.pushAlpha(mAlpha);
                  
                  int menuBgColor = Renderer2D.ColorUtil.rgba(18, 18, 18, (int)(80.0F * mAlpha));
                  int menuOutlineColor = Renderer2D.ColorUtil.rgba(33, 33, 33, (int)(255.0F * mAlpha));
                  
                  if (ru.noxium.module.impl.visuals.Hud.blur.get()) {
                        r2.prepareBlur(30.0F);
                        r2.blur(menuX, menuY, mWidth, mHeight, 10.0F, mAlpha);
                  }
                  
                  r2.rect(menuX, menuY, mWidth, mHeight, 10.0F, menuBgColor);
                  r2.rectOutline(menuX, menuY, mWidth, mHeight, 10.0F, menuOutlineColor, 2.0F);

                  drawMenuItem(r2, "Пинг", showPing, menuX + 10, menuY + 5, mWidth - 20, itemH);
                  drawMenuItem(r2, "Сервер", showServer, menuX + 10, menuY + 5 + itemH, mWidth - 20, itemH);
                  drawMenuItem(r2, "ФПС", showFps, menuX + 10, menuY + 5 + itemH * 2, mWidth - 20, itemH);
                  drawMenuItem(r2, "Логин", showLogin, menuX + 10, menuY + 5 + itemH * 3, mWidth - 20, itemH);
                  drawMenuItem(r2, "Полный IP", useFullIp, menuX + 10, menuY + 5 + itemH * 4, mWidth - 20, itemH);
                  r2.popAlpha();
            }
      }

      private static boolean shouldHide(ElementType type, boolean showPing, boolean showServer, boolean showLogin,
                  boolean showFps) {
            if (type == ElementType.PING)
                  return !showPing;
            if (type == ElementType.SERVER)
                  return !showServer;
            if (type == ElementType.LOGIN)
                  return !showLogin;
            if (type == ElementType.FPS)
                  return !showFps;
            return false;
      }

      private static void drawMenuItem(Renderer2D r2, String text, boolean enabled, float x, float y, float w, float h) {
            int textColor = Renderer2D.ColorUtil.rgba(160, 216, 255, enabled ? 255 : 150);
            r2.text(FontRegistry.INTER_MEDIUM, x + 5, y + h / 2.0F + 5.0F, 26.0F, text, textColor);
      }

      private static float lerp(float start, float end, float delta) {
            return start + (end - start) * delta;
      }

      private static void drawPlayerHead(Renderer2D r2, Identifier skinTexture, float x, float y, float size,
                  float alpha) {
            if (skinTexture == null)
                  return;
            try {
                  TextureManager textureManager = mc.getTextureManager();
                  if (textureManager == null)
                        return;
                  AbstractTexture texture = textureManager.getTexture(skinTexture);
                  if (texture == null)
                        return;

                  int glId = -1;
                  try {
                        Method getGlId = texture.getClass().getMethod("getGlId");
                        glId = (int) getGlId.invoke(texture);
                  } catch (Exception e1) {
                        try {
                              Method getGlTexture = texture.getClass().getMethod("getGlTexture");
                              Object internalTexture = getGlTexture.invoke(texture);
                              if (internalTexture != null) {
                                    Method internalGetId = internalTexture.getClass().getMethod("getGlId");
                                    glId = (int) internalGetId.invoke(internalTexture);
                              }
                        } catch (Exception e2) {
                        }
                  }

                  if (glId > 0) {
                        r2.pushAlpha(alpha);
                        r2.drawRgbaTextureWithUVRounded(glId, x, y, size, size, 0.0F, 0.0F, 1.0F, 1.0F, size / 2.0F);
                        r2.popAlpha();
                  }
            } catch (Exception var11) {
                  var11.printStackTrace();
            }
      }
}