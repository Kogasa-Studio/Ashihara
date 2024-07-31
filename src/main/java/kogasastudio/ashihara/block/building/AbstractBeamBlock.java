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
import net.minecraft.world.ItemInteractionResult;
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
    public ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult)
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
                    return ItemInteractionResult.SUCCESS;
                }
                else if (replaced.equals("oooo") && !state.getValue(WALL_FILL_TYPE).equals(WallFillType.NONE) && state.getValue(WALL_TYPE).getBlock() instanceof AbstractWallBlock wall)
                {
                    state = wall.defaultBlockState().setValue(AXIS, hitResult.getDirection().getAxis().equals(Direction.Axis.X) ? Direction.Axis.Z : Direction.Axis.X);
                    state = wall.updateState(level, pos, state);
                    level.setBlockAndUpdate(pos, state);
                    level.playSound(player, pos, SoundEvents.WOOD_BREAK, SoundSource.BLOCKS, 1.0f, 1.0f);
                    ParticleHelper.spawnBlockDestruction(level, hitResult.getLocation().x(), hitResult.getLocation().y(), hitResult.getLocation().z(), state, 10);
                    if (!player.isCreative()) Containers.dropItemStack(level, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, this.getBeam().getDefaultInstance());
                    return ItemInteractionResult.SUCCESS;
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
                        ? ((BlockItem) item.getItem()).getBlock().defaultBlockState().getSoundType()
                        : state.getValue(WALL_TYPE).getBlock().defaultBlockState().getSoundType();
                SoundEvent event = isWall ? type.getPlaceSound() : type.getBreakSound();
                level.setBlockAndUpdate(pos, state);
                level.playSound(player, pos, event, SoundSource.BLOCKS, 1.0f, 1.0f);
                if (isPickaxe)
                {
                    ParticleHelper.spawnBlockDestruction(level, pos.getX(), pos.getY(), pos.getZ(), state.getValue(WALL_TYPE).getBlock().defaultBlockState(), 10);
                    if (!player.isCreative()) Containers.dropItemStack(level, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, state.getValue(WALL_TYPE).getBlock().asItem().getDefaultInstance());
                }
                return ItemInteractionResult.SUCCESS;
            }
        }
        return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
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

    /*@Override
    public VoxelShape getShape(BlockState state, BlockGetter getter, BlockPos pos, CollisionContext context)
    {
        //墙体 上半
        VoxelShape W_X_U = Block.box(0,8,6,16,16,10);
        VoxelShape W_Z_U = Block.box(6,8,0,10,16,16);
        VoxelShape W_L_X_U = Block.box(16, 8, 7, 24, 16, 9);
        VoxelShape W_R_X_U = Block.box(-8, 8, 7, 0, 16, 9);
        VoxelShape W_L_Z_U = Block.box(7, 8, 16, 9, 16, 24);
        VoxelShape W_R_Z_U = Block.box(7, 8, -8, 9, 16, 0);
        //下半
        VoxelShape W_X_D = Block.box(0,0,6,16,8,10);
        VoxelShape W_Z_D = Block.box(6,0,0,10,8,16);
        VoxelShape W_L_X_D = Block.box(16, 0, 7, 24, 8, 9);
        VoxelShape W_R_X_D = Block.box(-8, 0, 7, 0, 8, 9);
        VoxelShape W_L_Z_D = Block.box(7, 0, 16, 9, 8, 24);
        VoxelShape W_R_Z_D = Block.box(7, 0, -8, 9, 8, 0);

        //长押
        VoxelShape N_X_1 = Shapes.or(Block.box(0, 12, 2, 16, 16, 14), Block.box(0, 11, 5, 16, 12, 11));
        VoxelShape N_X_2 = Shapes.or(Block.box(0, 8, 2, 16, 12, 14), Block.box(0, 7, 5, 16, 8, 11));
        VoxelShape N_X_3 = Shapes.or(Block.box(0, 4, 2, 16, 8, 14), Block.box(0, 8, 5, 16, 9, 11));
        VoxelShape N_X_4 = Shapes.or(Block.box(0, 0, 2, 16, 4, 14), Block.box(0, 4, 5, 16, 5, 11));

        VoxelShape N_Z_1 = Shapes.or(Block.box(2, 12, 0, 14, 16, 16), Block.box(5, 11, 0, 11, 12, 16));
        VoxelShape N_Z_2 = Shapes.or(Block.box(2, 8, 0, 14, 12, 16), Block.box(5, 7, 0, 11, 8, 16));
        VoxelShape N_Z_3 = Shapes.or(Block.box(2, 4, 0, 14, 8, 16), Block.box(5, 8, 0, 11, 9, 16));
        VoxelShape N_Z_4 = Shapes.or(Block.box(2, 0, 0, 14, 4, 16), Block.box(5, 4, 0, 11, 5, 16));
        //扩展
        VoxelShape N_L_X_1 = Shapes.or(Block.box(16, 12, 2, 30, 16, 14), Block.box(16, 11, 5, 24, 12, 11));
        VoxelShape N_L_X_2 = Shapes.or(Block.box(16, 8, 2, 30, 12, 14), Block.box(16, 7, 5, 24, 8, 11));
        VoxelShape N_L_X_3 = Shapes.or(Block.box(16, 4, 2, 30, 8, 14), Block.box(16, 8, 5, 24, 9, 11));
        VoxelShape N_L_X_4 = Shapes.or(Block.box(16, 0, 2, 30, 4, 14), Block.box(16, 4, 5, 24, 5, 11));

        VoxelShape N_R_X_1 = Shapes.or(Block.box(-14, 12, 2, 0, 16, 14), Block.box(-8, 11, 5, 0, 12, 11));
        VoxelShape N_R_X_2 = Shapes.or(Block.box(-14, 8, 2, 0, 12, 14), Block.box(-8, 7, 5, 0, 8, 11));
        VoxelShape N_R_X_3 = Shapes.or(Block.box(-14, 4, 2, 0, 8, 14), Block.box(-8, 8, 5, 0, 9, 11));
        VoxelShape N_R_X_4 = Shapes.or(Block.box(-14, 0, 2, 0, 4, 14), Block.box(-8, 4, 5, 0, 5, 11));

        VoxelShape N_L_Z_1 = Shapes.or(Block.box(2, 12, 16, 14, 16, 30), Block.box(5, 11, 16, 11, 12, 24));
        VoxelShape N_L_Z_2 = Shapes.or(Block.box(2, 8, 16, 14, 12, 30), Block.box(5, 7, 16, 11, 8, 24));
        VoxelShape N_L_Z_3 = Shapes.or(Block.box(2, 4, 16, 14, 8, 30), Block.box(5, 8, 16, 11, 9, 24));
        VoxelShape N_L_Z_4 = Shapes.or(Block.box(2, 0, 16, 14, 4, 30), Block.box(5, 4, 16, 11, 5, 24));

        VoxelShape N_R_Z_1 = Shapes.or(Block.box(2, 12, -14, 14, 16, 0), Block.box(5, 11, -8, 11, 12, 0));
        VoxelShape N_R_Z_2 = Shapes.or(Block.box(2, 8, -14, 14, 12, 0), Block.box(5, 7, -8, 11, 8, 0));
        VoxelShape N_R_Z_3 = Shapes.or(Block.box(2, 4, -14, 14, 8, 0), Block.box(5, 8, -8, 11, 9, 0));
        VoxelShape N_R_Z_4 = Shapes.or(Block.box(2, 0, -14, 14, 4, 0), Block.box(5, 4, -8, 11, 5, 0));

        //枋
        VoxelShape B_X_1 = Shapes.or(Block.box(0, 12, 6, 16, 16, 10));
        VoxelShape B_X_2 = Shapes.or(Block.box(0, 8, 6, 16, 12, 10));
        VoxelShape B_X_3 = Shapes.or(Block.box(0, 4, 6, 16, 8, 10));
        VoxelShape B_X_4 = Shapes.or(Block.box(0, 0, 6, 16, 4, 10));

        VoxelShape B_Z_1 = Shapes.or(Block.box(6, 12, 0, 10, 16, 16));
        VoxelShape B_Z_2 = Shapes.or(Block.box(6, 8, 0, 10, 12, 16));
        VoxelShape B_Z_3 = Shapes.or(Block.box(6, 4, 0, 10, 8, 16));
        VoxelShape B_Z_4 = Shapes.or(Block.box(6, 0, 0, 10, 4, 16));
        //扩展
        VoxelShape B_L_X_1 = Shapes.or(Block.box(16, 12, 6, 24, 16, 10));
        VoxelShape B_L_X_2 = Shapes.or(Block.box(16, 8, 6, 24, 12, 10));
        VoxelShape B_L_X_3 = Shapes.or(Block.box(16, 4, 6, 24, 8, 10));
        VoxelShape B_L_X_4 = Shapes.or(Block.box(16, 0, 6, 24, 4, 10));

        VoxelShape B_R_X_1 = Shapes.or(Block.box(-8, 12, 6, 0, 16, 10));
        VoxelShape B_R_X_2 = Shapes.or(Block.box(-8, 8, 6, 0, 12, 10));
        VoxelShape B_R_X_3 = Shapes.or(Block.box(-8, 4, 6, 0, 8, 10));
        VoxelShape B_R_X_4 = Shapes.or(Block.box(-8, 0, 6, 0, 4, 10));

        VoxelShape B_L_Z_1 = Shapes.or(Block.box(6, 12, 16, 10, 16, 24));
        VoxelShape B_L_Z_2 = Shapes.or(Block.box(6, 8, 16, 10, 12, 24));
        VoxelShape B_L_Z_3 = Shapes.or(Block.box(6, 4, 16, 10, 8, 24));
        VoxelShape B_L_Z_4 = Shapes.or(Block.box(6, 0, 16, 10, 4, 24));

        VoxelShape B_R_Z_1 = Shapes.or(Block.box(6, 12, -8, 10, 16, 0));
        VoxelShape B_R_Z_2 = Shapes.or(Block.box(6, 8, -8, 10, 12, 0));
        VoxelShape B_R_Z_3 = Shapes.or(Block.box(6, 4, -8, 10, 8, 0));
        VoxelShape B_R_Z_4 = Shapes.or(Block.box(6, 0, -8, 10, 4, 0));

        boolean AxisX = state.getValue(AXIS).equals(Direction.Axis.X);
        boolean l = state.getValue(L);
        boolean r = state.getValue(R);

        VoxelShape WALL;
        VoxelShape WALL_ORIGINAL;
        VoxelShape WALL_EXPANSION;

        switch (state.getValue(WALL_FILL_TYPE))
        {
            case TOP -> WALL_ORIGINAL = state.getValue(AXIS).equals(Direction.Axis.X) ? W_X_U : W_Z_U;
            case BOTTOM -> WALL_ORIGINAL = state.getValue(AXIS).equals(Direction.Axis.X) ? W_X_D : W_Z_D;
            case ALL -> WALL_ORIGINAL = state.getValue(AXIS).equals(Direction.Axis.X) ? Shapes.or(W_X_U, W_X_D) : Shapes.or(W_Z_U, W_Z_D);
        }
        if (state.getValue(L)) WALL_ORIGINAL = state.getValue(AXIS).equals(Direction.Axis.X) ? Shapes.or(WALL_ORIGINAL, W)

        return state.getValue(AXIS).equals(Direction.Axis.X) ? W_X : W_Z;
    }*/

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
