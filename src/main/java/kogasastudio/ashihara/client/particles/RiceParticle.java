package kogasastudio.ashihara.client.particles;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.RandomSource;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.jspecify.annotations.Nullable;

public class RiceParticle extends SingleQuadParticle
{
    protected RiceParticle(ClientLevel world, double x, double y, double z, TextureAtlasSprite sprite)
    {
        super(world, x, y, z, sprite);
        this.xd *= 0.8F;
        this.yd *= 0.8F;
        this.zd *= 0.8F;
        this.yd = (this.random.nextFloat() * 0.4F + 0.05F);
        this.quadSize *= this.random.nextFloat() * 2.0F + 0.2F;
        this.lifetime = (int) (16.0D / (Math.random() * 0.8D + 0.2D));
    }

    @Override
    public float getQuadSize(float scaleFactor)
    {
        return 0.2F;
    }

    @Override
    protected Layer getLayer()
    {
        return SingleQuadParticle.Layer.OPAQUE;
    }

    @Override
    public void tick()
    {
        if (this.age++ >= this.lifetime)
        {
            this.remove();
        } else
        {
            this.xo = this.x;
            this.yo = this.y;
            this.zo = this.z;
            this.yd -= 0.03D;
            this.move(this.xd, this.yd, this.zd);
            this.xd *= 0.999F;
            this.yd *= 0.999F;
            this.zd *= 0.999F;
            if (this.onGround)
            {
                this.xd *= 0.7F;
                this.zd *= 0.7F;
            }
        }
    }

    
    public static class RiceParticleProvider implements ParticleProvider<SimpleParticleType>
    {
        private final SpriteSet spriteSet;

        public RiceParticleProvider(SpriteSet spriteSet)
        {
            this.spriteSet = spriteSet;
        }

        @Override
        public Particle createParticle(SimpleParticleType options, ClientLevel level, double x, double y, double z, double xAux, double yAux, double zAux, RandomSource random)
        {
            RiceParticle riceparticle = new RiceParticle(level, x, y, z, spriteSet.get(random));
            return riceparticle;
        }
    }
}
