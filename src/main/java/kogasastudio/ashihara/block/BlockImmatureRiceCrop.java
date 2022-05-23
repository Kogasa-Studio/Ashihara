package kogasastudio.ashihara.block;

import kogasastudio.ashihara.item.ItemRegistryHandler;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.storage.loot.LootContext;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class BlockImmatureRiceCrop extends CropBlock
{
    public BlockImmatureRiceCrop()
    {
        super
        (
            BlockBehaviour.Properties.of(Material.PLANT)
            .noCollission()
            .randomTicks()
            .instabreak()
            .sound(SoundType.CROP)
        );
    }

    @Override
    protected ItemLike getBaseSeedId()
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
        else {
            return Collections.emptyList();
        }
    }
}
