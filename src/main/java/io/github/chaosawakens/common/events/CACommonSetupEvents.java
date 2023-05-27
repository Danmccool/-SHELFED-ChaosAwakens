package io.github.chaosawakens.common.events;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;

import io.github.chaosawakens.ChaosAwakens;
import io.github.chaosawakens.api.CAReflectionHelper;
import io.github.chaosawakens.api.wrapper.CarverWrapper;
import io.github.chaosawakens.api.wrapper.FeatureWrapper;
import io.github.chaosawakens.common.entity.boss.miniboss.HerculesBeetleEntity;
import io.github.chaosawakens.common.entity.boss.robo.RoboJefferyEntity;
import io.github.chaosawakens.common.entity.creature.land.AntEntity;
import io.github.chaosawakens.common.entity.creature.land.BeaverEntity;
import io.github.chaosawakens.common.entity.creature.land.GazelleEntity;
import io.github.chaosawakens.common.entity.creature.land.LettuceChickenEntity;
import io.github.chaosawakens.common.entity.creature.land.RubyBugEntity;
import io.github.chaosawakens.common.entity.creature.land.StinkBugEntity;
import io.github.chaosawakens.common.entity.creature.land.TreeFrogEntity;
import io.github.chaosawakens.common.entity.creature.land.applecow.AppleCowEntity;
import io.github.chaosawakens.common.entity.creature.land.applecow.CrystalAppleCowEntity;
import io.github.chaosawakens.common.entity.creature.land.applecow.EnchantedGoldenAppleCowEntity;
import io.github.chaosawakens.common.entity.creature.land.applecow.GoldenAppleCowEntity;
import io.github.chaosawakens.common.entity.creature.land.applecow.UltimateAppleCowEntity;
import io.github.chaosawakens.common.entity.creature.land.carrotpig.CarrotPigEntity;
import io.github.chaosawakens.common.entity.creature.land.carrotpig.CrystalCarrotPigEntity;
import io.github.chaosawakens.common.entity.creature.land.carrotpig.EnchantedGoldenCarrotPigEntity;
import io.github.chaosawakens.common.entity.creature.land.carrotpig.GoldenCarrotPigEntity;
import io.github.chaosawakens.common.entity.creature.water.WhaleEntity;
import io.github.chaosawakens.common.entity.creature.water.fish.GreenFishEntity;
import io.github.chaosawakens.common.entity.creature.water.fish.RockFishEntity;
import io.github.chaosawakens.common.entity.creature.water.fish.SparkFishEntity;
import io.github.chaosawakens.common.entity.creature.water.fish.WoodFishEntity;
import io.github.chaosawakens.common.entity.hostile.AggressiveAntEntity;
import io.github.chaosawakens.common.entity.hostile.EntEntity;
import io.github.chaosawakens.common.entity.hostile.robo.RoboPounderEntity;
import io.github.chaosawakens.common.entity.hostile.robo.RoboSniperEntity;
import io.github.chaosawakens.common.entity.hostile.robo.RoboWarriorEntity;
import io.github.chaosawakens.common.entity.neutral.land.dino.DimetrodonEntity;
import io.github.chaosawakens.common.entity.neutral.land.gator.CrystalGatorEntity;
import io.github.chaosawakens.common.entity.neutral.land.gator.EmeraldGatorEntity;
import io.github.chaosawakens.common.items.base.CABoatItem;
import io.github.chaosawakens.common.registry.CABiomes;
import io.github.chaosawakens.common.registry.CABlocks;
import io.github.chaosawakens.common.registry.CACommands;
import io.github.chaosawakens.common.registry.CAConfiguredStructures;
import io.github.chaosawakens.common.registry.CAEffects;
import io.github.chaosawakens.common.registry.CAEntityTypes;
import io.github.chaosawakens.common.registry.CAItems;
import io.github.chaosawakens.common.registry.CAStructures;
import io.github.chaosawakens.common.registry.CASurfaceBuilders;
import io.github.chaosawakens.common.registry.CATags;
import io.github.chaosawakens.common.registry.CAVanillaCompat;
import io.github.chaosawakens.common.registry.CAVillagers;
import io.github.chaosawakens.common.registry.CAWoodTypes;
import io.github.chaosawakens.common.util.TradeUtil;
import io.github.chaosawakens.common.util.TradeUtil.CABasicTrade;
import io.github.chaosawakens.common.util.TradeUtil.CAIngredientTrade;
import io.github.chaosawakens.common.worldgen.BiomeHandlers;
import io.github.chaosawakens.data.provider.CAAdvancementProvider;
import io.github.chaosawakens.data.provider.CABlockModelProvider;
import io.github.chaosawakens.data.provider.CABlockStateProvider;
import io.github.chaosawakens.data.provider.CACustomConversionProvider;
import io.github.chaosawakens.data.provider.CADimensionTypeProvider;
import io.github.chaosawakens.data.provider.CAGlobalLootModifierProvider;
import io.github.chaosawakens.data.provider.CAItemModelProvider;
import io.github.chaosawakens.data.provider.CALootTableProvider;
import io.github.chaosawakens.data.provider.CAParticleTypeProvider;
import io.github.chaosawakens.data.provider.CARecipeProvider;
import io.github.chaosawakens.data.provider.CATagProvider;
import io.github.chaosawakens.manager.CAConfigManager;
import io.github.chaosawakens.manager.CANetworkManager;
import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.block.Block;
import net.minecraft.command.CommandSource;
import net.minecraft.data.DataGenerator;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.attributes.RangedAttribute;
import net.minecraft.entity.merchant.villager.VillagerProfession;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.WorldGenRegistries;
import net.minecraft.world.World;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.FlatChunkGenerator;
import net.minecraft.world.gen.feature.structure.Structure;
import net.minecraft.world.gen.settings.DimensionStructuresSettings;
import net.minecraft.world.gen.settings.StructureSeparationSettings;
import net.minecraft.world.raid.Raid;
import net.minecraft.world.server.ServerChunkProvider;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.event.furnace.FurnaceFuelBurnTimeEvent;
import net.minecraftforge.event.village.VillagerTradesEvent;
import net.minecraftforge.event.village.WandererTradesEvent;
import net.minecraftforge.event.world.BiomeLoadingEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper.UnableToFindMethodException;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLLoadCompleteEvent;
import net.minecraftforge.fml.event.lifecycle.GatherDataEvent;
import net.minecraftforge.registries.ForgeRegistries;

