package kogasastudio.ashihara.block.building.component;

import kogasastudio.ashihara.block.blockentity.MultiBuiltBlockEntity;

public interface Connectable
{
    ComponentStateDefinition applyConnection(MultiBuiltBlockEntity be, ComponentStateDefinition definition);
}
