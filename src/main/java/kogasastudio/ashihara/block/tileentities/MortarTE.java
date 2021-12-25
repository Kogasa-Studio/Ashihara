package kogasastudio.ashihara.block.tileentities;

import net.minecraft.tileentity.TileEntity;

public class MortarTE extends TileEntity
{
    public MortarTE() {super(TERegistryHandler.MORTAR_TE.get());}

    /**
     * 这个byte的不同值决定舂的状态和其中该含有的物品。
     * 0 -> 稻谷
     * 1 -> 白米
     * 3 -> 待打制的麻糬
     * 4 -> 待揉的麻糬
     * 5 -> 作成的麻糬
     */
    public byte stat;
}
