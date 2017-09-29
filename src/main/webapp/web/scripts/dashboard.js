/**
 * Created by Anton on 29.09.2017.
 */

$(function () {
    logIn();
    searchFormSetUp();
});

function logIn () {
    $.post("/api/login", { userName: "ledorub", password: "123" }).done(todo);
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