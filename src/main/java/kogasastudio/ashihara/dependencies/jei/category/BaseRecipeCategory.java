package kogasastudio.ashihara.dependencies.jei.category;

import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.crafting.Recipe;

/**
 * @author DustW
 **/
public abstract class BaseRecipeCategory<T extends Recipe<?>> implements IRecipeCategory<T>
{
    protected final RecipeType<T> type;
    protected String translateKey;
    IDrawable icon;
    IDrawable background;

    public BaseRecipeCategory(RecipeType<T> type, IDrawable icon, IDrawable background)
    {
        this.type = type;
        this.icon = icon;
        this.background = background;
    }

    protected String defaultTranslateKey()
    {
        return type.getUid().toString().replace("/", ".");
    }

    @Override
    public Component getTitle()
    {
        return Component.translatable(translateKey == null ? translateKey = defaultTranslateKey() : translateKey);
    }

    @Override
    public IDrawable getBackground()
    {
        return background;
    }

    @Override
    public IDrawable getIcon()
    {
        return icon;
    }

    @Override
    public RecipeType<T> getRecipeType()
    {
        return type;
    }
}
