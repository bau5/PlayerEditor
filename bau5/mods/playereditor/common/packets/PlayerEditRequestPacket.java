package bau5.mods.playereditor.common.packets;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;

import net.minecraft.command.ICommandSender;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.Packet250CustomPayload;

import bau5.mods.playereditor.common.CommandPlayerEdit;

import com.google.common.io.ByteArrayDataInput;

import cpw.mods.fml.common.network.Player;

public class PlayerEditRequestPacket extends PEPacket {
	
	public static byte PACKET_ID = 1;
	private String playerToEdit;
	
	public PlayerEditRequestPacket() {}
	
	public PlayerEditRequestPacket(String plyr) {
		super(PACKET_ID);
		playerToEdit = plyr;
	}
	
	@Override
	public Packet makePacket() {
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		DataOutputStream dos = new DataOutputStream(bos);
		try{
			dos.writeByte(PACKET_ID);
			dos.writeUTF(playerToEdit);
		}catch(Exception ex){
			ex.printStackTrace();
		}
		if(bos.size() > 0)
			return buildPacket(bos.toByteArray());
		else
			return super.makePacket();
	}
	@Override
	public void handlePacket(Packet250CustomPayload packet, Player player,
			ByteArrayDataInput bis) {
		new CommandPlayerEdit().processCommand((ICommandSender) player, new String[] {"edit", bis.readUTF()});
	}

}
