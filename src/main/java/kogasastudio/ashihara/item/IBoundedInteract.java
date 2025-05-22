package kogasastudio.ashihara.item;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.AABB;

public interface IBoundedInteract
{
    AABB getBoundingBox(Entity entity);
}
