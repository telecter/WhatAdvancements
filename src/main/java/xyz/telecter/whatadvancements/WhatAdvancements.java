package xyz.telecter.whatadvancements;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import xyz.telecter.whatadvancements.command.AdvancementQueryCommand;

public class WhatAdvancements implements ModInitializer {
	public static final String MOD_ID = "whatadvancements";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, env) -> {
			AdvancementQueryCommand.register(dispatcher, registryAccess);
		});
	}
}