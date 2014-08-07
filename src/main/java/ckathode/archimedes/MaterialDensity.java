package ckathode.archimedes;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;

public class MaterialDensity
{
	public static final float			WATER_DENSITY		= 1.000F;
	public static final float			DEFAULT_DENSITY		= 0.34f;
	
	private static Map<Material, Float>	materialDensityMap	= new HashMap<Material, Float>();
	private static Map<String, Float>	blockDensityMap		= new HashMap<String, Float>();
	
	public static void addDensity(Material mat, float dens)
	{
		materialDensityMap.put(mat, Float.valueOf(dens));
	}
	
	public static void addDensity(Block block, float dens)
	{
		blockDensityMap.put(Block.blockRegistry.getNameForObject(block), Float.valueOf(dens));
	}
	
	public static float getDensity(Block block)
	{
		if (block == null) return DEFAULT_DENSITY;
		Float f = blockDensityMap.get(Block.blockRegistry.getNameForObject(block));
		if (f != null) return f.floatValue();
		return getDensity(block.getMaterial());
	}
	
	public static float getDensity(Material mat)
	{
		Float f = materialDensityMap.get(mat);
		if (f != null) return f.floatValue();
		return DEFAULT_DENSITY;
	}
}
