package kogasastudio.ashihara.block;

import kogasastudio.ashihara.item.ItemExmpleContainer;
import net.minecraft.block.BlockState;
import net.minecraft.block.CropsBlock;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootContext;
import net.minecraft.util.IItemProvider;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class BlockImmatureRiceCrop extends CropsBlock
{
    public BlockImmatureRiceCrop()
    {
        super
        (
            Properties.create(Material.PLANTS)
            .doesNotBlockMovement()
            .tickRandomly()
            .zeroHardnessAndResistance()
            .sound(SoundType.CROP)
        );
    }

    @Override
    protected IItemProvider getSeedsItem()
    {
        return ItemExmpleContainer.UNTHRESHED_RICE;
    }

    @Override
    public int getMaxAge()
    {
        return 2;
    }

    @Override
    public List<ItemStack> getDrops(BlockState state, LootContext.Builder builder)
    {
        List<ItemStack> list = new LinkedList<>();
        if (this.getAge(state) == 2)
        {
            list.add(new ItemStack(ItemExmpleContainer.ITEM_RICE_SEEDLING));
            return list;
        }
        else if (this.getAge(state) <= 1)
        {
            list.add(new ItemStack(ItemExmpleContainer.UNTHRESHED_RICE));
            return list;
        }
        else return Collections.emptyList();
    }
}
