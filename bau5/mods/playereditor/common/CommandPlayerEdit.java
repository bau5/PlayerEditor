package bau5.mods.playereditor.common;

import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.ContainerChest;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.packet.Packet100OpenWindow;
import net.minecraft.server.management.LowerStringMap;
import bau5.mods.playereditor.common.packets.PEPacketManager;

public class CommandPlayerEdit extends CommandBase {
	
	public class PlayerInventory extends ContainerChest{
		private NBTTagCompound playerTag;
		private String playerName;
		public PlayerInventory(IInventory base, IInventory remote, NBTTagCompound playerTag, String name){
			super(base, remote);
			this.playerTag = playerTag;
			this.playerName = name;
		}
		
		@Override
		public boolean canInteractWith(EntityPlayer par1EntityPlayer) 
		{
			return true;
		}
		@Override
		public void onCraftGuiClosed(EntityPlayer par1EntityPlayer) {
			PEServerConfigManager.instance.writePlayerData(PEServerConfigManager.instance.updatePlayerData(playerTag, this.getLowerChestInventory()), playerName);
			super.onCraftGuiClosed(par1EntityPlayer);
			if(par1EntityPlayer instanceof EntityPlayerMP)
				((EntityPlayerMP)par1EntityPlayer).playerNetServerHandler.sendPacketToPlayer(PEPacketManager.getPlayerListPacket());
		}
	}
	
	public String getCommandName() 
	{
		return "pl";
	}
	@Override
	public void processCommand(ICommandSender sender, String[] str) 
	{
		if(str.length == 0 || str.length > 2){
			throw new WrongUsageException(getCommandUsage(sender), new Object[0]);
		}
		EntityPlayerMP thePlayer = getCommandSenderAsPlayer(sender);
		if(str[0].equals("list")){
			thePlayer.playerNetServerHandler.sendPacketToPlayer(PEPacketManager.getPlayerListPacket());
		}else if(str[0].equals("edit")){
			if(str.length == 1)
				throw new WrongUsageException(getCommandUsage(sender), new Object[0]);
			String userName = str[1];
			boolean present = PEServerConfigManager.instance.doesPlayerExist(userName);
			if(present){
				EntityPlayerMP otherPlayer = PEServerConfigManager.instance.getPlayerForUsername(userName);
				if(otherPlayer != null){
					new CommandPeek().processCommand(thePlayer, new String[]{ otherPlayer.username, "true"});
				}else{
					NBTTagCompound tag = PEServerConfigManager.instance.readPlayerDataFromFile(userName, thePlayer);
					if(tag != null && tag.hasKey("Inventory")){
						NBTTagList invTag = tag.getTagList("Inventory");
						thePlayer.incrementWindowID();
				        thePlayer.playerNetServerHandler.sendPacketToPlayer(new Packet100OpenWindow(thePlayer.currentWindowId, 0, userName, thePlayer.inventory.getSizeInventory(), true));
				        thePlayer.openContainer = new PlayerInventory(thePlayer.inventory, makeIInventory(invTag, userName, thePlayer), tag, userName);
				        thePlayer.openContainer.windowId = thePlayer.currentWindowId;
				        thePlayer.openContainer.addCraftingToCrafters(thePlayer);
				        notifyAdmins(sender,"\u00A7c"  +thePlayer.getEntityName() +" \u00A78accessed \u00A7c"+userName+"'s\u00A78 inventory.", new Object[]{ thePlayer.getEntityName(), userName});		
					}
				}
			}else
				thePlayer.sendChatToPlayer("User with name " +userName +" does not exist.");
		}
	}
	
	private IInventory makeIInventory(NBTTagList invTag, String user, EntityPlayerMP model) {
		IInventory theInventory = new InventoryBasic(user, true, model.inventory.getSizeInventory());
		for(int i = 0; i < invTag.tagCount(); i++){
			NBTBase tag = invTag.tagAt(i);
			if(tag instanceof NBTTagCompound){
				NBTTagCompound theTag = (NBTTagCompound)tag;
				theInventory.setInventorySlotContents(theTag.getByte("Slot"), new ItemStack(theTag.getShort("id"), theTag.getByte("Count"), theTag.getShort("Damage")));
			}
		}
		return theInventory;
	}
	@Override
    public int getRequiredPermissionLevel()
    {
        return 4;
    }
	public String getCommandUsage(ICommandSender par1ICommandSender)
    {
        return "/" + this.getCommandName() + " <list, edit [playerName]>";
    }
}
