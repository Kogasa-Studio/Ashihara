package kogasastudio.ashihara.block.blockentity;

import kogasastudio.ashihara.inventory.BEFluidStackHandler;
import kogasastudio.ashihara.inventory.BEItemStackHandler;
import kogasastudio.ashihara.registry.BlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.fluid.FluidResource;
import net.neoforged.neoforge.transfer.item.ItemResource;

public class FermentationBlockEntity extends AshiharaCommonBE implements IItemHandler<FermentationBlockEntity>, IFluidHandler
{
    public enum Size
    {
        BASIN(1), VAT(1), LARGE_VAT(2);

        public final int blockSize;

        Size(int blockSize) { this.blockSize = blockSize; }
    }

    public final BEItemStackHandler<FermentationBlockEntity> inventory;
    public final BEFluidStackHandler<FermentationBlockEntity> fluid;
    public final Size size;

    public FermentationBlockEntity(BlockPos pos, BlockState state)
    {
        super(BlockEntities.FERMENTATION_BE.get(), pos, state);
        this.size = state.getBlock() instanceof IFermentationSizeProvider fp ? fp.getSize() : Size.BASIN;
        this.inventory = switch (this.size)
        {
            case BASIN -> new BEItemStackHandler<>(4, this);
            case VAT -> new BEItemStackHandler<>(8, this);
            case LARGE_VAT -> new BEItemStackHandler<>(16, this);
        };
        this.fluid = switch (this.size)
        {
            case BASIN -> new BEFluidStackHandler<>(16000, this);
            case VAT -> new BEFluidStackHandler<>(128000, this);
            case LARGE_VAT -> new BEFluidStackHandler<>(1024000, this);
        };
    }

    public static ResourceHandler<ItemResource> getItemHandler(FermentationBlockEntity be, Direction direction) {return be.inventory;}
    public static ResourceHandler<FluidResource> getFluidHandler(FermentationBlockEntity be, Direction direction) {return be.fluid;}

    @Override public BEFluidStackHandler<?> getTank() {return this.fluid;}
    @Override public ResourceHandler<ItemResource> getItemResource(FermentationBlockEntity be, Direction direction) {return getItemHandler(be, direction);}

    public interface IFermentationSizeProvider
    {
        Size getSize();
    }

    @Override
    protected void loadAdditional(ValueInput input)
    {
        input.readChild("inventory", this.inventory);
        input.readChild("fluid", this.fluid);
        super.loadAdditional(input);
    }

    @Override
    protected void saveAdditional(ValueOutput output)
    {
        output.putChild("inventory", this.inventory);
        output.putChild("fluid", this.fluid);
        super.saveAdditional(output);
    }
}