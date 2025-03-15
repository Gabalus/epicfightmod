package yesman.epicfight.events;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import top.ribs.scguns.init.ModSyncedDataKeys;
import top.ribs.scguns.item.GunItem;
import yesman.epicfight.api.animation.LivingMotion;
import yesman.epicfight.api.animation.LivingMotions;
import yesman.epicfight.api.client.forgeevent.UpdatePlayerMotionEvent;
import yesman.epicfight.main.EpicFightMod;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Mod.EventBusSubscriber(modid = EpicFightMod.MODID, bus = Bus.FORGE, value = Dist.CLIENT)
public class PlayerHandler {
    static Map<UUID, LivingMotion> preLivingMotions = new HashMap<>();

    @SubscribeEvent
    static void onLogout(PlayerEvent.PlayerLoggedOutEvent event) {
        preLivingMotions.remove(event.getEntity().getUUID());
    }

    @SubscribeEvent
    static void onPlayerMotionComposite(UpdatePlayerMotionEvent.BaseLayer event) {
        var playerPatch = event.getPlayerPatch();
        var player = playerPatch.getOriginal();

        if(player.getMainHandItem().getItem() instanceof GunItem) {
            if(ModSyncedDataKeys.AIMING.getValue(player)) {
                preLivingMotions.put(player.getUUID(), playerPatch.currentLivingMotion);
                playerPatch.currentLivingMotion = LivingMotions.AIM;
            }
        }
    }

    @SubscribeEvent
    static void onPlayerMotionComposite(UpdatePlayerMotionEvent.CompositeLayer event) {
        var playerPatch = event.getPlayerPatch();
        var player = playerPatch.getOriginal();
        var preLivingMotion = preLivingMotions.get(player.getUUID());

        if(preLivingMotion != null) {
            playerPatch.currentLivingMotion = preLivingMotion;
            preLivingMotions.remove(player.getUUID());
        }

        if(player.getMainHandItem().getItem() instanceof GunItem) {
            if(ModSyncedDataKeys.RELOADING.getValue(player)) {
                playerPatch.currentCompositeMotion = LivingMotions.RELOAD;
            } else if(ModSyncedDataKeys.AIMING.getValue(player)) {
                playerPatch.currentCompositeMotion = LivingMotions.AIM;
            } else if(ModSyncedDataKeys.SHOOTING.getValue(player)) {
                playerPatch.getClientAnimator().playReboundAnimation();
            } else {
                playerPatch.currentCompositeMotion = playerPatch.currentLivingMotion;
            }
        }
    }
}
