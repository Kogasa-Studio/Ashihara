package kogasastudio.ashihara.block;

import kogasastudio.ashihara.item.ItemExmpleContainer;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootContext;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraftforge.common.ToolType;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;

public class BlockDirtDepression extends Block
{
    public BlockDirtDepression()
    {
        super
        (
            Properties.create(Material.EARTH)
            .hardnessAndResistance(0.5F)
            .harvestTool(ToolType.SHOVEL)
            .harvestLevel(2)
            .sound(SoundType.GROUND)
            .notSolid()
        );
    }

    @Override
    public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) //碰撞箱的设定
    {
        return Block.makeCuboidShape(0.0D, 0.0D, 0.0D, 16.0D, 10.0D, 16.0D);
    }

    @Override
    public List<ItemStack> getDrops(BlockState state, LootContext.Builder builder)
    {
        List<ItemStack> list = new LinkedList<>();
        Random rand = new Random();
        list.add(new ItemStack(ItemExmpleContainer.DIRT_BALL, rand.nextInt(3) + 1));
        return list;
    }
}
