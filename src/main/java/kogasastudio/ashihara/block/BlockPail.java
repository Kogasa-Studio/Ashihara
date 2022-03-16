package kogasastudio.ashihara.block;

import kogasastudio.ashihara.block.tileentities.PailTE;
import kogasastudio.ashihara.block.tileentities.TERegistryHandler;
import kogasastudio.ashihara.helper.FluidHelper;
import kogasastudio.ashihara.item.ItemRegistryHandler;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.state.EnumProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.IBlockDisplayReader;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.common.ToolType;
import net.minecraftforge.fluids.FluidAttributes;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.templates.FluidTank;

import java.util.UUID;

import static kogasastudio.ashihara.item.ItemRegistryHandler.MINATO_AQUA;
import static kogasastudio.ashihara.item.ItemRegistryHandler.PAIL;
import static net.minecraft.fluid.Fluids.*;
import static net.minecraft.item.Items.*;
import static net.minecraft.state.properties.BlockStateProperties.HORIZONTAL_AXIS;

public class BlockPail extends Block
{
    public BlockPail()
    {
        super
        (
            Properties.create(Material.WOOD)
            .hardnessAndResistance(3.0F)
            .harvestTool(ToolType.AXE)
            .sound(SoundType.WOOD)
            .notSolid()
        );
    }

    public static final EnumProperty<Direction.Axis> AXIS = HORIZONTAL_AXIS;


    private ItemStack getIdentifiedItem(World worldIn, BlockPos pos)
    {
        PailTE te = (PailTE) worldIn.getTileEntity(pos);
        ItemStack stack = new ItemStack(PAIL.get());
        if (te != null && !worldIn.isRemote() && !te.getTank().orElse(new FluidTank(0)).isEmpty())
        {
            CompoundNBT nbt = te.write(new CompoundNBT());
            if (!nbt.isEmpty()) stack.setTagInfo("BlockEntityTag", nbt);
        }
        return stack;
    }

