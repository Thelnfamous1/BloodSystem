package me.Thelnfamous1.blood_system;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Registry;
import net.minecraft.world.effect.MobEffect;

import java.util.Optional;

public record MobEffectData(MobEffect effect, int duration, int amplifier) {
    public static final Codec<MobEffectData> CODEC = RecordCodecBuilder.create(builder -> builder.group(
            Registry.MOB_EFFECT.byNameCodec().fieldOf("effect").forGetter(med -> med.effect),
            Codec.INT.fieldOf("duration").forGetter(med -> med.duration),
            Codec.INT.optionalFieldOf("amplifier").forGetter(med -> Optional.of(med.amplifier)))
            .apply(builder, (effect, duration, amplifier) -> new MobEffectData(effect, duration, amplifier.orElse(0))));
}
