package yesman.epicfight.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import top.ribs.scguns.client.GunItemStackRenderer;
import top.ribs.scguns.client.handler.GunRenderingHandler;
import yesman.epicfight.world.capabilities.EpicFightCapabilities;

@Mixin(GunItemStackRenderer.class)
public class GunItemStackRendererMixin extends BlockEntityWithoutLevelRenderer {
    public GunItemStackRendererMixin(BlockEntityRenderDispatcher berd, EntityModelSet ems) {
        super(berd, ems);
    }

    // EpicFight uses a custom rendering system which causes this method to be called
    // for players other than the local player. This is not expected by Crayfishs Gun
    // Mod which causes a visual bug when firing a gun (muzzles are drawn for ALL
    // players holding a gun). This overwrite provides a workaround to fix this issue.
    // A custom item capability is used to retrieve the actual owner of a gun.
    /**
     * @Gabalus
     */
    @Overwrite(remap = false)
    public void renderByItem(ItemStack stack, ItemDisplayContext display, PoseStack poseStack, MultiBufferSource source, int light, int overlay) {
        var mc = Minecraft.getInstance();
        var id = stack.getCapability(EpicFightCapabilities.OWNER_ID).orElse(null);
        LivingEntity living = null;

        if(id != null) {
            if(mc.level.getEntity(id.value) instanceof LivingEntity entity) {
                living = entity;
            }
        }

        if(living == null) {
            living = mc.player;
        }

        poseStack.popPose();
        poseStack.pushPose();

        if(display == ItemDisplayContext.GROUND) {
            GunRenderingHandler.get().applyWeaponScale(stack, poseStack);
        }

        GunRenderingHandler.get().renderWeapon(living, stack, display, poseStack, source, light, Minecraft.getInstance().getDeltaFrameTime());
        poseStack.popPose();
        poseStack.pushPose();
    }
}
