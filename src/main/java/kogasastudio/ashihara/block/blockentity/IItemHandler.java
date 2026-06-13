package kogasastudio.ashihara.block.blockentity;

import net.minecraft.core.Direction;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.item.ItemResource;

public interface IItemHandler<T extends BlockEntity>
{
    ResourceHandler<ItemResource> getItemResource(T be, Direction direction);
}
