package kogasastudio.ashihara.block.furniture;

import kogasastudio.ashihara.block.building.component.ComponentStateDefinition;
import kogasastudio.ashihara.block.building.BaseMultiBuiltBlock;
import kogasastudio.ashihara.block.building.component.Interactable;
import kogasastudio.ashihara.block.blockentity.MultiBuiltBlockEntity;
import kogasastudio.ashihara.helper.ShapeHelper;
import kogasastudio.ashihara.registry.AdditionalModels;
import kogasastudio.ashihara.registry.BuildingComponents;
import kogasastudio.ashihara.registry.Blocks;
import kogasastudio.ashihara.registry.FurnitureComponents;
import kogasastudio.ashihara.utils.BuildingComponentModelResourceLocation;
import net.minecraft.core.BlockBox;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import javax.annotation.Nullable;
import java.util.List;
import java.util.function.Supplier;

public class BambooCurtainComponent extends FurnitureComponent implements Interactable, MultiBlockFurniture, ICustomData
{
    enum State { HEAD, BODY, TAIL, HEAD_ROLLED, TAIL_ROLLED }

    private final VoxelShape headOrBody, tail, rolled;

    public BambooCurtainComponent(String idIn, BuildingComponents.Type typeIn, Supplier<BaseMultiBuiltBlock> mat, List<ItemStack> drops, FurnitureRenderPass pass)
    {
        super(idIn, typeIn, mat, drops, pass);
        headOrBody = Shapes.join(Shapes.empty(), Shapes.box(0, 0, 0.46875, 1, 1, 0.53125), BooleanOp.OR);
        tail       = Shapes.join(Shapes.empty(), Shapes.box(0, 0.1875, 0.46875, 1, 1, 0.53125), BooleanOp.OR);
        rolled     = Shapes.or(Shapes.box(0, -0.0625, 0.40625, 1, 0.3125, 0.78125), Shapes.box(0, 0.3125, 0.46875, 1, 1, 0.53125));
        this.yMinRange = -1.0f;
        this.yMaxRange = 0.0f;
    }

    // ���� ICustomData ������������������������������������������������������������������������������������

    @Override public void serializeCustom(ValueOutput out, Object data) { out.putString("state", (String) data); }
    @Override public Object deserializeCustom(ValueInput in) { return in.getStringOr("state", State.HEAD_ROLLED.name()); }

    // ���� MultiBlockFurniture ��������������������������������������������������������������������

    @Override public VoxelShape getBaseShape() { return null; }

    @Override public BlockBox getExtent(Direction f)
    {
        return new BlockBox(new BlockPos(-1, -1, -1), new BlockPos(1, 1, 1));
    }

    @Override public VoxelShape rebuildShape(Vec3 ib, float rx, float ry, float rz)
    {
        VoxelShape s = ry != 0 ? ShapeHelper.rotateShape(this.rolled, -ry) : this.rolled;
        return ShapeHelper.sliceShape(ShapeHelper.offsetShape(s, ib.x(), ib.y(), ib.z()), 1, net.minecraft.core.Vec3i.ZERO);
    }

    // ���� Lifecycle ����������������������������������������������������������������������������������������

    @Override public void onPlaced(MultiBuiltBlockEntity be, ComponentStateDefinition def) { rebuildProxies(be, def); }

    @Override public void onRemoved(MultiBuiltBlockEntity be, ComponentStateDefinition def)
    {
        Level lv = be.getLevel();
        if (lv == null) return;
        VoxelShape maxF = ShapeHelper.offsetShape(ShapeHelper.rotateShape(this.rolled, -def.rotationY()), def.inBlockPos().x(), def.inBlockPos().y(), def.inBlockPos().z());
        var bb = maxF.bounds();
        BlockPos mp = be.getBlockPos();
        Vec3 mib = def.inBlockPos();
        for (int x = (int) Math.floor(bb.minX); x <= (int) Math.ceil(bb.maxX) - 1; x++)
        {
            for (int y = (int) Math.floor(bb.minY); y <= (int) Math.ceil(bb.maxY) - 1; y++)
            {
                for (int z = (int) Math.floor(bb.minZ); z <= (int) Math.ceil(bb.maxZ) - 1; z++)
                {
                    if (x == 0 && y == 0 && z == 0) continue;
                    BlockPos t = mp.offset(x, y, z);
                    if (lv.getBlockEntity(t) instanceof MultiBuiltBlockEntity mb) clearProxies(mb, mp, mib);
                }
            }
        }
    }

