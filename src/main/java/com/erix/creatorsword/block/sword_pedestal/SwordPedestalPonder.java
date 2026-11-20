package com.erix.creatorsword.block.sword_pedestal;

import com.erix.creatorsword.item.creator_sword.CreatorSwordItems;
import com.simibubi.create.AllItems;
import com.simibubi.create.content.kinetics.gauge.StressGaugeBlockEntity;
import net.minecraft.core.BlockPos;
import com.simibubi.create.foundation.ponder.CreateSceneBuilder;
import net.createmod.catnip.math.Pointing;
import net.createmod.ponder.api.registration.PonderPlugin;
import net.createmod.ponder.api.registration.PonderSceneRegistrationHelper;
import net.createmod.ponder.api.scene.SceneBuilder;
import net.createmod.ponder.api.scene.SceneBuildingUtil;
import net.createmod.ponder.foundation.PonderIndex;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import org.jetbrains.annotations.NotNull;

@EventBusSubscriber(modid = "creatorsword", value = Dist.CLIENT)
public class SwordPedestalPonder implements PonderPlugin {
    @SubscribeEvent
    public static void setup(FMLClientSetupEvent event) {
        PonderIndex.addPlugin(new SwordPedestalPonder());
    }

    @Override public @NotNull String getModId() { return "creatorsword"; }

    @Override public void registerScenes(@NotNull PonderSceneRegistrationHelper<ResourceLocation> helper) {
        for (var pedestal : new ResourceLocation[]{SwordPedestalBlocks.SWORD_PEDESTAL.getId(),
                SwordPedestalBlocks.BRASS_SWORD_PEDESTAL.getId()}) {
            helper.addStoryBoard(pedestal, "sword_pedestal/power", SwordPedestalPonder::power);
        }
    }

