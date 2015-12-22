var map;
var position;
var user_data;
var restaurantList = new Map();
var curRestIndex;
var updatelistTimer;
var newMarker;
var iconMarker;
var iconNewMarker;
var autorized = false;

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

function redirectReview(){
    href = window.location.href;
    reviewId = href.split("/review/")[1];
    if (reviewId !== null && reviewId !== undefined){
        reviewUrl = "/api/review/" + reviewId;
        $.ajax({
            type: "GET",
            url: reviewUrl,
            success: function (data) {
                openContainer(data);
            },
            error: function () {
                alert('Can not connect to the server');
            }
        });
    }
}

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
        getMarks();
        redirectReview();
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
    // according to GeoJSON fromat geodata must be stored in format [lng, lat] 
    var marker = DG.marker([obj["loc"][1], obj["loc"][0]],{icon: iconMarker})
        .addTo(map)
        .bindPopup(getContent(obj),{
            maxWidth: 500,
            minWidth: 300,
            sprawling: true})
        .bindLabel(obj["name"]);
    marker.on('click', function() {
        curRestIndex = obj["_id"];
    });
    restaurantList.set(obj["_id"], new Restaurant(obj, marker));
}

//generate content for marker popup
function getContent(obj) {
    restHref = "/api/restaurant/"+obj["_id"];
    result =
        '<b>' + obj["name"] + '</b>' +
        '<br>' + obj["description"] + '<br><br>' +
        '<b>Address: </b>' + obj["address"] + '</br>' +
        '<b>Phones: </b>' + obj["telephone"] + '</br>' +
        '<b>Working hours: </b>' + obj["workingHours"] + '</br></br><a onclick="openRestInfoPage()">View more</a>';
    if (autorized){
        result +=
            '<button type="button" class="btn btn-primary popupbtn" onclick="editRestaurant(\'' + obj["_id"] + '\')">Edit</button>' +
        '<button type="button" class="btn btn-danger popupbtn" onclick="deleteRestaurant(\'' + obj["_id"] + '\')">Delete</button>' + '<br>';
    }
    return result;
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

function openContainer(data){
    review = data;
    curRestIndex = review["restaurantId"]["$oid"];
    marker = restaurantList.get(curRestIndex).marker;
    map.setView(marker.getLatLng());
    $('#info_box').css({"visibility": "visible", "display": "block"});
    $('#info_container').css({"visibility": "visible", "display": "block"});
    $('#mapButton').removeAttr("active");
    marker.closePopup();
    setUrl(restaurantList.get(curRestIndex).name, "/review/" + review["_id"]["$oid"]);
    reviewMaping(restaurantList.get(curRestIndex), review);
}

function openRestInfoPage() {
    reviewUrl = "/api/review/by-rest/" + restaurantList.get(curRestIndex)._id;
    $.ajax({
        type: "GET",
        url: reviewUrl,
        success: function (data) {
            openContainer(data);
        },
        error: function () {
            alert('Can not connect to the server');
        }
    });
   // slider('reviewSlider', 0);
}

function reviewMaping(restaurant, review){
    $('.restName').text(restaurant["name"]);
    $('#rateCuisine').text(review["cuisine"] + "/5");
    $('#rateInterior').text(review["interior"] + "/5");
    $('#rateService').text(review["service"] + "/5");
    $('#rateTotal').text(review["resultMark"] + "/5");
    $('#restView').text(review["content"]);
    labels = review["labels"];
    $('.reviewBadge').remove();
    if (labels !== undefined && labels!==null) {
        for (i = 0; i < labels.length; i++) {
            $('.labels').append(
                '<h4><span class="reviewBadge badge" id="label-' + i + '">' + labels[i]["text"] + '</span></h4>'
            );
            $('#label-' + i).css("background-color", labels[i]["color"]);
        }
    }
}

function setUrl(title, newUrl){
    window.history.pushState("string", title, newUrl);
}

function editRestaurant(_id){
    editUrl = "/restaurant/" + _id + "/edit";
    window.open(editUrl, "_self", false);
}

function deleteRestaurant(_id){
    editUrl = "/restaurant/" + _id + "/delete";
    $.ajax({
        type: "DELETE",
        url: editUrl,
        success: function () {
            restaurantList.get(_id).marker.remove();
            restaurantList.delete(_id);
            $('#'+_id).remove();
        },
        error: function () {
            alert('Can not connect to the server');
        }
    });
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

function setAutorized(){
    autorized=true;
}






var arrayReviews = new Array();
var reviewSpeedOpen = 10;
var reviewTimer = 10;

// Loop through all the divs in the slider parent div //
// Calculate seach content divs height and set it to a variable //
function slider(target,showfirst) {
    var slider = document.getElementById(target);
    var divs = slider.getElementsByTagName('div');
    var divslength = divs.length;
    for(i = 0; i < divslength; i++) {
        var div = divs[i];
        var divid = div.id;
        if(divid.indexOf("reviewHeader") != -1) {
            div.onclick = new Function("processClick(this)");
        } else if(divid.indexOf("reviewContent") != -1) {
            var section = divid.replace('-reviewContent','');
            arrayReviews.push(section);
            div.maxh = div.offsetHeight;
            if(showfirst == 1 && i == 1) {
                div.style.display = 'block';
            } else {
                div.style.display = 'none';
            }
        }
    }
}

// Process the click - expand the selected content and collapse the others //
function processClick(div) {
    var catlength = arrayReviews.length;
    for(i = 0; i < catlength; i++) {
        var section = arrayReviews[i];
        var head = document.getElementById(section + '-reviewHeader');
        var cont = section + '-reviewContent';
        var contdiv = document.getElementById(cont);
        clearInterval(contdiv.timer);
        if(head == div && contdiv.style.display == 'none') {
            contdiv.style.height = '0px';
            contdiv.style.display = 'block';
            initSlide(cont,1);
        } else if(contdiv.style.display == 'block') {
            initSlide(cont,-1);
        }
    }
}

// Setup the variables and call the slide function //
function initSlide(id,dir) {
    var cont = document.getElementById(id);
    var maxh = cont.maxh;
    cont.direction = dir;
    cont.timer = setInterval("slide('" + id + "')", reviewTimer);
}

// Collapse or expand the div by incrementally changing the divs height and opacity //
function slide(id) {
    var cont = document.getElementById(id);
    var maxh = cont.maxh;
    var currheight = cont.offsetHeight;
    var dist;
    if(cont.direction == 1) {
        dist = (Math.round((maxh - currheight) / reviewSpeedOpen));
    } else {
        dist = (Math.round(currheight / reviewSpeedOpen));
    }
    if(dist <= 1) {
        dist = 1;
    }
    cont.style.height = currheight + (dist * cont.direction) + 'px';
    cont.style.opacity = currheight / cont.maxh;
    cont.style.filter = 'alpha(opacity=' + (currheight * 100 / cont.maxh) + ')';
    if(currheight < 2 && cont.direction != 1) {
        cont.style.display = 'none';
        clearInterval(cont.timer);
    } else if(currheight > (maxh - 2) && cont.direction == 1) {
        clearInterval(cont.timer);
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
