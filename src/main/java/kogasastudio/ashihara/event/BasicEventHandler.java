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
        if(world.getBlockState(pos.up()).getBlock() == Blocks.AIR)
        {
            //铲土洼
            if(item.getItem() instanceof ShovelItem && (clickState.matchesBlock(Blocks.DIRT) || (player.isSneaking() && clickState.matchesBlock(Blocks.GRASS_PATH))))
            {
                world.playSound(player, pos, SoundEvents.ITEM_SHOVEL_FLATTEN, SoundCategory.BLOCKS, 1.0F, 1.0F);
                world.setBlockState(pos, BlockRegistryHandler.BLOCK_DIRT_DEPRESSION.get().getDefaultState());
                player.swingArm(event.getHand());
                InventoryHelper.spawnItemStack(world, pos.getX(), pos.getY() + 0.5F, pos.getZ(), new ItemStack(ItemRegistryHandler.DIRT_BALL.get()));
                if(!player.abilities.isCreativeMode) {item.damageItem(1, player, (playerEntity) -> player.sendBreakAnimation(event.getHand()));}
            }
        }
    }

    //锄头事件
    @SubscribeEvent
    public static void onHoeUse(UseHoeEvent event)
    {
        ItemUseContext context = event.getContext();
        BlockPos pos = context.getPos();
        World world = context.getWorld();
        BlockState clickState = world.getBlockState(pos);
        PlayerEntity player = context.getPlayer();
        //矫正位置偏移
        BlockRayTraceResult result = new BlockRayTraceResult
        (
            context.getHitVec(),
            context.getFace(),
            pos.down(),
            context.isInside()
        );
        if (clickState.matchesBlock(BlockRegistryHandler.BLOCK_DIRT_DEPRESSION.get()))
        {
            //相当于手动放置
            world.playSound(player, pos, SoundEvents.ITEM_HOE_TILL, SoundCategory.BLOCKS, 1.0F, world.getRandom().nextFloat() * 0.4F + 0.8F);
            BlockState placement = BlockRegistryHandler.BLOCK_WATER_FIELD.get().getStateForPlacement(new BlockItemUseContext(world, player, context.getHand(), context.getItem(), result));
            world.setBlockState(pos, placement == null ? BlockRegistryHandler.BLOCK_WATER_FIELD.get().getDefaultState() : placement, 1);
            if (player != null) {player.swingArm(context.getHand());}
            if (player != null && !player.abilities.isCreativeMode) {context.getItem().damageItem(1, player, (playerEntity) -> player.sendBreakAnimation(context.getHand()));}
        }
    }
}
