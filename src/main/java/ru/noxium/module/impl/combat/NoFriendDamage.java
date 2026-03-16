package ru.noxium.module.impl.combat;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import ru.noxium.module.api.Category;
import ru.noxium.module.api.IModule;
import ru.noxium.module.api.Module;

@IModule(
   name = "No Friend Damage",
   description = "Жозки авто свин",
   category = Category.Combat,
   bind = -1
)
@Environment(EnvType.CLIENT)
public class NoFriendDamage extends Module {
}
