package com.createphotomovement;

import com.createphotomovement.content.kinetics.solargenerator.SolarGeneratorBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.DeferredBlock;

public class AllBlocks {
    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(CreatePhotomovement.MOD_ID);

    public static final DeferredBlock<SolarGeneratorBlock> SOLAR_GENERATOR = BLOCKS.register("solar_generator",
            () -> new SolarGeneratorBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.WOOD)
                    .strength(0.2F) // Daylight detector
                    .sound(SoundType.WOOD)
                    .noOcclusion()
            ));
}
