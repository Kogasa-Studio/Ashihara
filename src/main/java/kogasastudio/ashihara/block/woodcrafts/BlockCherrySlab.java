package kogasastudio.ashihara.block.woodcrafts;

import net.minecraft.block.SlabBlock;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;

public class BlockCherrySlab extends SlabBlock
{
    public BlockCherrySlab()
    {
        super
        (
            Properties.create(Material.WOOD)
            .hardnessAndResistance(2.0F, 3.0F)
            .sound(SoundType.WOOD)
        );
    }
}
