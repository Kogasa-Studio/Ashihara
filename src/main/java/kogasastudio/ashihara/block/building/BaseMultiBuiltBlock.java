package kogasastudio.ashihara.block.building;

import kogasastudio.ashihara.block.building.component.ComponentStateDefinition;
import kogasastudio.ashihara.block.blockentity.MultiBuiltBlockEntity;
import kogasastudio.ashihara.registry.Items;
import kogasastudio.ashihara.item.block.FurnitureComponentItem;
import kogasastudio.ashihara.block.furniture.SnappedUseOnContext;
import kogasastudio.ashihara.utils.GridSnapHelper;
import kogasastudio.ashihara.item.block.BuildingComponentItem;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.redstone.Orientation;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

import static kogasastudio.ashihara.helper.PositionHelper.*;

public class BaseMultiBuiltBlock extends Block implements EntityBlock, SimpleWaterloggedBlock
{
    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
    public final ComponentMaterial material;
    VoxelShape debug = Block.box(5.0D, 0.0D, 5.0D, 11.0D, 2.0D, 11.0D);

    public BaseMultiBuiltBlock(Properties properties, ComponentMaterial materialIn)
    {
        super(properties);
        this.material = materialIn;
        this.registerDefaultState(defaultBlockState().setValue(WATERLOGGED, false));
    }

    public BaseMultiBuiltBlock(ComponentMaterial materialIn)
    {
        this
        (
            Properties.of()
            .sound(materialIn.getSound())
            .forceSolidOn()
            .mapColor(materialIn.getColor())
            .strength(materialIn.getStrength()),
            materialIn
        );
    }

    @Override
    protected List<ItemStack> getDrops(BlockState pState, LootParams.Builder pParams)
    {
        BlockEntity be = pParams.getParameter(LootContextParams.BLOCK_ENTITY);
        List<ItemStack> ret = new ArrayList<>();
        if (be instanceof MultiBuiltBlockEntity mbe)
        {
            for (ComponentStateDefinition model : mbe.getComponents(MultiBuiltBlockEntity.OPCODE_READALL))
            {
                ret.addAll(model.component().getDrops(model, mbe));
            }
        }
        return ret.isEmpty() ? super.getDrops(pState, pParams) : ret;
    }

    @Override
    protected void neighborChanged(BlockState state, Level level, BlockPos pos, Block block, @org.jspecify.annotations.Nullable Orientation orientation, boolean movedByPiston)
    {
        super.neighborChanged(state, level, pos, block, orientation, movedByPiston);
        BlockEntity be = level.getBlockEntity(pos);
        if (be instanceof MultiBuiltBlockEntity mbe)
        {
            mbe.checkConnection();
            mbe.reloadShape();
            mbe.checkMaterial();
        }
    }

    @Override
    protected InteractionResult useItemOn(ItemStack pStack, BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer, InteractionHand pHand, BlockHitResult pHitResult)
    {
        UseOnContext context = new UseOnContext(pLevel, pPlayer, pHand, pStack, pHitResult);
        BlockEntity blockEntity = pLevel.getBlockEntity(pPos);
        if (blockEntity instanceof MultiBuiltBlockEntity be)
        {
            Vec3 vec = be.inBlockVec(context.getClickLocation());
            if (be.tryInteract(context)) return InteractionResult.SUCCESS;
            if
            (
                pStack.getItem() instanceof BuildingComponentItem componentItem
                && (coordsInRangeFixedX(context.getClickedFace(), vec.x(), 0, 1) && coordsInRangeFixedY(context.getClickedFace(), vec.y(), 0, 1) && coordsInRangeFixedZ(context.getClickedFace(), vec.z(), 0, 1))
                && be.tryPlace(context, componentItem.getComponent())
            ) return InteractionResult.SUCCESS;
            else if
            (
                pStack.getItem() instanceof FurnitureComponentItem furnitureItem
                && (coordsInRangeFixedX(context.getClickedFace(), vec.x(), 0, 1) && coordsInRangeFixedY(context.getClickedFace(), vec.y(), 0, 1) && coordsInRangeFixedZ(context.getClickedFace(), vec.z(), 0, 1))
                && be.tryPlaceFurniture
                (
                    new SnappedUseOnContext(context, GridSnapHelper.getGridStep(pPlayer), false),
                    furnitureItem.getComponent()
                )
            ) return InteractionResult.SUCCESS;
            else if ((pStack.is(Items.WOODEN_HAMMER) || pStack.is(Items.CHISEL)) && be.tryBreak(context))
            {
                if (be.getComponents(MultiBuiltBlockEntity.OPCODE_COMPONENT).isEmpty()
                    && be.getComponents(MultiBuiltBlockEntity.OPCODE_ADDITIONAL).isEmpty()
                    && be.getComponents(MultiBuiltBlockEntity.OPCODE_FURNITURE).isEmpty())
                    pLevel.removeBlock(pPos, false);
                return InteractionResult.SUCCESS;
            }
        }
        return super.useItemOn(pStack, pState, pLevel, pPos, pPlayer, pHand, pHitResult);
    }

