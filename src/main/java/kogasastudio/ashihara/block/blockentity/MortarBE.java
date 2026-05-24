package kogasastudio.ashihara.block.blockentity;

import com.geckolib.animation.object.EasingType;
import com.geckolib.animation.object.LoopType;
import com.geckolib.cache.animation.Animation;
import com.geckolib.cache.model.GeoBone;
import com.geckolib.renderer.base.GeoRenderState;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import kogasastudio.ashihara.Ashihara;
import kogasastudio.ashihara.block.blockentity.util.RenderAutoSwitch;
import kogasastudio.ashihara.block.blockentity.util.RenderSwitch;
import kogasastudio.ashihara.block.blockentity.util.ToolTipController;
import kogasastudio.ashihara.client.gui3d.util.BoneTracer;
import kogasastudio.ashihara.client.models.geo.InternalControlGeoModel;
import kogasastudio.ashihara.client.models.geo.SimpleInternalControlGeoModel;
import kogasastudio.ashihara.client.models.geo.UIPanelModel;
import kogasastudio.ashihara.helper.ParticleHelper;
import kogasastudio.ashihara.helper.RecipeHelper;
import kogasastudio.ashihara.interaction.recipes.MortarRecipe;
import kogasastudio.ashihara.inventory.BEFluidStackHandler;
import kogasastudio.ashihara.inventory.BEItemStackHandler;
import kogasastudio.ashihara.item.Otsuchi;
import kogasastudio.ashihara.registry.Items;
import kogasastudio.ashihara.registry.RecipeTypes;
import kogasastudio.ashihara.registry.BlockEntities;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.fluid.FluidResource;
import net.neoforged.neoforge.transfer.item.ItemResource;

