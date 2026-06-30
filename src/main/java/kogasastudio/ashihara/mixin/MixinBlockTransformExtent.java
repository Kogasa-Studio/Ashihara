package kogasastudio.ashihara.mixin;

import com.sk89q.worldedit.extent.transform.BlockTransformExtent;
import com.sk89q.worldedit.math.transform.AffineTransform;
import com.sk89q.worldedit.math.transform.Transform;
import com.sk89q.worldedit.util.concurrency.LazyReference;
import com.sk89q.worldedit.world.block.BaseBlock;
import com.sk89q.worldedit.world.block.BlockState;
import com.sk89q.worldedit.world.block.BlockStateHolder;
import org.enginehub.linbus.tree.LinCompoundTag;
import org.enginehub.linbus.tree.LinFloatTag;
import org.enginehub.linbus.tree.LinIntTag;
import org.enginehub.linbus.tree.LinListTag;
import org.enginehub.linbus.tree.LinTagType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Pseudo
@Mixin(BlockTransformExtent.class)
public class MixinBlockTransformExtent
{
    @Inject(method = "transform", at = @At("RETURN"), remap = false, cancellable = true)
    private static <B extends BlockStateHolder<B>> void onTransform(B b, Transform t, CallbackInfoReturnable<B> cir)
    {
        B r = cir.getReturnValue();
        if (!(r instanceof BaseBlock bb)) return;
        if (!(t instanceof AffineTransform aff)) return;
        LinCompoundTag nbt = nbt(bb);
        if (nbt == null) return;
        LinCompoundTag out = build(nbt, aff);
        if (out == null) return;
        BlockState s = state(bb);
        if (s == null) return;
        cir.setReturnValue((B)s.toBaseBlock(LazyReference.from(()->out)));
    }

    private static LinCompoundTag nbt(BaseBlock bb) { var ref=bb.getNbtReference(); return ref!=null?ref.getValue():null; }
    private static BlockState state(BaseBlock bb) {
        try { var f=BaseBlock.class.getDeclaredField("blockState"); f.setAccessible(true); return (BlockState)f.get(bb); }
        catch (Exception e) { return null; }
    }

    private static LinCompoundTag build(LinCompoundTag nbt, AffineTransform aff)
    {
        boolean found = false;
        for (String s : L) if (nbt.findListTag(s, LinTagType.compoundTag()) != null) { found=true; break; }
        if (!found) return null;

        double[] m = aff.coefficients();
        double a=m[0], b=m[2], c=m[8], d=m[10];
        boolean flip = a*d - b*c < 0;

        var out = LinCompoundTag.builder();
        for (var e : nbt.value().entrySet())
        {
            String k = e.getKey();
            if (has(k, L)) out.put(k, tList(e.getValue(), a,b,c,d,flip));
            else if (!k.equals("shape")) out.put(k, e.getValue());
        }
        return out.build();
    }
    private static final String[] L = {"models","additional_models","furniture"};

    @SuppressWarnings({"unchecked","rawtypes"})
    private static LinListTag<?> tList(Object t, double a,double b,double c,double d,boolean flip)
    {
        if (!(t instanceof LinListTag<?> l)) return (LinListTag<?>)t;
        LinListTag.Builder<LinCompoundTag> nl = LinListTag.builder(LinTagType.compoundTag());
        for (var x : l.value())
            nl.add(x instanceof LinCompoundTag cc ? tChild(cc,a,b,c,d,flip) : (LinCompoundTag)x);
        return nl.build();
    }

    private static LinCompoundTag tChild(LinCompoundTag ct, double a,double b,double c,double d,boolean flip)
    {
        var out = LinCompoundTag.builder();
        boolean zFlip = flip && a > 0.5 && d < -0.5;
        for (var e : ct.value().entrySet())
        {
            String k = e.getKey(); var v = e.getValue();
            if (k.equals("inBlockPos") && v instanceof LinCompoundTag pt)
               out.put(k, rPos(pt, a, b, c, d));
            else if (k.equals("custom") && v instanceof LinCompoundTag cc && cc.findTag("dx", LinTagType.intTag()) != null)
               out.put(k, rProxyCustom(cc, a, b, c, d));
            else if (k.equals("rotationY") && v instanceof LinFloatTag ft)
            {
               float rv = ft.value();
               if (flip) {
                   if (zFlip) rv = (float)((180.0 - rv + 360.0) % 360.0);
                   else rv = 360f - rv;
               } else {
                   rv = (float)((rv + Math.toDegrees(Math.atan2(b, a)) + 360) % 360);
               }
               out.putFloat(k, rv);
            }
          else if (k.equals("shape") && v instanceof LinListTag<?> sl)
              out.put(k, rShape(sl, a, b, c, d));
          else out.put(k, v);
        }
        return out.build();
    }

