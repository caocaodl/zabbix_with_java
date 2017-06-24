$(function() {
    /**
     * This file is part of Qunee for HTML5.
     * Copyright (c) 2015 by qunee.com
     **/

    function Matrix2D(m) {
        this.m = m || [1, 0, 0, 1, 0, 0];
    }
    Matrix2D.multiply = function(out, a, b) {
        var a0 = a[0],
            a1 = a[1],
            a2 = a[2],
            a3 = a[3];
        var b0 = b[0],
            b1 = b[1],
            b2 = b[2],
            b3 = b[3];
        out[0] = a0 * b0 + a2 * b1;
        out[1] = a1 * b0 + a3 * b1;
        out[2] = a0 * b2 + a2 * b3;
        out[3] = a1 * b2 + a3 * b3;
        return out;
    };
    Matrix2D.prototype = {
        m: null,
        rotate: function(rad, out) {
            var s = Math.sin(rad),
                c = Math.cos(rad);
            var rotateM = [c, s, -s, c];
            return Matrix2D.multiply(out || this.m, rotateM, this.m);
        },
        scale: function(sx, sy, out) {
            var scaleM = [sx, 0, 0, sy];
            return Matrix2D.multiply(out || this.m, scaleM, this.m);
        },
        translate: function(tx, ty, out) {
            out = out || this.m;
            out[4] += tx;
            out[5] += ty;
            return out;
        },
        translatePoint: function(x, y) {
            var a = this.m;
            var a0 = a[0],
                a1 = a[1],
                a2 = a[2],
                a3 = a[3];
            return {
                x: a0 * x + a2 * y + a[4],
                y: a1 * x + a3 * y + a[5]
            };
        }
    }

    Q.Matrix2D = Matrix2D;
    Q.loadClassPath(Matrix2D, 'Q.Matrix2D'); //为了能导入导出，需要能全局访问

    function HeatMap(canvas, points) {
        this.points = points || [];
        this.cache = {};
        this.canvas = canvas || document.createElement('canvas');
        this.defaultRadius = 80;
        this.defaultIntensity = 0.2;
        this.setGradientStops({
            0.00: 0xffffff00,
            0.10: 0x99e9fdff,
            0.20: 0x00c9fcff,
            0.30: 0x00e9fdff,
            0.30: 0x00a5fcff,
            0.40: 0x0078f2ff,
            0.50: 0x0e53e9ff,
            0.60: 0x4a2cd9ff,
            0.70: 0x890bbfff,
            0.80: 0x99019aff,
            0.90: 0x990664ff,
            0.99: 0x660000ff,
            1.00: 0x000000ff
        });
    }
    HeatMap.prototype = {
        setGradientStops: function(stops) {
            var ctx = document.createElement('canvas').getContext('2d');
            var grd = ctx.createLinearGradient(0, 0, 256, 0);

            for (var i in stops) {
                grd.addColorStop(i, 'rgba(' +
                    ((stops[i] >> 24) & 0xFF) + ',' +
                    ((stops[i] >> 16) & 0xFF) + ',' +
                    ((stops[i] >> 8) & 0xFF) + ',' +
                    ((stops[i] >> 0) & 0xFF) + ')');
            }

            ctx.fillStyle = grd;
            ctx.fillRect(0, 0, 256, 1);
            this.gradient = ctx.getImageData(0, 0, 256, 1).data;
        },
        drawHeatPoints: function(alpha, ctx) {
            var offsetX = 0,
                offsetY = 0;
            this.cache = {};

            ctx = ctx || this.canvas.getContext('2d');
            ctx.strokeStyle = "#888";
            ctx.lineWidth = 4;
            ctx.strokeRect(0, 0, this.canvas.width, this.canvas.height);

            ctx.save(); // Workaround for a bug in Google Chrome
            ctx.fillStyle = 'transparent';
            ctx.fillRect(0, 0, this.canvas.width, this.canvas.height);
            ctx.restore();

            for (var i in this.points) {

                var src = this.points[i];
                var rad = src.radius || this.defaultRadius;
                var int = src.intensity || this.defaultIntensity;
                var pos = src;
                var x = pos.x - rad + offsetX;
                var y = pos.y - rad + offsetY;

                if (!this.cache[int]) {
                    this.cache[int] = {};
                }

                if (!this.cache[int][rad]) {
                    var grd = ctx.createRadialGradient(rad, rad, 0, rad, rad, rad);
                    grd.addColorStop(0.0, 'rgba(0, 0, 0, ' + int + ')');
                    grd.addColorStop(1.0, 'transparent');
                    this.cache[int][rad] = grd;
                }

                ctx.fillStyle = this.cache[int][rad];
                ctx.translate(x, y);
                ctx.fillRect(0, 0, 2 * rad, 2 * rad);
                ctx.translate(-x, -y);
            }

            var dat = ctx.getImageData(0, 0, this.canvas.width, this.canvas.height);
            var dim = this.canvas.width * this.canvas.height * 4;
            var pix = dat.data;

            for (var p = 0; p < dim; /* */ ) {
                var a = pix[p + 3] * 4;
                pix[p++] = this.gradient[a++];
                pix[p++] = this.gradient[a++];
                pix[p++] = this.gradient[a++];
                pix[p++] = parseInt(this.gradient[a++] * (alpha || 1));
            }

            ctx.putImageData(dat, 0, 0);
        }
    }
     Q.HeatMap = HeatMap;
    Q.loadClassPath(HeatMap, 'Q.HeatMap'); //为了能导入导出，需要能全局访问
});