package com.salvo.metallurgical.recipe;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.common.util.RecipeMatcher;

import java.util.ArrayList;
import java.util.List;

public record BrickFurnaceRecipe(NonNullList<Ingredient> recipeIngredients, ItemStack recipeOutputItemStack, int recipeMeltingTime, float recipeExperience) implements Recipe<BrickFurnaceRecipeInput>
{
    @Override
    public NonNullList<Ingredient> getIngredients()
    {
        return this.recipeIngredients;
    }

    @Override
    public ItemStack getResultItem(HolderLookup.Provider provider)
    {
        return recipeOutputItemStack;
    }

    @Override
    public boolean matches(BrickFurnaceRecipeInput brickFurnaceRecipeInput, Level level)
    {
        if (level.isClientSide) { return false; }

        List<ItemStack> nonEmptyItems = new ArrayList<>();
        for (ItemStack inputItem : brickFurnaceRecipeInput.inputItems())
        {
            if (!inputItem.isEmpty())
            {
                nonEmptyItems.add(inputItem);
            }
        }

        if (nonEmptyItems.size() != recipeIngredients.size()) { return false; };
        return RecipeMatcher.findMatches(nonEmptyItems, recipeIngredients) != null;
    }

    @Override
    public ItemStack assemble(BrickFurnaceRecipeInput brickFurnaceRecipeInput, HolderLookup.Provider provider)
    {
        return recipeOutputItemStack.copy();
    }

    @Override
    public boolean canCraftInDimensions(int i, int i1)
    {
        return true;
    }

    @Override
    public RecipeType<?> getType()
    {
        return MetallurgicalRecipes.BRICK_FURNACE_TYPE.get();
    }

    // -----------------------------
    // Serializing
    // -----------------------------
    @Override
    public RecipeSerializer<?> getSerializer()
    {
        return MetallurgicalRecipes.BRICK_FURNACE_SERIALIZER.get();
    }

    public static class Serializer implements RecipeSerializer<BrickFurnaceRecipe>
    {
        private static final MapCodec<BrickFurnaceRecipe> CODEC = RecordCodecBuilder.mapCodec(recipeInstance -> recipeInstance.group(
                Ingredient.CODEC_NONEMPTY.listOf().fieldOf("ingredients").xmap((ingredients) ->
                {
                    NonNullList<Ingredient> nonNullIngredients = NonNullList.create();
                    nonNullIngredients.addAll(ingredients);
                    return nonNullIngredients;
                }, ingredients -> ingredients).forGetter(BrickFurnaceRecipe::recipeIngredients),
                ItemStack.CODEC.fieldOf("result").forGetter(BrickFurnaceRecipe::recipeOutputItemStack),
                Codec.INT.optionalFieldOf("melting_time", 200).forGetter(BrickFurnaceRecipe::recipeMeltingTime),
                Codec.FLOAT.optionalFieldOf("xp", 0.0F).forGetter(BrickFurnaceRecipe::recipeExperience)
        ).apply(recipeInstance, BrickFurnaceRecipe::new));

        private static final StreamCodec<RegistryFriendlyByteBuf, BrickFurnaceRecipe> STREAM_CODEC = StreamCodec.of(BrickFurnaceRecipe::toNetwork, BrickFurnaceRecipe::fromNetwork);

        @Override
        public MapCodec<BrickFurnaceRecipe> codec() { return CODEC; }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, BrickFurnaceRecipe> streamCodec() { return STREAM_CODEC; }
    }

    private static BrickFurnaceRecipe fromNetwork(RegistryFriendlyByteBuf buffer)
    {
        int i = buffer.readVarInt();
        NonNullList<Ingredient> inputIngredientsIn = NonNullList.withSize(i, Ingredient.of());
        inputIngredientsIn.replaceAll((ignored) -> Ingredient.CONTENTS_STREAM_CODEC.decode(buffer));
        ItemStack outputItemIn = ItemStack.STREAM_CODEC.decode(buffer);
        int meltingTime = buffer.readVarInt();
        float exp = buffer.readFloat();

        return new BrickFurnaceRecipe(inputIngredientsIn, outputItemIn, meltingTime, exp);
    }

    private static void toNetwork(RegistryFriendlyByteBuf buffer, BrickFurnaceRecipe recipe)
    {
        buffer.writeVarInt(recipe.recipeIngredients.size());
        for (Ingredient ingredient : recipe.recipeIngredients)
        {
            Ingredient.CONTENTS_STREAM_CODEC.encode(buffer, ingredient);
        }
        ItemStack.STREAM_CODEC.encode(buffer, recipe.recipeOutputItemStack);
        buffer.writeVarInt(recipe.recipeMeltingTime);
        buffer.writeFloat(recipe.recipeExperience);
    }
}
