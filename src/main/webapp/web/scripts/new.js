//div container
let filesContainer = $('#myFiles');
// 1 input
let inputFile = $('#media');
// arr with to be added pics
let files = [];

$(document).ready(function () {
    //pretty drag and drop
    $('input[type="file"]').imageuploadify();
    //submit the request
    $("#btnSubmit").click(sendApiRequest);
    console.log(files);
    inputFile.change(addToFiles);
});

function addToFiles () {

    let newFiles = [];

    for(let index = 0; index < inputFile[0].files.length; index++) {
        let file = inputFile[0].files[index];
        newFiles.push(file);
        files.push(file);
    }

    //presenting added elements again
    newFiles.forEach(file => {
        //so we can see and delete them
        let fileElement = $(`<p>${file.name}</p>`);
        fileElement.data('fileData', file);
        filesContainer.append(fileElement);

        //removing elements if needed
        fileElement.click(function(event) {
            let fileElement = $(event.target);
            let indexToRemove = files.indexOf(fileElement.data('fileData'));
            fileElement.remove();
            files.splice(indexToRemove, 1);
        });
    });
    console.log(files);
}



function sendApiRequest(event) {

    //stop submit the form, we will post it manually.
    event.preventDefault();

    // Get form
    let form = $('#fileUploadForm')[0];

    // Create an FormData object
    let data = new FormData(form);
    console.log(files);


    // appending files from the arr to the data(formdata)
    //files.forEach(file => {data.append('file', file);});
    for (let i = 0; i< files.length-1; ++i) {
        data.append('file', files[i]);
    }


    // If you want to add an extra field for the FormData
    data.append("CustomField", "This is some extra data, testing");

    //the way to console.log dataform
    for (let key of data.entries()) {
        console.log(key[0] + ', ' + key[1]);
    }
    // disabled the submit button
    $("#btnSubmit").prop("disabled", true);


    //TODO keep in mind that the price is an int for now
    //TODO notify user 10mb max size + alert

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

