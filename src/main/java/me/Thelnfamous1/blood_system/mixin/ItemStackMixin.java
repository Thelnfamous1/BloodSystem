package me.Thelnfamous1.blood_system.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import me.Thelnfamous1.blood_system.common.item.BloodFillableItem;
import me.Thelnfamous1.blood_system.common.util.TooltipFlagWrapper;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

import java.util.List;

@Mixin(ItemStack.class)
public abstract class ItemStackMixin {

    @Shadow public abstract Item getItem();

    @WrapOperation(method = "getTooltipLines",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/Item;appendHoverText(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/level/Level;Ljava/util/List;Lnet/minecraft/world/item/TooltipFlag;)V"))
    private void modifyTooltipFlag_getTooltipLines(Item instance, ItemStack pStack, Level pLevel, List<Component> pTooltipComponents, TooltipFlag pIsAdvanced, Operation<Void> original, @Nullable Player pPlayer, TooltipFlag originalParam){
        if(this.getItem() instanceof BloodFillableItem){
            original.call(instance, pStack, pLevel, pTooltipComponents, TooltipFlagWrapper.getOrCreateWrapper(pIsAdvanced, pPlayer != null && pPlayer.isCreative()));
        } else{
            original.call(instance, pStack, pLevel, pTooltipComponents, pIsAdvanced);
        }
    }
}
