/**
 * Created by Anton on 28.09.2017.
 */


$(function () {
    //getting correct link
    var queryObj = parseQueryObject();
    if (queryObj.hasOwnProperty("id")) {
        var id = queryObj.id;

        var link = "/api/post_view/" + id;
        console.log(link);
        $.getJSON(link, ready).fail(function () {console.log("failed to get /api/posts");});
    }
});

function ready(data) {
    renderPost(data);
    console.log(data);
}

function renderPost(data) {
    console.log("Im trying");
    var output = "";

        output += "<p>" + "Post Id: " + data.post_id + "</p>";
        output += "<p>" + "Post subject: " + data.post_subject + "</p>";
        output += "<p>" + "Post body: " + data.post_body + "</p>";
        output += "<p>" + "Post price: " + data.post_price + "</p>";

        //TODO use fancybox
        for (var i = 0; i<data.url.length; ++i) {
            output += "<img src=" + data.url[i] + " alt='some text'>";
        }


    $("#content").html(output);
}

function parseQueryObject() {
    // using substring to get a string from position 1
    var queryString = location.search.substring(1); /*"?gp=1&mp=23&sdfs=3rr"*/ ;
    var obj = {};
    // You can pass a regex into Javascript's split operator.
    var arr = queryString.split(/=|&/);
    if (queryString !== "") {
        arr.forEach(function (item, index) {
            if (index % 2 === 0) {
                obj[item] = arr[index + 1];
            }
        });
    }
    return obj;
}