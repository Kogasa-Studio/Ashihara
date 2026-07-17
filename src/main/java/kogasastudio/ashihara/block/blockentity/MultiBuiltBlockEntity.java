package kogasastudio.ashihara.block.blockentity;

import kogasastudio.ashihara.block.building.*;
import kogasastudio.ashihara.block.building.component.*;
import kogasastudio.ashihara.block.furniture.BambooCurtainComponent;
import kogasastudio.ashihara.block.furniture.FurnitureComponent;
import kogasastudio.ashihara.block.furniture.FurnitureProxyComponent;
import kogasastudio.ashihara.block.furniture.FurnitureProxyComponent;
import kogasastudio.ashihara.block.furniture.MultiBlockFurniture;
import kogasastudio.ashihara.helper.MathHelper;
import kogasastudio.ashihara.helper.ShapeHelper;
import kogasastudio.ashihara.registry.Blocks;
import kogasastudio.ashihara.registry.FurnitureComponents;
import kogasastudio.ashihara.registry.Items;
import kogasastudio.ashihara.registry.BlockEntities;
import net.minecraft.core.BlockBox;
import net.minecraft.core.Direction;
import kogasastudio.ashihara.utils.shape.VoxelShapeSerializer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("NullableProblems")
public class MultiBuiltBlockEntity extends AshiharaCommonBE implements IMultiBuiltBlock
{
    public static final int OPCODE_COMPONENT = 0;
    public static final int OPCODE_ADDITIONAL = 1;
    public static final int OPCODE_READALL = 2;
    public static final int OPCODE_FURNITURE = 3;

    public List<ComponentStateDefinition> COMPONENTS = new ArrayList<>();
    public List<ComponentStateDefinition> ADDITIONAL_COMPONENTS = new ArrayList<>();
    public List<ComponentStateDefinition> FURNITURE = new ArrayList<>();
    public List<Occupation> occupationCache = new ArrayList<>();

    public MultiBuiltBlockEntity(BlockPos pPos, BlockState pBlockState)
    {
        super(BlockEntities.MULTI_BUILT_BLOCKENTITY.get(), pPos, pBlockState);
    }

    /** Create a lightweight phantom MBE for preview / pre-check purposes. */
    public static MultiBuiltBlockEntity makePhantom(BlockPos pos)
    {
        return new MultiBuiltBlockEntity(pos, Blocks.MULTI_BUILT_BLOCK.get().defaultBlockState());
    }

    public boolean tryPlace(UseOnContext context, BuildingComponent component)
    {
        boolean flag = false;
        ComponentStateDefinition definition = component.definite(this, context);
        if (definition != null)
        {
            if (component instanceof AdditionalComponent)
            {
                boolean canAppend = true;
                for (ComponentStateDefinition def : ADDITIONAL_COMPONENTS)
                {
                    if (def.occupation().hashCode() == definition.occupation().hashCode() && def.equals(definition)) canAppend = false;
                }
                if (canAppend)
                {
                    this.ADDITIONAL_COMPONENTS.add(definition);
                    flag = true;
                }
            }
            else if (Occupation.join(definition.occupation(), this.occupationCache))
            {
                this.COMPONENTS.add(definition);
                flag = true;
            }
        }
        if (flag)
        {
            refresh();
            SoundEvent event = definition.component().getSoundType().getPlaceSound();
            this.level.playSound(null, this.worldPosition, event, SoundSource.BLOCKS, 1.0f, 1.0f);
            return true;
        }
        return false;
    }

    public boolean tryPlaceFurniture(UseOnContext context, FurnitureComponent component)
    {
        return tryPlaceFurniture(context, component, false);
    }

