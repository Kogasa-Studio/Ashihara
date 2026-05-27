package kogasastudio.ashihara.client.models.geo;

import net.minecraft.world.entity.Entity;

public interface IToast
{
    void intro(Entity entity);

    void outro(Entity entity);

    void init(Entity entity, boolean instant);
}
