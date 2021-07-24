package kogasastudio.ashihara.client.particles;

import net.minecraft.client.particle.*;
import net.minecraft.client.world.ClientWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class RiceParticle extends SpriteTexturedParticle
{
    protected RiceParticle(ClientWorld world, double x, double y, double z)
    {
        super(world, x, y, z, 0.0D, 0.0D, 0.0D);
        this.motionX *= 0.8F;
        this.motionY *= 0.8F;
        this.motionZ *= 0.8F;
        this.motionY = (this.rand.nextFloat() * 0.4F + 0.05F);
        this.particleScale *= this.rand.nextFloat() * 2.0F + 0.2F;
        this.maxAge = (int)(16.0D / (Math.random() * 0.8D + 0.2D));
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
            this.motionY -= 0.03D;
            this.move(this.motionX, this.motionY, this.motionZ);
            this.motionX *= 0.999F;
            this.motionY *= 0.999F;
            this.motionZ *= 0.999F;
            if (this.onGround) {
                this.motionX *= 0.7F;
                this.motionZ *= 0.7F;
            }
        }
    }

    @OnlyIn(Dist.CLIENT)
    public static class RiceParticleFactory implements IParticleFactory<GenericParticleData>
    {
        private final IAnimatedSprite spriteSet;

        public RiceParticleFactory(IAnimatedSprite spriteSet) {
            this.spriteSet = spriteSet;
        }

        @Override
        public Particle makeParticle(GenericParticleData typeIn, ClientWorld worldIn, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed)
        {
            RiceParticle riceparticle = new RiceParticle(worldIn, x, y, z);
            riceparticle.selectSpriteRandomly(this.spriteSet);
            return riceparticle;
        }
    }
}
