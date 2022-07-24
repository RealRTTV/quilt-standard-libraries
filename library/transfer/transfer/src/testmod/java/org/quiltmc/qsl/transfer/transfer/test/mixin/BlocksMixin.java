package org.quiltmc.qsl.transfer.transfer.test.mixin;

import net.minecraft.block.Blocks;
import org.quiltmc.qsl.transfer.transfer.test.TransferBlocks;
import org.spongepowered.asm.mixin.Debug;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Debug(export = true, print = true)
@Mixin(Blocks.class)
abstract class BlocksMixin {
	@Inject(method = "<clinit>", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/registry/DefaultedRegistry;iterator()Ljava/util/Iterator;"))
	private static void clinit(CallbackInfo ci) {
		TransferBlocks.init();
	}
}
