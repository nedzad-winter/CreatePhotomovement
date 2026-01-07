package com.createphotomovement;

import com.createphotomovement.content.kinetics.solargenerator.SolarGeneratorBlockEntity;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.DeferredHolder;

public class AllBlockEntityTypes {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITY_TYPES = DeferredRegister
            .create(Registries.BLOCK_ENTITY_TYPE, CreatePhotomovement.MOD_ID);

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<SolarGeneratorBlockEntity>> SOLAR_GENERATOR = BLOCK_ENTITY_TYPES
            .register("solar_generator",
                    () -> BlockEntityType.Builder.of(
                            (pos, state) -> new SolarGeneratorBlockEntity(AllBlockEntityTypes.SOLAR_GENERATOR.get(),
                                    pos, state),
                            AllBlocks.SOLAR_GENERATOR.get(),
                            AllBlocks.WHITE_SOLAR_GENERATOR.get(),
                            AllBlocks.ORANGE_SOLAR_GENERATOR.get(),
                            AllBlocks.MAGENTA_SOLAR_GENERATOR.get(),
                            AllBlocks.LIGHT_BLUE_SOLAR_GENERATOR.get(),
                            AllBlocks.YELLOW_SOLAR_GENERATOR.get(),
                            AllBlocks.LIME_SOLAR_GENERATOR.get(),
                            AllBlocks.PINK_SOLAR_GENERATOR.get(),
                            AllBlocks.GRAY_SOLAR_GENERATOR.get(),
                            AllBlocks.LIGHT_GRAY_SOLAR_GENERATOR.get(),
                            AllBlocks.CYAN_SOLAR_GENERATOR.get(),
                            AllBlocks.PURPLE_SOLAR_GENERATOR.get(),
                            AllBlocks.BLUE_SOLAR_GENERATOR.get(),
                            AllBlocks.BROWN_SOLAR_GENERATOR.get(),
                            AllBlocks.GREEN_SOLAR_GENERATOR.get(),
                            AllBlocks.RED_SOLAR_GENERATOR.get(),
                            AllBlocks.BLACK_SOLAR_GENERATOR.get()).build(null));
}
