package kogasastudio.ashihara.interaction.recipes;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import kogasastudio.ashihara.Ashihara;
import kogasastudio.ashihara.block.tileentities.MortarTE;
import kogasastudio.ashihara.helper.DataHelper;
import kogasastudio.ashihara.interaction.recipes.base.WrappedRecipe;
import kogasastudio.ashihara.inventory.BEItemStackHandler;
import kogasastudio.ashihara.registry.RecipeSerializers;
import kogasastudio.ashihara.registry.RecipeTypes;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.neoforged.neoforge.common.crafting.SizedIngredient;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.fluids.capability.templates.FluidTank;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class MortarRecipe extends WrappedRecipe<MortarRecipe, MortarTE>
{
    public final NonNullList<SizedIngredient> input;
    public final NonNullList<ItemStack> output;
    public final FluidStack fluidCost;
    //0: consume; other: produce
    public int fluidOpcode;
    public Queue<MortarTE.MortarToolType> sequence;

    public MortarRecipe(ResourceLocation idIn,
                        NonNullList<SizedIngredient> inputIn, NonNullList<ItemStack> outputIn,
                        FluidStack fluidCostIn,
                        int fluidOpcodeIn, Queue<MortarTE.MortarToolType> sequenceIn)
    {
        super(idIn);

        this.input = inputIn;
        this.output = outputIn;
        this.fluidCost = fluidCostIn;
        this.fluidOpcode = fluidOpcodeIn;
        this.sequence = sequenceIn;
    }

    public boolean testFluidOption(@Nullable FluidTank tank)
    {
        if (fluidCost == null || fluidCost.isEmpty()) return true;
        if (tank == null) return false;
        if (fluidOpcode == 0) return tank.drain(fluidCost.copy(), IFluidHandler.FluidAction.SIMULATE).getAmount() >= fluidCost.getAmount();
        else
        {
            if (fluidOpcode != 1) Ashihara.LOGGER_MAIN.warn("MortarRecipe fluid action defined incorrectly. Please define field 'fluid_action' to 'consume' or 'produce'. Related recipe: {}.", getId());
            return tank.fill(fluidCost.copy(), IFluidHandler.FluidAction.SIMULATE) >= fluidCost.getAmount();
        }
    }

    @Override
    public boolean testBE(MortarTE be)
    {
        BEItemStackHandler<?> inv = be.inventory;
        int multiplier = inv.testIngredients(this.getSizedIngredients(), be.getMaxParallel());
        if (multiplier == 0) return false;
        if (!testFluidOption(be.fluidTank)) return false;
        be.setMultiplier(multiplier);
        return true;
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

    public NonNullList<SizedIngredient> getSizedIngredients()
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

    public int getFluidOpcode() {return fluidOpcode;}

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
                NonNullList.codecOf(SizedIngredient.FLAT_CODEC).fieldOf("ingredients").forGetter(MortarRecipe::getSizedIngredients),
                NonNullList.codecOf(ItemStack.CODEC).fieldOf("output").forGetter(MortarRecipe::getOutput),
                FluidStack.CODEC.fieldOf("fluid").forGetter(MortarRecipe::getFluidCost),
                Codec.STRING.xmap(s -> s.equals("consume") ? 0 : s.equals("produce") ? 1 : -1, i -> i == 0 ? "consume" : "produce").fieldOf("fluid_action").forGetter(MortarRecipe::getFluidOpcode),
                MortarTE.MortarToolType.QUEUE_CODEC.fieldOf("sequence").forGetter(MortarRecipe::getSequence)
            ).apply(mortarRecipeInstance, MortarRecipe::new)
        );
        public static final StreamCodec<RegistryFriendlyByteBuf, MortarRecipe> STREAM_CODEC = StreamCodec.of(MortarRecipeSerializer::toNetwork, MortarRecipeSerializer::fromNetwork);

        public static MortarRecipe fromNetwork(RegistryFriendlyByteBuf buffer)
        {
            ResourceLocation id = ResourceLocation.STREAM_CODEC.decode(buffer);
            NonNullList<SizedIngredient> iListN = NonNullList.copyOf(SizedIngredient.STREAM_CODEC.apply(ByteBufCodecs.list()).decode(buffer));
            NonNullList<ItemStack> oListN = DataHelper.copyAndCast(ItemStack.LIST_STREAM_CODEC.decode(buffer));
            FluidStack fCostN = FluidStack.STREAM_CODEC.decode(buffer);
            int fluidOpcodeN = buffer.readInt();
            Queue<MortarTE.MortarToolType> sequenceN = new ConcurrentLinkedQueue<>(DataHelper.copyAndCast(MortarTE.MortarToolType.STREAM_CODEC.apply(ByteBufCodecs.list()).decode(buffer)));

            return new MortarRecipe(id, iListN, oListN, fCostN, fluidOpcodeN, sequenceN);
        }

        public static RegistryFriendlyByteBuf toNetwork(RegistryFriendlyByteBuf buffer, MortarRecipe recipe)
        {
            ResourceLocation.STREAM_CODEC.encode(buffer, recipe.getId());
            SizedIngredient.STREAM_CODEC.apply(ByteBufCodecs.list()).encode(buffer, recipe.getSizedIngredients());
            ItemStack.LIST_STREAM_CODEC.encode(buffer, recipe.getOutput());
            FluidStack.STREAM_CODEC.encode(buffer, recipe.getFluidCost());
            buffer.writeInt(recipe.fluidOpcode);
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
