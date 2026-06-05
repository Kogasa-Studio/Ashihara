package kogasastudio.ashihara.client.render.ber.dispatch;

import kogasastudio.ashihara.block.building.component.ComponentStateDefinition;
import kogasastudio.ashihara.block.blockentity.MultiBuiltBlockEntity;
import kogasastudio.ashihara.block.furniture.FurnitureComponent;
import kogasastudio.ashihara.block.furniture.ICustomRender;
import kogasastudio.ashihara.client.render.state.FurnitureRenderState;

@FunctionalInterface
public interface IFurnitureRenderer<T extends FurnitureComponent & ICustomRender>
{
    FurnitureRenderState collect(T component, ComponentStateDefinition def, MultiBuiltBlockEntity be);
}