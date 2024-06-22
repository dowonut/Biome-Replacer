package net.werdei.biome_replacer;

import net.fabricmc.api.ModInitializer;
import net.minecraft.registry.CombinedDynamicRegistries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.ServerDynamicRegistryType;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Identifier;
import net.minecraft.world.biome.Biome;
import net.werdei.biome_replacer.config.Config;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;

public class BiomeReplacer implements ModInitializer
{
    private static final Logger LOGGER = LogManager.getLogger();
    private static final String LOG_PREFIX = "[BiomeReplacer] ";

    private static Map<RegistryEntry<Biome>, RegistryEntry<Biome>> rules;

    @Override
    public void onInitialize()
    {
        Config.createIfAbsent();
    }

    public static void prepareReplacementRules(CombinedDynamicRegistries<ServerDynamicRegistryType> registryAccess)
    {
        rules = new HashMap<RegistryEntry<Biome>,RegistryEntry<Biome>>();
        var registry = registryAccess.getCombinedRegistryManager().get(RegistryKeys.BIOME);

        Config.reload();
        for (var rule : Config.rules.entrySet())
        {
            var oldBiome = getBiomeHolder(rule.getKey(), registry);
            var newBiome = getBiomeHolder(rule.getValue(), registry);
            if (oldBiome != null && newBiome != null)
                rules.put(oldBiome, newBiome);
        }

        log("Loaded " + rules.size() + " biome replacement rules");
    }

    private static Identifier getBiomeResourceLocation(String id, Registry<Biome> registry)
    {
        var rr = Identifier.of(id);
        if (registry.get(Identifier.of(id)) != null)
            return rr;

        logWarn("Biome " + id + " not found. The rule will be ignored.");
        return null;
    }

    private static RegistryEntry<Biome> getBiomeHolder(String id, Registry<Biome> registry)
    {
        var resourceKey = registry.getKey(registry.get(Identifier.of(id)));
        if (resourceKey.isPresent())
            return registry.entryOf(resourceKey.get());

        logWarn("Biome " + id + " not found. The rule will be ignored.");
        return null;
    }

    public static RegistryEntry<Biome> replaceIfNeeded(RegistryEntry<Biome> original)
    {
        var replacement = rules.get(original);
        return replacement == null ? original : replacement;
    }

    public static boolean noReplacements()
    {
        return rules == null || rules.isEmpty();
    }


    public static void log(String message)
    {
        LOGGER.info(LOG_PREFIX + message);
    }

    public static void logWarn(String message)
    {
        LOGGER.warn(LOG_PREFIX + message);
    }
}
