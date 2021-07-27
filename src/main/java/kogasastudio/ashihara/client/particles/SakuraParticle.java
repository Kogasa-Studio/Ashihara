package kogasastudio.ashihara.client.particles;

import net.minecraft.client.particle.*;
import net.minecraft.client.world.ClientWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class SakuraParticle extends SpriteTexturedParticle
{
    protected SakuraParticle(ClientWorld world, double x, double y, double z)
    {
        super(world, x, y, z, 0.0D, 0.0D, 0.0D);
        this.motionX *= 0.9F;
        this.motionY = 0;
        this.motionZ *= 0.9F;
        this.particleScale = 0.2F;
        this.maxAge = 200;
    }

    @Override
    public IParticleRenderType getRenderType()
    {
        return IParticleRenderType.PARTICLE_SHEET_OPAQUE;
    }

    @Override
    public float getScale(float scaleFactor)
    {
        return 0.2F;
    }

    @Override
    public void tick()
    {
        if (this.age++ >= this.maxAge)
        {
            this.setExpired();
        }
        else
        {
            this.prevPosX = this.posX;
            this.prevPosY = this.posY;
            this.prevPosZ = this.posZ;
            this.motionY -= 0.001D;
            this.move(this.motionX, this.motionY, this.motionZ);
            this.motionX *= 0.97F;
            this.motionY *= 0.999F;
            this.motionZ *= 0.97F;
            if (this.onGround)
            {
                this.motionX *= 0.7F;
                this.motionZ *= 0.7F;
            }
        }
    }

    @OnlyIn(Dist.CLIENT)
    public static class SakuraParticleFactory implements IParticleFactory<GenericParticleData>
    {
        private final IAnimatedSprite spriteSet;

        public SakuraParticleFactory(IAnimatedSprite spriteSet) {
            this.spriteSet = spriteSet;
        }

        @Override
        public Particle makeParticle(GenericParticleData typeIn, ClientWorld worldIn, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed)
        {
            SakuraParticle sakuraParticle = new SakuraParticle(worldIn, x, y, z);
            sakuraParticle.selectSpriteRandomly(this.spriteSet);
            return sakuraParticle;
        }
    }
}
