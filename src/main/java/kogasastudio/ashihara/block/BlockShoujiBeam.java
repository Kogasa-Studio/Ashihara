package kogasastudio.ashihara.block;

import kogasastudio.ashihara.utils.AshiharaWoodTypes;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.EnumProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.Direction;
import net.minecraft.util.IStringSerializable;

import net.minecraft.block.AbstractBlock.Properties;

public class BlockShoujiBeam extends Block implements IVariable<AshiharaWoodTypes>
{
    public BlockShoujiBeam()
    {
        super
        (
            Properties.of(Material.WOOD)
            .strength(0.3F)
            .sound(SoundType.WOOD)
        );
    }

    public static final EnumProperty<FillType> TOP = EnumProperty.create("top", FillType.class);
    public static final EnumProperty<FillType> UPPER = EnumProperty.create("upper", FillType.class);
    public static final EnumProperty<FillType> LOWER = EnumProperty.create("lower", FillType.class);
    public static final EnumProperty<FillType> BOTTOM = EnumProperty.create("bottom", FillType.class);

    public static final EnumProperty<Direction.Axis> AXIS = BlockStateProperties.HORIZONTAL_AXIS;
    public static final BooleanProperty FRONT = BooleanProperty.create("front");
    public static final BooleanProperty BACK = BooleanProperty.create("back");

    @Override
    protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> builder)
    {
        builder.add(TOP, UPPER, LOWER, BOTTOM, AXIS, FRONT, BACK);
    }

    @Override
    public AshiharaWoodTypes getType()
    {
        return AshiharaWoodTypes.OAK;
    }

    enum FillType implements IStringSerializable
    {
        BEAM("beam"),
        NAGESHI("nageshi"),
        WALL("wall"),
        EMPTY("empty");

        public final String name;

        FillType(String nameIn) {this.name = nameIn;}

        @Override
        public String getSerializedName() {return this.name;}
    }
}