    @Override
    protected VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext)
    {
        BlockEntity blockEntity = pLevel.getBlockEntity(pPos);
        if (blockEntity instanceof MultiBuiltBlockEntity be)
        {
            if (!be.getShape().isEmpty()) return be.getShape();
        }
        return debug;
    }

    @Override
    protected VoxelShape getCollisionShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
        return getShape(pState, pLevel, pPos, pContext);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder)
    {
        pBuilder.add(WATERLOGGED);
        super.createBlockStateDefinition(pBuilder);
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext pContext)
    {
        BlockState state = super.getStateForPlacement(pContext);
        if (state == null) return null;
        state = state.setValue(WATERLOGGED, pContext.getLevel().getFluidState(pContext.getClickedPos()).getType().equals(Fluids.WATER));
        return state;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState)
    {
        return new MultiBuiltBlockEntity(pPos, pState);
    }

    @Override
    public FluidState getFluidState(BlockState state)
    {
        return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
    }

    public BlockState applyMaterial(@Nullable BlockState origin)
    {
        BlockState state = this.defaultBlockState();
        if (origin == null) return state;
        state = state.setValue(BaseMultiBuiltBlock.WATERLOGGED, origin.getValue(BaseMultiBuiltBlock.WATERLOGGED));
        return state;
    }


    public enum ComponentMaterial
    {
        //Wall
        BAMBOO_BONES(1, SoundType.BAMBOO, MapColor.TERRACOTTA_BROWN, 0.3f),
        RAMMED_SOIL(2, SoundType.DRIPSTONE_BLOCK, MapColor.DIRT, 0.5f),
        WHITE_SOIL(3, SoundType.DEEPSLATE, MapColor.SNOW, 0.5f),
        WHITE_WOOD(3, SoundType.WOOD, MapColor.SNOW, 0.5f),
        //Window
        GREEN_WOOD(11, SoundType.WOOD, MapColor.WARPED_STEM, 0.5f),
        //Wood
        OAK_WOOD(21, SoundType.WOOD, MapColor.WOOD, 0.5f),
        SPRUCE_WOOD(22, SoundType.WOOD, MapColor.COLOR_BROWN, 0.5f),
        RED_WOOD(23, SoundType.WOOD, MapColor.COLOR_RED, 0.5f),
        BLACK_LACQUERED_WOOD(24, SoundType.WOOD, MapColor.COLOR_BLACK, 0.5f),
        //Deco
        GOLD_DECO(31, SoundType.LANTERN, MapColor.GOLD, 0.6f),
        GOLD_STRUCTURAL(31, SoundType.METAL, MapColor.GOLD, 1.0f),
        STONE(32, SoundType.STONE, MapColor.STONE, 1.0f),
        //Roof
        CYPRESS_SKIN(51, SoundType.NETHER_WOOD, MapColor.COLOR_BROWN, 0.5f),
        TERRACOTTA_TILE(52, SoundType.DEEPSLATE_TILES, MapColor.STONE, 0.8f),
        ;

        public MapColor getColor() {return color;}

        public int getPriority() {return priority;}

        public SoundType getSound() {return sound;}

        public float getStrength() {return strength;}

        public final SoundType sound;
        public final MapColor color;
        public final float strength;
        public final int priority;

        ComponentMaterial(int priorityIn, SoundType soundIn, MapColor colorIn, float strengthIn)
        {
            this.priority = priorityIn;
            this.sound = soundIn;
            this.color = colorIn;
            this.strength = strengthIn;
        }

        public BaseMultiBuiltBlock createStandardBlock()
        {
            return new BaseMultiBuiltBlock(this);
        }

        public BaseMultiBuiltBlock createStandardBlock(Properties properties)
        {
            return new BaseMultiBuiltBlock(properties, this);
        }
    }
}
