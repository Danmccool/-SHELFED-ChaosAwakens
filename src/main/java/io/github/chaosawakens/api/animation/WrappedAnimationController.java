package io.github.chaosawakens.api.animation;

import java.util.ArrayList;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.Util;
import software.bernie.geckolib3.core.builder.Animation;
import software.bernie.geckolib3.core.builder.ILoopType.EDefaultLoopTypes;
import software.bernie.geckolib3.core.controller.AnimationController;

public class WrappedAnimationController<E extends IAnimatableEntity> {
	protected E animatable;
	protected String name;
	protected ExpandedAnimationState animationState = ExpandedAnimationState.FINISHED;	
	protected Animation currentAnimation = none();
	protected double transitionLength;
	protected double transitionProgress = 0;
	protected double animationLength;
	protected double animationProgress = 0;	
	protected final AnimationController<E> controller;
	protected MinecraftServer server;
	
	public WrappedAnimationController(E animatable, int transitionTicks, AnimationController<E> controller) {
		this.animatable = animatable;
		this.transitionLength = transitionTicks;
		this.controller = controller;
		this.name = controller.getName();
		this.server = ((Entity) animatable).getServer();
	}
	
	public WrappedAnimationController(E animatable, AnimationController<E> controller) {
		this.animatable = animatable;
		this.transitionLength = animatable.animationInterval();
		this.controller = controller;
		this.name = controller.getName();
		this.server = ((Entity) animatable).getServer();
	}
	
	public void tick() { //TODO Sync client somewhat
		double delta = server == null ? Math.max(Minecraft.getInstance().getFrameTime() - Util.getMillis(), 0.0) / 50.0 : Math.max(server.getNextTickTime() - Util.getMillis(), 0.0) / 50.0;
		
		switch (animationState) {
		case TRANSITIONING:
			if (this.transitionProgress >= this.transitionLength) {
				this.transitionProgress = 0;
				this.animationState = ExpandedAnimationState.RUNNING;
			} else {
				this.transitionProgress += delta;
			}
			break;
		case RUNNING:
			if (this.animationProgress >= this.animationLength) {
				this.animationProgress = 0;
				if (this.currentAnimation.loop == EDefaultLoopTypes.LOOP) {
					this.animationProgress = 0;
					this.animationState = ExpandedAnimationState.TRANSITIONING;
				} else {
					this.animationState = ExpandedAnimationState.FINISHED;
				}
			} else {
				this.animationProgress += delta;
			}
			break;
		case STOPPED:
			if (this.currentAnimation != null) this.animationState = ExpandedAnimationState.TRANSITIONING;
			break;
		case FINISHED:
			break;
		}
	}
	
	public void playAnimation(IAnimationBuilder builder, boolean clearCache) {
		if (builder == null) {
			this.animationProgress = 0;
			this.animationLength = 0;
			this.transitionProgress = 0;
			this.animationState = ExpandedAnimationState.FINISHED;
		}
		
		if (!getCurrentAnimation().animationName.equals(builder.getAnimation().animationName) || clearCache) {
			if (clearCache) builder.playAnimation(true);
			else builder.playAnimation(false);
			
			this.animationProgress = 0;
			this.animationLength = builder.getAnimation().animationLength;
			this.transitionProgress = 0;
			this.animationState = ExpandedAnimationState.TRANSITIONING;
		}
		this.currentAnimation = builder.getAnimation();
		this.controller.setAnimation(builder.getBuilder());
	}
	
	public String getName() {
		return name;
	}
	
	public boolean isCurrentAnimationFinished() {
		return animationState.equals(ExpandedAnimationState.FINISHED);
	}
	
	public boolean isAnimationFinished(String targetAnimName) {
		return currentAnimation.animationName.equals(targetAnimName) && animationState.equals(ExpandedAnimationState.FINISHED);
	}
	
	public boolean isAnimationFinished(IAnimationBuilder targetAnim) {
		return isAnimationFinished(targetAnim.getAnimation().animationName);
	}
	
	public boolean isPlayingAnimation(String targetAnimName) {
		return currentAnimation.animationName.equals(targetAnimName) && (animationState.equals(ExpandedAnimationState.RUNNING) || animationState.equals(ExpandedAnimationState.TRANSITIONING));
	}
	
	public boolean isPlayingAnimation(IAnimationBuilder targetAnim) {
		return isPlayingAnimation(targetAnim.getAnimation().animationName);
	}
	
	public ExpandedAnimationState getAnimationState() {
		return animationState;
	}

	public double getAnimationProgressTicks() {
		return Math.ceil(animationProgress) + 3;
	}
	
	public double getAnimationLength() {
		return Math.floor(animationLength) - 4;
	}
	
	public AnimationController<E> getWrappedController() {
		return controller;
	}
	
	public Animation getCurrentAnimation() {
		return currentAnimation;
	}
	
	public static Animation none() {
		Animation noneAnimation = new Animation();
		noneAnimation.animationName = "None";
		noneAnimation.boneAnimations = new ArrayList<>();
		noneAnimation.animationLength = 0.0;
		return noneAnimation;
	}
}
