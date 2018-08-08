package com.yao.util;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


public class StringUtil {


	public static boolean isEmpty(String str){
		if(str==null||"".equals(str.trim())){
			return true;
		}else{
			return false;
		}
	}
	

	public static boolean isNotEmpty(String str){
		if((str!=null)&&!"".equals(str.trim())){
			return true;
		}else{
			return false;
		}
	}
	

	public static String formatLike(String str){
		if(isNotEmpty(str)){
			return "%"+str+"%";
		}else{
			return null;
		}
	}
	

	public static List<String> filterWhite(List<String> list){
		List<String> resultList=new ArrayList<String>();
		for(String l:list){
			if(isNotEmpty(l)){
				resultList.add(l);
			}
		}
		return resultList;
	}

	public static String getSoleStr(){
		System.out.println(UUID.randomUUID().toString());
		return UUID.randomUUID().toString();
	}

	public static void main(String[] args) {
		getSoleStr();
	}

}
