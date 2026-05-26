package kogasastudio.ashihara.client.gui3d.components;

import kogasastudio.ashihara.client.models.geo.BubbleModel;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import org.joml.Matrix4f;

public class BubbleComponent extends ModelComponent
{
    private static final Player p = Minecraft.getInstance().player;
    public boolean isActive = false;
    public BubbleComponent(BubbleModel model)
    {
        super(model, true);
    }

    public BubbleComponent(BubbleModel model, Matrix4f presetTransform)
    {
        super(model, true, presetTransform);
    }

    public void appear()
    {
        if (this.isActive) return;
        this.model().triggerIntro(p);
        this.isActive = true;
    }

    public void hide()
    {
        if (!this.isActive) return;
        this.model().triggerOutro(p);
        this.isActive = false;
    }

    @Override
    public void init()
    {
        super.init();
        this.model().init(p, true);
    }

    public BubbleModel model()
    {
        return (BubbleModel) this.model;
    }
}
