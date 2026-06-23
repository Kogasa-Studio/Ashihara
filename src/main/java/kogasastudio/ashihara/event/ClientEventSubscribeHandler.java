package kogasastudio.ashihara.event;

import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.platform.Window;
import kogasastudio.ashihara.Ashihara;
import kogasastudio.ashihara.client.gui3d.FermentationScreen;
import kogasastudio.ashihara.registry.FoodModelRegistry;
import kogasastudio.ashihara.network.EatingModePayload;
import kogasastudio.ashihara.registry.*;
import kogasastudio.ashihara.utils.EatingModeHelper;
import kogasastudio.ashihara.client.gui.overlay.GridSnapHudOverlay;
import kogasastudio.ashihara.client.gui.overlay.ContainerFoodHudOverlay;
import kogasastudio.ashihara.client.gui3d.PotScreen;
import kogasastudio.ashihara.client.render.item.BowlContentSpecialRenderer;
import kogasastudio.ashihara.client.render.hud.PlacementPreviewRenderer;
import kogasastudio.ashihara.client.render.state.Screen3DPiPRenderState;
import kogasastudio.ashihara.network.GridSnapPayload;
import kogasastudio.ashihara.client.particles.MapleLeafParticle;
import kogasastudio.ashihara.client.particles.ParticleRegistryHandler;
import net.neoforged.neoforge.client.event.InputEvent;
import net.neoforged.neoforge.client.network.ClientPacketDistributor;
import kogasastudio.ashihara.client.particles.RiceParticle;
import kogasastudio.ashihara.client.particles.SakuraParticle;
import kogasastudio.ashihara.client.render.ber.*;
import kogasastudio.ashihara.fluid.FluidRegistryHandler;
import net.minecraft.client.renderer.block.FluidModel;
import net.minecraft.client.resources.model.sprite.Material;
import net.neoforged.neoforge.client.event.RegisterFluidModelsEvent;
import net.neoforged.neoforge.client.fluid.FluidTintSources;
import kogasastudio.ashihara.utils.GridSnapHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.Model;
import net.minecraft.client.resources.model.EquipmentClientInfo;
import net.minecraft.world.item.ItemStack;
import net.minecraft.client.renderer.block.dispatch.BlockStateModel;
import net.minecraft.resources.Identifier;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import kogasastudio.ashihara.client.render.geo.pip.Screen3DPiPRenderer;
import net.neoforged.neoforge.client.event.*;
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;
import net.neoforged.neoforge.client.extensions.common.RegisterClientExtensionsEvent;
import net.neoforged.neoforge.client.model.standalone.SimpleUnbakedStandaloneModel;
import net.neoforged.neoforge.client.model.standalone.StandaloneModelKey;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

@EventBusSubscriber(modid = Ashihara.MODID, value = Dist.CLIENT)
public class ClientEventSubscribeHandler
{
    // --- 独立模型 key 缓存（仅客户端） ---
    private static Map<Identifier, StandaloneModelKey<BlockStateModel>> keyCache;

    public static StandaloneModelKey<BlockStateModel> getOrCreateKey(Identifier id)
    {
        if (keyCache == null) keyCache = new HashMap<>();
        return keyCache.computeIfAbsent(id, k -> new StandaloneModelKey<>(k::toDebugFileName));
    }

    // 注册独立模型
    @SubscribeEvent
    public static void onRegisterAdditionalModels(ModelEvent.RegisterStandalone event)
    {
        AdditionalModels.getModels().forEach(location ->
            event.register(getOrCreateKey(location.id()), SimpleUnbakedStandaloneModel.blockStateModel(location.id()))
        );
        FoodModelRegistry.registerStandalones(event);
    }

    @SubscribeEvent
    public static void onRegisterSpecialModels(RegisterSpecialModelRendererEvent event)
    {
        event.register
        (
            Identifier.fromNamespaceAndPath(Ashihara.MODID, "bowl_content"),
            BowlContentSpecialRenderer.Unbaked.MAP_CODEC
        );
    }