    private static void power(SceneBuilder builder, SceneBuildingUtil util) {
        var scene = new CreateSceneBuilder(builder);
        scene.title("sword_pedestal", "Powering machines with a Sword Pedestal");
        scene.configureBasePlate(0, 0, 5);
        scene.scaleSceneView(.85f);
        scene.rotateCameraY(180);
        scene.showBasePlate();
        scene.idle(15);
        var pos = util.grid().at(2, 1, 2);
        var cog = util.grid().at(1, 1, 2);
        var speedGauge = cog.above();
        var stressGauge = speedGauge.above();
        for (var block : new BlockPos[]{pos, cog, speedGauge, stressGauge}) {
            scene.world().showSection(util.select().position(block), Direction.DOWN);
            scene.idle(14);
        }
        scene.addKeyframe();
        showItem(scene, util, pos, CreatorSwordItems.CREATOR_SWORD.get().getDefaultInstance());
        scene.world().setKineticSpeed(util.select().position(pos), 64);
        scene.world().setKineticSpeed(util.select().fromTo(cog, stressGauge), -64);
        scene.idle(16);
        scene.world().modifyBlockEntity(stressGauge, StressGaugeBlockEntity.class,
                be -> be.updateFromNetwork(SwordPedestalBlocks.SWORD_PEDESTAL.get()
                        .getCapacityFor(CreatorSwordItems.CREATOR_SWORD.get().getDefaultInstance()), 0, 4));
        scene.overlay().showText(80).text("Insert a wrench or a Creator's Sword by right-clicking. The pedestal supplies power at 64 RPM.")
                .pointAt(util.vector().of(1.5, 2.5, 3)).placeNearTarget();
        scene.idle(90);
        scene.overlay().showText(60).text("Different inserted items provide different stress capacities.")
                .pointAt(util.vector().of(1.5, 3.5, 3)).placeNearTarget();
        scene.idle(70);
        ItemStack[] items = {AllItems.WRENCH.asStack(), CreatorSwordItems.CNY_CREATOR_SWORD.get().getDefaultInstance()};
        String[] descriptions = {"Wrenches provide 4,096 SU.",
                "Crimson Afterglow and other rare Creator's Swords provide 32,768 SU."};
        for (int i = 0; i < items.length; i++) {
            setDisplayedItem(scene, util, pos, items[i]);
            int capacity = SwordPedestalBlocks.SWORD_PEDESTAL.get().getCapacityFor(items[i]);
            scene.world().modifyBlockEntity(stressGauge, StressGaugeBlockEntity.class,
                    be -> be.updateFromNetwork(capacity, 0, 4));
            scene.overlay().showText(60).text(descriptions[i])
                    .pointAt(util.vector().of(1.5, 3.5, 3)).placeNearTarget();
            scene.idle(70);
        }
        scene.addKeyframe();
        var control = util.vector().of(2.5, 1 + 3 / 16.0, 3.001);
        scene.overlay().showFilterSlotInput(control, Direction.SOUTH, 35);
        scene.overlay().showControls(control, Pointing.UP, 35).rightClick();
        scene.idle(60);
        scene.overlay().showText(100).text("Hold right-click on the lower side panel to select the rotation direction.")
                .pointAt(control).placeNearTarget();
        scene.idle(40);
        scene.world().modifyBlockEntityNBT(util.select().position(pos), SwordPedestalBlockEntity.class,
                tag -> tag.putInt("ScrollValue", 1));
        scene.world().setKineticSpeed(util.select().position(pos), -64);
        scene.world().setKineticSpeed(util.select().fromTo(cog, stressGauge), 64);
        scene.idle(80);
        scene.world().setBlock(pos, net.minecraft.world.level.block.Blocks.AIR.defaultBlockState(), false);
        scene.world().setBlock(pos, SwordPedestalBlocks.BRASS_SWORD_PEDESTAL.getDefaultState(), false);
        setDisplayedItem(scene, util, pos, items[1]);
        scene.world().modifyBlockEntityNBT(util.select().position(pos), SwordPedestalBlockEntity.class,
                tag -> tag.putInt("ScrollValue", -64));
        scene.world().setKineticSpeed(util.select().position(pos), -64);
        scene.overlay().showFilterSlotInput(control, Direction.SOUTH, 35);
        scene.overlay().showText(80).text("The Brass Sword Pedestal also lets you adjust the rotation speed.")
                .pointAt(util.vector().centerOf(pos)).placeNearTarget();
        scene.idle(120);
        scene.addKeyframe();
        var side = util.vector().of(3, 1.5, 2.5);
        scene.overlay().showControls(side, Pointing.DOWN, 35).rightClick().withItem(AllItems.WRENCH.asStack());
        scene.idle(45);
        scene.overlay().showText(90).text("Right-click a side with a wrench to change the pedestal's pose.")
                .pointAt(side).placeNearTarget();
        scene.idle(40);
        var rotated = SwordPedestalBlocks.BRASS_SWORD_PEDESTAL.get().getRotatedBlockState(
                SwordPedestalBlocks.BRASS_SWORD_PEDESTAL.getDefaultState(), Direction.EAST);
        scene.world().modifyBlock(pos, state -> rotated, true);
        scene.world().setKineticSpeed(util.select().fromTo(cog, stressGauge), 0);
        scene.world().modifyBlockEntity(stressGauge, StressGaugeBlockEntity.class,
                be -> be.updateFromNetwork(0, 0, 3));
        scene.idle(60);
        scene.addKeyframe();
        var back = rotated.getValue(SwordPedestalBlock.FACING).getOpposite();
        if (back == Direction.NORTH || back == Direction.WEST) scene.rotateCameraY(180);
        scene.idle(25);
        var bottom = util.vector().centerOf(pos).add(net.minecraft.world.phys.Vec3.atLowerCornerOf(back.getNormal()).scale(.5));
        scene.overlay().showControls(bottom, Pointing.DOWN, 35).rightClick().withItem(AllItems.WRENCH.asStack());
        scene.idle(45);
        scene.overlay().showText(90).text("Right-click the bottom face with a wrench to enable or hide the output shaft.")
                .pointAt(bottom).placeNearTarget();
        scene.idle(40);
        scene.world().modifyBlock(pos, state -> state.setValue(SwordPedestalBlock.BOTTOM_SHAFT, true), true);
        scene.idle(60);
        scene.markAsFinished();
    }

    private static void showItem(CreateSceneBuilder scene, SceneBuildingUtil util, BlockPos pos, ItemStack item) {
        scene.overlay().showControls(util.vector().topOf(pos), Pointing.DOWN, 35).rightClick().withItem(item);
        scene.idle(45);
        setDisplayedItem(scene, util, pos, item);
    }

    private static void setDisplayedItem(CreateSceneBuilder scene, SceneBuildingUtil util, BlockPos pos, ItemStack item) {
        scene.world().modifyBlockEntityNBT(util.select().position(pos), SwordPedestalBlockEntity.class,
                tag -> tag.put("HeldItem", item.save(scene.getScene().getWorld().registryAccess())));
    }
}
