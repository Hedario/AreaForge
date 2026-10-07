package com.hedario.areaforge.commands;

import java.util.List;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.hedario.areaforge.AreaScheduler;
import com.hedario.areaforge.Methods;
import com.hedario.areaforge.configuration.ConfigManager;
import com.hedario.areaforge.data.ScheduledArea;
import com.hedario.areaforge.storage.AreaRepository;
import com.hedario.areaforge.util.PendingConfirmation;
import com.hedario.areaforge.util.PendingConfirmation.Action;

import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;

public class ConfirmCommand extends AFCommand {

	public ConfirmCommand() {
		super("confirm", "", "", new String[] {"confirm"});
	}

	@Override
	public void execute(CommandSender sender, List<String> args) {
		if (!(sender instanceof Player p) || !this.isCorrectLength(sender, 1, 2, args.size())) {
			return;
		}
		if (args.get(0).equalsIgnoreCase("cancel")) {
			PendingConfirmation.getConfirmations().remove(sender.getName());
			this.sendMessage(sender, ConfigManager.get().getString("Language.Messages.Choice_cancel"));
			return;
		}
		final Action action = Action.valueOf(args.get(0).toUpperCase());
		final String area = args.get(1);
		if (action == Action.AUTO_LOAD) {
			autoLoad(p, area);
			return;
		}
		PendingConfirmation pc = PendingConfirmation.getConfirmations().get(p.getName());
		
		if (pc == null) {
			sendConfirmation(p, area, action);
			return;
		}
		if (System.currentTimeMillis() >= pc.getExpiry()) {
			sendConfirmation(p, area, action);
			return;
		}
		if (pc.getAction() != action || !pc.getArea().equalsIgnoreCase(area)) {
			sendConfirmation(p, area, action);
			return;
		}

		if (action == Action.DELETE) {
			DeleteCommand.delete(sender, area);
		} else {
			sender.getServer().dispatchCommand(sender, "af load " + pc.getArea());
		}
		PendingConfirmation.getConfirmations().remove(sender.getName());
	}
	
	private void sendConfirmation(Player p, String area, Action action) {
		TextComponent message = new TextComponent(Methods.formatColors("&6&m-----&e AreaForge &6&m-----") + "\n");
		if (action == Action.DELETE) {
			message.addExtra(Methods.formatColors(Methods.setPlaceholder(ConfigManager.get().getString("Language.Messages.Choice_delete"), "%area%", area)));
		} else {
			message.addExtra(Methods.formatColors(Methods.setPlaceholder(ConfigManager.get().getString("Language.Messages.Choice_load"), "%area%", area)));
		}
		message.addExtra("\n\n");
		TextComponent confirm = new TextComponent(Methods.formatColors("&8[&a&lCONFIRM&8]"));
		confirm.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "af confirm " + action.name().toLowerCase() + " " + area));
		TextComponent cancel = new TextComponent(Methods.formatColors("&8[&c&lCANCEL&8]"));
		cancel.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "af confirm cancel"));
		message.addExtra(confirm);
		message.addExtra("   ");
		message.addExtra(cancel);
		PendingConfirmation.getConfirmations().put(p.getName(), new PendingConfirmation(p.getName(), area, action));
		p.spigot().sendMessage(message);
	}
	
	private void autoLoad(Player p, String area) {
		ScheduledArea sarea = AreaRepository.getScheduledArea(area);
		if (sarea == null) {
			sarea = AreaRepository.addScheduledArea(area, false, "01:00:00:000");
		}
		AreaScheduler.update(area, !sarea.isEnabled(), sarea.getTime());
		p.getServer().dispatchCommand(p, "af info " + area);
	}

	@Override
	public List<String> getTabCompletion(CommandSender sender, List<String> args) {
		return null;
	}

}
