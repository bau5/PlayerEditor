package bau5.mods.playereditor.common.packets;

import com.google.common.io.ByteArrayDataInput;

import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.Packet250CustomPayload;
import cpw.mods.fml.common.network.Player;

public class PEPacketManager {
	
	public static Packet getPlayerListPacket(){
		return new PlayerListRequestPacket().makePacket();
	}
	
	public static void handlePlayerListPacket(Packet250CustomPayload packet, Player player, ByteArrayDataInput bis){
		new PlayerListRequestPacket().handlePacket(packet, player, bis);
	}

	public static Packet getPlayerEditRequestPacket(String playerToEdit) {
		return new PlayerEditRequestPacket(playerToEdit).makePacket();
	}
	
	public static void handlePlayerEditPacket(Packet250CustomPayload packet, Player player, ByteArrayDataInput bis){
		new PlayerEditRequestPacket().handlePacket(packet, player, bis);
	}
}
