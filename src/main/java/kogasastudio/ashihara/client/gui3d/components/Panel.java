package kogasastudio.ashihara.client.gui3d.components;


public class Panel extends AbstractComponent
{
	public float width;
	public float height;

	public boolean drawBackground = true;
	public int backgroundColor = 0x88201010;
	public int borderColor = 0xFFE0D2AE;

	public Panel(float x, float y, float width, float height)
	{
		super(x, y, 0, width, height, 0);
	}

	public Panel setDrawBackground(boolean drawBackground)
	{
		this.drawBackground = drawBackground;
		return this;
	}

	public Panel setBackgroundColor(int backgroundColor)
	{
		this.backgroundColor = backgroundColor;
		return this;
	}

	public Panel setBorderColor(int borderColor)
	{
		this.borderColor = borderColor;
		return this;
	}

	protected void renderSelf(int mouseX, int mouseY, float partialTick)
	{
		if (!this.drawBackground)
		{
			return;
		}

		int left = Math.round(this.getGlobalX());
		int top = Math.round(this.getGlobalY());
		int right = Math.round(this.getGlobalX() + this.width);
		int bottom = Math.round(this.getGlobalY() + this.height);

		/*guiGraphics.fill(left, top, right, bottom, this.backgroundColor);
		guiGraphics.fill(left, top, right, top + 1, this.borderColor);
		guiGraphics.fill(left, bottom - 1, right, bottom, this.borderColor);
		guiGraphics.fill(left, top, left + 1, bottom, this.borderColor);
		guiGraphics.fill(right - 1, top, right, bottom, this.borderColor);*/
	}
}
