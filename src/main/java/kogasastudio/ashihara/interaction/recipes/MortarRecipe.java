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
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.PlacementInfo;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.neoforged.neoforge.common.crafting.SizedIngredient;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.FluidStackTemplate;
import net.neoforged.neoforge.transfer.fluid.FluidResource;
import net.neoforged.neoforge.transfer.transaction.Transaction;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

@SuppressWarnings("deprecation")
public class MortarRecipe extends WrappedRecipe<MortarRecipe, MortarBE>
{
    private final NonNullList<SizedIngredient> input;
    private final NonNullList<ItemStackTemplate> output;
    @Nullable
    public final FluidStackTemplate fluidCost;
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
            NonNullList.codecOf(ItemStackTemplate.CODEC).fieldOf("output").forGetter(r -> r.output),
            FluidStackTemplate.CODEC.optionalFieldOf("fluid").forGetter(r -> Optional.ofNullable(r.fluidCost)),
            Codec.STRING.xmap(s -> s.equals("consume") ? 0 : s.equals("produce") ? 1 : -1, i -> i == 0 ? "consume" : "produce").optionalFieldOf("fluid_action", 0).forGetter(MortarRecipe::getFluidOpcode),
            MortarBE.MortarToolType.QUEUE_CODEC.fieldOf("sequence").forGetter(MortarRecipe::getSequence)
        ).apply(mortarRecipeInstance, MortarRecipe::new)
    );

    public static final StreamCodec<RegistryFriendlyByteBuf, MortarRecipe> STREAM_CODEC = StreamCodec.of(
        MortarRecipe::toNetwork, MortarRecipe::fromNetwork
    );

    public static final RecipeSerializer<MortarRecipe> SERIALIZER = new RecipeSerializer<>(MAP_CODEC, STREAM_CODEC);

    public MortarRecipe(Identifier idIn,
                        NonNullList<SizedIngredient> inputIn,
                        NonNullList<ItemStackTemplate> outputIn,
                        Optional<FluidStackTemplate> fluidCostIn,
                        int fluidOpcodeIn,
                        Queue<MortarBE.MortarToolType> sequenceIn)
    {
        super(idIn);
        this.input = inputIn;
        this.output = outputIn;
        this.fluidCost = fluidCostIn.orElse(null);
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
        if (fluidCost == null || multiplier == 0) return multiplier;
        if (tank == null) return 0;
        FluidStack fluidStack = fluidCost.create();
        int totalAmount = fluidStack.getAmount() * multiplier;
        FluidResource resource = FluidResource.of(fluidStack);
        try (Transaction tx = Transaction.openRoot())
        {
            int moved;
            if (fluidOpcode == 0)
            {
                moved = tank.extract(resource, totalAmount, tx);
            }
            else
            {
                if (fluidOpcode != 1)
                    Ashihara.LOGGER_MAIN.warn("MortarRecipe fluid action defined incorrectly. Please define field 'fluid_action' to 'consume' or 'produce'. Related recipe: {}.", getId());
                moved = tank.insert(resource, totalAmount, tx);
            }
            if (!simulate) tx.commit();
            return fluidStack.getAmount() > 0 ? moved / fluidStack.getAmount() : multiplier;
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
        return this.output.isEmpty() ? ItemStack.EMPTY : this.output.getFirst().create();
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
        NonNullList<ItemStack> out = NonNullList.create();
        for (ItemStackTemplate template : this.output)
        {
            out.add(template.create());
        }
        return out;
    }

    public FluidStack getFluidCost()
    {
        return fluidCost == null ? FluidStack.EMPTY : fluidCost.create();
    }

    public int getFluidOpcode() {return fluidOpcode;}

    public Queue<MortarBE.MortarToolType> getSequence() {return sequence;}

    @Override
    public PlacementInfo placementInfo()
    {
        List<Ingredient> ingredients = this.input.stream()
            .map(SizedIngredient::ingredient)
            .filter(i -> !i.isEmpty())
            .toList();
        return ingredients.isEmpty() ? PlacementInfo.NOT_PLACEABLE : PlacementInfo.create(ingredients);
    }

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
        List<ItemStack> stacks = ItemStack.STREAM_CODEC.apply(ByteBufCodecs.list()).decode(buffer);
        NonNullList<ItemStackTemplate> oListN = NonNullList.create();
        for (ItemStack stack : stacks)
        {
            oListN.add(ItemStackTemplate.fromNonEmptyStack(stack));
        }
        Optional<FluidStackTemplate> fCostN = Optional.empty();
        if (buffer.readBoolean())
        {
            fCostN = Optional.of(FluidStackTemplate.fromNonEmptyStack(FluidStack.STREAM_CODEC.decode(buffer)));
        }
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
