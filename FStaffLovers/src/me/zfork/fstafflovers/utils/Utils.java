package me.zfork.fstafflovers.utils;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

import org.bukkit.Bukkit;
import org.bukkit.inventory.ItemStack;

public class Utils {
	
	private static final String version = Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3];
	
	public static ItemStack setNBTTag(ItemStack item){
		try {
			Class<?> ItemStack = Class.forName("net.minecraft.server." + version + ".ItemStack");
			Class<?> NBTTagCompound = Class.forName("net.minecraft.server." + version + ".NBTTagCompound");
			Class<?> NBTTagList = Class.forName("net.minecraft.server." + version + ".NBTTagList");
			Class<?> CraftItemStack = Class.forName("org.bukkit.craftbukkit." + version + ".inventory.CraftItemStack");
			Class<?> NBTBase = Class.forName("net.minecraft.server." + version + ".NBTBase");
			
			Method asNMSCopy = CraftItemStack.getDeclaredMethod("asNMSCopy", org.bukkit.inventory.ItemStack.class);
			Method asCraftMirror = CraftItemStack.getDeclaredMethod("asCraftMirror", ItemStack);
			Method hasTag = ItemStack.getDeclaredMethod("hasTag");
			Method setTag = ItemStack.getDeclaredMethod("setTag", NBTTagCompound);
			Method getTag = ItemStack.getDeclaredMethod("getTag");
			Method hasKey = NBTTagCompound.getDeclaredMethod("hasKey", String.class);
			Method set = NBTTagCompound.getDeclaredMethod("set", String.class, NBTBase);
			Method remove = NBTTagCompound.getDeclaredMethod("remove", String.class);

			asNMSCopy.setAccessible(true);
			asCraftMirror.setAccessible(true);
			hasTag.setAccessible(true);
			setTag.setAccessible(true);
			getTag.setAccessible(true);
			set.setAccessible(true);
			Constructor<?> NBTTagCompoundConstructor = NBTTagCompound.getConstructor();
			Constructor<?> NBTTagListConstructor = NBTTagList.getConstructor();
			NBTTagCompoundConstructor.setAccessible(true);
			NBTTagListConstructor.setAccessible(true);
			Object nmsStack = asNMSCopy.invoke(null, item);
			Object tag = null;
			if((Boolean) hasTag.invoke(nmsStack))
				tag = getTag.invoke(nmsStack);
			else
				tag = NBTTagCompoundConstructor.newInstance();
			if((Boolean) hasKey.invoke(tag, "ench")){
				remove.invoke(tag, "ench");
			}else{
				Object ench = NBTTagListConstructor.newInstance();
				set.invoke(tag, "ench", ench);
			}
			setTag.invoke(nmsStack, tag);
			return (ItemStack) asCraftMirror.invoke(null, nmsStack);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

}
