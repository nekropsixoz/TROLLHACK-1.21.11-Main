package ru.noxium.ui.gui;

import net.minecraft.client.gui.Click;
import net.minecraft.client.input.KeyInput;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;
import ru.noxium.proxy.ProxyManager;

public class ProxyScreen extends Screen {
    private final Screen parent;
    private TextFieldWidget ipPortField;
    private TextFieldWidget usernameField;
    private TextFieldWidget passwordField;
    private boolean proxyEnabled;
    private String errorText = "";
    private long errorTime = 0;

    public ProxyScreen(Screen parent) {
        super(Text.literal("Proxy Setting"));
        this.parent = parent;
        this.proxyEnabled = ProxyManager.data.enabled;
    }

    @Override
    protected void init() {
        int centerX = width / 2;
        int centerY = height / 2;

        ipPortField = new TextFieldWidget(textRenderer, centerX - 70, centerY - 45, 140, 20, Text.literal(""));
        ipPortField.setMaxLength(100);
        ipPortField.setText(
                ProxyManager.data.host + (ProxyManager.data.host.isEmpty() ? "" : ":" + ProxyManager.data.port));
        addSelectableChild(ipPortField);

        usernameField = new TextFieldWidget(textRenderer, centerX - 70, centerY, 140, 20, Text.literal(""));
        usernameField.setMaxLength(100);
        usernameField.setText(ProxyManager.data.username);
        addSelectableChild(usernameField);

        passwordField = new TextFieldWidget(textRenderer, centerX - 70, centerY + 45, 140, 20, Text.literal(""));
        passwordField.setMaxLength(100);
        passwordField.setText(ProxyManager.data.password);
        addSelectableChild(passwordField);
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        this.renderInGameBackground(context);

        int centerX = width / 2;
        int centerY = height / 2;

        // Draw Main Panel Background (Vanilla style)
        context.fill(centerX - 120, centerY - 100, centerX + 120, centerY + 140, 0xCC000000);
        // Manual borders
        context.fill(centerX - 120, centerY - 100, centerX + 120, centerY - 99, 0xFFFFFFFF); // top
        context.fill(centerX - 120, centerY + 139, centerX + 120, centerY + 140, 0xFFFFFFFF); // bottom
        context.fill(centerX - 120, centerY - 100, centerX - 119, centerY + 140, 0xFFFFFFFF); // left
        context.fill(centerX + 119, centerY - 100, centerX + 120, centerY + 140, 0xFFFFFFFF); // right

        context.drawCenteredTextWithShadow(textRenderer, "PROXY CONFIGURATION", centerX, centerY - 85, 0xFFFFFF);

        // Labels
        context.drawTextWithShadow(textRenderer, "IP:Port", centerX - 110, centerY - 58, 0xAAAAAA);
        context.drawTextWithShadow(textRenderer, "Username", centerX - 110, centerY - 13, 0xAAAAAA);
        context.drawTextWithShadow(textRenderer, "Password", centerX - 110, centerY + 32, 0xAAAAAA);

        // Checkbox
        int checkboxColor = proxyEnabled ? 0xFF00FF00 : 0xFFFF0000;
        context.fill(centerX - 110, centerY + 75, centerX - 100, centerY + 85, checkboxColor);
        context.drawTextWithShadow(textRenderer, "Enable Proxy", centerX - 95, centerY + 76, 0xFFFFFF);

        // Buttons (Simple vanilla style)
        drawVanillaButton(context, centerX - 110, centerY + 105, 70, 20, "Save", mouseX, mouseY);
        drawVanillaButton(context, centerX - 35, centerY + 105, 70, 20, "Import", mouseX, mouseY);
        drawVanillaButton(context, centerX + 40, centerY + 105, 70, 20, "Back", mouseX, mouseY);

        // Error text
        if (!errorText.isEmpty() && System.currentTimeMillis() - errorTime < 3000) {
            context.drawCenteredTextWithShadow(textRenderer, errorText, centerX, centerY + 130, 0xFF5555);
        }

        // Render fields (Minecraft logic)
        ipPortField.render(context, mouseX, mouseY, delta);
        usernameField.render(context, mouseX, mouseY, delta);
        passwordField.render(context, mouseX, mouseY, delta);
    }