    private static LinCompoundTag rProxyCustom(LinCompoundTag ct, double a,double b,double c,double d)
    {
        int dx = (int) Math.round(iv(ct, "dx") * a + iv(ct, "dz") * b);
        int dz = (int) Math.round(iv(ct, "dx") * c + iv(ct, "dz") * d);
        var cb = LinCompoundTag.builder();
        cb.putInt("dx", dx); cb.putInt("dy", iv(ct, "dy")); cb.putInt("dz", dz);
        for (var e : ct.value().entrySet())
        {
            String k = e.getKey(); var v = e.getValue();
            if (k.equals("mainInBlock") && v instanceof LinCompoundTag ib)
                cb.put(k, rPos(ib, a, b, c, d));
            else if (!k.equals("dx") && !k.equals("dy") && !k.equals("dz"))
                cb.put(k, v);
        }
        return cb.build();
    }

    private static int iv(LinCompoundTag t, String k) { var v = t.findTag(k, LinTagType.intTag()); return v != null ? v.value() : 0; }

    private static LinCompoundTag rPos(LinCompoundTag pt, double a,double b,double c,double d)
    {
        double x = dv(pt,"x"), y = dv(pt,"y"), z = dv(pt,"z");
        var nb = LinCompoundTag.builder();
        nb.putDouble("x", x*a + z*b);
        nb.putDouble("y", y);
        nb.putDouble("z", x*c + z*d);
        return nb.build();
    }

    @SuppressWarnings({"unchecked","rawtypes"})
    private static LinListTag<?> rShape(LinListTag<?> list, double a,double b,double c,double d)
    {
        LinListTag.Builder<LinCompoundTag> nl = LinListTag.builder(LinTagType.compoundTag());
        for (var box : list.value())
        {
            if (!(box instanceof LinCompoundTag bt)) { nl.add((LinCompoundTag) (Object) box); continue; }
            double x0=dv(bt,"x0"),z0=dv(bt,"z0"),x1=dv(bt,"x1"),z1=dv(bt,"z1");
            double[] xs=new double[4],zs=new double[4];
            rotCorner(x0, z0, a, b, c, d, xs, zs, 0); rotCorner(x0, z1, a, b, c, d, xs, zs, 1);
            rotCorner(x1, z0, a, b, c, d, xs, zs, 2); rotCorner(x1, z1, a, b, c, d, xs, zs, 3);
            var sb = LinCompoundTag.builder();
            sb.putDouble("y0", dv(bt, "y0"));
            double[] xr = fixNeg(min4(xs), max4(xs));
            double[] zr = fixNeg(min4(zs), max4(zs));
            sb.putDouble("x0", xr[0]); sb.putDouble("z0", zr[0]);
            sb.putDouble("y1", dv(bt, "y1"));
            sb.putDouble("x1", xr[1]); sb.putDouble("z1", zr[1]);
            nl.add(sb.build());
        }
        return nl.build();
    }

    private static void rotCorner(double x,double z,double a,double b,double c,double d,double[] xs,double[] zs,int i)
    {
        xs[i]=x*a+z*b; zs[i]=x*c+z*d;
    }

    private static double[] fixNeg(double min, double max)
    {
        double mid = (min + max) / 2.0;
        if (mid < 0.0) { min += 1.0; max += 1.0; }
        else if (mid > 1.0) { min -= 1.0; max -= 1.0; }
        return new double[]{min, max};
    }

    private static double dv(LinCompoundTag t,String k) {
        var v=t.findTag(k,LinTagType.doubleTag()); return v!=null?v.value():0.0;
    }
    private static double min4(double[] a){return Math.min(Math.min(a[0],a[1]),Math.min(a[2],a[3]));}
    private static double max4(double[] a){return Math.max(Math.max(a[0],a[1]),Math.max(a[2],a[3]));}
    private static boolean has(String s,String[]a){for(String x:a)if(x.equals(s))return true;return false;}
}
