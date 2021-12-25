package kogasastudio.ashihara.inventory.container;

import kogasastudio.ashihara.Ashihara;
import kogasastudio.ashihara.block.tileentities.MillTE;
import net.minecraft.client.Minecraft;
import net.minecraft.inventory.container.ContainerType;
import net.minecraftforge.common.extensions.IForgeContainerType;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class ContainerRegistryHandler
{
    public static final DeferredRegister<ContainerType<?>> CONTAINER_TYPES = DeferredRegister.create(ForgeRegistries.CONTAINERS, Ashihara.MODID);

    public static final RegistryObject<ContainerType<MillContainer>> MILL_CONTAINER
    = CONTAINER_TYPES.register("mill_container",
    () -> IForgeContainerType.create(((windowId, inv, data) -> new MillContainer(windowId, inv, Minecraft.getInstance().world, data.readBlockPos(), new MillTE().millData))));
}