    // ���� definite ������������������������������������������������������������������������������������������

    @Override public ComponentStateDefinition definite(MultiBuiltBlockEntity beIn, UseOnContext c)
    {
        Vec3 ib = beIn.inBlockVec(c.getClickLocation());
        double x = clampInBlock(ib.x() - 0.5, -0.5, 0.5);
        double y = clampInBlock(ib.y() - 1.0, -1.0, 0.0);
        double z = clampInBlock(ib.z() - 0.5, -0.5, 0.5);
        Direction d = c.getHorizontalDirection();
        float r = switch (d) { case WEST -> 270; case SOUTH -> 0; case EAST -> 90; default -> 180; };
        VoxelShape s = ShapeHelper.offsetShape(ShapeHelper.rotateShape(this.rolled, -r), x, y, z);
        return new ComponentStateDefinition(FurnitureComponents.get(id), new Vec3(x, y, z), 0, r, 0, s, AdditionalModels.BAMBOO_CURTAIN_HEAD_ROLLED, List.of(), State.HEAD_ROLLED.name());
    }

    // ���� Interaction ������������������������������������������������������������������������������������

    @Override public ComponentStateDefinition handleInteraction(UseOnContext c, ComponentStateDefinition def)
    {
        var be = (MultiBuiltBlockEntity) c.getLevel().getBlockEntity(c.getClickedPos());
        if (be == null) return def;
        var p = c.getPlayer();
        if (p == null || !c.getItemInHand().isEmpty()) return def;
        State s = stateOf(def);
        boolean shift = p.isShiftKeyDown();
        if (shift)
        {
            return switch (s)
            {
                case TAIL        -> setState(def, State.TAIL_ROLLED, AdditionalModels.BAMBOO_CURTAIN_TAIL_ROLLED, this.rolled);
                case TAIL_ROLLED -> setState(def, State.TAIL, AdditionalModels.BAMBOO_CURTAIN_TAIL, this.tail);
                default          -> def;
            };
        }
        return switch (s)
        {
            case HEAD         -> shrinkHead(be, def);
            case BODY         -> shrinkBody(be, def);
            case TAIL        -> extend(be, def, State.BODY);
            case HEAD_ROLLED -> extend(be, def, State.HEAD);
            default          -> def;
        };
    }

    @Override public SoundType getInteractSound() { return SoundType.BAMBOO; }

    // ���� Proxy management ��������������������������������������������������������������������������

    private void rebuildProxies(MultiBuiltBlockEntity be, ComponentStateDefinition def)
    {
        Level lv = be.getLevel();
        if (lv == null) return;
        State s = stateOf(def);
        VoxelShape cur = s == State.TAIL_ROLLED || s == State.HEAD_ROLLED ? this.rolled : s == State.TAIL ? this.tail : this.headOrBody;
        VoxelShape curF = ShapeHelper.offsetShape(ShapeHelper.rotateShape(cur, -def.rotationY()), def.inBlockPos().x(), def.inBlockPos().y(), def.inBlockPos().z());
        VoxelShape maxF = ShapeHelper.offsetShape(ShapeHelper.rotateShape(this.rolled, -def.rotationY()), def.inBlockPos().x(), def.inBlockPos().y(), def.inBlockPos().z());
        var bb = maxF.bounds();
        BlockPos mp = be.getBlockPos();
        Vec3 mib = def.inBlockPos();
        for (int x = (int) Math.floor(bb.minX); x <= (int) Math.ceil(bb.maxX) - 1; x++)
        {
            for (int y = (int) Math.floor(bb.minY); y <= (int) Math.ceil(bb.maxY) - 1; y++)
            {
                for (int z = (int) Math.floor(bb.minZ); z <= (int) Math.ceil(bb.maxZ) - 1; z++)
                {
                    if (x == 0 && y == 0 && z == 0) continue;
                    BlockPos t = mp.offset(x, y, z);
                    VoxelShape sl = ShapeHelper.sliceShape(curF, 1, new net.minecraft.core.Vec3i(x, y, z));
                    if (sl.isEmpty())
                    {
                        if (lv.getBlockEntity(t) instanceof MultiBuiltBlockEntity mb)
                        {
                            mb.FURNITURE.removeIf(dd ->
                            {
                                if (matchProxy(dd, mp, mib, t)) { mb.setChanged(); return true; }
                                return false;
                            });
                            mb.refresh();
                        }
                    }
                    else ensureProxy(lv, t, mp, mib, sl, def.model(), x, y, z);
                }
            }
        }
    }

