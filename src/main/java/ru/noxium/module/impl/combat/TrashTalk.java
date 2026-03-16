package ru.noxium.module.impl.combat;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import ru.noxium.event.EventInit;
import ru.noxium.event.player.AttackEvent;
import ru.noxium.event.impl.EventUpdate;
import ru.noxium.module.api.Category;
import ru.noxium.module.api.IModule;
import ru.noxium.module.api.Module;
import ru.noxium.module.api.setting.Setting;
import ru.noxium.module.api.setting.impl.BooleanSetting;
import ru.noxium.module.api.setting.impl.StringSetting;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@IModule(
   name = "TrashTalk",
   description = "Sends messages after killing players",
   category = Category.Combat,
   bind = -1
)
@Environment(EnvType.CLIENT)
public class TrashTalk extends Module {
   public static StringSetting customMessage = new StringSetting("CustomMessage", "owned by TROLLHACK 1.21.11");
   public static BooleanSetting useCustom = new BooleanSetting("UseCustom", false);

   private final Map<UUID, Float> damagedEntities = new HashMap<>();
   private final Map<UUID, Long> deathTimes = new HashMap<>();

   public TrashTalk() {
      this.addSettings(new Setting[]{customMessage, useCustom});
   }

   @EventInit
   public void onAttack(AttackEvent event) {
      if (event.getTarget() instanceof LivingEntity living) {
         UUID id = living.getUuid();
         float health = living.getHealth();
         
         // Store damaged entities
         if (!damagedEntities.containsKey(id)) {
            damagedEntities.put(id, living.getMaxHealth());
         }
         
         // Update last known health
         damagedEntities.put(id, health);
      }
   }

   @EventInit
   public void onUpdate(EventUpdate event) {
      if (mc.world == null || mc.player == null) {
         return;
      }

      // Check for deaths
      for (Map.Entry<UUID, Float> entry : damagedEntities.entrySet()) {
         UUID entityId = entry.getKey();
         
         // Find the entity in the world
         Entity entity = mc.world.getEntityById(entityId.hashCode());
         
         if (entity instanceof PlayerEntity player) {
            // Check if player died (health <= 0 or entity removed)
            if (player.getHealth() <= 0 || !player.isAlive()) {
               // Check if we already sent a message recently (prevent spam)
               Long lastDeathTime = deathTimes.get(entityId);
               long currentTime = System.currentTimeMillis();
               
               if (lastDeathTime == null || (currentTime - lastDeathTime) > 5000) {
                  sendTrashTalk(player.getName().getString());
                  deathTimes.put(entityId, currentTime);
               }
            }
         } else if (entity == null) {
            // Entity was removed from world (probably died)
            Long lastDeathTime = deathTimes.get(entityId);
            long currentTime = System.currentTimeMillis();
            
            if (lastDeathTime == null || (currentTime - lastDeathTime) > 5000) {
               // Try to get player name from cache or use generic message
               sendGenericKill();
               deathTimes.put(entityId, currentTime);
            }
         }
      }

      // Clean up dead entities from damage map
      damagedEntities.entrySet().removeIf(entry -> {
         Entity entity = mc.world.getEntityById(entry.getKey().hashCode());
         return entity == null || (entity instanceof LivingEntity && ((LivingEntity) entity).getHealth() <= 0);
      });
   }

   private void sendTrashTalk(String playerName) {
      if (playerName == null || playerName.isEmpty()) {
         return;
      }

      String message;
      if (useCustom.get() && !customMessage.get().isEmpty()) {
         message = playerName + " - " + customMessage.get();
      } else {
         message = playerName + " -1\n-1 owned by TROLLHACK 1.21.11\n-1 looser\n-1 own by nekro";
      }

      // Send message to chat
      if (mc.player != null) {
         mc.player.sendMessage(net.minecraft.text.Text.literal(message), false);
      }
   }

   private void sendGenericKill() {
      String message;
      if (useCustom.get() && !customMessage.get().isEmpty()) {
         message = "-1 " + customMessage.get();
      } else {
         message = "-1 owned by TROLLHACK 1.21.11\n-1 looser\n-1 own by nekro";
      }

      if (mc.player != null) {
         mc.player.sendMessage(net.minecraft.text.Text.literal(message), false);
      }
   }

   @Override
   public void onDisable() {
      super.onDisable();
      damagedEntities.clear();
      deathTimes.clear();
   }
}
