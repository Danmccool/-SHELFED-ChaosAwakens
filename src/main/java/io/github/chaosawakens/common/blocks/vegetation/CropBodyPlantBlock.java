package io.github.chaosawakens.common.blocks.vegetation;

import java.util.function.Supplier;

import net.minecraft.block.AbstractBodyPlantBlock;
import net.minecraft.block.AbstractTopPlantBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReader;

public class CropBodyPlantBlock extends AbstractBodyPlantBlock {
	protected final Supplier<? extends AbstractTopPlantBlock> headBlock;
	protected ItemStack cloneItemStack;
	
	public CropBodyPlantBlock(Direction direction, VoxelShape shape, boolean scheduleFluidTicks, Supplier<AbstractTopPlantBlock> headBlock, Properties properties) {
		super(properties, direction, shape, scheduleFluidTicks);
		this.headBlock = headBlock;
		this.cloneItemStack = new ItemStack(Items.AIR);
	}
	
	public CropBodyPlantBlock(Direction direction, VoxelShape shape, Supplier<AbstractTopPlantBlock> headBlock, Properties properties) {
		super(properties, direction, shape, false);
		this.headBlock = headBlock;
		this.cloneItemStack = new ItemStack(Items.AIR);
	}

	@Override
	public BlockState updateShape(BlockState state, Direction direction, BlockState facingState, IWorld worldIn, BlockPos curPos, BlockPos facingPos) {
		if (direction == this.growthDirection.getOpposite() && !state.canSurvive(worldIn, curPos)) {
			worldIn.getBlockTicks().scheduleTick(curPos, this, 1);
		}

		CropTopPlantBlock topBlock = (CropTopPlantBlock) this.getHeadBlock();
		if (direction == this.growthDirection) {
			Block block = facingState.getBlock();
			if (block != this && block != topBlock) return topBlock.getUpdateShapeState(worldIn);
		}

		if (this.scheduleFluidTicks) worldIn.getLiquidTicks().scheduleTick(curPos, Fluids.WATER, Fluids.WATER.getTickDelay(worldIn));

		return super.updateShape(state, direction, facingState, worldIn, curPos, facingPos);
	}

	@Override
	public boolean canSurvive(BlockState state, IWorldReader reader, BlockPos pos) {
		BlockPos downPos = pos.relative(this.growthDirection.getOpposite());
		BlockState downState = reader.getBlockState(downPos);
		Block belowBlock = downState.getBlock();
		return belowBlock == this.getHeadBlock() || belowBlock == this.getBodyBlock()
				|| downState.is(Blocks.GRASS_BLOCK) || downState.is(Blocks.DIRT)
				|| downState.is(Blocks.COARSE_DIRT) || downState.is(Blocks.PODZOL)
				|| downState.is(Blocks.FARMLAND);
	}

	@Override
	protected AbstractTopPlantBlock getHeadBlock() {
		return this.headBlock.get();
	}
	
	@Override
	public ItemStack getCloneItemStack(IBlockReader pLevel, BlockPos pPos, BlockState pState) {
		return cloneItemStack;
	}
	
	public CropBodyPlantBlock dropItem(Item cloneItemStack) {
		this.cloneItemStack = new ItemStack(cloneItemStack);
		
		return this;
	}
}