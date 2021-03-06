package com.projectzed.mod.tileentity.generator;

import java.util.HashMap;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import com.projectzed.api.energy.source.EnumType;
import com.projectzed.api.energy.source.Source;
import com.projectzed.api.tileentity.generator.AbstractTileEntityGenerator;
import com.projectzed.mod.ProjectZed;
import com.projectzed.mod.handler.PacketHandler;
import com.projectzed.mod.handler.message.MessageTileEntityGenerator;

/**
 * Class used to calculate and generate power through
 * nuclear fusion.
 * 
 * @author hockeyhurd
 * @version Nov 24, 2014
 */
public class TileEntityNuclear extends AbstractTileEntityGenerator {

	/** Variable tracking whether to use fusion or fission. */
	private boolean fusionMode;
	private boolean poweredLastUpdate = false;
	private int burnTime = 0;
	
	private byte placeDir, size, rel;
	private Block[] blocksArray;
	
	public TileEntityNuclear() {
		super("nuclearController");
		this.maxStored = (int) 1e7;
	}
	
	/**
	 * Sets direction of placed nuclear controller.
	 * 
	 * @param dir = direction.
	 * @param size = expected size of chamber.
	 */
	public void setPlaceDir(byte dir, byte size, byte rel) {
		this.placeDir = dir;
		this.size = size;
		this.rel = rel;
		this.blocksArray = new Block[(int)(size * size * size)];
	}
	
	/**
	 * @return placed direction.
	 */
	public byte getPlaceDir() {
		return this.placeDir;
	}
	
	/**
	 * @return expected chamber size.
	 */
	public byte getChamberSize() {
		return this.size;
	}

	/* (non-Javadoc)
	 * @see com.projectzed.api.tileentity.generator.AbstractTileEntityGenerator#getSizeInventory()
	 */
	public int getSizeInventory() {
		return 1;
	}

	/* (non-Javadoc)
	 * @see com.projectzed.api.tileentity.generator.AbstractTileEntityGenerator#getInventoryStackLimit()
	 */
	public int getInventoryStackLimit() {
		return 64;
	}

	/* (non-Javadoc)
	 * @see com.projectzed.api.tileentity.generator.AbstractTileEntityGenerator#initContentsArray()
	 */
	protected void initContentsArray() {
		this.slots = new ItemStack[1];
	}

	/* (non-Javadoc)
	 * @see com.projectzed.api.tileentity.generator.AbstractTileEntityGenerator#initSlotsArray()
	 */
	protected void initSlotsArray() {
	}

	/* (non-Javadoc)
	 * @see com.projectzed.api.tileentity.generator.AbstractTileEntityGenerator#isItemValidForSlot(int, net.minecraft.item.ItemStack)
	 */
	public boolean isItemValidForSlot(int slot, ItemStack stack) {
		return true;
	}

	/* (non-Javadoc)
	 * @see com.projectzed.api.tileentity.generator.AbstractTileEntityGenerator#getAccessibleSlotsFromSide(int)
	 */
	public int[] getAccessibleSlotsFromSide(int side) {
		return new int[] { 0 };
	}

	/* (non-Javadoc)
	 * @see com.projectzed.api.tileentity.generator.AbstractTileEntityGenerator#canInsertItem(int, net.minecraft.item.ItemStack, int)
	 */
	public boolean canInsertItem(int slot, ItemStack stack, int side) {
		return false;
	}

	/* (non-Javadoc)
	 * @see com.projectzed.api.tileentity.generator.AbstractTileEntityGenerator#canExtractItem(int, net.minecraft.item.ItemStack, int)
	 */
	public boolean canExtractItem(int slot, ItemStack stack, int side) {
		return false;
	}

	/* (non-Javadoc)
	 * @see com.projectzed.api.tileentity.generator.AbstractTileEntityGenerator#defineSource()
	 */
	public void defineSource() {
		this.source = new Source(EnumType.FISSION);
	}
	
	/**
	 * Handles setting the proper source of this te.
	 * @param type = type of source.
	 */
	public void setSource(EnumType type) {
		setSource(type, 1.0f);
	}
	
