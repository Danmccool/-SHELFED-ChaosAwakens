package io.github.chaosawakens.client;

import io.github.chaosawakens.ChaosAwakens;
import io.github.chaosawakens.client.entity.render.AppleCowRender;
import io.github.chaosawakens.client.entity.render.BeaverEntityRender;
import io.github.chaosawakens.client.entity.render.BrownAntEntityRender;
import io.github.chaosawakens.client.entity.render.EmeraldGatorEntityRender;
import io.github.chaosawakens.client.entity.render.EnchantedGoldenAppleCowRender;
import io.github.chaosawakens.client.entity.render.EntEntityRender;
import io.github.chaosawakens.client.entity.render.GoldenAppleCowRender;
import io.github.chaosawakens.client.entity.render.HerculesBeetleEntityRender;
import io.github.chaosawakens.client.entity.render.IrukandjiArrowRender;
import io.github.chaosawakens.client.entity.render.RainbowAntEntityRender;
import io.github.chaosawakens.client.entity.render.RedAntEntityRender;
import io.github.chaosawakens.client.entity.render.RoboSniperEntityRender;
import io.github.chaosawakens.client.entity.render.RubyBugEntityRender;
import io.github.chaosawakens.client.entity.render.TermiteEntityRender;
import io.github.chaosawakens.client.entity.render.UltimateArrowRender;
import io.github.chaosawakens.client.entity.render.UnstableAntEntityRender;
import io.github.chaosawakens.registry.ModBlocks;
import io.github.chaosawakens.registry.CAEntityTypes;
import io.github.chaosawakens.registry.CAItems;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.RenderTypeLookup;
import net.minecraft.entity.EntityType;
import net.minecraft.item.ItemModelsProperties;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@Mod.EventBusSubscriber(modid = ChaosAwakens.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientEventBusSubscriber {
	
	@SubscribeEvent
	public static void onClientSetup(FMLClientSetupEvent event) {
		RenderingRegistry.registerEntityRenderingHandler(CAEntityTypes.HERCULES_BEETLE.get(), HerculesBeetleEntityRender::new);
		RenderingRegistry.registerEntityRenderingHandler(CAEntityTypes.RUBY_BUG.get(), RubyBugEntityRender::new);
		RenderingRegistry.registerEntityRenderingHandler(CAEntityTypes.ENT.get(), EntEntityRender::new);
		RenderingRegistry.registerEntityRenderingHandler(CAEntityTypes.RED_ANT.get(), RedAntEntityRender::new);
		RenderingRegistry.registerEntityRenderingHandler(CAEntityTypes.BROWN_ANT.get(), BrownAntEntityRender::new);
		RenderingRegistry.registerEntityRenderingHandler(CAEntityTypes.RAINBOW_ANT.get(), RainbowAntEntityRender::new);
		RenderingRegistry.registerEntityRenderingHandler(CAEntityTypes.UNSTABLE_ANT.get(), UnstableAntEntityRender::new);
		RenderingRegistry.registerEntityRenderingHandler(CAEntityTypes.TERMITE.get(), TermiteEntityRender::new);
		RenderingRegistry.registerEntityRenderingHandler(CAEntityTypes.ULTIMATE_ARROW.get(), UltimateArrowRender::new);
		RenderingRegistry.registerEntityRenderingHandler(CAEntityTypes.IRUKANDJI_ARROW.get(), IrukandjiArrowRender::new);
		RenderingRegistry.registerEntityRenderingHandler(CAEntityTypes.APPLE_COW.get(), AppleCowRender::new);
		RenderingRegistry.registerEntityRenderingHandler(CAEntityTypes.GOLDEN_APPLE_COW.get(), GoldenAppleCowRender::new);
        RenderingRegistry.registerEntityRenderingHandler(CAEntityTypes.ENCHANTED_GOLDEN_APPLE_COW.get(), EnchantedGoldenAppleCowRender::new);
		RenderingRegistry.registerEntityRenderingHandler(CAEntityTypes.BEAVER.get(), BeaverEntityRender::new);
		RenderingRegistry.registerEntityRenderingHandler(CAEntityTypes.EMERALD_GATOR.get(), EmeraldGatorEntityRender::new);
		RenderingRegistry.registerEntityRenderingHandler(CAEntityTypes.ROBO_SNIPER.get(), RoboSniperEntityRender::new);
		
		RenderTypeLookup.setRenderLayer(ModBlocks.PINK_TOURMALINE_BLOCK.get(), RenderType.getCutout());
		RenderTypeLookup.setRenderLayer(ModBlocks.CATS_EYE_BLOCK.get(), RenderType.getCutout());
		
		RenderTypeLookup.setRenderLayer(ModBlocks.RED_ANT_NEST.get(), RenderType.getCutout());
		RenderTypeLookup.setRenderLayer(ModBlocks.BROWN_ANT_NEST.get(), RenderType.getCutout());
		RenderTypeLookup.setRenderLayer(ModBlocks.RAINBOW_ANT_NEST.get(), RenderType.getCutout());
		RenderTypeLookup.setRenderLayer(ModBlocks.UNSTABLE_ANT_NEST.get(), RenderType.getCutout());
		RenderTypeLookup.setRenderLayer(ModBlocks.TERMITE_NEST.get(), RenderType.getCutout());
		
		RenderTypeLookup.setRenderLayer(ModBlocks.GOLDEN_MELON_STEM.get(), RenderType.getCutoutMipped());
		RenderTypeLookup.setRenderLayer(ModBlocks.ATTACHED_GOLDEN_MELON_STEM.get(), RenderType.getCutoutMipped());
		
		RenderTypeLookup.setRenderLayer(ModBlocks.CRYSTAL_GRASS_BLOCK.get(), RenderType.getCutout());
		RenderTypeLookup.setRenderLayer(ModBlocks.KYANITE.get(), RenderType.getCutoutMipped());
		RenderTypeLookup.setRenderLayer(ModBlocks.CRYSTAL_LOG.get(), RenderType.getCutoutMipped());
		RenderTypeLookup.setRenderLayer(ModBlocks.CRYSTAL_WOOD.get(), RenderType.getCutoutMipped());
		RenderTypeLookup.setRenderLayer(ModBlocks.CRYSTAL_WOOD_PLANKS.get(), RenderType.getCutoutMipped());
		RenderTypeLookup.setRenderLayer(ModBlocks.RED_CRYSTAL_LEAVES.get(), RenderType.getCutoutMipped());
		RenderTypeLookup.setRenderLayer(ModBlocks.GREEN_CRYSTAL_LEAVES.get(), RenderType.getCutoutMipped());
		RenderTypeLookup.setRenderLayer(ModBlocks.YELLOW_CRYSTAL_LEAVES.get(), RenderType.getCutoutMipped());
		RenderTypeLookup.setRenderLayer(ModBlocks.PINK_TOURMALINE_CLUSTER.get(), RenderType.getCutoutMipped());
		RenderTypeLookup.setRenderLayer(ModBlocks.CATS_EYE_CLUSTER.get(), RenderType.getCutoutMipped());
		RenderTypeLookup.setRenderLayer(ModBlocks.BUDDING_PINK_TOURMALINE.get(), RenderType.getCutoutMipped());
		RenderTypeLookup.setRenderLayer(ModBlocks.BUDDING_CATS_EYE.get(), RenderType.getCutoutMipped());
		RenderTypeLookup.setRenderLayer(ModBlocks.CRYSTAL_CRAFTING_TABLE.get(), RenderType.getCutoutMipped());
		RenderTypeLookup.setRenderLayer(ModBlocks.CRYSTAL_FURNACE.get(), RenderType.getCutoutMipped());
		
		ItemModelsProperties.registerProperty(CAItems.SKATE_STRING_BOW.get(), new ResourceLocation("pull"),
				(stack, world, living) -> {
					if (living == null) {
						return 0.0F;
					} else {
						return living.getActiveItemStack() != stack ? 0.0F : (float)(stack.getUseDuration() - living.getItemInUseCount()) / 20.0F;
					}
		});
		ItemModelsProperties.registerProperty(CAItems.SKATE_STRING_BOW.get(), new ResourceLocation("pulling"),
				(stack, world, living) -> {
					return living != null && living.isHandActive() && living.getActiveItemStack() == stack ? 1.0F : 0.0F;
		});
	}
	
	@SubscribeEvent
	public static void onRegisterEntities(final RegistryEvent.Register<EntityType<?>> event) {}
}