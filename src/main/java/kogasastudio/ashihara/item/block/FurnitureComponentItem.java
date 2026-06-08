package kogasastudio.ashihara.item.block;

import kogasastudio.ashihara.block.building.BaseMultiBuiltBlock;
import kogasastudio.ashihara.block.blockentity.MultiBuiltBlockEntity;
import kogasastudio.ashihara.block.furniture.ContainerComponent;
import kogasastudio.ashihara.block.furniture.FurnitureComponent;
import kogasastudio.ashihara.block.furniture.SnappedUseOnContext;
import kogasastudio.ashihara.helper.BowlFoodHelper;
import kogasastudio.ashihara.helper.RenderHelper;
import kogasastudio.ashihara.registry.DataComponentTypes;
import kogasastudio.ashihara.utils.EatingModeHelper;
import kogasastudio.ashihara.utils.GridSnapHelper;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
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
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.component.Consumable;
import net.minecraft.world.level.Level;

import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.FluidType;
import net.neoforged.neoforge.transfer.access.ItemAccess;
import net.neoforged.neoforge.transfer.fluid.FluidResource;
import net.neoforged.neoforge.transfer.fluid.ItemAccessFluidHandler;
import net.neoforged.neoforge.transfer.item.ItemResource;

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

    public static ItemAccessFluidHandler getFluidHandler(ItemStack stack, ItemAccess access)
    {
        if (access == null || !(access.getResource().getItem() instanceof FurnitureComponentItem fci && fci.getComponent() instanceof ContainerComponent)) return null;

        return new ItemAccessFluidHandler(access, DataComponentTypes.FLUID_CONTENT.get(), 100)
        {
            @Override
            protected ItemResource update(ItemResource accessResource, int index, FluidResource newResource, int newAmount)
            {
                ItemResource result = super.update(accessResource, index, newResource, newAmount);
                return BowlFoodHelper.applyFluidToItemResource(result, newResource.toStack(Math.max(newAmount, FluidType.BUCKET_VOLUME)), newAmount);
            }
        };
    }

    @Override
    public InteractionResult use(Level level, Player player, InteractionHand hand)
    {
        ItemStack stack = player.getItemInHand(hand);
        Consumable consumable = stack.get(DataComponents.CONSUMABLE);
        boolean hasContent = consumable != null && consumable.canConsume(player, stack);
        if (hasContent && EatingModeHelper.isEnabled(player)) return consumable.startConsuming(player, stack, hand);
        return InteractionResult.PASS;
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
        ItemStack stack = pContext.getItemInHand();
        Player player = pContext.getPlayer();
        Consumable consumable = stack.get(DataComponents.CONSUMABLE);
        boolean hasContent = consumable != null && consumable.canConsume(player, stack);
        if (hasContent && EatingModeHelper.isEnabled(player)) return InteractionResult.PASS;

        BlockPlaceContext context = new SnappedUseOnContext(pContext, GridSnapHelper.getGridStep(player), false);
        InteractionResult b = super.place(pContext);
        BlockEntity blockEntity = pContext.getLevel().getBlockEntity(pContext.getClickedPos());
        if (blockEntity instanceof MultiBuiltBlockEntity be && be.tryPlaceFurniture(context, this.getComponent()))
        {
            if (b != InteractionResult.SUCCESS) pContext.getItemInHand().consume(1, player);
            b = InteractionResult.SUCCESS;
        }
        else
        {
            BlockEntity be2 = context.getLevel().getBlockEntity(context.getClickedPos().relative(context.getClickedFace().getOpposite()));
            if (be2 instanceof MultiBuiltBlockEntity be && be.tryPlaceFurniture(context, this.getComponent()))
            {
                if (b != InteractionResult.SUCCESS) pContext.getItemInHand().consume(1, player);
                b = InteractionResult.SUCCESS;
            }
        }
        return b;
    }

    // --------------------------------------------------
    // Bundle-like inventory behaviour
    // --------------------------------------------------

    @Override
    public boolean overrideStackedOnOther(ItemStack self, Slot slot, ClickAction clickAction, Player player)
    {
        if (self.getCount() != 1) return false;
        if (!(self.getItem() instanceof FurnitureComponentItem fci && fci.getComponent() instanceof ContainerComponent cc)) return false;
        if (!ContainerComponent.getFluidContent(self).isEmpty()) return false;

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
        if (!(self.getItem() instanceof FurnitureComponentItem fci && fci.getComponent() instanceof ContainerComponent cc)) return false;
        if (!ContainerComponent.getFluidContent(self).isEmpty()) return false;

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
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, TooltipDisplay display, Consumer<Component> builder, TooltipFlag flag)
    {
        if (!(stack.getItem() instanceof FurnitureComponentItem fci && fci.getComponent() instanceof ContainerComponent cc)) return;
        ItemStack food = ContainerComponent.getContent(stack);
        if (!food.isEmpty()) builder.accept(Component.translatable("tooltip.ashihara.bowl_content", food.getHoverName()));
        else
        {
            FluidStack fluidStack = ContainerComponent.getFluidContent(stack);
            if (!fluidStack.isEmpty()) builder.accept(Component.translatable("tooltip.ashihara.fluid_type").append(Component.empty().append(fluidStack.getHoverName()).append(" * ").append(String.valueOf(fluidStack.amount())).setStyle(Style.EMPTY.withColor(RenderHelper.getFluidTintColor(fluidStack)))));
        }
    }
}