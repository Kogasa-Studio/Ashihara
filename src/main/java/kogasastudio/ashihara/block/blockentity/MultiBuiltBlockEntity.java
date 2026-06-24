package kogasastudio.ashihara.block.blockentity;

import kogasastudio.ashihara.block.building.*;
import kogasastudio.ashihara.block.building.component.*;
import kogasastudio.ashihara.block.furniture.FurnitureComponent;
import kogasastudio.ashihara.helper.ShapeHelper;
import kogasastudio.ashihara.registry.Items;
import kogasastudio.ashihara.registry.BlockEntities;
import kogasastudio.ashihara.utils.shape.VoxelShapeSerializer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("NullableProblems")
public class MultiBuiltBlockEntity extends AshiharaCommonBE implements IMultiBuiltBlock
{
    public static final int OPCODE_COMPONENT = 0;
    public static final int OPCODE_ADDITIONAL = 1;
    public static final int OPCODE_READALL = 2;
    public static final int OPCODE_FURNITURE = 3;

    public List<ComponentStateDefinition> COMPONENTS = new ArrayList<>();
    public List<ComponentStateDefinition> ADDITIONAL_COMPONENTS = new ArrayList<>();
    public List<ComponentStateDefinition> FURNITURE = new ArrayList<>();
    public List<Occupation> occupationCache = new ArrayList<>();

    public MultiBuiltBlockEntity(BlockPos pPos, BlockState pBlockState)
    {
        super(BlockEntities.MULTI_BUILT_BLOCKENTITY.get(), pPos, pBlockState);
    }

    public boolean tryPlace(UseOnContext context, BuildingComponent component)
    {
        boolean flag = false;
        ComponentStateDefinition definition = component.definite(this, context);
        if (definition != null)
        {
            if (component instanceof AdditionalComponent)
            {
                boolean canAppend = true;
                for (ComponentStateDefinition def : ADDITIONAL_COMPONENTS)
                {
                    if (def.occupation().hashCode() == definition.occupation().hashCode() && def.equals(definition)) canAppend = false;
                }
                if (canAppend)
                {
                    this.ADDITIONAL_COMPONENTS.add(definition);
                    flag = true;
                }
            }
            else if (Occupation.join(definition.occupation(), this.occupationCache))
            {
                this.COMPONENTS.add(definition);
                flag = true;
            }
        }
        if (flag)
        {
            refresh();
            SoundEvent event = definition.component().getSoundType().getPlaceSound();
            this.level.playSound(null, this.worldPosition, event, SoundSource.BLOCKS, 1.0f, 1.0f);
            return true;
        }
        return false;
    }

    public boolean tryPlaceFurniture(UseOnContext context, FurnitureComponent component)
    {
        ComponentStateDefinition definition = component.definite(this, context);
        if (definition != null)
        {
            definition = FurnitureComponent.tryNudge(this.FURNITURE, definition);
            if (definition == null) return false;
            this.FURNITURE.add(definition);
            refresh();
            SoundEvent event = definition.component().getSoundType().getPlaceSound();
            this.level.playSound(null, this.worldPosition, event, SoundSource.BLOCKS, 1.0f, 1.0f);
            return true;
        }
        return false;
    }

    public boolean tryBreak(UseOnContext context)
    {
        ItemStack stack = context.getItemInHand();
        Vec3 vec = context.getClickLocation();
        Vec3 inBlockVec = inBlockVec(vec);
        int opcode = stack.is(Items.WOODEN_HAMMER) ? OPCODE_COMPONENT : stack.is(Items.CHISEL) ? OPCODE_ADDITIONAL : -1;
        if (opcode == OPCODE_COMPONENT || opcode == OPCODE_ADDITIONAL)
        {
            ComponentStateDefinition definition = getComponentByPosition(inBlockVec, opcode);
            if (definition != null)
            {
                return breakComponent(definition, context.getPlayer(), opcode);
            }
        }
        return false;
    }

