package bau5.mods.playereditor.common.packets;

import net.minecraft.network.INetworkManager;
import net.minecraft.network.packet.Packet250CustomPayload;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;

import cpw.mods.fml.common.network.IPacketHandler;
import cpw.mods.fml.common.network.Player;

public class PEPacketHandler implements IPacketHandler{
	public static String PACKET_CHANNEL = "bau5_pledit";
	@Override
	public void onPacketData(INetworkManager manager,
			Packet250CustomPayload packet, Player player) {
		if(packet.data == null)
			return;
		ByteArrayDataInput bis = ByteStreams.newDataInput(packet.data);
		byte id = bis.readByte();
		switch(id){
		case 0: PEPacketManager.handlePlayerListPacket(packet, player, bis);
			return;
		case 1: PEPacketManager.handlePlayerEditPacket(packet, player, bis);
			return;
		}
	}
}
