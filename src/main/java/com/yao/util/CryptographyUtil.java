package com.yao.util;

import org.apache.shiro.crypto.hash.Md5Hash;

import java.util.Hashtable;


public class CryptographyUtil {

	private static final String SALT = "yao";

	public static String md5(String str,String salt){
		return new Md5Hash(str,salt).toString();
	}
	
	public static void main(String[] args) {
		String password="yao_love.:#&yourself";
		
		System.out.println("Md5加密之后"+CryptographyUtil.md5(password, SALT));


		new ThreadLocal().set("bbb");

	}

	public static String getSalt() {
		return SALT;
	}


}
