package kogasastudio.ashihara.block;

import net.minecraft.block.DoublePlantBlock;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraftforge.common.IForgeShearable;

import net.minecraft.block.AbstractBlock.Properties;

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
