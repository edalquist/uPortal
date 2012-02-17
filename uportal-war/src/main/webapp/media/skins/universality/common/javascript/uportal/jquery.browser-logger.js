"use strict";
var up = up || {};

(function ($, fluid) {
    
    up.browserLogger = function(callerSettings) {
        var settings = $.extend({
            url: 'ajax/browserInfo',
            windowSize: true,
            screenSize: true
        }, callerSettings||{});
        
        var logData = function(action) {
            var data = {'action' : action};
            if (settings.windowSize) {
                data['window.width'] = $(window).width();
                data['window.height'] = $(window).height();
            }
            if (settings.screenSize) {
                data['screen.width'] = screen.width;
                data['screen.height'] = screen.height;
            }
            
            $.post(settings.url, data);
        };
        
        //Attach to window resize events to log size info
        $(window).resize(function () {
            if ($(this).data('windowSizeTimer')) {
                clearTimeout($(this).data('windowSizeTimer'));
                $(this).data('windowSizeTimer', null);
            }

            var timerId = setTimeout(function () {
                logData('resize');
            }, 500);
            
            $(this).data('windowSizeTimer', timerId);
        });
        
        //Log initial page sizes 50ms after page load
        setTimeout(function () {
        	logData('load');
        }, 50);
    };
})(jQuery, fluid);
