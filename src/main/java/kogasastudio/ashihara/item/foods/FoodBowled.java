package kogasastudio.ashihara.item.foods;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.world.World;

import net.minecraft.item.Item.Properties;

public class FoodBowled extends Item
{
    public FoodBowled(Properties properties) {super(properties.stacksTo(1));}

    @Override
    public ItemStack finishUsingItem(ItemStack stack, World worldIn, LivingEntity entityLiving)
    {
        ItemStack itemstack = super.finishUsingItem(stack, worldIn, entityLiving);
        return entityLiving instanceof PlayerEntity && ((PlayerEntity)entityLiving).abilities.instabuild ? itemstack : new ItemStack(Items.BOWL);
    }
}
