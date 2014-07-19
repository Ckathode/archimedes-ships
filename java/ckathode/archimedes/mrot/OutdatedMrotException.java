package ckathode.archimedes.mrot;

public class OutdatedMrotException extends RuntimeException
{
	private static final long	serialVersionUID	= 314159L;
	
	public OutdatedMrotException(String version)
	{
		super(version);
	}
}