    public boolean tryPlaceFurniture(UseOnContext context, FurnitureComponent component, boolean simulate)
    {
        ComponentStateDefinition definition = component.definite(this, context);
        if (definition != null)
        {
            definition = FurnitureComponent.tryNudge(this.FURNITURE, definition);
            if (definition == null) return false;

            // Multi-block: dynamic extent from shape bounds, filter empty slices
            VoxelShape fullShape = definition.shape();
            boolean isMultiBlock = component instanceof MultiBlockFurniture;
            if (isMultiBlock)
            {
                var bb = fullShape.bounds();
                int x0 = (int) Math.floor(bb.minX), x1 = (int) Math.ceil(bb.maxX) - 1;
                int y0 = (int) Math.floor(bb.minY), y1 = (int) Math.ceil(bb.maxY) - 1;
                int z0 = (int) Math.floor(bb.minZ), z1 = (int) Math.ceil(bb.maxZ) - 1;

                // Placement check: only blocks with non-empty slices
                for (int x = x0; x <= x1; x++)
                    for (int y = y0; y <= y1; y++)
                        for (int z = z0; z <= z1; z++)
                        {
                            if (x == 0 && y == 0 && z == 0) continue;
                            if (ShapeHelper.sliceShape(fullShape, 1, new net.minecraft.core.Vec3i(x, y, z)).isEmpty())
                                continue;
                            BlockPos target = this.worldPosition.offset(x, y, z);
                            if (this.level.getBlockEntity(target) instanceof MultiBuiltBlockEntity)
                                continue;
                            if (!this.level.getBlockState(target).canBeReplaced())
                                return false;
                        }
            }

            if (simulate) return true;

            // Slice origin
            if (isMultiBlock)
            {
                VoxelShape originSlice = ShapeHelper.sliceShape(fullShape, 1, net.minecraft.core.Vec3i.ZERO);
                definition = new ComponentStateDefinition(definition.component(), definition.inBlockPos(),
                    definition.rotationX(), definition.rotationY(), definition.rotationZ(),
                    originSlice, definition.model(), definition.occupation(), definition.customData());
            }

            this.FURNITURE.add(definition);
            refresh();

            // Place proxy components only where slice is non-empty
            if (isMultiBlock)
            {
                var bb = fullShape.bounds();
                int x0 = (int) Math.floor(bb.minX), x1 = (int) Math.ceil(bb.maxX) - 1;
                int y0 = (int) Math.floor(bb.minY), y1 = (int) Math.ceil(bb.maxY) - 1;
                int z0 = (int) Math.floor(bb.minZ), z1 = (int) Math.ceil(bb.maxZ) - 1;
                for (int x = x0; x <= x1; x++)
                    for (int y = y0; y <= y1; y++)
                        for (int z = z0; z <= z1; z++)
                        {
                            if (x == 0 && y == 0 && z == 0) continue;
                            VoxelShape slice = ShapeHelper.sliceShape(fullShape, 1, new net.minecraft.core.Vec3i(x, y, z));
                            if (slice.isEmpty()) continue;
                            BlockPos target = this.worldPosition.offset(x, y, z);
                            var proxyData = new FurnitureProxyComponent.ProxyData(-x, -y, -z, definition.inBlockPos());
                            var proxy = new ComponentStateDefinition(FurnitureComponents.FURNITURE_PROXY, new Vec3(0, 0, 0), 0, 0, 0, slice, definition.model(), List.of(), proxyData);
                            if (this.level.getBlockEntity(target) instanceof MultiBuiltBlockEntity subBe)
                            {
                                subBe.FURNITURE.add(proxy);
                                subBe.refresh();
                                continue;
                            }
                            this.level.setBlock(target, Blocks.MULTI_BUILT_BLOCK.get().defaultBlockState(), 3);
                            var subBe = (MultiBuiltBlockEntity) this.level.getBlockEntity(target);
                            if (subBe != null)
                            {
                                subBe.FURNITURE.add(proxy);
                                subBe.refresh();
                            }
                        }
            }

            SoundEvent event = definition.component().getSoundType().getPlaceSound();
            this.level.playSound(null, this.worldPosition, event, SoundSource.BLOCKS, 1.0f, 1.0f);
            return true;
        }
        return false;
    }

    public boolean tryBreak(UseOnContext context)
    {
        Vec3 vec = context.getClickLocation();
        Vec3 inBlockVec = inBlockVec(vec);
        ComponentStateDefinition definition = getComponentByPosition(inBlockVec, OPCODE_READALL);
        if (definition != null)
        {
            return breakComponent(definition, context.getPlayer(), OPCODE_READALL);
        }
        return false;
    }

