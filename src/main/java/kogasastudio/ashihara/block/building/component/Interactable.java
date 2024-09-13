package kogasastudio.ashihara.block.building.component;

import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.SoundType;

public interface Interactable
{
    ComponentStateDefinition handleInteraction(UseOnContext context, ComponentStateDefinition definition);

    default SoundType getInteractSound()
    {
        return SoundType.EMPTY;
    }
}
