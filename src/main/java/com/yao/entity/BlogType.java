package com.yao.entity;

/**
 * 博客类型实体
 * @author Administrator
 *
 */
public class BlogType {

	private Integer id; // 编号
	private String typeName; // 博客类型名称
	private Integer orderNo; // 排序序号 从小到大排序
	private Integer blogCount; // 数量,此类型的博客有多少个(仅仅是显示时用到,数据库中并没有该属性的对应字段)
	
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getTypeName() {
		return typeName;
	}
	public void setTypeName(String typeName) {
		this.typeName = typeName;
	}
	public Integer getOrderNo() {
		return orderNo;
	}
	public void setOrderNo(Integer orderNo) {
		this.orderNo = orderNo;
	}
	public Integer getBlogCount() {
		return blogCount;
	}
	public void setBlogCount(Integer blogCount) {
		this.blogCount = blogCount;
	}
	
	
}
