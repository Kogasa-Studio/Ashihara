package kogasastudio.ashihara.block.building.component;

import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.entity.BlockEntity;

public interface Decoratable
{
    ComponentStateDefinition decorate(BlockEntity beIn, UseOnContext context, ComponentStateDefinition current);
}
