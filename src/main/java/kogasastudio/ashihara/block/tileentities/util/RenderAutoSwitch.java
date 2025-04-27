package kogasastudio.ashihara.block.tileentities.util;

import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class RenderAutoSwitch extends RenderSwitch
{
    protected final Supplier<Boolean> checkerToHide;

    /**
     * Construct a render switch that can automatically shut down when the condition is valid for hiding. with given extra operations and checking conditions.
     * This type of RenderSwitch need only to be activated, so calling switchRender() will just re-init the switch.
     * @param init Extra operation to be executed first when initializing. Could be used to trigger intro animations.
     * @param hide Extra operation to be executed first when disabling render. Almost same as @init.
     * @param checkerToCompleteShutDown Condition to define if the rendering function this switch is actually controlling should be rendered. If true, then render.
     * @param checkerToHide Condition to define if this switch should execute hide function. If true, then hide.
     */
    public RenderAutoSwitch(Consumer<Player> init, Consumer<Player> hide, Supplier<Boolean> checkerToCompleteShutDown, Supplier<Boolean> checkerToHide)
    {
        super(init, hide, checkerToCompleteShutDown);
        this.checkerToHide = checkerToHide;
    }

    @Override
    public void switchRender(Player player)
    {
        init(player);
    }

    @Override
    public boolean checkRender()
    {
        if (!needCheck) return this.doRender;

        if (checkerToHide.get()) hide(Minecraft.getInstance().player);
        if (checker.get()) return true;
        if (this.forHiding) this.disableRender();
        return this.doRender;
    }

    @Override
    protected void disableRender()
    {
        super.disableRender();
    }
}
