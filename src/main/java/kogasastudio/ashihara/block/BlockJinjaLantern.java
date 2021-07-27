package kogasastudio.ashihara.block;

import kogasastudio.ashihara.item.ItemRegistryHandler;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootContext;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
//import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;

import java.util.LinkedList;
import java.util.List;

import static kogasastudio.ashihara.utils.EasyBlockActionHandler.getLightValueLit;

public class BlockJinjaLantern extends BlockLantern
{
    public BlockJinjaLantern()
    {
        super
        (
            Properties.create(Material.WOOD)
            .hardnessAndResistance(0.5F)
            .sound(SoundType.WOOD)
            .setLightLevel(getLightValueLit(15))
        );
    }

    @Override
    public List<ItemStack> getDrops(BlockState state, LootContext.Builder builder)
    {
        List<ItemStack> list = new LinkedList<>();
        list.add(new ItemStack(ItemRegistryHandler.ITEM_JINJA_LANTERN.get()));
        return list;
    }

    @Override
    public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context)
    {
        //VoxelShape upper = Block.makeCuboidShape(1.8D, 18.6D, 12.9D, 14.2D, 28.7D, 3.2D);
        return Block.makeCuboidShape(7.5D, 0.0D, 7.5D, 8.5D, 16.0D, 8.5D);
    }
}
