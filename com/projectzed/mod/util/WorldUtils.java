package com.projectzed.mod.util;

import java.util.Random;

import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import com.projectzed.mod.ProjectZed;

/**
 * Class containing code for general purpose code in the mc world. <br>
 * <bold>NOTE:</bold> This class should/will be mostly static.
 * 
 * @author hockeyhurd
 * @version Dec 22, 2014
 */
public class WorldUtils {

	private WorldUtils() {
	}

	/**
	 * Creates an EntityItem object from given parameters.
	 * 
	 * @param world = world object as reference.
	 * @param x = x-pos.
	 * @param y = y-pos.
	 * @param z = z-pos
	 * @return EntityItem object.
	 */
	public static EntityItem createEntityItem(World world, int x, int y, int z) {
		return new EntityItem(world, x, y, z);
	}

	/**
	 * Creates an EntityItem object from given parameters.
	 * 
	 * @param world = world object as reference.
	 * @param x = x-pos.
	 * @param y = y-pos.
	 * @param z = z-pos
	 * @param stack = Itemstack to create from.
	 * @return EntityItem object.
	 */
	public static EntityItem createEntityItemStack(World world, int x, int y, int z, ItemStack stack) {
		return new EntityItem(world, x, y, z, stack);
	}

	/**
	 * Method used to add drop items to world more efficiently.
	 * 
	 * @param stacks = stack array object to drop.
	 * @param world = world object as reference.
	 * @param x = x-pos.
	 * @param y = y-pos.
	 * @param z = z-pos
	 */
	public static void addItemDrop(ItemStack stack, World world, int x, int y, int z) {
		addItemDrop(stack, world, x, y, z, null);
	}

	/**
	 * Method used to add drop items to world more efficiently.
	 * 
	 * @param stack = stack object to drop.
	 * @param world = world object as reference.
	 * @param x = x-pos.
	 * @param y = y-pos.
	 * @param z = z-pos
	 * @param random = random object to use.
	 */
	public static void addItemDrop(ItemStack stack, World world, int x, int y, int z, Random random) {
		addItemDrop(new ItemStack[] {
			stack
		}, world, x, y, z, random);
	}

	/**
	 * Method used to add drop items to world more efficiently.
	 * @param stacks = stack array object to drop.
	 * @param world = world object as reference.
	 * @param x = x-pos.
	 * @param y = y-pos.
	 * @param z = z-pos
	 */
	public static void addItemDrop(ItemStack[] stacks, World world, int x, int y, int z) {
		addItemDrop(stacks, world, x, y, z, null);
	}

	/**
	 * Method used to add drop items to world more efficiently.
	 * 
	 * @param stacks = stack array object to drop.
	 * @param world = world object as reference.
	 * @param x = x-pos.
	 * @param y = y-pos.
	 * @param z = z-pos
	 * @param random = random object to use.
	 */
	public static void addItemDrop(ItemStack[] stacks, World world, int x, int y, int z, Random random) {
		if (stacks == null || stacks.length == 0 || world == null || world.isRemote) {
			ProjectZed.logHelper.severe("Error attempting to add item drops to world!");
			return;
		}

		for (ItemStack stack : stacks) {
			if (stack != null) {
				if (random == null) world.spawnEntityInWorld(createEntityItemStack(world, x, y, z, stack));
				else world.spawnEntityInWorld(createEntityItemStack(world, x + random.nextInt(3), y + random.nextInt(3), z + random.nextInt(3), stack));
			}
		}
	}

}