package kogasastudio.ashihara.inventory;

import net.minecraft.world.inventory.Slot;

@FunctionalInterface
public interface SlotFactory
{
    Slot create(BEItemStackHandler<?> inventory, int index, int x, int y);
}
