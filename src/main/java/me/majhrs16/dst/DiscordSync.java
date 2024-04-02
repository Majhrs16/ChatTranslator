package me.majhrs16.dst;

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

import me.majhrs16.cht.ChatTranslator;
import me.majhrs16.cht.util.util;

import java.util.*;

public class DiscordSync {
	private Timer timer;
	private final ChatTranslator plugin = ChatTranslator.getInstance();
	private final Map<Player, Map<String, PermissionAttachment>> map_permissions = new HashMap<>();

	public class Clock extends TimerTask {
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

				for (Role role : member.getRoles()) {
					String path = "discord.sync.roles." + role.getId();

					if (!plugin.config.get().contains(path))
						continue;

					String permission_id = plugin.getConfig().getString(path);
					assert permission_id != null;

					if (member.getRoles().contains(role) && !player.hasPermission(permission_id))

						if (map_permissions.containsKey(player)) {
							map_permissions.get(player).put(
								permission_id,
								addPermission(player, new Permission(permission_id))
							);

						} else {
							Map<String, PermissionAttachment> map = new HashMap<>();
								map.put(permission_id, addPermission(player, new Permission(permission_id)));
							map_permissions.put(player, map);
						}
				}

				ConfigurationSection section = plugin.config.get().getConfigurationSection("discord.sync");
				if (section == null) continue;

				for (Role role : member.getRoles()) {
					String path = "discord.sync.roles." + role.getId();

					String permission_id = plugin.getConfig().getString(path);
					assert permission_id != null;

					if (!member.getRoles().contains(role)
							&& player.hasPermission(permission_id)
							&& map_permissions.containsKey(player)) {

						Map<String, PermissionAttachment> map = map_permissions.get(player);
						PermissionAttachment pa = map.get(permission_id);
						removePermission(player, pa);

						if (map.isEmpty())
							map_permissions.remove(player);
					}
				}
			}
		}
	}

	private PermissionAttachment addPermission(Player player, Permission permission) {
		PermissionAttachment attachment = player.addAttachment(plugin);
		attachment.setPermission(permission, true);
		player.recalculatePermissions();
		return attachment;
	}

	private void removePermission(Player player, PermissionAttachment attachment) {
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

				for (Role role : member.getRoles()) {
					String path = "discord.sync.permissions." + role.getId();

					if (!plugin.config.get().contains(path))
						continue;

					String permission_id = plugin.getConfig().getString(path);
					assert permission_id != null;

					if (player.hasPermission(permission_id) && !member.getRoles().contains(role))
						addRole(guild, member, role);
				}

				ConfigurationSection section = plugin.config.get().getConfigurationSection("discord.sync");
				if (section == null) continue;

				for (Role role : member.getRoles()) {
					String path = "discord.sync.permissions." + role.getId();
					if (!plugin.config.get().contains(path))
						continue;

					String permission_id = plugin.config.get().getString(path);
					assert permission_id != null;

					plugin.logger.warn(permission_id);
					if (!player.hasPermission(permission_id) && member.getRoles().contains(role)) {
						plugin.logger.warn("CORONAU!");
						removeRole(guild, member, role);
					}
				}
			}
		}
	}

	private void addRole(Guild guild, Member member, Role role) {
		guild.addRoleToMember(member, role).queue();
	}

	private void removeRole(Guild guild, Member member, Role role) {
		guild.removeRoleFromMember(member, role).queue();
	}
}