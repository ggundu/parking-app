/**
 * 
 */
package com.parking.model;

import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.map.annotate.JsonSerialize;

/**
 * Represents the Geographical point. (Lat/Long)
 * @author gautam
 *
 */
@JsonIgnoreProperties(ignoreUnknown=true)
@JsonSerialize(include= JsonSerialize.Inclusion.NON_NULL)
public class Coordinates {

	private double latitude;
	private double longitude;

    @JsonCreator
    public Coordinates(@JsonProperty("latitude") double latitude,
                       @JsonProperty("longitude") double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    @JsonProperty("latitude")
    public double getLatitude() {
		return latitude;
	}

    @JsonProperty("longitude")
	public double getLongitude() {
		return longitude;
	}

	
	
	public String toString() {
		return "[Coordinates:Lat:"+getLatitude()+"; Long:"+getLongitude()+"]";
	}
	
}
