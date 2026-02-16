package com.createphotomovement.ponder;

import com.createphotomovement.AllBlocks;
import com.simibubi.create.foundation.ponder.CreateSceneBuilder;

import net.createmod.ponder.api.PonderPalette;
import net.createmod.ponder.api.element.ElementLink;
import net.createmod.ponder.api.element.WorldSectionElement;
import net.createmod.ponder.api.scene.SceneBuilder;
import net.createmod.ponder.api.scene.SceneBuildingUtil;
import net.createmod.ponder.api.scene.Selection;
import net.createmod.catnip.data.Iterate;
import net.createmod.catnip.math.Pointing;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

/**
 * Ponder scene storyboards for Solar Sails and Solar Windmill Bearing.
 */
public class SolarSailScenes {

        /**
         * Scene 1: Basics (Ported from Create BearingScenes#sails)
         */
        public static void basics(SceneBuilder builder, SceneBuildingUtil util) {
                builder.title("solar_sail.basics", "Assembling Solar Windmills");
                builder.configureBasePlate(0, 0, 5);
                builder.scaleSceneView(0.9f);
                builder.showBasePlate();

                BlockPos bearingPos = util.grid().at(2, 1, 2);

                // Show Bearing
                builder.world().showSection(util.select().position(bearingPos), Direction.DOWN);
                builder.idle(5);

                // Show the Sail structure (assumed to be above bearing in schematic)
                ElementLink<WorldSectionElement> plank = builder.world()
                                .showIndependentSection(util.select().position(bearingPos.above()), Direction.DOWN);
                builder.idle(10);

                // Animate "building" the sails
                for (int i = 0; i < 3; i++) {
                        for (Direction d : Iterate.horizontalDirections) {
                                BlockPos location = bearingPos.above(i + 1).relative(d);
                                builder.world().showSectionAndMerge(util.select().position(location), d.getOpposite(),
                                                plank);
                                builder.idle(2);
                        }
                }

                builder.overlay().showText(70)
                                .text("Solar Sails are handy blocks to create Solar Windmills with")
                                .pointAt(util.vector().blockSurface(util.grid().at(1, 3, 2), Direction.WEST))
                                .placeNearTarget()
                                .attachKeyFrame();
                builder.idle(80);

                builder.overlay().showOutlineWithText(util.select().position(bearingPos.above()), 80)
                                .colored(PonderPalette.GREEN)
                                .text("They will attach to blocks and each other without the need of Super Glue")
                                .attachKeyFrame()
                                .placeNearTarget();
                builder.idle(40);

                builder.world().configureCenterOfRotation(plank, util.vector().centerOf(bearingPos));

                // Rotate
                // builder.world().rotateBearing(bearingPos, 180, 75);
                builder.world().rotateSection(plank, 0, 180, 0, 75);
                builder.idle(76);
                // builder.world().rotateBearing(bearingPos, 180, 0);
                builder.world().rotateSection(plank, 0, 180, 0, 0);

                builder.rotateCameraY(-30);
                builder.idle(10);

                // Dyeing Interaction
                // Use Blue Dye
                BlockPos sailPos = util.grid().at(2, 3, 1); // A sail position

                builder.overlay().showControls(util.vector().blockSurface(sailPos, Direction.NORTH), Pointing.RIGHT, 30)
                                .withItem(new ItemStack(Items.BLUE_DYE));
                builder.idle(7);

                // Change block to Blue Solar Sail
                builder.world().setBlock(sailPos, AllBlocks.BLUE_SOLAR_SAIL.defaultBlockState()
                                .setValue(BlockStateProperties.FACING, Direction.WEST), false);

                builder.idle(10);
                builder.overlay().showText(40)
                                .colored(PonderPalette.BLUE)
                                .text("Right-Click with Dye to paint them")
                                .attachKeyFrame()
                                .pointAt(util.vector().blockSurface(sailPos, Direction.WEST))
                                .placeNearTarget();
                builder.idle(20);

                // Dye the rest of the column
                builder.overlay().showControls(util.vector().blockSurface(sailPos, Direction.NORTH), Pointing.RIGHT, 30)
                                .withItem(new ItemStack(Items.BLUE_DYE));
                builder.idle(7);

                // Replace a column
                builder.world().replaceBlocks(util.select().fromTo(2, 2, 1, 2, 4, 1), AllBlocks.BLUE_SOLAR_SAIL
                                .defaultBlockState()
                                .setValue(BlockStateProperties.FACING, Direction.WEST), false);

                builder.idle(20);

                // Spin again
                // builder.world().rotateBearing(bearingPos, 360, 150);
                builder.world().rotateSection(plank, 0, 360, 0, 150);

                // Additional info about sunlight
                builder.addKeyframe();

                builder.idle(20);
                builder.overlay().showText(60)
                                .text("The solar sails produce double the amount of power as a regular solar generator")
                                .pointAt(util.vector().topOf(bearingPos.above(4)))
                                .placeNearTarget();
                builder.idle(70);

                builder.idle(20);
                builder.overlay().showText(60)
                                .text("8 solar sails produce 1024 SU while the default sails produce 512 SU")
                                .pointAt(util.vector().topOf(bearingPos.above(4)))
                                .placeNearTarget();
                builder.idle(70);
        }

