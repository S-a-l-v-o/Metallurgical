package com.salvo.metallurgical.datagen;

import com.salvo.metallurgical.Metallurgical;
import com.salvo.metallurgical.registry.MetallurgicalBlocks;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.BlockTags;
import net.neoforged.neoforge.common.data.BlockTagsProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public class MetallurgicalBlockTagsProvider extends BlockTagsProvider
{
    public MetallurgicalBlockTagsProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, @Nullable ExistingFileHelper existingFileHelper)
    {
        super(output, lookupProvider, Metallurgical.MOD_ID, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider)
    {
        // -----------------------------
        // Mining Requirement
        // -----------------------------
        tag(BlockTags.MINEABLE_WITH_PICKAXE)
                .add(MetallurgicalBlocks.BRICK_FURNACE.get());

        // -----------------------------
        // Tool Tier Requirement
        // -----------------------------
        tag(BlockTags.NEEDS_STONE_TOOL)
                .add(MetallurgicalBlocks.BRICK_FURNACE.get())
                .add(MetallurgicalBlocks.FIRECLAY.get());
    }
}
