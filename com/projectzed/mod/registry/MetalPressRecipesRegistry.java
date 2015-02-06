/* This file is part of Project-Zed. Project-Zed is free software: you can redistribute it and/or modify it under the terms of the GNU General Public 
* License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version. Project-Zed is 
* distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A 
* PARTICULAR PURPOSE. See the GNU General Public License for more details. You should have received a copy of the GNU General Public License along 
* with Project-Zed. If not, see <http://www.gnu.org/licenses/>
*/
package com.projectzed.mod.registry;

import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Set;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

import com.projectzed.mod.ProjectZed;

/**
 * Class containing code for initializing the metal press's smelting recipe list. <br>
 * NOTE: This class was closely followed to PulverizerRecipes.java by author hockeyhurd. <br>
 * For more info on this click here to view in repo: click <a href="http://goo.gl/L7oiKb">here</a>.
 * 
 * @author hockeyhurd
 * @version Dec 9, 2014
 */
public class MetalPressRecipesRegistry {

	private static HashMap<ItemStack, ItemStack> mapVanilla;
	private static HashMap<String, String> mapModded;
	private static Set<Entry<String, String>> mapSet;

	private MetalPressRecipesRegistry() {
	}

	/**
	 * Main init method for initializing all the things.
	 */
	public static void init() {
		mapVanilla = new HashMap<ItemStack, ItemStack>();
		mapModded = new HashMap<String, String>();

		// Normal mapping.
		mapVanilla.put(new ItemStack(ProjectZed.ingotAluminium, 1), new ItemStack(ProjectZed.sheetAluminium, 1));
		mapVanilla.put(new ItemStack(ProjectZed.mixedAlloy, 1), new ItemStack(ProjectZed.sheetReinforced, 1));
		
		// Fall back/modded mapping.
		mapModded.put("ingotIron", "plateIron");
		mapModded.put("ingotGold", "plateGold");
		mapModded.put("ingotTin", "plateTin");
		mapModded.put("ingotCopper", "plateCopper");
		mapModded.put("ingotBronze", "plateBronze");

		initEntries();
	}

	/**
	 * Method used to init entries mapping.
	 */
	private static void initEntries() {
		mapSet = mapModded.entrySet();
	}
	
	/**
	 * Get the attempted map of recipe list.
	 * 
	 * @return map.
	 */
	public static HashMap<ItemStack, ItemStack> getMap() {
		return mapVanilla;
	}

	/**
	 * Static function used to get output of said itemstack from internal mappings and contacting to/from ore dictionary.
	 * 
	 * @param stack = stact to reference.
	 * @return output as itemstack.
	 */
	public static ItemStack pressList(ItemStack stack) {
		boolean flag = false;
		boolean flag2 = false;
		ItemStack temp = null;

		/*
		 * First attempt to see we have data handling for the given stack in the vanilla mapping, if not continue and use the fallback mapping
		 * (modded).
		 */
		if (mapVanilla.size() > 0) {
			for (ItemStack currentStack : mapVanilla.keySet()) {
				if (stack.getItem() == currentStack.getItem() && stack.getItemDamage() == currentStack.getItemDamage()) {
					temp = mapVanilla.get(currentStack);
					flag = true;
					break;
				}
			}
		}

		// If found data in vanilla mapping, return now, no need to continue.
		if (flag && temp != null) return temp;

		// Else not found, prepare data for collection from the Ore Dictionary.
		if (mapModded.size() > 0) {
			int currentID = OreDictionary.getOreID(stack);
			int id, id2; 
			String current = "", current2 = "";
			
			for (int i = 0; i < OreDictionary.getOreNames().length; i++) {
				for (Entry<String, String> s : mapSet) {
					current = s.getKey();
					current2 = s.getValue();
					id = OreDictionary.getOreID(current);
					id2 = OreDictionary.getOreID(current2);

					if (current.equals(OreDictionary.getOreNames()[i]) && currentID == id) flag = true;
					if (current2.equals(OreDictionary.getOreNames()[i]) && currentID == id2) flag2 = true;
					
					if (flag && flag2) break;
				}

				if (flag && flag2) {
					Block block = null;
					Item item = null;

					/*
					 * Checks if the stack is instance of Block or instance of Item. In theory, only one of the two objects should be null at a given
					 * instance; hence returning the correct stack size below.
					 */
					if (current.contains("ore")) block = Block.getBlockById(OreDictionary.getOreID(current));
					else if (current.contains("ingot")) item = Item.getItemById(OreDictionary.getOreID(current));
					temp = OreDictionary.getOres(current2).get(0);

					// Somewhat overly complicated but makes more sense writing like this imo.
					temp.stackSize = block != null && item == null ? 2 : (block == null && item != null ? 1 : 1);
					break;
				}
			}
		}

		// If found and stored in variable temp while != null, return data.
		if (flag && temp != null) {
			mapVanilla.put(stack, temp);
			return temp;
		}

		else return (ItemStack) null;
	}

}
