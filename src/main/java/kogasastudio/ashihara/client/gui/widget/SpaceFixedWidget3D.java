package kogasastudio.ashihara.client.gui.widget;

import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.network.chat.Component;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import software.bernie.geckolib.cache.object.GeoBone;

public abstract class SpaceFixedWidget3D extends AbstractWidget
{
    public GeoBone parent;

    public SpaceFixedWidget3D(int x, int y, int sizeX, int sizeY, Component message)
    {
        super(x, y, sizeX, sizeY, message);
    }
}
