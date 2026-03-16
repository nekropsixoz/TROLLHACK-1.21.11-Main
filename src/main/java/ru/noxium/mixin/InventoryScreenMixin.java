package ru.noxium.mixin;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import ru.noxium.util.DropAllManager;

@Mixin(InventoryScreen.class)
public abstract class InventoryScreenMixin extends Screen {

    private ButtonWidget dropAllButton;

    protected InventoryScreenMixin(Text title) {
        super(title);
    }

    @Inject(method = "init", at = @At("TAIL"))
    private void onInit(CallbackInfo ci) {
        // Calculate button position (centered above inventory)
        int x = this.width / 2 - 40;
        int y = this.height / 2 - 80;

        dropAllButton = ButtonWidget.builder(Text.literal("Выбросить все"), button -> {
            DropAllManager.getInstance().startDropping();
        })
                .dimensions(x, y, 80, 20)
                .build();

        this.addDrawableChild(dropAllButton);
    }

    @Inject(method = "render", at = @At("TAIL"))
    private void onRender(DrawContext context, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        // Update drop all manager
        DropAllManager.getInstance().tick();
    }
}
