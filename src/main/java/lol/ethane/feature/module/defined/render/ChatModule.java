package lol.ethane.feature.module.defined.render;

import com.google.common.collect.Lists;
import com.mojang.brigadier.suggestion.Suggestion;
import java.awt.Color;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import lol.aether.builders.Rectangle;
import lol.ethane.event.defined.render.Render2DEvent;
import lol.ethane.event.subscriber.Subscribe;
import lol.ethane.feature.module.Module;
import lol.ethane.feature.module.ModuleCategory;
import lol.ethane.feature.module.property.Property;
import lol.ethane.feature.module.property.impl.BooleanProperty;
import lol.ethane.feature.module.property.impl.NumberProperty;
import lol.ethane.mixin.accessor.ChatScreenAccessor;
import lol.ethane.mixin.accessor.CommandSuggestionsAccessor;
import lol.ethane.mixin.accessor.SuggestionsListAccessor;
import lol.ethane.utils.render.animation.Animation;
import lol.ethane.utils.render.animation.Easing;
import net.minecraft.class_2561;
import net.minecraft.class_2583;
import net.minecraft.class_303;
import net.minecraft.class_408;
import net.minecraft.class_4717;
import net.minecraft.class_5481;
import net.minecraft.class_4717.class_464;

public class ChatModule extends Module {
   private final Property<Double> visibleMessagesProperty = new NumberProperty("Visible messages", 10.0D, 5.0D, 30.0D, 1.0D);
   private final Property<Boolean> fontShadowProperty = new BooleanProperty("Font shadow", false);
   private final Animation openAnimation;
   private float cursorX;
   private class_303 lastNewestMessage;
   private float chatScrollAnimation;
   private float visualLineCount;
   private float scrollOffset;
   private float targetScrollOffset;
   private int totalVisualLines;
   private float suggestionBoxWidth;
   private float chatOffsetX;

   public ChatModule() {
      super("Chat", "Custom chat rendering.", ModuleCategory.RENDER);
      this.openAnimation = new Animation(Easing.OUT_QUART, 600L);
      this.addProperties(new Property[]{this.visibleMessagesProperty, this.fontShadowProperty});
   }

   public void onScroll(double delta) {
      this.targetScrollOffset += (float)(delta * 3.0D);
      this.targetScrollOffset = Math.max(0.0F, Math.min(this.targetScrollOffset, (float)Math.max(0, this.totalVisualLines - ((Double)this.visibleMessagesProperty.getValue()).intValue())));
   }

