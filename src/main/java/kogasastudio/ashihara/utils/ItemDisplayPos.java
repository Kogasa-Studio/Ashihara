package kogasastudio.ashihara.utils;

import com.mojang.blaze3d.matrix.MatrixStack;
import kogasastudio.ashihara.item.IHasCustomModel;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.item.BlockItem;
import net.minecraft.item.BlockNamedItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraftforge.items.ItemStackHandler;

import javax.annotation.Nullable;

import java.util.Arrays;

import static kogasastudio.ashihara.helper.RenderHelper.XTP;

public class ItemDisplayPos
{
    public final ItemStackHandler handler;
    public int slot;

    private float range;
    private Direction facing;
    private float[] pos;

    public ItemDisplayPos(ItemStackHandler handlerIn, int slotIn, float rangeIn, Direction facingIn, float[] posIn)
    {
        this.handler = handlerIn;
        this.slot = slotIn;
        this.range = rangeIn;
        this.facing = facingIn;
        if (posIn.length != 3) posIn = Arrays.copyOfRange(posIn, 0, 2);
        this.pos = posIn;
    }

    public ItemDisplayPos(ItemStackHandler handlerIn, int slotIn, int rangeIn, Direction facingIn)
    {
        this(handlerIn, slotIn, rangeIn, facingIn, new float[] {0.0f, 0.0f, 0.0f});
    }

    public ItemDisplayPos(ItemStackHandler handlerIn, int slotIn)
    {
        this(handlerIn, slotIn, 16, Direction.NORTH, new float[] {0.0f, 0.0f, 0.0f});
    }

    public ItemStack getDisplayStack() {return this.handler.getStackInSlot(this.slot).copy();}

    @Nullable
    public IHasCustomModel getItemCustomModel()
    {
        return this.getDisplayStack().getItem() instanceof IHasCustomModel ? (IHasCustomModel) this.getDisplayStack().getItem() : null;
    }

    public boolean hasCustomModel() {return this.getItemCustomModel() != null;}

    public void render(MatrixStack stackIn, IRenderTypeBuffer bufferIn, int combinedLightIn, int combinedOverlayIn)
    {
        stackIn.pushPose();
        if (this.getItemCustomModel() != null) this.getItemCustomModel().render(stackIn, bufferIn, combinedLightIn, combinedOverlayIn, this.getDisplayStack().getCount());
        else if (!this.getDisplayStack().isEmpty())
        {
            float[] translation = this.getTranslation();
            float scale = this.getScale();
            ItemStack stack = this.getDisplayStack();
            Direction facing = this.getFacing();
            boolean isBlock = stack.getItem() instanceof BlockItem && !(stack.getItem() instanceof BlockNamedItem);

//            float tHeight = isBlock ? 3.0f : 1.5f;
            stackIn.translate(XTP(translation[0]), XTP(translation[1]), XTP(translation[2]));
            stackIn.scale(scale, scale, scale);
            if (!isBlock)
            {
                stackIn.mulPose(Vector3f.XP.rotationDegrees(90.0f));
                stackIn.mulPose(Vector3f.ZP.rotationDegrees(facing.toYRot()));
            }
            else stackIn.mulPose(Vector3f.YP.rotationDegrees(facing.toYRot()));

            ItemRenderer renderer = Minecraft.getInstance().getItemRenderer();
            for (int i = 0; i < stack.getCount(); i += 1)
            {
                if (i != 0 ) stackIn.translate(XTP(0.0f), XTP(isBlock ? (1.0f / scale) * 4.0f : 0.0f), XTP(isBlock ? 0.0f : -1.2f));
                renderer.renderStatic(stack, ItemCameraTransforms.TransformType.FIXED, combinedLightIn, combinedOverlayIn, stackIn, bufferIn);
            }
        }
        stackIn.popPose();
    }

    public float[] getTranslation() {return this.pos;}

    public Direction getFacing() {return this.facing;}

    public float[] getPos(BlockPos posIn)
    {
        float[] pos = new float[3];
        pos[0] = posIn.getX() + this.pos[0];
        pos[1] = posIn.getY() + this.pos[1];
        pos[2] = posIn.getZ() + this.pos[2];
        return pos;
    }

    public float getScale() {return XTP(this.range);}

    public CompoundNBT serializeNBT(CompoundNBT compound)
    {
        compound.putInt("slotID", this.slot);
        compound.putString("facing", this.facing.getSerializedName());
        compound.putFloat("range", this.range);
        compound.putFloat("x", this.pos[0]);
        compound.putFloat("y", this.pos[1]);
        compound.putFloat("z", this.pos[2]);

        return compound;
    }

    public void deserializeNBT(CompoundNBT compound)
    {
        this.slot = compound.getInt("slotID");
        this.range = compound.getInt("range");
        this.facing = Direction.valueOf(compound.getString("facing"));
        this.pos[0] = compound.getFloat("x");
        this.pos[1] = compound.getFloat("y");
        this.pos[2] = compound.getFloat("z");
    }

    public void applyPos(float[] posIn) {this.pos = posIn;}
}