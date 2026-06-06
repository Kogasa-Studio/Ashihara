package kogasastudio.ashihara.item;

import kogasastudio.ashihara.network.OpenGuidebookPacket;
import kogasastudio.ashihara.registry.DataAttachmentTypes;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.server.ServerLifecycleHooks;

import java.util.HashMap;
import java.util.Map;

public class GuideBook extends Item
{
    public static Map<Integer, Page> PAGES = new HashMap<>();

    public GuideBook(Properties properties)
    {
        super(properties);
    }

    public GuideBook()
    {
        this(new Properties());
    }

    @Override
    public InteractionResult use(Level pLevel, Player pPlayer, InteractionHand pUsedHand)
    {
        if (!pPlayer.isLocalPlayer())
        {
            PacketDistributor.sendToPlayer((ServerPlayer) pPlayer, new OpenGuidebookPacket(""));
        }
        if (pLevel.isClientSide())
        {
            if (ServerLifecycleHooks.getCurrentServer() != null && ServerLifecycleHooks.getCurrentServer().getPlayerList().getPlayer(pPlayer.getUUID()) != null)
            {
                pPlayer.setData(DataAttachmentTypes.GUIDEBOOK_READING_PAGE, ServerLifecycleHooks.getCurrentServer().getPlayerList().getPlayer(pPlayer.getUUID()).getData(DataAttachmentTypes.GUIDEBOOK_READING_PAGE));
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

        public record Illustration(float x, float y, float width, float height, Identifier pic)
        {
        }
    }
}
