package com.projectzed.mod.block.container;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.Packet;
import net.minecraftforge.common.util.ForgeDirection;
import cofh.api.energy.IEnergyHandler;
import cofh.api.energy.IEnergyStorage;

import com.projectzed.api.storage.IEnergyContainer;
import com.projectzed.api.tileentity.container.AbstractTileEntityContainer;
import com.projectzed.api.tileentity.machine.AbstractTileEntityMachine;
import com.projectzed.mod.handler.PacketHandler;
import com.projectzed.mod.handler.message.MessageTileEntityRFBridge;
import com.projectzed.mod.util.Reference;

/**
 * Class containing te code for RF Bridge.
 * 
 * @author hockeyhurd
 * @version Nov 29, 2014
 */
public class TileEntityRFBridge extends AbstractTileEntityContainer implements IEnergyStorage {

	private int maxStorageRF;
	public int storedRF;
	private int importRateRF, exportRateRF;
	private boolean flip = false;

	public TileEntityRFBridge() {
		super("bridgeRF");
		this.maxStorage /= 10;
		this.importRate = Reference.Constants.BASE_PIPE_TRANSFER_RATE * 4;
		this.exportRate = Reference.Constants.BASE_PIPE_TRANSFER_RATE / 2 * 4;

		this.maxStorageRF = convertAndRoundToRF(this.maxStorage);
		this.importRateRF = convertAndRoundToRF(this.exportRate);
		this.exportRateRF = convertAndRoundToRF(this.importRate);
	}
	
	/**
	 * Set whether to receive rf or mcu.
	 * @param flip = mode to set (receive == true ? McU --> RF : RF --> McU).
	 */
	public void setFlip(boolean flip) {
		this.flip = flip;
	}

