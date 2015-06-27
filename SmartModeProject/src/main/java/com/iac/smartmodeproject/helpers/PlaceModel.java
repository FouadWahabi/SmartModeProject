package com.iac.smartmodeproject.helpers;

public class PlaceModel {

	private int id;
	private String label;
	private double lat;
	private double lang;
	private int radius;

	public PlaceModel() {

	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public double getLat() {
		return lat;
	}

	public void setLat(double lat) {
		this.lat = lat;
	}

	public double getLang() {
		return lang;
	}

	public void setLang(double lang) {
		this.lang = lang;
	}

	public int getRadius() {
		return radius;
	}

	public void setRadius(int radius) {
		this.radius = radius;
	}

}
