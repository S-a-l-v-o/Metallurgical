package com.salvo.metallurgical.registry;

import com.salvo.metallurgical.Metallurgical;
import com.salvo.metallurgical.block.BrickFurnaceBlock;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class MetallurgicalBlocks
{
    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(Metallurgical.MOD_ID);

    public static final DeferredBlock<Block> FIRECLAY = registerBlock("fireclay",
            () -> new Block(BlockBehaviour.Properties.of()
                    .sound(SoundType.WET_GRASS)
                    .strength(1.0F,2.0F)
                    .mapColor(MapColor.COLOR_RED)
            )
    );

    public static final DeferredBlock<Block> BRICK_FURNACE = registerBlock("brick_furnace",
            () -> new BrickFurnaceBlock(BlockBehaviour.Properties.of()
                    .sound(SoundType.STONE)
                    .strength(2.0F,6.0F)
                    .mapColor(MapColor.COLOR_RED)
                    .requiresCorrectToolForDrops()
                    .noOcclusion()
            )
    );

    //helpers for registering block items
    private static <T extends Block> DeferredBlock<T> registerBlock(String name, Supplier<T> block)
    {
        DeferredBlock<T> toReturn = BLOCKS.register(name, block);
        registerBlockItem(name, toReturn);
        return toReturn;
    }

    private static <T extends Block> void registerBlockItem(String name, DeferredBlock<T> block)
    {
        MetallurgicalItems.ITEMS.register(name, () -> new BlockItem(block.get(), new Item.Properties()));
    }

    public static void register(IEventBus eventBus)
    {
        BLOCKS.register(eventBus);
    }
}
