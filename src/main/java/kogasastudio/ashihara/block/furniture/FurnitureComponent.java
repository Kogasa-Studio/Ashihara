package kogasastudio.ashihara.block.furniture;

import kogasastudio.ashihara.block.building.BaseMultiBuiltBlock;
import kogasastudio.ashihara.block.building.component.BuildingComponent;
import kogasastudio.ashihara.block.building.component.ComponentStateDefinition;
import kogasastudio.ashihara.block.blockentity.MultiBuiltBlockEntity;
import kogasastudio.ashihara.registry.BuildingComponents;
import kogasastudio.ashihara.helper.ShapeHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.level.block.SoundType;

import java.util.List;

import java.util.List;
import java.util.function.Supplier;

public abstract class FurnitureComponent extends BuildingComponent
{
    public final FurnitureRenderPass rendererType;

    public FurnitureComponent
    (
        String idIn,
        BuildingComponents.Type typeIn,
        List<ItemStack> dropsIn,
        Supplier<BaseMultiBuiltBlock> materialIn,
        SoundType soundIn,
        FurnitureRenderPass rendererTypeIn
    )
    {
        super(idIn, typeIn, dropsIn, materialIn, soundIn);
        this.rendererType = rendererTypeIn;
    }

    public FurnitureComponent
    (
        String idIn,
        BuildingComponents.Type typeIn,
        Supplier<BaseMultiBuiltBlock> materialIn,
        List<ItemStack> dropsIn,
        FurnitureRenderPass rendererTypeIn
    )
    {
        super(idIn, typeIn, materialIn, dropsIn);
        this.rendererType = rendererTypeIn;
    }

    @Override
    public abstract ComponentStateDefinition definite(MultiBuiltBlockEntity beIn, UseOnContext context);

    /** Scale factor applied when rendering this component's model into chunk buffer. */
    public float modelScale() { return 1.0f; }

    public void onPlaced(MultiBuiltBlockEntity be, ComponentStateDefinition def) {}
    public void onRemoved(MultiBuiltBlockEntity be, ComponentStateDefinition def) {}

    /** Clamp in-block coordinate to [min, max] range. */
    protected static double clampInBlock(double v, double min, double max) { return Math.clamp(v, min, max); }

    /** Chebyshev distance check for overlap detection. */
    public static boolean tooClose(Vec3 a, Vec3 b, double threshold)
    {
        return tooClose(a, b, threshold, threshold, threshold);
    }

    /** Per-axis Chebyshev distance check. */
    public static boolean tooClose(Vec3 a, Vec3 b, double tx, double ty, double tz)
    {
        return Math.abs(a.x() - b.x()) < tx
            && Math.abs(a.y() - b.y()) < ty
            && Math.abs(a.z() - b.z()) < tz;
    }

    /**
     * Nudge a furniture definition away from existing components, or return
     * null if no valid position could be found.  The caller then decides
     * whether to place or merely preview the result.
     */
    @org.jspecify.annotations.Nullable
    public static ComponentStateDefinition tryNudge(List<ComponentStateDefinition> existing, ComponentStateDefinition def)
    {
        final double MAX_PROXIMITY = 0.125;
        var bb = def.shape().bounds();
        double tx = Math.min(MAX_PROXIMITY, bb.getXsize());
        double ty = Math.min(MAX_PROXIMITY, bb.getYsize());
        double tz = Math.min(MAX_PROXIMITY, bb.getZsize());
        Vec3 pos = def.inBlockPos();
        for (ComponentStateDefinition e : existing)
        {
            if (FurnitureProxyComponent.isProxy(e)) continue;
            Vec3 ep = e.inBlockPos();
            if (!tooClose(pos, ep, tx, ty, tz)) continue;
            double dx = pos.x() - ep.x(), dy = pos.y() - ep.y(), dz = pos.z() - ep.z();
            if (Math.abs(dx) < tx) pos = new Vec3(ep.x() + Math.signum(dx) * tx, pos.y(), pos.z());
            if (Math.abs(dy) < ty) pos = new Vec3(pos.x(), ep.y() + Math.signum(dy) * ty, pos.z());
            if (Math.abs(dz) < tz) pos = new Vec3(pos.x(), pos.y(), ep.z() + Math.signum(dz) * tz);
        }
        pos = new Vec3(Math.clamp(pos.x(), -0.5, 0.5), Math.clamp(pos.y(), 0.0, 1.0), Math.clamp(pos.z(), -0.5, 0.5));
        for (ComponentStateDefinition e : existing)
            if (!FurnitureProxyComponent.isProxy(e) && tooClose(e.inBlockPos(), pos, tx, ty, tz))
                return null;
        Vec3 delta = pos.subtract(def.inBlockPos());
        return new ComponentStateDefinition(def.component(), pos,
            def.rotationX(), def.rotationY(), def.rotationZ(),
            ShapeHelper.offsetShape(def.shape(), delta.x(), delta.y(), delta.z()),
            def.model(), def.occupation(), def.customData());
    }
}
