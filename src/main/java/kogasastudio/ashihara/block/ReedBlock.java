package kogasastudio.ashihara.block;

import net.minecraft.world.level.block.DoublePlantBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.material.MapColor;
import net.neoforged.neoforge.common.IShearable;

public class ReedBlock extends DoublePlantBlock implements IShearable
{
    public ReedBlock(Properties properties)
    {
        super(properties);
    }

    public ReedBlock()
    {
        this
        (
            Properties.of()
            .mapColor(MapColor.PLANT)
            .offsetType(OffsetType.XZ)
            .noCollision()
            .instabreak()
            .sound(SoundType.GRASS)
            .noOcclusion()
        );
    }
}
