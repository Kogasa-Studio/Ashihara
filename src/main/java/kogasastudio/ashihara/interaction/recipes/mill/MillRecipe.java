package kogasastudio.ashihara.interaction.recipes.mill;

import kogasastudio.ashihara.block.tileentities.MillTE;
import kogasastudio.ashihara.interaction.recipes.base.WrappedRecipe;
import kogasastudio.ashihara.interaction.recipes.register.RecipeSerializers;
import kogasastudio.ashihara.interaction.recipes.register.RecipeTypes;
import net.minecraft.core.HolderLookup;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.neoforged.neoforge.fluids.crafting.FluidIngredient;
import org.jetbrains.annotations.NotNull;

public class MillRecipe extends WrappedRecipe<MillRecipe> {

    @NotNull
    private final Ingredient millstone;

    @NotNull
    private final Ingredient inputItem;

    @NotNull
    private final FluidIngredient inputFluid;

    @NotNull
    private final ItemStack outputItem;

    @NotNull
    private final FluidIngredient outputFluid;

    private final float circle;
    private final int experience;

    public MillRecipe(@NotNull ResourceLocation id,
                      @NotNull Ingredient millstone,
                      @NotNull Ingredient inputItem,
                      @NotNull ItemStack outputItem) {
        this(id, millstone, inputItem, FluidIngredient.empty(), outputItem, FluidIngredient.empty(), 1, 100);
    }

    public MillRecipe(@NotNull ResourceLocation id,
                      @NotNull Ingredient millstone,
                      @NotNull Ingredient inputItem, @NotNull FluidIngredient inputFluid,
                      @NotNull ItemStack outputItem, @NotNull FluidIngredient outputFluid) {
        this(id, millstone, inputItem, inputFluid, outputItem, outputFluid, 1, 100);
    }

    public MillRecipe(@NotNull ResourceLocation id,
                      @NotNull Ingredient millstone,
                      @NotNull Ingredient inputItem, @NotNull FluidIngredient inputFluid,
                      @NotNull ItemStack outputItem, @NotNull FluidIngredient outputFluid,
                      float circle, int experience) {
        super(id);
        this.millstone = millstone;
        this.inputItem = inputItem;
        this.inputFluid = inputFluid;
        this.outputItem = outputItem;
        this.outputFluid = outputFluid;
        this.circle = circle;
        this.experience = experience;
    }

    public @NotNull Ingredient getMillstone() {
        return millstone;
    }

    public @NotNull Ingredient getInputItem() {
        return inputItem;
    }

    public @NotNull FluidIngredient getInputFluid() {
        return inputFluid;
    }

    public @NotNull ItemStack getOutputItem() {
        return outputItem;
    }

    public @NotNull FluidIngredient getOutputFluid() {
        return outputFluid;
    }

    @Override
    public @NotNull ItemStack getResultItem(@NotNull HolderLookup.Provider registries) {
        return outputItem.copy();
    }

    public float getCircle() {
        return circle;
    }

    public int getExperience() {
        return experience;
    }

    @Override
    public @NotNull RecipeSerializer<?> getSerializer() {
        return RecipeSerializers.MILL.get();
    }

    @Override
    public @NotNull RecipeType<?> getType() {
        return RecipeTypes.MILL.get();
    }

    // Todo: accepts inventory
    public boolean test(MillTE blockEntity) {
        return false;
    }
}
