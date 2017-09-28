

function getJson() {
	$.getJSON("/api/posts", ready).fail(function () {console.log("failed to get /api/posts");});
}

function logIn () {
    $.post("/api/login", { userName: "ledorub", password: "123" }).done(getJson);
}

function editUser () {
    var url = "/api/users";
    var data = {inputFirstName: "Blob", inputLastName: "Slob", inputPassword: "567" };
    tryPatchJson(url, data, successCallback, failureCallback);
}

function editPost() {
    var url = "/api/posts";
    var data = {inputPostId: 1, inputPostBody: "Totally new", inputPostSubject: "the subject is fantastically awesome", inputPostPrice: 1000 };
    tryPatchJson(url, data, successCallback, failureCallback);
}

function tryPatchJson(url, data, successCallback, failureCallback) {
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

    request.open("PATCH", url, true);
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

function ready(data) {
	console.log("logged in!");
	console.log(data);
}
