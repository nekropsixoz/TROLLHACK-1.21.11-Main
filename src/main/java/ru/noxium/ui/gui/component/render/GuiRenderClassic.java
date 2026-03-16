package ru.noxium.ui.gui.component.render;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.util.math.MatrixStack;
import ru.noxium.Noxium;
import ru.noxium.module.api.Category;
import ru.noxium.module.api.Module;
import ru.noxium.module.api.setting.Setting;
import ru.noxium.module.api.setting.impl.*;
import ru.noxium.ui.gui.GuiScreen;
import ru.noxium.ui.gui.component.setting.GuiRenderSetting;
import ru.noxium.util.color.ColorUtil;
import ru.noxium.util.render.animation.util.Easings;
import ru.noxium.util.render.core.Renderer2D;
import ru.noxium.util.render.math.animation.AnimationMath;
import ru.noxium.util.render.text.FontRegistry;
import ru.noxium.util.render.utils.KeyUtil;

import java.util.ArrayList;
import java.util.List;

@Environment(EnvType.CLIENT)
public class GuiRenderClassic extends GuiScreen {
    
    private static final float COLUMN_WIDTH = 120.0F;
    private static final float HEADER_HEIGHT = 18.0F;
    private static final float MODULE_HEIGHT = 18.0F;
    private static final float PADDING = 3.0F;
    private static final float COLUMN_SPACING = 8.0F;
    private static final float SEARCH_HEIGHT = 22.0F;
    private static final float SETTINGS_INDENT = 10.0F;
    
