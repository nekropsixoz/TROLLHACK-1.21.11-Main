package lol.ethane.event.defined.player;

import lol.ethane.event.EventCancellable;
import lombok.Generated;

public class PlayerMovementTickEvent extends EventCancellable {
   private final PlayerMovementTickEvent.State state;
   private boolean ground;

   @Generated
   public PlayerMovementTickEvent.State getState() {
      return this.state;
   }

   @Generated
   public boolean isGround() {
      return this.ground;
   }

   @Generated
   public void setGround(boolean ground) {
      this.ground = ground;
   }

   @Generated
   public PlayerMovementTickEvent(PlayerMovementTickEvent.State state, boolean ground) {
      this.state = state;
      this.ground = ground;
   }

   public static enum State {
      PRE,
      POST;

      // $FF: synthetic method
      private static PlayerMovementTickEvent.State[] $values() {
         return new PlayerMovementTickEvent.State[]{PRE, POST};
      }
   }
}
