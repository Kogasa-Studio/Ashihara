package kogasastudio.ashihara.inventory.container;

import kogasastudio.ashihara.block.blockentity.PotBlockEntity;
import kogasastudio.ashihara.registry.Blocks;
import kogasastudio.ashihara.registry.MenuTypes;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;

import java.util.Objects;

/**
 * 土锅容器 Menu。
 *
 * <p>槽位布局：
 * <ul>
 *   <li>0-3：{@link PotBlockEntity} 的 4 个食材槽</li>
 *   <li>4-39：玩家物品栏（36 格）</li>
 * </ul>
 *
 * <p>ContainerData（索引）：
 * <ul>
 *   <li>0：cookTime</li>
 *   <li>1：maxCookTime</li>
 * </ul>
 */
public class PotMenu extends AshiharaCommonContainer
{
    /** BE 食材槽数量。 */
    public static final int INGREDIENT_SLOTS = 4;
    /** 玩家槽位起始索引。 */
    public static final int PLAYER_SLOT_START = INGREDIENT_SLOTS;

    public final PotBlockEntity blockEntity;

    private final ContainerLevelAccess access;
    private final ContainerData cookData;

    // ── 客户端构造器（由 MenuType 工厂调用，通过网络包解析 BE 位置）────────────────
    public PotMenu(int windowId, Inventory playerInventory, FriendlyByteBuf data)
    {
        this(windowId, playerInventory, getBlockEntity(playerInventory, data), new SimpleContainerData(2));
    }

    // ── 服务端构造器（由 PotBlockEntity.createMenu() 直接调用）────────────────────
    public PotMenu(int windowId, Inventory playerInventory, PotBlockEntity be, ContainerData cookData)
    {
        super(MenuTypes.POT_MENU.get(), windowId);
        this.blockEntity = be;
        this.access = ContainerLevelAccess.create(be.getLevel(), be.getBlockPos());
        this.cookData = cookData;

        // 食材槽 0-3
        this.addSlotRange(be.inventory, 0, 0, 0, INGREDIENT_SLOTS, 0);

        // 玩家物品栏（背包 + 快捷栏）
        this.layoutPlayerInventorySlots(playerInventory, 8, 84);

        // 同步 cookTime / maxCookTime
        this.addDataSlots(cookData);
    }

    // ── 辅助方法 ────────────────────────────────────────────────────────────────

    private static PotBlockEntity getBlockEntity(Inventory inv, FriendlyByteBuf data)
    {
        Objects.requireNonNull(inv, "playerInventory cannot be null");
        Objects.requireNonNull(data, "data cannot be null");
        BlockEntity be = inv.player.level().getBlockEntity(data.readBlockPos());
        if (be instanceof PotBlockEntity pot) return pot;
        throw new IllegalStateException("Expected PotBlockEntity but got: " + be);
    }

    // ── 进度查询（供 PotScreen3D 读取） ─────────────────────────────────────────────

    public int getCookTime()    { return this.cookData.get(0); }
    public int getMaxCookTime() { return this.cookData.get(1); }

    public float getCookProgress()
    {
        int time = getCookTime();
        int max  = getMaxCookTime();
        return (max > 0 && time > 0) ? (float) time / max : 0f;
    }

    // ── 原版接口实现 ──────────────────────────────────────────────────────────────

    @Override
    public boolean stillValid(Player player)
    {
        return stillValid(this.access, player, Blocks.POT.get());
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index)
    {
        ItemStack copy = ItemStack.EMPTY;
        var slot = this.slots.get(index);
        if (slot == null || !slot.hasItem()) return copy;

        ItemStack stack = slot.getItem();
        copy = stack.copy();

        if (index < INGREDIENT_SLOTS)
        {
            // BE 槽 → 玩家物品栏
            if (!this.moveItemStackTo(stack, PLAYER_SLOT_START, this.slots.size(), true))
                return ItemStack.EMPTY;
        }
        else
        {
            // 玩家物品栏 → BE 槽
            if (!this.moveItemStackTo(stack, 0, INGREDIENT_SLOTS, false))
                return ItemStack.EMPTY;
        }

        if (stack.isEmpty()) slot.set(ItemStack.EMPTY);
        else slot.setChanged();

        if (stack.getCount() == copy.getCount()) return ItemStack.EMPTY;
        slot.onTake(player, stack);
        return copy;
    }
}

