package kogasastudio.ashihara.item;

import kogasastudio.ashihara.client.gui.GuideBookScreen;
import kogasastudio.ashihara.registry.DataComponentTypes;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.server.ServerLifecycleHooks;

import java.util.HashMap;
import java.util.Map;

public class GuideBook extends Item
{
    public static Map<Integer, Page> PAGES = new HashMap<>();

    static class SetScreen
    {
        public void run(Player player)
        {
            Minecraft.getInstance().setScreen(new GuideBookScreen(Component.empty(), player));
        }
    }

    private static SetScreen setScreen = new SetScreen();

    public GuideBook()
    {
        super(new Properties());
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level pLevel, Player pPlayer, InteractionHand pUsedHand)
    {
        if (pLevel.isClientSide())
        {
            if (ServerLifecycleHooks.getCurrentServer() != null && ServerLifecycleHooks.getCurrentServer().getPlayerList().getPlayer(pPlayer.getUUID()) != null)
            {
                pPlayer.setData(DataComponentTypes.GUIDEBOOK_READING_PAGE, ServerLifecycleHooks.getCurrentServer().getPlayerList().getPlayer(pPlayer.getUUID()).getData(DataComponentTypes.GUIDEBOOK_READING_PAGE));
                setScreen.run(pPlayer);
            }
        }
        return super.use(pLevel, pPlayer, pUsedHand);
    }

    public static class Page
    {
        public int getPageNumber()
        {
            return pageNumber;
        }

        public Illustration[] getIllustrations()
        {
            return illustrations;
        }

        public TextField[] getTextFields()
        {
            return textFields;
        }

        final int pageNumber;
        TextField[] textFields;
        Illustration[] illustrations;

        public Page(int pageNumberIn, TextField[] textFieldsIn, Illustration[] illustrationsIn)
        {
            this.pageNumber = pageNumberIn;
            this.textFields = textFieldsIn;
            this.illustrations = illustrationsIn;
        }

        /**
         * @param widthInFullWidthChar 若为竖排排版则为高度
         * @param heightInFullWidthChar 若为竖排排版则为宽度
         */
        public record TextField(float x, float y, int widthInFullWidthChar, int heightInFullWidthChar, int textColor, float charSize, boolean isTextColumned, String text)
        {
        }

        public record Illustration(float x, float y, float width, float height, ResourceLocation pic)
        {
        }
    }
}
