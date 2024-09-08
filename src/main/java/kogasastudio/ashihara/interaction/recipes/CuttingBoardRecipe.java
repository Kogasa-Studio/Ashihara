package kogasastudio.ashihara.interaction.recipes;

import com.google.gson.annotations.Expose;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import kogasastudio.ashihara.helper.DataHelper;
import kogasastudio.ashihara.interaction.recipes.base.WrappedRecipe;
import kogasastudio.ashihara.interaction.recipes.register.RecipeSerializers;
import kogasastudio.ashihara.interaction.recipes.register.RecipeTypes;
import kogasastudio.ashihara.utils.CuttingBoardToolType;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

public class CuttingBoardRecipe extends WrappedRecipe<CuttingBoardRecipe> {
    @Expose
    private final Ingredient ingredient;
    @Expose
    private final NonNullList<ItemStack> result;
    @Expose
    private final CuttingBoardToolType tool;

    public CuttingBoardRecipe(ResourceLocation idIn, Ingredient inputIn, NonNullList<ItemStack> outputIn, String typeIn) {
        super(idIn);
        this.ingredient = inputIn;
        this.result = outputIn;
        this.tool = CuttingBoardToolType.nameMatches(typeIn);
    }

    public String getInfo() {
        return
                "\n{\n    input: " + Arrays.toString(this.ingredient.getItems())
                        + "\n    output: " + this.result.toString()
                        + "\n    id: " + this.id.toString()
                        + "\n    type: " + this.tool.getName()
                        + "\n}";
    }

    @Override
    public boolean matches(@NotNull NonNullList<ItemStack> inputs, @NotNull Level level) {
        return ingredient.test(inputs.getFirst());
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return width * height >= 1;
    }

    @Override
    public NonNullList<Ingredient> getIngredients()
    {
        return NonNullList.of(this.ingredient);
    }

    @Override
    public @NotNull ItemStack getResultItem(HolderLookup.@NotNull Provider pRegistries) {
        return this.result.getFirst();
    }

    public Ingredient getInput() {
        return ingredient;
    }

    public CuttingBoardToolType getTool() {
        return this.tool;
    }

    public NonNullList<ItemStack> getOutput() {
        return DataHelper.copyAndCast(this.result);
    }

    @Override
    public RecipeType<?> getType() {
        return RecipeTypes.CUTTING_BOARD.get();
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return RecipeSerializers.CUTTING_BOARD.get();
    }

    public static class CuttingBoardRecipeSerializer implements RecipeSerializer<CuttingBoardRecipe> {
        private static final MapCodec<CuttingBoardRecipe> CODEC = RecordCodecBuilder.mapCodec(instance ->
                instance.group(
                        ResourceLocation.CODEC.fieldOf("id").forGetter(CuttingBoardRecipe::getId),
                        Ingredient.CODEC_NONEMPTY.fieldOf("ingredient").forGetter(CuttingBoardRecipe::getInput),
                        NonNullList.codecOf(ItemStack.CODEC).fieldOf("output").forGetter(CuttingBoardRecipe::getOutput),
                        Codec.STRING.fieldOf("tool").forGetter(recipe -> recipe.getTool().getName())
                ).apply(instance, CuttingBoardRecipe::new));

        private static final StreamCodec<RegistryFriendlyByteBuf, CuttingBoardRecipe> STREAM_CODEC = StreamCodec.of(CuttingBoardRecipeSerializer::toNetwork, CuttingBoardRecipeSerializer::fromNetwork);

        public static CuttingBoardRecipe fromNetwork(RegistryFriendlyByteBuf buffer) {
            ResourceLocation id = ResourceLocation.STREAM_CODEC.decode(buffer);
            Ingredient ingredientN = Ingredient.CONTENTS_STREAM_CODEC.decode(buffer);
            String toolTypeN = buffer.readUtf();
            NonNullList<ItemStack> outputN = DataHelper.copyAndCast(ItemStack.LIST_STREAM_CODEC.decode(buffer));

            return new CuttingBoardRecipe(id, ingredientN, outputN, toolTypeN);
        }

        public static void toNetwork(RegistryFriendlyByteBuf buffer, CuttingBoardRecipe recipe) {
            ResourceLocation.STREAM_CODEC.encode(buffer, recipe.id);
            Ingredient.CONTENTS_STREAM_CODEC.encode(buffer, recipe.ingredient);
            buffer.writeUtf(recipe.tool.getName());
            ItemStack.LIST_STREAM_CODEC.encode(buffer, recipe.result);
        }

        @Override
        public MapCodec<CuttingBoardRecipe> codec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, CuttingBoardRecipe> streamCodec() {
            return STREAM_CODEC;
        }
    }
}
