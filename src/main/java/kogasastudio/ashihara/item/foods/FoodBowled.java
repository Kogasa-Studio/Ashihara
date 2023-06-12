package kogasastudio.ashihara.item.foods;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;

public class FoodBowled extends Item
{
    public FoodBowled(Properties properties)
    {
        super(properties.stacksTo(1));
    }

    @Override
    public ItemStack finishUsingItem(ItemStack stack, Level worldIn, LivingEntity entityLiving)
    {
        ItemStack itemstack = super.finishUsingItem(stack, worldIn, entityLiving);
        return entityLiving instanceof Player && ((Player) entityLiving).getAbilities().instabuild ? itemstack : new ItemStack(Items.BOWL);
    }
}
