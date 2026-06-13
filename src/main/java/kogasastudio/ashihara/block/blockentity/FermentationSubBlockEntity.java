package kogasastudio.ashihara.block.blockentity;

import kogasastudio.ashihara.registry.BlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.fluid.FluidResource;
import net.neoforged.neoforge.transfer.item.ItemResource;

public class FermentationSubBlockEntity extends AshiharaCommonBE
{
    public BlockPos mainPos = BlockPos.ZERO;
    private int sliceIndex = -1;

    public FermentationSubBlockEntity(BlockPos pos, BlockState state)
    {
        super(BlockEntities.FERMENTATION_SUB_BE.get(), pos, state);
    }

    public void setMainPos(BlockPos pos) { this.mainPos = pos; setChanged(); }
    public BlockPos getMainPos() { return mainPos; }

    public void setSliceIndex(int index) { this.sliceIndex = index; setChanged(); }
    public int getSliceIndex() { return sliceIndex; }

    @SuppressWarnings({"unchecked", "rawtypes"})
    public static ResourceHandler<ItemResource> getItemHandler(FermentationSubBlockEntity be, Direction direction)
    {
        if (be.getLevel() == null) return null;
        BlockEntity ori = be.getLevel().getBlockEntity(be.getMainPos());
        if (ori instanceof IItemHandler itemHandler)
        {
            return itemHandler.getItemResource(ori, direction);
        }
        return null;
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    public static ResourceHandler<FluidResource> getFluidHandler(FermentationSubBlockEntity be, Direction direction)
    {
        if (be.getLevel() == null) return null;
        BlockEntity ori = be.getLevel().getBlockEntity(be.getMainPos());
        if (ori instanceof IFluidHandler fluidHandler)
        {
            return fluidHandler.getTank();
        }
        return null;
    }

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