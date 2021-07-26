package kogasastudio.ashihara.block.woodcrafts;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;

public class BlockCherryPlanks extends Block
{
    public BlockCherryPlanks()
    {
        super
        (
            Properties.create(Material.WOOD)
            .hardnessAndResistance(2.0F, 3.0F)
            .sound(SoundType.WOOD)
        );
    }
}
