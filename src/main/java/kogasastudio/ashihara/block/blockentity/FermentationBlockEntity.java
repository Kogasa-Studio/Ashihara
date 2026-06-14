package kogasastudio.ashihara.block.blockentity;

import kogasastudio.ashihara.client.gui3d.util.BoneTracer;
import kogasastudio.ashihara.client.models.geo.FermentationDisplayModel;
import kogasastudio.ashihara.inventory.BEFluidStackHandler;
import kogasastudio.ashihara.inventory.BEItemStackHandler;
import kogasastudio.ashihara.registry.BlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.fluid.FluidResource;
import net.neoforged.neoforge.transfer.item.ItemResource;

import java.util.LinkedHashMap;
import java.util.Map;

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
    public float prevFluidLevel = 0f;
    public float fluidLevel = 0f;
    public boolean fluidLevelChanged = false;
    public boolean inited = false;
    private FermentationDisplayModel model;
    public Map<String, BoneTracer> boneTracers = new LinkedHashMap<>();

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

    public String getSizeId()
    {
        return switch (this.size)
        {
            case VAT -> "vat";
            case LARGE_VAT -> "large_vat";
            default -> "basin";
        };
    }

    public char getSizePrefix()
    {
        return switch (this.size)
        {
            case VAT -> 'v';
            case LARGE_VAT -> 'l';
            default -> 'b';
        };
    }

    public BoneTracer getBoneTracer(String id)
    {
        return this.boneTracers.get(id + this.getSizeId());
    }

    public interface IFermentationSizeProvider
    {
        Size getSize();
    }

    public FermentationDisplayModel getModel()
    {
        if (this.level == null || !this.level.isClientSide()) return null;
        if (this.model == null)
        {
            this.model = new FermentationDisplayModel("assistance/fermentation_block_display", "textures/geo/empty.png", "block/fermentation_block_display");
            int maxSlot = switch (this.size)
            {
                case VAT -> 8;
                case LARGE_VAT -> 16;
                default -> 4;
            };
            for (int i = 0; i < maxSlot; i++)
            {
                String id = "item_display_" + this.getSizePrefix() + i;
                createTracer(id);
            }
            createTracer("fluid_display_" + this.getSizeId());
            createTracer("item_display_" + this.getSizeId());
        }
        return this.model;
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

    @Override
    public void onLoad()
    {
        super.onLoad();
        setChanged();
    }

    @Override
    public void setChanged()
    {
        super.setChanged();
        float t = (float) this.fluid.getFluidAmount() / (float) this.fluid.getCapacity();
        if (this.fluidLevel != t)
        {
            this.prevFluidLevel = this.fluidLevel;
            this.fluidLevel = t;
            this.fluidLevelChanged = true;
        }
        if (this.level != null && !this.level.isClientSide())
        {
            this.level.sendBlockUpdated(this.worldPosition, this.getBlockState(), this.getBlockState(), Block.UPDATE_ALL);
        }
    }

    private void createTracer(String name)
    {
        BoneTracer tracer = new BoneTracer(b -> b.name().equals(name));
        boneTracers.put(name, tracer);
        this.model.getRendererPoseSync().ashihara_1_21$addTracer(tracer);
    }
}