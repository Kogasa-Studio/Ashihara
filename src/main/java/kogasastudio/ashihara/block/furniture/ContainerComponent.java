package kogasastudio.ashihara.block.furniture;

import kogasastudio.ashihara.block.building.BaseMultiBuiltBlock;
import kogasastudio.ashihara.block.building.component.ComponentStateDefinition;
import kogasastudio.ashihara.block.building.component.Interactable;
import kogasastudio.ashihara.block.blockentity.MultiBuiltBlockEntity;
import kogasastudio.ashihara.datacomponent.ChopsticksFood;
import kogasastudio.ashihara.item.IContainerItem;
import kogasastudio.ashihara.helper.BowlFoodHelper;
import kogasastudio.ashihara.registry.BuildingComponents;
import kogasastudio.ashihara.registry.DataComponentTypes;
import kogasastudio.ashihara.registry.Items;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.NbtOps;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.ItemCapability;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.SimpleFluidContent;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.ResourceHandlerUtil;
import net.neoforged.neoforge.transfer.StacksResourceHandler;
import net.neoforged.neoforge.transfer.access.ItemAccess;
import net.neoforged.neoforge.transfer.fluid.FluidResource;
import net.neoforged.neoforge.transfer.fluid.FluidStacksResourceHandler;
import net.neoforged.neoforge.transfer.fluid.FluidUtil;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.item.ItemStacksResourceHandler;
import net.neoforged.neoforge.transfer.resource.ResourceStack;
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
    { super(idIn, typeIn, materialIn, dropsIn, rendererPassIn); }

    // --------------------------------------------------
    // ItemStack content
    // --------------------------------------------------
    private static final String CONTENT_TAG = "bowl_food";

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
        { if (food.isEmpty()) { tag.remove(CONTENT_TAG); BowlFoodHelper.clear(container); }
          else { tag.put(CONTENT_TAG, ItemStack.CODEC.encodeStart(NbtOps.INSTANCE, food).getOrThrow()); BowlFoodHelper.applyFood(container, food); } });
    }

    // --------------------------------------------------
    // Fluid content
    // --------------------------------------------------
    public static FluidStack getFluidContent(ItemStack container)
    { return container.getOrDefault(DataComponentTypes.FLUID_CONTENT, SimpleFluidContent.EMPTY).copy(); }

    public static void setFluidContent(ItemStack container, FluidStack fluid)
    {
        container.set(DataComponentTypes.FLUID_CONTENT, SimpleFluidContent.copyOf(fluid));
        if (fluid.isEmpty()) BowlFoodHelper.clear(container);
        else BowlFoodHelper.applyFluid(container, fluid);
    }

    // --------------------------------------------------
    // Sound helpers
    // --------------------------------------------------
    public static void playInsertSound(net.minecraft.world.entity.Entity entity)
    { entity.playSound(SoundEvents.BUNDLE_INSERT, 0.8F, 0.8F + entity.level().getRandom().nextFloat() * 0.4F); }

    public static void playRemoveOneSound(net.minecraft.world.entity.Entity entity)
    { entity.playSound(SoundEvents.BUNDLE_REMOVE_ONE, 0.8F, 0.8F + entity.level().getRandom().nextFloat() * 0.4F); }

    protected abstract StacksResourceHandler<?, ?> createContentHandler();
    protected abstract FluidStacksResourceHandler createFluidHandler();

    public static ContainerContent contentOf(ComponentStateDefinition def)
    { return def.customData() instanceof ContainerContent cc ? cc : ContainerContent.EMPTY; }

    @Nullable public static StacksResourceHandler<?, ?> getHandler(ComponentStateDefinition def) { return contentOf(def).handler(); }
    public static boolean hasContent(ComponentStateDefinition def) { return !contentOf(def).isEmpty(); }
    @Override public boolean doRender(MultiBuiltBlockEntity be, ComponentStateDefinition def) { return hasContent(def); }

    @Override
    public List<ItemStack> getDrops(ComponentStateDefinition def, MultiBuiltBlockEntity be)
    {
        List<ItemStack> result = new ArrayList<>(super.getDrops(def, be));
        if (def.customData() instanceof ContainerContent cc && cc.handler() instanceof ItemStacksResourceHandler handler)
            for (int i = 0; i < handler.size(); i++)
                if (!handler.getResource(i).isEmpty()) result.add(handler.getResource(i).toStack((int) handler.getAmountAsLong(i)));
        return result;
    }

    // --------------------------------------------------
    // ICustomData
    // --------------------------------------------------
    @Override public void serializeCustom(ValueOutput output, Object customData)
    {
        if (!(customData instanceof ContainerContent cc) || cc.isEmpty()) return;
        output.putString("type", cc.type().name());
        cc.handler().serialize(output.child("handler"));
        if (cc.chopLeft() > 0) output.putInt("chop_left", cc.chopLeft());
    }

    @Override public Object deserializeCustom(ValueInput input)
    {
        String t = input.getStringOr("type", "EMPTY");
        ContainerState.ContentType type = ContainerState.ContentType.valueOf(t);
        if (type == ContainerState.ContentType.EMPTY) return null;
        StacksResourceHandler<?, ?> h = type == ContainerState.ContentType.FLUID ? createFluidHandler() : createContentHandler();
        h.deserialize(input.childOrEmpty("handler"));
        int cl = input.getIntOr("chop_left", 0);
        return new ContainerContent(type, h, cl);
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
        ItemAccess access = ItemAccess.forPlayerInteraction(context.getPlayer(), context.getHand()).oneByOne();

        // ---- Shift + empty hand: pick up bowl with contents ----
        if (held.isEmpty() && player.isShiftKeyDown())
        {
            ItemStack bowl = getDropItem();
            if (cc.chopLeft() > 0)
            {
               bowl.set(DataComponentTypes.CHOP_LEFT.get(), cc.chopLeft());
               bowl.set(DataComponentTypes.MAX_BITES.get(), maxBites());
            }
            if (cc.handler() != null)
            {
                switch (cc.handler())
                {
                    case ItemStacksResourceHandler ih when ih.getAmountAsLong(0) > 0 ->
                    {
                        ItemResource res = ih.getResource(0);
                        int amount = (int) ih.getAmountAsLong(0);
                        try (Transaction tx = Transaction.openRoot())
                        { ih.extract(0, res, amount, tx); tx.commit(); }
                        setContent(bowl, res.toStack(amount));
                    }
                    case FluidStacksResourceHandler fh when fh.getAmountAsLong(0) > 0 ->
                    {
                        FluidStack fluid = fh.getResource(0).toStack((int) fh.getAmountAsLong(0));
                        try (Transaction tx = Transaction.openRoot())
                        { fh.extract(0, fh.getResource(0), (int) fh.getAmountAsLong(0), tx); tx.commit(); }
                        setFluidContent(bowl, fluid);
                    }
                    default -> {}
                }
            }
            popItem(player, bowl);
            return null;
        }

        // ---- EMPTY: insert fluid or food ----
        switch (cc.handler())
        {
            case null ->
            {
                if (held.isEmpty()) return definition;

                FluidStacksResourceHandler fh = createFluidHandler();
                var heldFluid = getFluidCap(held, access);
                if (heldFluid != null)
                {
                    ResourceStack<FluidResource> rs = tryMove(heldFluid, fh);
                    if (rs != null)
                    {
                        FluidUtil.triggerSoundAndGameEvent(rs.resource(), context.getLevel(), context.getClickedPos().getCenter(), player, false);
                        return withContent(definition, ContainerState.ContentType.FLUID, fh);
                    }
                }

                if (held.has(DataComponents.FOOD))
                {
                    if (held.has(DataComponents.USE_REMAINDER)) return definition;
                    if (held.getItem() instanceof IContainerItem) return definition;
                    var ih = (ItemStacksResourceHandler) createContentHandler();
                    try (Transaction tx = Transaction.openRoot())
                    {
                        if (ih.insert(0, ItemResource.of(held.getItem(), DataComponentPatch.EMPTY), 1, tx) > 0)
                        { tx.commit(); held.shrink(1); }
                    }
                    playInsertSound(player);
                    return withContent(definition, ContainerState.ContentType.ITEM, ih);
                }
                return definition;
            }

            // ---- FLUID bowl ----
            case FluidStacksResourceHandler fh ->
            {
                var heldFluid = getFluidCap(held, access);
                if (heldFluid != null)
                {
                    boolean flag = false;
                    ResourceStack<FluidResource> rs = tryMove(heldFluid, fh);
                    if (rs != null)
                    {
                        flag = true;
                        FluidUtil.triggerSoundAndGameEvent(rs.resource(), context.getLevel(), context.getClickedPos().getCenter(), player, false);
                    }
                    else
                    {
                        rs = tryMove(fh, heldFluid);
                        if (rs != null)
                        {
                            flag = true;
                            FluidUtil.triggerSoundAndGameEvent(rs.resource(), context.getLevel(), context.getClickedPos().getCenter(), player, true);
                        }
                    }
                    if (flag)
                    {
                        ContainerState.ContentType t = fh.getResource(0).isEmpty() ? ContainerState.ContentType.EMPTY : ContainerState.ContentType.FLUID;
                        fh = fh.getResource(0).isEmpty() ? null : fh;
                        return withContent(definition, t, fh);
                    }
                }
                return definition;
            }

            // ---- ITEM bowl ----
            case ItemStacksResourceHandler ih ->
            {
                // Chopsticks: set/consume chopLeft
                if (!held.isEmpty() && held.is(Items.CHOPSTICKS.get()))
                {
                    if (held.has(DataComponentTypes.CHOPSTICKS_FOOD.get())) return definition; // already carrying food
                    if (cc.chopLeft() == 0) cc = new ContainerContent(cc.type(), cc.handler(), maxBites());
                    int cl = cc.chopLeft() - 1;
                    var foodRes = ih.getResource(0);
                    int bitesPerItem = Math.max(1, maxBites() / containerStorage());
                    var foodStack = foodRes.toStack(1);
                    held.set(DataComponentTypes.CHOPSTICKS_FOOD.get(), new ChopsticksFood(foodStack.copy(), bitesPerItem));
                    BowlFoodHelper.applyFoodChopsticks(held, foodStack, bitesPerItem);
                    if (cl <= 0)
                    {
                        try (Transaction tx = Transaction.openRoot())
                        { ih.extract(0, foodRes, (int) ih.getAmountAsLong(0), tx); tx.commit(); }
                        player.playSound(SoundEvents.ITEM_PICKUP, 0.8f, 1.0f);
                        return withContent(definition, ContainerState.ContentType.EMPTY, null);
                    }
                    player.playSound(SoundEvents.ITEM_PICKUP, 0.8f, 1.0f);
                    return withContentAndChop(definition, ContainerState.ContentType.ITEM, ih, cl);
                }
                if (ih.getAmountAsLong(0) > 0)
                {
                    if (cc.chopLeft() > 0) return definition;
                    var res = ih.getResource(0);
                    int amount = (int) ih.getAmountAsLong(0);
                    try (Transaction tx = Transaction.openRoot())
                    { ih.extract(0, res, amount, tx); tx.commit(); popItem(player, res.toStack(amount)); }
                    playRemoveOneSound(player);
                    ContainerState.ContentType ct = ih.getResource(0).isEmpty() ? ContainerState.ContentType.EMPTY : ContainerState.ContentType.ITEM;
                    ih = ih.getResource(0).isEmpty() ? null : ih;
                    return withContent(definition, ct, ih);
                }
                if (!held.isEmpty() && held.has(DataComponents.FOOD))
                {
                    if (held.has(DataComponents.USE_REMAINDER)) return definition;
                    if (held.getItem() instanceof IContainerItem) return definition;
                    var res = ItemResource.of(held.getItem(), DataComponentPatch.EMPTY);
                    try (Transaction tx = Transaction.openRoot())
                    { if (ih.insert(0, res, 1, tx) > 0) { tx.commit(); held.shrink(1); } }
                    playInsertSound(player);
                    return withContent(definition, ContainerState.ContentType.ITEM, ih);
                }
            }
            default -> {}
        }

        return definition;
    }

    @Override
    public SoundType getInteractSound()
    {
        return SoundType.EMPTY;
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    @Nullable
    public static ResourceHandler<FluidResource> getFluidCap(ItemStack stack, ItemAccess access)
    {
        return (ResourceHandler<FluidResource>) stack.getCapability((ItemCapability) Capabilities.Fluid.ITEM, access);
    }

    private static ResourceStack<FluidResource> tryMove(ResourceHandler<FluidResource> from, ResourceHandler<FluidResource> to)
    {
        return ResourceHandlerUtil.moveFirst(from, to, fr -> true, Integer.MAX_VALUE, null);
    }

    private static ComponentStateDefinition withContent(ComponentStateDefinition def, ContainerState.ContentType type, StacksResourceHandler<?, ?> handler)
    {
        return new ComponentStateDefinition(def.component(),
            def.inBlockPos(), def.rotationX(), def.rotationY(), def.rotationZ(),
            def.shape(), def.model(), def.occupation(),
            new ContainerContent(type, handler));
    }

    private static ComponentStateDefinition withContentAndChop(ComponentStateDefinition def, ContainerState.ContentType type, StacksResourceHandler<?, ?> handler, int chopLeft)
    {
        return new ComponentStateDefinition(def.component(), def.inBlockPos(), def.rotationX(), def.rotationY(), def.rotationZ(), def.shape(), def.model(), def.occupation(), new ContainerContent(type, handler, chopLeft));
    }

    protected static void popItem(Player p, ItemStack s)
    {
        if (!p.getInventory().add(s)) p.level().addFreshEntity(new ItemEntity(p.level(), p.getX(), p.getY(), p.getZ(), s));
    }

    protected ItemStack getDropItem() { return this.drops.getFirst().copy(); }

    public abstract ContainerState.ContainerType containerType();
    public abstract ContainerState.ContainerSize size();
    public abstract int maxBites();
    public abstract int containerStorage();
}
