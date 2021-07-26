package kogasastudio.ashihara.block;

import net.minecraft.block.Block;
import net.minecraft.block.RotatedPillarBlock;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;

public class BlockCherryWood extends RotatedPillarBlock
{
    public BlockCherryWood()
    {
        super
        (
            Properties.create(Material.WOOD)
            .hardnessAndResistance(0.2F, 0.3F)
            .sound(SoundType.WOOD)
        );
    }
}
