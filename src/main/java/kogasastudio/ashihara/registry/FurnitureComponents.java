package kogasastudio.ashihara.registry;

import kogasastudio.ashihara.block.furniture.FurnitureComponent;
import kogasastudio.ashihara.block.furniture.FurnitureRenderPass;
import kogasastudio.ashihara.block.furniture.WoodenBowlComponent;
import net.minecraft.world.phys.shapes.Shapes;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FurnitureComponents
{
    public static final Map<String, FurnitureComponent> COMPONENTS = new HashMap<>();

    public static final FurnitureComponent WOODEN_BOWL_MID = register
    (
        new WoodenBowlComponent
        (
        "wooden_bowl_mid",
        BuildingComponents.Type.BAKED_MODEL,
        AdditionalModels.WOODEN_BOWL_MID,
        Shapes.box(0.34375, 0, 0.34375, 0.65625, 0.21875, 0.65625),
        () -> Blocks.MULTI_BUILT_BLOCK.get(),
        List.of(Items.WOODEN_BOWL_MID.toStack()),
        FurnitureRenderPass.CHUNK_BUFFER
        )
    );

    public static FurnitureComponent get(String id)
    {
        return COMPONENTS.getOrDefault(id, WOODEN_BOWL_MID);
    }

    private static FurnitureComponent register(FurnitureComponent component)
    {
        COMPONENTS.put(component.id, component);
        return component;
    }
}
