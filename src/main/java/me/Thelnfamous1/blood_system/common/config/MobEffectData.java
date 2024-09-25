package me.Thelnfamous1.blood_system.common.config;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Registry;
import net.minecraft.world.effect.MobEffect;

import java.util.Optional;

public record MobEffectData(MobEffect effect, int amplifier) {
    public static final Codec<MobEffectData> CODEC = RecordCodecBuilder.create(builder -> builder.group(
            Registry.MOB_EFFECT.byNameCodec().fieldOf("effect").forGetter(med -> med.effect),
            Codec.INT.optionalFieldOf("amplifier").forGetter(med -> Optional.of(med.amplifier)))
            .apply(builder, (effect, amplifier) -> new MobEffectData(effect, amplifier.orElse(0))));

}
