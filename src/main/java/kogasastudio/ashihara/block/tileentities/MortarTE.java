package kogasastudio.ashihara.block.tileentities;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import kogasastudio.ashihara.Ashihara;
import kogasastudio.ashihara.block.tileentities.util.RenderAutoSwitch;
import kogasastudio.ashihara.block.tileentities.util.RenderSwitch;
import kogasastudio.ashihara.block.tileentities.util.ToolTipController;
import kogasastudio.ashihara.client.models.geo.InternalControlGeoModel;
import kogasastudio.ashihara.client.models.geo.SimpleInternalControlGeoModel;
import kogasastudio.ashihara.client.models.geo.UIPanelModel;
import kogasastudio.ashihara.interaction.recipes.MortarRecipe;
import kogasastudio.ashihara.inventory.BEItemStackHandler;
import kogasastudio.ashihara.item.ItemOtsuchi;
import kogasastudio.ashihara.registry.RecipeTypes;
import kogasastudio.ashihara.registry.TERegistryHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.fluids.capability.templates.FluidTank;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.wrapper.RangedWrapper;
import software.bernie.geckolib.animation.Animation;
import software.bernie.geckolib.animation.EasingType;
import software.bernie.geckolib.cache.object.GeoBone;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.function.Predicate;

import static net.minecraft.world.level.block.Block.UPDATE_ALL;

public class MortarTE extends AshiharaMachineTE implements IRenderSwitchable, IRenderInWorldToolTip
{
    public final RenderSwitch switchFluid = new RenderAutoSwitch
    (
        p ->
        {
            if (this.fluid_display_position == null) this.init(p);
            this.fluid_display_position.triggerInternal
            (
                p, this.fluid_display_position.hashCode(),
                new InternalControlGeoModel.InternalAnimationBuilder("sync_liquid_level", Animation.LoopType.HOLD_ON_LAST_FRAME)
                .startBone("main")
                .lerpY(InternalControlGeoModel.InternalAnimationBuilder.VarType.POSITION, 10, this.lastLiquidLevel, this.getLiquidLevel(), EasingType.EASE_IN_OUT_QUAD)
                .endBone().build()
            );
            this.setNeedBlockUpdate();
        },
        p -> this.setNeedBlockUpdate(),
        this::stillTransiting,
        () -> !this.stillTransiting()
    );
    public FluidTank fluidTank = new FluidTank(16000);

    public SimpleInternalControlGeoModel item_display_positions;
    public SimpleInternalControlGeoModel fluid_display_position;
    public UIPanelModel ui_panel_model;
    public ToolTipController<MortarTE> toolTipController;

    private float lastLiquidLevel = 0;

    public int progress = 0;
    public float productionMultiplier = 1.0f;
    public MortarRecipe currentRecipe;
    private Queue<MortarToolType> queue = new ConcurrentLinkedDeque<>();
    public BEItemStackHandler<MortarTE> inventory = new BEItemStackHandler<>(4, this);

    public MortarTE(BlockPos pos, BlockState state)
    {
        super(TERegistryHandler.MORTAR_TE.get(), pos, state);
    }

    @Override
    public void init(Player player)
    {
        this.item_display_positions = new SimpleInternalControlGeoModel("geo/assistance/mortar_item_display_loc.geo.json", "", player);
        this.fluid_display_position = new SimpleInternalControlGeoModel("geo/assistance/mortar_fluid_display_loc.geo.json", "", player);
        this.ui_panel_model = new UIPanelModel(player).showHemmingEdge(true);
        this.toolTipController = new ToolTipController<>(this, this.ui_panel_model);
    }

    public void pushLastLiquidLevel()
    {
        this.lastLiquidLevel = (float) getLiquidLevel();
    }

    public boolean stillTransiting()
    {
        Optional<GeoBone> b = this.fluid_display_position.getBakedModel(this.fluid_display_position.getModelResource(this.fluid_display_position)).getBone("main");
        return b.isPresent() && (Math.abs(b.get().getPosY() - this.getLiquidLevel()) > 0.001);
    }

