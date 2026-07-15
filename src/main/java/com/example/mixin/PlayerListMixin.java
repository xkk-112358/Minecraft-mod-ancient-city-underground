package com.example.mixin;

import com.example.TemplateMod;
import com.mojang.datafixers.util.Pair;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.Connection;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.CommonListenerCookie;
import net.minecraft.server.players.PlayerList;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.core.registries.Registries;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;
import java.util.Random;

@Mixin(PlayerList.class)
public class PlayerListMixin {

    @Inject(method = "placeNewPlayer", at = @At("HEAD"))
    private void onPlaceNewPlayer(Connection connection, ServerPlayer player, CommonListenerCookie cookie, CallbackInfo ci) {
        // Only for overworld players
        if (!player.level().dimension().equals(Level.OVERWORLD)) return;

        ServerLevel level = (ServerLevel) player.level();

        try {
            // Find the nearest ancient city using structure lookup
            RegistryAccess registryAccess = level.registryAccess();
            var structureLookup = registryAccess.lookupOrThrow(Registries.STRUCTURE);

            ResourceKey<Structure> ancientCityKey = ResourceKey.create(
                Registries.STRUCTURE,
                Identifier.fromNamespaceAndPath("minecraft", "ancient_city")
            );

            structureLookup.get(ancientCityKey).ifPresentOrElse(holder -> {
                HolderSet<Structure> targetSet = HolderSet.direct(List.of(holder));
                ChunkGenerator generator = level.getChunkSource().getGenerator();
                BlockPos playerPos = player.blockPosition();

                Pair<BlockPos, Holder<Structure>> nearest = generator.findNearestMapStructure(
                    level, targetSet, playerPos, 100, false
                );

                if (nearest != null) {
                    BlockPos cityCenter = nearest.getFirst();
                    // Try to find a random safe position within the city (5-50 blocks from center)
                    // City is built at Y=-51 (floor), Y=-50 = player standing, Y=-49 = head space
                    BlockPos safePos = findRandomSafeSpot(level, cityCenter);
                    if (safePos != null) {
                        player.setPos(safePos.getX() + 0.5, -50, safePos.getZ() + 0.5);
                        TemplateMod.LOGGER.info("★ Random safe spawn in city: {} (center: {})", safePos, cityCenter);
                    } else {
                        // Fallback to city center courtyard
                        player.setPos(cityCenter.getX() + 0.5, -50, cityCenter.getZ() + 0.5);
                        TemplateMod.LOGGER.info("★ Fallback spawn at city center: {}", cityCenter);
                    }
                } else {
                    player.setPos(player.getX(), -51, player.getZ());
                    TemplateMod.LOGGER.warn("No ancient city found, falling back to Y=-51");
                }
            }, () -> {
                player.setPos(player.getX(), -51, player.getZ());
                TemplateMod.LOGGER.warn("Ancient city structure not in registry, falling back to Y=-51");
            });
        } catch (Exception e) {
            TemplateMod.LOGGER.error("Failed to find safe spawn: {}", e.getMessage());
            player.setPos(player.getX(), -51, player.getZ());
        }
    }

    /**
     * Search for a random safe position within the ancient city.
     * City ground level is Y=-51 (smooth deepslate).
     * A safe position has solid ground at Y=-51 and open air at Y=-50 (stand) and Y=-49 (head).
     */
    private BlockPos findRandomSafeSpot(ServerLevel level, BlockPos cityCenter) {
        Random random = new Random();
        int baseX = cityCenter.getX();
        int baseZ = cityCenter.getZ();
        int groundY = -51;

        // Try up to 40 random offsets within 5-50 blocks from center
        for (int attempt = 0; attempt < 40; attempt++) {
            // Random direction and distance
            double angle = random.nextDouble() * 2 * Math.PI;
            int distance = random.nextInt(46) + 5; // 5 to 50 blocks
            int dx = (int) Math.round(Math.cos(angle) * distance);
            int dz = (int) Math.round(Math.sin(angle) * distance);

            BlockPos checkPos = new BlockPos(baseX + dx, groundY, baseZ + dz);

            // Make sure chunk is loaded before checking blocks
            if (!level.isLoaded(checkPos) || !level.isLoaded(checkPos.above(2))) continue;

            BlockState ground = level.getBlockState(checkPos);
            BlockState body = level.getBlockState(checkPos.above());
            BlockState head = level.getBlockState(checkPos.above(2));

            // Safe: solid floor, open space for body and head
            if (!ground.isAir() && body.isAir() && head.isAir()) {
                return checkPos;
            }
        }
        return null; // No safe spot found, caller will use center
    }
}
