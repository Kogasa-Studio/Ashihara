package kogasastudio.ashihara.registry;

import kogasastudio.ashihara.Ashihara;
import kogasastudio.ashihara.inventory.container.PotMenu;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

/**
 * 所有 Ashihara MenuType 的注册中心。
 * 在 {@link kogasastudio.ashihara.Ashihara} 的构造器中注册本 DeferredRegister。
 */
public class MenuTypes
{
    public static final DeferredRegister<MenuType<?>> MENU_TYPES =
            DeferredRegister.create(Registries.MENU, Ashihara.MODID);

    /** 土锅容器菜单类型。客户端通过 MenuScreens 绑定到 PotScreen3D。 */
    public static final Supplier<MenuType<PotMenu>> POT_MENU =
            MENU_TYPES.register("pot_menu", () -> IMenuTypeExtension.create(PotMenu::new));
}