public class CACommonSetupEvents {	
	public static ObjectArrayList<FeatureWrapper> CONFIG_FEATURES = new ObjectArrayList<>();
	public static ObjectArrayList<CarverWrapper> CONFIG_CARVERS = new ObjectArrayList<>();
	private static Method codecMethod;

	//@SubscribeEvent
	public static void onFMLCommonSetupEvent(FMLCommonSetupEvent event) {
		CAEntityTypes.registerSpawnPlacementTypes();
		CANetworkManager.registerPackets();
		CAEffects.registerBrewingRecipes(); // Unused currently. Here for FUTURE use.
		Raid.WaveMember.create("illusioner", EntityType.ILLUSIONER, new int[]{0, 0, 0, 0, 1, 1, 0, 2});

		event.enqueueWork(() -> {
			CAVanillaCompat.registerVanillaCompat();
			CAStructures.setupStructures();
			CAConfiguredStructures.registerConfiguredStructures();
			CASurfaceBuilders.ConfiguredSurfaceBuilders.registerConfiguredSurfaceBuilders();
			CAVillagers.registerVillagerTypes();
			CAWoodTypes.registerWoodtypes();
			
			CAReflectionHelper.classLoad("io.github.chaosawakens.common.registry.CAConfiguredFeatures");
			CONFIG_FEATURES.forEach((wrapper) -> Registry.register(WorldGenRegistries.CONFIGURED_FEATURE, wrapper.getIdentifier(), wrapper.getFeatureType()));
			CONFIG_CARVERS.forEach((wrapper) -> Registry.register(WorldGenRegistries.CONFIGURED_CARVER, wrapper.getIdentifier(), wrapper.getCarver()));
		});
		
		BiomeDictionary.addTypes(RegistryKey.create(Registry.BIOME_REGISTRY, CABiomes.DENSE_MOUNTAINS.getId()), CABiomes.Type.MINING_PARADISE, CABiomes.Type.DENSE_MOUNTAINS);
		BiomeDictionary.addTypes(RegistryKey.create(Registry.BIOME_REGISTRY, CABiomes.STALAGMITE_VALLEY.getId()), CABiomes.Type.MINING_PARADISE, CABiomes.Type.STALAGMITE_VALLEY);
		BiomeDictionary.addTypes(RegistryKey.create(Registry.BIOME_REGISTRY, CABiomes.VILLAGE_PLAINS.getId()), CABiomes.Type.VILLAGE_MANIA, CABiomes.Type.VILLAGE_PLAINS);
		BiomeDictionary.addTypes(RegistryKey.create(Registry.BIOME_REGISTRY, CABiomes.VILLAGE_SAVANNA.getId()), CABiomes.Type.VILLAGE_MANIA, CABiomes.Type.VILLAGE_SAVANNA);
		BiomeDictionary.addTypes(RegistryKey.create(Registry.BIOME_REGISTRY, CABiomes.VILLAGE_TAIGA.getId()), CABiomes.Type.VILLAGE_MANIA, CABiomes.Type.VILLAGE_TAIGA);
		BiomeDictionary.addTypes(RegistryKey.create(Registry.BIOME_REGISTRY, CABiomes.VILLAGE_SNOWY.getId()), CABiomes.Type.VILLAGE_MANIA, CABiomes.Type.VILLAGE_SNOWY);
		BiomeDictionary.addTypes(RegistryKey.create(Registry.BIOME_REGISTRY, CABiomes.VILLAGE_DESERT.getId()), CABiomes.Type.VILLAGE_MANIA, CABiomes.Type.VILLAGE_DESERT);
		BiomeDictionary.addTypes(RegistryKey.create(Registry.BIOME_REGISTRY, CABiomes.DANGER_ISLANDS.getId()), CABiomes.Type.DANGER_ISLES);
		BiomeDictionary.addTypes(RegistryKey.create(Registry.BIOME_REGISTRY, CABiomes.CRYSTAL_PLAINS.getId()), CABiomes.Type.CRYSTAL_WORLD);
		BiomeDictionary.addTypes(RegistryKey.create(Registry.BIOME_REGISTRY, CABiomes.CRYSTAL_HILLS.getId()), CABiomes.Type.CRYSTAL_WORLD);
	}
	