        /**
         * Scene 2: Advanced (Mixed Sails)
         */
        public static void advanced(SceneBuilder builder, SceneBuildingUtil util) {
                builder.title("solar_sail.advanced", "Advanced Solar Windmills");
                builder.configureBasePlate(0, 0, 5);
                builder.scaleSceneView(0.9f);
                builder.showBasePlate();

                BlockPos bearingPos = util.grid().at(2, 1, 2);

                // Show base and bearing
                builder.world().showSection(util.select().fromTo(0, 0, 0, 4, 1, 4), Direction.DOWN);
                builder.idle(20);

                // Show rotating top (Sails)
                ElementLink<WorldSectionElement> rotatingTop = builder.world()
                                .showIndependentSection(util.select().fromTo(0, 2, 0, 4, 6, 4), Direction.DOWN);
                builder.idle(10);

                builder.world().configureCenterOfRotation(rotatingTop, util.vector().centerOf(bearingPos));

                // 1. Supports all sail blocks
                builder.overlay().showText(80)
                                .text("The Solar Windmill Bearing supports all sail blocks")
                                .pointAt(util.vector().blockSurface(bearingPos, Direction.WEST))
                                .placeNearTarget();
                builder.idle(80);

                // 2. Mix all kinds + Start Rotation
                builder.overlay().showText(40)
                                .text("You can mix all kinds of sails")
                                .pointAt(util.vector().blockSurface(bearingPos.above(2), Direction.WEST))
                                .placeNearTarget();
                builder.idle(40);

                // Start rotation
                // builder.world().rotateBearing(bearingPos, 360, 200);
                builder.world().rotateSection(rotatingTop, 0, 360, 0, 200);

                // 3. Combined SU
                builder.addKeyframe();
                builder.idle(40); // Initial delay for keyframe?

                builder.overlay().showText(40)
                                .text("The SU production of the Solar Sails and the Default Sails will be combined")
                                .pointAt(util.vector().topOf(bearingPos.above(5)))
                                .placeNearTarget();
                builder.idle(80);

                // 4. Resulting SU
                builder.overlay().showText(40)
                                .text("Resulting, in this scenario, in 1536 SU")
                                .pointAt(util.vector().topOf(bearingPos.above(5)))
                                .placeNearTarget();
                builder.idle(80);

                // 5. Day production (Double)
                builder.addKeyframe();
                builder.overlay().showText(80)
                                .text("During the day, the production of Solar Sails is double that of Default Sails")
                                .pointAt(util.vector().topOf(bearingPos.above(5)))
                                .placeNearTarget();
                builder.idle(80);

                // 6. Rain (50% more?) - Using exact user text logic but maybe clarification
                // User: "when raining it is 50% more than the solar sails"
                builder.addKeyframe();
                builder.overlay().showText(80)
                                .text("When raining it is 50%% more than the Solar Sails")
                                .pointAt(util.vector().topOf(bearingPos.above(5)))
                                .placeNearTarget();
                builder.idle(80);

                // 7. Night/Thunder
                builder.addKeyframe();
                builder.overlay().showText(80)
                                .text("When night and thunder, the Solar Sails produce as much as the Default Sails")
                                .pointAt(util.vector().topOf(bearingPos.above(5)))
                                .placeNearTarget();
                builder.idle(80);
        }
}
