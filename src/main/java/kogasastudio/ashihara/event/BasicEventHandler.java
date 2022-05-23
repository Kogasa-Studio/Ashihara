package kogasastudio.ashihara.event;

import kogasastudio.ashihara.Ashihara;
import kogasastudio.ashihara.block.BlockRegistryHandler;
import kogasastudio.ashihara.item.ItemRegistryHandler;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.item.ShovelItem;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.entity.player.UseHoeEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = Ashihara.MODID)
public class BasicEventHandler
{
    @SubscribeEvent
    public static void onRightClick(PlayerInteractEvent.RightClickBlock event)
    {
        ItemStack item = event.getItemStack();
        BlockPos pos = event.getPos();
        World world = event.getWorld();
        BlockState clickState = world.getBlockState(pos);
        PlayerEntity player = event.getPlayer();
        //对于上方无方块的操作
        if(world.getBlockState(pos.above()).getBlock() == Blocks.AIR)
        {
            //铲土洼
            if(item.getItem() instanceof ShovelItem && (clickState.is(Blocks.DIRT) || (player.isShiftKeyDown() && clickState.is(Blocks.GRASS_PATH))))
            {
                world.playSound(player, pos, SoundEvents.SHOVEL_FLATTEN, SoundCategory.BLOCKS, 1.0F, 1.0F);
                world.setBlockAndUpdate(pos, BlockRegistryHandler.DIRT_DEPRESSION.get().defaultBlockState());
                player.swing(event.getHand());
                InventoryHelper.dropItemStack(world, pos.getX(), pos.getY() + 0.5F, pos.getZ(), new ItemStack(ItemRegistryHandler.DIRT_BALL.get()));
                if(!player.abilities.instabuild) {item.hurtAndBreak(1, player, (playerEntity) -> player.broadcastBreakEvent(event.getHand()));}
            }
        }
    }

    //锄头事件
    @SubscribeEvent
    public static void onHoeUse(UseHoeEvent event)
    {
        ItemUseContext context = event.getContext();
        BlockPos pos = context.getClickedPos();
        World world = context.getLevel();
        BlockState clickState = world.getBlockState(pos);
        PlayerEntity player = context.getPlayer();
        //矫正位置偏移
        BlockRayTraceResult result = new BlockRayTraceResult
        (
            context.getClickLocation(),
            context.getClickedFace(),
            pos.below(),
            context.isInside()
        );
        if (clickState.is(BlockRegistryHandler.DIRT_DEPRESSION.get()))
        {
            //相当于手动放置
            world.playSound(player, pos, SoundEvents.HOE_TILL, SoundCategory.BLOCKS, 1.0F, world.getRandom().nextFloat() * 0.4F + 0.8F);
            BlockState placement = BlockRegistryHandler.WATER_FIELD.get().getStateForPlacement(new BlockItemUseContext(world, player, context.getHand(), context.getItemInHand(), result));
            world.setBlock(pos, placement == null ? BlockRegistryHandler.WATER_FIELD.get().defaultBlockState() : placement, 1);
            if (player != null) {player.swing(context.getHand());}
            if (player != null && !player.abilities.instabuild) {context.getItemInHand().hurtAndBreak(1, player, (playerEntity) -> player.broadcastBreakEvent(context.getHand()));}
        }
    }
}
