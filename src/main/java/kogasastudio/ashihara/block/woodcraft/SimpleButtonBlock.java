package kogasastudio.ashihara.block.woodcraft;

import net.minecraft.world.level.block.ButtonBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.properties.BlockSetType;
import net.minecraft.world.level.material.PushReaction;

public class SimpleButtonBlock extends ButtonBlock
{
    public SimpleButtonBlock(BlockBehaviour.Properties properties)
    {
        super(BlockSetType.OAK, 30, properties);
    }

    public SimpleButtonBlock()
    {
        this
        (
            BlockBehaviour.Properties.of()
            .noCollision()
            .pushReaction(PushReaction.DESTROY)
            .strength(0.5F)
        );
    }
}
