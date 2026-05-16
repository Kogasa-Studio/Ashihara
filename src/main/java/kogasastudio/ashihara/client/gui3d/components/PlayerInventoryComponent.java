package kogasastudio.ashihara.client.gui3d.components;

import kogasastudio.ashihara.client.models.geo.SimpleInternalControlGeoModel;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;
import org.joml.Matrix4f;

public class PlayerInventoryComponent extends ModelComponent
{
    public Inventory inventory;
    public PlayerInventoryComponent(SimpleInternalControlGeoModel model, Inventory inventory, Matrix4f presetTransform)
    {
        super(model, true, presetTransform);
        this.inventory = inventory;
    }

    @Override
    public void init()
    {
        super.init();
        for (int i = 0; i < this.inventory.getContainerSize(); i++)
        {
            if (this.inventory.getSlot(i) == null) continue;
            this.addChild(new ItemSlotComponent(
                    this.model,
                    "item_slot_" + i,
                    new Slot(this.inventory, i, 0, 0)
            ));
        }
    }
}
