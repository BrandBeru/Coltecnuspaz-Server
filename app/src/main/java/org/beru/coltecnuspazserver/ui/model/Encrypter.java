package org.beru.coltecnuspazserver.ui.model;

import android.util.Base64;

public class Encrypter {
    public static String encrypt(String toEncrypt){
        return Base64.encodeToString(toEncrypt.getBytes(),22);
    }
    public static String decrypt(String toDecrypt){
        byte[] decode = Base64.decode(toDecrypt.getBytes(), 22);

        return new String(decode);
    }
}
