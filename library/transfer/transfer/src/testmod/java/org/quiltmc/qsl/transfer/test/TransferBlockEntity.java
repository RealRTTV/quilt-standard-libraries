package org.quiltmc.qsl.transfer.test;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.item.ItemStack;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.quiltmc.qsl.transfer.SimpleTransferableItemInventory;

public class TransferBlockEntity extends BlockEntity implements SimpleTransferableItemInventory {
	private final DefaultedList<ItemStack> stacks = DefaultedList.ofSize(27, ItemStack.EMPTY);

	public TransferBlockEntity(BlockEntityType<?> blockEntityType, BlockPos blockPos, BlockState blockState) {
		super(blockEntityType, blockPos, blockState);
	}

	public void tick(World world, BlockPos pos, BlockState state) {
		for (Direction dir : Direction.values()) {
			BlockEntity blockEntity = world.getBlockEntity(pos.offset(dir));
		}
	}

	@Override
	public void setStack(int i, ItemStack stack) {
		stacks.set(i, stack);
	}

	@Override
	public ItemStack getStack(int slotId) {
		return stacks.get(slotId);
	}

	@Override
	public int getInvSlotSize() {
		return stacks.size();
	}
}
