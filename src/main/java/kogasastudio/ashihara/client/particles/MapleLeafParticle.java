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
        this.xd *= 0.9F;
        this.yd = 0;
        this.zd *= 0.9F;
        this.quadSize = 0.2F;
        this.lifetime = 200;
    }

    @Override
    public IParticleRenderType getRenderType()
    {
        return IParticleRenderType.PARTICLE_SHEET_OPAQUE;
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
        }
        else
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

    @OnlyIn(Dist.CLIENT)
    public static class MapleLeafParticleFactory implements IParticleFactory<GenericParticleData>
    {
        private final IAnimatedSprite spriteSet;

        public MapleLeafParticleFactory(IAnimatedSprite spriteSet) {
            this.spriteSet = spriteSet;
        }

        @Override
        public Particle createParticle(GenericParticleData typeIn, ClientWorld worldIn, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed)
        {
            MapleLeafParticle maple = new MapleLeafParticle(worldIn, x, y, z);
            maple.pickSprite(this.spriteSet);
            return maple;
        }
    }
}