    /**
     * Gets the height in pixels where liquid quad should be rendered.
     */
    public double getLiquidLevel()
    {
        return ((double) this.fluidTank.getFluidAmount() / this.fluidTank.getCapacity()) * 6d + 2d;
    }

    public int getMaxParallel()
    {
        return 16;
    }

    public void setMultiplier(int multiplier)
    {
        this.productionMultiplier = multiplier;
    }

    public float getProgress()
    {
        if (currentRecipe == null) return 0;
        return 1 - ((float) this.queue.size() / currentRecipe.getSequence().size());
    }

    public static IItemHandler getInv(MortarTE te, Direction side)
    {
        if (side.getAxis().equals(Direction.Axis.Y))
        {
            return new RangedWrapper(te.inventory, 0, 4);
        }
        return null;
    }

    public static IFluidHandler getFluid(MortarTE te, Direction side)
    {
        return te.fluidTank;
    }

    public void refreshRecipe()
    {
        if (this.level == null) return;
        RecipeManager recipeManager =  this.level.getRecipeManager();
        List<RecipeHolder<MortarRecipe>> recipes = recipeManager.getAllRecipesFor(RecipeTypes.MORTAR.get());
        Optional<RecipeHolder<MortarRecipe>> recipeOptional = recipes.stream().filter(h -> h.value().testBE(this)).findFirst();
        if (recipeOptional.isPresent())
        {
            MortarRecipe recipe = recipeOptional.get().value();
            if (recipe == currentRecipe) return;
            acceptRecipe(recipe);
            return;
        }
        acceptRecipe(null);
    }

    public void acceptRecipe(MortarRecipe recipe)
    {
        currentRecipe = recipe;
        this.progress = 0;
        if (recipe != null) queue = new ConcurrentLinkedDeque<>(currentRecipe.getSequence());
        setChanged();
    }

    public void finishRecipe(MortarRecipe recipe)
    {
    }

    public boolean process(ItemStack stack)
    {
        if (currentRecipe == null) return false;
        boolean flag = false;
        return flag;
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries)
    {
        if (this.level != null)
        {
            Optional<RecipeHolder<?>> holderOptional = this.level.getRecipeManager().byKey(ResourceLocation.parse(tag.getString("currentRecipe")));
            holderOptional.ifPresent(recipeHolder -> this.currentRecipe = (MortarRecipe) recipeHolder.value());
            this.level.sendBlockUpdated(this.getBlockPos(), this.getBlockState(), this.getBlockState(), UPDATE_ALL);
        }
        this.inventory.deserializeNBT(registries, tag.getCompound("contents"));
        this.fluidTank = this.fluidTank.readFromNBT(registries, tag.getCompound("fluid"));
        this.queue = new ConcurrentLinkedDeque<>();
        ListTag listTag = tag.getList("queue", Tag.TAG_STRING);
        for (int i = 0; i < listTag.size(); i++)
        {
            MortarToolType mortarToolType = MortarToolType.get(listTag.getString(i));
            this.queue.add(mortarToolType);
        }
        refreshRecipe();
        super.loadAdditional(tag, registries);
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries)
    {
        tag.putString("currentRecipe", this.currentRecipe == null ? "" : this.currentRecipe.getId().toString());
        tag.put("contents", this.inventory.serializeNBT(registries));
        tag.put("fluid", this.fluidTank.writeToNBT(registries, new CompoundTag()));
        ListTag listTag = new ListTag();
        for (MortarToolType type : this.queue)
        {
            listTag.add(StringTag.valueOf(type.id));
        }
        tag.put("queue", listTag);
        super.saveAdditional(tag, registries);
    }

    @Override
    public RenderSwitch getSwitch()
    {
        return this.switchFluid;
    }

    @Override
    public ToolTipController<?> getToolTipController()
    {
        return this.toolTipController;
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
            Optional<MortarToolType> type = Arrays.stream(MortarToolType.values()).filter(t -> t.id.equals(id)).findFirst();
            return type.orElseGet(() -> valueOf(id));
        }
    }
}
