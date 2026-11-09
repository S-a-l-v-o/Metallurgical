package com.salvo.metallurgical.block.blockentity;

import com.salvo.metallurgical.block.BrickFurnaceBlock;
import com.salvo.metallurgical.recipe.BrickFurnaceRecipe;
import com.salvo.metallurgical.recipe.BrickFurnaceRecipeInput;
import com.salvo.metallurgical.recipe.MetallurgicalRecipes;
import com.salvo.metallurgical.registry.MetallurgicalBlockEntities;
import com.salvo.metallurgical.screen.BrickFurnaceMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.Containers;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.items.ItemStackHandler;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class BrickFurnaceBlockEntity extends BlockEntity implements MenuProvider
{
    protected final ContainerData data;
    private int maxMeltingProgress = 200; //default maxMeltingProgress
    private int meltingProgress = 0;
    private int maxBurnTime = 0;
    private int burnTime = 0;
    private float storedExperience = 0.0F;
    private float recipeExperience = 0.0F;

    public BrickFurnaceBlockEntity(BlockPos pos, BlockState state)
    {
        super(MetallurgicalBlockEntities.BRICK_FURNACE_BLOCK_ENTITY.get(), pos, state);
        this.data = new ContainerData()
        {
            @Override
            public int get(int i)
            {
                return switch (i)
                {
                    case 0 -> BrickFurnaceBlockEntity.this.meltingProgress;
                    case 1 -> BrickFurnaceBlockEntity.this.maxMeltingProgress;
                    case 2 -> BrickFurnaceBlockEntity.this.burnTime;
                    case 3 -> BrickFurnaceBlockEntity.this.maxBurnTime;
                    default -> 0;
                };
            }

            @Override
            public void set(int i, int value)
            {
                switch (i)
                {
                    case 0: BrickFurnaceBlockEntity.this.meltingProgress = value;
                    case 1: BrickFurnaceBlockEntity.this.maxMeltingProgress = value;
                    case 2: BrickFurnaceBlockEntity.this.burnTime = value;
                    case 3: BrickFurnaceBlockEntity.this.maxBurnTime = value;
                };
            }

            @Override
            public int getCount()
            {
                return 4;
            }
        };
    }

    // -----------------------------
    // Container/BE Inventory
    // -----------------------------
    private static final int[] INPUT_SLOTS = {0,1,2,3};
    private static final int INPUT_SLOTS_SIZE = 4;
    private static final int FUEL_SLOT = 4;
    private static final int CRUCIBLE_SLOT = 5;
    private static final int OUTPUT_SLOT = 6;
    private static final int INVENTORY_SIZE = 7;

    public final ItemStackHandler itemHandler = new ItemStackHandler(INVENTORY_SIZE)
    {
        @Override
        protected void onContentsChanged(int slot)
        {
            setChanged();
            if (slot == OUTPUT_SLOT)
            {
                if (itemHandler.getStackInSlot(OUTPUT_SLOT).isEmpty())
                {
                    rewardAndResetStoredExperience();
                }
            }
            if (!level.isClientSide())
            {
                level.sendBlockUpdated(getBlockPos(), getBlockState(),getBlockState(),3);
            }
        }
    };

    public void drops()
    {
        SimpleContainer inventory = new SimpleContainer(itemHandler.getSlots());
        for (int i = 0; i < itemHandler.getSlots(); i++)
        {
            inventory.setItem(i, itemHandler.getStackInSlot(i));
        }
        Containers.dropContents(this.level, this.worldPosition, inventory);
    }

    public List<ItemStack> getInputItems()
    {
        List<ItemStack> inputItems = new ArrayList<>();
        for (int i = 0; i < INPUT_SLOTS_SIZE; i++)
        {
            if (!itemHandler.getStackInSlot(i).isEmpty())
            {
                inputItems.add(itemHandler.getStackInSlot(i));
            }
        }
        return inputItems;
    }

    public void rewardAndResetStoredExperience()
    {
        if (!level.isClientSide && storedExperience != 0.0F)
        {
            int i = Mth.floor(storedExperience);
            float f = Mth.frac(storedExperience);
            if (f != 0.0F && Math.random() < (double)f)
            {
                ++i;
            }

            ExperienceOrb.award((ServerLevel) level, this.worldPosition.getBottomCenter(), i);
        }
        this.storedExperience = 0.0F;
    }
    // -----------------------------
    // Client & Server Ticking
    // -----------------------------
    public void clientTick(Level level, BlockPos blockPos, BlockState blockState)
    {
        //CREDIT TO: Heavily based on TheFogIOF/AlloySmelter from ForgeControllerBlockEntity class https://github.com/TheFogIOF/AlloySmelter.git
        if (!blockState.getValue(BrickFurnaceBlock.LIT)) { return; }
        if (level.random.nextInt(10) == 0) level.playLocalSound((double)blockPos.getX() + 0.5, (double)blockPos.getY() + 0.5, (double)blockPos.getZ() + 0.5, SoundEvents.BLASTFURNACE_FIRE_CRACKLE, SoundSource.BLOCKS, 0.3F + level.random.nextFloat(), level.random.nextFloat() * 0.7F + 0.5F, false);
        if (level.random.nextDouble() > 0.8)
        {
            double randYOffset = -0.1 + (0.1 + 0.1) * level.random.nextDouble();
            double randXZOffset = -0.25 + (0.25 + 0.25) * level.random.nextDouble();
            switch (blockState.getValue(BrickFurnaceBlock.FACING))
            {
                case EAST ->
                {
                    level.addParticle(ParticleTypes.FLAME, blockPos.getX() + 1.05, blockPos.getY() + 0.4 + randYOffset, blockPos.getZ() + 0.5 + randXZOffset, 0, 0, 0);
                }
                case WEST ->
                {
                    level.addParticle(ParticleTypes.FLAME, blockPos.getX() - 0.05, blockPos.getY() + 0.4 + randYOffset, blockPos.getZ() + 0.5 + randXZOffset, 0, 0, 0);
                }
                case NORTH ->
                {
                    level.addParticle(ParticleTypes.FLAME, blockPos.getX() + 0.5 + randXZOffset, blockPos.getY() + 0.4 + randYOffset, blockPos.getZ() - 0.05, 0, 0, 0);
                }
                case SOUTH ->
                {
                    level.addParticle(ParticleTypes.FLAME, blockPos.getX() + 0.5 + randXZOffset, blockPos.getY() + 0.4 + randYOffset, blockPos.getZ() + 1.05, 0, 0, 0);
                }
            }
            level.addParticle(ParticleTypes.CAMPFIRE_COSY_SMOKE, blockPos.getX() + 0.5, blockPos.getY() + 2.05, blockPos.getZ() + 0.5, 0, 0.1, 0);
        }
    }

    public void serverTick(Level level, BlockPos blockPos, BlockState blockState)
    {
        level.setBlock(blockPos, blockState.setValue(BrickFurnaceBlock.LIT, isBurning()), 3);

        if (validFuelInFuelSlot() && !isBurning() && canMelt())
        {
            this.maxBurnTime = itemHandler.getStackInSlot(FUEL_SLOT).getBurnTime(RecipeType.SMELTING);
            this.burnTime += itemHandler.getStackInSlot(FUEL_SLOT).getBurnTime(RecipeType.SMELTING);
            itemHandler.extractItem(FUEL_SLOT, 1, false);
        }

        if (isBurning())
        {
            if (canMelt())
            {
                if(isFinishedMelting())
                {
                    meltInputIntoOutput();
                    setChanged(level, blockPos, blockState);
                }
                else
                {
                    increaseMeltingProgress();
                }
            }
            else
            {
                decreaseMeltingProgress();
            }
            decreaseBurnTime();
        }
        else
        {
            decreaseMeltingProgress();
        }
    }

    // -----------------------------
    // Recipe Logic
    // -----------------------------
    private boolean validFuelInFuelSlot()
    {
        return itemHandler.getStackInSlot(FUEL_SLOT).getBurnTime(RecipeType.SMELTING) != 0;
    }

    private boolean isBurning()
    {
        return this.burnTime > 0;
    }

    private boolean canMelt()
    {
        Optional<RecipeHolder<BrickFurnaceRecipe>> currentRecipe = getCurrentRecipe();
        if (currentRecipe.isEmpty()) return false;
        this.maxMeltingProgress = currentRecipe.get().value().recipeMeltingTime();
        this.recipeExperience = currentRecipe.get().value().recipeExperience();
        ItemStack outputItemStack = currentRecipe.get().value().recipeOutputItemStack();
        return canInsertAmountIntoOutputSlot(outputItemStack.getCount()) && canInsertItemStackIntoOutputSlot(outputItemStack);
    }

    private Optional<RecipeHolder<BrickFurnaceRecipe>> getCurrentRecipe()
    {
        List<ItemStack> inputItemStacks = getInputItems();
        return this.level.getRecipeManager().getRecipeFor(MetallurgicalRecipes.BRICK_FURNACE_TYPE.get(), new BrickFurnaceRecipeInput(inputItemStacks), level);
    }

    private boolean canInsertItemStackIntoOutputSlot(ItemStack outputItemStack)
    {
        return itemHandler.getStackInSlot(OUTPUT_SLOT).isEmpty() || itemHandler.getStackInSlot(OUTPUT_SLOT).getItem() == outputItemStack.getItem();
    }

    private boolean canInsertAmountIntoOutputSlot(int outputStackSize)
    {
        int maxStackSize = itemHandler.getStackInSlot(OUTPUT_SLOT).isEmpty() ? 64 : itemHandler.getStackInSlot(OUTPUT_SLOT).getMaxStackSize();
        int currentStackSize = itemHandler.getStackInSlot(OUTPUT_SLOT).getCount();

        return outputStackSize + currentStackSize <= maxStackSize;
    }

    private void increaseMeltingProgress()
    {
        this.meltingProgress++;
    }

    private void decreaseMeltingProgress()
    {
        if (this.meltingProgress < 2)
        {
            this.meltingProgress = 0;
        }
        else
        {
            this.meltingProgress -= 2;
        }
    }

    private void decreaseBurnTime()
    {
        if (this.burnTime < 1)
        {
            this.burnTime = 0;
        }
        else
        {
            this.burnTime--;
        }
    }

    private boolean isFinishedMelting()
    {
        return this.meltingProgress >= this.maxMeltingProgress;
    }

    private void meltInputIntoOutput()
    {
        Optional<RecipeHolder<BrickFurnaceRecipe>> currentRecipe = getCurrentRecipe();

        ItemStack recipeOutputItemStack = currentRecipe.get().value().recipeOutputItemStack();
        List<ItemStack> recipeInputItemStacks = new ArrayList<>();
        for (Ingredient ingredient: currentRecipe.get().value().recipeIngredients())
        {
            if (!ingredient.hasNoItems())
            {
                recipeInputItemStacks.add(ingredient.getItems()[0]);
            }
        }

        for (ItemStack recipeInput : recipeInputItemStacks)
        {
            for (int i = 0; i < INPUT_SLOTS_SIZE; i++)
            {
                if (!itemHandler.getStackInSlot(i).isEmpty() && itemHandler.getStackInSlot(i).is(recipeInput.getItem()))
                {
                    itemHandler.extractItem(i, 1, false);
                    break;
                }
            }
        }

        itemHandler.setStackInSlot(OUTPUT_SLOT, new ItemStack(recipeOutputItemStack.getItem(), recipeOutputItemStack.getCount() + itemHandler.getStackInSlot(OUTPUT_SLOT).getCount()));
        this.meltingProgress = 0;
        if (!(this.storedExperience > 2048))
        {
            this.storedExperience += this.recipeExperience;
        }
        this.recipeExperience = 0.0F;
    }

    // -----------------------------
    // Menu & GUI
    // -----------------------------
    @Override
    public Component getDisplayName()
    {
        return Component.translatable("block.metallurgical.brick_furnace");
    }

    @Override
    public @Nullable AbstractContainerMenu createMenu(int id, Inventory inventory, Player player)
    {
        return new BrickFurnaceMenu(id, inventory, this, this.data);
    }

    // -----------------------------
    // NBT & Data
    // -----------------------------
    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries)
    {
        tag.put("brick_furnace.inventory", itemHandler.serializeNBT(registries));
        tag.putInt("brick_furnace.melting_progress", meltingProgress);
        tag.putInt("brick_furnace.max_melting_progress", maxMeltingProgress);
        tag.putInt("brick_furnace.fuel_time", burnTime);

        super.saveAdditional(tag, registries);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries)
    {
        itemHandler.deserializeNBT(registries, tag.getCompound("brick_furnace.inventory"));
        meltingProgress = tag.getInt("brick_furnace.melting_progress");
        maxMeltingProgress = tag.getInt("brick_furnace.max_melting_progress");
        burnTime = tag.getInt("brick_furnace.fuel_time");

        super.loadAdditional(tag, registries);
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries)
    {
        return saveWithoutMetadata(registries);
    }

    @Override
    public @Nullable Packet<ClientGamePacketListener> getUpdatePacket()
    {
        return ClientboundBlockEntityDataPacket.create(this);
    }
}
