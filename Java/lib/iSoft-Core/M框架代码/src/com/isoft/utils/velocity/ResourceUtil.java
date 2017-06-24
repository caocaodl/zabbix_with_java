package com.isoft.utils.velocity;

/**
 * 
 * <p>�з����: <b>S686</b> </p>
 * 
 * <p>Title: �ľ�֮��--Ӧ�ò㹤����--ResourceUtil</p>
 * <p>Description: ���ϵͳ��Դ·������</p>
 * <p>Copyright: Copyright (c) 2008  �ൺ��ҵ��ý���޹�˾</p>
 * <p>Company: �ൺ��ҵ��ý���޹�˾</p>
 * 
 * ����ʱ�� : 2006-8-16-9:45:17
 * @author lish
 * @version $Revision: 1.0 $
 *
 */
public class ResourceUtil {
    
    /**
     * ���Ӧ�õ�classpath��ʵ·��
     * @param clazz
     * @return
     */
    @SuppressWarnings("unchecked")
    public static String getClassPath(Class clazz){
        if(clazz == null)
            return ResourceUtil.class.getClassLoader().getResource("").getPath();
        else
            return clazz.getClassLoader().getResource("").getPath();
    }
    
    /**
     * ���classpathĿ¼���ļ���ȫ·��
     * @param clazz
     * @param fileName
     * @return
     */
    @SuppressWarnings("unchecked")
    public static String getFileRealPathOfClassPath(Class clazz,String fileName){
        if(clazz == null)
            return ResourceUtil.class.getClassLoader().getResource(fileName).getPath();
        else
            return clazz.getClassLoader().getResource(fileName).getPath();
    }
}
