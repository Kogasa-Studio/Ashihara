package kogasastudio.ashihara.block;

import kogasastudio.ashihara.block.tileentities.MortarTE;
import kogasastudio.ashihara.helper.FluidHelper;
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
import net.minecraft.network.PacketBuffer;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.IntegerProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.common.ToolType;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import net.minecraftforge.fml.network.NetworkHooks;

import java.util.*;

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
        this.setDefaultState(this.getStateContainer().getBaseState().with(LEVEL, 0));
    }

    public static final IntegerProperty LEVEL = IntegerProperty.create("level", 0, 4);
    public static final DirectionProperty FACING = HorizontalBlock.HORIZONTAL_FACING;

    @Override
    public BlockState getStateForPlacement(BlockItemUseContext context)
    {
        return this.getDefaultState().with(FACING, context.getPlacementHorizontalFacing());
    }

    @Override
    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder)
    {
        builder.add(LEVEL, FACING);
    }

    @Override
    public void harvestBlock(World worldIn, PlayerEntity player, BlockPos pos, BlockState state, TileEntity te, ItemStack stack)
    {
        if (te instanceof MortarTE)
        {
            NonNullList<ItemStack> stacks = NonNullList.create();
            for (int i = 0; i < ((MortarTE) te).contents.getSlots(); i += 1)
            {
                ItemStack stackI = ((MortarTE) te).contents.getStackInSlot(i);
                if (!stackI.isEmpty()) stacks.add(stackI);
            }
            InventoryHelper.dropItems(worldIn, pos, stacks);
        }
        super.harvestBlock(worldIn, player, pos, state, te, stack);
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

    @Override
    public ActionResultType onBlockActivated(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit)
    {
        ItemStack stack = player.getHeldItem(handIn);
        MortarTE te = (MortarTE) worldIn.getTileEntity(pos);
        if (te == null) return ActionResultType.FAIL;

        FluidTank tank = te.getTank().orElse(new FluidTank(0));
        if (!stack.isEmpty() && FluidHelper.notifyFluidTankInteraction(player, handIn, stack, tank))
        {
            player.inventory.markDirty();
            worldIn.notifyBlockUpdate(pos, state, state, 3);
            return ActionResultType.SUCCESS;
        }
        else if (!worldIn.isRemote() && handIn.equals(Hand.MAIN_HAND))
        {
            if (player.isSneaking() && stack.isEmpty())
            {
                NetworkHooks.openGui((ServerPlayerEntity) player, te, (PacketBuffer packerBuffer) -> packerBuffer.writeBlockPos(te.getPos()));
                return ActionResultType.SUCCESS;
            }
            else if (te.notifyInteraction(stack, worldIn, pos, player))
            {
                worldIn.setBlockState(pos, state.with(LEVEL, te.getContentsActualSize()));
                return ActionResultType.SUCCESS;
            }

            if (stack.getItem().equals(ItemRegistryHandler.KOISHI.get()))
            {
                player.sendMessage
                (
                    new TranslationTextComponent
                    (
                    "\n{\n    te_contents: " + te.contents.toString()
                    + ";\n    te_contained_output: " + te.output.toString()
                    + ";\n    te_progress: " + te.progress
                    + ";\n    te_progress_total: " + te.progressTotal
                    + ";\n    te_pointer: " + te.pointer
                    + ";\n    te_recipeType: " + te.recipeType
                    + ";\n    te_sequence: " + Arrays.toString(te.sequence)
                    + ";\n    te_next_step: " + te.nextStep
                    + ";\n    te_working_statement_code: " + te.isWorking
                    ),
                    UUID.randomUUID()
                );
            }
        }
        return ActionResultType.PASS;
    }

    @Override
    public boolean hasTileEntity(BlockState state) {return true;}

    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {return new MortarTE();}
}
