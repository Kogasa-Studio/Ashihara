package kogasastudio.ashihara.block;

import kogasastudio.ashihara.block.tileentities.MortarTE;
import kogasastudio.ashihara.client.particles.GenericParticleData;
import kogasastudio.ashihara.client.particles.ParticleRegistryHandler;
import kogasastudio.ashihara.helper.FluidHelper;
import kogasastudio.ashihara.item.ItemOtsuchi;
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
import net.minecraft.state.StateContainer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.common.ToolType;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import net.minecraftforge.fml.network.NetworkHooks;

import java.util.*;

import static kogasastudio.ashihara.utils.AshiharaTags.CEREALS;
import static kogasastudio.ashihara.utils.AshiharaTags.CEREAL_PROCESSED;

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
    }

    public static final DirectionProperty FACING = HorizontalBlock.HORIZONTAL_FACING;

    @Override
    public BlockState getStateForPlacement(BlockItemUseContext context)
    {
        return this.getDefaultState().with(FACING, context.getPlacementHorizontalFacing());
    }

    @Override
    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder)
    {
        builder.add(FACING);
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
        VoxelShape bottom_1 = makeCuboidShape(3.0d,0.0d,3.0d,13.0d,2.0d,13.0d);
        VoxelShape bottom_2 = makeCuboidShape(2.0d,2.0d,2.0d,14.0d,4.0d,14.0d);
        VoxelShape n = makeCuboidShape(1.5d,4.0d,1.5d,14.5d,16.0d,3.5d);
        VoxelShape e = makeCuboidShape(12.5d,4.0d,3.5d,14.5d,16.0d,12.5d);
        VoxelShape s = makeCuboidShape(1.5d,4.0d,12.5d,14.5d,16.0d,14.5d);
        VoxelShape w = makeCuboidShape(1.5d, 4.0d, 3.5d, 3.5d, 16.0d, 12.5d);
        return VoxelShapes.or(bottom_1, bottom_2, n, e, s, w);
    }

    @Override
    public ActionResultType onBlockActivated(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit)
    {
        ItemStack stack = player.getHeldItem(handIn);
        MortarTE te = (MortarTE) worldIn.getTileEntity(pos);
        if (te == null) return ActionResultType.FAIL;

        FluidTank tank = te.getTank().orElse(new FluidTank(0));
        if (!stack.isEmpty() && FluidHelper.notifyFluidTankInteraction(player, handIn, stack, tank, worldIn, pos))
        {
            player.inventory.markDirty();
            te.notifyStateChanged();
            worldIn.notifyBlockUpdate(pos, state, state, 3);
            return ActionResultType.SUCCESS;
        }
        else if (handIn.equals(Hand.MAIN_HAND))
        {
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
            if (!player.isSneaking() && te.notifyInteraction(stack, worldIn, pos, player))
            {
                boolean isPowder = stack.getItem().isIn(CEREALS) || stack.getItem().isIn(CEREAL_PROCESSED);
                boolean isTool = stack.getItem().equals(ItemRegistryHandler.PESTLE.get()) || stack.getItem() instanceof ItemOtsuchi;
                if (!te.isWorking && !isTool)
                {
                    if (isPowder)
                    {
                        worldIn.playSound(player, pos, SoundEvents.BLOCK_SAND_PLACE, SoundCategory.BLOCKS, 1.0f, 1.0f);
                    }
                }
                else if (isTool)
                {
                    worldIn.playSound(player, pos, SoundEvents.BLOCK_WOOD_PLACE, SoundCategory.BLOCKS, 1.0F, 1.0F);
                    if (te.recipeType == 0)
                    {
                        Random rand = worldIn.getRandom();
                        for (int i = 0; i < 12; i += 1)
                        {
                            worldIn.addParticle
                            (
                                new GenericParticleData(new Vector3d(0, 0, 0), 0, ParticleRegistryHandler.RICE.get()),
                                (double) pos.getX() + 0.5D, (double) pos.getY() + 0.5D,
                                (double) pos.getZ() + 0.5D, rand.nextFloat() / 2.0F,
                                5.0E-5D,
                                rand.nextFloat() / 2.0F
                            );
                        }
                    }
                }
                else
                {
                    worldIn.playSound(player, pos, SoundEvents.ITEM_ARMOR_EQUIP_GENERIC, SoundCategory.BLOCKS, 1.0f, 1.0f);
                }
                worldIn.notifyBlockUpdate(pos, state, state, 3);
            }
            else if (!worldIn.isRemote())
            {
                NetworkHooks.openGui((ServerPlayerEntity) player, te, (PacketBuffer packerBuffer) -> packerBuffer.writeBlockPos(te.getPos()));
            }
            return ActionResultType.SUCCESS;
        }
        return ActionResultType.PASS;
    }

    @Override
    public boolean hasTileEntity(BlockState state) {return true;}

    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {return new MortarTE();}
}
