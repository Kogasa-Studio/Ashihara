package kogasastudio.ashihara.interaction.recipes;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import kogasastudio.ashihara.block.blockentity.PotBlockEntity;
import kogasastudio.ashihara.interaction.recipes.base.BERecipeInput;
import kogasastudio.ashihara.interaction.recipes.base.WrappedRecipe;
import kogasastudio.ashihara.inventory.BEItemStackHandler;
import kogasastudio.ashihara.registry.RecipeTypes;
import net.minecraft.core.NonNullList;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.neoforged.neoforge.common.crafting.SizedIngredient;
import net.neoforged.neoforge.fluids.FluidStack;

public class PotRecipe extends WrappedRecipe<PotRecipe, PotBlockEntity>
{
    private final NonNullList<SizedIngredient> input;
    private final ItemStack output;
    public final FluidStack fluidCost;
    public final FluidStack fluidProduction;
    //in ticks
    public final int cookTime;

    // --- Serialization (RecipeSerializer is now a record) ---
    public static final MapCodec<PotRecipe> MAP_CODEC = RecordCodecBuilder.mapCodec
    (
        mortarRecipeInstance ->
        mortarRecipeInstance.group
        (
            Identifier.CODEC.fieldOf("id").forGetter(PotRecipe::getId),
            NonNullList.codecOf(SizedIngredient.NESTED_CODEC).fieldOf("ingredients").forGetter(PotRecipe::getInput),
            ItemStack.CODEC.optionalFieldOf("output", ItemStack.EMPTY).forGetter(PotRecipe::getOutput),
            FluidStack.CODEC.optionalFieldOf("fluid_cost", FluidStack.EMPTY).forGetter(PotRecipe::getFluidCost),
            FluidStack.CODEC.optionalFieldOf("fluid_production", FluidStack.EMPTY).forGetter(PotRecipe::getFluidProduction),
            Codec.INT.fieldOf("cook_time").forGetter(PotRecipe::getCookTime)
        ).apply(mortarRecipeInstance, PotRecipe::new)
    );

    public static final StreamCodec<RegistryFriendlyByteBuf, PotRecipe> STREAM_CODEC = StreamCodec.of(PotRecipe::toNetwork, PotRecipe::fromNetwork);

    public static final RecipeSerializer<PotRecipe> SERIALIZER = new RecipeSerializer<>(MAP_CODEC, STREAM_CODEC);

    public NonNullList<SizedIngredient> getInput() {return input;}

    public ItemStack getOutput() {return output;}

    public FluidStack getFluidCost() {return fluidCost;}

    public FluidStack getFluidProduction() {return fluidProduction;}

    public int getCookTime() {return cookTime;}

    public PotRecipe(Identifier id, NonNullList<SizedIngredient> input, ItemStack output, FluidStack fluidCost, FluidStack fluidProduction, int cookTime)
    {
        super(id);
        this.input = input;
        this.output = output;
        this.fluidCost = fluidCost;
        this.fluidProduction = fluidProduction;
        this.cookTime = cookTime;
    }

    @Override
    public boolean testBE(PotBlockEntity be)
    {
        BEItemStackHandler<?> inv = be.inventory;
        int maxParallel = 64;
        for (SizedIngredient ingredient : input)
        {
            boolean anyMatch = false;
            for (ItemStack stack : inv.getAllContents())
            {
                if (ingredient.test(stack))
                {
                    anyMatch = true;
                    maxParallel = Math.min(maxParallel, stack.getCount() / ingredient.count());
                    break;
                }
            }
            if (!anyMatch)
            {
                return false;
            }
        }
        be.setParallel(maxParallel);
        return true;
    }

    @Override
    public RecipeSerializer<? extends Recipe<BERecipeInput>> getSerializer()
    {
        return SERIALIZER;
    }

    @Override
    public RecipeType<? extends Recipe<BERecipeInput>> getType()
    {
        return RecipeTypes.POT.get();
    }

    // --- Network codec helpers ---

    private static PotRecipe fromNetwork(RegistryFriendlyByteBuf buffer)
    {
        Identifier id = Identifier.STREAM_CODEC.decode(buffer);
        NonNullList<SizedIngredient> iListN = NonNullList.copyOf(SizedIngredient.STREAM_CODEC.apply(ByteBufCodecs.list()).decode(buffer));
        ItemStack oN = ItemStack.STREAM_CODEC.decode(buffer);
        FluidStack fCostN;
        if (buffer.readBoolean())
        {
            fCostN = FluidStack.STREAM_CODEC.decode(buffer);
        }
        else fCostN = FluidStack.EMPTY;
        FluidStack fProdN;
        if (buffer.readBoolean())
        {
            fProdN = FluidStack.STREAM_CODEC.decode(buffer);
        }
        else fProdN = FluidStack.EMPTY;
        int cookTimeN = buffer.readInt();
        return new PotRecipe(id, iListN, oN, fCostN, fProdN, cookTimeN);
    }

    private static void toNetwork(RegistryFriendlyByteBuf buffer, PotRecipe recipe)
    {
        Identifier.STREAM_CODEC.encode(buffer, recipe.getId());
        SizedIngredient.STREAM_CODEC.apply(ByteBufCodecs.list()).encode(buffer, recipe.getInput());
        ItemStack.STREAM_CODEC.encode(buffer, recipe.getOutput());
        if (!recipe.getFluidCost().isEmpty())
        {
            buffer.writeBoolean(true);
            FluidStack.STREAM_CODEC.encode(buffer, recipe.getFluidCost());
        }
        else buffer.writeBoolean(false);
        if (!recipe.getFluidProduction().isEmpty())
        {
            buffer.writeBoolean(true);
            FluidStack.STREAM_CODEC.encode(buffer, recipe.getFluidProduction());
        }
        else buffer.writeBoolean(false);
        buffer.writeInt(recipe.cookTime);
    }
}
