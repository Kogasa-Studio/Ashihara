package kogasastudio.ashihara.client.particles;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class SakuraParticle extends TextureSheetParticle {
    protected SakuraParticle(ClientLevel world, double x, double y, double z) {
        super(world, x, y, z, 0.0D, 0.0D, 0.0D);
        this.xd *= 0.9F;
        this.yd = 0;
        this.zd *= 0.9F;
        this.quadSize = 0.2F;
        this.lifetime = 200;
    }

    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_OPAQUE;
    }

    @Override
    public float getQuadSize(float scaleFactor) {
        return 0.2F;
    }

    @Override
    public void tick() {
        if (this.age++ >= this.lifetime) {
            this.remove();
        } else {
            this.xo = this.x;
            this.yo = this.y;
            this.zo = this.z;
            this.yd -= 0.001D;
            this.move(this.xd, this.yd, this.zd);
            this.xd *= 0.7F;
            this.yd *= 0.999F;
            this.zd *= 0.7F;
            if (this.onGround) {
                this.xd *= 0.5F;
                this.zd *= 0.5F;
            }
        }
    }

    @OnlyIn(Dist.CLIENT)
    public static class SakuraParticleFactory implements ParticleProvider<GenericParticleData> {
        private final SpriteSet spriteSet;

        public SakuraParticleFactory(SpriteSet spriteSet) {
            this.spriteSet = spriteSet;
        }

        @Override
        public Particle createParticle(GenericParticleData typeIn, ClientLevel worldIn, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
            SakuraParticle sakuraParticle = new SakuraParticle(worldIn, x, y, z);
            sakuraParticle.pickSprite(this.spriteSet);
            return sakuraParticle;
        }
    }
}