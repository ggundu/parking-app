/**
 * 
 */
package com.parking.model;

import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;

/**
 * Represents each Bike Parking Location.
 * 
 * @author gautam
 *
 */
@JsonIgnoreProperties(ignoreUnknown=true)
public class BikeParkingLocation {
    // Name of the Location.
    private String name;
    //private Address address;
    private String address;
    //Location coordinates.
    private Coordinates coordinates;
	private String parkingType;
	private String placementType;
	private int noOfRacksInstalled;
	private int noOfParkingSpaces;


    private double distanceFromOrigin = Integer.MAX_VALUE;


    @JsonCreator
    public BikeParkingLocation(@JsonProperty("location_name") String name,
                               @JsonProperty("yr_inst") String address,
                               @JsonProperty("coordinates") Coordinates coordinates,
                               @JsonProperty("bike_parking") String parkingType,
                               @JsonProperty("placement") String placementType,
                               @JsonProperty("racks_installed") int noOfRacksInstalled,
                               @JsonProperty("spaces") int noOfParkingSpaces) {
        this.name = name;
        this.address = address;
        this.coordinates = coordinates;
        this.parkingType = parkingType;
        this.placementType = placementType;
        this.noOfRacksInstalled = noOfRacksInstalled;
        this.noOfParkingSpaces = noOfParkingSpaces;
    }

    public String getName() {
        return name;
    }

    public String getAddress() {
        return address;
    }

    public Coordinates getCoordinates() {
        return coordinates;
    }

    public String getParkingType() {
		return parkingType;
	}
	public void setParkingType(String parkingType) {
		this.parkingType = parkingType;
	}
	public String getPlacementType() {
		return placementType;
	}
	public void setPlacementType(String placementType) {
		this.placementType = placementType;
	}
	public int getNoOfRacksInstalled() {
		return noOfRacksInstalled;
	}
	public void setNoOfRacksInstalled(int noOfRacksInstalled) {
		this.noOfRacksInstalled = noOfRacksInstalled;
	}
	public int getNoOfParkingSpaces() {
		return noOfParkingSpaces;
	}
	public void setNoOfParkingSpaces(int noOfParkingSpaces) {
		this.noOfParkingSpaces = noOfParkingSpaces;
	}

    public double getDistanceFromOrigin() {
        return distanceFromOrigin;
    }

    public void setDistanceFromOrigin(double distanceFromOrigin) {
        this.distanceFromOrigin = distanceFromOrigin;
    }


    public String toString() {
		StringBuilder builder = new StringBuilder("[BikeParking Location:");
		builder.append("Location Name:"+getName()+";");
		builder.append(getCoordinates().toString());
		builder.append("]");
		return builder.toString();
	}
}
