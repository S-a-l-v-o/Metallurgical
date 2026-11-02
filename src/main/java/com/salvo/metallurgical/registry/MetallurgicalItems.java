package com.salvo.metallurgical.registry;

import com.salvo.metallurgical.Metallurgical;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

public class MetallurgicalItems
{
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(Metallurgical.MOD_ID);

    public static void register(IEventBus eventBus)
    {
        ITEMS.register(eventBus);
    }
}
