package xyz.telecter.whatadvancements.command;

import com.mojang.brigadier.CommandDispatcher;

import net.minecraft.advancement.Advancement;
import net.minecraft.advancement.AdvancementEntry;
import net.minecraft.advancement.AdvancementProgress;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.command.argument.RegistryKeyArgumentType;
import net.minecraft.registry.RegistryKey;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;

public class AdvancementQueryCommand {
	public static void register(CommandDispatcher<ServerCommandSource> dispatcher,
			CommandRegistryAccess commandRegistryAccess) {
		dispatcher.register(
			CommandManager.literal("advancementquery")
				.requires(source -> source.hasPermissionLevel(4))
				.then(
					CommandManager.argument("target", EntityArgumentType.player())
					.then(
						CommandManager.argument("advancement", RegistryKeyArgumentType.registryKey(RegistryKey.ofRegistry(Identifier.ofVanilla("advancement"))))
						.executes((context) -> {
							ServerPlayerEntity target = EntityArgumentType.getPlayer(context, "target");
							AdvancementEntry entry = RegistryKeyArgumentType.getAdvancementEntry(context, "advancement");
							return execute(context.getSource(), target, entry);
						})
					)
				)
		);
	}

	private static int execute(ServerCommandSource source, ServerPlayerEntity player, AdvancementEntry entry) {
		AdvancementProgress progress = player.getAdvancementTracker().getProgress(entry);

		MutableText feedback = player.getDisplayName().copy()
				.append(" ")
				.append(progress.isDone() ? "has already" : "has not yet")
				.append(" ")
				.append(Text.literal("completed the advancement "))
				.append(entry.value().name().orElse(Advancement.getNameFromIdentity(entry)));
		source.sendFeedback(() -> feedback, false);

		if (progress.isAnyObtained()) {
			source.sendMessage(Text.literal("The following criteria have been obtained:"));
			MutableText obtainedFeedback = Text.empty();
			for (String obtained : progress.getObtainedCriteria()) {
				obtainedFeedback
						.append(obtained)
						.append("\n")
						.formatted(Formatting.GREEN);
			}
			source.sendMessage(obtainedFeedback);
		}
		if (!progress.isDone()) {
			source.sendMessage(Text.literal("The following criteria have not been obtained:"));
			MutableText unobtainedFeedback = Text.empty();
			for (String unobtained : progress.getUnobtainedCriteria()) {
				unobtainedFeedback
						.append(unobtained)
						.append("\n")
						.formatted(Formatting.RED);
			}
			source.sendMessage(unobtainedFeedback);
		}

		return 1;
	}
}
