package kogasastudio.ashihara.registry;

import kogasastudio.ashihara.block.furniture.FurnitureComponent;
import kogasastudio.ashihara.block.furniture.FurnitureRenderPass;
import kogasastudio.ashihara.block.furniture.BambooCurtainComponent;
import kogasastudio.ashihara.block.furniture.CurtainTableComponent;
import kogasastudio.ashihara.block.furniture.FurnitureProxyComponent;
import kogasastudio.ashihara.block.furniture.SimpleContainerComponent;
import kogasastudio.ashihara.block.furniture.ContainerState;
import net.minecraft.world.phys.shapes.Shapes;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FurnitureComponents
{
    public static final Map<String, FurnitureComponent> COMPONENTS = new HashMap<>();

    public static final FurnitureComponent WOODEN_BOWL_MID = register
    (
        new SimpleContainerComponent
        (
        "wooden_bowl_mid",
        BuildingComponents.Type.BAKED_MODEL,
        AdditionalModels.WOODEN_BOWL_MID,
        Shapes.box(0.34375, 0, 0.34375, 0.65625, 0.21875, 0.65625),
        Blocks.MULTI_BUILT_BLOCK,
        List.of(Items.WOODEN_BOWL_MID.toStack()),
        FurnitureRenderPass.CHUNK_BUFFER,
        ContainerState.ContainerType.BOWL, ContainerState.ContainerSize.MID,
        1, 3
        )
    );

    public static final FurnitureComponent WOODEN_BOWL_BIG = register
    (
        new SimpleContainerComponent
        (
            "wooden_bowl_big",
            BuildingComponents.Type.BAKED_MODEL,
            AdditionalModels.WOODEN_BOWL_BIG,
            Shapes.box(0.28125, 0, 0.28125, 0.71875, 0.25, 0.71875),
            Blocks.MULTI_BUILT_BLOCK,
            List.of(Items.WOODEN_BOWL_BIG.toStack()),
            FurnitureRenderPass.CHUNK_BUFFER,
            ContainerState.ContainerType.BOWL, ContainerState.ContainerSize.LARGE,
            2, 6
        )
    );

    public static final FurnitureComponent WOODEN_DISH_SMALL = register
    (
        new SimpleContainerComponent
        (
            "wooden_dish_small",
            BuildingComponents.Type.BAKED_MODEL,
            AdditionalModels.WOODEN_DISH_SMALL,
            Shapes.box(0.3125, 0, 0.3125, 0.6875, 0.078125, 0.6875),
            Blocks.MULTI_BUILT_BLOCK,
            List.of(Items.WOODEN_DISH_SMALL.toStack()),
            FurnitureRenderPass.CHUNK_BUFFER,
            ContainerState.ContainerType.PLATE, ContainerState.ContainerSize.SMALL,
            1, 2
        )
    );

    public static final FurnitureComponent WOODEN_DISH_MID = register
    (
        new SimpleContainerComponent
        (
            "wooden_dish_mid",
            BuildingComponents.Type.BAKED_MODEL,
            AdditionalModels.WOODEN_DISH_MID,
            Shapes.box(0.28125, 0, 0.28125, 0.71875, 0.09375, 0.71875),
            Blocks.MULTI_BUILT_BLOCK,
            List.of(Items.WOODEN_DISH_MID.toStack()),
            FurnitureRenderPass.CHUNK_BUFFER,
            ContainerState.ContainerType.PLATE, ContainerState.ContainerSize.MID,
            2, 5
        )
    );

    public static final FurnitureComponent WOODEN_DISH_BIG = register
    (
        new SimpleContainerComponent
        (
            "wooden_dish_big",
            BuildingComponents.Type.BAKED_MODEL,
            AdditionalModels.WOODEN_DISH_BIG,
            Shapes.box(0.21875, 0, 0.21875, 0.78125, 0.125, 0.78125),
            Blocks.MULTI_BUILT_BLOCK,
            List.of(Items.WOODEN_DISH_BIG.toStack()),
            FurnitureRenderPass.CHUNK_BUFFER,
            ContainerState.ContainerType.PLATE, ContainerState.ContainerSize.LARGE,
            4, 12
        )
    );

    public static final FurnitureComponent BAMBOO_CURTAIN = register
    (
        new BambooCurtainComponent
        (
            "bamboo_curtain",
            BuildingComponents.Type.BAKED_MODEL,
            Blocks.BAMBOO_BONES_COMPONENT,
            List.of(Items.BAMBOO_CURTAIN.toStack()),
            FurnitureRenderPass.CHUNK_BUFFER
        )
    );

    public static final FurnitureComponent FURNITURE_PROXY = register(new FurnitureProxyComponent());

    public static final FurnitureComponent CURTAIN_TABLE = register
    (
        new CurtainTableComponent
        (
            "curtain_table",
            BuildingComponents.Type.BAKED_MODEL,
            AdditionalModels.CURTAIN_TABLE,
            Blocks.SPRUCE_WOOD_COMPONENT,
            List.of(Items.CURTAIN_TABLE.toStack()),
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
