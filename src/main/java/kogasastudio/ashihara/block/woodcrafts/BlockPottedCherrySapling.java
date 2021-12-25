package kogasastudio.ashihara.block.woodcrafts;

import kogasastudio.ashihara.block.BlockRegistryHandler;
import net.minecraft.block.FlowerPotBlock;
import net.minecraft.block.material.Material;

public class BlockPottedCherrySapling extends FlowerPotBlock
{
    public BlockPottedCherrySapling()
    {
        super
        (
            BlockRegistryHandler.CHERRY_SAPLING.get(),
            Properties.create(Material.MISCELLANEOUS)
            .zeroHardnessAndResistance()
            .notSolid()
        );
    }
}
