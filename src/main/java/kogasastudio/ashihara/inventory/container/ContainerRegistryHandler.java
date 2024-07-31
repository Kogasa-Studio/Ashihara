package kogasastudio.ashihara.inventory.container;

import kogasastudio.ashihara.Ashihara;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class ContainerRegistryHandler
{
    public static final DeferredRegister<MenuType<?>> CONTAINER_TYPES = DeferredRegister.create(BuiltInRegistries.MENU, Ashihara.MODID);

    /*public static final Supplier<MenuType<MillContainer>> MILL_CONTAINER = CONTAINER_TYPES.register
    (
        "mill_container",
        () -> IMenuTypeExtension.create
        (
            (windowId, inv, data) -> new MillContainer(windowId, inv, getBe(inv, data))
        )
    );

    public static final Supplier<MenuType<MortarContainer>> MORTAR_CONTAINER = CONTAINER_TYPES.register
    (
        "mortar_container", () -> IMenuTypeExtension.create
        (
            (windowId, inv, data) -> new MortarContainer(windowId, inv, getBe(inv, data))
        )
    );*/

    // todo 如果这里 cast 失败了说明真的该崩了
    @SuppressWarnings("unchecked")
    static <T extends BlockEntity> T getBe(Inventory inv, FriendlyByteBuf data)
    {
        return (T) getLevel(inv).getBlockEntity(data.readBlockPos());
    }

    static Level getLevel(Inventory inv)
    {
        return inv.player.level();
    }
}
