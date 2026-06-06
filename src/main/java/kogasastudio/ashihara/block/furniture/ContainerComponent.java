package kogasastudio.ashihara.block.furniture;

import kogasastudio.ashihara.block.building.BaseMultiBuiltBlock;
import kogasastudio.ashihara.block.building.component.ComponentStateDefinition;
import kogasastudio.ashihara.block.building.component.Interactable;
import kogasastudio.ashihara.block.blockentity.MultiBuiltBlockEntity;
import kogasastudio.ashihara.registry.BuildingComponents;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.neoforged.neoforge.transfer.StacksResourceHandler;
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

    
    // ── ItemStack content (GUI inventory bundle behaviour) ──

    private static final String CONTENT_TAG = "bowl_food";

    public static ItemStack getContent(ItemStack container)
    {
        var cd = container.getOrDefault(net.minecraft.core.component.DataComponents.CUSTOM_DATA, net.minecraft.world.item.component.CustomData.EMPTY);
        if (!cd.contains(CONTENT_TAG)) return ItemStack.EMPTY;
        var tag = cd.copyTag().get(CONTENT_TAG);
        return tag != null ? ItemStack.CODEC.parse(net.minecraft.nbt.NbtOps.INSTANCE, tag).result().orElse(ItemStack.EMPTY) : ItemStack.EMPTY;
    }

    public static void setContent(ItemStack container, ItemStack food)
    {
        CustomData.update(DataComponents.CUSTOM_DATA, container, tag ->
        {
            if (food.isEmpty()) tag.remove(CONTENT_TAG);
            else tag.put(CONTENT_TAG, ItemStack.CODEC.encodeStart(net.minecraft.nbt.NbtOps.INSTANCE, food).getOrThrow());
        });
    }

    // ── Sound helpers ──

    protected static void playInsertSound(net.minecraft.world.entity.Entity entity)
    {
        entity.playSound(net.minecraft.sounds.SoundEvents.BUNDLE_INSERT, 0.8F, 0.8F + entity.level().getRandom().nextFloat() * 0.4F);
    }

    protected static void playRemoveOneSound(net.minecraft.world.entity.Entity entity)
    {
        entity.playSound(net.minecraft.sounds.SoundEvents.BUNDLE_REMOVE_ONE, 0.8F, 0.8F + entity.level().getRandom().nextFloat() * 0.4F);
    }

    protected abstract StacksResourceHandler<?, ?> createContentHandler();

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
        if (def.customData() instanceof ContainerContent cc
            && cc.handler() instanceof ItemStacksResourceHandler handler)
        {
            for (int i = 0; i < handler.size(); i++)
            {
                if (!handler.getResource(i).isEmpty()) result.add(handler.getResource(i).toStack((int) handler.getAmountAsLong(i)));
            }
        }
        return result;
    }

    // ── ICustomData ──

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
        StacksResourceHandler<?, ?> h = createContentHandler();
        h.deserialize(input.childOrEmpty("handler"));
        return new ContainerContent(type, h);
    }

    // ── Interactable ──

    @Override
    public ComponentStateDefinition handleInteraction(UseOnContext context,
        ComponentStateDefinition definition)
    {
        Player player = context.getPlayer();
        if (player == null) return definition;

        ContainerContent cc = contentOf(definition);
        ItemStacksResourceHandler handler = (ItemStacksResourceHandler) cc.handler();
        if (handler == null) return definition;

        ItemStack held = context.getItemInHand();

        if (held.isEmpty() && player.isShiftKeyDown())
        {
            BlockEntity be = context.getLevel().getBlockEntity(context.getClickedPos());
            if (be instanceof MultiBuiltBlockEntity mbe)
                for (ItemStack s : getDrops(definition, mbe)) popItem(player, s.copy());
            else popItem(player, this.drops.getFirst().copy());
            return null;
        }

        if (handler.getAmountAsLong(0) > 0)
        {
            ItemResource res = handler.getResource(0);
            int amount = (int) handler.getAmountAsLong(0);
            try (Transaction tx = Transaction.openRoot())
            {
                handler.extract(0, res, amount, tx);
                tx.commit();
                popItem(player, res.toStack(amount));
            }
            return new ComponentStateDefinition(definition.component(),
                definition.inBlockPos(), definition.rotationX(),
                definition.rotationY(), definition.rotationZ(),
                definition.shape(), definition.model(),
                definition.occupation(),
                new ContainerContent(ContainerState.ContentType.ITEM, handler));
        }

        if (!held.isEmpty() && held.has(DataComponents.FOOD))
        {
            ItemResource res = ItemResource.of(held.getItem(), DataComponentPatch.EMPTY);
            try (Transaction tx = Transaction.openRoot())
            {
                int inserted = handler.insert(0, res, 1, tx);
                if (inserted > 0) { tx.commit(); held.shrink(inserted); }
            }
            return new ComponentStateDefinition(definition.component(),
                definition.inBlockPos(), definition.rotationX(),
                definition.rotationY(), definition.rotationZ(),
                definition.shape(), definition.model(),
                definition.occupation(),
                new ContainerContent(ContainerState.ContentType.ITEM, handler));
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