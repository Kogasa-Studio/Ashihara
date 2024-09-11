package kogasastudio.ashihara.block.building.component;

import kogasastudio.ashihara.block.tileentities.MultiBuiltBlockEntity;

public interface Connectable
{
    ComponentStateDefinition applyConnection(MultiBuiltBlockEntity be, ComponentStateDefinition definition);
}