    private static boolean matchProxy(ComponentStateDefinition dd, BlockPos mainPos, Vec3 mainIb, BlockPos proxyPos)
    {
        var pd = FurnitureProxyComponent.getData(dd);
        return pd != null && pd.resolveMain(proxyPos).equals(mainPos) && pd.mainInBlockPos().distanceToSqr(mainIb) < 0.0001;
    }

    private void ensureProxy(Level lv, BlockPos pos, BlockPos mainPos, Vec3 mainIb, VoxelShape sl, BuildingComponentModelResourceLocation m, int dx, int dy, int dz)
    {
        MultiBuiltBlockEntity mb;
        if (lv.getBlockEntity(pos) instanceof MultiBuiltBlockEntity e) mb = e;
        else
        {
            if (!lv.getBlockState(pos).canBeReplaced()) return;
            lv.setBlock(pos, Blocks.MULTI_BUILT_BLOCK.get().defaultBlockState(), 3);
            mb = (MultiBuiltBlockEntity) lv.getBlockEntity(pos);
        }
        if (mb == null) return;
        var pd = new FurnitureProxyComponent.ProxyData(-dx, -dy, -dz, mainIb);
        for (int i = 0; i < mb.FURNITURE.size(); i++)
        {
            var d = mb.FURNITURE.get(i);
            if (FurnitureProxyComponent.isProxy(d) && matchProxy(d, mainPos, mainIb, pos))
            {
                mb.FURNITURE.set(i, new ComponentStateDefinition(FurnitureComponents.FURNITURE_PROXY, Vec3.ZERO, 0, 0, 0, sl, m, List.of(), pd));
                mb.refresh();
                return;
            }
        }
        mb.FURNITURE.add(new ComponentStateDefinition(FurnitureComponents.FURNITURE_PROXY, Vec3.ZERO, 0, 0, 0, sl, m, List.of(), pd));
        mb.refresh();
    }

    private static void clearProxies(MultiBuiltBlockEntity be, BlockPos mp, Vec3 ib)
    {
        be.FURNITURE.removeIf(dd -> matchProxy(dd, mp, ib, be.getBlockPos()));
        be.refresh();
    }

    // ���� Extend / shrink ����������������������������������������������������������������������������

    private ComponentStateDefinition extend(MultiBuiltBlockEntity be, ComponentStateDefinition def, State ns)
    {
        var tr = findTail(be, def);
        if (tr == null) return def;
        BlockPos bw = tr.tailBe.getBlockPos().below();
        if (!canExtendTo(tr.tailBe, bw, tr.tailDef.inBlockPos())) return def;
        var nt = makeTail(tr.tailDef);
        var sb = getOrCreateBE(tr.tailBe, bw);
        if (sb == null) return def;
        addCurtain(sb, nt);
        if (tr.tailBe != be)
        {
            var bd = setState(tr.tailDef, State.BODY, AdditionalModels.BAMBOO_CURTAIN_BODY, this.headOrBody);
            replaceCurtain(tr.tailBe, tr.tailDef, bd);
        }
        return setState(def, ns, ns == State.HEAD ? AdditionalModels.BAMBOO_CURTAIN_HEAD : AdditionalModels.BAMBOO_CURTAIN_BODY, this.headOrBody);
    }

