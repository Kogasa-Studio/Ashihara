package kogasastudio.ashihara.interaction.recipe;

import net.minecraft.inventory.Inventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;

import java.util.ArrayList;

import static kogasastudio.ashihara.Ashihara.LOGGER_MAIN;

public class MillRecipe
{
    private final Ingredient input;
    public ItemStack[] output = new ItemStack[4];

    public byte round;
    public int roundTicks;

    public MillRecipe(Ingredient inputN, ArrayList<ItemStack> outputN, byte roundN, int roundTicksN)
    {
        input = inputN;
        for (int i = 0; i < 4; i++)
        {
            output[i] = (i > outputN.size() - 1 ? ItemStack.EMPTY : outputN.get(i));
        }
        round = roundN;
        roundTicks = roundTicksN;
    }

    public ArrayList<ItemStack> getPeeledOutput()
    {
        ArrayList<ItemStack> list = new ArrayList<>();
        for (ItemStack stack : output)
        {
            if (!stack.isEmpty()) list.add(stack);
        }
        return list;
    }

    /**
     * 将一个任意格式的ItemStack集合解析为一个只能存在一种Item类型的规范集合
     * 原集合若有分散的物品, 会被集合到一个ItemStack里面, 物品的数量会加进去
     * @param stacks0 要解析的集合
     * @return 解析后的集合
     */
    public static ArrayList<ItemStack> analyze(ItemStack[] stacks0)
    {
        ArrayList<ItemStack> stacks = new ArrayList<>();
        ArrayList<Item> stackItems = new ArrayList<>();
        for (ItemStack stack : stacks0)
        {
            if (!stack.isEmpty())
            {
                if (!stackItems.contains(stack.getItem()))
                {
                    stacks.add(stack);
                    stackItems.add(stack.getItem());
                }
                else
                {
                    for (ItemStack stack1 : stacks)
                    {
                        if (stack1.getItem() == stack.getItem()) stack1.setCount(stack1.getCount() + stack.getCount());
                    }
                }
            }
        }
        return stacks;
    }

    public boolean tryApply(Inventory inv)
    {
        boolean b = true;
        for (int i = 0; i < inv.getSizeInventory(); i++)
        {
            if (!inv.getStackInSlot(i).isEmpty())
            {
                b = b && input.test(inv.getStackInSlot(i));
            }
        }
//        LOGGER_MAIN.info("1" + b);
        return b;
    }

    @Override
    public String toString()
    {
        StringBuilder produce = new StringBuilder();
        ArrayList<ItemStack> stacks1 = analyze(output);
        for (ItemStack stack : stacks1) {produce.append(stack.getItem().getName().getString()).append(": ").append(stack.getCount()).append(";");}
        return " 输出: " + produce.toString();
    }
}
