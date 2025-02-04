package io.github.chaosawakens.client.models.entity.creature.land;

import io.github.chaosawakens.ChaosAwakens;
import io.github.chaosawakens.client.models.entity.base.ExtendedAnimatedTickingGeoModel;
import io.github.chaosawakens.common.entity.creature.land.GazelleEntity;
import net.minecraft.util.ResourceLocation;
import software.bernie.geckolib3.core.processor.IBone;

public class GazelleEntityModel extends ExtendedAnimatedTickingGeoModel<GazelleEntity> {

	@Override
	public ResourceLocation getAnimationFileLocation(GazelleEntity animatable) {
		return ChaosAwakens.prefix("animations/entity/creature/land/gazelle.animation.json");
	}

	@Override
	public ResourceLocation getModelLocation(GazelleEntity animatable) {
		return ChaosAwakens.prefix("geo/entity/creature/land/gazelle.geo.json");
	}

	@Override
	public ResourceLocation getTextureLocation(GazelleEntity animatable) {
		switch (animatable.getGazelleType()) {
		default: return ChaosAwakens.prefix("textures/entity/creature/land/gazelle/gazelle.png");
		case 1: return ChaosAwakens.prefix("textures/entity/creature/land/gazelle/yellow_gazelle.png");
		case 2: return ChaosAwakens.prefix("textures/entity/creature/land/gazelle/dark_red_gazelle.png");
		case 3: return ChaosAwakens.prefix("textures/entity/creature/land/gazelle/tan_gazelle.png");
		case 4: return ChaosAwakens.prefix("textures/entity/creature/land/gazelle/beij_gazelle.png");
		case 5: return ChaosAwakens.prefix("textures/entity/creature/land/gazelle/black_gazelle.png");
		}
	}

	@Override
	protected boolean shouldApplyHeadRot() {
		return false;
	}

	@Override
	protected boolean shouldApplyChildScaling() {
		return true;
	}
	
	@Override
	protected boolean shouldScaleHeadWithChild() {
		return true;
	}
	
	@Override
	public IBone getBodyBone() {
		return getAnimationProcessor().getBone("Gazelle");
	}
	
	@Override
	public IBone getHeadBone() {
		return getAnimationProcessor().getBone("Head");
	}
}
