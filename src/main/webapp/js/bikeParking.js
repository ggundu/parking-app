var directionsDisplay;
var directionsService = new google.maps.DirectionsService();
var map;
var infowindow;
var allMarkers= new Array();
var fromCoordinates;

var allClosestLocationsArray;
var theClosestLocation;
var markerLocationsArray;

var message_NoCurrentLocationDetection = 'Sorry! The Geolocation service failed. Make sure you enable geolocation in your browser. If the problem persists, use \'From an Address\' option';

var message_NoClosebyLocationsFound = 'Sorry! No Bike Parking Locations are close to your search criteria. Please try other Addresses';

var defaultCoordinates = new Coordinates(37.788811,-122.407375);

function Coordinates(latitude, longitude) {
    this.latitude = latitude;
    this.longitude = longitude;
}


function initialize(coordinates) {
    directionsDisplay = new google.maps.DirectionsRenderer();
    geocoder = new google.maps.Geocoder();
    var sf = new google.maps.LatLng(coordinates.latitude, coordinates.longitude);
    var mapOptions = {
        zoom: 13,
        center: sf
    }
    var canvas = $("#map-canvas");
    $('#directions-panel').hide();
    $('#directions-panel').removeClass('directions-panel-width').addClass('directions-panel-nowidth');
    $('#map-canvas').removeClass('map-canvas-width').addClass('map-canvas-width-full');
    map = new google.maps.Map(canvas.get(0), mapOptions);
    directionsDisplay.setMap(map);
    directionsDisplay.setPanel($('#directions-panel').get(0));

    var input = $("#pac-input").get(0);
    var autocomplete = new google.maps.places.Autocomplete(input);
    autocomplete.bindTo('bounds', map);

}

function displayMarkers() {

    if(allClosestLocationsArray != null && allClosestLocationsArray.length > 0) {
        clearMarkers();
        infowindow = new google.maps.InfoWindow();

        $.each(allClosestLocationsArray, function(index, eachLocation) {
            addMarker(eachLocation);
        });

    }
}

//Add markers
function addMarker(parkingLocation) {
    var image = '/images/parking_bicycle-2-orange.png';
    var position = new google.maps.LatLng(parkingLocation.coordinates.latitude, parkingLocation.coordinates.longitude);
    var marker = (new google.maps.Marker({
        position: position,
        map: map,
        icon: image,
        draggable: false,
        animation: google.maps.Animation.DROP,
        clickable: true
    }));
    var contentString = '<div class="markerInfo"><strong>Location Name: </strong>'+parkingLocation.name+'<br/><strong>No. of Spaces: </strong>'+
        parkingLocation.noOfParkingSpaces+'<br/><a href="#" class="pickMarker"><strong>Directions to Here</strong></a>'
        +'</div>'


    allMarkers.push(marker);
    google.maps.event.addListener(marker, 'click', function() {
        infowindow.setContent(contentString);
        infowindow.open(map,marker);
        $("div.markerInfo").parent().css("white-space","nowrap");
        $("div.markerInfo a.pickMarker").click(function() {
            calcRoute(fromCoordinates, parkingLocation.coordinates);
        });
    });

}

function clearMarkers() {
    if(allMarkers.length > 0) {
        for (var i = 0; i < allMarkers.length; i++) {
            allMarkers[i].setMap(null);
        }
    }

}


function handleNoGeolocation() {

    showDialog(message_NoCurrentLocationDetection);

}

function calcRoute(fromCoordinates, toCoordinates) {
    $('#directions-panel').html("");
    var start = new google.maps.LatLng(fromCoordinates.latitude, fromCoordinates.longitude); // From Location
    var end = new google.maps.LatLng(toCoordinates.latitude, toCoordinates.longitude); //To Location (Closest Location)
    var request = {
        origin: start,
        destination: end,
        travelMode: google.maps.TravelMode.BICYCLING
    };

    directionsService.route(request, function (response, status) {
        if (status == google.maps.DirectionsStatus.OK) {
            directionsDisplay.setOptions({preserveViewPort: true});
            directionsDisplay.setDirections(response);

        }
    });

    $('#directions-panel').removeClass('directions-panel-nowidth').addClass('directions-panel-width');
    $('#directions-panel').show();
}


