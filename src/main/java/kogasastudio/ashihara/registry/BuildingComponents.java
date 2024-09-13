package kogasastudio.ashihara.registry;

import kogasastudio.ashihara.block.building.component.*;
import kogasastudio.ashihara.item.ItemRegistryHandler;
import net.minecraft.world.phys.shapes.Shapes;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static kogasastudio.ashihara.registry.AdditionalModels.*;

public class BuildingComponents
{
    public static final Map<String, Type> TYPE_MAP = new HashMap<>();
    public static final Map<String, BuildingComponent> COMPONENTS = new HashMap<>();

    public static final BuildingComponent RED_THICK_COLUMN = register
    (
        new Column
        (
            "red_thick_column",
            Type.BAKED_MODEL,
            List.of(ItemRegistryHandler.RED_THICK_COLUMN.toStack())
        )
    );
    public static final BuildingComponent RED_BEAM = register
    (
        new Beam
        (
            "red_beam",
            Type.BAKED_MODEL,
            AdditionalModels.RED_BEAM,
            RED_BEAM_L,
            RED_BEAM_R,
            RED_BEAM_A,
            List.of(ItemRegistryHandler.RED_BEAM.toStack())
        )
    );
    public static final BuildingComponent RED_HIJIKI = register
    (
        new Hijiki
        (
            "red_hijiki",
            Type.BAKED_MODEL,
            RED_HIJIKI_L,
            RED_HIJIKI_R,
            RED_HIJIKI_A,
            List.of(ItemRegistryHandler.RED_HIJIKI.toStack())
        )
    );
    public static final BuildingComponent RED_HIJIKI_SUPPORTER = register
    (
        new HijikiStandAlong
        (
            "red_hijiki_supporter",
            Type.BAKED_MODEL,
            AdditionalModels.RED_HIJIKI_SUPPORTER,
            Shapes.box(0.375, -0.03125, -0.25, 0.625, 0.25, 0.5),
            List.of(ItemRegistryHandler.RED_HIJIKI_SUPPORTER.toStack())
        )
    );
    public static final BuildingComponent RED_HIJIKI_CONNECTOR = register
    (
        new HijikiStandAlong
        (
            "red_hijiki_connector",
            Type.BAKED_MODEL,
            RED_HIJIKI_A,
            List.of(ItemRegistryHandler.RED_HIJIKI_CONNECTOR.toStack())
        )
    );
    public static final BuildingComponent RED_HIJIKI_LONG = register
    (
        new HijikiLong
        (
            "red_hijiki_long",
            Type.BAKED_MODEL,
            AdditionalModels.RED_HIJIKI_LONG,
            List.of(ItemRegistryHandler.RED_HIJIKI_LONG.toStack())
        )
    );
    public static final BuildingComponent RED_TOU = register
    (
        new Tou
        (
            "red_tou",
            Type.BAKED_MODEL,
            List.of(ItemRegistryHandler.RED_TOU.toStack())
        )
    );
    public static final BuildingComponent RED_BIG_TOU = register
    (
        new BigTou
        (
            "red_big_tou",
            Type.BAKED_MODEL,
            List.of(ItemRegistryHandler.RED_BIG_TOU.toStack())
        )
    );

    public static final AdditionalComponent BASE_STONE = (AdditionalComponent) register
    (
        new BaseStone
        (
            "base_stone",
            Type.BAKED_MODEL,
            List.of(ItemRegistryHandler.BASE_STONE.toStack())
        )
    );

    static
    {
        for (Type type : Type.values())
        {
            TYPE_MAP.put(type.name(), type);
        }
    }

    public static BuildingComponent get(String id)
    {
        return COMPONENTS.getOrDefault(id, BASE_STONE);
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
