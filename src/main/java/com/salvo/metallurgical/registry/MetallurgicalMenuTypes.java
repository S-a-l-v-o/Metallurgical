package com.salvo.metallurgical.registry;

import com.salvo.metallurgical.Metallurgical;
import com.salvo.metallurgical.screen.BrickFurnaceMenu;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;
import net.neoforged.neoforge.network.IContainerFactory;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class MetallurgicalMenuTypes
{
    public static final DeferredRegister<MenuType<?>> MENU_TYPES = DeferredRegister.create(Registries.MENU, Metallurgical.MOD_ID);

    public static final DeferredHolder<MenuType<?>, MenuType<BrickFurnaceMenu>> BRICK_FURNACE_MENU = registerMenuType("brick_furnace_menu", BrickFurnaceMenu::new);

    private static <T extends AbstractContainerMenu>DeferredHolder<MenuType<?>, MenuType<T>> registerMenuType(String name, IContainerFactory<T> factory)
    {
        return MENU_TYPES.register(name, () -> IMenuTypeExtension.create(factory));
    }

    public static void register(IEventBus eventBus)
    {
        MENU_TYPES.register(eventBus);
    }
}
