package kogasastudio.ashihara.block.furniture;

import kogasastudio.ashihara.block.building.BaseMultiBuiltBlock;
import kogasastudio.ashihara.block.building.component.ComponentStateDefinition;
import kogasastudio.ashihara.block.blockentity.MultiBuiltBlockEntity;
import kogasastudio.ashihara.helper.ShapeHelper;
import kogasastudio.ashihara.registry.BuildingComponents;
import kogasastudio.ashihara.registry.FurnitureComponents;
import kogasastudio.ashihara.utils.BuildingComponentModelResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.StacksResourceHandler;
import net.neoforged.neoforge.transfer.access.ItemAccess;
import net.neoforged.neoforge.transfer.fluid.FluidResource;
import net.neoforged.neoforge.transfer.fluid.FluidStacksResourceHandler;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.item.ItemStacksResourceHandler;
import net.neoforged.neoforge.transfer.transaction.Transaction;

import java.util.List;
import java.util.function.Supplier;

public class WoodenBowlComponent extends ContainerComponent
{
    private final BuildingComponentModelResourceLocation MODEL;
    private final VoxelShape SHAPE;

    public WoodenBowlComponent(String idIn, BuildingComponents.Type typeIn,
        BuildingComponentModelResourceLocation model, VoxelShape shape,
        Supplier<BaseMultiBuiltBlock> materialIn,
        List<ItemStack> dropsIn, FurnitureRenderPass rendererTypeIn)
    {
        super(idIn, typeIn, materialIn, dropsIn, rendererTypeIn);
        this.MODEL = model;
        this.SHAPE = shape != null ? shape
            : Shapes.box(5.5f / 16, 0, 5.5f / 16, 10.5f / 16, 3.5f / 16, 10.5f / 16);
    }

    @Override protected StacksResourceHandler<?, ?> createContentHandler() { return new ItemStacksResourceHandler(1); }

    @Override protected FluidStacksResourceHandler createFluidHandler() { return new FluidStacksResourceHandler(1, 100); }

    @Override protected ContainerState.ContainerType containerType() { return ContainerState.ContainerType.BOWL; }
    @Override protected ContainerState.ContainerSize size() { return ContainerState.ContainerSize.MID; }

    @Override
    public ComponentStateDefinition definite(MultiBuiltBlockEntity beIn, UseOnContext context)
    {
        Vec3 inBlock = beIn.inBlockVec(context.getClickLocation());
        double x = inBlock.x() - 8f / 16;
        double y = inBlock.y();
        double z = inBlock.z() - 8f / 16;
        VoxelShape shape = ShapeHelper.offsetShape(this.SHAPE, x, y, z);
        ItemStack held = context.getItemInHand();

        if (!(context instanceof SnappedUseOnContext sc && sc.simulate()))
        {
            // Try fluid first
            if (!held.isEmpty())
            {
                @SuppressWarnings({"unchecked", "rawtypes"})
                ResourceHandler<FluidResource> fluidCap = ContainerComponent.getFluidCap(held, ItemAccess.forStack(held));
                if (fluidCap != null && !fluidCap.getResource(0).isEmpty())
                {
                    FluidStacksResourceHandler fh = createFluidHandler();
                    try (Transaction tx = Transaction.openRoot())
                    {
                        FluidResource res = fluidCap.getResource(0);
                        fh.insert(0, res, (int) fluidCap.getAmountAsLong(0), tx);
                        tx.commit();
                    }
                    return new ComponentStateDefinition(FurnitureComponents.get(this.id),
                                                        new Vec3(x, y, z), 0, 0, 0, shape, MODEL, List.of(),
                                                        new ContainerContent(ContainerState.ContentType.FLUID, fh));
                }
            }

            // Fallback: item content
            var cc = new ContainerContent(ContainerState.ContentType.ITEM, createContentHandler());
            ItemStack stack = getContent(context.getItemInHand());
            if (cc.handler() instanceof ItemStacksResourceHandler is && !stack.isEmpty())
            {
                try (Transaction tx = Transaction.openRoot())
                {
                    is.insert(ItemResource.of(stack), stack.count(), tx);
                    tx.commit();
                }
                return new ComponentStateDefinition(FurnitureComponents.get(this.id), new Vec3(x, y, z), 0, 0, 0, shape, MODEL, List.of(), cc);
            }
        }

        return new ComponentStateDefinition(FurnitureComponents.get(this.id), new Vec3(x, y, z), 0, 0, 0, shape, MODEL, List.of(), null);
    }
}