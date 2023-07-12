package kogasastudio.ashihara.block.woodcrafts;

import net.minecraft.world.level.block.ButtonBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.properties.BlockSetType;
import net.minecraft.world.level.material.PushReaction;

public class SimpleButtonBlock extends ButtonBlock
{
    public SimpleButtonBlock()
    {
        super
                (
                        BlockBehaviour.Properties.of()
                                .noCollission()
                                .pushReaction(PushReaction.DESTROY)
                                .strength(0.5F),
                        BlockSetType.OAK, 30, true
                );
    }
}