    private ComponentStateDefinition shrinkHead(MultiBuiltBlockEntity be, ComponentStateDefinition def)
    {
        Level lv = be.getLevel();
        BlockPos c = be.getBlockPos().below();
        while (lv.getBlockEntity(c) instanceof MultiBuiltBlockEntity mb)
        {
            var cr = findCurtain(mb, def.inBlockPos());
            if (cr == null) break;
            removeCurtain(mb, cr);
            c = c.below();
        }
        return setState(def, State.HEAD_ROLLED, AdditionalModels.BAMBOO_CURTAIN_HEAD_ROLLED, this.rolled);
    }

    private ComponentStateDefinition shrinkBody(MultiBuiltBlockEntity be, ComponentStateDefinition def)
    {
        Level lv = be.getLevel();
        BlockPos c = be.getBlockPos();
        ComponentStateDefinition lastDef = def;
        MultiBuiltBlockEntity lastBe = be;
        int steps = 0;
        while (steps < 16)
        {
            c = c.below();
            steps++;
            if (!(lv.getBlockEntity(c) instanceof MultiBuiltBlockEntity mb)) break;
            var nx = findCurtain(mb, def.inBlockPos());
            if (nx == null) break;
            State ns = stateOf(nx);
            if (ns == State.TAIL_ROLLED) return def;
            if (ns == State.TAIL)
            {
                removeCurtain(mb, nx);
                if (lastBe == be)
                {
                    return setState(def, State.TAIL, AdditionalModels.BAMBOO_CURTAIN_TAIL, this.tail);
                }
                replaceCurtain(lastBe, lastDef, setState(lastDef, State.TAIL, AdditionalModels.BAMBOO_CURTAIN_TAIL, this.tail));
                return setState(def, State.BODY, def.model(), this.headOrBody);
            }
            lastDef = nx;
            lastBe = mb;
        }
        return def;
    }

    // ���� Curtain helpers ����������������������������������������������������������������������������

    private static void addCurtain(MultiBuiltBlockEntity be, ComponentStateDefinition def)
    {
        be.FURNITURE.add(def);
        if (def.component() instanceof FurnitureComponent fc) fc.onPlaced(be, def);
        be.refresh();
    }

    private static void removeCurtain(MultiBuiltBlockEntity be, ComponentStateDefinition def)
    {
        if (def.component() instanceof FurnitureComponent fc) fc.onRemoved(be, def);
        be.FURNITURE.remove(def);
        be.refresh();
    }

    private static void replaceCurtain(MultiBuiltBlockEntity be, ComponentStateDefinition o, ComponentStateDefinition n)
    {
        if (o.component() instanceof FurnitureComponent fc) fc.onRemoved(be, o);
        if (n.component() instanceof FurnitureComponent fc) fc.onPlaced(be, n);
        be.FURNITURE.replaceAll(dd -> dd == o ? n : dd);
        be.refresh();
    }

    private static TailResult findTail(MultiBuiltBlockEntity be, ComponentStateDefinition startDef)
    {
        var l = be.getLevel();
        BlockPos c = be.getBlockPos();
        var last = startDef;
        var lb = be;
        while (true)
        {
            c = c.below();
            if (!(l.getBlockEntity(c) instanceof MultiBuiltBlockEntity mb)) break;
            var nx = findCurtain(mb, startDef.inBlockPos());
            if (nx == null) break;
            State ns = stateOf(nx);
            if (ns == State.TAIL || ns == State.TAIL_ROLLED) { last = nx; lb = mb; break; }
            last = nx; lb = mb;
        }
        return new TailResult(last, lb);
    }

    private record TailResult(ComponentStateDefinition tailDef, MultiBuiltBlockEntity tailBe) {}

    public static void onChainBreak(MultiBuiltBlockEntity be, ComponentStateDefinition def)
    {
        var l = be.getLevel();
        BlockPos c = be.getBlockPos().below();
        while (l.getBlockEntity(c) instanceof MultiBuiltBlockEntity mb)
        {
            var cr = findCurtain(mb, def.inBlockPos());
            if (cr == null) break;
            removeCurtain(mb, cr);
            c = c.below();
        }
        BlockPos a = be.getBlockPos().above();
        if (l.getBlockEntity(a) instanceof MultiBuiltBlockEntity mb)
        {
            var ad = findCurtain(mb, def.inBlockPos());
            if (ad != null)
            {
                State as = stateOf(ad);
                if (as == State.BODY) replaceCurtain(mb, ad, setState(ad, State.TAIL, AdditionalModels.BAMBOO_CURTAIN_TAIL, ((BambooCurtainComponent) ad.component()).tail));
                else if (as == State.HEAD) replaceCurtain(mb, ad, setState(ad, State.HEAD_ROLLED, AdditionalModels.BAMBOO_CURTAIN_HEAD_ROLLED, ((BambooCurtainComponent) ad.component()).rolled));
            }
        }
    }

