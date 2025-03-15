package yesman.epicfight.events;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import yesman.epicfight.main.EpicFightMod;
import yesman.epicfight.world.capabilities.provider.GunCapabilityProvider;

@Mod.EventBusSubscriber(modid = EpicFightMod.MODID, bus = Bus.FORGE)
public class CapabilityHandler {
    @SubscribeEvent
    static void onAttachItemCapabilities(AttachCapabilitiesEvent<ItemStack> event) {
        if(event.getObject() != null) {
            var provider = new GunCapabilityProvider(event.getObject());

            if(provider.hasCapability()) {
                event.addCapability(new ResourceLocation(EpicFightMod.MODID, "item_cap"), provider);
            }
        }
    }
}

