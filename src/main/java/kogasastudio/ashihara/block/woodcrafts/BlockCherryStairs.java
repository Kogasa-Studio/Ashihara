package kogasastudio.ashihara.block.woodcrafts;

import kogasastudio.ashihara.block.BlockRegistryHandler;
import net.minecraft.block.StairsBlock;

public class BlockCherryStairs extends StairsBlock
{
    public BlockCherryStairs()
    {
        super
        (
            BlockRegistryHandler.CHERRY_PLANKS.get().getDefaultState(),
            Properties.from(BlockRegistryHandler.CHERRY_PLANKS.get())
        );
    }
}
