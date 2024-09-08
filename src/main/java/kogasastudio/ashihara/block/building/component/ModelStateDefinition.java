package kogasastudio.ashihara.block.building.component;

import kogasastudio.ashihara.utils.BuildingComponentModelResourceLocation;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.List;

public record ModelStateDefinition(BuildingComponent component, Vec3 inBlockPos, float rotation, VoxelShape shape, BuildingComponentModelResourceLocation model, List<Occupation> occupation)
{
}
