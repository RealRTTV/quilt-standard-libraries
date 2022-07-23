package org.quiltmc.qsl.transfer;

import net.minecraft.item.ItemStack;

public interface SimpleTransferableItemInventory extends TransferableItemInventory {
	void setStack(int i, ItemStack stack);

	@Override
	default void mixStack(int slotId, ItemStack stack) {
		if (ItemStack.canCombine(getStack(slotId), stack)) {
			stack.setCount(getStack(slotId).getCount() + stack.getCount());
		}
		setStack(slotId, stack);
	}
}
