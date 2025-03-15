package yesman.epicfight.api.animation.types;

import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import org.joml.Quaternionf;
import yesman.epicfight.api.animation.JointTransform;
import yesman.epicfight.api.animation.Pose;
import yesman.epicfight.api.animation.types.DynamicAnimation;
import yesman.epicfight.api.animation.types.StaticAnimation;
import yesman.epicfight.api.model.Armature;
import yesman.epicfight.api.utils.math.OpenMatrix4f;
import yesman.epicfight.api.utils.math.QuaternionUtils;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;

public class LockedAnimation extends StaticAnimation {
    public LockedAnimation(boolean repeatPlay, String path, Armature armature) {
        super(repeatPlay, path, armature);
    }

    @Override
    public void modifyPose(DynamicAnimation animation, Pose pose, LivingEntityPatch<?> entitypatch, float time, float partialTicks) {
        super.modifyPose(animation, pose, entitypatch, time, partialTicks);

        if (!entitypatch.isFirstPerson()) {
            var head = pose.getOrDefaultTransform("Head");
            var yawOffset = entitypatch.getOriginal().yBodyRot;
            var yHeadRot = entitypatch.getOriginal().getYHeadRot();
            head.frontResult(JointTransform.getRotation(QuaternionUtils.YP.rotationDegrees(Mth.wrapDegrees(yawOffset - yHeadRot))), OpenMatrix4f::mul);
            // TODO: prevent head from rotating with camera on x-Axis
        }
    }
}
