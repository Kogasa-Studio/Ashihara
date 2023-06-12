package kogasastudio.ashihara.helper;

import net.minecraft.core.NonNullList;
import net.minecraft.world.item.ItemStack;

/**
 * @author DustW
 **/
public class DataHelper
{
    public static NonNullList<ItemStack> copyFrom(NonNullList<ItemStack> list)
    {
        NonNullList<ItemStack> result = NonNullList.withSize(list.size(), ItemStack.EMPTY);

        for (int i = 0; i < list.size(); i++)
        {
            result.set(i, list.get(i).copy());
        }

        return result;
    }
}