	/**
	 * Handles setting the proper source of this te.
	 * @param type = type of source.
	 * @param modifier = modifier amount to offset energy output.
	 */
	public void setSource(EnumType type, float modifier) {
		this.source = new Source(type, modifier);
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.projectzed.api.tileentity.generator.AbstractTileEntityGenerator#canProducePower()
	 */
	@Override
	public boolean canProducePower() {
		boolean flag = false;
		if (blocksArray == null || blocksArray.length == 0 /*|| worldObj.getTotalWorldTime() % 20L != 0*/ || worldObj.isRemote) return false;
		
		else {
			// TODO: Varify offsets such that it works past 3x3x3 reaction chamber.
			
			int xp = this.xCoord - (placeDir == 1 ? 2 : (placeDir == 3 ? 0 : 1));
			int yp = this.yCoord + ((size - 1) / 2);
			int zp = this.zCoord - (placeDir == 3 ? 1 : (placeDir == 2 ? 2 : (placeDir == 1 ? 1 : 0)));
			int counter = 0;
			boolean show = false;

			if (show) {
				System.out.println("1: (" + (xp) + ", " + (zp) + ")");
				System.out.println("2: (" + (xp + size - 1) + ", " + (zp + size - 1) + ")");
			}
			
			HashMap<Block, Integer> mapping = new HashMap<Block, Integer>();
			
			for (int y = 0; y < size; y++) {
				for (int x = 0; x < size; x++) {
					for (int z = 0; z < size; z++) {
						Block b = worldObj.getBlock(xp + x, yp - y, zp + z);
						this.blocksArray[counter++] = b;
						
						if (!mapping.containsKey(b)) mapping.put(b, 1);
						else mapping.put(b, mapping.get(b) + 1);
					}
				}
			}
			
			if (mapping.size() > 0) {
				/*for (Entry<Block, Integer> b : mapping.entrySet()) {
					System.out.println(b.getKey().getUnlocalizedName() + ", " + b.getValue());
				}*/
				
				// TODO: Make this scalable past 3x3x3 reaction chamber!
				boolean[] checks = new boolean[5];
				if (mapping.containsKey(ProjectZed.nuclearChamberWall) && mapping.get(ProjectZed.nuclearChamberWall) == 12) checks[0] = true;
				if (mapping.containsKey(ProjectZed.nuclearChamberLock) && mapping.get(ProjectZed.nuclearChamberLock) == 8) checks[1] = true;
				if (mapping.containsKey(ProjectZed.nuclearReactantCore) && mapping.get(ProjectZed.nuclearReactantCore) == 1) checks[2] = true;
				if (mapping.containsKey(ProjectZed.thickenedGlass) && mapping.get(ProjectZed.thickenedGlass) == 5) checks[3] = true;
				if (mapping.containsKey(ProjectZed.fissionController) && mapping.get(ProjectZed.fissionController) == 1 && size == 3) checks[4] = true;
				else if (mapping.containsKey(ProjectZed.fusionController) && mapping.get(ProjectZed.fusionController) == 1 && size > 3) checks[4] = true;
				
				boolean flag2 = true;
				for (boolean b : checks) {
					if (!b) {
						flag2 = false;
						break;
					}
				}

				flag = flag2;
				return flag;
			}
			
		}
		
		return flag;
	}

	// TODO: This needs to be changed to uranium / w/e this requires for fuel.
	/**
	 * Function used to get the item burn time from given itemstack.
	 * @param stack = stack to (try) to burn.
	 * @return length or burn time of stack if burnable, else returns false.
	 */
	protected static int getItemBurnTime(ItemStack stack) {
		if (stack == null) return 0;
		else {
			Item item = stack.getItem();

			if (item == ProjectZed.fullFuelRod && stack.getItemDamage() < stack.getMaxDamage()) return 1600;
			
			return 0;
		}
	}

	/**
	 * Function used to determine if item in slot is fuel.
	 * @return true if is fuel, else returns false.
	 */
	protected boolean isFuel() {
		return getItemBurnTime(this.slots[0]) > 0;
	}

	/**
	 * Method used to consume fuel in given slot.
	 */
	protected void consumeFuel() {
		if (this.isFuel()) {
			if (this.slots[0] == null) return;
			else {
				ItemStack stack = this.slots[0];
				if (stack.getItemDamage() < stack.getMaxDamage() - 1) {
					stack.setItemDamage(stack.getItemDamage() + 1);
					this.slots[0] = stack;
				}
				
				else this.slots[0] = new ItemStack(ProjectZed.emptyFuelRod, 1, 0);
			}
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.projectzed.api.tileentity.generator.AbstractTileEntityGenerator#generatePower()
	 */
	@Override
	public void generatePower() {
		if (poweredLastUpdate && this.stored + this.source.getEffectiveSize() <= this.maxStored && this.burnTime > 0) this.stored += this.source.getEffectiveSize();
		if (this.stored > this.maxStored) this.stored = this.maxStored; // Redundancy check.
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.projectzed.api.tileentity.generator.AbstractTileEntityGenerator#updateEntity()
	 */
	@Override
	public void updateEntity() {
		if (this.worldObj != null && !this.worldObj.isRemote) {
			
			// Small, yet significant optimization to call checking of multiblock structure 1/tick instead of 20/tick.
			if (this.worldObj.getTotalWorldTime() % 20L == 0) poweredLastUpdate = canProducePower();
			
			if (this.slots[0] != null && isFuel() && poweredLastUpdate) {
				if (this.burnTime == 0) {
					this.burnTime = getItemBurnTime(this.slots[0]);
					consumeFuel();
				}
			}

			// this.powerMode = this.burnTime > 0;
			if (this.burnTime > 0) this.burnTime--;

			PacketHandler.INSTANCE.sendToAll(new MessageTileEntityGenerator(this));
		}
		super.updateEntity();
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.projectzed.api.tileentity.generator.AbstractTileEntityGenerator#readFromNBT(net.minecraft.nbt.NBTTagCompound)
	 */
	public void readFromNBT(NBTTagCompound comp) {
		super.readFromNBT(comp);
		int time = comp.getInteger("ProjectZedBurnTime");
		this.burnTime = time > 0 ? time : 0;
		
		byte dir = comp.getByte("ProjectZedNuclearDir");
		this.placeDir = (byte) (dir >= 0 && dir < 6 ? dir : this.blockMetadata);

		byte rel = comp.getByte("ProjectZedNuclearRel");
		this.rel = rel > -4 && rel < 4 ? rel : 0;
		
		byte size = comp.getByte("ProjectZedNuclearSize");
		this.size = size > 0 && size <= 7 ? size : 0;
		
		this.blocksArray = size > 0 ? new Block[(int)(size * size * size)] : null;
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.projectzed.api.tileentity.generator.AbstractTileEntityGenerator#writeToNBT(net.minecraft.nbt.NBTTagCompound)
	 */
	public void writeToNBT(NBTTagCompound comp) {
		super.writeToNBT(comp);
		comp.setInteger("ProjectZedBurnTime", this.burnTime);
		comp.setByte("ProjectZedNuclearDir", this.placeDir);
		comp.setByte("ProjectZedNuclearRel", this.rel);
		comp.setByte("ProjectZedNuclearSize", this.size);
	}
	
}
