package kogasastudio.ashihara.block;

import net.minecraft.world.level.block.DoublePlantBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.material.MapColor;
import net.neoforged.neoforge.common.IShearable;

public class ReedBlock extends DoublePlantBlock implements IShearable
{
    public ReedBlock()
    {
        super
                (
                        Properties.of()
                                .mapColor(MapColor.PLANT)
                                .noCollission()
                                .instabreak()
                                .sound(SoundType.GRASS)
                                .noOcclusion()
                );
    }
}
