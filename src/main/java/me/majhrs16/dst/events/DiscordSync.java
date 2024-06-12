package me.majhrs16.dst.events;

import me.majhrs16.cht.translator.ChatTranslatorAPI;
import me.majhrs16.cht.events.custom.Message;
import me.majhrs16.cht.util.cache.Config;
import me.majhrs16.cht.ChatTranslator;
import me.majhrs16.cht.util.util;

import org.bukkit.permissions.PermissionAttachment;
import org.bukkit.permissions.Permission;
import org.bukkit.entity.Player;
import org.bukkit.Bukkit;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Role;

import me.majhrs16.dst.DiscordTranslator;

import java.util.*;

public class DiscordSync {
	private Timer timer;
	private final ChatTranslator plugin = ChatTranslator.getInstance();
	private final ChatTranslatorAPI API = ChatTranslatorAPI.getInstance();
	private final Map<Player, Map<Permission, PermissionAttachment>> map_permissions = new HashMap<>();

	private class Clock extends TimerTask {
		public void run() {
			if (DiscordTranslator.isDisabled() || !Config.TranslateOthers.DISCORD_SYNC.IF()) return;

			try {
				DiscordSyncTask();
				clearPlayers();

			} catch (Exception e) {
				plugin.logger.error("[DEBUG] Error while syncing Minecraft permissions <-> Discord roles:");
				e.printStackTrace();
			}
		}
	}

	public void start() {
		timer = new Timer(true);
		timer.scheduleAtFixedRate(new Clock(), 0, 5000);
	}

	public void stop() {
		if (timer != null) timer.cancel();
	}

	public void clearPlayers() {
		for (Player player : new HashMap<>(map_permissions).keySet())
			if (!player.isOnline())
				map_permissions.remove(player);
	}
	public void setPermission(Player player, Permission permission, boolean state) {
		Map<Permission, PermissionAttachment> map = map_permissions.getOrDefault(player, new HashMap<>());
		PermissionAttachment attachment           = map.getOrDefault(permission, player.addAttachment(plugin));

		attachment.setPermission(permission, state);
		player.recalculatePermissions();

		map.put(permission, attachment);
		map_permissions.put(player, map);
	}

	public void addRole(Guild guild, Member member, Role role) {
		guild.addRoleToMember(member, role).queue();
	}

	public void removeRole(Guild guild, Member member, Role role) {
		guild.removeRoleFromMember(member, role).queue();
	}

	private void DiscordSyncTask() {
		for (Guild guild : DiscordTranslator.getJDA().getGuilds()) {
			for (Player player : Bukkit.getOnlinePlayers()) {
//				Get directly user ID.
				String[] result = plugin.storage.get(util.getUUID(player));

				if (result == null || result[1] == null)
					continue;

				Member member = guild.retrieveMemberById(result[1]).complete();
				if (member == null) continue;

				List<String> rules = plugin.config.get().getStringList("discord.sync");

				int line = 1;
				for (String rule : rules) {
					String path;

					String[] parts = rule.split("(<-|<->|->)");
					if (parts.length != 2) {
						final int fLine = line;
						plugin.logger.debug("rules.#%s.split.length: %s", fLine, parts.length);
						API.sendMessage(new Message.Builder().format("discord-translator.sync.error",
							format -> format.replace("%line%", String.valueOf(fLine))
						).build());

						continue;
					};

					String roleID       = parts[0].trim();
					String permissionID = parts[1].trim();

					path = "discord.sync.rule.#%s.type: ";
					if (rule.contains("<->"))
						plugin.logger.debug(path + "<->", line);

					if (rule.contains("->"))
						plugin.logger.debug(path + " ->", line);

					if (rule.contains("<-"))
						plugin.logger.debug(path + "<- ", line);

					Role role = null;

					try {
						role = guild.getRoleById(roleID);

					} catch (Exception e) {
						final int fLine = line;
						plugin.logger.debug("JDA.guild.getRoleById: %s", e);
						API.sendMessage(new Message.Builder().format("discord-translator.sync.error",
							format -> format.replace("%line%", String.valueOf(fLine))
						).build());
					}

					if (role == null) continue;

					Permission permission = new Permission(permissionID);

					path = "discord.sync.rule.#%s: Role: %s, Permission: %s, status = ";

					if (rule.contains("->") || rule.contains("<->")) {
						if (member.getRoles().contains(role) && !player.hasPermission(permission)) {
							plugin.logger.debug(path + "added permission", line, roleID, permissionID);
							setPermission(player, permission, true);

						} else if (!member.getRoles().contains(role) && player.hasPermission(permission)) {
							plugin.logger.debug(path + "removed permission", line, roleID, permissionID);
							setPermission(player, permission, false);
						}
					}

					if (rule.contains("<-") || rule.contains("<->")) {
						if (player.hasPermission(permission) && !member.getRoles().contains(role)) {
							plugin.logger.debug(path + "added role", line, roleID, permissionID);
							addRole(guild, member, role);

						} else if (!player.hasPermission(permission) && member.getRoles().contains(role)) {
							plugin.logger.debug(path + "removed role", line, roleID, permissionID);
							removeRole(guild, member, role);
						}
					}

					line ++;
				}
			}
		}
	}
}