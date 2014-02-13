package com.parking.services;

import com.parking.model.BikeParkingLocation;
import com.parking.model.Coordinates;
import com.parking.util.GeoCoordinatesUtil;
import com.socrata.exceptions.SodaError;
import junit.framework.Assert;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.annotations.Test;

import java.util.List;
import java.util.Map;

import static org.mockito.Mockito.when;

/**
 * JUnit for  BikeParkingLocationService
 * This JUnit class used Mockito to test some Unit Test cases and For Integration tests, it uses Spring TestNG.
 *
 * User: gautam
 * Date: 2/11/14
 * Time: 9:03 PM
 *
 */
@ContextConfiguration(
        locations = {"/META-INF/spring/applicationContext.xml"}
)
public class BikeParkingLocationServiceTest extends AbstractTestNGSpringContextTests {

    @Autowired
    BikeParkingLocationService bikeParkingLocationService;

    /**
     * This is an integration test that tests for valid locations to be returned and checks the validity of the returned
     * Location list along with checking each and every Location to be valid.
     *
     * This test a couple scenarios with different expected location count.
     */
    @Test
    public void fetchClosestBikeParkingLocations_testValidLocationList() {
        Coordinates coordinates = getSampleCoordinates();
        int expectedLocationCount = 10;
        List<BikeParkingLocation> locationList = fetchNClosestBikeParkingLocations(coordinates, expectedLocationCount);
        doAssertionsForValidLocations(locationList, expectedLocationCount, coordinates);

        expectedLocationCount = 20;
        locationList = fetchNClosestBikeParkingLocations(coordinates, expectedLocationCount);
        doAssertionsForValidLocations(locationList, expectedLocationCount, coordinates);
    }

    /**
     * Invokes the BikeParkingLocationService and returns Location List.
     * @param coordinates
     * @param expectedLocationCount
     * @return
     */
    private List<BikeParkingLocation> fetchNClosestBikeParkingLocations(Coordinates coordinates, int expectedLocationCount) {
        List<BikeParkingLocation> locationList = null;
        try {
            locationList = bikeParkingLocationService.fetchClosestBikeParkingLocations(coordinates, expectedLocationCount);

        }catch (Exception e) {
            Assert.fail();
        }
        return locationList;
    }

    /**
     * This method does all the required assertions on the actualLoctionList and each BikeParkingLocation inside the list.
     * @param actualLocationList
     * @param expectedLocationsCount
     * @param inputCoordinates
     */
    private void doAssertionsForValidLocations(List<BikeParkingLocation> actualLocationList, int expectedLocationsCount, Coordinates inputCoordinates) {
        Assert.assertNotNull(actualLocationList);
        Assert.assertEquals(actualLocationList.size(), expectedLocationsCount);

        Map<String, Coordinates> boundaryCoordinates = GeoCoordinatesUtil.getBoundaryCoordinates(inputCoordinates, 1);

        Coordinates minBoundaryCoordinates = boundaryCoordinates.get("minCoordinates");
        Coordinates maxBoundaryCoordinates = boundaryCoordinates.get("maxCoordinates");
        BikeParkingLocation firstLocation = actualLocationList.get(0);
        for(BikeParkingLocation location : actualLocationList) {
            Assert.assertNotNull(location.getName());
            Assert.assertTrue(location.getNoOfRacksInstalled() > 0);
            Assert.assertTrue(location.getNoOfParkingSpaces() > 0);
            Assert.assertTrue(location.getDistanceFromOrigin() >= firstLocation.getDistanceFromOrigin());
            Assert.assertTrue(assertBoundaryCoordindates(minBoundaryCoordinates, maxBoundaryCoordinates, location));
        }


    }

    /**
     * Validates the coordinates of the BikeParkingLocation to be within the boundary coordinates.
     * @param minCoordinates
     * @param maxCoordinates
     * @param parkingLocation
     * @return
     */
    private boolean assertBoundaryCoordindates(Coordinates minCoordinates, Coordinates maxCoordinates, BikeParkingLocation parkingLocation) {
        Coordinates locationCoordinates = parkingLocation.getCoordinates();
        if(locationCoordinates.getLatitude() >= minCoordinates.getLatitude() && locationCoordinates.getLatitude() <= maxCoordinates.getLatitude()
                && locationCoordinates.getLongitude() >= minCoordinates.getLongitude() && locationCoordinates.getLongitude() <= maxCoordinates.getLongitude()) {
               return true;
        }
        return false;
    }

    /**
     * Tests for Service to return an empty Location List.
     * This is Unit Test Case and Uses Mocks.
     *
     */
    @Test
    public void fetchClosestBikeParkingLocations_testEmptyLocationList() {

        Fixture fixture = new Fixture();
        try {
            List<BikeParkingLocation> parkingLocationList =  fixture.bikeParkingLocationService.fetchClosestBikeParkingLocations(getSampleCoordinates(), 10);
            Assert.assertNotNull(parkingLocationList);
            Assert.assertEquals(parkingLocationList.size(), 0);
        }catch (Exception e) {
            Assert.fail("Should not have thrown Exception");
        }
    }

    /**
     * Tests for Service to throw an Exception out.
     * This is Unit Test Case and Uses Mocks.
     *
     * @throws Exception
     */
    @Test
    public void fetchClosestBikeParkingLocations_testExceptionCase() throws Exception {
        Fixture fixture = new Fixture();
        Coordinates coordinates = getSampleCoordinates();
        fixture.doThrowExceptionFromSocrataService(coordinates);

        try {
            List<BikeParkingLocation>  parkingLocationList = fixture.bikeParkingLocationService.fetchClosestBikeParkingLocations(coordinates, 10);
            Assert.fail();
        }catch (Exception e) {
            Assert.assertTrue(e instanceof SodaError);
        }
    }


    /**
     * Returns a sample Coordinates object to be used for Unit/Integration tests.
     * @return
     */
    private Coordinates getSampleCoordinates() {
        Coordinates coordinates = new Coordinates(37.788811,-122.407375);
        return coordinates;
    }

    /**
     *  Handy Mockito mocking container Class to hold the mocking scenarios.
     *
     */
    private static final class Fixture {

        @Spy @InjectMocks
        BikeParkingLocationServiceImpl bikeParkingLocationService = new BikeParkingLocationServiceImpl();

        @Mock
        SocrataApiService socrataApiService;

        private Fixture() {
            MockitoAnnotations.initMocks(this);

        }

        public void doThrowExceptionFromSocrataService(Coordinates coordinates) throws Exception {
            Map<String, Coordinates> boundaryCoordinates = GeoCoordinatesUtil.getBoundaryCoordinates(coordinates, 1);

            Coordinates minBoundaryCoordinates = boundaryCoordinates.get("minCoordinates");
            Coordinates maxBoundaryCoordinates = boundaryCoordinates.get("maxCoordinates");

            when(socrataApiService.fetchBikeParkingLocationsWithinCoordinates(minBoundaryCoordinates, maxBoundaryCoordinates)).thenThrow(new SodaError("SodaError")) ;
        }


    }








}
