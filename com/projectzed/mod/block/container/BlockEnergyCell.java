package com.projectzed.mod.block.container;

import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

import com.projectzed.api.block.AbstractBlockContainer;
import com.projectzed.api.tileentity.container.AbstractTileEntityContainer;
import com.projectzed.mod.ProjectZed;
import com.projectzed.mod.proxy.ClientProxy;
import com.projectzed.mod.registry.TileEntityRegistry;
import com.projectzed.mod.tileentity.container.TileEntityEnergyBankBase;

import cpw.mods.fml.common.network.internal.FMLNetworkHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

/**
 * Class containing block code for energy bank cell.
 * 
 * @author hockeyhurd
 * @version Dec 3, 2014
 */
public class BlockEnergyCell extends AbstractBlockContainer {

	private final byte TIER;
	
	/**
	 * @param material
	 * @param assetDir
	 * @param name
	 */
	public BlockEnergyCell(Material material, String name) {
		super(material, ProjectZed.assetDir, name);
		this.setBlockName(name);
		this.TIER = Byte.parseByte("" + name.charAt(name.length() - 1));
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.projectzed.api.block.AbstractBlockContainer#registerBlockIcons(net.minecraft.client.renderer.texture.IIconRegister)
	 */
	@SideOnly(Side.CLIENT)
	public void registerBlockIcons(IIconRegister reg) {
		// blockIcon = reg.registerIcon(this.assetDir + name);
		blockIcon = reg.registerIcon(this.assetDir + "energyCellGeneric_icon");
	}
	
	@SideOnly(Side.CLIENT)
	public boolean renderAsNormalBlock() {
		return false;
	}
	
	@SideOnly(Side.CLIENT)
	public boolean isOpaqueCube() {
		return false;
	}
	
	@SideOnly(Side.CLIENT)
	public boolean canRenderInPass(int pass) {
		ClientProxy.renderPass = pass;
		return true;
	}
	
	@SideOnly(Side.CLIENT)
	public int getRenderBlockPass() {
		return 1;
	}
	
	@SideOnly(Side.CLIENT)
	public int getRenderType() {
		return ClientProxy.energyCell;
	}
	
	/* (non-Javadoc)
	 * @see com.projectzed.api.block.AbstractBlockContainer#getTileEntity()
	 */
	@Override
	protected AbstractTileEntityContainer getTileEntity() {
		TileEntityEnergyBankBase te = new TileEntityEnergyBankBase();
		
		if (this.TIER > 0) te.setTier(this.TIER);
		return te;
	}

	/* (non-Javadoc)
	 * @see com.projectzed.api.block.AbstractBlockContainer#onBlockActivated(net.minecraft.world.World, int, int, int, net.minecraft.entity.player.EntityPlayer, int, float, float, float)
	 */
	@Override
	public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float hitX, float hitY, float hitZ) {
		if (world.isRemote) return true;

		else {
			TileEntityEnergyBankBase te = (TileEntityEnergyBankBase) world.getTileEntity(x, y, z);
			if (te != null) FMLNetworkHandler.openGui(player, ProjectZed.instance, TileEntityRegistry.instance().getID(TileEntityEnergyBankBase.class), world, x, y, z);
			return true;
		}
	}

	/* (non-Javadoc)
	 * @see com.projectzed.api.block.AbstractBlockContainer#doBreakBlock(net.minecraft.world.World, int, int, int)
	 */
	@Override
	protected void doBreakBlock(World world, int x, int y, int z) {
		TileEntityEnergyBankBase te = new TileEntityEnergyBankBase();
		ProjectZed.logHelper.info("Stored:", te.getEnergyStored());
	}

}
