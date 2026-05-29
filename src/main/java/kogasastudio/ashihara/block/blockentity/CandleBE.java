package kogasastudio.ashihara.block.blockentity;

import kogasastudio.ashihara.registry.Blocks;
import kogasastudio.ashihara.registry.BlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

import static kogasastudio.ashihara.helper.MathHelper.simplifyDouble;

public class CandleBE extends AshiharaCommonBE
{
    private final NonNullList<double[]> posList = NonNullList.create();
    private final BlockState state = Blocks.CANDLE.get().defaultBlockState();

    public CandleBE(BlockPos pos, BlockState state)
    {
        super(BlockEntities.CANDLE_BE.get(), pos, state);
    }

    public boolean addCurrentCandle(double x, double y, double z)
    {
        if (this.level == null || posList.size() >= 4) return false;
        else if (this.level.isClientSide())
        {
            this.level.sendBlockUpdated(this.worldPosition, state, state, 3);
            return false;
        }

        double[] serialized = new double[]
                {
                        simplifyDouble(x, 2),
                        simplifyDouble(z, 2),
                        simplifyDouble(y, 2)
                };
        posList.add(serialized);
        this.level.sendBlockUpdated(this.worldPosition, state, state, 3);
        setChanged();
        return true;
    }

    public boolean addCurrentCandle(double x, double z, RandomSource rand)
    {
        return this.addCurrentCandle(x, 0 - (0.4d * rand.nextDouble()), z);
    }

    public int pickCandle(boolean pickAll, Level worldIn, BlockPos posIn)
    {
        if (pickAll || posList.size() == 1)
        {
            int i = posList.size();
            posList.clear();
            setChanged();
            worldIn.removeBlock(posIn, false);
            worldIn.sendBlockUpdated(posIn, state, state, 3);
            return worldIn.isClientSide() ? 0 : i;
        } else
        {
            int pointer = posList.size() - 1;
            posList.remove(pointer);
            worldIn.sendBlockUpdated(posIn, state, state, 3);
            setChanged();
            return worldIn.isClientSide() ? 0 : 1;
        }
    }

    public void init()
    {
        if (this.level == null) return;
        this.posList.clear();
        this.addCurrentCandle(0.5d, 0.0d, 0.5d);
    }

    public void init(double x, double z)
    {
        if (this.level == null) return;
        this.posList.clear();
        this.addCurrentCandle(x, z, this.level.getRandom());
    }

    @Override
    protected void loadAdditional(ValueInput input)
    {
        super.loadAdditional(input);
        for (ValueInput child : input.childrenListOrEmpty("posList"))
        {
            double x = child.getDoubleOr("x", 0.0);
            double y = child.getDoubleOr("y", 0.0);
            double z = child.getDoubleOr("z", 0.0);
            this.posList.add(new double[]{x, z, y});
        }
    }

    @Override
    protected void saveAdditional(ValueOutput output)
    {
        super.saveAdditional(output);
        var listOut = output.childrenList("posList");
        for (double[] d : this.posList)
        {
            if (d.length == 3)
            {
                ValueOutput child = listOut.addChild();
                child.putDouble("x", d[0]);
                child.putDouble("z", d[1]);
                child.putDouble("y", d[2]);
            }
        }
    }

    public NonNullList<double[]> getPosList()
    {
        return this.posList;
    }
}
