package majhrs16.cht.events;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.TabCompleteEvent;

import majhrs16.cht.ChatTranslator;

import java.util.ArrayList;
import java.util.List;

public class TabCompleter implements Listener {
	private ChatTranslator plugin = ChatTranslator.getInstance();

    @EventHandler
    public void onTabComplete(TabCompleteEvent event) {
        String buffer = event.getBuffer();

        if (buffer.startsWith("/"))
        	buffer = buffer.substring(1);

        System.out.println(buffer);

        String[] args = buffer.split(" ");

        List<String> completions;
        String path = args[0] + ".suggest";
        if (!args[0].equals("config-version") && plugin.commands.get().contains(path)) {
        	completions = new ArrayList<>();
        	completions.add(plugin.commands.get().getString(path));

        } else {
        	completions = event.getCompletions();
        }

        System.out.println(completions.toString());

        event.setCompletions(completions);
    }
}