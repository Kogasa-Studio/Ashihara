package kogasastudio.ashihara.utils;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tags.ITag;

import java.util.HashMap;
import java.util.Map;

public class CuttingBoardToolType
{
    public static final Map<String, CuttingBoardToolType> INSTANCES = new HashMap<>();

    public static final CuttingBoardToolType NONE = register(null, null);
    public static final CuttingBoardToolType AXE = register(AshiharaTags.AXE, "axe");
    public static final CuttingBoardToolType KNIFE = register(AshiharaTags.KNIFE, "knife");

    CuttingBoardToolType(ITag<Item> toolIn, String nameIn)
    {
        this.tool = toolIn;
        this.name = nameIn;
    }

    private final ITag<Item> tool;
    private final String name;

    public ITag<Item> getTool() {return this.tool;}

    public String getName() {return this.name;}

    public boolean toolMatches(ItemStack stack) {return stack.getItem().isIn(this.tool);}

    public static CuttingBoardToolType nameMatches(String nameIn) {return INSTANCES.get(nameIn) != null ? INSTANCES.get(nameIn) : NONE;}

    public boolean isEmpty() {return this.equals(NONE);}

    private static CuttingBoardToolType register(ITag<Item> tool, String name)
    {
        CuttingBoardToolType instance = new CuttingBoardToolType(tool, name);
        INSTANCES.put(name, instance);
        return instance;
    }
}