	//@SubscribeEvent
    public static void onFMLLoadCompleteEvent(FMLLoadCompleteEvent event) {
    	modifyAttributeValues();
    }
    
    // Took the main functionality of AttributeFix and clamped it into a singular method --Meme Man
    private static void modifyAttributeValues() {
    	 Map<RangedAttribute, ?> attributes = new HashMap<>();
    	 for (final Entry<RangedAttribute, ?> attributeEntry : attributes.entrySet()) {
    		 final RangedAttribute attribute = attributeEntry.getKey();
    		 attribute.maxValue = Math.max(attribute.maxValue, Double.MAX_VALUE);
    	 }
    }

	@SuppressWarnings("unchecked")
	//@SubscribeEvent(priority = EventPriority.HIGH)
	public static void onDimensionalSpacingWorldLoadEvent(final WorldEvent.Load event) {
		if (!(event.getWorld() instanceof ServerWorld)) return;

		ServerWorld serverWorld = (ServerWorld) event.getWorld();
		ServerChunkProvider chunkProvider = serverWorld.getChunkSource();

		try {
			if (codecMethod == null) codecMethod = ObfuscationReflectionHelper.findMethod(ChunkGenerator.class, "codec");
			// TODO Fix this
			ResourceLocation chunkGeneratorKey = Registry.CHUNK_GENERATOR.getKey((Codec<? extends ChunkGenerator>) codecMethod.invoke(chunkProvider.generator));
			if (chunkGeneratorKey != null && chunkGeneratorKey.getNamespace().equals("terraforged")) return;
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			ChaosAwakens.LOGGER.warn("[WORLDGEN]: " + e);
			e.printStackTrace();
		} catch (UnableToFindMethodException e) {
			if (CAConfigManager.MAIN_COMMON.terraforgedCheckMsg.get()) ChaosAwakens.LOGGER.info("[WORLDGEN]: Unable to check if " + serverWorld.dimension().location() + " is using Terraforged's ChunkGenerator due to Terraforged not being present or not accessible," + " if you aren't using Terraforged please ignore this message");
		}

		if (serverWorld.getChunkSource().getGenerator() instanceof FlatChunkGenerator && serverWorld.dimension().equals(World.OVERWORLD)) return;

		Map<Structure<?>, StructureSeparationSettings> testTempMap = new HashMap<>(chunkProvider.generator.getSettings().structureConfig());
		Object2ObjectMap<Structure<?>, StructureSeparationSettings> tempMap = new Object2ObjectArrayMap<>(testTempMap);

		tempMap.putIfAbsent(CAStructures.ACACIA_ENT_TREE.get(), DimensionStructuresSettings.DEFAULTS.get(CAStructures.ACACIA_ENT_TREE.get()));
		tempMap.putIfAbsent(CAStructures.BIRCH_ENT_TREE.get(), DimensionStructuresSettings.DEFAULTS.get(CAStructures.BIRCH_ENT_TREE.get()));
		tempMap.putIfAbsent(CAStructures.CRIMSON_ENT_TREE.get(), DimensionStructuresSettings.DEFAULTS.get(CAStructures.CRIMSON_ENT_TREE.get()));
		tempMap.putIfAbsent(CAStructures.DARK_OAK_ENT_TREE.get(), DimensionStructuresSettings.DEFAULTS.get(CAStructures.DARK_OAK_ENT_TREE.get()));
		tempMap.putIfAbsent(CAStructures.JUNGLE_ENT_TREE.get(),	DimensionStructuresSettings.DEFAULTS.get(CAStructures.JUNGLE_ENT_TREE.get()));
		tempMap.putIfAbsent(CAStructures.OAK_ENT_TREE.get(), DimensionStructuresSettings.DEFAULTS.get(CAStructures.OAK_ENT_TREE.get()));
		tempMap.putIfAbsent(CAStructures.SPRUCE_ENT_TREE.get(), DimensionStructuresSettings.DEFAULTS.get(CAStructures.SPRUCE_ENT_TREE.get()));
		tempMap.putIfAbsent(CAStructures.WARPED_ENT_TREE.get(), DimensionStructuresSettings.DEFAULTS.get(CAStructures.WARPED_ENT_TREE.get()));
//		tempMap.putIfAbsent(CAStructures.WASP_DUNGEON.get(), DimensionStructuresSettings.DEFAULTS.get(CAStructures.WASP_DUNGEON.get()));
//		tempMap.putIfAbsent(CAStructures.MINING_WASP_DUNGEON.get(),	DimensionStructuresSettings.DEFAULTS.get(CAStructures.MINING_WASP_DUNGEON.get()));

		chunkProvider.generator.getSettings().structureConfig = testTempMap;
	}
	
	//@SubscribeEvent(priority = EventPriority.HIGH)
	public static void onBiomeLoadingEvent(final BiomeLoadingEvent event) {
		BiomeHandlers.GenerationHandler.addFeatures(event);
		BiomeHandlers.MobSpawnHandler.addMobSpawns(event);
	}
	
