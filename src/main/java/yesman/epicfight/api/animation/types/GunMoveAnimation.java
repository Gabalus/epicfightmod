package yesman.epicfight.api.animation.types;

import net.minecraft.world.entity.LivingEntity;
import yesman.epicfight.api.model.Armature;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;

public class GunMoveAnimation extends GunAimAnimation {
    public GunMoveAnimation(boolean repeatPlay, String path1, String path2, String path3, String path4, Armature armature) {
        super(repeatPlay, path1, path2, path3, path4, armature);
    }

    // @Override
    // public float getPlaySpeed(LivingEntityPatch<?> entitypatch) {
    // 	float movementSpeed = 1.0F;

    // 	if (Math.abs(entitypatch.getOriginal().animationSpeed - entitypatch.getOriginal().animationSpeedOld) < 0.007F) {
    // 		movementSpeed *= (entitypatch.getOriginal().animationSpeed * 1.16F);
    // 	}

    // 	return movementSpeed;
    // }

    @Override
    public boolean canBePlayedReverse() {
        return true;
    }
}

