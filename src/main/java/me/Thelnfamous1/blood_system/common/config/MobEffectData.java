package me.Thelnfamous1.blood_system.common.config;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import org.apache.commons.lang3.tuple.Pair;

import java.util.Optional;

public record MobEffectData(MobEffect effect, int amplifier) {
    public static final Codec<MobEffectData> CODEC = RecordCodecBuilder.create(builder -> builder.group(
            Registry.MOB_EFFECT.byNameCodec().fieldOf("effect").forGetter(med -> med.effect),
            Codec.INT.optionalFieldOf("amplifier").forGetter(med -> Optional.of(med.amplifier)))
            .apply(builder, (effect, amplifier) -> new MobEffectData(effect, amplifier.orElse(0))));

    public static Pair<ResourceLocation, Integer> asPair(MobEffectData med) {
        return Pair.of(Registry.MOB_EFFECT.getKey(med.effect()), med.amplifier());
    }
}
