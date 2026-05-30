package kogasastudio.ashihara.block;

import kogasastudio.ashihara.interaction.HeatLevel;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;

public interface IHeatProvider
{
    HeatLevel getHeatLevel(BlockState bs, Direction side);
}
