package kogasastudio.ashihara.block.furniture;

import kogasastudio.ashihara.block.building.BaseMultiBuiltBlock;
import kogasastudio.ashihara.block.building.component.ComponentStateDefinition;
import kogasastudio.ashihara.block.building.component.Interactable;
import kogasastudio.ashihara.block.blockentity.MultiBuiltBlockEntity;
import kogasastudio.ashihara.registry.BuildingComponents;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.NbtOps;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.ItemCapability;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.StacksResourceHandler;
import net.neoforged.neoforge.transfer.fluid.FluidResource;
import net.neoforged.neoforge.transfer.fluid.FluidStacksResourceHandler;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.item.ItemStacksResourceHandler;
import net.neoforged.neoforge.transfer.transaction.Transaction;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public abstract class ContainerComponent extends FurnitureComponent
    implements ICustomRender, ICustomData, Interactable
{
    public ContainerComponent(String idIn, BuildingComponents.Type typeIn,
        Supplier<BaseMultiBuiltBlock> materialIn, List<ItemStack> dropsIn,
        FurnitureRenderPass rendererPassIn)
    {
        super(idIn, typeIn, materialIn, dropsIn, rendererPassIn);
    }

    // --------------------------------------------------
    // ItemStack content
    // --------------------------------------------------

    private static final String CONTENT_TAG = "bowl_food";
    private static final String FLUID_TAG = "bowl_fluid";

    public static ItemStack getContent(ItemStack container)
    {
        var cd = container.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY);
        if (!cd.contains(CONTENT_TAG)) return ItemStack.EMPTY;
        var tag = cd.copyTag().get(CONTENT_TAG);
        return tag != null ? ItemStack.CODEC.parse(NbtOps.INSTANCE, tag).result().orElse(ItemStack.EMPTY) : ItemStack.EMPTY;
    }

    public static void setContent(ItemStack container, ItemStack food)
    {
        CustomData.update(DataComponents.CUSTOM_DATA, container, tag ->
        {
            if (food.isEmpty()) { tag.remove(CONTENT_TAG); tag.remove(FLUID_TAG); }
            else tag.put(CONTENT_TAG, ItemStack.CODEC.encodeStart(NbtOps.INSTANCE, food).getOrThrow());
        });
    }

    // --------------------------------------------------
    // Fluid content
    // --------------------------------------------------

    public static FluidStack getFluidContent(ItemStack container)
    {
        var cd = container.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY);
        if (!cd.contains(FLUID_TAG)) return FluidStack.EMPTY;
        var tag = cd.copyTag().get(FLUID_TAG);
        return tag != null ? FluidStack.CODEC.parse(NbtOps.INSTANCE, tag).result().orElse(FluidStack.EMPTY) : FluidStack.EMPTY;
    }

    public static void setFluidContent(ItemStack container, FluidStack fluid)
    {
        CustomData.update(DataComponents.CUSTOM_DATA, container, tag ->
        {
            if (fluid.isEmpty()) { tag.remove(FLUID_TAG); tag.remove(CONTENT_TAG); }
            else tag.put(FLUID_TAG, FluidStack.CODEC.encodeStart(NbtOps.INSTANCE, fluid).getOrThrow());
        });
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private static ResourceHandler<FluidResource> getFluidCap(ItemStack stack)
    {
        return (ResourceHandler<FluidResource>) (Object) stack.getCapability((ItemCapability) Capabilities.Fluid.ITEM);
    }

    // --------------------------------------------------
    // Sound helpers
    // --------------------------------------------------

    protected static void playInsertSound(net.minecraft.world.entity.Entity entity)
    {
        entity.playSound(SoundEvents.BUNDLE_INSERT, 0.8F, 0.8F + entity.level().getRandom().nextFloat() * 0.4F);
    }

    protected static void playRemoveOneSound(net.minecraft.world.entity.Entity entity)
    {
        entity.playSound(SoundEvents.BUNDLE_REMOVE_ONE, 0.8F, 0.8F + entity.level().getRandom().nextFloat() * 0.4F);
    }

    protected static void playBucketFillSound(net.minecraft.world.entity.Entity entity, FluidStack fluid)
    {
        var s = fluid.getFluidType().getSound(fluid, net.neoforged.neoforge.common.SoundActions.BUCKET_FILL);
        if (s != null) entity.playSound(s, 1.0F, 1.0F);
    }

    protected static void playBucketEmptySound(net.minecraft.world.entity.Entity entity, FluidStack fluid)
    {
        var s = fluid.getFluidType().getSound(fluid, net.neoforged.neoforge.common.SoundActions.BUCKET_EMPTY);
        if (s != null) entity.playSound(s, 1.0F, 1.0F);
    }

    protected abstract StacksResourceHandler<?, ?> createContentHandler();
    protected abstract FluidStacksResourceHandler createFluidHandler();

    public static ContainerContent contentOf(ComponentStateDefinition def)
    {
        return def.customData() instanceof ContainerContent cc ? cc : ContainerContent.EMPTY;
    }

    @Nullable
    public static StacksResourceHandler<?, ?> getHandler(ComponentStateDefinition def)
    {
        return contentOf(def).handler();
    }

    public static boolean hasContent(ComponentStateDefinition def)
    {
        return !contentOf(def).isEmpty();
    }

    @Override
    public boolean doRender(MultiBuiltBlockEntity be, ComponentStateDefinition def)
    {
        return hasContent(def);
    }

    @Override
    public List<ItemStack> getDrops(ComponentStateDefinition def, MultiBuiltBlockEntity be)
    {
        List<ItemStack> result = new ArrayList<>(super.getDrops(def, be));
        if (def.customData() instanceof ContainerContent cc && cc.handler() instanceof ItemStacksResourceHandler handler)
        {
            for (int i = 0; i < handler.size(); i++)
                if (!handler.getResource(i).isEmpty())
                    result.add(handler.getResource(i).toStack((int) handler.getAmountAsLong(i)));
        }
        return result;
    }

    // --------------------------------------------------
    // ICustomData
    // --------------------------------------------------

    @Override
    public void serializeCustom(ValueOutput output, Object customData)
    {
        if (!(customData instanceof ContainerContent cc) || cc.isEmpty()) return;
        output.putString("type", cc.type().name());
        cc.handler().serialize(output.child("handler"));
    }

    @Override
    public Object deserializeCustom(ValueInput input)
    {
        String t = input.getStringOr("type", "EMPTY");
        ContainerState.ContentType type = ContainerState.ContentType.valueOf(t);
        if (type == ContainerState.ContentType.EMPTY) return null;
        StacksResourceHandler<?, ?> h = type == ContainerState.ContentType.FLUID
            ? createFluidHandler() : createContentHandler();
        h.deserialize(input.childOrEmpty("handler"));
        return new ContainerContent(type, h);
    }

    // --------------------------------------------------
    // Interactable
    // --------------------------------------------------

    @Override
    public ComponentStateDefinition handleInteraction(UseOnContext context, ComponentStateDefinition definition)
    {
        Player player = context.getPlayer();
        if (player == null) return definition;

        ContainerContent cc = contentOf(definition);
        ItemStack held = context.getItemInHand();

        // ---- EMPTY: try insert fluid or food ----
        if (cc.handler() == null)
        {
            if (!held.isEmpty())
            {
                var heldFluid = getFluidCap(held);
                if (heldFluid != null && !heldFluid.getResource(0).isEmpty())
                {
                    FluidStacksResourceHandler fh = createFluidHandler();
                    try (Transaction tx = Transaction.openRoot())
                    {
                        var res = heldFluid.getResource(0);
                        int moved = fh.insert(0, res, (int) heldFluid.getAmountAsLong(0), tx);
                        if (moved > 0) { heldFluid.extract(0, res, moved, tx); tx.commit(); }
                    }
                    return new ComponentStateDefinition(definition.component(),
                        definition.inBlockPos(), definition.rotationX(), definition.rotationY(), definition.rotationZ(),
                        definition.shape(), definition.model(), definition.occupation(),
                        new ContainerContent(ContainerState.ContentType.FLUID, fh));
                }
                if (held.has(DataComponents.FOOD))
                {
                    var ih = (ItemStacksResourceHandler) createContentHandler();
                    var res = ItemResource.of(held.getItem(), DataComponentPatch.EMPTY);
                    try (Transaction tx = Transaction.openRoot())
                    {
                        if (ih.insert(0, res, 1, tx) > 0) { tx.commit(); held.shrink(1); }
                    }
                    return new ComponentStateDefinition(definition.component(),
                        definition.inBlockPos(), definition.rotationX(), definition.rotationY(), definition.rotationZ(),
                        definition.shape(), definition.model(), definition.occupation(),
                        new ContainerContent(ContainerState.ContentType.ITEM, ih));
                }
            }
            return definition;
        }

        // ---- FLUID path ----
        if (cc.handler() instanceof FluidStacksResourceHandler fh)
        {
            FluidStack stored = FluidStack.EMPTY;
            if (fh.getAmountAsLong(0) > 0)
                stored = fh.getResource(0).toStack((int) fh.getAmountAsLong(0));

            // Shift + empty hand: dump fluid
            if (held.isEmpty() && player.isShiftKeyDown() && !stored.isEmpty())
            {
                try (Transaction tx = Transaction.openRoot())
                {
                    fh.extract(0, fh.getResource(0), (int) fh.getAmountAsLong(0), tx);
                    tx.commit();
                }
                return new ComponentStateDefinition(definition.component(),
                    definition.inBlockPos(), definition.rotationX(), definition.rotationY(), definition.rotationZ(),
                    definition.shape(), definition.model(), definition.occupation(),
                    new ContainerContent(ContainerState.ContentType.FLUID, fh));
            }

            // Held fluid handler interaction
            if (!held.isEmpty())
            {
                var heldFluid = getFluidCap(held);
                if (heldFluid != null)
                {
                    if (!stored.isEmpty() && heldFluid.getResource(0).isEmpty())
                    {
                        // Drain bowl into held
                        try (Transaction tx = Transaction.openRoot())
                        {
                            int moved = heldFluid.insert(FluidResource.of(stored.getFluid()), stored.getAmount(), tx);
                            if (moved > 0)
                            {
                                fh.extract(0, fh.getResource(0), moved, tx);
                                tx.commit();
                                playBucketFillSound(player, stored);
                            }
                        }
                    }
                    else if (stored.isEmpty() && !heldFluid.getResource(0).isEmpty())
                    {
                        // Fill bowl from held
                        try (Transaction tx = Transaction.openRoot())
                        {
                            var res = heldFluid.getResource(0);
                            int moved = fh.insert(0, res, (int) heldFluid.getAmountAsLong(0), tx);
                            if (moved > 0)
                            {
                                heldFluid.extract(0, res, moved, tx);
                                tx.commit();
                            }
                        }
                    }
                    return new ComponentStateDefinition(definition.component(),
                        definition.inBlockPos(), definition.rotationX(), definition.rotationY(), definition.rotationZ(),
                        definition.shape(), definition.model(), definition.occupation(),
                        new ContainerContent(ContainerState.ContentType.FLUID, fh));
                }
            }
            return definition;
        }

        // ---- ITEM path ----
        var ih = (ItemStacksResourceHandler) cc.handler();
        if (ih == null) return definition;

        if (held.isEmpty() && player.isShiftKeyDown())
        {
            BlockEntity be = context.getLevel().getBlockEntity(context.getClickedPos());
            if (be instanceof MultiBuiltBlockEntity mbe)
                for (ItemStack s : getDrops(definition, mbe)) popItem(player, s.copy());
            else popItem(player, this.drops.getFirst().copy());
            return null;
        }

        if (ih.getAmountAsLong(0) > 0)
        {
            var res = ih.getResource(0);
            int amount = (int) ih.getAmountAsLong(0);
            try (Transaction tx = Transaction.openRoot())
            {
                ih.extract(0, res, amount, tx);
                tx.commit();
                popItem(player, res.toStack(amount));
            }
            return new ComponentStateDefinition(definition.component(),
                definition.inBlockPos(), definition.rotationX(), definition.rotationY(), definition.rotationZ(),
                definition.shape(), definition.model(), definition.occupation(),
                new ContainerContent(ContainerState.ContentType.ITEM, ih));
        }

        if (!held.isEmpty() && held.has(DataComponents.FOOD))
        {
            var res = ItemResource.of(held.getItem(), DataComponentPatch.EMPTY);
            try (Transaction tx = Transaction.openRoot())
            {
                if (ih.insert(0, res, 1, tx) > 0) { tx.commit(); held.shrink(1); }
            }
            return new ComponentStateDefinition(definition.component(),
                definition.inBlockPos(), definition.rotationX(), definition.rotationY(), definition.rotationZ(),
                definition.shape(), definition.model(), definition.occupation(),
                new ContainerContent(ContainerState.ContentType.ITEM, ih));
        }

        return definition;
    }

    protected static void popItem(Player p, ItemStack s)
    {
        if (!p.getInventory().add(s))
            p.level().addFreshEntity(new ItemEntity(p.level(), p.getX(), p.getY(), p.getZ(), s));
    }

    protected abstract ContainerState.ContainerType containerType();
    protected abstract ContainerState.ContainerSize size();
}