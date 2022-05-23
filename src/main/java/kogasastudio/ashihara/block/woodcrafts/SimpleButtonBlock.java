package kogasastudio.ashihara.block.woodcrafts;

import net.minecraft.block.SoundType;
import net.minecraft.block.WoodButtonBlock;
import net.minecraft.block.material.Material;

import net.minecraft.block.AbstractBlock.Properties;

public class SimpleButtonBlock extends WoodButtonBlock
{
    public SimpleButtonBlock()
    {
        super
        (
            Properties.of(Material.DECORATION)
            .strength(0.5F)
            .sound(SoundType.WOOD)
        );
    }
}
