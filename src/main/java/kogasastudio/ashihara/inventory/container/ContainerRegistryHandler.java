package kogasastudio.ashihara.inventory.container;

import kogasastudio.ashihara.Ashihara;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ContainerRegistryHandler
{
    public static final DeferredRegister<MenuType<?>> CONTAINER_TYPES = DeferredRegister.create(ForgeRegistries.CONTAINERS, Ashihara.MODID);

    public static final RegistryObject<MenuType<MillContainer>> MILL_CONTAINER = CONTAINER_TYPES.register("mill_container",
            () -> IForgeMenuType.create(((windowId, inv, data) ->
                    new MillContainer(windowId, inv, getBe(inv, data)))));

    public static final RegistryObject<MenuType<MortarContainer>> MORTAR_CONTAINER = CONTAINER_TYPES.register("mortar_container",
            () -> IForgeMenuType.create(((windowId, inv, data) ->
                    new MortarContainer(windowId, inv, getBe(inv, data)))));

    // todo 如果这里 cast 失败了说明真的该崩了
    @SuppressWarnings("unchecked")
    static <T extends BlockEntity> T getBe(Inventory inv, FriendlyByteBuf data)
    {
        return (T) getLevel(inv).getBlockEntity(data.readBlockPos());
    }

    static Level getLevel(Inventory inv)
    {
        return inv.player.level;
    }
}
