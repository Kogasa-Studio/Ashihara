package kogasastudio.ashihara.block.tileentities;

import kogasastudio.ashihara.block.BlockRegistryHandler;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;

import java.util.Random;

import static kogasastudio.ashihara.helper.MathHelper.simplifyDouble;

public class CandleTE extends AshiharaMachineTE
{
    private final NonNullList<double[]> posList = NonNullList.create();
    private final BlockState state = BlockRegistryHandler.CANDLE.get().defaultBlockState();

    public CandleTE() {super(TERegistryHandler.CANDLE_TE.get());}

    public boolean addCurrentCandle(double x, double y, double z)
    {
        if (this.level == null || this.level.isClientSide() || posList.size() >= 4) return false;

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

    public boolean addCurrentCandle(double x, double z, Random rand)
    {
        return this.addCurrentCandle(x, 0 - (0.4d * rand.nextDouble()), z);
    }

    public int pickCandle(boolean pickAll, World worldIn, BlockPos posIn)
    {
        if (pickAll || posList.size() == 1)
        {
            int i = posList.size();
            posList.clear();
            setChanged();
            worldIn.removeBlock(posIn, false);
            worldIn.sendBlockUpdated(posIn, state, state, 3);
            return worldIn.isClientSide() ? 0 : i;
        }
        else
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
    public void load(BlockState state, CompoundNBT nbt)
    {
        super.load(state, nbt);
        ListNBT poses = nbt.getList("posList", Constants.NBT.TAG_COMPOUND);
        for (int i = 0; i < poses.size(); i += 1)
        {
            CompoundNBT array = poses.getCompound(i);
            double x = array.getDouble("x");
            double y = array.getDouble("y");
            double z = array.getDouble("z");
            this.posList.add(new double[]{x, z, y});
        }
    }

    @Override
    public CompoundNBT save(CompoundNBT compound)
    {
        super.save(compound);
        ListNBT nbt = new ListNBT();
        for (double[] d : this.posList)
        {
            if (d.length == 3)
            {
                CompoundNBT arrays = new CompoundNBT();
                arrays.putDouble("x", d[0]);
                arrays.putDouble("z", d[1]);
                arrays.putDouble("y", d[2]);
                nbt.add(arrays);
            }
        }
        compound.put("posList", nbt);
        return compound;
    }

    public NonNullList<double[]> getPosList()
    {
        return this.posList;
    }
}
