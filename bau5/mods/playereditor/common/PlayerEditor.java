package bau5.mods.playereditor.common;

import java.util.logging.Level;

import net.minecraft.command.ServerCommandManager;
import net.minecraftforge.common.Configuration;
import bau5.mods.playereditor.common.packets.PEPacketHandler;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.Init;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.Mod.PreInit;
import cpw.mods.fml.common.Mod.ServerStarting;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import cpw.mods.fml.common.network.NetworkMod;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.relauncher.Side;

@Mod (modid = "bau5_PlayerEditor", name="Player Editor", version="0.1"	)
@NetworkMod(clientSideRequired = false, serverSideRequired = false,
	channels = {"bau5_pledit"}, packetHandler = PEPacketHandler.class)
public class PlayerEditor {
	@Instance("bau5_PlayerEditor")
	public static PlayerEditor instance;
	@SidedProxy(clientSide = "bau5.mods.playereditor.common.CommonProxy",
				serverSide = "bau5.mods.playereditor.common.CommonProxy")
	public static CommonProxy proxy;
	public String COMMAND_NAME = "pl";
	
	@PreInit
	public void preInit(FMLPreInitializationEvent ev){
		Configuration config = new Configuration(ev.getSuggestedConfigurationFile());
		try{
			config.load();
			COMMAND_NAME = config.get(Configuration.CATEGORY_GENERAL, "Command name", "pl").getString();
			if(COMMAND_NAME.equals("")){
				System.out.println("Bad command name, defaulting to pledit.");
				COMMAND_NAME = "pl";
			}
		}catch(Exception ex){
			FMLLog.log(Level.SEVERE, ex, "Player Editor: Error encountered while loading config file.");
		}finally{
			config.save();	
		}
	}
	@Init
	public void init(FMLInitializationEvent ev){
		NetworkRegistry.instance().registerGuiHandler(this, proxy);
		if(FMLCommonHandler.instance().getSide() == Side.CLIENT){
			System.out.println("Client");
		}else if(FMLCommonHandler.instance().getSide() == Side.SERVER){
			System.out.println("Server");
			PEServerConfigManager.instance = new PEServerConfigManager();
		}
	}
	@ServerStarting
	public void serverStarting(FMLServerStartingEvent ev){
		ServerCommandManager serverCommandManager = (ServerCommandManager)ev.getServer().getCommandManager();
		serverCommandManager.registerCommand(new CommandPeek());
		serverCommandManager.registerCommand(new CommandPlayerEdit());
	}
}
