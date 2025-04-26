package yesman.epicfight.skill.passive;

import java.util.List;
import java.util.UUID;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import yesman.epicfight.client.gui.BattleModeGui;
import yesman.epicfight.skill.Skill;
import yesman.epicfight.skill.SkillContainer;
import yesman.epicfight.skill.passive.PassiveSkill;
import yesman.epicfight.world.capabilities.item.CapabilityItem;
import yesman.epicfight.world.capabilities.item.WeaponCategory;
import yesman.epicfight.world.entity.eventlistener.PlayerEventListener;
import yesman.epicfight.world.entity.eventlistener.PlayerEventListener.EventType;

public class SwiftEdgeSkill extends PassiveSkill {
    private static final UUID EVENT_UUID = UUID.fromString("e8b4f9d4-9a47-4b7e-8f2c-123456789abc");
    private static final CapabilityItem.WeaponCategories[] AVAILABLE_WEAPON_TYPES = {CapabilityItem.WeaponCategories.SWORD};

    private float speedBonus;

    public SwiftEdgeSkill(Builder<? extends Skill> builder) {
        super(builder);
    }

    @Override
    public void setParams(CompoundTag parameters) {
        super.setParams(parameters);
        this.speedBonus = parameters.getFloat("speed_bonus");
    }

    @Override
    public void onInitiate(SkillContainer container) {
        super.onInitiate(container);

        PlayerEventListener listener = container.getExecuter().getEventListener();
        listener.addEventListener(EventType.MODIFY_ATTACK_SPEED_EVENT, EVENT_UUID, (event) -> {
            Player player = event.getPlayerPatch().getOriginal();
            WeaponCategory mainCategory = event.getItemCapability().getWeaponCategory();
            ItemStack offhand = player.getOffhandItem();

            // Apply only if main hand has supported weapon and off-hand is empty
            if (offhand.isEmpty()) {
                for (WeaponCategory wc : AVAILABLE_WEAPON_TYPES) {
                    if (wc == mainCategory) {
                        float attackSpeed = event.getAttackSpeed();
                        event.setAttackSpeed(attackSpeed * (1.0F + this.speedBonus * 0.01F));
                        break;
                    }
                }
            }
        });
    }

    @Override
    public void onRemoved(SkillContainer container) {
        super.onRemoved(container);
        container.getExecuter().getEventListener().removeListener(EventType.MODIFY_ATTACK_SPEED_EVENT, EVENT_UUID);
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public List<Object> getTooltipArgsOfScreen(List<Object> list) {
        list.add(String.format("%.0f", this.speedBonus));
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < AVAILABLE_WEAPON_TYPES.length; i++) {
            sb.append(WeaponCategory.ENUM_MANAGER.toTranslated(AVAILABLE_WEAPON_TYPES[i]));
            if (i < AVAILABLE_WEAPON_TYPES.length - 1) {
                sb.append(", ");
            }
        }
        list.add(sb.toString());
        return list;
    }

    @Override
    public List<WeaponCategory> getAvailableWeaponCategories() {
        return List.of(AVAILABLE_WEAPON_TYPES);
    }
}
