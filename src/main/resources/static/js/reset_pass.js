var protocol = window.location.protocol === "https:" ? "https://" : "http://";
var serverContext = protocol + document.location.host;

function resetPass() {
    var email = $("#email").val();

    var url = serverContext + "/password/resetPassword";

    $.ajax({
        type: "POST",
        url: url,
        data: {email: email},
        success: function (data) {
            window.location.href = serverContext + "/login?message=" + data.message;
        },
        error: function (data) {
            if (data.responseJSON.error.indexOf("MailError") > -1) {
                var errors = [data.responseJSON.message];
                generateErrors(errors);
            } else {
                window.location.href =
                        serverContext + "/login?message=" + data.responseJSON.message;
            }
        }
    });
}


