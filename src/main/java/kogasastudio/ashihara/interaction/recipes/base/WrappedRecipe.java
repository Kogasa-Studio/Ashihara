package kogasastudio.ashihara.interaction.recipes.base;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.items.wrapper.RecipeWrapper;
import org.jetbrains.annotations.NotNull;

/**
 * @author DustW
 **/
public abstract class WrappedRecipe<T extends WrappedRecipe<?>> implements Recipe<RecipeWrapper> {
    protected final ResourceLocation id;

    public WrappedRecipe(ResourceLocation id) {
        this.id = id;
    }

    public boolean matches(@NotNull NonNullList<ItemStack> inputs, @NotNull Level level) {
        return false;
    }

    @Override
    public boolean matches(@NotNull RecipeWrapper wrapper, @NotNull Level level) {
        var inputs = NonNullList.<ItemStack>create();
        for (var i = 0; i < wrapper.size(); i++) {
            inputs.add(wrapper.getItem(i));
        }
        return matches(inputs, level);
    }

    @Override
    public @NotNull ItemStack assemble(@NotNull RecipeWrapper wrapper, @NotNull HolderLookup.Provider registries) {
        return ItemStack.EMPTY;
    }

    @Override
    public @NotNull ItemStack getResultItem(@NotNull HolderLookup.Provider registries) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return false;
    }

    public ResourceLocation getId() {
        return id;
    }

    @Override
    public @NotNull String getGroup() {
        return Recipe.super.getGroup();
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof WrappedRecipe<?> recipe && id.equals(recipe.id);
    }
}
