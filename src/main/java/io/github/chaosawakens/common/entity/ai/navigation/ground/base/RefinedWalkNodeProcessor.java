package io.github.chaosawakens.common.entity.ai.navigation.ground.base;

import javax.annotation.Nullable;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.MobEntity;
import net.minecraft.pathfinding.FlaggedPathPoint;
import net.minecraft.pathfinding.PathNodeType;
import net.minecraft.pathfinding.PathPoint;
import net.minecraft.pathfinding.WalkNodeProcessor;
import net.minecraft.tags.ITag;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.Region;

public class RefinedWalkNodeProcessor extends WalkNodeProcessor {
	@Nullable
	private ITag<Block> breakableBlocksTag;
	@Nullable
	private ITag<Block> validPassOverride;
	
	public RefinedWalkNodeProcessor() {
		
	}
	
	public RefinedWalkNodeProcessor setBreakableBlocks(ITag<Block> breakableBlocksTag) {
		this.breakableBlocksTag = breakableBlocksTag;
		return this;
	}
	
	@Override
	public void prepare(Region curRegion, MobEntity owner) {
		super.prepare(curRegion, owner);
	}
	
	@Override
	public PathPoint getStart() {
		return super.getStart();
	}
	
	@Override
	public PathNodeType getBlockPathType(IBlockReader pBlockaccess, int pX, int pY, int pZ, MobEntity pEntityliving, int pXSize, int pYSize, int pZSize, boolean pCanBreakDoors, boolean pCanEnterDoors) {
		return super.getBlockPathType(pBlockaccess, pX, pY, pZ, pEntityliving, pXSize, pYSize, pZSize, pCanBreakDoors, pCanEnterDoors);
	}
	
	@Override
	public FlaggedPathPoint getGoal(double targetX, double targetY, double targetZ) {
		return super.getGoal(targetX, targetY, targetZ);
	}
	
	@SuppressWarnings("deprecation")
	@Override
	protected PathNodeType evaluateBlockPathType(IBlockReader pLevel, boolean pCanOpenDoors, boolean pCanEnterDoors, BlockPos pPos, PathNodeType pNodeType) {
		BlockState targetState = pLevel.getBlockState(pPos);
		
		switch (pNodeType) {
		case BLOCKED: return targetState.is(breakableBlocksTag) ? PathNodeType.BREACH : targetState.isAir(pLevel, pPos) ? PathNodeType.OPEN : PathNodeType.BLOCKED;
		case BREACH: return targetState.is(validPassOverride) ? PathNodeType.WALKABLE : targetState.isAir(pLevel, pPos) ? PathNodeType.OPEN : PathNodeType.BREACH;
		default: return super.evaluateBlockPathType(pLevel, pCanOpenDoors, pCanEnterDoors, pPos, pNodeType);
		}
	}
	
	
}
