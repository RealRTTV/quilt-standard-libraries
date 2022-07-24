package org.quiltmc.qsl.transfer.transfer;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import org.jetbrains.annotations.Nullable;

@SuppressWarnings("unused")
public interface TransferableFluidInventory {
	FluidTank getTank(int slotId);

	int getTankCount();

	void setTank(int i, FluidTank tank);

	default void cleanupTank(int slotId) {
		if (getTank(slotId).isEmpty()) {
			setTank(slotId, FluidTank.empty(getTank(slotId).maxMb()));
		}
	}

	default void mixTank(int slotId, FluidTank tank) {
		if (FluidTank.canCombine(getTank(slotId), tank)) {
			tank.setMb(getTank(slotId).mb() + tank.mb());
		}
		setTank(slotId, tank);
	}

	default FluidTank splitTank(FluidTank tank, int mb) {
		return tank.split(mb);
	}

	default boolean canOtherTake(TransferableFluidInventory other, @Nullable BlockPos otherPos, @Nullable Direction face, int slotId, int mb) {
		return !getTank(slotId).isEmpty();
	}

	default boolean canOtherPut(TransferableFluidInventory other, @Nullable BlockPos otherPos, @Nullable Direction face, int slotId, FluidTank tank) {
		return FluidTank.canCombine(tank, getTank(slotId)) || getTank(slotId).isEmpty();
	}

	@Nullable
	default Direction fromPositions(@Nullable BlockPos receiver, @Nullable BlockPos sender) {
		if (receiver == null || sender == null) {
			return null;
		}

		return Direction.fromVector(receiver.subtract(sender));
	}

	static boolean transfer(TransferableFluidInventory from,  @Nullable BlockPos fromPos, TransferableFluidInventory to, @Nullable BlockPos toPos, int mb) {
		if (from.getTankCount() > 0 && to.getTankCount() > 0) {
			for (int fromSlotId = 0; fromSlotId < from.getTankCount(); fromSlotId++) {
				if (from.canOtherTake(to, toPos, from.fromPositions(toPos, fromPos), fromSlotId, mb) && transfer(from, fromPos, fromSlotId, to, toPos, mb)) {
					return true;
				}
			}
		}

		return false;
	}

	static boolean transfer(TransferableFluidInventory from,  @Nullable BlockPos fromPos, int fromSlotId, TransferableFluidInventory to, @Nullable BlockPos toPos, int mb) {
		boolean modified = false;
		if (to.getTankCount() > 0) {
			for (int toSlotId = 0; toSlotId < to.getTankCount() && !from.getTank(fromSlotId).isEmpty(); toSlotId++) {
				if (to.canOtherPut(from, fromPos, to.fromPositions(fromPos, toPos), toSlotId, from.getTank(fromSlotId))) {
					modified = transfer(from, fromPos, fromSlotId, to, toPos, toSlotId, mb) | modified;
				}
			}
		}

		return modified;
	}

	static boolean transfer(TransferableFluidInventory from, @Nullable BlockPos fromPos, int fromSlotId, TransferableFluidInventory to, @Nullable BlockPos toPos, int toSlotId, int mb) {
		FluidTank splitTank = from.splitTank(from.getTank(fromSlotId).copy(), mb);
		if (from.canOtherTake(to, toPos, from.fromPositions(toPos, fromPos), fromSlotId, mb) && to.canOtherPut(from, fromPos, to.fromPositions(fromPos, toPos), toSlotId, splitTank)) {
			splitTank = from.splitTank(from.getTank(fromSlotId), mb);
			to.mixTank(toSlotId, splitTank);
			from.cleanupTank(fromSlotId);
		}
		return false;
	}
}
