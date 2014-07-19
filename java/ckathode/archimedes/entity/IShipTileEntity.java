package ckathode.archimedes.entity;

public interface IShipTileEntity
{
	/**
	 * @param entityship
	 *            Ship
	 * @param x
	 *            ,
	 * @param y
	 *            ,
	 * @param z
	 *            The original tile entity coordinates.
	 */
	public void setParentShip(EntityShip entityship, int x, int y, int z);
	
	public EntityShip getParentShip();
}
