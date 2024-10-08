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
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

import static kogasastudio.ashihara.helper.PositionHelper.XTP;
import static kogasastudio.ashihara.helper.PositionHelper.coordsInRangeFixedY;

public class HangingFish extends AdditionalComponent implements Interactable
{
    private final BuildingComponentModelResourceLocation MODEL;

    private final VoxelShape SHAPE;
    private final Supplier<ItemStack> itemPredicate;
    private final Supplier<BuildingComponent> newComponent;
    private final VoxelShape newShape;
    private final BuildingComponentModelResourceLocation newModel;

    private final SoundType soundType;
    private SoundType interactSound;

    public HangingFish
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

    public HangingFish
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
        float r = direction.getAxis().equals(Direction.Axis.X) ? 90 : 0;

        Vec3 inBlockPos = beIn.transformVec3(beIn.inBlockVec(context.getClickLocation()));

        double y = inBlockPos.y();

        y = coordsInRangeFixedY(context.getClickedFace(), y, 0, XTP(8)) ? XTP(-8) : 0;

        Direction left = r == 0 ? Direction.WEST : Direction.NORTH;
        Direction right = left.getOpposite();

        List<Occupation> occupations = new ArrayList<>();
        occupations.addAll(Occupation.getEdged(left));
        occupations.addAll(Occupation.getEdged(right));
        occupations.addAll(Occupation.CENTER_ALL);

        VoxelShape shape = SHAPE;
        if (r == 90) shape = ShapeHelper.rotateShape(shape, 90);

        return new ComponentStateDefinition
        (
            BuildingComponents.get(this.id),
            new Vec3(0, y, 0),
            0, r, 0,
            shape,
            MODEL,
            occupations
        );
    }

    @Override
    public ComponentStateDefinition handleInteraction(UseOnContext context, ComponentStateDefinition definition)
    {
        if (this.itemPredicate != null && this.itemPredicate.get().is(context.getItemInHand().getItem()))
        {
            VoxelShape shape = newShape;
            if (definition.rotationY() == 90) shape = ShapeHelper.rotateShape(shape, 90);
            return new ComponentStateDefinition(this.newComponent.get(), definition.inBlockPos(), definition.rotationX(), definition.rotationY(), definition.rotationZ(), shape, this.newModel, definition.occupation());
        }
        return definition;
    }
}
