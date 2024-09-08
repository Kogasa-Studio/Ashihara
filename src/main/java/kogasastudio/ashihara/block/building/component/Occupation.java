package kogasastudio.ashihara.block.building.component;

import net.minecraft.core.Direction;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public enum Occupation
{
    F1_N("f1_n"),
    F1_NE("f1_ne"),
    F1_E("f1_e"),
    F1_SE("f1_se"),
    F1_S("f1_s"),
    F1_SW("f1_sw"),
    F1_W("f1_w"),
    F1_NW("f1_nw"),
    F1_C("f1_c"),
    F2_N("f2_n"),
    F2_NE("f2_ne"),
    F2_E("f2_e"),
    F2_SE("f2_se"),
    F2_S("f2_s"),
    F2_SW("f2_sw"),
    F2_W("f2_w"),
    F2_NW("f2_nw"),
    F2_C("f2_c"),
    F3_N("f3_n"),
    F3_NE("f3_ne"),
    F3_E("f3_e"),
    F3_SE("f3_se"),
    F3_S("f3_s"),
    F3_SW("f3_sw"),
    F3_W("f3_w"),
    F3_NW("f3_nw"),
    F3_C("f3_c"),
    F4_N("f4_n"),
    F4_NE("f4_ne"),
    F4_E("f4_e"),
    F4_SE("f4_se"),
    F4_S("f4_s"),
    F4_SW("f4_sw"),
    F4_W("f4_w"),
    F4_NW("f4_nw"),
    F4_C("f4_c");

    public static final Map<String, Occupation> OCCUPATION_MAP = new HashMap<>();
    static
    {
        for (Occupation entry : Occupation.values())
        {
            OCCUPATION_MAP.put(entry.getId(), entry);
        }
    }

    public static final Occupation[][][] MESHED_OCCUPATIONS = //y
    {
        { //z
            {F1_NW, F1_N, F1_NE}, //x
            {F1_W, F1_C, F1_E},
            {F1_SW, F1_S, F1_SE}
        },
        {
            {F2_NW, F2_N, F2_NE},
            {F2_W, F2_C, F2_E},
            {F2_SW, F2_S, F2_SE}
        },
        {
            {F3_NW, F3_N, F3_NE},
            {F3_W, F3_C, F3_E},
            {F3_SW, F3_S, F3_SE}
        },
        {
            {F4_NW, F4_N, F4_NE},
            {F4_W, F4_C, F4_E},
            {F4_SW, F4_S, F4_SE}
        }
    };

    public static final List<Occupation> ALL = List.of(Occupation.values());
    public static final List<Occupation> CENTER_ALL = List.of(Occupation.F1_C, Occupation.F2_C, Occupation.F3_C, Occupation.F4_C);
    public static final List<Occupation> N_ALL = List.of(Occupation.F1_N, Occupation.F2_N, Occupation.F3_N, Occupation.F4_N);
    public static final List<Occupation> NW_ALL = List.of(Occupation.F1_NW, Occupation.F2_NW, Occupation.F3_NW, Occupation.F4_NW);
    public static final List<Occupation> W_ALL = List.of(Occupation.F1_W, Occupation.F2_W, Occupation.F3_W, Occupation.F4_W);
    public static final List<Occupation> SW_ALL = List.of(Occupation.F1_SW, Occupation.F2_SW, Occupation.F3_SW, Occupation.F4_SW);
    public static final List<Occupation> S_ALL = List.of(Occupation.F1_S, Occupation.F2_S, Occupation.F3_S, Occupation.F4_S);
    public static final List<Occupation> SE_ALL = List.of(Occupation.F1_SE, Occupation.F2_SE, Occupation.F3_SE, Occupation.F4_SE);
    public static final List<Occupation> E_ALL = List.of(Occupation.F1_E, Occupation.F2_E, Occupation.F3_E, Occupation.F4_E);
    public static final List<Occupation> NE_ALL = List.of(Occupation.F1_NE, Occupation.F2_NE, Occupation.F3_NE, Occupation.F4_NE);

    public static boolean join(List<Occupation> accepter, List<Occupation> candidate)
    {
        return accepter.stream().noneMatch(candidate::contains);
    }

    public static List<Occupation> getEdged(Direction direction)
    {
        return switch (direction)
        {
            case NORTH -> N_ALL;
            case WEST -> W_ALL;
            case EAST -> E_ALL;
            default -> S_ALL;
        };
    }

    public static List<Occupation> getCornered(Direction n_or_s, Direction w_or_e)
    {
        if (n_or_s == Direction.NORTH)
        {
            return w_or_e == Direction.WEST ? NW_ALL : NE_ALL;
        }
        else return w_or_e == Direction.WEST ? SW_ALL : SE_ALL;
    }

    public static Occupation mapPosition(double x, double y, double z)
    {
        int xIndex = (int) Math.floor(x * 3);
        int yIndex = (int) Math.floor(y * 3);
        int zIndex = (int) Math.floor(z * 3);

        return MESHED_OCCUPATIONS[yIndex][zIndex][xIndex];
    }

    public String getId() {return id;}

    private final String id;

    Occupation(String idIn)
    {
        this.id = idIn;
    }
}
