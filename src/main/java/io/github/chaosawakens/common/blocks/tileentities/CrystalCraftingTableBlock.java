package io.github.chaosawakens.common.blocks.tileentities;

import io.github.chaosawakens.ChaosAwakens;
import io.github.chaosawakens.common.gui.container.CraftingTableContainer;
import io.github.chaosawakens.common.registry.CAStats;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.inventory.container.SimpleNamedContainerProvider;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.IWorldPosCallable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;

public class CrystalCraftingTableBlock extends Block {
	private static final TranslationTextComponent CONTAINER_NAME = new TranslationTextComponent("container." + ChaosAwakens.MODID + ".crystal_crafting_table");

	public CrystalCraftingTableBlock(Properties properties) {
		super(properties);
	}

	@Override
	public ActionResultType use(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit) {
		if (worldIn.isClientSide) return ActionResultType.SUCCESS;
		else {
			player.openMenu(state.getMenuProvider(worldIn, pos));
			player.awardStat(CAStats.INTERACT_WITH_CRYSTAL_CRAFTING_TABLE);
			
			return ActionResultType.CONSUME;
		}
	}

	@Override
	public INamedContainerProvider getMenuProvider(BlockState state, World worldIn, BlockPos pos) {
		return new SimpleNamedContainerProvider((id, inventory, player) -> new CraftingTableContainer(id, inventory, IWorldPosCallable.create(worldIn, pos), this), CONTAINER_NAME);
	}
}
