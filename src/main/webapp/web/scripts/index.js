$(function () {
    todo();

    $("#submit").click(search);
    $("#btn").click(upvotePost);
    $("#btnUnlike").click(unUpVotePost);
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
            output += " Upvotes: " + data.posts[i].upvotes;
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

function upvotePost () {
    //sendApiRequest("http://localhost:8080/post/3/upvote", "POST");
    /*$.ajax({
        type: "POST",
        url: "/api/posts/2/upvote",
        success: success,
    });*/
    $.ajax({
        url: '/api/posts/2/vote',
        type: 'PUT',
        data: {"vote": true}
    });
}

function unUpVotePost () {
    $.ajax({
        url: '/api/posts/2/vote',
        type: 'PUT',
        data: {"vote": false}
    });
}

function success() {
    console.log("Hello!");
}

function successCallback (data) {
    todo();
}

function failureCallback (data) {
    console.log(data);
    console.log("Snap!");
}