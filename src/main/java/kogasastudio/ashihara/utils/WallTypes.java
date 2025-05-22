package kogasastudio.ashihara.utils;

import kogasastudio.ashihara.registry.Blocks;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.level.block.Block;

public enum WallTypes implements StringRepresentable
{
    WHITE_SOIL("white_soil")
            {
                @Override
                public Block getBlock()
                {
                    return Blocks.THIN_WHITE_SOIL_WALL.get();
                }
            },
    PLANK("plank"),
    FRAME("frame");

    WallTypes(String nameIn) {this.name = nameIn;}

    String name;

    @Override
    public String getSerializedName()
    {
        return this.name;
    }

    public Block getBlock() {return net.minecraft.world.level.block.Blocks.AIR;}
}
