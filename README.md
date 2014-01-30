parking-app
===========

Application Description
-----------------------
This application, parking-app (Park-My-Bike!), is a java based single page application used to find the closest
bike parking locations either from the current location of the User or by input Address. 
Bike Parking Location data is being supplied from https://data.sfgov.org using Socrata API and for now the data is limited to 
SF city only.
Once the results are displayed, by default the application shows the closest parking location Bike Directions but the user can
select any other displayed location and get the directions to the selected location respectively.

UI Frameworks/Technologies Used: Html5, CSS3, Bootstrap, jQuery, Google Geo Location services, Google Maps, Google direction service.

Backend: Java, Spark framework, Socrata/SODA API, Maven build

Interesting pieces in the application are location resolution and the coordinate math to determine the location coordinates to search and
the API integration and the Query language for the SODA API.

Note: This application depends on the Web Services (API calls ) exposed at https://data.sfgov.org. This is the current real time dependency which could sometimes (potentially) cause some lag in the response time and there by not able to present the good customer experience (Instead of showing the closest parking locations, you might see an error message saying we could not retrieve results). 

This application currently has about 10 seconds as the timeout. If the response is not received within that limit, the UI will assume as backend error and display error dialog box to the user.  

This is one of the areas where the application needs improvement as to move away from realtime dependency on the API but rather load (via scheduled job) the parking locations into local mongodb and query mongodb instantly as the customer searches. This will avoid a out-network call and could be faster too. 


Developer Info
----------------
Goutham


As part of this development i have explored and learned quite some frameworks including Spark, Python Flask, Html5, Bootstrap, 
Backbone.js, Socrata API, JoSQL, Google Maps & Directions Service integration.







