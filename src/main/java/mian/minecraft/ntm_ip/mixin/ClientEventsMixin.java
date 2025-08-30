package mian.minecraft.ntm_ip.mixin;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.client.sounds.SoundEngine;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.sounds.SoundSource;
import net.tardis.mod.events.ClientEvents;
import net.tardis.mod.sound.SoundRegistry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ClientEvents.class)
public class ClientEventsMixin {
    @WrapWithCondition(method = "onTardisEntered", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/sounds/SoundManager;play(Lnet/minecraft/client/resources/sounds/SoundInstance;)V"))
    private static boolean OnEntered(SoundManager instance, SoundInstance pSound){
        // just to not have the sound play multiple times cuz it's annoying lowkey
        return instance.soundEngine.instanceBySource
                .get(SoundSource.PLAYERS).stream().noneMatch(
                        sound -> sound.getLocation().equals(SoundRegistry.FIRST_ENTRANCE_MUSIC.getId())
                );
    }
}