    private void drawVanillaButton(DrawContext context, int x, int y, int w, int h, String text, int mouseX,
            int mouseY) {
        boolean hovered = mouseX >= x && mouseX <= x + w && mouseY >= y && mouseY <= y + h;
        context.fill(x, y, x + w, y + h, hovered ? 0xFF555555 : 0xFF333333);
        // Manual button border
        context.fill(x, y, x + w, y + 1, 0xFFFFFFFF); // top
        context.fill(x, y + h - 1, x + w, y + h, 0xFFFFFFFF); // bottom
        context.fill(x, y, x + 1, y + h, 0xFFFFFFFF); // left
        context.fill(x + w - 1, y, x + w, y + h, 0xFFFFFFFF); // right
        context.drawCenteredTextWithShadow(textRenderer, text, x + w / 2, y + h / 2 - 4, 0xFFFFFF);
    }

    @Override
    public boolean mouseClicked(Click click, boolean bl) {
        double mouseX = click.x();
        double mouseY = click.y();
        int centerX = width / 2;
        int centerY = height / 2;

        // Checkbox
        if (mouseX >= centerX - 115 && mouseX <= centerX - 10 && mouseY >= centerY + 70 && mouseY <= centerY + 90) {
            proxyEnabled = !proxyEnabled;
            return true;
        }

        // Save
        if (mouseX >= centerX - 110 && mouseX <= centerX - 40 && mouseY >= centerY + 105 && mouseY <= centerY + 125) {
            save();
            client.setScreen(parent);
            return true;
        }

        // Import
        if (mouseX >= centerX - 35 && mouseX <= centerX + 35 && mouseY >= centerY + 105 && mouseY <= centerY + 125) {
            importFromClipboard();
            return true;
        }

        // Back
        if (mouseX >= centerX + 40 && mouseX <= centerX + 110 && mouseY >= centerY + 105 && mouseY <= centerY + 125) {
            client.setScreen(parent);
            return true;
        }

        return super.mouseClicked(click, bl);
    }

    private void save() {
        String text = ipPortField.getText();
        if (text.contains(":")) {
            String[] split = text.split(":");
            ProxyManager.data.host = split[0];
            try {
                ProxyManager.data.port = Integer.parseInt(split[1]);
            } catch (Exception e) {
                ProxyManager.data.port = 1080;
            }
        } else {
            ProxyManager.data.host = text;
            ProxyManager.data.port = 1080;
        }
        ProxyManager.data.username = usernameField.getText();
        ProxyManager.data.password = passwordField.getText();
        ProxyManager.data.enabled = proxyEnabled;
        ProxyManager.save();
    }

    private void importFromClipboard() {
        String clipboard = client.keyboard.getClipboard();
        if (clipboard == null || clipboard.isEmpty()) {
            showError("Clipboard is empty");
            return;
        }
        try {
            if (clipboard.contains("@")) {
                String[] atSplit = clipboard.split("@");
                String[] auth = atSplit[0].split(":");
                String[] addr = atSplit[1].split(":");
                ipPortField.setText(addr[0] + ":" + addr[1]);
                usernameField.setText(auth[0]);
                if (auth.length > 1)
                    passwordField.setText(auth[1]);
            } else {
                String[] split = clipboard.split(":");
                if (split.length >= 2) {
                    ipPortField.setText(split[0] + ":" + split[1]);
                    if (split.length >= 4) {
                        usernameField.setText(split[2]);
                        passwordField.setText(split[3]);
                    }
                } else
                    showError("Invalid data from clipboard");
            }
        } catch (Exception e) {
            showError("Invalid data from clipboard");
        }
    }

    private void showError(String text) {
        errorText = text;
        errorTime = System.currentTimeMillis();
    }

    @Override
    public boolean keyPressed(KeyInput input) {
        if (input.key() == GLFW.GLFW_KEY_ESCAPE) {
            client.setScreen(parent);
            return true;
        }
        return super.keyPressed(input);
    }

    @Override
    public void close() {
        client.setScreen(parent);
    }

    @Override
    public boolean shouldPause() {
        return false;
    }
}
