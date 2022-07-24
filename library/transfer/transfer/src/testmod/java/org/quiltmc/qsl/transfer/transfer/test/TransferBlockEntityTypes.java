package org.quiltmc.qsl.transfer.transfer.test;

import net.minecraft.block.entity.BlockEntityType;

public class TransferBlockEntityTypes {
	public static final BlockEntityType<TransferBlockEntity> TRANSFER = BlockEntityType.Builder.create(TransferBlockEntity::new, TransferBlocks.TRANSFER).build(null);
}