	//@SubscribeEvent
	public static void onRegisterFurnaceFuelEvent(FurnaceFuelBurnTimeEvent event) {
		ItemStack fuel = event.getItemStack();
		if (fuel.getItem() == CAItems.LAVA_PINK_TOURMALINE_BUCKET.get()) {
			event.setBurnTime(20000);
		} else if (fuel.getItem() == CABlocks.CRYSTAL_ENERGY.get().asItem()) {
			event.setBurnTime(3600);
		} else if (fuel.getItem() == CABlocks.CRYSTAL_CRAFTING_TABLE.get().asItem() || fuel.getItem() == CABlocks.APPLE_FENCE.get().asItem()
				|| fuel.getItem() == CABlocks.CHERRY_FENCE.get().asItem() || fuel.getItem() == CABlocks.DUPLICATION_FENCE.get().asItem()
				|| fuel.getItem() == CABlocks.MOLDY_FENCE.get().asItem() || fuel.getItem() == CABlocks.PEACH_FENCE.get().asItem()
				|| fuel.getItem() == CABlocks.SKYWOOD_FENCE.get().asItem() || fuel.getItem() == CABlocks.GINKGO_FENCE.get().asItem()
				|| fuel.getItem() == CABlocks.MESOZOIC_FENCE.get().asItem() || fuel.getItem() == CABlocks.DENSEWOOD_FENCE.get().asItem()
				|| fuel.getItem() == CABlocks.CRYSTAL_FENCE.get().asItem() || fuel.getItem() == CABlocks.APPLE_FENCE_GATE.get().asItem()
				|| fuel.getItem() == CABlocks.CHERRY_FENCE_GATE.get().asItem() || fuel.getItem() == CABlocks.DUPLICATION_FENCE_GATE.get().asItem()
				|| fuel.getItem() == CABlocks.PEACH_FENCE_GATE.get().asItem() || fuel.getItem() == CABlocks.SKYWOOD_FENCE_GATE.get().asItem()
				|| fuel.getItem() == CABlocks.GINKGO_FENCE_GATE.get().asItem() || fuel.getItem() == CABlocks.MESOZOIC_FENCE_GATE.get().asItem()
				|| fuel.getItem() == CABlocks.DENSEWOOD_FENCE_GATE.get().asItem()
				|| fuel.getItem() == CABlocks.CRYSTAL_FENCE_GATE.get().asItem()
				|| fuel.getItem() == CABlocks.APPLE_TRAPDOOR.get().asItem() || fuel.getItem() == CABlocks.CHERRY_TRAPDOOR.get().asItem()
				|| fuel.getItem() == CABlocks.DUPLICATION_TRAPDOOR.get().asItem() || fuel.getItem() == CABlocks.GINKGO_TRAPDOOR.get().asItem()
				|| fuel.getItem() == CABlocks.MESOZOIC_TRAPDOOR.get().asItem() || fuel.getItem() == CABlocks.DENSEWOOD_TRAPDOOR.get().asItem()
				|| fuel.getItem() == CABlocks.PEACH_TRAPDOOR.get().asItem() || fuel.getItem() == CABlocks.SKYWOOD_TRAPDOOR.get().asItem()) {
			event.setBurnTime(300);
		} else if (fuel.getItem() instanceof CABoatItem) {
			event.setBurnTime(1200);
		} else if (fuel.getItem() == CAItems.CRYSTAL_WOOD_SHOVEL.get() || fuel.getItem() == CAItems.CRYSTAL_WOOD_SWORD.get()
				|| fuel.getItem() == CAItems.CRYSTAL_WOOD_HOE.get() || fuel.getItem() == CAItems.CRYSTAL_WOOD_AXE.get()
				|| fuel.getItem() == CAItems.CRYSTAL_WOOD_PICKAXE.get() || fuel.getItem() == CAItems.APPLE_SIGN.get() 
				|| fuel.getItem() == CAItems.CHERRY_SIGN.get() || fuel.getItem() == CAItems.DUPLICATION_SIGN.get() 
				|| fuel.getItem() == CAItems.GINKGO_SIGN.get() || fuel.getItem() == CAItems.PEACH_SIGN.get() || fuel.getItem() == CAItems.SKYWOOD_SIGN.get()
				|| fuel.getItem() == CABlocks.APPLE_DOOR.get().asItem() || fuel.getItem() == CABlocks.CHERRY_DOOR.get().asItem()
				|| fuel.getItem() == CABlocks.DUPLICATION_DOOR.get().asItem() || fuel.getItem() == CABlocks.GINKGO_DOOR.get().asItem()
				|| fuel.getItem() == CABlocks.MESOZOIC_DOOR.get().asItem() || fuel.getItem() == CABlocks.DENSEWOOD_DOOR.get().asItem()
				|| fuel.getItem() == CABlocks.PEACH_DOOR.get().asItem() || fuel.getItem() == CABlocks.SKYWOOD_DOOR.get().asItem()) {
			event.setBurnTime(200);
		} else if (fuel.getItem() == CAItems.CRYSTAL_SHARD.get() || fuel.getItem() == CATags.Items.CRYSTAL_SAPLING) {
			event.setBurnTime(100);
		} else if(fuel.getItem() == CABlocks.TAR.get().asItem() ) {
			event.setBurnTime(10000);
		}
	}
	
