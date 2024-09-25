package me.Thelnfamous1.blood_system.mixin;

import com.electronwill.nightconfig.toml.TomlFormat;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import commoble.databuddy.config.ConfigHelper;
import org.spongepowered.asm.mixin.Mixin;

import java.util.Map;

@Mixin(ConfigHelper.TomlConfigOps.class)
public abstract class TomlConfigOpsMixin implements DynamicOps<Object> {

    @Override
    public DataResult<Object> mergeToMap(Object map, Map<Object, Object> values) {
        return DynamicOps.super.mergeToMap(map, values)
                .map(obj -> obj == this.empty()
                        ? TomlFormat.newConfig()
                        : obj);
    }
}
