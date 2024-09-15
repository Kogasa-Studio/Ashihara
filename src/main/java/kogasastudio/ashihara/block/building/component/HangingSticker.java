package kogasastudio.ashihara.block.building.component;

import kogasastudio.ashihara.block.tileentities.MultiBuiltBlockEntity;
import kogasastudio.ashihara.helper.ShapeHelper;
import kogasastudio.ashihara.registry.BuildingComponents;
import kogasastudio.ashihara.utils.BuildingComponentModelResourceLocation;
import net.minecraft.core.Direction;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.List;

import static kogasastudio.ashihara.helper.PositionHelper.XTP;
import static kogasastudio.ashihara.helper.PositionHelper.coordsInRangeFixed;

public class HangingSticker extends AdditionalComponent implements Interactable
{
    private VoxelShape SHAPE;
    private final BuildingComponentModelResourceLocation model;
    private final BuildingComponentModelResourceLocation endModel;

    public HangingSticker(String idIn, BuildingComponents.Type typeIn, BuildingComponentModelResourceLocation modelIn, BuildingComponentModelResourceLocation endModelIn, List<ItemStack> dropsIn)
    {
        super(idIn, typeIn, dropsIn);
        this.model = modelIn;
        this.endModel = endModelIn;
        initShape();
    }

    protected void initShape()
    {
        this.SHAPE = Shapes.or
        (
            Shapes.box(0.34375, 0, -0.125, 0.65625, 0.5, 1.125),
            Shapes.box(0.34375, 0.5, 0.4375, 0.65625, 0.75, 1.125),
            Shapes.box(0.34375, -0.25, -0.125, 0.65625, 0, 0.5625)
        );
    }

    @Override
    public ComponentStateDefinition definite(MultiBuiltBlockEntity beIn, UseOnContext context)
    {
        Vec3 inBlockPos = beIn.transformVec3(beIn.inBlockVec(context.getClickLocation()));

        Direction direction = context.getHorizontalDirection();
        direction = beIn.fromAbsolute(direction);
        float r = switch (direction)
        {
            case WEST -> 270;
            case SOUTH -> 0;
            case EAST -> 90;
            default -> 180;
        };

        double y = inBlockPos.y();

        y = coordsInRangeFixed(context.getClickedFace(), y, 0, XTP(8)) ? 0 : XTP(8);

        int floor = y == 0 ? 0 : 2;

        VoxelShape shape = SHAPE;
        shape = ShapeHelper.rotateShape(shape, -r);
        shape = ShapeHelper.offsetShape(shape, 0, y, 0);

        return new ComponentStateDefinition
        (
            BuildingComponents.get(this.id),
            new Vec3(0, y + XTP(4), 0),
            -26.5651f, r, 0,
            shape,
            this.model,
            List.of(Occupation.CENTER_ALL.get(floor))
        );
    }

    @Override
    public ComponentStateDefinition handleInteraction(UseOnContext context, ComponentStateDefinition definition)
    {
        if (context.getItemInHand().getItem() instanceof AxeItem)
        {
            BuildingComponentModelResourceLocation newModel = definition.model() == this.model ? this.endModel : this.model;
            return new ComponentStateDefinition(definition.component(), definition.inBlockPos(), definition.rotationX(), definition.rotationY(), definition.rotationZ(), definition.shape(), newModel, definition.occupation());
        }
        return definition;
    }
}
