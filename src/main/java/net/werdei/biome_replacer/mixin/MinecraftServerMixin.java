package net.werdei.biome_replacer.mixin;

import com.mojang.datafixers.DataFixer;
import net.minecraft.resource.ResourcePackManager;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.SaveLoader;
import net.minecraft.server.WorldGenerationProgressListenerFactory;
import net.minecraft.util.ApiServices;
import net.minecraft.world.level.storage.LevelStorage;
import net.werdei.biome_replacer.BiomeReplacer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.net.Proxy;

@Mixin(MinecraftServer.class)
public class MinecraftServerMixin
{
    @Inject(method = "<init>", at = @At(
            value = "INVOKE",
//            target = "Lnet/minecraft/server/SaveLoader;registries()Lnet/minecraft/registry/CombinedDynamicRegistries;"))
            target="Lnet/minecraft/server/SaveLoader;combinedDynamicRegistries()Lnet/minecraft/registry/CombinedDynamicRegistries;"
    ))
    private void onServerStart(Thread thread, LevelStorage.Session levelStorageAccess, ResourcePackManager packRepository, SaveLoader worldStem, Proxy proxy, DataFixer dataFixer, ApiServices services, WorldGenerationProgressListenerFactory chunkProgressListenerFactory, CallbackInfo ci)
    {
        BiomeReplacer.prepareReplacementRules(worldStem.combinedDynamicRegistries());
    }
}
