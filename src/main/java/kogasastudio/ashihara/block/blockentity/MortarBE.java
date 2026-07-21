package kogasastudio.ashihara.block.blockentity;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import kogasastudio.ashihara.Ashihara;
import kogasastudio.ashihara.client.gui3d.util.BoneTracer;
import kogasastudio.ashihara.client.models.geo.FermentationDisplayModel;
import kogasastudio.ashihara.helper.ParticleHelper;
import kogasastudio.ashihara.helper.RecipeHelper;
import kogasastudio.ashihara.interaction.recipes.MortarRecipe;
import kogasastudio.ashihara.inventory.BEFluidStackHandler;
import kogasastudio.ashihara.inventory.BEItemStackHandler;
import kogasastudio.ashihara.item.Otsuchi;
import kogasastudio.ashihara.registry.Items;
import kogasastudio.ashihara.registry.RecipeTypes;
import kogasastudio.ashihara.registry.BlockEntities;
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
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
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

public class MortarBE extends AshiharaCommonBE implements IItemHandler<MortarBE>, IFluidHandler
{
    public BEFluidStackHandler<MortarBE> fluidTank = new BEFluidStackHandler<>(16000, this);

    public int progress = 0;
    public float productionMultiplier = 1.0f;
    public MortarRecipe currentRecipe;
    public Identifier lastRecipe;
    private Queue<MortarToolType> queue = new ConcurrentLinkedDeque<>();
    public BEItemStackHandler<MortarBE> inventory = new BEItemStackHandler<>(4, this);

    public float prevFluidLevel = 0f;
    public float fluidLevel = 0f;
    public boolean fluidLevelChanged = false;
    public boolean inited = false;
    private FermentationDisplayModel model;
    public Map<String, BoneTracer> boneTracers = new LinkedHashMap<>();

    public MortarBE(BlockPos pos, BlockState state)
    {
        super(BlockEntities.MORTAR_BE.get(), pos, state);
    }

    public void pushLastLiquidLevel()
    {
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

    @Override
    public ResourceHandler<ItemResource> getItemResource(MortarBE be, Direction direction) {return this.inventory;}
    @Override
    public BEFluidStackHandler<?> getTank() {return this.fluidTank;}

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
            RecipeManager recipeManager = serverLevel.recipeAccess();
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
        this.prevFluidLevel = this.fluidLevel;
        this.fluidLevel = (float) this.fluidTank.getFluidAmount() / (float) this.fluidTank.getCapacity();
        this.fluidLevelChanged = true;
        this.queue = new ConcurrentLinkedDeque<>();
        for (MortarToolType type : input.listOrEmpty("queue", MortarToolType.CODEC))
        {
            this.queue.add(type);
        }
        if (this.level instanceof ServerLevel)
        {
            refreshRecipe();
        }
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

    public FermentationDisplayModel getModel()
    {
        if (this.level == null || !this.level.isClientSide()) return null;
        if (this.model == null)
        {
            this.model = new FermentationDisplayModel("assistance/mortar_display", "textures/geo/empty.png", "block/mortar_display");
            for (int i = 0; i < 8; i++)
            {
                String id = "level" + i;
                createTracer(id);
            }
            createTracer("fluid_display");
            createTracer("item_display");
        }
        return this.model;
    }

    private void createTracer(String name)
    {
        BoneTracer tracer = new BoneTracer(b -> b.name().equals(name));
        boneTracers.put(name, tracer);
        this.model.getRendererPoseSync().ashihara_1_21$addTracer(tracer);
    }

    public BoneTracer getBoneTracer(String id)
    {
        return this.boneTracers.get(id);
    }

    public void dropContents(Level level, BlockPos pos)
    {
        for (int i = 0; i < this.inventory.size(); i++)
        {
            ItemStack stack = this.inventory.getStackInSlot(i);
            if (!stack.isEmpty())
            {
                Block.popResource(level, pos, stack);
            }
        }
    }

    @Override
    public void setChanged()
    {
        super.setChanged();
        float t = (float) this.fluidTank.getFluidAmount() / (float) this.fluidTank.getCapacity();
        if (this.fluidLevel != t)
        {
            this.prevFluidLevel = this.fluidLevel;
            this.fluidLevel = t;
            this.fluidLevelChanged = true;
            if (this.level != null && !this.level.isClientSide()) this.sync();
        }
        if (this.level != null && !this.level.isClientSide())
        {
            this.level.sendBlockUpdated(this.worldPosition, this.getBlockState(), this.getBlockState(), Block.UPDATE_ALL);
        }
    }

    @Override
    public void preRemoveSideEffects(BlockPos pos, BlockState state)
    {
        if (this.level != null && !this.level.isClientSide())
        {
            dropContents(this.level, this.getBlockPos());
        }
        super.preRemoveSideEffects(pos, state);
    }

    @Override
    public void onLoad()
    {
        super.onLoad();
        this.setChanged();
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
