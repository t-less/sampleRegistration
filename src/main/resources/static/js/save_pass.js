var protocol = window.location.protocol === "https:" ? "https://" : "http://";
var serverContext = protocol + document.location.host;

function savePass() {

    var formData = $('form').serialize();
    var url = serverContext + "/password/savePassword";

    $.ajax({
        type: "POST",
        url: url,
        data: formData,
        success: function (data) {
            window.location.href = serverContext + "/login?message=" + data.message;
        },
        error: function (data) {
            var errors = [data.responseJSON.message];
            generateErrors(errors);
        }
    });
}


