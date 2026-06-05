package kogasastudio.ashihara.block.building.component;

import kogasastudio.ashihara.block.furniture.ICustomData;
import kogasastudio.ashihara.helper.ShapeHelper;
import kogasastudio.ashihara.registry.AdditionalModels;
import kogasastudio.ashihara.registry.BuildingComponents;
import kogasastudio.ashihara.registry.FurnitureComponents;
import kogasastudio.ashihara.utils.BuildingComponentModelResourceLocation;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public record ComponentStateDefinition(
    BuildingComponent component, Vec3 inBlockPos,
    float rotationX, float rotationY, float rotationZ,
    VoxelShape shape, BuildingComponentModelResourceLocation model,
    List<Occupation> occupation,
    @Nullable Object customData
)
{
    /** Convenience constructor — customData defaults to null. */
    public ComponentStateDefinition(
        BuildingComponent component, Vec3 inBlockPos,
        float rotationX, float rotationY, float rotationZ,
        VoxelShape shape, BuildingComponentModelResourceLocation model,
        List<Occupation> occupation)
    {
        this(component, inBlockPos, rotationX, rotationY, rotationZ, shape, model, occupation, null);
    }

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
        if (component() instanceof ICustomData cd && customData() != null)
        {
            cd.serializeCustom(output.child("custom"), customData());
        }
        ValueOutput modelTag = output.child("model");
        modelTag.putString("id", model().id().toString());
        modelTag.putString("variant", model().variant());
        return output;
    }

    public static ComponentStateDefinition deserializeNBT(ValueInput input)
    {
        String componentId = input.getStringOr("component", "");
        BuildingComponent component = BuildingComponents.COMPONENTS.get(componentId);
        if (component == null) component = FurnitureComponents.COMPONENTS.get(componentId);
        if (component == null)
            throw new RuntimeException("Error loading component: Component \"" + componentId + "\" does not exist!");
        ValueInput posTag = input.childOrEmpty("inBlockPos");
        Vec3 inBlockPos = new Vec3(
            posTag.getDoubleOr("x", 0), posTag.getDoubleOr("y", 0), posTag.getDoubleOr("z", 0));
        float rotationX = input.getFloatOr("rotationX", 0);
        float rotationY = input.getFloatOr("rotationY", 0);
        float rotationZ = input.getFloatOr("rotationZ", 0);
        VoxelShape shape = ShapeHelper.readNBT(input);
        List<Occupation> occupations = new ArrayList<>();
        Object customObj = null;
        ValueInput.ValueInputList occList = input.childrenListOrEmpty("occupation");
        for (ValueInput occTag : occList) occupations.add(Occupation.OCCUPATION_MAP.get(occTag.getStringOr("value", "")));
        if (component instanceof ICustomData cd) customObj = cd.deserializeCustom(input.childOrEmpty("custom"));
        ValueInput modelTag = input.childOrEmpty("model");
        BuildingComponentModelResourceLocation modelRL = AdditionalModels.get(Identifier.parse(modelTag.getStringOr("id", "")));
        return new ComponentStateDefinition(component, inBlockPos, rotationX, rotationY, rotationZ, shape, modelRL, occupations, customObj);
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