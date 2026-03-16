package ru.noxium.rpc;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import ru.noxium.util.other.IMinecraft;

@Environment(EnvType.CLIENT)
public class RPC implements IMinecraft {
   public static DiscordRichPresence presence = new DiscordRichPresence();
   public static boolean started;
   private static Thread thread;

   public void startRpc() {
      if (DiscordRPC.Loader.isAvailable()) {
         DiscordRPC rpc = DiscordRPC.Loader.getInstance();
         if (!started) {
            started = true;
            DiscordEventHandlers handlers = new DiscordEventHandlers();
            rpc.Discord_Initialize("", handlers, true, "");
            presence.startTimestamp = System.currentTimeMillis() / 1000L;
            presence.largeImageText = "";
            rpc.Discord_UpdatePresence(presence);
            thread = new Thread(() -> {
               while (!Thread.currentThread().isInterrupted()) {
                  rpc.Discord_RunCallbacks();
                  presence.details = "Build 1.0.0 [Dev]";
                  presence.state = "Update to soon";
                  presence.button_label_1 = "Telegram";
                  presence.button_url_1 = "https://t.me/";
                  presence.button_label_2 = "Discord";
                  presence.button_url_2 = "https://discord.com/";
                  rpc.Discord_UpdatePresence(presence);

                  try {
                     Thread.sleep(2000L);
                  } catch (InterruptedException var2x) {
                  }
               }
            }, "TH-RPC-Handler");
            thread.start();
         }
      }
   }
}
