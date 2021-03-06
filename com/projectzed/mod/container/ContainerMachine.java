package com.projectzed.mod.container;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ICrafting;
import net.minecraft.inventory.Slot;
import net.minecraft.inventory.SlotFurnace;
import net.minecraft.item.ItemStack;

import com.projectzed.api.tileentity.machine.AbstractTileEntityMachine;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

/**
 * Generic container class for most machines.
 * 
 * @author hockeyhurd
 * @version Oct 23, 2014
 */
public class ContainerMachine extends Container {

	private AbstractTileEntityMachine te;
	private int stored;
	private boolean powerMode;

	/** Time left for this furnace to burn for. */
	public int lastBurnTime;

	/** The start time for this fuel. */
	public int lastItemBurnTime;

	/** How long time left before item is cooked. */
	public int lastCookTime;

	public ContainerMachine(InventoryPlayer inv, AbstractTileEntityMachine te) {
		this.te = te;
		addSlots(inv, te);
	}

	/**
	 * Adds all slots, player and container.
	 * @param inv = inventory.
	 * @param te = tile entity object.
	 */
	private void addSlots(InventoryPlayer inv, AbstractTileEntityMachine te) {
		// Add 'crafting' slots to container.
		this.addSlotToContainer(new Slot(te, 0, 41, 21));
		this.addSlotToContainer(new SlotFurnace(inv.player, te, 1, 121, 21));

		// Adds the player inventory to furnace's gui.
		for (int y = 0; y < 3; y++) {
			for (int x = 0; x < 9; x++) {
				this.addSlotToContainer(new Slot(inv, x + y * 9 + 9, 8 + x * 18, 84 + y * 18));
			}
		}

		// Adds the player hotbar slots to the gui.
		for (int i = 0; i < 9; i++) {
			this.addSlotToContainer(new Slot(inv, i, 8 + i * 18, 142)); // 198
		}
	}

	public void addCraftingToCrafters(ICrafting craft) {
		super.addCraftingToCrafters(craft);
		craft.sendProgressBarUpdate(this, 0, this.te.cookTime);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.minecraft.inventory.Container#canInteractWith(net.minecraft.entity.player.EntityPlayer)
	 */
	@Override
	public boolean canInteractWith(EntityPlayer player) {
		return true;
	}

	public void detectAndSendChanges() {
		super.detectAndSendChanges();
		this.stored = this.te.getEnergyStored();

		for (int i = 0; i < this.crafters.size(); i++) {
			ICrafting icrafting = (ICrafting) this.crafters.get(i);

			if (this.lastCookTime != this.te.cookTime) icrafting.sendProgressBarUpdate(this, 0, this.te.cookTime);
		}
	}

	@SideOnly(Side.CLIENT)
	public void updateProgressBar(int slot, int newVal) {
		if (slot == 0) this.te.cookTime = newVal;
	}

	/**
	 * Gets the TE instance.
	 * @return te object.
	 */
	public AbstractTileEntityMachine getTE() {
		return this.te;
	}

	public boolean mergeItemStack(ItemStack stack, int start, int end, boolean reverse) {
		return super.mergeItemStack(stack, start, end, reverse);
	}

	/**
	 * Player shift-clicking a slot.
	 * @see net.minecraft.inventory.Container#transferStackInSlot(net.minecraft.entity.player.EntityPlayer, int)
	 */
	public ItemStack transferStackInSlot(EntityPlayer player, int index) {
		ItemStack stack = null;
		Slot slot = (Slot) this.inventorySlots.get(index);
		if (slot != null && slot.getHasStack()) {
			ItemStack slotStack = slot.getStack();
			stack = slotStack.copy();
			if (index < te.getSizeInvenotry()) {
				if (!this.mergeItemStack(slotStack, te.getSizeInvenotry(), this.inventorySlots.size(), false)) return null;
			}
			
			else {
				if (!this.getSlot(0).isItemValid(slotStack) || !this.mergeItemStack(slotStack, 0, te.getSizeInvenotry(), false)) return null;
			}

			if (slotStack.stackSize == 0) slot.putStack((ItemStack) null);
			else slot.onSlotChanged();

			if (slotStack.stackSize == stack.stackSize) return null;
			slot.onPickupFromSlot(player, slotStack);
		}

		return stack;
	}

}
