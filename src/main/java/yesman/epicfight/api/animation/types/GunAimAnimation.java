package yesman.epicfight.api.animation.types;

import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import org.joml.Quaternionf;
import yesman.epicfight.api.animation.AnimationPlayer;
import yesman.epicfight.api.animation.JointTransform;
import yesman.epicfight.api.animation.LivingMotions;
import yesman.epicfight.api.animation.Pose;
import yesman.epicfight.api.animation.types.AimAnimation;
import yesman.epicfight.api.animation.types.DynamicAnimation;
import yesman.epicfight.api.client.animation.ClientAnimator;
import yesman.epicfight.api.client.animation.Layer;
import yesman.epicfight.api.model.Armature;
import yesman.epicfight.api.utils.math.OpenMatrix4f;
import yesman.epicfight.api.utils.math.QuaternionUtils;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;

public class GunAimAnimation extends AimAnimation {
    private float totalTime;

    public GunAimAnimation(boolean repeatPlay, String path1, String path2, String path3, String path4, Armature armature) {
        super(repeatPlay, path1, path2, path3, path4, armature);
    }

    @Override
    public void tick(LivingEntityPatch<?> entitypatch) {
        super.tick(entitypatch);

        var animator = entitypatch.getClientAnimator();
        var layer = animator.getCompositeLayer(this.getPriority());
        var player = layer.animationPlayer;

        if(isRepeat() && player.getElapsedTime() >= this.totalTime - 0.06F) {
            layer.resume(); // cancels pause()
        }
    }

    @Override
    public void modifyPose(DynamicAnimation animation, Pose pose, LivingEntityPatch<?> entitypatch, float time, float partialTicks) {
        if (!entitypatch.isFirstPerson()) {
            var chest = pose.getOrDefaultTransform("Chest");
            var head = pose.getOrDefaultTransform("Head");
            var f = 90.0F;
            var ratio = (f - Math.abs(entitypatch.getOriginal().getXRot())) / f;
            var yawOffset = entitypatch.getOriginal().yBodyRot;
            var yHeadRot = entitypatch.getOriginal().getYHeadRot();
            var qHead = QuaternionUtils.YP.rotationDegrees(Mth.wrapDegrees(yawOffset - yHeadRot) * ratio);
            var qBody = QuaternionUtils.YP.rotationDegrees(Mth.wrapDegrees(yHeadRot - yawOffset) * ratio);

            // forced head rotation for swim (crawl) motion
            if(entitypatch.currentLivingMotion == LivingMotions.SWIM /* || entitypatch.currentLivingMotion == LivingMotions.FLY */) {
                qHead.mul(QuaternionUtils.XP.rotationDegrees(-80));
            }

            head.frontResult(JointTransform.getRotation(qHead), OpenMatrix4f::mul);
            chest.frontResult(JointTransform.getRotation(qBody), OpenMatrix4f::mul);
        }
    }
}
