package kogasastudio.ashihara.block.woodcrafts;

import net.minecraft.block.SoundType;
import net.minecraft.block.WoodButtonBlock;
import net.minecraft.block.material.Material;

public class SimpleButtonBlock extends WoodButtonBlock
{
    public SimpleButtonBlock()
    {
        super
        (
            Properties.create(Material.MISCELLANEOUS)
            .hardnessAndResistance(0.5F)
            .sound(SoundType.WOOD)
        );
    }
}
