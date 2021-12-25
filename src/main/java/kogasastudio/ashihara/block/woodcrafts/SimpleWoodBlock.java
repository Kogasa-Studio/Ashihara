package kogasastudio.ashihara.block.woodcrafts;

import net.minecraft.block.Block;
import net.minecraft.block.RotatedPillarBlock;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;

public class SimpleWoodBlock extends RotatedPillarBlock
{
    public SimpleWoodBlock()
    {
        super
        (
            Properties.create(Material.WOOD)
            .hardnessAndResistance(0.2F, 0.3F)
            .sound(SoundType.WOOD)
        );
    }
}
