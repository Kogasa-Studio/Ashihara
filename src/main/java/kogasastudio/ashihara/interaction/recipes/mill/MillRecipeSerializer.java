package kogasastudio.ashihara.interaction.recipes.mill;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.neoforged.neoforge.fluids.crafting.FluidIngredient;
import org.jetbrains.annotations.NotNull;

public class MillRecipeSerializer implements RecipeSerializer<MillRecipe> {

    public static final MapCodec<MillRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            ResourceLocation.CODEC.fieldOf("id").forGetter(MillRecipe::getId),
            Ingredient.CODEC.fieldOf("millstone").forGetter(MillRecipe::getMillstone),
            Ingredient.CODEC.fieldOf("inputItem").forGetter(MillRecipe::getInputItem),
            FluidIngredient.CODEC.optionalFieldOf("inputFluid", FluidIngredient.empty()).forGetter(MillRecipe::getInputFluid),
            ItemStack.CODEC.fieldOf("outputItem").forGetter(MillRecipe::getOutputItem),
            FluidIngredient.CODEC.optionalFieldOf("outputFluid", FluidIngredient.empty()).forGetter(MillRecipe::getOutputFluid),
            Codec.FLOAT.optionalFieldOf("circle", 1.0F).forGetter(MillRecipe::getCircle),
            Codec.INT.optionalFieldOf("experience", 100).forGetter(MillRecipe::getExperience)
    ).apply(instance, MillRecipe::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, MillRecipe> STREAM_CODEC = new StreamCodec<>() {
        @Override
        public @NotNull MillRecipe decode(@NotNull RegistryFriendlyByteBuf buf) {
            return new MillRecipe(ResourceLocation.STREAM_CODEC.decode(buf),
                    Ingredient.CONTENTS_STREAM_CODEC.decode(buf),
                    Ingredient.CONTENTS_STREAM_CODEC.decode(buf),
                    FluidIngredient.STREAM_CODEC.decode(buf),
                    ItemStack.STREAM_CODEC.decode(buf),
                    FluidIngredient.STREAM_CODEC.decode(buf),
                    ByteBufCodecs.FLOAT.decode(buf),
                    ByteBufCodecs.VAR_INT.decode(buf));
        }

        @Override
        public void encode(@NotNull RegistryFriendlyByteBuf buf, @NotNull MillRecipe recipe) {
            ResourceLocation.STREAM_CODEC.encode(buf, recipe.getId());
            Ingredient.CONTENTS_STREAM_CODEC.encode(buf, recipe.getMillstone());
            Ingredient.CONTENTS_STREAM_CODEC.encode(buf, recipe.getInputItem());
            FluidIngredient.STREAM_CODEC.encode(buf, recipe.getInputFluid());
            ItemStack.STREAM_CODEC.encode(buf, recipe.getOutputItem());
            FluidIngredient.STREAM_CODEC.encode(buf, recipe.getOutputFluid());
            ByteBufCodecs.FLOAT.encode(buf, recipe.getCircle());
            ByteBufCodecs.VAR_INT.encode(buf, recipe.getExperience());
        }
    };

    @Override
    public @NotNull MapCodec<MillRecipe> codec() {
        return CODEC;
    }

    @Override
    public @NotNull StreamCodec<RegistryFriendlyByteBuf, MillRecipe> streamCodec() {
        return STREAM_CODEC;
    }
}