   @Subscribe
   private void onRender(Render2DEvent event) {
      this.renderChatHistory(event);
      boolean isChatOpen = this.mc.field_1755 instanceof class_408;
      class_408 chatScreen = isChatOpen ? (class_408)this.mc.field_1755 : null;
      this.openAnimation.setDuration(400L);
      this.openAnimation.process(isChatOpen ? 1.0D : 0.0D);
      float scale = (float)this.openAnimation.getValue();
      if (!(scale <= 0.001F)) {
         float typeHeight = 15.0F;
         float typeWidth = 295.0F;
         float typeX = 10.0F;
         float typeY = (float)this.mc.method_22683().method_4502() - 15.0F - 10.0F;
         float centerX = 157.5F;
         float centerY = typeY + 7.5F;
         float bgWidth = 295.0F * scale;
         float bgHeight = 15.0F * scale;
         float bgX = 157.5F - bgWidth / 2.0F;
         float bgY = centerY - bgHeight / 2.0F;
         event.getContext().drawRectangle(Rectangle.builder().xywh(bgX, bgY, bgWidth, bgHeight).radius(5.0F * scale).color(new Color(0, 0, 0, 130)));
         this.suggestionBoxWidth = 0.0F;
         this.renderSuggestions(event, chatScreen, 10.0F, typeY, scale);
         if (chatScreen != null) {
            event.getGraphics().method_51448().pushMatrix();
            event.getGraphics().method_51448().translate(157.5F, centerY);
            event.getGraphics().method_51448().scale(scale, scale);
            event.getGraphics().method_51448().translate(-157.5F, -centerY);
            String text = ((lol.ethane.mixin.accessor.ChatScreenAccessor)chatScreen).getChatField().method_1882();
            int start = Math.min(((lol.ethane.mixin.accessor.TextFieldWidgetAccessor)((lol.ethane.mixin.accessor.ChatScreenAccessor)chatScreen).getChatField()).getCursor(), ((lol.ethane.mixin.accessor.TextFieldWidgetAccessor)((lol.ethane.mixin.accessor.ChatScreenAccessor)chatScreen).getChatField()).getSelectionStart());
            int end = Math.max(((lol.ethane.mixin.accessor.TextFieldWidgetAccessor)((lol.ethane.mixin.accessor.ChatScreenAccessor)chatScreen).getChatField()).getCursor(), ((lol.ethane.mixin.accessor.TextFieldWidgetAccessor)((lol.ethane.mixin.accessor.ChatScreenAccessor)chatScreen).getChatField()).getSelectionStart());
            String textBeforeCursor;
            int alpha;
            int color;
            if (start != end && start >= 0 && end <= text.length()) {
               textBeforeCursor = text.substring(0, start);
               String selectedText = text.substring(start, end);
               alpha = this.mc.field_1772.method_1727(textBeforeCursor);
               color = this.mc.field_1772.method_1727(selectedText);
               event.getGraphics().method_25294((int)(15.0F + (float)alpha), (int)(typeY + 3.0F), (int)(15.0F + (float)alpha + (float)color), (int)(typeY + 13.0F), (new Color(60, 60, 255, 100)).getRGB());
            }

            event.getGraphics().method_51433(this.mc.field_1772, text, 15, (int)(typeY + 4.0F), -1, (Boolean)this.fontShadowProperty.getValue());
            textBeforeCursor = text.substring(0, ((lol.ethane.mixin.accessor.TextFieldWidgetAccessor)((lol.ethane.mixin.accessor.ChatScreenAccessor)chatScreen).getChatField()).getCursor());
            float targetX = (float)(this.mc.field_1772.method_1727(textBeforeCursor) + 1);
            this.cursorX = (float)((double)this.cursorX + (double)(targetX - this.cursorX) * 0.15D);
            alpha = (int)(255.0D * Math.abs(Math.sin((double)System.currentTimeMillis() / 500.0D)));
            color = alpha << 24 | 16777215;
            event.getGraphics().method_51433(this.mc.field_1772, "_", (int)(15.0F + this.cursorX), (int)(typeY + 3.0F + (((lol.ethane.mixin.accessor.TextFieldWidgetAccessor)((lol.ethane.mixin.accessor.ChatScreenAccessor)chatScreen).getChatField()).getCursor() == text.length() ? 0.0F : 2.0F)), color, (Boolean)this.fontShadowProperty.getValue());
            event.getGraphics().method_51448().popMatrix();
            double mouseX = this.mc.field_1729.method_1603() * (double)this.mc.method_22683().method_4486() / (double)this.mc.method_22683().method_4480();
            double mouseY = this.mc.field_1729.method_1604() * (double)this.mc.method_22683().method_4502() / (double)this.mc.method_22683().method_4507();
            class_2583 style = this.getStyleAt(mouseX, mouseY);
            if (style != null && style.method_10969() != null) {
               event.getGraphics().method_51441(this.mc.field_1772, style, (int)mouseX, (int)mouseY);
            }
         }

      }
   }

   private void renderSuggestions(Render2DEvent event, class_408 chatScreen, float typeX, float typeY, float scale) {
      if (chatScreen != null) {
         class_4717 cmdSuggestions = ((lol.ethane.mixin.accessor.ChatScreenAccessor)chatScreen).getCommandSuggestions();
         if (cmdSuggestions != null) {
            class_464 suggestionsList = ((CommandSuggestionsAccessor)cmdSuggestions).getSuggestions();
            if (suggestionsList != null) {
               List<Suggestion> suggestions = ((SuggestionsListAccessor)suggestionsList).getSuggestionList();
               int currentIndex = ((SuggestionsListAccessor)suggestionsList).getCurrent();
               if (suggestions != null && !suggestions.isEmpty()) {
                  int maxVisible = Math.min(suggestions.size(), 10);
                  float lineHeight = 12.0F;
                  float padding = 4.0F;
                  float maxWidth = 0.0F;

                  float width;
                  for(int i = 0; i < maxVisible; ++i) {
                     width = (float)this.mc.field_1772.method_1727(((Suggestion)suggestions.get(i)).getText());
                     if (width > maxWidth) {
                        maxWidth = width;
                     }
                  }

                  float boxWidth = maxWidth + padding * 2.0F;
                  width = (float)maxVisible * lineHeight + padding * 2.0F;
                  float boxY = typeY - width - 10.0F;
                  event.getContext().drawRectangle(Rectangle.builder().xywh(typeX, boxY, boxWidth, width).radius(5.0F).color(new Color(0, 0, 0, 130)));
                  this.suggestionBoxWidth = boxWidth + 5.0F;

                  for(int i = 0; i < maxVisible; ++i) {
                     Suggestion suggestion = (Suggestion)suggestions.get(i);
                     float drawY = boxY + padding + (float)i * lineHeight;
                     int textColor = i == currentIndex ? (new Color(100, 180, 255)).getRGB() : -1;
                     if (i == currentIndex) {
                        event.getGraphics().method_25294((int)typeX, (int)drawY, (int)(typeX + boxWidth), (int)(drawY + lineHeight - 1.0F), (new Color(255, 255, 255, 30)).getRGB());
                     }

                     event.getGraphics().method_51433(this.mc.field_1772, suggestion.getText(), (int)(typeX + padding), (int)drawY + 1, textColor, (Boolean)this.fontShadowProperty.getValue());
                  }

               }
            }
         }
      }
   }

