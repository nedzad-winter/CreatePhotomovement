package com.createphotomovement.ponder;

import com.createphotomovement.AllBlocks;

import net.createmod.ponder.api.scene.SceneBuilder;
import net.createmod.ponder.api.scene.SceneBuildingUtil;
import net.createmod.ponder.api.scene.Selection;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;

/**
 * Ponder scene storyboards for the Solar Generator.
 */
public class SolarGeneratorScenes {

    /**
     * Scene 1: Basics - Sky access, power generation, and wrench interaction
     * Layout: Generator in center with shafts on all 4 sides (5x5 base)
     */
    public static void basics(SceneBuilder builder, SceneBuildingUtil util) {
        builder.title("solar_generator_basics", "Solar Generator Basics");
        builder.configureBasePlate(0, 0, 5);
        builder.showBasePlate();

        // Generator in center with shafts on all 4 sides
        BlockPos generatorPos = util.grid().at(2, 1, 2);
        Selection generator = util.select().position(generatorPos);

        // Shafts on all 4 sides
        BlockPos shaftNorth = util.grid().at(2, 1, 1);
        BlockPos shaftSouth = util.grid().at(2, 1, 3);
        BlockPos shaftEast = util.grid().at(3, 1, 2);
        BlockPos shaftWest = util.grid().at(1, 1, 2);
        Selection allShafts = util.select().position(shaftNorth)
                .add(util.select().position(shaftSouth))
                .add(util.select().position(shaftEast))
                .add(util.select().position(shaftWest));

        builder.idle(10);

        // === KEYFRAME 1: Introduction ===
        builder.addKeyframe();

        // Show generator
        builder.world().showSection(generator, Direction.DOWN);
        builder.idle(20);

        builder.overlay().showText(60)
                .text("The Solar Generator creates rotational force from sunlight")
                .pointAt(util.vector().blockSurface(generatorPos, Direction.UP))
                .placeNearTarget();
        builder.idle(70);

        // Show all shafts
        builder.world().showSection(allShafts, Direction.DOWN);
        builder.idle(10);

        builder.overlay().showText(50)
                .text("Connect shafts to transfer the rotation")
                .pointAt(util.vector().centerOf(generatorPos))
                .placeNearTarget();
        builder.idle(60);

        // === KEYFRAME 2: Power Output ===
        builder.addKeyframe();

        builder.overlay().showText(60)
                .text("The generator must have direct access to the sky")
                .pointAt(util.vector().blockSurface(generatorPos, Direction.UP))
                .placeNearTarget();
        builder.idle(70);

        builder.overlay().showText(50)
                .text("It generates 16 RPM with 16 SU capacity")
                .pointAt(util.vector().centerOf(generatorPos))
                .placeNearTarget();
        builder.idle(60);

        // === KEYFRAME 3: Wrench Rotation ===
        builder.addKeyframe();

        builder.overlay().showText(60)
                .text("Use a Wrench to rotate the block orientation")
                .pointAt(util.vector().centerOf(generatorPos))
                .placeNearTarget();
        builder.idle(70);

        // === KEYFRAME 4: Reverse Direction ===
        builder.addKeyframe();

        builder.overlay().showText(60)
                .text("Sneak + Wrench to reverse the rotation direction")
                .pointAt(util.vector().centerOf(generatorPos))
                .placeNearTarget();
        builder.idle(70);

        builder.overlay().showText(50)
                .text("The rotation now spins in the opposite direction!")
                .pointAt(util.vector().centerOf(generatorPos))
                .placeNearTarget();
        builder.idle(60);
    }

    /**
     * Scene 2: Weather conditions (5x5 base)
     */
    public static void weather(SceneBuilder builder, SceneBuildingUtil util) {
        builder.title("solar_generator_weather", "Weather Effects");
        builder.configureBasePlate(0, 0, 5);
        builder.showBasePlate();

        BlockPos generatorPos = util.grid().at(2, 1, 2);
        Selection generator = util.select().position(generatorPos);

        builder.idle(10);
        builder.world().showSection(generator, Direction.DOWN);
        builder.idle(20);

        builder.overlay().showText(60)
                .text("During clear weather, the generator runs at full speed (16 RPM)")
                .pointAt(util.vector().centerOf(generatorPos))
                .placeNearTarget();
        builder.idle(70);

        builder.overlay().showText(60)
                .text("During rain, power is reduced to half (8 RPM)")
                .pointAt(util.vector().centerOf(generatorPos))
                .placeNearTarget();
        builder.idle(70);

        builder.overlay().showText(60)
                .text("During thunderstorms, the generator stops completely")
                .pointAt(util.vector().centerOf(generatorPos))
                .placeNearTarget();
        builder.idle(70);

        builder.overlay().showText(50)
                .text("At night, there is not enough light to generate power")
                .pointAt(util.vector().centerOf(generatorPos))
                .placeNearTarget();
        builder.idle(60);
    }

