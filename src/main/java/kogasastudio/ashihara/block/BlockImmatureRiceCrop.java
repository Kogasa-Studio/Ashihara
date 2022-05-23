package kogasastudio.ashihara.block;

import kogasastudio.ashihara.item.ItemRegistryHandler;
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

import net.minecraft.block.AbstractBlock.Properties;

public class BlockImmatureRiceCrop extends CropsBlock
{
    public BlockImmatureRiceCrop()
    {
        super
        (
            Properties.of(Material.PLANT)
            .noCollission()
            .randomTicks()
            .instabreak()
            .sound(SoundType.CROP)
        );
    }

    @Override
    protected IItemProvider getBaseSeedId()
    {
        return ItemRegistryHandler.PADDY.get();
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
            list.add(new ItemStack(ItemRegistryHandler.RICE_SEEDLING.get()));
            return list;
        }
        else if (this.getAge(state) <= 1)
        {
            list.add(new ItemStack(ItemRegistryHandler.PADDY.get()));
            return list;
        }
        else return Collections.emptyList();
    }
}
