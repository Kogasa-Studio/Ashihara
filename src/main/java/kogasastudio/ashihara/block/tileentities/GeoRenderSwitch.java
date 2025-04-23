package kogasastudio.ashihara.block.tileentities;

import net.minecraft.world.entity.player.Player;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class GeoRenderSwitch
{
    private boolean doRender = false;
    private boolean needCheck = false;
    private boolean forHiding = false;

    private final Consumer<Player> init;
    private final Consumer<Player> hide;
    private final Supplier<Boolean> checker;

    public GeoRenderSwitch(Consumer<Player> init, Consumer<Player> hide, Supplier<Boolean> checker)
    {
        this.init = init;
        this.hide = hide;
        this.checker = checker;
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

    private void disableRender()
    {
        this.doRender = false;
        this.forHiding = false;
    }

    private void init(Player player)
    {
        init.accept(player);
        this.doRender = true;
        this.forHiding = false;
        pushRenderCheck();
    }

    private void hide(Player player)
    {
        hide.accept(player);
        this.forHiding = true;
        pushRenderCheck();
    }
}
