package kogasastudio.ashihara.client.particles;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.RandomSource;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.jspecify.annotations.Nullable;

public class SakuraParticle extends FallingLeavesParticle
{
    private float offsetSpeed;
    private float timePointer;
    private float rotSpeed;
    private final float spinAcceleration;
    protected SakuraParticle(ClientLevel world, double x, double y, double z, TextureAtlasSprite sprite, float fallAcceleration, float sideAcceleration, boolean swirl, boolean flowAway, float scale, float startVelocity)
    {
        super(world, x, y, z, sprite, fallAcceleration, sideAcceleration, swirl, flowAway, scale, startVelocity);
        this.xd = 0;
        this.yd = 0;
        this.zd = 0;
        this.timePointer = 0;
        this.offsetSpeed = 0;
        this.rotSpeed = (float) Math.toRadians(this.random.nextInt(25, 55) * (this.random.nextBoolean() ? 1 : -1));
        this.spinAcceleration = (float)Math.toRadians(this.random.nextBoolean() ? -5.0D : 5.0D);
        float f = (float) this.random.nextInt(20, 30) / 100;
        this.quadSize = f;
        this.setSize(f, f);
        this.lifetime = 200;
    }

    @Override
    public float getQuadSize(float scaleFactor)
    {
        return 0.2F;
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
            this.oRoll = this.roll;
            this.timePointer += 0.05;
            if (onGround) this.offsetSpeed = 0; else this.offsetSpeed += 0.0036 * this.timePointer;
            if (onGround) this.rotSpeed = 0; else this.rotSpeed += this.spinAcceleration / 20.0F;
            this.roll += this.rotSpeed / 20;
            this.xd = this.offsetSpeed / 20;
            this.yd -= 0.001D;
            this.zd = this.offsetSpeed / 20;
            this.move(this.xd, this.yd, this.zd);
            if (this.onGround)
            {
                this.xd *= 0.5F;
                this.zd *= 0.5F;
            }
        }
    }

    
    public static class SakuraParticleProvider implements ParticleProvider<SimpleParticleType>
    {
        private final SpriteSet spriteSet;

        public SakuraParticleProvider(SpriteSet spriteSet)
        {
            this.spriteSet = spriteSet;
        }

        @Override
        public Particle createParticle(SimpleParticleType options, ClientLevel level, double x, double y, double z, double xAux, double yAux, double zAux, RandomSource random)
        {
            SakuraParticle sakuraParticle = new SakuraParticle(level, x, y, z, spriteSet.get(random), 0.25F, 2.0F, true, true, 1.0F, 0.0F);
            return sakuraParticle;
        }
    }
}