package io.github.chaosawakens.common.entity.ai.goals.hostile;

import java.util.function.BiFunction;
import java.util.function.Supplier;

import io.github.chaosawakens.api.animation.SingletonAnimationBuilder;
import io.github.chaosawakens.common.entity.base.AnimatableMonsterEntity;
import io.github.chaosawakens.common.util.MathUtil;
import io.github.chaosawakens.common.util.ObjectUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;

public class AnimatableShootGoal extends Goal {
	private final AnimatableMonsterEntity owner;
	protected final byte attackId;
	Supplier<SingletonAnimationBuilder> shootAnim;
	protected final BiFunction<AnimatableMonsterEntity, Vector3d, Entity> projectileFactory;
	protected final Vector3d projectileOffset;
	protected final double actionPointTickStart;
	protected final double actionPointTickEnd;
	protected final int fireRate;
	protected final int probability;
	protected final double minimumDistance;
	protected boolean hasShotProjectile;

	public AnimatableShootGoal(AnimatableMonsterEntity owner, byte attackId, Supplier<SingletonAnimationBuilder> shootAnim,
			BiFunction<AnimatableMonsterEntity, Vector3d, Entity> projectileFactory, Vector3d projectileOffset,
			double actionPointTickStart, double actionPointTickEnd, int fireRate, int probability, double minimumDistance) {
		this.owner = owner;
		this.shootAnim = shootAnim;
		this.fireRate = fireRate;
		this.attackId = attackId;
		this.actionPointTickStart = actionPointTickStart;
		this.actionPointTickEnd = actionPointTickEnd;
		this.projectileOffset = projectileOffset;
		this.probability = probability;
		this.projectileFactory = projectileFactory;
		this.minimumDistance = minimumDistance;
	}
	
	public boolean canUse() {
		LivingEntity target = this.owner.getTarget();
		return ObjectUtil.performNullityChecks(false, target)
				&& owner.distanceTo(target) >= this.minimumDistance && this.owner.canSee(target) && !target.isInvulnerable()
				&& owner.isAlive() && !owner.isAttacking() && target.isAlive() && !target.isDeadOrDying()
				&& !this.owner.isOnAttackCooldown() && owner.getRandom().nextInt(this.probability) == 0;
	}
	
	@Override
	public boolean canContinueToUse() {
		LivingEntity target = this.owner.getTarget();
		return ObjectUtil.performNullityChecks(false, target) && !owner.isDeadOrDying()
				&& !this.shootAnim.get().hasAnimationFinished();
	}

	public void start() {
		owner.setAttackID(attackId);
		owner.getNavigation().stop();
		owner.playAnimation(shootAnim.get(), true);
		this.hasShotProjectile = false;
		owner.getLookControl().setLookAt(owner.getTarget(), 30.0F, 30.0F);
	}

	/**
	 * Reset the task's internal state. Called when this task is interrupted by
	 * another one
	 */
	public void stop() {
		owner.setAttackID((byte) 0);
		owner.stopAnimation(shootAnim.get());
		owner.setAttackCooldown(fireRate);
		this.hasShotProjectile = false;
	}

	/**
	 * Keep ticking a continuous task that has already been started
	 */
	public void tick() {
		owner.getNavigation().stop();
		LivingEntity target = this.owner.getTarget();
		if (MathUtil.isBetween(shootAnim.get().getWrappedAnimProgress(), actionPointTickStart, actionPointTickEnd)) {
			if (!this.hasShotProjectile) {
				World world = this.owner.level;
				world.addFreshEntity(this.projectileFactory.apply(this.owner, this.projectileOffset));
				this.hasShotProjectile = true;
			}
		} else {
			owner.getLookControl().setLookAt(target, 30.0F, 30.0F);
		}
	}
}