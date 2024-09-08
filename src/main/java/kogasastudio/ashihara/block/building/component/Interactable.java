package kogasastudio.ashihara.block.building.component;

import net.minecraft.world.item.context.BlockPlaceContext;

public interface Interactable
{
    boolean handleInteraction(BlockPlaceContext context, ModelStateDefinition definition);
}
