package kogasastudio.ashihara.interaction.recipes;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import kogasastudio.ashihara.block.blockentity.FermentationBlockEntity;
import kogasastudio.ashihara.interaction.recipes.base.BERecipeInput;
import kogasastudio.ashihara.interaction.recipes.base.WrappedRecipe;
import kogasastudio.ashihara.inventory.BEFluidStackHandler;
import kogasastudio.ashihara.registry.RecipeTypes;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class FermentationRecipe extends WrappedRecipe<FermentationRecipe, FermentationBlockEntity>
{
    private final List<SizedIngredient> inputItems;
    private final List<FluidStackTemplate> inputFluids;
    private final int amountTolerance;
    private final int priority;
    @Nullable private final Boolean needAir;
    private final List<String> needSize;
    @Nullable private final Integer minFluidAmount;
    @Nullable private final Integer maxFluidAmount;
    private final List<ItemStackTemplate> output;
    @Nullable private final FluidStackTemplate outputFluid;
    private final int time;

    // --- Serialization ---

    public static final MapCodec<FermentationRecipe> MAP_CODEC = RecordCodecBuilder.mapCodec
    (
        instance -> instance.group
        (
            Identifier.CODEC.fieldOf("id").forGetter(FermentationRecipe::getId),
            SizedIngredient.NESTED_CODEC.listOf().optionalFieldOf("input_items", List.of()).forGetter(FermentationRecipe::getInputItems),
            FluidStackTemplate.CODEC.listOf().optionalFieldOf("input_fluids", List.of()).forGetter(FermentationRecipe::getInputFluids),
            Codec.INT.optionalFieldOf("amount_tolerance", 0).forGetter(FermentationRecipe::getAmountTolerance),
            Codec.INT.optionalFieldOf("priority", 0).forGetter(FermentationRecipe::getPriority),
            Codec.BOOL.optionalFieldOf("need_air").forGetter(r -> Optional.ofNullable(r.needAir)),
            Codec.STRING.listOf().optionalFieldOf("need_size", List.of()).forGetter(FermentationRecipe::getNeedSize),
            Codec.INT.optionalFieldOf("min_fluid_amount").forGetter(r -> Optional.ofNullable(r.minFluidAmount)),
            Codec.INT.optionalFieldOf("max_fluid_amount").forGetter(r -> Optional.ofNullable(r.maxFluidAmount)),
            ItemStackTemplate.CODEC.listOf().optionalFieldOf("output", List.of()).forGetter(FermentationRecipe::getOutputTemplates),
            FluidStackTemplate.CODEC.optionalFieldOf("output_fluid").forGetter(r -> Optional.ofNullable(r.outputFluid)),
            Codec.INT.fieldOf("time").forGetter(FermentationRecipe::getTime)
        ).apply(instance, FermentationRecipe::new)
    );

    public static final StreamCodec<RegistryFriendlyByteBuf, FermentationRecipe> STREAM_CODEC =
        StreamCodec.of(FermentationRecipe::toNetwork, FermentationRecipe::fromNetwork);

    public static final RecipeSerializer<FermentationRecipe> SERIALIZER = new RecipeSerializer<>(MAP_CODEC, STREAM_CODEC);

    // --- Constructor ---

    public FermentationRecipe(Identifier id,
                              List<SizedIngredient> inputItems,
                              List<FluidStackTemplate> inputFluids,
                              int amountTolerance,
                              int priority,
                              Optional<Boolean> needAir,
                              List<String> needSize,
                              Optional<Integer> minFluidAmount,
                              Optional<Integer> maxFluidAmount,
                              List<ItemStackTemplate> output,
                              Optional<FluidStackTemplate> outputFluid,
                              int time)
    {
        super(id);
        this.inputItems = inputItems;
        this.inputFluids = inputFluids;
        this.amountTolerance = amountTolerance;
        this.priority = priority;
        this.needAir = needAir.orElse(null);
        this.needSize = needSize;
        this.minFluidAmount = minFluidAmount.orElse(null);
        this.maxFluidAmount = maxFluidAmount.orElse(null);
        this.output = output;
        this.outputFluid = outputFluid.orElse(null);
        this.time = time;
    }

    // --- Accessors ---

    public List<SizedIngredient> getInputItems()        { return inputItems; }
    public List<FluidStackTemplate> getInputFluids()    { return inputFluids; }
    public int getAmountTolerance()                     { return amountTolerance; }
    public int getPriority()                            { return priority; }
    @Nullable public Boolean getNeedAir()               { return needAir; }
    public List<String> getNeedSize()                   { return needSize; }
    @Nullable public Integer getMinFluidAmount()        { return minFluidAmount; }
    @Nullable public Integer getMaxFluidAmount()        { return maxFluidAmount; }
    public List<ItemStackTemplate> getOutputTemplates() { return output; }
    public int getTime()                                { return time; }

    public List<ItemStack> getOutputStacks()
    {
        return output.stream().map(ItemStackTemplate::create).toList();
    }

    public FluidStack getOutputFluid()
    {
        return outputFluid == null ? FluidStack.EMPTY : outputFluid.create();
    }

    // --- Input detection ---

    public boolean hasItemInput() { return !inputItems.isEmpty(); }

    // --- Fluid helpers ---

    public int getTotalConsumeAmount()
    {
        int total = 0;
        for (FluidStackTemplate fst : inputFluids) total += fst.create().getAmount();
        return total;
    }

    public int computeFluidProduction(int actualFluidAmount, int parallel)
    {
        if (outputFluid == null || inputFluids.isEmpty()) return 0;
        int consumeAmount = getTotalConsumeAmount();
        int productAmount = outputFluid.create().getAmount();
        if (consumeAmount <= 0) return productAmount * parallel;
        int bonusAmount = actualFluidAmount - consumeAmount * parallel;
        return productAmount * parallel + bonusAmount * productAmount / consumeAmount;
    }

    public void consumeFluidInputs(@Nullable BEFluidStackHandler<?> tank, int multiplier)
    {
        if (inputFluids.isEmpty() || tank == null || multiplier == 0) return;
        for (FluidStackTemplate fst : inputFluids)
        {
            FluidStack required = fst.create();
            int amount = required.getAmount() * multiplier;
            try (Transaction tx = Transaction.openRoot())
            {
                tank.extract(FluidResource.of(required), amount, tx);
                tx.commit();
            }
        }
    }

    // --- BE matching ---

    @Override
    public boolean testBE(FermentationBlockEntity be)
    {
        int multiplier = be.getMaxParallel();
        if (!inputItems.isEmpty())
        {
            multiplier = be.inventory.testIngredients(inputItems, multiplier, true);
            if (multiplier < 1) return false;
        }
        be.setParallel(multiplier);
        return true;
    }

    @Override
    public RecipeSerializer<? extends Recipe<BERecipeInput>> getSerializer() { return SERIALIZER; }

    @Override
    public RecipeType<? extends Recipe<BERecipeInput>> getType() { return RecipeTypes.FERMENTATION.get(); }

    // --- Network codec helpers ---

    private static FermentationRecipe fromNetwork(RegistryFriendlyByteBuf buf)
    {
        Identifier id = Identifier.STREAM_CODEC.decode(buf);
        List<SizedIngredient> items = SizedIngredient.STREAM_CODEC.apply(ByteBufCodecs.list()).decode(buf);
        int fluidCount = buf.readVarInt();
        List<FluidStackTemplate> fluids = new ArrayList<>(fluidCount);
        for (int i = 0; i < fluidCount; i++)
            fluids.add(FluidStackTemplate.fromNonEmptyStack(FluidStack.STREAM_CODEC.decode(buf)));
        int tolerance = buf.readVarInt();
        int priority = buf.readVarInt();
        byte airFlag = buf.readByte();
        Optional<Boolean> needAir = airFlag == 0 ? Optional.empty() : Optional.of(airFlag == 2);
        List<String> sizes = ByteBufCodecs.STRING_UTF8.apply(ByteBufCodecs.list()).decode(buf);
        Optional<Integer> minFluid = buf.readBoolean() ? Optional.of(buf.readVarInt()) : Optional.empty();
        Optional<Integer> maxFluid = buf.readBoolean() ? Optional.of(buf.readVarInt()) : Optional.empty();
        int outCount = buf.readVarInt();
        List<ItemStackTemplate> output = new ArrayList<>(outCount);
        for (int i = 0; i < outCount; i++)
            output.add(ItemStackTemplate.fromNonEmptyStack(ItemStack.STREAM_CODEC.decode(buf)));
        Optional<FluidStackTemplate> outFluid = Optional.empty();
        if (buf.readBoolean())
            outFluid = Optional.of(FluidStackTemplate.fromNonEmptyStack(FluidStack.STREAM_CODEC.decode(buf)));
        int time = buf.readVarInt();
        return new FermentationRecipe(id, items, fluids, tolerance, priority, needAir, sizes, minFluid, maxFluid, output, outFluid, time);
    }

    private static void toNetwork(RegistryFriendlyByteBuf buf, FermentationRecipe r)
    {
        Identifier.STREAM_CODEC.encode(buf, r.getId());
        SizedIngredient.STREAM_CODEC.apply(ByteBufCodecs.list()).encode(buf, r.inputItems);
        buf.writeVarInt(r.inputFluids.size());
        for (FluidStackTemplate fst : r.inputFluids) FluidStack.STREAM_CODEC.encode(buf, fst.create());
        buf.writeVarInt(r.amountTolerance);
        buf.writeVarInt(r.priority);
        if (r.needAir == null) buf.writeByte(0); else buf.writeByte(r.needAir ? 2 : 1);
        ByteBufCodecs.STRING_UTF8.apply(ByteBufCodecs.list()).encode(buf, r.needSize);
        buf.writeBoolean(r.minFluidAmount != null);
        if (r.minFluidAmount != null) buf.writeVarInt(r.minFluidAmount);
        buf.writeBoolean(r.maxFluidAmount != null);
        if (r.maxFluidAmount != null) buf.writeVarInt(r.maxFluidAmount);
        buf.writeVarInt(r.output.size());
        for (ItemStackTemplate ist : r.output) ItemStack.STREAM_CODEC.encode(buf, ist.create());
        if (r.outputFluid != null) { buf.writeBoolean(true); FluidStack.STREAM_CODEC.encode(buf, r.getOutputFluid()); }
        else buf.writeBoolean(false);
        buf.writeVarInt(r.time);
    }
}