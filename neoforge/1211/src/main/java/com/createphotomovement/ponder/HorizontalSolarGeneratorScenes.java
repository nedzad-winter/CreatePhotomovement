package com.createphotomovement.ponder;

import com.createphotomovement.AllBlocks;

import net.createmod.ponder.api.scene.SceneBuilder;
import net.createmod.ponder.api.scene.SceneBuildingUtil;
import net.createmod.ponder.api.scene.Selection;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;

/**
 * Ponder scene storyboards for the Horizontal Solar Generator.
 */
public class HorizontalSolarGeneratorScenes {

    /**
     * Scene 1: Horizontal Basics
     */
    public static void basics(SceneBuilder builder, SceneBuildingUtil util) {
        builder.title("horizontal_solar_generator.basics", "Horizontal Solar Generator Basics");
        builder.configureBasePlate(0, 0, 5);
        builder.showBasePlate();

        // Define coordinates for the generators
        // Assuming schematic has them at (3, 1, 2) and (1, 1, 2) similar to vertical
        BlockPos standardGenPos = util.grid().at(3, 1, 2);
        BlockPos advancedGenPos = util.grid().at(1, 1, 2);

        // Select the entire scene to ensure everything is shown
        Selection wholeScene = util.select().fromTo(0, 0, 0, 5, 5, 5);

        builder.idle(10);

        // === KEYFRAME 1: Introduction ===
        builder.addKeyframe();

        // Show everything
        builder.world().showSection(wholeScene, Direction.DOWN);
        builder.idle(20);

        // Explain Standard Generator
        builder.overlay().showText(60)
                .text("The Horizontal Solar Generator produces identical RPM vertical one (16 RPM)")
                .pointAt(util.vector().blockSurface(standardGenPos, Direction.UP))
                .placeNearTarget();
        builder.idle(70);

        // Explain Advanced Generator
        builder.overlay().showText(60)
                .text("The Advanced Horizontal Solar Generator produces identical RPM vertical one (32 RPM)")
                .pointAt(util.vector().blockSurface(advancedGenPos, Direction.UP))
                .placeNearTarget();
        builder.idle(70);

        builder.overlay().showText(60)
                .text("Both require direct access to the sky")
                .pointAt(util.vector().topOf(2, 3, 2))
                .placeNearTarget();
        builder.idle(70);

        builder.idle(20);
    }

    /**
     * Scene 2: Directional and Time-based generation
     */
    public static void directions(SceneBuilder builder, SceneBuildingUtil util) {
        builder.title("horizontal_solar_generator.directions", "Directional Generation");
        builder.configureBasePlate(0, 0, 5);
        builder.showBasePlate();

        // Select blocks starting from layer 1 (excluding baseplate)
        Selection blocks = util.select().layersFrom(1);

        builder.idle(10);

        // === KEYFRAME 1: Introduction ===
        builder.addKeyframe();

        // Show blocks
        builder.world().showSection(blocks, Direction.DOWN);
        builder.idle(20);

        builder.overlay().showText(70)
                .text("Horizontal Solar Generators calculate SU dynamically based on facing direction and time of day")
                .pointAt(util.vector().topOf(2, 1, 2))
                .placeNearTarget();
        builder.idle(80);

        // === KEYFRAME 2: SU Range ===
        builder.addKeyframe();
        builder.overlay().showText(70)
                .text("These range from 128 to 1024 SU for the Standard")
                .pointAt(util.vector().topOf(2, 1, 1))
                .placeNearTarget();
        builder.idle(80);

        builder.overlay().showText(70)
                .text("And 256 to 2048 SU for the Advanced")
                .pointAt(util.vector().topOf(2, 1, 3))
                .placeNearTarget();
        builder.idle(80);

        // === KEYFRAME 3: East facing ===
        builder.addKeyframe();
        // Highlight East facing (Assume one of them is east facing, or just point
        // generally)
        builder.overlay().showText(60)
                .text("East facing generators reach maximum efficiency in the morning")
                // Directions in the schematic are reversed
                .pointAt(util.vector().blockSurface(util.grid().at(1, 1, 1), Direction.WEST))
                .placeNearTarget();
        builder.idle(70);

        // === KEYFRAME 4: West facing ===
        builder.addKeyframe();
        // Highlight West facing
        builder.overlay().showText(60)
                .text("West facing generators reach maximum efficiency in the evening")
                // Directions in the schematic are reversed
                .pointAt(util.vector().blockSurface(util.grid().at(3, 1, 1), Direction.EAST))
                .placeNearTarget();
        builder.idle(70);

        // === KEYFRAME 5: North/South facing ===
        builder.addKeyframe();
        // Highlight North/South
        builder.overlay().showText(80)
                .text("North and South facing generators produce a constant reduced output")
                .pointAt(util.vector().blockSurface(util.grid().at(2, 1, 2), Direction.UP))
                .placeNearTarget();
        builder.idle(90);

        builder.overlay().showText(60)
                .text("Standard: 128 SU, Advanced: 256 SU")
                .pointAt(util.vector().blockSurface(util.grid().at(2, 1, 2), Direction.UP))
                .placeNearTarget();
        builder.idle(70);
    }

