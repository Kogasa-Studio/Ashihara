package kogasastudio.ashihara.block.furniture;

import net.neoforged.neoforge.transfer.StacksResourceHandler;
import kogasastudio.ashihara.block.building.BaseMultiBuiltBlock;
import kogasastudio.ashihara.block.building.component.ComponentStateDefinition;
import kogasastudio.ashihara.block.blockentity.MultiBuiltBlockEntity;
import kogasastudio.ashihara.registry.BuildingComponents;
import kogasastudio.ashihara.registry.DataComponentTypes;
import kogasastudio.ashihara.registry.FurnitureComponents;
import kogasastudio.ashihara.helper.ShapeHelper;
import kogasastudio.ashihara.utils.BuildingComponentModelResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.item.ItemStacksResourceHandler;
import net.neoforged.neoforge.transfer.fluid.FluidStacksResourceHandler;
import net.neoforged.neoforge.transfer.transaction.Transaction;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.access.ItemAccess;
import net.neoforged.neoforge.transfer.fluid.FluidResource;

import java.util.List;
import java.util.function.Supplier;

/**
 * Fully parameterized container component for any ContainerType/ContainerSize combination.
 */
public class SimpleContainerComponent extends ContainerComponent
{
    private final BuildingComponentModelResourceLocation MODEL;
    private final VoxelShape SHAPE;
    private final ContainerState.ContainerType containerType;
    private final ContainerState.ContainerSize containerSize;

    public SimpleContainerComponent
    (
        String idIn, BuildingComponents.Type typeIn,
        BuildingComponentModelResourceLocation model, VoxelShape shape,
        Supplier<BaseMultiBuiltBlock> materialIn, List<ItemStack> dropsIn,
        FurnitureRenderPass rendererPassIn,
        ContainerState.ContainerType containerType, ContainerState.ContainerSize containerSize,
        int containerStorage, int maxBites
    )
    {
        super(idIn, typeIn, materialIn, dropsIn, rendererPassIn, containerStorage, maxBites);
        this.MODEL = model;
        this.SHAPE = shape;
        this.containerType = containerType;
        this.containerSize = containerSize;
    }

    @Override protected StacksResourceHandler<?, ?> createContentHandler()
    {
        return new ItemStacksResourceHandler(1);
    }
    @Override protected FluidStacksResourceHandler createFluidHandler() { return new FluidStacksResourceHandler(1, 100); }
    @Override public ContainerState.ContainerType containerType() { return containerType; }
    @Override public ContainerState.ContainerSize size() { return containerSize; }

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
                    return new ComponentStateDefinition(FurnitureComponents.get(this.id), new Vec3(x, y, z), 0, 0, 0, shape, MODEL, List.of(), new ContainerContent(ContainerState.ContentType.FLUID, fh));
                }
            }

            // Fallback: item content
            int cl = held.getOrDefault(DataComponentTypes.CHOP_LEFT.get(), 0);
            var cc = new ContainerContent(ContainerState.ContentType.ITEM, createContentHandler(), cl);
            ItemStack stack = getContent(context.getItemInHand());
            if (cc.handler() instanceof ItemStacksResourceHandler is && !stack.isEmpty())
            {
                try (Transaction tx = Transaction.openRoot())
                {
                    is.insert(ItemResource.of(stack), stack.count(), tx);
                    tx.commit();
                }
                held.remove(DataComponentTypes.CHOP_LEFT.get());
                held.remove(DataComponentTypes.MAX_BITES.get());
                return new ComponentStateDefinition(FurnitureComponents.get(this.id), new Vec3(x, y, z), 0, 0, 0, shape, MODEL, List.of(), cc);
            }
        }

        return new ComponentStateDefinition(FurnitureComponents.get(this.id), new Vec3(x, y, z), 0, 0, 0, shape, MODEL, List.of(), null);
    }
}
