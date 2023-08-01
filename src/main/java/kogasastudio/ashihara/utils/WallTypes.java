package kogasastudio.ashihara.utils;

import kogasastudio.ashihara.block.BlockRegistryHandler;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

public enum WallTypes implements StringRepresentable
{
    WHITE_SOIL("white_soil")
            {
                @Override
                public Block getBlock()
                {
                    return BlockRegistryHandler.THIN_WHITE_SOIL_WALL.get();
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

    public Block getBlock() {return Blocks.AIR;}
}