    private boolean canExtendTo(MultiBuiltBlockEntity be, BlockPos t, Vec3 chainIb)
    {
        var l = be.getLevel();
        if (l == null) return false;
        if (l.getBlockEntity(t) instanceof MultiBuiltBlockEntity mb)
        {
            var existing = findCurtain(mb, chainIb);
            if (existing != null) return false;
            return !hasFloor(mb);
        }
        return l.getBlockState(t).canBeReplaced();
    }

    private boolean canExtendTo(MultiBuiltBlockEntity be, BlockPos t)
    {
        return canExtendTo(be, t, Vec3.ZERO);
    }

    @Nullable private static ComponentStateDefinition findCurtain(MultiBuiltBlockEntity be)
    {
        for (var d : be.FURNITURE) if (d.component() instanceof BambooCurtainComponent) return d;
        return null;
    }

    @Nullable private static ComponentStateDefinition findCurtain(MultiBuiltBlockEntity be, Vec3 inBlockPos)
    {
        for (var d : be.FURNITURE)
            if (d.component() instanceof BambooCurtainComponent && d.inBlockPos().distanceToSqr(inBlockPos) < 0.0001)
                return d;
        return null;
    }

    private static boolean hasFloor(MultiBuiltBlockEntity be)
    {
        for (var d : be.COMPONENTS) if (d.component().id.contains("floor")) return true;
        return false;
    }

    @Nullable private static MultiBuiltBlockEntity getOrCreateBE(MultiBuiltBlockEntity be, BlockPos p)
    {
        var l = be.getLevel();
        if (l.getBlockEntity(p) instanceof MultiBuiltBlockEntity mb) return mb;
        if (!l.getBlockState(p).canBeReplaced()) return null;
        l.setBlock(p, Blocks.MULTI_BUILT_BLOCK.get().defaultBlockState(), 3);
        return (MultiBuiltBlockEntity) l.getBlockEntity(p);
    }

    // ���� State helpers ��������������������������������������������������������������������������������

    private static State stateOf(ComponentStateDefinition def)
    {
        try { return State.valueOf((String) def.customData()); }
        catch (Exception e) { return State.HEAD_ROLLED; }
    }

    private static ComponentStateDefinition setState(ComponentStateDefinition def, State s, BuildingComponentModelResourceLocation m, VoxelShape rw)
    {
        VoxelShape full = ShapeHelper.offsetShape(ShapeHelper.rotateShape(rw, -def.rotationY()), def.inBlockPos().x(), def.inBlockPos().y(), def.inBlockPos().z());
        VoxelShape sliced = ShapeHelper.sliceShape(full, 1, net.minecraft.core.Vec3i.ZERO);
        return new ComponentStateDefinition(def.component(), def.inBlockPos(), def.rotationX(), def.rotationY(), def.rotationZ(), sliced, m, def.occupation(), s.name());
    }

    private ComponentStateDefinition makeTail(ComponentStateDefinition src)
    {
        VoxelShape full = ShapeHelper.offsetShape(ShapeHelper.rotateShape(this.tail, -src.rotationY()), src.inBlockPos().x(), src.inBlockPos().y(), src.inBlockPos().z());
        VoxelShape sliced = ShapeHelper.sliceShape(full, 1, net.minecraft.core.Vec3i.ZERO);
        return new ComponentStateDefinition(FurnitureComponents.get(id), new Vec3(src.inBlockPos().x(), src.inBlockPos().y(), src.inBlockPos().z()), src.rotationX(), src.rotationY(), src.rotationZ(), sliced, AdditionalModels.BAMBOO_CURTAIN_TAIL, List.of(), State.TAIL.name());
    }
}
