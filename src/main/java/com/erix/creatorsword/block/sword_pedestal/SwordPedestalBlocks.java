package com.erix.creatorsword.block.sword_pedestal;

import com.tterrag.registrate.providers.loot.RegistrateBlockLootTables;
import com.tterrag.registrate.util.entry.BlockEntityEntry;

import com.erix.creatorsword.CreatorSword;
import com.simibubi.create.foundation.data.CreateRegistrate;
import com.simibubi.create.foundation.data.SharedProperties;
import com.tterrag.registrate.util.entry.BlockEntry;
import com.tterrag.registrate.providers.ProviderType;
import com.tterrag.registrate.util.nullness.NonNullBiConsumer;
import com.tterrag.registrate.util.nullness.NonNullFunction;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.item.CreativeModeTabs;
import net.neoforged.neoforge.client.model.generators.ConfiguredModel;
import com.simibubi.create.foundation.block.ProperWaterloggedBlock;

import static com.simibubi.create.foundation.data.TagGen.axeOrPickaxe;

public class SwordPedestalBlocks {
    private static final CreateRegistrate REGISTRATE = CreatorSword.registrate();

    private static final List<BlockEntry<SwordPedestalBlock>> VARIANTS = new ArrayList<>();
    public static final BlockEntry<SwordPedestalBlock> SWORD_PEDESTAL =
            registerPedestal("sword_pedestal", MapColor.COLOR_GRAY, SwordPedestalBlock::new);
    public static final BlockEntry<SwordPedestalBlock> BRASS_SWORD_PEDESTAL =
            registerPedestal("brass_sword_pedestal", MapColor.TERRACOTTA_YELLOW, BrassSwordPedestalBlock::new);

    private static BlockEntry<SwordPedestalBlock> registerPedestal(String name, MapColor color,
            NonNullFunction<BlockBehaviour.Properties, SwordPedestalBlock> factory) {
        var entry = REGISTRATE.block(name, factory)
            .initialProperties(SharedProperties::stone)
            .properties(p -> p.mapColor(color))
            .properties(BlockBehaviour.Properties::noOcclusion)
            .transform(axeOrPickaxe())
            .setData(ProviderType.LANG, NonNullBiConsumer.noop())
            .blockstate((c, p) -> p.getVariantBuilder(c.getEntry()).forAllStatesExcept(state ->
                    ConfiguredModel.builder().modelFile(p.models().getExistingFile(p.modLoc(
                            "block/" + name + "/" + (state.getValue(SwordPedestalBlock.BOTTOM_SHAFT)
                                    ? "block_bottom" : "block")
                                    + (SwordPedestalBlock.modelRolled(state) ? "_rotated" : ""))))
                            .rotationX(SwordPedestalBlock.modelXRotation(state))
                            .rotationY(SwordPedestalBlock.modelYRotation(state)).build(), ProperWaterloggedBlock.WATERLOGGED))
            .loot(RegistrateBlockLootTables::dropSelf)
            .item(SwordPedestalBlockItem::new)
            .removeTab(CreativeModeTabs.SEARCH)
            .model((c, p) -> p.withExistingParent(c.getName(),
                    p.modLoc("block/" + name + "/item")))
            .build()
            .register();
        VARIANTS.add(entry);
        return entry;
    }

    public static final BlockEntityEntry<SwordPedestalBlockEntity> SWORD_PEDESTAL_ENTITY = registerBlockEntity();

    private static BlockEntityEntry<SwordPedestalBlockEntity> registerBlockEntity() {
        var builder = REGISTRATE.blockEntity("sword_pedestal", SwordPedestalBlockEntity::new)
                .renderer(() -> SwordPedestalRenderer::new);
        for (var variant : VARIANTS) builder.validBlocks(variant);
        return builder.register();
    }

    public static void registerCapabilities(net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent event) {
        event.registerBlockEntity(net.neoforged.neoforge.capabilities.Capabilities.ItemHandler.BLOCK,
                SWORD_PEDESTAL_ENTITY.get(), (pedestal, side) -> pedestal.getItemHandler());
    }

    public static void register() {
        // Load this class to register its entries with the shared CreateRegistrate.
    }
}
