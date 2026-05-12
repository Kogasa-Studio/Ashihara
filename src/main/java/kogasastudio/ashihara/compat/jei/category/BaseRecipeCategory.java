package kogasastudio.ashihara.compat.jei.category;

import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.recipe.category.IRecipeCategory;
import mezz.jei.api.recipe.types.IRecipeType;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.crafting.Recipe;

public abstract class BaseRecipeCategory<T extends Recipe<?>> implements IRecipeCategory<T>
{
    protected final IRecipeType<T> type;
    protected String translateKey;
    IDrawable icon;
    IDrawable background;

    public BaseRecipeCategory(IRecipeType<T> type, IDrawable icon, IDrawable background)
    {
        this.type = type;
        this.icon = icon;
        this.background = background;
    }

    protected String defaultTranslateKey()
    {
        return type.toString().replace("/", ".");
    }

    @Override
    public Component getTitle()
    {
        return Component.translatable(translateKey == null ? translateKey = defaultTranslateKey() : translateKey);
    }

    @Override
    public IDrawable getIcon()
    {
        return icon;
    }

    @Override
    public IRecipeType<T> getRecipeType()
    {
        return type;
    }
}