    /**
     * Scene 3: Obstructions
     * Shows everything at once, then explains each obstruction type
     */
    public static void obstructions(SceneBuilder builder, SceneBuildingUtil util) {
        builder.title("solar_generator_obstructions", "Obstructions");
        builder.configureBasePlate(0, 0, 7);
        builder.showBasePlate();

        // Show everything above the base plate at once
        Selection everything = util.select().fromTo(0, 1, 0, 6, 4, 4);
        builder.world().showSection(everything, Direction.DOWN);
        builder.idle(20);

        // Point to the center generator area
        BlockPos centerGen = util.grid().at(3, 1, 2);

        builder.overlay().showText(60)
                .text("The Solar Generator requires direct sky access")
                .pointAt(util.vector().centerOf(centerGen))
                .placeNearTarget();
        builder.idle(70);

        // Point to left generator with solid block
        BlockPos leftGen = util.grid().at(1, 1, 2);
        builder.overlay().showText(60)
                .text("Solid blocks completely obstruct sky access - No power!")
                .pointAt(util.vector().blockSurface(leftGen, Direction.UP))
                .placeNearTarget();
        builder.effects().indicateRedstone(leftGen);
        builder.idle(70);

        // Point to middle generator with glass
        BlockPos middleGen = util.grid().at(3, 1, 2);
        builder.overlay().showText(60)
                .text("Glass allows light through - power!")
                .pointAt(util.vector().blockSurface(middleGen, Direction.UP))
                .placeNearTarget();
        builder.effects().indicateRedstone(middleGen);
        builder.idle(70);

        // Point to right generator with snow
        BlockPos rightGen = util.grid().at(5, 1, 2);
        builder.overlay().showText(60)
                .text("Snow layers and carpets also block the generator")
                .pointAt(util.vector().blockSurface(rightGen, Direction.UP))
                .placeNearTarget();
        builder.effects().indicateRedstone(rightGen);
        builder.idle(70);
    }

    /**
     * Scene 4: Color changing with dyes (5x5 base)
     */
    public static void dyeing(SceneBuilder builder, SceneBuildingUtil util) {
        builder.title("solar_generator_dyeing", "Color Variants");
        builder.configureBasePlate(0, 0, 5);
        builder.showBasePlate();

        BlockPos generatorPos = util.grid().at(2, 1, 2);
        Selection generator = util.select().position(generatorPos);

        builder.idle(10);
        builder.world().showSection(generator, Direction.DOWN);
        builder.idle(20);

        builder.overlay().showText(60)
                .text("Solar Generators come in 17 color variants")
                .pointAt(util.vector().centerOf(generatorPos))
                .placeNearTarget();
        builder.idle(70);

        builder.overlay().showText(60)
                .text("Right-click with any dye to change the color")
                .pointAt(util.vector().centerOf(generatorPos))
                .placeNearTarget();
        builder.idle(40);

        // Show color changing animation - preserve horizontal axis
        builder.world().setBlock(generatorPos, AllBlocks.BLUE_SOLAR_GENERATOR.get().defaultBlockState()
                .setValue(net.minecraft.world.level.block.state.properties.BlockStateProperties.AXIS,
                        net.minecraft.core.Direction.Axis.Z),
                true);
        builder.idle(20);
        builder.world().setBlock(generatorPos, AllBlocks.RED_SOLAR_GENERATOR.get().defaultBlockState()
                .setValue(net.minecraft.world.level.block.state.properties.BlockStateProperties.AXIS,
                        net.minecraft.core.Direction.Axis.Z),
                true);
        builder.idle(20);
        builder.world().setBlock(generatorPos, AllBlocks.YELLOW_SOLAR_GENERATOR.get().defaultBlockState()
                .setValue(net.minecraft.world.level.block.state.properties.BlockStateProperties.AXIS,
                        net.minecraft.core.Direction.Axis.Z),
                true);
        builder.idle(20);
        builder.world().setBlock(generatorPos, AllBlocks.LIME_SOLAR_GENERATOR.get().defaultBlockState()
                .setValue(net.minecraft.world.level.block.state.properties.BlockStateProperties.AXIS,
                        net.minecraft.core.Direction.Axis.Z),
                true);
        builder.idle(30);

        builder.overlay().showText(50)
                .text("Or craft any generator with a dye in a crafting table")
                .pointAt(util.vector().centerOf(generatorPos))
                .placeNearTarget();
        builder.idle(60);

        builder.overlay().showText(50)
                .text("All color variants function identically")
                .pointAt(util.vector().centerOf(generatorPos))
                .placeNearTarget();
        builder.idle(60);
    }
}
