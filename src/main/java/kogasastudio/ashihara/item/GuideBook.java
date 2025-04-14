package kogasastudio.ashihara.item;

import kogasastudio.ashihara.client.gui.GuideBookScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import java.util.HashMap;
import java.util.Map;

public class GuideBook extends Item
{
    public static Map<Integer, Page> PAGES = new HashMap<>();

    public GuideBook()
    {
        super(new Properties());
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level pLevel, Player pPlayer, InteractionHand pUsedHand)
    {
        if (pLevel.isClientSide())
        {
            Screen screen = new GuideBookScreen(Component.empty(), pPlayer);
            Minecraft.getInstance().setScreen(screen);
        }
        return super.use(pLevel, pPlayer, pUsedHand);
    }

    public static class Page
    {
        public int getPageNumber()
        {
            return pageNumber;
        }

        public boolean isTextColumned()
        {
            return isTextColumned;
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
        final boolean isTextColumned;
        TextField[] textFields;
        Illustration[] illustrations;

        public Page(int pageNumberIn, boolean isTextColumned, TextField[] textFieldsIn, Illustration[] illustrationsIn)
        {
            this.pageNumber = pageNumberIn;
            this.isTextColumned = isTextColumned;
            this.textFields = textFieldsIn;
            this.illustrations = illustrationsIn;
        }

        /**
         * @param widthInFullWidthChar 若为竖排排版则为高度
         * @param heightInFullWidthChar 若为竖排排版则为宽度
         */
        public record TextField(float x, float y, int widthInFullWidthChar, int heightInFullWidthChar, String text)
        {
        }

        public record Illustration(float x, float y, float width, float height, ResourceLocation pic)
        {
        }
    }
}
