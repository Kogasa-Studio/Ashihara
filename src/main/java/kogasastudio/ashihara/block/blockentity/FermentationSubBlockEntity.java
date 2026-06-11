package kogasastudio.ashihara.block.blockentity;

import kogasastudio.ashihara.registry.BlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.shapes.VoxelShape;

public class FermentationSubBlockEntity extends AshiharaCommonBE
{
    private BlockPos mainPos = BlockPos.ZERO;
    private VoxelShape cachedShape;
    private int sliceIndex = -1;

    public FermentationSubBlockEntity(BlockPos pos, BlockState state)
    {
        super(BlockEntities.FERMENTATION_SUB_BE.get(), pos, state);
    }

    public void setMainPos(BlockPos pos) { this.mainPos = pos; setChanged(); }
    public BlockPos getMainPos() { return mainPos; }

    public void cacheShape(VoxelShape shape, int index)
    {
        this.cachedShape = shape;
        this.sliceIndex = index;
    }

    public VoxelShape getCachedShape() { return cachedShape; }
    public int getSliceIndex() { return sliceIndex; }

    @Override
    protected void saveAdditional(ValueOutput output)
    {
        output.putLong("main", mainPos.asLong());
        if (sliceIndex >= 0) output.putInt("slice", sliceIndex);
        super.saveAdditional(output);
    }

    @Override
    protected void loadAdditional(ValueInput input)
    {
        super.loadAdditional(input);
        mainPos = BlockPos.of(input.getLongOr("main", 0));
        sliceIndex = input.getIntOr("slice", -1);
    }
}