    public boolean breakComponent(ComponentStateDefinition definition, @Nullable Player player, int opcode)
    {
        if (this.getComponents(opcode).contains(definition))
        {
            SoundEvent event = definition.component().getSoundType().getBreakSound();
            List<ItemStack> drops = definition.component().getDrops(definition, this);
            Vec3 vec = definition.inBlockPos();
            this.level.playSound(null, this.worldPosition, event, SoundSource.BLOCKS, 1.0f, 1.0f);
            if (this.level.isClientSide() && !drops.getFirst().isEmpty())
            {
                RandomSource random = this.level.getRandom();
                ParticleOptions data = new ItemParticleOption(ParticleTypes.ITEM, drops.getFirst().getItem());
                for (int i = 0; i < 20; i += 1)
                {
                    this.level.addParticle
                    (
                        data,
                        this.getBlockPos().getX() + vec.x,
                        this.getBlockPos().getY() + vec.y,
                        this.getBlockPos().getZ() + vec.z,
                        ((double) random.nextFloat() - 0.5D) * 0.2D,
                        ((double) random.nextFloat() - 0.5D) * 0.2D,
                        ((double) random.nextFloat() - 0.5D) * 0.2D
                    );
                }
            }
            if (player != null && !player.isCreative())
            {
                for (ItemStack item : drops)
                {
                    ItemEntity entity = new ItemEntity(this.level, this.worldPosition.getX() + 0.5d, this.worldPosition.getY() + 0.5d, this.worldPosition.getZ() + 0.5d, item.copy());
                    entity.setDefaultPickUpDelay();
                    this.level.addFreshEntity(entity);
                }
            }
            this.getComponents(opcode).remove(definition);
            refresh();
            return true;
        }
        return false;
    }

    public void reloadShape()
    {
        VoxelShape shape = Shapes.empty();
        for (ComponentStateDefinition definition : this.COMPONENTS)
        {
            shape = Shapes.or(shape, definition.shape());
        }
        for (ComponentStateDefinition definition : this.ADDITIONAL_COMPONENTS)
        {
            shape = Shapes.or(shape, definition.shape());
        }
        for (ComponentStateDefinition definition : this.FURNITURE)
        {
            shape = Shapes.or(shape, definition.shape());
        }
        setShape(shape);
    }

    public void reloadOccupation()
    {
        this.occupationCache.clear();
        for (ComponentStateDefinition definition : this.COMPONENTS)
        {
            this.occupationCache.addAll(definition.occupation());
        }
    }

    public void checkConnection()
    {
        boolean flag = false;
        for (int i = 0; i < this.COMPONENTS.size(); i++)
        {
            ComponentStateDefinition definition = this.COMPONENTS.get(i);
            if (definition.component() instanceof Connectable comp)
            {
                this.COMPONENTS.set(i, comp.applyConnection(this, definition));
                flag = true;
            }
        }
        for (int i = 0; i < this.ADDITIONAL_COMPONENTS.size(); i++)
        {
            ComponentStateDefinition definition = this.ADDITIONAL_COMPONENTS.get(i);
            if (definition.component() instanceof Connectable comp)
            {
                this.ADDITIONAL_COMPONENTS.set(i, comp.applyConnection(this, definition));
                flag = true;
            }
        }
        if (flag) refresh();
    }

