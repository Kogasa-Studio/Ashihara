package kogasastudio.ashihara.block.tileentities;

import kogasastudio.ashihara.block.building.*;
import kogasastudio.ashihara.block.building.component.BuildingComponent;
import kogasastudio.ashihara.block.building.component.Connectable;
import kogasastudio.ashihara.block.building.component.ModelStateDefinition;
import kogasastudio.ashihara.block.building.component.Occupation;
import kogasastudio.ashihara.helper.MathHelper;
import kogasastudio.ashihara.helper.ShapeHelper;
import kogasastudio.ashihara.registry.BuildingComponents;
import kogasastudio.ashihara.utils.BuildingComponentModelResourceLocation;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

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

    public ModelStateDefinition getComponentByOccupation(Occupation occupation)
    {
        for (ModelStateDefinition m : this.MODEL_STATES)
        {
            if (m.occupation().contains(occupation)) return m;
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
        float rotation = switch (this.getBlockState().getValue(FACING))
        {
            case WEST -> 90;
            case SOUTH -> 180;
            case EAST -> 270;
            default -> 0;
        };
        double[] transformed = MathHelper.rotatePoint(vec3.x(), vec3.z(), 0.5, 0.5, rotation);
        return new Vec3(transformed[0], vec3.y(), transformed[1]);
    }

    public void refresh()
    {
        reloadShape();
        reloadOccupation();
        setChanged();
        this.getLevel().sendBlockUpdated(this.getBlockPos(), this.getBlockState(), this.getBlockState(), 3);
    }

    public VoxelShape getShape()
    {
        return shapeCache;
    }

    @Override
    protected void loadAdditional(CompoundTag pTag, HolderLookup.Provider pRegistries)
    {
        super.loadAdditional(pTag, pRegistries);
        this.MODEL_STATES.clear();
        ListTag models = pTag.getList("models", 10);
        for (Tag tag : models)
        {
            CompoundTag model = (CompoundTag) tag;
            BuildingComponent component = BuildingComponents.COMPONENTS.getOrDefault(model.getString("component"), null);
            if (component == null) throw new RuntimeException("Error loading component: Component \"" + model.getString("component") + "\" does not exist!");
            CompoundTag posTag = model.getCompound("inBlockPos");
            Vec3 inBlockPos = new Vec3(posTag.getDouble("x"), posTag.getDouble("y"), posTag.getDouble("z"));
            float rotation = model.getFloat("rotation");
            VoxelShape shape = ShapeHelper.readNBT(model);
            CompoundTag modelTag = model.getCompound("model");
            BuildingComponentModelResourceLocation modelRL = new BuildingComponentModelResourceLocation(ResourceLocation.parse(modelTag.getString("id")), modelTag.getString("variant"));
            List<Occupation> occupations = new ArrayList<>();
            ListTag occupation = model.getList("occupation", 10);
            for (Tag occupationTag : occupation)
            {
                CompoundTag occTag = (CompoundTag) occupationTag;
                occupations.add(Occupation.OCCUPATION_MAP.get(occTag.getString("value")));
            }
            this.MODEL_STATES.add(new ModelStateDefinition(component, inBlockPos, rotation, shape, modelRL, occupations));
        }
        reloadShape();
        reloadOccupation();
        setChanged();
        if (this.hasLevel()) this.getLevel().sendBlockUpdated(this.getBlockPos(), this.getBlockState(), this.getBlockState(), 3);
    }

    @Override
    protected void saveAdditional(CompoundTag pTag, HolderLookup.Provider pRegistries)
    {
        ListTag listTag = new ListTag();
        for (ModelStateDefinition definition : this.MODEL_STATES)
        {
            CompoundTag tag = new CompoundTag();
            tag.putString("component", definition.component().id);
            CompoundTag posTag = new CompoundTag();
            posTag.putDouble("x", definition.inBlockPos().x());
            posTag.putDouble("y", definition.inBlockPos().y());
            posTag.putDouble("z", definition.inBlockPos().z());
            tag.put("inBlockPos", posTag);
            tag.putFloat("rotation", definition.rotation());
            ShapeHelper.saveNBT(tag, definition.shape());
            ListTag occupationTag = new ListTag();
            for (Occupation occupation : definition.occupation())
            {
                CompoundTag occTag = new CompoundTag();
                occTag.putString("value", occupation.getId());
                occupationTag.add(occTag);
            }
            CompoundTag modelTag = new CompoundTag();
            modelTag.putString("id", definition.model().id().toString());
            modelTag.putString("variant", definition.model().variant());
            tag.put("model", modelTag);
            tag.put("occupation", occupationTag);
            listTag.add(tag);
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
