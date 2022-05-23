package kogasastudio.ashihara.utils;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.tags.Tag;

import java.util.HashMap;
import java.util.Map;

public class CuttingBoardToolType
{
    public static final Map<String, CuttingBoardToolType> INSTANCES = new HashMap<>();

    public static final CuttingBoardToolType NONE = register(null, null);
    public static final CuttingBoardToolType AXE = register(AshiharaTags.AXE, "axe");
    public static final CuttingBoardToolType KNIFE = register(AshiharaTags.KNIFE, "knife");

    CuttingBoardToolType(Tag<Item> toolIn, String nameIn)
    {
        this.tool = toolIn;
        this.name = nameIn;
    }

    private final Tag<Item> tool;
    private final String name;

    public Tag<Item> getTool() {return this.tool;}

    public String getName() {return this.name;}

    public boolean toolMatches(ItemStack stack) {return stack.getItem().is(this.tool);}

    public static CuttingBoardToolType nameMatches(String nameIn) {return INSTANCES.get(nameIn) != null ? INSTANCES.get(nameIn) : NONE;}

    public boolean isEmpty() {return this.equals(NONE);}

    private static CuttingBoardToolType register(Tag<Item> tool, String name)
    {
        CuttingBoardToolType instance = new CuttingBoardToolType(tool, name);
        INSTANCES.put(name, instance);
        return instance;
    }
}