package com.gt.core.utils;

import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;
import org.jasypt.encryption.pbe.config.EnvironmentPBEConfig;

/**
 * 配置文件加密工具类
 * @author Administrator
 *
 */
public class JasyptUtil {

	private static String password = "zlszfycxw2019";
	
	public static void main(String[] args) throws Exception {
		System.out.println(encrypt("root"));
        System.out.println(encrypt("jdbc:mysql://106.53.53.242:3306/demo?useUnicode=true&characterEncoding=utf8&autoReconnect=true&useSSL=false&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC"));
		System.out.println(encrypt("Zlsfznh@163.com"));
    }
	
	/**
	 * 加密
	 * @param str
	 * @throws Exception
	 */
	public static String encrypt(String str) throws Exception {
        StandardPBEStringEncryptor standardPBEStringEncryptor = new StandardPBEStringEncryptor();
        EnvironmentPBEConfig config = new EnvironmentPBEConfig();
 
        config.setAlgorithm("PBEWithMD5AndDES");          // 加密的算法，这个算法是默认的
        config.setPassword(password);                        // 加密的密钥
        standardPBEStringEncryptor.setConfig(config);

        String encryptedText = standardPBEStringEncryptor.encrypt(str);
        return encryptedText;
    }
	
    /**
     * 解密
     * @throws Exception
     */
    public static String decrypt(String str) throws Exception {
        StandardPBEStringEncryptor standardPBEStringEncryptor = new StandardPBEStringEncryptor();
        EnvironmentPBEConfig config = new EnvironmentPBEConfig();
 
        config.setAlgorithm("PBEWithMD5AndDES");
        config.setPassword(password);
        standardPBEStringEncryptor.setConfig(config);
        String plainText = standardPBEStringEncryptor.decrypt(str);
        return plainText;
    }
	
}
