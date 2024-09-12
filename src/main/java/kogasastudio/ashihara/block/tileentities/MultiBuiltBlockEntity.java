package kogasastudio.ashihara.block.tileentities;

import kogasastudio.ashihara.block.building.*;
import kogasastudio.ashihara.block.building.component.*;
import kogasastudio.ashihara.helper.MathHelper;
import kogasastudio.ashihara.helper.ShapeHelper;
import kogasastudio.ashihara.item.ItemRegistryHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

import static kogasastudio.ashihara.block.building.BaseMultiBuiltBlock.FACING;

@SuppressWarnings("all")
public class MultiBuiltBlockEntity extends AshiharaMachineTE implements IMultiBuiltBlock
{
    public static final int OPCODE_COMPONENT = 0;
    public static final int OPCODE_ADDITIONAL = 1;
    public static final int OPCODE_READALL = 2;

    public List<ComponentStateDefinition> COMPONENTS = new ArrayList<>();
    public List<ComponentStateDefinition> ADDITIONAL_COMPONENTS = new ArrayList<>();
    private VoxelShape shapeCache = Shapes.empty();
    public List<Occupation> occupationCache = new ArrayList<>();

    public MultiBuiltBlockEntity(BlockPos pPos, BlockState pBlockState)
    {
        super(TERegistryHandler.MULTI_BUILT_BLOCKENTITY.get(), pPos, pBlockState);
    }

