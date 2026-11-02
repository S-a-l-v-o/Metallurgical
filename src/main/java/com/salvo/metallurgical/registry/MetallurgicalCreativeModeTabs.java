package com.salvo.metallurgical.registry;

import com.salvo.metallurgical.Metallurgical;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class MetallurgicalCreativeModeTabs
{
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, Metallurgical.MOD_ID);

    public static final Supplier<CreativeModeTab> METALLURGICAL_TAB = CREATIVE_MODE_TABS.register("metallurgical_tab",
            () -> CreativeModeTab.builder()
                    .icon(() -> new ItemStack(MetallurgicalBlocks.BRICK_FURNACE.get()))
                    .title(Component.translatable("creativemodetab.metallurgical.metallurgical_tab"))
                    .displayItems((itemDisplayParameters, output) ->
                    {
                        output.accept(MetallurgicalBlocks.BRICK_FURNACE);
                    })
                    .build()
    );

    public static void register(IEventBus eventBus)
    {
        CREATIVE_MODE_TABS.register(eventBus);
    }
}
