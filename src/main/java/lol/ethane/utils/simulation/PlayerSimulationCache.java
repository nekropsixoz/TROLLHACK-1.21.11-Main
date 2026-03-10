package lol.ethane.utils.simulation;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import lol.ethane.event.EventDispatcher;
import lol.ethane.event.defined.game.PreGameTickEvent;
import lol.ethane.event.defined.press.MoveInputEvent;
import lol.ethane.event.subscriber.IEventSubscriber;
import lol.ethane.event.subscriber.Subscribe;
import net.minecraft.class_1657;
import net.minecraft.class_310;
import net.minecraft.class_746;

public class PlayerSimulationCache implements IEventSubscriber {
   private static final PlayerSimulationCache INSTANCE = new PlayerSimulationCache();
   private final Map<class_1657, SimulatedPlayerCache> otherPlayerCache = new ConcurrentHashMap();
   private SimulatedPlayerCache localPlayerCache = null;

   private PlayerSimulationCache() {
      EventDispatcher.subscribe(this);
   }

   public static PlayerSimulationCache getInstance() {
      return INSTANCE;
   }

   @Subscribe(
      priority = 100
   )
   public void onGameTick(PreGameTickEvent event) {
      this.otherPlayerCache.clear();
   }

   @Subscribe(
      priority = 200
   )
   public void onCriticalMovementInput(MoveInputEvent event) {
      this.localPlayerCache = null;
      this.updatePlayerCache(event, false);
   }

   @Subscribe
   public void onMovementInput(MoveInputEvent event) {
      this.updatePlayerCache(event, true);
   }

   @Subscribe(
      priority = -100
   )
   public void onModalMovementInput(MoveInputEvent event) {
      this.updatePlayerCache(event, true);
   }

   private void updatePlayerCache(MoveInputEvent event, boolean verify) {
      DirectionalInput directionalInput = DirectionalInput.fromMovement(event.getForward(), event.getSideways());
      if (!verify || this.localPlayerCache == null || !this.localPlayerCache.simulatedPlayer.input.directionalInput.equals(directionalInput)) {
         SimulatedPlayer.SimulatedPlayerInput input = SimulatedPlayer.SimulatedPlayerInput.fromClientPlayer(directionalInput, event.isJump(), event.isSprint(), event.isSneak());
         SimulatedPlayer simulatedPlayer = SimulatedPlayer.fromClientPlayer(input);
         this.localPlayerCache = new SimulatedPlayerCache(simulatedPlayer);
      }
   }

   public SimulatedPlayerCache getSimulationForOtherPlayers(class_1657 player) {
      return (SimulatedPlayerCache)this.otherPlayerCache.computeIfAbsent(player, (p) -> {
         SimulatedPlayer.SimulatedPlayerInput input = SimulatedPlayer.SimulatedPlayerInput.guessInput(p);
         SimulatedPlayer simulatedPlayer = SimulatedPlayer.fromOtherPlayer(p, input);
         return new SimulatedPlayerCache(simulatedPlayer);
      });
   }

   public SimulatedPlayerCache getSimulationForLocalPlayer() {
      if (this.localPlayerCache != null) {
         return this.localPlayerCache;
      } else {
         class_746 player = class_310.method_1551().field_1724;
         if (player == null) {
            return null;
         } else {
            DirectionalInput directionalInput = new DirectionalInput(player.field_3913);
            SimulatedPlayer.SimulatedPlayerInput input = SimulatedPlayer.SimulatedPlayerInput.fromClientPlayer(directionalInput);
            SimulatedPlayer simulatedPlayer = SimulatedPlayer.fromClientPlayer(input);
            this.localPlayerCache = new SimulatedPlayerCache(simulatedPlayer);
            return this.localPlayerCache;
         }
      }
   }
}
