$(function () {
    logIn();

});


function renderPosts(data) {
    console.log(data);
    $("#submit").click(search);

    var output = "";
    for (var i =0; i < data.posts.length; ++i) {
        output += "<p>";
            output += "Post Id: " + data.posts[i].post_id;
            output += "<a " + addIdToLink(data.logged_in_user_id, data.posts[i]) + " >";
            output += "Post subject: " + data.posts[i].post_subject;
            output += "</a>";
            output += "Post body: " + data.posts[i].post_body;
            output += "Post price: " + data.posts[i].post_price;
        output += "</p>";
    }
    $("#output").html(output);
}

function addIdToLink(user, post) {
    if (user != "unidentified_user") {
        var link = "href='/web/html/post.html?id=" + post.post_id + "'";
    }
    return link;
}

function logIn () {
    $.post("/api/login", { userName: "ledorub", password: "123" }).done(todo);

}

function todo() {
    $.getJSON("/api/posts", renderPosts).fail(function () {console.log("failed to get /api/posts");});
}

function editUser () {
    var url = "/api/users";
    var data = {inputFirstName: "Blob", inputLastName: "Slob", inputPassword: "567" };
    sendApiRequest(url, "PATCH", data, successCallback, failureCallback);
}

function editPost() {
    var url = "/api/posts";
    var data = {id: 1, postBody: "Totally new", postSubject: "the subject is fantastically awesome", postPrice: 1000 };
    sendApiRequest(url, "PATCH", data, successCallback, failureCallback);
}

function search() {
    sendApiRequest("/api/posts/queries", "POST", ["cheese", "cakes"], onSearchSuccess, failureCallback);
}

function onSearchSuccess (response) {
    renderPosts(JSON.parse(response.responseText));
}

function seePost() {
    $.getJSON("/api/post_view/1", renderPosts).fail(function () {console.log("Not Good");});
}

function goToPost() {
    $.getJSON("/api/post_view/1", renderPosts)
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

function successCallback () {
    console.log("Hurray!");
}

function failureCallback () {
    console.log("Snap!");
}