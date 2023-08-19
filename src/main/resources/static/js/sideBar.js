function SideBar() {
    SideBar.prototype.onDocReady = function () {
        const that = this;
        $(".side-bar-toggle").click(function () {
            that.toggleSideBar(this,
                'holyGrail-nav-container',
                'fa-angle-double-left fa-angle-double-right');
        });
        this.bindClickToMenuDropDowns();
    };

    SideBar.prototype.bindClickToMenuDropDowns = function () {
        const dropdown = document.getElementsByClassName("menu-dropdown");

        let i;
        for (i = 0; i < dropdown.length; i++) {
            dropdown[i].addEventListener("click", function () {
                $(this).parent().find('a.active').removeClass('active');
                this.classList.toggle("active");
                $(this).children(":first").toggleClass("fa-caret-down fa-caret-up");
                const dropdownContent = this.nextElementSibling;
                $(dropdownContent).slideToggle(1000);
            });
        }
    };

    SideBar.prototype.toggleSideBar = function (toggleSideBarElement, classOfElementToToggle, classesToToggle) {
        $(toggleSideBarElement).children(":first").toggleClass(classesToToggle);

        const elementToToggle = document.getElementsByClassName(classOfElementToToggle)[0];

        $(elementToToggle).fadeToggle(1000);
    };
}

$(document).ready(function () {
    const sideBar = new SideBar();
    sideBar.onDocReady();
});