package io.github.chaosawakens.common.entity.ai.goals.hostile;

import java.util.List;
import java.util.function.Predicate;
import java.util.function.Supplier;

import javax.annotation.Nullable;

import io.github.chaosawakens.api.animation.IAnimationBuilder;
import io.github.chaosawakens.common.entity.base.AnimatableMonsterEntity;
import io.github.chaosawakens.common.util.EntityUtil;
import io.github.chaosawakens.common.util.MathUtil;
import io.github.chaosawakens.common.util.ObjectUtil;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.Goal;

public class AnimatableMeleeGoal extends Goal {
	private final AnimatableMonsterEntity owner;
	private final Supplier<? extends IAnimationBuilder> meleeAnim;
	private final byte attackId;
	private final double actionPointTickStart;
	private final double actionPointTickEnd;
	private final double angleRange;
	private final int probability;
	@Nullable
	private Predicate<AnimatableMonsterEntity> extraActivationConditions;
	@Nullable
	private List<Supplier<? extends IAnimationBuilder>> animationsToPick;
	private final int presetCooldown;
	private int curCooldown;
	private Supplier<? extends IAnimationBuilder> curAnim;

	public AnimatableMeleeGoal(AnimatableMonsterEntity owner, Supplier<? extends IAnimationBuilder> meleeAnim, byte attackId, double actionPointTickStart, double actionPointTickEnd, double angleRange, int probability, int presetCooldown) {
		this.owner = owner;
		this.meleeAnim = meleeAnim;
		this.attackId = attackId;
		this.actionPointTickStart = actionPointTickStart;
		this.actionPointTickEnd = actionPointTickEnd;
		this.angleRange = angleRange;
		this.probability = probability;
		this.presetCooldown = presetCooldown;
	}

	public AnimatableMeleeGoal(AnimatableMonsterEntity owner, Supplier<? extends IAnimationBuilder> meleeAnim, byte attackId, double angleRange, double actionPointTickStart, double actionPointTickEnd) {
		this(owner, meleeAnim, attackId, actionPointTickStart, actionPointTickEnd, angleRange, 1, 20);
	}

	public AnimatableMeleeGoal(AnimatableMonsterEntity owner, Supplier<? extends IAnimationBuilder> meleeAnim, byte attackId, double actionPointTickStart, double actionPointTickEnd, int probability) {
		this(owner, meleeAnim, attackId, actionPointTickStart, actionPointTickEnd, 50, probability, 20);
	}

	public AnimatableMeleeGoal(AnimatableMonsterEntity owner, Supplier<? extends IAnimationBuilder> meleeAnim, byte attackId, double actionPointTickStart, double actionPointTickEnd) {
		this(owner, meleeAnim, attackId, actionPointTickStart, actionPointTickEnd, 50, 1, 20);
	}

	public AnimatableMeleeGoal(AnimatableMonsterEntity owner, Supplier<? extends IAnimationBuilder> meleeAnim, byte attackId, double actionPointTickStart, double actionPointTickEnd, double angleRange, Predicate<AnimatableMonsterEntity> activationPredicate) {
		this(owner, meleeAnim, attackId, actionPointTickStart, actionPointTickEnd, angleRange, 1, 20);
		this.extraActivationConditions = activationPredicate;
	}

	public AnimatableMeleeGoal(AnimatableMonsterEntity owner, Supplier<? extends IAnimationBuilder> meleeAnim, byte attackId, double actionPointTickStart, double actionPointTickEnd, Predicate<AnimatableMonsterEntity> activationPredicate) {
		this(owner, meleeAnim, attackId, actionPointTickStart, actionPointTickEnd, 50, 1, 20);
		this.extraActivationConditions = activationPredicate;
	}
	
	public AnimatableMeleeGoal(AnimatableMonsterEntity owner, Supplier<? extends IAnimationBuilder> meleeAnim, byte attackId, double angleRange, double actionPointTickStart, double actionPointTickEnd, int presetCooldown) {
		this(owner, meleeAnim, attackId, actionPointTickStart, actionPointTickEnd, angleRange, 1, presetCooldown);
	}

	public AnimatableMeleeGoal(AnimatableMonsterEntity owner, Supplier<? extends IAnimationBuilder> meleeAnim, byte attackId, double actionPointTickStart, double actionPointTickEnd, int probability, int presetCooldown) {
		this(owner, meleeAnim, attackId, actionPointTickStart, actionPointTickEnd, 50, probability, presetCooldown);
	}

	public AnimatableMeleeGoal(AnimatableMonsterEntity owner, Supplier<? extends IAnimationBuilder> meleeAnim, byte attackId, double actionPointTickStart, double actionPointTickEnd, Integer presetCooldown) {
		this(owner, meleeAnim, attackId, actionPointTickStart, actionPointTickEnd, 50, 1, presetCooldown);
	}

