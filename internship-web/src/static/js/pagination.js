function paginationFunction(currentPage, numberOfPages) {

    if (currentPage >= 4) {
        var start = document.getElementById("start");
        start.style.display = 'block';
    } else {
        var start = document.getElementById("start");
        start.style.display = 'none';
    }
    ;
    if (currentPage <= numberOfPages - 3 && numberOfPages >2) {
        var end = document.getElementById("end");
        end.style.display = 'block';
    } else {
        var end = document.getElementById("end");
        end.style.display = 'none';
    }
    ;
    if (currentPage >= 3) {
        var dotsNext = document.getElementById("dotsNext");
        dotsNext.style.display = 'block';
        var dotsPrev = document.getElementById("dotsPrev");
        dotsPrev.style.display = 'block';
    } else {
        var dotsNext = document.getElementById("dotsNext");
        var dotsPrev = document.getElementById("dotsPrev");
        dotsPrev.style.display = 'none';
        dotsNext.style.display = 'block';
    }
    ;
    if (currentPage >= numberOfPages - 3) {
        var dotsNext = document.getElementById("dotsNext");
        dotsNext.style.display = 'none';
    }
    ;
    var liNodes = [];
    var ul = document.getElementById("pagination");
    var allVals = 0;
    var len = ul.childNodes.length;
    for (var i = 0; i < len; i++) {
        if (ul.childNodes[i].nodeName == "LI") {
            allVals++;
            liNodes.push(ul.childNodes[i]);
        }

    }
    ;

}

