package kogasastudio.ashihara.interaction.recipes;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import kogasastudio.ashihara.block.blockentity.PotBlockEntity;
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
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.neoforged.neoforge.common.crafting.SizedIngredient;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.FluidStackTemplate;
import net.neoforged.neoforge.transfer.fluid.FluidResource;
import net.neoforged.neoforge.transfer.transaction.Transaction;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class PotRecipe extends WrappedRecipe<PotRecipe, PotBlockEntity>
{
    private final NonNullList<SizedIngredient> input;
    @Nullable
    private final ItemStackTemplate output;
    @Nullable
    public final FluidStackTemplate fluidCost;
    @Nullable
    public final FluidStackTemplate fluidProduction;
    //in ticks
    public final int cookTime;
    public final int priority;

    // --- Serialization ---
    public static final MapCodec<PotRecipe> MAP_CODEC = RecordCodecBuilder.mapCodec
    (
        instance ->
        instance.group
        (
            Identifier.CODEC.fieldOf("id").forGetter(PotRecipe::getId),
            NonNullList.codecOf(SizedIngredient.NESTED_CODEC).fieldOf("ingredients").forGetter(PotRecipe::getInput),
            ItemStackTemplate.CODEC.optionalFieldOf("output").forGetter(r -> Optional.ofNullable(r.output)),
            FluidStackTemplate.CODEC.optionalFieldOf("fluid_cost").forGetter(r -> Optional.ofNullable(r.fluidCost)),
            FluidStackTemplate.CODEC.optionalFieldOf("fluid_production").forGetter(r -> Optional.ofNullable(r.fluidProduction)),
            Codec.INT.fieldOf("cook_time").forGetter(PotRecipe::getCookTime),
            Codec.INT.optionalFieldOf("priority", 0).forGetter(PotRecipe::getPriority)
        ).apply(instance, PotRecipe::new)
    );

    public static final StreamCodec<RegistryFriendlyByteBuf, PotRecipe> STREAM_CODEC = StreamCodec.of(PotRecipe::toNetwork, PotRecipe::fromNetwork);

    public static final RecipeSerializer<PotRecipe> SERIALIZER = new RecipeSerializer<>(MAP_CODEC, STREAM_CODEC);

    public NonNullList<SizedIngredient> getInput() {return input;}

    public ItemStack getOutput()
    {
        return output == null ? ItemStack.EMPTY : this.output.create();
    }

    public FluidStack getFluidCost()
    {
        return fluidCost == null ? FluidStack.EMPTY : fluidCost.create();
    }

    public FluidStack getFluidProduction()
    {
        return fluidProduction == null ? FluidStack.EMPTY : fluidProduction.create();
    }

    public int getCookTime() {return cookTime;}

    public int getPriority() {return priority;}

    public PotRecipe(Identifier id,
                     NonNullList<SizedIngredient> input,
                     Optional<ItemStackTemplate> output,
                     Optional<FluidStackTemplate> fluidCost,
                     Optional<FluidStackTemplate> fluidProduction,
                     int cookTime,
                     int priority)
    {
        super(id);
        this.input = input;
        this.output = output.orElse(null);
        this.fluidCost = fluidCost.orElse(null);
        this.fluidProduction = fluidProduction.orElse(null);
        this.cookTime = cookTime;
        this.priority = priority;
    }

    // ── Fluid option helpers (仿 MortarRecipe) ────────────────────────────────

    public int testFluidCost(@Nullable BEFluidStackHandler<?> tank, int multiplier, boolean simulate)
    {
        if (fluidCost == null || multiplier == 0) return multiplier;
        if (tank == null) return 0;
        FluidStack stack = fluidCost.create();
        int totalAmount = stack.getAmount() * multiplier;
        FluidResource resource = FluidResource.of(stack);
        try (Transaction tx = Transaction.openRoot())
        {
            int moved = tank.extract(resource, totalAmount, tx);
            if (!simulate) tx.commit();
            return stack.getAmount() > 0 ? moved / stack.getAmount() : multiplier;
        }
    }

    public int testFluidProduction(@Nullable BEFluidStackHandler<?> tank, int multiplier, boolean simulate)
    {
        if (fluidProduction == null || multiplier == 0) return multiplier;
        if (tank == null) return 0;
        FluidStack stack = fluidProduction.create();
        int totalAmount = stack.getAmount() * multiplier;
        FluidResource resource = FluidResource.of(stack);
        try (Transaction tx = Transaction.openRoot())
        {
            int moved = tank.insert(resource, totalAmount, tx);
            if (!simulate) tx.commit();
            return stack.getAmount() > 0 ? moved / stack.getAmount() : multiplier;
        }
    }

    @Override
    public boolean testBE(PotBlockEntity be)
    {
        BEItemStackHandler<?> inv = be.inventory;
        int multiplier = inv.testIngredients(this.getInput(), be.getMaxParallel(), true);
        if (multiplier < 1) return false;
        be.setParallel(multiplier);
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
        Optional<ItemStackTemplate> optStack = Optional.empty();
        if (buffer.readBoolean())
        {
            optStack = Optional.of(ItemStackTemplate.fromNonEmptyStack(ItemStack.STREAM_CODEC.decode(buffer)));
        }
        Optional<FluidStackTemplate> fCostN = Optional.empty();
        if (buffer.readBoolean())
        {
            fCostN = Optional.of(FluidStackTemplate.fromNonEmptyStack(FluidStack.STREAM_CODEC.decode(buffer)));
        }
        Optional<FluidStackTemplate> fProdN = Optional.empty();
        if (buffer.readBoolean())
        {
            fProdN = Optional.of(FluidStackTemplate.fromNonEmptyStack(FluidStack.STREAM_CODEC.decode(buffer)));
        }
        int cookTimeN = buffer.readInt();
        int priorityN = buffer.readInt();
        return new PotRecipe(id, iListN, optStack, fCostN, fProdN, cookTimeN, priorityN);
    }

    private static void toNetwork(RegistryFriendlyByteBuf buffer, PotRecipe recipe)
    {
        Identifier.STREAM_CODEC.encode(buffer, recipe.getId());
        SizedIngredient.STREAM_CODEC.apply(ByteBufCodecs.list()).encode(buffer, recipe.getInput());
        if (recipe.output != null)
        {
            buffer.writeBoolean(true);
            ItemStack.STREAM_CODEC.encode(buffer, recipe.getOutput());
        }
        else buffer.writeBoolean(false);
        if (recipe.fluidCost != null)
        {
            buffer.writeBoolean(true);
            FluidStack.STREAM_CODEC.encode(buffer, recipe.getFluidCost());
        }
        else buffer.writeBoolean(false);
        if (recipe.fluidProduction != null)
        {
            buffer.writeBoolean(true);
            FluidStack.STREAM_CODEC.encode(buffer, recipe.getFluidProduction());
        }
        else buffer.writeBoolean(false);
        buffer.writeInt(recipe.cookTime);
        buffer.writeInt(recipe.priority);
    }
}
