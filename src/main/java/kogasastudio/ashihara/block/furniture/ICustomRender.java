package kogasastudio.ashihara.block.furniture;

import kogasastudio.ashihara.block.blockentity.MultiBuiltBlockEntity;
import kogasastudio.ashihara.block.building.component.ComponentStateDefinition;

public interface ICustomRender
{
    boolean doRender(MultiBuiltBlockEntity be, ComponentStateDefinition def);
}