package kogasastudio.ashihara.block.tileentities;

import kogasastudio.ashihara.block.building.*;
import kogasastudio.ashihara.block.building.component.BuildingComponent;
import kogasastudio.ashihara.block.building.component.Connectable;
import kogasastudio.ashihara.block.building.component.ModelStateDefinition;
import kogasastudio.ashihara.block.building.component.Occupation;
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
    public List<ModelStateDefinition> MODEL_STATES = new ArrayList<>();
    private VoxelShape shapeCache = Shapes.empty();
    public List<Occupation> occupationCache = new ArrayList<>();

    public MultiBuiltBlockEntity(BlockPos pPos, BlockState pBlockState)
    {
        super(TERegistryHandler.MULTI_BUILT_BLOCKENTITY.get(), pPos, pBlockState);
    }

    public boolean tryPlace(UseOnContext context, BuildingComponent component)
    {
        ModelStateDefinition definition = component.getModelDefinition(this, context);
        if (definition != null && Occupation.join(definition.occupation(), this.occupationCache))
        {
            this.MODEL_STATES.add(definition);
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
        double x = vec.x() - pos.getX();
        double y = vec.y() - pos.getY();
        double z = vec.z() - pos.getZ();
        Vec3 inBlockVec = transformVec3(new Vec3(x, y, z));
        if (stack.is(ItemRegistryHandler.WOODEN_HAMMER) || stack.is(ItemRegistryHandler.CHISEL))
        {
            ModelStateDefinition definition = getComponentByPosition(inBlockVec);
            if (definition != null)
            {
                return breakComponent(definition, context.getPlayer());
            }
        }
        return false;
    }

    public boolean breakComponent(ModelStateDefinition definition, @Nullable Player player)
    {
        if (this.MODEL_STATES.contains(definition))
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
            this.MODEL_STATES.remove(definition);
            refresh();
            return true;
        }
        return false;
    }

    public void reloadShape()
    {
        VoxelShape shape = Shapes.empty();
        for (ModelStateDefinition definition : this.MODEL_STATES)
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
        for (ModelStateDefinition definition : this.MODEL_STATES)
        {
            this.occupationCache.addAll(definition.occupation());
        }
    }

    public boolean checkConnection()
    {
        boolean flag = false;
        for (int i = 0; i < this.MODEL_STATES.size(); i++)
        {
            ModelStateDefinition definition = this.MODEL_STATES.get(i);
            if (definition.component() instanceof Connectable comp)
            {
                this.MODEL_STATES.set(i, comp.applyConnection(this, definition));
                flag = true;
            }
        }
        if (flag) refresh();
        return flag;
    }

    public ModelStateDefinition getComponentByPosition(Vec3 vec3)
    {
        for (ModelStateDefinition m : this.MODEL_STATES)
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

    public Vec3 transformVec3(Vec3 vec3)
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
        for (ModelStateDefinition model : this.getModels())
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
        this.MODEL_STATES.clear();
        ListTag models = pTag.getList("models", 10);
        for (Tag tag : models)
        {
            this.MODEL_STATES.add(ModelStateDefinition.deserializeNBT((CompoundTag) tag));
        }
        refresh();
    }

    @Override
    protected void saveAdditional(CompoundTag pTag, HolderLookup.Provider pRegistries)
    {
        ListTag listTag = new ListTag();
        for (ModelStateDefinition definition : this.MODEL_STATES)
        {
            listTag.add(definition.serializeNBT());
        }
        pTag.put("models", listTag);
        super.saveAdditional(pTag, pRegistries);
    }

    @Override
    public List<ModelStateDefinition> getModels()
    {
        return this.MODEL_STATES;
    }
}
