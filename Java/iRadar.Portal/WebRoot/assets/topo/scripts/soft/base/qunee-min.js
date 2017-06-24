window.Q = function(t, i, e) {
    "use strict";

    function n(t, i, e) {
        if (t[Nr]()) {
            var s = t._ez || t.getChildren();
            if (s) {
                s = s._im || s;
                for (var r = 0, h = s[jr]; h > r; r++)
                    if (i[Br](e, s[r]) === !1 || n(s[r], i, e) === !1) return !1;
                return !0
            }
        }
    }

    function s(t) {
        if (!t[Nr]()) return t instanceof xN ? t : null;
        for (var i, e = t._ez._im, n = e[jr] - 1; n >= 0;) {
            if (i = e[n], i = s(i)) return i;
            n--
        }
        return null
    }

    function r(t, i, e, n) {
        return n ? h(t, i, e) : a(t, i, e)
    }

    function h(t, i, e) {
        t = t._im || t;
        for (var n, s = 0, r = t.length; r > s; s++)
            if (n = t[s], n[Nr]() && !h(n.children, i, e) || i.call(e, n) === !1) return !1;
        return !0
    }

    function a(t, i, e) {
        t = t._im || t;
        for (var n, s = 0, r = t.length; r > s; s++)
            if (n = t[s], i[Br](e, n) === !1 || n[Nr]() && !a(n[zr], i, e)) return !1;
        return !0
    }

    function o(t, i, e, n) {
        return n ? _(t, i, e) : f(t, i, e)
    }

    function _(t, i, e) {
        t = t._im || t;
        for (var n, s = t[jr], r = s - 1; r >= 0; r--)
            if (n = t[r], n.hasChildren() && !_(n.children, i, e) || i.call(e, n) === !1) return !1;
        return !0
    }

    function f(t, i, e) {
        t = t._im || t;
        for (var n, s = t.length, r = s - 1; r >= 0; r--)
            if (n = t[r], i[Br](e, n) === !1 || n[Nr]() && !f(n[zr], i, e)) return !1;
        return !0
    }

    function u(t, i, e) {
        for (var n, s = (t._im || t)[$r](0); s[jr];) {
            n = s[0],
                s = s.splice(1);
            var r = i.call(e, n);
            if (r === !1) return !1;
            if (n.hasChildren()) {
                var h = n[zr];
                h = h._im || h,
                    s = s.concat(h)
            }
        }
        return !0
    }

    function c(t, i, e) {
        for (var n, s = (t._im || t)[$r](0); s[jr];) {
            n = s[s[jr] - 1],
                s = s[Gr](0, s.length - 1);
            var r = i.call(e, n);
            if (r === !1) return !1;
            if (n.hasChildren()) {
                var h = n.children;
                h = h._im || h,
                    s = s.concat(h)
            }
        }
        return !0
    }

    function d(t, i) {
        function e(t, e) {
            for (var n = t.length, s = e.length, r = n + s, h = new Array(r), a = 0, o = 0, _ = 0; r > _;) h[_++] = a === n ? e[o++] : o === s || i(t[a], e[o]) <= 0 ? t[a++] : e[o++];
            return h
        }

        function n(t) {
            var i = t.length,
                s = Math.ceil(i / 2);
            return 1 >= i ? t : e(n(t[$r](0, s)), n(t[$r](s)))
        }
        return n(t)
    }

    function l(t, i, e, n) {
        t instanceof LD && (t = t._im);
        for (var s = 0, r = (t._im || t)[jr]; r > s; s++) {
            var h = i[Br](e, t[s], s, n);
            if (h === !1) return !1
        }
        return !0
    }

    function v(t, i, e) {
        for (var n = t instanceof LD, s = t._im || t, r = 0, h = s.length; h > r; r++) {
            var a = s[r];
            i.call(e, a) && (n ? t.remove(a) : t[Gr](r, 1), r--, h--)
        }
    }

    function b(t, i, e, n) {
        t instanceof LD && (t = t._im);
        for (var s = (t._im || t)[jr] - 1; s >= 0; s--) {
            var r = i[Br](e, t[s], s, n);
            if (r === !1) return !1
        }
        return !0
    }

    function g(t) {
        if (t[Fr] instanceof Function) return t[Fr](!0);
        var i,
            e = [];
        return l(t,
                function(t) {
                    i = t && t.clone instanceof Function ? t.clone() : t,
                        e[Yr](i)
                },
                this),
            e
    }

    function y(t, i, n) {
        n === e || 0 > n ? t[Yr](i) : t[Gr](n, 0, i)
    }

    function m(t, i) {
        var e = t.indexOf(i);
        return 0 > e || e >= t[jr] ? !1 : t[Gr](e, 1)
    }

    function p(t, i) {
        var e = !1;
        return l(t,
                function(t) {
                    return i == t ? (e = !0, !1) : void 0
                }),
            e
    }

    function E(t, i, e) {
        return i instanceof Object ? t = F(i, t) : i && !e && (e = parseInt(i)),
            i && !e && (e = parseInt(i)),
            e ? setTimeout(t, e) : setTimeout(t)
    }

    function x(i, e) {
        return e && (i = F(e, i)),
            t[qr](i)
    }

    function T(t, i) {
        return t[Hr] = i,
            t
    }

    function w(t, i) {
        if (!t.hasOwnProperty(Ur)) {
            var e = t.getAttribute(Wr);
            if (!e) return T(t, i);
            for (var n = e[Xr](Vr), s = 0, r = n.length; r > s; s++)
                if (n[s] == i) return;
            return e += Vr + i,
                T(t, e)
        }
        t[Ur].add(i)
    }

    function O(t, i) {
        if (!t.hasOwnProperty(Ur)) {
            var e = t.getAttribute(Wr);
            if (!e || !e[Kr](i)) return;
            for (var n = "", s = e.split(Vr), r = 0, h = s.length; h > r; r++) s[r] != i && (n += s[r] + Vr);
            return T(t, n)
        }
        t[Ur][Zr](i)
    }

    function I(t) {
        return t instanceof Number || Jr == typeof t
    }

    function A(t) {
        return t !== e && (t instanceof String || Qr == typeof t)
    }

    function S(t) {
        return t !== e && (t instanceof Boolean || th == typeof t)
    }

    function C(t) {
        return Array.isArray(t)
    }

    function k(i) {
        i || (i = t.event),
            i.preventDefault ? i[ih]() : i[eh] = !1
    }

    function L(i) {
        i || (i = t.event),
            i.stopPropagation ? i.stopPropagation() : i[nh] || (i[nh] = !0)
    }

    function R(t) {
        k(t),
            L(t)
    }

    function D(t) {
        return Math[sh](Math[rh]() * t)
    }

    function M() {
        return Math.random() >= .5
    }

    function P(t, i) {
        var e = t;
        for (var n in i)
            if (i.__lookupGetter__) {
                var s = i.__lookupGetter__(n),
                    r = i.__lookupSetter__(n);
                s || r ? (s && e.__defineGetter__(n, s), r && e.__defineSetter__(n, r)) : e[n] = i[n]
            } else e[n] = i[n];
        return e
    }

    function N(t, i, e) {
        if (!(t instanceof Function)) throw new Error("subclass must be type of Function");
        var n = null;
        hh == typeof i && (n = i, i = t, t = function() {
            i.apply(this, arguments)
        });
        var s = t[ah],
            r = function() {};
        return r[ah] = i[ah],
            t[ah] = new r,
            t[oh] = i.prototype,
            t[oh].constructor = i,
            P(t[ah], s),
            n && P(t.prototype, n),
            e && P(t.prototype, e),
            t.prototype.class = t,
            t
    }

    function j(t, i, e) {
        return B(t, i, "constructor", e)
    }

    function B(t, i, e, n) {
        var s = i[oh];
        if (s) {
            var r = s[e];
            return r ? r[_h](t, n) : void 0
        }
    }

    function z(t) {
        return t.toFixed(4)
    }

    function $(t) {
        delete t.scope,
            delete t.handle
    }

    function G(t, i) {
        t[i] && ($(t[i]), delete t[i])
    }

    function F(t, i) {
        var e = function() {
            return e.handle.apply(e.scope, arguments)
        };
        return e[fh] = i,
            e.scope = t,
            e
    }

    function Y(t, i) {
        return t == i
    }

    function q(t, i, n, s, r) {
        if (s) return void Object.defineProperty(t, i, {
            value: n,
            enumerable: !0
        });
        var h = {
                configurable: !0,
                enumerable: !0
            },
            a = uh + i;
        n !== e && (t[a] = n),
            h.get = function() {
                return this[a]
            },
            h.set = function(t) {
                var e = this[a];
                if (Y(e, t)) return !1;
                var n = new KD(this, i, t, e);
                return this.beforeEvent(n) ? (this[a] = t, r && r.call(this, t, e), this.onEvent(n), !0) : !1
            },
            Object[ch](t, i, h)
    }

    function H(t, i) {
        for (var e = 0, n = i.length; n > e; e++) {
            var s = i[e];
            q(t, s.name || s, s[dh] || s.value, s[lh], s.onSetting)
        }
    }

    function U(t) {
        if (t && t > 0 && 1 > t) {
            var i = Math[sh](16777215 * Math[rh]());
            return vh + (i >> 16 & 255) + bh + (i >> 8 & 255) + bh + (255 & i) + bh + t.toFixed(2) + gh
        }
        return V(Math.floor(16777215 * Math.random()))
    }

    function W(t) {
        return t > 0 ? Math[sh](t) : Math[yh](t)
    }

    function X(t) {
        return t > 0 ? Math.ceil(t) : Math[sh](t)
    }

    function V(t) {
        return 16777216 > t ? mh + (ph + t.toString(16)).slice(-6) : vh + (t >> 16 & 255) + bh + (t >> 8 & 255) + bh + (255 & t) + bh + ((t >> 24 & 255) / 255).toFixed(2) + gh
    }

    function K(t, i, e) {
        hh != typeof e || e.hasOwnProperty(Eh) || (e.enumerable = !0),
            Object[ch](t, i, e)
    }

    function Z(t, i) {
        for (var e in i)
            if (xh != e[0]) {
                var n = i[e];
                hh != typeof n || n.hasOwnProperty(Eh) || (n.enumerable = !0)
            }
        Object.defineProperties(t, i)
    }

    function J(i, e) {
        e || (e = t);
        for (var n = i[Xr](Th), s = 0, r = n[jr]; r > s; s++) {
            var h = n[s];
            e = e[h]
        }
        return e
    }

    function Q(t) {
        return t instanceof MouseEvent || t instanceof Object && t[wh] !== e
    }

    function ti(i) {
        t[Oh] && console.log(i)
    }

    function ii(i) {
        t.console && console.trace(i)
    }

    function ei(i) {
        t[Oh] && console.error(i)
    }

    function ni(t, i, e) {
        var n,
            s,
            r;
        0 == t._my ? (n = -1, r = 0, s = i) : 0 == t._n0 ? (n = 0, r = 1, s = e) : (n = -1 / t._my, s = (t._my - n) * i + t._mx, r = 1);
        var h = new ND;
        return h._my = n,
            h._mx = s,
            h._n0 = r,
            h._mv = i,
            h._mr = e,
            h._kr = Math[Ih](n, r),
            h._n0os = Math.cos(h._kr),
            h._sin = Math.sin(h._kr),
            h
    }

    function si(t, i, e, n, s) {
        var r,
            h;
        i > n ? r = -1 : n > i && (r = 1),
            e > s ? h = -1 : s > e && (h = 1);
        var a,
            o;
        if (!r) return o = 0 > h ? t.y : t[Ah], {
            x: i,
            y: o
        };
        if (!h) return a = 0 > r ? t.x : t.right, {
            x: a,
            y: e
        };
        var _ = (e - s) / (i - n),
            f = e - _ * i,
            u = 0 > r ? i - t.x : i - t.right,
            c = 0 > h ? e - t.y : e - t.bottom;
        return Math.abs(_) >= Math[Sh](c / u) ? (o = 0 > h ? t.y : t[Ah], a = (o - f) / _) : (a = 0 > r ? t.x : t.right, o = _ * a + f), {
            x: a,
            y: o
        }
    }

    function ri(t, i, e, n, s, r, h, a) {
        return 0 >= h || 0 >= a || 0 >= e || 0 >= n ? !1 : (h += s, a += r, e += t, n += i, (s > h || h > t) && (r > a || a > i) && (t > e || e > s) && (i > n || n > r))
    }

    function hi(t, i, e, n, s, r) {
        return s >= t && t + e >= s && r >= i && i + n >= r
    }

    function ai(t, i, e, n, s, r, h, a) {
        return s >= t && r >= i && t + e >= s + h && i + n >= r + a
    }

    function oi(t, i, n) {
        if (!t) return {
            x: 0,
            y: 0
        };
        if (t.x !== e) return {
            x: t.x,
            y: t.y
        };
        var s,
            r,
            h = t[Ch],
            a = t[kh];
        switch (h) {
            case GD:
                s = 0;
                break;
            case YD:
                s = i;
                break;
            default:
                s = i / 2
        }
        switch (a) {
            case qD:
                r = 0;
                break;
            case UD:
                r = n;
                break;
            default:
                r = n / 2
        }
        return {
            x: s,
            y: r
        }
    }

    function _i(t, i, e) {
        t[zr][Lh](i, e),
            t[Rh](i, e)
    }

    function fi(t, i) {
        t._ez && (t._ez.remove(i), t[Dh](i))
    }

    function ui(t) {
        return t.replace(/^-ms-/, Mh)[Ph](/-([\da-z])/gi,
            function(t, i) {
                return i.toUpperCase()
            })
    }

    function ci(t, i) {
        var e = t.style;
        if (!e) return !1;
        var n,
            s;
        for (n in i) i.hasOwnProperty(n) && (s = dM(n)) && (e[s] = i[n]);
        return t
    }

    function di(t, i, e) {
        (i = dM(i)) && (t.style[i] = e)
    }

    function li(t, i) {
        return uM ? uM[Nh] ? void uM[Nh](t + jh + i + Bh, 0) : void(uM[zh] && uM[zh](t, i, 0)) : !1
    }

    function vi(i, e) {
        i[wh] && (i = i[$h] && i[$h].length ? i[$h][0] : i.touches[0]);
        var n = e[Gh](),
            s = i.clientX || 0,
            r = i.clientY || 0;
        return OD && pD && (t.pageXOffset && s == i[Fh] && (s -= t.pageXOffset), t[Yh] && r == i.pageY && (r -= t.pageYOffset)), {
            x: s - n.left,
            y: r - n.top
        }
    }

    function bi(t, i) {
        return this[uh + i] = vM(t, i,
            function(t) {
                return pi[Br](this, t, i)
            }, !1, this)
    }

    function gi(t) {
        var i = this;
        return t.getData = function() {
                return i._k2[qh](t)
            },
            t[Hh] = function() {
                return i._k2[Uh](t)
            },
            t
    }

    function yi(t) {
        this.__n0ancelClick || (this.__n0lickEvent = t, this.__n0lickTime ? this.__n0lickTime++ : (this.__n0lickTime = 1, setTimeout(F(this,
            function() {
                if (this.__n0lickEvent) {
                    var t = this.__n0lickTime;
                    this.__n0lickTime = 0,
                        1 == t ? this._hf(this.__n0lickEvent, Wh) : t > 1 && this._hf(this.__n0lickEvent, Xh),
                        this.__n0lickEvent = null
                }
            }), CD.DOUBLE_CLICK_INTERVAL_TIME)))
    }

    function mi(t) {
        if (t.touches) {
            for (var i = t[wh], e = [], n = 0, s = i[jr]; s > n; n++) {
                var r = i[n];
                e.push({
                    pageX: r[Fh],
                    pageY: r.pageY,
                    clientX: r.clientX,
                    clientY: r.clientY
                })
            }
            return {
                timeStamp: t[Vh],
                touches: e,
                scale: t.scale
            }
        }
        return {
            timeStamp: t[Vh],
            x: t.clientX,
            y: t[Kh]
        }
    }

    function pi(t, i) {
        switch (t = gi.call(this, t), i) {
            case "touchstart":
                if (t.touches.length >= 2) return this._9l = mi(t),
                    this._mxt.clear(),
                    this._22(),
                    void(this._mxf || (this._mxf = t, this._9l = mi(t)));
            case "mousedown":
                if (R(t), this._hf(t, Zh), this._mxf = t, this._9l = mi(t), t[Jh] || (this.__onLongPressFunction ? this.__longPressTimer && this._22() : this.__onLongPressFunction = F(this,
                        function() {
                            this.__longPressTimer = null,
                                this._mxf && (this.__n0ancelClick = !0, this._hf(this._mxf, Qh))
                        }), this.__longPressTimer = setTimeout(this.__onLongPressFunction, CD.LONG_PRESS_INTERVAL), this.__n0ancelClick = !1), OD) return;
                return void(mM._n0urrentInteractionSupport = this);
            case "touchend":
                if (!this._mxf) return void(this._moving = null);
                if (t[wh].length) return void(this._9l = mi(t));
                t.timeStamp - this._mxf[Vh] < 200 && yi.call(this, this._mxf);
            case "touchcancel":
                if (!this._mxf) return void(this._moving = null);
                this._moving && (this._moving = null, this._hi(t));
            case "mouseup":
                return void this._d8(t);
            case "click":
                return void yi.call(this, t);
            case "mousewheel":
            case "DOMMouseScroll":
                return t.delta = t[ta] || -t.detail,
                    this._hf(t, ia);
            case "touchmove":
                var e = t.touches.length;
                return this._moving || (this._moving = !0, 1 == e && this._df()),
                    void this._j7(t)
        }
        return this._hf(t, ea + i)
    }

    function Ei(t, i) {
        var e = uh + i;
        bM(t, i, this[e]),
            G(this, e)
    }

    function xi(i) {
        l(gM,
                function(t) {
                    bi.call(this, i, t)
                },
                this),
            OD || mM._9z || (mM._9z = !0, vM(t, na,
                function(t) {
                    if (mM._n0urrentInteractionSupport) {
                        R(t);
                        var i = mM._n0urrentInteractionSupport;
                        if (!mM._dragging) {
                            if (i._mxf) {
                                var e = i._mxf[sa] - t[sa],
                                    n = i._mxf[ra] - t.screenY;
                                if (4 > e * e + n * n) return
                            }
                            mM._dragging = !0,
                                i._df()
                        }
                        i._j7(t)
                    }
                }, !0), vM(t, ha,
                function(t) {
                    var i = mM._n0urrentInteractionSupport;
                    delete mM._n0urrentInteractionSupport,
                        mM._dragging && (delete mM._dragging, k(t), t = gi.call(i, t), i._hi(t), i._d8(t))
                }, !0))
    }

    function Ti(t) {
        l(gM,
                function(i) {
                    Ei.call(this, t, i)
                },
                this),
            OD || (mM._n0urrentInteractionSupport == this && (delete mM._dragging, delete mM._n0urrentInteractionSupport), this._22(), delete this._mxf, delete this._9l)
    }

    function wi(t, i, e) {
        this._lx = t,
            this._mxt = new Ii,
            xi.call(this, t),
            i && (this._listener = i),
            this._kd = e
    }

    function Oi(t) {
        return ED && t.metaKey || !ED && t.ctrlKey
    }

    function Ii() {
        this[aa] = []
    }

    function Ai(t, i, e, n, s) {
        Ci(t,
            function(n) {
                if (i) {
                    var s = n[oa];
                    if (!s) return void(e || RM)(_a + t + fa);
                    i(s)
                }
            },
            e, n, s)
    }

    function Si(t, i, e, n, s) {
        Ci(t,
            function(n) {
                if (i) {
                    var s = n[ua];
                    if (!s) return void(e || RM)(_a + t + ca);
                    try {
                        s = JSON.parse(s)
                    } catch (r) {
                        return void(e || RM)(r)
                    }
                    i(s)
                }
            },
            e, n, s)
    }

    function Ci(t, i, e, n, s) {
        (e === !1 || n === !1) && (s = !1);
        try {
            var r = new XMLHttpRequest,
                h = encodeURI(t);
            if (s !== !1) {
                var a;
                a = h.indexOf(da) > 0 ? "&" : da,
                    h += a + la + Date[va]()
            }
            r.open(ba, h),
                r[ga] = function() {
                    return 4 == r.readyState ? r[ya] && 200 != r[ya] ? void(e || RM)(_a + t + ma) : void(i && i(r)) : void 0
                },
                r.send(n)
        } catch (o) {
            (e || RM)(_a + t + ma, o)
        }
    }

    function ri(t, i, e, n, s, r, h, a) {
        return 0 >= h || 0 >= a || 0 >= e || 0 >= n ? !1 : (h += s, a += r, e += t, n += i, (s > h || h > t) && (r > a || a > i) && (t > e || e > s) && (i > n || n > r))
    }

    function ai(t, i, e, n, s, r, h, a) {
        return s >= t && r >= i && t + e >= s + h && i + n >= r + a
    }

    function ki(t, i, e) {
        return t instanceof Object && t.x ? Ri(t, i, 0, 0) : Li(t, i, e, 0, 0)
    }

    function Li(t, i, e, n, s) {
        var r = Math.sin(e),
            h = Math.cos(e),
            a = t - n,
            o = i - s;
        return t = a * h - o * r + n,
            i = a * r + o * h + s,
            new MD(t, i, e)
    }

    function Ri(t, i, e, n) {
        e = e || 0,
            n = n || 0;
        var s = Math[pa](i),
            r = Math[Ea](i),
            h = t.x - e,
            a = t.y - n;
        return t.x = h * r - a * s + e,
            t.y = h * s + a * r + n,
            t
    }

    function Di(t, i, e) {
        return Mi(t, i, e, 0, 0)
    }

    function Mi(t, i, e, n, s) {
        var r = Li(t.x, t.y, i, n, s),
            h = ki(t.x + t[xa], t.y, i, n, s),
            a = ki(t.x + t[xa], t.y + t.height, i, n, s),
            o = ki(t.x, t.y + t.height, i, n, s);
        return e ? e.clear() : e = new BD,
            e.addPoint(r),
            e[Ta](h),
            e[Ta](a),
            e.addPoint(o),
            e
    }

    function Pi(t, i) {
        var e = this[wa] || 1;
        this[Oa][xa] = t + Ia,
            this[Oa].height = i + Ia,
            this.width = t * e,
            this.height = i * e
    }

    function Ni() {
        this[Aa][xa] = this.canvas[xa]
    }

    function ji(t) {
        var i = t[Sa] || t[Ca] || t.msBackingStorePixelRatio || t[ka] || t[La] || 1;
        return PM / i
    }

    function Bi(t, e, n) {
        var s = i[Ra](Aa);
        if (s.g = s.getContext(Da), t !== !0 && !n) return t && e && (s[xa] = t, s.height = e),
            s;
        var r = s.g;
        return r[wa] = s[wa] = ji(r),
            s[Ma] = Pi,
            r._kn = Ni,
            t && e && s.setSize(t, e),
            s
    }

    function zi(t, i, n) {
        if (t === e || null === t) return {
            width: 0,
            height: 0
        };
        var s = $i();
        n = n || CD.FONT,
            s.font != n && (s[Pa] = n);
        for (var r = i * CD.LINE_HEIGHT, h = 0, a = 0, o = t.split(Na), _ = 0, f = o[jr]; f > _; _++) {
            var u = o[_];
            h = Math[ja](s[Ba](u).width, h),
                a += r
        }
        return {
            width: h,
            height: a
        }
    }

    function $i(t, i) {
        return NM || (NM = Bi()),
            t && i && (NM[xa] = t, NM.height = i),
            NM.g
    }

    function Gi(t) {
        return Math[za](t + Math[$a](t * t + 1))
    }

    function Fi(t, i) {
        i = i || t(1);
        var e = 1 / i,
            n = .5 * e,
            s = Math[Ga](1, i / 100);
        return function(r) {
            if (0 >= r) return 0;
            if (r >= i) return 1;
            for (var h = r * e, a = 0; a++ < 10;) {
                var o = t(h),
                    _ = r - o;
                if (Math.abs(_) <= s) return h;
                h += _ * n
            }
            return h
        }
    }

    function Yi(t, i, e) {
        var n = 1 - t,
            s = n * n * i[0] + 2 * n * t * i[2] + t * t * i[4],
            r = n * n * i[1] + 2 * n * t * i[3] + t * t * i[5];
        if (e) {
            var h = (i[0] + i[4] - 2 * i[2]) * t + i[2] - i[0],
                a = (i[1] + i[5] - 2 * i[3]) * t + i[3] - i[1];
            return {
                x: s,
                y: r,
                rotate: Math.atan2(a, h)
            }
        }
        return {
            t: t,
            x: s,
            y: r
        }
    }

    function qi(t, i, e) {
        var n = t - 2 * i + e;
        return 0 != n ? (t - i) / n : -1
    }

    function Hi(t, i) {
        i[Lh](t[4], t[5]);
        var e = qi(t[0], t[2], t[4]);
        if (e > 0 && 1 > e) {
            var n = Yi(e, t);
            i.add(n.x, n.y)
        }
        var s = qi(t[1], t[3], t[5]);
        if (s > 0 && 1 > s) {
            var n = Yi(s, t);
            i.add(n.x, n.y)
        }
        return i
    }

    function Ui(t) {
        if (t[0] == t[2] && t[1] == t[3] || t[1] == t[3] && t[1] == t[5]) {
            var i = t[0],
                e = t[1],
                n = t[4],
                s = t[5],
                r = Math[$a](jM(i, e, n, s));
            return function(t) {
                return r * t
            }
        }
        var h = t[0],
            a = t[2],
            o = t[4],
            _ = h - 2 * a + o,
            f = 2 * a - 2 * h;
        h = t[1],
            a = t[3],
            o = t[5];
        var u = h - 2 * a + o,
            c = 2 * a - 2 * h,
            d = 4 * (_ * _ + u * u),
            l = 4 * (_ * f + u * c),
            v = f * f + c * c,
            r = 4 * d * v - l * l,
            b = 1 / r,
            g = .125 * Math.pow(d, -1.5),
            y = 2 * Math[$a](d),
            m = (r * Gi(l / Math[$a](r)) + 2 * Math[$a](d) * l * Math[$a](v)) * g;
        return function(t) {
            var i = l + 2 * t * d,
                e = i / Math[$a](r),
                n = i * i * b;
            return (r * Math[za](e + Math[$a](n + 1)) + y * i * Math.sqrt(v + t * l + t * t * d)) * g - m
        }
    }

    function Wi(t, i, e) {
        var n = 1 - t,
            s = i[0],
            r = i[2],
            h = i[4],
            a = i[6],
            o = s * n * n * n + 3 * r * t * n * n + 3 * h * t * t * n + a * t * t * t;
        if (e) var _ = 3 * t * t * a + (6 * t - 9 * t * t) * h + (9 * t * t - 12 * t + 3) * r + (-3 * t * t + 6 * t - 3) * s;
        s = i[1],
            r = i[3],
            h = i[5],
            a = i[7];
        var f = s * n * n * n + 3 * r * t * n * n + 3 * h * t * t * n + a * t * t * t;
        if (e) {
            var u = 3 * t * t * a + (6 * t - 9 * t * t) * h + (9 * t * t - 12 * t + 3) * r + (-3 * t * t + 6 * t - 3) * s;
            return {
                x: o,
                y: f,
                rotate: Math.atan2(u, _)
            }
        }
        return {
            x: o,
            y: f
        }
    }

    function Xi(t, i, e, n) {
        var s = -t + 3 * i - 3 * e + n;
        if (0 == s) return [(t - i) / (2 * e - 4 * i + 2 * t)];
        var r = 2 * t - 4 * i + 2 * e,
            h = i - t,
            a = r * r - 4 * s * h;
        return 0 > a ? void 0 : 0 == a ? [-r / (2 * s)] : (a = Math[$a](a), [(a - r) / (2 * s), (-a - r) / (2 * s)])
    }

    function Vi(t, i) {
        i[Lh](t[6], t[7]);
        var e = Xi(t[0], t[2], t[4], t[6]);
        if (e)
            for (var n = 0; n < e.length; n++) {
                var s = e[n];
                if (!(0 >= s || s >= 1)) {
                    var r = Wi(s, t);
                    i.add(r.x, r.y)
                }
            }
        if (e = Xi(t[1], t[3], t[5], t[7]))
            for (var n = 0; n < e.length; n++) {
                var s = e[n];
                if (!(0 >= s || s >= 1)) {
                    var r = Wi(s, t);
                    i[Lh](r.x, r.y)
                }
            }
    }

    function Ki(t) {
        var i = {
                x: t[0],
                y: t[1]
            },
            e = {
                x: t[2],
                y: t[3]
            },
            n = {
                x: t[4],
                y: t[5]
            },
            s = {
                x: t[6],
                y: t[7]
            },
            r = i.x - 0,
            h = i.y - 0,
            a = e.x - 0,
            o = e.y - 0,
            _ = n.x - 0,
            f = n.y - 0,
            u = s.x - 0,
            c = s.y - 0,
            d = 3 * (-r + 3 * a - 3 * _ + u),
            l = 6 * (r - 2 * a + _),
            v = 3 * (-r + a),
            b = 3 * (-h + 3 * o - 3 * f + c),
            g = 6 * (h - 2 * o + f),
            y = 3 * (-h + o),
            m = function(t) {
                var i = d * t * t + l * t + v,
                    e = b * t * t + g * t + y;
                return Math[$a](i * i + e * e)
            },
            p = (m(0) + 4 * m(.5) + m(1)) / 6;
        return p
    }

    function Zi(t, i) {
        function e(t, i, e, n) {
            var s = -t + 3 * i - 3 * e + n,
                r = 2 * t - 4 * i + 2 * e,
                h = i - t;
            return function(t) {
                return 3 * (s * t * t + r * t + h)
            }
        }

        function n(t, i) {
            var e = s(t),
                n = r(t);
            return Math[$a](e * e + n * n) * i
        }
        var s = e(t[0], t[2], t[4], t[6]),
            r = e(t[1], t[3], t[5], t[7]);
        i = i || 100;
        var h = 1 / i;
        return function(t) {
            if (!t) return 0;
            for (var i, e = 0, s = 0;;) {
                if (i = e + h, i >= t) return s += n(e, i - e);
                s += n(e, h),
                    e = i
            }
        }
    }

    function Ji(t, i, e) {
        return jM(i, e, t.cx, t.cy) <= t._squareR + BM
    }

    function Qi(t, i, e, n) {
        return e = e || te(t, i),
            new ie((t.x + i.x) / 2, (t.y + i.y) / 2, e / 2, t, i, null, n)
    }

    function te(t, i) {
        return PD(t.x, t.y, i.x, i.y)
    }

    function ie(t, i, e, n, s, r, h) {
        this.cx = t,
            this.cy = i,
            this.r = e,
            this._squareR = e * e,
            this.p1 = n,
            this.p2 = s,
            this.p3 = r,
            this._otherPoint = h
    }

    function ee(t, i, e, n) {
        this.cx = t,
            this.cy = i,
            this[xa] = e,
            this[Fa] = n
    }

    function ne(t) {
        var i = t[0],
            e = t[1],
            n = t[2],
            s = ie._irCircle(i, e, n);
        return re(t, i, e, n, s)
    }

    function se(t, i) {
        i = i || he(t);
        for (var e, n = i.width / i.height, s = [], r = t[jr], h = 0; r > h; h++) e = t[h],
            s.push({
                x: e.x,
                y: e.y * n
            });
        var a = ne(s);
        return a ? new ee(a.cx, a.cy / n, 2 * a.r, 2 * a.r / n) : void 0
    }

    function re(t, i, e, n, s) {
        for (var r, h, a = t.length, o = s._squareR, _ = 0; a > _; _++)
            if (r = t[_], r != i && r != e && r != n) {
                var f = jM(s.cx, s.cy, r.x, r.y);
                f - BM > o && (o = f, h = r)
            }
        if (!h) return s;
        var u,
            c = ie._irCircle(h, i, e),
            d = ie._irCircle(h, i, n),
            l = ie._irCircle(h, n, e);
        return Ji(c, n.x, n.y) && (u = c),
            Ji(d, e.x, e.y) && (!u || u.r > d.r) && (u = d),
            Ji(l, i.x, i.y) && (!u || u.r > l.r) && (u = l),
            i = u.p1,
            e = u.p2,
            n = u.p3 || u._otherPoint,
            re(t, i, e, n, u)
    }

    function he(t) {
        for (var i, e = t.length, n = new BD, s = 0; e > s; s++) i = t[s],
            n[Lh](i.x, i.y);
        return n
    }

    function ae(t, i, e, n, s) {
        this._6a && this.validate();
        var r = s ? this[Ya](s) : this[qa],
            h = e / r[xa],
            a = t - h * r.x,
            o = n / r[Fa],
            _ = i - o * r.y,
            f = this._eq,
            u = [];
        return l(f,
                function(t) {
                    var i = t.clone(),
                        e = i[aa];
                    if (e && e.length) {
                        for (var n = e[jr], s = [], r = 0; n > r; r++) {
                            var f = e[r];
                            r++;
                            var c = e[r];
                            f = h * f + a,
                                c = o * c + _,
                                s[Yr](f),
                                s.push(c)
                        }
                        i.points = s
                    }
                    u.push(i)
                },
                this),
            new vP(u)
    }

    function oe(t, i, e, n, s, r) {
        if (s = s || 0, e = e || 0, !s && !r) return !1;
        if (!n) {
            var h = this[Ya](s);
            if (!h.intersectsPoint(t, i, e)) return !1
        }
        var a = Math[Ha](2 * e) || 1,
            o = $i(a, a),
            _ = (o[Aa], -t + e),
            f = -i + e;
        if (o.setTransform(1, 0, 0, 1, _, f), !o.isPointInStroke) {
            this._l9(o),
                s && o[Ua](),
                r && o.fill();
            for (var u = o.getImageData(0, 0, a, a).data, c = u.length / 4; c > 0;) {
                if (u[4 * c - 1] > lP) return !0;
                --c
            }
            return !1
        }
        return o[Wa] = (s || 0) + 2 * e,
            this._l9(o),
            s && o[Xa](e, e) ? !0 : r ? o.isPointInPath(e, e) : !1
    }

    function _e(t, i, e) {
        if (!this._io) return null;
        var n = this._eq;
        if (n.length < 2) return null;
        e === !1 && (t += this._io);
        var s = n[0];
        if (0 >= t) return Ps(s.points[0], s[aa][1], n[1][aa][0], n[1][aa][1], t, i);
        if (t >= this._io) {
            s = n[n.length - 1];
            var r,
                h,
                a = s.points,
                o = a[jr],
                _ = a[o - 2],
                f = a[o - 1];
            if (o >= 4) r = a[o - 4],
                h = a[o - 3];
            else {
                s = n[n[jr] - 2];
                var u = s[Va];
                r = u.x,
                    h = u.y
            }
            return Ps(_, f, _ + _ - r, f + f - h, t - this._io, i)
        }
        for (var c, d = 0, l = 1, o = n[jr]; o > l; l++)
            if (c = n[l], c._io) {
                if (!(d + c._io < t)) {
                    var v,
                        u = s[Va];
                    if (c.type == uP) {
                        var b = c.points;
                        v = fe(t - d, c, u.x, u.y, b[0], b[1], b[2], b[3], c._r)
                    } else {
                        if (!c._lf) return Ps(u.x, u.y, c[aa][0], c[aa][1], t - d, i);
                        var g = Fi(c._lf, c._io)(t - d),
                            b = c[aa];
                        v = c.type == fP && 6 == b[jr] ? Wi(g, [u.x, u.y].concat(b), !0) : Yi(g, [u.x, u.y][Ka](b), !0)
                    }
                    return i && (v.x -= i * Math.sin(v.rotate || 0), v.y += i * Math.cos(v[Za] || 0)),
                        v
                }
                d += c._io,
                    s = c
            } else s = c
    }

    function fe(t, i, e, n, s, r, h, a) {
        if (t <= i._l1) return Ps(e, n, s, r, t);
        if (t >= i._io) return t -= i._io,
            Ps(i._p2x, i._p2y, h, a, t);
        if (t -= i._l1, i._o) {
            var o = t / i._r;
            i._CCW && (o = -o);
            var _ = Li(i._p1x, i._p1y, o, i._o.x, i._o.y);
            return _[Za] += i._my1 || 0,
                _[Za] += Math.PI,
                _
        }
        return Ps(i._p1x, i._p1y, i._p2x, i._p2y, t)
    }

    function ni(t, i, e) {
        var n,
            s,
            r;
        0 == t._my ? (n = -1, r = 0, s = i) : 0 == t._n0 ? (n = 0, r = 1, s = e) : (n = -1 / t._my, s = (t._my - n) * i + t._mx, r = 1);
        var h = new ND;
        return h._my = n,
            h._mx = s,
            h._n0 = r,
            h._mv = i,
            h._mr = e,
            h
    }

    function ue(t) {
        return t %= 2 * Math.PI,
            0 > t && (t += 2 * Math.PI),
            t
    }

    function ce(t, i, e, n, s, r, h, a) {
        var o = PD(i, e, n, s),
            _ = PD(n, s, r, h);
        if (!o || !_) return t._d = 0,
            t._r = 0,
            t._l1 = o,
            t._l2 = _,
            t._io = 0;
        var f = le(n, s, i, e),
            u = le(n, s, r, h);
        t._my1 = f,
            t._my2 = u;
        var c = f - u;
        c = ue(c),
            c > Math.PI && (c = 2 * Math.PI - c, t._CCW = !0);
        var d = Math.PI - c,
            l = Math[Ja](c / 2),
            v = a / l,
            b = Math[Ga](o, _);
        v > b && (v = b, a = l * v);
        var g,
            y = n + Math.cos(f) * v,
            m = s + Math[pa](f) * v,
            p = n + Math[Ea](u) * v,
            E = s + Math.sin(u) * v,
            x = new ND(i, e, n, s),
            T = new ND(n, s, r, h),
            w = ni(x, y, m),
            O = ni(T, p, E),
            I = w._3y(O),
            A = Math[Ih](m - I.y, y - I.x),
            S = Math[Ih](E - I.y, p - I.x);
        g = t._CCW ? S : A;
        for (var C, k = 0; 4 > k;) {
            var L = k * RD;
            if (ue(L - g) <= d) {
                var R,
                    D;
                if (C ? C++ : C = 1, 0 == k ? (R = I.x + a, D = I.y) : 1 == k ? (R = I.x, D = I.y + a) : 2 == k ? (R = I.x - a, D = I.y) : (R = I.x, D = I.y - a), t[Qa + C] = {
                        x: R,
                        y: D
                    },
                    2 == C) break
            }
            k++
        }
        return t._p1x = y,
            t._p1y = m,
            t._p2x = p,
            t._p2y = E,
            t._o = I,
            t._d = v,
            t._r = a,
            t._l1 = o - v,
            t._l2 = _ - v,
            t._io = t._l1 + d * a
    }

    function de(t, i, e, n, s, r, h) {
        var a = le(e, n, t, i),
            o = le(e, n, s, r),
            _ = a - o;
        return h ? _ : (0 > _ && (_ = -_), _ > Math.PI && (_ -= Math.PI), _)
    }

    function le(t, i, e, n) {
        return Math[Ih](n - i, e - t)
    }

    function ve(t) {
        var i = $M.exec(t);
        if (i) return i[1];
        var e = t.lastIndexOf(Th);
        return e >= 0 && e < t.length - 1 ? t[to](e + 1) : void 0
    }

    function be(t) {
        if (!t) return null;
        if (t instanceof vP) return WM;
        if (t[io] instanceof Function) return UM;
        if (A(t)) {
            var i = ve(t);
            if (i) {
                if (!lD && GM.test(i)) return HM;
                if (FM[eo](i)) return qM
            }
            return YM
        }
    }

    function ge(t, i, e) {
        if (this._lf = be(t), !this._lf) throw new Error("the image format is not supported", t);
        this._lp = t,
            this._myx = i,
            this._8r = e,
            this.width = i || CD.IMAGE_WIDTH,
            this.height = e || CD[no],
            this._jn = {}
    }

    function ye(t, i, e, n) {
        return i ? (ZM[t] = new ge(i, e, n), t) : void delete ZM[t]
    }

    function me(t) {
        if (t._jx) return t._jx;
        var i = A(t);
        if (!i && !t.name) return t._jx = new ge(t);
        var e = t[so] || t;
        return e in ZM ? ZM[e] : ZM[e] = new ge(t)
    }

    function pe(t) {
        return t in ZM
    }

    function Ee(t, i, e) {
        e = e || {};
        var n = t[Ya](e.lineWidth);
        if (!n[xa] || !n.height) return !1;
        var s = i.getContext(Da),
            r = i[wa] || 1,
            h = e[ro] || ho,
            a = /full/i.test(h),
            o = /uniform/i [eo](h),
            _ = 1,
            f = 1;
        if (a) {
            var u = i.width,
                c = i[Fa],
                d = e[ao],
                l = 0,
                v = 0;
            if (d) {
                var b,
                    g,
                    y,
                    m;
                I(d) ? b = g = y = m = d : (b = d[oo] || 0, g = d.bottom || 0, y = d[_o] || 0, m = d.right || 0),
                    u -= y + m,
                    c -= b + g,
                    l += y,
                    v += b
            }
            _ = u / n[xa],
                f = c / n[Fa],
                o && (_ > f ? (l += (u - f * n.width) / 2, _ = f) : f > _ && (v += (c - _ * n[Fa]) / 2, f = _)), (l || v) && s.translate(l, v)
        }
        s[fo](-n.x * _, -n.y * f),
            t.draw(s, r, e, _, f, !0)
    }

    function xe(t, i, e) {
        var n = me(t);
        return n ? (n[uo](), (n._lf == HM || n._6l()) && n._9w(function(t) {
                t.source && (this.width = this.width, Ee(t[co], this, e))
            },
            i), void Ee(n, i, e)) : (DM[lo](vo + t), !1)
    }

    function Te(t) {
        var i = t.width,
            e = t[Fa];
        try {
            var n = t.g[bo](0, 0, i, e),
                s = n[go];
            return we(s, i, e)
        } catch (r) {
            DM[lo](r)
        }
    }

    function we(t, i) {
        var e,
            n,
            s,
            r,
            h,
            a = t.length,
            o = 0,
            _ = 0;
        for (h = 0; a > h; h += 4)
            if (t[h + 3] > 0) {
                e = (h + 4) / i / 4 | 0;
                break
            }
        for (h = a - 4; h >= 0; h -= 4)
            if (t[h + 3] > 0) {
                n = (h + 4) / i / 4 | 0;
                break
            }
        for (o = 0; i > o; o++) {
            for (_ = e; n > _; _++)
                if (t[_ * i * 4 + 4 * o + 3] > 0) {
                    s = o;
                    break
                }
            if (s >= 0) break
        }
        for (o = i - 1; o >= 0; o--) {
            for (_ = e; n > _; _++)
                if (t[_ * i * 4 + 4 * o + 3] > 0) {
                    r = o;
                    break
                }
            if (r >= 0) break
        }
        var f,
            u,
            c,
            d = [],
            l = [];
        for (o = s; r >= o; o++)
            for (c = [], d.push(c), _ = e; n >= _; _++) h = 4 * (_ * i + o),
                f = t[h + 3],
                f ? (u = {
                        a: f,
                        r: t[h],
                        g: t[h + 1],
                        b: t[h + 2]
                    },
                    c[Yr](u), l[Yr](u.r), l[Yr](u.g), l.push(u.b), l.push(u.a)) : (c[Yr](null), l.push(0), l[Yr](0), l[Yr](0), l[Yr](0));
        return d._x = s,
            d._y = e,
            d._width = r - s + 1,
            d._height = n - e + 1,
            d._iq = new BD(s, e, d._width, d._height),
            d._pixelSize = d._width * d._height,
            d
    }

    function Oe(t, i, e, n, s) {
        if (s = 1 | s, !s || 1 > s) {
            var r = t[e];
            return r ? r[n] : !1
        }
        var h = n - s,
            a = e - s;
        0 > h && (h = 0),
            0 > a && (a = 0);
        var o = e + s,
            _ = n + s;
        for (o > i[xa] && (o = i[xa]), _ > i.height && (_ = i.height); o > a;) {
            for (; _ > h;) {
                if (t[a][h]) return !0;
                h++
            }
            a++
        }
        return !1
    }

    function Ie(t) {
        if (mh == t[0]) {
            if (t = t.substring(1), 3 == t.length) t = t[0] + t[0] + t[1] + t[1] + t[2] + t[2];
            else if (6 != t.length) return;
            return t = parseInt(t, 16), [t >> 16 & 255, t >> 8 & 255, 255 & t]
        }
        if (/^rgb/i [eo](t)) {
            var i = t.indexOf(yo),
                e = t.indexOf(gh);
            if (0 > i || i > e) return;
            if (t = t.substring(i + 1, e), t = t[Xr](bh), t[jr] < 3) return;
            var n = parseInt(t[0]),
                s = parseInt(t[1]),
                r = parseInt(t[2]),
                h = 3 == t.length ? 255 : parseInt(t[3]);
            return [n, s, r, h]
        }
    }

    function Ae(t, i, e) {
        return e || (e = CD[mo]),
            e == MM[po] ? t * i : e == MM[Eo] ? Math[Ga](t, i) : e == MM[xo] ? 1 - (1 - i) / t : e == MM.BLEND_MODE_LINEAR_BURN ? t + i - 1 : e == MM[To] ? Math.max(t, i) : e == MM.BLEND_MODE_SCREEN ? t + i - t * i : i
    }

    function Se(t, i, e) {
        var n = Ie(i),
            s = t.g.getImageData(0, 0, t[xa], t[Fa]),
            r = s.data;
        if (e instanceof Function) r = e(t, r, n) || r;
        else {
            var h = n[0] / 255,
                a = n[1] / 255,
                o = n[2] / 255;
            if (e == MM[wo])
                for (var _ = 0, f = r.length; f > _; _ += 4) {
                    var u = 77 * r[_] + 151 * r[_ + 1] + 28 * r[_ + 2] >> 8;
                    r[_] = u * h | 0,
                        r[_ + 1] = u * a | 0,
                        r[_ + 2] = u * o | 0
                } else
                    for (var _ = 0, f = r.length; f > _; _ += 4) r[_] = 255 * Ae(h, r[_] / 255, e) | 0,
                        r[_ + 1] = 255 * Ae(a, r[_ + 1] / 255, e) | 0,
                        r[_ + 2] = 255 * Ae(o, r[_ + 2] / 255, e) | 0
        }
        var t = Bi(t.width, t.height);
        return t.g[Oo](s, 0, 0),
            t
    }

    function Ce(t, i, e, n) {
        return 1 > e && (e = 1),
            ke(t - e, i - e, 2 * e, 2 * e, n)
    }

    function ke(t, i, e, n, s) {
        e = Math.round(e) || 1,
            n = Math.round(n) || 1;
        var r = $i(e, n);
        r[Io](1, 0, 0, 1, -t, -i),
            s.draw(r);
        for (var h = r[bo](0, 0, e, n).data, a = h[jr] / 4; a-- > 0;)
            if (h[4 * a - 1] > lP) return !0;
        return !1
    }

    function Le(t, i, e, n, s, r) {
        t -= s.$x,
            i -= s.$y;
        var h = s._f9.intersection(t, i, e, n);
        if (!h) return !1;
        t = h.x * r,
            i = h.y * r,
            e = h[xa] * r,
            n = h[Fa] * r,
            e = Math.round(e) || 1,
            n = Math.round(n) || 1;
        var a = $i(),
            o = a.canvas;
        o.width < e || o.height < n ? (o[xa] = e, o.height = n) : (a[Io](1, 0, 0, 1, 0, 0), a.clearRect(0, 0, e, n)),
            a.setTransform(1, 0, 0, 1, -t - s.$x * r, -i - s.$y * r),
            a[Ao](r, r),
            s._j0(a, 1);
        for (var _ = a.getImageData(0, 0, e, n)[go], f = _[jr] / 4; f-- > 0;)
            if (_[4 * f - 1] > lP) return !0;
        return !1
    }

    function Re(t, i, e, n, s, r, h, a, o) {
        if (hi(t, i, e, n, a, o)) return null;
        var _,
            f,
            u,
            c = new dP(oP, [t + e - s, i]),
            d = new dP(_P, [t + e, i, t + e, i + r]),
            l = new dP(oP, [t + e, i + n - r]),
            v = new dP(_P, [t + e, i + n, t + e - s, i + n]),
            b = new dP(oP, [t + s, i + n]),
            g = new dP(_P, [t, i + n, t, i + n - r]),
            y = new dP(oP, [t, i + r]),
            m = new dP(_P, [t, i, t + s, i]),
            p = (new dP(cP), [c, d, l, v, b, g, y, m]),
            E = new BD(t + s, i + r, e - s - s, n - r - r);
        t > a ? (_ = GD, u = 5) : a > t + e ? (_ = YD, u = 1) : (_ = FD, u = 0),
            i > o ? (f = qD, _ == GD && (u = 7)) : o > i + n ? (f = UD, _ == YD ? u = 3 : _ == FD && (u = 4)) : (f = HD, _ == GD ? u = 6 : _ == YD && (u = 2));
        var x = Be(u, t, i, e, n, s, r, h, a, o, E),
            T = x[0],
            w = x[1],
            O = new vP,
            I = O._eq;
        I[Yr](new dP(aP, [T.x, T.y])),
            I[Yr](new dP(oP, [a, o])),
            I.push(new dP(oP, [w.x, w.y])),
            w._m1 && (I.push(w._m1), w._m1NO++);
        for (var A = w._m1NO % 8, S = T._m1NO; I[Yr](p[A]), ++A, A %= 8, A != S;);
        return T._m1 && I.push(T._m1),
            O[So](),
            O
    }

    function De(t, i, n, s, r, h, a, o, _, f, u, c, d, l) {
        var v = new ND(c, d, n, s),
            b = new ND(i[0], i[1], i[4], i[5]),
            g = b._3y(v, u),
            y = g[0],
            m = g[1];
        if (y._rest !== e) {
            y._m1NO = (t - 1) % 8,
                m._m1NO = (t + 1) % 8;
            var p = y._rest;
            7 == t ? (y.y = h + f + Math[Ga](l[Fa], p), m.x = r + _ + Math[Ga](l[xa], p)) : 5 == t ? (y.x = r + _ + Math.min(l[xa], p), m.y = h + o - f - Math.min(l.height, p)) : 3 == t ? (y.y = h + o - f - Math.min(l.height, p), m.x = r + a - _ - Math[Ga](l[xa], p)) : 1 == t && (y.x = r + a - _ - Math.min(l.width, p), m.y = h + f + Math.min(l.height, p))
        } else {
            v._md(v._mv, v._mr, y.x, y.y),
                y = v._$j(i),
                v._md(v._mv, v._mr, m.x, m.y),
                m = v._$j(i);
            var E = ze(i, [y, m]),
                x = E[0],
                T = E[2];
            y._m1NO = t,
                m._m1NO = t,
                y._m1 = new dP(_P, x.slice(2)),
                m._m1 = new dP(_P, T.slice(2))
        }
        return [y, m]
    }

    function Me(t, i, e, n, s, r, h, a, o, _) {
        var f,
            u;
        if (o - a >= i + r) f = {
                y: e,
                x: o - a
            },
            f._m1NO = 0;
        else {
            f = {
                y: e + h,
                x: Math.max(i, o - a)
            };
            var c = [i, e + h, i, e, i + r, e],
                d = new ND(o, _, f.x, f.y);
            if (f = d._$j(c)) {
                C(f) && (f = f[0].t > f[1].t ? f[0] : f[1]);
                var l = ze(c, [f]);
                l = l[0],
                    l && (f._m1 = new dP(_P, l[$r](2))),
                    f._m1NO = 7
            } else f = {
                    y: e,
                    x: i + r
                },
                f._m1NO = 0
        }
        if (i + n - r >= o + a) u = {
                y: e,
                x: o + a
            },
            u._m1NO = 0;
        else {
            u = {
                y: e + h,
                x: Math.min(i + n, o + a)
            };
            var v = [i + n - r, e, i + n, e, i + n, e + h],
                d = new ND(o, _, u.x, u.y);
            if (u = d._$j(v)) {
                C(u) && (u = u[0].t < u[1].t ? u[0] : u[1]);
                var l = ze(v, [u]);
                l && l[l[jr] - 1] && (u._m1 = new dP(_P, l[l[jr] - 1].slice(2))),
                    u._m1NO = 1
            } else u = {
                    y: e,
                    x: i + n - r
                },
                u._m1NO = 0
        }
        return [f, u]
    }

    function Pe(t, i, e, n, s, r, h, a, o, _) {
        var f,
            u;
        if (_ - a >= e + h) f = {
                x: i + n,
                y: _ - a
            },
            f._m1NO = 2;
        else {
            f = {
                x: i + n - r,
                y: Math.max(e, _ - a)
            };
            var c = [i + n - r, e, i + n, e, i + n, e + h],
                d = new ND(o, _, f.x, f.y);
            if (f = d._$j(c)) {
                C(f) && (f = f[0].t > f[1].t ? f[0] : f[1]);
                var l = ze(c, [f]);
                l = l[0],
                    l && (f._m1 = new dP(_P, l.slice(2))),
                    f._m1NO = 1
            } else f = {
                    x: i + n,
                    y: e + h
                },
                f._m1NO = 2
        }
        if (e + s - h >= _ + a) u = {
                x: i + n,
                y: _ + a
            },
            u._m1NO = 2;
        else {
            u = {
                x: i + n - r,
                y: Math[Ga](e + s, _ + a)
            };
            var v = [i + n, e + s - h, i + n, e + s, i + n - r, e + s],
                d = new ND(o, _, u.x, u.y);
            if (u = d._$j(v)) {
                C(u) && (u = u[0].t < u[1].t ? u[0] : u[1]);
                var l = ze(v, [u]);
                l[1] && (u._m1 = new dP(_P, l[1][$r](2))),
                    u._m1NO = 3
            } else u = {
                    x: i + n,
                    y: e + s - h
                },
                u._m1NO = 2
        }
        return [f, u]
    }

    function Ne(t, i, e, n, s, r, h, a, o, _) {
        var f,
            u;
        if (o - a >= i + r) u = {
                y: e + s,
                x: o - a
            },
            u._m1NO = 4;
        else {
            u = {
                y: e + s - h,
                x: Math[ja](i, o - a)
            };
            var c = [i + r, e + s, i, e + s, i, e + s - h],
                d = new ND(o, _, u.x, u.y);
            if (u = d._$j(c)) {
                C(u) && (u = u[0].t < u[1].t ? u[0] : u[1]);
                var l = ze(c, [u]);
                l = l[l.length - 1],
                    l && (u._m1 = new dP(_P, l.slice(2))),
                    u._m1NO = 5
            } else u = {
                    y: e + s,
                    x: i + r
                },
                u._m1NO = 4
        }
        if (i + n - r >= o + a) f = {
                y: e + s,
                x: o + a
            },
            f._m1NO = 4;
        else {
            f = {
                y: e + s - h,
                x: Math[Ga](i + n, o + a)
            };
            var v = [i + n, e + s - h, i + n, e + s, i + n - r, e + s],
                d = new ND(o, _, f.x, f.y);
            if (f = d._$j(v)) {
                C(f) && (f = f[0].t > f[1].t ? f[0] : f[1]);
                var l = ze(v, [f]);
                l[0] && (f._m1 = new dP(_P, l[0][$r](2))),
                    f._m1NO = 3
            } else f = {
                    y: e + s,
                    x: i + n - r
                },
                f._m1NO = 4
        }
        return [f, u]
    }

    function je(t, i, e, n, s, r, h, a, o, _) {
        var f,
            u;
        if (_ - a >= e + h) u = {
                x: i,
                y: _ - a
            },
            u._m1NO = 6;
        else {
            u = {
                x: i + r,
                y: Math.max(e, _ - a)
            };
            var c = [i, e + h, i, e, i + r, e],
                d = new ND(o, _, u.x, u.y);
            if (u = d._$j(c)) {
                C(u) && (u = u[0].t < u[1].t ? u[0] : u[1]);
                var l = ze(c, [u]);
                l = l[l.length - 1],
                    l && (u._m1 = new dP(_P, l[$r](2)))
            } else u = {
                x: i,
                y: e + h
            };
            u._m1NO = 7
        }
        if (e + s - h >= _ + a) f = {
                x: i,
                y: _ + a
            },
            f._m1NO = 6;
        else {
            f = {
                x: i + r,
                y: Math.min(e + s, _ + a)
            };
            var v = [i + r, e + s, i, e + s, i, e + s - h],
                d = new ND(o, _, f.x, f.y);
            if (f = d._$j(v)) {
                C(f) && (f = f[0].t > f[1].t ? f[0] : f[1]);
                var l = ze(v, [f]);
                l[0] && (f._m1 = new dP(_P, l[0].slice(2))),
                    f._m1NO = 5
            } else f = {
                    x: i,
                    y: e + s - h
                },
                f._m1NO = 6
        }
        return [f, u]
    }

    function Be(t, i, e, n, s, r, h, a, o, _, f) {
        var u = a / 2;
        switch (t) {
            case 7:
                var c = [i, e + h, i, e, i + r, e],
                    d = i + r,
                    l = e + h;
                return De(t, c, d, l, i, e, n, s, r, h, a, o, _, f);
            case 5:
                return c = [i + r, e + s, i, e + s, i, e + s - h],
                    d = i + r,
                    l = e + s - h,
                    De(t, c, d, l, i, e, n, s, r, h, a, o, _, f);
            case 3:
                return c = [i + n, e + s - h, i + n, e + s, i + n - r, e + s],
                    d = i + n - r,
                    l = e + s - h,
                    De(t, c, d, l, i, e, n, s, r, h, a, o, _, f);
            case 1:
                return c = [i + n - r, e, i + n, e, i + n, e + h],
                    d = i + n - r,
                    l = e + h,
                    De(t, c, d, l, i, e, n, s, r, h, a, o, _, f);
            case 0:
                return Me(t, i, e, n, s, r, h, u, o, _, f);
            case 2:
                return Pe(t, i, e, n, s, r, h, u, o, _, f);
            case 4:
                return Ne(t, i, e, n, s, r, h, u, o, _, f);
            case 6:
                return je(t, i, e, n, s, r, h, u, o, _, f)
        }
    }

    function ze(t, i) {
        for (var n, s, r, h, a, o, _ = t[0], f = t[1], u = t[2], c = t[3], d = t[4], l = t[5], v = [], b = 0; b < i.length; b++) a = i[b],
            o = a.t,
            0 != o && 1 != o ? (n = _ + (u - _) * o, s = f + (c - f) * o, r = u + (d - u) * o, h = c + (l - c) * o, v.push([_, f, n, s, a.x, a.y]), _ = a.x, f = a.y, u = r, c = h) : v.push(null);
        return r !== e && v.push([a.x, a.y, r, h, d, l]),
            v
    }

    function $e(t) {
        return this.$layoutByAnchorPoint && this._9x && (t.x -= this._9x.x, t.y -= this._9x.y),
            this[Co] && Ri(t, this[Co]),
            t.x += this[ko] || 0,
            t.y += this[Lo] || 0,
            this.$rotatable && this.$_hostRotate ? Ri(t, this.$_hostRotate) : t
    }

    function Ge(t) {
        return this[Ro] && this[Do] && Ri(t, -this[Do]),
            t.x -= this[ko] || 0,
            t.y -= this.$offsetY || 0,
            this.$rotate && Ri(t, -this.$rotate),
            this[Mo] && this._9x && (t.x += this._9x.x, t.y += this._9x.y),
            t
    }

    function Fe() {
        var t = this.$invalidateSize;
        this.$invalidateSize && (this[Po] = !1, this[No] = !0, this._7x[jo](this._iq), this.$padding && this._7x.grow(this[Bo]), this[zo] && this._7x[$o](this[zo]));
        var i = this._$v();
        if (i) var e = this[Go] && this.$pointerWidth;
        return this[No] && this[Mo] && (this[No] = !1, e && (t = !0), this._9x = oi(this[Fo], this._7x[xa], this._7x.height), this._9x.x += this._7x.x, this._9x.y += this._7x.y),
            i ? (t && (this._mxackgroundGradientInvalidateFlag = !0, Ye.call(this, e)), this._mxackgroundGradientInvalidateFlag && (this._mxackgroundGradientInvalidateFlag = !1, this._mxackgroundGradient = this[Yo] && this._ldShape && this._ldShape[qa] ? JM.prototype.generatorGradient[Br](this[Yo], this._ldShape[qa]) : null), t) : (this.__m8Pointer = !1, t)
    }

    function Ye(t) {
        var i = this._7x.x + this.$border / 2,
            e = this._7x.y + this.$border / 2,
            n = this._7x.width - this[zo],
            s = this._7x[Fa] - this.$border,
            r = 0,
            h = 0;
        if (this.$borderRadius && (I(this[qo]) ? r = h = this[qo] : (r = this[qo].x || 0, h = this[qo].y || 0), r = Math.min(r, n / 2), h = Math.min(h, s / 2)), t && (this._pointerX = this._9x.x - this[ko] + this[Ho], this._pointerY = this._9x.y - this[Lo] + this[Uo], !this._7x[Wo](this._pointerX, this._pointerY))) {
            var a = new gP(i, e, n, s, r, h, this[Xo], this._pointerX, this._pointerY);
            return this._ldShape = a._m1,
                this._ldShape[qa][Vo](i, e, n, s),
                void(this.__m8Pointer = !0)
        }
        this._ldShape && this._ldShape.clear(),
            this._ldShape = wN.getRect(i, e, n, s, r, h, this._ldShape),
            this._ldShape.bounds.set(i, e, n, s)
    }

    function qe(t, i, e, n) {
        return n && (t[xa] < 0 || t[Fa] < 0) ? (t.x = i, t.y = e, void(t.width = t.height = 0)) : (i < t.x ? (t[xa] += t.x - i, t.x = i) : i > t.x + t[xa] && (t[xa] = i - t.x), void(e < t.y ? (t[Fa] += t.y - e, t.y = e) : e > t.y + t[Fa] && (t[Fa] = e - t.y)))
    }

    function He(t, i, n) {
        var s,
            r = t[Ko],
            h = t.layoutByPath === e ? this.layoutByPath : t[Zo];
        return this.$data instanceof vP && h ? (s = zM._myo(r, this.$data, this.lineWidth, i, n), s.x *= this._iw, s.y *= this._ix) : (s = oi(r, this._7x[xa], this._7x.height), s.x += this._7x.x, s.y += this._7x.y),
            $e[Br](this, s)
    }

    function Ue(t, i) {
        if (i)
            if (i._7x.isEmpty()) t.$x = i.$x,
                t.$y = i.$y;
            else {
                var e = He[Br](i, t);
                t.$x = e.x,
                    t.$y = e.y,
                    t._hostRotate = e.rotate
            } else t.$x = 0,
            t.$y = 0;
        t[Jo] && pP.call(t)
    }

    function We(t) {
        if (t.lineDash === e) {
            var i,
                n;
            if (t[Qo]) i = t[t_],
                n = t[Qo];
            else {
                var s;
                if (t[i_] !== e) s = i_;
                else {
                    if (t[e_] === e) return !1;
                    s = e_
                }
                n = function(t) {
                        this[s] = t
                    },
                    i = function() {
                        return this[s]
                    }
            }
            K(t, n_, {
                get: function() {
                    return i[Br](this)
                },
                set: function(t) {
                    n[Br](this, t)
                }
            })
        }
        if (t[s_] === e) {
            var r;
            if (t[r_] !== e) r = r_;
            else {
                if (t.webkitLineDashOffset === e) return;
                r = h_
            }
            K(t, s_, {
                get: function() {
                    return this[r]
                },
                set: function(t) {
                    this[r] = t
                }
            })
        }
    }

    function Xe(t, i, e, n, s) {
        var r,
            h,
            a,
            o,
            _,
            f,
            u,
            c,
            d = function(t) {
                return function(i) {
                    t(i)
                }
            },
            l = function() {
                h = null,
                    a = null,
                    o = _,
                    _ = null,
                    f = null
            },
            v = function(t) {
                r = t,
                    u || (u = Bi()),
                    u[xa] = r.width,
                    u[Fa] = r.height,
                    i.width = r.width,
                    i[Fa] = r[Fa]
            },
            b = function(t) {
                g(),
                    l(),
                    h = t.transparencyGiven ? t[a_] : null,
                    a = 10 * t.delayTime,
                    _ = t[o_]
            },
            g = function() {
                if (f) {
                    var t = f.getImageData(0, 0, r[xa], r.height),
                        e = {
                            data: t,
                            _pixels: we(t.data, r[xa], r.height),
                            delay: a
                        };
                    s[Br](i, e)
                }
            },
            y = function(t) {
                f || (f = u.getContext(Da));
                var i = t[__] ? t[f_] : r[u_],
                    e = f.getImageData(t.leftPos, t.topPos, t[xa], t[Fa]);
                t[c_][d_](function(t, n) {
                        h !== t ? (e[go][4 * n + 0] = i[t][0], e.data[4 * n + 1] = i[t][1], e.data[4 * n + 2] = i[t][2], e[go][4 * n + 3] = 255) : (2 === o || 3 === o) && (e[go][4 * n + 3] = 0)
                    }),
                    f[l_](0, 0, r[xa], r[Fa]),
                    f.putImageData(e, t[v_], t[b_])
            },
            m = function() {},
            p = {
                hdr: d(v),
                gce: d(b),
                com: d(m),
                app: {
                    NETSCAPE: d(m)
                },
                img: d(y, !0),
                eof: function() {
                    g(),
                        e.call(i)
                }
            },
            E = new XMLHttpRequest;
        lD || E[g_]("text/plain; charset=x-user-defined"),
            E.onload = function() {
                c = new OP(E.responseText);
                try {
                    AP(c, p)
                } catch (t) {
                    n.call(i, y_)
                }
            },
            E[m_] = function() {
                n[Br](i, p_)
            },
            E.open(ba, t, !0),
            E[E_]()
    }

    function Ve(t) {
        var i = [51, 10, 10, 100, 101, 109, 111, 46, 113, 117, 110, 101, 101, 46, 99, 111, 109, 44, 109, 97, 112, 46, 113, 117, 110, 101, 101, 46, 99, 111, 109, 10, 50, 46, 48, 10, 49, 52, 51, 49, 51, 51, 55, 51, 51, 55, 50, 49, 56, 10, 10, 48, 10];
        return i[d_](function(e, n) {
                i[n] = t(e)
            }),
            i[x_]("")
    }

    function Ke(t, i) {
        try {
            if (null == t || t[jr] < 8) return;
            if (null == i || i.length <= 0) return;
            for (var e = "", n = 0; n < i[jr]; n++) e += i.charCodeAt(n).toString();
            var s = Math.floor(e[jr] / 5),
                r = parseInt(e.charAt(s) + e[T_](2 * s) + e[T_](3 * s) + e.charAt(4 * s) + e[T_](5 * s), 10),
                h = Math.round(i.length / 2),
                a = Math[w_](2, 31) - 1,
                o = parseInt(t.substring(t.length - 8, t.length), 16);
            for (t = t[to](0, t[jr] - 8), e += o; e.length > 10;) e = (parseInt(e.substring(0, 10), 10) + parseInt(e[to](10, e.length), 10)).toString();
            e = (r * e + h) % a;
            for (var _ = "", f = "", n = 0; n < t[jr]; n += 2) _ = parseInt(parseInt(t[to](n, n + 2), 16) ^ Math[sh](e / a * 255), 10),
                f += String[O_](_),
                e = (r * e + h) % a;
            return 0 | f[0] ? sN = DP[I_ + NP + A_](f) : null
        } catch (u) {}
    }

    function Ze() {
        var t = CP;
        if (!t) return void(fN = !0);
        nN = t;
        var i;
        t = t.split(bh);
        for (var e = 0; e < t[jr] && (i = Ke(t[e], LP), !(i && i[Xr](Na).length >= 8));) 1 == t[jr] && (i = Ke(t[e], S_)),
            e++;
        if (!i || i.split(Na).length < 8) return aN = !0,
            "" === LP || LP == C_ + $P + k_ + GP + L_ || LP == R_ + zP + D_ ? (oN = lN, fN = !1, cN = !1, void(eN = !1)) : (oN = lN, void(fN = !0));
        eN = i[Xr](Na);
        var n = eN[3];
        if (n != Kj) return aN = !0,
            void(cN = !0);
        fN = !1,
            cN = !1;
        var s = eN[0];
        (M_ == s || P_ == s) && (aN = !1);
        var r = eN[5];
        _N = r;
        var h = eN[6];
        oN = h
    }

    function Je() {
        var t = nN;
        if (t) {
            var i;
            t = t[Xr](bh);
            for (var e = 0; e < t.length && (i = vN(t[e], LP), !(i && i[Xr](Na).length >= 8));) 1 == t.length && (i = vN(t[e], S_)),
                e++;
            if (i.split(Na)[jr] >= 8) return void(uN = !1)
        }
        return "" === LP || LP == C_ + $P + k_ + GP + L_ || LP == R_ + zP + D_ ? void(uN = !1) : void(uN = !0)
    }

    function Qe() {
        if (aN) {
            var t = nr[UP + N_]._j0,
                i = hN;
            nr[UP + N_]._j0 = function() {
                t[_h](this, arguments),
                    i.call(this._n0m, this.g)
            };
            var e = os[UP + N_]._g3;
            os[UP + N_]._g3 = function(t) {
                e[_h](this, arguments),
                    i[Br](this, t)
            }
        }
    }

    function tn() {
        if (_N !== !0 && _N) {
            var t = _N.split(Th);
            if (3 != t[jr]) return void(IN[ah]._j0 = null);
            var i = parseInt(t[0], 10),
                e = parseInt(t[1], 10),
                n = parseInt(t[2], 10),
                s = 3,
                r = (365.2425 * (i - 2e3 + 10 * s) + (e - 1) * s * 10 + n) * s * 8 * s * 1200 * 1e3;
            kP > r && (IN.prototype._j0 = null)
        }
    }

    function en() {
        var t = 0 | oN;
        t && (LD[UP + N_]._k5 = function(i, n) {
            var s = i.id;
            return s === e || this[j_](s) ? !1 : this._im.length > t ? !1 : (y(this._im, i, n), this._la[s] = i, i)
        })
    }

    function nn() {
        fN && (LD[UP + N_]._k5 = LD[UP + N_]._fx)
    }

    function sn() {
        uN && (os[UP + N_]._jr = null)
    }

    function rn() {
        dN && (_s.prototype._jb = _s.prototype._62)
    }

    function hn() {
        cN && (WN[UP + N_]._jr = null)
    }

    function an() {
        eN === e && (os[UP + N_]._jr = null)
    }

    function on(t) {
        return t[B_] ? (t = t[B_], t._dc ? t._dc : t._gn === !1 ? t : null) : null
    }

    function _n(t, i, e) {
        if (e = e || i[z_], e == t) return !1;
        var n = t.getEdgeBundle(e);
        return n || (n = new zj(t, e), t._linkedNodes[e.id] = n),
            n._i0(i, t)
    }

    function fn(t, i, e) {
        if (e = e || i.toAgent, e == t) return !1;
        var n = t[$_](e);
        return n ? n._n0s(i, t) : void 0
    }

    function un(t, i, n) {
        return n === e && (n = i.toAgent),
            n != t ? (t._7r || (t._7r = new LD), t._7r[Lh](i) === !1 ? !1 : void t._9e++) : void 0
    }

    function cn(t, i, e) {
        return t._7r && t._7r[Zr](i) !== !1 ? (t._9e--, void fn(t, i, e)) : !1
    }

    function dn(t, i) {
        return i.fromAgent != t ? (t._95 || (t._95 = new LD), t._95.add(i) === !1 ? !1 : void t._my6++) : void 0
    }

    function ln(t, i) {
        return t._95 && t._95[Zr](i) !== !1 ? (t._my6--, void fn(i.fromAgent, i, t)) : !1
    }

    function vn(t, i) {
        if (i === e && (i = t instanceof EN), i) {
            if (t[G_]()) return null;
            var n = vn(t.from, !1);
            if (t.isLooped()) return n;
            for (var s = vn(t.to, !1); null != n && null != s;) {
                if (n == s) return n;
                if (n[F_](s)) return s;
                if (s[F_](n)) return n;
                n = vn(n, !1),
                    s = vn(s, !1)
            }
            return null
        }
        for (var r = t[B_]; null != r;) {
            if (r._hd()) return r;
            r = r[B_]
        }
        return null
    }

    function bn(t, i, e) {
        t._hd() && t[Nr]() && t[zr][d_](function(t) {
                    t instanceof xN && i[Lh](t) && bn(t, i, e)
                },
                this),
            t.hasFollowers() && t._dk.forEach(function(t) {
                (null == e || e.accept(t)) && i.add(t) && bn(t, i, e)
            })
    }

    function gn(t, i) {
        i.parent ? i[B_][Y_](i, i.parent[q_] - 1) : t[H_][U_](i, t.roots[jr] - 1)
    }

    function yn(t, i) {
        if (i instanceof EN) return void(i[G_]() || pn(t, i));
        for (gn(t, i); i = i.parent;) gn(t, i)
    }

    function mn(t, i) {
        if (i instanceof EN) return void(i[G_]() || pn(t, i));
        for (gn(t, i); i = i.parent;) gn(t, i)
    }

    function pn(t, i) {
        var e = i[W_];
        if (i.isLooped()) gn(t, e);
        else {
            var n = i.toAgent;
            gn(t, e),
                gn(t, n)
        }
    }

    function En(t, i) {
        return t._9e++,
            t._f7 ? (i._i2 = t._h6, t._h6._i4 = i, void(t._h6 = i)) : (t._f7 = i, void(t._h6 = i))
    }

    function xn(t, i) {
        t._9e--,
            t._h6 == i && (t._h6 = i._i2),
            i._i2 ? i._i2._i4 = i._i4 : t._f7 = i._i4,
            i._i4 && (i._i4._i2 = i._i2),
            i._i2 = null,
            i._i4 = null,
            fn(t, i, i.$to)
    }

    function Tn(t, i) {
        return t._my6++,
            t._h8 ? (i._jj = t._j8, t._j8._jk = i, void(t._j8 = i)) : (t._h8 = i, void(t._j8 = i))
    }

    function wn(t, i) {
        t._my6--,
            t._j8 == i && (t._j8 = i._jj),
            i._jj ? i._jj._jk = i._jk : t._h8 = i._jk,
            i._jk && (i._jk._jj = i._jj),
            i._jj = null,
            i._jk = null
    }

    function On(t, i) {
        return i = i || new LD,
            t.forEachEdge(function(t) {
                i[Lh]({
                    id: t.id,
                    edge: t,
                    fromAgent: t.$from._dc,
                    toAgent: t.$to._dc
                })
            }),
            t[X_](function(t) {
                t instanceof xN && On(t, i)
            }),
            i
    }

    function In(t, i, e) {
        return Sn(t, i, e) === !1 ? !1 : An(t, i, e)
    }

    function An(t, i, e) {
        if (t._f7)
            for (var n = t._f7; n;) {
                if (i[Br](e, n) === !1) return !1;
                n = n._i4
            }
    }

    function Sn(t, i, e) {
        if (t._h8)
            for (var n = t._h8; n;) {
                if (i.call(e, n) === !1) return !1;
                n = n._jk
            }
    }

    function Cn(t, i, n, s, r, h, a) {
        return h || a ? (h = h || 0, a = a === e ? h : a || 0, h = Math[Ga](h, s / 2), a = Math[Ga](a, r / 2), t[V_](i + h, n), t.lineTo(i + s - h, n), t[K_](i + s, n, i + s, n + a), t.lineTo(i + s, n + r - a), t.quadTo(i + s, n + r, i + s - h, n + r), t[Z_](i + h, n + r), t[K_](i, n + r, i, n + r - a), t[Z_](i, n + a), t.quadTo(i, n, i + h, n), t[So](), t) : (t.moveTo(i, n), t.lineTo(i + s, n), t[Z_](i + s, n + r), t[Z_](i, n + r), t[So](), t)
    }

    function kn(t, i) {
        var e = i.r || 1,
            n = i.cx || 0,
            s = i.cy || 0,
            r = e * Math[Ja](Math.PI / 8),
            h = e * Math[pa](Math.PI / 4);
        t[V_](n + e, s),
            t.quadTo(n + e, s + r, n + h, s + h),
            t.quadTo(n + r, s + e, n, s + e),
            t.quadTo(n - r, s + e, n - h, s + h),
            t[K_](n - e, s + r, n - e, s),
            t[K_](n - e, s - r, n - h, s - h),
            t.quadTo(n - r, s - e, n, s - e),
            t[K_](n + r, s - e, n + h, s - h),
            t[K_](n + e, s - r, n + e, s)
    }

    function Ln(t, i, e, n, s) {
        i instanceof ee && (n = i.width, s = i.height, e = i.cy - s / 2, i = i.cx - n / 2);
        var r = .5522848,
            h = n / 2 * r,
            a = s / 2 * r,
            o = i + n,
            _ = e + s,
            f = i + n / 2,
            u = e + s / 2;
        return t[V_](i, u),
            t.curveTo(i, u - a, f - h, e, f, e),
            t[J_](f + h, e, o, u - a, o, u),
            t[J_](o, u + a, f + h, _, f, _),
            t.curveTo(f - h, _, i, u + a, i, u),
            t
    }

    function Rn(t, i, e, n, s) {
        var r = 2 * n,
            h = 2 * s,
            a = i + n / 2,
            o = e + s / 2;
        return t[V_](a - r / 4, o - h / 12),
            t[Z_](i + .306 * n, e + .579 * s),
            t[Z_](a - r / 6, o + h / 4),
            t[Z_](i + n / 2, e + .733 * s),
            t[Z_](a + r / 6, o + h / 4),
            t.lineTo(i + .693 * n, e + .579 * s),
            t.lineTo(a + r / 4, o - h / 12),
            t.lineTo(i + .611 * n, e + .332 * s),
            t.lineTo(a + 0, o - h / 4),
            t.lineTo(i + .388 * n, e + .332 * s),
            t.closePath(),
            t
    }

    function Dn(t, i, e, n, s) {
        return t[V_](i, e),
            t.lineTo(i + n, e + s / 2),
            t.lineTo(i, e + s),
            t.closePath(),
            t
    }

    function Mn(t, i, e, n, s) {
        return t[V_](i, e + s / 2),
            t[Z_](i + n / 2, e),
            t[Z_](i + n, e + s / 2),
            t[Z_](i + n / 2, e + s),
            t.closePath(),
            t
    }

    function Pn(t, i, e, n, s, r) {
        return t.moveTo(i, e),
            t.lineTo(i + n, e + s / 2),
            t[Z_](i, e + s),
            r || (t[Z_](i + .25 * n, e + s / 2), t[So]()),
            t
    }

    function Nn(t, i, e, n, s) {
        if (!t || 3 > t) throw new Error("edge number must greater than 2");
        t = 0 | t,
            n = n || 50,
            s = s || 0,
            i = i || 0,
            e = e || 0;
        for (var r, h, a = 0, o = 2 * Math.PI / t, _ = new vP; t > a;) r = i + n * Math[Ea](s),
            h = e + n * Math.sin(s),
            a ? _[Z_](r, h) : _.moveTo(r, h),
            ++a,
            s += o;
        return _.closePath(),
            _
    }

    function jn() {
        var t = new vP;
        return t[V_](75, 40),
            t.curveTo(75, 37, 70, 25, 50, 25),
            t[J_](20, 25, 20, 62.5, 20, 62.5),
            t[J_](20, 80, 40, 102, 75, 120),
            t.curveTo(110, 102, 130, 80, 130, 62.5),
            t[J_](130, 62.5, 130, 25, 100, 25),
            t[J_](85, 25, 75, 37, 75, 40),
            t
    }

    function Bn() {
        var t = new vP;
        return t.moveTo(20, 0),
            t[Z_](80, 0),
            t[Z_](100, 100),
            t[Z_](0, 100),
            t.closePath(),
            t
    }

    function zn() {
        var t = new vP;
        return t.moveTo(100, 0),
            t.lineTo(100, 80),
            t.lineTo(0, 100),
            t.lineTo(0, 20),
            t[So](),
            t
    }

    function $n() {
        var t = new vP;
        return t.moveTo(20, 0),
            t.lineTo(100, 0),
            t.lineTo(80, 100),
            t[Z_](0, 100),
            t.closePath(),
            t
    }

    function Gn() {
        var t = new vP;
        return t.moveTo(43, 23),
            t.lineTo(28, 10),
            t[Z_](37, 2),
            t.lineTo(63, 31),
            t[Z_](37, 59),
            t[Z_](28, 52),
            t.lineTo(44, 38),
            t[Z_](3, 38),
            t[Z_](3, 23),
            t[So](),
            t
    }

    function Fn() {
        var t = new vP;
        return t.moveTo(1, 8),
            t.lineTo(7, 2),
            t[Z_](32, 26),
            t.lineTo(7, 50),
            t[Z_](1, 44),
            t.lineTo(18, 26),
            t[So](),
            t.moveTo(27, 8),
            t.lineTo(33, 2),
            t[Z_](57, 26),
            t.lineTo(33, 50),
            t[Z_](27, 44),
            t[Z_](44, 26),
            t.closePath(),
            t
    }

    function Yn() {
        var t = new vP;
        return t[V_](0, 15),
            t[Z_](23, 15),
            t[Z_](23, 1),
            t[Z_](47, 23),
            t[Z_](23, 43),
            t.lineTo(23, 29),
            t.lineTo(0, 29),
            t.closePath(),
            t
    }

    function qn() {
        var t = new vP;
        return t.moveTo(0, 21),
            t.lineTo(30, 21),
            t[Z_](19, 0),
            t[Z_](25, 0),
            t.lineTo(47, 25),
            t.lineTo(25, 48),
            t[Z_](19, 48),
            t[Z_](30, 28),
            t.lineTo(0, 28),
            t.closePath(),
            t
    }

    function Hn() {
        var t = new vP;
        return t[V_](0, 0),
            t.lineTo(34, 24),
            t[Z_](0, 48),
            t[Z_](14, 24),
            t.closePath(),
            t
    }

    function Un() {
        var t = new vP;
        return t.moveTo(20, 0),
            t[Z_](34, 14),
            t[Z_](20, 28),
            t.lineTo(22, 18),
            t.lineTo(1, 25),
            t[Z_](10, 14),
            t.lineTo(1, 3),
            t.lineTo(22, 10),
            t.closePath(),
            t
    }

    function Wn() {
        var t = new vP;
        return t.moveTo(4, 18),
            t[Z_](45, 18),
            t.lineTo(37, 4),
            t.lineTo(83, 25),
            t.lineTo(37, 46),
            t[Z_](45, 32),
            t.lineTo(4, 32),
            t[So](),
            t
    }

    function Xn() {
        var t = new vP;
        return t[V_](17, 11),
            t[Z_](27, 11),
            t[Z_](42, 27),
            t.lineTo(27, 42),
            t.lineTo(17, 42),
            t.lineTo(28, 30),
            t.lineTo(4, 30),
            t[Z_](4, 23),
            t.lineTo(28, 23),
            t[So](),
            t
    }

    function Vn() {
        wN.register(MM.SHAPE_CIRCLE, Ln(new vP, 0, 0, 100, 100)),
            wN[Q_](MM[tf], Cn(new vP, 0, 0, 100, 100)),
            wN.register(MM.SHAPE_ROUNDRECT, Cn(new vP, 0, 0, 100, 100, 20, 20)),
            wN[Q_](MM.SHAPE_STAR, Rn(new vP, 0, 0, 100, 100)),
            wN.register(MM.SHAPE_TRIANGLE, Dn(new vP, 0, 0, 100, 100)),
            wN.register(MM[ef], Nn(5)),
            wN[Q_](MM[nf], Nn(6)),
            wN.register(MM.SHAPE_DIAMOND, Mn(new vP, 0, 0, 100, 100)),
            wN.register(MM.SHAPE_HEART, jn()),
            wN.register(MM[sf], Bn()),
            wN[Q_](MM[rf], zn()),
            wN[Q_](MM.SHAPE_PARALLELOGRAM, $n());
        var t = new vP;
        t.moveTo(20, 0),
            t[Z_](40, 0),
            t[Z_](40, 20),
            t.lineTo(60, 20),
            t.lineTo(60, 40),
            t[Z_](40, 40),
            t[Z_](40, 60),
            t.lineTo(20, 60),
            t.lineTo(20, 40),
            t.lineTo(0, 40),
            t[Z_](0, 20),
            t.lineTo(20, 20),
            t.closePath(),
            wN[Q_](MM.SHAPE_CROSS, t),
            wN[Q_](MM[hf], Pn(new vP, 0, 0, 100, 100)),
            wN.register(MM[af], Gn()),
            wN.register(MM.SHAPE_ARROW_2, Fn()),
            wN[Q_](MM.SHAPE_ARROW_3, Yn()),
            wN[Q_](MM.SHAPE_ARROW_4, qn()),
            wN.register(MM.SHAPE_ARROW_5, Hn()),
            wN[Q_](MM[of], Un()),
            wN[Q_](MM[_f], Wn()),
            wN.register(MM.SHAPE_ARROW_8, Xn()),
            wN.register(MM.SHAPE_ARROW_OPEN, Pn(new vP, 0, 0, 100, 100, !0))
    }

    function Kn() {
        j(this, Kn, arguments),
            this[ff] = !0
    }

    function Zn() {
        j(this, Zn),
            this._$u = new eM
    }

    function Jn() {
        if (this._gn === !0) {
            var t = this._7r,
                i = this._95;
            if (t)
                for (t = t._im; t.length;) {
                    var e = t[0];
                    cn(this, e, e.toAgent)
                }
            if (i)
                for (i = i._im; i[jr];) {
                    var e = i[0];
                    ln(this, e, e[W_])
                }
            return void this[X_](function(t) {
                t._hd() && Jn.call(t)
            })
        }
        var n = On(this);
        n.forEach(function(t) {
                t = t[uf];
                var i = t.$from,
                    e = t[cf],
                    n = i.isDescendantOf(this),
                    s = e.isDescendantOf(this);
                n && !s ? (un(this, t), _n(this, t)) : s && !n && (dn(this, t), _n(t.fromAgent, t, this))
            },
            this)
    }

    function Qn() {
        j(this, Qn, arguments),
            this[df] = null
    }

    function ts(t, i, e, n) {
        return t[i] = e,
            n ? {
                get: function() {
                    return this[i]
                },
                set: function(t) {
                    if (t !== this[i]) {
                        this[i] = t, !this._9z,
                            this._1p = !0;
                        for (var e = n.length; --e >= 0;) this[n[e]] = !0
                    }
                }
            } : {
                get: function() {
                    return this[i]
                },
                set: function(t) {
                    t !== this[i] && (this[i] = t)
                }
            }
    }

    function is(t, i) {
        var e = {},
            n = {};
        for (var s in i) {
            var r = i[s];
            r[lf] && r.validateFlags.forEach(function(t, i, e) {
                    e[i] = vf + t,
                        n[t] = !0
                }),
                e[s] = ts(t, uh + s, r.value, r[lf])
        }
        for (var h in n) t[vf + h] = !0;
        Object[bf](t, e)
    }

    function es(t, i, e, n) {
        if (Array[gf](i))
            for (var s = i[jr]; --s >= 0;) es(t, i[s], e, n);
        else {
            var r = i.target;
            if (r) {
                if (r instanceof IN || (r = t[r]), !r) return
            } else r = t;
            if (n || (n = t.getProperty(i[yf], e)), i[mf] && (r[i[mf]] = n), i[pf]) {
                var h = i[pf];
                h instanceof Function || (h = t[h]),
                    h instanceof Function && h.call(t, n, r)
            }
        }
    }

    function ns() {
        AN[d_](function(t) {
                this[t] = {}
            },
            this)
    }

    function ss(t, i, e, n) {
        return n == MM[Ef] ? void(t[e] = i) : n == MM[xf] ? void t.set(e, i) : n == MM[Tf] ? void t[wf](e, i) : !1
    }

    function rs() {
        j(this, rs, arguments)
    }

    function hs() {
        j(this, hs, arguments)
    }

    function as(t) {
        var i = Bi(!0);
        return We(i.g),
            i[Of] = function() {
                return !1
            },
            t[If](i),
            ci(i, qN),
            i
    }

    function os(t) {
        this._mj = t,
            w(this._mj, Af),
            t[Sf] = 0,
            this._jm = as(t),
            this.ratio = this._jm[wa] || 1,
            this._topCanvas = new nr(this, t),
            this._g1 = [],
            this._my5 = new HN,
            this._jp = new _s(this),
            this._mk = new LD;
        var i = this;
        this._mk._fx = function(t, e, n) {
                e.destroy();
                var s = e.uiBounds;
                return e._hk && s && i._my5._ld(e.$x + e.uiBounds.x, e.$y + e.uiBounds.y, e[Cf].width, e.uiBounds[Fa]),
                    LD.prototype._fx.call(this, t, e, n)
            },
            this._mk.clear = function() {
                return this.forEach(function(t) {
                        t[kf]()
                    }),
                    LD.prototype.clear.call(this)
            },
            this._mxo = [],
            this._86 = {},
            this._88 = new BD,
            this._8i = [],
            this._mxk()
    }

    function _s(t) {
        this._n0m = t,
            this._jp = new kD,
            this._jp[wa] = t[wa],
            this._6v = new BD
    }

    function fs(t, i, e, n) {
        var s = us(t, i, e, n),
            r = [];
        if (vs(t)) cs(s, i, e, r, n[Lf](SN.EDGE_EXTEND));
        else {
            Os(t, i, e, r, s, n);
            var h = ds(t, n),
                a = h ? ps(t, s, i, e, n[Lf](SN.EDGE_SPLIT_PERCENT)) : n.getStyle(SN[Rf]);
            0 == a && (s = !s)
        }
        return r
    }

    function us(t, i, e) {
        if (null != t) {
            if (t == MM[Df] || t == MM.EDGE_TYPE_ORTHOGONAL_HORIZONTAL || t == MM[Mf] || t == MM[Pf] || t == MM.EDGE_TYPE_EXTEND_RIGHT) return !0;
            if (t == MM.EDGE_TYPE_ELBOW_VERTICAL || t == MM.EDGE_TYPE_ORTHOGONAL_VERTICAL || t == MM.EDGE_TYPE_VERTICAL_HORIZONTAL || t == MM[Nf] || t == MM.EDGE_TYPE_EXTEND_BOTTOM) return !1
        }
        var n = ys(i, e),
            s = ms(i, e);
        return n >= s
    }

    function cs(t, i, e, n, s) {
        t ? ks(i, e, n, s) : Ls(i, e, n, s)
    }

    function ds(t, i) {
        return i.getStyle(SN.EDGE_SPLIT_BY_PERCENT)
    }

    function ls(t) {
        return null != t && (t == MM.EDGE_TYPE_EXTEND_TOP || t == MM.EDGE_TYPE_EXTEND_LEFT || t == MM[jf] || t == MM.EDGE_TYPE_EXTEND_RIGHT)
    }

    function vs(t) {
        return t && (t == MM[Bf] || t == MM.EDGE_TYPE_ELBOW_HORIZONTAL || t == MM.EDGE_TYPE_ELBOW_VERTICAL)
    }

    function bs(t, i, e, n, s) {
        if (t == MM.EDGE_TYPE_HORIZONTAL_VERTICAL || t == MM.EDGE_TYPE_VERTICAL_HORIZONTAL) return new MD(n.x + n[xa] / 2, n.y + n[Fa] / 2);
        var r;
        if (ls(t)) {
            var h = Math.min(e.y, n.y),
                a = Math[Ga](e.x, n.x),
                o = Math[ja](e[Ah], n.bottom),
                _ = Math[ja](e[zf], n.right);
            if (r = s[Lf](SN.EDGE_EXTEND), t == MM[Nf]) return new MD((a + _) / 2, h - r);
            if (t == MM.EDGE_TYPE_EXTEND_LEFT) return new MD(a - r, (h + o) / 2);
            if (t == MM.EDGE_TYPE_EXTEND_BOTTOM) return new MD((a + _) / 2, o + r);
            if (t == MM.EDGE_TYPE_EXTEND_RIGHT) return new MD(_ + r, (h + o) / 2)
        }
        var f = ds(t, s);
        if (r = f ? ps(t, i, e, n, s[Lf](SN.EDGE_SPLIT_PERCENT)) : s.getStyle(SN[Rf]), r == Number.NEGATIVE_INFINITY || r == Number[$f]) return new MD(n.x + n[xa] / 2, n.y + n.height / 2);
        if (0 == r) return new MD(e.x + e[xa] / 2, e.y + e.height / 2);
        if (i) {
            var u = e.x + e.right < n.x + n.right;
            return new MD(Ts(u, r, e.x, e[xa]), e.y + e[Fa] / 2)
        }
        var c = e.y + e.bottom < n.y + n.bottom;
        return new MD(e.x + e[xa] / 2, Ts(c, r, e.y, e[Fa]))
    }

    function gs(t, i, e, n) {
        var s = Math.max(i, n) - Math.min(t, e);
        return s - (i - t + n - e)
    }

    function ys(t, i) {
        var e = Math.max(t.x + t[xa], i.x + i[xa]) - Math.min(t.x, i.x);
        return e - t[xa] - i.width
    }

    function ms(t, i) {
        var e = Math[ja](t.y + t.height, i.y + i.height) - Math.min(t.y, i.y);
        return e - t.height - i[Fa]
    }

    function ps(t, i, e, n, s) {
        var r = Es(s, i, e, n, null);
        return r * s
    }

    function Es(t, i, e, n) {
        return i ? xs(t, e.x, e.right, n.x, n[zf]) : xs(t, e.y, e[Ah], n.y, n[Ah])
    }

    function xs(t, i, e, n, s) {
        var r = gs(i, e, n, s),
            h = n + s > i + e;
        if (r > 0) {
            if (1 == t) return r + (s - n) / 2;
            if (t >= 0 && 1 > t) return r;
            if (0 > t) return h ? n - i : e - s
        }
        return Math.abs(h && t > 0 || !h && 0 > t ? e - s : i - n)
    }

    function Ts(t, i, e, n) {
        return t == i > 0 ? e + n + Math.abs(i) : e - Math.abs(i)
    }

    function ws(t, i) {
        var e = t[jr];
        if (!(3 > e)) {
            var n = i[Lf](SN[Gf]);
            if (n != MM[Ff]) {
                var s = i[Lf](SN[Yf]),
                    r = 0,
                    h = 0;
                s && (I(s) ? r = h = s : (r = s.x || 0, h = s.y || 0));
                for (var a, o, _, f, u = t[0], c = t[1], d = null, l = 2; e > l; l++) {
                    var v = t[l],
                        b = c.x - u.x,
                        g = c.y - u.y,
                        p = v.x - c.x,
                        E = v.y - c.y,
                        x = !b || b > -BM && BM > b,
                        T = !g || g > -BM && BM > g,
                        w = !p || p > -BM && BM > p,
                        O = !E || E > -BM && BM > E,
                        A = T;
                    (x && O || T && w) && (A ? (a = Math.min(2 == l ? Math.abs(b) : Math[Sh](b) / 2, r), o = Math.min(l == e - 1 ? Math.abs(E) : Math[Sh](E) / 2, h), _ = new MD(c.x - (b > 0 ? a : -a), c.y), f = new MD(c.x, c.y + (E > 0 ? o : -o))) : (a = Math.min(l == e - 1 ? Math.abs(p) : Math.abs(p) / 2, r), o = Math.min(2 == l ? Math[Sh](g) : Math[Sh](g) / 2, h), _ = new MD(c.x, c.y - (g > 0 ? o : -o)), f = new MD(c.x + (p > 0 ? a : -a), c.y)), m(t, c), l--, e--, (_.x != u.x || _.y != u.y) && (y(t, _, l), l++, e++), n == MM[qf] ? (y(t, f, l), l++, e++) : n == MM[Hf] && (y(t, [c, f], l), l++, e++)),
                    u = c,
                        c = v
                }
                null != d && f.x == c.x && f.y == c.y && m(t, c)
            }
        }
    }

    function Os(t, i, e, n, s, r) {
        var h = r.getStyle(SN.EDGE_CONTROL_POINT),
            a = null == h;
        if (null != h) {
            var o = (new BD)[Uf](i).union(e);
            o[Wf](h) || (s = Is(h.x, h.y, o.y, o.x, o.bottom, o.right))
        } else h = bs(t, s, i, e, r);
        s ? Cs(i, e, h, n, a) : Ss(i, e, h, n, a)
    }

    function Is(t, i, e, n, s, r) {
        return e > i && e - i > n - t && e - i > t - r || i > s && i - s > n - t && i - s > t - r ? !1 : !0
    }

    function As(t, i, e) {
        return i >= t.x && i <= t[zf] && e >= t.y && e <= t[Ah]
    }

    function Ss(t, i, e, n, s) {
        var r = Math[ja](t.y, i.y),
            h = Math.min(t.y + t[Fa], i.y + i[Fa]),
            a = null != e ? e.y : h + (r - h) / 2,
            o = t.x + t[xa] / 2,
            _ = i.x + i[xa] / 2;
        if (0 == s && null != e && (e.x >= t.x && e.x <= t.x + t[xa] && (o = e.x), e.x >= i.x && e.x <= i.x + i[xa] && (_ = e.x)), As(i, o, a) || As(t, o, a) || n.push(new MD(o, a)), As(i, _, a) || As(t, _, a) || n.push(new MD(_, a)), 0 == n.length)
            if (null != e) As(i, e.x, a) || As(t, e.x, a) || n.push(new MD(e.x, a));
            else {
                var f = Math.max(t.x, i.x),
                    u = Math[Ga](t.x + t.width, i.x + i.width);
                n.push(new MD(f + (u - f) / 2, a))
            }
    }

    function Cs(t, i, e, n, s) {
        var r = Math[ja](t.x, i.x),
            h = Math.min(t.x + t.width, i.x + i[xa]),
            a = null != e ? e.x : h + (r - h) / 2,
            o = t.y + t[Fa] / 2,
            _ = i.y + i[Fa] / 2;
        if (0 == s && null != e && (e.y >= t.y && e.y <= t.y + t.height && (o = e.y), e.y >= i.y && e.y <= i.y + i.height && (_ = e.y)), As(i, a, o) || As(t, a, o) || n.push(new MD(a, o)), As(i, a, _) || As(t, a, _) || n.push(new MD(a, _)), 0 == n.length)
            if (null != e) As(i, a, e.y) || As(t, a, e.y) || n.push(new MD(a, e.y));
            else {
                var f = Math.max(t.y, i.y),
                    u = Math[Ga](t.y + t.height, i.y + i[Fa]);
                n.push(new MD(a, f + (u - f) / 2))
            }
    }

    function ks(t, i, e, n) {
        var s = i.x + i.width < t.x,
            r = t.x + t[xa] < i.x,
            h = s ? t.x : t.x + t[xa],
            a = t.y + t.height / 2,
            o = r ? i.x : i.x + i[xa],
            _ = i.y + i[Fa] / 2,
            f = n,
            u = s ? -f : f,
            c = new MD(h + u, a);
        u = r ? -f : f;
        var d = new MD(o + u, _);
        if (s == r) {
            var l = s ? Math.min(h, o) - n : Math[ja](h, o) + n;
            e.push(new MD(l, a)),
                e[Yr](new MD(l, _))
        } else if (c.x < d.x == s) {
            var v = a + (_ - a) / 2;
            e.push(c),
                e.push(new MD(c.x, v)),
                e[Yr](new MD(d.x, v)),
                e.push(d)
        } else e[Yr](c),
            e[Yr](d)
    }

    function Ls(t, i, e, n) {
        var s = i.y + i.height < t.y,
            r = t.y + t.height < i.y,
            h = t.x + t[xa] / 2,
            a = s ? t.y : t.y + t.height,
            o = i.x + i[xa] / 2,
            _ = r ? i.y : i.y + i[Fa],
            f = n,
            u = s ? -f : f,
            c = new MD(h, a + u);
        u = r ? -f : f;
        var d = new MD(o, _ + u);
        if (s == r) {
            var l = s ? Math.min(a, _) - n : Math[ja](a, _) + n;
            e.push(new MD(h, l)),
                e.push(new MD(o, l))
        } else if (c.y < d.y == s) {
            var v = h + (o - h) / 2;
            e.push(c),
                e.push(new MD(v, c.y)),
                e[Yr](new MD(v, d.y)),
                e.push(d)
        } else e.push(c),
            e.push(d)
    }

    function Rs(t) {
        return t == MM[Xf] || t == MM.EDGE_TYPE_ORTHOGONAL_HORIZONTAL || t == MM.EDGE_TYPE_HORIZONTAL_VERTICAL || t == MM.EDGE_TYPE_ORTHOGONAL_VERTICAL || t == MM.EDGE_TYPE_VERTICAL_HORIZONTAL || t == MM[Nf] || t == MM.EDGE_TYPE_EXTEND_LEFT || t == MM.EDGE_TYPE_EXTEND_BOTTOM || t == MM.EDGE_TYPE_EXTEND_RIGHT || t == MM.EDGE_TYPE_ELBOW || t == MM[Df] || t == MM[Vf]
    }

    function Ds(t, i) {
        var e,
            n;
        i && i.width && i[Fa] ? (e = i[xa], n = i.height) : e = n = isNaN(i) ? CD.ARROW_SIZE : i;
        var s = wN[Kf](t, -e, -n / 2, e, n);
        return s || (s = new vP, s.moveTo(-e, -n / 2), s[Z_](0, 0), s[Z_](-e, n / 2)),
            s
    }

    function Ms(t, i) {
        var e = Math.sin(i),
            n = Math[Ea](i),
            s = t.x,
            r = t.y;
        return t.x = s * n - r * e,
            t.y = s * e + r * n,
            t
    }

    function Ps(t, i, e, n, s, r) {
        var h = Math.atan2(n - i, e - t),
            a = new MD(s, r);
        return a.rotate = h,
            Ms(a, h),
            a.x += t,
            a.y += i,
            a
    }

    function Ns(t, i, e, n, s) {
        i = si(n, i.x, i.y, e.x, e.y),
            e = si(s, e.x, e.y, i.x, i.y);
        var r = Math.PI / 2 + Math[Ih](e.y - i.y, e.x - i.x),
            h = t * Math.cos(r),
            a = t * Math[pa](r),
            o = e.x - i.x,
            _ = e.y - i.y,
            f = i.x + .25 * o,
            u = i.y + .25 * _,
            c = i.x + .75 * o,
            d = i.y + .75 * _;
        return [new dP(fP, [f + h, u + a, c + h, d + a])]
    }

    function js(t, i, n) {
        if (y(t, new dP(aP, [i.x, i.y]), 0), n) {
            if (t[jr] > 1) {
                var s = t[t[jr] - 1];
                if (_P == s.type && (s.points[2] === e || null === s.points[2])) return s.points[2] = n.x,
                    void(s[aa][3] = n.y);
                if (fP == s[N_] && (s.points[4] === e || null === s[aa][4])) return s[aa][4] = n.x,
                    void(s[aa][5] = n.y)
            }
            t[Yr](new dP(oP, [n.x, n.y]))
        }
    }

    function Bs(t, i, e, n, s, r, h, a) {
        return i[Zf]() ? void(e._eq = i._9q.toDatas()) : n == s ? void t.drawLoopedEdge(e, n, r, h) : void t.drawEdge(e, n, s, r, h, a)
    }

    function zs(t, i, e, n, s) {
        var r = n == s,
            h = t[Jf].getUI(n),
            a = r ? h : t[Jf].getUI(s),
            o = i.edgeType,
            _ = h.bodyBounds.clone(),
            f = r ? _ : a.bodyBounds[Fr](),
            u = i.hasPathSegments();
        if (!r && !o && !u) {
            var c = n[ff],
                d = s[ff];
            if (c != d) {
                var l,
                    v,
                    b,
                    g,
                    y = i[Qf];
                c ? (l = h, v = _, b = a, g = f) : (l = a, v = f, b = h, g = _);
                var m = Hs(v, l, c, b, g, y);
                if (m && 2 == m.length) {
                    var p = m[0],
                        E = m[1];
                    return e.moveTo(p.x, p.y),
                        E.x == p.x && E.y == p.y && (E.y += .01),
                        e.lineTo(E.x, E.y),
                        void(e._6a = !0)
                }
            }
        }
        Bs(t, i, e, h, a, o, _, f), (!r || u) && $s(t, i, e, h, a, o, _, f),
            e._6a = !0
    }

    function $s(t, i, n, s, r, h, a, o) {
        var _ = n._eq,
            f = a.center,
            u = o[tu];
        if (_.length) {
            var c = _[0],
                d = c.firstPoint,
                l = _[_[jr] - 1],
                v = l.lastPoint;
            a[iu](d.x, d.y) && (c[N_] == fP ? (f = d, d = {
                        x: c[aa][2],
                        y: c[aa][3]
                    },
                    c[aa] = c[aa].slice(2), c.type = _P) : c[N_] == _P && (f = d, d = {
                        x: c[aa][0],
                        y: c.points[1]
                    },
                    c.points = c.points[$r](2), c.type = oP)),
                Gs(s, a, d, f, e, e);
            var b,
                g = l.points[jr],
                y = v.x === e || v.y === e;
            g >= 4 && (y || o[iu](v.x, v.y)) && (y || (u = v), b = !0, v = {
                        x: l.points[g - 4],
                        y: l[aa][g - 3]
                    },
                    o[iu](v.x, v.y) && (u = v, g >= 6 ? (v = {
                            x: l[aa][g - 6],
                            y: l.points[g - 5]
                        },
                        l.points = l.points.slice(0, 4), l.type = _P) : 1 == _.length ? (v = {
                            x: f.x,
                            y: f.y
                        },
                        l.points = l.points[$r](0, 2), l.type = oP) : (l = _[_.length - 2], v = l[Va]))),
                Gs(r, o, v, u, e, e),
                b && (g = l[aa][jr], l.points[g - 2] = u.x, l[aa][g - 1] = u.y, u = null)
        } else {
            var m = Math[Ih](u.y - f.y, u.x - f.x),
                p = Math[Ea](m),
                E = Math.sin(m);
            Gs(s, a, u, f, p, E),
                Gs(r, o, f, u, -p, -E)
        }
        js(n._eq, f, u)
    }

    function Gs(t, i, n, s, r, h) {
        if (r === e) {
            var a = Math.atan2(n.y - s.y, n.x - s.x);
            r = Math.cos(a),
                h = Math.sin(a)
        }
        for (n = {
                x: n.x,
                y: n.y
            },
            i.contains(n.x, n.y) || (n = si(i, s.x, s.y, n.x, n.y));;) {
            if (!i.contains(n.x, n.y)) return s;
            if (t._h9(n.x - r, n.y - h)) {
                s.x = n.x - r / 4,
                    s.y = n.y - h / 4;
                break
            }
            n.x -= r,
                n.y -= h
        }
        return s
    }

    function Fs(t, i, e, n, s, r, h, a) {
        if (i.hasPathSegments()) return i._9q;
        var o = i.edgeType;
        if (Rs(o)) {
            var _ = fs(o, e, n, t, s, r);
            if (!_ || !_[jr]) return null;
            y(_, h, 0),
                _.push(a),
                o != MM.EDGE_TYPE_ELBOW && ws(_, t);
            for (var f = [], u = _.length, c = 1; u - 1 > c; c++) {
                var d = _[c];
                f[Yr](C(d) ? new dP(_P, [d[0].x, d[0].y, d[1].x, d[1].y]) : new dP(oP, [d.x, d.y]))
            }
            return f
        }
        if (i[eu]) {
            var l = t._2o();
            if (!l) return;
            return Ns(l, h, a, e, n)
        }
    }

    function Ys(t, i, e) {
        var n = t[Lf](SN[nu]),
            s = t._2o(),
            r = n + .2 * s,
            h = i.x + i.width - r,
            a = i.y,
            o = i.x + i.width,
            _ = i.y + r;
        n += s;
        var f = .707,
            u = -.707,
            c = i.x + i.width,
            d = i.y,
            l = c + f * n,
            v = d + u * n,
            b = {
                x: h,
                y: a
            },
            g = {
                x: l,
                y: v
            },
            y = {
                x: o,
                y: _
            },
            m = b.x,
            p = g.x,
            E = y.x,
            x = b.y,
            T = g.y,
            w = y.y,
            O = ((w - x) * (T * T - x * x + p * p - m * m) + (T - x) * (x * x - w * w + m * m - E * E)) / (2 * (p - m) * (w - x) - 2 * (E - m) * (T - x)),
            I = ((E - m) * (p * p - m * m + T * T - x * x) + (p - m) * (m * m - E * E + x * x - w * w)) / (2 * (T - x) * (E - m) - 2 * (w - x) * (p - m)),
            r = Math[$a]((m - O) * (m - O) + (x - I) * (x - I)),
            A = Math[Ih](b.y - I, b.x - O),
            S = Math.atan2(y.y - I, y.x - O),
            C = S - A;
        return 0 > C && (C += 2 * Math.PI),
            qs(O, I, A, C, r, r, !0, e)
    }

    function qs(t, i, e, n, s, r, h, a) {
        var o,
            _,
            f,
            u,
            c,
            d,
            l,
            v,
            b,
            g,
            y;
        if (Math[Sh](n) > 2 * Math.PI && (n = 2 * Math.PI), c = Math.ceil(Math.abs(n) / (Math.PI / 4)), o = n / c, _ = o, f = e, c > 0) {
            d = t + Math.cos(f) * s,
                l = i + Math.sin(f) * r,
                moveTo ? a.moveTo(d, l) : a[Z_](d, l);
            for (var m = 0; c > m; m++) f += _,
                u = f - _ / 2,
                v = t + Math.cos(f) * s,
                b = i + Math[pa](f) * r,
                g = t + Math[Ea](u) * (s / Math[Ea](_ / 2)),
                y = i + Math[pa](u) * (r / Math.cos(_ / 2)),
                a[K_](g, y, v, b)
        }
    }

    function Hs(t, i, n, s, r, h) {
        var a = r.cx,
            o = r.cy,
            _ = a < t.x,
            f = a > t[zf],
            u = o < t.y,
            c = o > t[Ah],
            d = t.cx,
            l = t.cy,
            v = _ || f,
            b = u || c,
            g = h === e || null === h;
        g && (h = Math[Ih](o - l, a - d), v || b || (h += Math.PI));
        var y = Math.cos(h),
            m = Math.sin(h),
            p = Ws(i, t, {
                x: a,
                y: o
            }, -y, -m);
        p || (h = Math.atan2(o - l, a - d), v || b || (h += Math.PI), y = Math.cos(h), m = Math.sin(h), p = Ws(i, t, {
            x: a,
            y: o
        }, -y, -m) || {
            x: d,
            y: l
        });
        var E = Ws(s, r, {
            x: p.x,
            y: p.y
        }, -p[su] || y, -p[ru] || m, !1) || {
            x: a,
            y: o
        };
        return n ? [p, E] : [E, p]
    }

    function Us(t, i, e, n, s, r) {
        var h = i < t.x,
            a = i > t[zf],
            o = e < t.y,
            _ = e > t.bottom;
        if (h && n > 0) {
            var f = t.x - i,
                u = e + f * s / n;
            if (u >= t.y && u <= t[Ah]) return {
                x: t.x,
                y: u,
                perX: n,
                perY: s
            }
        }
        if (a && 0 > n) {
            var f = t[zf] - i,
                u = e + f * s / n;
            if (u >= t.y && u <= t[Ah]) return {
                x: t[zf],
                y: u,
                perX: n,
                perY: s
            }
        }
        if (o && s > 0) {
            var c = t.y - e,
                d = i + c * n / s;
            if (d >= t.x && d <= t[zf]) return {
                x: d,
                y: t.y,
                perX: n,
                perY: s
            }
        }
        if (_ && 0 > s) {
            var c = t[Ah] - e,
                d = i + c * n / s;
            if (d >= t.x && d <= t[zf]) return {
                x: d,
                y: t[Ah],
                perX: n,
                perY: s
            }
        }
        return r !== !1 ? Us(t, i, e, -n, -s, !1) : void 0
    }

    function Ws(t, i, e, n, s, r) {
        if (!i[iu](e.x, e.y)) {
            if (e = Us(i, e.x, e.y, n, s, r), !e) return;
            return Xs(t, i, e, e.perX, e[ru])
        }
        return r === !1 ? Xs(t, i, e, n, s) : Xs(t, i, {
                x: e.x,
                y: e.y,
                perX: n,
                perY: s
            },
            n, s) || Xs(t, i, e, -n, -s)
    }

    function Xs(t, i, e, n, s) {
        for (;;) {
            if (!i[iu](e.x, e.y)) return;
            if (t._h9(e.x + n, e.y + s)) break;
            e.x += n,
                e.y += s
        }
        return e
    }

    function Vs(t) {
        return pe(t) ? t : t.match(/.(gif|jpg|jpeg|png)$/gi) ? t : (t = J(t), t instanceof Object && t.draw ? t : void 0)
    }

    function Ks(t) {
        for (var i = t.parent; i;) {
            if (i.enableSubNetwork) return i;
            i = i[B_]
        }
        return null
    }

    function Zs() {
        j(this, Zs, arguments)
    }

    function Js(t, e, n, s, r, h, a) {
        var o = i[Ra](hu);
        o.className = au,
            ci(o, KN),
            e && ci(o, e);
        var _ = i[Ra](ou);
        return h && (OD ? _.ontouchstart = h : _[_u] = h),
            _.name = a,
            _[fu] = n,
            ci(_, ZN),
            r && ci(_, r),
            s && di(_, uu, cu),
            o._img = _,
            o[If](_),
            t.appendChild(o),
            o
    }

    function Qs(t, e) {
        this._navPane = i[Ra](hu),
            this._navPane[Hr] = du,
            ci(this._navPane, {
                "background-color": lu,
                overflow: vu,
                "float": _o,
                "user-select": bu,
                position: gu
            }),
            this._top = Js(this._navPane, {
                    width: yu
                },
                CD[mu], !1, null, e, oo),
            this._left = Js(this._navPane, {
                    height: yu
                },
                CD[pu], !1, JN, e, _o),
            this._right = Js(this._navPane, {
                    height: yu,
                    right: Eu
                },
                CD[pu], !0, JN, e, zf),
            this._mxottom = Js(this._navPane, {
                    width: yu,
                    bottom: Eu
                },
                CD[mu], !0, null, e, Ah),
            t[If](this._navPane)
    }

    function tr(t, i) {
        this._n0m = t;
        var e = function(i) {
            var e,
                n,
                s = i.target,
                r = s[so];
            if (_o == r) e = 1;
            else if (zf == r) e = -1;
            else if (oo == r) n = 1;
            else {
                if (Ah != r) return;
                n = -1
            }
            OD && (s[Hr] = xu, setTimeout(function() {
                        s.className = ""
                    },
                    100)),
                R(i),
                t._k2._9m(e, n)
        };
        Qs.call(this, i, e),
            this._3s(i.clientWidth, i[Tu])
    }

    function ir(t, i) {
        this._n0m = t,
            this[wu](i, t)
    }

    function er() {
        j(this, er, arguments)
    }

    function nr(t, i) {
        this._n0m = t,
            this._jm = as(i),
            this.g = this._jm.g,
            this._8v = new LD
    }

    function sr(t) {
        var i = t[Ou],
            e = [];
        return t[Iu][d_](function(i) {
                t[Au](i) && t.isSelectable(i) && e[Yr](i)
            }),
            i[Vo](e)
    }

    function rr(t, i, n, s) {
        s === e && (s = CD[Su]);
        var r = t[Cu](i);
        return n ? t.zoomIn(r.x, r.y, s) : t.zoomOut(r.x, r.y, s)
    }

    function hr(t, i, e) {
        var n = t[qa];
        e = e || n,
            i = i || 1;
        var s = null;
        s && e.width * e.height * i * i > s && (i = Math[$a](s / e.width / e.height));
        var r = Bi();
        We(r.g),
            r[xa] = e[xa] * i,
            r.height = e.height * i,
            t._7u._g3(r.g, i, e);
        var h = null;
        try {
            h = r[ku](Lu)
        } catch (a) {
            DM.error(a)
        }
        return {
            canvas: r,
            data: h,
            width: r[xa],
            height: r[Fa]
        }
    }

    function ar(t) {
        this.graph = t,
            this.topCanvas = t.topCanvas
    }

    function or(t, i) {
        this[Ru] = t,
            this[Du] = i || Mu
    }

    function _r() {
        j(this, _r, arguments)
    }

    function fr(t, i) {
        if (!t) return i;
        var n = {};
        for (var s in t) n[s] = t[s];
        for (var s in i) n[s] === e && (n[s] = i[s]);
        return n
    }

    function ur() {
        j(this, ur, arguments)
    }

    function cr() {
        j(this, cr, arguments)
    }

    function dr() {
        j(this, dr, arguments)
    }

    function lr() {
        j(this, lr, arguments)
    }

    function vr(i, e, n) {
        i += t.pageXOffset,
            e += t[Yh];
        var s = n.getBoundingClientRect();
        return {
            x: i + s[_o],
            y: e + s.top
        }
    }

    function br(t, i, e) {
        var n = t[Pu],
            s = t.offsetHeight;
        t[Oa][_o] = i - n / 2 + Ia,
            t.style[oo] = e - s / 2 + Ia
    }

    function gr(t) {
        var e = i[Ra](Aa),
            n = e.getContext(Da),
            s = getComputedStyle(t, null),
            r = s.font;
        r || (r = s.fontStyle + Vr + s.fontSize + Vr + s.fontFamily),
            n.font = r;
        var h = t[Nu],
            a = h[Xr](Na),
            o = parseInt(s.fontSize),
            _ = 0,
            f = 0;
        return DM[d_](a,
            function(t) {
                var i = n.measureText(t)[xa];
                i > _ && (_ = i),
                    f += 1.2 * o
            }), {
            width: _,
            height: f
        }
    }

    function yr(t, e) {
        if (Jr == typeof t.selectionStart && Jr == typeof t[ju]) {
            var n = t[Nu],
                s = t[Bu];
            t.value = n.slice(0, s) + e + n.slice(t[ju]),
                t[ju] = t[Bu] = s + e.length
        } else if (zu != typeof i.selection) {
            var r = i.selection.createRange();
            r.text = e,
                r.collapse(!1),
                r[$u]()
        }
    }

    function mr(i) {
        if (lD) {
            var e = t[Gu] || t[Fu],
                n = t[Yu] || t.pageYOffset;
            return i.select(),
                void t.scrollTo(e, n)
        }
        i.select()
    }

    function pr() {}

    function Er(t) {
        this.graph = t,
            this.topCanvas = t[qu],
            this.handlerSize = OD ? 8 : 5
    }

    function xr(t) {
        this[Jf] = t,
            this[qu] = t.topCanvas,
            this[Hu] = OD ? 8 : 4,
            this._rotateHandleLength = OD ? 30 : 20
    }

    function Tr(t, i) {
        var e = new BD;
        return e.addPoint($e.call(t, {
                x: i.x,
                y: i.y
            })),
            e[Ta]($e.call(t, {
                x: i.x + i.width,
                y: i.y
            })),
            e.addPoint($e[Br](t, {
                x: i.x + i[xa],
                y: i.y + i[Fa]
            })),
            e[Ta]($e.call(t, {
                x: i.x,
                y: i.y + i[Fa]
            })),
            e
    }

    function wr(t) {
        t %= 2 * Math.PI;
        var i = Math[Ha](t / ij);
        return 0 == i || 4 == i ? "ew-resize" : 1 == i || 5 == i ? "nwse-resize" : 2 == i || 6 == i ? "ns-resize" : Uu
    }

    function Or(e, n, s) {
        var r = i.documentElement,
            h = new DM.Rect(t[Fu], t.pageYOffset, r[Wu] - 2, r.clientHeight - 2),
            a = e.offsetWidth,
            o = e[Xu];
        n + a > h.x + h.width && (n = h.x + h.width - a),
            s + o > h.y + h[Fa] && (s = h.y + h[Fa] - o),
            n < h.x && (n = h.x),
            s < h.y && (s = h.y),
            e.style[_o] = n + Ia,
            e[Oa].top = s + Ia
    }

    function Ir(t, i, e, n, s) {
        this[co] = t,
            this[N_] = Vu,
            this[Ku] = i,
            this.event = e,
            this.data = n,
            this.datas = s
    }

    function Ar(t) {
        this._52 = {},
            this._k2 = t,
            this._k2._1n.addListener(this._92, this),
            this[Zu] = MM[Ju]
    }

    function Sr(t) {
        return t >= 10 && 20 > t
    }

    function Cr(t) {
        return t == yj || t == Ej
    }

    function kr() {
        var t,
            i,
            e = {},
            n = [],
            s = 0,
            r = {},
            h = 0;
        this[Jf].forEach(function(a) {
                if (this[Qu](a))
                    if (a instanceof xN) {
                        var o = {
                            node: a,
                            id: a.id,
                            x: a.x,
                            y: a.y
                        };
                        for (this.appendNodeInfo && this.appendNodeInfo(a, o), e[a.id] = o, n[Yr](o), s++, i = a[B_]; i instanceof ON;) {
                            t || (t = {});
                            var _ = t[i.id];
                            _ || (_ = t[i.id] = {
                                    id: i.id,
                                    children: []
                                }),
                                _[zr][Yr](o),
                                i = i.parent
                        }
                    } else if (a instanceof EN && !a[tc]() && a.fromAgent && a[z_]) {
                    var o = {
                        edge: a
                    };
                    r[a.id] = o,
                        h++
                }
            },
            this);
        var a = {};
        for (var o in r) {
            var _ = r[o],
                f = _[uf],
                u = f[W_],
                c = f.toAgent,
                d = u.id + ic + c.id,
                l = c.id + ic + u.id;
            if (e[u.id] && e[c.id] && !a[d] && !a[l]) {
                var v = e[u.id],
                    b = e[c.id];
                _.from = v,
                    _.to = b,
                    a[d] = _,
                    this.appendEdgeInfo && this[ec](f, _)
            } else delete r[o],
                h--
        }
        return {
            groups: t,
            nodesArray: n,
            nodes: e,
            nodeCount: s,
            edges: r,
            edgeCount: h,
            minEnergy: this[nc](s, h)
        }
    }

    function Lr(t) {
        this[Jf] = t,
            this[sc] = {}
    }

    function Rr() {
        j(this, Rr, arguments)
    }

    function Dr(t, i, e, n, s) {
        n ? t.forEachEdge(function(n) {
                var r = n.otherNode(t);
                r != e && r._marker != s && i(r, t)
            },
            this, !0) : t.forEachOutEdge(function(n) {
            var r = n[z_];
            r != e && r._marker != s && i(r, t)
        })
    }
    var Nr = "hasChildren",
        jr = "length",
        Br = "call",
        zr = "children",
        $r = "slice",
        Gr = "splice",
        Fr = "clone",
        Yr = "push",
        qr = "requestAnimationFrame",
        Hr = "className",
        Ur = "classList",
        Wr = "class",
        Xr = "split",
        Vr = " ",
        Kr = "indexOf",
        Zr = "remove",
        Jr = "number",
        Qr = "string",
        th = "boolean",
        ih = "preventDefault",
        eh = "returnValue",
        nh = "cancelBubble",
        sh = "floor",
        rh = "random",
        hh = "object",
        ah = "prototype",
        oh = "superclass",
        _h = "apply",
        fh = "handle",
        uh = "$",
        ch = "defineProperty",
        dh = "defaultValue",
        lh = "readOnly",
        vh = "rgba(",
        bh = ",",
        gh = ")",
        yh = "ceil",
        mh = "#",
        ph = "000000",
        Eh = "enumerable",
        xh = "_",
        Th = ".",
        wh = "touches",
        Oh = "console",
        Ih = "atan2",
        Ah = "bottom",
        Sh = "abs",
        Ch = "horizontalPosition",
        kh = "verticalPosition",
        Lh = "add",
        Rh = "onChildAdd",
        Dh = "onChildRemove",
        Mh = "ms-",
        Ph = "replace",
        Nh = "insertRule",
        jh = "{",
        Bh = "}",
        zh = "addRule",
        $h = "changedTouches",
        Gh = "getBoundingClientRect",
        Fh = "pageX",
        Yh = "pageYOffset",
        qh = "getElementByMouseEvent",
        Hh = "getUI",
        Uh = "getUIByMouseEvent",
        Wh = "onclick",
        Xh = "ondblclick",
        Vh = "timeStamp",
        Kh = "clientY",
        Zh = "onstart",
        Jh = "button",
        Qh = "onlongpress",
        ta = "wheelDelta",
        ia = "onmousewheel",
        ea = "on",
        na = "mousemove",
        sa = "screenX",
        ra = "screenY",
        ha = "mouseup",
        aa = "points",
        oa = "responseXML",
        _a = "'",
        fa = "' XML format error.",
        ua = "responseText",
        ca = "' JSON format error.",
        da = "?",
        la = "__time=",
        va = "now",
        ba = "GET",
        ga = "onreadystatechange",
        ya = "status",
        ma = "' load error",
        pa = "sin",
        Ea = "cos",
        xa = "width",
        Ta = "addPoint",
        wa = "ratio",
        Oa = "style",
        Ia = "px",
        Aa = "canvas",
        Sa = "webkitBackingStorePixelRatio",
        Ca = "mozBackingStorePixelRatio",
        ka = "oBackingStorePixelRatio",
        La = "backingStorePixelRatio",
        Ra = "createElement",
        Da = "2d",
        Ma = "setSize",
        Pa = "font",
        Na = "\n",
        ja = "max",
        Ba = "measureText",
        za = "log",
        $a = "sqrt",
        Ga = "min",
        Fa = "height",
        Ya = "getBounds",
        qa = "bounds",
        Ha = "round",
        Ua = "stroke",
        Wa = "lineWidth",
        Xa = "isPointInStroke",
        Va = "lastPoint",
        Ka = "concat",
        Za = "rotate",
        Ja = "tan",
        Qa = "_mxoundaryPoint",
        to = "substring",
        io = "draw",
        eo = "test",
        no = "IMAGE_HEIGHT",
        so = "name",
        ro = "scaleMode",
        ho = "full.uniform",
        ao = "padding",
        oo = "top",
        _o = "left",
        fo = "translate",
        uo = "validate",
        co = "source",
        lo = "error",
        vo = "draw image error - ",
        bo = "getImageData",
        go = "data",
        yo = "(",
        mo = "BLEND_MODE",
        po = "BLEND_MODE_MULTIPLY",
        Eo = "BLEND_MODE_DARKEN",
        xo = "BLEND_MODE_COLOR_BURN",
        To = "BLEND_MODE_LIGHTEN",
        wo = "BLEND_MODE_GRAY",
        Oo = "putImageData",
        Io = "setTransform",
        Ao = "scale",
        So = "closePath",
        Co = "$rotate",
        ko = "$offsetX",
        Lo = "$offsetY",
        Ro = "$rotatable",
        Do = "$_hostRotate",
        Mo = "$layoutByAnchorPoint",
        Po = "$invalidateSize",
        No = "$invalidateAnchorPoint",
        jo = "setByRect",
        Bo = "$padding",
        zo = "$border",
        $o = "grow",
        Go = "showPointer",
        Fo = "$anchorPosition",
        Yo = "backgroundGradient",
        qo = "$borderRadius",
        Ho = "$pointerX",
        Uo = "$pointerY",
        Wo = "intersectsPoint",
        Xo = "$pointerWidth",
        Vo = "set",
        Ko = "position",
        Zo = "layoutByPath",
        Jo = "$invalidateRotate",
        Qo = "setLineDash",
        t_ = "getLineDash",
        i_ = "mozDash",
        e_ = "webkitLineDash",
        n_ = "lineDash",
        s_ = "lineDashOffset",
        r_ = "mozDashOffset",
        h_ = "webkitLineDashOffset",
        a_ = "transparencyIndex",
        o_ = "disposalMethod",
        __ = "lctFlag",
        f_ = "lct",
        u_ = "gct",
        c_ = "pixels",
        d_ = "forEach",
        l_ = "clearRect",
        v_ = "leftPos",
        b_ = "topPos",
        g_ = "overrideMimeType",
        y_ = "parse",
        m_ = "onerror",
        p_ = "xhr",
        E_ = "send",
        x_ = "join",
        T_ = "charAt",
        w_ = "pow",
        O_ = "fromCharCode",
        I_ = "decodeU",
        A_ = "omponent",
        S_ = "qunee",
        C_ = "lo",
        k_ = "lh",
        L_ = "t",
        R_ = "12",
        D_ = "0.0.1",
        M_ = "2",
        P_ = "3",
        N_ = "type",
        j_ = "containsById",
        B_ = "parent",
        z_ = "toAgent",
        $_ = "getEdgeBundle",
        G_ = "isInvalid",
        F_ = "isDescendantOf",
        Y_ = "setChildIndex",
        q_ = "childrenCount",
        H_ = "roots",
        U_ = "setIndex",
        W_ = "fromAgent",
        X_ = "forEachChild",
        V_ = "moveTo",
        K_ = "quadTo",
        Z_ = "lineTo",
        J_ = "curveTo",
        Q_ = "register",
        tf = "SHAPE_RECT",
        ef = "SHAPE_PENTAGON",
        nf = "SHAPE_HEXAGON",
        sf = "SHAPE_TRAPEZIUM",
        rf = "SHAPE_RHOMBUS",
        hf = "SHAPE_ARROW_STANDARD",
        af = "SHAPE_ARROW_1",
        of = "SHAPE_ARROW_6",
        _f = "SHAPE_ARROW_7",
        ff = "busLayout",
        uf = "edge",
        cf = "$to",
        df = "$image",
        lf = "validateFlags",
        vf = "$invalidate",
        bf = "defineProperties",
        gf = "isArray",
        yf = "property",
        mf = "bindingProperty",
        pf = "callback",
        Ef = "PROPERTY_TYPE_ACCESSOR",
        xf = "PROPERTY_TYPE_CLIENT",
        Tf = "PROPERTY_TYPE_STYLE",
        wf = "setStyle",
        Of = "onselectstart",
        If = "appendChild",
        Af = "Q-Graph",
        Sf = "tabIndex",
        Cf = "uiBounds",
        kf = "destroy",
        Lf = "getStyle",
        Rf = "EDGE_SPLIT_VALUE",
        Df = "EDGE_TYPE_ELBOW_HORIZONTAL",
        Mf = "EDGE_TYPE_HORIZONTAL_VERTICAL",
        Pf = "EDGE_TYPE_EXTEND_LEFT",
        Nf = "EDGE_TYPE_EXTEND_TOP",
        jf = "EDGE_TYPE_EXTEND_BOTTOM",
        Bf = "EDGE_TYPE_ELBOW",
        zf = "right",
        $f = "POSITIVE_INFINITY",
        Gf = "EDGE_CORNER",
        Ff = "EDGE_CORNER_NONE",
        Yf = "EDGE_CORNER_RADIUS",
        qf = "EDGE_CORNER_BEVEL",
        Hf = "EDGE_CORNER_ROUND",
        Uf = "union",
        Wf = "intersects",
        Xf = "EDGE_TYPE_ORTHOGONAL",
        Vf = "EDGE_TYPE_ELBOW_VERTICAL",
        Kf = "getShape",
        Zf = "hasPathSegments",
        Jf = "graph",
        Qf = "angle",
        tu = "center",
        iu = "contains",
        eu = "$bundleEnabled",
        nu = "EDGE_LOOPED_EXTAND",
        su = "perX",
        ru = "perY",
        hu = "div",
        au = "Q-Graph-Nav-Button",
        ou = "img",
        _u = "onmousedown",
        fu = "src",
        uu = "transform",
        cu = "rotate(180deg)",
        du = "Q-Graph-Nav",
        lu = "rgba(0, 0, 0, 0)",
        vu = "hidden",
        bu = "none",
        gu = "absolute",
        yu = "100%",
        mu = "NAVIGATION_IMAGE_TOP",
        pu = "NAVIGATION_IMAGE_LEFT",
        Eu = "0px",
        xu = "hover",
        Tu = "clientHeight",
        wu = "init",
        Ou = "selectionModel",
        Iu = "graphModel",
        Au = "isVisible",
        Su = "ZOOM_ANIMATE",
        Cu = "globalToLocal",
        ku = "toDataURL",
        Lu = "image/png",
        Ru = "interactions",
        Du = "defaultCursor",
        Mu = "default",
        Pu = "offsetWidth",
        Nu = "value",
        ju = "selectionEnd",
        Bu = "selectionStart",
        zu = "undefined",
        $u = "select",
        Gu = "scrollX",
        Fu = "pageXOffset",
        Yu = "scrollY",
        qu = "topCanvas",
        Hu = "handlerSize",
        Uu = "nesw-resize",
        Wu = "clientWidth",
        Xu = "offsetHeight",
        Vu = "interaction",
        Ku = "kind",
        Zu = "currentMode",
        Ju = "INTERACTION_MODE_DEFAULT",
        Qu = "isLayoutable",
        tc = "isLooped",
        ic = "-",
        ec = "appendEdgeInfo",
        nc = "minEnergyFunction",
        sc = "currentMovingNodes",
        rc = "userAgent",
        hc = "ontouchstart",
        ac = "match",
        oc = "webkitRequestAnimationFrame",
        _c = "msRequestAnimationFrame",
        fc = "setTimeout",
        uc = "oCancelAnimationFrame",
        cc = "#333",
        dc = "normal",
        lc = "FONT_SIZE",
        vc = "px ",
        bc = "matrix(",
        gc = ",0,0,",
        yc = "getByIndex",
        mc = "' not exist",
        pc = "getById",
        Ec = "clear",
        xc = "isEmpty",
        Tc = "firstElementChild",
        wc = "tagName",
        Oc = "toUpperCase",
        Ic = "Point(",
        Ac = ", ",
        Sc = "distance",
        Cc = "NaN",
        kc = "Size(",
        Lc = "MAX_VALUE",
        Rc = " , ",
        Dc = "sortName",
        Mc = "l",
        Pc = "c",
        Nc = "r",
        jc = "m",
        Bc = "b",
        zc = "LEFT_TOP",
        $c = "LEFT_BOTTOM",
        Gc = "CENTER_MIDDLE",
        Fc = "RIGHT_MIDDLE",
        Yc = "RIGHT_BOTTOM",
        qc = "CENTER_TOP",
        Hc = "CENTER_BOTTOM",
        Uc = "radius",
        Wc = "intersectsRect",
        Xc = "classify",
        Vc = "source: ",
        Kc = ", type: ",
        Zc = ", kind: ",
        Jc = "oldValue",
        Qc = "property.change",
        td = ", propertyName: ",
        id = ", oldValue: ",
        ed = ", value: ",
        nd = "propertyName",
        sd = "newIndex",
        rd = "oldIndex",
        hd = "getChildIndex",
        ad = "child.add",
        od = "child.remove",
        _d = "child.index",
        fd = "beforeEvent",
        ud = "listener",
        cd = "onEvent",
        dd = "listeners",
        ld = "addListener",
        vd = "scope",
        bd = "list",
        gd = ", data: ",
        yd = ", index: ",
        md = ", oldIndex: ",
        pd = "KIND_CLEAR",
        Ed = "index.change",
        xd = "getId",
        Td = "accept",
        wd = "_k5",
        Od = "_fx",
        Id = "toDatas",
        Ad = "filter",
        Sd = "listChangeDispatcher",
        Cd = "selectionChangeDispatcher",
        kd = "dataChangeDispatcher",
        Ld = "childIndexChangeDispatcher",
        Rd = "$roots",
        Dd = "getEdges",
        Md = "parentChangeDispatcher",
        Pd = "data '",
        Nd = "box",
        jd = "KIND_REMOVE",
        Bd = "head",
        zd = "Transform",
        $d = "createPopup",
        Gd = "createTextNode",
        Fd = "text/css",
        Yd = "qunee-styles",
        qd = ":",
        Hd = ";\n",
        Ud = "addEventListener",
        Wd = "removeEventListener",
        Xd = "stopPropagation",
        Vd = "LONG_PRESS_INTERVAL",
        Kd = "DOMMouseScroll",
        Zd = ",mousedown,mouseup,click,mousemove,keydown",
        Jd = ",touchstart,touchmove,touchend,touchcancel",
        Qd = "startdrag",
        tl = "ondrag",
        il = "onpinch",
        el = "enddrag",
        nl = "onrelease",
        sl = "html",
        rl = "onElementRemoved",
        hl = "onClear",
        al = "clientX",
        ol = "dScale",
        _l = "prev",
        fl = "-webkit-zoom-in",
        ul = "-webkit-zoom-out",
        cl = "-webkit-grab",
        dl = "-webkit-grabbing",
        ll = "-moz-zoom-in",
        vl = "-moz-zoom-out",
        bl = "-moz-grab",
        gl = "-moz-grabbing",
        yl = "crosshair",
        ml = "move",
        pl = "url(data:image/gif;base64,iVBORw0KGgoAAAANSUhEUgAAABAAAAAQCAYAAAAf8/9hAAAAGXRFW…MBw1CqGUBMlA1yA4gxhKhYwBnfpKQDqqREquRGYgBAgAEAD8h/4adTIzwAAAAASUVORK5CYII=) 8 8,crosshair",
        El = "bounceIn",
        xl = "cancelAnimationFrame",
        Tl = "_ll",
        wl = "0.0",
        Ol = "isIOS",
        Il = "Rect",
        Al = "Size",
        Sl = "PropertyChangeEvent",
        Cl = "Dispatcher",
        kl = "DataModel",
        Ll = "IListener",
        Rl = "loadURL",
        Dl = "loadXML",
        Ml = "isMetaKey",
        Pl = "HashList",
        Nl = "prompt",
        jl = "border.rect",
        Bl = "border",
        zl = "shadow",
        $l = "elbow",
        Gl = "elbow.H",
        Fl = "elbow.V",
        Yl = "orthogonal",
        ql = "orthogonal.H",
        Hl = "orthogonal.V",
        Ul = "orthogonal.H.V",
        Wl = "orthogonal.V.H",
        Xl = "extend.top",
        Vl = "extend.left",
        Kl = "extend.bottom",
        Zl = "extend.right",
        Jl = "zigzag",
        Ql = "bevel",
        tv = "rect",
        iv = "circle",
        ev = "ELLIPSE",
        nv = "oval",
        sv = "roundrect",
        rv = "star",
        hv = "triangle",
        av = "hexagon",
        ov = "pentagon",
        _v = "trapezium",
        fv = "rhombus",
        uv = "parallelogram",
        cv = "heart",
        dv = "diamond",
        lv = "cross",
        vv = "arrow.standard",
        bv = "arrow.1",
        gv = "arrow.2",
        yv = "arrow.3",
        mv = "arrow.4",
        pv = "arrow.5",
        Ev = "arrow.6",
        xv = "arrow.7",
        Tv = "arrow.8",
        wv = "arrow.open",
        Ov = "butt",
        Iv = "LINE_CAP_TYPE_SQUARE",
        Av = "square",
        Sv = "LINE_JOIN_TYPE_BEVEL",
        Cv = "miter",
        kv = "SELECTION_TYPE",
        Lv = "SELECTION_TYPE_SHADOW",
        Rv = "SELECTION_TOLERANCE",
        Dv = "SELECTION_BORDER",
        Mv = "BORDER_RADIUS",
        Pv = "DOUBLE_BUFFER",
        Nv = "IMAGE_MAX_SIZE",
        jv = "offset",
        Bv = "arcTo",
        zv = "quadraticCurveTo",
        $v = "MAX_CACHE_PIXELS",
        Gv = "removeChild",
        Fv = "Image load error - ",
        Yv = "onload",
        qv = "bind",
        Hv = "parentNode",
        Uv = "drawImage",
        Wv = "save",
        Xv = "fillStyle",
        Vv = "#CCC",
        Kv = "clip",
        Zv = "textAlign",
        Jv = "middle",
        Qv = "#888",
        tb = "normal ",
        ib = "#FFF",
        eb = "strokeText",
        nb = "strokeStyle",
        sb = "#000",
        rb = "fillText",
        hb = "restore",
        ab = "shadowColor",
        ob = "shadowBlur",
        _b = "shadowOffsetX",
        fb = "shadowOffsetY",
        ub = "Loading...",
        cb = "renderColor",
        db = "Error...",
        lb = "maxScale",
        vb = "image",
        bb = "load",
        gb = "registerImage",
        yb = "hasImage",
        mb = "colors",
        pb = "positions",
        Eb = "createLinearGradient",
        xb = "GRADIENT_TYPE_RADIAL",
        Tb = "addColorStop",
        wb = "GRADIENT_TYPE_LINEAR",
        Ob = "LINEAR_GRADIENT_HORIZONTAL",
        Ib = "q",
        Ab = "a",
        Sb = "z",
        Cb = "SEGMENT_LINE_TO",
        kb = "SEGMENT_CURVE_TO",
        Lb = "toJSON",
        Rb = "selectionShadowBlur",
        Db = "selectionColor",
        Mb = "selectionShadowOffsetX",
        Pb = "selectionShadowOffsetY",
        Nb = "SELECTION_TYPE_BORDER",
        jb = "selectionBorder",
        Bb = "lineJoin",
        zb = "selectionType",
        $b = "outlineStyle",
        Gb = "outline",
        Fb = "darken",
        Yb = "multiply",
        qb = "color.burn",
        Hb = "linear.burn",
        Ub = "lighten",
        Wb = "BLEND_MODE_SCREEN",
        Xb = "screen",
        Vb = "gray",
        Kb = "BLEND_MODE_LINEAR_BURN",
        Zb = "rgba(0,0,0,0)",
        Jb = "lineCap",
        Qb = "miterLimit",
        tg = "#1C6B9D",
        ig = "#186493",
        eg = "#145E8B",
        ng = "#115B87",
        sg = "#115A85",
        rg = "#125C89",
        hg = "#176291",
        ag = "#1D6C9F",
        og = "#2479B0",
        _g = "#2881BB",
        fg = "#1F6FA2",
        ug = "#115A86",
        cg = "#004063",
        dg = "beginPath",
        lg = "bezierCurveTo",
        vg = "#2e8ece",
        bg = "#efefef",
        gg = "arc",
        yg = "fill",
        mg = "#135D89",
        pg = "#186494",
        Eg = "#1F70A4",
        xg = "#257AB2",
        Tg = "#2377AD",
        wg = "#1E6DA0",
        Og = "#105984",
        Ig = "#f7f8f8",
        Ag = "#6A6969",
        Sg = "#4F4C4B",
        Cg = "#545252",
        kg = "#646262",
        Lg = "#6F6E6F",
        Rg = "#4C4948",
        Dg = "#494645",
        Mg = "#7D7D7D",
        Pg = "#808080",
        Ng = "#888888",
        jg = "#939293",
        Bg = "#9E9D9D",
        zg = "#A7A5A4",
        $g = "#A9A6A5",
        Gg = "#A7A4A3",
        Fg = "#FFFFFF",
        Yg = "#E9EAEA",
        qg = "#9fa0a0",
        Hg = "#c9caca",
        Ug = "#3e3a39",
        Wg = "#B2CBEA",
        Xg = "#2E8ECE",
        Vg = "#727171",
        Kg = "#b5b5b6",
        Zg = "Q-",
        Jg = "delay",
        Qg = "pos",
        ty = "charCodeAt",
        iy = "readByte",
        ey = "readBytes",
        ny = "Invalid LZW code.",
        sy = "read",
        ry = "ver",
        hy = "GIF",
        ay = "sig",
        oy = "Not a GIF file.",
        _y = "shift",
        fy = "colorRes",
        uy = "gctSize",
        cy = "bgColor",
        dy = "gctFlag",
        ly = "hdr",
        vy = "userInput",
        by = "gce",
        gy = "comment",
        yy = "com",
        my = "ptHeader",
        py = "pte",
        Ey = "unknown",
        xy = "readUnsigned",
        Ty = "app",
        wy = "appData",
        Oy = "identifier",
        Iy = "authCode",
        Ay = "label",
        Sy = "extType",
        Cy = "interlaced",
        ky = "reserved",
        Ly = "lctSize",
        Ry = "lzwMinCodeSize",
        Dy = "sentinel",
        My = "ext",
        Py = "eof",
        Ny = "Unknown block: 0x",
        jy = "keydown",
        By = "ctrlKey",
        zy = "\nVersion - ",
        $y = "version",
        Gy = "\nPublish Date - ",
        Fy = "publishDate",
        Yy = "alert",
        qy = "f43b4e1133375609ab7a3212e157552652782c3dc9abfe47273959defd82eba0b5f3781a7138bd02954591b7ffc3a95aca49c9b03a2ae3317fcd2397fee2cfafc22186728105736650,cb1a696d47a44f741bf667aefb497aa4dcfcdabc9817288dface24a7fd3c0afdb18ddb42e49bdca295d58191399e633bf2c34e9ebd8329aef54181232fd9a088ff85aea0de05a749e4",
        Hy = "\nLicensed to: ",
        Uy = "%20website%3A%20demo.qunee.com%2Cmap.qunee.com",
        Wy = "fil",
        Xy = "nee",
        Vy = "RIC",
        Ky = "setT",
        Zy = "ho",
        Jy = "7.",
        Qy = "ca",
        tm = "os",
        im = "ifr",
        em = "Canvas",
        nm = "Rend",
        sm = "Cont",
        rm = "proto",
        hm = "locat",
        am = "create",
        om = "11000",
        _m = "15000",
        fm = "17000",
        um = "20000",
        cm = "31000",
        dm = "32000",
        lm = "imeout",
        vm = "ion",
        bm = "stname",
        gm = "Qu",
        ym = " for HTML5",
        mm = "iframe",
        pm = "contentWindow",
        Em = "localhost",
        xm = "127.00.1",
        Tm = "Element",
        wm = "ame",
        Om = "display",
        Im = "cont",
        Am = "entWindow",
        Sm = "Date",
        Cm = "ering",
        km = "ext2D",
        Lm = "lText",
        Rm = "documentElement",
        Dm = "$name",
        Mm = "equals",
        Pm = "hasEdge",
        Nm = "forEachEdge",
        jm = "ui",
        Bm = "invalidate",
        zm = "Q.Element",
        $m = "uiClass",
        Gm = "zIndex",
        Fm = "enableSubNetwork",
        Ym = "$from",
        qm = "connect",
        Hm = "Q.Edge",
        Um = "from",
        Wm = "path.segment",
        Xm = "firePathChange",
        Vm = "disconnect",
        Km = "to",
        Zm = "edgeType",
        Jm = "bundleEnabled",
        Qm = "Q-node",
        tp = "$location",
        ip = "hasLoops",
        ep = "invalidateVisibility",
        np = "location",
        sp = "host",
        rp = "hasFollowers",
        hp = "toFollowers",
        ap = "Q.Node",
        op = "follower.add",
        _p = "follower.remove",
        fp = "size",
        up = "anchorPosition",
        cp = "$path",
        dp = "SHAPENODE_STYLES",
        lp = "ARROW_TO",
        vp = "putStyles",
        bp = "Q.ShapeNode",
        gp = "path",
        yp = "generator",
        mp = "Q.Bus",
        pp = "currentSubNetwork",
        Ep = "GROUP_TYPE",
        xp = "GROUP_TYPE_RECT",
        Tp = "GROUP_PADDING",
        wp = "$groupType",
        Op = "Q.Group",
        Ip = "expanded",
        Ap = "minSize",
        Sp = "groupType",
        Cp = "groupImage",
        kp = "Group",
        Lp = "Q.Text",
        Rp = "invalidateData",
        Dp = "#444",
        Mp = "$anchorPoint",
        Pp = "selectionBackgroundColor",
        Np = "fillRect",
        jp = "strokeRect",
        Bp = "offsetX",
        zp = "offsetY",
        $p = "borderColor",
        Gp = "$backgroundColor",
        Fp = "$backgroundGradient",
        Yp = "measure",
        qp = "onBoundsChanged",
        Hp = "$invalidateLocation",
        Up = "$invalidateVisibility",
        Wp = "$visible",
        Xp = "$data",
        Vp = "doValidate",
        Kp = "Visibility",
        Zp = "AnchorPoint",
        Jp = "Location",
        Qp = "BackgroundGradient",
        tE = "SELECTION_SHADOW_BLUR",
        iE = "SELECTION_COLOR",
        eE = "Rotate",
        nE = "Data",
        sE = "target",
        rE = "getProperty",
        hE = "selection.color",
        aE = "selection.border",
        oE = "selection.type",
        _E = "render.color",
        fE = "RENDER_COLOR_BLEND_MODE",
        uE = "shadow.blur",
        cE = "SHADOW_COLOR",
        dE = "shadow.color",
        lE = "SHADOW_OFFSET_X",
        vE = "shadow.offset.x",
        bE = "SHADOW_OFFSET_Y",
        gE = "shadow.offset.y",
        yE = "shape.stroke",
        mE = "SHAPE_STROKE_STYLE",
        pE = "shape.stroke.style",
        EE = "shape.line.dash",
        xE = "SHAPE_LINE_DASH_OFFSET",
        TE = "SHAPE_FILL_COLOR",
        wE = "shape.fill.color",
        OE = "SHAPE_FILL_GRADIENT",
        IE = "shape.fill.gradient",
        AE = "SHAPE_OUTLINE",
        SE = "shape.outline",
        CE = "SHAPE_OUTLINE_STYLE",
        kE = "shape.outline.style",
        LE = "line.cap",
        RE = "line.join",
        DE = "layout.by.path",
        ME = "BACKGROUND_COLOR",
        PE = "background.color",
        NE = "background.gradient",
        jE = "BORDER",
        BE = "border.width",
        zE = "BORDER_COLOR",
        $E = "border.color",
        GE = "BORDER_LINE_DASH",
        FE = "border.line.dash",
        YE = "border.radius",
        qE = "IMAGE_BORDER",
        HE = "image.border.width",
        UE = "IMAGE_BORDER_STYLE",
        WE = "image.border.style",
        XE = "IMAGE_RADIUS",
        VE = "image.radius",
        KE = "image.padding",
        ZE = "IMAGE_Z_INDEX",
        JE = "image.z.index",
        QE = "label.rotate",
        tx = "LABEL_POSITION",
        ix = "label.position",
        ex = "label.color",
        nx = "LABEL_FONT_SIZE",
        sx = "label.font.size",
        rx = "label.font.family",
        hx = "label.font.style",
        ax = "LABEL_PADDING",
        ox = "label.padding",
        _x = "label.pointer.width",
        fx = "label.pointer",
        ux = "LABEL_RADIUS",
        cx = "label.radius",
        dx = "LABEL_OFFSET_X",
        lx = "label.offset.x",
        vx = "LABEL_OFFSET_Y",
        bx = "label.offset.y",
        gx = "LABEL_SIZE",
        yx = "label.size",
        mx = "LABEL_ALIGN_POSITION",
        px = "label.align.position",
        Ex = "label.border",
        xx = "label.border.style",
        Tx = "LABEL_BACKGROUND_COLOR",
        wx = "LABEL_BACKGROUND_GRADIENT",
        Ox = "label.rotatable",
        Ix = "label.shadow.blur",
        Ax = "LABEL_SHADOW_COLOR",
        Sx = "label.shadow.color",
        Cx = "LABEL_Z_INDEX",
        kx = "label.z.index",
        Lx = "label.on.top",
        Rx = "GROUP_BACKGROUND_GRADIENT",
        Dx = "group.stroke",
        Mx = "group.stroke.color",
        Px = "GROUP_STROKE_LINE_DASH",
        Nx = "EDGE_BUNDLE_LABEL_ROTATE",
        jx = "EDGE_BUNDLE_LABEL_POSITION",
        Bx = "EDGE_BUNDLE_LABEL_FONT_SIZE",
        zx = "EDGE_BUNDLE_LABEL_FONT_FAMILY",
        $x = "EDGE_BUNDLE_LABEL_FONT_STYLE",
        Gx = "EDGE_BUNDLE_LABEL_POINTER_WIDTH",
        Fx = "EDGE_BUNDLE_LABEL_RADIUS",
        Yx = "EDGE_BUNDLE_LABEL_OFFSET_X",
        qx = "EDGE_BUNDLE_LABEL_BACKGROUND_COLOR",
        Hx = "EDGE_BUNDLE_LABEL_ROTATABLE",
        Ux = "EDGE_WIDTH",
        Wx = "edge.width",
        Xx = "EDGE_COLOR",
        Vx = "edge.color",
        Kx = "edge.outline",
        Zx = "EDGE_OUTLINE_STYLE",
        Jx = "edge.outline.style",
        Qx = "EDGE_LINE_DASH",
        tT = "edge.line.dash",
        iT = "EDGE_FROM_OFFSET",
        eT = "edge.from.offset",
        nT = "edge.to.offset",
        sT = "edge.bundle.gap",
        rT = "edge.looped.extand",
        hT = "edge.extend",
        aT = "edge.control.point",
        oT = "EDGE_SPLIT_BY_PERCENT",
        _T = "EDGE_SPLIT_PERCENT",
        fT = "edge.split.percent",
        uT = "edge.split.value",
        cT = "edge.corner",
        dT = "edge.corner.radius",
        lT = "arrow.from",
        vT = "ARROW_FROM_SIZE",
        bT = "arrow.from.size",
        gT = "arrow.from.offset",
        yT = "arrow.from.stroke",
        mT = "ARROW_FROM_STROKE_STYLE",
        pT = "arrow.from.outline",
        ET = "ARROW_FROM_OUTLINE_STYLE",
        xT = "ARROW_FROM_LINE_DASH",
        TT = "arrow.from.line.dash",
        wT = "ARROW_FROM_LINE_DASH_OFFSET",
        OT = "ARROW_FROM_FILL_COLOR",
        IT = "ARROW_FROM_FILL_GRADIENT",
        AT = "ARROW_FROM_LINE_CAP",
        ST = "arrow.from.line.cap",
        CT = "ARROW_FROM_LINE_JOIN",
        kT = "arrow.from.line.join",
        LT = "arrow.to",
        RT = "arrow.to.size",
        DT = "ARROW_TO_OFFSET",
        MT = "arrow.to.offset",
        PT = "ARROW_TO_STROKE",
        NT = "arrow.to.stroke",
        jT = "ARROW_TO_STROKE_STYLE",
        BT = "arrow.to.outline",
        zT = "ARROW_TO_OUTLINE_STYLE",
        $T = "arrow.to.line.dash",
        GT = "ARROW_TO_FILL_COLOR",
        FT = "arrow.to.fill.color",
        YT = "ARROW_TO_LINE_CAP",
        qT = "arrow.to.line.cap",
        HT = "arrow.to.line.join",
        UT = "SELECTION_SHADOW_OFFSET_Y",
        WT = "color",
        XT = "fontSize",
        VT = "backgroundColor",
        KT = "showOnTop",
        ZT = "fontFamily",
        JT = "LABEL_FONT_STYLE",
        QT = "fontStyle",
        tw = "alignPosition",
        iw = "LABEL_ROTATE",
        ew = "pointerWidth",
        nw = "LABEL_POINTER",
        sw = "borderRadius",
        rw = "LABEL_ROTATABLE",
        hw = "rotatable",
        aw = "LABEL_SHADOW_OFFSET_Y",
        ow = "RENDER_COLOR",
        _w = "renderColorBlendMode",
        fw = "_2w",
        uw = "PADDING",
        cw = "borderLineDash",
        dw = "BORDER_LINE_DASH_OFFSET",
        lw = "borderLineDashOffset",
        vw = "_n01",
        bw = "fillColor",
        gw = "fillGradient",
        yw = "SHAPE_LINE_DASH",
        mw = "LINE_CAP",
        pw = "LINE_JOIN",
        Ew = "LAYOUT_BY_PATH",
        xw = "IMAGE_BACKGROUND_COLOR",
        Tw = "IMAGE_BORDER_LINE_DASH_OFFSET",
        ww = "checkBody",
        Ow = "_5t",
        Iw = "GROUP_BACKGROUND_COLOR",
        Aw = "shape",
        Sw = "GROUP_STROKE_STYLE",
        Cw = "_4v",
        kw = "fromArrow",
        Lw = "toArrow",
        Rw = "EDGE_OUTLINE",
        Dw = "fromArrowSize",
        Mw = "fromArrowOffset",
        Pw = "ARROW_FROM_STROKE",
        Nw = "fromArrowStroke",
        jw = "fromArrowStrokeStyle",
        Bw = "ARROW_FROM_OUTLINE",
        zw = "fromArrowOutline",
        $w = "fromArrowFillColor",
        Gw = "fromArrowLineDash",
        Fw = "fromArrowLineJoin",
        Yw = "fromArrowLineCap",
        qw = "ARROW_TO_SIZE",
        Hw = "toArrowSize",
        Uw = "toArrowOffset",
        Ww = "toArrowStroke",
        Xw = "toArrowStrokeStyle",
        Vw = "ARROW_TO_OUTLINE",
        Kw = "toArrowOutline",
        Zw = "toArrowOutlineStyle",
        Jw = "toArrowFillColor",
        Qw = "ARROW_TO_FILL_GRADIENT",
        tO = "toArrowFillGradient",
        iO = "toArrowLineDash",
        eO = "ARROW_TO_LINE_JOIN",
        nO = "toArrowLineJoin",
        sO = "toArrowLineCap",
        rO = "EDGE_BUNDLE_LABEL_COLOR",
        hO = "bundleLabel",
        aO = "EDGE_BUNDLE_LABEL_PADDING",
        oO = "EDGE_BUNDLE_LABEL_POINTER",
        _O = "EDGE_BUNDLE_LABEL_BORDER",
        fO = "EDGE_BUNDLE_LABEL_BORDER_STYLE",
        uO = "invalidateShape",
        cO = "IMAGE_BACKGROUND_GRADIENT",
        dO = "IMAGE_PADDING",
        lO = "IMAGE_BORDER_COLOR",
        vO = "ARROW_FROM",
        bO = "ARROW_FROM_OFFSET",
        gO = "ARROW_TO_LINE_DASH_OFFSET",
        yO = "invalidateChildrenIndex",
        mO = "invalidateSize",
        pO = "removeBinding",
        EO = "onBindingPropertyChange",
        xO = "propertyType",
        TO = "addChild",
        wO = "initBindingProperties",
        OO = "bindingProperties",
        IO = "addBinding",
        AO = "initialize",
        SO = "$selectionShadowOffsetX",
        CO = "$shadowOffsetY",
        kO = "UI_BOUNDS_GROW",
        LO = "$invalidateBounds",
        RO = "bodyChanged",
        DO = "$renderColor",
        MO = "$renderColorBlendMode",
        PO = "$shadowBlur",
        NO = "$shadowOffsetX",
        jO = "selected",
        BO = "body",
        zO = "onDataChanged",
        $O = "$invalidateScale",
        GO = "$invalidateFillGradient",
        FO = "generatorGradient",
        YO = "$fillGradient",
        qO = "$lineWidth",
        HO = "$fillColor",
        UO = "Scale",
        WO = "FillGradient",
        XO = "ALIGN_POSITION",
        VO = "setMeasuredBounds",
        KO = "$size",
        ZO = "Font",
        JO = "$invalidateFont",
        QO = "$font",
        tI = "FONT_FAMILY",
        iI = "pathBounds",
        eI = "$invalidateFromArrow",
        nI = "$invalidateToArrow",
        sI = "$outline",
        rI = "fromArrowLocation",
        hI = "$fromArrow",
        aI = "$fromArrowShape",
        oI = "fromArrowStyles",
        _I = "Gradient",
        fI = "$toArrow",
        uI = "$toArrowShape",
        cI = "$toArrowOffset",
        dI = "$toArrowSize",
        lI = "toArrowStyles",
        vI = "ArrowStroke",
        bI = "ArrowStrokeStyle",
        gI = "ArrowStyles",
        yI = "ArrowLineDash",
        mI = "ArrowLineDashOffset",
        pI = "ArrowFillColor",
        EI = "ArrowFillGradient",
        xI = "ArrowLineCap",
        TI = "ArrowLineJoin",
        wI = "ArrowOutline",
        OI = "ArrowOutlineStyle",
        II = "toArrowLocation",
        AI = "FromArrow",
        SI = "ToArrow",
        CI = "canBind",
        kI = "getYOffset",
        LI = "isPositiveOrder",
        RI = "getBundleLabel",
        DI = "editable",
        MI = "validatePoints",
        PI = "checkBundleLabel",
        NI = "drawReferenceLine",
        jI = "EDGE_TYPE_ORTHOGONAL_HORIZONTAL",
        BI = "0 0",
        zI = ".Q-Graph",
        $I = "text-align: left; outline: none;-webkit-tap-highlight-color:rgba(0,0,0,0);user-select: none",
        GI = "forEachReverse",
        FI = "0",
        YI = "originAtCenter",
        qI = "sort",
        HI = "addRect",
        UI = "uiId",
        WI = "getContext",
        XI = "LABEL_POINTER_WIDTH",
        VI = "LABEL_BORDER",
        KI = "#555555",
        ZI = "ARROW_SIZE",
        JI = "EDGE_EXTEND",
        QI = "EDGE_BUNDLE_GAP",
        tA = "EDGE_BUNDLE_LABEL_ANCHOR_POSITION",
        iA = "#075bc5",
        eA = "SHAPE_STROKE",
        nA = "#2898E0",
        sA = "navigation.scrollbar",
        rA = "NAVIGATION_NONE",
        hA = "navigation.none",
        aA = "navigation.button",
        oA = "NAVIGATION_TYPE",
        _A = "NAVIGATION_SCROLLBAR",
        fA = "invalidateRender",
        uA = "KIND_ADD",
        cA = "_kn",
        dA = "getIndexById",
        lA = "UI '",
        vA = "' not found",
        bA = "isBundleEnabled",
        gA = "reverseExpanded",
        yA = "NAVIGATION_BUTTON",
        mA = "element.bounds",
        pA = "resize",
        EA = "updateViewport",
        xA = "ondragover",
        TA = "dataTransfer",
        wA = "getData",
        OA = "text",
        IA = "getDropInfo",
        AA = "dropAction",
        SA = "stopEvent",
        CA = "Node",
        kA = "Text",
        LA = "createText",
        RA = "ShapeNode",
        DA = "createGroup",
        MA = "shiftKey",
        PA = "properties",
        NA = "styles",
        jA = "ELEMENT_CREATED",
        BA = "onInteractionEvent",
        zA = "resizable",
        $A = "linkable",
        GA = "getDefaultStyle",
        FA = "callLater",
        YA = "centerTo",
        qA = "scaleStep",
        HA = "focus",
        UA = "scrollTo",
        WA = "unselect",
        XA = "reverseSelect",
        VA = "unSelectAll",
        KA = "addCustomInteraction",
        ZA = "tooltip",
        JA = "_onresize",
        QA = "innerHTML",
        tS = "Delete Elements - ",
        iS = "ELEMENT_REMOVED",
        eS = "Shape",
        nS = "onElementCreated",
        sS = "Line",
        rS = "Styles",
        hS = "createEdge",
        aS = "Edge",
        oS = "edgeUIClass",
        _S = "interactionProperties",
        fS = "onLabelEdit",
        uS = "allowEmptyLabel",
        cS = "Label Can't Empty",
        dS = "interactionMode",
        lS = "agentEdge",
        vS = "+",
        bS = "bindableEdges",
        gS = "toLogical",
        yS = "cursor",
        mS = "removeListener",
        pS = "propertyChangeDispatcher",
        ES = "visible",
        xS = "GROUP_TYPE_ELLIPSE",
        TS = "invalidateFlag",
        wS = "_$z",
        OS = "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAABQAAAAoCAYAAAD+MdrbAAAAGXRFW…vO6jeHvWPz6tZHy2mxfohdMw3KHqTvNZ0sQYzv2df+CTAAM91P5i8bXigAAAAASUVORK5CYII=",
        IS = "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAACgAAAAUCAYAAAD/Rn+7AAAAGXRFW…NqTvl/MF9mTD6aEjJa9kbtUc0Z//Dh+LdM3v2v9lOAAQDlEZVA4N7FygAAAABJRU5ErkJggg==",
        AS = "10px",
        SS = "opacity 0.2s ease-in",
        CS = "relative",
        kS = "block",
        LS = ".Q-Graph-Nav img",
        RS = "opacity:1;background-color: rgba(0, 0, 0, 0.5)",
        DS = ".Q-Graph-Nav",
        MS = "opacity:0;",
        PS = "transition",
        NS = ":opacity 3s cubic-bezier(0.8, 0, 0.8, 1)",
        jS = ".Q-Graph-Nav:hover",
        BS = "opacity:1;",
        zS = ":opacity 0.3s linear",
        $S = "viewportBounds",
        GS = ".Q-Graph-ScrollBar",
        FS = "position: absolute;box-sizing: border-box;box-shadow: #FFF 0px 0px 1px; background-color: rgba(120,120,120,0.3);border-radius: 4px;margin: 1px;",
        YS = "margin-bottom: 8px;",
        qS = "margin-right: 8px;",
        HS = ".Q-Graph-ScrollPane",
        US = ":opacity 3s cubic-bezier(0.8, 0, 0.8, 1);",
        WS = "Q-Graph-ScrollPane",
        XS = "isH",
        VS = "Both",
        KS = "visibility",
        ZS = "removeById",
        JS = "ANIMATION_MAXTIME",
        QS = "easeOutStrong",
        tC = "animationType",
        iC = "ANIMATION_TYPE",
        eC = "INTERACTION_HANDLER_SIZE_TOUCH",
        nC = "INTERACTION_ROTATE_HANDLER_SIZE_DESKTOP",
        sC = "element",
        rC = "removeDrawable",
        hC = "styleDraw",
        aC = "addDrawable",
        oC = "start",
        _C = "doDraw",
        fC = "SEGMENT_QUAD_TO",
        uC = "createEdgeByInteraction",
        cC = "finish",
        dC = "currentPoint",
        lC = "SEGMENT_MOVE_TO",
        vC = "createShapeByInteraction",
        bC = "createLineByInteraction",
        gC = "CreateLineInteraction",
        yC = "EdgeUI",
        mC = "bodyBounds",
        pC = "canLinkFrom",
        EC = "canLinkTo",
        xC = "responded",
        TC = "EDGE_LINE_DASH_OFFSET",
        wC = "CreateSimpleEdgeInteraction",
        OC = "textarea",
        IC = "Q-LabelEditor",
        AC = "solid #08E 1px",
        SC = "5px",
        CC = "boxShadow",
        kC = "onkeydown",
        LC = "onkeypress",
        RC = "keyCode",
        DC = "altKey",
        MC = "onValueChange",
        PC = "stopEdit",
        NC = "onSizeChange",
        jC = "stopEditWhenClickOnWindow",
        BC = "mousedown",
        zC = "setText",
        $C = "isEditing",
        GC = "labelEditor",
        FC = "upSubNetwork",
        YC = "enableDoubleClickToOverview",
        qC = "isEditable",
        HC = "toCanvas",
        UC = "removeSelectionByInteraction",
        WC = "open",
        XC = "export image - ",
        VC = " x ",
        KC = "currentDraggingElement",
        ZC = "isSelected",
        JC = "isMovable",
        QC = "draggingElements",
        tk = "ELEMENT_MOVE_START",
        ik = "beforeInteractionEvent",
        ek = "ELEMENT_MOVING",
        nk = "datas",
        sk = "moveElements",
        rk = "get",
        hk = "linkedWith",
        ak = "step",
        ok = "enableInertia",
        _k = "drawLineId",
        fk = "#555",
        uk = "pathSegments",
        ck = "point",
        dk = "SELECT_START",
        lk = "SELECTION_RECTANGLE_STROKE_COLOR",
        vk = "SELECTION_RECTANGLE_FILL_COLOR",
        bk = "SELECTION_RECTANGLE_STROKE",
        gk = "isSelectable",
        yk = "nwse-resize",
        mk = "ns-resize",
        pk = "RIGHT_TOP",
        Ek = "ew-resize",
        xk = "rgba(0, 255, 0, 1)",
        Tk = "#FF0",
        wk = "isResizable",
        Ok = "RESIZE_START",
        Ik = "SEGMENT_CLOSE",
        Ak = "setLocation",
        Sk = "RESIZING",
        Ck = "sendToTop",
        kk = "setSelection",
        Lk = "SELECT",
        Rk = "TOOLTIP_DELAY",
        Dk = "Q-Tooltip",
        Mk = "css",
        Pk = "#FFFFCA",
        Nk = "1px solid #D9D9D9",
        jk = "2px 4px",
        Bk = "getTooltip",
        zk = "<br>",
        $k = "textContent",
        Gk = "createFunction",
        Fk = "TOOLTIP_DURATION",
        Yk = "enableTooltip",
        qk = "enableWheelZoom",
        Hk = "element.move.start",
        Uk = "element.moving",
        Wk = "ELEMENT_MOVE_END",
        Xk = "element.move.end",
        Vk = "element.created",
        Kk = "element.removed",
        Zk = "POINT_MOVE_START",
        Jk = "point.move.start",
        Qk = "point.moving",
        tL = "POINT_MOVE_END",
        iL = "point.move.end",
        eL = "resize.start",
        nL = "resizing",
        sL = "resize.end",
        rL = "EDGE_BUNDLE",
        hL = "edge.bundle",
        aL = "select.start",
        oL = "select.between",
        _L = "SELECT_END",
        fL = "select.end",
        uL = "LONG_CLICK",
        cL = "long.click",
        dL = "currentInteractionMode",
        lL = "getInteractionInstances",
        vL = "registerInteractions",
        bL = "view",
        gL = "INTERACTION_MODE_SELECTION",
        yL = "selection",
        mL = "zoomin",
        pL = "INTERACTION_MODE_ZOOMOUT",
        EL = "zoomout",
        xL = "create.simple.edge",
        TL = "INTERACTION_MODE_CREATE_EDGE",
        wL = "create.edge",
        OL = "create.shape",
        IL = "create.line",
        AL = "INTERACTION_MODE_CREATE_SIMPLE_EDGE",
        SL = "INTERACTION_MODE_ZOOMIN",
        CL = "PanInteraction",
        kL = "DoubleClickInteraction",
        LL = "TooltipInteraction",
        RL = "Layouter",
        DL = "animate",
        ML = "byAnimate",
        PL = "getLayoutResult",
        NL = "DIRECTION_CENTER",
        jL = "DIRECTION_TOP",
        BL = "even",
        zL = "two.side",
        $L = "even.h",
        GL = "even.v",
        FL = "LAYOUT_TYPE_EVEN",
        YL = "LAYOUT_TYPE_TWO_SIDE",
        qL = "defaultSize",
        HL = "getNodeSize",
        UL = "vGap",
        WL = "parentChildrenDirection",
        XL = "layoutType",
        VL = "doLayout",
        KL = "parentBounds",
        ZL = "node",
        JL = "_n0v",
        QL = "_dv",
        tR = "hostDY",
        iR = "hostDX",
        eR = "nodeY",
        nR = "nodeX",
        sR = "layoutReverse",
        rR = "layoutDatas",
        hR = "timer",
        aR = "resetLayoutDatas",
        oR = "nodes",
        _R = "invalidateLayoutDatas",
        fR = "onstop",
        uR = "stop",
        cR = "gap",
        dR = "BalloonLayouter",
        lR = "proportional",
        vR = "regular",
        bR = "uniform",
        gR = "variable",
        yR = "ANGLE_SPACING_PROPORTIONAL",
        mR = "ANGLE_SPACING_REGULAR",
        pR = "RADIUS_MODE_UNIFORM",
        ER = "layouter",
        xR = "node1",
        TR = "node2",
        wR = "EDGE_BUNDLE_EXPANDED",
        OR = "stack",
        IR = "popIdx",
        AR = "quads",
        SR = "massX",
        CR = "pop",
        kR = "mass",
        LR = "isInternal",
        RR = "massY",
        DR = "MIN_VALUE",
        MR = "elastic",
        PR = "layoutElasticity",
        NR = "layoutMass",
        jR = "edges",
        BR = "groups",
        zR = "update",
        $R = "minEnergy",
        GR = "currentEnergy",
        FR = "timeStep",
        YR = "duration",
        qR = "hasInEdge",
        HR = "forEachByTopoDepthFirstSearch",
        UR = "forEachByTopoBreadthFirstSearch",
        WR = "isIE",
        XR = "isWebkit",
        VR = "isFirefox",
        KR = "DefaultStyles",
        ZR = "Defaults",
        JR = "Consts",
        QR = "NodeUI",
        tD = "LabelUI",
        iD = "ImageUI",
        eD = "Path",
        nD = "InteractionEvent",
        sD = "TreeLayouter",
        rD = "Qunee for HTML5",
        hD = "2.0",
        aD = "2.0 beta",
        oD = "Qunee - Diagramming Components for HTML5/Canvas",
        _D = "IDrawable",
        fD = "19/5/2015",
        uD = 0;
    if (t.navigator) {
        var cD = navigator[rc],
            dD = /opera/i [eo](cD),
            lD = !dD && /msie/i.test(cD),
            vD = /rv:11.0/i.test(cD);
        if (vD && (lD = !0), /msie\s[6,7,8]/i.test(cD)) throw new Error("your browser is not supported");
        var bD = /webkit|khtml/i.test(cD),
            gD = !bD && /gecko/i [eo](cD),
            yD = /firefox\//i [eo](cD),
            mD = /Chrome\//i.test(cD),
            pD = !mD && /Safari\//i.test(cD),
            ED = /Macintosh;/i.test(cD),
            xD = /(iPad|iPhone|iPod)/g.test(cD),
            TD = /Android/g.test(cD),
            wD = /Windows Phone/g.test(cD),
            OD = (xD || TD || wD) && hc in t,
            ID = cD[ac](/AppleWebKit\/([0-9\.]*)/);
        if (ID && ID[jr] > 1) var AD = parseFloat(ID[1]);
        if (TD && (parseFloat(cD.match(/Android\s([0-9\.]*)/)[1]), AD && 534.3 >= AD)) var SD = !0
    }
    t[qr] || (t.requestAnimationFrame = t[oc] || t.mozRequestAnimationFrame || t.oRequestAnimationFrame || t[_c] ||
            function(i) {
                return t[fc](function() {
                        i()
                    },
                    1e3 / 60)
            }),
        t.cancelAnimationFrame || (t.cancelAnimationFrame = t.webkitCancelAnimationFrame || t.mozCancelAnimationFrame || t[uc] || t.msCancelAnimationFrame ||
            function(i) {
                return t.clearTimeout(i)
            });
    var CD = {
        SELECTION_TOLERANCE: 2,
        DOUBLE_BUFFER: e,
        LABEL_COLOR: cc
    };
    Z(CD, {
        FONT_STYLE: {
            get: function() {
                return this._fontStyle || (this._fontStyle = dc)
            },
            set: function(t) {
                this._fontStyle != t && (this._fontStyle = t, this._fontChanged = !0)
            }
        },
        FONT_SIZE: {
            get: function() {
                return this._fontSize || (this._fontSize = 12)
            },
            set: function(t) {
                this._fontSize != t && (this._fontSize = t, this._fontChanged = !0)
            }
        },
        FONT_FAMILY: {
            get: function() {
                return this._fontFamily || (this._fontFamily = "Verdana,helvetica,arial,sans-serif")
            },
            set: function(t) {
                this._fontFamily != t && (this._fontFamily = t, this._fontChanged = !0)
            }
        },
        FONT: {
            get: function() {
                return (this._fontChanged || this._fontChanged === e) && (this._fontChanged = !1, this._font = this.FONT_STYLE + Vr + this[lc] + vc + this.FONT_FAMILY),
                    this._font
            }
        }
    });
    var kD = function() {};
    kD.prototype = {
        _mo: 0,
        _mp: 0,
        _kf: !0,
        _kg: 1,
        _es: function(t, i, e) {
            var n = this._mxq(i),
                s = this._mxp(e),
                r = t * n,
                h = t * s;
            return this._96(t, i - r, e - h)
        },
        _mxq: function(t) {
            return (t - this._mo) / this._kg
        },
        _mxp: function(t) {
            return (t - this._mp) / this._kg
        },
        _d2: function(t, i) {
            return this._96(this._kg, this._mo + t, this._mp + i)
        },
        _96: function(t, i, e) {
            return this._kg == t && this._mo == i && this._mp == e ? !1 : (this._kf && (1 != this.ratio || 2 != this.ratio ? (i = Math.round(i * this.ratio) / this.ratio, e = Math.round(e * this.ratio) / this[wa]) : (i = Math[Ha](i), e = Math.round(e))), this._mo = i, this._mp = e, this._kg = t, void(this._3i && this._3i()))
        },
        _fk: function() {
            return {
                a: this._kg,
                b: 0,
                c: 0,
                d: this._kg,
                e: this._mo,
                f: this._mp
            }
        },
        toString: function() {
            return bc + z(this._kg) + gc + z(this._kg) + bh + z(this._mo) + bh + z(this._mp) + gh
        },
        _fl: function(t) {
            di(t, uu, this.toString())
        }
    };
    var LD = function(t) {
        this._im = [],
            this._la = {},
            t && this.add(t)
    };
    LD[ah] = {
            _im: null,
            _la: null,
            get: function(t) {
                return this[yc](t)
            },
            getById: function(t) {
                return this._la[t]
            },
            getByIndex: function(t) {
                return this._im[t]
            },
            forEach: function(t, i, e) {
                return l(this._im, t, i, e)
            },
            forEachReverse: function(t, i, e) {
                return b(this._im, t, i, e)
            },
            size: function() {
                return this._im[jr]
            },
            contains: function(t) {
                return this.containsById(t.id)
            },
            containsById: function(t) {
                return this._la.hasOwnProperty(t)
            },
            setIndex: function(t, i) {
                var e = this._im[Kr](t);
                if (0 > e) throw new Error(_a + t.id + mc);
                return e == i ? !1 : (this._im[Gr](e, 1), this._im[Gr](i, 0, t), !0)
            },
            setIndexAfter: function(t, i) {
                var e = this._im.indexOf(t);
                if (0 > e) throw new Error(_a + t.id + mc);
                return e == i ? i : e == i + 1 ? i + 1 : (e > i && (i += 1), this._im.splice(e, 1), this._im[Gr](i, 0, t), i)
            },
            setIndexBefore: function(t, i) {
                var e = this._im[Kr](t);
                if (0 > e) throw new Error(_a + t.id + mc);
                return e == i ? i : e == i - 1 ? i - 1 : (i > e && (i -= 1), this._im.splice(e, 1), this._im.splice(i, 0, t), i)
            },
            indexOf: function(t) {
                return this._im[Kr](t)
            },
            getIndexById: function(t) {
                var i = this[pc](t);
                return i ? this._im[Kr](i) : -1
            },
            add: function(t, i) {
                return C(t) ? this._f3(t, i) : this._k5(t, i)
            },
            addFirst: function(t) {
                return this[Lh](t, 0)
            },
            _f3: function(t, i) {
                if (0 == t.length) return !1;
                var n = !1,
                    s = i >= 0;
                t = t._im || t;
                for (var r = 0, h = t[jr]; h > r; r++) {
                    var a = t[r];
                    null !== a && a !== e && this._k5(a, i, !0) && (n = !0, s && i++)
                }
                return n
            },
            _k5: function(t, i) {
                var n = t.id;
                return n === e || this.containsById(n) ? !1 : (y(this._im, t, i), this._la[n] = t, t)
            },
            remove: function(t) {
                return C(t) ? this._myc(t) : t.id ? this._fx(t.id, t) : this.removeById(t)
            },
            _myc: function(t) {
                if (0 == t.length) return !1;
                var i = !1;
                t = t._im || t;
                for (var n = 0, s = t[jr]; s > n; n++) {
                    var r = t[n];
                    if (null !== r && r !== e) {
                        r.id === e && (r = this._la[r]);
                        var h = r.id;
                        this._fx(h, r, !0) && (i = !0)
                    }
                }
                return i
            },
            _fx: function(t, i) {
                return t !== e && this.containsById(t) ? ((null === i || i === e) && (i = this.getById(t)), delete this._la[t], m(this._im, i), !0) : !1
            },
            removeById: function(t) {
                var i = this._la[t];
                return i ? this._fx(t, i) : !1
            },
            set: function(t) {
                if (!t || 0 == t) return void this[Ec]();
                if (this.isEmpty() || !C(t)) return this[Ec](),
                    this[Lh](t);
                var i = [],
                    e = {},
                    n = 0;
                if (l(t,
                        function(t) {
                            this._la[t.id] ? (e[t.id] = t, n++) : i[Yr](t)
                        },
                        this), n != this[jr]) {
                    var s = [];
                    this[d_](function(t) {
                                e[t.id] || s.push(t)
                            },
                            this),
                        s[jr] && this._myc(s)
                }
                return i.length && this._f3(i), !0
            },
            clear: function() {
                return this[xc]() ? !1 : (this._im.length = 0, this._la = {}, !0)
            },
            toDatas: function() {
                return this._im.slice(0)
            },
            isEmpty: function() {
                return 0 == this._im[jr]
            },
            valueOf: function() {
                return this._im[jr]
            },
            clone: function(t) {
                var i = new LD;
                return i.add(t ? g(this._im) : this.toDatas()),
                    i
            }
        },
        Z(LD[ah], {
            datas: {
                get: function() {
                    return this._im
                }
            },
            random: {
                get: function() {
                    return this._im && this._im[jr] ? this._im[D(this._im[jr])] : null
                }
            },
            length: {
                get: function() {
                    return this._im ? this._im.length : 0
                }
            }
        });
    var RD = (2 * Math.PI, .5 * Math.PI),
        DD = function(t, i) {
            i = i.toUpperCase();
            for (var e = lD ? t.firstChild : t[Tc]; e && (1 != e.nodeType || e[wc] && e[wc].toUpperCase() != i);) e = lD ? e.nextSibling : e.nextElementSibling;
            return e && 1 == e.nodeType && e[wc] && e.tagName[Oc]() == i ? e : null
        },
        MD = function(t, i, e) {
            t instanceof MD && (i = t.y, t = t.x, e = t.rotate),
                this[Vo](t, i, e)
        },
        PD = function(t, i, e, n) {
            var s = t - e,
                r = i - n;
            return Math.sqrt(s * s + r * r)
        };
    MD.prototype = {
            x: 0,
            y: 0,
            rotate: e,
            set: function(t, i, e) {
                this.x = t || 0,
                    this.y = i || 0,
                    this.rotate = e || 0
            },
            negate: function() {
                this.x = -this.x,
                    this.y = -this.y
            },
            offset: function(t, i) {
                this.x += t,
                    this.y += i
            },
            equals: function(t) {
                return this.x == t.x && this.y == t.y
            },
            distanceTo: function(t) {
                return PD(this.x, this.y, t.x, t.y)
            },
            toString: function() {
                return Ic + this.x + Ac + this.y + gh
            },
            clone: function() {
                return new MD(this.x, this.y)
            }
        },
        Object.defineProperty(MD[ah], Sc, {
            get: function() {
                return Math[$a](this.x * this.x + this.y * this.y)
            }
        });
    var ND = function(t, i, n, s) {
        t !== e && this._md(t, i, n, s)
    };
    ND.prototype = {
        _mv: null,
        _mr: null,
        _ms: null,
        _mt: null,
        _my: null,
        _mx: null,
        _n0: 1,
        _md: function(t, i, e, n) {
            this._mv = t,
                this._mr = i,
                this._ms = e,
                this._mt = n,
                t == e ? (this._my = -1, this._n0 = 0, this._mx = t) : (this._my = (i - n) / (t - e), this._mx = i - this._my * t, this._n0 = 1),
                this._kr = Math[Ih](this._mt - this._mr, this._ms - this._mv),
                this._n0os = Math.cos(this._kr),
                this._sin = Math[pa](this._kr)
        },
        _n09: function(t) {
            return 0 == this._n0 ? Number[Cc] : this._my * t + this._mx
        },
        _n0f: function(t) {
            return 0 == this._my ? Number.NaN : (t - this._mx) / this._my
        },
        _$j: function(t) {
            var i,
                e,
                n,
                s,
                r,
                h = t[0],
                a = t[2],
                o = t[4],
                _ = t[1],
                f = t[3],
                u = t[5],
                c = this._my,
                d = this._mx,
                l = this._n0;
            if (0 == l ? (n = Math[$a]((-c * c * h - c * d) * o + c * c * a * a + 2 * c * d * a - c * d * h), s = -c * a + c * h, r = c * o - 2 * c * a + c * h) : (n = Math.sqrt((-_ + c * h + d) * u + f * f + (-2 * c * a - 2 * d) * f + (c * o + d) * _ + (-c * c * h - c * d) * o + c * c * a * a + 2 * c * d * a - c * d * h), s = -f + _ + c * a - c * h, r = u - 2 * f + _ - c * o + 2 * c * a - c * h), 0 != r) {
                i = (n + s) / r,
                    e = (-n + s) / r;
                var v,
                    b;
                return i >= 0 && 1 >= i && (v = Yi(i, t)),
                    e >= 0 && 1 >= e && (b = Yi(e, t)),
                    v && b ? [v, b] : v ? v : b ? b : void 0
            }
        },
        _3y: function(t, i, e) {
            if (this._my == t._my || 0 == this._n0 && 0 == t._n0) return null;
            var n,
                s;
            if (n = 0 == this._n0 ? this._mx : 0 == t._n0 ? t._mx : (t._mx - this._mx) / (this._my - t._my), s = 0 == this._my ? this._mx : 0 == t._my ? t._mx : this._n0 ? this._my * n + this._mx : t._my * n + t._mx, !i) return {
                x: n,
                y: s
            };
            var r,
                h,
                a;
            if (e) r = -i / 2,
                h = -r;
            else {
                r = -PD(this._mv, this._mr, n, s),
                    h = PD(this._ms, this._mt, n, s);
                var o = -r + h;
                if (o > i) {
                    var _ = i / o;
                    r *= _,
                        h *= _
                } else a = (i - o) / 2
            }
            var f = this._7j(n, s, r),
                u = this._7j(n, s, h);
            return a && (f._rest = a, u._rest = a), [f, u]
        },
        _7j: function(t, i, e) {
            return 0 == this._n0 ? {
                x: t,
                y: i + e
            } : {
                x: t + e * this._n0os,
                y: i + e * this._sin
            }
        }
    };
    var jD = function(t, i) {
        this.width = t,
            this[Fa] = i
    };
    jD.prototype = {
        width: 0,
        height: 0,
        isEmpty: function() {
            return this[xa] <= 0 || this.height <= 0
        },
        clone: function() {
            return new jD(this.width, this.height)
        },
        toString: function() {
            return kc + this.width + Ac + this[Fa] + gh
        }
    };
    var BD = function(t, i, n, s) {
        n === e && (n = -1),
            s === e && (s = -1),
            this.x = t || 0,
            this.y = i || 0,
            this[xa] = n,
            this.height = s
    };
    BD.prototype = {
            x: 0,
            y: 0,
            width: -1,
            height: -1,
            setByRect: function(t) {
                this.x = t.x || 0,
                    this.y = t.y || 0,
                    this[xa] = t[xa] || 0,
                    this.height = t[Fa] || 0
            },
            set: function(t, i, e, n) {
                this.x = t || 0,
                    this.y = i || 0,
                    this[xa] = e || 0,
                    this.height = n || 0
            },
            offset: function(t, i) {
                this.x += t,
                    this.y += i
            },
            contains: function(t, i) {
                return t instanceof BD ? ai(this.x, this.y, this.width, this.height, t.x, t.y, t.width, t.height) : t >= this.x && t <= this.x + this.width && i >= this.y && i <= this.y + this.height
            },
            intersectsPoint: function(t, i, e) {
                return this[xa] <= 0 && this.height <= 0 ? !1 : e ? this.intersectsRect(t - e, i - e, 2 * e, 2 * e) : t >= this.x && t <= this.x + this[xa] && i >= this.y && i <= this.y + this.height
            },
            intersectsRect: function(t, i, e, n) {
                return ri(this.x, this.y, this.width, this.height, t, i, e, n)
            },
            intersects: function(t, i) {
                return t instanceof BD ? this.intersectsRect(t.x, t.y, t.width, t.height) : this.intersectsPoint(t, i)
            },
            intersection: function(t, i, e, n) {
                var s = this.x,
                    r = this.y,
                    h = s;
                h += this.width;
                var a = r;
                a += this[Fa];
                var o = t;
                o += e;
                var _ = i;
                return _ += n,
                    t > s && (s = t),
                    i > r && (r = i),
                    h > o && (h = o),
                    a > _ && (a = _),
                    h -= s,
                    a -= r,
                    0 > h || 0 > a ? null : new BD(s, r, h, a)
            },
            addPoint: function(t) {
                this[Lh](t.x, t.y)
            },
            add: function(t, i) {
                if (t instanceof BD) return this.addRect(t.x, t.y, t[xa], t.height);
                if (t instanceof MD && (i = t.y, t = t.x), this[xa] < 0 || this[Fa] < 0) return this.x = t,
                    this.y = i,
                    void(this[xa] = this.height = 0);
                var e = this.x,
                    n = this.y,
                    s = this.width,
                    r = this[Fa];
                s += e,
                    r += n,
                    e > t && (e = t),
                    n > i && (n = i),
                    t > s && (s = t),
                    i > r && (r = i),
                    s -= e,
                    r -= n,
                    s > Number[Lc] && (s = Number.MAX_VALUE),
                    r > Number.MAX_VALUE && (r = Number[Lc]),
                    this[Vo](e, n, s, r)
            },
            addRect: function(t, i, e, n) {
                var s = this[xa],
                    r = this.height;
                (0 > s || 0 > r) && this.set(t, i, e, n);
                var h = e,
                    a = n;
                if (!(0 > h || 0 > a)) {
                    var o = this.x,
                        _ = this.y;
                    s += o,
                        r += _;
                    var f = t,
                        u = i;
                    h += f,
                        a += u,
                        o > f && (o = f),
                        _ > u && (_ = u),
                        h > s && (s = h),
                        a > r && (r = a),
                        s -= o,
                        r -= _,
                        s > Number.MAX_VALUE && (s = Number.MAX_VALUE),
                        r > Number[Lc] && (r = Number.MAX_VALUE),
                        this[Vo](o, _, s, r)
                }
            },
            grow: function(t, i, e, n) {
                return I(t) ? 1 == arguments.length ? n = i = e = t || 0 : 2 == arguments[jr] ? (e = t || 0, n = i || 0) : (t = t || 0, i = i || 0, e = e || 0, n = n || 0) : (i = t.left || 0, e = t[Ah] || 0, n = t[zf] || 0, t = t[oo] || 0),
                    this.x -= i,
                    this.y -= t,
                    this.width += i + n,
                    this[Fa] += t + e,
                    this
            },
            isEmpty: function() {
                return this[xa] <= 0 && this[Fa] <= 0
            },
            toString: function() {
                return this.x + Rc + this.y + Rc + this[xa] + Rc + this[Fa]
            },
            union: function(t) {
                var i = this.width,
                    e = this.height;
                if (0 > i || 0 > e) return new BD(t.x, t.y, t.width, t.height);
                var n = t[xa],
                    s = t.height;
                if (0 > n || 0 > s) return new BD(this.x, this.y, this[xa], this.height);
                var r = this.x,
                    h = this.y;
                i += r,
                    e += h;
                var a = t.x,
                    o = t.y;
                return n += a,
                    s += o,
                    r > a && (r = a),
                    h > o && (h = o),
                    n > i && (i = n),
                    s > e && (e = s),
                    i -= r,
                    e -= h,
                    i > Number[Lc] && (i = Number.MAX_VALUE),
                    e > Number[Lc] && (e = Number[Lc]),
                    new BD(r, h, i, e)
            },
            clear: function() {
                this[Vo](0, 0, -1, -1)
            },
            equals: function(t) {
                return this.x == t.x && this.y == t.y && this[xa] == t[xa] && this.height == t[Fa]
            },
            clone: function(t, i) {
                return new BD(this.x + (t || 0), this.y + (i || 0), this[xa], this[Fa])
            },
            getIntersectionPoint: function(t, i, e, n) {
                return si(this, t, i, e, n)
            }
        },
        N(BD, jD),
        Z(BD[ah], {
            bottom: {
                get: function() {
                    return this.y + this[Fa]
                }
            },
            right: {
                get: function() {
                    return this.x + this[xa]
                }
            },
            cx: {
                get: function() {
                    return this.x + this.width / 2
                }
            },
            cy: {
                get: function() {
                    return this.y + this[Fa] / 2
                }
            },
            center: {
                get: function() {
                    return new MD(this.cx, this.cy)
                }
            }
        });
    var zD = function(t, i, e, n) {
        1 == arguments[jr] ? i = e = n = t : 2 == arguments.length && (e = t, n = i),
            this[Vo](t, i, e, n)
    };
    zD[ah] = {
        top: 0,
        bottom: 0,
        left: 0,
        right: 0,
        set: function(t, i, e, n) {
            this[oo] = t || 0,
                this[_o] = i || 0,
                this.bottom = e || 0,
                this.right = n || 0
        },
        clone: function() {
            return new zD(this.top, this.left, this[Ah], this.right)
        },
        equals: function(t) {
            return t && this[oo] == t.top && this.bottom == t.bottom && this.left == t.left && this.right == t.right
        }
    };
    var $D = function(t, i) {
        this.horizontalPosition = t,
            this.verticalPosition = i
    };
    $D[ah] = {
            verticalPosition: !1,
            horizontalPosition: !1,
            toString: function() {
                return (this[Ch] || "") + (this[kh] || "")
            }
        },
        K($D[ah], Dc, {
            get: function() {
                return (this.horizontalPosition || "") + (this.verticalPosition || "")
            }
        });
    var GD = Mc,
        FD = Pc,
        YD = Nc,
        qD = L_,
        HD = jc,
        UD = Bc;
    $D[zc] = new $D(GD, qD),
        $D.LEFT_MIDDLE = new $D(GD, HD),
        $D[$c] = new $D(GD, UD),
        $D.CENTER_TOP = new $D(FD, qD),
        $D[Gc] = new $D(FD, HD),
        $D.CENTER_BOTTOM = new $D(FD, UD),
        $D.RIGHT_TOP = new $D(YD, qD),
        $D[Fc] = new $D(YD, HD),
        $D[Yc] = new $D(YD, UD);
    var WD = [$D.LEFT_TOP, $D.LEFT_MIDDLE, $D.LEFT_BOTTOM, $D[qc], $D[Gc], $D[Hc], $D.RIGHT_TOP, $D.RIGHT_MIDDLE, $D.RIGHT_BOTTOM];
    K($D, rh, {
        get: function() {
            return WD[D(WD.length)]
        }
    });
    var XD = function(t, i, e, n, s) {
        this.set(t, i, e, n),
            this[Uc] = s
    };
    XD[ah] = {
            radius: 0,
            classify: function(t, i, e, n) {
                return i > t ? 0 : i + n > t ? 1 : e - n > t ? 2 : e > t ? 3 : 4
            },
            intersectsRect: function(t, i, e, n) {
                if (B(this, XD, Wc, arguments) === !1) return !1;
                var s = this.x,
                    r = this.y,
                    h = s + this[xa],
                    a = r + this.height,
                    o = 2 * radius,
                    _ = 2 * radius,
                    f = Math[Ga](this[xa], Math[Sh](o)) / 2,
                    u = Math[Ga](this[Fa], Math[Sh](_)) / 2,
                    c = this.classify(t, s, h, f),
                    d = this.classify(t + e, s, h, f),
                    l = this[Xc](i, r, a, u),
                    v = this.classify(i + n, r, a, u);
                return 2 == c || 2 == d || 2 == l || 2 == v ? !0 : 2 > c && d > 2 || 2 > l && v > 2 ? !0 : (t = 1 == d ? t = t + e - (s + f) : t -= h - f, i = 1 == v ? i = i + n - (r + u) : i -= a - u, t /= f, i /= u, 1 >= t * t + i * i)
            },
            intersectsPoint: function(t, i) {
                if (B(this, XD, Wo, arguments) === !1) return !1;
                var e = this.x,
                    n = this.y,
                    s = e + this[xa],
                    r = n + this.height;
                if (e > t || n > i || t >= s || i >= r) return !1;
                var h = 2 * radius,
                    a = 2 * radius,
                    o = Math.min(this.width, Math[Sh](h)) / 2,
                    _ = Math[Ga](this[Fa], Math.abs(a)) / 2;
                return t >= (e += o) && t < (e = s - o) ? !0 : i >= (n += _) && i < (n = r - _) ? !0 : (t = (t - e) / o, i = (i - n) / _, 1 >= t * t + i * i)
            },
            clone: function() {
                return new XD(this.x, this.y, this[xa], this[Fa], this[Uc])
            }
        },
        N(XD, BD);
    var VD = function(t, i, e, n) {
        this.source = t,
            this[N_] = i,
            this.kind = e,
            this.value = n
    };
    VD.prototype = {
        source: null,
        type: null,
        kind: null,
        value: null,
        toString: function() {
            return Vc + this[co] + Kc + this.type + Zc + this.kind
        }
    };
    var KD = function(t, i, e, n, s) {
        this.source = t,
            this.kind = i,
            this[Jc] = n,
            this[Nu] = e,
            this.propertyType = s
    };
    KD[ah] = {
            type: Qc,
            propertyType: null,
            toString: function() {
                return Vc + this[co] + Kc + this.type + td + this.kind + id + this.oldValue + ed + this[Nu]
            }
        },
        N(KD, VD),
        K(KD[ah], nd, {
            get: function() {
                return this[Ku]
            },
            set: function(t) {
                this.kind = t
            }
        });
    var ZD = function(t, i, e) {
        this.source = t,
            this[Jc] = t[B_],
            this[Nu] = i,
            this[sd] = e,
            this.oldValue && (this[rd] = this.oldValue[hd](t))
    };
    ZD.prototype = {
            kind: B_
        },
        N(ZD, KD);
    var JD = function(t, i) {
        this[co] = t,
            this.value = i
    };
    JD[ah][Ku] = ad,
        N(JD, KD);
    var QD = function(t, i) {
        this.source = t,
            this.value = i
    };
    QD[ah][Ku] = od,
        N(QD, KD);
    var tM = function(t, i, e, n) {
        this.source = i,
            this[Jc] = e,
            this[Nu] = n,
            this[B_] = t,
            this.child = i,
            this[rd] = e,
            this.newIndex = n
    };
    tM.prototype.kind = _d,
        N(tM, KD);
    var iM = function() {};
    iM[ah] = {
        listener: null,
        beforeEvent: function(t) {
            return null != this.listener && this.listener[fd] ? this.listener[fd](t) : !0
        },
        onEvent: function(t) {
            null != this[ud] && this.listener[cd] && this.listener.onEvent(t)
        }
    };
    var eM = function() {
            j(this, eM, arguments),
                this.events = {},
                this[dd] = []
        },
        nM = function(t, i) {
            this[ud] = t,
                this.scope = i,
                t instanceof Function ? this.onEvent = t : (this[cd] = t.onEvent, this.beforeEvent = t[fd]),
                this.equals = function(t) {
                    return t && this.listener == t.listener && this.scope == t.scope
                }
        };
    nM.prototype = {
            equals: function(t) {
                return t && this.listener == t[ud] && this.scope == t.scope
            },
            destroy: function() {
                delete this.scope,
                    delete this.listener
            }
        },
        eM[ah] = {
            listeners: null,
            _mx3: function() {
                return this[dd] && this.listeners.length > 0
            },
            _79: function(t, i) {
                return t instanceof eM ? t : new nM(t, i)
            },
            _8w: function(t, i) {
                if (t instanceof eM) return this[dd][Kr](t);
                for (var e = this[dd], n = 0, s = e[jr]; s > n; n++) {
                    var r = e[n];
                    if (r.listener == t && r.scope == i) return n
                }
                return -1
            },
            contains: function(t, i) {
                return this._8w(t, i) >= 0
            },
            addListener: function(t, i) {
                return this[iu](t, i) ? !1 : void this.listeners.push(this._79(t, i))
            },
            removeListener: function(t, i, e) {
                var n = this._8w(t, i);
                if (n >= 0) {
                    var s = this.listeners[Gr](n, 1)[0];
                    e || $(s)
                }
            },
            on: function(t, i) {
                this[ld](t, i)
            },
            un: function(t, i, e) {
                this.removeListener(t, i, e)
            },
            onEvent: function(t) {
                return this.listeners ? void l(this[dd],
                    function(i) {
                        i[cd] && (i[vd] ? i.onEvent[Br](i[vd], t) : i.onEvent(t))
                    },
                    this) : !1
            },
            beforeEvent: function(t) {
                return this.listeners ? l(this.listeners,
                    function(i) {
                        return i[fd] ? i.scope ? i.beforeEvent.call(i[vd], t) : i.beforeEvent(t) : !0
                    },
                    this) : !0
            },
            _d7: function(t) {
                return this.beforeEvent(t) === !1 ? !1 : (this[cd](t), !0)
            },
            clear: function() {
                this.listeners = []
            },
            destroy: function() {
                this[Ec]()
            }
        },
        N(eM, iM);
    var sM = {
            onEvent: function() {},
            beforeEvent: function() {}
        },
        rM = function(t, i, e, n, s) {
            this.source = t,
                this.type = bd,
                this[Ku] = i,
                this.data = e,
                this.index = n,
                this[rd] = s
        };
    rM[ah] = {
            index: -1,
            oldIndex: -1,
            toString: function() {
                return Vc + this.source + Kc + this.type + Zc + this.kind + gd + this[go] + yd + this.index + md + this.oldIndex
            }
        },
        N(rM, VD),
        rM.KIND_ADD = Lh,
        rM.KIND_REMOVE = Zr,
        rM[pd] = Ec,
        rM.KIND_INDEX_CHANGE = Ed;
    var hM = function() {
        this.id = ++uD,
            this._mxr = {}
    };
    hM[ah] = {
            _mxr: null,
            id: null,
            get: function(t) {
                return this._mxr[t]
            },
            set: function(t, i) {
                var e = this.get(t);
                if (e === i) return !1;
                var n = new KD(this, t, i, e);
                return n.propertyType = MM.PROPERTY_TYPE_CLIENT,
                    this._myk(t, i, n, this._mxr)
            },
            _myk: function(t, i, n, s) {
                return this.beforeEvent(n) === !1 ? !1 : (s || (s = this._mxr), i === e ? delete s[t] : s[t] = i, this[cd](n), !0)
            },
            remove: function(t) {
                this.set(t, null)
            },
            valueOf: function() {
                return this.id
            },
            toString: function() {
                return this.id
            },
            _d4: function(t, i) {
                if (i === e && (i = -1), this == t || t == this._iy) return !1;
                if (t && this == t._iy && !t._d4(null)) return !1;
                var n = new ZD(this, t, i);
                if (!this.beforeEvent(n)) return !1;
                var s,
                    r,
                    h = this._iy;
                return t && (s = new JD(t, this), !t[fd](s)) ? !1 : null == h || (r = new QD(h, this), h[fd](r)) ? (this._iy = t, null != t && _i(t, this, i), null != h && fi(h, this), this[cd](n), null != t && t[cd](s), null != h && h.onEvent(r), this.onParentChanged(h, t), !0) : !1
            },
            addChild: function(t, i) {
                var e = t._d4(this, i);
                return e && this.onChildAdd(t, i),
                    e
            },
            onChildAdd: function() {},
            removeChild: function(t) {
                if (!this._ez || !this._ez.contains(t)) return !1;
                var i = t._d4(null);
                return this[Dh](t),
                    i
            },
            onChildRemove: function() {},
            toChildren: function() {
                return this._ez ? this._ez.toDatas() : null
            },
            clearChildren: function() {
                if (this._ez && this._ez.length) {
                    var t = this.toChildren();
                    l(t,
                            function(t) {
                                t._d4(null)
                            },
                            this),
                        this.onChildrenClear(t)
                }
            },
            forEachChild: function(t, i) {
                return this[Nr]() ? this._ez.forEach(t, i) : !1
            },
            onChildrenClear: function() {},
            getChildIndex: function(t) {
                return this._ez && this._ez.length ? this._ez.indexOf(t) : -1
            },
            setChildIndex: function(t, i) {
                if (!this._ez || !this._ez[jr]) return !1;
                var e = this._ez[Kr](t);
                if (0 > e || e == i) return !1;
                var n = new tM(this, t, e, i);
                return this.beforeEvent(n) === !1 ? !1 : (this._ez.remove(t) && this._ez[Lh](t, i), this.onEvent(n), !0)
            },
            hasChildren: function() {
                return this._ez && this._ez.length > 0
            },
            getChildAt: function(t) {
                return null == this._ez ? null : this._ez._im[t]
            },
            isDescendantOf: function(t) {
                if (!t.hasChildren()) return !1;
                for (var i = this.parent; null != i;) {
                    if (t == i) return !0;
                    i = i[B_]
                }
                return !1
            },
            onParentChanged: function() {},
            firePropertyChangeEvent: function(t, i, e, n) {
                this[cd](new KD(this, t, i, e, n))
            }
        },
        N(hM, iM),
        Z(hM[ah], {
            childrenCount: {
                get: function() {
                    return this._ez ? this._ez[jr] : 0
                }
            },
            children: {
                get: function() {
                    return this._ez || (this._ez = new LD),
                        this._ez
                }
            },
            parent: {
                get: function() {
                    return this._iy
                },
                set: function(t) {
                    this._d4(t, -1)
                }
            },
            properties: {
                get: function() {
                    return this._mxr
                },
                set: function(t) {
                    this._mxr != t && (this._mxr = t)
                }
            }
        });
    var aM = function() {
        this._im = [],
            this._la = {},
            this._1n = new eM
    };
    aM.prototype = {
            beforeEvent: function(t) {
                return null != this._1n && this._1n[fd] ? this._1n[fd](t) : !0
            },
            onEvent: function(t) {
                return this._1n instanceof Function ? void this._1n(t) : void(null != this._1n && this._1n[cd] && this._1n.onEvent(t))
            },
            _1n: null,
            setIndex: function(t, i) {
                if (!this[iu](t)) throw new Error(_a + t[xd]() + mc);
                var e = this[Kr](t);
                if (e == i) return !1;
                var n = new rM(this, rM.KIND_INDEX_CHANGE, t, i, e);
                return this[fd](n) === !1 ? !1 : (this._im[Zr](t) >= 0 && this._im.add(i, t), this[cd](n), !0)
            },
            _f3: function(t, i) {
                if (0 == t[jr]) return !1;
                var n = !1,
                    s = i >= 0,
                    r = new rM(this, rM.KIND_ADD, t, i);
                if (this.beforeEvent(r) === !1) return !1;
                var h = [];
                t = t._im || t;
                for (var a = 0, o = t[jr]; o > a; a++) {
                    var _ = t[a];
                    null !== _ && _ !== e && this._k5(_, i, !0) && (h[Yr](_), n = !0, s && i++)
                }
                return r[go] = h,
                    this.onEvent(r),
                    n
            },
            _k5: function(t, i, e) {
                if (this[Td](t) === !1) return !1;
                if (e) return B(this, aM, wd, arguments);
                var n = new rM(this, rM.KIND_ADD, t, i);
                return this[fd](n) === !1 ? !1 : B(this, aM, wd, arguments) ? (this._k6(t, n), t) : !1
            },
            _k6: function(t, i) {
                this.onEvent(i)
            },
            _myc: function(t) {
                if (0 == t[jr]) return !1;
                var i = new rM(this, rM.KIND_REMOVE, t);
                if (this.beforeEvent(i) === !1) return !1;
                var n = [],
                    s = !1;
                t = t._im || t;
                for (var r = 0, h = t[jr]; h > r; r++) {
                    var a = t[r];
                    if (null !== a && a !== e) {
                        var o = a.id || a;
                        a.id === e && (a = null),
                            this._fx(o, a, !0) && (n.push(a), s = !0)
                    }
                }
                return i[go] = n,
                    this[cd](i),
                    s
            },
            _fx: function(t, i, e) {
                if (e) return B(this, aM, Od, arguments);
                var n = new rM(this, rM.KIND_REMOVE, i);
                return this.beforeEvent(n) === !1 ? !1 : B(this, aM, Od, arguments) ? (this.onEvent(n), !0) : !1
            },
            clear: function() {
                if (this.isEmpty()) return !1;
                var t = new rM(this, rM[pd], this[Id]());
                return this[fd](t) === !1 ? !1 : B(this, aM, Ec) ? (this[cd](t), !0) : !1
            },
            accept: function(t) {
                return this[Ad] && this[Ad](t) === !1 ? !1 : !0
            }
        },
        N(aM, LD),
        K(aM.prototype, Sd, {
            get: function() {
                return this._1n
            }
        });
    var oM = function() {
        j(this, oM, arguments),
            this.selectionChangeDispatcher = new eM,
            this._selectionModel = new _M(this),
            this._selectionModel._1n = this[Cd],
            this[kd] = new eM,
            this.dataChangeDispatcher[ld]({
                    beforeEvent: this.beforeDataPropertyChange,
                    onEvent: this.onDataPropertyChanged
                },
                this),
            this.parentChangeDispatcher = new eM,
            this[Ld] = new eM,
            this.$roots = new LD;
        var t = this;
        this.$roots[U_] = function(i, e) {
            if (!t[Rd].contains(i)) throw new Error(_a + i.id + mc);
            var n = t[Rd]._im.indexOf(i);
            if (n == e) return !1;
            t[Rd]._im.splice(n, 1),
                t[Rd]._im.splice(e, 0, i),
                t._mxkIndexFlag = !0;
            var s = new tM(t, i, n, e);
            return t._2s(s), !0
        }
    };
    oM[ah] = {
            selectionModel: null,
            selectionChangeDispatcher: null,
            dataChangeDispatcher: null,
            parentChangeDispatcher: null,
            roots: null,
            _k6: function(t, i) {
                t.listener = this[kd],
                    t[B_] || this.$roots[Lh](t),
                    this.onEvent(i)
            },
            _fx: function(t, i) {
                if (B(this, oM, Od, arguments)) {
                    if (i instanceof EN) i.disconnect();
                    else if (i instanceof xN) {
                        var e = i[Dd]();
                        this.remove(e)
                    }
                    var n = i.parent;
                    return null == n ? this.$roots[Zr](i) : (n.removeChild(i), n.__6a = !0),
                        i.hasChildren() && this.remove(i.toChildren()),
                        i[ud] = null, !0
                }
                return !1
            },
            _5l: function(t) {
                var i = t[co];
                this[iu](i) && (null == i[B_] ? this.$roots[Lh](i) : null == t.oldValue && this[Rd][Zr](i), this[Md].onEvent(t))
            },
            _2s: function(t) {
                this[Ld][cd](t)
            },
            beforeDataPropertyChange: function(t) {
                return t instanceof ZD ? this[Md][fd](t) : !0
            },
            onDataPropertyChanged: function(t) {
                return t instanceof ZD ? (this._mxkIndexFlag = !0, t[co]._mxkIndexFlag = !0, void this._5l(t)) : void(t instanceof tM && (this._mxkIndexFlag = !0, t[co]._mxkIndexFlag = !0, this._2s(t)))
            },
            toRoots: function() {
                return this[Rd].toDatas()
            },
            _eu: function(t) {
                var i,
                    e = t._iy;
                i = e ? e._ez : this[Rd];
                var n = i.indexOf(t);
                if (0 > n) throw new Error(Pd + t + "' not exist in the box");
                return 0 == n ? e : i.getByIndex(n - 1)
            },
            _ew: function(t) {
                var i,
                    e = t._iy;
                i = e ? e._ez : this.$roots;
                var n = i[Kr](t);
                if (0 > n) throw new Error(Pd + t + "' not exist in the box");
                return n == i.length - 1 ? e ? this._ew(e) : null : i[yc](n + 1)
            },
            forEachByDepthFirst: function(t, i, e) {
                return this[Rd][jr] ? r(this.$roots, t, i, e) : !1
            },
            forEachByDepthFirstReverse: function(t, i, e) {
                return this[Rd].length ? o(this[Rd], t, i, e) : !1
            },
            forEachByBreadthFirst: function(t, i) {
                return this.$roots[jr] ? u(this[Rd], t, i) : !1
            },
            forEachByBreadthFirstReverse: function(t, i) {
                return this[Rd][jr] ? c(this[Rd], t, i) : !1
            },
            clear: function() {
                return B(this, oM, Ec) ? (this.$roots[Ec](), this.selectionModel.clear(), !0) : !1
            }
        },
        N(oM, aM),
        Z(oM.prototype, {
            selectionModel: {
                get: function() {
                    return this._selectionModel
                }
            },
            roots: {
                get: function() {
                    return this.$roots
                }
            }
        });
    var _M = function(t) {
        j(this, _M),
            this[Nd] = t,
            this._mxoxChangeListener = {
                onEvent: function(t) {
                    rM[jd] == t[Ku] ? null != t.data ? this.remove(t[go]) : null != t.datas && this.remove(t.datas) : rM.KIND_CLEAR == t[Ku] && this.clear()
                }
            },
            this.box[Sd][ld](this._mxoxChangeListener, this)
    };
    _M[ah] = {
            box: null,
            isSelected: function(t) {
                return this[j_](t.id || t)
            },
            select: function(t) {
                return this.add(t)
            },
            unselect: function(t) {
                return this[Zr](t)
            },
            reverseSelect: function(t) {
                return this.contains(t) ? this[Zr](t) : this.add(t)
            },
            accept: function(t) {
                return this[Nd].contains(t)
            }
        },
        N(_M, aM);
    var fM = null,
        uM = null,
        cM = null,
        dM = function() {
            if (!i[Ra]) return function(t) {
                return t
            };
            var t = i.createElement(hu),
                n = t[Oa],
                s = {};
            return function(t) {
                if (s[t]) return s[t];
                var i = ui(t);
                return n[i] !== e || cM && n[i = ui(cM + i)] !== e ? (s[t] = i, i) : t
            }
        }(),
        lM = {};
    !
    function() {
        if (!i[Bd]) return !1;
        for (var n = i.head, s = "Webkit Moz O ms Khtml".split(Vr), r = 0; r < s.length; r++)
            if (n.style[s[r] + zd] !== e) {
                cM = ic + s[r].toLowerCase() + ic;
                break
            }
        var h = i.createElement(Oa);
        t[$d] || h.appendChild(i[Gd]("")),
            h.classList && (fM = !0),
            h[N_] = Fd,
            h.id = Yd,
            n[If](h),
            uM = h.sheet;
        var a,
            o;
        for (var _ in lM) {
            var f = lM[_];
            a = _,
                o = "";
            for (var u in f) o += dM(u) + qd + f[u] + Hd;
            li(a, o)
        }
    }();
    var vM = function(t, i, e, n, s) {
            if (s) {
                var r = function(t) {
                    r[fh].call(r[vd], t)
                };
                return r.scope = s,
                    r.handle = e,
                    t.addEventListener(i, r, n),
                    r
            }
            return t[Ud](i, e, n),
                e
        },
        bM = function(t, i, e) {
            t[Wd](i, e)
        },
        k = function(t) {
            t[ih] ? t.preventDefault() : t.returnValue = !1
        },
        L = function(t) {
            t[Xd] ? t.stopPropagation() : t[nh] || (t[nh] = !0)
        },
        R = function(t) {
            k(t),
                L(t)
        };
    CD.DOUBLE_CLICK_INTERVAL_TIME = OD ? 500 : 300,
        CD[Vd] = OD ? 1500 : 1e3;
    var gM,
        yM = ia in t ? "mousewheel" : Kd;
    gM = yM + Zd,
        OD && (gM += Jd),
        gM = gM[Xr](bh),
        wi[ah] = {
            _kd: null,
            _hn: function() {
                var t = this._lx;
                t && Ti.call(this, t)
            },
            destroy: function() {
                this._hn()
            },
            _mxf: null,
            _22: function() {
                this.__longPressTimer && (clearTimeout(this.__longPressTimer), this.__longPressTimer = null)
            },
            _df: function() {
                this.__n0ancelClick = !0,
                    this._22(),
                    this._hf(this._mxf, Qd),
                    this._mxt.clear()
            },
            _mxt: null,
            _6b: function(t) {
                var i = this._9l;
                this._9l = mi(t),
                    this._mxt.add(i, this._9l, t)
            },
            _j7: function(t) {
                this._6b(t),
                    this._hf(t, tl),
                    t[wh] && t[wh].length > 1 && this._hf(t, il)
            },
            _hi: function(t) {
                OD || this._6b(t);
                var i = this._mxt.getCurrentSpeed();
                i && (t.vx = i.x, t.vy = i.y),
                    this._hf(t, el),
                    this._mxt.clear()
            },
            _d8: function(t) {
                this._mxf && (this._22(), this._hf(t, nl), this._mxf = null, this._9l = null)
            },
            _hf: function(t, i) {
                this._listener && this._listener[i] instanceof Function && this._listener[i].call(this._listener, t, this._kd || this._lx)
            }
        };
    var mM = function(t) {
        this._k2 = t,
            j(this, mM, [t[sl]])
    };
    mM._n0urrentInteractionSupport = null,
        mM[ah] = {
            _4i: function(t) {
                this._4j(function(i) {
                    i[rl] instanceof Function && i[rl](t, this._k2)
                })
            },
            _7c: function() {
                this._4j(function(t) {
                    t[hl] instanceof Function && t.onClear(this._k2)
                })
            },
            _hn: function() {
                this._26 && this._2q(this._26),
                    this._$l && this._2q(this._$l);
                var t = this._k2[sl];
                t && Ti.call(this, t)
            },
            _k2: null,
            _26: null,
            _$l: null,
            _7f: function(t) {
                return this._26 == t ? !1 : (this._26 && this._26.length && this._2q(this._26), void(this._26 = t))
            },
            _$a: function(t) {
                this._$l || (this._$l = []),
                    this._$l.push(t)
            },
            _6: function(t) {
                this._$l && 0 != this._$l.length && m(this._$l, t)
            },
            _hf: function(t, i, e) {
                this._k2[i] instanceof Function && this._k2[i].call(this._k2, t, e),
                    this._26 && this._ga(t, i, this._26, e),
                    this._$l && this._ga(t, i, this._$l, e)
            },
            _4j: function(t) {
                this._26 && l(this._26, t, this),
                    this._$l && l(this._$l, t, this)
            },
            _ga: function(t, i, e, n) {
                if (!C(e)) return void this._9v(t, i, e, n);
                for (var s = 0; s < e.length; s++) {
                    var r = e[s];
                    this._9v(t, i, r, n)
                }
            },
            _9v: function(t, i, e, n) {
                var s = e[i];
                s && s.call(e, t, this._k2, n)
            },
            _34: function(t) {
                t[kf] instanceof Function && t.destroy.call(t, this._k2)
            },
            _2q: function(t) {
                if (!C(t)) return void this._34(t);
                for (var i = 0; i < t.length; i++) {
                    var e = t[i];
                    e && this._34(e)
                }
            }
        },
        N(mM, wi),
        Ii[ah] = {
            limitCount: 10,
            points: null,
            add: function(t, i, e) {
                var n = i[Vh] - t[Vh] || 1;
                e.interval = n;
                var s,
                    r;
                if (!e[wh]) return s = i.x - t.x,
                    r = i.y - t.y,
                    e.dx = s,
                    e.dy = r,
                    void this._k5(s, r, n);
                var h = e[wh].length;
                if (1 == h) s = e.touches[0].clientX - t.touches[0][al],
                    r = e[wh][0][Kh] - t[wh][0].clientY;
                else {
                    for (var a, o, _, f = [], u = [], c = 0, d = 0, l = 0, v = 0, b = 0, g = 0, y = 0, h = t.touches[jr]; h > y; y++) {
                        a = t.touches[y];
                        var m = a[al],
                            p = a[Kh];
                        c += m,
                            d += p,
                            y && (b = Math[ja](b, Math.sqrt((m - o) * (m - o) + (p - _) * (p - _)))),
                            o = m,
                            _ = p,
                            f.push({
                                x: m,
                                y: p
                            })
                    }
                    c /= h,
                        d /= h;
                    for (var y = 0, h = e[wh].length; h > y; y++) {
                        a = e.touches[y];
                        var m = a.clientX,
                            p = a.clientY;
                        l += m,
                            v += p,
                            y && (g = Math[ja](g, Math.sqrt((m - o) * (m - o) + (p - _) * (p - _)))),
                            o = m,
                            _ = p,
                            u[Yr]({
                                x: m,
                                y: p
                            })
                    }
                    if (l /= h, v /= h, s = l - c, r = v - d, b && g) {
                        var E = g / b;
                        e.scale && t.scale && (E = e.scale / t[Ao]),
                            e.center = {
                                x: l,
                                y: v,
                                clientX: l,
                                clientY: v
                            },
                            e[ol] = E,
                            e[_l] = t
                    }
                }
                e.dx = s,
                    e.dy = r,
                    this._k5(s, r, n)
            },
            _k5: function(t, i, e) {
                var n = {
                    interval: e,
                    dx: t,
                    dy: i
                };
                this.points.splice(0, 0, n),
                    this[aa].length > this.limitCount && this[aa].pop()
            },
            getCurrentSpeed: function() {
                if (!this.points.length) return null;
                for (var t = 0, i = 0, e = 0, n = 0, s = this[aa].length; s > n; n++) {
                    var r = this[aa][n],
                        h = r.interval;
                    if (h > 300) break;
                    if (t += r.interval, i += r.dx, e += r.dy, t > 500) break
                }
                return 0 == t || 0 == i && 0 == e ? null : {
                    x: i / t,
                    y: e / t
                }
            },
            clear: function() {
                this[aa] = []
            }
        };
    var pM,
        EM,
        xM,
        TM;
    bD ? (pM = fl, EM = ul, xM = cl, TM = dl) : gD ? (pM = ll, EM = vl, xM = bl, TM = gl) : (pM = yl, EM = yl, xM = Mu, TM = ml);
    var wM = pl,
        OM = Math.PI,
        IM = Math[w_],
        AM = Math.sin,
        SM = 1.70158,
        CM = {
            swing: function(t) {
                return -Math.cos(t * OM) / 2 + .5
            },
            easeNone: function(t) {
                return t
            },
            easeIn: function(t) {
                return t * t
            },
            easeOut: function(t) {
                return (2 - t) * t
            },
            easeBoth: function(t) {
                return (t *= 2) < 1 ? .5 * t * t : .5 * (1 - --t * (t - 2))
            },
            easeInStrong: function(t) {
                return t * t * t * t
            },
            easeOutStrong: function(t) {
                return 1 - --t * t * t * t
            },
            easeBothStrong: function(t) {
                return (t *= 2) < 1 ? .5 * t * t * t * t : .5 * (2 - (t -= 2) * t * t * t)
            },
            elasticIn: function(t) {
                var i = .3,
                    e = i / 4;
                return 0 === t || 1 === t ? t : -(IM(2, 10 * (t -= 1)) * AM(2 * (t - e) * OM / i))
            },
            elasticOut: function(t) {
                var i = .3,
                    e = i / 4;
                return 0 === t || 1 === t ? t : IM(2, -10 * t) * AM(2 * (t - e) * OM / i) + 1
            },
            elasticBoth: function(t) {
                var i = .45,
                    e = i / 4;
                return 0 === t || 2 === (t *= 2) ? t : 1 > t ? -.5 * IM(2, 10 * (t -= 1)) * AM(2 * (t - e) * OM / i) : IM(2, -10 * (t -= 1)) * AM(2 * (t - e) * OM / i) * .5 + 1
            },
            backIn: function(t) {
                return 1 === t && (t -= .001),
                    t * t * ((SM + 1) * t - SM)
            },
            backOut: function(t) {
                return (t -= 1) * t * ((SM + 1) * t + SM) + 1
            },
            backBoth: function(t) {
                return (t *= 2) < 1 ? .5 * t * t * (((SM *= 1.525) + 1) * t - SM) : .5 * ((t -= 2) * t * (((SM *= 1.525) + 1) * t + SM) + 2)
            },
            bounceIn: function(t) {
                return 1 - CM.bounceOut(1 - t)
            },
            bounceOut: function(t) {
                var i,
                    e = 7.5625;
                return i = 1 / 2.75 > t ? e * t * t : 2 / 2.75 > t ? e * (t -= 1.5 / 2.75) * t + .75 : 2.5 / 2.75 > t ? e * (t -= 2.25 / 2.75) * t + .9375 : e * (t -= 2.625 / 2.75) * t + .984375
            },
            bounceBoth: function(t) {
                return .5 > t ? .5 * CM[El](2 * t) : .5 * CM.bounceOut(2 * t - 1) + .5
            }
        },
        kM = function(t) {
            this._ij = t
        };
    kM.prototype = {
        _ij: null,
        _l2: function(t) {
            var i = Date[va]();
            this._ll();
            var e = this;
            this._requestID = requestAnimationFrame(function n() {
                var s = Date.now(),
                    r = s - i;
                return !r || e._ij && e._ij(r) !== !1 ? (i = s, void(e._requestID = requestAnimationFrame(n))) : (e._ll(), void(t instanceof Function && t[Br]()))
            })
        },
        _ll: function() {
            return this._requestID ? (t[xl](this._requestID), void delete this._requestID) : !1
        },
        _d3: function() {
            return null != this._requestID
        }
    };
    var LM = function(t, i, e, n) {
        this._onStep = t,
            this._kd = i || this,
            this._3p = n,
            e && e > 0 && (this._ie = e)
    };
    LM[ah] = {
            _ie: 1e3,
            _3p: null,
            _dm: 0,
            _ll: function() {
                return this._dm = 0,
                    this._n08 = 0,
                    B(this, LM, Tl)
            },
            _n08: 0,
            _ij: function(t) {
                if (this._dm += t, this._dm >= this._ie) return this._onStep[Br](this._kd, 1, (1 - this._n08) * this._ie, t, this._ie), !1;
                var i = this._dm / this._ie;
                return this._3p && (i = this._3p(i)),
                    this._onStep[Br](this._kd, i, (i - this._n08) * this._ie, t, this._ie) === !1 ? !1 : void(this._n08 = i)
            }
        },
        N(LM, kM);
    var RM = function(t) {
            ei(t)
        },
        DM = {
            version: wl,
            extend: N,
            doSuperConstructor: j,
            doSuper: B,
            createFunction: F,
            setClass: T,
            appendClass: w,
            removeClass: O,
            forEach: l,
            forEachReverse: b,
            isNumber: I,
            isString: A,
            isBoolean: S,
            isArray: C,
            eventPreventDefault: k,
            eventStopPropagation: L,
            stopEvent: R,
            callLater: E,
            nextFrame: x,
            forEachChild: n,
            forEachByDepthFirst: r,
            forEachByDepthFirstReverse: o,
            forEachByBreadthFirst: u,
            randomInt: D,
            randomBool: M,
            randomColor: U,
            addEventListener: vM,
            getFirstElementChildByTagName: DD
        };
    DM.isTouchSupport = OD,
        DM[Ol] = xD,
        DM.intersectsPoint = hi,
        DM.containsRect = ai,
        DM[Il] = BD,
        DM[Al] = jD,
        DM.Point = MD,
        DM.Insets = zD,
        DM.Event = VD,
        DM[Sl] = KD,
        DM.ListEvent = rM,
        DM.Handler = iM,
        DM[Cl] = eM,
        DM.Position = $D,
        DM.Data = hM,
        DM.SelectionModel = _M,
        DM[kl] = oM,
        DM[Ll] = sM,
        DM[Rl] = Ci,
        DM[Dl] = Ai,
        DM.loadJSON = Si,
        DM[Ml] = Oi,
        DM.calculateDistance = PD,
        DM[Pl] = LD,
        DM.DragSupport = wi,
        DM.alert = function(t) {
            alert(t)
        },
        DM[Nl] = function(t, i, e, n) {
            var s = prompt(t, i);
            return s != i && e ? e.call(n, s) : s
        },
        DM.confirm = function(t, i, e) {
            var n = confirm(t);
            return n && i ? i.call(e) : n
        },
        DM.addCSSRule = li;
    var MM = {
        SELECTION_TYPE_BORDER_RECT: jl,
        SELECTION_TYPE_BORDER: Bl,
        SELECTION_TYPE_SHADOW: zl,
        NS_SVG: "http://www.w3.org/2000/svg",
        PROPERTY_TYPE_ACCESSOR: 0,
        PROPERTY_TYPE_STYLE: 1,
        PROPERTY_TYPE_CLIENT: 2,
        EDGE_TYPE_DEFAULT: null,
        EDGE_TYPE_ELBOW: $l,
        EDGE_TYPE_ELBOW_HORIZONTAL: Gl,
        EDGE_TYPE_ELBOW_VERTICAL: Fl,
        EDGE_TYPE_ORTHOGONAL: Yl,
        EDGE_TYPE_ORTHOGONAL_HORIZONTAL: ql,
        EDGE_TYPE_ORTHOGONAL_VERTICAL: Hl,
        EDGE_TYPE_HORIZONTAL_VERTICAL: Ul,
        EDGE_TYPE_VERTICAL_HORIZONTAL: Wl,
        EDGE_TYPE_EXTEND_TOP: Xl,
        EDGE_TYPE_EXTEND_LEFT: Vl,
        EDGE_TYPE_EXTEND_BOTTOM: Kl,
        EDGE_TYPE_EXTEND_RIGHT: Zl,
        EDGE_TYPE_ZIGZAG: Jl,
        EDGE_CORNER_NONE: bu,
        EDGE_CORNER_ROUND: Ha,
        EDGE_CORNER_BEVEL: Ql,
        GROUP_TYPE_RECT: tv,
        GROUP_TYPE_CIRCLE: iv,
        GROUP_TYPE_ELLIPSE: ev,
        SHAPE_CIRCLE: nv,
        SHAPE_RECT: tv,
        SHAPE_ROUNDRECT: sv,
        SHAPE_STAR: rv,
        SHAPE_TRIANGLE: hv,
        SHAPE_HEXAGON: av,
        SHAPE_PENTAGON: ov,
        SHAPE_TRAPEZIUM: _v,
        SHAPE_RHOMBUS: fv,
        SHAPE_PARALLELOGRAM: uv,
        SHAPE_HEART: cv,
        SHAPE_DIAMOND: dv,
        SHAPE_CROSS: lv,
        SHAPE_ARROW_STANDARD: vv,
        SHAPE_ARROW_1: bv,
        SHAPE_ARROW_2: gv,
        SHAPE_ARROW_3: yv,
        SHAPE_ARROW_4: mv,
        SHAPE_ARROW_5: pv,
        SHAPE_ARROW_6: Ev,
        SHAPE_ARROW_7: xv,
        SHAPE_ARROW_8: Tv,
        SHAPE_ARROW_OPEN: wv
    };
    MM.LINE_CAP_TYPE_BUTT = Ov,
        MM.LINE_CAP_TYPE_ROUND = Ha,
        MM[Iv] = Av,
        MM[Sv] = Ql,
        MM.LINE_JOIN_TYPE_ROUND = Ha,
        MM.LINE_JOIN_TYPE_MITER = Cv,
        CD[kv] = MM[Lv],
        CD[Rv] = 3,
        CD[Dv] = 2,
        CD.SELECTION_SHADOW_BLUR = 7,
        CD.SELECTION_COLOR = V(3422561023),
        CD.SELECTION_TYPE = MM.SELECTION_TYPE_SHADOW,
        CD[Mv] = 10,
        CD.POINTER_WIDTH = 10,
        CD[Pv] = e,
        CD.ARROW_SIZE = 10,
        CD[Nv] = 200,
        CD.LINE_HEIGHT = 1.2;
    var PM = t.devicePixelRatio || 1;
    1 > PM && (PM = 1);
    var NM;
    DM.createCanvas = Bi;
    var jM = function(t, i, e, n) {
        var s = t - e,
            r = i - n;
        return s * s + r * r
    };
    ie[ah] = {
            equals: function(t) {
                return this.cx == t.cx && this.cy == t.cy && this.r == t.r
            }
        },
        ie._irCircle = function(t, i, e) {
            if (!e) return Qi(t, i);
            var n = jM(t.x, t.y, i.x, i.y),
                s = jM(t.x, t.y, e.x, e.y),
                r = jM(e.x, e.y, i.x, i.y);
            if (n + BM >= s + r) return Qi(t, i, 0, e);
            if (s + BM >= n + r) return Qi(t, e, 0, i);
            if (r + BM >= n + s) return Qi(i, e, 0, t);
            var h;
            Math.abs(e.y - i.y) < 1e-4 && (h = t, t = i, i = h),
                h = e.x * (t.y - i.y) + t.x * (i.y - e.y) + i.x * (-t.y + e.y);
            var a = (e.x * e.x * (t.y - i.y) + (t.x * t.x + (t.y - i.y) * (t.y - e.y)) * (i.y - e.y) + i.x * i.x * (-t.y + e.y)) / (2 * h),
                o = (i.y + e.y) / 2 - (e.x - i.x) / (e.y - i.y) * (a - (i.x + e.x) / 2);
            return new ie(a, o, PD(a, o, t.x, t.y), t, i, e)
        };
    var BM = .01,
        zM = {
            _myo: function(t, i, n, s, r) {
                var h = 0,
                    a = 0,
                    o = i._io;
                if (n = n || 0, t.x === e) {
                    var _ = t[Ch],
                        f = t.verticalPosition,
                        u = !0;
                    switch (_) {
                        case YD:
                            u = !1;
                            break;
                        case FD:
                            h += o / 2
                    }
                    switch (f) {
                        case qD:
                            a -= n / 2;
                            break;
                        case UD:
                            a += n / 2
                    }
                } else h = t.x,
                    a = t.y,
                    Math[Sh](h) > 0 && Math[Sh](h) < 1 && (h *= o);
                r && null != s && (a += s.y, h += Math.abs(s.x) < 1 ? s.x * o : s.x);
                var c = _e.call(i, h, a, u);
                return c ? (r || null == s || c[jv](s), c) : {
                    x: 0,
                    y: 0
                }
            },
            _l9: function(t, i) {
                var e = i.type,
                    n = i.points;
                switch (e) {
                    case uP:
                        t[Bv](n[0], n[1], n[2], n[3], i._r);
                        break;
                    case aP:
                        t[V_](n[0], n[1]);
                        break;
                    case oP:
                        t.lineTo(n[0], n[1]);
                        break;
                    case _P:
                        t[zv](n[0], n[1], n[2], n[3]);
                        break;
                    case fP:
                        t.bezierCurveTo(n[0], n[1], n[2], n[3], n[4], n[5]);
                        break;
                    case cP:
                        t.closePath()
                }
            },
            _63: function(t, i, e, n) {
                var s = i.type;
                if (s != aP && s != cP) {
                    var r = e.lastPoint,
                        h = i.points;
                    switch (e.type == aP && t.add(r.x, r.y), s) {
                        case uP:
                            ce(i, r.x, r.y, h[0], h[1], h[2], h[3], h[4]),
                                t.add(h[0], h[1]),
                                t.add(i._p1x, i._p1y),
                                t.add(i._p2x, i._p2y),
                                i._mxoundaryPoint1 && t.add(i._mxoundaryPoint1.x, i._mxoundaryPoint1.y),
                                i._mxoundaryPoint2 && t.add(i._mxoundaryPoint2.x, i._mxoundaryPoint2.y);
                            break;
                        case oP:
                            t.add(h[0], h[1]);
                            break;
                        case _P:
                            Hi([r.x, r.y][Ka](h), t);
                            break;
                        case fP:
                            Vi([r.x, r.y].concat(h), t);
                            break;
                        case cP:
                            n && t[Lh](n[aa][0], n.points[1])
                    }
                }
            },
            _66: function(t, i, e) {
                var n = t.type;
                if (n == aP) return 0;
                var s = i[Va],
                    r = t.points;
                switch (n == fP && 4 == r.length && (n = _P), n) {
                    case oP:
                        return PD(r[0], r[1], s.x, s.y);
                    case uP:
                        return t._io;
                    case _P:
                        var h = Ui([s.x, s.y][Ka](r));
                        return t._lf = h,
                            h(1);
                    case fP:
                        var h = Zi([s.x, s.y].concat(r));
                        return t._lf = h,
                            h(1) || Ki([s.x, s.y].concat(r));
                    case cP:
                        if (s && e) return t[aa] = e[aa],
                            PD(e.points[0], e.points[1], s.x, s.y)
                }
                return 0
            }
        },
        $M = /^data:image\/(\w+);base64,/i,
        GM = /^gif/i,
        FM = /^svg/i,
        YM = 10,
        qM = 11,
        HM = 12,
        UM = 20,
        WM = 30;
    CD.IMAGE_WIDTH = 50,
        CD.IMAGE_HEIGHT = 30,
        CD[$v] = 1e6;
    var XM = 1,
        VM = 2,
        KM = 3;
    ge[ah] = {
            _jv: 0,
            _6a: !0,
            _jx: null,
            _jm: null,
            _lp: null,
            _lf: null,
            _myx: e,
            _8r: e,
            _6l: function() {
                return this._jv == XM
            },
            getBounds: function(t) {
                return this._lf == WM ? this._lp.getBounds(t) : (this._6a && this._fa(), this)
            },
            validate: function() {
                this._6a && this._fa()
            },
            _fa: function() {
                if (this._6a = !1, this._lf == WM) return this._lp[uo](),
                    void this[jo](this._lp.bounds);
                if (this._lf == UM) return void this._91();
                if (this._jv != XM) try {
                    this._db()
                } catch (t) {
                    this._jv = KM,
                        DM[lo](t)
                }
            },
            _54: function() {
                this._d7(),
                    this._dispatcher.clear(),
                    delete this._dispatcher
            },
            _hc: function(t) {
                this._jx && this._jx.parentNode && this._jx.parentNode[Gv](this._jx),
                    this._jv = KM,
                    DM.error(Fv + this._lp),
                    this._pixels = null,
                    this._jm = null,
                    this._jx = null,
                    t !== !1 && this._54()
            },
            _db: function() {
                var t = this._lp;
                if (this._jv = XM, this._dispatcher = new eM, this._lf == HM) {
                    for (var e in xP) this[e] = xP[e];
                    return void Xe(this._lp, this, this._n0t, this._hc, this._dr)
                }
                this._jx || (lD ? (this._jx = i[Ra](ou), this._jx[Oa].visibility = vu, i.body[If](this._jx)) : this._jx = new Image),
                    this._jx.src = t, !lD && this._jx.width ? (this._jx[Yv] = this._jx.onerror = null, this._7s()) : (this._jx[Yv] = lD ?
                        function(t) {
                            setTimeout(this._7s[qv](this, t), 100)
                        }.bind(this) : this._7s.bind(this), this._jx[m_] = this._hc[qv](this))
            },
            _7s: function() {
                this._jv = VM;
                var t = this._jx.width,
                    i = this._jx.height;
                if (this._jx[Hv] && this._jx[Hv][Gv](this._jx), !t || !i) return void this._hc();
                this.width = t,
                    this.height = i;
                var e = this._d9();
                e[xa] = t,
                    e.height = i,
                    e.g[Uv](this._jx, 0, 0, t, i),
                    this._pixels = lD && this._lf == qM ? null : Te(e),
                    this._54()
            },
            _91: function() {
                var t = this._lp;
                if (!(t.draw instanceof Function)) return void this._hc(!1);
                var i = t.width || CD[Nv],
                    e = t[Fa] || CD.IMAGE_MAX_SIZE,
                    n = this._d9(),
                    s = n.g;
                t.draw(s);
                var r = s.getImageData(0, 0, i, e),
                    h = we(r[go], i, e);
                this.x = h._x,
                    this.y = h._y,
                    this.width = h._width,
                    this.height = h._height,
                    n[xa] = this.width,
                    n[Fa] = this[Fa],
                    s.putImageData(r, -this.x, -this.y),
                    this._pixels = h
            },
            _d9: function() {
                return this._jm || (this._jm = Bi())
            },
            _68: function(t, i, e, n, s, r) {
                i[Wv](),
                    i[tv](0, 0, n, s),
                    i[Xv] = r || Vv,
                    i.fill(),
                    i[Kv](),
                    i[Zv] = tu,
                    i.textBaseline = Jv,
                    i.fillStyle = Qv;
                var h = 6 * (i[Aa][wa] || 1);
                i[Pa] = tb + h + "px Verdana,helvetica,arial,sans-serif",
                    i.strokeStyle = ib,
                    i[Wa] = 1,
                    i[eb](t, n / 2 + .5, s / 2 + .5),
                    i[nb] = sb,
                    i[eb](t, n / 2 - .5, s / 2 - .5),
                    i[rb](t, n / 2, s / 2),
                    i[hb]()
            },
            draw: function(t, i, e, n, s, r) {
                if (this[xa] && this[Fa]) {
                    i = i || 1,
                        n = n || 1,
                        s = s || 1;
                    var h = this.width * n,
                        a = this[Fa] * s;
                    if (r && e[ab] && (t[ab] = e[ab], t[ob] = (e.shadowBlur || 0) * i, t[_b] = (e.shadowOffsetX || 0) * i, t[fb] = (e[fb] || 0) * i), this._jv == XM) return this._68(ub, t, i, h, a, e[cb]);
                    if (this._jv == KM) return this._68(db, t, i, h, a, e.renderColor);
                    if (this._lf == WM) return t[Ao](n, s),
                        void this._lp[io](t, i, e);
                    var o = this._f2(i, n, s);
                    return o ? ((this.x || this.y) && t[fo](this.x * n, this.y * s), t[Ao](n / o.scale, s / o[Ao]), void o._l9(t, e.renderColor, e.renderColorBlendMode)) : void this._jr(t, i, n, s, this.width * n, this.height * s)
                }
            },
            _jr: function(t, i, e, n, s, r) {
                if (this._lf == UM) return 1 != e && 1 != n && t[Ao](e, n),
                    void this._lp.draw(t);
                if (this._jx) {
                    if (!yD) return void t.drawImage(this._jx, 0, 0, s, r);
                    var e = i * s / this.width,
                        n = i * r / this.height;
                    t[Ao](1 / e, 1 / n),
                        t.drawImage(this._jx, 0, 0, s * e, r * n)
                }
            },
            _jn: null,
            _f2: function(t, i, e) {
                if (this._lf == YM || (t *= Math[ja](i, e)) <= 1) return this._defaultCache || (this._defaultCache = this._ex(this._jm || this._jx, 1)),
                    this._defaultCache;
                var n = this._jn.maxScale || 0;
                if (t = Math.ceil(t), n >= t) {
                    for (var s = t, r = this._jn[s]; !r && ++s <= n;) r = this._jn[s];
                    if (r) return r
                }
                t % 2 && t++;
                var h = this[xa] * t,
                    a = this[Fa] * t;
                if (h * a > CD[$v]) return null;
                var o = Bi(h, a);
                return (this.x || this.y) && o.g[fo](-this.x * t, -this.y * t),
                    this._jr(o.g, 1, t, t, h, a),
                    this._ex(o, t)
            },
            _ex: function(t, i) {
                var e = new bP(t, i);
                return this._jn[i] = e,
                    this._jn[lb] = i,
                    e
            },
            _h9: function(t, i, e) {
                if (this._lf == WM) return this._lp._h9.apply(this._lp, arguments);
                if (!(this._pixels || this._jx && this._jx._pixels)) return !0;
                var n = this._pixels || this._jx._pixels;
                return t -= n._iq.x,
                    i -= n._iq.y,
                    t = Math[Ha](t),
                    i = Math[Ha](i),
                    Oe(n, n._iq, t, i, e)
            },
            _d7: function() {
                this._dispatcher && this._dispatcher.onEvent(new VD(this, vb, bb, this._jx))
            },
            _9w: function(t, i) {
                this._dispatcher && this._dispatcher[ld](t, i)
            },
            _6i: function(t, i) {
                this._dispatcher && this._dispatcher.removeListener(t, i)
            },
            _mxd: function(t) {
                this._jn = {}, (t || this[xa] * this[Fa] > 1e5) && (this._jx = null, this._jm = null)
            }
        },
        N(ge, BD);
    var ZM = {};
    DM[Uv] = xe,
        DM[gb] = ye,
        DM[yb] = pe,
        DM.getAllImages = function() {
            var t = [];
            for (var i in ZM) t[Yr](i);
            return t
        };
    var JM = function(t, i, e, n, s, r) {
        this.type = t,
            this[mb] = i,
            this[pb] = e,
            this[Qf] = n || 0,
            this.tx = s || 0,
            this.ty = r || 0
    };
    MM.GRADIENT_TYPE_RADIAL = Nc,
        MM.GRADIENT_TYPE_LINEAR = Mc,
        JM.prototype = {
            type: null,
            colors: null,
            positions: null,
            angle: null,
            tx: 0,
            ty: 0,
            position: $D[Gc],
            isEmpty: function() {
                return null == this.colors || 0 == this.colors[jr]
            },
            _6t: function() {
                var t = this[mb][jr];
                if (1 == t) return [0];
                for (var i = [], e = 1 / (t - 1), n = 0; t > n; n++) i.push(e * n);
                return this[pb] || (this.positions = i),
                    i
            },
            generatorGradient: function(t) {
                if (null == this[mb] || 0 == this[mb][jr]) return null;
                var i,
                    e = $i();
                if (this.type == MM.GRADIENT_TYPE_LINEAR) {
                    var n = this[Qf];
                    n > Math.PI && (n -= Math.PI);
                    var s;
                    if (n <= Math.PI / 2) {
                        var r = Math[Ih](t.height, t[xa]),
                            h = Math[$a](t[xa] * t.width + t.height * t[Fa]),
                            a = r - n;
                        s = Math[Ea](a) * h
                    } else {
                        var r = Math.atan2(t[xa], t.height),
                            h = Math.sqrt(t.width * t[xa] + t.height * t.height),
                            a = r - (n - Math.PI / 2);
                        s = Math[Ea](a) * h
                    }
                    var o = s / 2,
                        _ = o * Math.cos(n),
                        f = o * Math.sin(n),
                        u = t.x + t[xa] / 2 - _,
                        c = t.y + t[Fa] / 2 - f,
                        d = t.x + t[xa] / 2 + _,
                        l = t.y + t.height / 2 + f;
                    i = e[Eb](u, c, d, l)
                } else {
                    if (!(this[N_] = MM[xb])) return null;
                    var v = oi(this.position, t[xa], t.height);
                    v.x += t.x,
                        v.y += t.y,
                        this.tx && (v.x += Math[Sh](this.tx) < 1 ? t[xa] * this.tx : this.tx),
                        this.ty && (v.y += Math[Sh](this.ty) < 1 ? t[Fa] * this.ty : this.ty);
                    var b = PD(v.x, v.y, t.x, t.y);
                    b = Math.max(b, PD(v.x, v.y, t.x, t.y + t[Fa])),
                        b = Math[ja](b, PD(v.x, v.y, t.x + t[xa], t.y + t[Fa])),
                        b = Math[ja](b, PD(v.x, v.y, t.x + t.width, t.y)),
                        i = e.createRadialGradient(v.x, v.y, 0, v.x, v.y, b)
                }
                var g = this.colors,
                    y = this[pb];
                y && y.length == g[jr] || (y = this._6t());
                for (var m = 0, p = g.length; p > m; m++) i[Tb](y[m], g[m]);
                return i
            }
        };
    var QM = new JM(MM.GRADIENT_TYPE_LINEAR, [V(2332033023), V(1154272460), V(1154272460), V(1442840575)], [.1, .3, .7, .9], Math.PI / 2),
        tP = new JM(MM[wb], [V(2332033023), V(1154272460), V(1154272460), V(1442840575)], [.1, .3, .7, .9], 0),
        iP = (new JM(MM.GRADIENT_TYPE_LINEAR, [V(1154272460), V(1442840575)], [.1, .9], 0), new JM(MM.GRADIENT_TYPE_RADIAL, [V(2298478591), V(1156509422), V(1720223880), V(1147561574)], [.1, .3, .7, .9], 0, -.3, -.3)),
        eP = [V(0), V(4294901760), V(4294967040), V(4278255360), V(4278250239), V(4278190992), V(4294901958), V(0)],
        nP = [0, .12, .28, .45, .6, .75, .8, 1],
        sP = new JM(MM.GRADIENT_TYPE_LINEAR, eP, nP),
        rP = new JM(MM[wb], eP, nP, Math.PI / 2),
        hP = new JM(MM.GRADIENT_TYPE_RADIAL, eP, nP);
    JM.LINEAR_GRADIENT_VERTICAL = QM,
        JM[Ob] = tP,
        JM.RADIAL_GRADIENT = iP,
        JM.RAINBOW_LINEAR_GRADIENT = sP,
        JM.RAINBOW_LINEAR_GRADIENT_VERTICAL = rP,
        JM.RAINBOW_RADIAL_GRADIENT = hP;
    var aP = jc,
        oP = Mc,
        _P = Ib,
        fP = Pc,
        uP = Ab,
        cP = Sb;
    MM.SEGMENT_MOVE_TO = aP,
        MM[Cb] = oP,
        MM.SEGMENT_QUAD_TO = _P,
        MM[kb] = fP,
        MM.SEGMENT_ARC_TO = uP,
        MM.SEGMENT_CLOSE = cP;
    var dP = function(t, i) {
        this.id = ++uD,
            C(t) ? this.points = t : (this[N_] = t, this[aa] = i)
    };
    dP.prototype = {
            toJSON: function() {
                return {
                    type: this.type,
                    points: this.points
                }
            },
            parseJSON: function(t) {
                this.type = t[N_],
                    this[aa] = t[aa]
            },
            points: null,
            type: oP,
            clone: function() {
                return new dP(this.type, g(this.points))
            },
            move: function(t, i) {
                if (this.points)
                    for (var e = 0, n = this.points[jr]; n > e; e++) {
                        var s = this[aa][e];
                        DM.isNumber(s) && (this[aa][e] += e % 2 == 0 ? t : i)
                    }
            }
        },
        Z(dP[ah], {
            lastPoint: {
                get: function() {
                    return this[N_] == uP ? {
                        x: this._p2x,
                        y: this._p2y
                    } : {
                        x: this.points[this.points[jr] - 2],
                        y: this[aa][this.points.length - 1]
                    }
                }
            },
            firstPoint: {
                get: function() {
                    return {
                        x: this.points[0],
                        y: this.points[1]
                    }
                }
            }
        }),
        DM.PathSegment = dP;
    var lP = 0,
        vP = function(t) {
            this.bounds = new BD,
                this._eq = t || []
        };
    vP[ah] = {
            toJSON: function() {
                var t = [];
                return this._eq[d_](function(i) {
                        t[Yr](i[Lb]())
                    }),
                    t
            },
            parseJSON: function(t) {
                var i = this._eq;
                t[d_](function(t) {
                    i.push(new dP(t[N_], t.points))
                })
            },
            clear: function() {
                this._eq.length = 0,
                    this.bounds[Ec](),
                    this._io = 0,
                    this._6a = !0
            },
            _e2: !0,
            _6e: function(t, i) {
                this._e2 && 0 === this._eq[jr] && t != aP && this._eq[Yr](new dP(aP, [0, 0])),
                    this._eq.push(new dP(t, i)),
                    this._6a = !0
            },
            moveTo: function(t, i) {
                this._6e(aP, [t, i])
            },
            lineTo: function(t, i) {
                this._6e(oP, [t, i])
            },
            quadTo: function(t, i, e, n) {
                this._6e(_P, [t, i, e, n])
            },
            curveTo: function(t, i, e, n, s, r) {
                this._6e(fP, [t, i, e, n, s, r])
            },
            arcTo: function(t, i, e, n, s) {
                this._6e(uP, [t, i, e, n, s])
            },
            closePath: function() {
                this._6e(cP)
            },
            _7m: function(t, i, e, n, s) {
                if (n.selectionColor) {
                    if (e == MM.SELECTION_TYPE_SHADOW) {
                        if (!n[Rb]) return;
                        return t.shadowColor = n[Db],
                            t[ob] = n[Rb] * i,
                            t.shadowOffsetX = (n[Mb] || 0) * i,
                            void(t.shadowOffsetY = (n[Pb] || 0) * i)
                    }
                    if (e == MM[Nb]) {
                        if (!n.selectionBorder) return;
                        t[nb] = n[Db],
                            t.lineWidth = n[jb] + (s[Wa] || 0),
                            this._l9(t),
                            t.stroke()
                    }
                }
            },
            _6a: !0,
            _eq: null,
            _io: 0,
            lineCap: Ov,
            lineJoin: Ha,
            draw: function(t, i, e, n, s) {
                t.lineCap = e.lineCap || this.lineCap,
                    t.lineJoin = e[Bb] || this[Bb],
                    n && (s || (s = e), this._7m(t, i, s[zb], s, e)),
                    e[$b] && (this._l9(t), t[Wa] = e.lineWidth + 2 * (e[Gb] || 0), t[nb] = e.outlineStyle, t[Ua]()),
                    t[Wa] = 0,
                    this._l9(t),
                    e.fillColor && (t[Xv] = e[cb] || e.fillColor, t.fill()),
                    e.fillGradient && (t.fillStyle = e._fillGradient || e.fillGradient, t.fill()),
                    e.lineWidth && (t[Wa] = e.lineWidth, e[n_] && (t.lineDash = e.lineDash, t.lineDashOffset = e[s_]), t[nb] = e.renderColor || e[nb], t[Ua](), t[n_] = [])
            },
            _l9: function(t) {
                t.beginPath();
                for (var i, e, n = 0, s = this._eq.length; s > n; n++) i = this._eq[n],
                    zM._l9(t, i, e),
                    e = i
            },
            validate: function() {
                if (this._6a = !1, this[qa][Ec](), this._io = 0, 0 != this._eq.length)
                    for (var t, i, e = this._eq, n = 1, s = e[0], r = s, h = e.length; h > n; n++) t = e[n],
                        t.type == aP ? r = t : (zM._63(this.bounds, t, s, r), i = zM._66(t, s, r), t._io = i, this._io += i),
                        s = t
            },
            getBounds: function(t, i) {
                if (this._6a && this[uo](), i = i || new BD, t) {
                    var e = t / 2;
                    i[Vo](this[qa].x - e, this[qa].y - e, this.bounds.width + t, this[qa][Fa] + t)
                } else i.set(this[qa].x, this.bounds.y, this[qa][xa], this.bounds[Fa]);
                return i
            },
            _h9: function(t, i, e, n, s, r) {
                return oe.call(this, t, i, e, n, s, r)
            },
            _mxm: function() {
                return [][Ka](this._eq)
            },
            generator: function(t, i, e, n, s) {
                return ae[Br](this, t, i, e, n, s)
            },
            getLocation: function(t, i) {
                return _e.call(this, t, i || 0)
            }
        },
        Z(vP[ah], {
            length: {
                get: function() {
                    return this._6a && this[uo](),
                        this._io
                }
            },
            _empty: {
                get: function() {
                    return 0 == this._eq[jr]
                }
            }
        }),
        MM[Eo] = Fb,
        MM.BLEND_MODE_MULTIPLY = Yb,
        MM.BLEND_MODE_COLOR_BURN = qb,
        MM.BLEND_MODE_LINEAR_BURN = Hb,
        MM[To] = Ub,
        MM[Wb] = Xb,
        MM[wo] = Vb,
        CD[mo] = MM[Kb];
    var bP = function(t, i, e) {
        this._jm = t,
            this[Ao] = i || 1,
            t instanceof Image && (e = !1),
            this._ib = e
    };
    bP.prototype = {
        scale: 1,
        _jm: null,
        _jn: null,
        _ib: !0,
        _l9: function(t, i, e) {
            if (!i || this._ib === !1) return void t[Uv](this._jm, 0, 0);
            this._jn || (this._jn = {});
            var n = i + e,
                s = this._jn[n];
            s || (s = Se(this._jm, i, e), s || (this._ib = !1), this._jn[n] = s || this._jm),
                t[Uv](s, 0, 0)
        }
    };
    var gP = function(t, i, e, n, s, r, h, a, o) {
            this._m1 = Re(t, i, e, n, s, r, h, a, o)
        },
        yP = {
            server: {
                draw: function(t) {
                    t.save(),
                        t[fo](0, 0),
                        t.beginPath(),
                        t[V_](0, 0),
                        t[Z_](40, 0),
                        t[Z_](40, 40),
                        t[Z_](0, 40),
                        t[So](),
                        t.clip(),
                        t[fo](0, 0),
                        t.translate(0, 0),
                        t.scale(1, 1),
                        t.translate(0, 0),
                        t[nb] = Zb,
                        t[Jb] = Ov,
                        t.lineJoin = Cv,
                        t[Qb] = 4,
                        t[Wv](),
                        t.save(),
                        t.restore(),
                        t.save(),
                        t[hb](),
                        t.save(),
                        t[hb](),
                        t.save(),
                        t.restore(),
                        t.save(),
                        t[hb](),
                        t.save(),
                        t.restore(),
                        t.save(),
                        t.restore(),
                        t[Wv](),
                        t[hb](),
                        t.save(),
                        t[hb](),
                        t.save(),
                        t.restore(),
                        t[Wv](),
                        t.restore(),
                        t[Wv](),
                        t.restore(),
                        t[Wv](),
                        t[hb](),
                        t[hb](),
                        t.save();
                    var i = t[Eb](6.75, 3.9033, 30.5914, 27.7447);
                    i[Tb](.0493, tg),
                        i[Tb](.0689, ig),
                        i[Tb](.0939, eg),
                        i[Tb](.129, ng),
                        i.addColorStop(.2266, sg),
                        i.addColorStop(.2556, rg),
                        i.addColorStop(.2869, hg),
                        i.addColorStop(.3194, ag),
                        i[Tb](.3525, og),
                        i.addColorStop(.3695, _g),
                        i.addColorStop(.5025, fg),
                        i.addColorStop(.9212, ug),
                        i.addColorStop(1, cg),
                        t[Xv] = i,
                        t[dg](),
                        t.moveTo(25.677, 4.113),
                        t[lg](25.361, 2.4410000000000007, 23.364, 2.7940000000000005, 22.14, 2.7990000000000004),
                        t[lg](19.261, 2.813, 16.381, 2.8260000000000005, 13.502, 2.8400000000000003),
                        t.bezierCurveTo(12.185, 2.846, 10.699000000000002, 2.652, 9.393, 2.8790000000000004),
                        t.bezierCurveTo(9.19, 2.897, 8.977, 2.989, 8.805, 3.094),
                        t.bezierCurveTo(8.084999999999999, 3.5109999999999997, 7.436999999999999, 4.1259999999999994, 6.776, 4.63),
                        t.bezierCurveTo(5.718999999999999, 5.436, 4.641, 6.22, 3.6029999999999998, 7.05),
                        t.bezierCurveTo(4.207, 6.5889999999999995, 21.601999999999997, 36.579, 21.028, 37.307),
                        t[lg](22.019, 36.063, 23.009999999999998, 34.819, 24.000999999999998, 33.575),
                        t.bezierCurveTo(24.587999999999997, 32.84, 25.589999999999996, 31.995000000000005, 25.593999999999998, 30.983000000000004),
                        t.bezierCurveTo(25.595999999999997, 30.489000000000004, 25.598, 29.994000000000003, 25.601, 29.500000000000004),
                        t.bezierCurveTo(25.612, 26.950000000000003, 25.622, 24.400000000000006, 25.633, 21.85),
                        t[lg](25.657, 16.318, 25.680999999999997, 10.786000000000001, 25.704, 5.253),
                        t[lg](25.706, 4.885, 25.749, 4.478, 25.677, 4.113),
                        t.bezierCurveTo(25.67, 4.077, 25.697, 4.217, 25.677, 4.113),
                        t[So](),
                        t.fill(),
                        t.stroke(),
                        t.restore(),
                        t.save(),
                        t.save(),
                        t[Xv] = vg,
                        t.beginPath(),
                        t[V_](19.763, 6.645),
                        t.bezierCurveTo(20.002000000000002, 6.643999999999999, 20.23, 6.691999999999999, 20.437, 6.778),
                        t.bezierCurveTo(20.644000000000002, 6.864999999999999, 20.830000000000002, 6.991, 20.985, 7.146999999999999),
                        t[lg](21.14, 7.302999999999999, 21.266, 7.488999999999999, 21.352999999999998, 7.696999999999999),
                        t[lg](21.438999999999997, 7.903999999999999, 21.487, 8.133, 21.487, 8.372),
                        t.lineTo(21.398, 36.253),
                        t.bezierCurveTo(21.397, 36.489, 21.349, 36.713, 21.262, 36.917),
                        t.bezierCurveTo(21.174, 37.121, 21.048000000000002, 37.305, 20.893, 37.458),
                        t[lg](20.738, 37.611, 20.553, 37.734, 20.348, 37.818999999999996),
                        t[lg](20.141, 37.903999999999996, 19.916, 37.95099999999999, 19.679, 37.949),
                        t.lineTo(4.675, 37.877),
                        t[lg](4.4399999999999995, 37.876000000000005, 4.216, 37.827000000000005, 4.012, 37.741),
                        t.bezierCurveTo(3.8089999999999997, 37.653999999999996, 3.6249999999999996, 37.528999999999996, 3.4719999999999995, 37.376),
                        t[lg](3.3179999999999996, 37.221, 3.1939999999999995, 37.037, 3.1079999999999997, 36.833999999999996),
                        t[lg](3.022, 36.629999999999995, 2.9739999999999998, 36.406, 2.9739999999999998, 36.172),
                        t.lineTo(2.924, 8.431),
                        t.bezierCurveTo(2.923, 8.192, 2.971, 7.964, 3.057, 7.758),
                        t.bezierCurveTo(3.143, 7.552, 3.267, 7.365, 3.4219999999999997, 7.209),
                        t.bezierCurveTo(3.5769999999999995, 7.052999999999999, 3.76, 6.925, 3.965, 6.837),
                        t.bezierCurveTo(4.17, 6.749, 4.396, 6.701, 4.633, 6.7),
                        t[Z_](19.763, 6.645),
                        t.closePath(),
                        t.fill(),
                        t[Ua](),
                        t.restore(),
                        t[hb](),
                        t[Wv](),
                        t[Xv] = bg,
                        t[dg](),
                        t[gg](12.208, 26.543, 2.208, 0, 6.283185307179586, !0),
                        t.closePath(),
                        t[yg](),
                        t[Ua](),
                        t[hb](),
                        t.save(),
                        t[Xv] = vg,
                        t[dg](),
                        t.arc(12.208, 26.543, 1.876, 0, 6.283185307179586, !0),
                        t[So](),
                        t.fill(),
                        t[Ua](),
                        t[hb](),
                        t[Wv](),
                        t.fillStyle = bg,
                        t[dg](),
                        t.moveTo(19.377, 17.247),
                        t[lg](19.377, 17.724, 18.991999999999997, 18.108999999999998, 18.516, 18.108999999999998),
                        t[Z_](5.882, 18.108999999999998),
                        t.bezierCurveTo(5.404999999999999, 18.108999999999998, 5.02, 17.723, 5.02, 17.247),
                        t[Z_](5.02, 11.144),
                        t[lg](5.02, 10.666, 5.406, 10.281, 5.882, 10.281),
                        t.lineTo(18.516, 10.281),
                        t.bezierCurveTo(18.993, 10.281, 19.377, 10.666, 19.377, 11.144),
                        t.lineTo(19.377, 17.247),
                        t[So](),
                        t.fill(),
                        t.stroke(),
                        t[hb](),
                        t[Wv](),
                        t[Wv](),
                        t[Xv] = vg,
                        t[dg](),
                        t.moveTo(18.536, 13.176),
                        t.bezierCurveTo(18.536, 13.518, 18.261000000000003, 13.794, 17.919, 13.794),
                        t[Z_](6.479, 13.794),
                        t[lg](6.1370000000000005, 13.794, 5.861, 13.518, 5.861, 13.176),
                        t[Z_](5.861, 11.84),
                        t.bezierCurveTo(5.861, 11.498, 6.137, 11.221, 6.479, 11.221),
                        t.lineTo(17.918, 11.221),
                        t.bezierCurveTo(18.259999999999998, 11.221, 18.535, 11.497, 18.535, 11.84),
                        t.lineTo(18.535, 13.176),
                        t.closePath(),
                        t[yg](),
                        t.stroke(),
                        t[hb](),
                        t.save(),
                        t.fillStyle = vg,
                        t[dg](),
                        t.moveTo(18.536, 16.551),
                        t[lg](18.536, 16.892999999999997, 18.261000000000003, 17.168999999999997, 17.919, 17.168999999999997),
                        t.lineTo(6.479, 17.168999999999997),
                        t[lg](6.1370000000000005, 17.168999999999997, 5.861, 16.892999999999997, 5.861, 16.551),
                        t[Z_](5.861, 15.215999999999998),
                        t[lg](5.861, 14.872999999999998, 6.137, 14.596999999999998, 6.479, 14.596999999999998),
                        t[Z_](17.918, 14.596999999999998),
                        t.bezierCurveTo(18.259999999999998, 14.596999999999998, 18.535, 14.872999999999998, 18.535, 15.215999999999998),
                        t[Z_](18.535, 16.551),
                        t[So](),
                        t.fill(),
                        t.stroke(),
                        t.restore(),
                        t.restore(),
                        t[hb]()
                }
            },
            exchanger2: {
                draw: function(t) {
                    t.save(),
                        t.translate(0, 0),
                        t.beginPath(),
                        t[V_](0, 0),
                        t[Z_](40, 0),
                        t[Z_](40, 40),
                        t.lineTo(0, 40),
                        t.closePath(),
                        t.clip(),
                        t.translate(0, 0),
                        t.translate(0, 0),
                        t.scale(1, 1),
                        t.translate(0, 0),
                        t.strokeStyle = Zb,
                        t.lineCap = Ov,
                        t.lineJoin = Cv,
                        t.miterLimit = 4,
                        t[Wv](),
                        t[Wv](),
                        t[hb](),
                        t[Wv](),
                        t[hb](),
                        t.save(),
                        t.restore(),
                        t.save(),
                        t.restore(),
                        t.save(),
                        t[hb](),
                        t.save(),
                        t.restore(),
                        t.save(),
                        t.restore(),
                        t[Wv](),
                        t[hb](),
                        t.save(),
                        t[hb](),
                        t[Wv](),
                        t[hb](),
                        t[hb](),
                        t.save();
                    var i = t[Eb](.4102, 24.3613, 39.5898, 24.3613);
                    i.addColorStop(0, tg),
                        i[Tb](.0788, sg),
                        i[Tb](.2046, mg),
                        i.addColorStop(.3649, pg),
                        i[Tb](.5432, Eg),
                        i[Tb](.6798, xg),
                        i[Tb](.7462, Tg),
                        i[Tb](.8508, wg),
                        i.addColorStop(.98, rg),
                        i[Tb](1, Og),
                        t[Xv] = i,
                        t[dg](),
                        t[V_](.41, 16.649),
                        t[lg](.633, 19.767, .871, 20.689, 1.094, 23.807000000000002),
                        t.bezierCurveTo(1.29, 26.548000000000002, 3.324, 28.415000000000003, 5.807, 29.711000000000002),
                        t[lg](10.582, 32.202000000000005, 16.477, 32.806000000000004, 21.875999999999998, 32.523),
                        t[lg](26.929, 32.258, 32.806, 31.197000000000003, 36.709999999999994, 27.992000000000004),
                        t.bezierCurveTo(38.30499999999999, 26.728000000000005, 38.83599999999999, 25.103000000000005, 38.998999999999995, 23.161000000000005),
                        t[lg](39.589, 16.135000000000005, 39.589, 16.135000000000005, 39.589, 16.135000000000005),
                        t.bezierCurveTo(39.589, 16.135000000000005, 3.26, 16.647, .41, 16.649),
                        t.closePath(),
                        t[yg](),
                        t.stroke(),
                        t[hb](),
                        t.save(),
                        t[Wv](),
                        t.fillStyle = vg,
                        t[dg](),
                        t.moveTo(16.4, 25.185),
                        t.bezierCurveTo(12.807999999999998, 24.924999999999997, 9.139, 24.238, 5.857999999999999, 22.705),
                        t.bezierCurveTo(3.175999999999999, 21.450999999999997, -.32200000000000095, 18.971999999999998, .544999999999999, 15.533999999999999),
                        t.bezierCurveTo(1.3499999999999992, 12.335999999999999, 4.987999999999999, 10.495999999999999, 7.807999999999999, 9.428999999999998),
                        t[lg](11.230999999999998, 8.133999999999999, 14.911999999999999, 7.519999999999999, 18.558, 7.345999999999998),
                        t.bezierCurveTo(22.233, 7.169999999999998, 25.966, 7.437999999999998, 29.548000000000002, 8.300999999999998),
                        t.bezierCurveTo(32.673, 9.052999999999999, 36.192, 10.296, 38.343, 12.814999999999998),
                        t.bezierCurveTo(40.86600000000001, 15.768999999999998, 39.208000000000006, 19.066999999999997, 36.406000000000006, 21.043999999999997),
                        t[lg](33.566, 23.046999999999997, 30.055000000000007, 24.071999999999996, 26.670000000000005, 24.676999999999996),
                        t[lg](23.289, 25.28, 19.824, 25.436, 16.4, 25.185),
                        t.bezierCurveTo(13.529, 24.977, 19.286, 25.396, 16.4, 25.185),
                        t.closePath(),
                        t.fill(),
                        t[Ua](),
                        t.restore(),
                        t[hb](),
                        t[Wv](),
                        t.save(),
                        t.save(),
                        t.save(),
                        t[Wv](),
                        t.fillStyle = Ig,
                        t[dg](),
                        t[V_](5.21, 21.754),
                        t.lineTo(8.188, 17.922),
                        t.lineTo(9.53, 18.75),
                        t[Z_](15.956, 16.004),
                        t[Z_](18.547, 17.523),
                        t.lineTo(12.074, 20.334),
                        t[Z_](13.464, 21.204),
                        t[Z_](5.21, 21.754),
                        t[So](),
                        t.fill(),
                        t[Ua](),
                        t[hb](),
                        t.restore(),
                        t.restore(),
                        t[Wv](),
                        t[Wv](),
                        t[Wv](),
                        t[Xv] = Ig,
                        t.beginPath(),
                        t[V_](17.88, 14.61),
                        t[Z_](9.85, 13.522),
                        t[Z_](11.703, 12.757),
                        t[Z_](7.436, 10.285),
                        t.lineTo(10.783, 8.942),
                        t.lineTo(15.091, 11.357),
                        t[Z_](16.88, 10.614),
                        t[Z_](17.88, 14.61),
                        t[So](),
                        t[yg](),
                        t.stroke(),
                        t.restore(),
                        t[hb](),
                        t[Wv](),
                        t[Wv](),
                        t.fillStyle = Ig,
                        t.beginPath(),
                        t[V_](17.88, 14.61),
                        t[Z_](9.85, 13.522),
                        t[Z_](11.703, 12.757),
                        t.lineTo(7.436, 10.285),
                        t[Z_](10.783, 8.942),
                        t.lineTo(15.091, 11.357),
                        t[Z_](16.88, 10.614),
                        t[Z_](17.88, 14.61),
                        t[So](),
                        t.fill(),
                        t.stroke(),
                        t.restore(),
                        t[hb](),
                        t.restore(),
                        t[Wv](),
                        t.save(),
                        t[Wv](),
                        t[Xv] = Ig,
                        t[dg](),
                        t.moveTo(23.556, 15.339),
                        t.lineTo(20.93, 13.879),
                        t.lineTo(26.953, 11.304),
                        t.lineTo(25.559, 10.567),
                        t[Z_](33.251, 9.909),
                        t[Z_](31.087, 13.467),
                        t.lineTo(29.619, 12.703),
                        t.lineTo(23.556, 15.339),
                        t.closePath(),
                        t.fill(),
                        t.stroke(),
                        t.restore(),
                        t.restore(),
                        t[hb](),
                        t[Wv](),
                        t.save(),
                        t[Wv](),
                        t.fillStyle = Ig,
                        t[dg](),
                        t[V_](30.028, 23.383),
                        t.lineTo(24.821, 20.366),
                        t[Z_](22.915, 21.227),
                        t[Z_](21.669, 16.762),
                        t[Z_](30.189, 17.942),
                        t[Z_](28.33, 18.782),
                        t[Z_](33.579, 21.725),
                        t.lineTo(30.028, 23.383),
                        t.closePath(),
                        t.fill(),
                        t.stroke(),
                        t[hb](),
                        t.restore(),
                        t.save(),
                        t.save(),
                        t.fillStyle = Ig,
                        t[dg](),
                        t.moveTo(30.028, 23.383),
                        t[Z_](24.821, 20.366),
                        t[Z_](22.915, 21.227),
                        t[Z_](21.669, 16.762),
                        t.lineTo(30.189, 17.942),
                        t.lineTo(28.33, 18.782),
                        t[Z_](33.579, 21.725),
                        t.lineTo(30.028, 23.383),
                        t.closePath(),
                        t[yg](),
                        t[Ua](),
                        t[hb](),
                        t[hb](),
                        t[hb](),
                        t[hb](),
                        t[hb](),
                        t.restore()
                }
            },
            exchanger: {
                draw: function(t) {
                    t.save(),
                        t.translate(0, 0),
                        t[dg](),
                        t[V_](0, 0),
                        t[Z_](40, 0),
                        t.lineTo(40, 40),
                        t.lineTo(0, 40),
                        t[So](),
                        t[Kv](),
                        t.translate(0, 0),
                        t[fo](0, 0),
                        t.scale(1, 1),
                        t[fo](0, 0),
                        t[nb] = Zb,
                        t.lineCap = Ov,
                        t.lineJoin = Cv,
                        t[Qb] = 4,
                        t.save(),
                        t.save(),
                        t.restore(),
                        t[Wv](),
                        t[hb](),
                        t[Wv](),
                        t.restore(),
                        t.save(),
                        t[hb](),
                        t.save(),
                        t.restore(),
                        t.save(),
                        t[hb](),
                        t[Wv](),
                        t.restore(),
                        t.restore(),
                        t[Wv]();
                    var i = t.createLinearGradient(.2095, 20.7588, 39.4941, 20.7588);
                    i[Tb](0, Ag),
                        i[Tb](.0788, Sg),
                        i[Tb](.352, Cg),
                        i[Tb](.6967, kg),
                        i.addColorStop(.8916, Lg),
                        i.addColorStop(.9557, Rg),
                        i[Tb](1, Dg),
                        t.fillStyle = i,
                        t.beginPath(),
                        t[V_](39.449, 12.417),
                        t[Z_](39.384, 9.424),
                        t.bezierCurveTo(39.384, 9.424, .7980000000000018, 22.264, .3710000000000022, 23.024),
                        t.bezierCurveTo(-.026999999999997804, 23.733, .4240000000000022, 24.903000000000002, .5190000000000022, 25.647000000000002),
                        t[lg](.7240000000000022, 27.244000000000003, .9240000000000023, 28.841, 1.1350000000000022, 30.437),
                        t.bezierCurveTo(1.3220000000000023, 31.843, 2.7530000000000023, 32.094, 3.9620000000000024, 32.094),
                        t.bezierCurveTo(8.799000000000003, 32.092, 13.636000000000003, 32.091, 18.473000000000003, 32.089),
                        t[lg](23.515, 32.086999999999996, 28.556000000000004, 32.086, 33.598, 32.083999999999996),
                        t[lg](34.859, 32.083999999999996, 36.286, 31.979999999999997, 37.266, 31.081999999999997),
                        t[lg](37.537, 30.820999999999998, 37.655, 30.535999999999998, 37.699999999999996, 30.229999999999997),
                        t[Z_](37.711, 30.316999999999997),
                        t.lineTo(39.281, 16.498999999999995),
                        t.bezierCurveTo(39.281, 16.498999999999995, 39.467999999999996, 15.126999999999995, 39.489, 14.666999999999994),
                        t[lg](39.515, 14.105, 39.449, 12.417, 39.449, 12.417),
                        t[So](),
                        t[yg](),
                        t[Ua](),
                        t.restore(),
                        t[Wv](),
                        t[Wv](),
                        t.save(),
                        t[Wv](),
                        t[hb](),
                        t[Wv](),
                        t[hb](),
                        t.save(),
                        t.restore(),
                        t[Wv](),
                        t[hb](),
                        t.save(),
                        t.restore(),
                        t[Wv](),
                        t[hb](),
                        t[Wv](),
                        t[hb](),
                        t[Wv](),
                        t[hb](),
                        t[Wv](),
                        t.restore(),
                        t[hb](),
                        t.save();
                    var i = t[Eb](19.8052, 7.7949, 19.8052, 24.7632);
                    i[Tb](0, Mg),
                        i[Tb](.1455, Pg),
                        i.addColorStop(.2975, Ng),
                        i[Tb](.4527, jg),
                        i[Tb](.6099, Bg),
                        i.addColorStop(.7687, zg),
                        i.addColorStop(.9268, $g),
                        i[Tb](.9754, Gg),
                        i[Tb](1, Fg),
                        t[Xv] = i,
                        t.beginPath(),
                        t.moveTo(33.591, 24.763),
                        t[lg](23.868000000000002, 24.754, 14.145, 24.746000000000002, 4.423000000000002, 24.738000000000003),
                        t[lg](3.140000000000002, 24.737000000000002, -.48799999999999777, 24.838000000000005, .3520000000000021, 22.837000000000003),
                        t.bezierCurveTo(1.292000000000002, 20.594000000000005, 2.2330000000000023, 18.351000000000003, 3.1730000000000023, 16.108000000000004),
                        t[lg](4.113000000000002, 13.865000000000006, 5.054000000000002, 11.623000000000005, 5.994000000000002, 9.380000000000004),
                        t.bezierCurveTo(6.728000000000002, 7.629000000000005, 9.521000000000003, 7.885000000000004, 11.156000000000002, 7.880000000000004),
                        t.bezierCurveTo(16.974000000000004, 7.861000000000004, 22.793000000000003, 7.843000000000004, 28.612000000000002, 7.825000000000005),
                        t.bezierCurveTo(30.976000000000003, 7.818000000000005, 33.341, 7.810000000000005, 35.707, 7.803000000000004),
                        t.bezierCurveTo(36.157000000000004, 7.802000000000004, 36.609, 7.787000000000004, 37.06, 7.804000000000005),
                        t[lg](37.793, 7.833000000000005, 39.389, 7.875000000000004, 39.385000000000005, 9.424000000000005),
                        t[lg](39.38400000000001, 9.647000000000006, 39.31, 10.138000000000005, 39.27700000000001, 10.359000000000005),
                        t.bezierCurveTo(38.81900000000001, 13.361000000000004, 38.452000000000005, 15.764000000000006, 37.99400000000001, 18.766000000000005),
                        t[lg](37.806000000000004, 19.998000000000005, 37.61800000000001, 21.230000000000004, 37.43000000000001, 22.462000000000007),
                        t.bezierCurveTo(37.151, 24.271, 35.264, 24.77, 33.591, 24.763),
                        t[So](),
                        t.fill(),
                        t.stroke(),
                        t[hb](),
                        t[hb](),
                        t[hb](),
                        t[Wv](),
                        t[Wv](),
                        t[Wv](),
                        t[Xv] = Ig,
                        t.beginPath(),
                        t[V_](10.427, 19.292),
                        t.lineTo(5.735, 16.452),
                        t.lineTo(12.58, 13.8),
                        t[Z_](12.045, 15.07),
                        t.lineTo(20.482, 15.072),
                        t.lineTo(19.667, 17.887),
                        t[Z_](11.029, 17.851),
                        t.lineTo(10.427, 19.292),
                        t[So](),
                        t[yg](),
                        t[Ua](),
                        t.restore(),
                        t.restore(),
                        t.save(),
                        t.save(),
                        t.fillStyle = Ig,
                        t[dg](),
                        t.moveTo(13.041, 13.042),
                        t[Z_](8.641, 10.73),
                        t.lineTo(14.82, 8.474),
                        t.lineTo(14.373, 9.537),
                        t[Z_](22.102, 9.479),
                        t[Z_](21.425, 11.816),
                        t.lineTo(13.54, 11.85),
                        t.lineTo(13.041, 13.042),
                        t.closePath(),
                        t.fill(),
                        t[Ua](),
                        t[hb](),
                        t[hb](),
                        t[Wv](),
                        t[Wv](),
                        t.fillStyle = Ig,
                        t.beginPath(),
                        t.moveTo(29.787, 16.049),
                        t[Z_](29.979, 14.704),
                        t[Z_](21.51, 14.706),
                        t[Z_](22.214, 12.147),
                        t.lineTo(30.486, 12.116),
                        t.lineTo(30.653, 10.926),
                        t[Z_](36.141, 13.4),
                        t[Z_](29.787, 16.049),
                        t.closePath(),
                        t[yg](),
                        t.stroke(),
                        t.restore(),
                        t[hb](),
                        t[Wv](),
                        t.save(),
                        t.fillStyle = Ig,
                        t.beginPath(),
                        t.moveTo(28.775, 23.14),
                        t[Z_](29.011, 21.49),
                        t[Z_](19.668, 21.405),
                        t[Z_](20.523, 18.295),
                        t.lineTo(29.613, 18.338),
                        t.lineTo(29.815, 16.898),
                        t[Z_](35.832, 19.964),
                        t[Z_](28.775, 23.14),
                        t[So](),
                        t[yg](),
                        t[Ua](),
                        t[hb](),
                        t[hb](),
                        t[hb](),
                        t.restore()
                }
            },
            cloud: {
                draw: function(t) {
                    t[Wv](),
                        t[dg](),
                        t[V_](0, 0),
                        t.lineTo(90.75, 0),
                        t.lineTo(90.75, 62.125),
                        t.lineTo(0, 62.125),
                        t.closePath(),
                        t[Kv](),
                        t[nb] = Zb,
                        t[Jb] = Ov,
                        t[Bb] = Cv,
                        t.miterLimit = 4,
                        t[Wv]();
                    var i = t.createLinearGradient(44.0054, 6.4116, 44.0054, 51.3674);
                    i[Tb](0, "rgba(159, 160, 160, 0.7)"),
                        i[Tb](.9726, Yg),
                        t[Xv] = i,
                        t[dg](),
                        t.moveTo(57.07, 20.354),
                        t[lg](57.037, 20.354, 57.006, 20.358, 56.974000000000004, 20.358),
                        t[lg](54.461000000000006, 14.308, 48.499, 10.049000000000001, 41.538000000000004, 10.049000000000001),
                        t[lg](33.801, 10.049000000000001, 27.309000000000005, 15.316000000000003, 25.408000000000005, 22.456000000000003),
                        t[lg](18.988000000000007, 23.289, 14.025000000000006, 28.765000000000004, 14.025000000000006, 35.413000000000004),
                        t.bezierCurveTo(14.025000000000006, 42.635000000000005, 19.880000000000006, 48.49, 27.102000000000004, 48.49),
                        t[lg](29.321000000000005, 48.49, 31.407000000000004, 47.933, 33.237, 46.961),
                        t.bezierCurveTo(34.980000000000004, 49.327, 37.78, 50.867999999999995, 40.945, 50.867999999999995),
                        t.bezierCurveTo(43.197, 50.867999999999995, 45.261, 50.086, 46.896, 48.785999999999994),
                        t.bezierCurveTo(49.729, 50.78699999999999, 53.244, 51.98799999999999, 57.07, 51.98799999999999),
                        t.bezierCurveTo(66.412, 51.98799999999999, 73.986, 44.90699999999999, 73.986, 36.17099999999999),
                        t[lg](73.986, 27.436, 66.413, 20.354, 57.07, 20.354),
                        t[So](),
                        t[yg](),
                        t[Ua](),
                        t[hb](),
                        t[hb]()
                }
            },
            node: {
                width: 60,
                height: 100,
                draw: function(t) {
                    t.save(),
                        t.translate(0, 0),
                        t[dg](),
                        t[V_](0, 0),
                        t.lineTo(40, 0),
                        t.lineTo(40, 40),
                        t.lineTo(0, 40),
                        t[So](),
                        t.clip(),
                        t.translate(0, 0),
                        t[fo](0, 0),
                        t.scale(1, 1),
                        t.translate(0, 0),
                        t.strokeStyle = Zb,
                        t.lineCap = Ov,
                        t[Bb] = Cv,
                        t[Qb] = 4,
                        t.save(),
                        t.fillStyle = qg,
                        t.beginPath(),
                        t[V_](13.948, 31.075),
                        t.lineTo(25.914, 31.075),
                        t.quadraticCurveTo(25.914, 31.075, 25.914, 31.075),
                        t.lineTo(25.914, 34.862),
                        t[zv](25.914, 34.862, 25.914, 34.862),
                        t[Z_](13.948, 34.862),
                        t.quadraticCurveTo(13.948, 34.862, 13.948, 34.862),
                        t[Z_](13.948, 31.075),
                        t[zv](13.948, 31.075, 13.948, 31.075),
                        t[So](),
                        t[yg](),
                        t[Ua](),
                        t[hb](),
                        t.save(),
                        t[Xv] = Hg,
                        t.beginPath(),
                        t.moveTo(29.679, 35.972),
                        t.bezierCurveTo(29.679, 36.675000000000004, 29.110999999999997, 37.244, 28.407999999999998, 37.244),
                        t.lineTo(11.456, 37.244),
                        t[lg](10.751999999999999, 37.244, 10.183, 36.675, 10.183, 35.972),
                        t[Z_](10.183, 36.136),
                        t[lg](10.183, 35.431000000000004, 10.751999999999999, 34.863, 11.456, 34.863),
                        t[Z_](28.407, 34.863),
                        t[lg](29.11, 34.863, 29.678, 35.431, 29.678, 36.136),
                        t[Z_](29.678, 35.972),
                        t[So](),
                        t[yg](),
                        t[Ua](),
                        t.restore(),
                        t.save(),
                        t.fillStyle = Hg,
                        t.beginPath(),
                        t[V_](.196, 29.346),
                        t.bezierCurveTo(.196, 30.301, .9690000000000001, 31.075, 1.925, 31.075),
                        t[Z_](37.936, 31.075),
                        t.bezierCurveTo(38.891, 31.075, 39.665, 30.301, 39.665, 29.346),
                        t[Z_](39.665, 27.174),
                        t[Z_](.196, 27.174),
                        t[Z_](.196, 29.346),
                        t.closePath(),
                        t[yg](),
                        t[Ua](),
                        t.restore(),
                        t[Wv](),
                        t.fillStyle = Ug,
                        t.beginPath(),
                        t.moveTo(37.937, 3.884),
                        t.lineTo(1.926, 3.884),
                        t.bezierCurveTo(.97, 3.884, .19699999999999984, 4.657, .19699999999999984, 5.614),
                        t[Z_](.19699999999999984, 27.12),
                        t[Z_](39.666000000000004, 27.12),
                        t.lineTo(39.666000000000004, 5.615),
                        t.bezierCurveTo(39.665, 4.657, 38.892, 3.884, 37.937, 3.884),
                        t[So](),
                        t.fill(),
                        t[Ua](),
                        t.restore(),
                        t.save(),
                        t[Wv](),
                        t.restore(),
                        t.save(),
                        t[hb](),
                        t[hb](),
                        t.save();
                    var i = t.createLinearGradient(6.9609, 2.9341, 32.9008, 28.874);
                    i[Tb](0, Wg),
                        i.addColorStop(1, Xg),
                        t[Xv] = i,
                        t.beginPath(),
                        t[V_](35.788, 6.39),
                        t.lineTo(4.074, 6.39),
                        t.bezierCurveTo(3.315, 6.39, 2.702, 7.003, 2.702, 7.763),
                        t.lineTo(2.702, 24.616),
                        t.lineTo(37.159, 24.616),
                        t[Z_](37.159, 7.763),
                        t.bezierCurveTo(37.159, 7.003, 36.546, 6.39, 35.788, 6.39),
                        t.closePath(),
                        t.fill(),
                        t[Ua](),
                        t[hb](),
                        t.restore()
                }
            },
            group: {
                draw: function(t) {
                    t.save(),
                        t[fo](0, 0),
                        t[dg](),
                        t[V_](0, 0),
                        t[Z_](47.75, 0),
                        t[Z_](47.75, 40),
                        t[Z_](0, 40),
                        t[So](),
                        t[Kv](),
                        t.translate(0, 0),
                        t.translate(0, 0),
                        t[Ao](1, 1),
                        t[fo](0, 0),
                        t.strokeStyle = Zb,
                        t.lineCap = Ov,
                        t[Bb] = Cv,
                        t[Qb] = 4,
                        t.save(),
                        t.save(),
                        t.fillStyle = qg,
                        t[dg](),
                        t[V_](10.447, 26.005),
                        t[Z_](18.847, 26.005),
                        t[zv](18.847, 26.005, 18.847, 26.005),
                        t[Z_](18.847, 28.663),
                        t[zv](18.847, 28.663, 18.847, 28.663),
                        t[Z_](10.447, 28.663),
                        t.quadraticCurveTo(10.447, 28.663, 10.447, 28.663),
                        t.lineTo(10.447, 26.005),
                        t.quadraticCurveTo(10.447, 26.005, 10.447, 26.005),
                        t[So](),
                        t.fill(),
                        t[Ua](),
                        t.restore(),
                        t[Wv](),
                        t.fillStyle = Hg,
                        t.beginPath(),
                        t.moveTo(21.491, 29.443),
                        t.bezierCurveTo(21.491, 29.935000000000002, 21.094, 30.338, 20.597, 30.338),
                        t.lineTo(8.698, 30.338),
                        t[lg](8.201, 30.338, 7.8020000000000005, 29.936, 7.8020000000000005, 29.443),
                        t[Z_](7.8020000000000005, 29.557000000000002),
                        t[lg](7.8020000000000005, 29.063000000000002, 8.201, 28.662000000000003, 8.698, 28.662000000000003),
                        t.lineTo(20.597, 28.662000000000003),
                        t[lg](21.093, 28.662000000000003, 21.491, 29.062, 21.491, 29.557000000000002),
                        t.lineTo(21.491, 29.443),
                        t[So](),
                        t[yg](),
                        t[Ua](),
                        t.restore(),
                        t[Wv](),
                        t[Xv] = Hg,
                        t[dg](),
                        t[V_](.789, 24.79),
                        t.bezierCurveTo(.789, 25.461, 1.334, 26.005, 2.0060000000000002, 26.005),
                        t.lineTo(27.289, 26.005),
                        t.bezierCurveTo(27.961000000000002, 26.005, 28.504, 25.461, 28.504, 24.79),
                        t[Z_](28.504, 23.267),
                        t[Z_](.789, 23.267),
                        t.lineTo(.789, 24.79),
                        t[So](),
                        t.fill(),
                        t.stroke(),
                        t[hb](),
                        t.save(),
                        t[Xv] = Ug,
                        t.beginPath(),
                        t[V_](27.289, 6.912),
                        t[Z_](2.006, 6.912),
                        t.bezierCurveTo(1.3339999999999996, 6.912, .7889999999999997, 7.455, .7889999999999997, 8.126),
                        t[Z_](.7889999999999997, 23.227),
                        t[Z_](28.503999999999998, 23.227),
                        t[Z_](28.503999999999998, 8.126),
                        t[lg](28.504, 7.455, 27.961, 6.912, 27.289, 6.912),
                        t[So](),
                        t.fill(),
                        t.stroke(),
                        t[hb](),
                        t[Wv](),
                        t.save(),
                        t[hb](),
                        t[Wv](),
                        t.restore(),
                        t.restore(),
                        t.save();
                    var i = t.createLinearGradient(5.54, 6.2451, 23.7529, 24.458);
                    i.addColorStop(0, Wg),
                        i.addColorStop(1, Xg),
                        t.fillStyle = i,
                        t[dg](),
                        t[V_](25.78, 8.671),
                        t[Z_](3.514, 8.671),
                        t[lg](2.9819999999999998, 8.671, 2.549, 9.101999999999999, 2.549, 9.635),
                        t.lineTo(2.549, 21.466),
                        t[Z_](26.743, 21.466),
                        t.lineTo(26.743, 9.636),
                        t.bezierCurveTo(26.743, 9.102, 26.312, 8.671, 25.78, 8.671),
                        t.closePath(),
                        t.fill(),
                        t[Ua](),
                        t.restore(),
                        t.restore(),
                        t.save(),
                        t[Wv](),
                        t[Xv] = qg,
                        t[dg](),
                        t[V_](27.053, 33.602),
                        t.lineTo(36.22, 33.602),
                        t[zv](36.22, 33.602, 36.22, 33.602),
                        t[Z_](36.22, 36.501),
                        t[zv](36.22, 36.501, 36.22, 36.501),
                        t.lineTo(27.053, 36.501),
                        t.quadraticCurveTo(27.053, 36.501, 27.053, 36.501),
                        t[Z_](27.053, 33.602),
                        t.quadraticCurveTo(27.053, 33.602, 27.053, 33.602),
                        t.closePath(),
                        t[yg](),
                        t.stroke(),
                        t.restore(),
                        t.save(),
                        t[Xv] = Hg,
                        t[dg](),
                        t.moveTo(39.104, 37.352),
                        t.bezierCurveTo(39.104, 37.891, 38.67, 38.327, 38.13, 38.327),
                        t[Z_](25.143, 38.327),
                        t[lg](24.602, 38.327, 24.166, 37.891, 24.166, 37.352),
                        t.lineTo(24.166, 37.477999999999994),
                        t.bezierCurveTo(24.166, 36.937, 24.602, 36.501, 25.143, 36.501),
                        t.lineTo(38.131, 36.501),
                        t.bezierCurveTo(38.671, 36.501, 39.105, 36.937, 39.105, 37.477999999999994),
                        t[Z_](39.105, 37.352),
                        t.closePath(),
                        t.fill(),
                        t.stroke(),
                        t[hb](),
                        t[Wv](),
                        t.fillStyle = Hg,
                        t[dg](),
                        t.moveTo(16.514, 32.275),
                        t[lg](16.514, 33.004999999999995, 17.107, 33.601, 17.839, 33.601),
                        t[Z_](45.433, 33.601),
                        t.bezierCurveTo(46.166, 33.601, 46.758, 33.005, 46.758, 32.275),
                        t.lineTo(46.758, 30.607999999999997),
                        t.lineTo(16.514, 30.607999999999997),
                        t[Z_](16.514, 32.275),
                        t[So](),
                        t[yg](),
                        t[Ua](),
                        t[hb](),
                        t[Wv](),
                        t.fillStyle = Ug,
                        t[dg](),
                        t.moveTo(45.433, 12.763),
                        t.lineTo(17.839, 12.763),
                        t[lg](17.107, 12.763, 16.514, 13.356, 16.514, 14.089),
                        t[Z_](16.514, 30.57),
                        t[Z_](46.757999999999996, 30.57),
                        t[Z_](46.757999999999996, 14.088),
                        t.bezierCurveTo(46.758, 13.356, 46.166, 12.763, 45.433, 12.763),
                        t[So](),
                        t.fill(),
                        t.stroke(),
                        t.restore(),
                        t[Wv](),
                        t.save(),
                        t.restore(),
                        t.save(),
                        t[hb](),
                        t[hb](),
                        t[Wv](),
                        i = t[Eb](21.6973, 12.0352, 41.5743, 31.9122),
                        i[Tb](0, Wg),
                        i[Tb](1, Xg),
                        t[Xv] = i,
                        t.beginPath(),
                        t[V_](43.785, 14.683),
                        t[Z_](19.486, 14.683),
                        t.bezierCurveTo(18.903000000000002, 14.683, 18.433, 15.153, 18.433, 15.735),
                        t.lineTo(18.433, 28.649),
                        t[Z_](44.837, 28.649),
                        t[Z_](44.837, 15.734),
                        t[lg](44.838, 15.153, 44.367, 14.683, 43.785, 14.683),
                        t[So](),
                        t[yg](),
                        t[Ua](),
                        t.restore(),
                        t[hb](),
                        t[Wv](),
                        t.globalAlpha = .5,
                        t[dg](),
                        t[V_](23.709, 36.33),
                        t.lineTo(4.232, 36.33),
                        t.lineTo(4.232, 27.199),
                        t[Z_](5.304, 27.199),
                        t[Z_](5.304, 35.259),
                        t[Z_](23.709, 35.259),
                        t.lineTo(23.709, 36.33),
                        t.closePath(),
                        t[yg](),
                        t[Ua](),
                        t.restore(),
                        t[hb]()
                }
            },
            subnetwork: {
                draw: function(t) {
                    t.save(),
                        t[fo](0, 0),
                        t.beginPath(),
                        t[V_](0, 0),
                        t[Z_](60.75, 0),
                        t[Z_](60.75, 42.125),
                        t.lineTo(0, 42.125),
                        t.closePath(),
                        t[Kv](),
                        t[fo](0, .26859504132231393),
                        t.scale(.6694214876033058, .6694214876033058),
                        t.translate(0, 0),
                        t.strokeStyle = Zb,
                        t[Jb] = Ov,
                        t.lineJoin = Cv,
                        t[Qb] = 4,
                        t[Wv](),
                        t.save(),
                        t.restore(),
                        t[Wv](),
                        t[hb](),
                        t.restore(),
                        t[Wv]();
                    var i = t.createLinearGradient(43.6724, -2.7627, 43.6724, 59.3806);
                    i[Tb](0, "rgba(159, 160, 160, 0.7)"),
                        i.addColorStop(.9726, Yg),
                        t.fillStyle = i,
                        t.beginPath(),
                        t[V_](61.732, 16.509),
                        t[lg](61.686, 16.509, 61.644, 16.515, 61.599, 16.515),
                        t.bezierCurveTo(58.126, 8.152000000000001, 49.884, 2.2650000000000006, 40.262, 2.2650000000000006),
                        t[lg](29.567, 2.2650000000000006, 20.594, 9.545000000000002, 17.966, 19.415),
                        t.bezierCurveTo(9.09, 20.566, 2.229, 28.136, 2.229, 37.326),
                        t.bezierCurveTo(2.229, 47.309, 10.322, 55.403000000000006, 20.306, 55.403000000000006),
                        t[lg](23.374000000000002, 55.403000000000006, 26.257, 54.633, 28.787, 53.28900000000001),
                        t.bezierCurveTo(31.197, 56.56000000000001, 35.067, 58.69000000000001, 39.442, 58.69000000000001),
                        t.bezierCurveTo(42.555, 58.69000000000001, 45.408, 57.60900000000001, 47.669, 55.81200000000001),
                        t[lg](51.586, 58.57800000000001, 56.443999999999996, 60.238000000000014, 61.732, 60.238000000000014),
                        t[lg](74.64699999999999, 60.238000000000014, 85.116, 50.45000000000002, 85.116, 38.37400000000001),
                        t[lg](85.116, 26.298, 74.646, 16.509, 61.732, 16.509),
                        t[So](),
                        t.fill(),
                        t.stroke(),
                        t[hb](),
                        t.save(),
                        t.save(),
                        t[Xv] = qg,
                        t[dg](),
                        t.moveTo(34.966, 44.287),
                        t.lineTo(45.112, 44.287),
                        t[zv](45.112, 44.287, 45.112, 44.287),
                        t.lineTo(45.112, 47.497),
                        t.quadraticCurveTo(45.112, 47.497, 45.112, 47.497),
                        t[Z_](34.966, 47.497),
                        t[zv](34.966, 47.497, 34.966, 47.497),
                        t.lineTo(34.966, 44.287),
                        t[zv](34.966, 44.287, 34.966, 44.287),
                        t.closePath(),
                        t[yg](),
                        t[Ua](),
                        t.restore(),
                        t[Wv](),
                        t[Xv] = Vg,
                        t.beginPath(),
                        t[V_](48.306, 48.439),
                        t[lg](48.306, 49.034, 47.824999999999996, 49.52, 47.226, 49.52),
                        t[Z_](32.854, 49.52),
                        t.bezierCurveTo(32.253, 49.52, 31.771, 49.034000000000006, 31.771, 48.439),
                        t.lineTo(31.771, 48.578),
                        t.bezierCurveTo(31.771, 47.981, 32.253, 47.497, 32.854, 47.497),
                        t[Z_](47.226, 47.497),
                        t[lg](47.824999999999996, 47.497, 48.306, 47.98, 48.306, 48.578),
                        t[Z_](48.306, 48.439),
                        t[So](),
                        t[yg](),
                        t.stroke(),
                        t[hb](),
                        t.save(),
                        t[Xv] = Kg,
                        t[dg](),
                        t.moveTo(23.302, 42.82),
                        t.bezierCurveTo(23.302, 43.63, 23.96, 44.287, 24.772, 44.287),
                        t.lineTo(55.308, 44.287),
                        t.bezierCurveTo(56.12, 44.287, 56.775, 43.629999999999995, 56.775, 42.82),
                        t[Z_](56.775, 40.98),
                        t.lineTo(23.302, 40.98),
                        t.lineTo(23.302, 42.82),
                        t[So](),
                        t.fill(),
                        t[Ua](),
                        t[hb](),
                        t.save(),
                        t.fillStyle = Ug,
                        t.beginPath(),
                        t[V_](55.307, 21.229),
                        t[Z_](24.771, 21.229),
                        t.bezierCurveTo(23.959, 21.229, 23.301000000000002, 21.884, 23.301000000000002, 22.695),
                        t[Z_](23.301000000000002, 40.933),
                        t[Z_](56.774, 40.933),
                        t.lineTo(56.774, 22.695),
                        t.bezierCurveTo(56.774, 21.884, 56.119, 21.229, 55.307, 21.229),
                        t[So](),
                        t[yg](),
                        t[Ua](),
                        t[hb](),
                        t[Wv](),
                        t.save(),
                        t[hb](),
                        t.save(),
                        t.restore(),
                        t[hb](),
                        t[Wv](),
                        i = t[Eb](29.04, 20.4219, 51.0363, 42.4181),
                        i.addColorStop(0, Wg),
                        i[Tb](1, Xg),
                        t[Xv] = i,
                        t[dg](),
                        t[V_](53.485, 23.353),
                        t.lineTo(26.592, 23.353),
                        t[lg](25.948999999999998, 23.353, 25.427, 23.873, 25.427, 24.517000000000003),
                        t.lineTo(25.427, 38.807),
                        t.lineTo(54.647, 38.807),
                        t[Z_](54.647, 24.517000000000003),
                        t.bezierCurveTo(54.648, 23.873, 54.127, 23.353, 53.485, 23.353),
                        t[So](),
                        t.fill(),
                        t[Ua](),
                        t[hb](),
                        t.restore(),
                        t[hb]()
                }
            }
        };
    for (var mP in yP) ye(Zg + mP, yP[mP]);
    var pP = function() {
            this[Jo] = !1;
            var t = this._f9;
            t[Ec]();
            var i = this._7x.x + this[zo] / 2,
                e = this._7x.y + this[zo] / 2,
                n = this._7x[xa] - this.$border,
                s = this._7x[Fa] - this.$border,
                r = $e.call(this, {
                    x: i,
                    y: e
                });
            qe(t, r.x, r.y, !0),
                r = $e[Br](this, {
                    x: i + n,
                    y: e
                }),
                qe(t, r.x, r.y),
                r = $e[Br](this, {
                    x: i + n,
                    y: e + s
                }),
                qe(t, r.x, r.y),
                r = $e[Br](this, {
                    x: i,
                    y: e + s
                }),
                qe(t, r.x, r.y),
                this.__m8Pointer && (r = $e[Br](this, {
                    x: this._pointerX,
                    y: this._pointerY
                }), qe(t, r.x, r.y)),
                this[zo] && t.grow(this.$border / 2)
        },
        EP = 20,
        xP = {
            _fw: !1,
            _jo: null,
            _n0y: 0,
            _l6: -1,
            _l4: null,
            _dr: function(t) {
                this._jo || (this._jo = [], this._jv = VM),
                    this._jo.push(t),
                    this._dt(),
                    this._l2()
            },
            _l2: function() {
                if (!this._l4) {
                    var t = this;
                    this._l4 = setTimeout(function i() {
                            return t._dt() !== !1 ? void(t._l4 = setTimeout(i, t._g8())) : void delete t._l4
                        },
                        this._g8())
                }
            },
            _g8: function() {
                return Math[ja](EP, this._jo[this._l6][Jg])
            },
            _dt: function() {
                return this._g9(this._l6 + 1)
            },
            _g9: function(t) {
                if (this._fw) t %= this._n0y;
                else if (t >= this._jo[jr]) return !1;
                if (this._l6 == t) return !1;
                this._l6 = t;
                var i = this._jo[this._l6],
                    e = i._n0ache;
                return e || (i._n0ache = e = Bi(this.width, this.height), e.g.putImageData(i[go], 0, 0), e._pixels = i._pixels),
                    this._jx = e,
                    this.$invalidateSize = !0,
                    this._d7()
            },
            _n0t: function() {
                return this._jo ? (this._fw = !0, this._n0y = this._jo.length, 1 == this._n0y ? this._d7() : void this._l2()) : void this._hc()
            },
            _ll: function() {
                this._l4 && (clearTimeout(this._l4), delete this._l4)
            },
            _d7: function() {
                var t = this._dispatcher[dd];
                if (!t || !t.length) return !1;
                for (var i = new VD(this, vb, bb, this._jx), e = 0, n = t.length; n > e; e++) {
                    var s = t[e];
                    s[vd]._iy && s[vd]._iy._hned ? (t.splice(e, 1), e--, n--) : s.onEvent.call(s.scope, i)
                }
                return t.length > 0
            },
            _9w: function(t, i) {
                this._dispatcher[ld](t, i),
                    this._fw && !this._l4 && this._l2()
            },
            _6i: function(t, i) {
                this._dispatcher.removeListener(t, i),
                    this._dispatcher._mx3() || this._ll()
            },
            _hn: function() {
                this._ll(),
                    this._dispatcher[Ec]()
            },
            _f2: function() {
                var t = this._jx._mxufferedImage;
                return t || (this._jx._mxufferedImage = t = new bP(this._jx, 1)),
                    t
            }
        },
        TP = function(t) {
            return t.reduce(function(t, i) {
                    return 2 * t + i
                },
                0)
        },
        wP = function(t) {
            for (var i = [], e = 7; e >= 0; e--) i.push(!!(t & 1 << e));
            return i
        },
        OP = function(t) {
            this[go] = t,
                this.len = this[go].length,
                this[Qg] = 0,
                this.readByte = function() {
                    if (this.pos >= this.data.length) throw new Error("Attempted to read past end of stream.");
                    return 255 & t[ty](this[Qg]++)
                },
                this.readBytes = function(t) {
                    for (var i = [], e = 0; t > e; e++) i[Yr](this[iy]());
                    return i
                },
                this.read = function(t) {
                    for (var i = "", e = 0; t > e; e++) i += String.fromCharCode(this[iy]());
                    return i
                },
                this.readUnsigned = function() {
                    var t = this[ey](2);
                    return (t[1] << 8) + t[0]
                }
        },
        IP = function(t, i) {
            for (var e, n, s = 0, r = function(t) {
                        for (var e = 0, n = 0; t > n; n++) i.charCodeAt(s >> 3) & 1 << (7 & s) && (e |= 1 << n),
                            s++;
                        return e
                    },
                    h = [], a = 1 << t, o = a + 1, _ = t + 1, f = [], u = function() {
                        f = [],
                            _ = t + 1;
                        for (var i = 0; a > i; i++) f[i] = [i];
                        f[a] = [],
                            f[o] = null
                    };;)
                if (n = e, e = r(_), e !== a) {
                    if (e === o) break;
                    if (e < f[jr]) n !== a && f.push(f[n].concat(f[e][0]));
                    else {
                        if (e !== f.length) throw new Error(ny);
                        f[Yr](f[n][Ka](f[n][0]))
                    }
                    h[Yr].apply(h, f[e]),
                        f[jr] === 1 << _ && 12 > _ && _++
                } else u();
            return h
        },
        AP = function(t, i) {
            i || (i = {});
            var e = function(i) {
                    for (var e = [], n = 0; i > n; n++) e.push(t[ey](3));
                    return e
                },
                n = function() {
                    var i,
                        e;
                    e = "";
                    do i = t.readByte(),
                        e += t[sy](i);
                    while (0 !== i);
                    return e
                },
                s = function() {
                    var n = {};
                    if (n.sig = t.read(3), n[ry] = t.read(3), hy !== n[ay]) throw new Error(oy);
                    n[xa] = t.readUnsigned(),
                        n.height = t.readUnsigned();
                    var s = wP(t.readByte());
                    n.gctFlag = s[_y](),
                        n[fy] = TP(s[Gr](0, 3)),
                        n.sorted = s.shift(),
                        n[uy] = TP(s.splice(0, 3)),
                        n[cy] = t.readByte(),
                        n.pixelAspectRatio = t[iy](),
                        n[dy] && (n.gct = e(1 << n[uy] + 1)),
                        i[ly] && i.hdr(n)
                },
                r = function(e) {
                    var s = function(e) {
                            var n = (t.readByte(), wP(t.readByte()));
                            e.reserved = n[Gr](0, 3),
                                e.disposalMethod = TP(n[Gr](0, 3)),
                                e[vy] = n.shift(),
                                e.transparencyGiven = n[_y](),
                                e.delayTime = t.readUnsigned(),
                                e.transparencyIndex = t[iy](),
                                e.terminator = t[iy](),
                                i.gce && i[by](e)
                        },
                        r = function(t) {
                            t[gy] = n(),
                                i[yy] && i.com(t)
                        },
                        h = function(e) {
                            t.readByte(),
                                e[my] = t[ey](12),
                                e.ptData = n(),
                                i[py] && i[py](e)
                        },
                        a = function(e) {
                            var s = function(e) {
                                    t.readByte(),
                                        e[Ey] = t[iy](),
                                        e.iterations = t[xy](),
                                        e.terminator = t.readByte(),
                                        i[Ty] && i.app.NETSCAPE && i[Ty].NETSCAPE(e)
                                },
                                r = function(t) {
                                    t[wy] = n(),
                                        i[Ty] && i[Ty][t.identifier] && i[Ty][t.identifier](t)
                                };
                            switch (t[iy](), e[Oy] = t.read(8), e[Iy] = t.read(3), e.identifier) {
                                case "NETSCAPE":
                                    s(e);
                                    break;
                                default:
                                    r(e)
                            }
                        },
                        o = function(t) {
                            t.data = n(),
                                i.unknown && i.unknown(t)
                        };
                    switch (e.label = t.readByte(), e[Ay]) {
                        case 249:
                            e.extType = by,
                                s(e);
                            break;
                        case 254:
                            e[Sy] = yy,
                                r(e);
                            break;
                        case 1:
                            e.extType = py,
                                h(e);
                            break;
                        case 255:
                            e.extType = Ty,
                                a(e);
                            break;
                        default:
                            e[Sy] = Ey,
                                o(e)
                    }
                },
                h = function(s) {
                    var r = function(t, i) {
                        for (var e = new Array(t.length), n = t.length / i, s = function(n, s) {
                                    var r = t.slice(s * i, (s + 1) * i);
                                    e.splice.apply(e, [n * i, i][Ka](r))
                                },
                                r = [0, 4, 2, 1], h = [8, 8, 4, 2], a = 0, o = 0; 4 > o; o++)
                            for (var _ = r[o]; n > _; _ += h[o]) s(_, a),
                                a++;
                        return e
                    };
                    s.leftPos = t.readUnsigned(),
                        s.topPos = t[xy](),
                        s[xa] = t.readUnsigned(),
                        s[Fa] = t.readUnsigned();
                    var h = wP(t.readByte());
                    s.lctFlag = h.shift(),
                        s[Cy] = h[_y](),
                        s.sorted = h.shift(),
                        s[ky] = h[Gr](0, 2),
                        s.lctSize = TP(h[Gr](0, 3)),
                        s.lctFlag && (s.lct = e(1 << s[Ly] + 1)),
                        s[Ry] = t.readByte();
                    var a = n();
                    s.pixels = IP(s.lzwMinCodeSize, a),
                        s.interlaced && (s.pixels = r(s.pixels, s[xa])),
                        i.img && i.img(s)
                },
                a = function() {
                    var e = {};
                    switch (e[Dy] = t[iy](), String.fromCharCode(e.sentinel)) {
                        case "!":
                            e.type = My,
                                r(e);
                            break;
                        case ",":
                            e[N_] = ou,
                                h(e);
                            break;
                        case ";":
                            e[N_] = Py,
                                i[Py] && i[Py](e);
                            break;
                        default:
                            throw new Error(Ny + e[Dy].toString(16))
                    }
                    Py !== e[N_] && setTimeout(a, 0)
                },
                o = function() {
                    s(),
                        setTimeout(a, 0)
                };
            o()
        },
        SP = "";
    i.addEventListener && i.addEventListener(jy,
        function(t) {
            if (t[By] && t.shiftKey && t.altKey && 73 == t.keyCode) {
                var i = DM.name + zy + DM[$y] + Gy + DM[Fy] + Na + DM.about + Na + DM.copyright + SP;
                DM[Yy](i)
            }
        }, !1);
    var CP = qy;
    SP = Hy + decodeURIComponent(Uy);
    var kP,
        LP,
        RP,
        DP = t,
        MP = Wy,
        PP = Xy,
        NP = Vy,
        jP = Ky,
        BP = Zy,
        zP = Jy,
        $P = Qy,
        GP = tm,
        FP = im,
        YP = em,
        qP = nm,
        HP = sm,
        UP = rm,
        WP = hm,
        XP = am,
        VP = om,
        KP = _m,
        ZP = fm,
        JP = um,
        QP = cm,
        tN = dm,
        iN = DP['setTimeout'];
    //debugger
    iN && (LP = DP[WP + vm][BP + bm], iN[Br](DP, Ze, VP), iN[Br](DP, function() {
        nN && nN == CP && (dN = !1)
    }, KP));
    var eN,
        nN,
        sN,
        rN = 111,
        hN = function(t, i) {
            i || (i = gm + PP + ym);
            try {
                RP[Br](t, i, 6 * rN, 1 * rN)
            } catch (e) {}
        },
        aN = !0,
        oN = !0,
        _N = !0,
        fN = !0,
        uN = !0,
        cN = !0,
        dN = !0,
        lN = OD ? 200 : 1024,
        vN = function(t, i) {
            return Ke ? Ke(t, i) || "" : void 0
        };
    if (i.createElement) {
        var bN = i[Ra](mm);
        bN[Oa].display = bu,
            bN.onload = function(t) {
                var i = t.target[pm],
                    e = LP;
                return void this[Hv].parentNode.removeChild(this[Hv]);
            };
        var gN = i[Ra](hu);
        gN.style.width = Eu,
            gN[Oa][Fa] = Eu,
            gN.style.overflow = vu,
            gN.appendChild(bN),
            i.documentElement[If](gN)
    }
    if (i[XP + Tm]) {
        //debugger       
    }
    var mN = function(t, i, e, n) {
        this.source = t,
            this[Ku] = i,
            this[Jc] = n,
            this.value = e
    };
    mN.prototype = {
            propertyType: MM[Tf]
        },
        N(mN, KD);
    var pN = function(t) {
        this.id = ++uD,
            this._mxr = {},
            this._ii = {},
            t && (this[Dm] = t)
    };
    pN[ah] = {
            _ii: null,
            getStyle: function(t) {
                return this._ii[t]
            },
            setStyle: function(t, i) {
                var e = this._ii[t];
                return e === i || e && i && e.equals && e[Mm](i) ? !1 : this._myk(t, i, new mN(this, t, i, e), this._ii)
            },
            putStyles: function(t, i) {
                for (var e in t) {
                    var n = t[e];
                    i ? this._ii[e] = n : this.setStyle(e, n)
                }
            },
            _$w: !0,
            invalidateVisibility: function(t) {
                this._$w = !0,
                    t || (this instanceof xN && this[Pm]() && this[Nm](function(t) {
                            t._$w = !0
                        }),
                        this._hd() && this.hasChildren() && this.forEachChild(function(t) {
                            t.invalidateVisibility()
                        }))
            },
            onParentChanged: function() {
                this.invalidateVisibility()
            },
            _hd: function() {
                return !this._4a && this instanceof ON
            },
            invalidate: function() {
                this.onEvent(new VD(this, jm, Bm))
            },
            _mxh: null,
            addUI: function(t, i) {
                if (this._mxh || (this._mxh = new LD), this._mxh[j_](t.id)) return !1;
                var e = {
                    id: t.id,
                    ui: t,
                    bindingProperties: i
                };
                this._mxh[Lh](e);
                var n = new VD(this, jm, Lh, e);
                return this[cd](n)
            },
            removeUI: function(t) {
                if (!this._mxh) return !1;
                var i = this._mxh[pc](t.id);
                return i ? (this._mxh[Zr](i), void this[cd](new VD(this, jm, Zr, i))) : !1
            },
            toString: function() {
                return this.$name || this.id
            },
            type: zm,
            _4a: !1
        },
        N(pN, hM),
        H(pN.prototype, [$m, so, Gm]),
        Z(pN.prototype, {
            enableSubNetwork: {
                get: function() {
                    return this._4a
                },
                set: function(t) {
                    if (this._4a != t) {
                        var i = this._4a;
                        this._4a = t,
                            this instanceof xN && this._11(),
                            this.onEvent(new KD(this, Fm, t, i))
                    }
                }
            },
            bindingUIs: {
                get: function() {
                    return this._mxh
                }
            },
            styles: {
                get: function() {
                    return this._ii
                },
                set: function(t) {
                    if (this._ii != t) {
                        for (var i in this._ii) i in t || (t[i] = e);
                        this.putStyles(t),
                            this._ii = t
                    }
                }
            }
        });
    var EN = function(t, i, e) {
        this.id = ++uD,
            this._mxr = {},
            this._ii = {},
            e && (this[Dm] = e),
            this[Ym] = t,
            this[cf] = i,
            this[qm]()
    };
    EN.prototype = {
            $uiClass: rs,
            _jj: null,
            _i2: null,
            _jk: null,
            _i4: null,
            _do: !1,
            type: Hm,
            otherNode: function(t) {
                return t == this.from ? this.to : t == this.to ? this[Um] : void 0
            },
            connect: function() {
                if (this._do) return !1;
                if (!this.$from || !this.$to) return !1;
                if (this._do = !0, this[Ym] == this.$to) return void this.$from._hs(this);
                Tn(this.$to, this),
                    En(this.$from, this),
                    _n(this[Ym], this, this[cf]);
                var t = this.fromAgent,
                    i = this.toAgent;
                if (t != i) {
                    var e;
                    this.$from._dc && (un(t, this, i), e = !0),
                        this[cf]._dc && (dn(i, this, t), e = !0),
                        e && _n(t, this, i)
                }
            },
            disconnect: function() {
                if (!this._do) return !1;
                if (this._do = !1, this[Ym] == this.$to) return void this.$from._mxx(this);
                xn(this[Ym], this),
                    wn(this[cf], this),
                    fn(this[Ym], this, this[cf]);
                var t = this.fromAgent,
                    i = this[z_];
                if (t != i) {
                    var e;
                    this[Ym]._dc && (cn(t, this, i), e = !0),
                        this.$to._dc && (ln(i, this, t), e = !0),
                        e && fn(t, this, i)
                }
            },
            isConnected: function() {
                return this._do
            },
            isInvalid: function() {
                return !this.$from || !this.$to
            },
            isLooped: function() {
                return this[Ym] == this[cf]
            },
            getEdgeBundle: function(t) {
                return t ? this._32() : this.isLooped() ? this[Ym]._49 : this.$from[$_](this[cf])
            },
            hasEdgeBundle: function() {
                var t = this[$_](!0);
                return t && t.edges[jr] > 1
            },
            _32: function() {
                var t = this.fromAgent,
                    i = this.toAgent;
                return t == i ? this[Ym]._dc || this.$to._dc ? null : this[Ym]._49 : this.fromAgent[$_](this[z_])
            },
            _9q: null,
            hasPathSegments: function() {
                return this._9q && !this._9q.isEmpty()
            },
            isBundleEnabled: function() {
                return this.bundleEnabled && !this.hasPathSegments()
            },
            firePathChange: function(t) {
                this.onEvent(new KD(this, Wm, t))
            },
            addPathSegement: function(t, i, e) {
                var n = new dP(i || oP, t);
                this._9q || (this._9q = new LD),
                    this._9q.add(n, e),
                    this.firePathChange(n)
            },
            removePathSegementByIndex: function(t) {
                if (!this._9q) return !1;
                var i = this._9q.getByIndex(t);
                i && (this._9q.remove(i), this[Xm](i))
            },
            removePathSegement: function(t) {
                return this._9q ? (this._9q.remove(t), void this.firePathChange(t)) : !1
            },
            movePathSegment: function(t, i, e) {
                if (!this._9q) return !1;
                if (t = t || 0, i = i || 0, DM.isNumber(e)) {
                    var n = this._9q[yc](e);
                    return n ? (n.move(t, i), void this[Xm]()) : !1
                }
                l(function(e) {
                        e[ml](t, i)
                    }),
                    this[Xm]()
            }
        },
        N(EN, pN),
        Z(EN.prototype, {
            pathSegments: {
                get: function() {
                    return this._9q
                },
                set: function(t) {
                    this._9q = t,
                        this.firePathChange()
                }
            },
            from: {
                get: function() {
                    return this[Ym]
                },
                set: function(t) {
                    if (this.$from != t) {
                        var i = new KD(this, Um, t, this.$from);
                        this.beforeEvent(i) !== !1 && (this[Vm](), this[Ym] = t, this.connect(), this.onEvent(i))
                    }
                }
            },
            to: {
                get: function() {
                    return this.$to
                },
                set: function(t) {
                    if (this.$to != t) {
                        var i = new KD(this, Km, t, this[cf]);
                        this.beforeEvent(i) !== !1 && (this.disconnect(), this[cf] = t, this[qm](), this.onEvent(i))
                    }
                }
            },
            fromAgent: {
                get: function() {
                    return this.$from ? this.$from._dc || this.$from : null
                }
            },
            toAgent: {
                get: function() {
                    return this[cf] ? this.$to._dc || this[cf] : null
                }
            }
        }),
        H(EN[ah], [Zm, {
                name: Jm,
                value: !0
            },
            Qf
        ]);
    var xN = function(t, i, e) {
        this.id = ++uD,
            this._mxr = {},
            this._ii = {},
            t && (this[Dm] = t),
            this[df] = Qm,
            this.$anchorPosition = $D.CENTER_MIDDLE,
            this[tp] = {
                x: i || 0,
                y: e || 0
            },
            this._linkedNodes = {}
    };
    xN[ah] = {
            $uiClass: hs,
            _dc: null,
            forEachEdge: function(t, i, e) {
                return !e && this._jz && this._jz[d_](t, i) === !1 ? !1 : In(this, t, i)
            },
            forEachOutEdge: function(t, i) {
                return An(this, t, i)
            },
            forEachInEdge: function(t, i) {
                return Sn(this, t, i)
            },
            getEdges: function() {
                var t = [];
                return this.forEachEdge(function(i) {
                        t[Yr](i)
                    }),
                    t
            },
            _h8: null,
            _f7: null,
            _j8: null,
            _h6: null,
            _my6: 0,
            _9e: 0,
            hasInEdge: function() {
                return null != this._h8
            },
            hasOutEdge: function() {
                return null != this._f7
            },
            hasEdge: function() {
                return null != this._h8 || null != this._f7 || this[ip]()
            },
            linkedWith: function(t) {
                return t[Um] == this || t.to == this || t[W_] == this || t.toAgent == this
            },
            hasEdgeWith: function(t) {
                var i = this.getEdgeBundle(t);
                return i && i.edges.length > 0
            },
            _jz: null,
            _49: null,
            hasLoops: function() {
                return this._jz && this._jz[jr] > 0
            },
            _hs: function(t) {
                return this._jz || (this._jz = new LD, this._49 = new zj(this, this, this._jz)),
                    this._49._i0(t)
            },
            _mxx: function(t) {
                return this._49 ? this._49._n0s(t) : void 0
            },
            getEdgeBundle: function(t) {
                return t == this ? this._49 : this._linkedNodes[t.id] || t._linkedNodes[this.id]
            },
            _6f: function() {
                return this._95 && this._95[jr]
            },
            _57: function() {
                return this._7r && this._7r.length
            },
            _8t: function() {
                return this._6f() || this._57()
            },
            _7r: null,
            _95: null,
            _mxv: function() {
                var t = this._dc,
                    i = on(this);
                if (t != i) {
                    var e = On(this);
                    this._8z(i),
                        e.forEach(function(t) {
                                var i = t.fromAgent,
                                    e = t.toAgent,
                                    t = t[uf],
                                    n = t.$from._dc,
                                    s = t[cf]._dc;
                                i != e && (i && cn(i, t, e || t[cf]), e && ln(e, t, i || t[Ym])),
                                    n != s && (n && un(n, t, s || t[cf]), s && dn(s, t, n || t.$from), _n(n || t.$from, t, s || t.$to))
                            },
                            this)
                }
            },
            onParentChanged: function() {
                this[ep](),
                    this._mxv()
            },
            _7p: null,
            _11: function() {
                var t;
                if (this._4a ? t = null : (t = this._dc, t || this._gn !== !1 || (t = this)), this._7p == t) return !1;
                if (this._7p = t, this._ez && this._ez._im.length)
                    for (var i, e = this._ez._im, n = 0, s = e[jr]; s > n; n++) i = e[n],
                        i instanceof xN && i._8z(t)
            },
            setLocation: function(t, i) {
                if (this.$location && this.$location.x == t && this.$location.y == i) return !1;
                var e = new KD(this, np, this.$location, {
                    x: t,
                    y: i
                });
                return this[fd](e) === !1 ? !1 : (this.$location ? (this[tp].x = t, this.$location.y = i) : this.$location = new MD(t, i), this[cd](e), !0)
            },
            _dk: null,
            addFollower: function(t) {
                return null == t ? !1 : t.host = this
            },
            removeFollower: function(t) {
                return this._dk && this._dk.contains(t) ? t[sp] = null : !1
            },
            hasFollowers: function() {
                return this._dk && !this._dk.isEmpty()
            },
            toFollowers: function() {
                return this[rp]() ? this._dk.toDatas() : null
            },
            clearFollowers: function() {
                this[rp]() && (this.toFollowers(), l(this[hp](),
                    function(t) {
                        t[sp] = null
                    }))
            },
            getFollowerIndex: function(t) {
                return this._dk && this._dk.contains(t) ? this._dk[Kr](t) : -1
            },
            setFollowerIndex: function(t, i) {
                return this._dk && this._dk.contains(t) ? void this._dk[U_](t, i) : -1
            },
            getFollowerCount: function() {
                return null == !this._dk ? 0 : this._dk[jr]
            },
            _94: function() {
                return this._dk ? this._dk : (this._dk = new LD, this._dk)
            },
            isFollow: function(t) {
                if (!t || !this._host) return !1;
                for (var i = this._host; i;) {
                    if (i == t) return !0;
                    i = i._host
                }
                return !1
            },
            _8z: function(t) {
                return t == this._dc ? !1 : (this._dc = t, this[ep](), void this._11())
            },
            type: ap
        },
        N(xN, pN),
        Z(xN[ah], {
            loops: {
                get: function() {
                    return this._jz
                }
            },
            edgeCount: {
                get: function() {
                    return this._my6 + this._9e
                }
            },
            agentNode: {
                get: function() {
                    return this._dc || this
                }
            },
            host: {
                set: function(t) {
                    if (this == t || t == this._host) return !1;
                    var i = new KD(this, sp, this._host, t);
                    if (!1 === this.beforeEvent(i)) return !1;
                    var e = null,
                        n = null,
                        s = this._host;
                    if (null != t && (e = new KD(t, op, null, this), !1 === t.beforeEvent(e))) return !1;
                    if (null != s && (n = new KD(s, _p, null, this), !1 === s.beforeEvent(n))) return !1;
                    if (this._host = t, null != t) {
                        var r = t._94();
                        r[Lh](this)
                    }
                    if (null != s) {
                        var r = s._94();
                        r[Zr](this)
                    }
                    return this[cd](i),
                        null != t && t.onEvent(e),
                        null != s && s[cd](n), !0
                },
                get: function() {
                    return this._host
                }
            }
        }),
        H(xN[ah], [np, fp, vb, Za, up]),
        Z(xN.prototype, {
            x: {
                get: function() {
                    return this.location.x
                },
                set: function(t) {
                    t != this.location.x && (this.location = new MD(t, this.location.y))
                }
            },
            y: {
                get: function() {
                    return this[np].y
                },
                set: function(t) {
                    t != this.location.y && (this.location = new MD(this.location.x, t))
                }
            }
        });
    var TN = function(t, i) {
        t instanceof vP && (i = t, t = e),
            j(this, TN, [t]),
            this[cp] = i || new vP,
            this.image = this.$path,
            this[up] = null,
            this[$m] = er,
            CD.SHAPENODE_STYLES || (CD[dp] = {},
                CD.SHAPENODE_STYLES[SN[lp]] = !1),
            this[vp](CD.SHAPENODE_STYLES)
    };
    TN.prototype = {
            $uiClass: er,
            type: bp,
            moveTo: function(t, i) {
                this[gp].moveTo(t, i),
                    this[Xm]()
            },
            lineTo: function(t, i) {
                this[gp].lineTo(t, i),
                    this[Xm]()
            },
            quadTo: function(t, i, e, n) {
                this[gp].quadTo(t, i, e, n),
                    this[Xm]()
            },
            curveTo: function(t, i, e, n, s, r) {
                this[gp].curveTo(t, i, e, n, s, r),
                    this[Xm]()
            },
            arcTo: function(t, i, e, n, s) {
                this.path[Bv](t, i, e, n, s),
                    this[Xm]()
            },
            closePath: function() {
                this.path.closePath(),
                    this[Xm]()
            },
            clear: function() {
                this[gp][Ec](),
                    this.firePathChange()
            },
            firePathChange: function() {
                this[gp]._6a = !0,
                    this.onEvent(new KD(this, Wm))
            }
        },
        N(TN, xN),
        H(TN.prototype, [gp]),
        Z(TN.prototype, {
            pathSegments: {
                get: function() {
                    return this[gp]._eq
                }
            },
            length: {
                get: function() {
                    return this[gp][jr]
                }
            }
        }),
        DM.ShapeNode = TN;
    var wN = {
        _jf: {},
        register: function(t, i) {
            wN._jf[t] = i
        },
        getShape: function(t, i, n, s, r, h) {
            s === e && (s = i, r = n, i = 0, n = 0),
                s || (s = 50),
                r || (r = 50);
            var a = wN._jf[t];
            return a ? a[yp] instanceof Function ? a.generator(i, n, s, r, h) : a : void 0
        },
        getRect: function(t, i, e, n, s, r, h) {
            return Cn(h || new vP, t, i, e, n, s, r)
        },
        getAllShapes: function(t, i, e, n, s) {
            var r = {};
            for (var h in wN._jf) {
                var a = wN[Kf](h, t, i, e, n, s);
                a && (r[h] = a)
            }
            return r
        },
        createRegularShape: function(t, i, e, n, s) {
            return Nn(t, i, e, n, s)
        }
    };
    Vn(),
        Kn.prototype = {
            type: mp
        },
        N(Kn, TN),
        DM.Bus = Kn,
        Zn.prototype = {
            _f5: function(t) {
                var i,
                    e = t._iy;
                i = e ? e._ez : this[Rd];
                var n = i[Kr](t);
                if (0 > n) throw new Error(Pd + t + "' not exist in the box");
                for (; n >= 0;) {
                    if (0 == n) return e instanceof xN ? e : null;
                    n -= 1;
                    var r = i.getByIndex(n);
                    if (r = s(r)) return r
                }
                return null
            },
            forEachNode: function(t, i) {
                this[d_](function(e) {
                    return e instanceof xN && t[Br](i, e) === !1 ? !1 : void 0
                })
            },
            _3q: null
        },
        N(Zn, oM),
        Z(Zn[ah], {
            propertyChangeDispatcher: {
                get: function() {
                    return this._$u
                }
            },
            randomNode: {
                get: function() {
                    return this._k2Model.randomNode
                }
            },
            currentSubNetwork: {
                get: function() {
                    return this._3q
                },
                set: function(t) {
                    if (t && !t[Fm] && (t = null), this._3q != t) {
                        var i = this._3q;
                        this._3q = t,
                            this._$u[cd](new KD(this, pp, t, i))
                    }
                }
            }
        }),
        CD[Ep] = MM[xp],
        CD[Tp] = 5,
        CD.GROUP_EXPANDED = !0,
        CD.GROUP_MIN_SIZE = {
            width: 60,
            height: 60
        };
    var ON = function(t, i, n) {
        j(this, ON, arguments), (i === e || n === e) && (this.$location.invalidateFlag = !0),
            this[wp] = CD.GROUP_TYPE,
            this[Bo] = CD.GROUP_PADDING,
            this.$image = yP.group,
            this.$minSize = CD.GROUP_MIN_SIZE,
            this.expanded = CD.GROUP_EXPANDED
    };
    ON[ah] = {
            type: Op,
            $uiClass: Zs,
            _9t: function() {
                return !this._gn && !this._dc
            },
            forEachOutEdge: function(t, i, e) {
                return An(this, t, i) === !1 ? !1 : !e && this._9t() && this._7r ? this._7r[d_](t, i) : void 0
            },
            forEachInEdge: function(t, i, e) {
                return Sn(this, t, i) === !1 ? !1 : !e && this._9t() && this._95 ? this._95[d_](t, i) : void 0
            },
            forEachEdge: function(t, i, e) {
                return B(this, ON, Nm, arguments) === !1 ? !1 : e || e || !this._9t() ? void 0 : this._95 && this._95.forEach(t, i) === !1 ? !1 : this._7r ? this._7r[d_](t, i) : void 0
            },
            hasInEdge: function(t) {
                return t ? null != this._h8 : null != this._h8 || this._6f()
            },
            hasOutEdge: function(t) {
                return t ? null != this._f7 : null != this._f7 || this._57()
            },
            hasEdge: function(t) {
                return t ? null != this._h8 || null != this._f7 : null != this._h8 || null != this._f7 || this._8t()
            }
        },
        N(ON, xN),
        Z(ON[ah], {
            expanded: {
                get: function() {
                    return this._gn
                },
                set: function(t) {
                    if (this._gn != t) {
                        var i = new KD(this, Ip, t, this._gn);
                        this[fd](i) !== !1 && (this._gn = t, this._11(), this[cd](i), this._dc || Jn.call(this))
                    }
                }
            }
        }),
        H(ON.prototype, [Ap, Sp, ao, Cp]),
        DM[kp] = ON,
        Qn[ah][N_] = Lp,
        N(Qn, xN),
        DM.Text = Qn;
    var IN = function(t) {
        this._iq = new BD,
            this._7x = new BD,
            this._f9 = new BD,
            this.id = ++uD,
            t && (this[go] = t)
    };
    IN.prototype = {
            invalidate: function() {
                this[Rp]()
            },
            _1p: !0,
            _iq: null,
            _7x: null,
            _f9: null,
            _9z: !1,
            _iw: 1,
            _ix: 1,
            _hk: !0,
            _7y: 0,
            _6h: 0,
            _iy: null,
            _9x: null,
            borderColor: Dp,
            borderLineDash: null,
            borderLineDashOffset: null,
            syncSelection: !0,
            syncSelectionStyles: !0,
            _1e: function() {
                this[Mp] = oi(this[up], this._7y, this._6h)
            },
            setMeasuredBounds: function(t, i, e, n) {
                return t instanceof Object && (e = t.x, n = t.y, i = t.height, t = t[xa]),
                    this._iq[xa] == t && this._iq.height == i && this._iq.x == e && this._iq.y == n ? !1 : void this._iq[Vo](e || 0, n || 0, t || 0, i || 0)
            },
            initialize: function() {},
            measure: function() {},
            draw: function() {},
            _7m: function(t, i, e) {
                e[zb] == MM[Lv] ? (t[ab] = e.selectionColor, t[ob] = e.selectionShadowBlur * i, t.shadowOffsetX = (e.selectionShadowOffsetX || 0) * i, t[fb] = (e.selectionShadowOffsetY || 0) * i) : this._2e(t, i, e)
            },
            _2e: function(t, i, e) {
                var n = e[jb] || 0;
                e[Pp] && (t.fillStyle = e[Pp], t[Np](this._7x.x - n / 2, this._7x.y - n / 2, this._7x.width + n, this._7x.height + n)),
                    t.strokeStyle = e.selectionColor,
                    t.lineWidth = n,
                    t[jp](this._7x.x - n / 2, this._7x.y - n / 2, this._7x.width + n, this._7x.height + n)
            },
            _j0: function(t, i, e, n) {
                if (!this._hk) return !1;
                if (this.syncSelection || (e = this.selected), (e && !this.syncSelectionStyles || !n) && (n = this), t[Wv](), t[fo](this.$x, this.$y), this.$rotatable && this[Do] && t[Za](this[Do]), (this.offsetX || this.offsetY) && t[fo](this[Bp], this[zp]), this[Co] && t[Za](this.$rotate), this[Mo] && this._9x && t[fo](-this._9x.x, -this._9x.y), this[ab] && (t[ab] = this[ab], t[ob] = this[ob] * i, t.shadowOffsetX = this.shadowOffsetX * i, t.shadowOffsetY = this[fb] * i), e && n[zb] == MM.SELECTION_TYPE_BORDER_RECT && (this._2e(t, i, n), e = !1), this._$v() && this._ldShape && !this._ldShape._empty) {
                    this._ldShape[uo]();
                    var s = {
                        lineWidth: this.$border,
                        strokeStyle: this[$p],
                        lineDash: this.borderLineDash,
                        lineDashOffset: this.borderLineDashOffset,
                        fillColor: this.$backgroundColor,
                        fillGradient: this._mxackgroundGradient,
                        lineCap: Ov,
                        lineJoin: Ha
                    };
                    this._ldShape[io](t, i, s, e, n),
                        e = !1,
                        t[ab] = Zb
                }
                t.beginPath(),
                    this[io](t, i, e, n),
                    t.restore()
            },
            invalidateData: function() {
                this.$invalidateData = !0,
                    this._1p = !0
            },
            invalidateSize: function() {
                this[Po] = !0,
                    this._1p = !0
            },
            invalidateRender: function() {
                this._1p = !0
            },
            _53: function() {},
            _$v: function() {
                return this.$backgroundColor || this.$backgroundGradient || this[zo]
            },
            _4f: function() {
                return this[Gp] || this[Fp]
            },
            doValidate: function() {
                return this.$invalidateData && (this.$invalidateData = !1, this[Yp]() !== !1 && (this.$invalidateSize = !0)),
                    this[Po] && this.validateSize && this.validateSize(),
                    Fe.call(this) ? (this[Jo] = !0, this[qp] && this.onBoundsChanged(), !0) : this.$invalidateLocation ? (this[Hp] = !1, !0) : void 0
            },
            validate: function() {
                var t = this._hk;
                return this.$invalidateVisibility && (this[Up] = !1, this._hk = this[Wp], !this._hk || (this[Xp] || this.$showEmpty) && this._53() !== !1 || (this._hk = !1), !this._hk) ? t : this._hk ? (this._1p = !1, this._9z || (this.initialize(), this._9z = !0), this[Vp]()) : t
            },
            _h9: function(t, i, e, n) {
                if (t -= this.$x, i -= this.$y, !this._f9.intersectsPoint(t, i, e)) return !1;
                var s = Ge.call(this, {
                    x: t,
                    y: i
                });
                return t = s.x,
                    i = s.y, !n && this._$v() && this._ldShape && this._ldShape._h9(t, i, e, !1, this.$border, this[Gp] || this.$backgroundGradient) ? !0 : this._du ? this._du(t, i, e) : this._iq.intersectsPoint(t, i, e)
            },
            onDataChanged: function() {
                this.$invalidateData = !0,
                    this._1p = !0,
                    this[Up] = !0
            },
            getBounds: function() {
                var t = this._f9.clone();
                return t[jv](this.x, this.y),
                    this.parent && (this[B_].rotate && Di(t, this[B_][Za], t), t.offset(this[B_].x || 0, this[B_].y || 0)),
                    t
            },
            destroy: function() {
                this._hned = !0
            },
            _dw: !1
        },
        Z(IN.prototype, {
            data: {
                get: function() {
                    return this.$data
                },
                set: function(t) {
                    if (this[Xp] != t) {
                        var i = this[Xp];
                        this.$data = t,
                            this.onDataChanged(t, i)
                    }
                }
            },
            parent: {
                get: function() {
                    return this._iy
                }
            },
            showOnTop: {
                get: function() {
                    return this._dw
                },
                set: function(t) {
                    t != this._dw && (this._dw = t, this._1p = !0, this._iy && this._iy._my4 && this._iy._my4(this))
                }
            }
        }),
        is(IN.prototype, {
            visible: {
                value: !0,
                validateFlags: [Kp]
            },
            showEmpty: {
                validateFlags: [Kp]
            },
            anchorPosition: {
                value: $D[Gc],
                validateFlags: [Zp]
            },
            position: {
                value: $D.CENTER_MIDDLE,
                validateFlags: [Jp]
            },
            offsetX: {
                value: 0,
                validateFlags: [Jp]
            },
            offsetY: {
                value: 0,
                validateFlags: [Jp]
            },
            layoutByAnchorPoint: {
                value: !0,
                validateFlags: [Al, Zp]
            },
            padding: {
                value: 0,
                validateFlags: [Al]
            },
            border: {
                value: 0,
                validateFlags: [Al]
            },
            borderRadius: {
                value: CD.BORDER_RADIUS
            },
            showPointer: {
                value: !1,
                validateFlags: [Al]
            },
            pointerX: {
                value: 0,
                validateFlags: [Al]
            },
            pointerY: {
                value: 0,
                validateFlags: [Al]
            },
            pointerWidth: {
                value: CD.POINTER_WIDTH
            },
            backgroundColor: {
                validateFlags: [Al]
            },
            backgroundGradient: {
                validateFlags: [Al, Qp]
            },
            selected: {
                value: !1,
                validateFlags: [Al]
            },
            selectionBorder: {
                value: CD[Dv],
                validateFlags: [Al]
            },
            selectionShadowBlur: {
                value: CD[tE],
                validateFlags: [Al]
            },
            selectionColor: {
                value: CD[iE],
                validateFlags: [Al]
            },
            selectionType: {
                value: CD[kv],
                validateFlags: [Al]
            },
            selectionShadowOffsetX: {
                value: 0,
                validateFlags: [Al]
            },
            selectionShadowOffsetY: {
                value: 0,
                validateFlags: [Al]
            },
            shadowBlur: {
                value: 0,
                validateFlags: [Al]
            },
            shadowColor: {
                validateFlags: [Al]
            },
            shadowOffsetX: {
                value: 0,
                validateFlags: [Al]
            },
            shadowOffsetY: {
                value: 0,
                validateFlags: [Al]
            },
            renderColorBlendMode: {},
            renderColor: {},
            x: {
                value: 0,
                validateFlags: [Jp]
            },
            y: {
                value: 0,
                validateFlags: [Jp]
            },
            rotatable: {
                value: !0,
                validateFlags: [eE, Al]
            },
            rotate: {
                value: 0,
                validateFlags: [eE, Al]
            },
            _hostRotate: {
                validateFlags: [eE]
            },
            lineWidth: {
                value: 0,
                validateFlags: [nE]
            }
        });
    var AN = [MM[Ef], MM[Tf], MM[xf]];
    ns[ah] = {
        removeBinding: function(t) {
            for (var i = AN.length; --i >= 0;) {
                var e = AN[i],
                    n = this[e];
                for (var s in n) {
                    var r = n[s];
                    Array[gf](r) ? (v(r,
                        function(i) {
                            return i[sE] == t
                        },
                        this), r.length || delete n[s]) : r.target == t && delete n[s]
                }
            }
        },
        _2b: function(t, i, e) {
            if (!e && (e = this[i.propertyType || MM.PROPERTY_TYPE_ACCESSOR], !e)) return !1;
            var n = e[t];
            n ? (Array.isArray(n) || (e[t] = n = [n]), n[Yr](i)) : e[t] = i
        },
        _2u: function(t, i, e, n, s, r) {
            t = t || MM[Ef];
            var h = this[t];
            if (!h) return !1;
            var a = {
                property: i,
                propertyType: t,
                bindingProperty: n,
                target: e,
                callback: s,
                invalidateSize: r
            };
            this._2b(i, a, h)
        },
        onBindingPropertyChange: function(t, i, e, n) {
            var s = this[e || MM[Ef]];
            if (!s) return !1;
            var r = s[i];
            return r ? (t._1p = !0, es(t, r, e, n), !0) : !1
        },
        initBindingProperties: function(t, i) {
            for (var n = AN.length; --n >= 0;) {
                var s = AN[n],
                    r = this[s];
                for (var h in r) {
                    var a = r[h];
                    if (a.bindingProperty) {
                        var o = a[sE];
                        if (o) {
                            if (!(o instanceof IN || (o = t[o]))) continue
                        } else o = t;
                        var _;
                        _ = i === !1 ? t[rE](a.property, s) : s == MM.PROPERTY_TYPE_STYLE ? t[Jf][Lf](t[Xp], a.property) : t[Xp][a[yf]],
                            _ !== e && (o[a[mf]] = _)
                    }
                }
            }
        }
    };
    var SN = {};
    SN[iE] = hE,
        SN[Dv] = aE,
        SN[tE] = "selection.shadow.blur",
        SN.SELECTION_SHADOW_OFFSET_X = "selection.shadow.offset.x",
        SN.SELECTION_SHADOW_OFFSET_Y = "selection.shadow.offset.y",
        SN[kv] = oE,
        SN.RENDER_COLOR = _E,
        SN[fE] = "render.color.blend.mode",
        SN.SHADOW_BLUR = uE,
        SN[cE] = dE,
        SN[lE] = vE,
        SN[bE] = gE,
        SN.SHAPE_STROKE = yE,
        SN[mE] = pE,
        SN.SHAPE_LINE_DASH = EE,
        SN[xE] = "shape.line.dash.offset",
        SN[TE] = wE,
        SN[OE] = IE,
        SN[AE] = SE,
        SN[CE] = kE,
        SN.LINE_CAP = LE,
        SN.LINE_JOIN = RE,
        SN.LAYOUT_BY_PATH = DE,
        SN[ME] = PE,
        SN.BACKGROUND_GRADIENT = NE,
        SN[jE] = BE,
        SN[zE] = $E,
        SN[GE] = FE,
        SN.BORDER_LINE_DASH_OFFSET = "border.line.dash.offset",
        SN.BORDER_RADIUS = YE,
        SN.PADDING = ao,
        SN.IMAGE_BACKGROUND_COLOR = "image.background.color",
        SN.IMAGE_BACKGROUND_GRADIENT = "image.background.gradient",
        SN[qE] = HE,
        SN[UE] = SN.IMAGE_BORDER_COLOR = WE,
        SN.IMAGE_BORDER_LINE_DASH = "image.border.line.dash",
        SN.IMAGE_BORDER_LINE_DASH_OFFSET = "image.border.line.dash.offset",
        SN[XE] = SN.IMAGE_BORDER_RADIUS = VE,
        SN.IMAGE_PADDING = KE,
        SN[ZE] = JE,
        SN.LABEL_ROTATE = QE,
        SN[tx] = ix,
        SN.LABEL_ANCHOR_POSITION = "label.anchor.position",
        SN.LABEL_COLOR = ex,
        SN[nx] = sx,
        SN.LABEL_FONT_FAMILY = rx,
        SN.LABEL_FONT_STYLE = hx,
        SN[ax] = ox,
        SN.LABEL_POINTER_WIDTH = _x,
        SN.LABEL_POINTER = fx,
        SN[ux] = cx,
        SN[dx] = lx,
        SN[vx] = bx,
        SN[gx] = yx,
        SN[mx] = px,
        SN.LABEL_BORDER = Ex,
        SN.LABEL_BORDER_STYLE = xx,
        SN[Tx] = "label.background.color",
        SN[wx] = "label.background.gradient",
        SN.LABEL_ROTATABLE = Ox,
        SN.LABEL_SHADOW_BLUR = Ix,
        SN[Ax] = Sx,
        SN.LABEL_SHADOW_OFFSET_X = "label.shadow.offset.x",
        SN.LABEL_SHADOW_OFFSET_Y = "label.shadow.offset.y",
        SN[Cx] = kx,
        SN.LABEL_ON_TOP = Lx,
        SN.GROUP_BACKGROUND_COLOR = "group.background.color",
        SN[Rx] = "group.background.gradient",
        SN.GROUP_STROKE = Dx,
        SN.GROUP_STROKE_STYLE = Mx,
        SN[Px] = "group.stroke.line.dash",
        SN.GROUP_STROKE_LINE_DASH_OFFSET = "group.stroke.line.dash.offset",
        SN[Nx] = "edge.bundle.label.rotate",
        SN[jx] = "edge.bundle.label.position",
        SN.EDGE_BUNDLE_LABEL_ANCHOR_POSITION = "edge.bundle.label.anchor.position",
        SN.EDGE_BUNDLE_LABEL_COLOR = "edge.bundle.label.color",
        SN[Bx] = "edge.bundle.label.font.size",
        SN[zx] = "edge.bundle.label.font.family",
        SN[$x] = "edge.bundle.label.font.style",
        SN.EDGE_BUNDLE_LABEL_PADDING = "edge.bundle.label.padding",
        SN[Gx] = "edge.bundle.label.pointer.width",
        SN.EDGE_BUNDLE_LABEL_POINTER = "edge.bundle.label.pointer",
        SN[Fx] = "edge.bundle.label.radius",
        SN[Yx] = "edge.bundle.label.offset.x",
        SN.EDGE_BUNDLE_LABEL_OFFSET_Y = "edge.bundle.label.offset.y",
        SN.EDGE_BUNDLE_LABEL_BORDER = "edge.bundle.label.border",
        SN.EDGE_BUNDLE_LABEL_BORDER_STYLE = "edge.bundle.label.border.color",
        SN[qx] = "edge.bundle.label.background.color",
        SN.EDGE_BUNDLE_LABEL_BACKGROUND_GRADIENT = "edge.bundle.label.background.gradient",
        SN[Hx] = "edge.bundle.label.rotatable",
        SN[Ux] = Wx,
        SN[Xx] = Vx,
        SN.EDGE_OUTLINE = Kx,
        SN[Zx] = Jx,
        SN[Qx] = tT,
        SN.EDGE_LINE_DASH_OFFSET = "edge.line.dash.offset",
        SN[iT] = eT,
        SN.EDGE_TO_OFFSET = nT,
        SN.EDGE_BUNDLE_GAP = sT,
        SN[nu] = rT,
        SN.EDGE_EXTEND = hT,
        SN.EDGE_CONTROL_POINT = aT,
        SN[oT] = "edge.split.by.percent",
        SN[_T] = fT,
        SN[Rf] = uT,
        SN.EDGE_CORNER = cT,
        SN[Yf] = dT,
        SN.ARROW_FROM = lT,
        SN[vT] = bT,
        SN.ARROW_FROM_OFFSET = gT,
        SN.ARROW_FROM_STROKE = yT,
        SN[mT] = "arrow.from.stroke.style",
        SN.ARROW_FROM_OUTLINE = pT,
        SN[ET] = "arrow.from.outline.style",
        SN[xT] = TT,
        SN[wT] = "arrow.from.line.dash.offset",
        SN[OT] = "arrow.from.fill.color",
        SN[IT] = "arrow.from.fill.gradient",
        SN[AT] = ST,
        SN[CT] = kT,
        SN[lp] = LT,
        SN.ARROW_TO_SIZE = RT,
        SN[DT] = MT,
        SN[PT] = NT,
        SN[jT] = "arrow.to.stroke.style",
        SN.ARROW_TO_OUTLINE = BT,
        SN[zT] = "arrow.to.outline.style",
        SN.ARROW_TO_LINE_DASH = $T,
        SN.ARROW_TO_LINE_DASH_OFFSET = "arrow.to.line.dash.offset",
        SN[GT] = FT,
        SN.ARROW_TO_FILL_GRADIENT = "arrow.to.fill.gradient",
        SN[YT] = qT,
        SN.ARROW_TO_LINE_JOIN = HT;
    var CN = new ns,
        kN = MM[Ef],
        LN = MM[Tf],
        RN = !1;
    CN._2u(LN, SN[kv], null, zb),
        CN._2u(LN, SN.SELECTION_BORDER, null, jb),
        CN._2u(LN, SN[tE], null, Rb),
        CN._2u(LN, SN[iE], null, Db),
        CN._2u(LN, SN.SELECTION_SHADOW_OFFSET_X, null, "selectionShadowOffsetX"),
        CN._2u(LN, SN[UT], null, "selectionShadowOffsetY"),
        CN._2u(kN, so, Ay, go),
        CN._2u(LN, SN.LABEL_POSITION, Ay, Ko),
        CN._2u(LN, SN.LABEL_ANCHOR_POSITION, Ay, up),
        CN._2u(LN, SN.LABEL_COLOR, Ay, WT),
        CN._2u(LN, SN.LABEL_FONT_SIZE, Ay, XT),
        CN._2u(LN, SN.LABEL_BORDER, Ay, Bl),
        CN._2u(LN, SN.LABEL_BORDER_STYLE, Ay, $p),
        CN._2u(LN, SN.LABEL_BACKGROUND_COLOR, Ay, VT),
        CN._2u(LN, SN.LABEL_ON_TOP, Ay, KT),
        RN || (CN._2u(LN, SN.SHADOW_BLUR, null, ob), CN._2u(LN, SN[cE], null, ab), CN._2u(LN, SN.SHADOW_OFFSET_X, null, _b), CN._2u(LN, SN[bE], null, fb), CN._2u(LN, SN.LABEL_FONT_FAMILY, Ay, ZT), CN._2u(LN, SN[JT], Ay, QT), CN._2u(LN, SN[mx], Ay, tw), CN._2u(LN, SN[iw], Ay, Za), CN._2u(LN, SN[ax], Ay, ao), CN._2u(LN, SN.LABEL_POINTER_WIDTH, Ay, ew), CN._2u(LN, SN[nw], Ay, Go), CN._2u(LN, SN.LABEL_RADIUS, Ay, sw), CN._2u(LN, SN[dx], Ay, Bp), CN._2u(LN, SN[vx], Ay, zp), CN._2u(LN, SN[rw], Ay, hw), CN._2u(LN, SN[wx], Ay, Yo), CN._2u(LN, SN.LABEL_SIZE, Ay, fp), CN._2u(LN, SN.LABEL_SHADOW_BLUR, Ay, ob), CN._2u(LN, SN.LABEL_SHADOW_COLOR, Ay, ab), CN._2u(LN, SN.LABEL_SHADOW_OFFSET_X, Ay, _b), CN._2u(LN, SN[aw], Ay, fb), CN._2u(LN, SN.LABEL_Z_INDEX, Ay, Gm), CN._2u(LN, SN[ow], null, cb), CN._2u(LN, SN.RENDER_COLOR_BLEND_MODE, null, _w));
    var DN = new ns;
    DN._2u(kN, np),
        DN._2u(kN, up, null, fw),
        DN._2u(kN, Za, null, Za),
        RN || (DN._2u(LN, SN[ME], null, VT), DN._2u(LN, SN.BACKGROUND_GRADIENT, null, Yo), DN._2u(LN, SN[uw], null, ao), DN._2u(LN, SN.BORDER, null, Bl), DN._2u(LN, SN.BORDER_RADIUS, null, sw), DN._2u(LN, SN[zE], null, $p), DN._2u(LN, SN.BORDER_LINE_DASH, null, cw), DN._2u(LN, SN[dw], null, lw)),
        DN._2u(kN, vb, vb, go, vw),
        DN._2u(kN, fp, vb, fp),
        DN._2u(LN, SN.SHAPE_STROKE, vb, Wa),
        DN._2u(LN, SN[mE], vb, nb),
        DN._2u(LN, SN.SHAPE_FILL_COLOR, vb, bw),
        RN || (DN._2u(LN, SN[AE], vb, Gb), DN._2u(LN, SN[CE], vb, $b), DN._2u(LN, SN[OE], vb, gw), DN._2u(LN, SN[yw], vb, n_), DN._2u(LN, SN[xE], vb, s_), DN._2u(LN, SN[mw], vb, Jb), DN._2u(LN, SN[pw], vb, Bb), DN._2u(LN, SN[Ew], vb, Zo), DN._2u(LN, SN[xw], vb, VT), DN._2u(LN, SN.IMAGE_BACKGROUND_GRADIENT, vb, Yo), DN._2u(LN, SN.IMAGE_PADDING, vb, ao), DN._2u(LN, SN.IMAGE_BORDER, vb, Bl), DN._2u(LN, SN.IMAGE_BORDER_RADIUS, vb, sw), DN._2u(LN, SN.IMAGE_BORDER_COLOR, vb, $p), DN._2u(LN, SN.IMAGE_BORDER_LINE_DASH, vb, cw), DN._2u(LN, SN[Tw], vb, lw), DN._2u(LN, SN[ZE], vb, Gm)),
        DN._2u(kN, Ip, null, null, ww),
        DN._2u(kN, Fm, null, null, ww);
    var MN = new ns;
    MN._2u(kN, Sp, null, null, Ow),
        MN._2u(kN, Cp, null, null, Ow),
        MN._2u(kN, Ap, null, null, Ow),
        MN._2u(kN, ao, null, null, Ow),
        MN._2u(LN, SN[Iw], Aw, bw),
        MN._2u(LN, SN.GROUP_BACKGROUND_GRADIENT, Aw, gw),
        MN._2u(LN, SN.GROUP_STROKE, Aw, Wa),
        MN._2u(LN, SN[Sw], Aw, nb),
        MN._2u(LN, SN.GROUP_STROKE_LINE_DASH, Aw, n_),
        MN._2u(LN, SN.GROUP_STROKE_LINE_DASH_OFFSET, Aw, s_);
    var PN = new ns;
    PN._2u(kN, Um, Aw, null, Cw),
        PN._2u(kN, Km, Aw, null, Cw),
        PN._2u(kN, Zm, Aw, null, Cw),
        PN._2u(LN, SN.EDGE_WIDTH, Aw, Wa),
        PN._2u(LN, SN.EDGE_COLOR, Aw, nb),
        PN._2u(LN, SN.ARROW_FROM, Aw, kw),
        PN._2u(LN, SN[lp], Aw, Lw),
        RN || (PN._2u(LN, SN[Rw], Aw, Gb), PN._2u(LN, SN[Zx], Aw, $b), PN._2u(LN, SN.EDGE_LINE_DASH, Aw, n_), PN._2u(LN, SN.EDGE_LINE_DASH_OFFSET, Aw, s_), PN._2u(LN, SN.EDGE_CONTROL_POINT, Aw, null, Cw), PN._2u(LN, SN.EDGE_FROM_OFFSET, Aw, null, Cw), PN._2u(LN, SN.EDGE_TO_OFFSET, Aw, null, Cw), PN._2u(LN, SN[mw], Aw, Jb), PN._2u(LN, SN[pw], Aw, Bb), PN._2u(kN, Wm, null, null, Cw, !0), PN._2u(kN, Qf, null, null, Cw, !0), PN._2u(LN, SN.ARROW_FROM_SIZE, Aw, Dw), PN._2u(LN, SN.ARROW_FROM_OFFSET, Aw, Mw), PN._2u(LN, SN[Pw], Aw, Nw), PN._2u(LN, SN[mT], Aw, jw), PN._2u(LN, SN[Bw], Aw, zw), PN._2u(LN, SN.ARROW_FROM_OUTLINE_STYLE, Aw, "fromArrowOutlineStyle"), PN._2u(LN, SN.ARROW_FROM_FILL_COLOR, Aw, $w), PN._2u(LN, SN[IT], Aw, "fromArrowFillGradient"), PN._2u(LN, SN.ARROW_FROM_LINE_DASH, Aw, Gw), PN._2u(LN, SN[wT], Aw, "fromArrowLineDashOffset"), PN._2u(LN, SN[CT], Aw, Fw), PN._2u(LN, SN.ARROW_FROM_LINE_CAP, Aw, Yw), PN._2u(LN, SN[qw], Aw, Hw), PN._2u(LN, SN.ARROW_TO_OFFSET, Aw, Uw), PN._2u(LN, SN[PT], Aw, Ww), PN._2u(LN, SN.ARROW_TO_STROKE_STYLE, Aw, Xw), PN._2u(LN, SN[Vw], Aw, Kw), PN._2u(LN, SN[zT], Aw, Zw), PN._2u(LN, SN.ARROW_TO_FILL_COLOR, Aw, Jw), PN._2u(LN, SN[Qw], Aw, tO), PN._2u(LN, SN.ARROW_TO_LINE_DASH, Aw, iO), PN._2u(LN, SN.ARROW_TO_LINE_DASH_OFFSET, Aw, "toArrowLineDashOffset"), PN._2u(LN, SN[eO], Aw, nO), PN._2u(LN, SN.ARROW_TO_LINE_CAP, Aw, sO));
    var NN = new ns;
    NN._2u(LN, SN[rO], hO, WT),
        NN._2u(LN, SN.EDGE_BUNDLE_LABEL_POSITION, hO, Ko),
        NN._2u(LN, SN.EDGE_BUNDLE_LABEL_ANCHOR_POSITION, hO, up),
        NN._2u(LN, SN.EDGE_BUNDLE_LABEL_FONT_SIZE, hO, XT),
        NN._2u(LN, SN[Hx], hO, hw),
        RN || (NN._2u(LN, SN[Nx], hO, Za), NN._2u(LN, SN.EDGE_BUNDLE_LABEL_FONT_FAMILY, hO, ZT), NN._2u(LN, SN[$x], hO, QT), NN._2u(LN, SN[aO], hO, ao), NN._2u(LN, SN.EDGE_BUNDLE_LABEL_POINTER_WIDTH, hO, ew), NN._2u(LN, SN[oO], hO, Go), NN._2u(LN, SN[Fx], hO, sw), NN._2u(LN, SN.EDGE_BUNDLE_LABEL_OFFSET_X, hO, Bp), NN._2u(LN, SN.EDGE_BUNDLE_LABEL_OFFSET_Y, hO, zp), NN._2u(LN, SN[_O], hO, Bl), NN._2u(LN, SN[fO], hO, $p), NN._2u(LN, SN[qx], hO, VT), NN._2u(LN, SN.EDGE_BUNDLE_LABEL_BACKGROUND_GRADIENT, hO, Yo));
    var jN = new ns;
    jN._2u(kN, np),
        jN._2u(LN, SN.BACKGROUND_COLOR, null, VT),
        jN._2u(LN, SN.BACKGROUND_GRADIENT, null, Yo),
        jN._2u(LN, SN.PADDING, null, ao),
        jN._2u(LN, SN[jE], null, Bl),
        jN._2u(LN, SN.BORDER_RADIUS, null, sw),
        jN._2u(LN, SN[zE], null, $p),
        jN._2u(LN, SN[GE], null, cw),
        jN._2u(LN, SN[dw], null, lw),
        jN._2u(kN, Za, null, Za),
        jN._2u(kN, Wm, null, null, uO),
        jN._2u(kN, gp, vb, go),
        jN._2u(kN, fp, vb, fp),
        jN._2u(LN, SN.SHAPE_STROKE, vb, Wa),
        jN._2u(LN, SN[mE], vb, nb),
        jN._2u(LN, SN.SHAPE_FILL_COLOR, vb, bw),
        jN._2u(LN, SN[OE], vb, gw),
        RN || (jN._2u(LN, SN.SHAPE_OUTLINE, vb, Gb), jN._2u(LN, SN[CE], vb, $b), jN._2u(LN, SN.SHAPE_LINE_DASH, vb, n_), jN._2u(LN, SN.SHAPE_LINE_DASH_OFFSET, vb, s_), jN._2u(LN, SN[mw], vb, Jb), jN._2u(LN, SN[pw], vb, Bb), jN._2u(LN, SN[Ew], vb, Zo), jN._2u(LN, SN.IMAGE_BACKGROUND_COLOR, vb, VT), jN._2u(LN, SN[cO], vb, Yo), jN._2u(LN, SN[dO], vb, ao), jN._2u(LN, SN.IMAGE_BORDER, vb, Bl), jN._2u(LN, SN.IMAGE_BORDER_RADIUS, vb, sw), jN._2u(LN, SN[lO], vb, $p), jN._2u(LN, SN.IMAGE_BORDER_LINE_DASH, vb, cw), jN._2u(LN, SN[Tw], vb, lw), jN._2u(LN, SN[vO], vb, kw), jN._2u(LN, SN[vT], vb, Dw), jN._2u(LN, SN[bO], vb, Mw), jN._2u(LN, SN[Pw], vb, Nw), jN._2u(LN, SN[mT], vb, jw), jN._2u(LN, SN[OT], vb, $w), jN._2u(LN, SN.ARROW_FROM_FILL_GRADIENT, vb, "fromArrowFillGradient"), jN._2u(LN, SN.ARROW_FROM_LINE_DASH, vb, Gw), jN._2u(LN, SN[wT], vb, "fromArrowLineDashOffset"), jN._2u(LN, SN[CT], vb, Fw), jN._2u(LN, SN.ARROW_FROM_LINE_CAP, vb, Yw), jN._2u(LN, SN.ARROW_TO_SIZE, vb, Hw), jN._2u(LN, SN[DT], vb, Uw), jN._2u(LN, SN.ARROW_TO, vb, Lw), jN._2u(LN, SN[PT], vb, Ww), jN._2u(LN, SN[jT], vb, Xw), jN._2u(LN, SN.ARROW_TO_FILL_COLOR, vb, Jw), jN._2u(LN, SN[Qw], vb, tO), jN._2u(LN, SN.ARROW_TO_LINE_DASH, vb, iO), jN._2u(LN, SN[gO], vb, "toArrowLineDashOffset"), jN._2u(LN, SN[eO], vb, nO), jN._2u(LN, SN.ARROW_TO_LINE_CAP, vb, sO));
    var BN = function(t, i) {
            return t = t.zIndex,
                i = i[Gm],
                t == i ? 0 : (t = t || 0, i = i || 0, t > i ? 1 : i > t ? -1 : void 0)
        },
        zN = function(t, i) {
            this.uiBounds = new BD,
                j(this, zN, arguments),
                this.id = this.$data.id,
                this[Jf] = i,
                this._ez = [],
                this._n06 = new ns
        };
    zN[ah] = {
            syncSelection: !1,
            graph: null,
            layoutByAnchorPoint: !1,
            _n06: null,
            _ez: null,
            addChild: function(t, i) {
                t._iy = this,
                    i !== e ? y(this._ez, t, i) : this._ez[Yr](t),
                    t._dw && this._my4(t),
                    this[yO](),
                    this[mO]()
            },
            removeChild: function(t) {
                this._n06[pO](t),
                    t._iy = null,
                    m(this._ez, t),
                    this._iv && this._iv[Zr](t),
                    this.invalidateSize()
            },
            getProperty: function(t, i) {
                return i == MM.PROPERTY_TYPE_STYLE ? this[Jf].getStyle(this.$data, t) : i == MM.PROPERTY_TYPE_CLIENT ? this.$data.get(t) : this[Xp][t]
            },
            getStyle: function(t) {
                return this.graph[Lf](this[Xp], t)
            },
            _$z: function(t, i, e) {
                var n = this._n06[EO](this, t, i, e);
                return CN.onBindingPropertyChange(this, t, i, e) || n
            },
            onPropertyChange: function(t) {
                if (Gm == t[Ku]) return this.invalidateRender(), !0;
                if (jm == t[N_]) {
                    if (Bm == t.kind) return this[Bm](), !0;
                    var i = t.value;
                    return i && i.ui ? (Lh == t[Ku] ? this._99(i) : Zr == t[Ku] && this[Gv](i.ui), !0) : !1
                }
                return this._$z(t.kind, t[xO] || kN, t[Nu])
            },
            label: null,
            initLabel: function() {
                var t = new GN;
                t[so] = Ay,
                    this[TO](t),
                    this[Ay] = t
            },
            initialize: function() {
                this.initLabel(),
                    this[Xp]._mxh && this[Xp]._mxh[d_](this._99, this),
                    CN.initBindingProperties(this),
                    this._n06[wO](this, !1)
            },
            addBinding: function(t, i) {
                return i.property ? (i[sE] = t, void this._n06._2b(i[yf], i)) : !1
            },
            _f0: function(t, i) {
                var e = this.$data;
                if (!e._mxh) return !1;
                var n = e._mxh.getById(t.id);
                if (!n || !n[OO]) return !1;
                var s = n.bindingProperties;
                if (C(s)) {
                    var r = !1;
                    return l(s,
                            function(t) {
                                return go == t.bindingProperty ? (r = ss(e, i, t[yf], t.propertyType), !1) : void 0
                            },
                            this),
                        r
                }
                return go == s[mf] ? ss(e, i, s[yf], s[xO]) : !1
            },
            _99: function(t) {
                var i = t.ui;
                if (i) {
                    var e = t[OO];
                    e && (Array.isArray(e) ? e[d_](function(t) {
                                this[IO](i, t)
                            },
                            this) : this.addBinding(i, e)),
                        this[TO](i)
                }
            },
            validate: function() {
                return this._9z || (this[AO](), this._9z = !0),
                    this[Vp]()
            },
            _$e: !0,
            invalidateChildrenIndex: function() {
                this._$e = !0
            },
            doValidate: function() {
                if (this._1p && (this._1p = !1, this.validateChildren() && (this[Yp](), this.$invalidateSize = !0), this._$e && (this._$e = !1, mD ? this._ez = d(this._ez, BN) : this._ez.sort(BN))), Fe.call(this) && (this[Jo] = !0), this[Jo]) {
                    pP.call(this),
                        this[Cf][jo](this._f9);
                    var t = Math.max(this.$shadowOffsetX || 0, this[SO] || 0),
                        i = Math.max(this[CO] || 0, this.$selectionShadowOffsetY || 0),
                        e = Math.max(this.$shadowBlur, this.$selectionShadowBlur),
                        n = CD[kO] || 0,
                        s = e - t + n,
                        r = e + t + n,
                        h = e - i + n,
                        a = e + i + n;
                    return 0 > s && (s = 0),
                        0 > r && (r = 0),
                        0 > h && (h = 0),
                        0 > a && (a = 0),
                        this.uiBounds.grow(h, s, a, r),
                        this.onBoundsChanged && this[qp](),
                        this[LO] = !0, !0
                }
            },
            validateChildren: function() {
                var t,
                    i = this._mxody,
                    e = this[RO];
                i && (i[DO] = this.$renderColor, i[MO] = this.$renderColorBlendMode, i.$shadowColor = this.$shadowColor, i[PO] = this.$shadowBlur, i[NO] = this[NO], i.$shadowOffsetY = this[CO]),
                    this[RO] = !1,
                    i && i._1p && (e = i[uo]() || e, i.$x = 0, i.$y = 0, i[Jo] && pP.call(i), t = !0);
                for (var n = 0, s = this._ez[jr]; s > n; n++) {
                    var r = this._ez[n];
                    r != i && (r._1p && r[uo]() || e) && r._hk && (Ue(r, i, this), t || (t = !0))
                }
                return t
            },
            measure: function() {
                this._iq[Ec]();
                for (var t, i, e = 0, n = this._ez.length; n > e; e++) t = this._ez[e],
                    t._hk && (i = t._f9, i[xa] <= 0 || i.height <= 0 || this._iq.addRect(t.$x + i.x, t.$y + i.y, i[xa], i[Fa]))
            },
            _iv: null,
            _my4: function(t) {
                if (!this._iv) {
                    if (!t.showOnTop) return;
                    return this._iv = new LD,
                        this._iv[Lh](t)
                }
                return t.showOnTop ? this._iv[Lh](t) : this._iv.remove(t)
            },
            draw: function(t, i, e) {
                for (var n, s = 0, r = this._ez[jr]; r > s; s++) n = this._ez[s],
                    n._hk && !n[KT] && n._j0(t, i, e, this)
            },
            _90: function(t, i) {
                if (!this._hk || !this._iv || !this._iv[jr]) return !1;
                t.save(),
                    t[fo](this.$x, this.$y),
                    this[Ro] && this.$_hostRotate && t.rotate(this.$_hostRotate), (this[Bp] || this.offsetY) && t.translate(this.offsetX, this.offsetY),
                    this[Co] && t.rotate(this[Co]),
                    this.$layoutByAnchorPoint && this._9x && t[fo](-this._9x.x, -this._9x.y),
                    this.shadowColor && (t.shadowColor = this.shadowColor, t.shadowBlur = this.shadowBlur * i, t.shadowOffsetX = this.shadowOffsetX * i, t[fb] = this.shadowOffsetY * i),
                    t.beginPath();
                for (var e, n = 0, s = this._ez[jr]; s > n; n++) e = this._ez[n],
                    e._hk && e.showOnTop && e._j0(t, i, this[jO], this);
                t.restore()
            },
            _du: function(t, i, e) {
                if (e) {
                    if (!this._iq[Wc](t - e, i - e, 2 * e, 2 * e)) return !1
                } else if (!this._iq[Wo](t, i)) return !1;
                return this._5e(t, i, e)
            },
            _5e: function(t, i, e) {
                for (var n, s = this._ez.length - 1; s >= 0; s--)
                    if (n = this._ez[s], n._hk && n._h9(t, i, e)) return n;
                return !1
            },
            destroy: function() {
                this._hned = !0;
                for (var t, i = this._ez.length - 1; i >= 0; i--) t = this._ez[i],
                    t.destroy()
            }
        },
        N(zN, IN),
        Z(zN.prototype, {
            renderColorBlendMode: {
                get: function() {
                    return this[MO]
                },
                set: function(t) {
                    this.$renderColorBlendMode = t,
                        this._1p = !0,
                        this.body && (this.body[_w] = this.$renderColorBlendMode)
                }
            },
            renderColor: {
                get: function() {
                    return this.$renderColor
                },
                set: function(t) {
                    this[DO] = t,
                        this._1p = !0,
                        this.body && (this[BO].renderColor = this[DO])
                }
            },
            bodyBounds: {
                get: function() {
                    if (this[LO]) {
                        this.$invalidateBounds = !1;
                        var t,
                            i = this[BO];
                        t = i && i._hk && !this._$v() ? i._f9.clone() : this._f9.clone(),
                            this.rotate && Di(t, this[Za], t),
                            t.x += this.$x,
                            t.y += this.$y,
                            this._mxj = t
                    }
                    return this._mxj
                }
            },
            body: {
                get: function() {
                    return this._mxody
                },
                set: function(t) {
                    t && this._mxody != t && (this._mxody = t, this[RO] = !0, this[mO]())
                }
            }
        }),
        CD.UI_BOUNDS_GROW = 1;
    var $N = function() {
        j(this, $N, arguments)
    };
    $N.prototype = {
            strokeStyle: sb,
            lineWidth: 0,
            fillColor: null,
            fillGradient: null,
            _iw: 1,
            _ix: 1,
            outline: 0,
            onDataChanged: function(t) {
                B(this, $N, zO, arguments),
                    this._jx && this._7s && this._jx._6i(this._7s, this),
                    t && this._n01(t)
            },
            _n01: function(t) {
                this._jx = me(t),
                    this._jx.validate(), (this._jx._lf == HM || this._jx._6l()) && (this._7s || (this._7s = function() {
                        this[Rp](),
                            this._iy && this._iy[Jf] && (this._iy.invalidateSize(), this._iy[Jf][Bm]())
                    }), this._jx._9w(this._7s, this))
            },
            _jx: null,
            initialize: function() {
                this._n01(this.$data)
            },
            _53: function() {
                return this._jx && this._jx.draw
            },
            _8q: function(t) {
                if (!t || t[xa] <= 0 || t[Fa] <= 0 || !this.$size || !(this[fp] instanceof Object)) return this._iw = 1,
                    void(this._ix = 1);
                var i = this[fp][xa],
                    n = this[fp][Fa];
                if ((i === e || null === i) && (i = -1), (n === e || null === n) && (n = -1), 0 > i && 0 > n) return this._iw = 1,
                    void(this._ix = 1);
                var s,
                    r,
                    h = t[xa],
                    a = t.height;
                i >= 0 && (s = i / h),
                    n >= 0 && (r = n / a),
                    0 > i ? s = r : 0 > n && (r = s),
                    this._iw = s,
                    this._ix = r
            },
            validateSize: function() {
                if (this[$O]) {
                    this.$invalidateScale = !1;
                    var t = this._originalBounds;
                    this._iw,
                        this._ix,
                        this._8q(t),
                        this.setMeasuredBounds(t.width * this._iw, t.height * this._ix, t.x * this._iw, t.y * this._ix)
                }
            },
            measure: function() {
                var t = this._jx.getBounds(this[Wa] + this.outline);
                return t ? (this[$O] = !0, void(this._originalBounds = t[Fr]())) : void this._iq.set(0, 0, 0, 0)
            },
            onBoundsChanged: function() {
                this.$invalidateFillGradient = !0
            },
            _1t: function() {
                this[GO] = !1,
                    this._fillGradient = this.fillGradient ? JM.prototype[FO].call(this[YO], this._7x) : null
            },
            draw: function(t, i, e, n) {
                if (this._iw && this._ix) {
                    if (this[GO] && this._1t(), t.save(), this._jx._lf == WM) return t[Ao](this._iw, this._ix),
                        this._jx._lp.draw(t, i, this, e, n || this),
                        void t.restore();
                    e && this._7m(t, i, n),
                        this._jx.draw(t, i, this, this._iw, this._ix),
                        t[hb]()
                }
            },
            _du: function(t, i, e) {
                if (this._jx._h9) {
                    t /= this._iw,
                        i /= this._ix;
                    var n = (this._iw + this._ix) / 2;
                    return n > 1 && (e /= n, e = 0 | e),
                        this._jx._lp instanceof vP ? this._jx._lp._h9(t, i, e, !0, this[qO], this[HO] || this.$fillGradient) : this._jx._h9(t, i, e)
                }
                return !0
            },
            $invalidateScale: !0,
            $invalidateFillGradient: !0
        },
        N($N, IN),
        is($N[ah], {
            fillColor: {},
            size: {
                validateFlags: [Al, UO]
            },
            fillGradient: {
                validateFlags: [WO]
            }
        }),
        Z($N[ah], {
            originalBounds: {
                get: function() {
                    return this._originalBounds
                }
            }
        }),
        CD[XO] = $D[Gc];
    var GN = function() {
        j(this, GN, arguments),
            this.color = CD.LABEL_COLOR
    };
    GN[ah] = {
            color: CD.LABEL_COLOR,
            showPointer: !0,
            fontSize: null,
            fontFamily: null,
            fontStyle: null,
            _gw: null,
            alignPosition: null,
            measure: function() {
                this.font;
                var t = zi(this.$data, this.$fontSize || CD[lc], this.$font);
                if (this._gw = t, this.$size) {
                    var i = this.$size[xa] || 0,
                        e = this.$size[Fa] || 0;
                    return this[VO](i > t.width ? i : t.width, e > t.height ? e : t[Fa])
                }
                return this.setMeasuredBounds(t[xa], t.height)
            },
            _du: function(t, i, e) {
                return this.$data ? Ce(t, i, e, this) : !1
            },
            draw: function(t, i, e, n) {
                e && this._7m(t, i, n);
                var s = this.$fontSize || CD[lc];
                if (this[Ro] && this[Do]) {
                    var r = ue(this.$_hostRotate);
                    r > RD && 3 * RD > r && (t[fo](this._iq[xa] / 2, this._iq.height / 2), t[Za](Math.PI), t[fo](-this._iq[xa] / 2, -this._iq.height / 2))
                }
                var h = this.alignPosition || CD.ALIGN_POSITION,
                    a = h.horizontalPosition,
                    o = h[kh],
                    _ = s * CD.LINE_HEIGHT,
                    f = _ / 2;
                if (o != qD && this._gw.height < this._iq.height) {
                    var u = this._iq[Fa] - this._gw[Fa];
                    f += o == HD ? u / 2 : u
                }
                t.translate(0, f),
                    t[Pa] != this.$font && (t.font = this.$font),
                    a == FD ? (t.textAlign = tu, t.translate(this._iq[xa] / 2, 0)) : a == YD ? (t.textAlign = zf, t[fo](this._iq.width, 0)) : t[Zv] = _o,
                    t.textBaseline = Jv,
                    t[Xv] = this[WT];
                for (var c = 0, d = this[Xp].split(Na), l = 0, v = d[jr]; v > l; l++) {
                    var b = d[l];
                    t[rb](b, 0, c),
                        c += _
                }
            },
            _53: function() {
                return null != this[Xp] || this[KO]
            },
            $invalidateFont: !0
        },
        N(GN, IN),
        is(GN[ah], {
            size: {
                validateFlags: [nE]
            },
            fontStyle: {
                validateFlags: [nE, ZO]
            },
            fontSize: {
                validateFlags: [nE, ZO]
            },
            fontFamily: {
                validateFlags: [nE, ZO]
            }
        }),
        Z(GN[ah], {
            font: {
                get: function() {
                    return this.$invalidateFont && (this[JO] = !1, this[QO] = (this.$fontStyle || CD.FONT_STYLE) + Vr + (this.$fontSize || CD.FONT_SIZE) + vc + (this.$fontFamily || CD[tI])),
                        this.$font
                }
            }
        });
    var FN = function(t) {
        t = t || new vP,
            this[iI] = new BD,
            j(this, FN, [t])
    };
    FN.prototype = {
            layoutByPath: !0,
            layoutByAnchorPoint: !1,
            measure: function() {
                this[eI] = !0,
                    this[nI] = !0,
                    this[Xp][Ya](this.$lineWidth + this[sI], this.pathBounds),
                    this.setMeasuredBounds(this[iI])
            },
            validateSize: function() {
                if (this.$invalidateFromArrow || this.$invalidateToArrow) {
                    var t = this.pathBounds[Fr]();
                    if (this[eI]) {
                        this[eI] = !1;
                        var i = this.validateFromArrow();
                        i && t.add(i)
                    }
                    if (this.$invalidateToArrow) {
                        this.$invalidateToArrow = !1;
                        var i = this.validateToArrow();
                        i && t[Lh](i)
                    }
                    this[VO](t)
                }
            },
            validateFromArrow: function() {
                if (!this.$data._io || !this.$fromArrow) return void(this.$fromArrowShape = null);
                var t = this.$data,
                    i = 0,
                    e = 0,
                    n = this.$fromArrowOffset;
                n && (isNaN(n) && (n.x || n.y) ? (i += n.x || 0, e += n.y || 0) : i += n || 0, i > 0 && 1 > i && (i *= t._io)),
                    this[rI] = t.getLocation(i, e),
                    this[rI][Za] = Math.PI + this[rI].rotate || 0,
                    this.$fromArrowShape = Ds(this[hI], this.$fromArrowSize);
                var s = this[aI].getBounds(this[oI][Wa] + this[oI][Gb]);
                return this.fromArrowFillGradient instanceof DM[_I] ? this[oI]._fillGradient = JM[ah].generatorGradient.call(this.fromArrowFillGradient, s) : this[oI] && (this.fromArrowStyles._fillGradient = null),
                    Mi(s, this.fromArrowLocation[Za], s, s[zf], s.cy),
                    s.offset(this[rI].x, this.fromArrowLocation.y),
                    s
            },
            validateToArrow: function() {
                if (!this.$data._io || !this[fI]) return void(this[uI] = null);
                var t = this.$data,
                    i = 0,
                    e = 0,
                    n = this[cI];
                n && (isNaN(n) && (n.x || n.y) ? (i += n.x || 0, e += n.y || 0) : i += n || 0),
                    0 > i && i > -1 && (i *= t._io),
                    i += t._io,
                    this.toArrowLocation = t.getLocation(i, e),
                    this[uI] = Ds(this[fI], this[dI]);
                var s = this.$toArrowShape[Ya](this[lI].lineWidth + this[lI].outline);
                return this[tO] instanceof DM[_I] ? this.toArrowStyles._fillGradient = JM.prototype[FO][Br](this[tO], s) : this[lI] && (this.toArrowStyles._fillGradient = null),
                    Mi(s, this.toArrowLocation[Za], s, s[zf], s.cy),
                    s.offset(this.toArrowLocation.x, this.toArrowLocation.y),
                    s
            },
            _2i: function(t) {
                var i = t ? "from" : Km,
                    n = this[i + vI];
                n === e && (n = this[qO]);
                var s = this[i + bI];
                s === e && (s = this.strokeStyle);
                var r = this[i + gI];
                r || (this[i + gI] = r = {}),
                    r[Wa] = n,
                    r[nb] = s,
                    r.lineDash = this[i + yI],
                    r[s_] = this[i + mI],
                    r.fillColor = this[i + pI],
                    r[gw] = this[i + EI],
                    r.lineCap = this[i + xI],
                    r.lineJoin = this[i + TI],
                    r.outline = this[i + wI] || 0,
                    r.outlineStyle = this[i + OI]
            },
            doValidate: function() {
                return this.$fromArrow && this._2i(!0),
                    this.$toArrow && this._2i(!1),
                    B(this, FN, Vp)
            },
            drawArrow: function(t, i, e, n) {
                if (this[hI] && this.$fromArrowShape) {
                    t.save();
                    var s = this.fromArrowLocation,
                        r = s.x,
                        h = s.y,
                        a = s.rotate;
                    t.translate(r, h),
                        a && t[Za](a),
                        this[aI][io](t, i, this.fromArrowStyles, e, n),
                        t.restore()
                }
                if (this[fI] && this[uI]) {
                    t.save();
                    var s = this.toArrowLocation,
                        r = s.x,
                        h = s.y,
                        a = s.rotate;
                    t.translate(r, h),
                        a && t[Za](a),
                        this[uI].draw(t, i, this.toArrowStyles, e, n),
                        t[hb]()
                }
            },
            outlineStyle: null,
            outline: 0,
            onBoundsChanged: function() {
                this[GO] = !0
            },
            _1t: function() {
                this[GO] = !1,
                    this._fillGradient = this.$fillGradient ? JM.prototype.generatorGradient.call(this[YO], this._7x) : null
            },
            draw: function(t, i, e, n) {
                this.$invalidateFillGradient && this._1t(),
                    this[Xp].draw(t, i, this, e, n),
                    this.drawArrow(t, i, e, n)
            },
            _du: function(t, i, e) {
                if (this[Xp]._h9(t, i, e, !0, this[qO] + this.$outline, this[HO] || this.$fillGradient)) return !0;
                if (this[fI] && this.$toArrowShape) {
                    var n = t - this[II].x,
                        s = i - this[II].y;
                    if (this[II].rotate) {
                        var r = ki(n, s, -this[II][Za]);
                        n = r.x,
                            s = r.y
                    }
                    var h = this.toArrowStyles.fillColor || this.toArrowStyles.fillGradient;
                    if (this.$toArrowShape._h9(n, s, e, !0, this.toArrowStyles[Wa], h)) return !0
                }
                if (this[hI] && this[aI]) {
                    var n = t - this[rI].x,
                        s = i - this[rI].y;
                    if (this.fromArrowLocation.rotate) {
                        var r = ki(n, s, -this[rI].rotate);
                        n = r.x,
                            s = r.y
                    }
                    var h = this[oI][bw] || this[oI].fillGradient;
                    if (this.$fromArrowShape._h9(n, s, e, !0, this.fromArrowStyles[Wa], h)) return !0
                }
                return !1
            },
            $fromArrowOutline: 0,
            $toArrowOutline: 0,
            $invalidateFillGradient: !0,
            $invalidateFromArrow: !0,
            $invalidateToArrow: !0
        },
        N(FN, IN),
        is(FN.prototype, {
            fillColor: {},
            fillGradient: {
                validateFlags: [WO]
            },
            fromArrowOffset: {
                validateFlags: [AI, Al]
            },
            fromArrowSize: {
                validateFlags: [AI, Al]
            },
            fromArrow: {
                validateFlags: [AI, Al]
            },
            fromArrowOutline: {
                validateFlags: [AI, Al]
            },
            fromArrowStroke: {
                validateFlags: [AI, Al]
            },
            toArrowOffset: {
                validateFlags: [SI, Al]
            },
            toArrowSize: {
                validateFlags: [SI, Al]
            },
            toArrow: {
                validateFlags: [SI, Al]
            },
            toArrowOutline: {
                validateFlags: [SI, Al]
            },
            toArrowStroke: {
                validateFlags: [SI, Al]
            },
            outline: {
                value: 0,
                validateFlags: [nE]
            }
        }),
        Z(FN.prototype, {
            length: {
                get: function() {
                    return this.data.length
                }
            }
        }),
        rs.prototype = {
            shape: null,
            path: null,
            initialize: function() {
                B(this, rs, AO),
                    this.path = new vP,
                    this.path._e2 = !1,
                    this.shape = new FN(this.path),
                    this[TO](this[Aw], 0),
                    this._mxody = this[Aw],
                    PN[wO](this)
            },
            _1r: !0,
            _5n: null,
            _$v: function() {
                return !1
            },
            _4f: function() {
                return !1
            },
            validatePoints: function() {
                this[Aw][Rp]();
                var t = this.$data,
                    i = this.path;
                i.clear();
                var e = t.fromAgent,
                    n = t[z_];
                e && n && zs(this, t, i, e, n)
            },
            drawLoopedEdge: function(t, i, e, n) {
                Ys(this, n, t)
            },
            drawEdge: function(t, i, e, n, s, r) {
                var h = this.getStyle(SN[iT]),
                    a = this.getStyle(SN.EDGE_TO_OFFSET);
                if (h && (s.x += h.x || 0, s.y += h.y || 0), a && (r.x += a.x || 0, r.y += a.y || 0), n == MM.EDGE_TYPE_ZIGZAG) {
                    var o = s.center,
                        _ = r.center,
                        f = (o.x + _.x) / 2,
                        u = (o.y + _.y) / 2,
                        c = o.x - _.x,
                        d = o.y - _.y,
                        l = Math[$a](c * c + d * d),
                        v = Math.atan2(d, c);
                    v += Math.PI / 6,
                        l *= .04,
                        l > 30 && (l = 30);
                    var b = Math[Ea](v) * l,
                        g = Math.sin(v) * l;
                    return t.lineTo(f - g, u + b),
                        void t.lineTo(f + g, u - b)
                }
                var y = Fs(this, this.data, s, r, i, e, s.center, r.center);
                y && (t._eq = y)
            },
            _2o: function() {
                if (!this[Xp].isBundleEnabled()) return null;
                var t = this[Jf]._7u._80(this.$data);
                if (!t || !t[CI](this[Jf]) || !t._gn) return null;
                var i = t[kI](this);
                return t[LI](this[Xp]) || (i = -i),
                    i
            },
            checkBundleLabel: function() {
                var t = this[RI]();
                return t ? (this[hO] || this.createBundleLabel(), this.bundleLabel._hk = !0, void(this[hO].data = t)) : void(this[hO] && (this.bundleLabel._hk = !1, this.bundleLabel[go] = null))
            },
            createBundleLabel: function() {
                var t = new GN;
                t[DI] = !1,
                    this[hO] = t,
                    this.addChild(this.bundleLabel),
                    NN[wO](this)
            },
            getBundleLabel: function() {
                return this.graph.getBundleLabel(this.data)
            },
            doValidate: function() {
                return this._1r && (this._1r = !1, this[MI]()),
                    this[PI](),
                    B(this, rs, Vp)
            },
            _4v: function() {
                this._1r = !0,
                    this.invalidateSize()
            },
            _$z: function(t, i, e) {
                var n = this._n06.onBindingPropertyChange(this, t, i, e);
                return n = CN.onBindingPropertyChange(this, t, i, e) || n,
                    this.bundleLabel && this[hO][Xp] && (n = NN[EO](this, t, i, e) || n),
                    PN.onBindingPropertyChange(this, t, i, e) || n
            }
        },
        N(rs, zN),
        rs[NI] = function(t, i, e, n) {
            if (t.moveTo(i.x, i.y), !n || n == MM.EDGE_TYPE_DEFAULT) return void t.lineTo(e.x, e.y);
            if (n == MM.EDGE_TYPE_VERTICAL_HORIZONTAL) t.lineTo(i.x, e.y);
            else if (n == MM[Mf]) t[Z_](e.x, i.y);
            else if (0 == n[Kr](MM[Xf])) {
                var s;
                s = n == MM[jI] ? !0 : n == MM.EDGE_TYPE_ORTHOGONAL_VERTICAL ? !1 : Math.abs(i.x - e.x) > Math[Sh](i.y - e.y);
                var r = (i.x + e.x) / 2,
                    h = (i.y + e.y) / 2;
                s ? (t[Z_](r, i.y), t[Z_](r, e.y)) : (t[Z_](i.x, h), t.lineTo(e.x, h))
            }
            t[Z_](e.x, e.y)
        },
        Z(rs[ah], {
            length: {
                get: function() {
                    return this.path ? this.path.length : 0
                }
            }
        }),
        hs.prototype = {
            _2w: null,
            image: null,
            initialize: function() {
                B(this, hs, AO),
                    this._my9(),
                    DN[wO](this)
            },
            _n01: function() {
                this[go].image ? this[vb] && (this.body = this[vb]) : this[Ay] && (this[BO] = this.label)
            },
            _my9: function() {
                this.image = new $N,
                    this[TO](this.image, 0),
                    this._n01()
            },
            doValidate: function() {
                this[BO] && (this instanceof Zs && !this[Xp][Cp] && this._5y() ? this[BO][Mo] = !1 : (this.body.$layoutByAnchorPoint = null != this._2w, this.body[up] = this._2w));
                var t = this[Xp].$location,
                    i = 0,
                    e = 0;
                t && (i = t.x, e = t.y);
                var n = this.$x != i || this.$y != e;
                return n && (this.$invalidateBounds = !0),
                    this.$x = i,
                    this.$y = e,
                    zN[ah].doValidate[Br](this) || n
            },
            _$z: function(t, i, e) {
                var n = this._n06[EO](this, t, i, e);
                return n = CN.onBindingPropertyChange(this, t, i, e) || n,
                    DN[EO](this, t, i, e) || n
            }
        },
        N(hs, zN);
    var YN = function(t, i) {
            return t = t[Xp][Gm] || 0,
                i = i.$data.zIndex || 0,
                t - i
        },
        qN = {
            position: gu,
            "user-select": bu,
            "transform-origin": BI,
            "-webkit-tap-highlight-color": Zb
        };
    li(zI, $I),
        os[ah] = {
            _kt: 1,
            _mxo: null,
            _86: null,
            _88: null,
            _$g: !0,
            _mk: null,
            _mj: null,
            _jm: null,
            _my5: null,
            _6a: !1,
            _9z: !1,
            _jp: null,
            _4n: function(t, i) {
                for (var e = this._mxo, n = 0, s = e.length; s > n; n++)
                    if (t.call(i, e[n]) === !1) return !1
            },
            _ec: function(t, i) {
                this._mk[d_](t, i)
            },
            _10: function(t, i) {
                for (var e = this._mxo, n = e[jr] - 1; n >= 0; n--)
                    if (t[Br](i, e[n]) === !1) return !1
            },
            _4o: function(t, i) {
                this._mk[GI](t, i)
            },
            _3s: function(t, i) {
                this._6r && this._6r._3s && this._6r._3s(t, i)
            },
            _n02: function() {
                ci(this._mj, {
                        overflow: vu,
                        padding: FI
                    }),
                    this._jp._4e(),
                    this._k2 && this._k2[YI] ? this._jp._ef(0, 0) : this._jp._2g = !0
            },
            _4q: function() {
                return this._$g && (this._$g = !1, this._1x()),
                    this._88
            },
            _3o: function() {
                return this._jp._1d ? !1 : (this._jp._1d = !0, void this._mxk())
            },
            _mxk: function() {
                this._6a || (this._6a = !0, x(this._fa.bind(this)))
            },
            _myd: function() {
                var t = !this._9z || 0 == this._mk[jr];
                this._9z || (this._9z = !0, this._n02()),
                    this._mye(t);
                var i = this._jm.g;
                if (this._mk[xc]()) return i._kn(),
                    this._topCanvas._j0(),
                    this._6a = !1,
                    this._jp._jb(this, !0),
                    void this._4q();
                if (this._jp._jb(this, this._my5._lh), this._jr) {
                    var e = this._kg;
                    i[Aa].ratio && (e *= i[Aa].ratio),
                        this._jr(i, e, t)
                }
                this._my5._kn(),
                    this._jp._6x(),
                    this._topCanvas._j0(),
                    this._6a = !1
            },
            _fa: function() {
                this._6a && (this._hned || (this._9z && this._k2 && this._k2._$w && (this._k2._$w = !1, this._k2.forEach(function(t) {
                    t[ep](!0)
                })), this._myd(), this._2k()))
            },
            _g1: null,
            _1g: function(t, i, e, n, s) {
                if (!e || !n) return void this._5f();
                var r = this._mxo,
                    h = this._86;
                this._5f(),
                    this._g1[jr] = 0;
                var a,
                    o = {},
                    _ = this._my5;
                s = s || _._lh;
                for (var f, u, c, d, l, v, b = this._mk._im, g = t + e, y = i + n, m = 0, p = b.length; p > m; m++)
                    if (v = b[m], l = v.__oldBounds, v.__oldBounds = null, v._hk)
                        if (d = v.__iqChanged, v.__iqChanged = !1, f = v.uiBounds, u = f.x + v.$x, c = f.y + v.$y, g > u && y > c && u + f[xa] > t && c + f[Fa] > i) {
                            if (a = v.$data.zIndex, a in o || (o[a] = !0, this._g1.push(a || 0)), r[Yr](v), this._86[v.id] = v, s) continue;
                            l && (_._ld(l.x, l.y, l.width, l[Fa]), s = _._lh),
                                d && (_._ld(u, c, f[xa], f[Fa]), s = _._lh)
                        } else !s && h[v.id] && l && (_._ld(l.x, l.y, l[xa], l.height), s = _._lh);
                else !s && l && (_._ld(l.x, l.y, l[xa], l.height), s = _._lh)
            },
            _mxy: function(t) {
                var i = t.$data.__hkChanged;
                return t.$data.__hkChanged = !1,
                    t._1p || t[Xp]._6a ? (t[Xp]._6a = !1, t._9z && (t.__oldBounds = {
                        x: t.$x + t.uiBounds.x,
                        y: t.$y + t.uiBounds.y,
                        width: t[Cf].width,
                        height: t.uiBounds[Fa]
                    }), t.__iqChanged = t[uo](), i || t.__iqChanged) : (i && t._9z && (t.__oldBounds = {
                        x: t.$x + t[Cf].x,
                        y: t.$y + t[Cf].y,
                        width: t.uiBounds.width,
                        height: t.uiBounds.height
                    }), i)
            },
            _jr: function(t, i, e, n) {
                n = n || this._jp._6v;
                var s = n.x,
                    r = n.y,
                    h = n.width,
                    a = n[Fa];
                this._1g(s, r, h, a, e),
                    this._4q(),
                    this._g1.length && (mD ? (this._g1[qI](), this._mxo = d(this._mxo, YN)) : this._mxo.sort(YN)),
                    this._i1(t, i)
            },
            _i1: function(t, i) {
                t.save(),
                    this._my5._it(t, this._jm, this._jp),
                    this._jp._myu(t);
                for (var e, n, s = this._mxo, r = [], h = 0, a = s[jr]; a > h; h++) e = s[h],
                    n = e[Cf], (this._my5._lh || this._my5._dq(n.x + e.$x, n.y + e.$y, n[xa], n.height)) && (e._j0(t, i), e._iv && e._iv[jr] && r[Yr](e));
                if (r.length)
                    for (h = 0, a = r[jr]; a > h; h++) r[h]._90(t, i);
                t[hb]()
            },
            _g3: function(t, i, e) {
                t[Wv](),
                    t.translate(-e.x * i, -e.y * i),
                    t.scale(i, i);
                var n,
                    s,
                    r = this._mk._im.slice();
                this._g1[jr] && (mD ? (this._g1[qI](), r = d(r, YN)) : r.sort(YN));
                for (var h = [], a = 0, o = r[jr]; o > a; a++) n = r[a],
                    n._hk && (s = n[Cf], e.intersectsRect(s.x + n.$x, s.y + n.$y, s[xa], s[Fa]) && (n._j0(t, i), n._iv && n._iv[jr] && h.push(n)));
                if (h[jr])
                    for (a = 0, o = h.length; o > a; a++) h[a]._90(t, i);
                t.restore()
            },
            _15: function() {},
            _1x: function() {
                for (var t, i, e = this._mk._im, n = new BD, s = e.length - 1; s >= 0; s--) t = e[s],
                    t._hk && (i = t.uiBounds, n[HI](t.$x + i.x, t.$y + i.y, i[xa], i.height));
                var r = this._88;
                this._88 = n,
                    n[Mm](r) || this._15(r, n)
            },
            _mye: function() {
                for (var t, i = this._mk._im, e = i[jr] - 1; e >= 0; e--) t = i[e],
                    this._mxy(t) && !this._$g && (this._$g = !0)
            },
            _1v: function(t, i, e, n) {
                this._my5._lh || (t && (t > 0 && this._my5._ld(this._jp._6v.x, this._jp._6v.y, t / this._jp._kg, this._jp._9a / this._jp._kg), e + t < this._jp._myr && this._my5._ld(this._jp._6v.x + (e + t) / this._jp._kg, this._jp._6v.y, (this._jp._myr - e - t) / this._jp._kg, this._jp._9a / this._jp._kg)), i && (i > 0 && this._my5._ld(this._jp._6v.x, this._jp._6v.y, this._jp._myr / this._jp._kg, i / this._jp._kg), n + i < this._jp._9a && this._my5._ld(this._jp._6v.x, this._jp._6v.y + (n + i) / this._jp._kg, this._jp._myr / this._jp._kg, (this._jp._9a - n - i) / this._jp._kg)))
            },
            _d2: function(t, i) {
                this._mxk(),
                    this._jp._d2(t, i)
            },
            _myg: function(t, i, e) {
                this._mxk(),
                    this._jp._myg(t, i, e)
            },
            _83: function() {},
            _es: function(t, i, e) {
                return this._9z ? void(this._jp._es(t, i, e) !== !1 && this._mxk()) : void(this._jp._kg = t)
            },
            _1u: function() {
                var t = this._4q();
                if (!t[xc]()) {
                    var i = this._jp._myr / t[xa],
                        e = this._jp._9a / t[Fa],
                        n = Math.min(i, e);
                    return n = Math[ja](this._gg, Math.min(this._gf, n)), {
                        scale: n,
                        cx: t.cx,
                        cy: t.cy
                    }
                }
            },
            _jt: function(t, i, e) {
                return this._jp._jt(t, i, e) === !1 ? !1 : void this._mxk()
            },
            _hy: function(t, i) {
                return this._jp._hy(t, i) === !1 ? !1 : void this._mxk()
            },
            _ju: function(t, i) {
                return this._jp._ju(t, i) === !1 ? !1 : void this._mxk()
            },
            _6m: function() {
                return this._jp._6mFlag ? !1 : (this._jp._6mFlag = !0, void this._mxk())
            },
            _5f: function() {
                this._mxo[jr] = 0,
                    this._86 = {}
            },
            _kp: function() {
                this._kn()
            },
            _hn: function() {
                this._kn(),
                    this._hned = !0,
                    this._6a = !1,
                    this._topCanvas[Ec](),
                    this._8i.length = 0,
                    this._6r && (this._6r._hn(), delete this._6r)
            },
            _kn: function() {
                this._9z = !1,
                    this._$g = !0,
                    this._mk[Ec](),
                    this._5f(),
                    this._my5._kn(),
                    this._mxk()
            },
            _8d: function(t, i, e, n) {
                var s = this._kg;
                return new BD(this._mxq(t), this._mxp(i), e / s, n / s)
            },
            _mxq: function(t) {
                return this._jp._mxq(t)
            },
            _mxp: function(t) {
                return this._jp._mxp(t)
            },
            _ee: function(t) {
                return this._jp._ee(t)
            },
            _ed: function(t) {
                return this._jp._ed(t)
            },
            _kl: function(t) {
                return this._mk[pc](t.id || t)
            },
            _$d: function(t) {
                var i = this._8e(t);
                return i.x = this._mxq(i.x),
                    i.y = this._mxp(i.y),
                    i
            },
            _fr: function(t, i) {
                return {
                    x: this._ee(t),
                    y: this._ed(i)
                }
            },
            _ei: function(t, i) {
                return {
                    x: this._mxq(t),
                    y: this._mxp(i)
                }
            },
            _8e: function(t) {
                return vi(t, this._mj)
            },
            _44: function(t) {
                if (t.uiId !== e) return t.uiId ? this._mk.getById(t[UI]) : null;
                var i = Math.round(CD.SELECTION_TOLERANCE / this._jp._kg) || .1;
                this._jm[wa] && (i *= this._jm.ratio);
                for (var n, s = this._$d(t), r = s.x, h = s.y, a = this._mxo, o = a.length - 1; o >= 0; o--)
                    if (n = a[o], n._hk && n._h9(r, h, i)) return t[UI] = n.id,
                        n;
                t.uiId = null
            },
            _h9: function(t) {
                var i = this._44(t);
                if (!i) return null;
                var e = Math.round(CD[Rv] / this._jp._kg) || 1;
                this._jm[wa] && (e *= this._jm.ratio);
                var n = this._$d(t),
                    s = n.x,
                    r = n.y,
                    h = i._h9(s, r, e, !0);
                return h instanceof IN ? h : i
            },
            _myn: function(t) {
                t.id !== e && (t = t.id);
                var i = this._mk.getById(t);
                return i ? new BD((i.$x || 0) + i[Cf].x, (i.$y || 0) + i.uiBounds.y, i.uiBounds[xa], i[Cf][Fa]) : void 0
            },
            _8i: null,
            _2k: function() {
                if (!this._8i.length) return !1;
                var t = this._8i;
                this._8i = [],
                    l(t,
                        function(t) {
                            try {
                                t.delay ? E(t[Br], t.scope, t.delay) : t.call.call(t.scope)
                            } catch (i) {}
                        },
                        this),
                    this._fa()
            },
            callLater: function(t, i, e) {
                i && I(i) && (e = i, i = null);
                var n = this._8i;
                n.push({
                        call: t,
                        scope: i,
                        delay: e
                    }),
                    this._6a || this._2k()
            }
        },
        Z(os.prototype, {
            _6v: {
                get: function() {
                    return this._jp._6v
                }
            },
            _eg: {
                get: function() {
                    return this._jp._eg
                },
                set: function(t) {
                    return !t || 1 > t ? !1 : void(this._jp._eg = t)
                }
            },
            _gf: {
                get: function() {
                    return this._jp._gf
                },
                set: function(t) {
                    return !t || 1 > t ? !1 : void(this._jp._gf = t)
                }
            },
            _gg: {
                get: function() {
                    return this._jp._gg
                },
                set: function(t) {
                    return !t || 0 >= t ? !1 : void(this._jp._gg = t)
                }
            },
            _kg: {
                get: function() {
                    return this._jp._gk()
                },
                set: function(t) {
                    this._es(t)
                }
            },
            _mo: {
                get: function() {
                    return this._jp._kz()
                }
            },
            _mp: {
                get: function() {
                    return this._jp._l1()
                }
            }
        }),
        _s[ah] = {
            _n0m: null,
            _myr: 0,
            _9a: 0,
            _2g: !0,
            _1d: !0,
            _jp: null,
            _6v: null,
            _eg: 1.3,
            _gf: 10,
            _gg: .1,
            _kg: 1,
            _mo: 0,
            _mp: 0,
            _6x: function() {
                this._jp._fl(this._n0m._jm)
            },
            _4e: function() {
                return this._1d = !1,
                    this._62(this._n0m._mj.clientWidth, this._n0m._mj.clientHeight)
            },
            _62: function(t, i) {
                return this._myr == t && this._9a == i ? !1 : (this._myr = t, this._9a = i, void this._n0m._3s(t, i))
            },
            _ef: function(t, i, e) {
                e && (e = Math[ja](this._gg, Math[Ga](this._gf, e)), this._kg = e),
                    this._mo = this._myr / 2 - t * this._kg,
                    this._mp = this._9a / 2 - i * this._kg,
                    this._2g = !0
            },
            _3d: function(t, i) {
                t = t || this._myr,
                    i = i || this._9a,
                    this._6v[Vo](-this._mo / this._kg, -this._mp / this._kg, t / this._kg, i / this._kg)
            },
            _jt: function(t, i, e) {
                return this._es(this._65() * t, i, e)
            },
            _ju: function(t, i) {
                return this._es(this._65() * this._eg, t, i)
            },
            _hy: function(t, i) {
                return this._es(this._65() / this._eg, t, i)
            },
            _es: function(t, i, n) {
                this._6mFlag = !1,
                    t = Math.max(this._gg, Math[Ga](this._gf, t));
                var s = this._65();
                return i === e && (i = this._myr / 2, n = this._9a / 2),
                    t != s && (this._2g = !0, this._n0m._83(s, t)),
                    this._jp._es(t / this._kg, i, n)
            },
            _65: function() {
                return this._kg * this._jp._kg
            },
            _d2: function(t, i) {
                this._jp._d2(t, i)
            },
            _myg: function(t, i, e) {
                var n = this._kz(),
                    s = this._l1(),
                    r = this._gk();
                return e && (e = Math.max(this._gg, Math.min(this._gf, e))),
                    t != n || i != s || e && e != r ? (e && e != r ? (e /= this._kg, this._2g = !0) : e = this._jp._kg, t -= n * e, i -= s * e, this._jp._96(e, t, i), this._n0m._3i(n, s, r, arguments[0], arguments[1], arguments[2]), r != arguments[2] && this._n0m._83(r, arguments[2]), !0) : !1
            },
            _6m: function() {
                this._6mFlag = !0
            },
            _gk: function() {
                return this._kg * this._jp._kg
            },
            _kz: function() {
                return this._mo * this._jp._kg + this._jp._mo
            },
            _l1: function() {
                return this._mp * this._jp._kg + this._jp._mp
            },
            _jb: function(t, i) {
                this._1d && this._4e(),
                    OD && TD && (i = !0);
                var e = t._jm,
                    n = e.ratio || 1,
                    s = e[Wu],
                    r = e[Tu],
                    h = this._myr != s,
                    a = this._9a != r,
                    o = h || a;
                o && t._topCanvas._jm.setSize(this._myr, this._9a);
                var _ = this._mo,
                    f = this._mp,
                    u = this._kg;
                if (this._6mFlag) {
                    this._6mFlag = !1;
                    var c = t._1u();
                    c && this._ef(c.cx, c.cy, c[Ao])
                }
                if (this._2g || i || o) return this._2g = !1,
                    this._kg *= this._jp._kg,
                    this._mo = this._mo * this._jp._kg + this._jp._mo,
                    this._mp = this._mp * this._jp._kg + this._jp._mp,
                    this._jp._kg = 1,
                    this._jp._mo = 0,
                    this._jp._mp = 0,
                    o && e[Ma](this._myr, this._9a),
                    t._my5._lh = !0,
                    this._3d(this._myr, this._9a),
                    void((_ != this._mo || f != this._mp || u != this._kg) && (t._3i(_, f, u, this._mo, this._mp, this._kg), u != this._kg && t._83(u, this._kg)));
                var d = this._jp._mo,
                    l = this._jp._mp;
                if (d || l) {
                    this._jp._mo = 0,
                        this._jp._mp = 0,
                        this._mo += d,
                        this._mp += l,
                        this._3d(s, r);
                    var v = e.g;
                    this._dy(v, e, d * n, l * n),
                        t._1v(d, l, s, r),
                        t._3i(_, f, u, this._mo, this._mp, this._kg)
                }
            },
            _dy: function(t, e, n, s) {
                var r = this._mxackCanvas;
                r || (r = this._mxackCanvas = i.createElement(Aa), r.g = r[WI](Da)),
                    r[xa] = e.width,
                    r[Fa] = e.height,
                    r.g.drawImage(e, n, s),
                    t._kn(),
                    t[Uv](r, 0, 0)
            },
            _myu: function(t) {
                1 != t[Aa][wa] && t.scale(t.canvas[wa], t[Aa].ratio),
                    t[fo](this._mo, this._mp),
                    t.scale(this._kg, this._kg)
            },
            _mxq: function(t) {
                return (t - this._mo) / this._kg
            },
            _mxp: function(t) {
                return (t - this._mp) / this._kg
            },
            _ee: function(t) {
                return t * this._kg + this._mo
            },
            _ed: function(t) {
                return t * this._kg + this._mp
            }
        };
    var HN = function() {
        this._fp = [],
            this._iq = new BD
    };
    HN.prototype = {
        _fo: 20,
        _fp: null,
        _lh: !1,
        _iq: null,
        _kn: function() {
            this._lh = !1,
                this._fp[jr] = 0,
                this._iq[Ec]()
        },
        _i7: function() {
            return this._lh || this._fp.length > 0
        },
        _ld: function(t, i, e, n) {
            this._lh || 0 >= e || 0 >= n || (this._fp[Yr]({
                x: t,
                y: i,
                width: e,
                height: n
            }), this._iq[HI](t, i, e, n))
        },
        _fs: function(t) {
            this._ld(t.x, t.y, t[xa], t.height)
        },
        _dq: function(t, i, e, n) {
            if (!this._iq.intersectsRect(t, i, e, n)) return !1;
            if (SD || this._fp.length >= this._fo) return !0;
            for (var s, r = 0, h = this._fp[jr]; h > r; r++)
                if (s = this._fp[r], ri(t, i, e, n, s.x, s.y, s[xa], s.height)) return !0;
            return !1
        },
        _it: function(t, i, e) {
            if (this._lh) return t.setTransform(1, 0, 0, 1, 0, 0),
                void t.clearRect(0, 0, i.width, i.height);
            t[dg]();
            var n,
                s,
                r,
                h,
                a = e._kg,
                o = this._fp,
                _ = i[wa] || 1;
            if (SD || o.length >= this._fo) return n = e._ee(this._iq.x) * _,
                s = e._ed(this._iq.y) * _,
                r = X(n + this._iq.width * a * _) - (n = W(n)),
                h = X(s + this._iq[Fa] * a * _) - (s = W(s)),
                t[l_](n, s, r, h),
                t[tv](n, s, r, h),
                void t[Kv]();
            for (var f, u = 0, c = o.length; c > u; u++) f = o[u],
                n = e._ee(f.x) * _,
                s = e._ed(f.y) * _,
                r = X(n + f.width * a * _) - (n = W(n)),
                h = X(s + f.height * a * _) - (s = W(s)),
                t.clearRect(n, s, r, h),
                t.rect(n, s, r, h);
            t[Kv]()
        }
    };
    var UN = {};
    UN[SN[iE]] = CD.SELECTION_COLOR,
        UN[SN[Dv]] = CD.SELECTION_BORDER,
        UN[SN[tE]] = CD.SELECTION_SHADOW_BLUR,
        UN[SN[kv]] = MM.SELECTION_TYPE_SHADOW,
        UN[SN.SELECTION_SHADOW_OFFSET_X] = 2,
        UN[SN[UT]] = 2,
        UN[SN.LABEL_COLOR] = CD.LABEL_COLOR,
        UN[SN.LABEL_POSITION] = $D[Hc],
        UN[SN.LABEL_ANCHOR_POSITION] = $D.CENTER_TOP,
        UN[SN[ax]] = new zD(0, 2),
        UN[SN[XI]] = 8,
        UN[SN[ux]] = 8,
        UN[SN.LABEL_POINTER] = !0,
        UN[SN[VI]] = 0,
        UN[SN.LABEL_BORDER_STYLE] = sb,
        UN[SN[rw]] = !0,
        UN[SN.LABEL_BACKGROUND_COLOR] = null,
        UN[SN.LABEL_BACKGROUND_GRADIENT] = null,
        UN[SN.EDGE_COLOR] = KI,
        UN[SN[Ux]] = 1.5,
        UN[SN.GROUP_BACKGROUND_COLOR] = V(3438210798),
        UN[SN.GROUP_STROKE] = 1,
        UN[SN.GROUP_STROKE_STYLE] = sb,
        UN[SN.ARROW_TO] = !0,
        UN[SN.ARROW_FROM_SIZE] = CD.ARROW_SIZE,
        UN[SN.ARROW_TO_SIZE] = CD[ZI],
        UN[SN[nu]] = 10,
        UN[SN.EDGE_CORNER_RADIUS] = 8,
        UN[SN.EDGE_CORNER] = MM[Hf],
        UN[SN[oT]] = !0,
        UN[SN[JI]] = 20,
        UN[SN[_T]] = .5,
        UN[SN[Rf]] = 20,
        UN[SN[QI]] = 20,
        UN[SN[tA]] = $D[Hc],
        UN[SN.EDGE_BUNDLE_LABEL_POSITION] = $D.CENTER_TOP,
        UN[SN[rO]] = iA,
        UN[SN[eA]] = 1,
        UN[SN[mE]] = nA,
        UN[SN.RENDER_COLOR_BLEND_MODE] = CD.BLEND_MODE,
        MM.NAVIGATION_SCROLLBAR = sA,
        MM[rA] = hA,
        MM.NAVIGATION_BUTTON = aA,
        CD[oA] = MM[_A];
    var WN = function(t, e) {
        this._k2 = t,
            A(e) && (e = i.getElementById(e)),
            e && e[wc] || (e = i.createElement(hu)),
            j(this, WN, [e]),
            this._k2._$u.addListener(this._13, this),
            this._k2._9.addListener(this._27, this),
            this._k2._1n[ld](this._92, this),
            this._k2._1b.addListener(this._72, this),
            this._k2._$n.addListener(this._39, this),
            this._k2._$s[ld](this._3v, this),
            this._mx5 = {},
            this._45(CD.NAVIGATION_TYPE, !0)
    };
    WN.prototype = {
            _$f: null,
            _3v: function(t) {
                var i = t.source,
                    e = t[go];
                if (e)
                    if (this._9z) {
                        var n,
                            s;
                        if (C(e))
                            for (var r = 0, h = e[jr]; h > r; r++) s = e[r].id,
                                n = this._mk.getById(s),
                                n && (n[jO] = i.containsById(s), n[fA]());
                        else {
                            if (s = e.id, n = this._mk[pc](s), !n) return;
                            n[jO] = i.containsById(s),
                                n[fA]()
                        }
                        this._mxk()
                    } else {
                        this._$f || (this._$f = {});
                        var n,
                            s;
                        if (C(e))
                            for (var r = 0, h = e[jr]; h > r; r++) s = e[r].id,
                                this._$f[s] = !0;
                        else s = e.id,
                            this._$f[s] = !0
                    }
            },
            _k2: null,
            _n0q: function(t) {
                var i = t.uiClass;
                return i ? new i(t, this._k2) : void 0
            },
            _13: function() {},
            _27: function(t) {
                if (!this._9z) return !1;
                var i = t.source,
                    e = t.kind;
                Fm == e && this._k2[ep](),
                    $m == e ? (this._mk.removeById(i.id), this._k6(i)) : Ip == e && i._hd() && t[Nu] && this._5u(i);
                var n = this._mk.getById(i.id);
                n && n._9z && n.onPropertyChange(t) && this._mxk()
            },
            _40: function(t) {
                var i = this._kl(t);
                i && (i[Rp](), this._mxk())
            },
            _92: function(t) {
                if (!this._9z) return !1;
                switch (this._$g = !0, t.kind) {
                    case rM[uA]:
                        this._k6(t.data);
                        break;
                    case rM.KIND_REMOVE:
                        this._gx(t[go]);
                        break;
                    case rM[pd]:
                        this._hl(t.data)
                }
            },
            _kn: function() {
                this._mx5 = {},
                    B(this, WN, cA)
            },
            _mx5: null,
            _k6: function(t) {
                var i = this._n0q(t);
                i && (this._mk.add(i), this._9z && (this._mx5[t.id] = t), this._mxk())
            },
            _gx: function(t) {
                if (DM.isArray(t)) {
                    for (var i, e = [], n = 0, s = t.length; s > n; n++) i = t[n].id,
                        e.push(i),
                        delete this._mx5[i];
                    t = e
                } else t = t.id,
                    delete this._mx5[t];
                this._mk.remove(t) && this._mxk()
            },
            _hl: function() {
                this._kn()
            },
            _72: function(t) {
                return this._9z ? void(t.source instanceof xN && !this._mx5[t.source.id] && (t[Jc] && (this._40(t[Jc]), t.oldValue.__6a = !0), t.value && (this._40(t[Nu]), t[Nu].__6a = !0), this._5u(t[co]))) : !1
            },
            _39: function(t) {
                return this._9z ? void(t[co] instanceof xN && !this._mx5[t[co].id] && this._5u(t[co])) : !1
            },
            _mye: function(t) {
                return t ? this._$y() : void this._8u()
            },
            _3a: function(t) {
                if (t._edgeBundleInvalidateFlag) {
                    var i = t.getEdgeBundle(!0);
                    i ? i._fa(this._k2) : t._edgeBundleInvalidateFlag = !1
                }
            },
            _$y: function() {
                var t,
                    i = (this._k2, this._k2.graphModel),
                    e = this._mk,
                    n = [],
                    s = 1;
                if (i.forEachByDepthFirst(function(i) {
                            return i instanceof EN ? (this._3a(i), void n[Yr](i)) : (t = this._n0q(i), void(t && (e[Lh](t), t._hk = this._e5(i, !1, !0), i.__l6 = s++)))
                        },
                        this), e.length)
                    for (var r = e._im, s = r[jr] - 1; s >= 0; s--) t = r[s],
                        t._hk && this._47(t, t[Xp]);
                for (var h, s = 0, a = n[jr]; a > s; s++)
                    if (h = n[s], t = this._n0q(h))
                        if (t._hk = this._e5(h, !0, !0), t._hk) {
                            this._47(t, h, !0),
                                e.add(t);
                            var o = h.fromAgent,
                                _ = h.toAgent,
                                f = o.__l6 || 0;
                            o != _ && (f = Math[ja](f, _.__l6 || 0)),
                                h.__l6 = f
                        } else e.add(t);
                if (n[jr] && e._im[qI](function(t, i) {
                        return t[Xp].__l6 - i[Xp].__l6
                    }), this._$f) {
                    var u = i.selectionModel;
                    for (var c in this._$f)
                        if (u.containsById(c)) {
                            var t = e.getById(c);
                            t && (t.selected = !0)
                        }
                    this._$f = null
                }
            },
            _8u: function() {
                for (var t in this._mx5) {
                    var i = this._mx5[t];
                    i instanceof xN ? this._5u(i) : this._5r(i)
                }
                this._mx5 = {};
                for (var e, n, s, r = this._mk._im, h = [], a = r[jr] - 1; a >= 0; a--) e = r[a],
                    n = e[Xp],
                    s = n instanceof EN,
                    s && this._3a(n),
                    e._hk = this._e5(n, s),
                    e._hk ? s ? h[Yr](e) : this._47(e, n) && !this._$g && (this._$g = !0) : n.__hkChanged && e._9z && (e.__oldBounds = {
                        x: e.$x + e[Cf].x,
                        y: e.$y + e[Cf].y,
                        width: e.uiBounds.width,
                        height: e[Cf].height
                    });
                if (h.length)
                    for (var a = 0, o = h.length; o > a; a++) e = h[a],
                        this._47(e, e.$data) && !this._$g && (this._$g = !0)
            },
            _47: function(t, i, n) {
                if (n || n === e && i instanceof EN) return i.__4v && (i.__4v = !1, t._4v()),
                    this._mxy(t);
                if (i.__6a && i._hd() && (t._5t(), i.__6a = !1), this._mxy(t)) {
                    var s = this._4x(i);
                    return s && (s.__6a = !0),
                        i.hasEdge() && i[Nm](function(t) {
                                t.__4v = !0
                            },
                            this), !0
                }
            },
            _3c: function(t, i) {
                var e = t[W_],
                    n = t.toAgent,
                    s = i[dA](e.id);
                if (e == n) return s;
                var r = i.getIndexById(n.id);
                return Math.max(s, r)
            },
            _3e: function(t, i) {
                var e = this.graphModel._f5(t);
                return e ? i[dA](e.id) : 0
            },
            _5u: function(t) {
                var i = this._mk,
                    e = i.getById(t.id);
                if (!e) throw new Error(lA + t[so] + vA);
                var s = this._3e(t, i),
                    r = [e];
                t.hasChildren() && n(t,
                        function(t) {
                            t instanceof xN && (e = i.getById(t.id), e && r.push(e))
                        },
                        this),
                    this._4c(i, s, r)
            },
            _5r: function(t) {
                var i = this._mk.getById(t.id);
                if (i) {
                    var e = this._3c(t, this._mk);
                    this._mk.setIndexBefore(i, e)
                }
            },
            _4c: function(t, i, e) {
                function n(t) {
                    s[Lh](t)
                }
                var s = new LD;
                l(e,
                        function(e) {
                            i = t.setIndexAfter(e, i),
                                e.$data[Nm](n)
                        },
                        this),
                    0 != s[jr] && s[d_](this._5r, this)
            },
            _80: function(t) {
                return t.getEdgeBundle(!0)
            },
            _5o: function(t) {
                if (!t[bA]()) return !1;
                var i = t[$_](!0);
                i && i[gA]() !== !1 && this._mxk()
            },
            _4x: function(t) {
                var i = vn(t);
                return i && i[Ip] ? i : null
            },
            _gz: function(t) {
                return vn(t)
            },
            _3k: function(t, i, e) {
                t._$w = !1;
                var n = t._hk;
                t._hk = this._53(t, i),
                    e || t._hk == n || (t.__hkChanged = !0)
            },
            _53: function(t, i) {
                return this._46(t, i) ? !this._k2._hkFilter || this._k2._hkFilter(t) !== !1 : !1
            },
            _e5: function(t, i, e) {
                return t._$w && this._3k(t, i, e),
                    t._hk
            },
            _98: function(t) {
                return !this._k2._3q || this._k2._3q == Ks(t)
            },
            _46: function(t, i) {
                if (t.visible === !1) return !1;
                if (i === e && (i = t instanceof EN), !i) return this._k2._3q != Ks(t) ? !1 : !t._dc;
                var n = t.fromAgent,
                    s = t[z_];
                if (!n || !s) return !1;
                if (n == s && !t.isLooped()) return !1;
                if (t[bA]()) {
                    var r = t.getEdgeBundle(!0);
                    if (r && !r._e5(t)) return !1
                }
                var h = this._e5(n, !1),
                    a = this._e5(s, !1);
                return h && a ? !0 : !1
            },
            _6s: null,
            _6r: null,
            _45: function(t, i) {
                return i || t != this._6s ? (this._6s = t, this._6r && (this._6r._hn(), delete this._6r), t == MM[_A] ? void(this._6r = new ir(this, this._mj)) : t == MM[yA] ? void(this._6r = new tr(this, this._mj)) : void 0) : !1
            },
            _3i: function(t, i, e, n, s, r) {
                this._k2._4s(new KD(this._k2, uu, {
                        tx: n,
                        ty: s,
                        scale: r
                    }, {
                        tx: t,
                        ty: i,
                        scale: e
                    })),
                    this._5d()
            },
            _83: function(t, i) {
                this._k2._4s(new KD(this._k2, Ao, i, t))
            },
            _5d: function() {
                this._6r && this._6r._jb(),
                    this._k2._4s(new KD(this._k2, qa))
            },
            _15: function(t, i) {
                this._k2._4s(new KD(this._k2, mA, i, t)),
                    this._5d()
            }
        },
        N(WN, os),
        Z(WN[ah], {
            graphModel: {
                get: function() {
                    return this._k2._k2Model
                }
            }
        });
    var XN = function(i, e) {
        this._$u = new eM,
            this._$u.on(function(t) {
                    pp == t[Ku] && this.invalidateVisibility()
                },
                this),
            this._1n = new eM,
            this._1n.addListener(function(t) {
                    !this.currentSubNetwork || t.kind != rM[pd] && t[Ku] != rM[jd] || this.graphModel[iu](this[pp]) || (this.currentSubNetwork = null)
                },
                this),
            this._9 = new eM,
            this._1b = new eM,
            this._$n = new eM,
            this._$s = new eM,
            this[Iu] = e || new Zn,
            this._7u = new WN(this, i),
            this._30 = new Ar(this),
            this._1m = new eM,
            this._onresize = vM(t, pA,
                function() {
                    this[EA]()
                }, !1, this),
            this._7u._mj.ondrop = function(t) {
                this.ondrop(t)
            }[qv](this),
            this._7u._mj[xA] = function(t) {
                this.ondragover(t)
            }[qv](this)
    };

    XN[ah] = {
            originAtCenter: !0,
            editable: !1,
            ondragover: function(t) {
                DM.stopEvent(t)
            },
            getDropInfo: function(t, i) {
                var e = null;
                if (i) try {
                    e = JSON.parse(i)
                } catch (n) {}
                return e
            },
            ondrop: function(t) {
                var i = t[TA];
                if (i) {
                    var e = i[wA](OA),
                        n = this[IA](t, e);
                    n || (n = {},
                        n[vb] = i.getData(vb), n[N_] = i[wA](N_), n.label = i[wA](Ay), n.groupImage = i.getData(Cp));
                    var s = this[Cu](t);
                    if (s = this.toLogical(s.x, s.y), !(this[AA] instanceof Function && this.dropAction[Br](this, t, s, n) === !1) && (n[vb] || n.label)) {
                        var r = n[vb],
                            h = n.type,
                            a = n[Ay],
                            o = n.groupImage;
                        DM[SA](t);
                        var _;
                        if (h && CA != h ? kA == h ? _ = this[LA](a, s.x, s.y) : RA == h ? _ = this.createShapeNode(a, s.x, s.y) : kp == h ? (_ = this[DA](a, s.x, s.y), o && (o = Vs(o), o && (_[Cp] = o))) : (h = J(h), h instanceof Function && h[ah] instanceof xN && (_ = new h, _[so] = a, _.location = new MD(s.x, s.y), this._k2Model[Lh](_))) : _ = this.createNode(a, s.x, s.y), _) {
                            if (r && (r = Vs(r), r && (_[vb] = r)), t[MA]) {
                                var f = this.getElementByMouseEvent(t);
                                (f.enableSubNetwork || f instanceof ON) && (_.parent = f)
                            }
                            if (n[PA])
                                for (var u in n.properties) _[u] = n[PA][u];
                            if (n.clientProperties)
                                for (var u in n.clientProperties) _[Vo](u, n.clientProperties[u]);
                            if (n.styles && _[vp](n[NA]), this.onElementCreated(_, t, n) === !1) return !1;
                            var c = new Ir(this, Ir[jA], t, _);
                            return this[BA](c),
                                _
                        }
                    }
                }
            },
            enableDoubleClickToOverview: !0,
            _7u: null,
            _$u: null,
            _1n: null,
            _9: null,
            _$s: null,
            _1b: null,
            _$n: null,
            _21: function(t) {
                return this._$u[fd](t)
            },
            _4s: function(t) {
                this._$u.onEvent(t)
            },
            isVisible: function(t) {
                return this._7u._e5(t)
            },
            isMovable: function(t) {
                return t instanceof xN && t.movable !== !1
            },
            isSelectable: function(t) {
                return t.selectable !== !1
            },
            isEditable: function(t) {
                return t.editable !== !1
            },
            isRotatable: function(t) {
                return t.rotatable !== !1
            },
            isResizable: function(t) {
                return t[zA] !== !1
            },
            canLinkFrom: function(t) {
                return t[$A] !== !1
            },
            canLinkTo: function(t) {
                return t.linkable !== !1
            },
            createNode: function(t, i, e) {
                var n = new xN(t, i, e);
                return this._k2Model.add(n),
                    n
            },
            createText: function(t, i, e) {
                var n = new Qn(t, i, e);
                return this._k2Model[Lh](n),
                    n
            },
            createShapeNode: function(t, i, e, n) {
                I(i) && (n = e, e = i, i = null);
                var s = new TN(t, i);
                return s[tp] = new MD(e, n),
                    this._k2Model.add(s),
                    s
            },
            createGroup: function(t, i, e) {
                var n = new ON(t, i, e);
                return this._k2Model[Lh](n),
                    n
            },
            createEdge: function(t, i, e) {
                if (t instanceof xN) {
                    var n = e;
                    e = i,
                        i = t,
                        t = n
                }
                var s = new EN(i, e);
                return t && (s.$name = t),
                    this._k2Model[Lh](s),
                    s
            },
            addElement: function(t) {
                this._k2Model.add(t)
            },
            removeElement: function(t) {
                this._k2Model[Zr](t)
            },
            clear: function() {
                this._k2Model[Ec]()
            },
            getStyle: function(t, i) {
                var n = t._ii[i];
                return n !== e ? n : this[GA](i)
            },
            getDefaultStyle: function(t) {
                if (this._ii) {
                    var i = this._ii[t];
                    if (i !== e) return i
                }
                return UN[t]
            },
            translate: function(t, i, e) {
                return e ? this.translateTo(this.tx + t, this.ty + i, this[Ao], e) : this._7u._d2(t, i)
            },
            translateTo: function(t, i, e, n) {
                if (n) {
                    var s = this._5j();
                    return s._l7(t, i, e, n)
                }
                return this._7u._myg(t, i, e)
            },
            centerTo: function(t, i, e, n) {
                return (!e || 0 >= e) && (e = this[Ao]),
                    this.translateTo(this[xa] / 2 - t * e, this.height / 2 - i * e, e, n)
            },
            moveToCenter: function(t, i) {
                this.callLater(function() {
                        var e = this.bounds;
                        this.centerTo(e.cx, e.cy, t, i)
                    },
                    this)
            },
            zoomToOverview: function(t) {
                return t ? this[FA](function() {
                        var i = this._7u._1u();
                        i && this[YA](i.cx, i.cy, i[Ao], t)
                    },
                    this) : void this._7u._6m()
            },
            zoomAt: function(t, i, n, s) {
                if (s === e && (s = CD.ZOOM_ANIMATE), i === e && (i = this.width / 2), i = i || 0, n === e && (n = this[Fa] / 2), n = n || 0, s) {
                    var r = this.scale;
                    return t = r * t,
                        t >= this[lb] || t <= this.minScale ? !1 : (i = t * (this.tx - i) / r + i, n = t * (this.ty - n) / r + n, this.translateTo(i, n, t, s))
                }
                return this._7u._jt(t, i, n)
            },
            zoomOut: function(t, i, e) {
                return this.zoomAt(1 / this[qA], t, i, e)
            },
            zoomIn: function(t, i, e) {
                return this.zoomAt(this.scaleStep, t, i, e)
            },
            _5j: function() {
                return this._panAnimation || (this._panAnimation = new tj(this)),
                    this._panAnimation
            },
            enableInertia: !0,
            _9m: function(t, i) {
                var e = this._5j();
                return e._fi(t || 0, i || 0)
            },
            getUI: function(t) {
                return Q(t) ? this._7u._44(t) : this._7u._kl(t)
            },
            getUIByMouseEvent: function(t) {
                return this._7u._44(t)
            },
            hitTest: function(t) {
                return this._7u._h9(t)
            },
            globalToLocal: function(t) {
                return this._7u._8e(t)
            },
            toCanvas: function(t, i) {
                return this._7u._fr(t, i)
            },
            toLogical: function(t, i) {
                return Q(t) ? this._7u._$d(t) : this._7u._ei(t, i)
            },
            getElementByMouseEvent: function(t) {
                var i = this._7u._44(t);
                return i ? i[Xp] : void 0
            },
            getElement: function(t) {
                if (Q(t)) {
                    var i = this._7u._44(t);
                    return i ? i[Xp] : null
                }
                return this._k2Model[pc](t)
            },
            invalidate: function() {
                this._7u._mxk()
            },
            invalidateUI: function(t) {
                t[Bm](),
                    this.invalidate()
            },
            invalidateElement: function(t) {
                this._7u._40(t)
            },
            getUIBounds: function(t) {
                return this._7u._myn(t)
            },
            forEachVisibleUI: function(t, i) {
                return this._7u._4n(t, i)
            },
            forEachReverseVisibleUI: function(t, i) {
                return this._7u._10(t, i)
            },
            forEachUI: function(t, i) {
                return this._7u._ec(t, i)
            },
            forEachReverseUI: function(t, i) {
                return this._7u._4o(t, i)
            },
            forEach: function(t, i) {
                return this._k2Model[d_](t, i)
            },
            getElementByName: function(t) {
                var i;
                return this._k2Model.forEach(function(e) {
                        return e[so] == t ? (i = e, !1) : void 0
                    }),
                    i
            },
            focus: function(i) {
                if (i) {
                    var e = t[Gu] || t[Fu],
                        n = t.scrollY || t.pageYOffset;
                    return this[sl][HA](),
                        void t[UA](e, n)
                }
                this.html.focus()
            },
            callLater: function(t, i, e) {
                this._7u[FA](t, i, e)
            },
            exportImage: function(t, i) {
                return hr(this, t, i)
            },
            setSelection: function(t) {
                return this._k2Model._selectionModel[Vo](t)
            },
            select: function(t) {
                return this._k2Model._selectionModel[$u](t)
            },
            unselect: function(t) {
                return this._k2Model._selectionModel[WA](t)
            },
            reverseSelect: function(t) {
                return this._k2Model._selectionModel[XA](t)
            },
            selectAll: function() {
                sr(this)
            },
            unSelectAll: function() {
                this[Ou].clear()
            },
            unselectAll: function() {
                this[VA]()
            },
            isSelected: function(t) {
                return this._k2Model._selectionModel.contains(t)
            },
            sendToTop: function(t) {
                yn(this._k2Model, t)
            },
            sendToBottom: function(t) {
                mn(this._k2Model, t)
            },
            moveElements: function(t, i, e) {
                var n = [],
                    s = new LD;
                return l(t,
                        function(t) {
                            t instanceof xN ? n.push(t) : t instanceof EN && s[Lh](t)
                        }),
                    this._dz(n, i, e, s)
            },
            _dz: function(t, i, e, n) {
                if (0 == i && 0 == e || 0 == t.length && 0 == n[jr]) return !1;
                if (0 != t.length) {
                    var s = this._4w(t);
                    n = this._4t(s, n),
                        l(s,
                            function(t) {
                                var n = t[tp];
                                n ? t.setLocation(n.x + i, n.y + e) : t.setLocation(i, e)
                            })
                }
                return !0
            },
            _4t: function(t, i) {
                return i
            },
            _4w: function(t) {
                var i = new LD;
                return l(t,
                        function(t) {
                            !this.isMovable(t),
                                i[Lh](t),
                                bn(t, i, this._movableFilter)
                        },
                        this),
                    i
            },
            reverseExpanded: function(t) {
                return this._7u._5o(t)
            },
            _30: null,
            _1m: null,
            beforeInteractionEvent: function(t) {
                return this._1m[fd](t)
            },
            onInteractionEvent: function(t) {
                this._1m[cd](t)
            },
            addCustomInteraction: function(t) {
                this._30[KA](t)
            },
            enableWheelZoom: !0,
            enableTooltip: !0,
            getTooltip: function(t) {
                return t[ZA] || t.name
            },
            updateViewport: function() {
                this._7u._3o()
            },
            destroy: function() {
                this._4s(new KD(this, kf, !0, this._hned)),
                    this._hned = !0,
                    bM(t, pA, this._onresize),
                    G(this, JA),
                    this._30.destroy(),
                    this[Iu] = new Zn;
                var i = this.html;
                this._7u._hn(),
                    i && (i[QA] = "")
            },
            onPropertyChange: function(t, i, e) {
                this._$u[ld](function(n) {
                    n.kind == t && i[Br](e, n)
                })
            },
            removeSelection: function() {
                var t = this[Ou]._im;
                return t && 0 != t.length ? (t = t[$r](), this._k2Model[Zr](t), t) : !1
            },
            removeSelectionByInteraction: function(t) {
                var i = this.selectionModel.datas;
                return i && 0 != i[jr] ? void DM.confirm(tS + i.length,
                    function() {
                        var i = this.removeSelection();
                        if (i) {
                            var e = new Ir(this, Ir[iS], t, i);
                            this.onInteractionEvent(e)
                        }
                    },
                    this) : !1
            },
            createShapeByInteraction: function(t, i, e, n) {
                var s = new vP(i);
                i[jr] > 2 && s.closePath();
                var r = this.createShapeNode(eS, s, e, n);
                this[nS](r, t);
                var h = new Ir(this, Ir.ELEMENT_CREATED, t, r);
                return this[BA](h),
                    r
            },
            createLineByInteraction: function(t, i, e, n) {
                var s = new vP(i),
                    r = this.createShapeNode(sS, s, e, n);
                r.setStyle(DM.Styles[TE], null),
                    r[wf](DM[rS][OE], null),
                    r.setStyle(DM[rS].LAYOUT_BY_PATH, !0),
                    this[nS](r, t);
                var h = new Ir(this, Ir.ELEMENT_CREATED, t, r);
                return this.onInteractionEvent(h),
                    r
            },
            createEdgeByInteraction: function(t, i, e, n) {
                var s = this[hS](aS, t, i);
                if (n) s._9q = n;
                else {
                    var r = this[oS],
                        h = this.edgeType;
                    this.interactionProperties && (r = this[_S].uiClass || r, h = this.interactionProperties.edgeType || h),
                        r && (s[$m] = r),
                        h && (s.edgeType = h)
                }
                this.onElementCreated(s, e);
                var a = new Ir(this, Ir.ELEMENT_CREATED, e, s);
                return this[BA](a),
                    s
            },
            onElementCreated: function(t) {
                !t.parent && this[pp] && (t.parent = this.currentSubNetwork)
            },
            allowEmptyLabel: !1,
            startLabelEdit: function(t, i, e, n) {
                var s = this;
                if (t.editTitle) {

                    e.startEdit(n.x, n.y, i.data, this[Lf](t, SN.LABEL_FONT_SIZE),
                        function(e) {
                            return s[fS](t, i, e, i[B_])
                        })
                }
            },
            onLabelEdit: function(t, i, e, n) {
                return e || this[uS] ? void(Ay == i.name ? t[so] = e : n._f0(i, e) === !1 && (i.data = e, this.invalidateElement(t))) : (DM[Yy](cS), !1)
            },
            setInteractionMode: function(t, i) {
                this[dS] = t,
                    this.interactionProperties = i
            },
            upSubNetwork: function() {
                return this._3q ? this[pp] = Ks(this._3q) : !1
            },
            _$w: !1,
            invalidateVisibility: function() {
                this._$w = !0,
                    this[Bm]()
            },
            getBundleLabel: function(t) {
                var i = t.getEdgeBundle(!0);
                return i && i[lS] == t ? vS + i[bS].length : null
            }
        },
        Z(XN[ah], {
            center: {
                get: function() {
                    return this[gS](this[sl].clientWidth / 2, this[sl][Tu] / 2)
                }
            },
            visibleFilter: {
                get: function() {
                    return this._hkFilter
                },
                set: function(t) {
                    this._hkFilter = t,
                        this.invalidate()
                }
            },
            topCanvas: {
                get: function() {
                    return this._7u._topCanvas
                }
            },
            propertyChangeDispatcher: {
                get: function() {
                    return this._$u
                }
            },
            listChangeDispatcher: {
                get: function() {
                    return this._1n
                }
            },
            dataPropertyChangeDispatcher: {
                get: function() {
                    return this._9
                }
            },
            selectionChangeDispatcher: {
                get: function() {
                    return this._$s
                }
            },
            parentChangeDispatcher: {
                get: function() {
                    return this._1b
                }
            },
            childIndexChangeDispatcher: {
                get: function() {
                    return this._$n
                }
            },
            bounds: {
                get: function() {
                    return this._7u._4q()
                }
            },
            interactionDispatcher: {
                get: function() {
                    return this._1m
                }
            },
            cursor: {
                set: function(t) {
                    this[sl][Oa].cursor = t || this._30[Du]
                },
                get: function() {
                    return this[sl].style[yS]
                }
            },
            interactionMode: {
                get: function() {
                    return this._30._n0urrentMode
                },
                set: function(t) {
                    var i = this.interactionMode;
                    i != t && (this._30.currentMode = t, this._4s(new KD(this, dS, i, t)))
                }
            },
            scaleStep: {
                get: function() {
                    return this._7u._eg
                },
                set: function(t) {
                    this._7u._eg = t
                }
            },
            maxScale: {
                get: function() {
                    return this._7u._gf
                },
                set: function(t) {
                    this._7u._gf = t
                }
            },
            minScale: {
                get: function() {
                    return this._7u._gg
                },
                set: function(t) {
                    this._7u._gg = t
                }
            },
            scale: {
                get: function() {
                    return this._7u._kg
                },
                set: function(t) {
                    return this._7u._kg = t
                }
            },
            tx: {
                get: function() {
                    return this._7u._mo
                }
            },
            ty: {
                get: function() {
                    return this._7u._mp
                }
            },
            styles: {
                get: function() {
                    return this._ii
                },
                set: function(t) {
                    this._ii = t
                }
            },
            selectionModel: {
                get: function() {
                    return this._k2Model._selectionModel
                }
            },
            graphModel: {
                get: function() {
                    return this._k2Model
                },
                set: function(t) {
                    if (this._k2Model == t) return !1;
                    var i = this._k2Model,
                        e = new KD(this, Iu, i, t);
                    return this._21(e) === !1 ? !1 : (null != i && (i.propertyChangeDispatcher[mS](this._$u, this), i[Sd][mS](this._1n, this), i.dataChangeDispatcher.removeListener(this._9, this), i[Md][mS](this._1b, this), i.childIndexChangeDispatcher[mS](this._$n, this), i.selectionChangeDispatcher[mS](this._$s, this)), this._k2Model = t, this._k2Model && (this._k2Model[pS].addListener(this._$u, this), this._k2Model.listChangeDispatcher[ld](this._1n, this), this._k2Model.dataChangeDispatcher[ld](this._9, this), this._k2Model.parentChangeDispatcher[ld](this._1b, this), this._k2Model.childIndexChangeDispatcher.addListener(this._$n, this), this._k2Model.selectionChangeDispatcher[ld](this._$s, this)), this._7u && this._7u._kp(), void this._4s(e))
                }
            },
            count: {
                get: function() {
                    return this._k2Model[jr]
                }
            },
            width: {
                get: function() {
                    return this[sl][Wu]
                }
            },
            height: {
                get: function() {
                    return this[sl][Tu]
                }
            },
            viewportBounds: {
                get: function() {
                    return this._7u._6v
                }
            },
            html: {
                get: function() {
                    return this._7u._mj
                }
            },
            navigationType: {
                get: function() {
                    return this._7u._6s
                },
                set: function(t) {
                    this._7u._45(t)
                }
            },
            _3q: {
                get: function() {
                    return this._k2Model._3q
                }
            },
            currentSubNetwork: {
                get: function() {
                    return this._k2Model[pp]
                },
                set: function(t) {
                    this._k2Model[pp] = t
                }
            }
        }),
        Zs.prototype = {
            initialize: function() {
                B(this, Zs, AO),
                    this.checkBody()
            },
            _myi: function() {
                this._m1 = new vP,
                    this[Aw] = new $N(this._m1),
                    this.shape[Zo] = !1,
                    this.addChild(this[Aw], 0),
                    this.body = this.shape
            },
            checkBody: function() {
                return this._5y() ? (this._2c = !0, this.shape ? (this.shape[ES] = !0, this[BO] = this[Aw]) : (this._myi(), MN[wO](this)), void(this.image && (this.image.visible = !1))) : (this[vb] ? (this.image[ES] = !0, this.body = this[vb]) : this._my9(), void(this.shape && (this[Aw][ES] = !1)))
            },
            _5y: function() {
                return this.$data._hd() && this.$data.expanded
            },
            _m1: null,
            _2c: !0,
            _5t: function() {
                this._1p = !0,
                    this._2c = !0
            },
            doValidate: function() {
                if (this._2c && this._5y()) {
                    if (this._2c = !1, this.shape.invalidateData(), this.$data.groupImage) {
                        this[Aw].data = this.$data[Cp];
                        var t = this._29();
                        return this[Aw][Bp] = t.x + t[xa] / 2,
                            this[Aw][zp] = t.y + t.height / 2,
                            this[Aw][fp] = {
                                width: t[xa],
                                height: t[Fa]
                            },
                            hs[ah][Vp][Br](this)
                    }
                    this[Aw].offsetX = 0,
                        this[Aw][zp] = 0;
                    var i = this._8m(this[Xp].groupType);
                    this._m1.clear(),
                        i instanceof BD ? Cn(this._m1, i.x, i.y, i.width, i.height, i.rx, i.ry) : i instanceof ie ? kn(this._m1, i) : i instanceof ee && Ln(this._m1, i),
                        this._m1._6a = !0,
                        this.shape[Rp]()
                }
                return hs.prototype[Vp].call(this)
            },
            _6k: function(t, i, e) {
                switch (t) {
                    case MM.GROUP_TYPE_CIRCLE:
                        return new ie(0, 0, Math[ja](i, e) / 2);
                    case MM[xS]:
                        return new ee(0, 0, i, e);
                    default:
                        return new BD(-i / 2, -e / 2, i, e)
                }
            },
            _29: function() {
                return this._8m(null)
            },
            _8m: function(t) {
                var i = this.data,
                    e = i.padding,
                    n = i.minSize,
                    s = 60,
                    r = 60;
                if (n && (s = n.width, r = n.height), !i.hasChildren()) return this._6k(t, s, r);
                var h,
                    a = this[Xp]._ez._im;
                (t == MM.GROUP_TYPE_CIRCLE || t == MM[xS]) && (h = []);
                for (var o, _, f, u, c = new BD, d = 0, l = a[jr]; l > d; d++) {
                    var v = a[d];
                    if (this[Jf][Au](v)) {
                        var b = this[Jf][Hh](v);
                        b && (o = b.$x + b._f9.x, _ = b.$y + b._f9.y, f = b._f9[xa], u = b._f9[Fa], c[HI](o, _, f, u), h && (h[Yr]({
                            x: o,
                            y: _
                        }), h[Yr]({
                            x: o + f,
                            y: _
                        }), h[Yr]({
                            x: o + f,
                            y: _ + u
                        }), h.push({
                            x: o,
                            y: _ + u
                        })))
                    }
                }
                e && c.grow(e);
                var g = this[Xp][tp];
                g ? g.invalidateFlag && (g[TS] = !1, g.x = c.cx, g.y = c.cy) : g = this.$data.$location = {
                    x: c.cx,
                    y: c.cy
                };
                var y,
                    m = g.x,
                    p = g.y;
                if (t == MM.GROUP_TYPE_CIRCLE) {
                    y = ne(h),
                        y.cx -= m,
                        y.cy -= p;
                    var E = Math[ja](s, r) / 2;
                    return y.r < E && (y.cx += E - y.r, y.cy += E - y.r, y.r = E),
                        y
                }
                return t == MM.GROUP_TYPE_ELLIPSE ? (y = se(h, c), y.cx -= m, y.cy -= p, y.width < s && (y.cx += (s - y.width) / 2, y[xa] = s), y[Fa] < r && (y.cy += (r - y[Fa]) / 2, y[Fa] = r), y) : (y = c, c[xa] < s && (c.width = s), c.height < r && (c[Fa] = r), c.offset(-m, -p), y)
            },
            _$z: function(t, i, e) {
                if (!this._5y()) return B(this, Zs, wS, arguments);
                var n = this._n06.onBindingPropertyChange(this, t, i, e);
                return n = CN[EO](this, t, i, e) || n,
                    n = DN[EO](this, t, i, e) || n,
                    MN[EO](this, t, i, e) || n
            }
        },
        N(Zs, hs);
    var VN = {
        draw: function() {}
    };
    CD.NAVIGATION_IMAGE_LEFT = OS,
        CD[mu] = IS;
    var KN = {
            position: gu,
            "text-align": tu
        },
        ZN = {
            padding: AS,
            transition: SS
        },
        JN = {
            position: CS,
            display: kS
        };
    li(LS, "opacity:0.7;vertical-align:middle;"),
        li(".Q-Graph-Nav img:hover,img.hover", RS),
        OD || (li(DS, MS + dM(PS) + NS), li(jS, BS + dM(PS) + zS)),
        tr[ah] = {
            _n07: function(t, i) {
                return t._hk == i ? !1 : (t._hk = i, void(t[Oa].display = i ? "block" : bu))
            },
            _3s: function(t, i) {
                var e = i / 2 - this._left._img.clientHeight / 2 + Ia;
                this._left._img[Oa].top = e,
                    this._right._img[Oa][oo] = e,
                    this._navPane[Oa].width = t + Ia,
                    this._navPane[Oa][Fa] = i + Ia
            },
            _9i: function(t, i, e, n) {
                this._n07(this._top, t),
                    this._n07(this._left, i),
                    this._n07(this._mxottom, e),
                    this._n07(this._right, n)
            },
            _hn: function() {
                var t = this._navPane.parentNode;
                t && t.removeChild(this._navPane)
            },
            _jb: function() {
                var t = this._n0m._k2;
                if (t) {
                    var i = t.bounds;
                    if (i.isEmpty()) return void this._9i(!1, !1, !1, !1);
                    var e = t[$S],
                        n = e.y > i.y + 1,
                        s = e.x > i.x + 1,
                        r = e[Ah] < i[Ah] - 1,
                        h = e.right < i[zf] - 1;
                    this._9i(n, s, r, h)
                }
            }
        };
    var QN = 8;
    li(GS, FS),
        li(".Q-Graph-ScrollBar:hover", "background-color: #7E7E7E;" + dM(PS) + ": background-color 0.2s linear;"),
        li(".Q-Graph-ScrollBar--V", "width: 8px;right: 0px;"),
        li(".Q-Graph-ScrollBar--H", "height: 8px;bottom: 0px;"),
        li(".Q-Graph-ScrollBar--V.Both", YS),
        li(".Q-Graph-ScrollBar--H.Both", qS),
        OD || (li(HS, MS + dM(PS) + US), li(".Q-Graph:hover .Q-Graph-ScrollPane", BS + dM(PS) + ":opacity 0.3s linear;")),
        ir.prototype = {
            _hn: function() {
                this._verticalDragSupport._hn(),
                    this._horizontalDragSupport._hn(),
                    delete this._verticalDragSupport,
                    delete this._horizontalDragSupport,
                    this._lx.parentNode && this._lx[Hv].removeChild(this._lx)
            },
            _lx: null,
            _mx0: null,
            _84: null,
            init: function(t) {
                var e = i.createElement(hu);
                e[Hr] = WS,
                    ci(e, {
                        width: yu,
                        height: yu,
                        position: CS
                    });
                var n = i[Ra](hu);
                n.className = "Q-Graph-ScrollBar Q-Graph-ScrollBar--V";
                var s = i.createElement(hu);
                s.className = "Q-Graph-ScrollBar Q-Graph-ScrollBar--H",
                    e.appendChild(n),
                    e[If](s),
                    t.appendChild(e),
                    this._lx = e,
                    this._84 = s,
                    this._mx0 = n,
                    s[XS] = !0;
                var r = this,
                    h = {
                        ondrag: function(t, i) {
                            var e = r._n0m._k2;
                            if (e) {
                                var n = i.isH,
                                    s = n ? t.dx : t.dy;
                                if (s && i.scale) {
                                    var h = e[Ao] / i.scale;
                                    n ? e.translate(-h * s, 0) : e[fo](0, -h * s),
                                        DM.stopEvent(t)
                                }
                            }
                        },
                        enddrag: function(t, i) {
                            var e = r._n0m._k2;
                            if (e && e.enableInertia) {
                                var n = i.isH,
                                    s = n ? t.vx : t.vy;
                                if (Math.abs(s) > .1) {
                                    var h = e.scale / i[Ao];
                                    s *= h,
                                        n ? e._9m(-s, 0) : e._9m(0, -s)
                                }
                            }
                        }
                    };
                this._verticalDragSupport = new wi(n, h),
                    this._horizontalDragSupport = new wi(s, h)
            },
            _jb: function() {
                var t = this._n0m._k2;
                if (t) {
                    var i = t.bounds;
                    if (i.isEmpty()) return this._4k(!1),
                        void this._4g(!1);
                    var e = t[$S],
                        n = t.width,
                        s = t.height,
                        r = t[Ao],
                        h = 1 / r,
                        a = e.x > i.x + h || e[zf] < i[zf] - h,
                        o = e.y > i.y + h || e.bottom < i[Ah] - h,
                        _ = a && o;
                    _ ? (w(this._mx0, VS), w(this._84, VS)) : (O(this._mx0, VS), O(this._84, VS)),
                        this._4k(a, e, i, _ ? n - QN : n),
                        this._4g(o, e, i, _ ? s - QN : s)
                }
            },
            _4k: function(t, i, e, n) {
                if (!t) return this._84.style[Om] = bu,
                    void(this._84[Ao] = 0);
                var s = Math[Ga](i.x, e.x),
                    r = Math[ja](i[zf], e.right),
                    h = r - s,
                    a = n / h;
                this._84[Ao] = a,
                    this._84[Oa].left = parseInt((i.x - s) * a) + Ia,
                    this._84[Oa][zf] = parseInt((r - i[zf]) * a) + Ia,
                    this._84.style.display = ""
            },
            _4g: function(t, i, e, n) {
                if (!t) return this._mx0.style.display = bu,
                    void(this._mx0.scale = 0);
                var s = Math[Ga](i.y, e.y),
                    r = Math.max(i[Ah], e.bottom),
                    h = r - s,
                    a = n / h;
                this._mx0.scale = a,
                    this._mx0[Oa][oo] = parseInt((i.y - s) * a) + Ia,
                    this._mx0[Oa].bottom = parseInt((r - i.bottom) * a) + Ia,
                    this._mx0.style.display = ""
            }
        },
        er[ah] = {
            shape: null,
            initialize: function() {
                B(this, er, AO),
                    this._my9(),
                    jN.initBindingProperties(this)
            },
            _my9: function() {
                this.image = new FN(this.$data[gp]),
                    this.addChild(this[vb], 0),
                    this.body = this[vb]
            },
            invalidateShape: function() {
                this.image[Rp](),
                    this[fA]()
            },
            _$z: function(t, i, e) {
                var n = this._n06.onBindingPropertyChange(this, t, i, e);
                return n = CN.onBindingPropertyChange(this, t, i, e) || n,
                    jN.onBindingPropertyChange(this, t, i, e) || n
            },
            doValidate: function() {
                this.body && (this[BO][Mo] = null != this._2w, this.body.anchorPosition = this._2w);
                var t = this.$data.$location,
                    i = 0,
                    e = 0;
                t && (i = t.x, e = t.y);
                var n = this.$x != i || this.$y != e;
                return n && (this[LO] = !0),
                    this.$x = i,
                    this.$y = e,
                    zN.prototype[Vp][Br](this) || n
            }
        },
        N(er, zN),
        Z(er.prototype, {
            length: {
                get: function() {
                    return this.data.length
                }
            }
        }),
        nr.prototype = {
            _m8: function() {
                this._jm.style.visibility = ES
            },
            _j9: function() {
                this._jm[Oa][KS] = vu
            },
            clear: function() {
                this._8v.clear(),
                    this._mxk()
            },
            contains: function(t) {
                return t instanceof Object && t.id && (t = t.id),
                    this._8v.containsById(t)
            },
            addDrawable: function(t, i) {
                if (i) {
                    var e = {
                        id: ++uD,
                        drawable: t,
                        scope: i
                    };
                    return this._8v.add(e),
                        e
                }
                return t.id || (t.id = ++uD),
                    this._8v.add(t),
                    t
            },
            removeDrawable: function(t) {
                return t.id ? void this._8v[Zr](t) : this._8v[ZS](t)
            },
            _8v: null,
            invalidate: function() {
                this._mxk()
            },
            _mxk: function() {
                this._n0m._6a || this._j0()
            },
            _j0: function() {
                di(this._jm, uu, "");
                var t = this._n0m._kg,
                    i = this.g;
                i[Io](1, 0, 0, 1, 0, 0),
                    i.clearRect(0, 0, this._jm.width, this._jm.height),
                    i[Wv](),
                    this._n0m._jp._myu(i);
                for (var e = this._8v._im, n = 0, s = e[jr]; s > n; n++) i[Wv](),
                    i.beginPath(),
                    this._h4(i, e[n], t),
                    i.restore();
                i.restore()
            },
            _h4: function(t, i, e) {
                return i instanceof Function ? void i(t, e) : void(i.drawable instanceof Function && i[vd] && i.drawable[Br](i[vd], t, e))
            }
        },
        CD[Su] = !0;
    var tj = function(t) {
        this._k2 = t
    };
    CD.ANIMATION_MAXTIME = 600,
        CD.ANIMATION_TYPE = CM.easeOut,
        tj.prototype = {
            _k2: null,
            _my: .001,
            _dh: null,
            _d0: function(t) {
                return t > 1 ? 1 : -1 > t ? -1 : t
            },
            _fi: function(t, i) {
                t *= .6,
                    i *= .6,
                    t = this._d0(t),
                    i = this._d0(i),
                    this._ll();
                var e = Math[$a](t * t + i * i);
                if (.01 > e) return !1;
                var n = Math[Ga](CD[JS], e / this._my);
                this._speedX = t,
                    this._speedY = i,
                    this._myX = t / n,
                    this._myY = i / n,
                    this._dh = new LM(this._5s, this, n, CM[QS]),
                    this._dh._l2()
            },
            _5s: function(t, i) {
                if (0 != t) {
                    var e = this._speedX * i - .5 * this._myX * i * i,
                        n = this._speedY * i - .5 * this._myY * i * i;
                    this._speedX -= this._myX * i,
                        this._speedY -= this._myY * i,
                        this._k2.translate(e, n)
                }
            },
            _ll: function() {
                this._dh && this._dh._ll()
            },
            _if: function(t) {
                var i = this._fromTX + (this._toTX - this._fromTX) * t,
                    e = this._fromTY + (this._toTY - this._fromTY) * t,
                    n = this._fromScale + (this._toScale - this._fromScale) * t;
                this._k2.translateTo(i, e, n)
            },
            _l7: function(t, i, e, n) {
                var s = this._k2,
                    r = s[Ao];
                if (0 >= e && (e = r), this._ll(), t != s.tx || i != s.ty || e != r) {
                    var h,
                        a,
                        o;
                    n instanceof Object && (h = n.duration, a = n.maxTime, o = n[tC]);
                    var _ = s.tx,
                        f = s.ty;
                    if (!h) {
                        var u = PD(t, i, _, f);
                        if (h = u / 2, e != r) {
                            var c = e > r ? e / r : r / e;
                            h = Math.max(h, 50 * c)
                        }
                    }
                    a = a || CD[JS],
                        o = o || CD[iC],
                        h = Math.min(a, h),
                        this._fromTX = _,
                        this._fromTY = f,
                        this._fromScale = r,
                        this._toTX = t,
                        this._toTY = i,
                        this._toScale = e,
                        this._dh = new LM(this._if, this, h, o),
                        this._dh._l2()
                }
            }
        },
        CD[eC] = 8,
        CD.INTERACTION_HANDLER_SIZE_DESKTOP = 4,
        CD.INTERACTION_ROTATE_HANDLER_SIZE_TOUCH = 30,
        CD[nC] = 20;
    var ij = Math.PI / 4;
    ar[ah] = {
            onElementRemoved: function(t, i) {
                this[sC] && (t == this[sC] || C(t) && p(t, this[sC])) && this[kf](i)
            },
            onClear: function(t) {
                this[sC] && this[kf](t)
            },
            destroy: function() {
                delete this[sC],
                    this[rC]()
            },
            invalidate: function() {
                this[qu].invalidate()
            },
            removeDrawable: function() {
                this._fzId && (this[qu].removeDrawable(this._fzId), delete this._fzId, this[Bm]())
            },
            addDrawable: function() {
                this._fzId || (this._fzId = this[qu].addDrawable(this.doDraw, this).id, this[Bm]())
            },
            doDraw: function() {},
            escapable: !0,
            onkeydown: function(t, i) {
                this.escapable && 27 == t.keyCode && (R(t), this[kf](i))
            }
        },
        DM.ResizeInteraction = xr,
        or.prototype = {
            defaultCursor: Mu,
            getInteractionInstances: function(t) {
                if (!this[Ru]) return null;
                for (var i = [], e = 0, n = this[Ru][jr]; n > e; e++) {
                    var s = this.interactions[e];
                    s instanceof Function ? i.push(new s(t)) : s instanceof Object && i[Yr](s)
                }
                return i
            }
        },
        _r.prototype = {
            _di: null,
            _ip: null,
            destroy: function() {
                B(this, _r, kf, arguments),
                    delete this._ip,
                    delete this._9l,
                    delete this._di
            },
            doDraw: function(t) {
                var i = this[aa];
                i && (i[d_](function(i) {
                        this.drawPoint(t, i)
                    },
                    this), this.isClosePath && t.closePath(), this[hC](t))
            },
            styleDraw: function(t) {
                var i = fr(this.graph.interactionProperties, this.getDefaultDrawStyles(this[Jf]));
                i[Wa] && (t[Wa] = i[Wa], i.lineCap && (t.lineCap = i[Jb]), i.lineJoin && (t[Bb] = i[Bb]), i.lineDash && (t[n_] = i.lineDash, t.lineDashOffset = i.lineDashOffset || 0), t.strokeStyle = i[nb], t[Ua]()),
                    i[Xv] && (t.fillStyle = i.fillStyle, t.fill())
            },
            drawPoint: function(t, i, e) {
                if (e) return void t.moveTo(i.x, i.y);
                if (DM.isArray(i)) {
                    var n = i[0],
                        s = i[1];
                    t[zv](n.x, n.y, s.x, s.y)
                } else t[Z_](i.x, i.y)
            },
            _fe: function(t) {
                this._ip || (this._ip = [], this[aC]()),
                    this._ip.push(t),
                    this.invalidate()
            }
        },
        Z(_r.prototype, {
            currentPoint: {
                get: function() {
                    return this._9l
                },
                set: function(t) {
                    this._9l = t,
                        this[Bm]()
                }
            },
            points: {
                get: function() {
                    return this._9l && this._ip && this._ip[jr] ? this._ip[Ka](this._9l) : void 0
                }
            }
        }),
        N(_r, ar),
        ur.prototype = {
            destroy: function() {
                B(this, ur, kf, arguments),
                    delete this._l2Time,
                    delete this[oC]
            },
            doDraw: function(t, i) {
                return this._ip ? this._ip.length <= 1 ? lr[ah].doDraw[Br](this, t, i) : void B(this, ur, _C, arguments) : void 0
            },
            ondblclick: function(t, i) {
                this[kf](i)
            },
            finish: function(t, i, e) {
                if (this._l2Time && Date.now() - this._l2Time < 200) return void this[kf](i);
                var n;
                this._ip && this._ip.length >= 2 && (this._ip[_y](), n = new LD, l(this._ip,
                        function(t) {
                            if (DM.isArray(t)) {
                                var i = t[0],
                                    e = t[1];
                                n[Lh](new dP(MM[fC], [i.x, i.y, e.x, e.y]))
                            } else n.add(new dP(MM.SEGMENT_LINE_TO, [t.x, t.y]))
                        },
                        this)),
                    i[uC](this.start, e, t, n),
                    this[kf](i)
            },
            onstart: function(t, i) {
                if (2 != t.button) {
                    var e = t.getData(),
                        n = e instanceof xN;
                    return this[oC] ? n ? void this[cC](t, i, e) : void this._fe(i[gS](t)) : void(n && (this.start = e, this._l2Time = Date[va](), this._fe(i.toLogical(t))))
                }
            },
            onmousemove: function(t, i) {
                this[oC] && (this[dC] = i.toLogical(t))
            },
            startdrag: function(t) {
                this.start && (t.responded = !0)

            },
            ondrag: function(t, i) {
                this[oC] && (this[dC] = i[gS](t))
            },
            enddrag: function(t, i) {
                if (this[oC]) {
                    var e = t.getData();
                    e instanceof xN && this.finish(t, i, e)
                }
            },
            getDefaultDrawStyles: function() {
                return {
                    lineWidth: this[Jf].getDefaultStyle(SN[Ux]),
                    strokeStyle: this[Jf].getDefaultStyle(SN[Xx]),
                    lineDash: this[Jf].getDefaultStyle(SN[Qx]),
                    lineDashOffset: this[Jf].getDefaultStyle(SN.EDGE_LINE_DASH_OFFSET),
                    lineCap: this[Jf].getDefaultStyle(SN.LINE_CAP),
                    lineJoin: this[Jf].getDefaultStyle(SN.LINE_JOIN)
                }
            }
        },
        N(ur, _r),
        cr.prototype = {
            getDefaultDrawStyles: function() {
                return {
                    lineWidth: this[Jf][GA](SN[eA]),
                    strokeStyle: this[Jf].getDefaultStyle(SN[mE]),
                    fillStyle: this[Jf].getDefaultStyle(SN.SHAPE_FILL_COLOR)
                }
            },
            finish: function(t, i) {
                if (this._ip && this._ip[jr]) {
                    var e = this._ip,
                        n = 0,
                        s = 0,
                        r = 0;
                    e[d_](function(t) {
                            return DM[gf](t) ? void t[d_](function() {
                                n += t.x,
                                    s += t.y,
                                    r++
                            }) : (n += t.x, s += t.y, void r++)
                        }),
                        n /= r,
                        s /= r;
                    var h = [];
                    e[d_](function(t, i) {
                            if (0 == i) return void h.push(new dP(MM[lC], [t.x - n, t.y - s]));
                            if (DM.isArray(t)) {
                                var e = t[0],
                                    r = t[1];
                                h[Yr](new dP(MM[fC], [e.x - n, e.y - s, r.x - n, r.y - s]))
                            } else h.push(new dP(MM[Cb], [t.x - n, t.y - s]))
                        }),
                        this.createElement(t, h, n, s),
                        this[kf](i)
                }
            },
            startdrag: function(t) {
                t.responded = !0
            },
            createElement: function(t, i, e, n) {
                return this[Jf][vC](t, i, e, n)
            },
            onstart: function(t, i) {
                var e = i.toLogical(t);
                this._di = e,
                    this._fe(e)
            },
            onmousemove: function(t, i) {
                this._di && (this[dC] = i.toLogical(t))
            },
            ondblclick: function(t, i) {
                if (this._di) {
                    if (this._ip[jr] < 3) return void this[kf](i);
                    delete this._ip[this._ip.length - 1],
                        this[cC](t, i)
                }
            },
            isClosePath: !0
        },
        N(cr, _r),
        DM.CreateShapeInteraction = cr,
        dr[ah] = {
            isClosePath: !1,
            createElement: function(t, i, e, n) {
                return this[Jf][bC](t, i, e, n)
            },
            getDefaultDrawStyles: function() {
                return {
                    lineWidth: UN[SN.SHAPE_STROKE],
                    strokeStyle: UN[SN[mE]],
                    lineDash: this.graph[GA](SN[yw]),
                    lineDashOffset: this[Jf].getDefaultStyle(SN.SHAPE_LINE_DASH_OFFSET),
                    lineCap: this[Jf][GA](SN.LINE_CAP),
                    lineJoin: this.graph[GA](SN[pw])
                }
            }
        },
        N(dr, cr),
        DM[gC] = dr,
        lr[ah] = {
            destroy: function(t) {
                B(this, lr, kf, arguments),
                    t.cursor = "",
                    this.start = null
            },
            doDraw: function(t) {
                if (this.start && this.currentPoint) {
                    var i,
                        e;
                    this.graph.interactionProperties && (i = this[Jf][_S].uiClass, e = this.graph[_S].edgeType),
                        i = i || this.graph[oS] || DM[yC],
                        e = e || this[Jf].edgeType;
                    var n = i.drawReferenceLine || DM.EdgeUI[NI],
                        s = this.graph[Hh](this.start);
                    s && s[mC] && (s = s.bodyBounds.center, n(t, s, this.currentPoint, e), this[hC](t))
                }
            },
            canLinkFrom: function(t, i) {
                return t instanceof xN && i[pC](t)
            },
            canLinkTo: function(t, i) {
                return t instanceof xN && i[EC](t, this.start)
            },
            startdrag: function(t, i) {
                var e = t[wA]();
                this.canLinkFrom(e, i) && (t[xC] = !0, this[oC] = e, i.cursor = yl, this.addDrawable())
            },
            ondrag: function(t, i) {
                this.start && (DM.stopEvent(t), this.currentPoint = i.toLogical(t), this.invalidate())
            },
            enddrag: function(t, i) {
                if (this.start) {
                    this.invalidate();
                    var e = t[wA]();
                    this[EC](e, i) && i[uC](this.start, e, t),
                        this[kf](i)
                }
            },
            getDefaultDrawStyles: function() {
                return {
                    lineWidth: this.graph.getDefaultStyle(SN[Ux]),
                    strokeStyle: this[Jf][GA](SN.EDGE_COLOR),
                    lineDash: this[Jf][GA](SN[Qx]),
                    lineDashOffset: this.graph.getDefaultStyle(SN[TC]),
                    lineCap: this.graph[GA](SN.LINE_CAP),
                    lineJoin: this.graph[GA](SN[pw])
                }
            }
        },
        N(lr, _r),
        DM[wC] = lr,
        pr.prototype = {
            html: null,
            createHTML: function() {
                var t = i.createElement(OC);
                t[Hr] = IC,
                    t[Oa][Ko] = gu,
                    t.style.textAlign = tu,
                    t[Oa][Bl] = AC,
                    t.style.padding = SC,
                    t.style[CC] = "0px 0px 10px rgba(40, 85, 184, 0.75)",
                    t.style[Om] = bu,
                    t.style.overflow = vu;
                var e = this;
                return t.oninput = function(t) {
                        e.onValueChange(t)
                    },
                    t[kC] = function(t) {
                        return 27 == t.keyCode ? void e.cancelEdit() : void 0
                    },
                    t[LC] = function(i) {
                        if (13 == i.keyCode || 10 == i[RC]) {
                            if (i.preventDefault(), i[DC] || i[By] || i.shiftKey) return yr(t, Na),
                                void e[MC](i);
                            e[PC]()
                        }
                    },
                    i.body.appendChild(t),
                    t
            },
            setText: function(t, i) {
                this.html.value = t || "",
                    i && (this[sl].style.fontSize = i),
                    mr(this[sl]),
                    this.onSizeChange(this[sl])
            },
            onSizeChange: function(t) {
                var i = (t.offsetWidth, t[Xu], gr(t));
                return t.style.width = i.width + 30 + Ia,
                    t[Oa].height = i[Fa] + 10 + Ia,
                    i
            },
            onValueChange: function(t) {
                var i = t[sE];
                this[NC](i),
                    i.style.left = i.x - i.offsetWidth / 2 + Ia
            },
            startEdit: function(i, e, n, s, r) {
                if (this[sl] || (this.html = this.createHTML()), !this[jC]) {
                    var h = this;
                    this.stopEditWhenClickOnWindow = function(t) {
                        t.target != h[sl] && h.cancelEdit()
                    }
                }
                t[Ud](BC, this[jC], !0),
                    this.callback = r,
                    this[sl].x = i,
                    this.html.y = e,
                    this[sl].style[Om] = kS,
                    br(this.html, i, e),
                    this[zC](n, s || 10),
                    br(this.html, i, e)
            },
            isEditing: function() {
                return bu != this[sl].style[Om]
            },
            cancelEdit: function() {
                this.stopEdit(!0)
            },
            stopEdit: function(i) {
                if (this[$C]()) {
                    t.removeEventListener(BC, this.stopEditWhenClickOnWindow);
                    var e = this[sl].value;
                    if (!i && this[pf] && this.callback(e) === !1) return !1;
                    this.html.style.display = bu,
                        this.html.value = null,
                        this[pf] = null
                }
            },
            destroy: function() {
                this[sl] && i.body.removeChild(this.html)
            }
        },
        DM.LabelEditor = pr;
    var ej = function(t) {
        this[Jf] = t
    };
    ej[ah] = {
        destroy: function(t) {
            t[GC] && (t[GC].destroy(), delete t.labelEditor)
        },
        ondblclick: function(t, i) {
            var e = t[wA]();
            if (!e) return i[pp] ? void i[FC]() : void(i[YC] && i.zoomToOverview(CD[Su]));
            if (i[DI] && i[qC](e)) {
                var n = i.hitTest(t);
                if (n instanceof GN && n[DI] !== !1) {
                    var s = i[GC];
                    s || (i[GC] = s = new pr);
                    var r = n[Ya]();
                    return r = i[HC](r.x + r[xa] / 2, r.y + r.height / 2),
                        r = vr(r.x, r.y, i.html),
                        void i.startLabelEdit(e, n, s, r)
                }
            }
            var h = e instanceof ON,
                a = e instanceof EN && e.hasEdgeBundle();
            return e._4a && (Oi(t) || !h && !a) ? void(i[pp] = e) : h ? void(e.expanded = !e.expanded) : a ? void this[Jf].reverseExpanded(e) : void 0
        }
    };
    var nj = function(t) {
        this.graph = t
    };
    nj.prototype = {
        onkeydown: function(t, i) {
            if (i.editable) {
                var e = t[RC];
                if (8 == e || 46 == e || 127 == e) return i[UC](t),
                    void k(t);
                if (Oi(t)) {
                    if (67 == e);
                    else if (86 == e);
                    else if (90 == e);
                    else if (89 != e) return;
                    k(t)
                }
            }
        }
    };
    var sj = function(t) {
        this[Jf] = t
    };
    sj[ah] = {
        onkeydown: function(i, e) {
            if (i.metaKey && 83 == i[RC]) {
                var n = e.exportImage(e.scale, e[$S]),
                    s = t[WC](),
                    r = s.document;
                r.title = XC + n.width + VC + n.height;
                var h = r.createElement(ou);
                h.src = n[go],
                    r.body.appendChild(h),
                    k(i)
            }
        }
    };
    var rj = function(t) {
        this[Jf] = t
    };
    rj.prototype = {
        destroy: function() {
            delete this.draggingElements,
                delete this[KC]
        },
        _2m: function(t) {
            var i = new LD;
            return t[Ou][d_](function(e) {
                        t.isMovable(e) && t.isVisible(e) && i.add(e)
                    },
                    this),
                i
        },
        onstart: function(t, i) {
            this.currentDraggingElement && this[kf](i)
        },
        startdrag: function(t, i) {
            if (!t.responded) {
                var e = t.getData();
                if (!e || !i[ZC](e) || !i[JC](e)) return void this[kf](i);
                t[xC] = !0,
                    this[KC] = e,
                    this[QC] = this._2m(i);
                var n = new Ir(i, Ir[tk], t, this[KC], this.draggingElements.datas);
                return i[ik](n) === !1 ? void this.destroy(i) : void i[BA](n)
            }
        },
        ondrag: function(t, i) {
            if (this.currentDraggingElement) {
                R(t);
                var e = t.dx,
                    n = t.dy,
                    s = i.scale;
                e /= s,
                    n /= s;
                var r = new Ir(i, Ir[ek], t, this[KC], this.draggingElements[nk]);
                i[sk](this.draggingElements[nk], e, n),
                    i.onInteractionEvent(r)
            }
        },
        enddrag: function(t, i) {
            if (this[KC]) {
                if (this.draggingElements && this.draggingElements[jr]) {
                    if (t[MA]) {
                        var e,
                            n = i[gS](t),
                            s = n.x,
                            r = n.y;
                        i.forEachReverseVisibleUI(function(t) {
                                    var i = t[go];
                                    if (!this.draggingElements[iu](i) && t[Cf].intersectsPoint(s - t.x, r - t.y) && t._h9(s, r, 1)) {
                                        if (i instanceof DM.Edge) {
                                            if (!i.enableSubNetwork) return;
                                            for (var n = this[QC].length; n-- > 0;) {
                                                var h = this.draggingElements[rk](n);
                                                if (h instanceof DM.Node && h[hk](i)) return
                                            }
                                            return e = i, !1
                                        }
                                        return (i.enableSubNetwork || i._hd() && i[Ip]) && (e = i), !1
                                    }
                                },
                                this),
                            e && this[QC][d_](function(t) {
                                    for (var i = t.parent; i;) {
                                        if (this[QC][iu](i)) return;
                                        i = i[B_]
                                    }
                                    t[B_] = e
                                },
                                this)
                    }
                    var h = new Ir(i, Ir.ELEMENT_MOVE_END, t, this.currentDraggingElement, this.draggingElements[nk]);
                    i[BA](h)
                }
                this.destroy(i)
            }
        },
        onpinch: function(t, i) {
            this.currentDraggingElement && this[el](t, i)
        },
        step: 1,
        onkeydown: function(t, i) {
            if (Oi(t)) {
                var e,
                    n;
                if (37 == t[RC] ? e = -1 : 39 == t[RC] ? e = 1 : 38 == t.keyCode ? n = -1 : 40 == t[RC] && (n = 1), e || n) {
                    var s = this._2m(i)[nk];
                    if (0 != s[jr]) {
                        k(t),
                            e = e || 0,
                            n = n || 0;
                        var r = this[ak] / i.scale,
                            h = new Ir(i, Ir.ELEMENT_MOVE_END, t, null, s);
                        i[sk](s, e * r, n * r),
                            i[BA](h)
                    }
                }
            }
        }
    };
    var hj = function(t) {
        this[Jf] = t
    };
    hj[ah] = {
            onkeydown: function(t, i) {
                Oi(t) || (37 == t[RC] ? (this._58(i, 1, 0), k(t)) : 39 == t[RC] ? (this._58(i, -1, 0), k(t)) : 38 == t.keyCode ? (this._58(i, 0, 1), k(t)) : 40 == t.keyCode && (this._58(i, 0, -1), k(t)))
            },
            _58: function(t, i, e) {
                t._9m(i, e)
            },
            onstart: function(t, i) {
                this._l2 && this[kf](i)
            },
            _l2: !1,
            startdrag: function(t, i) {
                t[xC] || (t[xC] = !0, this._l2 = !0, i.cursor = TM)
            },
            ondrag: function(t, i) {
                this._l2 && (R(t), i[fo](t.dx || 0, t.dy || 0))
            },
            enddrag: function(t, i) {
                if (this._l2) {
                    if (i[ok] !== !1) {
                        var e = t.vx,
                            n = t.vy;
                        (Math.abs(e) > .1 || Math[Sh](n) > .1) && i._9m(e, n)
                    }
                    this[kf](i)
                }
            },
            onpinch: function(t, i) {
                this._l2 = !0;
                var e = t.dScale;
                if (e && 1 != e) {
                    var n = i.globalToLocal(t.center);
                    i.zoomAt(e, n.x, n.y, !1)
                }
            },
            destroy: function(t) {
                this._l2 = !1,
                    t[yS] = null
            }
        },
        Er.prototype = {
            onElementRemoved: function(t, i) {
                this.element && (t == this.element || C(t) && p(t, this.element)) && this.destroy(i)
            },
            onClear: function(t) {
                this[sC] && this[kf](t)
            },
            destroy: function(t) {
                t.cursor = null,
                    this[sC] && delete this[sC]._editting,
                    delete this.element,
                    delete this._9q,
                    delete this._9l,
                    delete this._n0anEdit,
                    this._71()
            },
            _71: function() {
                this.drawLineId && (this[qu][rC](this[_k]), delete this.drawLineId, this.topCanvas[Bm]())
            },
            _mya: function() {
                this.drawLineId && this[qu].contains(this.drawLineId) || (this.drawLineId = this.topCanvas[aC](this.drawLine, this).id, this.topCanvas[Bm]())
            },
            _9q: null,
            _55: function(t) {
                this._9q = t,
                    this[Bm]()
            },
            _e6: function(t, i, e, n) {
                t.beginPath(),
                    t[gg](i, e, this.handlerSize / n, 0, 2 * Math.PI, !1),
                    t[Wa] = 1 / n,
                    t[n_] = [],
                    t[nb] = Qv,
                    t[Xv] = "rgba(255, 255, 0, 0.8)",
                    t.stroke(),
                    t.fill()
            },
            _fz: function(t, i, e, n) {
                n ? t.moveTo(i, e) : t[Z_](i, e)
            },
            drawLine: function(t, i) {
                if (this._9q && this._9q.length) {
                    t.save();
                    var e = this[sC] instanceof TN;
                    e && (t.translate(this[sC].x, this.element.y), this[sC].rotate && t[Za](this[sC].rotate));
                    var n,
                        s = [];
                    t[dg](),
                        l(this._9q,
                            function(i) {
                                if (i[N_] != MM.SEGMENT_CLOSE)
                                    for (var e = 0, r = i.points; e + 1 < r[jr];) {
                                        var h = r[e],
                                            a = r[e + 1],
                                            o = {
                                                x: h,
                                                y: a
                                            };
                                        s.push(o),
                                            this._fz(t, o.x, o.y, null == n),
                                            n = o,
                                            e += 2
                                    }
                            },
                            this),
                        t.lineWidth = 1 / i,
                        t[n_] = [2 / i, 3 / i],
                        t[nb] = fk,
                        t[Ua](),
                        l(s,
                            function(e) {
                                this._e6(t, e.x, e.y, i)
                            },
                            this),
                        t.restore()
                }
            },
            invalidate: function() {
                this[qu].invalidate()
            },
            _42: function(t, i) {
                this[sC] = t,
                    t._editting = !0,
                    this._n0anEdit = !0,
                    this._55(i)
            },
            _5a: function(t, i) {
                if (i[qC](t)) {
                    var e = t[uk];
                    if (e && 0 != e.length) return e
                }
            },
            _ho: function(t, i) {
                t -= this.element.x,
                    i -= this.element.y;
                var e = {
                    x: t,
                    y: i
                };
                return this.element[Za] && Ms(e, -this.element[Za]),
                    e
            },
            ondblclick: function(t, i) {
                if (!i[DI]) return void(this.element && this[kf](i));
                var e = t.getData();
                if (!e || e == this[sC] || e._editting) return void this[kf](i);
                var n = this._5a(e, i);
                return n ? (this._mya(), void this._42(e, n)) : void(this.element && this[kf](i))
            },
            onstart: function(t, i) {
                if (!i[DI]) return void(this[sC] && this[kf](i));
                if (!t.responded) {
                    var e = t[wA]();
                    if (e == this[sC]) return void(this.element && this._mya());
                    if (this.element && this._g2(t, i)) return void(t[xC] = !0);
                    if (this[sC]) return void this.destroy(i);
                    if (e instanceof EN) {
                        var n = this._5a(e, i);
                        if (!n) return void(this.element && this.destroy(i));
                        this._mya(),
                            this._42(e, n)
                    }
                }
            },
            onrelease: function() {
                this[sC] && (this._n0anEdit = !0)
            },
            _9l: null,
            _g2: function(t, i) {
                var e = i[gS](t);
                this[sC] instanceof TN && (e = this._ho(e.x, e.y));
                var n,
                    s = i.scale,
                    r = this[Hu] / s;
                return l(this._9q,
                        function(t, i) {
                            for (var s = 0, h = t.points; s + 1 < h.length;) {
                                var a = h[s],
                                    o = h[s + 1],
                                    _ = PD(e.x, e.y, a, o);
                                if (r > _) return n = {
                                    segment: t,
                                    index: i,
                                    pointIndex: s
                                }, !1;
                                s += 2
                            }
                        },
                        this),
                    n
            },
            startdrag: function(t, i) {
                if (this.element && this._n0anEdit && (this._9l = this._g2(t, i), this._9l)) {
                    this._71(),
                        t[xC] = !0;
                    var e = new Ir(i, Ir.POINT_MOVE_START, t, this.element);
                    e[ck] = this._9l,
                        i.onInteractionEvent(e)
                }
            },
            ondrag: function(t, i) {
                if (this[sC] && this._9l) {
                    var e = t.dx,
                        n = t.dy,
                        s = i.scale;
                    if (e /= s, n /= s, this[sC][Za]) {
                        var r = {
                            x: e,
                            y: n
                        };
                        Ms(r, -this.element.rotate),
                            e = r.x,
                            n = r.y
                    }
                    var h = this._9l.segment,
                        a = this._9l.pointIndex;
                    h[aa][a] += e,
                        h[aa][a + 1] += n,
                        this.element.firePathChange();
                    var o = new Ir(i, Ir.POINT_MOVING, t, this.element);
                    o.point = this._9l,
                        i[BA](o)
                }
            },
            enddrag: function(t, i) {
                if (this.element && this._9l) {
                    this._mya();
                    var e = new Ir(i, Ir.POINT_MOVE_END, t, this[sC]);
                    e[ck] = this._9l,
                        i.onInteractionEvent(e)
                }
            }
        },
        CD.SELECTION_RECTANGLE_STROKE = 1,
        CD.SELECTION_RECTANGLE_STROKE_COLOR = V(3724541951),
        CD.SELECTION_RECTANGLE_FILL_COLOR = V(1430753245);
    var aj = function(t) {
        this[Jf] = t,
            this.topCanvas = t._7u._topCanvas
    };
    aj.prototype = {
        onstart: function(t, i) {
            this._l2 && this.destroy(i)
        },
        startdrag: function(t, i) {
            t[xC] || (t[xC] = !0, this._l2 = i.toLogical(t), i[yS] = yl, this._18Id = this[qu].addDrawable(this._18, this).id)
        },
        ondrag: function(t, i) {
            if (this._l2) {
                R(t),
                    this._end = i.toLogical(t),
                    this.invalidate();
                var e = new Ir(i, Ir[dk], t, i[Ou]);
                i[BA](e)
            }
        },
        enddrag: function(t, i) {
            if (this._l2) {
                this._faTimer && clearTimeout(this._faTimer),
                    this._fa(!0),
                    this[kf](i);
                var e = new Ir(i, Ir.SELECT_END, t, i.selectionModel);
                i[BA](e)
            }
        },
        onpinch: function(t, i) {
            this._l2 && this.enddrag(t, i)
        },
        _18: function(t, i) {
            t[nb] = CD[lk],
                t[Xv] = CD[vk],
                t.lineWidth = CD[bk] / i;
            var e = this._l2.x,
                n = this._l2.y;
            t.rect(e, n, this._end.x - e, this._end.y - n),
                t[yg](),
                t.stroke()
        },
        invalidate: function() {
            return this[TS] ? void this.topCanvas.invalidate() : (this[TS] = !0, void(this._faTimer = setTimeout(this._fa[qv](this), 100)))
        },
        _fa: function(t) {
            if (this._faTimer = null, this[TS] = !1, !this._l2) return void this.topCanvas[Bm]();
            var i = Math[Ga](this._l2.x, this._end.x),
                e = Math[Ga](this._l2.y, this._end.y),
                n = Math.abs(this._l2.x - this._end.x),
                s = Math[Sh](this._l2.y - this._end.y);
            if (!n || !s) return void this.graph.selectionModel.clear();
            var r,
                h = [],
                a = this[Jf][Ao];
            if (this.graph.forEachVisibleUI(function(t) {
                        t._hk && this.graph[gk](t.$data) && (r = t._f9, (ai(i, e, n, s, r.x + t._x, r.y + t._y, r[xa], r[Fa]) || Le(i, e, n, s, t, a)) && h[Yr](t[Xp]))
                    },
                    this), this.graph.selectionModel[Vo](h), this.topCanvas[Bm](), !t) {
                var o = new Ir(this.graph, Ir.SELECT_BETWEEN, null, this[Jf].selectionModel);
                this[Jf].onInteractionEvent(o)
            }
        },
        destroy: function(t) {
            this._l2 = null,
                t.cursor = null,
                this._18Id && (this.topCanvas[rC](this._18Id), delete this._18Id, this[qu][Bm]())
        }
    };
    var ij = Math.PI / 4;
    xr.prototype = {
            _e9: !1,
            _e7: !1,
            onElementRemoved: function(t, i) {
                this[sC] && (t == this[sC] || C(t) && p(t, this.element)) && this.destroy(i)
            },
            onClear: function(t) {
                this[sC] && this[kf](t)
            },
            ondblclick: function(t, i) {
                this[sC] && this[kf](i)
            },
            destroy: function(t) {
                t[yS] = null,
                    delete this.element,
                    delete this._mxj,
                    delete this._mxody,
                    delete this._9l,
                    delete this._n0anEdit,
                    delete this._ip,
                    delete this._rotatePoint,
                    delete this._e7,
                    delete this._e9,
                    delete this._insets,
                    this._71()
            },
            _71: function() {
                this._fzId && (this.topCanvas.removeDrawable(this._fzId), delete this._fzId, this[qu].invalidate())
            },
            _mya: function() {
                this._fzId && this[qu].contains(this._fzId) || (this._fzId = this.topCanvas[aC](this._fz, this).id, this[qu].invalidate())
            },
            _mxj: null,
            _ip: null,
            _8c: function(t) {
                this._mxj = t;
                var i = this._mxj.x,
                    e = this._mxj.y,
                    n = this._mxj[xa],
                    s = this._mxj[Fa];
                if (this._e7) {
                    var r = [];
                    r.push({
                            x: i,
                            y: e,
                            p: $D.LEFT_TOP,
                            cursor: yk,
                            rotate: 5 * ij
                        }),
                        r[Yr]({
                            x: i + n / 2,
                            y: e,
                            p: $D[qc],
                            cursor: mk,
                            rotate: 6 * ij
                        }),
                        r[Yr]({
                            x: i + n,
                            y: e,
                            p: $D[pk],
                            cursor: Uu,
                            rotate: 7 * ij
                        }),
                        r[Yr]({
                            x: i,
                            y: e + s / 2,
                            p: $D.LEFT_MIDDLE,
                            cursor: Ek,
                            rotate: 4 * ij
                        }),
                        r.push({
                            x: i + n,
                            y: e + s / 2,
                            p: $D[Fc],
                            cursor: Ek,
                            rotate: 0
                        }),
                        r.push({
                            x: i,
                            y: e + s,
                            p: $D.LEFT_BOTTOM,
                            cursor: Uu,
                            rotate: 3 * ij
                        }),
                        r[Yr]({
                            x: i + n / 2,
                            y: e + s,
                            p: $D[Hc],
                            cursor: mk,
                            rotate: 2 * ij
                        }),
                        r[Yr]({
                            x: i + n,
                            y: e + s,
                            p: $D[Yc],
                            cursor: yk,
                            rotate: ij
                        }),
                        this._ip = r
                }
                this._rotatePoint = this._e9 ? {
                        x: i + n / 2,
                        y: e,
                        cursor: wM
                    } : null,
                    this._mxk()
            },
            _e6: function(t, i, e, n) {
                t[dg]();
                var s = (this.handlerSize - 1) / n;
                t.rect(i - s, e - s, 2 * s, 2 * s),
                    t[Wa] = 1 / n,
                    t[n_] = [],
                    t[nb] = Qv,
                    t.fillStyle = "rgba(255, 255, 255, 0.8)",
                    t[Ua](),
                    t[yg]()
            },
            _5c: function(t, i, e, n, s, r) {
                s = s || this[Hu],
                    r = r || xk,
                    t[dg](),
                    s /= n,
                    t[gg](i, e, s, 0, 2 * Math.PI, !1),
                    t[Wa] = 1 / n,
                    t[n_] = [],
                    t.strokeStyle = Qv,
                    t.fillStyle = r,
                    t.stroke(),
                    t[yg]()
            },
            _ho: function(t, i) {
                t -= this[sC].x,
                    i -= this.element.y;
                var e = {
                    x: t,
                    y: i
                };
                return this[sC][Za] && Ms(e, -this[sC][Za]),
                    e
            },
            _fz: function(t, i) {
                if (this._mxj) {
                    if (t[Wv](), t[fo](this.element.x, this.element.y), this.element[Za] && t[Za](this[sC].rotate), this._rotatePoint) {
                        this._5c(t, 0, 0, i, 3, Tk);
                        var e = this._rotatePoint.x,
                            n = this._rotatePoint.y - this._rotateHandleLength / i;
                        t.beginPath(),
                            t.moveTo(e, this._rotatePoint.y),
                            t.lineTo(e, n),
                            t.lineWidth = 1 / i,
                            t.strokeStyle = fk,
                            t.stroke(),
                            this._5c(t, e, n, i)
                    }
                    if (this._ip) {
                        var s = this._mxj.x,
                            r = this._mxj.y,
                            h = this._mxj.width,
                            a = this._mxj.height;
                        t[dg](),
                            t[tv](s, r, h, a),
                            t.lineWidth = 1 / i,
                            t.lineDash = [2 / i, 3 / i],
                            t.strokeStyle = fk,
                            t[Ua](),
                            l(this._ip,
                                function(e) {
                                    this._e6(t, e.x, e.y, i)
                                },
                                this)
                    }
                    t[hb]()
                }
            },
            _mxk: function() {
                this.topCanvas[Bm]()
            },
            _42: function(t, i, e, n) {
                this.element = t,
                    this._mya();
                var s = i.getUI(t);
                this._mxody = s[BO],
                    this._e7 = e,
                    this._e9 = n,
                    this._9s()
            },
            _9s: function() {
                var t = Tr(this._mxody, this._mxody._iq),
                    i = Tr(this._mxody, this._mxody._7x);
                this._insets = new zD(t.y - i.y, t.x - i.x, i[Ah] - t[Ah], i.right - t.right),
                    this._8c(i)
            },
            _myy: function(t, i) {
                return (!t._hd() || !t[Ip]) && i[wk](t)
            },
            _myz: function(t, i) {
                return (!t._hd() || !t[Ip]) && i.isRotatable(t)
            },
            _mxl: function(t, i) {
                return t instanceof xN && i[qC](t)
            },
            onstart: function(t, i) {
                if (!i[DI]) return void(this.element && this[kf](i));
                if (!t[xC]) {
                    var e = i.getUI(t),
                        n = t[wA]();
                    if (n != this.element) {
                        if (this.element) {
                            if (this._g2(t, i)) return void(t.responded = !0);
                            this.destroy(i)
                        }
                        if (n && !n._editting && this._mxl(n, i)) {
                            var s = this._myy(n, i, e),
                                r = this._myz(n, i, e);
                            (s || r) && this._42(n, i, s, r)
                        }
                    }
                }
            },
            onrelease: function(t, i) {
                this[sC] && (this._n0anEdit = !0, this._mya(), i.callLater(function() {
                        this._9s()
                    },
                    this))
            },
            _9l: null,
            _g2: function(t, i) {
                var e = i[gS](t);
                e = this._ho(e.x, e.y);
                var n = i[Ao],
                    s = this[Hu] / n;
                if (this._rotatePoint) {
                    var r = this._rotatePoint.x,
                        h = this._rotatePoint.y - this._rotateHandleLength / n;
                    if (PD(e.x, e.y, r, h) < s) return this._rotatePoint
                }
                if (this._ip && this._ip[jr]) {
                    var a;
                    return l(this._ip,
                            function(t) {
                                return PD(e.x, e.y, t.x, t.y) < s ? (a = t, !1) : void 0
                            },
                            this),
                        a
                }
            },
            onmousemove: function(t, i) {
                if (this.element) {
                    var e = this._g2(t, i);
                    if (!e) return void(i.cursor = null);
                    if (e != this._rotatePoint && this[sC][Za]) {
                        var n = e.rotate + this[sC].rotate;
                        return void(i[yS] = wr(n))
                    }
                    i[yS] = e.cursor
                }
            },
            startdrag: function(t, i) {
                if (this[sC] && (this._71(), this._n0anEdit && (this._9l = this._g2(t, i), this._9l))) {
                    if (t[xC] = !0, this._9l == this._rotatePoint) return this._9l.start = i[gS](t),
                        void(this._9l.rotate = this[sC].rotate || 0);
                    var e = new Ir(i, Ir[Ok], t, this.element);
                    e.point = this._9l,
                        i.onInteractionEvent(e)
                }
            },
            _77: function(t, i, e, n, s, r) {
                var h = this._mxj,
                    a = h.x,
                    o = h.y,
                    _ = h[xa],
                    f = h[Fa];
                if (r) {
                    var u = n != _;
                    u ? s = n * f / _ : n = s * _ / f
                }
                var c = t.path._eq,
                    d = n / _,
                    l = s / f,
                    v = -a * d + i,
                    b = -o * l + e;
                c[d_](function(t) {
                        if (t[N_] != MM[Ik]) {
                            var n = t[aa];
                            if (n && n.length)
                                for (var s = 0, r = n.length; r > s; s += 2) {
                                    var h = n[s],
                                        _ = n[s + 1];
                                    n[s] = (h - a) * d + i - v,
                                        n[s + 1] = (_ - o) * l + e - b
                                }
                        }
                    }),
                    this._mxj[Vo](i - v, e - b, n, s),
                    t[Ak](t.x + v, t.y + b),
                    t.firePathChange()
            },
            _4l: function(t, i, e, n, s) {
                if (this.element instanceof TN) return this._77(this[sC], t, i, e, n, s);
                var r = this._mxody instanceof GN;
                if (!r && s) {
                    var h = this._mxj,
                        a = this._mxody.originalBounds,
                        o = e != h.width;
                    o ? n = e * a[Fa] / a[xa] : e = n * a.width / a[Fa]
                }
                var _ = this[sC].anchorPosition,
                    f = new jD(e - this._insets.left - this._insets[zf], n - this._insets[oo] - this._insets[Ah]);
                if (f.width < 1 && (e = this._insets.left + this._insets[zf] + 1, f[xa] = 1), f[Fa] < 1 && (n = this._insets[oo] + this._insets[Ah] + 1, f.height = 1), r ? this.element.setStyle(SN[gx], f) : this.element.size = f, _) {
                    var u = oi(_, e, n),
                        c = u.x + t - (this._mxody[Bp] || 0),
                        d = u.y + i - (this._mxody.offsetY || 0);
                    if (this._mxj[Vo](t - c, i - d, e, n), this[sC][Za]) {
                        var u = Ms({
                                x: c,
                                y: d
                            },
                            this.element[Za]);
                        c = u.x,
                            d = u.y
                    }
                    this[sC].x += c,
                        this.element.y += d
                } else {
                    var c = this._mxj.x * e / this._mxj.width - t,
                        d = this._mxj.y * n / this._mxj.height - i;
                    if (this._mxj.set(t + c, i + d, e, n), this.element.rotate) {
                        var u = Ms({
                                x: c,
                                y: d
                            },
                            this.element.rotate);
                        c = u.x,
                            d = u.y
                    }
                    this[sC].x -= c,
                        this[sC].y -= d
                }
            },
            ondrag: function(t, i) {
                if (this[sC] && this._9l) {
                    if (this._9l == this._rotatePoint) {
                        var e = i.toLogical(t),
                            n = de(e.x, e.y, this[sC].x, this.element.y, this._9l[oC].x, this._9l.start.y, !0);
                        return n += this._9l[Za] || 0,
                            t.shiftKey && (n = Math[Ha](n / Math.PI * 4) * Math.PI / 4),
                            void(this.element.rotate = n % (2 * Math.PI))
                    }
                    var s = t.dx,
                        r = t.dy,
                        h = i[Ao];
                    if (s /= h, r /= h, this[sC].rotate) {
                        var e = {
                            x: s,
                            y: r
                        };
                        Ms(e, -this[sC].rotate),
                            s = e.x,
                            r = e.y
                    }
                    var a = this._9l.p,
                        o = this._mxj,
                        _ = o.x,
                        f = o.y,
                        u = o.width,
                        c = o[Fa];
                    a[Ch] == GD ? s >= u ? (_ += u, u = s - u || 1) : (_ += s, u -= s) : a.horizontalPosition == YD && (-s >= u ? (u = -s - u || 1, _ -= u) : u += s),
                        a.verticalPosition == qD ? r >= c ? (f += c, c = r - c || 1) : (f += r, c -= r) : a[kh] == UD && (-r >= c ? (c = -r - c || 1, f -= c) : c += r),
                        this._4l(_, f, u, c, t[MA]);
                    var d = new Ir(i, Ir[Sk], t, this[sC]);
                    d[ck] = this._9l,
                        i.onInteractionEvent(d)
                }
            },
            enddrag: function(t, i) {
                if (this.element && this._9l && this._9l != this._rotatePoint) {
                    var e = new Ir(i, Ir.RESIZE_END, t, this.element);
                    e.point = this._9l,
                        i.onInteractionEvent(e)
                }
            }
        },
        DM.ResizeInteraction = xr;
    var oj = function(t) {
        this.graph = t
    };
    oj[ah] = {
        onstart: function(t, i) {
            if (!t[xC]) {
                OD || lD || i.focus(!0);
                var e = t[wA]();
                if (e && !i.isSelectable(e) && (e = null), e && Oi(t)) {
                    i[XA](e);
                    var n = new Ir(i, Ir.SELECT, t, i[Ou]);
                    return void i.onInteractionEvent(n)
                }
                if (!e || !i.selectionModel.isSelected(e)) {
                    e ? (i.setSelection(e), i[Ck](e)) : i[kk](null);
                    var n = new Ir(i, Ir[Lk], t, i.selectionModel);
                    i.onInteractionEvent(n)
                }
            }
        },
        onkeydown: function(t, i) {
            return 27 == t.keyCode ? void i[VA]() : void(Oi(t) && 65 == t[RC] && (i.selectAll(), k(t)))
        }
    };
    var _j = 0,
        fj = 15;
    CD.TOOLTIP_DURATION = 3e3,
        CD[Rk] = 1e3;
    var uj = function(t) {
        this[Jf] = t
    };
    uj.prototype = {
        _my2: {},
        _my0: null,
        _9y: function() {
            delete this._initTimer,
                this._my2.data && (this._my0 || (this._my0 = i.createElement(hu), this._my0.className = Dk, DM[Mk](this._my0, {
                    "background-color": Pk,
                    overflow: vu,
                    "box-shadow": "0 5px 10px rgba(136, 136, 136, 0.5)",
                    color: sb,
                    "pointer-events": bu,
                    border: Nk,
                    padding: jk,
                    display: kS,
                    position: gu
                })), this._my0[Hv] || i[BO][If](this._my0), this._mxz(this[Jf], this._my2[go]))
        },
        _mxz: function(t, i) {
            var e = t[Bk](i),
                n = OA == i.tooltipType;
            e && !n && (e = e.replace(/\n/g, zk)),
                n ? this._my0[$k] = e || "" : this._my0.innerHTML = e || "";
            var s = this._my2.evt.pageX + _j,
                r = this._my2.evt.pageY + fj;
            Or(this._my0, s, r),
                this._deleteTimer && (clearTimeout(this._deleteTimer), delete this._deleteTimer),
                this._deleteTimer = setTimeout(DM[Gk](this, this._8o), t.tooltipDuration || CD[Fk])
        },
        _8o: function() {
            delete this._deleteTimer,
                this._my0 && this._my0[Hv] && this._my0[Hv][Gv](this._my0),
                delete this._my0,
                this._my2 = {}
        },
        _em: function(t, i, e, n) {
            if (!this._my0) {
                var s = n.tooltipDelay;
                return isNaN(s) && (s = CD[Rk]),
                    void(this._initTimer = setTimeout(DM.createFunction(this, this._9y), s))
            }
            this._mxz(n, t)
        },
        onstart: function(t, i) {
            this.destroy(i)
        },
        onmousemove: function(t, i) {
            if (i[Yk]) {
                var e = t[wA]();
                if (this._my2.evt = t, this._my2[go] != e && (this._my2.data = e, this._initTimer && (clearTimeout(this._initTimer), delete this._initTimer), e)) {
                    var n = i.getTooltip(e);
                    n && this._em(e, n, t, i)
                }
            }
        },
        destroy: function() {
            this._initTimer && (clearTimeout(this._initTimer), delete this._initTimer),
                this._deleteTimer && (clearTimeout(this._deleteTimer), delete this._deleteTimer),
                this._my0 && this._8o()
        }
    };
    var cj = function(t) {
        this.graph = t
    };
    cj[ah] = {
        onmousewheel: function(t, i) {
            if (i[qk] !== !1) {
                if (i._scaling) return void k(t);
                i._scaling = !0,
                    E(function() {
                            delete i._scaling
                        },
                        this, 100),
                    rr(i, t, t.delta > 0) !== !1 && k(t)
            }
        }
    };
    var dj = function(t) {
        this[Jf] = t
    };
    dj.prototype = {
        onclick: function(t, i) {
            rr(i, t, !Oi(t))
        }
    };
    var lj = function(t) {
        this.graph = t
    };
    lj.prototype = {
            onclick: function(t, i) {
                rr(i, t, Oi(t))
            }
        },
        N(Ir, VD),
        Ir.ELEMENT_MOVE_START = Hk,
        Ir.ELEMENT_MOVING = Uk,
        Ir[Wk] = Xk,
        Ir[jA] = Vk,
        Ir.ELEMENT_REMOVED = Kk,
        Ir[Zk] = Jk,
        Ir.POINT_MOVING = Qk,
        Ir[tL] = iL,
        Ir[Ok] = eL,
        Ir.RESIZING = nL,
        Ir.RESIZE_END = sL,
        Ir[rL] = hL,
        Ir[Lk] = $u,
        Ir[dk] = aL,
        Ir.SELECT_BETWEEN = oL,
        Ir[_L] = fL,
        Ir[uL] = cL,
        Ar[ah] = {
            _92: function(t) {
                if (this._interactionSupport) switch (t[Ku]) {
                    case rM[jd]:
                        this._interactionSupport._4i(t[go]);
                        break;
                    case rM[pd]:
                        this._interactionSupport._7c(t[go])
                }
            },
            destroy: function() {
                delete this._k2,
                    delete this._52,
                    this._interactionSupport && (this._interactionSupport._hn(), delete this._interactionSupport)
            },
            _k2: null,
            _52: null,
            defaultMode: null,
            _gr: function(t, i, e) {
                this._52[t] = new or(i, e),
                    t == this.currentMode && this._interactionSupport._7f(i)
            },
            addCustomInteraction: function(t) {
                this._interactionSupport._$a(t)
            },
            _mn: function(t) {
                var i = this._52[t];
                return i ? i : vj[t]
            }
        },
        Z(Ar[ah], {
            defaultCursor: {
                get: function() {
                    return this[dL] ? this.currentInteractionMode[Du] : void 0
                }
            },
            currentMode: {
                get: function() {
                    return this._n0urrentMode
                },
                set: function(t) {
                    this._n0urrentMode != t && (this._n0urrentMode, this._interactionSupport || (this._interactionSupport = new mM(this._k2)), this._n0urrentMode = t, this[dL] = this._mn(this._n0urrentMode), this._k2.cursor = this[Du], this._interactionSupport._7f(this.currentInteractionMode ? this.currentInteractionMode[lL](this._k2) : []))
                }
            }
        });
    var vj = {};
    CD[vL] = function(t, i, e) {
            var n = new or(i, e);
            vj[t] = n
        },
        MM.INTERACTION_MODE_VIEW = bL,
        MM.INTERACTION_MODE_DEFAULT = Mu,
        MM[gL] = yL,
        MM.INTERACTION_MODE_ZOOMIN = mL,
        MM[pL] = EL,
        MM.INTERACTION_MODE_CREATE_SIMPLE_EDGE = xL,
        MM[TL] = wL,
        MM.INTERACTION_MODE_CREATE_SHAPE = OL,
        MM.INTERACTION_MODE_CREATE_LINE = IL,
        CD[vL](MM.INTERACTION_MODE_VIEW, [oj, hj, cj, sj, ej, uj]),
        CD.registerInteractions(MM[AL], [nj, lr, oj, hj, cj, sj, uj]),
        CD[vL](MM[TL], [nj, ur, oj, hj, cj, sj, uj]),
        CD.registerInteractions(MM.INTERACTION_MODE_CREATE_SHAPE, [nj, cr, oj, hj, cj, sj, uj]),
        CD[vL](MM.INTERACTION_MODE_CREATE_LINE, [dr, oj, hj, cj, sj, uj]),
        CD[vL](MM[Ju], [nj, xr, Er, oj, rj, hj, cj, sj, ej, uj]),
        CD.registerInteractions(MM[gL], [nj, xr, Er, oj, rj, aj, hj, cj, sj, ej, uj]),
        CD.registerInteractions(MM[SL], [cj, sj, dj], pM),
        CD.registerInteractions(MM[pL], [cj, sj, lj], EM),
        DM[CL] = hj,
        DM.SelectionInteraction = oj,
        DM.MoveInteraction = rj,
        DM.WheelZoomInteraction = cj,
        DM[kL] = ej,
        DM.ExportInteraction = sj,
        DM[LL] = uj,
        DM.RectangleSelectionInteraction = aj,
        DM.PointsInteraction = Er;
    var bj = function(t) {
        this.graph = t
    };
    DM[RL] = bj,
        bj[ah] = {
            getNodeBounds: function(t) {
                return this[Jf].getUIBounds(t)
            },
            isLayoutable: function(t) {
                return t.layoutable !== !1
            },
            getLayoutResult: function() {},
            updateLocations: function(t, i, e, n, s) {
                if (i === !0) {
                    if (this.animate || (this[DL] = new Yj), e && (this[DL].duration = e), n && (this.animate.animationType = n), this.animate.locations = t, s) {
                        var r = s,
                            h = this;
                        s = function() {
                            r[Br](h, t)
                        }
                    }
                    return void this.animate[oC](s)
                }
                for (var a in t) {
                    var o = t[a],
                        _ = o.node;
                    _[Ak](o.x, o.y)
                }
                s && s[Br](this, t)
            },
            _eo: function(t) {
                var i,
                    e,
                    n,
                    s = null;
                t && (i = t[ML], s = t[pf], e = t.duration, n = t[tC]);
                var r = this[PL](t);
                return r ? (this.updateLocations(r, i, e, n, s), r) : !1
            },
            doLayout: function(t, i) {
                return this.graph && i !== !0 ? void this[Jf][FA](function() {
                        this._eo(t)
                    },
                    this) : this._eo(t)
            }
        };
    var gj = 11,
        yj = 12,
        mj = 13,
        pj = 21,
        Ej = 22,
        xj = 23;
    MM.DIRECTION_RIGHT = gj,
        MM.DIRECTION_LEFT = yj,
        MM[NL] = mj,
        MM.DIRECTION_BOTTOM = pj,
        MM[jL] = Ej,
        MM.DIRECTION_MIDDLE = xj;
    var Tj = BL,
        wj = zL,
        Oj = $L,
        Ij = GL;
    MM[FL] = Tj,
        MM.LAYOUT_TYPE_EVEN_HORIZONTAL = Oj,
        MM.LAYOUT_TYPE_EVEN_VERTICAL = Ij,
        MM[YL] = wj,
        DM.isHorizontalDirection = Sr;
    var Aj = function(t) {
        this[Jf] = t
    };
    Aj.prototype = {
            hGap: 50,
            vGap: 50,
            parentChildrenDirection: pj,
            layoutType: Tj,
            defaultSize: {
                width: 50,
                height: 60
            },
            getNodeSize: function(t) {
                if (this.graph._7u._9z) {
                    var i = this[Jf][Hh](t);
                    if (i) return i._f9
                }
                return t.image && t[vb][qa] ? {
                    width: t[vb].bounds[xa],
                    height: t[vb][qa][Fa]
                } : this[qL]
            },
            _n04: function(t, i) {
                if (this.isLayoutable(t)) {
                    var e = this[HL](t),
                        n = t.id,
                        s = (t.parentChildrenDirection, i ? this._9r[i.id] : this._my7);
                    this._9r[n] = new Sj(t.hGap || this.hGap, t[UL] || this.vGap, t.layoutType || this.layoutType, t[WL], s, t, e[xa], e[Fa])
                }
            },
            _9r: null,
            _my7: null,
            _kn: function() {
                this._9r = null,
                    this._my7 = null
            },
            getLayoutResult: function(t) {
                var i,
                    e,
                    n,
                    s,
                    r = this.graph;
                t instanceof Object && (i = t.x, e = t.y, r = t.root || this[Jf], n = t[qa], s = t.undirected),
                    this._9r = {},
                    this._my7 = new Sj,
                    this._my7._md(this.hGap, this.vGap, this.parentChildrenDirection, this[XL]);
                var h = {},
                    a = Uj(r, this._n04, this, !1, s);
                return a && (this._my7._eo(i || 0, e || 0, h), n && n.set(this._my7.x, this._my7.y, this._my7.width, this._my7.height)),
                    this._kn(),
                    h
            },
            doLayout: function(t, i) {
                if (I(t)) {
                    var e = t,
                        n = 0;
                    I(i) && (n = i),
                        t = {
                            x: e,
                            y: n
                        },
                        i = !0
                }
                return B(this, Aj, VL, [t, i])
            }
        },
        N(Aj, bj);
    var Sj = function(t, i, e, n, s, r, h, a) {
        this._lv = t || 0,
            this._lu = i || 0,
            this[XL] = e,
            this.parentChildrenDirection = n,
            this[KL] = s,
            s && s._g5(this),
            this[ZL] = r,
            this._dv = h,
            this._n0v = a
    };
    Sj.prototype = {
            _md: function(t, i, e, n) {
                this._lv = t,
                    this._lu = i,
                    this[WL] = e,
                    this[XL] = n
            },
            _7z: function() {
                this._ez = []
            },
            _lv: 0,
            _lu: 0,
            _ez: null,
            _dv: 0,
            _n0v: 0,
            layoutType: null,
            parentChildrenDirection: null,
            _g5: function(t) {
                this._ez || (this._ez = []),
                    this._ez[Yr](t)
            },
            _n0x: function(t, i, e, n) {
                var s = new BD;
                return e(this._ez,
                        function(e) {
                            e._3t(t, i),
                                s.add(e),
                                n ? t += e[xa] + this._lv : i += e[Fa] + this._lu
                        },
                        this),
                    s
            },
            _7o: function(t, i, e, n, s) {
                var r,
                    h = n ? this._lv : this._lu,
                    a = n ? this._lu : this._lv,
                    o = n ? "width" : Fa,
                    _ = n ? "height" : xa,
                    f = n ? "_dv" : JL,
                    u = n ? "_n0v" : QL,
                    c = n ? "hostDX" : tR,
                    d = n ? "hostDY" : iR,
                    v = new BD,
                    b = 0,
                    g = 0,
                    y = [],
                    m = 0,
                    p = 0;
                e(this._ez,
                        function(e) {
                            var s = p >= g;
                            e._inheritedParentChildrenDirection = s ? n ? yj : Ej : n ? gj : pj,
                                e._3t(t, i),
                                s ? (y.push(e), b = Math[ja](b, e[o]), g += e[_] + a) : (r || (r = []), r[Yr](e), m = Math.max(m, e[o]), p += e[_] + a)
                        },
                        this),
                    g -= a,
                    p -= a;
                var E = Math.max(g, p),
                    x = h,
                    T = 0;
                this[ZL] && (s && (x += this[f] + h, E > this[u] ? this[d] = (E - this[u]) / 2 : T = (this[u] - E) / 2), this[c] = b + x / 2 - this[f] / 2);
                var w = 0,
                    O = T;
                return l(y,
                        function(t) {
                            n ? t.offset(b - t[o], O) : t[jv](O, b - t[o]),
                                O += a + t[_],
                                v[Lh](t)
                        },
                        this),
                    r ? (O = T, w = b + x, l(r,
                        function(t) {
                            n ? t.offset(w, O) : t[jv](O, w),
                                O += a + t[_],
                                v.add(t)
                        },
                        this), v) : v
            },
            offset: function(t, i) {
                this.x += t,
                    this.y += i,
                    this.nodeX += t,
                    this.nodeY += i,
                    this._69(t, i)
            },
            _mxb: function(t, i) {
                return 2 * this.cx - t - i - t
            },
            _mxc: function(t, i) {
                return 2 * this.cy - t - i - t
            },
            _lj: function(t) {
                if (this._ez && 0 != this._ez.length) {
                    if (t) return this[ZL] && (this.nodeX += this._mxb(this.nodeX, this._dv)),
                        void l(this._ez,
                            function(t) {
                                t.offset(this._mxb(t.x, t.width), 0)
                            },
                            this);
                    this.node && (this.nodeY += this._mxc(this.nodeY, this._n0v)),
                        l(this._ez,
                            function(t) {
                                t[jv](0, this._mxc(t.y, t.height))
                            },
                            this)
                }
            },
            _69: function(t, i) {
                this._ez && l(this._ez,
                    function(e) {
                        e[jv](t, i)
                    },
                    this)
            },
            _3t: function(t, i) {
                return this.x = t || 0,
                    this.y = i || 0,
                    this._ez && 0 != this._ez.length ? void this._24(this.x, this.y, this.layoutType) : void(this[ZL] && (this[xa] = this._dv, this.height = this._n0v, this.nodeX = this.x, this[eR] = this.y))
            },
            _6c: function(t) {
                this.node && (t[this.node.id] = {
                        node: this.node,
                        x: this[nR] + this._dv / 2,
                        y: this.nodeY + this._n0v / 2,
                        left: this.nodeX,
                        top: this.nodeY,
                        width: this._dv,
                        height: this._n0v
                    }),
                    this._ez && l(this._ez,
                        function(i) {
                            i._6c(t)
                        },
                        this)
            },
            _eo: function(t, i, e) {
                this._3t(t, i),
                    this._6c(e)
            },
            _24: function(t, i, n) {
                var s,
                    r = t,
                    h = i;
                !this[WL] && this[KL] && (this[WL] = this._inheritedParentChildrenDirection || this.parentBounds[WL]);
                var a = this[WL],
                    o = Sr(a);
                if (this.node) {
                    s = a == mj || a == xj;
                    var _ = Cr(a);
                    s || (o ? t += this._dv + this._lv : i += this._n0v + this._lu)
                }
                var f,
                    u = this.node && this[ZL][sR] ? b : l;
                if (n == wj) f = this._7o(t, i, u, !o, s);
                else {
                    var c;
                    c = n == Tj ? !o : n == Oj,
                        f = this._n0x(t, i, u, c, s)
                }
                var d = 0,
                    v = 0;
                f && !f.isEmpty() && (d = f[xa], v = f.height, this.add(f)),
                    this[ZL] && (this.nodeX = r, this[eR] = h, this.hostDX !== e || this[tR] !== e ? (this.nodeX += this.hostDX || 0, this.nodeY += this[tR] || 0) : o ? this[eR] += v / 2 - this._n0v / 2 : this.nodeX += d / 2 - this._dv / 2, this.addRect(this.nodeX, this[eR], this._dv, this._n0v), _ && this._lj(o))
            },
            node: null,
            uiBounds: null
        },
        N(Sj, BD),
        Lr.prototype = {
            layoutDatas: null,
            isMovable: function(t) {
                return !this.currentMovingNodes[t.id]
            },
            _6z: !1,
            _3x: function() {
                this._6z = !0,
                    this.graph._1n[ld](this._8x, this),
                    this.graph._1m.addListener(this._2f, this)
            },
            _1z: function() {
                this._6z = !1,
                    this.graph._1n[mS](this._8x, this),
                    this[Jf]._1m[mS](this._2f, this)
            },
            invalidateFlag: !0,
            invalidateLayoutDatas: function() {
                this[TS] = !0
            },
            resetLayoutDatas: function() {
                return this.invalidateFlag = !1,
                    this[rR] = kr[Br](this)
            },
            _2f: function(t) {
                Ir[tk] == t[Ku] ? (this[sc] = {},
                    t.datas[d_](function(t) {
                            this[sc][t.id] = t
                        },
                        this)) : Ir[Wk] == t[Ku] && (this.currentMovingNodes = {})
            },
            _8x: function() {
                this.invalidateLayoutDatas()
            },
            isRunning: function() {
                return this[hR] && this[hR]._d3()
            },
            getLayoutResult: function() {
                this.stop(),
                    this[aR]();
                for (var t = this.getMaxIterations(this.layoutDatas.nodeCount || 0, this[rR].edgeCount || 0), i = 0; t > i && this.step(!1) !== !1; i++);
                var e = this[rR].nodes;
                return this.onstop(),
                    e
            },
            _lw: function() {
                return !1
            },
            step: function(t) {
                if (t === !1) return this._lw(this.timeStep);
                (this[TS] || !this.layoutDatas) && this[aR]();
                var i = this._lw(t),
                    e = this[rR][oR];
                for (var n in e) {
                    var s = e[n],
                        r = s[ZL];
                    this[JC](r) ? r.setLocation(s.x, s.y) : (s.x = r.x, s.y = r.y, s.vx = 0, s.vy = 0)
                }
                return i
            },
            onstop: function() {
                delete this[rR]
            },
            start: function(t) {
                if (this.isRunning()) return !1;
                this._6z || this._3x(),
                    this._ijr || (this._ijr = F(this,
                        function(t) {
                            return this[ak](t)
                        })),
                    this[_R](),
                    this.timer = new kM(this._ijr);
                var i = this;
                return this.timer._l2(function() {
                    i[fR](),
                        t && t()
                }), !0
            },
            stop: function() {
                this.timer && (this[hR]._ll(), this[fR]())
            },
            getMaxIterations: function(t) {
                return Math[Ga](1e3, 3 * t + 10)
            },
            minEnergyFunction: function(t, i) {
                return 10 + Math.pow(t + i, 1.4)
            },
            resetGraph: function() {
                this._6z || this._3x(),
                    this[aR]()
            },
            destroy: function() {
                this[uR](),
                    this._1z()
            }
        },
        N(Lr, bj);
    var Cj = function(t, i, e, n) {
        this[Jf] = t,
            I(i) && (this[Uc] = i),
            I(e) && (this[cR] = e),
            I(n) && (this.startAngle = n)
    };
    DM[dR] = Cj;
    var kj = lR,
        Lj = vR,
        Rj = bR,
        Dj = gR;
    MM[yR] = kj,
        MM[mR] = Lj,
        MM[pR] = Rj,
        MM.RADIUS_MODE_VARIABLE = Dj,
        Cj[ah] = {
            angleSpacing: kj,
            radiusMode: Dj,
            gap: 4,
            radius: 50,
            startAngle: 0,
            _9r: null,
            _my7: null,
            _kn: function() {
                this._9r = null,
                    this._my7 = null
            },
            getLayoutResult: function(t) {
                var i,
                    e = 0,
                    n = 0,
                    s = this.graph;
                t instanceof Object && (e = t.cx || 0, n = t.cy || 0, s = t.root || this.graph, i = t[qa]),
                    this._9r = {},
                    this._my7 = new Nj(this);
                var r = {},
                    h = Wj(s, this._n04, this);
                return h && (this._my7._ez && 1 == this._my7._ez.length && (this._my7 = this._my7._ez[0]), this._my7._e4(!0), this._my7._5w(e, n, this.startAngle, r, i)),
                    this._kn(),
                    r
            },
            _n04: function(t, i) {
                if (this.isLayoutable(t)) {
                    var e = i ? this._9r[i.id] : this._my7;
                    this._9r[t.id] = new Nj(this, t, e)
                }
            },
            defaultSize: 40,
            getRadius: function() {
                return this.radius
            },
            getNodeSize: function(t) {
                if (this.graph._7u._9z) {
                    var i = this[Jf].getUI(t);
                    if (i) return (i._f9.width + i._f9.height) / 2
                }
                return this.defaultSize
            },
            getGap: function() {
                return this[cR]
            },
            _36: function(t, i, e) {
                return this.getNodeSize(t, i, e) + this.getGap(t, i, e)
            }
        };
    var Mj = function(t) {
            var i,
                e = this._ez[jr],
                n = 0,
                s = 0;
            if (l(this._ez,
                    function(t) {
                        var e = t._e4();
                        1 > e && (e = 1),
                            s += e,
                            e > n && (n = e, i = t)
                    },
                    this), e > 1) {
                var r = 0,
                    h = {},
                    a = s / e / 3;
                s = 0,
                    l(this._ez,
                        function(t) {
                            var i = t._m3;
                            a > i && (i = a),
                                h[t.id] = i,
                                s += i
                        },
                        this);
                var o = jj / s;
                l(this._ez,
                    function(i, e) {
                        var n = h[i.id],
                            s = n * o;
                        0 === e && (r = t ? -s / 2 : -s),
                            i._kr = r + s / 2,
                            i._kj = s,
                            r += s
                    },
                    this)
            }
            return [n, i._kj]
        },
        Pj = function(t) {
            var i = this._82,
                e = 2 * Math.PI / i,
                n = 0,
                s = t ? 0 : i > 1 ? -e / 2 : 0;
            return l(this._ez,
                function(t) {
                    t._kr = s % jj,
                        s += e,
                        t._kj = e;
                    var i = t._e4();
                    i > n && (n = i)
                },
                this), [n, e]
        },
        Nj = function(t, i, e) {
            this[ER] = t,
                i && (this._m2 = i, this.id = i.id),
                e && (e._g5(this), e._m4 = !1, this._ks = e._ks + 1)
        },
        jj = 2 * Math.PI;
    Nj[ah] = {
            _kj: 0,
            _kr: 0,
            _jd: 0,
            _e1: 0,
            _n0j: 0,
            _ks: 0,
            _m4: !0,
            _m3: 0,
            _h2: 0,
            _ez: null,
            _m2: null,
            _g5: function(t) {
                this._ez || (this._ez = []),
                    this._ez.push(t),
                    t.parent = this
            },
            _h0: function(t) {
                if (this._kr = (this._kr + t) % jj, this._ez) {
                    var i = this._ez[jr];
                    if (1 == i) return void this._ez[0]._h0(this._kr);
                    t = this._kr + Math.PI,
                        l(this._ez,
                            function(i) {
                                i._h0(t)
                            },
                            this)
                }
            },
            _82: 0,
            _74: function(t) {
                return this._m2 && (this._h2 = this.layouter._36(this._m2, this._ks, this._m4) / 2),
                    this._ez ? (this._h2, this._82 = this._ez[jr], this._82 <= 2 || this.layouter.angleSpacing == Lj ? Pj[Br](this, t) : Mj[Br](this, t)) : null
            },
            _e4: function(t) {
                var i = this._74(t);
                if (!i) return this._m3 = this._h2;
                var e = i[0],
                    n = i[1],
                    s = this.layouter.getRadius(this._m2, this._ks);
                if (s < this._h2 && (s = this._h2), this._e1 = s, this._h2 + e > s && (s = this._h2 + e), e && this._82 > 1 && n < Math.PI) {
                    var r = e / Math.sin(n / 2);
                    r > s && (s = r)
                }
                return this._jd = s,
                    this._m3 = s + e,
                    this._m2 && this._ez && this[ER].radiusMode == Dj && l(this._ez,
                        function(t) {
                            var i = t._m3;
                            1 == t._82 && (i /= 2);
                            var e = this._h2 + i,
                                n = t._kj;
                            if (n && n < Math.PI) {
                                var s = Math[pa](n / 2),
                                    r = i / s;
                                r > i && (i = r)
                            }
                            e > i && (i = e),
                                t._n0j = i
                        },
                        this), (!this._m2 || t) && this._h0(0),
                    this._m3
            },
            _5w: function(t, i, e, n, s) {
                if (this._m2 && (n[this._m2.id] = {
                            x: t,
                            y: i,
                            node: this._m2
                        },
                        s && s.addRect(t - this._h2 / 2, i - this._h2 / 2, this._h2, this._h2)), this._ez) {
                    if (!this._m2 && 1 == this._ez[jr]) return void this._ez[0]._5w(t, i, e, n, s);
                    e = e || 0;
                    var r = this._jd,
                        h = this._e1;
                    l(this._ez,
                        function(a) {
                            var o = r;
                            a._n0j && (o = Math[ja](h, a._n0j));
                            var _ = a._kr + e,
                                f = t + o * Math.cos(_),
                                u = i + o * Math.sin(_);
                            a._5w(f, u, e, n, s)
                        },
                        this)
                }
            }
        },
        N(Cj, bj);
    var Bj = function() {
        j(this, Bj, arguments)
    };
    N(Bj, Rr);
    var zj = function(t, i) {
        this[xR] = t,
            this[TR] = i,
            t == i ? (this[tc] = !0, this._k3 = t._jz) : this._k3 = new LD,
            this._8g = [],
            this._gn = CD[wR]
    };
    CD.EDGE_BUNDLE_EXPANDED = !0,
        zj[ah] = {
            node1: null,
            node2: null,
            _k3: null,
            _gn: CD.EDGE_BUNDLE_EXPANDED,
            _8g: null,
            _gi: null,
            agentEdge: null,
            _myt: function(t, i, e) {
                this._k3.forEach(function(n) {
                    return e && n[Ym] != e && n.fromAgent != e ? void 0 : t.call(i, n)
                })
            },
            _5z: 0,
            _5b: 0,
            _i0: function(t, i) {
                return this._k3.add(t) === !1 ? !1 : (i == this.node1 ? this._5z++ : this._5b++, this._9z ? void this._17(t) : void(this._9z = !0))
            },
            _n0s: function(t, i) {
                return this._k3.remove(t) === !1 ? !1 : (i == this.node1 ? this._5z-- : this._5b--, this._mxkBindableFlag = !0, this._6a = !0, void this._k3[d_](function(t) {
                        t._edgeBundleInvalidateFlag = !0,
                            t.__4v = !0
                    },
                    this))
            },
            _17: function(t) {
                this._mxkBindableFlag = !0,
                    this._6a = !0,
                    t._edgeBundleInvalidateFlag = !0,
                    t.__4v = !0
            },
            _mxk: function() {
                this._6a || (this._6a = !0, this._k3[d_](function(t) {
                    t._edgeBundleInvalidateFlag = !0
                }))
            },
            isEmpty: function() {
                return this._k3[xc]()
            },
            isPositiveOrder: function(t) {
                return this[xR] == t[Ym] || this[xR] == t[W_]
            },
            canBind: function(t) {
                return t && this._6a && this._fa(t),
                    this._k3.length > 1 && this._8g.length > 1
            },
            _hu: function(t) {
                return this._8g.indexOf(t)
            },
            getYOffset: function(t) {
                return this._gi[t.id]
            },
            _4z: function(t) {
                if (!this[CI]()) return void(this._gi = {});
                var i = {},
                    e = this._8g.length;
                if (!(2 > e)) {
                    var n = 0,
                        s = this._8g[0];
                    i[s.id] = 0;
                    for (var r = 1; e > r; r++) {
                        s = this._8g[r];
                        var h = t.getStyle(s, SN[QI]) || UN[SN[QI]];
                        n += h,
                            i[s.id] = n
                    }
                    if (!this[tc])
                        for (var a = n / 2, r = 0; e > r; r++) s = this._8g[r],
                            i[s.id] -= a;
                    this._gi = i
                }
            },
            _myj: function(t) {
                return this._gn == t ? !1 : (this._gn = t, this._mxk(), !0)
            },
            reverseExpanded: function() {
                return this._myj(!this._gn)
            },
            _1k: function() {
                this._mxkBindableFlag = !1,
                    this._8g[jr] = 0;
                var t;
                this._k3[d_](function(i) {
                            if (i.isBundleEnabled()) {
                                if (!this[LI](i)) return t || (t = []),
                                    void t.push(i);
                                this._8g.push(i)
                            }
                        },
                        this),
                    t && (this._8g = t.concat(this._8g))
            },
            _e5: function(t) {
                return t == this[lS] || !this.canBind() || this._gn
            },
            _fa: function(t) {
                this._6a = !1,
                    this._k3.forEach(function(t) {
                        t._edgeBundleInvalidateFlag = !1
                    }),
                    this._mxkBindableFlag && this._1k();
                var i = this._gn,
                    e = this[CI](),
                    n = !e || i;
                l(this._8g,
                        function(t) {
                            t._$w = !0,
                                t._hkInBundle = n,
                                n && (t.__4v = !0)
                        },
                        this),
                    n ? this._9o(null, t) : (this._9o(this._8g[0], t), this[lS]._hkInBundle = !0, this.agentEdge.__4v = !0),
                    n && this._4z(t)
            },
            _9o: function(t, i) {
                if (t != this.agentEdge) {
                    var e = this.agentEdge;
                    return this[lS] = t,
                        i && i._4s(new KD(this, lS, t, e)), !0
                }
            }
        },
        Z(zj[ah], {
            bindableEdges: {
                get: function() {
                    return this._8g
                }
            },
            edges: {
                get: function() {
                    return this._k3._im
                }
            },
            length: {
                get: function() {
                    return this._k3 ? this._k3.length : 1
                }
            },
            expanded: {
                get: function() {
                    return this._gn
                },
                set: function(t) {
                    return this._gn == t ? !1 : (this._gn = t, void this._mxk())
                }
            }
        });
    var $j = function() {
            function t(t, i) {
                this[ZL] = t,
                    this.body = i
            }

            function i() {
                this[OR] = [],
                    this.popIdx = 0
            }
            var e = -1e6,
                n = .8;
            i[ah] = {
                isEmpty: function() {
                    return 0 === this.popIdx
                },
                push: function(i, e) {
                    var n = this[OR][this[IR]];
                    n ? (n.node = i, n.body = e) : this.stack[this[IR]] = new t(i, e),
                        ++this[IR]
                },
                pop: function() {
                    return this[IR] > 0 ? this.stack[--this[IR]] : void 0
                },
                reset: function() {
                    this[IR] = 0
                }
            };
            var s = [],
                r = new i,
                h = function() {
                    this[BO] = null,
                        this.quads = [],
                        this.mass = 0,
                        this.massX = 0,
                        this.massY = 0,
                        this.left = 0,
                        this[oo] = 0,
                        this[Ah] = 0,
                        this[zf] = 0,
                        this.isInternal = !1
                },
                a = [],
                o = 0,
                _ = function() {
                    var t;
                    return a[o] ? (t = a[o], t.quads[0] = null, t[AR][1] = null, t.quads[2] = null, t.quads[3] = null, t.body = null, t.mass = t[SR] = t.massY = 0, t[_o] = t[zf] = t[oo] = t[Ah] = 0, t.isInternal = !1) : (t = new h, a[o] = t),
                        ++o,
                        t
                },
                f = _(),
                u = function(t, i) {
                    var e = Math[Sh](t.x - i.x),
                        n = Math[Sh](t.y - i.y);
                    return 1e-8 > e && 1e-8 > n
                },
                c = function(t) {
                    for (r.reset(), r[Yr](f, t); !r.isEmpty();) {
                        var i = r[CR](),
                            e = i[ZL],
                            n = i.body;
                        if (e.isInternal) {
                            var s = n.x,
                                h = n.y;
                            e.mass = e[kR] + n.mass,
                                e[SR] = e.massX + n.mass * s,
                                e.massY = e.massY + n.mass * h;
                            var a = 0,
                                o = e[_o],
                                c = (e[zf] + o) / 2,
                                d = e[oo],
                                l = (e.bottom + d) / 2;
                            if (s > c) {
                                a += 1;
                                var v = o;
                                o = c,
                                    c += c - v
                            }
                            if (h > l) {
                                a += 2;
                                var b = d;
                                d = l,
                                    l += l - b
                            }
                            var g = e[AR][a];
                            g || (g = _(), g.left = o, g.top = d, g.right = c, g.bottom = l, e[AR][a] = g),
                                r.push(g, n)
                        } else if (e.body) {
                            var y = e[BO];
                            if (e[BO] = null, e[LR] = !0, u(y, n)) {
                                if (e[zf] - e[_o] < 1e-8) return;
                                do {
                                    var m = Math.random(),
                                        p = (e[zf] - e[_o]) * m,
                                        E = (e[Ah] - e[oo]) * m;
                                    y.x = e[_o] + p,
                                        y.y = e.top + E
                                }
                                while (u(y, n))
                            }
                            r.push(e, y),
                                r.push(e, n)
                        } else e[BO] = n
                    }
                },
                d = function(t) {
                    var i,
                        r,
                        h,
                        a,
                        o = s,
                        _ = 1,
                        u = 0,
                        c = 1;
                    for (o[0] = f; _;) {
                        var d = o[u],
                            l = d[BO];
                        _ -= 1,
                            u += 1,
                            l && l !== t ? (r = l.x - t.x, h = l.y - t.y, a = Math.sqrt(r * r + h * h), 0 === a && (r = (Math.random() - .5) / 50, h = (Math[rh]() - .5) / 50, a = Math.sqrt(r * r + h * h)), i = e * l.mass * t[kR] / (a * a), -1e3 > i && (i = -1e3), i /= a, t.fx = t.fx + i * r, t.fy = t.fy + i * h) : (r = d[SR] / d.mass - t.x, h = d[RR] / d.mass - t.y, a = Math.sqrt(r * r + h * h), 0 === a && (r = (Math.random() - .5) / 50, h = (Math.random() - .5) / 50, a = Math.sqrt(r * r + h * h)), (d.right - d[_o]) / a < n ? (i = e * d[kR] * t[kR] / (a * a), -1e3 > i && (i = -1e3), i /= a, t.fx = t.fx + i * r, t.fy = t.fy + i * h) : (d.quads[0] && (o[c] = d[AR][0], _ += 1, c += 1), d.quads[1] && (o[c] = d[AR][1], _ += 1, c += 1), d.quads[2] && (o[c] = d[AR][2], _ += 1, c += 1), d.quads[3] && (o[c] = d[AR][3], _ += 1, c += 1)))
                    }
                },
                l = function(t, i) {
                    e = i;
                    var n,
                        s = Number[Lc],
                        r = Number[Lc],
                        h = Number[DR],
                        a = Number[DR],
                        u = t,
                        d = u[jr];
                    for (n = d; n--;) {
                        var l = u[n].x,
                            v = u[n].y;
                        s > l && (s = l),
                            l > h && (h = l),
                            r > v && (r = v),
                            v > a && (a = v)
                    }
                    var b = h - s,
                        g = a - r;
                    for (b > g ? a = r + b : h = s + g, o = 0, f = _(), f[_o] = s, f.right = h, f.top = r, f[Ah] = a, n = d; n--;) c(u[n], f)
                };
            return {
                init: l,
                update: d
            }
        },
        Gj = function(t) {
            t.fx -= t.x * this.attractive,
                t.fy -= t.y * this.attractive
        },
        Fj = function(t) {
            if (0 != t.k) {
                var i = this._n0b,
                    e = t[Um],
                    n = t.to,
                    s = n.x - e.x,
                    r = n.y - e.y,
                    h = s * s + r * r,
                    a = Math[$a](h) || .1,
                    o = (a - i) * t.k * this[MR];
                o /= a;
                var _ = o * s,
                    f = o * r;
                n.fx -= _,
                    n.fy -= f,
                    e.fx += _,
                    e.fy += f
            }
        };
    Rr[ah] = {
            appendNodeInfo: function(t, i) {
                i.mass = t.layoutMass || 1,
                    i.fx = 0,
                    i.fy = 0,
                    i.vx = 0,
                    i.vy = 0
            },
            appendEdgeInfo: function(t, i) {
                i.k = t[PR] || 1
            },
            setMass: function(t, i) {
                t[NR] = i,
                    this.layoutDatas && this[rR].nodes && (t = this[rR].nodes[t.id], t && (t.mass = i))
            },
            setElasticity: function(t, i) {
                t[PR] = i,
                    this[rR] && this.layoutDatas[jR] && (t = this.layoutDatas.edges[t.id], t && (t.k = i))
            },
            _n0b: 50,
            _hh: .5,
            timeStep: .15,
            repulsion: 50,
            attractive: .1,
            elastic: 3,
            _m5: 1e3,
            _j4: function(t) {
                return this._m5 + .3 * (t - this._m5)
            },
            _lw: function(t, i) {
                var e = (Date[va](), this.layoutDatas.nodes);
                for (var n in e) {
                    var s = e[n];
                    i && (s.x += Math.random() - .5, s.y += Math.random() - .5),
                        Gj[Br](this, s)
                }
                var r = this.layoutDatas[BR];
                if (r)
                    for (var n in r) {
                        var h = r[n],
                            a = h.children,
                            o = 0,
                            _ = 0;
                        a.forEach(function(t) {
                                o += t.x,
                                    _ += t.y
                            }),
                            o /= a[jr],
                            _ /= a.length;
                        var f = 10 * this.attractive;
                        a[d_](function(t) {
                            t.fx -= (t.x - o) * f,
                                t.fy -= (t.y - _) * f
                        })
                    }
                var u = this._nbodyForce;
                u || (u = this._nbodyForce = $j()),
                    u[wu](this.layoutDatas.nodesArray, -this.repulsion * this.repulsion * this.repulsion);
                for (var n in e) u[zR](e[n]);
                if (this.elastic) {
                    var c = this[rR][jR];
                    for (var n in c) Fj.call(this, c[n])
                }
                return this._m6(t)
            },
            _m6: function(t) {
                var i = this.layoutDatas[$R],
                    e = (this.layoutDatas[GR], this[rR][oR]),
                    t = this[FR],
                    n = 0,
                    s = this._hh;
                for (var r in e) {
                    var h = e[r],
                        a = h.fx / h[kR],
                        o = h.fy / h.mass,
                        _ = h.vx += a * t,
                        f = h.vy += o * t;
                    h.x += _ * t,
                        h.y += f * t,
                        i > n && (n += 2 * (_ * _ + f * f)),
                        h.fx = 0,
                        h.fy = 0,
                        h.vx *= s,
                        h.vy *= s
                }
                return this[rR][GR] = n,
                    n >= i
            }
        },
        N(Rr, Lr),
        DM.SpringLayouter = Rr;
    var Yj = function(t) {
        this.locations = t
    };
    Yj.prototype = {
            oldLocations: null,
            _eb: null,
            duration: 700,
            animationType: CM.easeOutStrong,
            _7a: function(t) {
                if (this._eb = t, this.oldLocations = {},
                    t)
                    for (var i in t) {
                        var e = t[i],
                            n = e[ZL];
                        this.oldLocations[i] = {
                            x: n.x,
                            y: n.y
                        }
                    }
            },
            setLocation: function(t, i, e) {
                t[Ak](i, e)
            },
            forEach: function(t, i) {
                for (var e in this.locations) {
                    var n = this.oldLocations[e],
                        s = this.locations[e];
                    t.call(i, n, s)
                }
            },
            _je: function(t) {
                this.forEach(function(i, e) {
                        var n = e.node,
                            s = i.x + (e.x - i.x) * t,
                            r = i.y + (e.y - i.y) * t;
                        this.setLocation(n, s, r)
                    },
                    this)
            },
            stop: function() {
                this._mynimate && this._mynimate._ll()
            },
            start: function(t) {
                this._mynimate ? (this._mynimate._ll(), this._mynimate._ie = this[YR], this._mynimate._dhType = this[tC], this._mynimate._onfinish = this._onfinish) : this._mynimate = new LM(this._je, this, this.duration, this.animationType),
                    this._mynimate._l2(t)
            }
        },
        Z(Yj[ah], {
            locations: {
                get: function() {
                    return this._eb
                },
                set: function(t) {
                    this._eb != t && this._7a(t)
                }
            }
        });
    var qj = function(t) {
            var i = new LD;
            return t.forEach(function(t) {
                    t instanceof xN && (t[qR]() || i.add(t._dc || t))
                }),
                i
        },
        Hj = function(t, i, e, n, s, r) {
            if (i instanceof hM) return t(i, e, n, s, r),
                i;
            if (i instanceof XN) {
                var h = new LD;
                i._k2Model.forEach(function(t) {
                        return i.isVisible(t) ? t._hd() && t._gn && t[Nr]() ? void(t.$location && (t.$location.invalidateFlag = !1)) : void h.add(t) : void 0
                    }),
                    i = h
            }
            var i = qj(i);
            return l(i,
                    function(i) {
                        t(i, e, n, s, r)
                    }),
                i
        },
        Uj = function(t, i, e, n, s) {
            return Hj(Xj, t, i, e, n, s)
        },
        Wj = function(t, i, e, n, s) {
            return Hj(Vj, t, i, e, n, s)
        };
    Zn[ah][HR] = function(t, i, e, n) {
            Uj(this, t, i, e, n)
        },
        Zn[ah][UR] = function(t, i, e, n) {
            Wj(this, t, i, e, n)
        };
    var Xj = function(t, i, e, n, s) {
            function r(t, i, e, n, s, h, a, o) {
                t._marker = h,
                    n || i[Br](e, t, o, a),
                    Dr(t,
                        function(o) {
                            r(o, i, e, n, s, h, a + 1, t)
                        },
                        o, s, h),
                    n && i.call(e, t, o, a)
            }
            r(t, i, e, n, s, {},
                0)
        },
        Vj = function(t, i, e, n, s) {
            function r(t, i, e, n, s, h, a) {
                var o,
                    _ = t.length;
                t[d_](function(t, r) {
                        var f = t.v;
                        f._marker = h,
                            n || i[Br](e, f, t._from, a, r, _),
                            Dr(f,
                                function(t) {
                                    o || (o = []),
                                        t._marker = h,
                                        o.push({
                                            v: t,
                                            _from: f
                                        })
                                },
                                f, s, h)
                    }),
                    o && r(o, i, e, n, s, h, a + 1),
                    n && t.forEach(function(t, n) {
                        i[Br](e, t.v, t._from, a, n, _)
                    })
            }
            r([{
                    v: t
                }], i, e, n, s, {},
                0)
        };
    DM.toColor = V,
        DM.log = ti,
        DM.error = ei,
        DM.trace = ii,
        DM[WR] = lD,
        DM.isOpera = dD,
        DM[XR] = bD,
        DM.isGecko = gD,
        DM[VR] = yD,
        DM.isSafari = pD,
        DM.isChrome = mD,
        DM.isMac = ED,
        DM[KR] = UN,
        DM[ZR] = CD,
        DM[rS] = SN,
        DM[JR] = MM,
        DM.Graphs = yP,
        DM.Graph = XN,
        DM.BaseUI = IN,
        DM.ElementUI = zN,
        DM[QR] = hs,
        DM[yC] = rs,
        DM[tD] = GN,
        DM[iD] = $N,
        DM.Shapes = wN,
        DM[eD] = vP,
        DM[_I] = JM,
        DM[nD] = Ir,
        DM.Element = pN,
        DM[CA] = xN,
        DM[aS] = EN,
        DM.GraphModel = Zn,
        DM.EdgeBundle = zj,
        DM[sD] = Aj,
        DM.name = rD;
    var Kj = hD;
    return DM[$y] = aD,
        DM.about = oD,
        DM.copyright = "Copyright © 2015 Qunee.com",
        DM.css = ci,
        DM[_D] = VN,
        ti = function() {},
        DM.publishDate = fD,
        DM;
}(window, document);