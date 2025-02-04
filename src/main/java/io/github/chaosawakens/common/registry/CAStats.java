package io.github.chaosawakens.common.registry;

import io.github.chaosawakens.ChaosAwakens;
import net.minecraft.stats.IStatFormatter;
import net.minecraft.stats.StatType;
import net.minecraft.stats.Stats;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

@EventBusSubscriber(modid = ChaosAwakens.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class CAStats {
	public static final DeferredRegister<StatType<?>> STAT_TYPES = DeferredRegister.create(ForgeRegistries.STAT_TYPES, ChaosAwakens.MODID);

	public static final ResourceLocation INTERACT_WITH_CRYSTAL_CRAFTING_TABLE = registerCustomStat("interact_with_crystal_crafting_table");
	public static final ResourceLocation INTERACT_WITH_CRYSTAL_FURNACE = registerCustomStat("interact_with_crystal_furnace");
	public static final ResourceLocation INTERACT_WITH_DEFOSSILIZER = registerCustomStat("interact_with_defossilizer");
	public static final ResourceLocation INTERACT_WITH_ROBO_CRATE = registerCustomStat("interact_with_robo_crate");

	private static ResourceLocation registerCustomStat(String key) {
		ResourceLocation statKeyLocation = new ResourceLocation(ChaosAwakens.MODID, key);
		Registry.register(Registry.CUSTOM_STAT, key, statKeyLocation);
		Stats.CUSTOM.get(statKeyLocation, IStatFormatter.DEFAULT);
		return statKeyLocation;
	}
}