	public AnimatableMeleeGoal(AnimatableMonsterEntity owner, Supplier<? extends IAnimationBuilder> meleeAnim, byte attackId, double actionPointTickStart, double actionPointTickEnd, double angleRange, int presetCooldown, Predicate<AnimatableMonsterEntity> activationPredicate) {
		this(owner, meleeAnim, attackId, actionPointTickStart, actionPointTickEnd, angleRange, 1, presetCooldown);
		this.extraActivationConditions = activationPredicate;
	}

	public AnimatableMeleeGoal(AnimatableMonsterEntity owner, Supplier<? extends IAnimationBuilder> meleeAnim, byte attackId, double actionPointTickStart, double actionPointTickEnd, int presetCooldown, Predicate<AnimatableMonsterEntity> activationPredicate) {
		this(owner, meleeAnim, attackId, actionPointTickStart, actionPointTickEnd, 50, 1, presetCooldown);
		this.extraActivationConditions = activationPredicate;
	}
	
	public AnimatableMeleeGoal pickBetweenAnimations(Supplier<? extends IAnimationBuilder>... animations) {
		this.animationsToPick = new ObjectArrayList<Supplier<? extends IAnimationBuilder>>(animations.length);
		
		for (Supplier<? extends IAnimationBuilder> anim : animations) {
			if (anim == null || animationsToPick.contains(anim)) continue;
			
			animationsToPick.add(anim);
		}
		
		return this;
	}

	@Override
	public boolean canUse() {
		if (curCooldown > 0) curCooldown--;
		
		return ObjectUtil.performNullityChecks(false, owner, owner.getTarget(), attackId) && curCooldown <= 0 && !owner.getTarget().isInvulnerable() && owner.isAlive() && !owner.isAttacking() && owner.getTarget().isAlive()
				&& owner.distanceTo(owner.getTarget()) <= owner.getMeleeAttackReachSqr(owner.getTarget())
				&& (extraActivationConditions != null ? extraActivationConditions.test(owner) && owner.getRandom().nextInt(probability) == 0 : owner.getRandom().nextInt(probability) == 0);
	}

	@Override
	public boolean canContinueToUse() {
		return ObjectUtil.performNullityChecks(false, owner, curAnim.get(), attackId) && owner.isAlive() && !curAnim.get().hasAnimationFinished();
	}

	@Override
	public void start() {
		owner.setAttackID(attackId);
		owner.getNavigation().stop();
		
		Supplier<? extends IAnimationBuilder> targetAnim = animationsToPick != null && !animationsToPick.isEmpty() ? animationsToPick.get(owner.level.getRandom().nextInt(animationsToPick.size())) : meleeAnim;
		
		owner.playAnimation(targetAnim.get(), true);
		
		this.curAnim = targetAnim;
	}

	@Override
	public void stop() {
		owner.setAttackID((byte) 0);
		owner.playAnimation(owner.getIdleAnim(), true);
		
		this.curAnim = null;
		this.curCooldown = presetCooldown;
	}
	
	@Override
	public boolean isInterruptable() {
		return owner.isDeadOrDying();
	}

	@Override
	public void tick() {
		owner.getNavigation().stop();
		owner.setDeltaMovement(0, owner.getDeltaMovement().y(), 0);
		LivingEntity target = owner.getTarget();
		
		if (!ObjectUtil.performNullityChecks(false, target)) return;
		
		double reach = owner.getMeleeAttackReachSqr(target);
		List<LivingEntity> potentialAffectedTargets = EntityUtil.getAllEntitiesAround(owner, reach, reach, reach, reach);

		if (curAnim.get().getWrappedAnimProgress() < actionPointTickStart) owner.lookAt(target, 30F, 30F);;
		for (LivingEntity potentialAffectedTarget : potentialAffectedTargets) {			
			double targetAngle = MathUtil.getRelativeAngleBetweenEntities(owner, potentialAffectedTarget);
			double attackAngle = owner.yBodyRot % 360;
			
			if (targetAngle < 0) targetAngle += 360;
			if (attackAngle < 0) attackAngle += 360;
			
			double relativeHitAngle = targetAngle - attackAngle;
			float hitDistanceSqr = (float) (Math.sqrt((target.getZ() - owner.getZ()) * (target.getZ() - owner.getZ()) + (target.getX() - owner.getX()) * (target.getX() - owner.getX())) - owner.getBbWidth() / 2F);
			
			if (MathUtil.isBetween(curAnim.get().getWrappedAnimProgress(), actionPointTickStart, actionPointTickEnd)) {
				if (hitDistanceSqr <= reach && MathUtil.isWithinAngleRestriction(relativeHitAngle, angleRange)) owner.doHurtTarget(potentialAffectedTarget);
			}
		}
		if (curAnim.get().getWrappedAnimProgress() >= actionPointTickStart) EntityUtil.freezeEntityRotation(owner);
	}
}