	private int convertAndRoundToRF(int mcu) {
		return (int) Math.floor(Reference.Constants.getRFFromMcU(mcu));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.projectzed.api.tileentity.container.AbstractTileEntityContainer#getSizeInventory()
	 */
	public int getSizeInventory() {
		return 0;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.projectzed.api.tileentity.container.AbstractTileEntityContainer#getInventoryStackLimit()
	 */
	public int getInventoryStackLimit() {
		return 0;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.projectzed.api.tileentity.container.AbstractTileEntityContainer#initContentsArray()
	 */
	protected void initContentsArray() {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.projectzed.api.tileentity.container.AbstractTileEntityContainer#initSlotsArray()
	 */
	protected void initSlotsArray() {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.projectzed.api.tileentity.container.AbstractTileEntityContainer#isItemValidForSlot(int, net.minecraft.item.ItemStack)
	 */
	public boolean isItemValidForSlot(int slot, ItemStack stack) {
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.projectzed.api.tileentity.container.AbstractTileEntityContainer#getAccessibleSlotsFromSide(int)
	 */
	public int[] getAccessibleSlotsFromSide(int side) {
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.projectzed.api.tileentity.container.AbstractTileEntityContainer#canInsertItem(int, net.minecraft.item.ItemStack, int)
	 */
	public boolean canInsertItem(int slot, ItemStack stack, int side) {
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.projectzed.api.tileentity.container.AbstractTileEntityContainer#canExtractItem(int, net.minecraft.item.ItemStack, int)
	 */
	public boolean canExtractItem(int slot, ItemStack stack, int side) {
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.projectzed.api.tileentity.container.AbstractTileEntityContainer#getMaxImportRate()
	 */
	public int getMaxImportRate() {
		return this.importRate;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.projectzed.api.tileentity.container.AbstractTileEntityContainer#getMaxExportRate()
	 */
	public int getMaxExportRate() {
		return this.exportRate;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.projectzed.api.tileentity.container.AbstractTileEntityContainer#requestPower(com.projectzed.api.storage.IEnergyContainer, int)
	 */
	public int requestPower(IEnergyContainer cont, int amount) {
		if (cont != null && this.exportRate >= amount && this.stored - amount >= 0 && flip) {
			this.stored -= amount;
			return amount;
		}

		else return 0;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.projectzed.api.tileentity.container.AbstractTileEntityContainer#importContents()
	 */
	protected void importContents() {
		if (this.stored >= this.maxStorage) {
			this.stored = this.maxStorage;
			return;
		}
		
		if (flip) return;

		int x = this.xCoord;
		int y = this.yCoord;
		int z = this.zCoord;
		List<IEnergyContainer> containers = new ArrayList<IEnergyContainer>();

		// -x
		if (worldObj.getTileEntity(x - 1, y, z) != null && worldObj.getTileEntity(x - 1, y, z) instanceof IEnergyContainer && !(worldObj.getTileEntity(x - 1, y, z) instanceof AbstractTileEntityMachine)) {
			IEnergyContainer cont = (IEnergyContainer) worldObj.getTileEntity(x - 1, y, z);
			containers.add(cont);
		}

		// +x
		if (worldObj.getTileEntity(x + 1, y, z) != null && worldObj.getTileEntity(x + 1, y, z) instanceof IEnergyContainer && !(worldObj.getTileEntity(x + 1, y, z) instanceof AbstractTileEntityMachine)) {
			IEnergyContainer cont = (IEnergyContainer) worldObj.getTileEntity(x + 1, y, z);
			containers.add(cont);
		}

		// -y
		if (worldObj.getTileEntity(x, y - 1, z) != null && worldObj.getTileEntity(x, y - 1, z) instanceof IEnergyContainer && !(worldObj.getTileEntity(x, y - 1, z) instanceof AbstractTileEntityMachine)) {
			IEnergyContainer cont = (IEnergyContainer) worldObj.getTileEntity(x, y - 1, z);
			containers.add(cont);
		}

		// +y
		if (worldObj.getTileEntity(x, y + 1, z) != null && worldObj.getTileEntity(x, y + 1, z) instanceof IEnergyContainer && !(worldObj.getTileEntity(x, y + 1, z) instanceof AbstractTileEntityMachine)) {
			IEnergyContainer cont = (IEnergyContainer) worldObj.getTileEntity(x, y + 1, z);
			containers.add(cont);
		}

		// -z
		if (worldObj.getTileEntity(x, y, z - 1) != null && worldObj.getTileEntity(x, y, z - 1) instanceof IEnergyContainer && !(worldObj.getTileEntity(x, y, z - 1) instanceof AbstractTileEntityMachine)) {
			IEnergyContainer cont = (IEnergyContainer) worldObj.getTileEntity(x, y, z - 1);
			containers.add(cont);
		}

		// +z
		if (worldObj.getTileEntity(x, y, z + 1) != null && worldObj.getTileEntity(x, y, z + 1) instanceof IEnergyContainer && !(worldObj.getTileEntity(x, y, z + 1) instanceof AbstractTileEntityMachine)) {
			IEnergyContainer cont = (IEnergyContainer) worldObj.getTileEntity(x, y, z + 1);
			containers.add(cont);
		}

		if (containers.size() > 0) {
			for (IEnergyContainer c : containers) {
				if (this.stored >= this.maxStorage) {
					this.stored = this.maxStorage;
					break;
				}
				if (c.getEnergyStored() - c.getMaxExportRate() > 0 && this.stored + c.getMaxExportRate() <= this.maxStorage) this.stored += c.requestPower(this, c.getMaxExportRate());
			}
		}

		containers.removeAll(Collections.EMPTY_LIST);
	}
	
	protected void convertEnergy() {
		if (this.stored - this.exportRate > 0 && this.storedRF + this.importRateRF <= this.maxStorageRF && !flip) {
			this.stored -= this.exportRate;
			this.storedRF += this.importRateRF;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.projectzed.api.tileentity.container.AbstractTileEntityContainer#exportContents()
	 */
	protected void exportContents() {
		if (this.storedRF > 0 && !flip) {
			int x = this.xCoord;
			int y = this.yCoord;
			int z = this.zCoord;
			
			if (worldObj.getTileEntity(x - 1, y, z) instanceof IEnergyHandler) {
				IEnergyHandler hand = (IEnergyHandler) worldObj.getTileEntity(x - 1, y, z);
				hand.receiveEnergy(ForgeDirection.WEST, this.extractEnergy(this.exportRateRF, false), false);
			}
		}
	}

	// RF STUFF:
	@Override
	public int receiveEnergy(int maxReceive, boolean simulate) {
		/*
		 * int energyReceved = Math.min(this.maxStorageRF - this.storedRF, Math.min(this.importRateRF, maxReceive)); if (!simulate) this.storedRF +=
		 * energyReceved;
		 * 
		 * return energyReceved;
		 */

		return 0;
	}

	@Override
	public int extractEnergy(int maxExtract, boolean simulate) {
		int energyExtracted = Math.min(this.storedRF, Math.min(this.exportRateRF, maxExtract));
		if (!simulate) this.storedRF -= energyExtracted;

		return energyExtracted;
	}

	@Override
	public int getMaxEnergyStored() {
		return this.maxStorageRF;
	}
	
	public void setRFStored(int amount) {
		this.storedRF = amount;
	}
	
	public void updateEntity() {
		importContents();
		convertEnergy();
		exportContents();
		PacketHandler.INSTANCE.sendToAll(new MessageTileEntityRFBridge(this));
		// System.out.println(this.stored + ", " + this.storedRF);
		
		super.updateEntity();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.projectzed.api.tileentity.container.AbstractTileEntityContainer#readFromNBT(net.minecraft.nbt.NBTTagCompound)
	 */
	public void readFromNBT(NBTTagCompound comp) {
		super.readFromNBT(comp);
		this.storedRF = comp.getInteger("ProjectZedRF");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.projectzed.api.tileentity.container.AbstractTileEntityContainer#writeToNBT(net.minecraft.nbt.NBTTagCompound)
	 */
	public void writeToNBT(NBTTagCompound comp) {
		super.writeToNBT(comp);
		comp.setInteger("ProjectZedRF", this.storedRF);
	}
	
	@Override
	public Packet getDescriptionPacket() {
		return PacketHandler.INSTANCE.getPacketFrom(new MessageTileEntityRFBridge(this));
	}

}