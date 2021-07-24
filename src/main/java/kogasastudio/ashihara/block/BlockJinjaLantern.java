package kogasastudio.ashihara.block;

import kogasastudio.ashihara.item.ItemRegistryHandler;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.HorizontalBlock;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.loot.LootContext;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
//import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import static kogasastudio.ashihara.utils.EasyBlockActionHandler.getLightValueLit;

public class BlockJinjaLantern extends Block
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
        this.setDefaultState(this.stateContainer.getBaseState().with(LIT, Boolean.FALSE).with(FACING, Direction.NORTH));
    }
    public static final BooleanProperty LIT = BlockStateProperties.LIT;
    public static final DirectionProperty FACING = HorizontalBlock.HORIZONTAL_FACING;

    @Override
    public BlockState getStateForPlacement(BlockItemUseContext context)
    {
        return this.getDefaultState().with(FACING, context.getPlacementHorizontalFacing().rotateY());
    }

    @Override
    public List<ItemStack> getDrops(BlockState state, LootContext.Builder builder)
    {
        List<ItemStack> list = new LinkedList<>();
        list.add(new ItemStack(ItemRegistryHandler.ITEM_JINJA_LANTERN.get()));
        return list;
    }

    @Override
    public ActionResultType onBlockActivated(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit)
    {
        if(player.getHeldItem(handIn).getItem() == Items.AIR)
        {
            Random random = new Random();
            Boolean instantState = worldIn.getBlockState(pos).get(LIT);
            worldIn.playSound(player, pos, SoundEvents.ITEM_FLINTANDSTEEL_USE, SoundCategory.BLOCKS, 1.0F, random.nextFloat() * 0.4F + 0.8F);
            worldIn.setBlockState(pos, state.with(LIT, !instantState));
            return ActionResultType.SUCCESS;
        }
        else return ActionResultType.PASS;
    }

    @Override
    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) //不明所以地类似注册BS的东西
    {
        builder.add(LIT, FACING);
    }

    @Override
    public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context)
    {
        //VoxelShape upper = Block.makeCuboidShape(1.8D, 18.6D, 12.9D, 14.2D, 28.7D, 3.2D);
        return Block.makeCuboidShape(7.5D, 0.0D, 7.5D, 8.5D, 16.0D, 8.5D);
    }
}
