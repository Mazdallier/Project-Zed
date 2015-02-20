/* This file is part of Project-Zed. Project-Zed is free software: you can redistribute it and/or modify it under the terms of the GNU General Public 
* License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version. Project-Zed is 
* distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A 
* PARTICULAR PURPOSE. See the GNU General Public License for more details. You should have received a copy of the GNU General Public License along 
* with Project-Zed. If not, see <http://www.gnu.org/licenses/>
*/
package com.projectzed.mod.block.container;

import net.minecraft.block.material.Material;

import com.projectzed.api.tileentity.container.AbstractTileEntityFluidContainer;
import com.projectzed.mod.proxy.ClientProxy;
import com.projectzed.mod.tileentity.container.TileEntityFluidTankTier0;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

/**
 * Tier 0 fluid tank block code.
 * 
 * @author hockeyhurd
 * @version Jan 24, 2015
 */
public class BlockTankTier0 extends AbstractBlockTankBase {

	/**
	 * @param material
	 */
	public BlockTankTier0(Material material) {
		super(material, "fluidTankTier0");
		this.tier = (byte) 0;
	}

	/*
	 * (non-Javadoc)
	 * @see com.projectzed.mod.block.container.BlockTankBase#getRenderType()
	 */
	@Override
	@SideOnly(Side.CLIENT)
	public int getRenderType() {
		return ClientProxy.fluidTankTier0;
	}

	/*
	 * (non-Javadoc)
	 * @see com.projectzed.mod.block.container.BlockTankBase#getTileEntity()
	 */
	@Override
	public AbstractTileEntityFluidContainer getTileEntity() {
		return new TileEntityFluidTankTier0();
	}


}