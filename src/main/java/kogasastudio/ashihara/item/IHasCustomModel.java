package kogasastudio.ashihara.item;

import net.minecraft.client.renderer.model.Model;

public interface IHasCustomModel
{
    Model getModel();

    default float getScale() {return 1.0f;}

    default int getModelStackSize() {return 4;}

    default float[] getTranslation(int stackSize) {return new float[]{8.0f, 0.0f, 8.0f};}

    default float getXOffset(int stackSize) {return 0.0f;}

    default float getYOffset(int stackSize) {return stackSize == 0 ? 0.0f : 1.2f;}

    default float getZOffset(int stackSize) {return 0.0f;}
}
