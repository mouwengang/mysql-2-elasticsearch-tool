package com.tk.content;

public class Condition {

	public static final Integer CONDIONTYPE_EQUAL = 0;
	public static final Integer CONDIONTYPE_RANGE = 1;
	
	//条件类型0为等于,1为范围查询
	private Integer conditionType;
	//列所属类型，有两种类型 Integer Date
	private String columnType;
	//条件为0时的值
	private Object value;
	//范围查询的开端
	private Object begin;
	//范围查询的结束
	private Object end;
	
	public Integer getConditionType() {
		return conditionType;
	}
	public void setConditionType(Integer conditionType) {
		this.conditionType = conditionType;
	}
	public Object getValue() {
		return value;
	}
	public void setValue(Object value) {
		this.value = value;
	}
	public Object getBegin() {
		return begin;
	}
	public void setBegin(Object begin) {
		this.begin = begin;
	}
	public Object getEnd() {
		return end;
	}
	public void setEnd(Object end) {
		this.end = end;
	}
	public String getColumnType() {
		return columnType;
	}
	public void setColumnType(String columnType) {
		this.columnType = columnType;
	}
}