    @Override
    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder)
    {
        builder.add(AXIS);
    }

    @Override
    public BlockState getStateForPlacement(BlockItemUseContext context)
    {
        return this.getDefaultState().with(AXIS, context.getPlacementHorizontalFacing().getAxis());
    }

    @Override
    public int getLightValue(BlockState state, IBlockReader world, BlockPos pos)
    {
        int ambientLight = super.getLightValue(state, world, pos);
        if (ambientLight == 15) {return ambientLight;}
        TileEntity tileEntity = world.getTileEntity(pos);
        if (tileEntity != null && tileEntity.getType().equals(TERegistryHandler.PAIL_TE.get()))
        {
            PailTE te = (PailTE) tileEntity;
            FluidStack fluid = te.getTank().orElse(new FluidTank(0)).getFluid();
            if (!fluid.isEmpty())
            {
                FluidAttributes fluidAttributes = fluid.getFluid().getAttributes();
                ambientLight = Math.max(ambientLight, world instanceof IBlockDisplayReader
                ? fluidAttributes.getLuminosity((IBlockDisplayReader) world, pos)
                : fluidAttributes.getLuminosity(fluid));
            }
        }
        return ambientLight;
    }

    @Override
    public void onBlockHarvested(World worldIn, BlockPos pos, BlockState state, PlayerEntity player)
    {
        if (!player.isCreative())
        {
            ItemStack stack = getIdentifiedItem(worldIn, pos);

            ItemEntity entity = new ItemEntity(worldIn, (double) pos.getX() + 0.5d, (double) pos.getY() + 0.5d, pos.getZ() + 0.5d, stack);
            entity.setDefaultPickupDelay();
            worldIn.addEntity(entity);
        }
        super.onBlockHarvested(worldIn, pos, state, player);
    }

    @Override
    public ActionResultType onBlockActivated(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit)
    {
        ItemStack stack = player.getHeldItem(handIn);
        PailTE te = (PailTE) worldIn.getTileEntity(pos);

        if (te == null) return ActionResultType.FAIL;

        FluidTank bucket = te.getTank().orElse(new FluidTank(0));
        if (!stack.isEmpty() && FluidHelper.notifyFluidTankInteraction(player, handIn, stack, bucket))
        {
            player.inventory.markDirty();
            worldIn.notifyBlockUpdate(pos, state, state, 3);
            return ActionResultType.SUCCESS;
        }

        if (stack.isEmpty() && !player.isSneaking())
        {
            ItemStack item = getIdentifiedItem(worldIn, pos);
            player.setHeldItem(handIn, item);
            worldIn.removeBlock(pos, false);
            return ActionResultType.SUCCESS;
        }

        if (stack.getItem().equals(ItemRegistryHandler.KOISHI.get()))
        {
            if (!worldIn.isRemote())
            {
                player.sendMessage
                (
                    new TranslationTextComponent
                    (
                        "\n{\n    fluid: " + bucket.getFluid().getFluid().getRegistryName()
                        + ";\n    amount: " + bucket.getFluidAmount() + ";\n}"
                    ), UUID.randomUUID()
                );
            }
            return ActionResultType.SUCCESS;
        }
        if (stack.getItem().equals(MINATO_AQUA.get()) && !worldIn.isRemote())
        {
            player.sendMessage(new TranslationTextComponent("Debu!"), UUID.randomUUID());
            return ActionResultType.SUCCESS;
        }

        if (!bucket.isEmpty())
        {
            if (stack.getItem().equals(GLASS_BOTTLE) && bucket.getFluid().getFluid().equals(WATER))
            {
                if (bucket.getFluidAmount() >= 250)
                {
                    player.setHeldItem(handIn, new ItemStack(POTION));
                    bucket.drain(250, IFluidHandler.FluidAction.EXECUTE);
                    return ActionResultType.SUCCESS;
                }
                else return ActionResultType.PASS;
            }
            else if (stack.isEmpty() && player.isSneaking())
            {
                bucket.drain(bucket.getFluidAmount(), IFluidHandler.FluidAction.EXECUTE);
                return ActionResultType.SUCCESS;
            }
        }
        return ActionResultType.PASS;
    }

    @Override
    public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context)
    {
        VoxelShape sideN = makeCuboidShape(3.0d, 0.0d, 3.0d, 13.0d, 10.0d, 4.0d);
        VoxelShape sideE = makeCuboidShape(12.0d, 0.0d, 4.0d, 13.0d, 10.0d, 12.0d);
        VoxelShape sideS = makeCuboidShape(3.0d, 0.0d, 12.0d, 13.0d, 10.0d, 13.0d);
        VoxelShape sideW = makeCuboidShape(3.0d, 0.0d, 4.0d, 4.0d, 10.0d, 12.0d);
        VoxelShape bottom = makeCuboidShape(4.0d, 0.5d, 4.0d, 12.0d, 1.5d, 12.0d);
        VoxelShape plankLeftX = makeCuboidShape(7.0d, 10.0d, 3.0d, 9.0d, 16.0d, 4.0d);
        VoxelShape plankRightX = makeCuboidShape(7.0d, 10.0d, 12.0d, 9.0d, 16.0d, 13.0d);
        VoxelShape stickX = makeCuboidShape(7.5d, 14.5d, 4.0d, 8.5d, 15.5d, 12.0d);
        VoxelShape plankLeftZ = makeCuboidShape(3.0d, 10.0d, 7.0d, 4.0d, 16.0d, 9.0d);
        VoxelShape plankRightZ = makeCuboidShape(12.0d, 10.0d, 7.0d, 13.0d, 16.0d, 9.0d);
        VoxelShape stickZ = makeCuboidShape(4.0d, 14.5d, 7.5d, 12.0d, 15.5d, 8.5d);

        VoxelShape bucket = VoxelShapes.or(bottom, sideN, sideE, sideS, sideW);
        VoxelShape handleX = VoxelShapes.or(stickX, plankLeftX, plankRightX);
        VoxelShape handleZ = VoxelShapes.or(stickZ, plankLeftZ, plankRightZ);

        return state.get(AXIS).equals(Direction.Axis.X) ? VoxelShapes.or(bucket, handleX) : VoxelShapes.or(bucket, handleZ);
    }

    @Override
    public boolean hasTileEntity(BlockState state) {return true;}

    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {return new PailTE();}
}