   private void renderChatHistory(Render2DEvent event) {
      ArrayList messages;
      synchronized(((lol.ethane.mixin.accessor.ChatHudLineAccessor)this.mc.field_1705.method_1743()).getMessages()) {
         messages = new ArrayList(((lol.ethane.mixin.accessor.ChatHudLineAccessor)this.mc.field_1705.method_1743()).getMessages());
      }

      if (!messages.isEmpty()) {
         class_303 currentNewest = (class_303)messages.getFirst();
         if (this.lastNewestMessage != currentNewest) {
            if (this.lastNewestMessage != null && messages.contains(this.lastNewestMessage)) {
               int newCount = 0;

               List split;
               for(Iterator var5 = messages.iterator(); var5.hasNext(); newCount += split.size()) {
                  class_303 m = (class_303)var5.next();
                  if (m == this.lastNewestMessage) {
                     break;
                  }

                  split = this.mc.field_1772.method_1728(m.comp_893(), 279);
               }

               this.chatScrollAnimation += (float)(newCount * 10);
            } else {
               this.chatScrollAnimation = 0.0F;
            }

            this.lastNewestMessage = currentNewest;
         }

         this.chatScrollAnimation = (float)((double)this.chatScrollAnimation * 0.85D);
         if ((double)this.chatScrollAnimation < 0.1D) {
            this.chatScrollAnimation = 0.0F;
         }

         float chatWidth = 295.0F;
         float scale = (float)this.openAnimation.getValue();
         float y = (float)(this.mc.method_22683().method_4502() - 45) + (1.0F - scale) * 20.0F;
         boolean chatOpen = this.mc.field_1755 instanceof class_408;
         if (!chatOpen) {
            this.scrollOffset = 0.0F;
            this.targetScrollOffset = 0.0F;
            this.suggestionBoxWidth = 0.0F;
         }

         this.scrollOffset += (this.targetScrollOffset - this.scrollOffset) * 0.3F;
         this.chatOffsetX += (this.suggestionBoxWidth - this.chatOffsetX) * 0.15F;
         int currentTick = ((lol.ethane.mixin.accessor.ChatHudAccessor)this.mc.field_1705).getScrolledLines();
         int targetLineCount = 0;
         List<class_5481> allVisualLines = new ArrayList();
         Iterator var11 = messages.iterator();

         while(true) {
            class_303 msg;
            List linesToRender;
            do {
               if (!var11.hasNext()) {
                  this.totalVisualLines = allVisualLines.size();
                  if (chatOpen) {
                     targetLineCount = Math.min(allVisualLines.size(), ((Double)this.visibleMessagesProperty.getValue()).intValue());
                  } else {
                     targetLineCount = Math.min(targetLineCount, ((Double)this.visibleMessagesProperty.getValue()).intValue());
                  }

                  this.visualLineCount = (float)((double)this.visualLineCount + (double)((float)targetLineCount - this.visualLineCount) * 0.1D);
                  int renderCount = (int)Math.ceil((double)this.visualLineCount);
                  renderCount = Math.min(renderCount, allVisualLines.size());
                  if (renderCount <= 0 && (double)this.visualLineCount < 0.1D) {
                     return;
                  }

                  int scrollStart = (int)this.scrollOffset;
                  int scrollEnd = Math.min(scrollStart + renderCount, allVisualLines.size());
                  linesToRender = allVisualLines.subList(scrollStart, scrollEnd);
                  float totalHeight = this.visualLineCount * 10.0F;
                  float rectBottom = y + 10.0F;
                  float rectTop = rectBottom - totalHeight - 3.0F;
                  float rectHeight = rectBottom - rectTop;
                  if (rectHeight <= 6.0F) {
                     return;
                  }

                  event.getContext().drawRectangle(Rectangle.builder().xywh(10.0F + this.chatOffsetX, rectTop, chatWidth, rectHeight).radius(5.0F).color(new Color(0, 0, 0, 130)));
                  float finalScrollAnimation = this.chatScrollAnimation;
                  float finalScrollOffset = this.scrollOffset;
                  float finalChatOffsetX = this.chatOffsetX;
                  int scMinY = (int)(rectTop + 1.5F);
                  int scMaxY = (int)(rectTop + rectHeight);
                  event.getGraphics().method_44379((int)(10.0F + finalChatOffsetX), scMinY, (int)(10.0F + finalChatOffsetX + chatWidth), scMaxY);

                  for(int i = 0; i < linesToRender.size(); ++i) {
                     class_5481 seq = (class_5481)linesToRender.get(i);
                     float scrollFraction = finalScrollOffset - (float)scrollStart;
                     float drawY = y - (float)(i * 10) + finalScrollAnimation + scrollFraction * 10.0F;
                     event.getGraphics().method_51430(this.mc.field_1772, seq, (int)(14.0F + finalChatOffsetX), (int)drawY, -1, (Boolean)this.fontShadowProperty.getValue());
                  }

                  event.getGraphics().method_44380();
                  return;
               }

               msg = (class_303)var11.next();
            } while(msg == null);

            boolean expired = !chatOpen && currentTick - msg.comp_892() > 200;
            linesToRender = this.mc.field_1772.method_1728(msg.comp_893(), (int)chatWidth - 16);
            Iterator var15 = Lists.reverse(linesToRender).iterator();

            while(var15.hasNext()) {
               class_5481 seq = (class_5481)var15.next();
               allVisualLines.add(seq);
               if (!expired) {
                  ++targetLineCount;
               }
            }
         }
      }
   }

