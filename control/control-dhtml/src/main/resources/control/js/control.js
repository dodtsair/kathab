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

$(document).ready(function() {
    $(document).on("level", function(event, data) {
        var pieData = [];
        for(var dataPoint in data) {
            pieData.push(data[dataPoint]);
        }
        $(".level-filter").sparkline(pieData, {
            type: 'pie',
            width: '100%',
            height: '100%'});
    });
    $(document).on("prefix", function(event, data) {
        var avg = 0;
        var count = 0;
        var max = 0;
        for(var dataPoint in data) {
            avg += data[dataPoint];
            count++;
            if(max < data[dataPoint]) {
                max = data[dataPoint];
            }
        }
        avg = avg / count;
        for(var dataPoint in data) {
            var bulletData = [];
            bulletData.push(avg);
            bulletData.push(data[dataPoint]);
            bulletData.push(max);
            bulletData.push(avg);
            bulletData.push(avg - (max - avg));
            $('.prefix-filter').append($(document.createElement('span')).html(bulletData.join()));
        }
        $('.prefix-filter span').sparkline('html', {
             type: 'bullet',
             targetColor: 'rgba(0,0,0,0)',
             width: '100%',
             height: '100%'})
    });
});
