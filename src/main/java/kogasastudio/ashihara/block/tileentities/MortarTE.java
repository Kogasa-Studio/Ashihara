package kogasastudio.ashihara.block.tileentities;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import kogasastudio.ashihara.Ashihara;
import kogasastudio.ashihara.client.models.geo.SimpleInternalControlGeoModel;
import kogasastudio.ashihara.interaction.recipes.MortarRecipe;
import kogasastudio.ashihara.item.ItemOtsuchi;
import kogasastudio.ashihara.registry.TERegistryHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.fluids.capability.templates.FluidTank;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemStackHandler;
import net.neoforged.neoforge.items.wrapper.RangedWrapper;
import software.bernie.geckolib.renderer.GeoObjectRenderer;

import java.util.List;
import java.util.Optional;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.function.Predicate;

public class MortarTE extends AshiharaMachineTE implements IFluidHandler // extends AshiharaMachineTE implements MenuProvider, IFluidHandler
{
    public FluidTank fluidTank = new FluidTank(16000);

    public SimpleInternalControlGeoModel item_display_positions;
    public SimpleInternalControlGeoModel fluid_display_position;

    //public final GeoObjectRenderer<SimpleInternalControlGeoModel> ITEM_RENDERER;

    public static final int SLOT_0 = 0;
    public static final int SLOT_1 = 1;
    public static final int SLOT_2 = 2;
    public static final int SLOT_3 = 3;

    public boolean renderFloatingTip = false;
    public boolean transitingLiquidLevel = false;

    public float progress = 0f;
    public float productionMultiplier = 1.0f;
    public MortarRecipe currentRecipe;
    private Queue<MortarToolType> queue;
    public ItemStackHandler inventory = new ItemStackHandler(4);

    public MortarTE(BlockPos pos, BlockState state)
    {
        super(TERegistryHandler.MORTAR_TE.get(), pos, state);
    }

    public void init(Player player)
    {
        this.item_display_positions = new SimpleInternalControlGeoModel("geo/assistance/mortar_item_display_loc.geo.json", "", player);
        this.fluid_display_position = new SimpleInternalControlGeoModel("geo/assistance/light_wood_edge.geo.json", "", player);
    }

    @Override
    public int getTanks()
    {
        return 1;
    }

    @Override
    public FluidStack getFluidInTank(int tank)
    {
        return fluidTank.getFluidInTank(tank);
    }

    public static IItemHandler getInv(MortarTE te, Direction side)
    {
        if (side.getAxis().equals(Direction.Axis.Y))
        {
            return new RangedWrapper(te.inventory, 0, 4);
        }
        return null;
    }

    public void acceptRecipe(MortarRecipe recipe)
    {
        currentRecipe = recipe;
        queue = new ConcurrentLinkedDeque<>(currentRecipe.getSequence());
    }

    public boolean process()
    {
        if (currentRecipe == null) return false;
        boolean flag = false;
        return flag;
    }

    @Override
    public int getTankCapacity(int tank)
    {
        return fluidTank.getTankCapacity(tank);
    }

    @Override
    public boolean isFluidValid(int tank, FluidStack stack)
    {
        return fluidTank.isFluidValid(tank, stack);
    }

    @Override
    public int fill(FluidStack resource, FluidAction action)
    {
        return fluidTank.fill(resource, action);
    }

    @Override
    public FluidStack drain(FluidStack resource, FluidAction action)
    {
        return fluidTank.drain(resource, action);
    }

    @Override
    public FluidStack drain(int maxDrain, FluidAction action)
    {
        return fluidTank.drain(maxDrain, action);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries)
    {
        if (this.level != null)
        {
            Optional<RecipeHolder<?>> holderOptional = this.level.getRecipeManager().byKey(ResourceLocation.parse(tag.getString("currentRecipe")));
            holderOptional.ifPresent(recipeHolder -> this.currentRecipe = (MortarRecipe) recipeHolder.value());
        }
        this.inventory.deserializeNBT(registries, tag.getCompound("contents"));
        this.fluidTank = this.fluidTank.readFromNBT(registries, tag.getCompound("fluid"));
        super.loadAdditional(tag, registries);
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries)
    {
        tag.putString("currentRecipe", this.currentRecipe == null ? "" : this.currentRecipe.getId().toString());
        tag.put("contents", this.inventory.serializeNBT(registries));
        tag.put("fluid", this.fluidTank.writeToNBT(registries, new CompoundTag()));
        super.saveAdditional(tag, registries);
    }

    public enum MortarToolType
    {
        PESTLE("pestle", i -> i.is(ItemTags.create(ResourceLocation.fromNamespaceAndPath(Ashihara.MODID, "pestle")))),
        OTSUCHI("otsuchi", i -> i.getItem() instanceof ItemOtsuchi || i.is(ItemTags.create(ResourceLocation.fromNamespaceAndPath(Ashihara.MODID, "otsuchi")))),
        HAND("hand", ItemStack::isEmpty);

        public final Predicate<ItemStack> itemPredicate;
        public final String id;
        public static final Codec<MortarToolType> CODEC = Codec.STRING.xmap(MortarToolType::get, MortarToolType::getId);
        public static final Codec<Queue<MortarToolType>> QUEUE_CODEC = RecordCodecBuilder.create
        (
            instance -> instance.group(Codec.list(CODEC).fieldOf("queue").forGetter(List::copyOf)).apply(instance, ConcurrentLinkedDeque::new)
        );

        public static final StreamCodec<RegistryFriendlyByteBuf, MortarToolType> STREAM_CODEC = new StreamCodec<>()
        {
            @Override
            public MortarToolType decode(RegistryFriendlyByteBuf buffer)
            {
                return get(buffer.readUtf());
            }

            @Override
            public void encode(RegistryFriendlyByteBuf buffer, MortarToolType value)
            {
                buffer.writeUtf(value.getId());
            }
        };

        MortarToolType(String id, Predicate<ItemStack> itemPredicate)
        {
            this.id = id;
            this.itemPredicate = itemPredicate;
        }

        public boolean is(ItemStack item)
        {
            return itemPredicate.test(item);
        }

        public String getId()
        {
            return id;
        }

        public static MortarToolType get(String id)
        {
            return valueOf(id);
        }
    }
}
