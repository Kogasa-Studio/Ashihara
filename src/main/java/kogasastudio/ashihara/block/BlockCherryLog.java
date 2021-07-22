package kogasastudio.ashihara.block;

import kogasastudio.ashihara.item.ItemRegistryHandler;
import net.minecraft.block.BlockState;
import net.minecraft.block.RotatedPillarBlock;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootContext;
import net.minecraft.util.Direction;

import java.util.LinkedList;
import java.util.List;

public class BlockCherryLog extends RotatedPillarBlock
{
    public BlockCherryLog()
    {
        super
        (
            Properties.create(Material.WOOD, (state) ->
            state.get(RotatedPillarBlock.AXIS) == Direction.Axis.Y ? MaterialColor.ADOBE : MaterialColor.STONE)
            .hardnessAndResistance(2.0F)
            .sound(SoundType.WOOD)
        );
    }

    @Override
    public List<ItemStack> getDrops(BlockState state, LootContext.Builder builder)
    {
        List<ItemStack> list = new LinkedList<>();
        list.add(new ItemStack(ItemRegistryHandler.ITEM_CHERRY_LOG.get()));
        return list;
    }
}
