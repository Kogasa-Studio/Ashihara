package kogasastudio.ashihara.interaction.recipes;

import com.google.gson.annotations.Expose;
import kogasastudio.ashihara.interaction.recipes.base.BaseRecipe;
import kogasastudio.ashihara.interaction.recipes.register.RecipeSerializers;
import kogasastudio.ashihara.interaction.recipes.register.RecipeTypes;
import kogasastudio.ashihara.utils.CuttingBoardToolType;
import net.minecraft.core.NonNullList;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;

import java.util.Arrays;
import java.util.List;

public class CuttingBoardRecipe extends BaseRecipe {
    @Expose
    private final Ingredient input;
    @Expose
    private final NonNullList<ItemStack> output;
    @Expose
    private final CuttingBoardToolType tool;

    public CuttingBoardRecipe(ResourceLocation idIn, Ingredient inputIn, NonNullList<ItemStack> outputIn, CuttingBoardToolType typeIn) {
        this.id = idIn;
        this.input = inputIn;
        this.output = outputIn;
        this.tool = typeIn;
    }

    public String getInfo() {
        return
                "\n{\n    input: " + Arrays.toString(this.input.getItems())
                        + "\n    output: " + this.output.toString()
                        + "\n    id: " + this.id.toString()
                        + "\n    type: " + this.tool.getName()
                        + "\n}";
    }

    @Override
    public boolean matches(List<ItemStack> inputs) {
        return input.test(inputs.get(0));
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return width * height >= 1;
    }

    @Override
    public NonNullList<Ingredient> getIngredients() {
        return NonNullList.of(this.input);
    }

    @Override
    public ItemStack getResultItem() {
        return this.output.get(0);
    }

    public NonNullList<ItemStack> getOutput() {
        return this.output;
    }

    public CuttingBoardToolType getTool() {
        return this.tool;
    }

    @Override
    public ResourceLocation getId() {
        return this.id;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return RecipeSerializers.CUTTING_BOARD.get();
    }

    @Override
    public RecipeType<?> getType() {
        return RecipeTypes.CUTTING_BOARD.get();
    }
}