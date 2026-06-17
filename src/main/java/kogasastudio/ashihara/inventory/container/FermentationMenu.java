package kogasastudio.ashihara.inventory.container;

import kogasastudio.ashihara.block.FermentationBlock;
import kogasastudio.ashihara.block.blockentity.FermentationBlockEntity;
import kogasastudio.ashihara.registry.MenuTypes;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;

import java.util.Objects;

public class FermentationMenu extends AshiharaCommonContainer
{
    public final FermentationBlockEntity blockEntity;
    private final ContainerLevelAccess access;

    public FermentationMenu(int windowId, Inventory playerInventory, FriendlyByteBuf data)
    {
        this(windowId, playerInventory, getBlockEntity(playerInventory, data), new SimpleContainerData(2));
    }

    public FermentationMenu(int windowId, Inventory playerInventory, FermentationBlockEntity be, ContainerData cookData)
    {
        super(MenuTypes.FERMENTATION_MENU.get(), windowId);
        this.blockEntity = be;
        this.access = ContainerLevelAccess.create(be.getLevel(), be.getBlockPos());

        // 食材槽 0-3
        this.addSlotRange(be.inventory, 0, 0, 1000, be.inventory.size(), 0);

        // 玩家物品栏（背包 + 快捷栏）
        this.layoutPlayerInventorySlots(playerInventory, 8, 84);

        // 同步 cookTime / maxCookTime
        this.addDataSlots(cookData);
    }

    @Override
    public boolean stillValid(Player player)
    {
        return access.evaluate((level, pos) -> level.getBlockState(pos).getBlock() instanceof FermentationBlock && player.isWithinBlockInteractionRange(pos, 4.0), true);
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index)
    {
        ItemStack copy = ItemStack.EMPTY;
        var slot = this.slots.get(index);
        if (!slot.hasItem()) return copy;
        int invSize = this.blockEntity.inventory.size();

        ItemStack stack = slot.getItem();
        copy = stack.copy();

        if (index < invSize)
        {
            // BE 槽 → 玩家物品栏
            if (!this.moveItemStackTo(stack, invSize + 1, this.slots.size(), true)) return ItemStack.EMPTY;
        }
        else
        {
            // 玩家物品栏 → BE 槽
            if (!this.moveItemStackTo(stack, 0, invSize, false)) return ItemStack.EMPTY;
        }

        if (stack.isEmpty()) slot.set(ItemStack.EMPTY);
        else slot.setChanged();

        if (stack.getCount() == copy.getCount()) return ItemStack.EMPTY;
        slot.onTake(player, stack);
        return copy;
    }

    private static FermentationBlockEntity getBlockEntity(Inventory inv, FriendlyByteBuf data)
    {
        Objects.requireNonNull(inv, "playerInventory cannot be null");
        Objects.requireNonNull(data, "data cannot be null");
        BlockEntity be = inv.player.level().getBlockEntity(data.readBlockPos());
        if (be instanceof FermentationBlockEntity f) return f;
        throw new IllegalStateException("Expected PotBlockEntity but got: " + be);
    }
}
