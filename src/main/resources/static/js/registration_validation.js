function validateRegistration(messagesObj) {

    var invalidFirstName = messagesObj.invalidFirstName;
    var invalidLastName = messagesObj.invalidLastName;
    var invalidEmail = messagesObj.invalidEmail;
    var invalidPass = messagesObj.invalidPass;
    var invalidMatchingPass = messagesObj.invalidMatchingPass;
    var passwordsDoNotMatch = messagesObj.passwordsDoNotMatch;

    if (document.f.firstName.value == "") {
        var errors = [invalidFirstName];
        generateErrors(errors);
        document.f.firstName.focus();
        return false;
    }
    if (document.f.lastName.value == "") {
        var errors = [invalidLastName];
        generateErrors(errors);
        document.f.lastName.focus();
        return false;
    }
    if (document.f.email.value == "") {
        var errors = [invalidEmail];
        generateErrors(errors);
        document.f.email.focus();
        return false;
    }
    if (document.f.password.value == "") {
        var errors = [invalidPass];
        generateErrors(errors);
        document.f.password.focus();
        return false;
    }
    if (document.f.matchingPassword.value == "") {
        var errors = [invalidMatchingPass];
        generateErrors(errors);
        document.f.matchingPassword.focus();
        return false;
    }
    if (document.f.password.value != document.f.matchingPassword.value) {
        var errors = [passwordsDoNotMatch];
        generateErrors(errors);
        document.f.matchingPassword.focus();
        return false;
    }

    return true;

}


