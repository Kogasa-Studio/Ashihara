package kogasastudio.ashihara.client.models.geo;

import com.geckolib.cache.model.GeoBone;
import net.minecraft.world.entity.player.Player;

public class HemmingEdgeModel extends SimpleInternalControlGeoModel
{
    public HemmingEdgeModel(String modelPrefix, String texturePrefix)
    {
        super(modelPrefix, texturePrefix);
    }

    /**
     * @param divides Relative location in range [0, 1], should be defaulted to be 0.5 to map the center location.
     */
    public void syncFrame(float xStart, float xEnd, float yStart, float yEnd, float divides)
    {
        //this.getBone("up").ifPresent(b -> {b.setPosX(-(xEnd - xStart) * divides);b.setPosY(yEnd);});
        //this.getBone("right").ifPresent(b -> {b.setPosX(-xStart);b.setPosY((yEnd - yStart) * divides);});
        //this.getBone("down").ifPresent(b -> {b.setPosX(-(xEnd - xStart) * divides);b.setPosY(yStart);});
        //this.getBone("left").ifPresent(b -> {b.setPosX(xStart - xEnd);b.setPosY((yEnd - yStart) * divides);});
    }

    public void syncMain(GeoBone from)
    {
        /*this.getBone("main").ifPresent
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
        );*/
    }
}
