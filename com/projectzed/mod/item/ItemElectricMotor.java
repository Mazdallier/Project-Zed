/* This file is part of Project-Zed. Project-Zed is free software: you can redistribute it and/or modify it under the terms of the GNU General Public 
* License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version. Project-Zed is 
* distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A 
* PARTICULAR PURPOSE. See the GNU General Public License for more details. You should have received a copy of the GNU General Public License along 
* with Project-Zed. If not, see <http://www.gnu.org/licenses/>
*/
package com.projectzed.mod.item;

import com.hockeyhurd.api.item.AbstractItemMetalic;
import com.projectzed.mod.ProjectZed;

/**
 * Item class for electricMotor.
 * 
 * @author hockeyhurd
 * @version May 5, 2015
 */
public class ItemElectricMotor extends AbstractItemMetalic {

	/**
	 * @param name
	 * @param assetDir
	 */
	public ItemElectricMotor(String name, String assetDir) {
		super(name, assetDir);
		this.setCreativeTab(ProjectZed.modCreativeTab);
	}

}