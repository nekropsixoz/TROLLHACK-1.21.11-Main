package ru.noxium.ui.gui.component.mouse;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import ru.noxium.Noxium;
import ru.noxium.module.api.Category;
import ru.noxium.module.api.Module;
import ru.noxium.module.api.setting.Setting;
import ru.noxium.module.api.setting.impl.*;
import ru.noxium.ui.gui.GuiScreen;
import ru.noxium.ui.gui.component.render.GuiRenderMain;
import ru.noxium.util.render.math.animation.anim.util.Easings;
import ru.noxium.util.render.core.Renderer2D;
import ru.noxium.util.render.text.FontRegistry;

import java.util.ArrayList;
import java.util.List;

@Environment(EnvType.CLIENT)
public class GuiMouseClickedClassic extends GuiScreen {
    
    private static final float COLUMN_WIDTH = 120.0F;
    private static final float HEADER_HEIGHT = 18.0F;
    private static final float MODULE_HEIGHT = 18.0F;
    private static final float PADDING = 3.0F;
    private static final float COLUMN_SPACING = 8.0F;
    private static final float SEARCH_HEIGHT = 22.0F;
    private static final float SETTINGS_INDENT = 10.0F;
    
    public static boolean mouseClickedClassic(Renderer2D renderer2D, int mouseX, int mouseY, int button) {
        float startX = 20.0F;
        float startY = 50.0F;
        
        // Проверяем клик по поиску
        if (button == 0) {
            float searchWidth = 160.0F;
            if (mouseX >= startX && mouseX <= startX + searchWidth 
                && mouseY >= 20.0F && mouseY <= 20.0F + SEARCH_HEIGHT) {
                GuiScreen.activeSearch = true;
                return true;
            }
        }
        
        Category[] categories = Category.values();
        
        for (int i = 0; i < categories.length; i++) {
            Category category = categories[i];
            List<Module> modules = getFilteredModules(category);
            
            if (modules.isEmpty() && !GuiScreen.searchText.isEmpty()) {
                continue;
            }
            
            float columnX = startX + i * (COLUMN_WIDTH + COLUMN_SPACING);
            float columnY = startY;
            
            float moduleY = columnY + HEADER_HEIGHT + PADDING * 2;
            for (Module module : modules) {
                // Проверяем клик по модулю
                if (mouseX >= columnX + PADDING && mouseX <= columnX + COLUMN_WIDTH - PADDING
                    && mouseY >= moduleY && mouseY <= moduleY + MODULE_HEIGHT) {
                    
                    if (button == 0) {
                        // Левая кнопка - toggle модуля
                        module.toggle();
                        return true;
                    } else if (button == 1 && !module.getSettings().isEmpty()) {
                        // Правая кнопка - открыть/закрыть настройки
                        if (GuiScreen.openSettingsModules.contains(module)) {
                            GuiScreen.openSettingsModules.remove(module);
                            GuiScreen.getModuleSettingsAnimation(module).run(0.0, 0.6F, Easings.QUART_OUT);
                            GuiScreen.getModuleSettingsAlphaAnimation(module).run(0.0, 0.16F, Easings.SINE_OUT);
                        } else {
                            GuiScreen.openSettingsModules.add(module);
                            GuiScreen.getModuleSettingsAlphaAnimation(module).run(1.0, 0.16F, Easings.SINE_OUT);
                            GuiScreen.getModuleSettingsAnimation(module).run(1.0, 0.6F, Easings.QUART_OUT);
                        }
                        return true;
                    }
                }
                
                moduleY += MODULE_HEIGHT;
                
                // Обрабатываем клики по настройкам если модуль открыт
                if (GuiScreen.openSettingsModules.contains(module) && !module.getSettings().isEmpty()) {
                    float settingsAnim = GuiScreen.getModuleSettingsAnimation(module).get();
                    
                    if (settingsAnim > 0.5F) {
                        float settingY = moduleY + PADDING;
                        float settingX = columnX + PADDING + SETTINGS_INDENT;
                        float settingWidth = COLUMN_WIDTH - PADDING * 2 - SETTINGS_INDENT - 5.0F;
                        
                        for (Setting setting : module.getSettings()) {
                            if (handleSettingClickClassic(renderer2D, setting, settingX, settingY, settingWidth, mouseX, mouseY, button)) {
                                return true;
                            }
                            settingY += getSettingHeightClassic(renderer2D, setting) * settingsAnim;
                        }
                        
                        moduleY = settingY + PADDING;
                    } else {
                        // Пропускаем высоту настроек для следующего модуля
                        float settingsHeight = PADDING * 2;
                        for (Setting setting : module.getSettings()) {
                            settingsHeight += getSettingHeightClassic(renderer2D, setting);
                        }
                        moduleY += settingsHeight * settingsAnim;
                    }
                }
            }
        }
        
        return false;
    }
    
