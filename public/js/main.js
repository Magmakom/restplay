var map;
var position;
var user_data;
var restaurantList = new Map();
var curRestIndex;
var updatelistTimer;

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
    position = DG.latLng(54.98, 82.89);
    map = DG.map('map', {
        'center': position,
        'zoom': 14,
        fullscreenControl: false
    });
    map.locate({setView: true, watch: false})
        .on('locationfound', function (e) {
            position = DG.latLng(e.latitude, e.longitude);
        })
        .on('locationerror', function (e) {
            console.log(e);
        });
    getMarks();
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
    var iconMarker = DG.icon({
        iconUrl: '/images/marker.svg',
        iconSize: [36,70149, 48,93516]
    });
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
