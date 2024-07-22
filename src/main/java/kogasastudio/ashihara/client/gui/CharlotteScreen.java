package kogasastudio.ashihara.client.gui;

import kogasastudio.ashihara.Ashihara;
import kogasastudio.ashihara.client.gui.modules.Screen;
import net.minecraft.client.GameNarrator;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.ObjectSelectionList;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class CharlotteScreen extends Screen
{
    public static final ResourceLocation CHARLOTTE_GUI = new ResourceLocation(Ashihara.MODID, "textures/gui/charlotte.png");
    private Button confirmPointButton;
    private PointList list;
    public CharlotteScreen()
    {
        super(GameNarrator.NO_TITLE, false);
        this.setWidth(256);
        this.setHeight(192);
    }

    @Override
    protected void init()
    {
        this.list = new PointList();
        this.addWidget(list);
        this.confirmPointButton = this.addRenderableWidget(new ConfirmButton(this.getStartX() + 18, this.getStartY() + 153, button -> list.addEntry()));
    }

    @Override
    public void render(GuiGraphics gui, int mouseX, int mouseY, float partialTicks)
    {
        int sX = this.getStartX();
        int sY = this.getStartY();
        this.renderBackground(gui);
        gui.blit(CHARLOTTE_GUI, sX + 81, sY + 80, 0, 192, 155, 92, 301, 300); //渲染选框背景
        this.list.render(gui, mouseX, mouseY, partialTicks); //渲染选项和滑块
        gui.blit(CHARLOTTE_GUI, sX, sY, 0, 0, 256, 192, 301, 300); //渲染主gui
        super.render(gui, mouseX, mouseY, partialTicks);
    }

    @OnlyIn(Dist.CLIENT)
    class ConfirmButton extends Button
    {
        protected ConfirmButton(int x, int y, OnPress onPress)
        {
            super(x, y, 45, 15, Component.translatable("gui.ashihara.narrators.button.confirm_point"), onPress, DEFAULT_NARRATION);
        }

        public void renderWidget(GuiGraphics gui, int mouseX, int mouseY, float partialTicks)
        {
            int i = 155;
            int j = 226;
            if (this.isHovered())
            {
                gui.blit(CHARLOTTE_GUI, this.getX() - 2, this.getY() - 2, i + 45, j, 49, 19, 301, 300); //渲染按钮选框
            }
            //if (this.clicked(mouseX, mouseY)) j += 15;

            gui.blit(CHARLOTTE_GUI, this.getX(), this.getY(), i, j, 45, 15, 301, 300); //渲染按钮
            gui.drawString(CharlotteScreen.this.font, Component.translatable("gui.ashihara.narrators.button.confirm_point"), this.getX() + 5, this.getY() + 3, 0xffffff, false);
        }
    }

    @OnlyIn(Dist.CLIENT)
    class PointList extends ObjectSelectionList<PointList.Entry>
    {
        int sX;
        int sY;
        public void addEntry()
        {
            this.addEntry(new PointList.Entry(this.children().size()));
        }

        public PointList()
        {
            super(CharlotteScreen.this.minecraft, 146, 90, 81, 171, 18);
            this.sX = (CharlotteScreen.this.width - 256) / 2;
            this.sY = (CharlotteScreen.this.height - 192) / 2;
            this.y0 = sY + y0;
            this.y1 = sY + y1;
            this.x0 = sX + 81;
            this.x1 = x0 + 146;
        }

        @Override
        protected int getScrollbarPosition() {return sX + 230;}

        @Override
        public int getRowWidth() {return 146;}

        @Override
        protected int getRowTop(int pIndex) {return this.y0 - (int)this.getScrollAmount() + pIndex * this.itemHeight + this.headerHeight;}

        @Override
        public int getRowLeft() {return this.x0;}

        @Override
        protected void renderList(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick)
        {
            int i = this.getRowLeft();
            int j = this.getRowWidth();
            int l = this.getItemCount();

            for(int i1 = 0; i1 < l; ++i1)
            {
                int j1 = this.getRowTop(i1);
                int k1 = this.getRowBottom(i1);
                if (k1 > this.y0 && j1 < this.y1)
                {
                    this.renderItem(pGuiGraphics, pMouseX, pMouseY, pPartialTick, i1, i, j1, j, this.itemHeight);
                }
            }
        }

        @Override
        public void render(GuiGraphics gui, int pMouseX, int pMouseY, float pPartialTick)
        {
            this.renderList(gui, pMouseX, pMouseY, pPartialTick);
            int maxScroll = this.getMaxScroll();
            int cX = 230;
            int cY = 80;
            if (maxScroll > 0)
            {
                int scrollBarLength = 82;
                int currentSliderPos = (int) (scrollBarLength * this.getScrollAmount() / (float) maxScroll);
                currentSliderPos = Mth.clamp(currentSliderPos, 0, 82);
                cY += currentSliderPos;
            }
            else gui.setColor(0.75f, 0.75f, 0.75f, 1);
            gui.blit(CHARLOTTE_GUI, sX + cX, sY + cY, 224, 245, 6, 10, 301, 300); //渲染滑块
            gui.setColor(1, 1, 1, 1);
        }

        @OnlyIn(Dist.CLIENT)
        class Entry extends ObjectSelectionList.Entry<PointList.Entry>
        {
            private final int id;
            private SwitchButton upButton;//不对，这个entry甚至没有自己的x和y
            private SwitchButton downButton; //2024.5.22 1:44 a.m.: ojng我吃了你妈

            public Entry(int id) {this.id = id;}

            @Override
            public Component getNarration()
            {
                return Component.translatable("narrator.select");
            }

            @Override
            public void render(GuiGraphics gui, int pIndex, int pTop, int pLeft, int pWidth, int pHeight, int pMouseX, int pMouseY, boolean pHovering, float pPartialTick)
            {
                gui.blit(CHARLOTTE_GUI, pLeft + 1, pTop + 1, 155, 192, pWidth - 2, pHeight - 2, 301, 300); //渲染选项
                if (isMouseOver(pMouseX, pMouseY, pLeft, pTop, pLeft + pWidth, pTop + pHeight)) gui.blit(CHARLOTTE_GUI, pLeft, pTop, 155, 208, pWidth, pHeight, 301, 300); //渲染选框
                //if (Mth.clamp(pMouseX, pLeft + 130, pLeft + 142) == pMouseX && Mth.clamp(pMouseY, PointList.this.getRowTop(pIndex)))
                gui.drawString(CharlotteScreen.this.font, Component.literal("#" + this.id), pLeft + 5, pTop + 5, 0xffffff, false);
                gui.drawString(CharlotteScreen.this.font, Component.translatable("item.ashihara.aqua"), pLeft + 18 + 5, pTop + 5, 0xffffff, false);
            }

            //public boolean isAreaHovered(int pIndex, int mouseX, int mouseY, )

            public boolean isMouseOver(int mouseX, int mouseY, int areaX0, int areaY0, int areaX1, int areaY1)
            {
                return Mth.clamp(mouseX, areaX0, areaX1) == mouseX && Mth.clamp(mouseY, areaY0, areaY1) == mouseY;
            }

            @OnlyIn(Dist.CLIENT)
            class SwitchButton extends Button
            {
                private final boolean isDown;

                protected SwitchButton(int pX, int pY, OnPress pOnPress, boolean isDown)
                {
                    super(pX, pY, 10, 7, Component.empty(), pOnPress, DEFAULT_NARRATION);
                    this.isDown = isDown;
                }

                public void renderWidget(GuiGraphics gui, int mouseX, int mouseY, float partialTicks)
                {
                    int u = 200;
                    int v = isDown ? 252 : 245;

                    if (this.isHovered) gui.blit(CHARLOTTE_GUI, this.getX(), this.getY(), u, v, 10, 7, 301, 300);//渲染按钮
                }
            }
        }
    }
}
