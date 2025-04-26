package yesman.epicfight.skill.guard;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import yesman.epicfight.api.animation.types.StaticAnimation;
import yesman.epicfight.gameasset.Animations;
import yesman.epicfight.gameasset.EpicFightSounds;
import yesman.epicfight.particle.EpicFightParticles;
import yesman.epicfight.particle.HitParticleType;
import yesman.epicfight.skill.Skill;
import yesman.epicfight.skill.SkillContainer;
import yesman.epicfight.skill.SkillDataKeys;
import yesman.epicfight.world.capabilities.EpicFightCapabilities;
import yesman.epicfight.world.capabilities.entitypatch.player.PlayerPatch;
import yesman.epicfight.world.capabilities.item.CapabilityItem;
import yesman.epicfight.world.entity.eventlistener.HurtEvent;

/**
 * A mirror‐parry that reflects damage and stuns the attacker on a perfect parry.
 */
public class MirrorParrySkill extends ParryingSkill {
    // Redeclare because the original PARRY_WINDOW is private in ParryingSkill :contentReference[oaicite:0]{index=0}
    private static final int PARRY_WINDOW = 8;

    private static final int STUN_DURATION_TICKS = 40;       // 2 seconds
    private static final float REFLECT_DAMAGE_FACTOR = 1.0f; // reflect 100%
    private static final float PARRY_STAMINA_PENALTY = 5.0f;

    public MirrorParrySkill(GuardSkill.Builder builder) {
        super(builder);
    }

    @Override
    public void guard(SkillContainer container, CapabilityItem itemCap,
                      HurtEvent.Pre event, float knockback, float impact, boolean advanced) {
        // Only on advanced guardable sources
        if (isHoldingWeaponAvailable(event.getPlayerPatch(), itemCap, BlockType.ADVANCED_GUARD)
                && isBlockableSource(event.getDamageSource(), true)) {

            ServerPlayer defender = event.getPlayerPatch().getOriginal();
            // Capture the incoming damage :contentReference[oaicite:1]{index=1}
            float originalDamage = event.getAmount();       // getAmount() :contentReference[oaicite:2]{index=2}

            // Check perfect‐parry timing :contentReference[oaicite:3]{index=3}
            boolean perfect = defender.tickCount
                    - container.getDataManager().getDataValue(SkillDataKeys.LAST_ACTIVE.get())
                    < PARRY_WINDOW;

            if (perfect) {
                event.setParried(true);
                event.setAmount(0f);
                                    container.getDataManager()
                                             .setDataSync(SkillDataKeys.PENALTY.get(), PARRY_STAMINA_PENALTY, defender);

                                            event.getPlayerPatch()
                                         .consumeForSkill(this, Skill.Resource.STAMINA, PARRY_STAMINA_PENALTY * impact);

                // Reflect + stun attacker
                if (event.getDamageSource().getDirectEntity() instanceof LivingEntity attacker) {
                    float reflect = originalDamage * REFLECT_DAMAGE_FACTOR;
                    // Use the correct factory method for thorns damage :contentReference[oaicite:5]{index=5}
                    attacker.hurt(event.getDamageSource(), reflect);
                    // Apply slowness V as a stun :contentReference[oaicite:6]{index=6}
                    attacker.addEffect(new MobEffectInstance(
                            MobEffects.MOVEMENT_SLOWDOWN,
                            STUN_DURATION_TICKS,
                            4,
                            false,
                            false
                    ));

                    // Audio‐visual feedback
                    defender.playSound(
                            EpicFightSounds.BLUNT_HIT_HARD.get(), // BLUNT_HIT_HARD :contentReference[oaicite:7]{index=7}
                            1.0F, 1.0F
                    );
                    EpicFightParticles.AIR_BURST.get()     // AIR_BURST :contentReference[oaicite:8]{index=8}
                            .spawnParticleWithArgument(
                                    (ServerLevel) defender.level(),
                                    HitParticleType.MIDDLE_OF_ENTITIES,  // :contentReference[oaicite:9]{index=9}
                                    HitParticleType.ATTACKER_Y_ROTATION, // :contentReference[oaicite:10]{index=10}
                                    defender, attacker
                            );
                    PlayerPatch<?> defenderPatch = EpicFightCapabilities
                            .getEntityPatch(defender, PlayerPatch.class);
                    if (defenderPatch != null) {
                        defenderPatch.playAnimationSynchronized(Animations.FIST_AUTO1, 0.0F);
                    }
                }

                this.dealEvent(event.getPlayerPatch(), event, true);
                return;
            }
        }

        // Fallback to standard parry/guard
        super.guard(container, itemCap, event, knockback, impact, advanced);
    }
}