    public boolean breakComponent(ComponentStateDefinition definition, @Nullable Player player, int opcode)
    {
        // Forward proxy breaks to the main BE
        if (FurnitureProxyComponent.isProxy(definition))
        {
            var data = FurnitureProxyComponent.getData(definition);
            if (data != null && this.level.getBlockEntity(data.resolveMain(this.worldPosition)) instanceof MultiBuiltBlockEntity mainBe)
            {
                for (var def : mainBe.FURNITURE)
                    if (def.inBlockPos().distanceToSqr(data.mainInBlockPos()) < 0.0001)
                        return mainBe.breakComponent(def, player, MultiBuiltBlockEntity.OPCODE_FURNITURE);
            }
            return false;
        }

        // Resolve actual list (READALL returns a copy — must locate the real source)
        int actualOpcode = opcode == OPCODE_READALL ? -1 : opcode;
        if (actualOpcode < 0)
        {
            if (FURNITURE.contains(definition)) actualOpcode = OPCODE_FURNITURE;
            else if (ADDITIONAL_COMPONENTS.contains(definition)) actualOpcode = OPCODE_ADDITIONAL;
            else if (COMPONENTS.contains(definition)) actualOpcode = OPCODE_COMPONENT;
            else return false;
        }
        if (this.getComponents(actualOpcode).contains(definition))
        {
            if (extentFromDef(definition) != null && actualOpcode == OPCODE_FURNITURE)
                removeMultiBlockProxies(definition, extentFromDef(definition));

            if (definition.component() instanceof BambooCurtainComponent)
                BambooCurtainComponent.onChainBreak(this, definition);

            SoundEvent event = definition.component().getSoundType().getBreakSound();
            List<ItemStack> drops = definition.component().getDrops(definition, this);
            Vec3 vec = definition.inBlockPos();
            this.level.playSound(null, this.worldPosition, event, SoundSource.BLOCKS, 1.0f, 1.0f);
            if (this.level.isClientSide() && !drops.getFirst().isEmpty())
            {
                RandomSource random = this.level.getRandom();
                ParticleOptions data = new ItemParticleOption(ParticleTypes.ITEM, drops.getFirst().getItem());
                for (int i = 0; i < 20; i += 1)
                {
                    this.level.addParticle
                    (
                        data,
                        this.getBlockPos().getX() + vec.x,
                        this.getBlockPos().getY() + vec.y,
                        this.getBlockPos().getZ() + vec.z,
                        ((double) random.nextFloat() - 0.5D) * 0.2D,
                        ((double) random.nextFloat() - 0.5D) * 0.2D,
                        ((double) random.nextFloat() - 0.5D) * 0.2D
                    );
                }
            }
            //TODO: 这里玩家为null也通过检测会导致WE移除等也掉落物品。之后靠重写那个啥总之是方块破坏方法也可能是别的破坏方法来完成，现在暂时保留这个bug
            if (player != null && !player.isCreative())
            {
                for (ItemStack item : drops)
                {
                    ItemEntity entity = new ItemEntity(this.level, this.worldPosition.getX() + 0.5d, this.worldPosition.getY() + 0.5d, this.worldPosition.getZ() + 0.5d, item.copy());
                    entity.setDefaultPickUpDelay();
                    this.level.addFreshEntity(entity);
                }
            }
            if (definition.component() instanceof FurnitureComponent fc) fc.onRemoved(this, definition);
            this.getComponents(actualOpcode).remove(definition);
            refresh();
            return true;
        }
        return false;
    }

    public void reloadShape()
    {
        VoxelShape shape = Shapes.empty();
        for (ComponentStateDefinition def : this.COMPONENTS)
        {
            if (def.component().getBaseShape() != null)
            {
                shape = Shapes.or(shape, def.component().rebuildShape(def.inBlockPos(), def.rotationX(), def.rotationY(), def.rotationZ()));
            }
            else
            {
                shape = Shapes.or(shape, def.shape());
            }
        }
        for (ComponentStateDefinition def : this.ADDITIONAL_COMPONENTS)
        {
            if (def.component().getBaseShape() != null)
            {
                shape = Shapes.or(shape, def.component().rebuildShape(def.inBlockPos(), def.rotationX(), def.rotationY(), def.rotationZ()));
            }
            else
            {
                shape = Shapes.or(shape, def.shape());
            }
        }
        for (ComponentStateDefinition def : this.FURNITURE)
        {
            if (def.component().getBaseShape() != null)
            {
                shape = Shapes.or(shape, def.component().rebuildShape(def.inBlockPos(), def.rotationX(), def.rotationY(), def.rotationZ()));
            }
            else
            {
                shape = Shapes.or(shape, def.shape());
            }
        }
        setShape(shape);
    }

