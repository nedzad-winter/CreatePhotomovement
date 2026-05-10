package com.createphotomovement.content.kinetics.solarwindmill;

import com.createphotomovement.AllBlockEntityTypes;
import com.simibubi.create.content.contraptions.bearing.WindmillBearingBlock;
import com.simibubi.create.content.contraptions.bearing.WindmillBearingBlockEntity;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class SolarWindmillBearingBlock extends WindmillBearingBlock {

    public SolarWindmillBearingBlock(Properties properties) {
        super(properties);
    }

    @Override
    public Class<WindmillBearingBlockEntity> getBlockEntityClass() {
        // Fabric usually uses getBlockEntityClass instead of getBlockEntityType in some
        // versions,
        // but Create checks this for validity.
        // Actually, looking at WindmillBearingBlock in Fabric, it might just use
        // standard BE registration.
        // I'll stick to the NeoForge pattern but use
        // AllBlockEntityTypes.SOLAR_WINDMILL_BEARING
        // However, I need to cast it safely.
        return WindmillBearingBlockEntity.class;
    }

    @Override
    public BlockEntityType<? extends WindmillBearingBlockEntity> getBlockEntityType() {
        return AllBlockEntityTypes.SOLAR_WINDMILL_BEARING;
    }

    @Override
    @SuppressWarnings("deprecation")
    public boolean propagatesSkylightDown(BlockState state, BlockGetter level, BlockPos pos) {
        return true;
    }

    @Override
    @SuppressWarnings("deprecation")
    public int getLightBlock(BlockState state, BlockGetter level, BlockPos pos) {
        return 0;
    }

    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state,
            BlockEntityType<T> type) {
        return (l, p, s, be) -> {
            if (be instanceof SolarWindmillBearingBlockEntity solarBe) {
                // Manually call the standard tick logic (inherited from
                // WindmillBearingBlockEntity -> SmartBlockEntity)
                solarBe.tick();
                // Then call our custom solar logic (server-side only)
                if (!level.isClientSide) {
                    solarBe.solarTick();
                }
            }
        };
    }
}
