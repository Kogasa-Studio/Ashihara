package kogasastudio.ashihara.block.woodcrafts;

import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.WoodButtonBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.Material;

public class SimpleButtonBlock extends WoodButtonBlock {
    public SimpleButtonBlock() {
        super
                (
                        BlockBehaviour.Properties.of(Material.DECORATION)
                                .strength(0.5F)
                                .sound(SoundType.WOOD)
                );
    }
}