    public void reloadOccupation()
    {
        this.occupationCache.clear();
        for (ComponentStateDefinition definition : this.COMPONENTS)
        {
            this.occupationCache.addAll(definition.occupation());
        }
    }

    public void checkConnection()
    {
        boolean flag = false;
        for (int i = 0; i < this.COMPONENTS.size(); i++)
        {
            ComponentStateDefinition definition = this.COMPONENTS.get(i);
            if (definition.component() instanceof Connectable comp)
            {
                this.COMPONENTS.set(i, comp.applyConnection(this, definition));
                flag = true;
            }
        }
        for (int i = 0; i < this.ADDITIONAL_COMPONENTS.size(); i++)
        {
            ComponentStateDefinition definition = this.ADDITIONAL_COMPONENTS.get(i);
            if (definition.component() instanceof Connectable comp)
            {
                this.ADDITIONAL_COMPONENTS.set(i, comp.applyConnection(this, definition));
                flag = true;
            }
        }
        if (flag) refresh();
    }

    public boolean tryInteract(UseOnContext context)
    {
        int opcode = OPCODE_COMPONENT;
        Vec3 vec = context.getClickLocation();
        Vec3 inBlockPos = inBlockVec(vec);
        ComponentStateDefinition definition = getComponentByPosition(inBlockPos, opcode);
        if (definition == null)
        {
            opcode = OPCODE_ADDITIONAL;
            definition = getComponentByPosition(inBlockPos, opcode);
        }
        if (definition == null)
        {
            opcode = OPCODE_FURNITURE;
            definition = getComponentByPosition(inBlockPos, opcode);
        }
        if (definition == null) return false;
        return interactWith(definition, context, opcode);
    }

    public boolean interactWith(ComponentStateDefinition definition, UseOnContext context, int opcode)
    {
        // Proxy: forward to main BE without touching proxy BE's FURNITURE
        if (FurnitureProxyComponent.isProxy(definition))
        {
            var pd = FurnitureProxyComponent.getData(definition);
            if (pd != null && this.level != null)
            {
                var mainPos = pd.resolveMain(this.worldPosition);
                if (this.level.getBlockEntity(mainPos) instanceof MultiBuiltBlockEntity mainBe)
                {
                    for (var mainDef : mainBe.FURNITURE)
                    {
                        if (mainDef.inBlockPos().distanceToSqr(pd.mainInBlockPos()) < 0.0001)
                        {
                            var mainCtx = new UseOnContext(context.getLevel(), context.getPlayer(), context.getHand(),
                                context.getItemInHand(), new BlockHitResult(Vec3.atCenterOf(mainPos), context.getClickedFace(), mainPos, false));
                            return mainBe.interactWith(mainDef, mainCtx, OPCODE_FURNITURE);
                        }
                    }
                }
            }
            return false;
        }
        if (definition.component() instanceof Interactable comp)
        {
            var list = getComponents(opcode);
            for (int i = 0; i < list.size(); i++)
            {
                if (list.get(i) != definition) continue;
                ComponentStateDefinition interacted = comp.handleInteraction(context, definition);
                if (interacted == null)
                {
                    if (definition.component() instanceof FurnitureComponent fc) fc.onRemoved(this, definition);
                    list.remove(i);
                    if (opcode == OPCODE_FURNITURE) refresh();
                    else { refresh(); SoundEvent event = definition.component().getSoundType().getBreakSound(); this.level.playSound(null, this.worldPosition, event, SoundSource.BLOCKS, 1.0f, 1.0f); }
                    return true;
                }
                if (interacted == definition) return false;
                list.set(i, interacted);
                if (interacted.component() instanceof FurnitureComponent fc) fc.onPlaced(this, interacted);
                SoundType s = comp.getInteractSound();
                if (s != SoundType.EMPTY) this.level.playSound(null, this.worldPosition, s.getPlaceSound(), SoundSource.BLOCKS, 1.0f, 1.0f);
                refresh();
                return true;
            }
        }
        else if (definition.component() instanceof Decoratable comp)
        {
            ComponentStateDefinition decoration = comp.decorate(this, context, definition);
            boolean canAppend = true;
            for (ComponentStateDefinition d : ADDITIONAL_COMPONENTS)
            {
                if (d.occupation().hashCode() == decoration.occupation().hashCode() && d.equals(decoration)) canAppend = false;
            }
            if (canAppend)
            {
                ADDITIONAL_COMPONENTS.add(definition);
                this.level.playSound(null, this.worldPosition, decoration.component().getSoundType().getPlaceSound(), SoundSource.BLOCKS, 1.0f, 1.0f);
                refresh();
                return true;
            }
        }
        return false;
    }

