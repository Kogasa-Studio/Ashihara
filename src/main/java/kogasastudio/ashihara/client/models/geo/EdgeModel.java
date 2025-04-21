package kogasastudio.ashihara.client.models.geo;

import net.minecraft.world.entity.player.Player;
import software.bernie.geckolib.cache.object.GeoBone;

public class EdgeModel extends SimpleInternalControlGeoModel
{
    final float baseScaleX;
    final float baseScaleY;

    public EdgeModel(String modelPrefix, String texturePrefix, Player player, float baseScaleX, float baseScaleY)
    {
        super(modelPrefix, texturePrefix, player);
        this.baseScaleX = baseScaleX;
        this.baseScaleY = baseScaleY;
    }

    public void syncFrame(float xStart, float xEnd, float yStart, float yEnd, float xScale, float yScale)
    {
        this.getBone("up").ifPresent(b -> {b.setPosX(-xEnd);b.setPosY(yEnd);b.setScaleX(xScale / baseScaleX + baseScaleX);});
        this.getBone("right").ifPresent(b -> {b.setPosX(-xStart);b.setPosY(yEnd);b.setScaleX(yScale / baseScaleY);});
        this.getBone("down").ifPresent(b -> {b.setPosX(-xEnd);b.setPosY(yStart);b.setScaleX(xScale / baseScaleX + baseScaleX);});
        this.getBone("left").ifPresent(b -> {b.setPosX(-xEnd);b.setPosY(yEnd);b.setScaleX(yScale / baseScaleY);});
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