    public boolean tryInteract(UseOnContext context)
    {
        int opcode = OPCODE_COMPONENT;
        Vec3 vec = context.getClickLocation();
        Vec3 inBlockPos = inBlockVec(vec);
        ComponentStateDefinition definition = getComponentByPosition(inBlockPos, opcode);
        if (definition == null)
        {
            opcode = OPCODE_ADDITIONAL;
            definition = getComponentByPosition(inBlockPos, opcode);
        }
        if (definition == null)
        {
            opcode = OPCODE_FURNITURE;
            definition = getComponentByPosition(inBlockPos, opcode);
        }
        if (definition == null) return false;
        for (int i = 0; i < this.getComponents(opcode).size(); i++)
        {
            ComponentStateDefinition def = this.getComponents(opcode).get(i);
            if (def == definition && definition.component() instanceof Interactable comp)
            {
                ComponentStateDefinition interacted = comp.handleInteraction(context, def);
                if (interacted == null)
                {
                    this.getComponents(opcode).remove(i);
                    if (opcode == OPCODE_FURNITURE)
                        refresh();
                    else
                    {
                        refresh();
                        SoundEvent event = definition.component().getSoundType().getBreakSound();
                        this.level.playSound(null, this.worldPosition, event, SoundSource.BLOCKS, 1.0f, 1.0f);
                    }
                    return true;
                }
                if (interacted == def) return false;
                this.getComponents(opcode).set(i, interacted);
                SoundType interactSound = comp.getInteractSound();
                if (interactSound != SoundType.EMPTY) this.level.playSound(null, this.worldPosition, comp.getInteractSound().getPlaceSound(), SoundSource.BLOCKS, 1.0f, 1.0f);
                refresh();
                return true;
            }
            else if (def == definition && definition.component() instanceof Decoratable comp)
            {
                ComponentStateDefinition decoration = comp.decorate(this, context, def);
                boolean canAppend = true;
                for (ComponentStateDefinition d : ADDITIONAL_COMPONENTS)
                {
                    if (d.occupation().hashCode() == decoration.occupation().hashCode() && d.equals(decoration)) canAppend = false;
                }
                if (canAppend)
                {
                    this.ADDITIONAL_COMPONENTS.add(definition);
                    this.level.playSound(null, this.worldPosition, decoration.component().getSoundType().getPlaceSound(), SoundSource.BLOCKS, 1.0f, 1.0f);
                    refresh();
                    return true;
                }
            }
        }
        return false;
    }

    public void checkMaterial()
    {
        if (this.level == null) return;
        BaseMultiBuiltBlock newMaterial = (BaseMultiBuiltBlock) this.getBlockState().getBlock();
        for (ComponentStateDefinition definition : this.getComponents(OPCODE_READALL))
        {
            if (definition.component().getMaterial().get().material.getPriority() > newMaterial.material.getPriority()) newMaterial = definition.component().getMaterial().get();
        }
        if (newMaterial != this.getBlockState().getBlock())
        {
            this.level.setBlock(this.getBlockPos(), newMaterial.applyMaterial(this.getBlockState()), 3);
            this.level.setBlockEntity(this);
        }
    }

    public ComponentStateDefinition getComponentByPosition(Vec3 vec3, int opcode)
    {
        for (ComponentStateDefinition m : this.getComponents(opcode))
        {
            if (m.shape().bounds().distanceToSqr(vec3) <= 0.00001) return m;
        }
        return null;
    }

    //将绝对坐标转换为以本方块坐标为原点的局部坐标
    public Vec3 inBlockVec(Vec3 vec)
    {
        double x = snapEdge(vec.x() - this.getBlockPos().getX());
        double y = snapEdge(vec.y() - this.getBlockPos().getY());
        double z = snapEdge(vec.z() - this.getBlockPos().getZ());
        return new Vec3(x, y, z);
    }

    private static double snapEdge(double v)
    {
        double snapped = Math.round(v * 16.0) / 16.0;
        return Math.abs(v - snapped) < 0.0001 ? snapped : v;
    }

    public void refresh()
    {
        refresh(true);
    }

    public void refresh(boolean reloadShape)
    {
        if (reloadShape) reloadShape();
        reloadOccupation();
        sync();
        setChanged();
        checkMaterial();
        if (this.hasLevel()) this.getLevel().sendBlockUpdated(this.getBlockPos(), this.getBlockState(), this.getBlockState(), 3);
        if (COMPONENTS.isEmpty() && ADDITIONAL_COMPONENTS.isEmpty() && FURNITURE.isEmpty())
            this.getLevel().removeBlock(this.getBlockPos(), false);
    }

