package kogasastudio.ashihara.block.building;

import kogasastudio.ashihara.block.building.component.ComponentStateDefinition;

import java.util.List;

public interface IMultiBuiltBlock
{
    List<ComponentStateDefinition> getComponents(int opcode);
}
