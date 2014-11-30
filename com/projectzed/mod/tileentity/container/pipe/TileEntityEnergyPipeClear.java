package com.projectzed.mod.tileentity.container.pipe;

import com.projectzed.api.source.EnumColor;

/**
 * 
 * @author hockeyhurd
 * @version Nov 15, 2014
 */
public class TileEntityEnergyPipeClear extends TileEntityEnergyPipeBase {

	public TileEntityEnergyPipeClear() {
		super();
		this.importRate *= 4;
		this.exportRate *= 4;
	}
	
	public EnumColor getColor() {
		return EnumColor.CLEAR;
	}

}
