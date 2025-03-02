package kogasastudio.ashihara.item;

import kogasastudio.ashihara.client.gui.GuideBookScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class GuideBook extends Item
{
    public GuideBook()
    {
        super(new Properties());
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level pLevel, Player pPlayer, InteractionHand pUsedHand)
    {
        Minecraft mc = Minecraft.getInstance();
        if (pLevel.isClientSide())
        {
            Screen screen = new GuideBookScreen(Component.empty(), pPlayer);
            //screen.init(mc, mc.getWindow().getWidth(), mc.getWindow().getHeight());
            Minecraft.getInstance().setScreen(screen);
        }
        return super.use(pLevel, pPlayer, pUsedHand);
    }
}
