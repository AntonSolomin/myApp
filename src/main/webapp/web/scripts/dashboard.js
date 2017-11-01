/**
 * Created by Anton on 29.09.2017.
 */

$(function () {
    $("#logIn").click(logIn);
    getUserData();
});

function logIn () {
    $.post("/api/login", { userName: "bruminator", password: "qwe" }).done(function(data){console.log(data);});
}

function getUserData() {
    $.getJSON("/api/users/bruminator", renderUser).fail(function () {console.log("failed to get");});
}

function renderUser(data) {
    let userName = data.username + " dashboard";
    $("#userNameTitle").html(userName);

    let output;
    let message = " posts.";
    output += "<p>" + data.first_name + "</p>";
    output += "<p>" + data.last_name + "</p>";
    output += "<p>" + data.user_id + "</p>";
    output += "<p>" + data.username + "</p>";
    output += "<p>" + "Your posts are: " + renderDashboardPosts(data.posts) +"</p>";
    if (data.posts.length == 1) {
        message = " post."
    };
    output += "<p>" + "You have " + data.posts.length + message +"</p>";
    output += "<p>" + "You liked " + data.posts_you_liked.length + message +"</p>";

    $("#content").html(output);
    console.log(data);
}

function renderDashboardPosts(posts) {
    let message = "";
    for (let post of posts) {
        message += "<p>" + post.post_subject + " " +post.post_price + "</p>";
    }
    return message;
}



function sendApiRequest(url, method, data, successCallback, failureCallback) {
    let request = new XMLHttpRequest();
    request.onreadystatechange = function () {
        if (this.readyState == 4) {
            if (this.status == 200) {
                successCallback(this);
            } else {
                failureCallback(this);
            }
        }
    };

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
    let url = "/api/users";
    let data = {inputFirstName: "Blob", inputLastName: "Slob", inputPassword: "567" };
    sendApiRequest(url, "PATCH", data, successCallback, failureCallback);
}

function editPost() {
    let url = "/api/posts";
    let data = {id: 1, postBody: "Totally new", postSubject: "the subject is fantastically awesome", postPrice: 1000 };
    sendApiRequest(url, "PATCH", data, successCallback, failureCallback);
}

function deletePost () {
    let queryArray = "1";
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