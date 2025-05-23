package kogasastudio.ashihara.interaction.recipes;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import kogasastudio.ashihara.Ashihara;
import kogasastudio.ashihara.block.blockentity.MortarBE;
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

public class MortarRecipe extends WrappedRecipe<MortarRecipe, MortarBE>
{
    private final NonNullList<SizedIngredient> input;
    private final NonNullList<ItemStack> output;
    public final FluidStack fluidCost;
    //0: consume; other: produce
    public int fluidOpcode;
    public Queue<MortarBE.MortarToolType> sequence;

    public MortarRecipe(ResourceLocation idIn,
                        NonNullList<SizedIngredient> inputIn, NonNullList<ItemStack> outputIn,
                        FluidStack fluidCostIn,
                        int fluidOpcodeIn, Queue<MortarBE.MortarToolType> sequenceIn)
    {
        super(idIn);

        this.input = inputIn;
        this.output = outputIn;
        this.fluidCost = fluidCostIn;
        this.fluidOpcode = fluidOpcodeIn;
        this.sequence = sequenceIn;
    }

    public int testFluidOption(@Nullable FluidTank tank, int multiplier, IFluidHandler.FluidAction action)
    {
        if (fluidCost == null || fluidCost.isEmpty() || multiplier == 0) return multiplier;
        if (tank == null) return 0;
        FluidStack fluid = fluidCost.copyWithAmount(fluidCost.getAmount() * multiplier);
        if (fluidOpcode == 0) return tank.drain(fluid, action).getAmount() / fluid.getAmount();
        else
        {
            if (fluidOpcode != 1) Ashihara.LOGGER_MAIN.warn("MortarRecipe fluid action defined incorrectly. Please define field 'fluid_action' to 'consume' or 'produce'. Related recipe: {}.", getId());
            return tank.fill(fluid, action) / fluid.getAmount();
        }
    }

    @Override
    public boolean testBE(MortarBE be)
    {
        BEItemStackHandler<?> inv = be.inventory;
        int multiplier = inv.testIngredients(this.getSizedIngredients(), be.getMaxParallel(), true);
        multiplier = testFluidOption(be.fluidTank, multiplier, IFluidHandler.FluidAction.SIMULATE);
        if (multiplier < 1) return false;
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
        return NonNullList.copyOf(this.input);
    }

    public NonNullList<ItemStack> getOutput()
    {
        NonNullList<ItemStack> output = NonNullList.create();
        for (ItemStack stack : this.output)
        {
            output.add(stack.copy());
        }
        return output;
    }

    public FluidStack getFluidCost()
    {
        return fluidCost == null ? FluidStack.EMPTY : fluidCost;
    }

    public int getFluidOpcode() {return fluidOpcode;}

    public Queue<MortarBE.MortarToolType> getSequence() {return sequence;}

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
                FluidStack.CODEC.optionalFieldOf("fluid", FluidStack.EMPTY).forGetter(MortarRecipe::getFluidCost),
                Codec.STRING.xmap(s -> s.equals("consume") ? 0 : s.equals("produce") ? 1 : -1, i -> i == 0 ? "consume" : "produce").optionalFieldOf("fluid_action", 0).forGetter(MortarRecipe::getFluidOpcode),
                MortarBE.MortarToolType.QUEUE_CODEC.fieldOf("sequence").forGetter(MortarRecipe::getSequence)
            ).apply(mortarRecipeInstance, MortarRecipe::new)
        );
        public static final StreamCodec<RegistryFriendlyByteBuf, MortarRecipe> STREAM_CODEC = StreamCodec.of(MortarRecipeSerializer::toNetwork, MortarRecipeSerializer::fromNetwork);

        public static MortarRecipe fromNetwork(RegistryFriendlyByteBuf buffer)
        {
            ResourceLocation id = ResourceLocation.STREAM_CODEC.decode(buffer);
            NonNullList<SizedIngredient> iListN = NonNullList.copyOf(SizedIngredient.STREAM_CODEC.apply(ByteBufCodecs.list()).decode(buffer));
            NonNullList<ItemStack> oListN = DataHelper.copyAndCast(ItemStack.LIST_STREAM_CODEC.decode(buffer));
            FluidStack fCostN;
            if (buffer.readBoolean())
            {
                fCostN = FluidStack.STREAM_CODEC.decode(buffer);
            }
            else fCostN = FluidStack.EMPTY;
            int fluidOpcodeN = buffer.readInt();
            Queue<MortarBE.MortarToolType> sequenceN = new ConcurrentLinkedQueue<>(DataHelper.copyAndCast(MortarBE.MortarToolType.STREAM_CODEC.apply(ByteBufCodecs.list()).decode(buffer)));

            return new MortarRecipe(id, iListN, oListN, fCostN, fluidOpcodeN, sequenceN);
        }

        public static RegistryFriendlyByteBuf toNetwork(RegistryFriendlyByteBuf buffer, MortarRecipe recipe)
        {
            ResourceLocation.STREAM_CODEC.encode(buffer, recipe.getId());
            SizedIngredient.STREAM_CODEC.apply(ByteBufCodecs.list()).encode(buffer, recipe.getSizedIngredients());
            ItemStack.LIST_STREAM_CODEC.encode(buffer, recipe.getOutput());
            if (!recipe.getFluidCost().isEmpty())
            {
                buffer.writeBoolean(true);
                FluidStack.STREAM_CODEC.encode(buffer, recipe.getFluidCost());
            }
            else buffer.writeBoolean(false);
            buffer.writeInt(recipe.fluidOpcode);
            MortarBE.MortarToolType.STREAM_CODEC.apply(ByteBufCodecs.list()).encode(buffer, NonNullList.copyOf(recipe.getSequence()));
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
