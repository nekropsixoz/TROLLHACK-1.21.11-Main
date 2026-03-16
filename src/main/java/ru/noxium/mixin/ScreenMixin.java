package ru.noxium.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import ru.noxium.Noxium;
import ru.noxium.event.EventManager;
import ru.noxium.event.impl.EventScreen;
import ru.noxium.util.render.core.Renderer2D;
import ru.noxium.util.render.text.FontRegistry;

@Environment(EnvType.CLIENT)
@Mixin(Screen.class)
public class ScreenMixin {

    @Inject(method = "render", at = @At("TAIL"))
    private void onRenderScreen(DrawContext context, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (Noxium.isModInitialized() && client != null) {
            Noxium.ensureRendererInitialized();
            Renderer2D renderer = Noxium.getRenderer();
            if (renderer != null) {
                int width = client.getWindow().getFramebufferWidth();
                int height = client.getWindow().getFramebufferHeight();
                if (width > 0 && height > 0) {
                    boolean rendererBegun = false;
                    try {
                        renderer.begin(width, height);
                        rendererBegun = true;
                        EventManager.call(
                                new EventScreen(client, renderer, FontRegistry.INTER_MEDIUM, width, height, context));
                    } finally {
                        if (rendererBegun) {
                            renderer.end();
                        }
                    }
                }
            }
        }
    }
}
