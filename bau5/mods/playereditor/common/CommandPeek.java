package bau5.mods.playereditor.common;

import cpw.mods.fml.server.FMLServerHandler;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.ContainerChest;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.Packet100OpenWindow;

public class CommandPeek extends CommandBase {

	public class RemoteInventory extends ContainerChest{
		boolean editMode = false;
		public RemoteInventory(IInventory base, IInventory remote, boolean editMode){
			super(base, remote);
			this.editMode = editMode;
		}
		
		@Override
		public ItemStack transferStackInSlot(EntityPlayer player, int par2)
	    {
			return editMode ? super.transferStackInSlot(player, par2) : null;
	    }
		@Override
		public ItemStack slotClick(int par1, int par2, int par3, EntityPlayer par4EntityPlayer)
		{
			return editMode ? super.slotClick(par1, par2, par3, par4EntityPlayer) : null;
		}
		@Override
		public boolean canInteractWith(EntityPlayer par1EntityPlayer) 
		{
			return true;
		}
	}
	@Override
	public String getCommandName() 
	{
		return "peek";
	}

	@Override
	public void processCommand(ICommandSender sender, String[] str) 
	{
		if(str.length == 0 || str.length > 2){
			throw new WrongUsageException(getCommandUsage(sender), new Object[0]);
		}
		EntityPlayerMP source = getCommandSenderAsPlayer(sender);
		EntityPlayerMP target = func_82359_c(sender, str[0]);
		boolean mode = (str.length == 2) ? mode = ((str[1].equals("true")) ? true : false) : false;
		if(target == null)
			return;
        source.incrementWindowID();
        source.playerNetServerHandler.sendPacketToPlayer(new Packet100OpenWindow(source.currentWindowId, 0, target.getEntityName(), target.inventory.getSizeInventory(), true));
        source.openContainer = new RemoteInventory(source.inventory, target.inventory, mode);
        source.openContainer.windowId = source.currentWindowId;
        source.openContainer.addCraftingToCrafters(source);
		notifyAdmins(sender,"\u00A7c"  +source.getEntityName() +" \u00A78peeked at \u00A7c" +target.getEntityName() +" \u00A78" +((mode) ? "in edit mode.": "just to check in."), new Object[]{ target.getEntityName(), mode});		
	}
	
	@Override
    public int getRequiredPermissionLevel()
    {
        return 4;
    }
	public String getCommandUsage(ICommandSender par1ICommandSender)
    {
        return "/" + this.getCommandName() + " <player> [mode]";
    }

}
