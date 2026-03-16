package ru.noxium.rpc;

import com.sun.jna.Structure;
import java.util.Arrays;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import ru.noxium.rpc.callbacks.DisconnectedCallback;
import ru.noxium.rpc.callbacks.ErroredCallback;
import ru.noxium.rpc.callbacks.JoinGameCallback;
import ru.noxium.rpc.callbacks.JoinRequestCallback;
import ru.noxium.rpc.callbacks.ReadyCallback;
import ru.noxium.rpc.callbacks.SpectateGameCallback;

@Environment(EnvType.CLIENT)
public class DiscordEventHandlers extends Structure {
   public DisconnectedCallback disconnected;
   public JoinRequestCallback joinRequest;
   public SpectateGameCallback spectateGame;
   public ReadyCallback ready;
   public ErroredCallback errored;
   public JoinGameCallback joinGame;

   protected List<String> getFieldOrder() {
      return Arrays.asList("ready", "disconnected", "errored", "joinGame", "spectateGame", "joinRequest");
   }
}
