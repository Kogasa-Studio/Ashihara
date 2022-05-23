package kogasastudio.ashihara.block.tileentities;

import kogasastudio.ashihara.interaction.recipe.CuttingBoardRecipe;
import net.minecraft.block.BlockState;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.particles.BlockParticleData;
import net.minecraft.particles.IParticleData;
import net.minecraft.particles.ItemParticleData;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.wrapper.RecipeWrapper;

import java.util.Optional;
import java.util.Random;

//import static kogasastudio.ashihara.Ashihara.LOGGER_MAIN;

public class CuttingBoardTE extends AshiharaMachineTE
{
    private ItemStack content = ItemStack.EMPTY;

    public CuttingBoardTE() {super(TERegistryHandler.CUTTING_BOARD_TE.get());}

    public ItemStack getContent() {return this.content.copy();}

    public Optional<CuttingBoardRecipe> tryMatchRecipe(RecipeWrapper wrapper)
    {
        if(this.level == null) return Optional.empty();

        return this.level.getRecipeManager().getRecipeFor(CuttingBoardRecipe.TYPE, wrapper, this.level);
    }

    public void cut(CuttingBoardRecipe recipe)
    {
        if (this.level == null) return;
        SoundEvent event = SoundEvents.AXE_STRIP;
        this.level.playSound(null, this.worldPosition, event, SoundCategory.BLOCKS, 1.0f, 1.0f);
        if (this.level.isClientSide())
        {
            Random random = this.level.getRandom();
            IParticleData data = this.content.getItem() instanceof BlockItem
            ? new BlockParticleData(ParticleTypes.BLOCK, ((BlockItem) this.content.getItem()).getBlock().defaultBlockState())
            : new ItemParticleData(ParticleTypes.ITEM, this.content);
            for (int i = 0; i < 10; i += 1)
            {
                this.level.addParticle
                (
                    data,
                    (double) this.worldPosition.getX() + 0.5D,
                    (double) this.worldPosition.getY() + 0.7D,
                    (double) this.worldPosition.getZ() + 0.5D,
                    ((double) random.nextFloat() - 0.5D) * 0.2D,
                    ((double) random.nextFloat() - 0.5D) * 0.2D,
                    ((double) random.nextFloat() - 0.5D) * 0.2D
                );
            }
        }
        for (int i = 0; i < this.content.getCount(); i += 1)
        {
            for (ItemStack stack : recipe.getOutput())
            {
                ItemEntity entity = new ItemEntity
                (this.level, this.worldPosition.getX() + 0.5d, this.worldPosition.getY() + 0.5d, this.worldPosition.getZ() + 0.5d, stack.copy());
                entity.setDefaultPickUpDelay();
                this.level.addFreshEntity(entity);
            }
        }
        this.content = ItemStack.EMPTY;
        setChanged();
    }

    public boolean handleInteraction(PlayerEntity playerIn, Hand handIn, World worldIn, BlockPos posIn)
    {
        ItemStack stack = playerIn.getItemInHand(handIn);
        if (!this.content.isEmpty())
        {
            if (stack.isEmpty())
            {
                playerIn.setItemInHand(handIn, this.content);
                this.content = ItemStack.EMPTY;
                worldIn.playSound(playerIn, posIn, SoundEvents.ITEM_FRAME_REMOVE_ITEM, SoundCategory.BLOCKS, 1.0f, 1.0f);
                setChanged();
                return true;
            }
            else
            {
                ItemStackHandler inv = new ItemStackHandler(NonNullList.withSize(1, this.content));
                Optional<CuttingBoardRecipe> recipe = tryMatchRecipe(new RecipeWrapper(inv));
//                LOGGER_MAIN.info
//                (
//                    "\n{\n    inv: " + inv.serializeNBT()
//                    + ";\n    recipe: " + (recipe.isPresent() ? recipe.get().getInfo() : "not provided")
//                    + ";\n    tool: " + (recipe.isPresent() ? recipe.get().getTool().getName() : "not provided")
//                    + ";\n    tool_matches: " + (recipe.isPresent() ? recipe.get().getTool().toolMatches(stack) : "not provided")
//                    + ";\n}"
//                );
                if (recipe.isPresent() && recipe.get().getTool().toolMatches(stack))
                {
                    this.cut(recipe.get());
                    if (!playerIn.isCreative()) stack.hurtAndBreak(1, playerIn, player -> player.broadcastBreakEvent(handIn));
                    return true;
                }
            }
        }
        else
        {
            this.content = stack.split(Math.min(stack.getCount(), 4));
            worldIn.playSound(playerIn, posIn, SoundEvents.ITEM_FRAME_ADD_ITEM, SoundCategory.BLOCKS, 1.0f, 1.0f);
            setChanged();
            return true;
        }
        return false;
    }

    @Override
    public void load(BlockState state, CompoundNBT nbt)
    {
        super.load(state, nbt);
        this.content = ItemStack.of(nbt.getCompound("content"));
    }

    @Override
    public CompoundNBT save(CompoundNBT compound)
    {
        compound.put("content", this.content.save(new CompoundNBT()));
        super.save(compound);
        return compound;
    }
}
