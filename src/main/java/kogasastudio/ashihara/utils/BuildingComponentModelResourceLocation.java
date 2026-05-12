package kogasastudio.ashihara.utils;

import net.minecraft.resources.Identifier;

public class BuildingComponentModelResourceLocation
{
    public final Identifier id;
    public final String variant;

    public BuildingComponentModelResourceLocation(Identifier id, String variant)
    {
        this.id = id;
        this.variant = variant;
    }

    public Identifier id() {return id;}

    public String variant() {return variant;}
}