    private static boolean handleSettingClickClassic(Renderer2D renderer2D, Setting setting, float x, float y, float width, 
            int mouseX, int mouseY, int button) {
        
        if (setting instanceof BooleanSetting boolSetting) {
            float checkBoxSize = 12.0F;
            float checkBoxX = x + width - checkBoxSize;
            float checkBoxY = y + 1.0F;
            if (button == 0 && GuiRenderMain.isHovered(mouseX, mouseY, checkBoxX, checkBoxY, checkBoxSize, checkBoxSize)) {
                boolSetting.set(!boolSetting.get());
                if (Noxium.get.configManager != null) {
                    Noxium.get.configManager.autoSave();
                }
                return true;
            }
        }
        
        if (setting instanceof SliderSetting sliderSetting) {
            float sliderY = y + 12.0F;
            float sliderWidth = width;
            if (button == 0 && GuiRenderMain.isHovered(mouseX, mouseY, x, sliderY - 4.0F, sliderWidth, 12.0F)) {
                GuiScreen.activeSliderSetting = sliderSetting;
                GuiScreen.sliderX = x;
                GuiScreen.sliderY = sliderY;
                GuiScreen.sliderWidth = sliderWidth;
                float progress = (mouseX - x) / sliderWidth;
                progress = Math.max(0.0F, Math.min(1.0F, progress));
                sliderSetting.current = sliderSetting.minimum + (sliderSetting.maximum - sliderSetting.minimum) * progress;
                if (Noxium.get.configManager != null) {
                    Noxium.get.configManager.autoSave();
                }
                return true;
            }
        }
        
        if (setting instanceof ModeSetting modeSetting) {
            float currentModeButtonHeight = 12.0F;
            float currentModeButtonY = y + 11.0F;
            float currentModeWidth = renderer2D.measureText(FontRegistry.INTER_MEDIUM, modeSetting.currentMode, 12.0F).width + 12.0F;
            
            if (button == 0 && GuiRenderMain.isHovered(mouseX, mouseY, x, currentModeButtonY, currentModeWidth, currentModeButtonHeight)) {
                modeSetting.opened = !modeSetting.opened;
                return true;
            }
            
            if (modeSetting.opened && modeSetting.dropdownAnim.get() > 0.5F) {
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

                float dropdownY = currentModeButtonY + currentModeButtonHeight + 2.0F;
                float dropdownHeight = calcY + modeHeight;
                
                if (button == 0 && GuiRenderMain.isHovered(mouseX, mouseY, x, dropdownY, width, dropdownHeight)) {
                    float currentX = padding;
                    float currentY = 2.0F;

                    for (String mode : modeSetting.modes) {
                        if (!mode.equals(modeSetting.currentMode)) {
                            float modeWidth = renderer2D.measureText(FontRegistry.INTER_MEDIUM, mode, 12.0F).width + padding * 2.0F;
                            if (currentX + modeWidth > width && currentX > padding) {
                                currentX = padding;
                                currentY += modeHeight + verticalSpacing;
                            }

                            if (GuiRenderMain.isHovered(mouseX, mouseY, x + currentX, dropdownY + currentY, modeWidth, modeHeight)) {
                                modeSetting.currentMode = mode;
                                modeSetting.index = modeSetting.modes.indexOf(mode);
                                modeSetting.opened = false;
                                if (Noxium.get.configManager != null) {
                                    Noxium.get.configManager.autoSave();
                                }
                                return true;
                            }

                            currentX += modeWidth + modeSpacing;
                        }
                    }
                }
            }
        }
        
        if (setting instanceof BindSettings bindSetting) {
            float bindHeight = 12.0F;
            float keyTextWidth = renderer2D.measureText(FontRegistry.INTER_MEDIUM, 
                bindSetting.active ? "..." : ru.noxium.util.render.utils.KeyUtil.getKey(bindSetting.key), 12.0F).width;
            float buttonWidth = Math.max(20.0F, keyTextWidth + 10.0F);
            float bindButtonX = x + width - buttonWidth;
            
            if (GuiRenderMain.isHovered(mouseX, mouseY, bindButtonX, y, buttonWidth, bindHeight)) {
                if (button == 0) {
                    if (GuiScreen.activeBindSetting != bindSetting) {
                        if (GuiScreen.activeBindSetting != null) {
                            GuiScreen.activeBindSetting.active = false;
                        }
                        GuiScreen.activeBindSetting = bindSetting;
                        bindSetting.active = true;
                    }
                    return true;
                }
            }
        }
        
        if (setting instanceof StringSetting stringSetting) {
            float textFieldHeight = 12.0F;
            float textFieldWidth = width - 45.0F;
            float textFieldX = x + 45.0F;
            
            if (button == 0 && GuiRenderMain.isHovered(mouseX, mouseY, textFieldX, y, textFieldWidth, textFieldHeight)) {
                if (GuiScreen.activeStringSetting != stringSetting) {
                    if (GuiScreen.activeStringSetting != null) {
                        GuiScreen.activeStringSetting.active = false;
                    }
                    GuiScreen.activeStringSetting = stringSetting;
                    stringSetting.active = true;
                }
                return true;
            }
        }
        
        if (setting instanceof MultiBooleanSetting multiBooleanSetting) {
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

                if (button == 0 && GuiRenderMain.isHovered(mouseX, mouseY, currentX, currentY, boolWidth, boolHeight)) {
                    boolSetting.set(!boolSetting.get());
                    if (Noxium.get.configManager != null) {
                        Noxium.get.configManager.autoSave();
                    }
                    return true;
                }

                currentX += boolWidth + spacing;
            }
        }
        
        return false;
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
}