    @SubscribeEvent
    public static void onParticleFactoryRegister(RegisterParticleProvidersEvent event)
    {
        event.registerSpriteSet(ParticleRegistryHandler.RICE.get(), RiceParticle.RiceParticleProvider::new);
        event.registerSpriteSet(ParticleRegistryHandler.SAKURA.get(), SakuraParticle.SakuraParticleProvider::new);
        event.registerSpriteSet(ParticleRegistryHandler.MAPLE_LEAF.get(), MapleLeafParticle.MapleLeafParticleProvider::new);
    }

    // 绑定TER
    @SubscribeEvent
    public static void onTERBind(EntityRenderersEvent.RegisterRenderers event)
    {
        event.registerBlockEntityRenderer(BlockEntities.MARKABLE_LANTERN_BE.get(), MarkableLanternBER::new);
        event.registerBlockEntityRenderer(BlockEntities.PAIL_BE.get(), PailBER::new);
        event.registerBlockEntityRenderer(BlockEntities.CANDLE_BE.get(), CandleBER::new);
        event.registerBlockEntityRenderer(BlockEntities.MULTI_BUILT_BLOCKENTITY.get(), MultiBuiltBlockRenderer::new);
        event.registerBlockEntityRenderer(BlockEntities.CHARLOTTE_BE.get(), CharlotteBER::new);
        event.registerBlockEntityRenderer(BlockEntities.MORTAR_BE.get(), MortarBER::new);
        event.registerBlockEntityRenderer(BlockEntities.POT_BE.get(), PotBER::new);
        event.registerBlockEntityRenderer(BlockEntities.FERMENTATION_BE.get(), FermentationBER::new);
        event.registerBlockEntityRenderer(BlockEntities.CUTTING_BOARD_BE.get(), CuttingBoardBER::new);
    }

    // TODO(migration): PailModel 注册需迁移到 ModelEvent.RegisterAdditional 或 UnbakedModelLoader
    // @SubscribeEvent
    // public static void onModelBaked(ModelEvent.ModifyBakingResult event) { ... }

    // 绑定GUI
    @SubscribeEvent
    public static void onRegisterMenuScreens(RegisterMenuScreensEvent event)
    {
        event.register(MenuTypes.POT_MENU.get(), PotScreen::new);
        event.register(MenuTypes.FERMENTATION_MENU.get(), FermentationScreen::new);
    }

    // 注册客户端扩展（流体、装备模型等）
    @SubscribeEvent
    public static void onRegisterClientExtensions(RegisterClientExtensionsEvent event)
    {
        // 盔甲模型扩展（Sujikabuto，待实装 SujikabotoModel）
        event.registerItem(new IClientItemExtensions()
        {
            @Override
            public Model getHumanoidArmorModel(ItemStack itemStack, EquipmentClientInfo.LayerType layerType, Model original)
            {
                // TODO: return new SujikabutoModel(0.8F) when SujikabutoModel is implemented
                return original;
            }
        }, Items.SUJIKABUTO.get());

        // 流体摄像头叠加纹理（still/flowing/tint 由 FluidType.Properties 设置）
        event.registerFluidType(new IClientFluidTypeExtensions()
        {
            @Override
            public @Nullable Identifier getRenderOverlayTexture(@NotNull Minecraft mc)
            {
                return Identifier.withDefaultNamespace("textures/misc/underwater.png");
            }
        }, FluidRegistryHandler.AshiharaFluidTypes.TYPE_SOY_MILK.get());

        event.registerFluidType(new IClientFluidTypeExtensions()
        {
            @Override
            public @Nullable Identifier getRenderOverlayTexture(@NotNull Minecraft mc)
            {
                return Identifier.withDefaultNamespace("textures/misc/underwater.png");
            }
        }, FluidRegistryHandler.AshiharaFluidTypes.TYPE_OIL.get());
        event.registerFluidType(new IClientFluidTypeExtensions()
        {
            @Override
            public @Nullable Identifier getRenderOverlayTexture(@NotNull Minecraft mc)
            {
                return Identifier.withDefaultNamespace("textures/misc/underwater.png");
            }
        }, FluidRegistryHandler.AshiharaFluidTypes.TYPE_RICE_PORRIDGE.get());
    }

