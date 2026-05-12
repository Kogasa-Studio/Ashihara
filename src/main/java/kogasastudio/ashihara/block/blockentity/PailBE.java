package kogasastudio.ashihara.block.blockentity;

import kogasastudio.ashihara.inventory.BEFluidStackHandler;
import kogasastudio.ashihara.registry.BlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

public class PailBE extends AshiharaMachineBE implements IFluidHandler
{
    public final BEFluidStackHandler<PailBE> bucket = new BEFluidStackHandler<>(16000, this);

    public PailBE(BlockPos pos, BlockState state)
    {
        super(BlockEntities.PAIL_BE.get(), pos, state);
    }

    @Override
    public BEFluidStackHandler<PailBE> getTank()
    {
        return this.bucket;
    }

    @Override
    protected void loadAdditional(ValueInput input)
    {
        super.loadAdditional(input);
        input.readChild("bucket", this.bucket);
    }

    @Override
    protected void saveAdditional(ValueOutput output)
    {
        super.saveAdditional(output);
        output.putChild("bucket", this.bucket);
    }
}
