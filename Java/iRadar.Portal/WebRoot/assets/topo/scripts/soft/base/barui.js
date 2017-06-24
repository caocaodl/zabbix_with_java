$(function() {
    /**
     * This file is part of Qunee for HTML5.
     * Copyright (c) 2015 by qunee.com
     **/
    var BarUI = function(data) {
        Q.doSuperConstructor(this, BarUI, arguments);
    }

    BarUI.prototype = {
        width: 100,
        height: 12,
        measure: function() {
            this.setMeasuredBounds(this.width, this.height);
        },
        draw: function(g, scale, selected) {
            var value = this.data * 100 | 0;
            var data = this.data;
            if (data > 1) {
                data = 1;
            } else if (data < 0) {
                data = 0;
            }
            var color;
            if (value < 40) {
                color = "#0F0";
            } else if (value < 70) {
                color = "#FF0";
            } else {
                color = "#F00";
            }
            g.fillStyle = color;
            var w = data * this.width;
            var showVal=this.data==0.01?0:value;
            showVal+="%";
            g.fillRect(0, 0, w, this.height);
            g.beginPath();
            g.strokeStyle = "#CCC";
            g.strokeRect(0, 0, this.width, this.height);
            g.fillStyle = "#555";
            g.textBaseline = "middle";
            if (value > 83) {
                g.textAlign = "right";
                g.fillText(showVal, this.width - 1, this.height / 2);
                return;
            }
            g.fillText(showVal, w + 3, this.height / 2);
        }
    }
    Q.extend(BarUI, Q.BaseUI);
    Q.BarUI = BarUI;
    Q.loadClassPath(BarUI, 'Q.BarUI'); //为了能导入导出，需要能全局访问

    function formatNumber(number, decimal, unit) {
        return number.toFixed(decimal) + unit;
    }

    function CustomServerNode(name, id, image, data) {
        Q.doSuperConstructor(this, CustomServerNode);

        this.init(name, id, image, data);
        var _this = this;
        if (data && data.length) {
            for (var i = 0; i < data.length; i++) {
                var obj = data[i],
                    val = "";
                obj.itemtype === "bar" ? val = 0.01 : val = "0";
                this.set(obj.itemkey, val);
            };
        }
    }
    var w = 160,
        h = 120,
        r = 10;
    CustomServerNode.prototype = {
        _showDetail: true,
        iconSize: {
            width: 23
        },
        shape: Q.Shapes.getRect(-w / 2, -h / 2, w, h, r, r),
        init: function(name, id, image, data) {
            this.set("image", image);
            this.set("id", id);
            this.set("name", name);
            this.name = name; // "Double click show detail";

            this.image = this.shape;
            var gradient = new Q.Gradient(Q.Consts.GRADIENT_TYPE_LINEAR, ["#F4F4F4", "#FFFFFF", "#DFDFDF", "#E9E9E9"]);
            gradient.angle = Math.PI / 2;
            this.setStyle(Q.Styles.SHAPE_FILL_GRADIENT, gradient);
            this.setStyle(Q.Styles.SHAPE_STROKE, 0);
            this.setStyle(Q.Styles.SHAPE_OUTLINE, 1);
            this.setStyle(Q.Styles.SHAPE_OUTLINE_STYLE, "#C9C9C9");
            this.setStyle(Q.Styles.LAYOUT_BY_PATH, false);

            function addUIAt(node, ui, x, y, bindingProperty, value) {
                ui.syncSelection = false;
                ui.zIndex = 1;
                ui.position = {
                    x: x,
                    y: y
                };
                ui.anchorPosition = Q.Position.LEFT_TOP;
                ui.fontSize = 10;
                var binding;
                if (bindingProperty) {
                    binding = {
                        property: bindingProperty,
                        propertyType: Q.Consts.PROPERTY_TYPE_CLIENT,
                        bindingProperty: "data"
                    }
                }
                node.addUI(ui, binding);
                return ui;
            }

            var icon = new Q.ImageUI(image);
            icon.size = this.iconSize;
            addUIAt(this, icon, 20, 15, "icon").anchorPosition = Q.Position.CENTER_MIDDLE;
            // addUIAt(this, new Q.LabelUI(name), 30, 5);
            // addUIAt(this, new Q.LabelUI(id), 30, 22).color = "#D00";

            if (data && data.length) {
                var _this = this,
                    barLabelX = 27,
                    barLabelY = 47,
                    barImgX = 30,
                    textLabelX = 10,
                    textLabelY = 90,
                    textConX = 60;

                Q.forEach(data, function(val, index) {
                    if (val.itemtype === "bar") {
                        addUIAt(_this, new Q.LabelUI(val.itemCh), barLabelX, barLabelY).anchorPosition = Q.Position.RIGHT_MIDDLE;
                        addUIAt(_this, new BarUI(), barImgX, barLabelY, val.itemkey).anchorPosition = Q.Position.LEFT_MIDDLE;
                        barLabelY += 18;

                    } else if (val.itemtype === "text") {

                        //证明此面板全部是text
                        barLabelY == 47 && textLabelY == 90 ? textLabelY = 47 : "";
                        val.itemCh.length >= 5 ? textConX = 105 : "";

                        addUIAt(_this, new Q.LabelUI(val.itemCh), textLabelX, textLabelY).anchorPosition = Q.Position.LEFT_MIDDLE;
                        var key = val.itemkey;
                        var ui = addUIAt(_this, new Q.LabelUI(), textConX, textLabelY, key);

                        ui.anchorPosition = Q.Position.LEFT_MIDDLE; //Q.Position.LEFT_MIDDLE;
                        ui.color = "#C20";

                        textLabelY += 16;

                    }
                });
            }
        }
    }
    Q.extend(CustomServerNode, Q.Node);
    Q.CustomServerNode = CustomServerNode;
    Q.loadClassPath(CustomServerNode, 'Q.CustomServerNode');
    Object.defineProperties(CustomServerNode.prototype, {
        showDetail: {
            get: function() {
                return this._showDetail;
            },
            set: function(show) {
                if (this._showDetail == show) {
                    return;
                }
                this._showDetail = show;

                this.image = show ? this.shape : this.get("image");
                this.name = this.get("name"); //show ? "双击合并" : (this.get("name") + "\n" + this.get("id"));
                var uis = this.bindingUIs;
                if (uis) {
                    var _this = this;
                    uis.forEach(function(ui, index) {
                        var alarm = ui.ui.alarm;
                        if (alarm) {

                        } else {
                            ui.ui.visible = show;
                        }

                    })
                    this.invalidate();
                }
            }
        }
    })
});