	//@SubscribeEvent
	public static void onRegisterCommandsEvent(RegisterCommandsEvent event) {
		CommandDispatcher<CommandSource> commandDispatcher = event.getDispatcher();
		CACommands.register(commandDispatcher);
	}
	
	//@SubscribeEvent
	public static void onEntityAttributeCreationEvent(final EntityAttributeCreationEvent event) {
		event.put(CAEntityTypes.OAK_ENT.get(), EntEntity.setCustomAttributes().build());
		event.put(CAEntityTypes.APPLE_ENT.get(), EntEntity.setCustomAttributes().build());
		event.put(CAEntityTypes.ACACIA_ENT.get(), EntEntity.setCustomAttributes().build());
		event.put(CAEntityTypes.BIRCH_ENT.get(), EntEntity.setCustomAttributes().build());
		event.put(CAEntityTypes.CHERRY_ENT.get(), EntEntity.setCustomAttributes().build());
		event.put(CAEntityTypes.DARK_OAK_ENT.get(), EntEntity.setCustomAttributes().build());
		event.put(CAEntityTypes.JUNGLE_ENT.get(), EntEntity.setCustomAttributes().build());
		event.put(CAEntityTypes.PEACH_ENT.get(), EntEntity.setCustomAttributes().build());
		event.put(CAEntityTypes.SKYWOOD_ENT.get(), EntEntity.setCustomAttributes().build());
		event.put(CAEntityTypes.GINKGO_ENT.get(), EntEntity.setCustomAttributes().build());
		event.put(CAEntityTypes.SPRUCE_ENT.get(), EntEntity.setCustomAttributes().build());
		event.put(CAEntityTypes.CRIMSON_ENT.get(), EntEntity.setCustomAttributes().build());
		event.put(CAEntityTypes.WARPED_ENT.get(), EntEntity.setCustomAttributes().build());
		event.put(CAEntityTypes.RED_ANT.get(), AggressiveAntEntity.setCustomAttributes().build());
		event.put(CAEntityTypes.BROWN_ANT.get(), AntEntity.setCustomAttributes().build());
		event.put(CAEntityTypes.RAINBOW_ANT.get(), AntEntity.setCustomAttributes().build());
		event.put(CAEntityTypes.UNSTABLE_ANT.get(), AntEntity.setCustomAttributes().build());
		event.put(CAEntityTypes.TERMITE.get(), AggressiveAntEntity.setCustomAttributes().build());
		event.put(CAEntityTypes.TREE_FROG.get(), TreeFrogEntity.setCustomAttributes().build());
		event.put(CAEntityTypes.HERCULES_BEETLE.get(), HerculesBeetleEntity.setCustomAttributes().build());
	//	event.put(CAEntityTypes.THROWBACK_HERCULES_BEETLE.get(), HerculesBeetleEntity.setCustomAttributes().build());
	//	event.put(CAEntityTypes.BIRD.get(), BirdEntity.setCustomAttributes().build());
		event.put(CAEntityTypes.APPLE_COW.get(), AppleCowEntity.setCustomAttributes().build());
		event.put(CAEntityTypes.GOLDEN_APPLE_COW.get(), GoldenAppleCowEntity.setCustomAttributes().build());
		event.put(CAEntityTypes.ULTIMATE_APPLE_COW.get(), UltimateAppleCowEntity.setCustomAttributes().build());
		event.put(CAEntityTypes.ENCHANTED_GOLDEN_APPLE_COW.get(), EnchantedGoldenAppleCowEntity.setCustomAttributes().build());
		event.put(CAEntityTypes.CRYSTAL_APPLE_COW.get(), CrystalAppleCowEntity.setCustomAttributes().build());
		event.put(CAEntityTypes.CRYSTAL_GATOR.get(), CrystalGatorEntity.setCustomAttributes().build());
		event.put(CAEntityTypes.CRYSTAL_CARROT_PIG.get(), CrystalCarrotPigEntity.setCustomAttributes().build());
		event.put(CAEntityTypes.CARROT_PIG.get(), CarrotPigEntity.setCustomAttributes().build());
		event.put(CAEntityTypes.GOLDEN_CARROT_PIG.get(), GoldenCarrotPigEntity.setCustomAttributes().build());
		event.put(CAEntityTypes.ENCHANTED_GOLDEN_CARROT_PIG.get(), EnchantedGoldenCarrotPigEntity.setCustomAttributes().build());
		event.put(CAEntityTypes.BEAVER.get(), BeaverEntity.setCustomAttributes().build());
		event.put(CAEntityTypes.DIMETRODON.get(), DimetrodonEntity.setCustomAttributes().build());
		event.put(CAEntityTypes.GAZELLE.get(), GazelleEntity.setCustomAttributes().build());
		event.put(CAEntityTypes.LETTUCE_CHICKEN.get(), LettuceChickenEntity.setCustomAttributes().build());
		event.put(CAEntityTypes.EMERALD_GATOR.get(), EmeraldGatorEntity.setCustomAttributes().build());
		event.put(CAEntityTypes.RUBY_BUG.get(), RubyBugEntity.setCustomAttributes().build());
		event.put(CAEntityTypes.STINK_BUG.get(), StinkBugEntity.setCustomAttributes().build());
		event.put(CAEntityTypes.ROBO_JEFFERY.get(), RoboJefferyEntity.setCustomAttributes().build());
		event.put(CAEntityTypes.ROBO_POUNDER.get(), RoboPounderEntity.setCustomAttributes().build());
		event.put(CAEntityTypes.ROBO_SNIPER.get(), RoboSniperEntity.setCustomAttributes().build());
		event.put(CAEntityTypes.ROBO_WARRIOR.get(), RoboWarriorEntity.setCustomAttributes().build());
//		event.put(CAEntityTypes.WASP.get(), WaspEntity.setCustomAttributes().build());
		event.put(CAEntityTypes.WHALE.get(), WhaleEntity.setCustomAttributes().build());
		event.put(CAEntityTypes.GREEN_FISH.get(), GreenFishEntity.setCustomAttributes().build());
		event.put(CAEntityTypes.ROCK_FISH.get(), RockFishEntity.setCustomAttributes().build());
		event.put(CAEntityTypes.SPARK_FISH.get(), SparkFishEntity.setCustomAttributes().build());
		event.put(CAEntityTypes.WOOD_FISH.get(), WoodFishEntity.setCustomAttributes().build());
	//	event.put(CAEntityTypes.LAVA_EEL.get(), LavaEelEntity.setCustomAttributes().build());
	}
	
