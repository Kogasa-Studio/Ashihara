package kogasastudio.ashihara.client.particles;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.RandomSource;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

public class MapleLeafParticle extends FallingLeavesParticle
{
    protected MapleLeafParticle(ClientLevel world, double x, double y, double z, TextureAtlasSprite sprite, float fallAcceleration, float sideAcceleration, boolean swirl, boolean flowAway, float scale, float startVelocity)
    {
        super(world, x, y, z, sprite, fallAcceleration, sideAcceleration, swirl, flowAway, scale, startVelocity);
        this.lifetime = 200;
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
            this.yd -= 0.001D;
            this.move(this.xd, this.yd, this.zd);
            this.xd *= 0.7F;
            this.yd *= 0.999F;
            this.zd *= 0.7F;
            if (this.onGround)
            {
                this.xd *= 0.5F;
                this.zd *= 0.5F;
            }
        }
    }

    
    public static class MapleLeafParticleProvider implements ParticleProvider<SimpleParticleType>
    {
        private final SpriteSet spriteSet;

        public MapleLeafParticleProvider(SpriteSet spriteSet)
        {
            this.spriteSet = spriteSet;
        }

        @Override
        public Particle createParticle(SimpleParticleType options, ClientLevel level, double x, double y, double z, double xAux, double yAux, double zAux, RandomSource random)
        {
            return new MapleLeafParticle(level, x, y, z, spriteSet.get(random), 0.25F, 2.0F, true, true, 1.0F, 0.0F);
        }
    }
}
