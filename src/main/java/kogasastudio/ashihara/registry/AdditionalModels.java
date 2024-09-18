package kogasastudio.ashihara.registry;

import kogasastudio.ashihara.Ashihara;
import kogasastudio.ashihara.utils.BuildingComponentModelResourceLocation;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ModelEvent;

import java.util.ArrayList;
import java.util.List;

@EventBusSubscriber(bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class AdditionalModels
{
    public static final String STANDALONE_VARIANT = "standalone";
    private static final List<BuildingComponentModelResourceLocation> models = new ArrayList<>();

    //Structural
    public static final BuildingComponentModelResourceLocation RED_THICK_COLUMN = register("block/building_blocks/column/column_thick_red");
    public static final BuildingComponentModelResourceLocation RED_THICK_COLUMN_SHORT = register("block/components/red_thick_column_short");
    public static final BuildingComponentModelResourceLocation RED_COLUMN = register("block/components/red_column");
    public static final BuildingComponentModelResourceLocation RED_COLUMN_SHORT = register("block/components/red_column_short");

    public static final BuildingComponentModelResourceLocation RED_BEAM = register("block/components/red_beam");
    public static final BuildingComponentModelResourceLocation RED_BEAM_L = register("block/components/red_beam_l");
    public static final BuildingComponentModelResourceLocation RED_BEAM_R = register("block/components/red_beam_r");
    public static final BuildingComponentModelResourceLocation RED_BEAM_A = register("block/components/red_beam_a");

    public static final BuildingComponentModelResourceLocation RED_HIJIKI_L = register("block/components/red_hijiki_l");
    public static final BuildingComponentModelResourceLocation RED_HIJIKI_R = register("block/components/red_hijiki_r");
    public static final BuildingComponentModelResourceLocation RED_HIJIKI_A = register("block/components/red_hijiki_a");
    public static final BuildingComponentModelResourceLocation RED_HIJIKI_SUPPORTER = register("block/components/red_hijiki_supporter");
    public static final BuildingComponentModelResourceLocation RED_HIJIKI_LONG = register("block/components/red_hijiki_long");
    public static final BuildingComponentModelResourceLocation RED_HIJIKI_CORNER_NORMAL = register("block/components/red_hijiki_cornered_normal");
    public static final BuildingComponentModelResourceLocation RED_HIJIKI_CORNER_NORMAL_LONG = register("block/components/red_hijiki_cornered_normal_long");
    public static final BuildingComponentModelResourceLocation RED_HIJIKI_CORNER_LONG = register("block/components/red_hijiki_cornered_long");

    public static final BuildingComponentModelResourceLocation RED_RAFTER_STICKING_BEAM = register("block/components/red_rafter_sticking_beam");

    public static final BuildingComponentModelResourceLocation RED_TOU = register("block/components/red_tou");
    public static final BuildingComponentModelResourceLocation RED_BIG_TOU = register("block/components/red_big_tou");
    public static final BuildingComponentModelResourceLocation RED_ONI_TOU = register("block/components/red_tou_oni");

    //Additional
    public static final BuildingComponentModelResourceLocation BASE_STONE_0 = register("block/components/base_stone_0");
    public static final BuildingComponentModelResourceLocation BASE_STONE_1 = register("block/components/base_stone_1");
    public static final BuildingComponentModelResourceLocation BASE_STONE_2 = register("block/components/base_stone_2");

    public static final BuildingComponentModelResourceLocation BAMBOO_WALL_BONES = register("block/components/bamboo_wall_bones");
    public static final BuildingComponentModelResourceLocation BAMBOO_WALL_BONES_QUARTER = register("block/components/bamboo_wall_bones_quarter");
    public static final BuildingComponentModelResourceLocation RAMMED_SOIL_WALL = register("block/components/rammed_soil_wall");
    public static final BuildingComponentModelResourceLocation RAMMED_SOIL_WALL_QUARTER = register("block/components/rammed_soil_wall_quarter");
    public static final BuildingComponentModelResourceLocation WHITE_SOIL_WALL = register("block/components/white_soil_wall");
    public static final BuildingComponentModelResourceLocation WHITE_SOIL_WALL_QUARTER = register("block/components/white_soil_wall_quarter");

    public static final BuildingComponentModelResourceLocation RED_HANGING_STICKER = register("block/components/red_hanging_sticker");
    public static final BuildingComponentModelResourceLocation RED_HANGING_STICKER_END = register("block/components/red_hanging_sticker_end");
    public static final BuildingComponentModelResourceLocation RED_HANGING_STICKER_CORNER = register("block/components/red_hanging_sticker_corner");
    public static final BuildingComponentModelResourceLocation RED_HANGING_STICKER_END_CORNER = register("block/components/red_hanging_sticker_end_corner");

    public static final BuildingComponentModelResourceLocation RED_PLANKS_FLOOR = register("block/components/red_planks_floor");
    public static final BuildingComponentModelResourceLocation RED_PLANKS_FLOOR_QUARTER = register("block/components/red_planks_floor_quarter");
    public static final BuildingComponentModelResourceLocation RED_LATTICED_CEILING_WHITE = register("block/components/red_latticed_ceiling_white");
    public static final BuildingComponentModelResourceLocation RED_LATTICED_CEILING_WHITE_QUARTER = register("block/components/red_latticed_ceiling_white_quarter");

    public static final BuildingComponentModelResourceLocation RED_BENDED_STICKERS = register("block/components/red_bended_stickers");

    public static final BuildingComponentModelResourceLocation RED_STEEP_RAFTER = register("block/components/red_steep_rafter");
    public static final BuildingComponentModelResourceLocation RED_SMOOTH_RAFTER = register("block/components/red_smooth_rafter");
    public static final BuildingComponentModelResourceLocation RED_RAFTER_END = register("block/components/red_rafter_end");
    public static final BuildingComponentModelResourceLocation RED_RAFTER_CONNECTOR = register("block/components/red_rafter_connector");

    public static final BuildingComponentModelResourceLocation SPRUCE_RAFTER_PLANKS = register("block/components/spruce_rafter_planks");
    public static final BuildingComponentModelResourceLocation WHITE_RAFTER_PLANKS = register("block/components/white_rafter_planks");

    public static final BuildingComponentModelResourceLocation CYPRESS_ROOF = register("block/components/cypress_roof");
    public static final BuildingComponentModelResourceLocation CYPRESS_ROOF_CORNER = register("block/components/cypress_roof_corner");
    public static final BuildingComponentModelResourceLocation CYPRESS_ROOF_EDGE = register("block/components/cypress_roof_edge");
    public static final BuildingComponentModelResourceLocation CYPRESS_ROOF_EDGE_CORNER = register("block/components/cypress_roof_edge_corner");
    public static final BuildingComponentModelResourceLocation CYPRESS_ROOF_HALF = register("block/components/cypress_roof_half");
    public static final BuildingComponentModelResourceLocation CYPRESS_ROOF_QUARTER = register("block/components/cypress_roof_quarter");
    public static final BuildingComponentModelResourceLocation CYPRESS_ROOF_QUARTER_CORNER = register("block/components/cypress_roof_quarter_corner");

    @SubscribeEvent
    public static void onRegisterAdditionalModels(ModelEvent.RegisterAdditional event)
    {
        models.forEach(location -> event.register(location.toModelResourceLocation()));
    }

    private static BuildingComponentModelResourceLocation register(String path)
    {
        BuildingComponentModelResourceLocation rl = new BuildingComponentModelResourceLocation(ResourceLocation.fromNamespaceAndPath(Ashihara.MODID, path), STANDALONE_VARIANT);
        models.add(rl);
        return rl;
    }
}
