package kogasastudio.ashihara.item;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public interface IHasPreSwing
{
    void prepareAttack(ItemStack stack, Player player, Entity target);
    void actuallyAttack(ItemStack stack, Player entity);
}
