package kogasastudio.ashihara.block.building;

import kogasastudio.ashihara.block.IVariable;
import kogasastudio.ashihara.helper.ParticleHelper;
import kogasastudio.ashihara.helper.ShapeHelper;
import kogasastudio.ashihara.utils.AshiharaWoodTypes;
import kogasastudio.ashihara.utils.WallTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public abstract class AbstractBeamBlock extends Block implements IVariable<AshiharaWoodTypes>, IExpandable
{
    public static final EnumProperty<Direction.Axis> AXIS = BlockStateProperties.HORIZONTAL_AXIS;
    public static final EnumProperty<WallTypes> WALL_TYPE = EnumProperty.create("wall", WallTypes.class);
    public static final EnumProperty<WallFillType> WALL_FILL_TYPE = EnumProperty.create("fill", WallFillType.class);
    public static final EnumProperty<Combination> COMBINATION = EnumProperty.create("combination", Combination.class);
    public static final Map<String, Combination> COMBINATIONS = new HashMap<>();

    public AbstractBeamBlock()
    {
        super
                (
                        Properties.of()
                                .mapColor(MapColor.WOOD)
                                .strength(0.3F)
                                .sound(SoundType.WOOD)
                                .noOcclusion()
                );
        this.registerDefaultState
                (
                        defaultBlockState()
                                .setValue(AXIS, Direction.Axis.X)
                                .setValue(WALL_TYPE, WallTypes.WHITE_SOIL)
                                .setValue(WALL_FILL_TYPE, WallFillType.NONE)
                                .setValue(COMBINATION, Combination.BOOO)
                                .setValue(L, false)
                                .setValue(R, false)
                );
        for (Combination combination : Combination.values())
        {
            COMBINATIONS.put(combination.getSerializedName(), combination);
        }
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context)
    {
        BlockState state = this.defaultBlockState();
        BlockState fromState = context.getLevel().getBlockState(context.getClickedPos().relative(context.getHorizontalDirection()));
        if (context.getPlayer() != null && !context.getPlayer().isShiftKeyDown() && fromState.is(state.getBlock())) return fromState;
        state = state.setValue(AXIS, context.getHorizontalDirection().getAxis().equals(Direction.Axis.X) ? Direction.Axis.Z : Direction.Axis.X);
        state = this.updateState(context.getLevel(), context.getClickedPos(), state);

        double yIns = context.getClickLocation().y() - context.getClickedPos().getY();
        int floor = (int) Math.floor((1 - yIns) / 0.25);
        floor = floor >= 4 ? 3 : Math.max(floor, 0);
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < 4; i += 1)
        {
            builder.append(i == floor ? 'b' : 'o');
        }
        String replaced = builder.toString();
        if (COMBINATIONS.containsKey(replaced))
        {
            state = state.setValue(COMBINATION, COMBINATIONS.get(replaced));
        }
        return state;
    }

    public Item getBeam() {return ItemStack.EMPTY.getItem();}

    //高级障子壁构筑主要逻辑
    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult)
    {
        ItemStack item = player.getItemInHand(hand);
        boolean isBeam = item.getItem().equals(this.getBeam());
        boolean isAxe = item.getItem() instanceof AxeItem;
        boolean isWall = item.getItem() instanceof BlockItem && ((BlockItem) item.getItem()).getBlock() instanceof AbstractWallBlock;
        boolean isPickaxe = item.getItem() instanceof PickaxeItem;
        if ((isBeam && !hitResult.getDirection().getAxis().equals(state.getValue(AXIS))) || isAxe)
        {
            boolean changed = false;
            char replacement = 'o';
            String combinationIns = state.getValue(COMBINATION).getSerializedName();
            //得到[0,1]内的方块内精确坐标，并划分为4个层，从上往下为0，1，2，3
            double yIns = hitResult.getLocation().y() - pos.getY();
            int floor = (int) Math.floor((1 - yIns) / 0.25);
            floor = floor >= 4 ? 3 : Math.max(floor, 0);
            //判断组合形式，若右击的「层」上下均无「长押」或「枋」，则依据手持物品判断当层状态。
            //当手持物为枋时，点击的层若为空则设为枋，若为枋则设为长押，若为长押则不行动。
            //当手持物为斧时，点击的层若为空则不行动，若为枋则设为空，若为长押则设为枋。
            char ignoredState = isBeam ? 'n' : 'o';
            boolean upperAble = floor - 1 <= 0 || combinationIns.charAt(floor - 1) == 'o';
            boolean underAble = floor + 1 >= 3 || combinationIns.charAt(floor + 1) == 'o';
            if (isBeam && (combinationIns.charAt(floor) != 'n' && upperAble && underAble))
            {
                replacement = combinationIns.charAt(floor) == 'o' ? 'b' : 'n';
                changed = true;
            }
            else if (isAxe && combinationIns.charAt(floor) != 'o')
            {
                replacement = combinationIns.charAt(floor) == 'n' ? 'b' : 'o';
                changed = true;
            }
            //总执行块，如果新的组合符合已有形式，则转换状态为新组合
            if (changed)
            {
                StringBuilder builder = new StringBuilder();
                for (int i = 0; i < combinationIns.length(); i += 1)
                {
                    builder.append(i == floor ? replacement : combinationIns.charAt(i));
                }
                String replaced = builder.toString();
                if (COMBINATIONS.containsKey(replaced))
                {
                    state = state.setValue(COMBINATION, COMBINATIONS.get(replaced));
                    SoundEvent event = isBeam ? SoundEvents.WOOD_PLACE : SoundEvents.WOOD_BREAK;
                    level.setBlockAndUpdate(pos, state);
                    level.playSound(player, pos, event, SoundSource.BLOCKS, 1.0f, 1.0f);
                    if (isAxe) ParticleHelper.spawnBlockDestruction(level, hitResult.getLocation().x(), hitResult.getLocation().y(), hitResult.getLocation().z(), state, 10);
                    return InteractionResult.SUCCESS;
                }
                else if (replaced.equals("oooo") && !state.getValue(WALL_FILL_TYPE).equals(WallFillType.NONE) && state.getValue(WALL_TYPE).getBlock() instanceof AbstractWallBlock)
                {
                    AbstractWallBlock wall = (AbstractWallBlock) state.getValue(WALL_TYPE).getBlock();
                    state = wall.defaultBlockState().setValue(AXIS, hitResult.getDirection().getAxis().equals(Direction.Axis.X) ? Direction.Axis.Z : Direction.Axis.X);
                    state = wall.updateState(level, pos, state);
                    level.setBlockAndUpdate(pos, state);
                    level.playSound(player, pos, SoundEvents.WOOD_BREAK, SoundSource.BLOCKS, 1.0f, 1.0f);
                    ParticleHelper.spawnBlockDestruction(level, hitResult.getLocation().x(), hitResult.getLocation().y(), hitResult.getLocation().z(), state, 10);
                    if (!player.isCreative()) Containers.dropItemStack(level, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, this.getBeam().getDefaultInstance());
                    return InteractionResult.SUCCESS;
                }
            }
        }
        else if (isWall || isPickaxe)
        {
            double yIns = hitResult.getLocation().y() - pos.getY();
            boolean changed = false;

            boolean top = state.getValue(WALL_FILL_TYPE).equals(WallFillType.TOP);
            boolean bottom = state.getValue(WALL_FILL_TYPE).equals(WallFillType.BOTTOM);
            boolean all = state.getValue(WALL_FILL_TYPE).equals(WallFillType.ALL);
            boolean none = state.getValue(WALL_FILL_TYPE).equals(WallFillType.NONE);
            if (isWall && !all)
            {
                AbstractWallBlock wall = (AbstractWallBlock) ((BlockItem) item.getItem()).getBlock();
                boolean typeMatches = wall.getType().equals(state.getValue(WALL_TYPE));
                if (typeMatches || none)
                {
                    if (none) state = state.setValue(WALL_TYPE, wall.getType());
                    if (yIns < 0.5 && (top || none))
                    {
                        state = state.setValue(WALL_FILL_TYPE, top ? WallFillType.ALL : WallFillType.BOTTOM);
                        changed = true;
                    }
                    else if (yIns <= 1 && (bottom || none))
                    {
                        state = state.setValue(WALL_FILL_TYPE, bottom ? WallFillType.ALL : WallFillType.TOP);
                        changed = true;
                    }
                }
            }
            else if (isPickaxe && !none)
            {
                if (yIns < 0.5 && (bottom || all))
                {
                    state = state.setValue(WALL_FILL_TYPE, bottom ? WallFillType.NONE : WallFillType.TOP);
                    changed = true;
                }
                else if (yIns <= 1 && (top || all))
                {
                    state = state.setValue(WALL_FILL_TYPE, top ? WallFillType.NONE : WallFillType.BOTTOM);
                    changed = true;
                }
            }
            if (changed)
            {
                SoundType type = isWall
                        ? ((BlockItem) item.getItem()).getBlock().getSoundType(((BlockItem) item.getItem()).getBlock().defaultBlockState())
                        : state.getValue(WALL_TYPE).getBlock().getSoundType(state.getValue(WALL_TYPE).getBlock().defaultBlockState());
                SoundEvent event = isWall ? type.getPlaceSound() : type.getBreakSound();
                level.setBlockAndUpdate(pos, state);
                level.playSound(player, pos, event, SoundSource.BLOCKS, 1.0f, 1.0f);
                if (isPickaxe)
                {
                    ParticleHelper.spawnBlockDestruction(level, pos.getX(), pos.getY(), pos.getZ(), state.getValue(WALL_TYPE).getBlock().defaultBlockState(), 10);
                    if (!player.isCreative()) Containers.dropItemStack(level, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, state.getValue(WALL_TYPE).getBlock().asItem().getDefaultInstance());
                }
                return InteractionResult.SUCCESS;
            }
        }
        return InteractionResult.PASS;
    }

    public BlockState updateState(Level level, BlockPos pos, BlockState state)
    {
        Direction left = this.getDirectionByAxis(state.getValue(AXIS), RelativeDirection.LEFT);
        Direction right = this.getDirectionByAxis(state.getValue(AXIS), RelativeDirection.RIGHT);

        BlockState l = level.getBlockState(pos.relative(left));
        BlockState r = level.getBlockState(pos.relative(right));

        state = this.expand(RelativeDirection.LEFT, state, !l.isAir() && !ShapeHelper.canBlockSupport(l, level, pos.relative(left), left.getOpposite(), ShapeHelper.getWallShape(right, state.getValue(AXIS))));
        state = this.expand(RelativeDirection.RIGHT, state, !r.isAir() && !ShapeHelper.canBlockSupport(r, level, pos.relative(right), right.getOpposite(), ShapeHelper.getWallShape(left, state.getValue(AXIS))));

        return state;
    }

    @Override
    public void neighborChanged(BlockState state, Level level, BlockPos pos, Block block, BlockPos neighborPos, boolean flag)
    {
        BlockState updated = this.updateState(level, pos, state);
        if (!updated.equals(state)) level.setBlockAndUpdate(pos, updated);
        super.neighborChanged(state, level, pos, block, neighborPos, flag);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter getter, BlockPos pos, CollisionContext context)
    {
        VoxelShape X = Block.box(0,0,6,16,16,10);
        VoxelShape Z = Block.box(6,0,0,10,16,16);

        return state.getValue(AXIS).equals(Direction.Axis.X) ? X : Z;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder)
    {
        builder.add(AXIS, L, R, WALL_TYPE, WALL_FILL_TYPE, COMBINATION);
    }

    public enum WallFillType implements StringRepresentable
    {
        NONE("none"),
        TOP("top"),
        BOTTOM("bottom"),
        ALL("all");

        public final String name;

        WallFillType(String nameIn)
        {
            this.name = nameIn;
        }

        @Override
        public String getSerializedName()
        {
            return this.name;
        }
    }

    enum Combination implements StringRepresentable
    {
        BOOO("booo"),
        OBOO("oboo"),
        OOBO("oobo"),
        OOOB("ooob"),
        BOBO("bobo"),
        BOOB("boob"),
        OBOB("obob"),
        NOOO("nooo"),
        ONOO("onoo"),
        OONO("oono"),
        OOON("ooon"),
        NOBO("nobo"),
        BONO("bono"),
        NONO("nono"),
        NOOB("noob"),
        BOON("boon"),
        NOON("noon"),
        ONOB("onob"),
        OBON("obon"),
        ONON("onon");
        public final String name;

        Combination(String nameIn) {this.name = nameIn;}

        @Override
        public String getSerializedName()
        {
            return this.name;
        }
    }
}
