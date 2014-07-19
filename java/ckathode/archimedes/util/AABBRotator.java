package ckathode.archimedes.util;

import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.Vec3;

public class AABBRotator
{
	private static Vec3	vec00, vec01, vec10, vec11;
	private static Vec3	vec0h, vec1h, vech0, vech1;
	static
	{
		vec00 = Vec3.createVectorHelper(0D, 0D, 0D);
		vec01 = Vec3.createVectorHelper(0D, 0D, 0D);
		vec10 = Vec3.createVectorHelper(0D, 0D, 0D);
		vec11 = Vec3.createVectorHelper(0D, 0D, 0D);
		
		vec0h = Vec3.createVectorHelper(0D, 0D, 0D);
		vec1h = Vec3.createVectorHelper(0D, 0D, 0D);
		vech0 = Vec3.createVectorHelper(0D, 0D, 0D);
		vech1 = Vec3.createVectorHelper(0D, 0D, 0D);
	}
	
	/**
	 * @param aabb
	 *            The axis aligned boundingbox to rotate
	 * @param ang
	 *            The angle to rotate the aabb in radians
	 */
	public static void rotateAABBAroundY(AxisAlignedBB aabb, double xoff, double zoff, float ang)
	{
		double y0 = aabb.minY;
		double y1 = aabb.maxY;
		
		vec00.xCoord = aabb.minX - xoff;
		vec00.zCoord = aabb.minZ - zoff;
		
		vec01.xCoord = aabb.minX - xoff;
		vec01.zCoord = aabb.maxZ - zoff;
		
		vec10.xCoord = aabb.maxX - xoff;
		vec10.zCoord = aabb.minZ - zoff;
		
		vec11.xCoord = aabb.maxX - xoff;
		vec11.zCoord = aabb.maxZ - zoff;
		
		vec00.rotateAroundY(ang);
		vec01.rotateAroundY(ang);
		vec10.rotateAroundY(ang);
		vec11.rotateAroundY(ang);
		
		vec0h.xCoord = (vec00.xCoord + vec01.xCoord) / 2D;
		vec0h.zCoord = (vec00.zCoord + vec01.zCoord) / 2D;
		
		vec1h.xCoord = (vec10.xCoord + vec11.xCoord) / 2D;
		vec1h.zCoord = (vec10.zCoord + vec11.zCoord) / 2D;
		
		vech0.xCoord = (vec00.xCoord + vec10.xCoord) / 2D;
		vech0.zCoord = (vec00.zCoord + vec10.zCoord) / 2D;
		
		vech1.xCoord = (vec01.xCoord + vec11.xCoord) / 2D;
		vech1.zCoord = (vec01.zCoord + vec11.zCoord) / 2D;
		
		aabb.setBounds(minX(), y0, minZ(), maxX(), y1, maxZ()).offset(xoff, 0F, zoff);
	}
	
	private static double minX()
	{
		return Math.min(Math.min(Math.min(vec0h.xCoord, vec1h.xCoord), vech0.xCoord), vech1.xCoord);
	}
	
	private static double minZ()
	{
		return Math.min(Math.min(Math.min(vec0h.zCoord, vec1h.zCoord), vech0.zCoord), vech1.zCoord);
	}
	
	private static double maxX()
	{
		return Math.max(Math.max(Math.max(vec0h.xCoord, vec1h.xCoord), vech0.xCoord), vech1.xCoord);
	}
	
	private static double maxZ()
	{
		return Math.max(Math.max(Math.max(vec0h.zCoord, vec1h.zCoord), vech0.zCoord), vech1.zCoord);
	}
}
