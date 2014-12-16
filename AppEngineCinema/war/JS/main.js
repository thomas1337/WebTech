function getLocation() {


    if (navigator.geolocation) {
        navigator.geolocation.getCurrentPosition(showPosition, showError);
    } else {
        //x.innerHTML = "Geolocation is not supported by this browser.";
        alert('no geo');
    }
}

function showPosition(position) {
    var latit = position.coords.latitude;
    var longt = position.coords.longitude;

    initialize(latit, longt);
}


function showError(error) {
    switch (error.code) {
        case error.PERMISSION_DENIED:
            x.innerHTML = "User denied the request for Geolocation."
            break;
        case error.POSITION_UNAVAILABLE:
            x.innerHTML = "Location information is unavailable."
            break;
        case error.TIMEOUT:
            x.innerHTML = "The request to get user location timed out."
            break;
        case error.UNKNOWN_ERROR:
            x.innerHTML = "An unknown error occurred."
            break;
    }

}

function initialize(lat, long) {


    myLatlng = new google.maps.LatLng(lat, long);

    var mapCanvas = document.getElementById('map_canvas');
    var mapOptions = {
        center: myLatlng,
        zoom: 10,
        mapTypeId: google.maps.MapTypeId.ROADMAP
    }
    map = new google.maps.Map(mapCanvas, mapOptions);

    var marker = new google.maps.Marker({
        position: myLatlng,
        map: map,
        title: "Me"
    });


    displayCinemas(map);
}


function displayCinemas(map) {
    service = new google.maps.places.PlacesService(map);
    google.maps.event.addListenerOnce(map, 'bounds_changed', performSearch);
}

function performSearch() {
    var request = {
        location: myLatlng,
        radius: 3000,
        keyword: 'Kino'
    };
    service.nearbySearch(request, callback);
}


function callback(results, status) {
    if (status != google.maps.places.PlacesServiceStatus.OK) {
        alert(status);
        return;
    }
    for (var i = 0, result; result = results[i]; i++) {
        createMarker(result);
    }
}

function createMarker(place) {


    var infowindow = new google.maps.InfoWindow({
        content: place.vicinity
    });
    var marker = new google.maps.Marker({
        map: map,
        position: place.geometry.location,
        icon: {
            // Star
            path: 'M 0,-24 6,-7 24,-7 10,4 15,21 0,11 -15,21 -10,4 -24,-7 -6,-7 z',
            fillColor: '#ff0000',
            fillOpacity: 1,
            scale: 1 / 4,
            strokeColor: '#bd8d2c',
            strokeWeight: 1
        }


    });

    google.maps.event.addListener(marker, 'click', function () {
        infowindow.open(map, marker);
    });


}




function displayAllSeats () {
    var movieID = document.getElementById("mySelect").selectedIndex;
    seatsPositions=[];
    userSelectedPositions=[];
    receivedReservedPositions=[];
    drawReservationPlan();
    //GET LIST OF RESERVED
    jsonRequest("GET", "/appenginecinema?movie="+movieID, drawFetchedReservedSeats);

}


function jsonRequest(httpmethod, url, callback, optString) // How can I use this callback?
{
    console.log("jsonRequest called");
    var request = new XMLHttpRequest();
    request.onreadystatechange = function()
    {
        if (request.readyState == 4 && request.status == 200)
        {
            callback(request.responseText); // Another callback here
        }
    };
    request.open(httpmethod, url, true);
    //xmlhttp.setRequestHeader("Content-Type", "application/json;charset=UTF-8");
    request.send(optString);
}

function drawFetchedReservedSeats(data) {
    //alert(data);

    var fetchedReservedList=JSON.parse(data);
    //if can decompose an array of the json string
    if( Object.prototype.toString.call( fetchedReservedList ) === '[object Array]' ) {
        //alert( 'Array!' );
        var i;
        var seatPlace;
        var reservation;
        for(i=0;i<seatsPositions.length;i++){

            seatPlace=seatsPositions[i];

            if(containsNumber(fetchedReservedList ,i)){

                reservation={

                    position: i,
                    yPos:seatPlace.rowNumber,
                    xPos:seatPlace.columnNumber
                }

                receivedReservedPositions.push(reservation);
                drawReservationPlan(reservation);

            }
        }
    }else{
        //error case
        document.getElementById("messageLabel").innerHTML =data;
    }
}

function handleReservationRequestResult(data){

    //reservation request was success
    //most probably the reservation was success and data contains the message
    //with the reservation id
    //alert("handleReservationRequestResult");

    //TODO put previous selected into receivedReservedPositions list
    var i;
    for(i=0; i< userSelectedPositions.length;i++){
        receivedReservedPositions.push(userSelectedPositions[i])
    }

    //clear user selected list
    userSelectedPositions=[];

    document.getElementById("messageLabel").innerHTML =data;
    document.getElementById("submit_button").disabled=true;
    document.getElementById("further_reservation_link").style.display = 'block';
}


function drawReservationPlan(placeObj) {



    var c = document.getElementById("myCanvas");
    var ctx = c.getContext("2d");



    var row;
    var rowOffset=60;
    var spaceBtwRows=10;
    var seatsAmount=7;
    //draw rows
    for(row=1;row<=7; row++){

        if(row>5){
            rowOffset=10;
            seatsAmount=8;
        }

        if((placeObj)&&(placeObj.yPos==row)){
            drawRow(c,ctx, 50*row+spaceBtwRows,rowOffset, seatsAmount,row, placeObj);
            return;
        }

        else if(!placeObj){
            drawRow(c,ctx, 50*row+spaceBtwRows,rowOffset, seatsAmount, row);


        }
    }


}
// ALL DRAWN SEAT POSITIONS
var seatsPositions=[];
//ALL SELECTED SEAT POSITIONS
var receivedReservedPositions=[];

