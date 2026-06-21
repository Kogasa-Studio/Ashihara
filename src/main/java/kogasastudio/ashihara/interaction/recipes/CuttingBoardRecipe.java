package kogasastudio.ashihara.interaction.recipes;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import kogasastudio.ashihara.block.blockentity.CuttingBoardBE;
import kogasastudio.ashihara.interaction.recipes.base.BERecipeInput;
import kogasastudio.ashihara.interaction.recipes.base.WrappedRecipe;
import kogasastudio.ashihara.registry.RecipeTypes;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class CuttingBoardRecipe extends WrappedRecipe<CuttingBoardRecipe, CuttingBoardBE>
{
    private final Ingredient ingredient;
    private final List<ItemStackTemplate> result;
    private final Ingredient tool;
    private final boolean consume;

    public CuttingBoardRecipe(Identifier idIn, Ingredient inputIn, List<ItemStackTemplate> outputIn, Ingredient toolIn, boolean consumeIn) {
        super(idIn);
        this.ingredient = inputIn;
        this.result = outputIn;
        this.tool = toolIn;
        this.consume = consumeIn;
    }

    @Override
    public @NotNull ItemStack assemble(@NotNull BERecipeInput input)
    {
        return this.result.isEmpty() ? ItemStack.EMPTY : this.result.getFirst().create();
    }

    @Override
    public boolean testBE(CuttingBoardBE be)
    {
        return false;
    }

    public Ingredient getInput() {
        return ingredient;
    }

    public Ingredient getTool() {
        return this.tool;
    }

    public boolean shouldConsume() {
        return this.consume;
    }

    public List<ItemStackTemplate> getOutputTemplates() {
        return this.result;
    }

    public List<ItemStack> getOutput() {
        return this.result.stream().map(ItemStackTemplate::create).toList();
    }

    @Override
    public RecipeType<? extends Recipe<BERecipeInput>> getType() {
        return RecipeTypes.CUTTING_BOARD.get();
    }

    // --- Serialization (RecipeSerializer is now a record) ---
    public static final MapCodec<CuttingBoardRecipe> MAP_CODEC = RecordCodecBuilder.mapCodec(instance ->
        instance.group(
            Identifier.CODEC.fieldOf("id").forGetter(CuttingBoardRecipe::getId),
            Ingredient.CODEC.fieldOf("ingredient").forGetter(CuttingBoardRecipe::getInput),
            ItemStackTemplate.CODEC.listOf().fieldOf("output").forGetter(CuttingBoardRecipe::getOutputTemplates),
            Ingredient.CODEC.fieldOf("tool").forGetter(CuttingBoardRecipe::getTool),
            Codec.BOOL.optionalFieldOf("consume", false).forGetter(CuttingBoardRecipe::shouldConsume)
        ).apply(instance, CuttingBoardRecipe::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, CuttingBoardRecipe> STREAM_CODEC =
        StreamCodec.of(CuttingBoardRecipe::toNetwork, CuttingBoardRecipe::fromNetwork);

    public static final RecipeSerializer<CuttingBoardRecipe> SERIALIZER = new RecipeSerializer<>(MAP_CODEC, STREAM_CODEC);

    @Override
    public RecipeSerializer<CuttingBoardRecipe> getSerializer() {
        return SERIALIZER;
    }

    private static CuttingBoardRecipe fromNetwork(RegistryFriendlyByteBuf buffer) {
        Identifier id = Identifier.STREAM_CODEC.decode(buffer);
        Ingredient ingredientN = Ingredient.CONTENTS_STREAM_CODEC.decode(buffer);
        Ingredient toolN = Ingredient.CONTENTS_STREAM_CODEC.decode(buffer);
        boolean consumeN = buffer.readBoolean();
        List<ItemStack> stacks = ItemStack.STREAM_CODEC.apply(ByteBufCodecs.list()).decode(buffer);
        List<ItemStackTemplate> outputN = new ArrayList<>(stacks.size());
        for (ItemStack s : stacks) outputN.add(ItemStackTemplate.fromNonEmptyStack(s));
        return new CuttingBoardRecipe(id, ingredientN, outputN, toolN, consumeN);
    }

    private static void toNetwork(RegistryFriendlyByteBuf buffer, CuttingBoardRecipe recipe) {
        Identifier.STREAM_CODEC.encode(buffer, recipe.id);
        Ingredient.CONTENTS_STREAM_CODEC.encode(buffer, recipe.ingredient);
        Ingredient.CONTENTS_STREAM_CODEC.encode(buffer, recipe.tool);
        buffer.writeBoolean(recipe.consume);
        ItemStack.STREAM_CODEC.apply(ByteBufCodecs.list()).encode(buffer, recipe.getOutput());
    }
}
