// Lightweight UI helpers for Grocery App.
(function () {
    "use strict";

    // Auto-dismiss flash messages after 6 seconds.
    document.addEventListener("DOMContentLoaded", function () {
        document.querySelectorAll(".flash").forEach(function (el) {
            setTimeout(function () {
                el.style.transition = "opacity .4s";
                el.style.opacity = "0";
                setTimeout(function () { el.remove(); }, 450);
            }, 6000);
        });
    });
})();
