package me.majhrs16.dst.events;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.permissions.PermissionAttachment;
import org.bukkit.permissions.Permission;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.Bukkit;

import me.majhrs16.dst.utils.AccountManager;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.JDA;

import me.majhrs16.dst.DiscordTranslator;
import me.majhrs16.cht.ChatTranslator;
import me.majhrs16.cht.util.util;

import java.util.*;

public class DiscordSync {
	private Timer timer;
	private final ChatTranslator plugin = ChatTranslator.getInstance();
	private final Map<Player, Map<String, PermissionAttachment>> map_permissions = new HashMap<>();

	private class Clock extends TimerTask {
		boolean is_one = true;

		@Override
		public void run() {
			if (is_one) permissionSyncTask();
			else roleSyncTask();

			is_one = !is_one;
		}
	}

	public void start() {
		timer = new Timer(true);
		timer.scheduleAtFixedRate(new Clock(), 0, 5000);
	}

	public void stop() {
		if (timer != null) timer.cancel();
	}

	public void roleSyncTask() {
		JDA jda = DiscordTranslator.getJDA();
		if (jda == null) return;

		for (Guild guild : jda.getGuilds()) {
			Collection<User> users = new HashSet<>();
			for (Member member : guild.getMembers())
				users.add(member.getUser());

			for (Member member : guild.retrieveMembers(true, users).get()) {
				UUID player_uuid = AccountManager.getMinecraft(member.getUser().getId());

				if (player_uuid == null)
					continue;

				OfflinePlayer off_player = AccountManager.getOfflinePlayer(player_uuid);

				if (off_player == null || !off_player.isOnline())
					continue;

				Player player = (Player) off_player;

				ConfigurationSection section = plugin.config.get().getConfigurationSection("discord.sync.roles");
				if (section == null) continue;

				for (String role_id : section.getKeys(false)) {
					Role role = guild.getRoleById(role_id);
					if (role == null)
						continue;

					String path = "discord.sync.roles." + role_id;
					if (!plugin.config.get().contains(path))
						continue;

					String permission_id = plugin.getConfig().getString(path);
					if (permission_id == null)
						continue;

					Map<String, PermissionAttachment> map = map_permissions.getOrDefault(player, new HashMap<>());

					if (member.getRoles().contains(role) && !player.hasPermission(permission_id)) {
						map.put(permission_id, addPermission(player, new Permission(permission_id)));

					} else if (!member.getRoles().contains(role) && player.hasPermission(permission_id)) {
						removePermission(player, map.remove(permission_id));
					}

					if (map.isEmpty()) map_permissions.remove(player);
					else map_permissions.put(player, map);
				}

			}
		}

		clearPlayers();
	}

	public PermissionAttachment addPermission(Player player, Permission permission) {
		PermissionAttachment attachment = player.addAttachment(plugin);
		attachment.setPermission(permission, true);
		player.recalculatePermissions();
		return attachment;
	}

	public void removePermission(Player player, PermissionAttachment attachment) {
		player.removeAttachment(attachment);
		player.recalculatePermissions();
	}

	public void permissionSyncTask() {
		JDA jda = DiscordTranslator.getJDA();
		if (jda == null) return;

		for (Guild guild : jda.getGuilds()) {
			for (Player player : Bukkit.getOnlinePlayers()) {
				User user = AccountManager.getDiscord(util.getUUID(player));
				if (user == null) continue;

				Member member = guild.getMemberById(user.getId());
				if (member == null) continue;

				ConfigurationSection section = plugin.config.get().getConfigurationSection("discord.sync.permissions");
				if (section == null) continue;

				for (String role_id : section.getKeys(false)) {
					Role role = guild.getRoleById(role_id);
					if (role == null)
						continue;

					String path = "discord.sync.permissions." + role_id;
					if (!plugin.config.get().contains(path))
						continue;

					String permission_id = plugin.getConfig().getString(path);
					if (permission_id == null)
						continue;

					if (player.hasPermission(permission_id) && !member.getRoles().contains(role)) {
						plugin.logger.warn("CORONAU!");
						addRole(guild, member, role);

					} else if (!player.hasPermission(permission_id) && member.getRoles().contains(role)) {
						plugin.logger.warn("CORONAU!!!");
						removeRole(guild, member, role);
					}
				}
			}
		}

		clearPlayers();
	}

	public void addRole(Guild guild, Member member, Role role) {
		guild.addRoleToMember(member, role).queue();
	}

	public void removeRole(Guild guild, Member member, Role role) {
		guild.removeRoleFromMember(member, role).queue();
	}

	////////////////
	// Utils!

	public void clearPlayers() {
		for (Player player : map_permissions.keySet())
			if (!player.isOnline())
				map_permissions.remove(player);
	}
}