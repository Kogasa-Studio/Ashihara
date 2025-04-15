package kogasastudio.ashihara.helper;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.StringSplitter;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.network.chat.Style;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FontHelper
{
    public static void renderFormattedText(PoseStack poseStack, List<String> list, int x, int y, int color, boolean dropShadow, boolean columned, MultiBufferSource bufferSource, Font.DisplayMode mode, int backgroundColor, int packedLight)
    {
        if (list.isEmpty()) return;
        Font font = Minecraft.getInstance().font;
        int maxLineSize = list.getFirst().length();
        for (int i = 0; i < list.size(); i++)
        {
            String line = list.get(i);
            poseStack.pushPose();
            font.drawInBatch(line, columned ? x + (maxLineSize - line.length()) * 9 : x, y + i * 9, color, dropShadow, poseStack.last().pose(), bufferSource, mode, backgroundColor, packedLight);
            poseStack.popPose();
        }
    }

    public static void renderColumnedText(PoseStack poseStack, String text, int x, int y, int color, boolean dropShadow, int maxHeight, MultiBufferSource bufferSource, Font.DisplayMode mode, int backgroundColor, int packedLight)
    {
        Font font = Minecraft.getInstance().font;
        StringSplitter splitter = font.getSplitter();
        List<String> rawList = new ArrayList<>();
        splitter.splitLines(text, maxHeight, Style.EMPTY, true, ((style, currentPos, contentWidth) ->
        {
            String lineText = text.substring(currentPos, contentWidth);
            lineText = StringUtils.stripEnd(lineText, "\n");
            rawList.add(lineText);
        }));
        int maxLineSize = rawList.getFirst().length();
        char[][] charsMatrix = new char[maxLineSize][rawList.size()];
        List<String> list = new ArrayList<>();
        for (int i = 0; i < rawList.size(); i++)
        {
            String line = rawList.get(i);
            char[] chars = line.toCharArray();
            for (int j = 0; j < chars.length; j++)
            {
                charsMatrix[j][rawList.size() - i - 1] = chars[j];
            }//column the line ant copy which to the right-started current column.
        }
        int maxRelinedLineSize = 0;
        for (char[] line : charsMatrix)
        {
            String reLined = new String(line);
            reLined = reLined.replaceAll(Arrays.toString(Character.toChars(0)), "");
            list.add(reLined);
            maxRelinedLineSize = Math.max(maxRelinedLineSize, reLined.length());
        }
        for (int i = 0; i < list.size(); i++)
        {
            String line = list.get(i);
            poseStack.pushPose();
            font.drawInBatch(line, x + (maxLineSize - line.length()) * 9, y + i * 9, color, dropShadow, poseStack.last().pose(), bufferSource, mode, backgroundColor, packedLight);
            poseStack.popPose();
        }
    }

    public static List<String> transformToColumn(List<String> rawList)
    {
        if (rawList.isEmpty()) return rawList;
        int maxLineSize = rawList.getFirst().length();
        for (String s : rawList) {maxLineSize = Math.max(maxLineSize, s.length());}
        char[][] charsMatrix = new char[maxLineSize][rawList.size()];
        List<String> list = new ArrayList<>();
        for (int i = 0; i < rawList.size(); i++)
        {
            String line = rawList.get(i);
            char[] chars = line.toCharArray();
            for (int j = 0; j < chars.length; j++)
            {
                charsMatrix[j][rawList.size() - i - 1] = chars[j];
            }//column the line and copy which to the right-started current column.
        }
        int maxRelinedLineSize = 0;
        for (char[] line : charsMatrix)
        {
            String reLined = new String(line);
            reLined = reLined.replaceAll(Arrays.toString(Character.toChars(0)), "　");
            list.add(reLined);
            maxRelinedLineSize = Math.max(maxRelinedLineSize, reLined.length());
        }
        return list;
    }

    public static List<String> processText(String text, int maxWidth)
    {
        Font font = Minecraft.getInstance().font;
        StringSplitter splitter = font.getSplitter();
        List<String> list = new ArrayList<>();
        splitter.splitLines(text, maxWidth, Style.EMPTY, true, ((style, currentPos, contentWidth) ->
        {
            String lineText = text.substring(currentPos, contentWidth);
            lineText = StringUtils.stripEnd(lineText, "\n");
            list.add(lineText);
        }));
        return list;
    }
    /*
    OOOOOO     OOOOOOOO
    OOOOOO      OOOOOOO   maxheight: 3
    OOOOOO  ->  OOOOOOO
    OOOO
     */
}
