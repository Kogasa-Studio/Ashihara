package kogasastudio.ashihara.registry;

import kogasastudio.ashihara.block.building.component.Beam;
import kogasastudio.ashihara.block.building.component.BuildingComponent;
import kogasastudio.ashihara.block.building.component.Column;
import kogasastudio.ashihara.item.ItemRegistryHandler;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static kogasastudio.ashihara.registry.AdditionalModels.*;

public class BuildingComponents
{
    public static final Map<String, Type> TYPE_MAP = new HashMap<>();
    public static final Map<String, BuildingComponent> COMPONENTS = new HashMap<>();

    public static final BuildingComponent RED_THICK_COLUMN = register(new Column("red_thick_column", Type.BAKED_MODEL, List.of(ItemRegistryHandler.RED_THICK_COLUMN.toStack())));

    public static final BuildingComponent RED_BEAM = register(new Beam("red_beam", Type.BAKED_MODEL, AdditionalModels.RED_BEAM, RED_BEAM_L, RED_BEAM_R, RED_BEAM_A, List.of(ItemRegistryHandler.RED_THIN_BEAM.toStack())));

    static
    {
        for (Type type : Type.values())
        {
            TYPE_MAP.put(type.name(), type);
        }
    }

    private static BuildingComponent register(BuildingComponent component)
    {
        COMPONENTS.put(component.id, component);
        return component;
    }

    public enum Type
    {
        BAKED_MODEL("baked_model"),
        ENTITY_MODEL("entity_model"),
        ;

        public final String name;

        Type(String nameIn)
        {
            this.name = nameIn;
        }
    }
}
