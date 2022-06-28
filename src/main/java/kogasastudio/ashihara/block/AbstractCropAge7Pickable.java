package kogasastudio.ashihara.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

import static kogasastudio.ashihara.item.ItemRegistryHandler.*;
import static net.minecraft.item.Items.AIR;
import static net.minecraft.item.Items.BONE_MEAL;

public class AbstractCropAge7Pickable extends AbstractCropAge7
{
    public AbstractCropAge7Pickable(int max, int turn)
    {
        super
        (
            Properties.create(Material.PLANTS)
            .doesNotBlockMovement()
            .tickRandomly()
            .hardnessAndResistance(0.2F)
            .sound(SoundType.CROP)
        );
        this.ageAvailable = max;
        this.ageTurnIn = turn;
    }

    protected final int ageAvailable;
    protected final int ageTurnIn;

    @Override
    public ActionResultType onBlockActivated(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit)
    {
        int age = state.get(AGE);
        ItemStack stack = player.getHeldItem(handIn);
        if (age < this.ageAvailable && stack.getItem().equals(BONE_MEAL)) return ActionResultType.PASS;
        if (age == this.ageAvailable)
        {
            if (!worldIn.isRemote())
            {
                for (ItemStack stack1 : getDrops(state, (ServerWorld) worldIn, pos, null))
                {
                    spawnAsEntity(worldIn, pos, stack1);
                }
            }
            worldIn.playSound(player, pos, SoundEvents.ITEM_SWEET_BERRIES_PICK_FROM_BUSH, SoundCategory.BLOCKS, 1.0F, 0.8F + worldIn.rand.nextFloat() * 0.4F);
            worldIn.setBlockState(pos, state.with(AGE, this.ageTurnIn));
            return ActionResultType.SUCCESS;
        }
        return super.onBlockActivated(state, worldIn, pos, player, handIn, hit);
    }
}
