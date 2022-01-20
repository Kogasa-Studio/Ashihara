package kogasastudio.ashihara.block;

import kogasastudio.ashihara.block.tileentities.MortarTE;
import kogasastudio.ashihara.client.particles.GenericParticleData;
import kogasastudio.ashihara.client.particles.ParticleRegistryHandler;
import kogasastudio.ashihara.item.ItemExmpleContainer;
import kogasastudio.ashihara.item.ItemRegistryHandler;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.HorizontalBlock;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.loot.LootContext;
import net.minecraft.network.PacketBuffer;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.IntegerProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.common.ToolType;
import net.minecraftforge.fml.network.NetworkHooks;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;

public class BlockMortar extends Block
{
    public BlockMortar()
    {
        super
        (
            Properties.create(Material.WOOD)
            .hardnessAndResistance(3.0F)
            .harvestTool(ToolType.AXE)
            .sound(SoundType.WOOD)
            .notSolid()
        );
        this.setDefaultState(this.getStateContainer().getBaseState().with(LEVEL, 0)/*.with(PROCESS, 0)*/);
    }

    @Override
    public List<ItemStack> getDrops(BlockState state, LootContext.Builder builder)
    {
        List<ItemStack> list = new LinkedList<>();
        list.add(new ItemStack(ItemRegistryHandler.ITEM_MORTAR.get()));
//        if (state.get(LEVEL) != 0)
//        {
//            if (state.get(PROCESS) == 4) {list.add(new ItemStack(ItemExmpleContainer.RICE, state.get(LEVEL)));}
//            else {list.add(new ItemStack(ItemExmpleContainer.UNTHRESHED_RICE, state.get(LEVEL)));}
//        }
        return list;
    }

    public static final IntegerProperty LEVEL = IntegerProperty.create("level", 0, 4);
//    public static final IntegerProperty PROCESS = IntegerProperty.create("process", 0, 4);
    public static final DirectionProperty FACING = HorizontalBlock.HORIZONTAL_FACING;

    @Override
    public BlockState getStateForPlacement(BlockItemUseContext context)
    {
        return this.getDefaultState().with(FACING, context.getPlacementHorizontalFacing());
    }

    @Override
    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder)
    {
        builder.add(LEVEL/*, PROCESS*/, FACING);
    }

    @Override
    public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context)
    {
        VoxelShape shape1 = Block.makeCuboidShape(2,0,0,14,16,3);
        VoxelShape shape2 = Block.makeCuboidShape(0,0,2,3,16,14);
        VoxelShape shape3 = Block.makeCuboidShape(2,0,13,14,16,16);
        VoxelShape shape4 = Block.makeCuboidShape(13,0,2,16,16,14);
        VoxelShape shape5 = Block.makeCuboidShape(3,0,3,13,3,13);
        return VoxelShapes.or(shape1, shape2, shape3, shape4, shape5);
    }

    /**
     * 当手持稻谷时右击加稻谷（LEVEL）
     * 手持捣杵右击时加进度（PROCESS）
     * 如果进度不为0时加稻谷则重置进度
     * 进度为4时空手右击获得（LEVEL）份白米并重置稻谷和进度
     */
    @Override
    public ActionResultType onBlockActivated(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit)
    {
        if (!worldIn.isRemote() && handIn.equals(Hand.MAIN_HAND))
        {
            ItemStack stack = player.getHeldItem(handIn);
            MortarTE te = (MortarTE) worldIn.getTileEntity(pos);
            if (te == null) return ActionResultType.FAIL;
            if (player.isSneaking() && stack.isEmpty())
            {
                NetworkHooks.openGui((ServerPlayerEntity) player, te, (PacketBuffer packerBuffer) -> packerBuffer.writeBlockPos(te.getPos()));
                return ActionResultType.SUCCESS;
            }
            else if (te.notifyInteraction(stack, player, worldIn, pos, player))
            {
                worldIn.setBlockState(pos, state.with(LEVEL, te.getContentsActualSize()));
                return ActionResultType.SUCCESS;
            }
        }
        return ActionResultType.PASS;
        /*int level = state.get(LEVEL);
        int process = state.get(PROCESS);
        ItemStack item = player.getHeldItem(handIn);
        if (item.getItem() == ItemExmpleContainer.UNTHRESHED_RICE)
        {
            if (level < 4 && process != 4)
            {
                worldIn.playSound(player, pos, SoundEvents.BLOCK_SAND_PLACE, SoundCategory.BLOCKS, 1.0F, 1.0F);
                worldIn.setBlockState(pos, state.with(LEVEL, level + 1).with(PROCESS, 0));
                item.shrink(1);
                return ActionResultType.SUCCESS;
            } else return ActionResultType.PASS;
        }
        else if (item.getItem() == ItemExmpleContainer.PESTLE)
        {
            if (!player.getCooldownTracker().hasCooldown(item.getItem()) && (level != 0 && process != 4))
            {
                Random rand = new Random();
                worldIn.playSound(player, pos, SoundEvents.BLOCK_WOOD_PLACE, SoundCategory.BLOCKS, 1.0F, 1.0F);
                worldIn.setBlockState(pos, state.with(PROCESS, process + 1));
                for(byte b = 0; b < 12; b += 1)
                {
                    worldIn.addParticle(new GenericParticleData(new Vector3d(0,0,0), 0, ParticleRegistryHandler.RICE.get()), (double)pos.getX() + 0.5D, (double)pos.getY() + 0.5D, (double)pos.getZ() + 0.5D, rand.nextFloat() / 2.0F, 5.0E-5D, rand.nextFloat() / 2.0F);
                }
                player.getCooldownTracker().setCooldown(item.getItem(), 8);
                if (!player.isCreative()) {item.damageItem(1, player, (playerEntity) -> player.sendBreakAnimation(handIn));}
                return ActionResultType.SUCCESS;
            } else return ActionResultType.PASS;
        }
        else if (item.getItem() == Items.AIR)
        {
            if (process == 4)
            {
                InventoryHelper.spawnItemStack(worldIn, pos.getX(), pos.getY() + 0.5, pos.getZ(), new ItemStack(ItemExmpleContainer.RICE, level));
                worldIn.setBlockState(pos, state.with(LEVEL, 0).with(PROCESS, 0));
                return ActionResultType.SUCCESS;
            } else return ActionResultType.PASS;
        }
        else return ActionResultType.PASS;*/
    }

    @Override
    public boolean hasTileEntity(BlockState state) {return true;}

    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {return new MortarTE();}
}