    public void checkMaterial()
    {
        if (this.level == null) return;
        BaseMultiBuiltBlock newMaterial = (BaseMultiBuiltBlock) this.getBlockState().getBlock();
        for (ComponentStateDefinition definition : this.getComponents(OPCODE_READALL))
        {
            BaseMultiBuiltBlock mat = null;
            if (FurnitureProxyComponent.isProxy(definition))
            {
                var pd = FurnitureProxyComponent.getData(definition);
                if (pd != null && this.level.getBlockEntity(pd.resolveMain(this.worldPosition)) instanceof MultiBuiltBlockEntity mbe)
                {
                    for (var d : mbe.FURNITURE)
                        if (d.inBlockPos().distanceToSqr(pd.mainInBlockPos()) < 0.0001)
                            mat = d.component().getMaterial().get();
                }
            }
            else mat = definition.component().getMaterial().get();
            if (mat != null && mat.material.getPriority() > newMaterial.material.getPriority())
                newMaterial = mat;
        }
        if (newMaterial != this.getBlockState().getBlock())
        {
            this.inMaterialChange = true;
            this.level.setBlock(this.getBlockPos(), newMaterial.applyMaterial(this.getBlockState()), 3);
            this.level.setBlockEntity(this);
            this.inMaterialChange = false;
        }
    }

    public ComponentStateDefinition getComponentByPosition(Vec3 vec3, int opcode)
    {
        for (ComponentStateDefinition m : this.getComponents(opcode))
        {
            if (m.shape().isEmpty()) continue;
            boolean complex = m.component().isComplexShape();
            if (FurnitureProxyComponent.isProxy(m))
            {
                var pd = FurnitureProxyComponent.getData(m);
                if (pd != null && this.level != null)
                {
                    var mp = pd.resolveMain(this.worldPosition);
                    if (this.level.getBlockEntity(mp) instanceof MultiBuiltBlockEntity mainBe)
                        for (var d : mainBe.FURNITURE)
                            if (d.inBlockPos().distanceToSqr(pd.mainInBlockPos()) < 0.0001)
                            { complex = d.component().isComplexShape(); break; }
                }
            }
            boolean hit;
            if (complex)
            {
                boolean[] found = {false};
                m.shape().forAllBoxes((x1, y1, z1, x2, y2, z2) ->
                {
                    double sx = MathHelper.simplifyDouble(vec3.x, 5), sy = MathHelper.simplifyDouble(vec3.y, 5), sz = MathHelper.simplifyDouble(vec3.z, 5);
                    if (sx >= MathHelper.simplifyDouble(x1, 5) && sx <= MathHelper.simplifyDouble(x2, 5)
                        && sy >= MathHelper.simplifyDouble(y1, 5) && sy <= MathHelper.simplifyDouble(y2, 5)
                        && sz >= MathHelper.simplifyDouble(z1, 5) && sz <= MathHelper.simplifyDouble(z2, 5))
                        found[0] = true;
                });
                hit = found[0];
            }
            else hit = m.shape().bounds().distanceToSqr(vec3) <= 0.00001;
            if (hit) return m;
        }
        return null;
    }

