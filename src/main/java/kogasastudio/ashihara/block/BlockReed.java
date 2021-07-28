package kogasastudio.ashihara.block;

import net.minecraft.block.DoublePlantBlock;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraftforge.common.IForgeShearable;

public class BlockReed extends DoublePlantBlock implements IForgeShearable
{
    public BlockReed()
    {
        super
        (
            Properties.create(Material.TALL_PLANTS)
            .zeroHardnessAndResistance()
            .sound(SoundType.PLANT)
        );
    }
}
