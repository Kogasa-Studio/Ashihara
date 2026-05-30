package kogasastudio.ashihara.interaction;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.StringRepresentable;

public enum HeatLevel implements StringRepresentable
{
    NONE("none", 0x6f7374),
    LOW("low", 0x9a1d09),
    MEDIUM("medium", 0xeb6311),
    HIGH("high", 0xffe756);

    final String id;
    final int color;

    HeatLevel(String id, int color)
    {
        this.id = id;
        this.color = color;
    }

    @Override
    public String getSerializedName()
    {
        return id;
    }

    public Component getDisplayName()
    {
        return Component.translatable("heat_level.ashihara." + id).setStyle(Style.EMPTY.withColor(this.getColor()));
    }

    public int getColor()
    {
        return color;
    }

    public static final EnumCodec<HeatLevel> CODEC = StringRepresentable.fromEnum(HeatLevel::values);

    public static final StreamCodec<RegistryFriendlyByteBuf, HeatLevel> STREAM_CODEC = StreamCodec.of(
        (buf, val) -> buf.writeByte(val.ordinal()),
        buf -> HeatLevel.values()[buf.readByte()]
    );
}