    public boolean tryPlace(UseOnContext context, BuildingComponent component)
    {
        boolean flag = false;
        ComponentStateDefinition definition = component.definite(this, context);
        if (definition != null)
        {
            if (component instanceof AdditionalComponent additionalComponent)
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

    public boolean tryBreak(UseOnContext context)
    {
        ItemStack stack = context.getItemInHand();
        Vec3 vec = context.getClickLocation();
        BlockPos pos = context.getClickedPos();
        Vec3 inBlockVec = transformVec3(inBlockVec(vec));
        int opcode = stack.is(ItemRegistryHandler.WOODEN_HAMMER) ? OPCODE_COMPONENT : stack.is(ItemRegistryHandler.CHISEL) ? OPCODE_ADDITIONAL : -1;
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
            List<ItemStack> drops = definition.component().drops;
            this.level.playSound(null, this.worldPosition, event, SoundSource.BLOCKS, 1.0f, 1.0f);
            if (this.level.isClientSide() && !drops.get(0).isEmpty())
            {
                RandomSource random = this.level.getRandom();
                ParticleOptions data = new ItemParticleOption(ParticleTypes.ITEM, drops.get(0));
                for (int i = 0; i < 10; i += 1)
                {
                    this.level.addParticle
                    (
                    data,
                    (double) this.worldPosition.getX() + 0.5D,
                    (double) this.worldPosition.getY() + 0.7D,
                    (double) this.worldPosition.getZ() + 0.5D,
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
        float rotation = switch (this.getBlockState().getValue(FACING))
        {
            case WEST -> -90;
            case SOUTH -> -180;
            case EAST -> -270;
            default -> 0;
        };
        shape = ShapeHelper.rotateShape(shape, rotation);
        this.shapeCache = shape;
    }

    public void reloadOccupation()
    {
        this.occupationCache.clear();
        for (ComponentStateDefinition definition : this.COMPONENTS)
        {
            this.occupationCache.addAll(definition.occupation());
        }
    }

    public boolean checkConnection()
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
        if (flag) refresh();
        return flag;
    }

    public ComponentStateDefinition getComponentByPosition(Vec3 vec3, int opcode)
    {
        for (ComponentStateDefinition m : this.getComponents(opcode))
        {
            if (m.shape().bounds().distanceToSqr(vec3) <= 0) return m;
        }
        return null;
    }

    //将本方块的内部坐标系中的方向转换为绝对坐标系中的方向
    public Direction toAbsolute(Direction dir)
    {
        Direction current = this.getBlockState().getValue(FACING);
        return switch (current)
        {
            case WEST -> dir.getCounterClockWise();
            case EAST -> dir.getClockWise();
            case SOUTH -> dir.getOpposite();
            default -> dir;
        };
    }

    //将绝对坐标系的方向转换为方块内部坐标系的方向
    public Direction fromAbsolute(Direction dir)
    {
        Direction current = this.getBlockState().getValue(FACING);
        return switch (current)
        {
            case WEST -> dir.getClockWise();
            case EAST -> dir.getCounterClockWise();
            case SOUTH -> dir.getOpposite();
            default -> dir;
        };
    }

    public Vec3 inBlockVec(Vec3 vec)
    {
        double x = vec.x() - this.getBlockPos().getX();
        double y = vec.y() - this.getBlockPos().getY();
        double z = vec.z() - this.getBlockPos().getZ();
        return new Vec3(x, y, z);
    }

    public Vec3 transformVec3(Vec3 vec3)
    {
        double rotation = switch (this.getBlockState().getValue(FACING))
        {
            case WEST -> 90;
            case SOUTH -> 180;
            case EAST -> 270;
            default -> 0;
        };
        rotation = Math.toRadians(rotation);
        double[] transformed = MathHelper.rotatePoint(vec3.x(), vec3.z(), 0.5, 0.5, rotation);
        return new Vec3(transformed[0], vec3.y(), transformed[1]);
    }

    public Vec3 outLayVec3(Vec3 vec3)
    {
        double rotation = switch (this.getBlockState().getValue(FACING))
        {
            case WEST -> -90;
            case SOUTH -> -180;
            case EAST -> -270;
            default -> 0;
        };
        rotation = Math.toRadians(rotation);
        double[] transformed = MathHelper.rotatePoint(vec3.x(), vec3.z(), 0.5, 0.5, rotation);
        return new Vec3(transformed[0], vec3.y(), transformed[1]);
    }

    public void refresh()
    {
        reloadShape();
        reloadOccupation();
        setChanged();
        if (this.hasLevel()) this.getLevel().sendBlockUpdated(this.getBlockPos(), this.getBlockState(), this.getBlockState(), 3);
    }

    public VoxelShape getShape()
    {
        return shapeCache;
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
        super.setRemoved();
    }

    @Override
    protected void loadAdditional(CompoundTag pTag, HolderLookup.Provider pRegistries)
    {
        super.loadAdditional(pTag, pRegistries);
        this.COMPONENTS.clear();
        this.ADDITIONAL_COMPONENTS.clear();
        ListTag models = pTag.getList("models", 10);
        for (Tag tag : models)
        {
            this.COMPONENTS.add(ComponentStateDefinition.deserializeNBT((CompoundTag) tag));
        }
        ListTag additional_models = pTag.getList("additional_models", 10);
        for (Tag tag : additional_models)
        {
            this.ADDITIONAL_COMPONENTS.add(ComponentStateDefinition.deserializeNBT((CompoundTag) tag));
        }
        refresh();
    }

    @Override
    protected void saveAdditional(CompoundTag pTag, HolderLookup.Provider pRegistries)
    {
        ListTag listTag = new ListTag();
        for (ComponentStateDefinition definition : this.COMPONENTS)
        {
            listTag.add(definition.serializeNBT());
        }
        pTag.put("models", listTag);
        ListTag additionalListTag = new ListTag();
        for (ComponentStateDefinition definition : this.ADDITIONAL_COMPONENTS)
        {
            additionalListTag.add(definition.serializeNBT());
        }
        pTag.put("additional_models", additionalListTag);
        super.saveAdditional(pTag, pRegistries);
    }

    @Override
    public List<ComponentStateDefinition> getComponents(int opcode)
    {
        if (opcode == OPCODE_READALL)
        {
            List<ComponentStateDefinition> components = new ArrayList<>();
            components.addAll(COMPONENTS);
            components.addAll(ADDITIONAL_COMPONENTS);
            return components;
        }
        return switch (opcode)
        {
            case OPCODE_COMPONENT -> this.COMPONENTS;
            case OPCODE_ADDITIONAL -> this.ADDITIONAL_COMPONENTS;
            default -> List.of();
        };
    }
}
