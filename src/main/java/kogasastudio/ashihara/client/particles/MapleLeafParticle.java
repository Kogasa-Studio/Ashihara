package kogasastudio.ashihara.client.particles;

import net.minecraft.client.particle.*;
import net.minecraft.client.world.ClientWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class MapleLeafParticle extends SpriteTexturedParticle
{
    protected MapleLeafParticle(ClientWorld world, double x, double y, double z)
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
            this.motionX *= 0.7F;
            this.motionY *= 0.999F;
            this.motionZ *= 0.7F;
            if (this.onGround)
            {
                this.motionX *= 0.5F;
                this.motionZ *= 0.5F;
            }
        }
    }

    @OnlyIn(Dist.CLIENT)
    public static class MapleLeafParticleFactory implements IParticleFactory<GenericParticleData>
    {
        private final IAnimatedSprite spriteSet;

        public MapleLeafParticleFactory(IAnimatedSprite spriteSet) {
            this.spriteSet = spriteSet;
        }

        @Override
        public Particle makeParticle(GenericParticleData typeIn, ClientWorld worldIn, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed)
        {
            MapleLeafParticle maple = new MapleLeafParticle(worldIn, x, y, z);
            maple.selectSpriteRandomly(this.spriteSet);
            return maple;
        }
    }
}