import java.util.*;
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
                new InternalControlGeoModel.InternalAnimationBuilder("sync_liquid_level", LoopType.HOLD_ON_LAST_FRAME)
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
    public BEFluidStackHandler<MortarBE> fluidTank = new BEFluidStackHandler<>(16000, this);

    public SimpleInternalControlGeoModel item_display_positions;
    public Map<String, BoneTracer> boneTracers = new LinkedHashMap<>();
    public final BoneTracer level0 = createTracer("level0");
    public final BoneTracer level1 = createTracer("level1");
    public final BoneTracer level2 = createTracer("level2");
    public final BoneTracer level3 = createTracer("level3");
    public final BoneTracer level4 = createTracer("level4");
    public final BoneTracer level5 = createTracer("level5");
    public final BoneTracer level6 = createTracer("level6");
    public final BoneTracer level7 = createTracer("level7");
    public SimpleInternalControlGeoModel fluid_display_position;
    public UIPanelModel ui_panel_model;
    public ToolTipController<MortarBE> toolTipController;

    private float lastLiquidLevel = 0;

    public int progress = 0;
    public float productionMultiplier = 1.0f;
    public MortarRecipe currentRecipe;
    public Identifier lastRecipe;
    private Queue<MortarToolType> queue = new ConcurrentLinkedDeque<>();
    public BEItemStackHandler<MortarBE> inventory = new BEItemStackHandler<>(4, this);

    public MortarBE(BlockPos pos, BlockState state)
    {
        super(BlockEntities.MORTAR_BE.get(), pos, state);
        this.item_display_positions = new SimpleInternalControlGeoModel("assistance/mortar_item_display_loc", "textures/geo/empty.png", Minecraft.getInstance().player);
        this.fluid_display_position = new SimpleInternalControlGeoModel("assistance/mortar_fluid_display_loc", "textures/geo/empty.png", Minecraft.getInstance().player);
        for (BoneTracer tracer : boneTracers.values())
        {
            this.item_display_positions.getRendererPoseSync().ashihara_1_21$addTracer(tracer);
        }
    }

    private BoneTracer createTracer(String name)
    {
        BoneTracer tracer = new BoneTracer(b -> b.name().equals(name));
        boneTracers.put(name, tracer);
        return tracer;
    }

    @Override
    public void init(Player player)
    {
        this.ui_panel_model = new UIPanelModel(player).showHemmingEdge(true);
        this.toolTipController = new ToolTipController<>(this, this.ui_panel_model);
    }

    public void pushLastLiquidLevel()
    {
        this.lastLiquidLevel = (float) getLiquidLevel();
    }

    public boolean stillTransiting()
    {
        Optional<GeoBone> b = this.fluid_display_position.getBakedModel(this.fluid_display_position.getModelResource(new GeoRenderState.Impl(Map.of()))).getBone("main");
        return b.isPresent() && (Math.abs(/*b.get().getPosY()*/ - this.getLiquidLevel()) > 0.001);
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

    public static ResourceHandler<ItemResource>  getInv  (MortarBE te, Direction side) { return te.inventory; }
    public static ResourceHandler<FluidResource> getFluid(MortarBE te, Direction side) { return te.fluidTank; }

    public void refreshRecipe()
    {
        if (this.level == null) return;
        if (this.queue.isEmpty()) acceptRecipe(null);
        Collection<RecipeHolder<MortarRecipe>> recipes = RecipeHelper.getRecipesByType(this.level, RecipeTypes.MORTAR.get());

        Optional<RecipeHolder<MortarRecipe>> recipeOptional = Optional.empty();
        boolean flag = true;
        if (this.lastRecipe != null)
        {
            Identifier lastId = this.lastRecipe;
            recipeOptional = recipes.stream().filter(h -> h.value().getId().equals(lastId) && h.value().testBE(this)).findFirst();
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
            recipe.testFluidOption(this.fluidTank, multiplier, false);
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
    protected void loadAdditional(ValueInput input)
    {
        if (this.level instanceof ServerLevel serverLevel)
        {
            RecipeManager recipeManager = (RecipeManager) serverLevel.recipeAccess();
            input.getString("currentRecipe").ifPresent(id ->
            {
                if (!id.isEmpty())
                {
                    ResourceKey<Recipe<?>> key = ResourceKey.create(Registries.RECIPE, Identifier.parse(id));
                    recipeManager.byKey(key).ifPresent(holder -> this.currentRecipe = (MortarRecipe) holder.value());
                }
            });
            this.level.sendBlockUpdated(this.getBlockPos(), this.getBlockState(), this.getBlockState(), UPDATE_ALL);
        }
        input.getString("lastRecipe").ifPresent(id ->
        {
            if (!id.isEmpty()) this.lastRecipe = Identifier.parse(id);
        });
        input.readChild("contents", this.inventory);
        input.readChild("fluid",    this.fluidTank);
        this.queue = new ConcurrentLinkedDeque<>();
        for (MortarToolType type : input.listOrEmpty("queue", MortarToolType.CODEC))
        {
            this.queue.add(type);
        }
        refreshRecipe();
        super.loadAdditional(input);
    }

    @Override
    protected void saveAdditional(ValueOutput output)
    {
        output.putString("currentRecipe", this.currentRecipe == null ? "" : this.currentRecipe.getId().toString());
        output.putString("lastRecipe",    this.lastRecipe    == null ? "" : this.lastRecipe.toString());
        output.putChild("contents", this.inventory);
        output.putChild("fluid",    this.fluidTank);
        var queueList = output.list("queue", MortarToolType.CODEC);
        for (MortarToolType type : this.queue)
        {
            queueList.add(type);
        }
        super.saveAdditional(output);
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
        PESTLE("pestle", i -> i.is(Items.PESTLE) || i.is(ItemTags.create(Identifier.fromNamespaceAndPath(Ashihara.MODID, "pestle"))), Component.translatable("tooltip.ashihara.mortar.pestle"), SoundEvents.PLAYER_ATTACK_WEAK),
        OTSUCHI("otsuchi", i -> i.getItem() instanceof Otsuchi || i.is(ItemTags.create(Identifier.fromNamespaceAndPath(Ashihara.MODID, "otsuchi"))), Component.translatable("tooltip.ashihara.mortar.otsuchi"), SoundEvents.PLAYER_ATTACK_STRONG),
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
