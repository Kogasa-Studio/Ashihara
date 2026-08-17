package kogasastudio.ashihara.client.render.geo;

import com.geckolib.renderer.GeoArmorRenderer;
import kogasastudio.ashihara.client.models.geo.HagoromoModel;
import kogasastudio.ashihara.item.armor.HagoromoItem;
import net.minecraft.client.renderer.entity.state.HumanoidRenderState;
import net.minecraft.world.entity.EquipmentSlot;

import java.util.List;

public class HagoromoRenderer extends GeoArmorRenderer<HagoromoItem, HumanoidRenderState>
{
    public HagoromoRenderer()
    {
        super(new HagoromoModel());
    }

    @Override
    public List<ArmorSegment> getSegmentsForSlot(HumanoidRenderState state, EquipmentSlot slot)
    {
        return switch (slot)
        {
            case CHEST -> List.of(ArmorSegment.CHEST, ArmorSegment.RIGHT_ARM, ArmorSegment.LEFT_ARM);
            default -> List.of();
        };
    }

    @Override
    public String getBoneNameForSegment(HumanoidRenderState state, ArmorSegment segment)
    {
        return switch (segment)
        {
            case CHEST -> "armorBody";
            case RIGHT_ARM -> "armorRightArm";
            case LEFT_ARM -> "armorLeftArm";
            default -> "";
        };
    }
}