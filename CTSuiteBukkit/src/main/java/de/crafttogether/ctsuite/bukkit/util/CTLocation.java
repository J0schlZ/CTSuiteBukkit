package de.crafttogether.ctsuite.bukkit.util;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

public class CTLocation {
	private Double x = null;
	private Double y = null;
	private Double z = null;
	private String world = null;
	private String server = null;
	private Float yaw = null;
	private Float pitch = null;

	public CTLocation() {

	}
	
	public CTLocation(Location loc) {
		this.x = loc.getX();
		this.y = loc.getY();
		this.z = loc.getZ();
		this.world = loc.getWorld().getName();
		this.yaw = loc.getYaw();
		this.pitch = loc.getPitch();
	}
	
	public CTLocation(Location loc, String server) {
		this.x = loc.getX();
		this.y = loc.getY();
		this.z = loc.getZ();
		this.world = loc.getWorld().getName();
		this.yaw = loc.getYaw();
		this.pitch = loc.getPitch();
		this.server = server;
	}
	
	public CTLocation(Location loc, String world, String server) {
		this.x = loc.getX();
		this.y = loc.getY();
		this.z = loc.getZ();
		this.world = world;
		this.yaw = loc.getYaw();
		this.pitch = loc.getPitch();
		this.server = server;
	}
	
	public CTLocation(Double x, Double y, Double z, String world, String server, float yaw, float pitch) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.world = world;
		this.server = server;
		this.yaw = yaw;
		this.pitch = pitch;
	}

	public void setX(Double x) {
		this.x = x;
	}
	
	public void setY(Double y) {
		this.y = y;
	}
	
	public void setZ(Double z) {
		this.z = z;
	}
	
	public void setWorld(String world) {
		this.world = world;
	}
	
	public void setServer(String server) {
		this.server = server;
	}
	
	public void setYaw(Float yaw) {
		this.yaw = yaw;
	}
	
	public void setPitch(Float pitch) {
		this.pitch = pitch;
	}
	
	public Double getX() {
		return this.x;
	}
	
	public Double getY() {
		return this.y;
	}
	
	public Double getZ() {
		return this.z;
	}
	
	public String getWorld() {
		return this.world;
	}
	
	public String getServer() {
		return this.server;
	}
	
	public Float getYaw() {
		return this.yaw;
	}
	
	public Float getPitch() {
		return this.pitch;
	}
	
	public Location getLocation() {
		return CTLocation.getLocation(this);
	}
	
	public static Location getLocation(CTLocation ctLoc) {
		World world = null;
		
		if (!Bukkit.getServer().getName().equalsIgnoreCase(ctLoc.getServer()))
			return null;
		
		world = Bukkit.getServer().getWorld(ctLoc.getWorld());
		
		if (world == null)
			return null;
		
		return new Location(world, ctLoc.getX(), ctLoc.getY(), ctLoc.getZ(), ctLoc.getYaw(), ctLoc.getPitch());
	}
	
	public String toString() {
		return CTLocation.toString(this);
	}
	
	public static CTLocation fromString(String str) {		
		String[] data = str.split(":");
		Double x = Math.round(Double.parseDouble(data[0]) *100) / 100.0;
		Double y = Math.round(Double.parseDouble(data[1]) *100) / 100.0;
		Double z = Math.round(Double.parseDouble(data[2]) *100) / 100.0;
		String world = data[3];
		String server = data[4];
		Float yaw = (float) (Math.round(Float.parseFloat(data[5]) *100) / 100.0);
		Float pitch = (float) (Math.round(Float.parseFloat(data[6]) *100) / 100.0);
		
		return new CTLocation(x, y, z, world, server, yaw, pitch);
	}
	
	public static String toString(CTLocation loc) {
		return loc.getX() + ":" + loc.getY() + ":" + loc.getZ() + ":" + loc.getWorld() + ":" + loc.getServer() + ":" + loc.getYaw() + ":" + loc.getPitch();
	}
}

