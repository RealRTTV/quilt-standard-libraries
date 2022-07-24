package org.quiltmc.qsl.transfer.transfer;

import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;

public final class FluidTank {
	private final Fluid fluid;
	private final int maxMb;
	private int mb;

	public FluidTank(Fluid fluid, int maxMb) {
		this.fluid = fluid;
		this.maxMb = maxMb;
		this.mb = 0;
	}

	public FluidTank(Fluid fluid, int maxMb, int mb) {
		this.fluid = fluid;
		this.maxMb = maxMb;
		this.mb = mb;
	}

	public static FluidTank empty(int mb) {
		return new FluidTank(Fluids.EMPTY, mb);
	}

	public static boolean canCombine(FluidTank tank, FluidTank otherTank) {
		return tank.isOf(otherTank.fluid());
	}

	private boolean isOf(Fluid fluid) {
		return fluid() == fluid;
	}

	public Fluid fluid() {
		return fluid;
	}

	public int maxMb() {
		return maxMb;
	}

	public int mb() {
		return mb;
	}

	public void setMb(int mb) {
		this.mb = mb;
	}

	public FluidTank split(int mb) {
		int i = Math.min(mb, maxMb);
		FluidTank tank = copy();
		tank.setMb(i);
		drain(i);
		return tank;
	}

	public void drain(int mb) {
		drip(-mb);
	}

	public void drip(int mb) {
		setMb(mb() + mb);
	}

	public FluidTank copy() {
		if (isEmpty()) {
			return FluidTank.empty(maxMb());
		} else {
			return new FluidTank(fluid(), maxMb(), mb());
		}
	}

	public boolean isEmpty() {
		if (fluid == null) {
			return true;
		} else if (fluid == Fluids.EMPTY) {
			return true;
		} else {
			return mb <= 0;
		}
	}
}
