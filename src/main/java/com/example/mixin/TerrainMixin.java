package com.example.mixin;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.chunk.LevelChunkSection;
import net.minecraft.world.level.chunk.ProtoChunk;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Runs when a ProtoChunk is fully generated and converted to LevelChunk.
 * At this point, ALL generation stages are complete.
 * We process each chunk's sections to enforce:
 * - Y > BEDROCK_Y → air
 * - Y = BEDROCK_Y → bedrock
 * - Y < BEDROCK_Y → normal terrain
 */
@Mixin(LevelChunk.class)
public class TerrainMixin {

    private static final int BEDROCK_Y = 60;

    @Inject(method = "<init>(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/level/chunk/ProtoChunk;Lnet/minecraft/world/level/chunk/LevelChunk$PostLoadProcessor;)V",
            at = @At("RETURN"))
    private void onFromProtoChunk(ServerLevel level, ProtoChunk proto, LevelChunk.PostLoadProcessor processor, CallbackInfo ci) {
        LevelChunk chunk = (LevelChunk)(Object)this;
        processChunk(chunk);
    }

    private void processChunk(LevelChunk chunk) {
        LevelChunkSection[] sections = chunk.getSections();
        int minY = chunk.getMinY();

        for (int i = 0; i < sections.length; i++) {
            int bottomY = minY + i * 16;
            int topY = bottomY + 15;
            int bedrockLocal = BEDROCK_Y - bottomY;

            if (topY < BEDROCK_Y + 1) continue;              // entirely below bedrock
            if (bottomY > BEDROCK_Y) {                        // entirely above bedrock Y
                if (!sections[i].hasOnlyAir()) {
                    clearSection(sections[i]);
                }
                continue;
            }
            // contains BEDROCK_Y
            carveSection(sections[i], bedrockLocal);
        }
    }

    private void clearSection(LevelChunkSection section) {
        var states = section.getStates();
        for (int x = 0; x < 16; x++)
            for (int y = 0; y < 16; y++)
                for (int z = 0; z < 16; z++)
                    states.set(x, y, z, Blocks.AIR.defaultBlockState());
    }

    private void carveSection(LevelChunkSection section, int bedrockLocalY) {
        var states = section.getStates();

        // Clear blocks above BEDROCK_Y within this section
        for (int ly = bedrockLocalY + 1; ly < 16; ly++)
            for (int x = 0; x < 16; x++)
                for (int z = 0; z < 16; z++)
                    states.set(x, ly, z, Blocks.AIR.defaultBlockState());

        // Set BEDROCK_Y to bedrock
        for (int x = 0; x < 16; x++)
            for (int z = 0; z < 16; z++)
                states.set(x, bedrockLocalY, z, Blocks.BEDROCK.defaultBlockState());
    }
}
