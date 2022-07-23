package org.quiltmc.qsl.transfer;

import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import org.jetbrains.annotations.Nullable;

public interface TransferableItemInventory {
	/**
	 * @param other the inventory requesting to take from here
	 * @param otherPos the block pos of the inventory which wants to take from here
	 * @param dir if the block is next to this one, it specifies what face of this block is being interacted with
	 * @param slotId the slot which the other inventory wants to take the item from
	 * @param amount the amount requested to be taken
	 * @return if the other inventory can take the selected stack from this one
	 */
	default boolean canOtherTake(TransferableItemInventory other, BlockPos otherPos, @Nullable Direction dir, int slotId, int amount) {
		return !getStack(slotId).isEmpty();
	}

	/**
	 * when an item is taken from a slot, it is common for that slot to be emptied, this method is called for you to empty that slot
	 * @param slotId the slot in which to clean up
	 */
	default void cleanupSlot(int slotId) {
		if (getStack(slotId).isEmpty()) {
			mixStack(slotId, ItemStack.EMPTY);
		}
	}

	/**
	 * this method is entirely for access, it is not recommended to hook on to it
	 */
	ItemStack getStack(int slotId);

	/**
	 * this method is entirely for access, it is not recommended to hook on to it
	 * <p>
	 * this should <b>not</b> just set a slot to a stack, but should first try to combine the stacks together
	 */
	void mixStack(int slotId, ItemStack stack);

	/**
	 * @return the amount of slots in the inventory
	 */
	int getInvSlotSize();

	/**
	 * decrements the count of this stack, while returning a new stack with the requested amount
	 * @param stack the stack to take from
	 * @param amount the amount to take
	 * @return the new stack with the specified amount
	 */
	default ItemStack splitStack(ItemStack stack, int amount) {
		return stack.split(amount);
	}

	/**
	 * @param other the other inventory requesting if something can be placed here
	 * @param otherPos the block pos of the inventory which wants to take from here
	 * @param dir if the block is next to this one, it specifies what face of this block is being interacted with
	 * @param slotId the slot in which a stack is requested to be placed in
	 * @param stack the stack requested to be placed in a slot
	 * @return if the other inventory can put the selected stack into the specified slot
	 */
	default boolean canOtherPut(TransferableItemInventory other, BlockPos otherPos, @Nullable Direction dir, int slotId, ItemStack stack) {
		return ItemStack.canCombine(getStack(slotId), stack);
	}

	static boolean transfer(TransferableItemInventory from, BlockPos fromPos, TransferableItemInventory to, BlockPos toPos, int amount) {
		if (from.getInvSlotSize() > 0 && to.getInvSlotSize() > 0) {
	        for (int slotIdFrom = 0; slotIdFrom < from.getInvSlotSize(); slotIdFrom++) {
				if (from.canOtherTake(to, toPos, Direction.fromVector(toPos.subtract(fromPos)), slotIdFrom, amount) && transfer(from, fromPos, slotIdFrom, to, toPos, amount)) {
					return true;
				}
			}
		}

		return false;
	}

	static boolean transfer(TransferableItemInventory from, BlockPos fromPos, int slotIdFrom, TransferableItemInventory to, BlockPos toPos, int amount) {
		int originalCount = from.getStack(slotIdFrom).getCount();
		if (to.getInvSlotSize() > 0) {
			for (int slotIdTo = 0; slotIdTo < to.getInvSlotSize() && !from.getStack(slotIdFrom).isEmpty(); slotIdTo++) {
				if (to.canOtherPut(from, fromPos, Direction.fromVector(fromPos.subtract(toPos)), slotIdTo, from.getStack(slotIdFrom))) {
					transfer(from, fromPos, slotIdFrom, to, toPos, slotIdTo, Math.min(amount, to.getStack(slotIdTo).getMaxCount() - to.getStack(slotIdTo).getCount()));
				}
			}
		}

		return from.getStack(slotIdFrom).getCount() < originalCount;
	}

	static boolean transfer(TransferableItemInventory from, BlockPos fromPos, int slotIdFrom, TransferableItemInventory to, BlockPos toPos, int slotIdTo, int amount) {
		ItemStack splitStack = from.splitStack(from.getStack(slotIdFrom).copy(), amount);
		if (from.canOtherTake(to, toPos, Direction.fromVector(toPos.subtract(fromPos)), slotIdFrom, amount) && to.canOtherPut(from, fromPos, Direction.fromVector(fromPos.subtract(toPos)), slotIdTo, splitStack)) {
			splitStack = from.splitStack(from.getStack(slotIdFrom), amount);
			to.mixStack(slotIdTo, splitStack);
			from.cleanupSlot(slotIdFrom);
			return true;
		}
		return false;
	}
}
