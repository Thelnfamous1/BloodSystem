package me.Thelnfamous1.blood_system.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import me.Thelnfamous1.blood_system.common.util.TomlConfigOpsFix;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(value = DynamicOps.class, remap = false)
public interface DynamicOpsMixin {

    @ModifyReturnValue(method = "mergeToMap(Ljava/lang/Object;Ljava/util/Map;)Lcom/mojang/serialization/DataResult;", at = @At("RETURN"))
    private DataResult<Object> post_mergeToMap(DataResult<Object> original){
        if(this instanceof TomlConfigOpsFix configOpsFix){
            return configOpsFix.blood_system$fixMergeToMapResult(original);
        }
        return original;
    }
}
