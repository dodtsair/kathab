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

$(document).ready(function(){
    $('.jframe').each(function (index, preElement) {
        var element = $(preElement);
        var url = element.attr('data-src');
//        $.get('http://html.comsci.us/examples/blank1.html').success(function (fragment) {
        $.get(element.attr('data-src'), 'xml', function (fragment) {
//        element.load(element.attr('data-src')).success(function (fragment) {
            var fragment = $(fragment);
//        tempContainer.load(element.attr('data-src'), function (fragment) {
//        $.ajax({url: 'http://html.comsci.us/examples/blank1.html'}).success(function (fragment) {
            fragment.children('html>*').unwrap();
            fragment.children('head>*').unwrap();
            fragment.children('body>*').unwrap();
            //Elements with a relative src link
            var urlAttribs = ['href', 'src']
            for(var urlAttribIndex in urlAttribs) {
                fragment.children('[' + urlAttribs[urlAttribIndex] + ']').each(function (index, srcElement) {
                    var srcElement = $(srcElement);
                    var href = srcElement.attr(urlAttribs[urlAttribIndex]);
                    srcElement.attr(urlAttribs[urlAttribIndex], element.attr('data-src') + '/../' + srcElement.attr(urlAttribs[urlAttribIndex]));
                })
            }
            element.append(fragment.children());
            $('body').children('.wrapped').each(function(index, value) {
                alert(index, value);
            });
            element.trigger('jframe-ready')
        });
    });

});


