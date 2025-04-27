package kogasastudio.ashihara.block.tileentities.util;

import net.minecraft.world.entity.player.Player;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class RenderSwitch
{
    protected boolean doRender = false;
    protected boolean needCheck = false;
    protected boolean forHiding = false;

    protected final Consumer<Player> init;
    protected final Consumer<Player> hide;
    protected final Supplier<Boolean> checker;

    /**
     * Construct a render switch with given extra operations and checking conditions.
     * @param init Extra operation to be executed first when initializing. Could be used to trigger intro animations.
     * @param hide Extra operation to be executed first when disabling render. Almost same as @init.
     * @param checker Condition to define if the rendering function this switch is actually controlling should be rendered. If true, then render.
     */
    public RenderSwitch(Consumer<Player> init, Consumer<Player> hide, Supplier<Boolean> checker)
    {
        this.init = init;
        this.hide = hide;
        this.checker = checker;
    }

    public boolean doRender()
    {
        return this.doRender;
    }

    public void switchRender(Player player)
    {
        if (!this.doRender) init(player);
        else hide(player);
    }

    public void pushRenderCheck()
    {
        this.needCheck = true;
    }

    /**
     * Check and defines whether the BER should work. Should be used in BER.
     * @return BER should be rendered
     */
    public boolean checkRender()
    {
        if (!needCheck) return this.doRender;

        if (checker.get()) return true;
        if (this.forHiding) this.disableRender();
        this.needCheck = false;
        return this.doRender;
    }

    protected void disableRender()
    {
        this.doRender = false;
        this.forHiding = false;
        this.needCheck = false;
    }

    protected void init(Player player)
    {
        init.accept(player);
        this.doRender = true;
        this.forHiding = false;
        pushRenderCheck();
    }

    protected void hide(Player player)
    {
        hide.accept(player);
        this.forHiding = true;
        pushRenderCheck();
    }
}