    @SubscribeEvent
    public static void onRegisterFluidModels(RegisterFluidModelsEvent event)
    {
        // 豆乳
        event.register(new FluidModel.Unbaked(
            new Material(FluidRegistryHandler.MILK_STILL),
            new Material(FluidRegistryHandler.MILK_FLOW),
            new Material(FluidRegistryHandler.WATER_OVERLAY),
            FluidTintSources.constant(0xFFFFFFFF)
        ), FluidRegistryHandler.SOY_MILK, FluidRegistryHandler.SOY_MILK_FLOWING);

        // 油
        event.register(new FluidModel.Unbaked(
            new Material(FluidRegistryHandler.WATER_STILL),
            new Material(FluidRegistryHandler.WATER_FLOW),
            new Material(FluidRegistryHandler.WATER_OVERLAY),
            FluidTintSources.constant(0xFFA8F4E9)
        ), FluidRegistryHandler.OIL, FluidRegistryHandler.OIL_FLOWING);

        // 白米粥
        event.register(new FluidModel.Unbaked(
            new Material(FluidRegistryHandler.PORRIDGE_STILL),
            new Material(FluidRegistryHandler.PORRIDGE_FLOW),
            new Material(FluidRegistryHandler.WATER_OVERLAY),
            FluidTintSources.constant(0xFFF5EBE0)
        ), FluidRegistryHandler.RICE_PORRIDGE, FluidRegistryHandler.RICE_PORRIDGE_FLOWING);
    }
        /** 注册 GuideBook 的 PiP 渲染器工厂（Mod 事件总线，仅客户端）。 */
    @SubscribeEvent
    public static void onRegisterPiPRenderers(RegisterPictureInPictureRenderersEvent event)
    {
        event.register(Screen3DPiPRenderState.class, Screen3DPiPRenderer::new);
    }

    @SubscribeEvent
    public static void onAfterRenderLevel(RenderLevelStageEvent.AfterLevel event)
    {
        PlacementPreviewRenderer.onRenderLevel(event);
    }

    @SubscribeEvent
    public static void onKeyInput(InputEvent.Key event)
    {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || mc.screen != null) return;
        if (KeyMappings.EATING_MODE_KEY.consumeClick())
        {
            boolean current = EatingModeHelper.isEnabled(mc.player);
            EatingModeHelper.setEnabled(mc.player, !current);
            ClientPacketDistributor.sendToServer(new EatingModePayload(!current));
        }
    }

    @SubscribeEvent
    public static void registerGuiLayers(RegisterGuiLayersEvent event)
    {
        event.registerAboveAll(GridSnapHudOverlay.LAYER_ID, new GridSnapHudOverlay());
        event.registerAboveAll(ContainerFoodHudOverlay.LAYER_ID, new ContainerFoodHudOverlay());
    }

    @SubscribeEvent
    public static void onMouseScroll(InputEvent.MouseScrollingEvent event)
    {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || mc.screen != null) return;

        Window window = mc.getWindow();
        boolean altDown = InputConstants.isKeyDown(window, InputConstants.KEY_LALT)
        || InputConstants.isKeyDown(window, InputConstants.KEY_RALT);
        if (!altDown) return;

        double delta = event.getScrollDeltaY();
        if (delta == 0) return;

        event.setCanceled(true);

        int current = GridSnapHelper.getGridStep(mc.player);
        int next = delta > 0
        ? GridSnapHelper.cyclePrev(current)
        : GridSnapHelper.cycleNext(current);

        GridSnapHelper.setGridStep(mc.player, next);
        ClientPacketDistributor.sendToServer(new GridSnapPayload(next));
    }
}
