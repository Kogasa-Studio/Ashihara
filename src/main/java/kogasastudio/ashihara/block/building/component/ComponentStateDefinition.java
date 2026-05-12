package kogasastudio.ashihara.block.building.component;

import kogasastudio.ashihara.helper.ShapeHelper;
import kogasastudio.ashihara.registry.AdditionalModels;
import kogasastudio.ashihara.registry.BuildingComponents;
import kogasastudio.ashihara.utils.BuildingComponentModelResourceLocation;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.ArrayList;
import java.util.List;

public record ComponentStateDefinition(BuildingComponent component, Vec3 inBlockPos, float rotationX, float rotationY, float rotationZ, VoxelShape shape, BuildingComponentModelResourceLocation model, List<Occupation> occupation)
{
    public ValueOutput serialize(ValueOutput output)
    {
        output.putString("component", component().id);
        ValueOutput posTag = output.child("inBlockPos");
        posTag.putDouble("x", inBlockPos().x());
        posTag.putDouble("y", inBlockPos().y());
        posTag.putDouble("z", inBlockPos().z());
        output.putFloat("rotationX", rotationX());
        output.putFloat("rotationY", rotationY());
        output.putFloat("rotationZ", rotationZ());
        ShapeHelper.saveNBT(output, shape());
        ValueOutput.ValueOutputList occupationTag = output.childrenList("occupation");
        for (Occupation occupation : occupation())
        {
            ValueOutput occTag = occupationTag.addChild();
            occTag.putString("value", occupation.getId());
        }
        ValueOutput modelTag = output.child("model");
        modelTag.putString("id", model().id().toString());
        modelTag.putString("variant", model().variant());
        return output;
    }

    public static ComponentStateDefinition deserializeNBT(ValueInput input)
    {
        BuildingComponent component = BuildingComponents.COMPONENTS.getOrDefault(input.getStringOr("component", ""), null);
        if (component == null) throw new RuntimeException("Error loading component: Component \"" + input.getString("component") + "\" does not exist!");
        ValueInput posTag = input.childOrEmpty("inBlockPos");
        Vec3 inBlockPos = new Vec3(posTag.getDoubleOr("x", 0), posTag.getDoubleOr("y", 0), posTag.getDoubleOr("z", 0));
        float rotationX = input.getFloatOr("rotationX", 0);
        float rotationY = input.getFloatOr("rotationY", 0);
        float rotationZ = input.getFloatOr("rotationZ", 0);
        VoxelShape shape = ShapeHelper.readNBT(input);
        ValueInput modelTag = input.childOrEmpty("model");
        BuildingComponentModelResourceLocation modelRL = AdditionalModels.get(Identifier.parse(modelTag.getStringOr("id", "")));
        List<Occupation> occupations = new ArrayList<>();
        ValueInput.ValueInputList occupation = input.childrenListOrEmpty("occupation");
        for (ValueInput occupationTag : occupation)
        {
            occupations.add(Occupation.OCCUPATION_MAP.get(occupationTag.getStringOr("value", "")));
        }
        return new ComponentStateDefinition(component, inBlockPos, rotationX, rotationY, rotationZ, shape, modelRL, occupations);
    }

    @Override
    public boolean equals(Object obj)
    {
        if (!(obj instanceof ComponentStateDefinition definition)) return false;
        boolean componentEqual = component().equals(definition.component());
        boolean rotationEqual = rotationY() == definition.rotationY();
        boolean shapeEqual = definition.shape().bounds().equals(shape().bounds());
        boolean occupationEqual = occupation().hashCode() == definition.occupation().hashCode();
        return componentEqual && rotationEqual && shapeEqual && occupationEqual;
    }
}
