$(function () {
	$("#submitPost").click(submitPost);
});

function submitPost () {
$.post("/api/posts", {
    postBody: $("#postSubject").val(),
    postSubject: $("#postBody").val(),
    postPrice: $("#postPrice").val()}).done(getData);
}

function getData() {
    $.getJSON("/api/posts", onDataReady).fail(function(){console.log("It is not working");});
}

function onDataReady () {
    console.log("It is working");
}