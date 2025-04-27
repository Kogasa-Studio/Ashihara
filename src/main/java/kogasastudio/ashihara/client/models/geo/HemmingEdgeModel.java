package kogasastudio.ashihara.client.models.geo;

import net.minecraft.world.entity.player.Player;
import software.bernie.geckolib.cache.object.GeoBone;

public class HemmingEdgeModel extends SimpleInternalControlGeoModel
{
    public HemmingEdgeModel(String modelPrefix, String texturePrefix, String animationsPrefix, Player player)
    {
        super(modelPrefix, texturePrefix, animationsPrefix, player);
    }

    public void syncFrame(float xStart, float xEnd, float yStart, float yEnd, int divides)
    {
        this.getBone("up").ifPresent(b -> {b.setPosX(-(xEnd - xStart) / divides);b.setPosY(yEnd);});
        this.getBone("right").ifPresent(b -> {b.setPosX(-xStart);b.setPosY(yEnd);});
        this.getBone("down").ifPresent(b -> {b.setPosX(-xStart);b.setPosY(yStart);});
        this.getBone("left").ifPresent(b -> {b.setPosX(-xEnd);b.setPosY(yStart);});
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
