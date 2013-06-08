package bau5.mods.playereditor.common;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Iterator;
import java.util.List;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemInWorldManager;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.management.ServerConfigurationManager;
import net.minecraft.world.chunk.storage.AnvilSaveHandler;
import net.minecraft.world.storage.SaveHandler;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.relauncher.ReflectionHelper;
import cpw.mods.fml.server.FMLServerHandler;

public class PEServerConfigManager extends ServerConfigurationManager{
	public static PEServerConfigManager instance;
	public PEServerConfigManager(){
		super(FMLCommonHandler.instance().getMinecraftServerInstance());
	}
	
	@Override
	public String[] getAvailablePlayerDat() {
		return super.getAvailablePlayerDat();
	}
	@Override
	public EntityPlayerMP getPlayerForUsername(String par1Str) {
		List ls = FMLServerHandler.instance().getServer().getConfigurationManager().playerEntityList;
		Iterator iterator = ls.iterator();
        EntityPlayerMP entityplayermp;

        do
        {
            if (!iterator.hasNext())
            {
                return null;
            }

            entityplayermp = (EntityPlayerMP)iterator.next();
        }
        while (!entityplayermp.username.equalsIgnoreCase(par1Str));

        return entityplayermp;
	}
	public NBTTagCompound readPlayerDataFromFile(String username, EntityPlayerMP model) {
		EntityPlayerMP playerMP = new EntityPlayerMP(model.mcServer, model.worldObj, username, new ItemInWorldManager(model.worldObj));
		return FMLServerHandler.instance().getServer().getConfigurationManager().readPlayerDataFromFile(playerMP);
	}
	
	public void writePlayerData(NBTTagCompound playerCompound, String playerName){
		if(getPlayerForUsername(playerName) != null){
			getPlayerForUsername(playerName).readEntityFromNBT(playerCompound);
		}
		try
        {
			Object obj = ReflectionHelper.getPrivateValue(ServerConfigurationManager.class, FMLServerHandler.instance().getServer().getConfigurationManager(), "playerNBTManagerObj");
			if(obj instanceof SaveHandler){
				SaveHandler theSaver = (SaveHandler)obj;
				Object obj2 = ReflectionHelper.getPrivateValue(SaveHandler.class, theSaver, "playersDirectory");
				if(obj2 != null && obj2 instanceof File){
					File playersDirectory = (File)obj2;
		            File file1 = new File(playersDirectory, playerName + ".dat.tmp");
		            File file2 = new File(playersDirectory, playerName + ".dat");
		            CompressedStreamTools.writeCompressed(playerCompound, new FileOutputStream(file1));
		            if (file2.exists())
		            {
		                file2.delete();
		            }

		            file1.renameTo(file2);
				}
			}
			

        }
        catch (Exception exception)
        {
            MinecraftServer.getServer().getLogAgent().logWarning("Failed to save player data for " + playerName);
        }
	}
	public boolean doesPlayerExist(String name){
		String[] plyrs = getAvailablePlayerDat();
		for(String plyr : plyrs)
			if(plyr.equalsIgnoreCase(name))
				return true;
		return false;
	}

	public NBTTagCompound updatePlayerData(NBTTagCompound playerTag, IInventory playerInventory) {
		NBTTagList list = new NBTTagList("Inventory");
		int i;
        NBTTagCompound nbttagcompound;

        for (i = 0; i < playerInventory.getSizeInventory(); ++i)
        {
            if (playerInventory.getStackInSlot(i) != null)
            {
                nbttagcompound = new NBTTagCompound();
                nbttagcompound.setByte("Slot", (byte)i);
                playerInventory.getStackInSlot(i).writeToNBT(nbttagcompound);
                list.appendTag(nbttagcompound);
            }
        }
		playerTag.setTag("Inventory", list);
		return playerTag;
	}
}
