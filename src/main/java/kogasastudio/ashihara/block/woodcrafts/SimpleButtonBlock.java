package kogasastudio.ashihara.block.woodcrafts;

import net.minecraft.world.level.block.SoundType;
import net.minecraft.block.WoodButtonBlock;
import net.minecraft.world.level.material.Material;

import net.minecraft.world.level.block.state.BlockBehaviour;

public class SimpleButtonBlock extends WoodButtonBlock
{
    public SimpleButtonBlock()
    {
        super
        (
            BlockBehaviour.Properties.of(Material.DECORATION)
            .strength(0.5F)
            .sound(SoundType.WOOD)
        );
    }
}
