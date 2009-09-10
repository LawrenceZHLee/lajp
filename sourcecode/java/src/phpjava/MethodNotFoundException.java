package phpjava;

/**
 * 方法找不到异常
 * @author diaoyf
 *
 */
public class MethodNotFoundException extends Exception
{
	private static final long	serialVersionUID	= 1L;
	public MethodNotFoundException()
	{
		super();
	}
	public MethodNotFoundException(String exMessage)
	{
		super(exMessage);
	}
}
