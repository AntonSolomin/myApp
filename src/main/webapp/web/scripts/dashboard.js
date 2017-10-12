/**
 * Created by Anton on 29.09.2017.
 */

$(function () {
    $("#logIn").click(logIn);
    $("#getUser").click(seeUser);
});

function logIn () {
    $.post("/api/login", { userName: "bruminator", password: "qwe" }).done(function(data){console.log(data);});
}

function seeUser() {
    $.getJSON("/api/users/bruminator", renderUser).fail(function () {console.log("failed to get");});
}

function renderUser(data) {
    var userName = data.username + " dashboard";
    $("#userNameTitle").html(userName);

    var output;
    output += "<p>" + data.first_name + "</p>";
    output += "<p>" + data.last_name + "</p>";
    output += "<p>" + data.user_id + "</p>";
    output += "<p>" + data.username + "</p>";
    output += "<p>" + "Your posts are: " + data.posts + " posts." +"</p>";

    //output += "<p>" + "You have " + data.posts.length + " posts." +"</p>";


    $("#content").html(output);
    console.log(data);
}

function editUser () {
    var url = "/api/users";
    var data = {inputFirstName: "Blob", inputLastName: "Slob", inputPassword: "567" };
    sendApiRequest(url, "PATCH", data, successCallback, failureCallback);
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

function deletePost () {
    var queryArray = "1";
    sendApiRequest('/api/posts/', "DELETE", queryArray, successCallback, failureCallback);
}

function deleteUser () {
    $.ajax({
        type : "DELETE",
        url : "/api/users/",
        success: successCallback,
        error: failureCallback
    });
}