    public static void renderClassic(Renderer2D renderer2D, MatrixStack pose, int mouseX, int mouseY, float mainAlpha) {
        int bgColor = Renderer2D.ColorUtil.rgba(18, 18, 18, (int)(80.0F * mainAlpha));
        int outlineColor = Renderer2D.ColorUtil.rgba(33, 33, 33, (int)(255.0F * mainAlpha));
        int headerColor = Renderer2D.ColorUtil.rgba(26, 26, 26, (int)(200.0F * mainAlpha));
        int textColor = Renderer2D.ColorUtil.rgba(160, 216, 255, (int)(255.0F * mainAlpha));
        int textDimColor = Renderer2D.ColorUtil.rgba(160, 216, 255, (int)(150.0F * mainAlpha));
        int hoverColor = Renderer2D.ColorUtil.rgba(160, 216, 255, (int)(30.0F * mainAlpha));
        int enabledColor = Renderer2D.ColorUtil.rgba(160, 216, 255, (int)(50.0F * mainAlpha));
        int mainColor40 = Renderer2D.ColorUtil.rgba(160, 216, 255, (int)(150.0F * mainAlpha));
        int mainColor6 = Renderer2D.ColorUtil.rgba(18, 18, 18, (int)(80.0F * mainAlpha));
        
        Category[] categories = Category.values();
        float startX = 20.0F;
        float startY = 50.0F;
        
        // Рендерим поиск
        renderSearch(renderer2D, startX, 20.0F, mainAlpha, mouseX, mouseY, bgColor, outlineColor, textColor);
        
        // Рендерим колонки с категориями
        for (int i = 0; i < categories.length; i++) {
            Category category = categories[i];
            List<Module> modules = getFilteredModules(category);
            
            if (modules.isEmpty() && !GuiScreen.searchText.isEmpty()) {
                continue;
            }
            
            float columnX = startX + i * (COLUMN_WIDTH + COLUMN_SPACING);
            float columnY = startY;
            
            // Вычисляем высоту колонки с учетом открытых настроек
            float columnHeight = HEADER_HEIGHT + PADDING * 2 + PADDING;
            for (Module module : modules) {
                columnHeight += MODULE_HEIGHT;
                
                // Добавляем высоту настроек если модуль открыт
                if (GuiScreen.openSettingsModules.contains(module) && !module.getSettings().isEmpty()) {
                    float settingsAnim = GuiScreen.getModuleSettingsAnimation(module).get();
                    float settingsHeight = PADDING * 2;
                    for (Setting setting : module.getSettings()) {
                        settingsHeight += getSettingHeightClassic(renderer2D, setting);
                    }
                    columnHeight += settingsHeight * settingsAnim;
                }
            }
            
            if (ru.noxium.module.impl.visuals.Hud.blur.get()) {
                renderer2D.prepareBlur(30.0F);
                renderer2D.blur(columnX, columnY, COLUMN_WIDTH, columnHeight, 10.0F, mainAlpha);
            }
            
            renderer2D.rect(columnX, columnY, COLUMN_WIDTH, columnHeight, 10.0F, bgColor);
            renderer2D.rectOutline(columnX, columnY, COLUMN_WIDTH, columnHeight, 10.0F, outlineColor, 2.0F);
            
            renderer2D.rect(columnX + PADDING, columnY + PADDING, COLUMN_WIDTH - PADDING * 2, HEADER_HEIGHT, 6.0F, headerColor);
            
            String categoryName = getCategoryName(category);
            float textWidth = renderer2D.measureText(FontRegistry.INTER_MEDIUM, categoryName, 16.0F).width;
            float textX = columnX + (COLUMN_WIDTH - textWidth) / 2.0F;
            renderer2D.text(FontRegistry.INTER_MEDIUM, textX, columnY + PADDING + 12.0F, 16.0F, categoryName, textColor);
            
            float moduleY = columnY + HEADER_HEIGHT + PADDING * 2;
            for (Module module : modules) {
                boolean isHovered = mouseX >= columnX + PADDING && mouseX <= columnX + COLUMN_WIDTH - PADDING
                    && mouseY >= moduleY && mouseY <= moduleY + MODULE_HEIGHT;
                
                if (module.enable) {
                    renderer2D.rect(columnX + PADDING, moduleY, COLUMN_WIDTH - PADDING * 2, MODULE_HEIGHT, 4.0F, enabledColor);
                } else if (isHovered) {
                    renderer2D.rect(columnX + PADDING, moduleY, COLUMN_WIDTH - PADDING * 2, MODULE_HEIGHT, 4.0F, hoverColor);
                }
                
                int moduleTextColor = module.enable ? textColor : textDimColor;
                renderer2D.text(FontRegistry.INTER_MEDIUM, columnX + PADDING + 5.0F, moduleY + 12.0F, 15.0F, module.name, moduleTextColor);
                
                if (!module.getSettings().isEmpty()) {
                    String arrow = GuiScreen.openSettingsModules.contains(module) ? "−" : "+";
                    renderer2D.text(FontRegistry.INTER_MEDIUM, columnX + COLUMN_WIDTH - PADDING - 12.0F, moduleY + 12.0F, 15.0F, arrow, textDimColor);
                }
                
                moduleY += MODULE_HEIGHT;
                
                // Рендерим настройки если модуль открыт
                if (GuiScreen.openSettingsModules.contains(module) && !module.getSettings().isEmpty()) {
                    float settingsAnim = GuiScreen.getModuleSettingsAnimation(module).get();
                    float settingsAlphaAnim = GuiScreen.getModuleSettingsAlphaAnimation(module).get();
                    
                    if (settingsAnim > 0.01F) {
                        renderer2D.pushAlpha(settingsAlphaAnim);
                        float settingY = moduleY + PADDING;
                        float settingX = columnX + PADDING + SETTINGS_INDENT;
                        float settingWidth = COLUMN_WIDTH - PADDING * 2 - SETTINGS_INDENT - 5.0F;
                        
                        for (Setting setting : module.getSettings()) {
                            float settingHeight = renderSettingClassic(renderer2D, setting, settingX, settingY, settingWidth, 
                                mouseX, mouseY, outlineColor, textColor, mainColor6, mainColor40, textDimColor, mainAlpha);
                            settingY += settingHeight * settingsAnim;
                        }
                        
                        moduleY = settingY + PADDING;
                        renderer2D.popAlpha();
                    }
                }
            }
        }
    }

    
    private static float getSettingHeightClassic(Renderer2D renderer2D, Setting setting) {
        if (setting instanceof NoneSetting) {
            return ((NoneSetting) setting).get();
        } else if (setting instanceof BooleanSetting) {
            return 14.0F;
        } else if (setting instanceof SliderSetting) {
            return 22.0F;
        } else if (setting instanceof ModeSetting modeSetting) {
            float baseHeight = 24.0F;
            if (modeSetting.dropdownAnim.get() > 0.01F) {
                float settingWidth = COLUMN_WIDTH - PADDING * 2 - SETTINGS_INDENT - 5.0F;
                float modeSpacing = 2.0F;
                float modeHeight = 12.0F;
                float padding = 3.0F;
                float verticalSpacing = 2.0F;
                float calcX = padding;
                float calcY = 0.0F;

                for (String mode : modeSetting.modes) {
                    if (!mode.equals(modeSetting.currentMode)) {
                        float modeWidth = renderer2D.measureText(FontRegistry.INTER_MEDIUM, mode, 12.0F).width + padding * 2.0F;
                        if (calcX + modeWidth > settingWidth && calcX > padding) {
                            calcX = padding;
                            calcY += modeHeight + verticalSpacing;
                        }
                        calcX += modeWidth + modeSpacing;
                    }
                }

                float dropdownHeight = (calcY + modeHeight) * modeSetting.dropdownAnim.get();
                return baseHeight + dropdownHeight + 2.0F;
            }
            return baseHeight;
        } else if (setting instanceof BindSettings) {
            return 16.0F;
        } else if (setting instanceof StringSetting) {
            return 18.0F;
        } else if (setting instanceof HueSetting) {
            return 18.0F;
        } else if (setting instanceof MultiBooleanSetting multiBooleanSetting) {
            float settingWidth = COLUMN_WIDTH - PADDING * 2 - SETTINGS_INDENT - 5.0F;
            float startY = 12.0F;
            float currentX = 0.0F;
            float currentY = startY;
            float spacing = 3.0F;
            float boolHeight = 12.0F;
            float padding = 4.0F;

            for (BooleanSetting boolSetting : multiBooleanSetting.settings) {
                float nameWidth = renderer2D.measureText(FontRegistry.INTER_MEDIUM, boolSetting.name, 12.0F).width;
                float boolWidth = nameWidth + padding * 2.0F;
                if (currentX + boolWidth > settingWidth) {
                    currentX = 0.0F;
                    currentY += boolHeight + spacing;
                }
                currentX += boolWidth + spacing;
            }

            return currentY + boolHeight;
        } else {
            return 18.0F;
        }
    }
    