function searchAndNavigateToClosest(fromCoordinates) {
    /*
     1. Input Coordinates acts a Starting Points.
     2. Make a server request to fetch closest Parking Locations.

     */
    // loadingDialog.open();
    $("#dialog-loading").dialog({
        dialogClass: "no-close",
        modal:true,
        hide: { effect: "explode", duration: 500 }
    });
    this.fromCoordinates = fromCoordinates;
    fetchParkingLocations(fromCoordinates);
    console.log("Returned after fetch");

}

function fetchParkingLocations(fromCoordinates) {
    var reqData = "lat="+fromCoordinates.latitude +"&long="+ fromCoordinates.longitude;

    var errorTimeout = setTimeout(function() {
        $("#dialog-loading").dialog("close");
        showDialog("Oops...Sorry! Something is wrong with our server or the request.");
    }, 10000);

    $.getJSON("/closestBikeLocations", reqData, function (data) {
        debug(data.length);
        clearTimeout(errorTimeout);
        $("#dialog-loading").dialog("close");

        if (data.length > 0) {
            allClosestLocationsArray = data;
            markerLocationsArray = new Array();
            length = data.length;
            theClosestLocation = data[0];
            for (var i = 1; i < length; i++) {
                markerLocationsArray[i-1] = data[i];
            }
            if(DEBUG_MODE){
                debug("Closest Location:"+ JSON.stringify(theClosestLocation));
            }
            $('#map-canvas').removeClass('map-canvas-width-full').addClass('map-canvas-width');
            google.maps.event.trigger(map, 'resize');

            calcRoute(fromCoordinates, theClosestLocation.coordinates);
            displayMarkers();
            $(".result").html('We Found '+ allClosestLocationsArray.length+'\ closest locations for you\!<div> Please scroll down for directions </div> <span class=\"smallFont\"\>(Note: Please pan or zoom-out the map if you don\'t see them all)</span>');


        }else {
            initialize(defaultCoordinates);
            showDialog(message_NoClosebyLocationsFound);
        }

    })
}

function calcRouteWithAddress() {
    var address = document.getElementById('pac-input').value;

    geocoder.geocode({
        'address': address
    }, function (results, status) {
        if (status == google.maps.GeocoderStatus.OK) {

            var coordinates = new Coordinates(results[0].geometry.location.lat(),results[0].geometry.location.lng());
            searchAndNavigateToClosest(coordinates);
        } else {
            showDialog('Sorry! Could not resolve the address you typed. Please try a valid address');
        }
    });
}

function showDialog(message) {
    $("#dialog-message .modal-content").html('<strong>'+message+'</strong>');
    $('.result').html('');

    $("#dialog-message").dialog({
        dialogClass:"no-close",
        modal:true,
        hide: { effect: "explode", duration: 500 },
        buttons: {
            OK: function() {
                $( this ).dialog( "close" );
            }
        }
    });
}



function getCurrentLocation() {
    if (navigator.geolocation) {
        navigator.geolocation.getCurrentPosition(function (position) {
            var pos = new google.maps.LatLng(position.coords.latitude,
                position.coords.longitude);
            var coordinates = new Coordinates(position.coords.latitude,position.coords.longitude);
            searchAndNavigateToClosest(coordinates);



        }, function () {
            handleNoGeolocation();
        });
    } else {
        // Browser doesn't support Geolocation
        handleNoGeolocation();
    }
}

$(function () {
    initialize(defaultCoordinates);
    $('input[name="inputLocation"]').prop('checked', false);

    $("input.inputLocation").click(function() {
        if($(this).attr("value") == 'currentLocation') {
            $("#address-selection").hide();
            getCurrentLocation();
        }else {
            $("#address-selection").show();
        }
    });

    $("input.search").click(function(event) {
        event.preventDefault();
        $(this).focus();
        calcRouteWithAddress();

    })
});


var DEBUG_MODE = true;

function debug(message) {
    if(DEBUG_MODE && window.console != undefined) {
        console.log(message);
    }
}