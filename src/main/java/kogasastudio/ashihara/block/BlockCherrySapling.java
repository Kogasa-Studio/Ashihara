package kogasastudio.ashihara.block;

import kogasastudio.ashihara.block.trees.CherryBlossomTree;
import kogasastudio.ashihara.item.ItemRegistryHandler;
import net.minecraft.block.BlockState;
import net.minecraft.block.SaplingBlock;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootContext;

import java.util.LinkedList;
import java.util.List;

public class BlockCherrySapling extends SaplingBlock
{
    public BlockCherrySapling()
    {
        super(new CherryBlossomTree(), Properties.create(Material.PLANTS).doesNotBlockMovement().tickRandomly().zeroHardnessAndResistance().sound(SoundType.PLANT));
    }

    @Override
    public List<ItemStack> getDrops(BlockState state, LootContext.Builder builder)
    {
        List<ItemStack> list = new LinkedList<>();
        list.add(new ItemStack(ItemRegistryHandler.ITEM_CHERRY_SAPLING.get()));
        return list;
    }
}