//
var userSelectedPositions=[];

/**
 * Draw a single raw or draw a single selection placeObj in a row
 * @param c canvas element
 * @param ctx drawing context
 * @param spaceBetweenRows
 * @param rowOffset
 * @param amount
 * @param row vertical posiiton of seat
 * @param placeObj optional if single selected seat selection has to be drawn
 */
function drawRow(c,ctx, spaceBetweenRows, rowOffset,amount,row, placeObj) {



    var spaceBtwSeats = 5;
    var column;

    for (column = 1; column <= amount; column++) {

        if((placeObj)&&(placeObj.xPos==column)){
            ctx.beginPath();
            //ctx.arc(c.width / 20, 50, c.width / 20, 0, Math.PI);
            ctx.arc( (((c.width / 20) + spaceBtwSeats) * 2 * (column-1)) + rowOffset+(c.width / 20), spaceBetweenRows, c.width / 20, 0, Math.PI);

            ctx.fillStyle='#990000';

            ctx.fill();
            ctx.closePath();
            ctx.fillStyle='#ffffff';
            ctx.fillText(placeObj.position, (((c.width / 20) + spaceBtwSeats) * 2 * (column-1)) + rowOffset+(c.width / 20), spaceBetweenRows+15);


            return;

        }else if(!placeObj){
            var x=(((c.width / 20) + spaceBtwSeats) * 2 * (column-1)) + rowOffset+(c.width / 20);
            var radius=c.width / 20;
            ctx.beginPath();
            ctx.arc(x , spaceBetweenRows, radius, 0, Math.PI);


            ctx.fillStyle='#555555';
            ctx.fill();
            ctx.closePath();

            var seatPlace=
            {

                x:x,
                y:spaceBetweenRows,
                seatWidth: radius*2,
                seatHeight:radius,
                rowNumber: row,
                columnNumber: column

            }

            seatsPositions.push(seatPlace);
            ctx.fillStyle='#ffffff';
            ctx.fillText(seatsPositions.indexOf(seatPlace), x, spaceBetweenRows+15);

            //ctx.fill();

        }

    }


}

/**
 * Handles canvas click, if seat was clicked selection is performed
 * @param event
 */
function canvasClick(event){

    var canvas = document.getElementById("myCanvas");


    var pos=getMousePos(canvas, event);

    //alert("x:" + pos.x + " y:" + pos.y);

    var seatPlace;
    var xRange;
    var yRange;
    var i;
    var prevSelection;
    for(i=0;i<seatsPositions.length;i++){

         seatPlace=seatsPositions[i];

         xRange=seatPlace.x+seatPlace.seatWidth;
         yRange=seatPlace.y+seatPlace.seatHeight;
        if(((pos.x>seatPlace.x)&&(pos.x<xRange))&&
            ((pos.y>seatPlace.y)&&(pos.y<yRange))){

            var selection={

                position:i,
                yPos:seatPlace.rowNumber,
                xPos:seatPlace.columnNumber
            }

            //if allready reservations there check if not the same are coming in if YES dont store the same
           // if(userSelectedPositions.length!=0){

                if(!containsObject(selection, receivedReservedPositions)){
                    userSelectedPositions.push(selection);
                    drawReservationPlan(selection);
                }




            //}else{//store if no resevration yet in the list

              //  userSelectedPositions.push(selection);
              //  drawReservationPlan(selection);
          // }



            return;
        }

    }

}




/**
 *  Check list for equal object by property not by reference
 * @param obj
 * @param list
 * @returns {boolean}
 */
function containsObject(obj, list) {
    var i;
    for (i = 0; i < list.length; i++) {
        if (isEquivalent (list[i] , obj)) {
            return true;
        }
    }

    return false;
}
/**
 * Checks if int array contains number
 * @param array
 * @param value
 * @returns {boolean}
 */
function containsNumber(array, value) {
    for (var i = 0; i < array.length; i++) {
        if (array[i] == value) return true;
    }
    return false;
}

/**
 * Equality comparison based on object propertys
 * @param a
 * @param b
 * @returns {boolean}
 */
function isEquivalent(a, b) {
    // Create arrays of property names
    var aProps = Object.getOwnPropertyNames(a);
    var bProps = Object.getOwnPropertyNames(b);

    // If number of properties is different,
    // objects are not equivalent
    if (aProps.length != bProps.length) {
        return false;
    }

    for (var i = 0; i < aProps.length; i++) {
        var propName = aProps[i];

        // If values of same property are not equal,
        // objects are not equivalent
        if (a[propName] !== b[propName]) {
            return false;
        }
    }

    // If we made it this far, objects
    // are considered equivalent
    return true;
}

function getMousePos(canvas, evt) {
    var rect = canvas.getBoundingClientRect();
    return {
        x: evt.clientX - rect.left,
        y: evt.clientY - rect.top
    };
}


function buyTicket(){



    //var selections = JSON.stringify();
    var i;
    var seatNumbers=[];

    if(userSelectedPositions.length==0){
        return false;
    }
    for(i=0;i<userSelectedPositions.length;i++){
        seatNumbers.push(userSelectedPositions[i].position);

    }

    var reservation={
        "name":document.getElementById("name").value,
        "email" : document.getElementById("email").value,
        "seats": seatNumbers,
        "movieId" : document.getElementById("mySelect").selectedIndex

    }

    jsonRequest("POST", "/appenginecinema", handleReservationRequestResult, JSON.stringify(reservation));
    
    return false;
}

window.onload = new function () {

    google.maps.event.addDomListener(window, 'load', getLocation);

};

