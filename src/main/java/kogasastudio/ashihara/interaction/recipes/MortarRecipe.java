package kogasastudio.ashihara.interaction.recipes;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import kogasastudio.ashihara.block.tileentities.MortarTE;
import kogasastudio.ashihara.helper.DataHelper;
import kogasastudio.ashihara.interaction.recipes.base.WrappedRecipe;
import kogasastudio.ashihara.registry.RecipeSerializers;
import kogasastudio.ashihara.registry.RecipeTypes;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.common.util.RecipeMatcher;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.fluids.capability.templates.FluidTank;
import org.apache.logging.log4j.LogManager;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.stream.Collectors;

public class MortarRecipe extends WrappedRecipe<MortarRecipe>
{
    public final NonNullList<Ingredient> input;
    public final NonNullList<ItemStack> output;
    public final FluidStack fluidCost;
    public int progress;
    public Queue<MortarTE.MortarToolType> sequence;

    public MortarRecipe(ResourceLocation idIn,
                        NonNullList<Ingredient> inputIn, NonNullList<ItemStack> outputIn,
                        FluidStack fluidCostIn,
                        int progressIn, Queue<MortarTE.MortarToolType> sequenceIn)
    {
        super(idIn);

        this.input = inputIn;
        this.output = outputIn;
        this.fluidCost = fluidCostIn;
        this.progress = progressIn;
        this.sequence = sequenceIn;
    }

    public boolean testInputFluid(@Nullable FluidTank tank)
    {
        return tank == null ? fluidCost == null :
                tank.drain(fluidCost.copy(), IFluidHandler.FluidAction.SIMULATE).getAmount() >= fluidCost.getAmount();
    }

    @Override
    public boolean matches(@NotNull NonNullList<ItemStack> inputs, @NotNull Level level)
    {
        if (this.input == null)
        {
            LogManager.getLogger().error("MortarRecipe.matches: input is null. id: " + getId());
            return false;
        }

        inputs = NonNullList.copyOf(inputs.stream().filter(i -> !i.isEmpty()).collect(Collectors.toList()));

        return RecipeMatcher.findMatches(inputs, this.input) != null;
    }

    @Override
    public RecipeType<?> getType()
    {
        return RecipeTypes.MORTAR.get();
    }

    @Override
    public boolean canCraftInDimensions(int width, int height)
    {
        return width * height >= this.input.size();
    }

    @Override
    public @NotNull ItemStack getResultItem(HolderLookup.@NotNull Provider registries)
    {
        return this.output.get(0).copy();
    }

    @Override
    public ResourceLocation getId()
    {
        return this.id;
    }

    @Override
    public NonNullList<Ingredient> getIngredients()
    {
        return this.input;
    }

    public NonNullList<ItemStack> getOutput()
    {
        return this.output;
    }

    public FluidStack getFluidCost()
    {
        return fluidCost == null ? FluidStack.EMPTY : fluidCost;
    }

    public int getProgress() {return progress;}

    public Queue<MortarTE.MortarToolType> getSequence() {return sequence;}

    @Override
    public RecipeSerializer<?> getSerializer()
    {
        return RecipeSerializers.MORTAR.get();
    }

    public static class MortarRecipeSerializer implements RecipeSerializer<MortarRecipe>
    {
        public static final MapCodec<MortarRecipe> MAP_CODEC = RecordCodecBuilder.mapCodec
        (
            mortarRecipeInstance ->
            mortarRecipeInstance.group
            (
                ResourceLocation.CODEC.fieldOf("id").forGetter(MortarRecipe::getId),
                NonNullList.codecOf(Ingredient.CODEC).fieldOf("ingredients").forGetter(MortarRecipe::getIngredients),
                NonNullList.codecOf(ItemStack.CODEC).fieldOf("output").forGetter(MortarRecipe::getOutput),
                FluidStack.CODEC.fieldOf("fluid").forGetter(MortarRecipe::getFluidCost),
                Codec.INT.fieldOf("progress").forGetter(MortarRecipe::getProgress),
                MortarTE.MortarToolType.QUEUE_CODEC.fieldOf("sequence").forGetter(MortarRecipe::getSequence)
            ).apply(mortarRecipeInstance, MortarRecipe::new)
        );
        public static final StreamCodec<RegistryFriendlyByteBuf, MortarRecipe> STREAM_CODEC = StreamCodec.of(MortarRecipeSerializer::toNetwork, MortarRecipeSerializer::fromNetwork);

        public static MortarRecipe fromNetwork(RegistryFriendlyByteBuf buffer)
        {
            ResourceLocation id = ResourceLocation.STREAM_CODEC.decode(buffer);
            NonNullList<Ingredient> iListN = NonNullList.copyOf(Ingredient.CONTENTS_STREAM_CODEC.apply(ByteBufCodecs.list()).decode(buffer));
            NonNullList<ItemStack> oListN = DataHelper.copyAndCast(ItemStack.LIST_STREAM_CODEC.decode(buffer));
            FluidStack fCostN = FluidStack.STREAM_CODEC.decode(buffer);
            int progressN = buffer.readInt();
            Queue<MortarTE.MortarToolType> sequenceN = new ConcurrentLinkedQueue<>(DataHelper.copyAndCast(MortarTE.MortarToolType.STREAM_CODEC.apply(ByteBufCodecs.list()).decode(buffer)));

            return new MortarRecipe(id, iListN, oListN, fCostN, progressN, sequenceN);
        }

        public static RegistryFriendlyByteBuf toNetwork(RegistryFriendlyByteBuf buffer, MortarRecipe recipe)
        {
            ResourceLocation.STREAM_CODEC.encode(buffer, recipe.getId());
            Ingredient.CONTENTS_STREAM_CODEC.apply(ByteBufCodecs.list()).encode(buffer, recipe.getIngredients());
            ItemStack.LIST_STREAM_CODEC.encode(buffer, recipe.getOutput());
            FluidStack.STREAM_CODEC.encode(buffer, recipe.getFluidCost());
            buffer.writeInt(recipe.progress);
            MortarTE.MortarToolType.STREAM_CODEC.apply(ByteBufCodecs.list()).encode(buffer, NonNullList.copyOf(recipe.getSequence()));
            return buffer;
        }

        @Override
        public MapCodec<MortarRecipe> codec()
        {
            return MAP_CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, MortarRecipe> streamCodec()
        {
            return STREAM_CODEC;
        }
    }
}
