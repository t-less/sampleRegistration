var protocol = window.location.protocol === "https:" ? "https://" : "http://";
var serverContext = protocol + document.location.host;

function register() {
    var formData = $('form').serialize();
    var url = serverContext + "/registration";

    $.ajax({
        type: "POST",
        url: url,
        data: formData,

        success: function (data) {
            if (data.message == "success") {
                window.location.href = serverContext + "/successRegister";
            }
        },
        error: function (data) {
            if (data.responseJSON.error.indexOf("MailError") > -1 
                    || data.responseJSON.error.indexOf("InternalError") > -1
                    || data.responseJSON.error.indexOf("UserAlreadyExist") > -1) {
                var errors = [data.responseJSON.message];
                generateErrors(errors);
            }  else {
                var errors = $.parseJSON(data.responseJSON.message);
                generateErrors(errors);
            }
        }
    });
}




