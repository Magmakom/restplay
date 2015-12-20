var map;
var position;
var user_data;
var restaurantList = new Map();
var curRestIndex;
var updatelistTimer;
var newMarker;
var iconMarker;
var iconNewMarker;

function Restaurant(obj, marker) {
    this._id = obj["_id"];
    this.name = obj["name"];
    this.description = obj["description"];
    this.address = obj["address"];
    this.telephone = obj["telephone"];
    this.workingHours = obj["workingHours"];
    this.marker = marker;
}

$(function () {
    $('#signIn').click(function () {
        $('#overlay').fadeIn('fast', function () {
            $('#box').animate({'top': '160px'}, 500);
        });
    });
    $('#boxclose').click(function () {
        $('#box').animate({'top': '-400px'}, 500, function () {
            $('#overlay').fadeOut('fast');
        });
    });
});

$(function () {
    $('#rest_info_close').click(function () {
        $('#info_box').css({"visibility": "hidden", "display": "none"});
        $('#info_container').css({"visibility": "hidden", "display": "none"});
        marker = restaurantList.get(curRestIndex).marker;
        marker.openPopup();
    });
});

function initMap() {
    $('#info_box').css({"visibility": "hidden", "display": "none"});
    $('#info_container').css({"visibility": "hidden", "display": "none"});
    DG.then(function() {
        position = DG.latLng(46.482526, 30.7233095);
        map = DG.map('map', {
            'center': position,
            fullscreenControl: false
        });
        map.locate({setView: true, watch: false})
            .on('locationfound', function (e) {
                position = DG.latLng(e.latitude, e.longitude);
            })
            .on('locationerror', function (e) {
                console.log(e);
            });
        iconMarker = DG.icon({
            iconUrl: '/images/marker.svg',
            iconSize: [36,70149, 48,93516]
        });
        iconNewMarker = DG.icon({
            iconUrl: '/images/newmarker.svg',
            iconSize: [36,70149, 48,93516]
        });
        getMarks()
    });
}

// get all active marks from server
function getMarks() {
    user_data = {
        mod: "all"
    };
    $.ajax({
        type: "GET",
        url: "/api/restaurant",
        data: {
            user_data: $.toJSON(user_data)
        },
        success: function (data) {
            var dbl = data;
            if (dbl.length > 0) {
                for (var i = 0; i < dbl.length; i++) {
                    putMarker(dbl[i]);
                }
            }
        },
        error: function () {
            alert('Can not connect to the server');
        }
    });
}

function putMarker(obj) {
    var marker = DG.marker([obj["lat"], obj["lng"]],{icon: iconMarker})
        .addTo(map)
        .bindPopup(getContent(obj),{maxWidth: 500})
        .bindLabel(obj["name"]);
    marker.on('click', function() {
        curRestIndex = obj["_id"];
    });
    restaurantList.set(obj["_id"], new Restaurant(obj, marker));
}

//generate content for marker popup
function getContent(obj) {
    return '<b>' + obj["name"] + '</b><br>' +
        obj["description"] + '<br><br>' +
        '<b>Address: </b>' + obj["address"] + '</br>' +
        '<b>Phones: </b>' + obj["telephone"] + '</br>' +
        '<b>Working hours: </b>' + obj["workingHours"] + '</br></br><a onclick="openRestInfoPage()">View more</a>';
     //   '<b>Working hours: </b>' + obj["workingHours"] + '</br></br><a onclick="openRestInfoPage(\'' + obj["_id"] + '\')">View more</a>';
}

function getRestaurantsByName() {
    // GET to server
    var inputText = document.getElementById("search").value;
}

// target rest from search box
function targetRestaurant(index) {
    if (curRestIndex!=null){
        marker = restaurantList.get(curRestIndex).marker;
        marker.closePopup();
    }
    curRestIndex = index;
    marker = restaurantList.get(curRestIndex).marker;
    openRestInfoPage();
}

function openRestInfoPage() {
    marker = restaurantList.get(curRestIndex).marker;
    map.setView(marker.getLatLng());
    $('#info_box').css({"visibility": "visible", "display": "block"});
    $('#info_container').css({"visibility": "visible", "display": "block"});
    $('#mapButton').removeAttr("active");
    marker.closePopup();
}

function createNewMarker(){
    $('#addRestButton').remove();
    newMarker = DG.marker(map.getCenter(),{
        icon: iconNewMarker,
        draggable: true})
        .bindPopup('<a onclick="openEditrestaurantPage(newMarker)">Edit restaurant info</a>',{
            maxWidth: 350,
            sprawling: true
        })
        .addTo(map)
        .bindLabel("New restaurant");
}

function openEditrestaurantPage(marker){
    editUrl = "/restaurant/new" + "?" + "lat=" + marker.getLatLng().lat + "&lng=" + marker.getLatLng().lng;
    window.open(editUrl, "_self", false);
}

function updateRatingPage(){
    $('.rating_table_row').remove();

    user_data = {
        mod: "top100"
    };
    $.ajax({
        type: "GET",
        url: "/api/ratingPage",
        data: {
            user_data: $.toJSON(user_data)
        },
        success: function (data) {
            var restaurants = data;
            if (restaurants.length > 0) {
                for (var i = 0; i < dbl.length; i++) {
                    $('.rating_table_body').append(
                        '<tr class="rating_table_row">' +
                        '<th scope="row">i</th>'+
                        '<td>restaurants["name"]</td>'+
                        '<td>restaurants["cuisine"]</td>'+
                        '<td>restaurants["interior"]</td>'+
                        '<td>restaurants["service"]</td>'+
                        '<td>5</td>'+
                        '</tr>');
                }
            }
        },
        error: function () {
            alert('Can not connect to the server');
        }
    });

    for (var i = 0; i < items.length; i++) {
        if (items[i]["name"].toLowerCase().indexOf(inputText.toLowerCase()) > -1 || inputText === "") {
            $('.list-group').append(
                '<button type="button" class="list-group-item" onclick="targetRestaurant(' + i + ')"><span class="badge">5</span>'
                + items[i]["name"]
                + '</button>');
        }
    }
}

// no needs today coz of react.js
function updateList() {
    return
    if (updatelistTimer !== null) {
        clearTimeout(updatelistTimer);
    }
    updatelistTimer = setTimeout(getRestaurantsByName, 1000);
    $('.list-group-item').remove();
    inputText = document.getElementById("search").value;
    items = Array.from(restaurants);
    for (var i = 0; i < items.length; i++) {
        if (items[i]["name"].toLowerCase().indexOf(inputText.toLowerCase()) > -1 || inputText === "") {
            $('.list-group').append(
                '<button type="button" class="list-group-item" onclick="targetRestaurant(' + i + ')"><span class="badge">5</span>'
                + items[i]["name"]
                + '</button>');
        }
    }
}

//document.addEventListener("DOMContentLoaded", initMap());
$(document).ready(initMap());
