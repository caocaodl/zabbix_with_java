package com.isoft.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import com.isoft.biz.util.BizError;
import com.isoft.dictionary.ErrorCodeEnum;

public class SerializeUtil {

    public static  byte[] encodeFast64(Object obj){
        try
        {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            OutputStream zos = new GZIPOutputStream(baos);
            ObjectOutputStream oos = new ObjectOutputStream(zos);
            oos.writeObject(obj);
            oos.close();
            zos.close();
            baos.close();

            return Base64Util.encodeToByte(baos.toByteArray(),false);
        } catch (IOException e) {
            throw BizError.createBizLogicException(ErrorCodeEnum.BIZLOGIC_UNKNOWN, e);
        }
    }

    public static Object decodeFast64(byte[] s){
        try
        {
            ByteArrayInputStream decodedStream = new ByteArrayInputStream( Base64Util.decodeFast( s ) );
            InputStream unzippedStream = new GZIPInputStream(decodedStream);
            ObjectInputStream ois = new ObjectInputStream(unzippedStream);
            Object obj = ois.readObject();
            ois.close();
            unzippedStream.close();
            decodedStream.close();
            return obj;
        }
        catch (Exception e)
        {
            throw BizError.createBizLogicException(ErrorCodeEnum.BIZLOGIC_UNKNOWN, e);
        }
    }
}
