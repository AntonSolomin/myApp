$.post("/api/login", { userName: "ledorub", password: "123" }).done(getJson);

function getJson() {
	$.getJSON("/api/posts", ready).fail(function () {console.log("failed to get /api/posts");});
}

function ready(data) {
	console.log("logged in!");
	console.log(data);
}
