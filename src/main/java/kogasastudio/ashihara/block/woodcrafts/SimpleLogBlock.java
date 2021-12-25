package kogasastudio.ashihara.block.woodcrafts;

import net.minecraft.block.RotatedPillarBlock;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.util.Direction;

public class SimpleLogBlock extends RotatedPillarBlock
{
    public SimpleLogBlock()
    {
        super
        (
            Properties.create(Material.WOOD, (state) ->
            state.get(RotatedPillarBlock.AXIS) == Direction.Axis.Y ? MaterialColor.ADOBE : MaterialColor.STONE)
            .hardnessAndResistance(2.0F)
            .sound(SoundType.WOOD)
        );
    }
}
