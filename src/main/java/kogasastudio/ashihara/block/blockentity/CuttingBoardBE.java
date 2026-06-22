package kogasastudio.ashihara.block.blockentity;

import kogasastudio.ashihara.helper.ParticleHelper;
import kogasastudio.ashihara.helper.RecipeHelper;
import kogasastudio.ashihara.interaction.recipes.CuttingBoardRecipe;
import kogasastudio.ashihara.registry.BlockEntities;
import kogasastudio.ashihara.registry.RecipeTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.Vec3;

import java.util.Optional;

//import static kogasastudio.ashihara.Ashihara.LOGGER_MAIN;

public class CuttingBoardBE extends AshiharaCommonBE
{
    private ItemStack content = ItemStack.EMPTY;
    public float[] displayRot = new float[] {0, 0, 0, 0};

    public CuttingBoardBE(BlockPos pos, BlockState state)
    {
        super(BlockEntities.CUTTING_BOARD_BE.get(), pos, state);
    }

    public ItemStack getContent()
    {
        return this.content.copy();
    }

    public Optional<RecipeHolder<CuttingBoardRecipe>> tryMatchRecipe(ItemStack content)
    {
        return RecipeHelper.getRecipesByType(this.level, RecipeTypes.CUTTING_BOARD.get())
                .stream()
                .filter(h -> h.value().getInput().test(content))
                .findFirst();
    }

    public void cut(CuttingBoardRecipe recipe, ItemStack toolStack)
    {
        if (this.level == null) return;
        SoundEvent event = recipe.getCustomSound() != null ? recipe.getCustomSound() : kogasastudio.ashihara.registry.SoundEvents.CUT.get();
        this.level.playSound(null, this.worldPosition, event, SoundSource.BLOCKS, 1.0f, 1.0f);
        if (this.level.isClientSide())
        {
            ParticleHelper.spawnItemStackDestruction(this.level, this.content, new Vec3(this.worldPosition.getX() + 0.5d, this.worldPosition.getY() + 0.7d, this.worldPosition.getZ() + 0.5d), 10);
        }
        for (int i = 0; i < this.content.getCount(); i += 1)
        {
            for (ItemStack stack : recipe.getOutput())
            {
                Block.popResource(this.level, this.worldPosition, stack.copy());
            }
        }
        this.content = ItemStack.EMPTY;
        setChanged();
    }

    public boolean handleInteraction(Player playerIn, InteractionHand handIn, Level worldIn, BlockPos posIn)
    {
        ItemStack stack = playerIn.getItemInHand(handIn);
        if (!this.content.isEmpty())
        {
            if (stack.isEmpty())
            {
                playerIn.setItemInHand(handIn, this.content);
                this.content = ItemStack.EMPTY;
                worldIn.playSound(playerIn, posIn, SoundEvents.ITEM_FRAME_REMOVE_ITEM, SoundSource.BLOCKS, 1.0f, 1.0f);
                setChanged();
                return true;
            } else
            {
                Optional<RecipeHolder<CuttingBoardRecipe>> recipe = tryMatchRecipe(this.content);
                if (recipe.isPresent() && recipe.get().value().getTool().test(stack))
                {
                    CuttingBoardRecipe r = recipe.get().value();
                    this.cut(r, stack);
                    if (r.shouldConsume() && !playerIn.isCreative())
                    {
                        if (stack.isDamageableItem()) stack.hurtAndBreak(1, playerIn, stack.getEquipmentSlot());
                        else stack.shrink(1);
                    }
                    return true;
                }
            }
        } else
        {
            this.content = stack.split(Math.min(stack.getCount(), 4));
            worldIn.playSound(playerIn, posIn, SoundEvents.ITEM_FRAME_ADD_ITEM, SoundSource.BLOCKS, 1.0f, 1.0f);
            this.cacheRot(worldIn.getGameTime());
            setChanged();
            return true;
        }
        return false;
    }

    private void cacheRot(long seed)
    {
        RandomSource displayRand = RandomSource.create(seed);
        for (int i = 0; i < 4; i++)
        {
            this.displayRot[i] = displayRand.nextFloat();
        }
    }

    @Override
    public void preRemoveSideEffects(BlockPos pos, BlockState state)
    {
        if (this.level != null) Block.popResource(this.level, this.worldPosition, this.content.copy());
        super.preRemoveSideEffects(pos, state);
    }

    @Override
    public void onLoad()
    {
        this.cacheRot(42L);
        super.onLoad();
    }

    @Override
    public void loadAdditional(ValueInput input)
    {
        super.loadAdditional(input);
        this.content = input.read("Content", ItemStack.CODEC).orElse(ItemStack.EMPTY);
    }

    @Override
    protected void saveAdditional(ValueOutput output)
    {
        super.saveAdditional(output);
        if (!this.content.isEmpty()) output.store("Content", ItemStack.CODEC, this.content);
    }
}
