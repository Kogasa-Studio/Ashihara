package kogasastudio.ashihara.utils;

import com.mojang.blaze3d.vertex.PoseStack;
import kogasastudio.ashihara.helper.InWorldTipRenderHelper;

public class InWorldTooltipInfoWrapper
{
    public PoseStack poseStack;
    protected float maxX = 0;
    protected float maxY = 0;

    public void init(PoseStack poseStack)
    {
        this.poseStack = poseStack;
        this.maxX = 0;
        this.maxY = 0;
        pushPose();
        this.poseStack.scale(1 / 16f, 1 / 16f, 1 / 16f);
    }

    public void cast()
    {
        this.maxX = 0;
        this.maxY = 0;
        popPose();
        this.poseStack = null;
    }

    public void pushPose()
    {
        this.poseStack.pushPose();
    }

    public void popPose()
    {
        this.poseStack.popPose();
    }

    public void translate(float x, float y)
    {
        this.poseStack.translate(x, y, 0);
        this.maxX += x;
        this.maxY += y;
    }

    public float getMaxX()
    {
        return maxX;
    }

    public float getMaxY()
    {
        return maxY;
    }

    public void check(float x, float y)
    {
        this.maxX = Math.max(this.maxX, x);
        this.maxY = Math.max(this.maxY, y);
    }

    public void check(InWorldTipRenderHelper.XY xy)
    {
        check(xy.getX(), xy.getY());
    }

    public void checkAndOffsetY(InWorldTipRenderHelper.XY xy)
    {
        check(xy.getX(), xy.getY());
        translate(0, xy.getY());
    }
}
