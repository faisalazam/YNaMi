$(document).ready(function () {
    $(document.getElementById("breadcrumbs"))
        .find('a[href^="$http"]')
        .each(function () {
            var oldUrl = $(this).attr("href"); // Get current url
            var newUrl = oldUrl.replace("$http", "http"); // Create new url
            $(this).attr("href", newUrl); // Set href value
        });
});