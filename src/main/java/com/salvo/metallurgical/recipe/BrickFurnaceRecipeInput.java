package com.salvo.metallurgical.recipe;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeInput;

import java.util.List;

public record BrickFurnaceRecipeInput(List<ItemStack> inputItems) implements RecipeInput
{
    @Override
    public ItemStack getItem(int slot)
    {
        ItemStack inputItem;
        switch (slot)
        {
            case 0 -> inputItem = this.inputItems.get(0);
            case 1 -> inputItem = this.inputItems.get(1);
            case 2 -> inputItem = this.inputItems.get(2);
            case 3 -> inputItem = this.inputItems.get(3);
            default -> throw new IllegalArgumentException("Brick Furnace does not contain recipe input slot " + slot);
        }

        return inputItem;
    }

    public List<ItemStack> getItems()
    {
        return inputItems;
    }

    @Override
    public int size()
    {
        return inputItems.size();
    }

    @Override
    public boolean isEmpty()
    {
        return inputItems.isEmpty();
    }
}
