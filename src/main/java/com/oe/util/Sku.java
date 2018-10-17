package com.oe.util;

import java.util.ArrayList;
import java.util.List;

public class Sku {
	private String id;
	private String name;
	private String description;
	private String keyFeature;
	private String img1;
	private String img2;
	private String img3;
	private String img4;
	private String img5;
	private double price;
	private String keywords;
	private String link;
	private String picLink;
	private String ageGroup;
	private String sizeGroup;
	private String amazonLink;
	
	List<SubSku> subSkus = new ArrayList<SubSku>();
	private List<String> sizes = new ArrayList<String>();
	public List<String> getSizes() {
		return sizes;
	}
	public void setSizes(List<String> sizes) {
		this.sizes = sizes;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getDescription() {
		return description;
	}
	public String getAmazonLink() {
		return amazonLink;
	}
	public void setAmazonLink(String amazonLink) {
		this.amazonLink = amazonLink;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getKeyFeature() {
		return keyFeature;
	}
	public void setKeyFeature(String keyFeature) {
		this.keyFeature = keyFeature;
	}
	public String getImg1() {
		return img1;
	}
	public void setImg1(String img1) {
		this.img1 = img1;
	}
	public String getImg2() {
		return img2;
	}
	public void setImg2(String img2) {
		this.img2 = img2;
	}
	public String getImg3() {
		return img3;
	}
	public void setImg3(String img3) {
		this.img3 = img3;
	}
	public String getImg4() {
		return img4;
	}
	public void setImg4(String img4) {
		this.img4 = img4;
	}
	public String getImg5() {
		return img5;
	}
	public void setImg5(String img5) {
		this.img5 = img5;
	}
	public double getPrice() {
		return price;
	}
	public void setPrice(double price) {
		this.price = price;
	}
	public String getKeywords() {
		return keywords;
	}
	public void setKeywords(String keywords) {
		this.keywords = keywords;
	}
	public List<SubSku> getSubSkus() {
		return subSkus;
	}
	public void setSubSkus(List<SubSku> subSkus) {
		this.subSkus = subSkus;
	}
	public String getLink() {
		return link;
	}
	public void setLink(String link) {
		this.link = link;
	}
	public String getPicLink() {
		return picLink;
	}
	public void setPicLink(String picLink) {
		this.picLink = picLink;
	}
	public String getAgeGroup() {
		return ageGroup;
	}
	public void setAgeGroup(String ageGroup) {
		this.ageGroup = ageGroup;
	}
	public String getSizeGroup() {
		return sizeGroup;
	}
	public void setSizeGroup(String sizeGroup) {
		this.sizeGroup = sizeGroup;
	}
	
	@Override
	public String toString() {
		return "Sku [id=" + id + ", name=" + name + ", description=" + description + ", keyFeature=" + keyFeature
				+ ", img1=" + img1 + ", img2=" + img2 + ", img3=" + img3 + ", img4=" + img4 + ", img5=" + img5
				+ ", price=" + price + ", keywords=" + keywords + ", link=" + link + ", picLink=" + picLink
				+ ", ageGroup=" + ageGroup + ", sizeGroup=" + sizeGroup + ", subSkus=" + subSkus + "]";
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Sku other = (Sku) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}
	
}
