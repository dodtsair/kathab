requirejs.config({
    shim: {
        'com.beebole.pure/pure-js/pure': {
            //These script dependencies should be loaded before loading
            //pure.js
            deps: ['org.jquery/jquery-js/jquery'],
        },
        'org.jquery/jquery-sparkline/jquery.sparkline': {
            //These script dependencies should be loaded before loading
            //sparkline.js
            deps: ['org.jquery/jquery-js/jquery'],
        },
        'org.boazglean.kathab.web/web-api/jframe': {
            //These script dependencies should be loaded before loading
            //jframe.js
            deps: ['org.jquery/jquery-js/jquery'],
        },
        'org.boazglean.kathab.web/web-api/hash': {
            //These script dependencies should be loaded before loading
            //hash.js
            deps: ['org.jquery/jquery-js/jquery'],
        },
    }
});