package com.salvo.metallurgical.datagen;

import com.salvo.metallurgical.registry.MetallurgicalBlocks;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.*;
import net.minecraft.world.item.Items;
import net.neoforged.neoforge.common.conditions.IConditionBuilder;

import java.util.concurrent.CompletableFuture;

public class MetallurgicalRecipeProvider extends RecipeProvider implements IConditionBuilder
{
    public MetallurgicalRecipeProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries)
    {
        super(output, registries);
    }

    @Override
    protected void buildRecipes(RecipeOutput recipeOutput)
    {
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, MetallurgicalBlocks.BRICK_FURNACE.get(),1)
                .pattern("BBB")
                .pattern("B B")
                .pattern("BCB")
                .define('B', Items.BRICKS)
                .define('C', Items.CAMPFIRE)
                .unlockedBy("has_bricks", has(Items.BRICKS))
                .save(recipeOutput, "metallurgical:craftng/brick_furnace");
    }
}
