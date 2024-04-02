package me.majhrs16.dst.utils;

import me.majhrs16.lib.minecraft.BukkitUtils;
import org.bukkit.Bukkit;

import java.util.UUID;

@Deprecated
public class FakePlayer {
	private static final Class<?> playerInteractManegerClass;
	private static final Class<?> entityPlayerClass;
	private static final Class<?> gameProfileClass;

	static {
		try {
			Class<?> craftPlayerClass = BukkitUtils.getOnlinePlayers()[0].getClass();
			entityPlayerClass = craftPlayerClass.getMethod("getHandle").getReturnType();
			playerInteractManegerClass = entityPlayerClass.getDeclaredField("playerInteractManager").getType();
			gameProfileClass = entityPlayerClass.getDeclaredField("profile").getType();

		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public static UUID create(String name) {
		try {
			Object server = Bukkit.getServer().getClass().getMethod("getServer").invoke(Bukkit.getServer());
			Object world = server.getClass().getMethod("getWorldServer", int.class).invoke(server, 0);
			Object gameProfile = gameProfileClass.getConstructor(UUID.class, String.class).newInstance(UUID.randomUUID(), name);
			Object playerInteractManager = playerInteractManegerClass.getConstructor(world.getClass()).newInstance(world);
			Object fakePlayer = entityPlayerClass.getConstructor(
				server.getClass(),
				world.getClass(),
				gameProfile.getClass(),
				playerInteractManager.getClass()
			).newInstance(server, world, gameProfile, playerInteractManager);

			return (UUID) fakePlayer.getClass().getMethod("getUniqueID").invoke(fakePlayer);

		} catch (Exception e) {
			return null;
		}
	}
}