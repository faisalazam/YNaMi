function SideBar() {
    SideBar.prototype.onDocReady = function () {
        var that = this;
        $(".side-bar-toggle").click(function () {
            that.toggleSideBar(this, 'holyGrail-nav-container', 'fa-angle-double-left fa-angle-double-right');
        });
        this.bindClickToMenuDropDowns();
    };

    SideBar.prototype.bindClickToMenuDropDowns = function () {
        var that = this;
        var dropdown = document.getElementsByClassName("menu-dropdown");

        var i;
        for (i = 0; i < dropdown.length; i++) {
            dropdown[i].addEventListener("click", function() {
                $( this ).parent().find( 'a.active' ).removeClass( 'active' );
                this.classList.toggle("active");
                $(this).children(":first").toggleClass("fa-caret-down fa-caret-up");
                var dropdownContent = this.nextElementSibling;
                $(dropdownContent).slideToggle(1000);
            });
        }
    };

    SideBar.prototype.toggleSideBar = function (toggleSideBarElement, classOfElementToToggle, classesToToggle) {
        $(toggleSideBarElement).children(":first").toggleClass(classesToToggle);

        var elementToToggle = document.getElementsByClassName(classOfElementToToggle)[0];

        $(elementToToggle).fadeToggle( 1000);
    };
}

$(document).ready(function () {
    var sideBar = new SideBar();
    sideBar.onDocReady();
});