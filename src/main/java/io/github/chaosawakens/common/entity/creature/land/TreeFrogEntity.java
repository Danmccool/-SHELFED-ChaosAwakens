package io.github.chaosawakens.common.entity.creature.land;

import javax.annotation.Nullable;

import io.github.chaosawakens.api.animation.IAnimatableEntity;
import io.github.chaosawakens.api.animation.SingletonAnimationBuilder;
import io.github.chaosawakens.common.entity.base.AnimatableAnimalEntity;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.entity.AgeableEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ILivingEntityData;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.IServerWorld;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import software.bernie.geckolib3.core.PlayState;
import software.bernie.geckolib3.core.builder.ILoopType.EDefaultLoopTypes;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.manager.AnimationFactory;

public class TreeFrogEntity extends AnimatableAnimalEntity {
	private final AnimationFactory factory = new AnimationFactory(this);
	private static final DataParameter<Integer> TYPE_ID = EntityDataManager.defineId(TreeFrogEntity.class, DataSerializers.INT);
	private final AnimationController<TreeFrogEntity> mainController = createMainMappedController("treefrogmaincontroller");
	private final AnimationController<TreeFrogEntity> ambienceController = createMainMappedController("treefrogambiencecontroller");
	private final SingletonAnimationBuilder idleAnim = new SingletonAnimationBuilder(this, "Idle", EDefaultLoopTypes.LOOP);
	private final SingletonAnimationBuilder jumpAnim = new SingletonAnimationBuilder(this, "Jump", EDefaultLoopTypes.PLAY_ONCE);
	private final SingletonAnimationBuilder blinkAnim = new SingletonAnimationBuilder(this, "Blink", random.nextInt(3)).setController(ambienceController);

	public TreeFrogEntity(EntityType<? extends AnimalEntity> type, World world) {
		super(type, world);
	}
	
	public static AttributeModifierMap.MutableAttribute setCustomAttributes() {
		return MobEntity.createLivingAttributes()
				.add(Attributes.MAX_HEALTH, 6)
				.add(Attributes.MOVEMENT_SPEED, 0D)
				.add(Attributes.JUMP_STRENGTH, 2.0D)
				.add(Attributes.FOLLOW_RANGE, 8.0D);
	}
	
	@Override
	public AnimationFactory getFactory() {
		return factory;
	}

	@Override
	public AnimationController<? extends IAnimatableEntity> getMainController() {
		return mainController;
	}

	@Override
	public int animationInterval() {
		return 1;
	}

	@Override
	public <E extends IAnimatableEntity> PlayState mainPredicate(AnimationEvent<E> event) {
		return PlayState.CONTINUE;
	}
	
	public <E extends IAnimatableEntity> PlayState ambiencePredicate(AnimationEvent<E> event) {
		if (!isMoving() && !isPlayingAnimation(blinkAnim, ambienceController)) playAnimation(blinkAnim);
		return PlayState.CONTINUE;
	}
	
	@Override
	protected void registerGoals() {
		
	}
	
	@Override
	protected void defineSynchedData() {
		super.defineSynchedData();
		this.entityData.define(TYPE_ID, 0);
	}
	
	private int getRandomTreeFrogType(IWorld world) {
		return this.random.nextInt(4);
	}
	
	public int getTreeFrogType() {
		return MathHelper.clamp(this.entityData.get(TYPE_ID), 0, 5);
	}

	public void setTreeFrogType(int type) {
		this.entityData.set(TYPE_ID, type);
	}
	
	@Override
	public void addAdditionalSaveData(CompoundNBT nbt) {
		super.addAdditionalSaveData(nbt);
		nbt.putInt("TreeFrogType", getTreeFrogType());
	}

	@Override
	public void readAdditionalSaveData(CompoundNBT nbt) {
		super.readAdditionalSaveData(nbt);
		setTreeFrogType(nbt.getInt("TreeFrogType"));
	}

	@Override
	public SingletonAnimationBuilder getIdleAnim() {
		return idleAnim;
	}

	@Override
	public SingletonAnimationBuilder getWalkAnim() {
		return null;
	}

	@Override
	public SingletonAnimationBuilder getDeathAnim() {
		return null;
	}
	
	@Override
	public void aiStep() {
		super.aiStep();
		
		if (hasCustomName() && getCustomName().getContents().contentEquals("froakie")) setTreeFrogType(4);
	}
	
	@Override
	protected int calculateFallDamage(float pDistance, float pDamageMultiplier) {
		return 0;
	}
	
	@Nullable
	@Override
	public ILivingEntityData finalizeSpawn(IServerWorld pLevel, DifficultyInstance pDifficulty, SpawnReason pReason, ILivingEntityData pSpawnData, CompoundNBT pDataTag) {
		int randTreeFrogType = getRandomTreeFrogType(pLevel);
		
		if (pSpawnData instanceof TreeFrogData) randTreeFrogType = ((TreeFrogData) pSpawnData).treeFrogType;
		else pSpawnData = new TreeFrogData(randTreeFrogType);
		
		setTreeFrogType(randTreeFrogType);
		return super.finalizeSpawn(pLevel, pDifficulty, pReason, pSpawnData, pDataTag);
	}

	@Override
	public AgeableEntity getBreedOffspring(ServerWorld pServerLevel, AgeableEntity pMate) {
		return null;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public ObjectArrayList<AnimationController<TreeFrogEntity>> getControllers() {
		return new ObjectArrayList<AnimationController<TreeFrogEntity>>(1);
	}
	
	private class TreeFrogData extends AgeableData {
		public final int treeFrogType;

		private TreeFrogData(int treeFrogType) {
			super(true);
			this.treeFrogType = treeFrogType;
		}
	}
}