package bau5.mods.playereditor.common.packets;

import java.io.ByteArrayOutputStream;

import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.Packet250CustomPayload;
import bau5.mods.projectbench.common.packets.PBPacketHandler;

import com.google.common.io.ByteArrayDataInput;

import cpw.mods.fml.common.network.Player;

public class PEPacket extends Packet250CustomPayload{
	public byte PACKET_ID = 127;
	public PEPacket() {}
	public PEPacket(byte id){
		channel = PEPacketHandler.PACKET_CHANNEL;
		PACKET_ID = id;
	}
	public PEPacket(byte[] bs) {
		super(PBPacketHandler.PACKET_CHANNEL, bs);
	}
	public void handlePacket(Packet250CustomPayload packet, Player player, ByteArrayDataInput bis) {
		System.out.println("ERROR: Incorrect usage of PBPacket reading.");
	}
	public Packet makePacket(){
		System.out.println("ERROR: Incorrect usage of PBPacket writing.");
		return new Packet250CustomPayload(channel, new byte[]{0});
	}
	public Packet buildPacket(byte[] bytes){
		Packet250CustomPayload packet = new Packet250CustomPayload();
		packet.channel = channel;
		packet.data = bytes;
		packet.length = bytes.length;
		packet.isChunkDataPacket = false;
		return packet;
	}
}
