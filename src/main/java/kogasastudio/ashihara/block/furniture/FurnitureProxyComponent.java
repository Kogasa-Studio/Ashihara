package kogasastudio.ashihara.block.furniture;

import kogasastudio.ashihara.block.building.component.ComponentStateDefinition;
import kogasastudio.ashihara.block.blockentity.MultiBuiltBlockEntity;
import kogasastudio.ashihara.registry.Blocks;
import kogasastudio.ashihara.registry.BuildingComponents;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.Vec3;

import java.util.List;

/**
 * Sentinel component for multi-block furniture sub-blocks.
 * Material, sound, and drops are resolved at call sites via {@link ProxyData}.
 */
public class FurnitureProxyComponent extends FurnitureComponent implements ICustomData
{
    public record ProxyData(BlockPos mainPos, Vec3 mainInBlockPos) {}

    public FurnitureProxyComponent()
    {
        super("furniture_proxy", BuildingComponents.Type.BAKED_MODEL, List.of(),
              () -> Blocks.SPRUCE_WOOD_COMPONENT.get(), SoundType.BAMBOO, FurnitureRenderPass.CHUNK_BUFFER);
    }

    public static boolean isProxy(ComponentStateDefinition def)
    {
        return def.component() instanceof FurnitureProxyComponent;
    }

    public static ProxyData getData(ComponentStateDefinition def)
    {
        return def.customData() instanceof ProxyData pd ? pd : null;
    }

    @Override
    public List<ItemStack> getDrops(ComponentStateDefinition def, MultiBuiltBlockEntity mbe)
    {
        var pd = getData(def);
        if (pd != null && mbe.getLevel() != null)
        {
            var mainBe = mbe.getLevel().getBlockEntity(pd.mainPos());
            if (mainBe instanceof MultiBuiltBlockEntity mb)
                for (var d : mb.FURNITURE)
                    if (d.inBlockPos().equals(pd.mainInBlockPos()))
                        return d.component().getDrops(d, mb);
        }
        return List.of();
    }

    @Override
    public void serializeCustom(ValueOutput output, Object data)
    {
        if (data instanceof ProxyData pd)
        {
            output.putLong("mainPos", pd.mainPos().asLong());
            ValueOutput p = output.child("mainInBlock");
            p.putDouble("x", pd.mainInBlockPos().x());
            p.putDouble("y", pd.mainInBlockPos().y());
            p.putDouble("z", pd.mainInBlockPos().z());
        }
    }

    @Override
    public Object deserializeCustom(ValueInput input)
    {
        BlockPos mp = BlockPos.of(input.getLongOr("mainPos", 0));
        ValueInput p = input.childOrEmpty("mainInBlock");
        Vec3 ib = new Vec3(p.getDoubleOr("x", 0), p.getDoubleOr("y", 0), p.getDoubleOr("z", 0));
        return new ProxyData(mp, ib);
    }

    @Override
    public ComponentStateDefinition definite(MultiBuiltBlockEntity beIn, UseOnContext context) { return null; }
}
