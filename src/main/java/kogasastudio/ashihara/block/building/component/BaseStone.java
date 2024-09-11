package kogasastudio.ashihara.block.building.component;

import kogasastudio.ashihara.Ashihara;
import kogasastudio.ashihara.block.tileentities.MultiBuiltBlockEntity;
import kogasastudio.ashihara.registry.AdditionalModels;
import kogasastudio.ashihara.registry.BuildingComponents;
import kogasastudio.ashihara.utils.BuildingComponentModelResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.Shapes;

import java.util.List;

public class BaseStone extends AdditionalComponent
{
    public BaseStone(String idIn, BuildingComponents.Type typeIn, List<ItemStack> dropsIn)
    {
        super(idIn, typeIn, dropsIn);
    }

    @Override
    public ComponentStateDefinition definite(MultiBuiltBlockEntity beIn, UseOnContext context)
    {
        RandomSource random = Ashihara.getRandom();
        int i = random.nextInt(3);
        BuildingComponentModelResourceLocation location = switch (i)
        {
            case 1 -> AdditionalModels.BASE_STONE_1;
            case 2 -> AdditionalModels.BASE_STONE_2;
            default -> AdditionalModels.BASE_STONE_0;
        };

        return new ComponentStateDefinition
        (
            BuildingComponents.BASE_STONE,
            new Vec3(0, 0, 0),
            0,
            Shapes.box(0.0625, -0.1875, 0.0625, 0.9375, 0.3125, 0.9375),
            location,
            List.of(Occupation.F1_C)
        );
    }

    @Override
    public SoundType getSoundType()
    {
        return SoundType.STONE;
    }
}
