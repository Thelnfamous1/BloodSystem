package me.Thelnfamous1.blood_system.mixin;

import com.electronwill.nightconfig.toml.TomlFormat;
import com.mojang.serialization.DataResult;
import commoble.databuddy.config.ConfigHelper;
import me.Thelnfamous1.blood_system.common.util.TomlConfigOpsFix;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(value = ConfigHelper.TomlConfigOps.class, remap = false)
public abstract class TomlConfigOpsMixin implements TomlConfigOpsFix {

    @Shadow public abstract Object empty();

    @Override
    public DataResult<Object> blood_system$fixMergeToMapResult(DataResult<?> result) {
        return result.map(obj -> obj == this.empty()
                ? TomlFormat.newConfig()
                : obj);
    }
}