    /**
     * Scene 3: Obstructions
     */
    public static void obstructions(SceneBuilder builder, SceneBuildingUtil util) {
        builder.title("horizontal_solar_generator.obstructions", "Obstructions");
        builder.configureBasePlate(0, 0, 5);
        builder.showBasePlate();

        // Coordinates
        BlockPos generatorPos = util.grid().at(3, 1, 2);
        BlockPos glassPos = util.grid().at(2, 1, 2);
        BlockPos snowPos = util.grid().at(1, 1, 2);

        // Select blocks
        Selection generatorSel = util.select().position(generatorPos);
        Selection glassSel = util.select().position(glassPos);
        Selection snowSel = util.select().position(snowPos);

        builder.idle(10);

        // === KEYFRAME 1: Introduction ===
        builder.addKeyframe();

        // Show generator
        builder.world().showSection(generatorSel, Direction.DOWN);
        builder.idle(20);

        builder.overlay().showText(70)
                .text("The generator checks up to 10 blocks in front of its solar face for blocks")
                .pointAt(util.vector().blockSurface(generatorPos, Direction.WEST))
                .placeNearTarget();
        builder.idle(80);

        builder.overlay().showText(60)
                .text("If no block is in its way it produces its full efficiency")
                .pointAt(util.vector().blockSurface(generatorPos, Direction.WEST))
                .placeNearTarget();
        builder.idle(70);

        // === KEYFRAME 2: Adjacent Obstruction ===
        builder.addKeyframe();

        // Show Glass
        builder.world().showSection(glassSel, Direction.DOWN);
        builder.idle(20);

        builder.overlay().showText(60)
                .text("Transparent blocks don't affect the production")
                .pointAt(util.vector().blockSurface(glassPos, Direction.UP))
                .placeNearTarget();
        builder.idle(70);

        builder.overlay().showText(60)
                .text("If you place a solid block directly in front of it, the generator produces 0")
                .pointAt(util.vector().blockSurface(glassPos, Direction.UP))
                .placeNearTarget();
        builder.idle(70);

        // === KEYFRAME 3: Distance Obstruction ===
        builder.addKeyframe();

        // Show Snow
        builder.world().showSection(snowSel, Direction.DOWN);
        builder.idle(20);

        builder.overlay().showText(70)
                .text("If you have a solid block from the 2nd to 10th position")
                .pointAt(util.vector().blockSurface(snowPos, Direction.UP))
                .placeNearTarget();
        builder.idle(80);

        builder.overlay().showText(70)
                .text("The generator is considered 'shadowed' and it produces its bare minimum")
                .pointAt(util.vector().blockSurface(generatorPos, Direction.UP))
                .placeNearTarget();
        builder.idle(80);

        builder.overlay().showText(60)
                .text("128 SU for the standard and 256 SU for the advanced")
                .pointAt(util.vector().blockSurface(generatorPos, Direction.UP))
                .placeNearTarget();
        builder.idle(70);
    }