    private static float renderSettingClassic(Renderer2D renderer2D, Setting setting, float x, float y, float width,
            int mouseX, int mouseY, int outlineColor, int textColor, int mainColor6, int mainColor40, 
            int textDimColor, float mainAlpha) {
        
        if (setting instanceof BooleanSetting boolSetting) {
            float checkBoxSize = 12.0F;
            float checkBoxX = x + width - checkBoxSize;
            float checkBoxY = y + 1.0F;
            
            boolSetting.anim.update();
            boolSetting.anim.run(boolSetting.get() ? 1.0 : 0.0, 0.15F, Easings.SINE_OUT);
            
            int bgColor = Renderer2D.ColorUtil.rgba(18, 18, 18, (int)(80.0F * mainAlpha));
            
            renderer2D.rect(checkBoxX, checkBoxY, checkBoxSize, checkBoxSize, 10.0F, bgColor);
            
            if (boolSetting.anim.get() > 0.01F) {
                float circleSize = 6.0F * boolSetting.anim.get();
                float circleX = checkBoxX + (checkBoxSize - circleSize) / 2.0F;
                float circleY = checkBoxY + (checkBoxSize - circleSize) / 2.0F;
                renderer2D.circle(circleX + circleSize / 2.0F, circleY + circleSize / 2.0F, circleSize / 2.0F, 0.0F, 1.0F, textColor);
            }
            
            renderer2D.text(FontRegistry.INTER_MEDIUM, x, y + 9.0F, 13.0F, setting.name, mainColor40);
            return 14.0F;
            
        } else if (setting instanceof SliderSetting sliderSetting) {
            float sliderHeight = 4.0F;
            float sliderY = y + 12.0F;
            float sliderWidth = width;
            
            ru.noxium.util.render.math.animation.anim.util.Animation2 sliderAnim = GuiScreen.getSliderAnimation(sliderSetting);
            float targetProgress = (sliderSetting.current - sliderSetting.minimum) / (sliderSetting.maximum - sliderSetting.minimum);
            sliderAnim.update();
            sliderAnim.run(targetProgress, 0.24F, ru.noxium.util.render.math.animation.anim.util.Easings.QUART_OUT);
            float progress = (float)sliderAnim.getValue();
            float progressWidth = sliderWidth * progress;
            
            int trackColor = Renderer2D.ColorUtil.rgba(255, 255, 255, (int)(255.0F * mainAlpha));
            int thumbColor = Renderer2D.ColorUtil.rgba(176, 176, 176, (int)(255.0F * mainAlpha));
            
            renderer2D.rect(x, sliderY, sliderWidth, sliderHeight, 2.0F, trackColor);
            
            float thumbSize = 8.0F;
            float thumbX = x + progressWidth - thumbSize / 2.0F;
            float thumbY = sliderY - (thumbSize - sliderHeight) / 2.0F;
            renderer2D.circle(thumbX + thumbSize / 2.0F, thumbY + thumbSize / 2.0F, thumbSize / 2.0F, 0.0F, 1.0F, thumbColor);
            renderer2D.shadow(thumbX, thumbY, thumbSize, thumbSize, thumbSize / 2.0F, 3.0F, 0.5F, 
                Renderer2D.ColorUtil.rgba(176, 176, 176, (int)(100.0F * mainAlpha)));
            
            String valueText = sliderSetting.percent ? 
                String.format("%.1f%%", sliderSetting.current) : 
                String.format("%.1f", sliderSetting.current);
            renderer2D.text(FontRegistry.INTER_MEDIUM, x, y + 8.0F, 13.0F, setting.name, mainColor40);
            renderer2D.text(FontRegistry.INTER_MEDIUM, x + width - renderer2D.measureText(FontRegistry.INTER_MEDIUM, valueText, 12.0F).width, 
                y + 8.0F, 12.0F, valueText, textColor);
            return 22.0F;
            
        } else if (setting instanceof ModeSetting modeSetting) {
            int bgColorDark = Renderer2D.ColorUtil.rgba(26, 26, 26, (int)(80.0F * mainAlpha));
            
            renderer2D.text(FontRegistry.INTER_MEDIUM, x, y + 8.0F, 13.0F, setting.name, mainColor40);
            
            float currentModeButtonHeight = 12.0F;
            float currentModeButtonY = y + 11.0F;
            float currentModeWidth = renderer2D.measureText(FontRegistry.INTER_MEDIUM, modeSetting.currentMode, 12.0F).width + 12.0F;
            
            renderer2D.rect(x, currentModeButtonY, currentModeWidth, currentModeButtonHeight, 10.0F, mainColor6);
            renderer2D.text(FontRegistry.INTER_MEDIUM, x + 6.0F, currentModeButtonY + 8.0F, 12.0F, modeSetting.currentMode, textColor);
            
            modeSetting.dropdownAnim.update();
            modeSetting.dropdownAnim.run(modeSetting.opened ? 1.0 : 0.0, 0.2F, Easings.QUART_OUT);
            float dropdownAnimValue = modeSetting.dropdownAnim.get();
            
            if (dropdownAnimValue > 0.01F) {
                float modeSpacing = 2.0F;
                float modeHeight = 12.0F;
                float padding = 3.0F;
                float verticalSpacing = 2.0F;
                float calcX = padding;
                float calcY = 0.0F;

                for (String mode : modeSetting.modes) {
                    if (!mode.equals(modeSetting.currentMode)) {
                        float modeWidth = renderer2D.measureText(FontRegistry.INTER_MEDIUM, mode, 12.0F).width + padding * 2.0F;
                        if (calcX + modeWidth > width && calcX > padding) {
                            calcX = padding;
                            calcY += modeHeight + verticalSpacing;
                        }
                        calcX += modeWidth + modeSpacing;
                    }
                }

                float dropdownHeight = (calcY + modeHeight) * dropdownAnimValue;
                float dropdownY = currentModeButtonY + currentModeButtonHeight + 2.0F;
                
                renderer2D.pushRoundedClipRect(x, dropdownY, width, dropdownHeight, 0.0F, 0.0F, 10.0F, 10.0F);
                renderer2D.rect(x, dropdownY, width, dropdownHeight, 10.0F, bgColorDark);
                
                float currentX = padding;
                float currentY = 2.0F;

                for (String mode : modeSetting.modes) {
                    if (!mode.equals(modeSetting.currentMode)) {
                        float modeWidth = renderer2D.measureText(FontRegistry.INTER_MEDIUM, mode, 12.0F).width + padding * 2.0F;
                        if (currentX + modeWidth > width && currentX > padding) {
                            currentX = padding;
                            currentY += modeHeight + verticalSpacing;
                        }

                        boolean modeHovered = GuiRenderMain.isHovered(mouseX, mouseY, x + currentX, dropdownY + currentY, modeWidth, modeHeight);
                        if (modeHovered) {
                            renderer2D.rect(x + currentX, dropdownY + currentY, modeWidth, modeHeight, 3.0F, Renderer2D.ColorUtil.rgba(160, 216, 255, (int)(30.0F * mainAlpha)));
                        }
                        
                        renderer2D.text(FontRegistry.INTER_MEDIUM, x + currentX + padding, dropdownY + currentY + 8.0F, 12.0F, 
                            mode, ColorUtil.multAlpha(textDimColor, dropdownAnimValue));
                        currentX += modeWidth + modeSpacing;
                    }
                }
                
                renderer2D.popClipRect();
                return currentModeButtonHeight + 13.0F + dropdownHeight + 2.0F;
            }
            return currentModeButtonHeight + 13.0F;
            
        } else if (setting instanceof BindSettings bindSetting) {
            float bindHeight = 12.0F;
            String keyText = bindSetting.active ? "..." : KeyUtil.getKey(bindSetting.key);
            float keyTextWidth = renderer2D.measureText(FontRegistry.INTER_MEDIUM, keyText, 12.0F).width;
            float buttonWidth = Math.max(20.0F, keyTextWidth + 10.0F);
            float bindButtonX = x + width - buttonWidth;
            
            renderer2D.text(FontRegistry.INTER_MEDIUM, x, y + 9.0F, 13.0F, setting.name, mainColor40);
            renderer2D.rectOutline(bindButtonX, y, buttonWidth, bindHeight, 3.0F, outlineColor, 1.0F);
            renderer2D.rect(bindButtonX, y, buttonWidth, bindHeight, 3.0F, mainColor6);
            renderer2D.text(FontRegistry.INTER_MEDIUM, bindButtonX + buttonWidth / 2.0F - keyTextWidth / 2.0F, 
                y + 8.0F, 12.0F, keyText, bindSetting.active ? textColor : mainColor40);
            return 16.0F;
            
        } else if (setting instanceof StringSetting stringSetting) {
            float textFieldHeight = 12.0F;
            float textFieldWidth = width - 45.0F;
            float textFieldX = x + 45.0F;
            
            renderer2D.text(FontRegistry.INTER_MEDIUM, x, y + 9.0F, 13.0F, setting.name, mainColor40);
            renderer2D.rectOutline(textFieldX, y, textFieldWidth, textFieldHeight, 3.0F, outlineColor, 1.0F);
            renderer2D.rect(textFieldX, y, textFieldWidth, textFieldHeight, 3.0F, mainColor6);
            
            String inputText = stringSetting.input;
            if (inputText.isEmpty()) {
                renderer2D.text(FontRegistry.INTER_MEDIUM, textFieldX + 4.0F, y + 8.0F, 11.0F, "Enter text", mainColor40);
            } else {
                renderer2D.text(FontRegistry.INTER_MEDIUM, textFieldX + 4.0F, y + 8.0F, 11.0F, inputText, textColor);
            }
            
            boolean isActive = GuiScreen.activeStringSetting == stringSetting && stringSetting.active;
            if (isActive) {
                long currentTime = System.currentTimeMillis();
                boolean showCursor = currentTime / 500L % 2L == 0L;
                if (showCursor) {
                    float cursorX = textFieldX + 4.0F + renderer2D.measureText(FontRegistry.INTER_MEDIUM, inputText, 11.0F).width;
                    renderer2D.rect(cursorX, y + 2.0F, 1.0F, 8.0F, 0.5F, textColor);
                }
            }
            return 18.0F;
            
        } else if (setting instanceof HueSetting hueSetting) {
            float colorHeight = 12.0F;
            float colorWidth = 35.0F;
            float colorX = x + width - colorWidth;
            
            renderer2D.text(FontRegistry.INTER_MEDIUM, x, y + 9.0F, 13.0F, setting.name, mainColor40);
            renderer2D.rectOutline(colorX, y, colorWidth, colorHeight, 3.0F, outlineColor, 1.0F);
            renderer2D.rect(colorX, y, colorWidth, colorHeight, 3.0F, mainColor6);
            
            java.awt.Color hueColor = hueSetting.getColor();
            renderer2D.rect(colorX + 2.0F, y + 2.0F, colorWidth - 4.0F, colorHeight - 4.0F, 2.0F, 
                Renderer2D.ColorUtil.replAlpha(hueColor.getRGB(), (int)(255.0F * mainAlpha)));
            return 18.0F;
            
        } else if (setting instanceof MultiBooleanSetting multiBooleanSetting) {
            renderer2D.text(FontRegistry.INTER_MEDIUM, x, y + 8.0F, 13.0F, setting.name, mainColor40);
            float startY = y + 12.0F;
            float currentX = x;
            float currentY = startY;
            float spacing = 3.0F;
            float boolHeight = 12.0F;
            float padding = 4.0F;

            for (BooleanSetting boolSetting : multiBooleanSetting.settings) {
                float nameWidth = renderer2D.measureText(FontRegistry.INTER_MEDIUM, boolSetting.name, 12.0F).width;
                float boolWidth = nameWidth + padding * 2.0F;
                if (currentX + boolWidth > x + width) {
                    currentX = x;
                    currentY += boolHeight + spacing;
                }

                renderer2D.rectOutline(currentX, currentY, boolWidth, boolHeight, 3.0F, outlineColor, 1.0F);
                renderer2D.rect(currentX, currentY, boolWidth, boolHeight, 3.0F, mainColor6);
                
                String animKey = setting.name + "_" + boolSetting.name;
                GuiRenderSetting.multiBooleanAnimation.putIfAbsent(animKey, boolSetting.get() ? 1.0F : 0.0F);
                float anim = GuiRenderSetting.multiBooleanAnimation.get(animKey);
                float target = boolSetting.get() ? 1.0F : 0.0F;
                anim = AnimationMath.fast(anim, target, 10.0F);
                GuiRenderSetting.multiBooleanAnimation.put(animKey, anim);
                int boolTextColor = ColorUtil.overCol(mainColor40, textColor, anim);
                
                renderer2D.text(FontRegistry.INTER_MEDIUM, currentX + padding, currentY + 8.0F, 12.0F, boolSetting.name, boolTextColor);
                currentX += boolWidth + spacing;
            }

            return currentY - y + boolHeight;
        }
        
        return 18.0F;
    }
    