	//@SubscribeEvent
	public static void onWandererTradesEvent(WandererTradesEvent event) {
		TradeUtil.addWandererTrades(event,
				new CABasicTrade(1, CABlocks.CYAN_ROSE.get().asItem(), 12),
				new CABasicTrade(1, CABlocks.RED_ROSE.get().asItem(), 12),
				new CABasicTrade(1, CABlocks.PAEONIA.get().asItem(), 12),
				new CABasicTrade(3, CABlocks.TUBE_WORM.get().asItem(), 12),
				new CABasicTrade(5, CABlocks.APPLE_SAPLING.get().asItem(), 8),
				new CABasicTrade(5, CABlocks.CHERRY_SAPLING.get().asItem(), 8),
				new CABasicTrade(5, CABlocks.PEACH_SAPLING.get().asItem(), 8),
				new CABasicTrade(5, Items.NAME_TAG, 5));

		if (CAConfigManager.MAIN_COMMON.wanderingTraderSellsTriffidGoo.get()) {
			TradeUtil.addWandererTrades(event,
					new CABasicTrade(4, CAItems.TRIFFID_GOO.get().asItem(), 5));
		}

		if (CAConfigManager.MAIN_COMMON.wanderingTraderSellsUraniumAndTitanium.get()) {
			TradeUtil.addRareWandererTrades(event,
					new CABasicTrade(15, CAItems.TITANIUM_NUGGET.get(), 3),
					new CABasicTrade(15, CAItems.URANIUM_NUGGET.get(), 3));
		}
	}

