package kogasastudio.ashihara.block;

import net.minecraft.world.level.block.DoublePlantBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.material.Material;
import net.minecraftforge.common.IForgeShearable;

public class BlockReed extends DoublePlantBlock implements IForgeShearable
{
    public BlockReed()
    {
        super
        (
            Properties.of(Material.REPLACEABLE_PLANT)
            .noCollission()
            .instabreak()
            .sound(SoundType.GRASS)
            .noOcclusion()
        );
    }
}
