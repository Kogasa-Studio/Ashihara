package kogasastudio.ashihara.block;

import kogasastudio.ashihara.block.tileentities.MortarTE;
import kogasastudio.ashihara.client.particles.GenericParticleData;
import kogasastudio.ashihara.client.particles.ParticleRegistryHandler;
import kogasastudio.ashihara.helper.FluidHelper;
import kogasastudio.ashihara.item.ItemOtsuchi;
import kogasastudio.ashihara.item.ItemRegistryHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import net.minecraftforge.network.NetworkHooks;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Random;
import java.util.UUID;

import static kogasastudio.ashihara.utils.AshiharaTags.CEREALS;
import static kogasastudio.ashihara.utils.AshiharaTags.CEREAL_PROCESSED;

public class MortarBlock extends Block implements EntityBlock
{
    public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;

    public MortarBlock()
    {
        super
                (
                        Properties.of(Material.WOOD)
                                .strength(3.0F)
                                // todo tag .harvestTool(ToolType.AXE)
                                .sound(SoundType.WOOD)
                                .noOcclusion()
                );
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context)
    {
        return this.defaultBlockState().setValue(FACING, context.getHorizontalDirection());
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder)
    {
        builder.add(FACING);
    }

    @Override
    public void playerDestroy(Level worldIn, Player player, BlockPos pos, BlockState state, BlockEntity te, ItemStack stack)
    {
        if (te instanceof MortarTE)
        {
            NonNullList<ItemStack> stacks = NonNullList.create();
            for (int i = 0; i < ((MortarTE) te).contents.getSlots(); i += 1)
            {
                ItemStack stackI = ((MortarTE) te).contents.getStackInSlot(i);
                if (!stackI.isEmpty()) stacks.add(stackI);
            }
            Containers.dropContents(worldIn, pos, stacks);
        }
        super.playerDestroy(worldIn, player, pos, state, te, stack);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context)
    {
        VoxelShape bottom_1 = box(3.0d, 0.0d, 3.0d, 13.0d, 2.0d, 13.0d);
        VoxelShape bottom_2 = box(2.0d, 2.0d, 2.0d, 14.0d, 4.0d, 14.0d);
        VoxelShape n = box(1.5d, 4.0d, 1.5d, 14.5d, 16.0d, 3.5d);
        VoxelShape e = box(12.5d, 4.0d, 3.5d, 14.5d, 16.0d, 12.5d);
        VoxelShape s = box(1.5d, 4.0d, 12.5d, 14.5d, 16.0d, 14.5d);
        VoxelShape w = box(1.5d, 4.0d, 3.5d, 3.5d, 16.0d, 12.5d);
        return Shapes.or(bottom_1, bottom_2, n, e, s, w);
    }

    @Override
    public InteractionResult use(BlockState state, Level worldIn, BlockPos pos, Player player, InteractionHand handIn, BlockHitResult hit)
    {
        ItemStack stack = player.getItemInHand(handIn);
        MortarTE te = (MortarTE) worldIn.getBlockEntity(pos);
        if (te == null) return InteractionResult.FAIL;

        FluidTank tank = te.getTank().orElse(new FluidTank(0));
        if (!stack.isEmpty() && FluidHelper.notifyFluidTankInteraction(player, handIn, stack, tank, worldIn, pos))
        {
            player.getInventory().setChanged();
            te.notifyStateChanged();
            worldIn.sendBlockUpdated(pos, state, state, 3);
            return InteractionResult.SUCCESS;
        } else if (handIn.equals(InteractionHand.MAIN_HAND))
        {
            if (stack.getItem().equals(ItemRegistryHandler.KOISHI.get()))
            {
                player.sendMessage
                        (
                                new TranslatableComponent
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
            if (!player.isShiftKeyDown() && te.notifyInteraction(stack, worldIn, pos, player))
            {
                boolean isPowder = stack.is(CEREALS) || stack.is(CEREAL_PROCESSED);
                boolean isTool = stack.getItem().equals(ItemRegistryHandler.PESTLE.get()) || stack.getItem() instanceof ItemOtsuchi;
                if (!te.isWorking && !isTool)
                {
                    if (isPowder)
                    {
                        worldIn.playSound(player, pos, SoundEvents.SAND_PLACE, SoundSource.BLOCKS, 1.0f, 1.0f);
                    }
                } else if (isTool)
                {
                    worldIn.playSound(player, pos, SoundEvents.WOOD_PLACE, SoundSource.BLOCKS, 1.0F, 1.0F);
                    if (te.recipeType == 0)
                    {
                        Random rand = worldIn.getRandom();
                        for (int i = 0; i < 12; i += 1)
                        {
                            worldIn.addParticle
                                    (
                                            new GenericParticleData(new Vec3(0, 0, 0), 0, ParticleRegistryHandler.RICE.get()),
                                            (double) pos.getX() + 0.5D, (double) pos.getY() + 0.5D,
                                            (double) pos.getZ() + 0.5D, rand.nextFloat() / 2.0F,
                                            5.0E-5D,
                                            rand.nextFloat() / 2.0F
                                    );
                        }
                    }
                } else
                {
                    worldIn.playSound(player, pos, SoundEvents.ARMOR_EQUIP_GENERIC, SoundSource.BLOCKS, 1.0f, 1.0f);
                }
                worldIn.sendBlockUpdated(pos, state, state, 3);
            } else if (!worldIn.isClientSide())
            {
                NetworkHooks.openGui((ServerPlayer) player, te, (FriendlyByteBuf packerBuffer) -> packerBuffer.writeBlockPos(te.getBlockPos()));
            }
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.PASS;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState)
    {
        return new MortarTE(pPos, pState);
    }
}