    @Override
    public void setRemoved()
    {
        for (ComponentStateDefinition model : this.getComponents(OPCODE_COMPONENT))
        {
            SoundEvent event = model.component().getSoundType().getBreakSound();
            this.level.playSound(null, this.getBlockPos(), event, SoundSource.BLOCKS, 1.0f, 1.0f);
        }
        for (ComponentStateDefinition model : this.getComponents(OPCODE_ADDITIONAL))
        {
            SoundEvent event = model.component().getSoundType().getBreakSound();
            this.level.playSound(null, this.getBlockPos(), event, SoundSource.BLOCKS, 1.0f, 1.0f);
        }
        for (ComponentStateDefinition model : this.FURNITURE)
        {
            SoundEvent event = model.component().getSoundType().getBreakSound();
            this.level.playSound(null, this.getBlockPos(), event, SoundSource.BLOCKS, 1.0f, 1.0f);
        }
        super.setRemoved();
    }

    @Override
    protected void loadAdditional(ValueInput input)
    {
        super.loadAdditional(input);
        this.COMPONENTS.clear();
        this.ADDITIONAL_COMPONENTS.clear();
        this.FURNITURE.clear();
        for (ValueInput child : input.childrenListOrEmpty("models"))
        {
            this.COMPONENTS.add(ComponentStateDefinition.deserializeNBT(child));
        }
        for (ValueInput child : input.childrenListOrEmpty("additional_models"))
        {
            this.ADDITIONAL_COMPONENTS.add(ComponentStateDefinition.deserializeNBT(child));
        }
        for (ValueInput child : input.childrenListOrEmpty("furniture"))
        {
            this.FURNITURE.add(ComponentStateDefinition.deserializeNBT(child));
        }

        boolean shapeLoaded = loadShape(input);
        refresh(!shapeLoaded);
    }

    @Override
    protected void saveAdditional(ValueOutput output)
    {
        var listTag = output.childrenList("models");
        for (ComponentStateDefinition definition : this.COMPONENTS)
        {
            ValueOutput o = listTag.addChild();
            definition.serialize(o);
        }
        var additionalListTag = output.childrenList("additional_models");
        for (ComponentStateDefinition definition : this.ADDITIONAL_COMPONENTS)
        {
            ValueOutput o = additionalListTag.addChild();
            definition.serialize(o);
        }
        var furnitureListTag = output.childrenList("furniture");
        for (ComponentStateDefinition definition : this.FURNITURE)
        {
            ValueOutput o = furnitureListTag.addChild();
            definition.serialize(o);
        }
        saveShape(output);
        super.saveAdditional(output);
    }

    @Override
    public List<ComponentStateDefinition> getComponents(int opcode)
    {
        if (opcode == OPCODE_READALL)
        {
            List<ComponentStateDefinition> components = new ArrayList<>();
            components.addAll(COMPONENTS);
            components.addAll(ADDITIONAL_COMPONENTS);
            components.addAll(FURNITURE);
            return components;
        }
        return switch (opcode)
        {
            case OPCODE_COMPONENT -> this.COMPONENTS;
            case OPCODE_ADDITIONAL -> this.ADDITIONAL_COMPONENTS;
            case OPCODE_FURNITURE -> this.FURNITURE;
            default -> List.of();
        };
    }

    // <editor-fold desc="VoxelShape Persistent Storage">

    private VoxelShape shape = Shapes.empty();

    private void setShape(VoxelShape shape) {this.shape = shape;}

    public VoxelShape getShape() {return shape;}

    private void saveShape(ValueOutput output)
    {
        VoxelShapeSerializer.saveShape(getShape(), output.child("shape"));
    }

    private boolean loadShape(ValueInput input)
    {
        VoxelShape result = VoxelShapeSerializer.loadShape(input.childOrEmpty("shape"));
        if (result != null)
        {
            setShape(result);
            return true;
        }
        return false;
    }

    // </editor-fold>
}
