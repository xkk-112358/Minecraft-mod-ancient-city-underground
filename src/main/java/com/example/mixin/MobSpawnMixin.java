package com.example.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.NaturalSpawner;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Prevents mobs from spawning at Y > 0 (above the bedrock roof).
 */
@Mixin(NaturalSpawner.class)
public class MobSpawnMixin {

    // Inject into the simple position-based spawn method
    @Inject(method = "spawnCategoryForPosition(Lnet/minecraft/world/entity/MobCategory;Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/core/BlockPos;)V",
            at = @At("HEAD"), cancellable = true)
    private static void onSpawnCategoryForPosition(MobCategory category, ServerLevel level, BlockPos pos, CallbackInfo ci) {
        if (pos.getY() > 60) {
            ci.cancel();
        }
    }

    // Inject into the more complex overload that also takes ChunkAccess, SpawnPredicate, AfterSpawnCallback
    @Inject(method = "spawnCategoryForPosition(Lnet/minecraft/world/entity/MobCategory;Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/level/chunk/ChunkAccess;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/NaturalSpawner$SpawnPredicate;Lnet/minecraft/world/level/NaturalSpawner$AfterSpawnCallback;)V",
            at = @At("HEAD"), cancellable = true)
    private static void onSpawnCategoryForPositionFull(MobCategory category, ServerLevel level, net.minecraft.world.level.chunk.ChunkAccess chunk, BlockPos pos, NaturalSpawner.SpawnPredicate predicate, NaturalSpawner.AfterSpawnCallback callback, CallbackInfo ci) {
        if (pos.getY() > 60) {
            ci.cancel();
        }
    }
}
