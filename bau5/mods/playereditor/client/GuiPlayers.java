package bau5.mods.playereditor.client;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import bau5.mods.playereditor.common.CommandPlayerEdit;
import bau5.mods.playereditor.common.packets.PEPacketManager;

import com.google.common.util.concurrent.SettableFuture;

import cpw.mods.fml.common.network.PacketDispatcher;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.world.World;

public class GuiPlayers extends GuiScreen{
	private EntityPlayer currentActive;
	private World worldObj;
	private String[] thePlayers = null;
	
	private int xSize = 0;
	private int ySize = 0;
	
	private int guiStartLeft = 0;
	private int guiStopRight = 0;
	private int guiStartTop  = 0;
	private int guiStopBottom = 0;
	private int boxHeight = 12;
	
	private int scrollPos = 0;
	
	
	public GuiPlayers(EntityPlayer player, World world) {
		currentActive = player;
		worldObj = world;
	}
	@Override
	public void drawScreen(int par1, int par2, float par3) {
		if(thePlayers == null)
			return;
		if(guiStartLeft == 0 && guiStartTop == 0 && this.width != 0 && this.height != 0){
			guiStartLeft = width/4;
			guiStopRight  = this.width - width/4 + 16;
			guiStartTop = height/4;
			guiStopBottom = guiStartTop+((10)*boxHeight) + 2;
		}
		if(scrollPos < 0)
			scrollPos = 0;
		if(scrollPos > thePlayers.length-10 && scrollPos > 0)
			scrollPos -= 1;
			
		drawRect(guiStartLeft, guiStartTop, guiStopRight, guiStopBottom, -804253680);
		drawPlayersGui(par1, par2);
	}
	
	private void drawPlayersGui(int par1, int par2) {
		FontRenderer fontrenderer = this.mc.fontRenderer;
		int lngth = thePlayers.length;
		if(lngth > 10){
			lngth = 10;
		}
		for(int i = 0; i < lngth; i++){
			int startLeft = guiStartLeft+2;
			int startTop  = guiStartTop+2+(i*boxHeight);
			int stopRight = this.width - width/4 - 2;
			int stopBottom= guiStartTop+((i+1)*boxHeight);
			boolean mouseOver = false;
			if(par1 >= startLeft && par2 >= startTop && par1 <= stopRight && par2 <= stopBottom)
				mouseOver = true;
			drawRect(startLeft, startTop, stopRight, stopBottom, (mouseOver ? 999999999 :553648127));
			if(i+scrollPos >= thePlayers.length || i+scrollPos < 0){
				mc.thePlayer.addChatMessage(EnumChatFormatting.RED +"Encountered an error while displaying players.");
				return;
			}
			fontrenderer.drawStringWithShadow(thePlayers[i + scrollPos], guiStartLeft+4, guiStartTop+4+(i*boxHeight), 16777215);
		}
		
	}
	@Override
	public void handleKeyboardInput() {
		if(Keyboard.getEventKeyState()){
            int i = Keyboard.getEventKey();
            if(i == 1){
                 this.mc.displayGuiScreen((GuiScreen)null);
                 this.mc.setIngameFocus();
            }
            if(i == 208){
            	scrollPos += 1;
            }
            if(i == 200){
            	scrollPos -= 1;
            }
		}
	}
	public void addAvailablePlayers(String[] players) {
		thePlayers = players;
	}
	@Override
	protected void mouseClicked(int x, int y, int clickType) {
		super.mouseClicked(x, y, clickType);
		if(x>guiStartLeft && x < guiStopRight && y > guiStartTop && y < guiStopBottom){
			int yLoc = y - guiStartTop;
			int index = yLoc/12 +scrollPos;
			handlePlayerNameClicked(index);
		}
	}
	private void handlePlayerNameClicked(int index) {
		PacketDispatcher.sendPacketToServer(PEPacketManager.getPlayerEditRequestPacket(thePlayers[index]));
	}
	
    public static void drawPlayerOnGui(Minecraft par0Minecraft, int par1, int par2, int par3, float par4, float par5)
    {
        GL11.glEnable(GL11.GL_COLOR_MATERIAL);
        GL11.glPushMatrix();
        GL11.glTranslatef((float)par1, (float)par2, 50.0F);
        GL11.glScalef((float)(-par3), (float)par3, (float)par3);
        GL11.glRotatef(180.0F, 0.0F, 0.0F, 1.0F);
        float f2 = par0Minecraft.thePlayer.renderYawOffset;
        float f3 = par0Minecraft.thePlayer.rotationYaw;
        float f4 = par0Minecraft.thePlayer.rotationPitch;
        GL11.glRotatef(135.0F, 0.0F, 1.0F, 0.0F);
        RenderHelper.enableStandardItemLighting();
        GL11.glRotatef(-135.0F, 0.0F, 1.0F, 0.0F);
        GL11.glRotatef(-((float)Math.atan((double)(par5 / 40.0F))) * 20.0F, 1.0F, 0.0F, 0.0F);
        par0Minecraft.thePlayer.renderYawOffset = (float)Math.atan((double)(par4 / 40.0F)) * 20.0F;
        par0Minecraft.thePlayer.rotationYaw = (float)Math.atan((double)(par4 / 40.0F)) * 40.0F;
        par0Minecraft.thePlayer.rotationPitch = -((float)Math.atan((double)(par5 / 40.0F))) * 20.0F;
        par0Minecraft.thePlayer.rotationYawHead = par0Minecraft.thePlayer.rotationYaw;
        GL11.glTranslatef(0.0F, par0Minecraft.thePlayer.yOffset, 0.0F);
        RenderManager.instance.playerViewY = 180.0F;
        RenderManager.instance.renderEntityWithPosYaw(par0Minecraft.thePlayer, 0.0D, 0.0D, 0.0D, 0.0F, 1.0F);
        par0Minecraft.thePlayer.renderYawOffset = f2;
        par0Minecraft.thePlayer.rotationYaw = f3;
        par0Minecraft.thePlayer.rotationPitch = f4;
        GL11.glPopMatrix();
        RenderHelper.disableStandardItemLighting();
        GL11.glDisable(GL12.GL_RESCALE_NORMAL);
        OpenGlHelper.setActiveTexture(OpenGlHelper.lightmapTexUnit);
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        OpenGlHelper.setActiveTexture(OpenGlHelper.defaultTexUnit);
    }
}
