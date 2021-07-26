package kogasastudio.ashihara.block.woodcrafts;

import kogasastudio.ashihara.block.BlockExampleContainer;
import net.minecraft.block.FlowerPotBlock;
import net.minecraft.block.material.Material;

public class BlockPottedCherrySapling extends FlowerPotBlock
{
    public BlockPottedCherrySapling()
    {
        super
        (
            BlockExampleContainer.BLOCK_CHERRY_SAPLING,
            Properties.create(Material.MISCELLANEOUS)
            .zeroHardnessAndResistance()
            .notSolid()
        );
    }
}
