package com.projectzed.mod.item.metals;

import com.hockeyhurd.api.item.AbstractItemMetalic;
import com.projectzed.mod.ProjectZed;

/**
 * 
 * @author hockeyhurd
 * @version Nov 6, 2014
 */
public class ItemIngotCopper extends AbstractItemMetalic {

	/**
	 * @param name
	 * @param assetDir
	 */
	public ItemIngotCopper(String name, String assetDir) {
		super(name, assetDir);
		this.setCreativeTab(ProjectZed.modCreativeTab);
	}

}
