package kogasastudio.ashihara.interaction.recipes;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import kogasastudio.ashihara.Ashihara;
import kogasastudio.ashihara.block.blockentity.MortarBE;
import kogasastudio.ashihara.helper.DataHelper;
import kogasastudio.ashihara.interaction.recipes.base.BERecipeInput;
import kogasastudio.ashihara.interaction.recipes.base.WrappedRecipe;
import kogasastudio.ashihara.inventory.BEFluidStackHandler;
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
import net.neoforged.neoforge.transfer.fluid.FluidResource;
import net.neoforged.neoforge.transfer.transaction.Transaction;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

@SuppressWarnings("deprecation")
public class MortarRecipe extends WrappedRecipe<MortarRecipe, MortarBE>
{
    private final NonNullList<SizedIngredient> input;
    private final NonNullList<ItemStack> output;
    public final FluidStack fluidCost;
    //0: consume; other: produce
    public int fluidOpcode;
    public Queue<MortarBE.MortarToolType> sequence;

    // --- Serialization (RecipeSerializer is now a record) ---
    public static final MapCodec<MortarRecipe> MAP_CODEC = RecordCodecBuilder.mapCodec
    (
        mortarRecipeInstance ->
        mortarRecipeInstance.group
        (
            Identifier.CODEC.fieldOf("id").forGetter(MortarRecipe::getId),
            NonNullList.codecOf(SizedIngredient.NESTED_CODEC).fieldOf("ingredients").forGetter(MortarRecipe::getSizedIngredients),
            NonNullList.codecOf(ItemStack.CODEC).fieldOf("output").forGetter(MortarRecipe::getOutput),
            FluidStack.CODEC.optionalFieldOf("fluid", FluidStack.EMPTY).forGetter(MortarRecipe::getFluidCost),
            Codec.STRING.xmap(s -> s.equals("consume") ? 0 : s.equals("produce") ? 1 : -1, i -> i == 0 ? "consume" : "produce").optionalFieldOf("fluid_action", 0).forGetter(MortarRecipe::getFluidOpcode),
            MortarBE.MortarToolType.QUEUE_CODEC.fieldOf("sequence").forGetter(MortarRecipe::getSequence)
        ).apply(mortarRecipeInstance, MortarRecipe::new)
    );

    public static final StreamCodec<RegistryFriendlyByteBuf, MortarRecipe> STREAM_CODEC = StreamCodec.of(
        MortarRecipe::toNetwork, MortarRecipe::fromNetwork
    );

    public static final RecipeSerializer<MortarRecipe> SERIALIZER = new RecipeSerializer<>(MAP_CODEC, STREAM_CODEC);

    public MortarRecipe(Identifier idIn,
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

    /**
     * Tests whether the fluid option of this recipe can be satisfied by {@code tank},
     * optionally committing the change.
     *
     * <p>Note: the return value is the number of <em>full recipe batches</em> that can be
     * satisfied from ({@code fluidCost.amount * return} == total fluid consumed/produced).
     *
     * @param tank       the fluid tank to test against (may be {@code null})
     * @param multiplier the maximum number of parallel batches to attempt
     * @param simulate   if {@code true} the transaction is rolled back
     * @return the number of batches satisfiable, clamped to [0, multiplier]
     */
    public int testFluidOption(@Nullable BEFluidStackHandler<?> tank, int multiplier, boolean simulate)
    {
        if (fluidCost == null || fluidCost.isEmpty() || multiplier == 0) return multiplier;
        if (tank == null) return 0;
        int totalAmount = fluidCost.getAmount() * multiplier;
        FluidResource resource = FluidResource.of(fluidCost);
        try (Transaction tx = Transaction.openRoot())
        {
            int moved;
            if (fluidOpcode == 0)
            {
                // Consume fluid from tank
                moved = tank.extract(resource, totalAmount, tx);
            }
            else
            {
                if (fluidOpcode != 1)
                    Ashihara.LOGGER_MAIN.warn("MortarRecipe fluid action defined incorrectly. Please define field 'fluid_action' to 'consume' or 'produce'. Related recipe: {}.", getId());
                // Produce fluid into tank
                moved = tank.insert(resource, totalAmount, tx);
            }
            if (!simulate) tx.commit();
            // Return how many full batches were satisfied
            return fluidCost.getAmount() > 0 ? moved / fluidCost.getAmount() : multiplier;
        }
    }

    @Override
    public boolean testBE(MortarBE be)
    {
        BEItemStackHandler<?> inv = be.inventory;
        int multiplier = inv.testIngredients(this.getSizedIngredients(), be.getMaxParallel(), true);
        multiplier = testFluidOption(be.fluidTank, multiplier, true);
        if (multiplier < 1) return false;
        be.setMultiplier(multiplier);
        return true;
    }

    @Override
    public RecipeType<? extends Recipe<BERecipeInput>> getType()
    {
        return RecipeTypes.MORTAR.get();
    }

    @Override
    public @NotNull ItemStack assemble(@NotNull BERecipeInput input)
    {
        return this.output.isEmpty() ? ItemStack.EMPTY : this.output.getFirst().copy();
    }

    public Identifier getId()
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
    public RecipeSerializer<MortarRecipe> getSerializer()
    {
        return SERIALIZER;
    }

    // --- Network codec helpers ---

    private static MortarRecipe fromNetwork(RegistryFriendlyByteBuf buffer)
    {
        Identifier id = Identifier.STREAM_CODEC.decode(buffer);
        NonNullList<SizedIngredient> iListN = NonNullList.copyOf(SizedIngredient.STREAM_CODEC.apply(ByteBufCodecs.list()).decode(buffer));
        NonNullList<ItemStack> oListN = DataHelper.copyAndCast(ItemStack.STREAM_CODEC.apply(ByteBufCodecs.list()).decode(buffer));
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

    private static void toNetwork(RegistryFriendlyByteBuf buffer, MortarRecipe recipe)
    {
        Identifier.STREAM_CODEC.encode(buffer, recipe.getId());
        SizedIngredient.STREAM_CODEC.apply(ByteBufCodecs.list()).encode(buffer, recipe.getSizedIngredients());
        ItemStack.STREAM_CODEC.apply(ByteBufCodecs.list()).encode(buffer, recipe.getOutput());
        if (!recipe.getFluidCost().isEmpty())
        {
            buffer.writeBoolean(true);
            FluidStack.STREAM_CODEC.encode(buffer, recipe.getFluidCost());
        }
        else buffer.writeBoolean(false);
        buffer.writeInt(recipe.fluidOpcode);
        MortarBE.MortarToolType.STREAM_CODEC.apply(ByteBufCodecs.list()).encode(buffer, NonNullList.copyOf(recipe.getSequence()));
    }
}
