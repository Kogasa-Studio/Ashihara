package kogasastudio.ashihara.block;

import kogasastudio.ashihara.utils.AshiharaWoodTypes;
import net.minecraft.core.Direction;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.material.MapColor;

public class ShoujiBeamBlock extends Block implements IVariable<AshiharaWoodTypes>
{
    public static final EnumProperty<FillType> TOP = EnumProperty.create("top", FillType.class);
    public static final EnumProperty<FillType> UPPER = EnumProperty.create("upper", FillType.class);
    public static final EnumProperty<FillType> LOWER = EnumProperty.create("lower", FillType.class);
    public static final EnumProperty<FillType> BOTTOM = EnumProperty.create("bottom", FillType.class);
    public static final EnumProperty<Direction.Axis> AXIS = BlockStateProperties.HORIZONTAL_AXIS;
    public static final BooleanProperty FRONT = BooleanProperty.create("front");
    public static final BooleanProperty BACK = BooleanProperty.create("back");

    public ShoujiBeamBlock()
    {
        super
                (
                        Properties.of()
                                .mapColor(MapColor.WOOD)
                                .strength(0.3F)
                                .sound(SoundType.WOOD)
                );
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder)
    {
        builder.add(TOP, UPPER, LOWER, BOTTOM, AXIS, FRONT, BACK);
    }

    @Override
    public AshiharaWoodTypes getType()
    {
        return AshiharaWoodTypes.OAK;
    }

    enum FillType implements StringRepresentable
    {
        BEAM("beam"),
        NAGESHI("nageshi"),
        WALL("wall"),
        EMPTY("empty");

        public final String name;

        FillType(String nameIn)
        {
            this.name = nameIn;
        }

        @Override
        public String getSerializedName()
        {
            return this.name;
        }
    }
}
