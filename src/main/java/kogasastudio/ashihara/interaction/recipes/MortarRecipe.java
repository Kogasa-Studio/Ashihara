package kogasastudio.ashihara.interaction.recipes;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
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
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;

public class MortarRecipe extends BaseRecipe {
    @Expose
    private final NonNullList<Ingredient> input;
    @Expose
    private final NonNullList<ItemStack> output;

    @Expose
    @SerializedName("fluid")
    private final FluidStack fluidCost;

    @Expose
    public int progress;
    @Expose
    public byte recipeType;
    @Expose
    public byte[] sequence;

    public MortarRecipe(ResourceLocation idIn, String groupId,
                        NonNullList<Ingredient> inputIn, NonNullList<ItemStack> outputIn,
                        FluidStack fluidCostIn,
                        int progressIn, byte recipeTypeIn, byte[] sequenceIn) {
        this.id = idIn;
        this.group = groupId;

        this.input = inputIn;
        this.output = outputIn;
        this.fluidCost = fluidCostIn;
        this.progress = progressIn;
        this.recipeType = recipeTypeIn;
        this.sequence = sequenceIn;
    }

    public String getInfo() {
        return
                "\n{\ninput: " + this.input.toString()
                        + "\noutput: " + this.output.toString()
                        + "\nid: " + this.id.toString()
                        + "\nprogress: " + this.progress
                        + "\nrecipeType: " + this.recipeType
                        + "\nsequence: " + Arrays.toString(this.sequence)
                        + "\n}";
    }

    public boolean testInputFluid(@Nullable FluidTank tank) {
        return tank == null ? fluidCost == null :
                tank.drain(fluidCost.copy(), IFluidHandler.FluidAction.SIMULATE).getAmount() >= fluidCost.getAmount();
    }

    @Override
    public boolean matches(List<ItemStack> inputs) {
        return RecipeMatcher.findMatches(inputs, this.input) != null;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return RecipeSerializers.MORTAR.get();
    }

    @Override
    public RecipeType<?> getType() {
        return RecipeTypes.MORTAR.get();
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return width * height >= this.input.size();
    }

    @Override
    public ItemStack getResultItem() {
        return this.output.get(0).copy();
    }

    @Override
    public NonNullList<Ingredient> getIngredients() {
        return this.input;
    }

    public NonNullList<ItemStack> getOutput() {
        return this.output;
    }

    @Override
    public String getGroup() {
        return this.group;
    }

    @Override
    public ResourceLocation getId() {
        return this.id;
    }

    public FluidStack getFluidCost() {
        return fluidCost;
    }
}