    private static void renderSearch(Renderer2D renderer2D, float x, float y, float mainAlpha, int mouseX, int mouseY,
                                     int bgColor, int outlineColor, int textColor) {
        float searchWidth = 160.0F;
        float searchHeight = SEARCH_HEIGHT;
        
        if (ru.noxium.module.impl.visuals.Hud.blur.get()) {
            renderer2D.prepareBlur(30.0F);
            renderer2D.blur(x, y, searchWidth, searchHeight, 8.0F, mainAlpha);
        }
        
        renderer2D.rect(x, y, searchWidth, searchHeight, 8.0F, bgColor);
        renderer2D.rectOutline(x, y, searchWidth, searchHeight, 8.0F, outlineColor, 2.0F);
        
        renderer2D.text(FontRegistry.ICONS, x + 6.0F, y + 16.0F, 18.0F, "Q", textColor);
        
        String displayText = GuiScreen.searchText.isEmpty() ? "Search..." : GuiScreen.searchText;
        int searchTextColor = GuiScreen.searchText.isEmpty() 
            ? Renderer2D.ColorUtil.rgba(160, 216, 255, (int)(100.0F * mainAlpha))
            : textColor;
        renderer2D.text(FontRegistry.INTER_MEDIUM, x + 28.0F, y + 15.0F, 14.0F, displayText, searchTextColor);
    }
    
    private static List<Module> getFilteredModules(Category category) {
        List<Module> allModules = Noxium.get.manager.getType(category);
        if (GuiScreen.searchText.isEmpty()) {
            return allModules;
        }
        
        List<Module> filtered = new ArrayList<>();
        String search = GuiScreen.searchText.toLowerCase();
        for (Module module : allModules) {
            if (module.name.toLowerCase().contains(search)) {
                filtered.add(module);
            }
        }
        return filtered;
    }
    
    private static String getCategoryName(Category category) {
        switch (category) {
            case Combat: return "Combat";
            case Movement: return "Movement";
            case Player: return "Player";
            case Visuals: return "Render";
            case Misc: return "Misc";
            default: return category.name();
        }
    }
}
