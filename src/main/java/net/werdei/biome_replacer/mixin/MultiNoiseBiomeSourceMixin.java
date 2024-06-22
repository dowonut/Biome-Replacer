package net.werdei.biome_replacer.mixin;

import com.mojang.datafixers.util.Pair;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.source.BiomeSource;
import net.minecraft.world.biome.source.MultiNoiseBiomeSource;
import net.minecraft.world.biome.source.util.MultiNoiseUtil;
import net.werdei.biome_replacer.BiomeReplacer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.List;

@Mixin(MultiNoiseBiomeSource.class)
public abstract class MultiNoiseBiomeSourceMixin extends BiomeSource
{
    @Unique
    private MultiNoiseUtil.Entries<RegistryEntry<Biome>> modifiedParameters = null;


    @Inject(method = "getBiomeEntries", at = @At("RETURN"), cancellable = true)
    private void parameters(CallbackInfoReturnable<MultiNoiseUtil.Entries<RegistryEntry<Biome>>> cir)
    {
        if (modifiedParameters == null)
            findAndReplace(cir.getReturnValue());
        cir.setReturnValue(modifiedParameters);
    }

    @Unique
    private void findAndReplace(MultiNoiseUtil.Entries<RegistryEntry<Biome>> parameterList)
    {
        if (BiomeReplacer.noReplacements())
        {
            modifiedParameters = parameterList;
            BiomeReplacer.log("No rules found, not replacing anything");
            return;
        }

        List<Pair<MultiNoiseUtil.NoiseHypercube, RegistryEntry<Biome>>> newParameterList = new ArrayList<>();

        for (var value : parameterList.getEntries())
            newParameterList.add(new Pair<>(
                    value.getFirst(),
                    BiomeReplacer.replaceIfNeeded(value.getSecond())
            ));

        modifiedParameters = new MultiNoiseUtil.Entries<>(newParameterList);
        BiomeReplacer.log("Biomes replaced successfully");
    }
}
