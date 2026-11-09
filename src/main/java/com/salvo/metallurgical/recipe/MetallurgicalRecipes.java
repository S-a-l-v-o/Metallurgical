package com.salvo.metallurgical.recipe;

import com.salvo.metallurgical.Metallurgical;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class MetallurgicalRecipes
{
    public static final DeferredRegister<RecipeSerializer<?>> RECIPE_SERIALIZERS = DeferredRegister.create(Registries.RECIPE_SERIALIZER, Metallurgical.MOD_ID);

    public static final DeferredRegister<RecipeType<?>> RECIPE_TYPES = DeferredRegister.create(Registries.RECIPE_TYPE, Metallurgical.MOD_ID);

    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<BrickFurnaceRecipe>> BRICK_FURNACE_SERIALIZER = RECIPE_SERIALIZERS.register("brick_furnace_melting", BrickFurnaceRecipe.Serializer::new);

    public static final DeferredHolder<RecipeType<?>, RecipeType<BrickFurnaceRecipe>> BRICK_FURNACE_TYPE = RECIPE_TYPES.register("brick_furnace_melting", () -> new RecipeType<BrickFurnaceRecipe>()
    {
        @Override
        public String toString()
        {
            return "brick_furnace_melting";
        }
    });

    public static void register(IEventBus eventBus)
    {
        RECIPE_SERIALIZERS.register(eventBus);
        RECIPE_TYPES.register(eventBus);
    }
}
