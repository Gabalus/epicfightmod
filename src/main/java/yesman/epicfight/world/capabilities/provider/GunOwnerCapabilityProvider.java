package yesman.epicfight.world.capabilities.provider;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.living.LivingEvent.LivingTickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import top.ribs.scguns.item.GunItem;
import yesman.epicfight.main.EpicFightMod;
import yesman.epicfight.world.capabilities.EpicFightCapabilities;

@EventBusSubscriber(modid = EpicFightMod.MODID, bus = Bus.FORGE)
public class GunOwnerCapabilityProvider implements ICapabilityProvider {
    public static class OwnerId { public int value; }
    private LazyOptional<OwnerId> id = LazyOptional.of(() -> new OwnerId());

    @Override
    public <T> @NotNull LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        return cap == EpicFightCapabilities.OWNER_ID ? id.cast() : LazyOptional.empty();
    }

    @SubscribeEvent
    static void onAttachCapabilitiesToItemStack(AttachCapabilitiesEvent<ItemStack> event) {
        if(event.getObject().getItem() instanceof GunItem) {
            event.addCapability(
                    new ResourceLocation(EpicFightMod.MODID, "owner_id"),
                    new GunOwnerCapabilityProvider());
        }
    }

    @SubscribeEvent
    static void onLivingTick(LivingTickEvent event) {
        event.getEntity().getMainHandItem()
                .getCapability(EpicFightCapabilities.OWNER_ID)
                .ifPresent(cap -> cap.value = event.getEntity().getId());
    }
}
