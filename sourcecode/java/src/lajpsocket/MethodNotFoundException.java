//-----------------------------------------------------------
// LAJP-java(socket) (2009-09 http://code.google.com/p/lajp/)
// 
// Version: 9.09.01
// License: http://www.apache.org/licenses/LICENSE-2.0
//-----------------------------------------------------------

package lajpsocket;

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
