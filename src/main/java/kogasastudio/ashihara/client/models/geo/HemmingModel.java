package kogasastudio.ashihara.client.models.geo;

import net.minecraft.world.entity.player.Player;
import software.bernie.geckolib.cache.object.GeoBone;

public class HemmingModel extends SimpleInternalControlGeoModel
{
    public HemmingModel(String modelPrefix, String texturePrefix, Player player)
    {
        super(modelPrefix, texturePrefix, player);
    }

    public void syncFrame(float xStart, float xEnd, float yStart, float yEnd)
    {
        this.getBone("up_left").ifPresent(b -> {b.setPosX(-xEnd);b.setPosY(yEnd);});
        this.getBone("up_right").ifPresent(b -> {b.setPosX(-xStart);b.setPosY(yEnd);});
        this.getBone("down_right").ifPresent(b -> {b.setPosX(-xStart);b.setPosY(yStart);});
        this.getBone("down_left").ifPresent(b -> {b.setPosX(-xEnd);b.setPosY(yStart);});
    }

    public void syncMain(GeoBone from)
    {
        this.getBone("main").ifPresent
        (
            b ->
            {
                b.setPosX(from.getPosX());
                b.setPosY(from.getPosY());
                b.setPosZ(from.getPosZ());
                b.setRotX(from.getRotX());
                b.setRotY(from.getRotY());
                b.setRotZ(from.getRotZ());
                b.setScaleX(from.getScaleX());
                b.setScaleY(from.getScaleY());
                b.setScaleZ(from.getScaleZ());
            }
        );
    }
}
