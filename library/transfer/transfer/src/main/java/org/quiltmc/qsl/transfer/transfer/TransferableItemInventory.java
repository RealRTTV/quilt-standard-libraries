package org.quiltmc.qsl.transfer.transfer;

import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import org.jetbrains.annotations.Nullable;

@SuppressWarnings("unused")
public interface TransferableItemInventory {
	/**
	 * this method is entirely for access, it is not recommended to hook on to it
	 */
	ItemStack getStack(int slotId);

	/**
	 * this method is entirely for access, it is not recommend to hook on to it
	 */
	void setStack(int i, ItemStack stack);

	/**
	 * @return the amount of slots in the inventory
	 */
	int getInvSlotSize();

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
	 * takes in a current slot and a stack and tries to combine them, replacing it if they can't combine
	 */
	default void mixStack(int slotId, ItemStack stack) {
		if (ItemStack.canCombine(getStack(slotId), stack)) {
			stack.setCount(getStack(slotId).getCount() + stack.getCount());
		}
		setStack(slotId, stack);
	}

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
	 * @param other the inventory requesting to take from here
	 * @param otherPos the block pos of the inventory which wants to take from here
	 * @param face if the block is next to this one, it specifies what face of this block is being interacted with
	 * @param slotId the slot which the other inventory wants to take the item from
	 * @param amount the amount requested to be taken
	 * @return if the other inventory can take the selected stack from this one
	 */
	default boolean canOtherTake(TransferableItemInventory other, @Nullable BlockPos otherPos, @Nullable Direction face, int slotId, int amount) {
		return !getStack(slotId).isEmpty();
	}

	/**
	 * @param other the other inventory requesting if something can be placed here
	 * @param otherPos the block pos of the inventory which wants to take from here
	 * @param face if the block is next to this one, it specifies what face of this block is being interacted with
	 * @param slotId the slot in which a stack is requested to be placed in
	 * @param stack the stack requested to be placed in a slot
	 * @return if the other inventory can put the selected stack into the specified slot
	 */
	default boolean canOtherPut(TransferableItemInventory other, @Nullable BlockPos otherPos, @Nullable Direction face, int slotId, ItemStack stack) {
		return ItemStack.canCombine(getStack(slotId), stack) || getStack(slotId).isEmpty();
	}

	/**
	 * @param receiver the block pos of the block receiving the transfer
	 * @param sender the block pos of the block sending the transfer
	 * @return optionally, the face of the block the sender is using
	 */
	@Nullable
	default Direction fromPositions(@Nullable BlockPos receiver, @Nullable BlockPos sender) {
		if (receiver == null || sender == null) {
			return null;
		}

		return Direction.fromVector(receiver.subtract(sender));
	}

	static boolean transfer(TransferableItemInventory from, @Nullable BlockPos fromPos, TransferableItemInventory to, @Nullable BlockPos toPos, int amount) {
		if (from.getInvSlotSize() > 0 && to.getInvSlotSize() > 0) { // planned multithreading since this is O(n^2), which even though it's uncommon to reach that, is slow for me
	        for (int slotIdFrom = 0; slotIdFrom < from.getInvSlotSize(); slotIdFrom++) {
				if (from.canOtherTake(to, toPos, from.fromPositions(fromPos, toPos), slotIdFrom, amount) && transfer(from, fromPos, slotIdFrom, to, toPos, amount)) {
					return true;
				}
			}
		}

		return false;
	}

	static boolean transfer(TransferableItemInventory from, @Nullable BlockPos fromPos, int slotIdFrom, TransferableItemInventory to, @Nullable BlockPos toPos, int amount) {
		boolean modified = false;
		if (to.getInvSlotSize() > 0) {
			for (int slotIdTo = 0; slotIdTo < to.getInvSlotSize() && !from.getStack(slotIdFrom).isEmpty(); slotIdTo++) {
				if (to.canOtherPut(from, fromPos, to.fromPositions(fromPos, toPos), slotIdTo, from.getStack(slotIdFrom))) {
					modified = transfer(from, fromPos, slotIdFrom, to, toPos, slotIdTo, Math.min(amount, to.getStack(slotIdTo).getMaxCount() - to.getStack(slotIdTo).getCount())) | modified;
				}
			}
		}

		return modified;
	}

	static boolean transfer(TransferableItemInventory from, @Nullable BlockPos fromPos, int slotIdFrom, TransferableItemInventory to, @Nullable BlockPos toPos, int slotIdTo, int amount) {
		ItemStack splitStack = from.splitStack(from.getStack(slotIdFrom).copy(), amount);
		if (from.canOtherTake(to, toPos, from.fromPositions(toPos, fromPos), slotIdFrom, amount) && to.canOtherPut(from, fromPos, to.fromPositions(fromPos, toPos), slotIdTo, splitStack)) {
			splitStack = from.splitStack(from.getStack(slotIdFrom), amount);
			to.mixStack(slotIdTo, splitStack);
			from.cleanupSlot(slotIdFrom);
			return true;
		}
		return false;
	}
}
