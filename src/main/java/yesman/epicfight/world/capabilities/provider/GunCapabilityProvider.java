package yesman.epicfight.world.capabilities.provider;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.common.util.NonNullSupplier;
import top.ribs.scguns.common.GripType;
import top.ribs.scguns.item.GrenadeItem;
import top.ribs.scguns.item.GunItem;
import yesman.epicfight.world.capabilities.EpicFightCapabilities;
import yesman.epicfight.world.capabilities.item.CapabilityItem;
import yesman.epicfight.world.capabilities.item.GunCapabilityPresets;

public class GunCapabilityProvider implements ICapabilityProvider, NonNullSupplier<CapabilityItem> {
    private final LazyOptional<CapabilityItem> optional = LazyOptional.of(this);
    private CapabilityItem capability;

    public GunCapabilityProvider(ItemStack itemStack) {
        if(itemStack.getItem() instanceof GunItem gunItem) {
            var gripType = gunItem.getGun().getGeneral().getGripType(itemStack);

            this.capability = gripType == GripType.ONE_HANDED
                    ? GunCapabilityPresets.PISTOL.apply(gunItem).build() : gripType == GripType.TWO_HANDED
                    ? GunCapabilityPresets.RIFLE.apply(gunItem).build() : gripType == GripType.BAZOOKA
                    ? GunCapabilityPresets.BAZOOKA.apply(gunItem).build() : gripType == GripType.MINI_GUN
                    ? GunCapabilityPresets.MINI_GUN.apply(gunItem).build() : null;
        } else if(itemStack.getItem() instanceof GrenadeItem grenadeItem) {
            this.capability = GunCapabilityPresets.GRENADE.apply(grenadeItem).build();
        }
    }

    @Override
    public <T> @NotNull LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        return cap == EpicFightCapabilities.CAPABILITY_ITEM ? this.optional.cast() : LazyOptional.empty();
    }

    @Override
    public @NotNull CapabilityItem get() {
        return this.capability;
    }

    public boolean hasCapability() {
        return this.capability != null;
    }
}