    //将绝对坐标转换为以本方块坐标为原点的局部坐标
    public Vec3 inBlockVec(Vec3 vec)
    {
        double x = snapEdge(vec.x() - this.getBlockPos().getX());
        double y = snapEdge(vec.y() - this.getBlockPos().getY());
        double z = snapEdge(vec.z() - this.getBlockPos().getZ());
        return new Vec3(x, y, z);
    }

    private static double snapEdge(double v)
    {
        double snapped = Math.round(v * 16.0) / 16.0;
        return Math.abs(v - snapped) < 0.0001 ? snapped : v;
    }

    public void refresh()
    {
        refresh(true);
    }

    public void refresh(boolean reloadShape)
    {
        if (reloadShape) reloadShape();
        reloadOccupation();
        sync();
        setChanged();
        checkMaterial();
        if (this.hasLevel()) this.getLevel().sendBlockUpdated(this.getBlockPos(), this.getBlockState(), this.getBlockState(), 3);
        if (COMPONENTS.isEmpty() && ADDITIONAL_COMPONENTS.isEmpty() && FURNITURE.isEmpty())
            this.getLevel().removeBlock(this.getBlockPos(), false);
    }

    @Override
    public void setRemoved()
    {
        for (ComponentStateDefinition model : this.getComponents(OPCODE_COMPONENT))
        {
            SoundEvent event = model.component().getSoundType().getBreakSound();
            this.level.playSound(null, this.getBlockPos(), event, SoundSource.BLOCKS, 1.0f, 1.0f);
        }
        for (ComponentStateDefinition model : this.getComponents(OPCODE_ADDITIONAL))
        {
            SoundEvent event = model.component().getSoundType().getBreakSound();
            this.level.playSound(null, this.getBlockPos(), event, SoundSource.BLOCKS, 1.0f, 1.0f);
        }
        for (ComponentStateDefinition model : this.FURNITURE)
        {
            SoundEvent event = model.component().getSoundType().getBreakSound();
            this.level.playSound(null, this.getBlockPos(), event, SoundSource.BLOCKS, 1.0f, 1.0f);
        }
        super.setRemoved();
    }

    @Override
    protected void loadAdditional(ValueInput input)
    {
        super.loadAdditional(input);
        this.COMPONENTS.clear();
        this.ADDITIONAL_COMPONENTS.clear();
        this.FURNITURE.clear();
        for (ValueInput child : input.childrenListOrEmpty("models"))
        {
            this.COMPONENTS.add(ComponentStateDefinition.deserializeNBT(child));
        }
        for (ValueInput child : input.childrenListOrEmpty("additional_models"))
        {
            this.ADDITIONAL_COMPONENTS.add(ComponentStateDefinition.deserializeNBT(child));
        }
        for (ValueInput child : input.childrenListOrEmpty("furniture"))
        {
            this.FURNITURE.add(ComponentStateDefinition.deserializeNBT(child));
        }

        refresh(true);
    }

    @Override
    protected void saveAdditional(ValueOutput output)
    {
        var listTag = output.childrenList("models");
        for (ComponentStateDefinition definition : this.COMPONENTS)
        {
            ValueOutput o = listTag.addChild();
            definition.serialize(o);
        }
        var additionalListTag = output.childrenList("additional_models");
        for (ComponentStateDefinition definition : this.ADDITIONAL_COMPONENTS)
        {
            ValueOutput o = additionalListTag.addChild();
            definition.serialize(o);
        }
        var furnitureListTag = output.childrenList("furniture");
        for (ComponentStateDefinition definition : this.FURNITURE)
        {
            ValueOutput o = furnitureListTag.addChild();
            definition.serialize(o);
        }
        super.saveAdditional(output);
    }

    @Override
    public List<ComponentStateDefinition> getComponents(int opcode)
    {
        if (opcode == OPCODE_READALL)
        {
            List<ComponentStateDefinition> components = new ArrayList<>();
            components.addAll(COMPONENTS);
            components.addAll(ADDITIONAL_COMPONENTS);
            components.addAll(FURNITURE);
            return components;
        }
        return switch (opcode)
        {
            case OPCODE_COMPONENT -> this.COMPONENTS;
            case OPCODE_ADDITIONAL -> this.ADDITIONAL_COMPONENTS;
            case OPCODE_FURNITURE -> this.FURNITURE;
            default -> List.of();
        };
    }

