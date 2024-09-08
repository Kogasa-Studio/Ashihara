package kogasastudio.ashihara.utils;

import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.resources.ResourceLocation;

public record BuildingComponentModelResourceLocation(ResourceLocation id, String variant)
{
    public ModelResourceLocation toModelResourceLocation()
    {
        return new ModelResourceLocation(this.id(), this.variant());
    }

    public static BuildingComponentModelResourceLocation fromModelResourceLocation(ModelResourceLocation modelResourceLocation)
    {
        return new BuildingComponentModelResourceLocation(modelResourceLocation.id(), modelResourceLocation.variant());
    }
}
