package org.quiltmc.qsl.transfer.transfer.test;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

public class TransferBlockEntity extends BlockEntity {
	public TransferBlockEntity(BlockPos blockPos, BlockState blockState) {
		super(TransferBlockEntityTypes.TRANSFER, blockPos, blockState);
	}

	public void tick(World world, BlockPos pos, BlockState state) {
		for (Direction dir : Direction.values()) {
			BlockEntity blockEntity = world.getBlockEntity(pos.offset(dir));
		}
	}
}
