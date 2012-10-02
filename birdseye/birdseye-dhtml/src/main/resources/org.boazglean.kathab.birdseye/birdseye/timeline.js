/*
 * The MIT License
 *
 * Copyright 2012 mpower.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

requirejs.config({
    baseUrl: '..',
    shim: {
        'com.beebole.pure/pure-js/pure': {
            //These script dependencies should be loaded before loading
            //pure.js
            deps: ['org.jquery/jquery-js/jquery'],
        },
        'org.jquery/jquery-sparkline/jquery.sparkline': {
            //These script dependencies should be loaded before loading
            //pure.js
            deps: ['org.jquery/jquery-js/jquery'],
        },
    }
});

requirejs(['org.jquery/jquery-js/jquery', 'com.beebole.pure/pure-js/pure', 'org.jquery/jquery-sparkline/jquery.sparkline'],
function   () {

     $(document).ready(function() {

         var timelineRender = $('.event-timeline').compile(
         {
            '.@data-values' : function(arg) {
                var series = [];
                var eventCounts = [];
                for(var event in arg.context) {
                    series.push(event)
                }
                series.sort(function(a, b) {
                    return a - b;
                })
                for(var pos in series) {
                    eventCounts.push(series[pos] + ":" + arg.context[series[pos]])
                }
                return eventCounts.join();
            }
         });

        $(document).on("summary/period", function(event, data) {
            $('.event-timeline').replaceWith(timelineRender(data));
            $('.event-timeline').sparkline('html', {
                type: 'line',
                width: '100%',
                height: '100%',
                tagValuesAttribute: 'data-values'
            });
        });
        $.getScript('../../api/summary/period')
     });
});