   public class_2583 getStyleAt(double mouseX, double mouseY) {
      ArrayList messages;
      synchronized(((lol.ethane.mixin.accessor.ChatHudLineAccessor)this.mc.field_1705.method_1743()).getMessages()) {
         messages = new ArrayList(((lol.ethane.mixin.accessor.ChatHudLineAccessor)this.mc.field_1705.method_1743()).getMessages());
      }

      if (messages.isEmpty()) {
         return null;
      } else {
         float chatWidth = 295.0F;
         float y = (float)(this.mc.method_22683().method_4502() - 45);
         List<class_5481> allVisualLines = new ArrayList();
         Iterator var9 = messages.iterator();

         while(var9.hasNext()) {
            class_303 msg = (class_303)var9.next();
            if (msg != null) {
               List<class_5481> split = this.mc.field_1772.method_1728(msg.comp_893(), (int)chatWidth - 16);
               allVisualLines.addAll(Lists.reverse(split));
            }
         }

         int renderCount = (int)Math.ceil((double)this.visualLineCount);
         renderCount = Math.min(renderCount, allVisualLines.size());
         if (renderCount <= 0 && (double)this.visualLineCount < 0.1D) {
            return null;
         } else {
            int scrollStart = (int)this.scrollOffset;
            int scrollEnd = Math.min(scrollStart + renderCount, allVisualLines.size());
            List<class_5481> linesToRender = allVisualLines.subList(scrollStart, scrollEnd);
            float totalHeight = this.visualLineCount * 10.0F;
            float rectBottom = y + 10.0F;
            float rectTop = rectBottom - totalHeight - 3.0F;
            float rectHeight = rectBottom - rectTop;
            int scMinY = (int)(rectTop + 1.5F);
            int scMaxY = (int)(rectTop + rectHeight);
            if (!(mouseY < (double)scMinY) && !(mouseY > (double)scMaxY)) {
               if (!(mouseX < (double)(10.0F + this.chatOffsetX)) && !(mouseX > (double)(10.0F + this.chatOffsetX + chatWidth))) {
                  float finalScrollAnimation = this.chatScrollAnimation;
                  float finalScrollOffset = this.scrollOffset;
                  float finalChatOffsetX = this.chatOffsetX;

                  for(int i = 0; i < linesToRender.size(); ++i) {
                     class_5481 seq = (class_5481)linesToRender.get(i);
                     float scrollFraction = finalScrollOffset - (float)scrollStart;
                     float drawY = y - (float)(i * 10) + finalScrollAnimation + scrollFraction * 10.0F;
                     if (mouseY >= (double)drawY && mouseY < (double)(drawY + 10.0F)) {
                        float relativeX = (float)(mouseX - (double)(14.0F + finalChatOffsetX));
                        return this.getStyleFromSeq(seq, (int)relativeX);
                     }
                  }

                  return null;
               } else {
                  return null;
               }
            } else {
               return null;
            }
         }
      }
   }

   private class_2583 getStyleFromSeq(class_5481 seq, int x) {
      float[] width = new float[]{0.0F};
      class_2583[] foundStyle = new class_2583[]{null};
      seq.accept((a, style, codePoint) -> {
         String s = new String(Character.toChars(codePoint));
         float charWidth = (float)this.mc.field_1772.method_27525(class_2561.method_43470(s).method_10862(style));
         if (width[0] + charWidth >= (float)x) {
            foundStyle[0] = style;
            return false;
         } else {
            width[0] += charWidth;
            return true;
         }
      });
      return foundStyle[0];
   }
}
