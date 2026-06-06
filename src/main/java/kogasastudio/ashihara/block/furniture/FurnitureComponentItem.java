package kogasastudio.ashihara.block.furniture;

import kogasastudio.ashihara.block.building.BaseMultiBuiltBlock;
import kogasastudio.ashihara.block.blockentity.MultiBuiltBlockEntity;
import kogasastudio.ashihara.utils.GridSnapHelper;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickAction;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.inventory.tooltip.BundleTooltip;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.BundleContents;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.transfer.access.ItemAccess;
import net.neoforged.neoforge.transfer.fluid.FluidResource;
import net.neoforged.neoforge.transfer.fluid.FluidStacksResourceHandler;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class FurnitureComponentItem extends BlockItem
{
    private final Supplier<? extends FurnitureComponent> component;

    public FurnitureComponentItem(Supplier<? extends FurnitureComponent> componentIn, Supplier<BaseMultiBuiltBlock> block)
    {
        this(componentIn, block, new Properties());
    }

    public FurnitureComponentItem(Supplier<? extends FurnitureComponent> componentIn, Supplier<BaseMultiBuiltBlock> block, Properties properties)
    {
        super(block.get(), properties);
        this.component = componentIn;
    }

    public FurnitureComponent getComponent() { return this.component.get(); }

    public static FluidStacksResourceHandler getFluidHandler(ItemStack stack, ItemAccess access)
    {
        if (!(stack.getItem() instanceof FurnitureComponentItem fci && fci.getComponent() instanceof ContainerComponent)) return null;

        FluidStacksResourceHandler handler = new FluidStacksResourceHandler(1, 100)
        {
            @Override
            protected void onContentsChanged(int index, FluidStack previous)
            {
                ContainerComponent.setFluidContent(stack, getAmountAsLong(0) > 0 ? getResource(0).toStack((int) getAmountAsLong(0)) : FluidStack.EMPTY);
            }
        };

        FluidStack stored = ContainerComponent.getFluidContent(stack);
        if (!stored.isEmpty())
        {
            handler.set(0, FluidResource.of(stored.getFluid()), stored.getAmount());
        }
        return handler;
    }

    @Override
    protected boolean canPlace(BlockPlaceContext pContext, BlockState pState)
    {
        if (pContext.getLevel().getBlockState(pContext.getClickedPos()).is(this.getBlock()))
        {
            BlockEntity blockEntity = pContext.getLevel().getBlockEntity(pContext.getClickedPos());
            if (blockEntity instanceof MultiBuiltBlockEntity be && be.tryPlaceFurniture(pContext, this.getComponent()))
                return false;
        }
        return super.canPlace(pContext, pState);
    }

    @Override
    public InteractionResult place(BlockPlaceContext pContext)
    {
        Player player = pContext.getPlayer();
        BlockPlaceContext context = player != null
            ? new SnappedUseOnContext(pContext, GridSnapHelper.getGridStep(player)) : pContext;
        InteractionResult b = super.place(pContext);
        BlockEntity blockEntity = pContext.getLevel().getBlockEntity(pContext.getClickedPos());
        if (blockEntity instanceof MultiBuiltBlockEntity be && be.tryPlaceFurniture(context, this.getComponent()))
        {
            b = InteractionResult.SUCCESS;
        }
        else
        {
            BlockEntity be2 = context.getLevel().getBlockEntity(
                context.getClickedPos().relative(context.getClickedFace().getOpposite()));
            if (be2 instanceof MultiBuiltBlockEntity be && be.tryPlaceFurniture(context, this.getComponent()))
                b = InteractionResult.SUCCESS;
        }
        return b;
    }

    // ── Bundle-like inventory behaviour ──

    @Override
    public boolean overrideStackedOnOther(ItemStack self, Slot slot, ClickAction clickAction, Player player)
    {
        if (self.getCount() != 1) return false;
        if (!(self.getItem() instanceof FurnitureComponentItem fci
            && fci.getComponent() instanceof ContainerComponent cc)) return false;

        if (clickAction == ClickAction.SECONDARY && slot.hasItem()
            && slot.getItem().has(DataComponents.FOOD))
        {
            ItemStack food = slot.getItem();
            ItemStack current = ContainerComponent.getContent(self);
            if (current.isEmpty())
            {
                ContainerComponent.setContent(self, food.copyWithCount(1));
                ContainerComponent.playInsertSound(player);
                slot.safeTake(1, 1, player);
                return true;
            }
        }
        if (clickAction == ClickAction.SECONDARY && !slot.hasItem())
        {
            ItemStack food = ContainerComponent.getContent(self);
            if (!food.isEmpty())
            {
                ContainerComponent.setContent(self, ItemStack.EMPTY);
                ContainerComponent.playRemoveOneSound(player);
                slot.safeInsert(food);
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean overrideOtherStackedOnMe(ItemStack self, ItemStack other, Slot slot,
        ClickAction clickAction, Player player, SlotAccess carriedItem)
    {
        if (self.getCount() != 1) return false;
        if (!(self.getItem() instanceof FurnitureComponentItem fci
            && fci.getComponent() instanceof ContainerComponent cc)) return false;

        if (clickAction == ClickAction.PRIMARY && !other.isEmpty()
            && other.has(DataComponents.FOOD))
        {
            ItemStack current = ContainerComponent.getContent(self);
            if (current.isEmpty())
            {
                ContainerComponent.setContent(self, other.copyWithCount(1));
                ContainerComponent.playInsertSound(player);
                other.shrink(1);
                return true;
            }
        }
        return false;
    }

    @Override
    public Optional<TooltipComponent> getTooltipImage(ItemStack stack)
    {
        if (!(stack.getItem() instanceof FurnitureComponentItem fci
            && fci.getComponent() instanceof ContainerComponent cc))
            return Optional.empty();
        ItemStack food = ContainerComponent.getContent(stack);
        if (food.isEmpty()) return Optional.empty();
        BundleContents.Mutable mut = new BundleContents.Mutable(BundleContents.EMPTY);
        mut.tryInsert(food);
        return Optional.of(new BundleTooltip(mut.toImmutable()));
    }

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context,
        TooltipDisplay display, Consumer<Component> builder, TooltipFlag flag)
    {
        if (!(stack.getItem() instanceof FurnitureComponentItem fci
            && fci.getComponent() instanceof ContainerComponent cc)) return;
        ItemStack food = ContainerComponent.getContent(stack);
        if (!food.isEmpty())
            builder.accept(Component.translatable("tooltip.ashihara.bowl_content",
                food.getHoverName()));
    }
}