	//@SubscribeEvent
	public static void onVillagerTradesEvent(VillagerTradesEvent event) {
		TradeUtil.addVillagerTrades(event, VillagerProfession.FARMER, TradeUtil.NOVICE,
				new CABasicTrade(2, CAItems.CHERRIES.get(), 6, 16, 2),
				new CABasicTrade(2, CAItems.LETTUCE.get(), 6, 12, 2),
				new CABasicTrade(1, CAItems.CORN.get(), 5, 12, 3),
				new CABasicTrade(1, CAItems.TOMATO.get(), 5, 12, 3));
		TradeUtil.addVillagerTrades(event, VillagerProfession.FARMER, TradeUtil.APPRENTICE,
				new CABasicTrade(1, CAItems.PEACH.get(), 6, 16, 5),
				new CABasicTrade(2, CAItems.STRAWBERRY.get(), 6, 12, 5),
				new CABasicTrade(1, CAItems.RADISH.get(), 5, 12, 8));
		TradeUtil.addVillagerTrades(event, VillagerProfession.FARMER, TradeUtil.JOURNEYMAN,
				new CABasicTrade(1, CAItems.RADISH_STEW.get(), 1, 12, 15));
		TradeUtil.addVillagerTrades(event, VillagerProfession.FARMER, TradeUtil.EXPERT,
				new CABasicTrade(1, CAItems.GOLDEN_MELON_SLICE.get().asItem(), 4, 12, 15),
				new CABasicTrade(1, CAItems.GOLDEN_POTATO.get().asItem(), 4, 12, 15));
		TradeUtil.addVillagerTrades(event, VillagerProfession.FARMER, TradeUtil.MASTER,
				new CABasicTrade(CAItems.GOLDEN_MELON_SLICE.get().asItem(), 6, 1, 12, 30),
				new CABasicTrade(CAItems.GOLDEN_POTATO.get().asItem(), 6, 1, 12, 30));

		TradeUtil.addVillagerTrades(event, VillagerProfession.BUTCHER, TradeUtil.NOVICE,
				new CABasicTrade(CAItems.BACON.get(), 18, 1, 16, 2),
				new CABasicTrade(CAItems.CORNDOG.get(), 18, 1, 16, 2));
		TradeUtil.addVillagerTrades(event, VillagerProfession.BUTCHER, TradeUtil.APPRENTICE,
				new CABasicTrade(1, CAItems.COOKED_BACON.get(), 6, 16, 5),
				new CABasicTrade(1, CAItems.COOKED_CORNDOG.get(), 6, 16, 5));
		TradeUtil.addVillagerTrades(event, VillagerProfession.BUTCHER, TradeUtil.JOURNEYMAN,
				new CABasicTrade(CAItems.CRAB_MEAT.get(), 12, 1, 16, 20),
				new CABasicTrade(1, CAItems.COOKED_CRAB_MEAT.get(), 5, 16, 10));
		TradeUtil.addVillagerTrades(event, VillagerProfession.BUTCHER, TradeUtil.EXPERT,
				new CABasicTrade(CAItems.PEACOCK_LEG.get(), 12, 1, 16, 20),
				new CABasicTrade(1, CAItems.COOKED_PEACOCK_LEG.get(), 5, 16, 20));

		TradeUtil.addVillagerTrades(event, VillagerProfession.FLETCHER, TradeUtil.MASTER,
				new CABasicTrade(3, CAItems.PEACOCK_FEATHER.get(), 4, 12, 30));

		TradeUtil.addVillagerTrades(event, VillagerProfession.FISHERMAN, TradeUtil.JOURNEYMAN,
				new CABasicTrade(3, CAItems.BLUE_FISH.get(), 1, 16, 10),
				new CABasicTrade(3, CAItems.GRAY_FISH.get(), 1, 16, 10),
				new CABasicTrade(3, CAItems.GREEN_FISH.get(), 1, 12, 10),
				new CABasicTrade(3, CAItems.PINK_FISH.get(), 1, 12, 10));
		TradeUtil.addVillagerTrades(event, VillagerProfession.FISHERMAN, TradeUtil.EXPERT,
				new CABasicTrade(4, CAItems.ROCK_FISH.get(), 1, 16, 20),
				new CABasicTrade(4, CAItems.SPARK_FISH.get(), 1, 12, 20),
				new CABasicTrade(4, CAItems.WOOD_FISH.get(), 1, 16, 20));
		TradeUtil.addVillagerTrades(event, VillagerProfession.FISHERMAN, TradeUtil.MASTER,
				new CABasicTrade(5, CAItems.FIRE_FISH.get(), 1, 12, 20),
				new CABasicTrade(5, CAItems.LAVA_EEL.get(), 1, 12, 20),
				new CABasicTrade(5, CAItems.SUN_FISH.get(), 1, 8, 30));
		
		TradeUtil.addVillagerTrades(event, CAVillagers.ARCHAEOLOGIST.get(), TradeUtil.NOVICE,
				new CABasicTrade(1, Items.WATER_BUCKET, 1, 6, 2),
				new CABasicTrade(2, CAItems.ALUMINUM_POWER_CHIP.get(), 3, 8, 2),
				new CABasicTrade(1, CAItems.COPPER_LUMP.get(), 6, 6, 3),
				new CAIngredientTrade(Pair.of(Ingredient.of(CATags.Items.ARCHAEOLOGIST_FOSSILS_COMMON), 3), 5, 3, 3));
		TradeUtil.addVillagerTrades(event, CAVillagers.ARCHAEOLOGIST.get(), TradeUtil.APPRENTICE,
				new CABasicTrade(1, Items.LAVA_BUCKET, 1, 6, 5),
				new CAIngredientTrade(1, Pair.of(Ingredient.of(CATags.Items.ARCHAEOLOGIST_FOSSILS_COMMON), 3), 4, 5),
				new CAIngredientTrade(3, Pair.of(Ingredient.of(CATags.Items.ARCHAEOLOGIST_FOSSILS), 1), 9, 8),
				new CAIngredientTrade(2, Pair.of(Ingredient.of(CATags.Items.ARCHAEOLOGIST_SPAWN_EGGS_COMMON), 1), 3, 8));
		TradeUtil.addVillagerTrades(event, CAVillagers.ARCHAEOLOGIST.get(), TradeUtil.JOURNEYMAN,
				new CABasicTrade(CAItems.ALUMINUM_POWER_CHIP.get(), 8, 2, 15, 8, 2),
				new CAIngredientTrade(1, Pair.of(Ingredient.of(CATags.Items.ARCHAEOLOGIST_SPAWN_EGGS_UNCOMMON), 2), 3, 10),
				new CAIngredientTrade(4, Pair.of(Ingredient.of(CATags.Items.ARCHAEOLOGIST_FOSSILS), 2), 6, 10),
				new CAIngredientTrade(1, Pair.of(Ingredient.of(CATags.Items.ARCHAEOLOGIST_FOSSILS_UNCOMMON), 1), 6, 20),
				new CAIngredientTrade(Pair.of(Ingredient.of(CATags.Items.ARCHAEOLOGIST_FOSSILS), 6), 11, 3, 20));
		TradeUtil.addVillagerTrades(event, CAVillagers.ARCHAEOLOGIST.get(), TradeUtil.EXPERT,
				new CABasicTrade(1, CAItems.ALUMINUM_POWER_CHIP.get(), 6, 5, 20),
				new CABasicTrade(Items.LAVA_BUCKET, 1, 2, 2, 20),
				new CAIngredientTrade(2, Pair.of(Ingredient.of(CATags.Items.ARCHAEOLOGIST_SPAWN_EGGS), 2), 3, 20),
				new CAIngredientTrade(2, Pair.of(Ingredient.of(CATags.Items.ARCHAEOLOGIST_FOSSILS_RARE), 1), 6, 20),
				new CAIngredientTrade(3, Pair.of(Ingredient.of(CATags.Items.ARCHAEOLOGIST_FOSSILS_RARE), 1), 6, 30));
		TradeUtil.addVillagerTrades(event, CAVillagers.ARCHAEOLOGIST.get(), TradeUtil.MASTER,
				new CAIngredientTrade(2, Pair.of(Ingredient.of(CATags.Items.ARCHAEOLOGIST_FOSSILS), 3), 6, 30),
				new CAIngredientTrade(3, Pair.of(Ingredient.of(CATags.Items.ARCHAEOLOGIST_FOSSILS_EPIC), 1), 6, 30),
				new CAIngredientTrade(3, Pair.of(Ingredient.of(CATags.Items.ARCHAEOLOGIST_SPAWN_EGGS_RARE), 2), 7, 30),
				new CAIngredientTrade(4, Pair.of(Ingredient.of(CATags.Items.ARCHAEOLOGIST_SPAWN_EGGS_EPIC), 1), 3, 40));
	}
	
