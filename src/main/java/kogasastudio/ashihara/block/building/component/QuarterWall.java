package kogasastudio.ashihara.block.building.component;

import kogasastudio.ashihara.block.building.BaseMultiBuiltBlock;
import kogasastudio.ashihara.block.tileentities.MultiBuiltBlockEntity;
import kogasastudio.ashihara.helper.ShapeHelper;
import kogasastudio.ashihara.registry.BuildingComponents;
import kogasastudio.ashihara.utils.BuildingComponentModelResourceLocation;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;

import javax.annotation.Nullable;
import java.util.List;
import java.util.function.Supplier;

import static kogasastudio.ashihara.helper.PositionHelper.XTP;

public class QuarterWall extends AdditionalComponent implements Interactable
{
    private final BuildingComponentModelResourceLocation MODEL;

    private final VoxelShape SHAPE;
    private final Supplier<ItemStack> itemPredicate;
    private final Supplier<BuildingComponent> newComponent;
    private final VoxelShape newShape;
    private final BuildingComponentModelResourceLocation newModel;

    private final SoundType soundType;
    private SoundType interactSound;

    public QuarterWall
    (
        String idIn,
        BuildingComponents.Type typeIn,
        BuildingComponentModelResourceLocation modelIn,
        VoxelShape shapeIn,
        Supplier<BaseMultiBuiltBlock> materialIn,
        List<ItemStack> dropsIn,
        @Nullable Supplier<ItemStack> itemPredicateIn,
        @Nullable Supplier<BuildingComponent> newComponentIn,
        @Nullable VoxelShape newShapeIn,
        @Nullable BuildingComponentModelResourceLocation newModelIn,
        SoundType soundType
    )
    {
        super(idIn, typeIn, materialIn, dropsIn);
        this.MODEL = modelIn;
        this.SHAPE = shapeIn;
        this.itemPredicate = itemPredicateIn;
        this.newComponent = newComponentIn;
        this.newShape = newShapeIn;
        this.newModel = newModelIn;
        this.soundType = soundType;
    }

    public QuarterWall
    (
        String idIn,
        BuildingComponents.Type typeIn,
        BuildingComponentModelResourceLocation modelIn,
        VoxelShape shapeIn,
        Supplier<BaseMultiBuiltBlock> materialIn,
        List<ItemStack> dropsIn,
        @Nullable Supplier<ItemStack> itemPredicateIn,
        @Nullable Supplier<BuildingComponent> newComponentIn,
        @Nullable VoxelShape newShapeIn,
        @Nullable BuildingComponentModelResourceLocation newModelIn,
        SoundType soundType,
        SoundType interactSound
    )
    {
        this(idIn, typeIn, modelIn, shapeIn, materialIn, dropsIn, itemPredicateIn, newComponentIn, newShapeIn, newModelIn, soundType);
        this.interactSound = interactSound;
    }

    @Override
    public SoundType getSoundType()
    {
        return this.soundType;
    }

    @Override
    public SoundType getInteractSound()
    {
        return this.interactSound == null ? super.getSoundType() : interactSound;
    }

    @Override
    public ComponentStateDefinition definite(MultiBuiltBlockEntity beIn, UseOnContext context)
    {
        Direction direction = context.getHorizontalDirection();
        direction = beIn.fromAbsolute(direction);

        Vec3 inBlockPos = beIn.transformVec3(beIn.inBlockVec(context.getClickLocation()));
        double y = inBlockPos.y();

        if (context.getClickedFace().equals(Direction.UP))
        {
            y = (y >= 0 && y < XTP(8)) ? 0 : XTP(8);
        }
        else y = (y > 0 && y <= XTP(8)) ? 0 : XTP(8);

        Direction occDir;
        double x;
        double z;
        float r;
        if (direction.getAxis().equals(Direction.Axis.Z))
        {
            x = inBlockPos.x() <= XTP(8) ? XTP(-4) : XTP(4);
            occDir = inBlockPos.x() <= XTP(8) ? Direction.WEST : Direction.EAST;
            z = 0;
            r = 0;
        }
        else
        {
            z = inBlockPos.z() <= XTP(8) ? XTP(-4) : XTP(4);
            occDir = inBlockPos.z() <= XTP(8) ? Direction.NORTH : Direction.SOUTH;
            x = 0;
            r = 90;
        }

        int floor = y == 0 ? 0 : 2;

        Occupation occupation = Occupation.getEdged(occDir).get(floor);

        VoxelShape shape = SHAPE;
        shape = ShapeHelper.rotateShape(shape, r);
        shape = ShapeHelper.offsetShape(shape, x, y, z);

        return new ComponentStateDefinition
        (
            BuildingComponents.get(this.id),
            new Vec3(x, y, z),
            0, r, 0,
            shape,
            MODEL,
            List.of(occupation)
        );
    }

    @Override
    public ComponentStateDefinition handleInteraction(UseOnContext context, ComponentStateDefinition definition)
    {
        if (this.itemPredicate != null && this.itemPredicate.get().is(context.getItemInHand().getItem()))
        {
            VoxelShape shape = newShape;
            if (definition.rotationY() == 90) shape = ShapeHelper.rotateShape(shape, 90);
            Vec3 vec = definition.inBlockPos();
            shape = ShapeHelper.offsetShape(shape, vec.x(), vec.y(), vec.z());
            return new ComponentStateDefinition(this.newComponent.get(), definition.inBlockPos(), definition.rotationX(), definition.rotationY(), definition.rotationZ(), shape, this.newModel, definition.occupation());
        }
        return definition;
    }
}
