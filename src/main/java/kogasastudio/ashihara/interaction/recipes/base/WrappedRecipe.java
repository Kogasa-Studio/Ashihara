package kogasastudio.ashihara.interaction.recipes.base;

import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.PlacementInfo;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeBookCategory;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.NotNull;

/**
 * @author DustW
 **/
public abstract class WrappedRecipe<T extends WrappedRecipe<?, ?>, B extends BlockEntity> implements Recipe<BERecipeInput> {
    protected final Identifier id;

    public WrappedRecipe(Identifier id) {
        this.id = id;
    }

    public abstract boolean testBE(B be);

    @Override
    public boolean matches(@NotNull BERecipeInput input, @NotNull net.minecraft.world.level.Level level) {
        return false;
    }

    @Override
    public @NotNull ItemStack assemble(@NotNull BERecipeInput input) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean showNotification() {
        return false;
    }

    @Override
    public @NotNull String group() {
        return "";
    }

    @Override
    public PlacementInfo placementInfo() {
        return PlacementInfo.NOT_PLACEABLE;
    }

    @Override
    public RecipeBookCategory recipeBookCategory() {
        return null;
    }

    public Identifier getId() {
        return id;
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof WrappedRecipe<?, ?> recipe && id.equals(recipe.id);
    }
}
