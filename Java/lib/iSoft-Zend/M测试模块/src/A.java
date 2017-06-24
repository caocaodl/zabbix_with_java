
import com.isoft.zend.ZendUtils;
import com.isoft.zend.ext.date.ZDateExport;

public class A {

	public static void main(String[] args) {
		long time1 = ZendUtils.time();
		System.out.println("Current Time:" + time1);
		long time2 = ZDateExport.strtotime("tomorrow");
		System.out.println(time2);
		System.out.println((time2 - time1) / 3600);
		System.out.println(ZendUtils.date("d M Y H:i:s", time2));
	}

}
