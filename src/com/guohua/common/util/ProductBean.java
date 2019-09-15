package com.guohua.common.util;

public class ProductBean {
	/**
	 * 商品ID
	 */
	private int product_id;
	/**
	 * 商品数量
	 */
	private int product_count;
	/**
	 * 商品价格
	 */
	private double product_all_price;
	/**
	 * 商品名称
	 */
	private String product_name;
	
	public double getProduct_all_price() {
		return product_all_price;
	}


	public void setProduct_all_price(double product_all_price) {
		this.product_all_price = product_all_price;
	}


	public int getProduct_id() {
		return product_id;
	}


	public void setProduct_id(int product_id) {
		this.product_id = product_id;
	}


	public String getProduct_name() {
		return product_name;
	}


	public void setProduct_name(String product_name) {
		this.product_name = product_name;
	}


	public int getProduct_count() {
		return product_count;
	}


	public void setProduct_count(int product_count) {
		this.product_count = product_count;
	}


	@Override
	public String toString() {
		return "product_id : " + product_id + "--" + "product_name : " + product_name + "--" + 
			   "product_count : " + product_count + "--" + "product_all_price : " + product_all_price;
	}

}
