package com.hedario.areaforge.commands;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.hedario.areaforge.AreaForge;
import com.hedario.areaforge.AreaScheduler;
import com.hedario.areaforge.Methods;
import com.hedario.areaforge.configuration.ConfigManager;
import com.hedario.areaforge.storage.AreaRepository;
import com.hedario.areaforge.util.PendingConfirmation;
import com.hedario.areaforge.util.PendingConfirmation.Action;

import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;

public class DeleteCommand extends AFCommand {

	public DeleteCommand() {
		super("delete", "/af delete <area>", ConfigManager.get().getString("Language.Commands.Delete.Description"), new String[] { "delete", "del", "d" });
	}

	@Override
	public void execute(final CommandSender sender, final List<String> args) {
		if (!this.hasPermission(sender) || !this.isCorrectLength(sender, 1, 1, args.size())) {
			return;
		}
		final String name = args.get(0);
		if (!AreaRepository.getAreaNames().contains(name)) {
			this.sendMessage(sender, Methods.setPlaceholder(ConfigManager.get().getString("Language.Messages.Not_found"), "%area%", name));
			return;
		}
			
		if (!(sender instanceof Player p)) {
			delete(sender, name);
			return;
		}

		PendingConfirmation pc = PendingConfirmation.getConfirmations().get(p.getName());
		if (pc == null) {
			sendConfirmation(p, name);
			return;
		}

		if (System.currentTimeMillis() >= pc.getExpiry()) {
			PendingConfirmation.getConfirmations().remove(p.getName());
			sendConfirmation(p, name);
			return;
		}
		if (pc.getAction() != Action.DELETE || !pc.getArea().equalsIgnoreCase(name)) {
			sendConfirmation(p, name);
			return;
		}
		delete(sender, name);
	}

	private void sendConfirmation(final Player player, final String name) {
		TextComponent message = new TextComponent(Methods.formatColors("&6&m-----&e AreaForge &6&m-----") + "\n");
		message.addExtra(Methods.formatColors(Methods.setPlaceholder(ConfigManager.get().getString("Language.Messages.Choice_delete"), "%area%", name)));
		message.addExtra("\n\n");
		TextComponent confirm = new TextComponent(Methods.formatColors("&8[&a&lCONFIRM&8]"));
		confirm.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "af delete " + name));
		TextComponent cancel = new TextComponent(Methods.formatColors("&8[&c&lCANCEL&8]"));
		cancel.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "af confirm cancel"));
		message.addExtra(confirm);
		message.addExtra("   ");
		message.addExtra(cancel);

		PendingConfirmation.getConfirmations().put(player.getName(), new PendingConfirmation(player.getName(), name, Action.DELETE));
		player.spigot().sendMessage(message);
	}

	public static void delete(final CommandSender sender, final String name) {
		final Path dir = AreaForge.getInstance().getDataFolder().toPath().resolve("Areas").resolve(name);
		if (!Files.exists(dir)) {
			Methods.sendMessage(sender, Methods.setPlaceholder(ConfigManager.get().getString("Language.Commands.Delete.Error"), "%area%", name), true);
			return;
		}
		try (Stream<Path> paths = Files.walk(dir)) {
			paths.sorted(Comparator.reverseOrder()).forEach(path -> {
				try {
					Files.deleteIfExists(path);
				} catch (IOException e) {
					throw new UncheckedIOException(e);
				}
			});
		} catch (UncheckedIOException e) {
			Methods.sendMessage(sender, Methods.setPlaceholder(ConfigManager.get().getString("Language.Commands.Delete.Fail"), "%area%", name), true);
			e.printStackTrace();
			return;

		} catch (IOException e) {
			Methods.sendMessage(sender, Methods.setPlaceholder(ConfigManager.get().getString("Language.Commands.Delete.Fail"), "%area%", name), true);
			e.printStackTrace();
			return;
		}
		AreaRepository.delete(name);
		AreaScheduler.getAreas().remove(name);
		Methods.sendMessage(sender, Methods	.setPlaceholder(ConfigManager.get().getString("Language.Commands.Delete.Success"), "%area%", name), true);
	}

	@Override
	public List<String> getTabCompletion(CommandSender sender, List<String> args) {
		return AreaRepository.getAreaNames();
	}

}