    /**
     * Scene 4: Dyeing
     */
    public static void dyeing(SceneBuilder builder, SceneBuildingUtil util) {
        builder.title("horizontal_solar_generator.dyeing", "Color Variants");
        builder.configureBasePlate(0, 0, 5);
        builder.showBasePlate();

        BlockPos standardPos = util.grid().at(3, 1, 2);
        BlockPos advancedPos = util.grid().at(1, 1, 2);

        Selection standardSel = util.select().position(standardPos);
        Selection advancedSel = util.select().position(advancedPos);

        builder.idle(10);
        builder.world().showSection(standardSel, Direction.DOWN);
        builder.world().showSection(advancedSel, Direction.DOWN);
        builder.idle(20);

        builder.overlay().showText(60)
                .text("Both Standard and Advanced Horizontal Solar Generators can be dyed")
                .pointAt(util.vector().blockSurface(standardPos, Direction.UP))
                .placeNearTarget();
        builder.idle(70);

        builder.overlay().showText(60)
                .text("Right-click with any dye to change the color")
                .pointAt(util.vector().blockSurface(advancedPos, Direction.UP))
                .placeNearTarget();
        builder.idle(40);

        // Animate Standard
        builder.world().setBlock(standardPos, AllBlocks.BLUE_HORIZONTAL_SOLAR_GENERATOR.get().defaultBlockState()
                .setValue(net.minecraft.world.level.block.state.properties.BlockStateProperties.HORIZONTAL_FACING,
                        Direction.NORTH),
                true);
        builder.world().setBlock(advancedPos, AllBlocks.BLUE_HORZ_ADV_SOLAR_GENERATOR.get().defaultBlockState()
                .setValue(net.minecraft.world.level.block.state.properties.BlockStateProperties.HORIZONTAL_FACING,
                        Direction.NORTH),
                true);
        builder.idle(20);

        builder.world().setBlock(standardPos, AllBlocks.RED_HORIZONTAL_SOLAR_GENERATOR.get().defaultBlockState()
                .setValue(net.minecraft.world.level.block.state.properties.BlockStateProperties.HORIZONTAL_FACING,
                        Direction.NORTH),
                true);
        builder.world().setBlock(advancedPos, AllBlocks.RED_HORZ_ADV_SOLAR_GENERATOR.get().defaultBlockState()
                .setValue(net.minecraft.world.level.block.state.properties.BlockStateProperties.HORIZONTAL_FACING,
                        Direction.NORTH),
                true);
        builder.idle(20);

        builder.world().setBlock(standardPos, AllBlocks.YELLOW_HORIZONTAL_SOLAR_GENERATOR.get().defaultBlockState()
                .setValue(net.minecraft.world.level.block.state.properties.BlockStateProperties.HORIZONTAL_FACING,
                        Direction.NORTH),
                true);
        builder.world().setBlock(advancedPos, AllBlocks.YELLOW_HORZ_ADV_SOLAR_GENERATOR.get().defaultBlockState()
                .setValue(net.minecraft.world.level.block.state.properties.BlockStateProperties.HORIZONTAL_FACING,
                        Direction.NORTH),
                true);
        builder.idle(20);

        builder.world().setBlock(standardPos, AllBlocks.LIME_HORIZONTAL_SOLAR_GENERATOR.get().defaultBlockState()
                .setValue(net.minecraft.world.level.block.state.properties.BlockStateProperties.HORIZONTAL_FACING,
                        Direction.NORTH),
                true);
        builder.world().setBlock(advancedPos, AllBlocks.LIME_HORZ_ADV_SOLAR_GENERATOR.get().defaultBlockState()
                .setValue(net.minecraft.world.level.block.state.properties.BlockStateProperties.HORIZONTAL_FACING,
                        Direction.NORTH),
                true);
        builder.idle(30);

        builder.overlay().showText(60)
                .text("You can also craft them with dyes")
                .pointAt(util.vector().topOf(2, 1, 2))
                .placeNearTarget();
        builder.idle(70);
    }
}
