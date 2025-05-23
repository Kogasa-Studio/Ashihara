package kogasastudio.ashihara.block.blockentity;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import kogasastudio.ashihara.Ashihara;
import kogasastudio.ashihara.block.blockentity.util.RenderAutoSwitch;
import kogasastudio.ashihara.block.blockentity.util.RenderSwitch;
import kogasastudio.ashihara.block.blockentity.util.ToolTipController;
import kogasastudio.ashihara.client.models.geo.InternalControlGeoModel;
import kogasastudio.ashihara.client.models.geo.SimpleInternalControlGeoModel;
import kogasastudio.ashihara.client.models.geo.UIPanelModel;
import kogasastudio.ashihara.helper.ParticleHelper;
import kogasastudio.ashihara.interaction.recipes.MortarRecipe;
import kogasastudio.ashihara.inventory.BEItemStackHandler;
import kogasastudio.ashihara.item.Otsuchi;
import kogasastudio.ashihara.registry.Items;
import kogasastudio.ashihara.registry.RecipeTypes;
import kogasastudio.ashihara.registry.BlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
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
import static net.minecraft.world.level.block.Block.popResource;

public class MortarBE extends AshiharaMachineBE implements IRenderSwitchable, IRenderInWorldToolTip
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
    public ToolTipController<MortarBE> toolTipController;

    private float lastLiquidLevel = 0;

    public int progress = 0;
    public float productionMultiplier = 1.0f;
    public MortarRecipe currentRecipe;
    public ResourceLocation lastRecipe;
    private Queue<MortarToolType> queue = new ConcurrentLinkedDeque<>();
    public BEItemStackHandler<MortarBE> inventory = new BEItemStackHandler<>(4, this);

    public MortarBE(BlockPos pos, BlockState state)
    {
        super(BlockEntities.MORTAR_BE.get(), pos, state);
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

    public Queue<MortarToolType> getQueue()
    {
        return queue;
    }

    public float getProgress()
    {
        if (currentRecipe == null) return 0;
        return 1 - ((float) this.queue.size() / currentRecipe.getSequence().size());
    }

    public static IItemHandler getInv(MortarBE te, Direction side)
    {
        return new RangedWrapper(te.inventory, 0, 4);
    }

    public static IFluidHandler getFluid(MortarBE te, Direction side)
    {
        return te.fluidTank;
    }

    public void refreshRecipe()
    {
        if (this.level == null) return;
        if (this.queue.isEmpty()) acceptRecipe(null);
        RecipeManager recipeManager =  this.level.getRecipeManager();
        List<RecipeHolder<MortarRecipe>> recipes = recipeManager.getAllRecipesFor(RecipeTypes.MORTAR.get());

        Optional<RecipeHolder<MortarRecipe>> recipeOptional = Optional.empty();
        boolean flag = true;
        if (this.lastRecipe != null)
        {
            recipeOptional = recipes.stream().filter(h -> h.value().getId().equals(this.lastRecipe) && h.value().testBE(this)).findFirst();
            if (recipeOptional.isPresent()) flag = false;
        }
        if (flag) recipeOptional = recipes.stream().filter(h -> h.value().testBE(this)).findFirst();

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
        if (currentRecipe != null) lastRecipe = currentRecipe.getId();
        currentRecipe = recipe;
        this.progress = 0;
        if (recipe != null && this.queue.isEmpty()) queue = new ConcurrentLinkedDeque<>(currentRecipe.getSequence()).reversed();
        else this.queue = new ConcurrentLinkedDeque<>();
        setChanged();
    }

    public void finishRecipe(MortarRecipe recipe)
    {
        if (recipe != null)
        {
            int multiplier = this.inventory.testIngredients(recipe.getSizedIngredients(), this.getMaxParallel(), false);
            for (int i = 0; i < multiplier; i++)
            {
                List<ItemStack> remains = this.inventory.insert(recipe.getOutput(), false);
                if (this.level != null)
                {
                    for (ItemStack stack : remains)
                    {
                        if (!stack.isEmpty()) popResource(this.level, this.worldPosition, stack);
                    }
                }
            }
            recipe.testFluidOption(this.fluidTank, multiplier, IFluidHandler.FluidAction.EXECUTE);
        }
        acceptRecipe(null);
    }

    public boolean process(ItemStack stack)
    {
        refreshRecipe();
        if (currentRecipe == null || this.queue.isEmpty() || this.level == null) return false;
        boolean flag = false;
        MortarToolType toolType = this.queue.peek();
        if (toolType.is(stack))
        {
            for (ItemStack i : currentRecipe.getOutput())
            {
                ParticleHelper.spawnItemStackDestruction(this.level, i, new Vec3(this.worldPosition.getX() + 0.5, this.worldPosition.getY() + 0.3, this.worldPosition.getZ() + 0.5), 25, 1.5, 2, 1.5);
            }
            level.playSound(null, this.worldPosition, toolType.getSound(), SoundSource.BLOCKS, 1f, 1f);
            this.queue.poll();
            if (this.queue.isEmpty()) finishRecipe(currentRecipe);
            flag = true;
            setChanged();
        }
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
        this.lastRecipe = ResourceLocation.parse(tag.getString("lastRecipe"));
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
        tag.putString("lastRecipe", this.lastRecipe == null ? "" : this.lastRecipe.toString());
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
        PESTLE("pestle", i -> i.is(Items.PESTLE) || i.is(ItemTags.create(ResourceLocation.fromNamespaceAndPath(Ashihara.MODID, "pestle"))), Component.translatable("tooltip.ashihara.mortar.pestle"), SoundEvents.PLAYER_ATTACK_WEAK),
        OTSUCHI("otsuchi", i -> i.getItem() instanceof Otsuchi || i.is(ItemTags.create(ResourceLocation.fromNamespaceAndPath(Ashihara.MODID, "otsuchi"))), Component.translatable("tooltip.ashihara.mortar.otsuchi"), SoundEvents.PLAYER_ATTACK_STRONG),
        HAND("hand", ItemStack::isEmpty, Component.translatable("tooltip.ashihara.mortar.hand"), SoundEvents.ARMOR_EQUIP_ELYTRA.value());

        public final Predicate<ItemStack> itemPredicate;
        public final String id;
        public final SoundEvent soundEvent;
        public final Component name;
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

        MortarToolType(String id, Predicate<ItemStack> itemPredicate, Component name, SoundEvent soundEvent)
        {
            this.id = id;
            this.itemPredicate = itemPredicate;
            this.soundEvent = soundEvent;
            this.name = name;
        }

        public boolean is(ItemStack item)
        {
            return itemPredicate.test(item);
        }

        public String getId()
        {
            return id;
        }

        public SoundEvent getSound() {return soundEvent;}

        public Component getName() {return name;}

        public static MortarToolType get(String id)
        {
            Optional<MortarToolType> type = Arrays.stream(MortarToolType.values()).filter(t -> t.id.equals(id)).findFirst();
            return type.orElseGet(() -> valueOf(id));
        }
    }
}
