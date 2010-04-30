//-----------------------------------------------------------
// LAJP-java (2009-09 http://code.google.com/p/lajp/)
// 
// License: http://www.apache.org/licenses/LICENSE-2.0
//-----------------------------------------------------------

package lajp;
import java.util.ArrayList;

/**
 * 参数节点
 * @author diaoyf
 *
 */
public class ArgsNode
{
	/**
	 * 节点类型
	 * <li>i：整形</li>
	 * <li>d：浮点形</li>
	 * <li>b：布尔形</li>
	 * <li>s：字符形</li>
	 * <li>a：数组</li>
	 * <li>O：对象</li>
	 * <li>N：null</li>
	 */
	String type;
	
	/** 节点下标名称, php关联数组key或对象属性名 */
	String name;
	
	/**
	 * 节点值
	 * <li>整形: int</li>
	 * <li>浮点形: double</li>
	 * <li>布尔形: boolean</li>
	 * <li>字符形: java.lang.String</li>
	 * <li>数组: null</li>
	 * <li>对象: 对象类名</li>
	 */
	Object Value;
	/** 父 */
	ArgsNode father;
	/** 子集 */
	ArrayList<ArgsNode> subList = new ArrayList<ArgsNode>();
	
	/**
	 * 构造方法
	 * @param father
	 * @param type
	 * @param value
	 */
	private ArgsNode(ArgsNode father, String type, String name, Object value)
	{
		this.father = father;
		this.type = type;
		this.name = name;
		this.Value = value;
	}
	
	/**
	 * 创建一个节点, 父节点为null, 子节点为0长度ArrayList<Node>
	 * @param type
	 * @param value
	 * @return
	 */
	public static ArgsNode createNode(String type, String name, Object value)
	{
		ArgsNode node = new ArgsNode(null, type, name, value);
		
		return node;
	}
	
	/**
	 * 增加一个子节点
	 * @param childNode
	 */
	public void addChild(ArgsNode childNode)
	{
		if (childNode == null)
		{
			return;
		}
		
		childNode.father = this;
		subList.add(childNode);
	}
}
