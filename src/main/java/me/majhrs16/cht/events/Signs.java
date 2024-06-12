package me.majhrs16.cht.events;

import me.majhrs16.cht.events.custom.Formats;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.command.CommandSender;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.Listener;
import org.bukkit.entity.Player;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;

import me.majhrs16.lib.exceptions.ParseYamlException;
import me.majhrs16.lib.utils.Str;

import me.majhrs16.cht.translator.ChatTranslatorAPI;
import me.majhrs16.cht.events.custom.Message;
import me.majhrs16.cht.util.cache.Config;
import me.majhrs16.cht.ChatTranslator;
import me.majhrs16.cht.util.util;

import java.util.Arrays;

public class Signs implements Listener {
	private final ChatTranslator plugin = ChatTranslator.getInstance();
	private final ChatTranslatorAPI API = ChatTranslatorAPI.getInstance();

	@EventHandler
	public void updateSign(PlayerInteractEvent event) {
		if (plugin.isDisabled() || !Config.TranslateOthers.Signs.ENABLE.IF())
			return;

		if (event.getAction() == Action.LEFT_CLICK_BLOCK) {
			Block block = event.getClickedBlock();

			if (block == null || !(block.getState() instanceof Sign))
				return;

			if (event.getPlayer().isSneaking())
				event.setCancelled(true);

			Sign sign = (Sign) block.getState();

			String path = String.format("%s_%s_%s_%s", block.getWorld().getName(), block.getX(), block.getY(), block.getZ());

			FileConfiguration signs = plugin.signs.get();
			Message from = new Message.Builder()
				.setSender(null)
				.setLangSource(util.convertStringToLang(signs.getString(path + ".lang")))
				.setLangTarget(API.getLang(event.getPlayer()))
				.setMessages(new Formats.Builder()
					.setTexts(signs.getStringList(path + ".text").toArray(new String[0]))
				).build();
			from = API.formatMessage(from);

			if (from.getMessages().getFormats().length == 0)
				return;

			String[] lines;

			if (Config.TranslateOthers.Signs.WRAP.IF())
				lines = Str.wrapText(
					String.join("\n", from.getMessages().getTexts()),
					15
				).split("\n");

			else
				lines = from.getMessages().getTexts();

			for (int i = 0; i < Math.min(lines.length, 4); i++)
				sign.setLine(i, lines[i]);
			sign.update();
		}
	}

	@EventHandler
	public void onSignBreak(BlockBreakEvent event) {
		if (plugin.isDisabled() && !Config.TranslateOthers.Signs.ENABLE.IF())
			return;

		FileConfiguration signs = plugin.signs.get();
		Player player = event.getPlayer();
		Block block   = event.getBlock();
		String path   = String.format("%s_%s_%s_%s", player.getWorld().getName(), block.getX(), block.getY(), block.getZ());

		if (signs.contains(path)) {
			signs.set(path + ".text", null);
			signs.set(path + ".lang", null);
			signs.set(path, null);
			plugin.signs.save();

			try {
				plugin.signs.reload();

			} catch (ParseYamlException e) {
				plugin.logger.error(e.toString());
			}
		}
	}

	@EventHandler
	public void onSignPlace(SignChangeEvent event) {
		if (plugin.isDisabled() && !Config.TranslateOthers.Signs.ENABLE.IF())
			return;

		FileConfiguration signs = plugin.signs.get();
		Player player = event.getPlayer();
		Block block   = event.getBlock();
		String path   = String.format("%s_%s_%s_%s", player.getWorld().getName(), block.getX(), block.getY(), block.getZ());

		if (Arrays.equals(event.getLines(), new String[]{"", "", "", ""}) || event.getLines().length == 0)
			return;

		signs.set(path + ".text", event.getLines());
		signs.set(path + ".lang", API.getLang(player).getCode());
		plugin.signs.save();
		try {
			plugin.signs.reload();

		} catch (ParseYamlException e) {
			plugin.logger.error(e.toString());
		}
	}
}