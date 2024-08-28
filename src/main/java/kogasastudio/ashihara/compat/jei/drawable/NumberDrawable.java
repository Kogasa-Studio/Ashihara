package kogasastudio.ashihara.compat.jei.drawable;

import mezz.jei.api.gui.drawable.IDrawable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;

import java.util.function.Supplier;

/**
 * @author DustW
 **/
public class NumberDrawable implements IDrawable
{

    Font font = Minecraft.getInstance().font;
    Supplier<? extends Number> numberSupplier;

    public NumberDrawable(Supplier<? extends Number> numberSupplier)
    {
        this.numberSupplier = numberSupplier;
    }

    @Override
    public int getWidth()
    {
        return font.width(numberSupplier.get().toString());
    }

    @Override
    public int getHeight()
    {
        return font.lineHeight;
    }

    @Override
    public void draw(GuiGraphics guiGraphics, int xOffset, int yOffset)
    {
        guiGraphics.drawString(font, Component.translatable(numberSupplier.get().toString()), xOffset, yOffset, 0xFFFFFFFF);
    }
}
