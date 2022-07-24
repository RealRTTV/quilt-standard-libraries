package org.quiltmc.qsl.transfer.transfer.test;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.MapColor;
import net.minecraft.block.Material;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

@SuppressWarnings("unused")
public class TransferBlocks {
	public static final Block TRANSFER = Registry.register(Registry.BLOCK, new Identifier("transfer", "transfer"), new TransferBlock(AbstractBlock.Settings.of(Material.STONE, MapColor.DEEPSLATE_GRAY)));

	public static void init() {
		System.out.println("initialized");
	}
}
