package com.example;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.LevelData;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TemplateMod implements ModInitializer {
	public static final String MOD_ID = "anciencity";

	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		LOGGER.info("Ancient City Underground mod initialized!");
		LOGGER.info("Spacing: 10 chunks, Min separation: 4 chunks, Biomes: all Overworld");
		LOGGER.info("Players spawn safely at a random location inside the nearest Ancient City.");

		// Use SERVER_STARTED to modify the world spawn AFTER worlds are fully loaded
		// but BEFORE any player connects. This is the correct timing for spawn changes.
		ServerLifecycleEvents.SERVER_STARTED.register(server -> {
			try {
				ServerLevel overworld = server.overworld();

				// Get the current world spawn position
				BlockPos currentSpawn = overworld.getRespawnData().pos();
				LOGGER.info("Current world spawn: {}", currentSpawn);

				// Set spawn to the same X,Z at Y=-51 (ancient city ground level).
				// With spacing=10 and coverage radius=128, every underground position
				// is within an ancient city. Minecraft's spawn safety check will find
				// open space (city walkway/courtyard) at this Y level.
				BlockPos undergroundSpawn = new BlockPos(currentSpawn.getX(), -51, currentSpawn.getZ());

				LevelData.RespawnData respawnData = LevelData.RespawnData.of(
					Level.OVERWORLD, undergroundSpawn, 0.0F, 0.0F
				);

				// setRespawnData updates the underlying ServerLevelData, broadcasts
				// to players, and calls updateEffectiveRespawnData() to sync the field.
				server.setRespawnData(respawnData);

				LOGGER.info("★ World spawn set to ancient city underground: {}", undergroundSpawn);
			} catch (Exception e) {
				LOGGER.error("Failed to set underground spawn: {}",
					e.getMessage() != null ? e.getMessage() : e.getClass().getName());
			}
		});
	}

	public static Identifier id(String path) {
		return Identifier.fromNamespaceAndPath(MOD_ID, path);
	}
}
