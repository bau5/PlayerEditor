package bau5.mods.playereditor.common.packets;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.Packet250CustomPayload;
import bau5.mods.playereditor.client.GuiPlayers;
import bau5.mods.playereditor.common.PEServerConfigManager;
import bau5.mods.playereditor.common.PlayerEditor;
import bau5.mods.projectbench.common.ProjectBench;

import com.google.common.io.ByteArrayDataInput;

import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.common.network.Player;


public class PlayerListRequestPacket extends PEPacket{
	public static byte PACKET_ID = 0;
	
	public PlayerListRequestPacket() { super(PACKET_ID); }
	
	@Override
	public Packet makePacket(){
		if(PEServerConfigManager.instance != null){
			String[] players = PEServerConfigManager.instance.getAvailablePlayerDat();
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			DataOutputStream dos = new DataOutputStream(bos);
			try{
				dos.writeByte(PACKET_ID);
				dos.writeByte(players.length);
				for(String str : players)
					dos.writeUTF(str);
			}catch(Exception ex){
				ex.printStackTrace();
			}
			return buildPacket(bos.toByteArray());
		}else{
			return super.makePacket();
		}
	}
	@Override
	public void handlePacket(Packet250CustomPayload packet, Player player, ByteArrayDataInput bis) {
		EntityPlayer pl = (EntityPlayer)player;
		pl.openGui(PlayerEditor.instance, 0, pl.worldObj, (int)pl.posX, (int)pl.posY, (int)pl.posZ);
		if(FMLClientHandler.instance().getClient().currentScreen instanceof GuiPlayers){
			GuiPlayers gui = (GuiPlayers)FMLClientHandler.instance().getClient().currentScreen;
			byte num = bis.readByte();
			String[] players = new String[num];
			for(int i = 0; i < num; i++){
				players[i] = bis.readUTF();
			}
			gui.addAvailablePlayers(players);
		}
	}
}
