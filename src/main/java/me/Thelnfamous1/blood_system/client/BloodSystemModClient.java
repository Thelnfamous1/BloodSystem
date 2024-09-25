package me.Thelnfamous1.blood_system.client;

import me.Thelnfamous1.blood_system.common.network.BloodSystemNetwork;
import me.Thelnfamous1.blood_system.common.network.ServerboundRequestBloodSync;
import net.minecraftforge.client.event.ClientPlayerNetworkEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class BloodSystemModClient {

    @SubscribeEvent
    public void onClientLogin(ClientPlayerNetworkEvent.LoggingIn event){
        BloodSystemNetwork.SYNC_CHANNEL.sendToServer(new ServerboundRequestBloodSync());
    }
}
