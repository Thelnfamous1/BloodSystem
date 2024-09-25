package me.Thelnfamous1.blood_system.common.codec;

import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.util.Pair;
import com.mojang.datafixers.util.Unit;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.Lifecycle;
import com.mojang.serialization.MapLike;
import com.mojang.serialization.RecordBuilder;

import java.util.*;

public interface BaseNavigableMapCodec<K, V> {
    Codec<K> keyCodec();

    Codec<V> elementCodec();

    default <T> DataResult<NavigableMap<K, V>> decode(final DynamicOps<T> ops, final MapLike<T> input) {
        final TreeMap<K, V> read = new TreeMap<>();
        final ImmutableList.Builder<Pair<T, T>> failed = ImmutableList.builder();

        final DataResult<Unit> result = input.entries().reduce(
            DataResult.success(Unit.INSTANCE, Lifecycle.stable()),
            (r, pair) -> {
                final DataResult<K> k = keyCodec().parse(ops, pair.getFirst());
                final DataResult<V> v = elementCodec().parse(ops, pair.getSecond());

                final DataResult<Pair<K, V>> entry = k.apply2stable(Pair::of, v);
                entry.error().ifPresent(e -> failed.add(pair));

                return r.apply2stable((u, p) -> {
                    read.put(p.getFirst(), p.getSecond());
                    return u;
                }, entry);
            },
            (r1, r2) -> r1.apply2stable((u1, u2) -> u1, r2)
        );

        final NavigableMap<K, V> elements = Collections.unmodifiableNavigableMap(read);
        final T errors = ops.createMap(failed.build().stream());

        return result.map(unit -> elements).setPartial(elements).mapError(e -> e + " missed input: " + errors);
    }

    default <T> RecordBuilder<T> encode(final NavigableMap<K, V> input, final DynamicOps<T> ops, final RecordBuilder<T> prefix) {
        for (final Map.Entry<K, V> entry : input.entrySet()) {
            prefix.add(keyCodec().encodeStart(ops, entry.getKey()), elementCodec().encodeStart(ops, entry.getValue()));
        }
        return prefix;
    }
}