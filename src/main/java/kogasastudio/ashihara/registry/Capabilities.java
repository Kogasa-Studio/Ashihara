package kogasastudio.ashihara.registry;

import kogasastudio.ashihara.Ashihara;
import kogasastudio.ashihara.block.blockentity.DirtCookStoveBE;
import kogasastudio.ashihara.block.blockentity.MortarBE;
import kogasastudio.ashihara.block.blockentity.PotBlockEntity;
import kogasastudio.ashihara.item.block.FurnitureComponentItem;
import kogasastudio.ashihara.interaction.HeatLevel;
import net.minecraft.core.Direction;
import net.minecraft.resources.Identifier;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.capabilities.BlockCapability;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;

@EventBusSubscriber
public class Capabilities
{
    public static final BlockCapability<HeatLevel, Direction> HEAT_LEVEL = BlockCapability.createSided(Identifier.fromNamespaceAndPath(Ashihara.MODID, "heat_level"), HeatLevel.class);

    @SubscribeEvent
    public static void registerCaps(RegisterCapabilitiesEvent event)
    {
        event.registerBlockEntity(
            net.neoforged.neoforge.capabilities.Capabilities.Item.BLOCK, BlockEntities.MORTAR_BE.get(), MortarBE::getInv);
        event.registerBlockEntity(
            net.neoforged.neoforge.capabilities.Capabilities.Fluid.BLOCK, BlockEntities.MORTAR_BE.get(), MortarBE::getFluid);
        event.registerBlockEntity(
            net.neoforged.neoforge.capabilities.Capabilities.Item.BLOCK, BlockEntities.POT_BE.get(), PotBlockEntity::getItemHandler);
        event.registerBlockEntity(
            net.neoforged.neoforge.capabilities.Capabilities.Fluid.BLOCK, BlockEntities.POT_BE.get(), PotBlockEntity::getFluidHandler);
        event.registerBlockEntity(
            net.neoforged.neoforge.capabilities.Capabilities.Item.BLOCK, BlockEntities.DIRT_COOKSTOVE_BE.get(), DirtCookStoveBE::getFuelStorage);
        event.registerBlockEntity(HEAT_LEVEL, BlockEntities.DIRT_COOKSTOVE_BE.get(), DirtCookStoveBE::getHeatLevel);

        event.registerItem(net.neoforged.neoforge.capabilities.Capabilities.Fluid.ITEM, FurnitureComponentItem::getFluidHandler, Items.WOODEN_BOWL_MID.get());
    }
}