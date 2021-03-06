package com.projectzed.mod.util;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Set;

import com.hockeyhurd.api.util.LogHelper;

import cpw.mods.fml.common.Loader;

/**
 * Class used as a helper for determining what mods
 * are loaded so that I can make appropriate changes for my
 * blocks, items, machines, etc.
 * 
 * @author hockeyhurd
 * @version Nov 29, 2014
 */
public class ModsLoadedHelper {

	/** Variable for whether Thermal Expansion Exists */
	public boolean te4Loaded = false;
	
	/** Mapping containing name and flag of existence. */
	private HashMap<String, Boolean> mapping;
	
	/** static instance of this class */
	private static ModsLoadedHelper mlh = new ModsLoadedHelper(); 
	
	private ModsLoadedHelper() {
	}
	
	/**
	 * Getter for the single instance of this class.
	 * @return class instance object.
	 */
	public static ModsLoadedHelper instance() {
		return mlh;
	}

	/**
	 * Function used to determine whether a mod by id exists.
	 * <br>Shortened version of Loaded.isModLoaded(modid)
	 * @param modid = modid by string name.
	 * @return true if exists, else return false.
	 */
	private boolean isModLoaded(String modid) {
		return Loader.isModLoaded(modid);
	}
	
	/**
	 * Method when called initializes all variables and mappings.
	 */
	public void init() {
		if (isModLoaded("ThermalExpansion")) te4Loaded = true;
		
		initMapping();
	}
	
	/**
	 * Method used to log the loading and init of this class.
	 * @param log = logger to use.
	 */
	public void logFindings(LogHelper log) {
		log.info("Detecting other soft-dependent mods.");
		
		Iterator iter = getEntries().iterator();
		do {
			Entry<String, Boolean> current = (Entry<String, Boolean>) iter.next();
			if (current.getValue()) log.info(current.getKey(), "detected! Wrapping into mod!");
			else log.warn(current.getKey(), "not detected!");
		}
		while (iter.hasNext());
		
		log.info("Finished detecting soft-dependent mods.");
	}

	/**
	 * Method used to init the mapping and add objects to said mapping.
	 */
	private void initMapping() {
		mapping = new HashMap<String, Boolean>();
		
		mapping.put("Thermal Expansion", te4Loaded);
	}
	
	/**
	 * Function used to get all entries in the set of mapping.
	 * @return entries in mapping if not null, else returns null.
	 */
	private final Set<Entry<String, Boolean>> getEntries() {
		return mlh.mapping.entrySet();
	}

}
