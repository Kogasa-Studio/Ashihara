package kogasastudio.ashihara.event;

import kogasastudio.ashihara.block.BlockRegistryHandler;
import kogasastudio.ashihara.block.tileentities.TERegistryHandler;
import kogasastudio.ashihara.client.gui.MillScreen;
import kogasastudio.ashihara.client.gui.MortarScreen;
import kogasastudio.ashihara.client.models.baked.PailModel;
import kogasastudio.ashihara.client.particles.MapleLeafParticle;
import kogasastudio.ashihara.client.particles.ParticleRegistryHandler;
import kogasastudio.ashihara.client.particles.RiceParticle;
import kogasastudio.ashihara.client.particles.SakuraParticle;
import kogasastudio.ashihara.client.render.ter.*;
import kogasastudio.ashihara.fluid.FluidRegistryHandler;
import kogasastudio.ashihara.inventory.container.ContainerRegistryHandler;
import kogasastudio.ashihara.item.ItemRegistryHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.particle.ParticleEngine;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.ModelEvent;
import net.minecraftforge.client.event.RegisterParticleProvidersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

import java.util.Map;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientEventSubscribeHandler
{
    private static void setRenderType(Block block, RenderType type, FMLClientSetupEvent event)
    {
        event.enqueueWork(() -> ItemBlockRenderTypes.setRenderLayer(block, type));
    }

    private static void setRenderType(Fluid fluid, RenderType type, FMLClientSetupEvent event)
    {
        event.enqueueWork(() -> ItemBlockRenderTypes.setRenderLayer(fluid, type));
    }

    //设置渲染方式
    @SubscribeEvent
    public static void onRenderTypeSetup(FMLClientSetupEvent event)
    {
        setRenderType(BlockRegistryHandler.RICE_CROP.get(), RenderType.cutoutMipped(), event);
        setRenderType(BlockRegistryHandler.IMMATURE_RICE.get(), RenderType.cutoutMipped(), event);
        setRenderType(BlockRegistryHandler.CHERRY_BLOSSOM.get(), RenderType.cutoutMipped(), event);
        setRenderType(BlockRegistryHandler.MAPLE_LEAVES_RED.get(), RenderType.cutoutMipped(), event);
        setRenderType(BlockRegistryHandler.CHERRY_SAPLING.get(), RenderType.cutoutMipped(), event);
        setRenderType(BlockRegistryHandler.RED_MAPLE_SAPLING.get(), RenderType.cutoutMipped(), event);
        setRenderType(BlockRegistryHandler.JINJA_LANTERN.get(), RenderType.cutoutMipped(), event);
        setRenderType(BlockRegistryHandler.STONE_LANTERN.get(), RenderType.cutoutMipped(), event);
        setRenderType(BlockRegistryHandler.CHERRY_BLOSSOM_VINES.get(), RenderType.cutoutMipped(), event);
        setRenderType(BlockRegistryHandler.FALLEN_SAKURA.get(), RenderType.cutoutMipped(), event);
        setRenderType(BlockRegistryHandler.FALLEN_MAPLE_LEAVES_RED.get(), RenderType.cutoutMipped(), event);
        setRenderType(BlockRegistryHandler.POTTED_CHERRY_SAPLING.get(), RenderType.cutoutMipped(), event);
        setRenderType(BlockRegistryHandler.POTTED_RED_MAPLE_SAPLING.get(), RenderType.cutoutMipped(), event);
        setRenderType(BlockRegistryHandler.LANTERN_LONG_WHITE.get(), RenderType.cutoutMipped(), event);
        setRenderType(BlockRegistryHandler.LANTERN_LONG_RED.get(), RenderType.cutoutMipped(), event);
        setRenderType(BlockRegistryHandler.HOUSE_LIKE_HANGING_LANTERN.get(), RenderType.cutoutMipped(), event);
        setRenderType(BlockRegistryHandler.HEXAGONAL_HANGING_LANTERN.get(), RenderType.cutoutMipped(), event);
        setRenderType(BlockRegistryHandler.CHRYSANTHEMUM.get(), RenderType.cutoutMipped(), event);
        setRenderType(BlockRegistryHandler.REED.get(), RenderType.cutoutMipped(), event);
        setRenderType(BlockRegistryHandler.SHORTER_REED.get(), RenderType.cutoutMipped(), event);
        setRenderType(BlockRegistryHandler.HYDRANGEA_BUSH.get(), RenderType.cutoutMipped(), event);
        setRenderType(BlockRegistryHandler.TEA_TREE.get(), RenderType.cutoutMipped(), event);
        setRenderType(BlockRegistryHandler.GOLD_FENCE_DECORATION.get(), RenderType.cutoutMipped(), event);
        setRenderType(BlockRegistryHandler.RED_FENCE_EXPANSION.get(), RenderType.cutoutMipped(), event);
        setRenderType(BlockRegistryHandler.SPRUCE_FENCE_EXPANSION.get(), RenderType.cutoutMipped(), event);
        setRenderType(BlockRegistryHandler.SOY_BEANS.get(), RenderType.cutoutMipped(), event);
        setRenderType(BlockRegistryHandler.SWEET_POTATOES.get(), RenderType.cutoutMipped(), event);
        setRenderType(BlockRegistryHandler.CUCUMBERS.get(), RenderType.cutoutMipped(), event);
        setRenderType(BlockRegistryHandler.MEAL_TABLE.get(), RenderType.cutoutMipped(), event);

        setRenderType(FluidRegistryHandler.SOY_MILK.get(), RenderType.translucent(), event);
        setRenderType(FluidRegistryHandler.SOY_MILK_FLOWING.get(), RenderType.translucent(), event);
        setRenderType(FluidRegistryHandler.OIL.get(), RenderType.translucent(), event);
        setRenderType(FluidRegistryHandler.OIL_FLOWING.get(), RenderType.translucent(), event);
    }

    //注册粒子
    @SubscribeEvent
    public static void onParticleFactoryRegister(RegisterParticleProvidersEvent event)
    {
        ParticleEngine manager = Minecraft.getInstance().particleEngine;
        manager.register(ParticleRegistryHandler.RICE.get(), RiceParticle.RiceParticleProvider::new);
        manager.register(ParticleRegistryHandler.SAKURA.get(), SakuraParticle.SakuraParticleProvider::new);
        manager.register(ParticleRegistryHandler.MAPLE_LEAF.get(), MapleLeafParticle.MapleLeafParticleProvider::new);
    }

    //绑定TER
    @SubscribeEvent
    public static void onTERBind(EntityRenderersEvent.RegisterRenderers event)
    {
        event.registerBlockEntityRenderer(TERegistryHandler.MARKABLE_LANTERN_TE.get(), MarkableLanternTER::new);
        event.registerBlockEntityRenderer(TERegistryHandler.MILL_TE.get(), MillTER::new);
        event.registerBlockEntityRenderer(TERegistryHandler.PAIL_TE.get(), PailTER::new);
        event.registerBlockEntityRenderer(TERegistryHandler.CANDLE_TE.get(), CandleTER::new);
        event.registerBlockEntityRenderer(TERegistryHandler.MORTAR_TE.get(), MortarTER::new);
        event.registerBlockEntityRenderer(TERegistryHandler.CUTTING_BOARD_TE.get(), CuttingBoardTER::new);
    }

    @SubscribeEvent
    public static void onModelBaked(ModelEvent.ModifyBakingResult event)
    {
        Map<ResourceLocation, BakedModel> modelRegistry = event.getModels();
        ModelResourceLocation location = new ModelResourceLocation
                (ItemRegistryHandler.PAIL.getId(), "inventory");
        BakedModel existingModel = modelRegistry.get(location);
        if (existingModel == null)
        {
            throw new RuntimeException("Did not find Obsidian Hidden in registry");
        } else if (existingModel instanceof PailModel)
        {
            throw new RuntimeException("Tried to replace Obsidian Hidden twice");
        } else
        {
            PailModel model = new PailModel(existingModel);
            modelRegistry.put(location, model);
        }
    }

    //绑定GUI
    @SubscribeEvent
    public static void onScreenBind(FMLClientSetupEvent event)
    {
        event.enqueueWork(() ->
        {
            MenuScreens.register(ContainerRegistryHandler.MILL_CONTAINER.get(), MillScreen::new);
            MenuScreens.register(ContainerRegistryHandler.MORTAR_CONTAINER.get(), MortarScreen::new);
        });
    }

//    @SubscribeEvent
//    public static void onColorSetup(ColorHandlerEvent.Block event)
//    {
//        event.getBlockColors().register
//                (
//                    (state, reader, pos, tintIndex) ->
//                    pos != null && reader != null ? BiomeColors.getWaterColor(reader, pos) : -1, BlockExampleContainer.WATER_FIELD
//                );
//    }
}
