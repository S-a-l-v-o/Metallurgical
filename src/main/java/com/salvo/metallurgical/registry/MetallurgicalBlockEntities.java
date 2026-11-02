package com.salvo.metallurgical.registry;

import com.salvo.metallurgical.Metallurgical;
import com.salvo.metallurgical.block.blockentity.BrickFurnaceBlockEntity;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class MetallurgicalBlockEntities
{
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITY_TYPES = DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, Metallurgical.MOD_ID);

    public static final Supplier<BlockEntityType<BrickFurnaceBlockEntity>> BRICK_FURNACE_BLOCK_ENTITY = BLOCK_ENTITY_TYPES.register("brick_forge_block_entity",
            () -> BlockEntityType.Builder.of(
                    BrickFurnaceBlockEntity::new,
                    MetallurgicalBlocks.BRICK_FURNACE.get()
            )
            .build(null)
    );

    public static void register(IEventBus eventBus)
    {
        BLOCK_ENTITY_TYPES.register(eventBus);
    }
}
