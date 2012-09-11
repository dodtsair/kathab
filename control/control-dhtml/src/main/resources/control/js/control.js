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
            sliceColors: ['rgba(256,0,0,100)','rgba(256,256,0,100)','rgba(0,256,0,100)','rgba(0,0,256,100)','rgba(256,256,256,100)'],
            width: '100%',
            height: '100%'});
    });
    $(document).on("prefix", function(event, data) {
        data.points.sort(function(a,b) {
            return b.value - a.value;
        });
        for(var dataPoint in data.points) {
            var bulletData = [];
            bulletData.push(data.mean);
            bulletData.push(data.points[dataPoint].value);
            bulletData.push(data.mean + data.stdDeviation*2);
            bulletData.push(data.mean + data.stdDeviation);
            bulletData.push(data.mean - data.stdDeviation);
            $('.prefix-filter').append($(document.createElement('span')).html(bulletData.join()));
        }
        $('.prefix-filter span').sparkline('html', {
             type: 'bullet',
             targetColor: 'rgba(0,0,0,100)',
             rangeColors: ['rgba(100,100,100,100)','rgba(66,66,66,100)','rgba(33,33,33,100)'],
             width: '100%',
             height: '100%'})
    });
});
