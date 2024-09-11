package kogasastudio.ashihara.block.building.component;

import kogasastudio.ashihara.helper.ShapeHelper;
import kogasastudio.ashihara.registry.BuildingComponents;
import kogasastudio.ashihara.utils.BuildingComponentModelResourceLocation;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public record ComponentStateDefinition(BuildingComponent component, Vec3 inBlockPos, float rotation, VoxelShape shape, BuildingComponentModelResourceLocation model, List<Occupation> occupation)
{
    public CompoundTag serializeNBT()
    {
        CompoundTag tag = new CompoundTag();
        tag.putString("component", component().id);
        CompoundTag posTag = new CompoundTag();
        posTag.putDouble("x", inBlockPos().x());
        posTag.putDouble("y", inBlockPos().y());
        posTag.putDouble("z", inBlockPos().z());
        tag.put("inBlockPos", posTag);
        tag.putFloat("rotation", rotation());
        ShapeHelper.saveNBT(tag, shape());
        ListTag occupationTag = new ListTag();
        for (Occupation occupation : occupation())
        {
            CompoundTag occTag = new CompoundTag();
            occTag.putString("value", occupation.getId());
            occupationTag.add(occTag);
        }
        CompoundTag modelTag = new CompoundTag();
        modelTag.putString("id", model().id().toString());
        modelTag.putString("variant", model().variant());
        tag.put("model", modelTag);
        tag.put("occupation", occupationTag);
        return tag;
    }

    public static ComponentStateDefinition deserializeNBT(CompoundTag model)
    {
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
        return new ComponentStateDefinition(component, inBlockPos, rotation, shape, modelRL, occupations);
    }

    @Override
    public boolean equals(Object obj)
    {
        if (!(obj instanceof ComponentStateDefinition definition)) return false;
        boolean componentEqual = component().equals(definition.component());
        boolean rotationEqual = rotation() == definition.rotation();
        boolean shapeEqual = definition.shape().bounds().equals(shape().bounds());
        boolean occupationEqual = new HashSet<>(occupation()).containsAll(definition.occupation()) && new HashSet<>(definition.occupation()).containsAll(occupation());
        return componentEqual && rotationEqual && shapeEqual && occupationEqual;
    }
}