	//@SubscribeEvent
	public static void onRemapBlocksEvent(RegistryEvent.MissingMappings<Block> event) {
		for (RegistryEvent.MissingMappings.Mapping<Block> mapping : event.getAllMappings()) {
			if (mapping.key.getNamespace().equals(ChaosAwakens.MODID) && mapping.key.getPath().contains("fossilised_frog")) {
				String newName = mapping.key.getPath().replace("fossilised_frog", "fossilised_tree_frog");
				ResourceLocation remap = new ResourceLocation(ChaosAwakens.MODID, newName);
				mapping.remap(ForgeRegistries.BLOCKS.getValue(remap));
			}
		}
	}

	//@SubscribeEvent
	public static void onRemapItemsEvent(RegistryEvent.MissingMappings<Item> event) {
		for (RegistryEvent.MissingMappings.Mapping<Item> mapping : event.getAllMappings()) {
			if (mapping.key.getNamespace().equals(ChaosAwakens.MODID) && mapping.key.getPath().contains("frog_spawn_egg")) {
				String newName = mapping.key.getPath().replace("frog_spawn_egg", "tree_frog_spawn_egg");
				ResourceLocation remap = new ResourceLocation(ChaosAwakens.MODID, newName);
				mapping.remap(ForgeRegistries.ITEMS.getValue(remap));
			}
		}
	}

	//@SubscribeEvent
	public static void onRemapEntitiesEvent(RegistryEvent.MissingMappings<EntityType<?>> event) {
		for (RegistryEvent.MissingMappings.Mapping<EntityType<?>> mapping : event.getAllMappings()) {
			if (mapping.key.getNamespace().equals(ChaosAwakens.MODID) && mapping.key.getPath().contains("frog")) {
				String newName = mapping.key.getPath().replace("frog", "tree_frog");
				ResourceLocation remap = new ResourceLocation(ChaosAwakens.MODID, newName);
				mapping.remap(ForgeRegistries.ENTITIES.getValue(remap));
			}
		}
	}
	
	//@SubscribeEvent
	public static void onGatherDataEvent(final GatherDataEvent event) {
		DataGenerator dataGenerator = event.getGenerator();
		final ExistingFileHelper existing = event.getExistingFileHelper();
		if (event.includeClient()) {
			dataGenerator.addProvider(new CABlockModelProvider(dataGenerator, existing));
			dataGenerator.addProvider(new CAItemModelProvider(dataGenerator, existing));
			dataGenerator.addProvider(new CABlockStateProvider(dataGenerator, existing));
		}
		//		dataGenerator.addProvider(new CALanguageProvider(dataGenerator));
		dataGenerator.addProvider(new CAParticleTypeProvider(dataGenerator));
		if (event.includeServer()) {
			dataGenerator.addProvider(new CAAdvancementProvider(dataGenerator));
			dataGenerator.addProvider(new CACustomConversionProvider(dataGenerator));
			dataGenerator.addProvider(new CADimensionTypeProvider(dataGenerator));
			dataGenerator.addProvider(new CAGlobalLootModifierProvider(dataGenerator));
			dataGenerator.addProvider(new CALootTableProvider(dataGenerator));
			dataGenerator.addProvider(new CARecipeProvider(dataGenerator));
			dataGenerator.addProvider(new CATagProvider.CABlockTagProvider(dataGenerator, existing));
			dataGenerator.addProvider(new CATagProvider.CAItemTagProvider(dataGenerator, existing));
			dataGenerator.addProvider(new CATagProvider.CAEntityTypeTagProvider(dataGenerator, existing));
		}
	}
}