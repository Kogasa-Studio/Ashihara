package kogasastudio.ashihara.client.gui3d.components;

import kogasastudio.ashihara.client.gui3d.util.OBB;
import kogasastudio.ashihara.client.render.state.GUI3DComponentRenderState;
import kogasastudio.ashihara.helper.RenderHelper;
import net.neoforged.neoforge.fluids.FluidStack;

import java.util.List;
import java.util.function.Supplier;

public class FluidDisplayComponent extends AbstractComponent
{
    public Supplier<FluidStack> fluid;
    public Supplier<OBB> obb;

    public FluidDisplayComponent(Supplier<FluidStack> fluid, Supplier<OBB> obb)
    {
        this.fluid = fluid;
        this.obb = obb;
    }

    public FluidStack getFluidStack()
    {
        return fluid.get() == null ? FluidStack.EMPTY : fluid.get();
    }

    @Override
    protected void collectSelfRenderStates(List<GUI3DComponentRenderState> output, int mouseX, int mouseY, float partialTick)
    {
        FluidStack fluidStack = getFluidStack();
        if (fluidStack.isEmpty()) return;

        OBB obb = this.obb.get();
        if (obb == null) return;
        output.add(new GUI3DComponentRenderState((poseStack, submitNodeCollector) ->
        {
            poseStack.pushPose();
            poseStack.translate(0.5, 0.5, 0.5);
            RenderHelper.renderFluidOnOBB(poseStack, obb, fluidStack, submitNodeCollector, 15728880, 1);
            poseStack.popPose();
        }, true));
    }
}
