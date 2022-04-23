package kogasastudio.ashihara.utils;

import kogasastudio.ashihara.item.IHasCustomModel;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.items.ItemStackHandler;

import javax.annotation.Nullable;

import static kogasastudio.ashihara.helper.RenderHelper.XTP;

public class ItemDisplayPos
{
    public final ItemStackHandler handler;
    public final int slot;

    private float[] pos = new float[3];
    private int range = 16;

    public ItemDisplayPos(ItemStackHandler handlerIn, int slotIn)
    {
        this.handler = handlerIn;
        this.slot = slotIn;
    }

    public ItemStack getDisplayStack() {return this.handler.getStackInSlot(this.slot).copy();}

    @Nullable
    public IHasCustomModel getItemCustomModel()
    {
        return this.getDisplayStack().getItem() instanceof IHasCustomModel ? (IHasCustomModel) this.getDisplayStack().getItem() : null;
    }

    public float[] getTranslation() {return this.pos;}

    public float[] getPos(BlockPos posIn)
    {
        float[] pos = new float[3];
        pos[0] = posIn.getX() + this.pos[0];
        pos[1] = posIn.getY() + this.pos[1];
        pos[2] = posIn.getZ() + this.pos[2];
        return pos;
    }

    public float getScale() {return XTP(this.range);}

    public CompoundNBT deserializeNBT(CompoundNBT compound)
    {
        CompoundNBT items = new CompoundNBT();
        compound.putInt("slotID", this.slot);
        compound.putInt("range", this.range);
        compound.putFloat("x", this.pos[0]);
        compound.putFloat("y", this.pos[1]);
        compound.putFloat("z", this.pos[2]);

        return compound;
    }
}