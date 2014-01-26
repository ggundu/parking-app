package com.parking.services;

import com.parking.model.BikeParkingLocation;
import com.parking.model.Coordinates;
import com.socrata.api.HttpLowLevel;
import com.socrata.api.Soda2Consumer;
import com.socrata.builders.SoqlQueryBuilder;
import com.socrata.exceptions.LongRunningQueryException;
import com.socrata.exceptions.SodaError;
import com.socrata.model.soql.OrderByClause;
import com.socrata.model.soql.SoqlQuery;
import com.socrata.model.soql.SortOrder;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.GenericType;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;

/**
 * Implementation for SocrataApiService.
 * User: gautam
 * Date: 1/22/14
 * Time: 11:53 PM
 *
 */
@Service
public class SocrataApiServiceImpl implements SocrataApiService {

    private static final Logger logger = Logger.getLogger(SocrataApiServiceImpl.class);

    private final Soda2Consumer consumer = Soda2Consumer.newConsumer(
            "https://data.sfgov.org",
            "gouthamnath@gmail.com", "Socrata1883",
            "iyWFFyFuIf0DXopxNv5QcIMIA");
    private List<String> selectColumnlist = new ArrayList<>();
    private List<OrderByClause> orderByClauseList = new ArrayList<>();


    @PostConstruct
    private void init() {
        selectColumnlist.add("location_name"); // Location Name
        selectColumnlist.add("yr_inst"); // Address
        selectColumnlist.add("coordinates");
        selectColumnlist.add("bike_parking");
        selectColumnlist.add("placement");
        selectColumnlist.add("racks_installed");
        selectColumnlist.add("spaces");

        OrderByClause orderByName = new OrderByClause(SortOrder.Ascending, "location_name");
        orderByClauseList.add(orderByName);
        OrderByClause orderByNoOfRacks = new OrderByClause(SortOrder.Descending, "racks_installed");
        orderByClauseList.add(orderByNoOfRacks);
        OrderByClause orderBySpaces = new OrderByClause(SortOrder.Descending, "spaces");
        orderByClauseList.add(orderBySpaces);
    }

    /**
     * @see com.parking.services.SocrataApiService#fetchBikeParkingLocationsWithinCoordinates(com.parking.model.Coordinates, com.parking.model.Coordinates)
     *
     */
    @Override
    public List<BikeParkingLocation> fetchBikeParkingLocationsWithinCoordinates(Coordinates minCoordinates, Coordinates maxCoordinates) throws Exception {
        SoqlQuery query = buildSocrataQuery(minCoordinates, maxCoordinates, 1000);
        ClientResponse response = null;
        try {
          response  = consumer.query("w969-5mn4", HttpLowLevel.JSON_TYPE, query);
        }catch (LongRunningQueryException | SodaError apiException) {
            logger.error("Socrata Exception: Exception Occurred while querying Socrata API. Exception:"+apiException.getMessage(), apiException);
            throw apiException;
        }
        final List<BikeParkingLocation> locationList = response.getEntity(new GenericType<List<BikeParkingLocation>>(){});

        return locationList;
    }

    /**
     * Builds Socrata query using the required selection criteria.
     * Query criteria: spaces > 0 and racks_installed > 0 and status = 'COMPLETE'
     * @param minCoordinate
     * @param maxCoordinates
     * @param noOfLocationsToFetch
     * @return
     */
    private SoqlQuery buildSocrataQuery(Coordinates minCoordinate, Coordinates maxCoordinates, int noOfLocationsToFetch) {
        StringBuilder whereClause = new StringBuilder("spaces !=  '0' AND racks_installed != '0' AND status='COMPLETE'");
        whereClause.append(" AND coordinates.latitude >="+ minCoordinate.getLatitude());
        whereClause.append(" AND coordinates.latitude <="+maxCoordinates.getLatitude());
        whereClause.append(" AND coordinates.longitude >="+minCoordinate.getLongitude());
        whereClause.append(" AND coordinates.longitude <="+maxCoordinates.getLongitude());
        SoqlQuery query = new SoqlQueryBuilder().addSelectPhrases(selectColumnlist).setWhereClause(whereClause.toString()).setLimit(noOfLocationsToFetch).build();

        return query;
    }
}
