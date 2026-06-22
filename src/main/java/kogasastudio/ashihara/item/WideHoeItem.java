package kogasastudio.ashihara.item;

import kogasastudio.ashihara.registry.Blocks;
import kogasastudio.ashihara.registry.Items;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.HoeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ToolMaterial;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
// vanilla Blocks used via fully-qualified name
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.common.ItemAbilities;

public class WideHoeItem extends HoeItem
{
    public WideHoeItem(ToolMaterial material, float attackDamageBaseline, float attackSpeedBaseline, Properties properties)
    {
        super(material, attackDamageBaseline, attackSpeedBaseline, properties);
    }

    @Override
    public InteractionResult useOn(UseOnContext context)
    {
        Level level = context.getLevel();
        BlockPos pos = context.getClickedPos();
        Player player = context.getPlayer();
        ItemStack stack = context.getItemInHand();

        if (player == null) return InteractionResult.PASS;

        // shift + right click: 3x3 to farmland
        if (player.isShiftKeyDown())
        {
            boolean tilled = false;
            for (int dx = -1; dx <= 1; dx++)
            {
                for (int dz = -1; dz <= 1; dz++)
                {
                    BlockPos target = pos.offset(dx, 0, dz);
                    if (convertSingle(level, target, player, context.getHand(), stack, context.getHorizontalDirection(), true)) tilled = true;
                }
            }
            if (tilled)
            {
                level.playSound(player, pos, SoundEvents.HOE_TILL, SoundSource.BLOCKS, 1.0F, level.getRandom().nextFloat() * 0.4F + 0.8F);
                player.swing(context.getHand());
                stack.hurtAndBreak(3, player, context.getHand());
                return InteractionResult.SUCCESS;
            }
            return InteractionResult.PASS;
        }

        // normal right click: single conversion
        if (convertSingle(level, pos, player, context.getHand(), stack, context.getHorizontalDirection(), false))
        {
            level.playSound(player, pos, SoundEvents.HOE_TILL, SoundSource.BLOCKS, 1.0F, level.getRandom().nextFloat() * 0.4F + 0.8F);
            player.swing(context.getHand());
            stack.hurtAndBreak(1, player, context.getHand());
            return InteractionResult.SUCCESS;
        }

        return InteractionResult.PASS;
    }

    /**
     * @param goWide if true, converts sand to salt field. If false (shift mode), converts to farmland.
     */
    private boolean convertSingle(Level level, BlockPos pos, Player player, InteractionHand hand, ItemStack stack, Direction facing, boolean goWide)
    {
        BlockState state = level.getBlockState(pos);
        UseOnContext ctx = new UseOnContext(level, player, hand, stack, new BlockHitResult(Vec3.atCenterOf(pos), facing, pos, false));

        // tillable block -> farmland (shift) or dirt depression (normal)
        BlockState tilled = state.getToolModifiedState(ctx, ItemAbilities.HOE_TILL, false);
        if (tilled != null)
        {
            level.setBlock(pos, goWide ? net.minecraft.world.level.block.Blocks.FARMLAND.defaultBlockState() : Blocks.DIRT_DEPRESSION.get().defaultBlockState(), 11);
            if (!goWide) Block.popResourceFromFace(level, pos, Direction.UP, Items.DIRT_BALL.toStack(2));
            return true;
        }
        // sand -> salt field (only in normal mode with air above)
        if (!goWide && state.is(BlockTags.SAND) && level.getBlockState(pos.above()).isAir())
        {
            level.setBlock(pos, Blocks.SALT_FIELD.get().defaultBlockState(), 11);
            return true;
        }
        return false;
    }
}

