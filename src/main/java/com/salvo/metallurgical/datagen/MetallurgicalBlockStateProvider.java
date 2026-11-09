package com.salvo.metallurgical.datagen;

import com.salvo.metallurgical.Metallurgical;
import com.salvo.metallurgical.registry.MetallurgicalBlocks;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.client.model.generators.BlockStateProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.registries.DeferredBlock;

public class MetallurgicalBlockStateProvider extends BlockStateProvider
{
    public MetallurgicalBlockStateProvider(PackOutput output, ExistingFileHelper exFileHelper)
    {
        super(output, Metallurgical.MOD_ID, exFileHelper);
    }

    @Override
    protected void registerStatesAndModels()
    {
        blockWithItem(MetallurgicalBlocks.FIRECLAY);
    }

    private void blockWithItem(DeferredBlock<?> deferredBlock)
    {
        simpleBlockWithItem(deferredBlock.get(), cubeAll(deferredBlock.get()));
    }
}
