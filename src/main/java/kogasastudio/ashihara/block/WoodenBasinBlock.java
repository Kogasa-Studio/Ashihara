package kogasastudio.ashihara.block;

import kogasastudio.ashihara.block.blockentity.FermentationBlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class WoodenBasinBlock extends FermentationBlock
{
    private static final VoxelShape SHAPE = Shapes.box(0.0625, 0, 0.0625, 0.9375, 0.5, 0.9375);

    public WoodenBasinBlock(BlockBehaviour.Properties properties)
    {
        super(properties, FermentationBlockEntity.Size.BASIN);
    }

    @Override
    protected VoxelShape getFullShape(boolean hasLid)
    {
        return SHAPE;
    }
}