    // <editor-fold desc="VoxelShape Persistent Storage">

    private VoxelShape shape = Shapes.empty();

    private void setShape(VoxelShape shape) {this.shape = shape;}

    public VoxelShape getShape() {return shape;}

    private void saveShape(ValueOutput output)
    {
        VoxelShapeSerializer.saveShape(getShape(), output.child("shape"));
    }

    private boolean loadShape(ValueInput input)
    {
        VoxelShape result = VoxelShapeSerializer.loadShape(input.childOrEmpty("shape"));
        if (result != null)
        {
            setShape(result);
            return true;
        }
        return false;
    }

    // </editor-fold>

    // -- Multi-block helpers -----------------------------------------

    private BlockBox extentFromDef(ComponentStateDefinition def)
    {
        if (!(def.component() instanceof MultiBlockFurniture)) return null;
        var base = def.component().getBaseShape();
        if (base == null)
            return ((MultiBlockFurniture) def.component()).getExtent(Direction.fromYRot(def.rotationY()));
        int r = (int) def.rotationY();
        VoxelShape full = r != 0 ? ShapeHelper.rotateShape(base, -r) : base;
        full = ShapeHelper.offsetShape(full, def.inBlockPos().x(), def.inBlockPos().y(), def.inBlockPos().z());
        var bb = full.bounds();
        return new BlockBox(
            new BlockPos((int) Math.floor(bb.minX), (int) Math.floor(bb.minY), (int) Math.floor(bb.minZ)),
            new BlockPos((int) Math.ceil(bb.maxX) - 1, (int) Math.ceil(bb.maxY) - 1, (int) Math.ceil(bb.maxZ) - 1));
    }

    private void removeMultiBlockProxies(ComponentStateDefinition mainDef, BlockBox extent)
    {
        if (extent == null) return;
        for (int x = extent.min().getX(); x <= extent.max().getX(); x++)
            for (int y = extent.min().getY(); y <= extent.max().getY(); y++)
                for (int z = extent.min().getZ(); z <= extent.max().getZ(); z++)
                {
                    if (x == 0 && y == 0 && z == 0) continue;
                    BlockPos target = this.worldPosition.offset(x, y, z);
                    if (this.level.getBlockEntity(target) instanceof MultiBuiltBlockEntity subBe)
                    {
                        subBe.FURNITURE.removeIf(d ->
                        {
                            var pd = FurnitureProxyComponent.getData(d);
                            return pd != null && pd.resolveMain(subBe.worldPosition).equals(this.worldPosition)
                                && pd.mainInBlockPos().equals(mainDef.inBlockPos());
                        });
                        subBe.refresh();
                    }
                }
    }

   private transient boolean inMaterialChange = false;

   @Override
   public void preRemoveSideEffects(BlockPos pos, BlockState state)
   {
       super.preRemoveSideEffects(pos, state);
       if (this.inMaterialChange) return;
       for (var def : new ArrayList<>(this.FURNITURE))
        {
            if (def.component() instanceof BambooCurtainComponent)
            {
                BambooCurtainComponent.onChainBreak(this, def);
                ((BambooCurtainComponent) def.component()).onRemoved(this, def);
                continue;
            }
            // Forward proxy destruction to main BE
            if (FurnitureProxyComponent.isProxy(def))
            {
                var pd = FurnitureProxyComponent.getData(def);
                if (pd != null && this.level != null
                    && this.level.getBlockEntity(pd.resolveMain(this.worldPosition)) instanceof MultiBuiltBlockEntity mainBe)
                {
                    for (var mainDef : new ArrayList<>(mainBe.FURNITURE))
                        if (mainDef.inBlockPos().equals(pd.mainInBlockPos()))
                            mainBe.breakComponent(mainDef, null, OPCODE_FURNITURE);
                }
                continue;
            }
            // Clean up sub-blocks of multi-block main components
            var extent = extentFromDef(def);
            if (extent != null) removeMultiBlockProxies(def, extent);
           }
       }
   }
