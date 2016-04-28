package com.walrusone.skywars.nms.v1_9_R1;

import com.walrusone.skywars.nms.NMS;
import net.minecraft.server.v1_9_R1.*;
import org.bukkit.*;
import org.bukkit.Chunk;
import org.bukkit.FireworkEffect.Type;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_9_R1.CraftChunk;
import org.bukkit.craftbukkit.v1_9_R1.CraftServer;
import org.bukkit.craftbukkit.v1_9_R1.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_9_R1.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;


public class NMSHandler implements NMS {
	public void respawnPlayer(Player player) {
		((CraftServer)Bukkit.getServer()).getHandle().moveToWorld(((CraftPlayer)player).getHandle(), 0, false);
	}
	
	public void updateChunks(World world, List<Chunk> chunks) {
		for (Chunk currentChunk: chunks) {
			net.minecraft.server.v1_9_R1.World mcWorld = ((CraftChunk) currentChunk).getHandle().world;
	        for (EntityHuman eh : (List<EntityHuman>) mcWorld.players) {
	        	EntityPlayer ep = (EntityPlayer) eh;
	          //  ep.chunkCoordIntPairQueue.add(new ChunkCoordIntPair(currentChunk.getX(), currentChunk.getZ()));
			//	world.refreshChunk(currentChunk.getX(), currentChunk.getZ()); //welp replacing a removed method by a deprecated one. Welp welp welp.
				PacketMapChunk.refreshChunk(currentChunk);
	        }
		}
	}
	
	public void sendParticles(World world, String type, float x, float y, float z, float offsetX, float offsetY, float offsetZ, float data, int amount) {
		EnumParticle particle = EnumParticle.valueOf(type);
		PacketPlayOutWorldParticles particles = new PacketPlayOutWorldParticles(particle, false, x, y, z, offsetX, offsetY, offsetZ, data, amount, 1);
		for (Player player: world.getPlayers()) {
			CraftPlayer start = (CraftPlayer) player; //Replace player with your player.
			EntityPlayer target = start.getHandle();
			PlayerConnection connect = target.playerConnection;
			connect.sendPacket(particles);
		}
	}
	
	public String getName(ItemStack stack) {
		return CraftItemStack.asNMSCopy(stack).getName();
	}
	
	public FireworkEffect getFireworkEffect(Color one, Color two, Color three, Color four, Color five, Type type) {
		return FireworkEffect.builder().flicker(false).withColor(one, two, three, four).withFade(five).with(type).trail(true).build();
	}
	
	public void sendTitle(Player player, int fadein, int stay, int fadeout, String title, String subtitle) {
		PlayerConnection pConn = ((CraftPlayer) player).getHandle().playerConnection;
		PacketPlayOutTitle pTitleInfo = new PacketPlayOutTitle(PacketPlayOutTitle.EnumTitleAction.TIMES, (IChatBaseComponent) null, (int) fadein, (int) stay, (int) fadeout);
		pConn.sendPacket(pTitleInfo);
		if (subtitle != null) {
			subtitle = subtitle.replaceAll("%player%", player.getDisplayName());
			subtitle = ChatColor.translateAlternateColorCodes('&', subtitle);
			IChatBaseComponent iComp = IChatBaseComponent.ChatSerializer.a("{\"text\": \"" + subtitle + "\"}");
			PacketPlayOutTitle pSubtitle = new PacketPlayOutTitle(PacketPlayOutTitle.EnumTitleAction.SUBTITLE, iComp);
			pConn.sendPacket(pSubtitle);
		}
		if (title != null) {
			title = title.replaceAll("%player%", player.getDisplayName());
			title = ChatColor.translateAlternateColorCodes('&', title);
			IChatBaseComponent iComp = IChatBaseComponent.ChatSerializer.a("{\"text\": \"" + title + "\"}");
			PacketPlayOutTitle pTitle = new PacketPlayOutTitle(PacketPlayOutTitle.EnumTitleAction.TITLE, iComp);
			pConn.sendPacket(pTitle);
		}
	}
	
	public boolean isOnePointSeven() {
		return false;
	} 
	
}
