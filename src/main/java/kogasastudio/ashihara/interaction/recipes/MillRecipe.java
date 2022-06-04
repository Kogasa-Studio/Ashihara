package kogasastudio.ashihara.interaction.recipes;

import com.google.gson.annotations.Expose;
import kogasastudio.ashihara.interaction.recipes.base.BaseRecipe;
import kogasastudio.ashihara.interaction.recipes.register.RecipeSerializers;
import kogasastudio.ashihara.interaction.recipes.register.RecipeTypes;
import net.minecraft.core.NonNullList;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraftforge.common.util.RecipeMatcher;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import org.apache.logging.log4j.LogManager;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;

public class MillRecipe extends BaseRecipe {
    @Expose
    private final NonNullList<Ingredient> input;
    @Expose
    private final Map<Ingredient, Byte> inputCosts;
    @Expose
    private final NonNullList<ItemStack> output;

    @Expose
    private final FluidStack inputFluid;
    @Expose
    private final FluidStack outputFluid;

    @Expose
    public byte round;
    @Expose
    public int roundTicks;
    @Expose
    public float exp;

    public MillRecipe
            (
                    ResourceLocation recipeId,
                    String groupId,
                    NonNullList<Ingredient> inputIn,
                    Map<Ingredient, Byte> inputCostsIn,
                    NonNullList<ItemStack> outputIn,
                    FluidStack inFluid,
                    FluidStack outFluid,
                    byte roundIn, int roundTicksIn, float expIn
            ) {
        this.id = recipeId;
        this.group = groupId;

        this.input = inputIn;
        this.inputCosts = inputCostsIn;
        this.output = outputIn;
        this.inputFluid = inFluid;
        this.outputFluid = outFluid;
        this.round = roundIn;
        this.roundTicks = roundTicksIn;
        this.exp = expIn;
    }

    public boolean testInputFluid(@Nullable FluidTank tank) {
        return tank == null ? inputFluid == null :
                tank.drain(inputFluid.copy(), IFluidHandler.FluidAction.SIMULATE).getAmount() >= inputFluid.getAmount();
    }

    public FluidStack getInputFluid() {
        return inputFluid;
    }

    public FluidStack getOutputFluid() {
        return this.outputFluid.copy();
    }

    public byte getCosts(Ingredient ingredient) {
        return this.inputCosts.get(ingredient);
    }

    @Override
    public NonNullList<Ingredient> getIngredients() {
        return this.input;
    }

    @Override
    public boolean matches(List<ItemStack> inputs) {
        if (inputs == null || this.input == null) {
            LogManager.getLogger().error("MortarRecipe.matches: input is null. id: " + getId());
        }

        return RecipeMatcher.findMatches(inputs, this.input) != null;
    }

    @Override
    public String getGroup() {
        return this.group;
    }

    @Override
    public ResourceLocation getId() {
        return this.id;
    }

    //这个也没啥用
    @Override
    public ItemStack getResultItem() {
        return this.output.get(0).copy();
    }

    //出结果的
    public NonNullList<ItemStack> getCraftingResult() {
        return this.output;
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return width * height >= this.input.size();
    }

    @Override
    public RecipeType<?> getType() {
        return RecipeTypes.MILL.get();
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return RecipeSerializers.MILL.get();
    }
}
