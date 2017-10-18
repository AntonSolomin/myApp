$(function () {
    todo();
    $("#submit").click(search);
    $("#query").keydown(function (event) {
        if (event.keyCode == 13) {
            $("#submit").click();
            return false;
        }
    });
});

function renderPosts(data) {
    console.log(data);
    var userDashBoardLink = "Hello " + "<a href='/web/html/dashboard.html'>" + data.logged_in_user_id + "</a>";

    var output = "";
    for (var i =0; i < data.posts.length; ++i) {
        output += "<p>";
            output += "<a " + addIdToLink(data.logged_in_user_id, data.posts[i]) + " >";
            output += data.posts[i].post_subject + " ";
            output += "</a>";
            output += data.posts[i].post_body + " ";
            output += data.posts[i].post_price;
        output += "</p>";
    }
    $("#output").html(output);
    $("#userName").html(userDashBoardLink);
}

function addIdToLink(user, post) {
    if (user != "unidentified_user") {
        var link = "href='/web/html/post.html?id=" + post.post_id + "'";
    }
    return link;
}

function logIn () {
    $.post("/api/login", { userName: "bruminator", password: "qwe" }).done(todo);
}

function logOut() {
    $.post("/api/logout").done(function () {
        $.getJSON("/api/games", onDataReady);
        location.reload();
    });
}

function logOut() {
    $.post("/api/logout").done(function () {console.log("logged out");});
}

function todo() {
    $.getJSON("/api/posts", renderPosts).fail(function () {console.log("failed to get /api/posts");});
    console.log("Hurray!");
}

function search() {
    var query = $("#query").val();
    var queryArray = query.split(" ");
    sendApiRequest("/api/posts/queries", "POST", queryArray, onSearchSuccess, failureCallback);
}

function onSearchSuccess (response) {
    renderPosts(JSON.parse(response.responseText));
}

function sendApiRequest(url, method, data, successCallback, failureCallback) {
    var request = new XMLHttpRequest();
    request.onreadystatechange = function () {
        if (this.readyState == 4) {
            if (this.status == 200) {
                successCallback(this);
            } else {
                failureCallback(this);
            }
        }
    }

    request.open(method, url, true);
    request.setRequestHeader("Content-type", "application/json");
    if (data != null) {
        request.send(JSON.stringify(data));
    } else {
        request.send();
    }
}

function successCallback (data) {
    todo();
}

function failureCallback (data) {
    console.log(data);
    console.log("Snap!");
}