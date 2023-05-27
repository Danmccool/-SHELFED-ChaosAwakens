package io.github.chaosawakens.common.entity.base;

import javax.annotation.Nullable;

import io.github.chaosawakens.api.animation.IAnimatableEntity;
import io.github.chaosawakens.api.animation.SingletonAnimationBuilder;
import io.github.chaosawakens.common.entity.ai.goals.passive.water.RandomRoamSwimmingGoal;
import io.github.chaosawakens.common.registry.CAEffects;
import net.minecraft.entity.EntitySize;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.Pose;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.goal.AvoidEntityGoal;
import net.minecraft.entity.ai.goal.PanicGoal;
import net.minecraft.entity.passive.fish.AbstractFishEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.IPacket;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.EntityPredicates;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkHooks;
import software.bernie.geckolib3.core.PlayState;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.manager.AnimationFactory;

public abstract class AnimatableFishEntity extends AbstractFishEntity implements IAnimatableEntity {
	private static final DataParameter<Boolean> SWIMMING = EntityDataManager.defineId(AnimatableFishEntity.class, DataSerializers.BOOLEAN);
	public float prevBodyRot;

	public AnimatableFishEntity(EntityType<? extends AbstractFishEntity> type, World world) {
		super(type, world);
	}

	@Override
	public abstract AnimationFactory getFactory();

	@Override
	public abstract AnimationController<? extends IAnimatableEntity> getMainController();

	@Override
	public abstract int animationInterval();

	@Override
	public abstract <E extends IAnimatableEntity> PlayState mainPredicate(AnimationEvent<E> event);

	@Override
	protected void registerGoals() {
		this.goalSelector.addGoal(0, new PanicGoal(this, 1.25D));
		this.goalSelector.addGoal(1, new AvoidEntityGoal<PlayerEntity>(this, PlayerEntity.class, 8.0F, 1.6D, 1.4D, EntityPredicates.NO_SPECTATORS::test));
		this.goalSelector.addGoal(1, new RandomRoamSwimmingGoal(this));
	}

	@Nullable
	abstract public SingletonAnimationBuilder getIdleAnim();

	@Nullable
	abstract public SingletonAnimationBuilder getSwimAnim();

	@Nullable
	abstract public SingletonAnimationBuilder getDeathAnim();
	
	@Override
	protected SoundEvent getFlopSound() {
		return SoundEvents.COD_FLOP;
	}

	@Override
	protected SoundEvent getSwimSound() {
		return SoundEvents.FISH_SWIM;
	}

	@Override
	protected SoundEvent getAmbientSound() {
		return SoundEvents.COD_AMBIENT;
	}

	@Override
	protected SoundEvent getDeathSound() {
		return SoundEvents.COD_DEATH;
	}
	
	public boolean canRoam() {
		return canRandomSwim();
	}

	@Override
	protected void defineSynchedData() {
		super.defineSynchedData();
		this.entityData.define(SWIMMING, !isStuck() && isInWater());
	}

	public boolean isSwimming() {
		return this.entityData.get(SWIMMING);
	}

	public void setSwimming(boolean moving) {
		this.entityData.set(SWIMMING, moving);
	}
	
	@Override
	public int getMaxAirSupply() {
		return super.getMaxAirSupply();
	}

	public boolean isStuck() {
		double dx = getX() - xo;
		double dz = getZ() - zo;
		double dxSqr = dx * dx;
		double dzSqr = dz * dz;
		return dxSqr + dzSqr < 2.500000277905201E-7;
	}

	@Override
	protected void tickDeath() {
		if (getDeathAnim() != null) {
			playAnimation(getDeathAnim());
			if (getDeathAnim().hasAnimationFinished()) remove();

			for(int i = 0; i < 20; ++i) {
				double xOffset = this.random.nextGaussian() * 0.02D;
				double yOffset = this.random.nextGaussian() * 0.02D;
				double zOffset = this.random.nextGaussian() * 0.02D;
				this.level.addParticle(ParticleTypes.POOF, getRandomX(1.0D), getRandomY(), getRandomZ(1.0D), xOffset, yOffset, zOffset);
			}
		} else {
			super.tickDeath();
		}
	}

	@Override
	public void tick() {
		this.prevBodyRot = yBodyRot;
		super.tick();

		if (!level.isClientSide) setSwimming(!isStuck());
		handleBaseAnimations();
	}

	protected double getFollowRange() {
		return this.getAttributeValue(Attributes.FOLLOW_RANGE);
	}

	protected double getAttackDamage() {
		return this.getAttributeValue(Attributes.ATTACK_DAMAGE);
	}

	protected double getAttackSpeed() {
		return this.getAttributeValue(Attributes.ATTACK_SPEED);
	}

	protected double getMovementSpeed() {
		return this.getAttributeValue(Attributes.MOVEMENT_SPEED);
	}

	protected double getFlyingSpeed() {
		return this.getAttributeValue(Attributes.FLYING_SPEED);
	}

	protected double getKnockbackResistance() {
		return this.getAttributeValue(Attributes.KNOCKBACK_RESISTANCE);
	}

	protected double getArmor() {
		return this.getAttributeValue(Attributes.ARMOR);
	}

	protected void setFollowRange(double newBaseValue) {
		this.getAttribute(Attributes.FOLLOW_RANGE).setBaseValue(newBaseValue);;
	}

	protected void setAttackDamage(double newBaseValue) {
		this.getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(newBaseValue);;
	}

	protected void setAttackSpeed(double newBaseValue) {
		this.getAttribute(Attributes.ATTACK_SPEED).setBaseValue(newBaseValue);;
	}

	protected void setMovementSpeed(double newBaseValue) {
		this.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(newBaseValue);;
	}

	protected void setFlyingSpeed(double newBaseValue) {
		this.getAttribute(Attributes.FLYING_SPEED).setBaseValue(newBaseValue);;
	}

	protected void setKnockbackResistance(double newBaseValue) {
		this.getAttribute(Attributes.KNOCKBACK_RESISTANCE).setBaseValue(newBaseValue);;
	}

	protected void setArmor(double newBaseValue) {
		this.getAttribute(Attributes.ARMOR).setBaseValue(newBaseValue);;
	}

	@Override
	public boolean canUpdate() {
		if (hasEffect(CAEffects.PARALYSIS_EFFECT.get())) return false;
		return super.canUpdate();
	}

	@Override
	public IPacket<?> getAddEntityPacket() {
		return NetworkHooks.getEntitySpawningPacket(this);
	}
	
	@Override
	protected float getStandingEyeHeight(Pose pose, EntitySize size) {
		return this.isBaby() ? size.height * 0.45F : 0.6F;
	}

	protected void handleBaseAnimations() {
		if (getIdleAnim() != null) playAnimation(getIdleAnim());
		if (getSwimAnim() != null && isSwimming()) {
			if (isInWater()) playAnimation(getSwimAnim());
		}
	}
}