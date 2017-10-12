$(document).ready(function () {
    $("#btnSubmit").click(sendApiRequest);
});

function sendApiRequest(event) {

    //stop submit the form, we will post it manually.
    event.preventDefault();

    // Get form
    var form = $('#fileUploadForm')[0];

    // Create an FormData object
    var data = new FormData(form);

    // If you want to add an extra field for the FormData
    data.append("CustomField", "This is some extra data, testing");

    //the way to console.log dataform
    for (var key in data.entries()) {
        console.log(key[0] + ', ' + key[1]);
    }
    // disabled the submit button
    $("#btnSubmit").prop("disabled", true);

    $.ajax({
        type: "POST",
        enctype: 'multipart/form-data',
        url: "/api/posts",
        data: data,
        processData: false,
        contentType: false,
        cache: false,
        timeout: 600000,
        success: succeessCallBack,
        error: failureCallBack
    });

}

function succeessCallBack (data) {
    $("#result").text(data);
    console.log("SUCCESS : ", data);
    $("#btnSubmit").prop("disabled", false);
}

function failureCallBack(e) {
    $("#result").text(e.responseText);
    console.log("ERROR : ", e);
    $("#btnSubmit").prop("disabled", false);
}