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
    $.extend( {
        hash: function(newHash) {
            if(newHash == undefined) {
                var hashObj = {};
                var urlHash = window.location.hash.slice(1);
                var hashes = urlHash.split('&');
                for(var pos in hashes) {
                    var pair = hashes[pos].split('=');
                    if(pair[0].length > 0 && pair[1].length > 0) {
                        hashObj[pair[0]] = pair[1];
                    }
                }
                return hashObj;
            }
            else {
                var hash = '#';
                for (var key in newHash) {
                    hash = hash.concat(key, '=', newHash[key]);
                    hash = hash.concat('&');
                }
                hash = hash.substr(0, hash.length - 1);
                window.location.hash = hash;
            }
        }
    });
});