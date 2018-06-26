function generateErrors(errorsArray) {
    var errors = document.getElementById('hidden-errors');
    var ul = errors.getElementsByTagName('ul')[0];
    removeChildren(ul);
    errorsArray.forEach(function (error) {
        var li = document.createElement('li');
        li.innerText = error;
        ul.appendChild(li);
    });
    errors.style.display = 'flex';
}

function removeChildren(element) {
    while (element.firstChild) {
        element.removeChild(element.firstChild);
    }
}


