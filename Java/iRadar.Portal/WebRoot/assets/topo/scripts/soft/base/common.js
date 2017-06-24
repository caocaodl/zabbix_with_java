(function(Q, $) {
  var forEach = function(object, call, scope) {
    if (Array.isArray(object)) {
      return object.forEach(function(v) {
        call.call(this, v);
      }, scope);
    }
    for (var name in object) {
      call.call(scope, object[name], name);
    }
  }

  function DemoTree(items, parent) {
    this.itemMap = {};
    this.items = items;
    this.html = this._createNavTree(items);
    if (parent) {
      parent.appendChild(this.html);
    }
  }

  DemoTree.prototype = {
    itemMap: null,
    _createNavItem: function(name, item, parent) {
      var li = document.createElement("li");
      var a = document.createElement("a");
      if (item.url) {
        a.setAttribute("target", "_blank");
        a.setAttribute("href", item.url);
      } else {
        a.setAttribute("href", "#" + name);
      }
      a.innerHTML = name;
      li.appendChild(a);
      a.appendChild(document.createElement("span"));
      li.data = item;

      if (item.status) {
        $(li).addClass(item.status);
      }
      item.parent = parent;
      item.dom = li;
      item.name = name;
      if (Q.isArray(item)) {
        var ul = this._createNavTree(item);
        li.appendChild(ul);
      }
      this.itemMap[name] = item;
      return li;
    },
    _createNavTree: function(list) {
      var ul = document.createElement("ul");
      $(ul).addClass("nav");
      forEach(list, function(item, name) {
        ul.appendChild(this._createNavItem(name || item.name, item, name ? null : list));
      }, this);
      return ul;
    },
    forEachItem: function(call, scop) {

    }
  }

  Q.DemoTree = DemoTree;

})(Q, jQuery);

;
(function(Q, $) {
  function getFirstChild(parent, childClass) {
    var child = parent.find(childClass);
    if (child.length) {
      return child[0];
    }
  }
  var DIALOG_HTML = '<div class="modal-dialog">\
    <div class="modal-content">\
    <div class="modal-header">\
    <button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;<\/button>\
    <h4 class="modal-title">Title<\/h4>\
    <\/div>\
    <div class="modal-body"><\/div>\
    <\/div>\
    <\/div>';

  function Dialog(title, parent) {
    var html = this.html = document.createElement('div');
    //.bs-example-modal .modal {
    //        position: relative;
    //        top: auto;
    //        right: auto;
    //        bottom: auto;
    //        left: auto;
    //        z-index: 1;
    //        display: block;
    //    }
    html.style.position = 'relative';
    html.style.top = 'auto';
    html.style.right = 'auto';
    html.style.bottom = 'auto';
    html.style.left = 'auto';
    html.style.zIndex = '1';
    html.style.display = 'block';

    if (parent) {
      parent.appendChild(html);
    }
    html = $(html);
    html.addClass('modal');
    html.html(DIALOG_HTML);
    this.body = getFirstChild(html, '.modal-body');
    this._title = getFirstChild(html, '.modal-title');
    this.setTitle(title);
  }
  Dialog.prototype = {
    html: null,
    body: null,
    _title: null,
    setTitle: function(title, byInnerHTML) {
      if (byInnerHTML) {
        this._title.innerHTML = title;
        return;
      }
      this._title.textContent = title;
    },
    show: function() {
      $(this.html).modal("show");
    },
    hide: function() {
      $(this.html).modal("hide");
    }
  }

  var dialog;

  function showDialog(title, innerHTML) {
    if (!dialog) {
      dialog = new Dialog(title, document.body);
    }
    dialog.body.innerHTML = innerHTML || '';
    dialog.show();
    return dialog;
  }
  Q.Dialog = Dialog;
  Q.showDialog = showDialog;
}(Q, jQuery));
(function(Q, $) {
  var template = '<div class="graph-export-panel modal fade">\
  <div class="modal-dialog">\
  <div class="modal-content">\
  <div class="modal-body">\
  <h3 style="text-align: center;">图片导出预览</h3>\
  <div>\
  <label>画布大小</label>\
  <span class ="graph-export-panel__canvas_size"></span>\
  </div>\
  <div style="text-align: center;" title="双击选择全画布范围">\
  <div class ="graph-export-panel__export_canvas" style="position: relative; display: inline-block;">\
  </div>\
  </div>\
  <div>\
  <label>导出范围</label>\
  <span class ="graph-export-panel__export_bounds"></span>\
  </div>\
  <div>\
  <label>缩放比例: <input class ="graph-export-panel__export_scale" type="range" value="1" step="0.2" min="0.2" max="3"><span class ="graph-export-panel__export_scale_label">1</span></label>\
  </div>\
  <div>\
  <label>输出大小: </label><span class ="graph-export-panel__export_size"></span>\
  </div>\
  <div style="text-align: right">\
  <button type="submit" class="btn btn-primary graph-export-panel__export_submit">导出</button>\
  <button type="submit" class="btn btn-primary graph-export-panel__print_submit">打印</button>\
  </div>\
  </div>\
  </div>\
  </div>\
  </div>';

  ///ExportPanel

  function ResizeBox(parent, onBoundsChange) {
    this.onBoundsChange = onBoundsChange;
    this.parent = parent;
    this.handleSize = Q.isTouchSupport ? 20 : 8;

    this.boundsDiv = this._createDiv(this.parent);
    this.boundsDiv.type = "border";
    this.boundsDiv.style.position = "absolute";
    this.boundsDiv.style.border = "dashed 1px #888";
    var handles = "lt,t,rt,l,r,lb,b,rb";
    handles = handles.split(",");
    for (var i = 0, l = handles.length; i < l; i++) {
      var name = handles[i];
      var handle = this._createDiv(this.parent);
      handle.type = "handle";
      handle.name = name;
      handle.style.position = "absolute";
      handle.style.backgroundColor = "#FFF";
      handle.style.border = "solid 1px #555";
      handle.style.width = handle.style.height = this.handleSize + "px";
      var cursor;
      if (name == 'lt' || name == 'rb') {
        cursor = "nwse-resize";
      } else if (name == 'rt' || name == 'lb') {
        cursor = "nesw-resize";
      } else if (name == 't' || name == 'b') {
        cursor = "ns-resize";
      } else {
        cursor = "ew-resize";
      }
      handle.style.cursor = cursor;
      this[handles[i]] = handle;
    }
    this.interaction = new Q.DragSupport(this.parent, this);
  }
  ResizeBox.prototype = {
    destroy: function() {
      this.interaction.destroy();
    },
    update: function(width, height) {
      this.wholeBounds = new Q.Rect(0, 0, width, height);
      this._setBounds(this.wholeBounds.clone());
    },
    ondblclick: function(evt) {
      if (this._bounds.equals(this.wholeBounds)) {
        if (!this.oldBounds) {
          this.oldBounds = this.wholeBounds.clone().grow(-this.wholeBounds.height / 5, -this.wholeBounds.width / 5);
        }
        this._setBounds(this.oldBounds, true);
        return;
      }
      this._setBounds(this.wholeBounds.clone(), true);
    },
    startdrag: function(evt) {
      if (evt.target.type) {
        this.dragItem = evt.target;
      }
    },
    ondrag: function(evt) {
      if (!this.dragItem) {
        return;
      }
      Q.stopEvent(evt);
      var dx = evt.dx;
      var dy = evt.dy;
      if (this.dragItem.type == "border") {
        this._bounds.offset(dx, dy);
        this._setBounds(this._bounds, true);
      } else if (this.dragItem.type == "handle") {
        var name = this.dragItem.name;
        if (name[0] == 'l') {
          this._bounds.x += dx;
          this._bounds.width -= dx;
        } else if (name[0] == 'r') {
          this._bounds.width += dx;
        }
        if (name[name.length - 1] == 't') {
          this._bounds.y += dy;
          this._bounds.height -= dy;
        } else if (name[name.length - 1] == 'b') {
          this._bounds.height += dy;
        }
        this._setBounds(this._bounds, true);
      }

    },
    enddrag: function(evt) {
      if (!this.dragItem) {
        return;
      }
      this.dragItem = false;
      if (this._bounds.width < 0) {
        this._bounds.x += this._bounds.width;
        this._bounds.width = -this._bounds.width;
      } else if (this._bounds.width == 0) {
        this._bounds.width = 1;
      }
      if (this._bounds.height < 0) {
        this._bounds.y += this._bounds.height;
        this._bounds.height = -this._bounds.height;
      } else if (this._bounds.height == 0) {
        this._bounds.height = 1;
      }
      if (this._bounds.width > this.wholeBounds.width) {
        this._bounds.width = this.wholeBounds.width;
      }
      if (this._bounds.height > this.wholeBounds.height) {
        this._bounds.height = this.wholeBounds.height;
      }
      if (this._bounds.x < 0) {
        this._bounds.x = 0;
      }
      if (this._bounds.y < 0) {
        this._bounds.y = 0;
      }
      if (this._bounds.right > this.wholeBounds.width) {
        this._bounds.x -= this._bounds.right - this.wholeBounds.width;
      }
      if (this._bounds.bottom > this.wholeBounds.height) {
        this._bounds.y -= this._bounds.bottom - this.wholeBounds.height;
      }

      this._setBounds(this._bounds, true);
    },
    _createDiv: function(parent) {
      var div = document.createElement("div");
      parent.appendChild(div);
      return div;
    },
    _setHandleLocation: function(handle, x, y) {
      handle.style.left = (x - this.handleSize / 2) + "px";
      handle.style.top = (y - this.handleSize / 2) + "px";
    },
    _setBounds: function(bounds) {
      if (!bounds.equals(this.wholeBounds)) {
        this.oldBounds = bounds;
      }
      this._bounds = bounds;
      bounds = bounds.clone();
      bounds.width += 1;
      bounds.height += 1;
      this.boundsDiv.style.left = bounds.x + "px";
      this.boundsDiv.style.top = bounds.y + "px";
      this.boundsDiv.style.width = bounds.width + "px";
      this.boundsDiv.style.height = bounds.height + "px";

      this._setHandleLocation(this.lt, bounds.x, bounds.y);
      this._setHandleLocation(this.t, bounds.cx, bounds.y);
      this._setHandleLocation(this.rt, bounds.right, bounds.y);
      this._setHandleLocation(this.l, bounds.x, bounds.cy);
      this._setHandleLocation(this.r, bounds.right, bounds.cy);
      this._setHandleLocation(this.lb, bounds.x, bounds.bottom);
      this._setHandleLocation(this.b, bounds.cx, bounds.bottom);
      this._setHandleLocation(this.rb, bounds.right, bounds.bottom);
      if (this.onBoundsChange) {
        this.onBoundsChange(this._bounds);
      }
    }
  }
  Object.defineProperties(ResizeBox.prototype, {
    bounds: {
      get: function() {
        return this._bounds;
      },
      set: function(v) {
        this._setBounds(v);
      }
    }
  });

  function ExportPanel() {
    var export_panel = $('<div/>').html(template).contents();
    this.html = export_panel = export_panel[0];
    document.body.appendChild(this.html);
    export_panel.addEventListener("mousedown", function(evt) {
      // Bug13315
      if (export_panel.offsetWidth - evt.clientX < 20) {
        return;
      }
      if (evt.target == export_panel) {
        this.destroy();
      }
    }.bind(this), false);
    var export_scale = this._getChild(".graph-export-panel__export_scale");
    var export_scale_label = this._getChild(".graph-export-panel__export_scale_label");
    export_scale.onchange = function(evt) {
      export_scale_label.textContent = this.scale = export_scale.value;
      this.updateOutputSize();
    }.bind(this);
    var exportImage = function(print, evt) {

      var graph = this.graph;
      if (!graph) {
        return;
      }
      var scale = export_scale.value;
      var s = this.imageInfo.scale;
      var clipBounds = new Q.Rect(this.clipBounds.x / s, this.clipBounds.y / s, this.clipBounds.width / s, this.clipBounds.height / s);
      clipBounds.offset(this.bounds.x, this.bounds.y);
      var imageInfo = graph.exportImage(scale, clipBounds);

      if (!imageInfo || !imageInfo.data) {
        return false;
      }
      //Bug13315 chrome unprintable
      // var win = window.open(imageInfo.data);
      var win = window.open();
      var doc = win.document;
      doc.title = graph.name || "";
      //        doc.title = "export image - " + imageInfo.width + " x " + imageInfo.height;
      var img = doc.createElement("img");
      img.src = imageInfo.data;
      doc.body.style.textAlign = "center";
      doc.body.style.margin = "0px";
      doc.body.appendChild(img);
      if (print === true) {
        var style = doc.createElement("style");
        style.setAttribute("type", "text/css");
        style.setAttribute("media", "print");
        var printCSS = "img {max-width: 100%; max-height: 100%;}";
        if (clipBounds.width / clipBounds.height > 1.2) {
          printCSS += "\n @page { size: landscape; }";
        }
        style.appendChild(document.createTextNode(printCSS));
        doc.head.appendChild(style);

        img.style.maxWidth = "100%";
        img.style.maxHeight = "100%";

        setTimeout(function() {
          win.print();
          win.onfocus = function() {
            win.close();
          }
        }, 100);

   
      }
    }
    var export_submit = this._getChild(".graph-export-panel__export_submit");
    export_submit.onclick = exportImage.bind(this);
    var print_submit = this._getChild(".graph-export-panel__print_submit");
    print_submit.onclick = exportImage.bind(this, true);
  }
  ExportPanel.prototype = {
    canvas: null,
    html: null,
    _getChild: function(selector) {
      return $(this.html).find(selector)[0];
    },
    initCanvas: function() {
      var export_canvas = this._getChild('.graph-export-panel__export_canvas');
      export_canvas.innerHTML = "";

      var canvas = Q.createCanvas(true);
      export_canvas.appendChild(canvas);
      this.canvas = canvas;

      var export_bounds = this._getChild(".graph-export-panel__export_bounds");
      var export_size = this._getChild(".graph-export-panel__export_size");
      var clipBounds;
      var drawPreview = function() {
        var canvas = this.canvas;
        var g = canvas.g;
        var ratio = canvas.ratio || 1;
        g.save();
        //g.scale(1/g.ratio, 1/g.ratio);
        g.clearRect(0, 0, canvas.width, canvas.height);
        g.drawImage(this.imageInfo.canvas, 0, 0);
        g.beginPath();
        g.moveTo(0, 0);
        g.lineTo(canvas.width, 0);
        g.lineTo(canvas.width, canvas.height);
        g.lineTo(0, canvas.height);
        g.lineTo(0, 0);

        var x = clipBounds.x * ratio,
          y = clipBounds.y * ratio,
          width = clipBounds.width * ratio,
          height = clipBounds.height * ratio;
        g.moveTo(x, y);
        g.lineTo(x, y + height);
        g.lineTo(x + width, y + height);
        g.lineTo(x + width, y);
        g.closePath();
        g.fillStyle = "rgba(0, 0, 0, 0.3)";
        g.fill();
        g.restore();
      }
      var onBoundsChange = function(bounds) {
        clipBounds = bounds;
        this.clipBounds = clipBounds;
        drawPreview.call(this);
        var w = clipBounds.width / this.imageInfo.scale | 0;
        var h = clipBounds.height / this.imageInfo.scale | 0;
        export_bounds.textContent = (clipBounds.x / this.imageInfo.scale | 0) + ", " + (clipBounds.y / this.imageInfo.scale | 0) + ", " + w + ", " + h;
        this.updateOutputSize();
      }
      this.updateOutputSize = function() {
        var export_scale = this._getChild(".graph-export-panel__export_scale");
        var scale = export_scale.value;
        var w = clipBounds.width / this.imageInfo.scale * scale | 0;
        var h = clipBounds.height / this.imageInfo.scale * scale | 0;
        var info = w + " X " + h;
        if (w * h > 3000 * 4000) {
          info += "<span style='color: #F66;'>图幅太大，导出时可能出现内存不足</span>";
        }
        export_size.innerHTML = info;
      }
      var resizeHandler = new ResizeBox(canvas.parentNode, onBoundsChange.bind(this));
      this.update = function() {
        var ratio = this.canvas.ratio || 1;
        var width = this.imageInfo.width / ratio;
        var height = this.imageInfo.height / ratio;
        this.canvas.setSize(width, height);
        resizeHandler.update(width, height);
      }
    },
    destroy: function() {
      this.graph = null;
      this.imageInfo = null
      this.clipBounds = null;
      this.bounds = null;
    },
    show: function(graph) {
      $(this.html).modal("show");

      this.graph = graph;
      var bounds = graph.bounds;
      this.bounds = bounds;

      var canvas_size = this._getChild(".graph-export-panel__canvas_size");
      canvas_size.textContent = (bounds.width | 0) + " X " + (bounds.height | 0);

      var size = Math.min(500, screen.width / 1.3);
      var imageScale;
      if (bounds.width > bounds.height) {
        imageScale = Math.min(1, size / bounds.width);
      } else {
        imageScale = Math.min(1, size / bounds.height);
      }
      if (!this.canvas) {
        this.initCanvas();
      }
      this.imageInfo = graph.exportImage(imageScale * this.canvas.ratio);
      this.imageInfo.scale = imageScale;

      this.update();
    }
  }
  var exportPanel;

  function showExportPanel(graph) {
    if (!exportPanel) {
      exportPanel = new ExportPanel();
    }
    exportPanel.show(graph);
  }

  Q.showExportPanel = showExportPanel;
})(Q, jQuery);
(function(Q, $) {
  //扩展连线UI
  function FlexEdgeUI(edge, graph) {
    Q.doSuperConstructor(this, FlexEdgeUI, arguments);
  }
  FlexEdgeUI.prototype = {
    drawEdge: function(path, fromUI, toUI, edgeType, fromBounds, toBounds) {
      var from = fromBounds.center;
      var to = toBounds.center;
      var cx = (from.x + to.x) / 2;
      var cy = (from.y + to.y) / 2;
      var dx = from.x - to.x,
        dy = from.y - to.y;
      var distance = Math.sqrt(dx * dx + dy * dy);
      var angle = Math.atan2(dy, dx);
      distance = Math.min(30, distance * 0.2);
      if (angle > 0) {
        distance = -distance;
      }
      var cos = Math.cos(angle) * distance;
      var sin = Math.sin(angle) * distance;

      path.curveTo(cx - cos, cy + sin, cx + cos, cy - sin);
    }
  }
  Q.extend(FlexEdgeUI, Q.EdgeUI);
  FlexEdgeUI.drawReferenceLine = function(g, start, end, type) {
    g.moveTo(start.x, start.y);
    var cx = (start.x + end.x) / 2;
    var cy = (start.y + end.y) / 2;
    g.bezierCurveTo(start.x, cy, end.x, cy, end.x, end.y);
  }
  if (Q.loadClassPath) {
    Q.loadClassPath(FlexEdgeUI, "Q.FlexEdgeUI");
  }
  Q.FlexEdgeUI = FlexEdgeUI;
}(Q, jQuery));
//json export and parse support
! function(Q, window, document) {
  if (Q.Graph.prototype.parseJSON) {
    return;
  }

  //<=v1.7

  function JSONSerializer1_7(options) {
    this._refs = {};
    if (options) {
      this.root = options.root;
    }
  }

  JSONSerializer1_7.prototype = {
    _refs: null,
    _index: 1,
    root: null,
    reset: function() {
      this._refs = {};
      this._index = 1;
    },
    getREF: function(id) {
      return this._refs[id];
    },
    clearRef: function() {
      for (var id in this._refs) {
        var json = this._refs[id];
        var value = json._value;
        if (value) {
          if (!value._refed) {
            delete json._refId;
          }
          delete value._refed;
          delete value._refId;
          delete json._value;
        }
      }
      this.reset();
    },
    toJSON: function(value) {
      if (!(value instanceof Object)) {
        return value;
      }
      if (value instanceof Function && !value._classPath) {
        return null;
      }
      if (value._refId !== undefined) {
        value._refed = true;
        return {
          _ref: value._refId
        };
      }
      var refId = this._index++;
      value._refId = refId;
      var json = this._toJSON(value);
      json._refId = refId;
      json._value = value;
      this._refs[refId] = json;
      return json;
    },
    _toJSON: function(value) {
      if (value._classPath) {
        return {
          _classPath: value._classPath
        };
      }
      if (!value._className) {
        return value;
      }
      var result = {
        _className: value._className
      };
      if (value.toJSON) {
        result.json = value.toJSON(this);
      } else {
        result.json = value;
      }
      return result;
    },
    parseJSON: function(json) {
      if (!(json instanceof Object)) {
        return json;
      }
      if (json._ref !== undefined) {
        return this._refs[json._ref];
      }
      if (json._refId !== undefined) {
        return this._refs[json._refId] = this._parseJSON(json);
      }
      return this._parseJSON(json);
    },
    _parseJSON: function(json) {
      if (json._classPath) {
        return getByPath(json._classPath);
      }
      if (json._className) {
        var F = getByPath(json._className);
        var v = new F();
        ///防止相互引用导致的问题
        if (json._refId !== undefined) {
          this._refs[json._refId] = v;
        }
        if (v && json.json) {
          json = json.json;
          if (v.parseJSON) {
            v.parseJSON(json, this);
          } else {
            for (var n in json) {
              v[n] = json[n];
            }
          }
        }
        return v;
      }
      return json;
    }
  }

  //v1.8

  function isEmptyObject(obj) {
    if (!(obj instanceof Object)) {
      return !obj;
    }
    if (Array.isArray(obj)) {
      return obj.length == 0;
    }
    for (var key in obj) {
      return false;
    }
    return true;
  }

  function getByPath(pathName, scope) {
    var paths = pathName.split('.');
    scope = scope || window;
    var i = -1;
    while (scope && ++i < paths.length) {
      var path = paths[i];
      scope = scope[path];
    }
    return scope;
  }

  function loadClassPath(object, namespace, loadChild) {
    object._classPath = namespace;
    if (object instanceof Function) {
      object.prototype._className = object._classPath;
      object.prototype._class = object;
      //            Q.log(v._className);
      //            continue;
    }
    if (loadChild === false) {
      return;
    }
    for (var name in object) {
      if (name[0] == '_' || name[0] == '$' || name == 'superclass' || name == 'constructor' || name == 'prototype' || name.indexOf('.') >= 0) {
        continue;
      }
      var v = object[name];
      if (!v || !(v instanceof Object) || v._classPath) {
        continue;
      }
      loadClassPath(v, namespace + '.' + name);
    }
  }

  var prototypes = {};

  function getPrototype(data) {
    var className = data._className;
    if (!className) {
      return null;
    }
    var prototype = prototypes[className];
    if (!prototype) {
      var clazz = data._class;
      prototype = prototypes[className] = new clazz();
    }
    return prototype;
  }

  function equals(a, b) {
    return a == b || (a && b && a.equals && a.equals(b));
  }

  Q.HashList.prototype.toJSON = function(serializer) {
    var datas = [];
    this.forEach(function(data) {
      datas.push(serializer.toJSON(data));
    })
    return datas;
  }

  Q.HashList.prototype.parseJSON = function(json, serializer) {
    json.forEach(function(item) {
      this.add(serializer.parseJSON(item));
    }, this)
  }

  function exportElementProperties(serializer, properties, info, element) {
    var prototype = getPrototype(element);
    properties.forEach(function(name) {
      var value = element[name];
      if (!equals(value, prototype[name])) {
        var json = serializer.toJSON(value);
        if (json || !value) {
          info[name] = json;
        }
      }
    }, element);
  }

  function exportProperties(serializer, properties) {
    var info;
    for (var s in properties) {
      if (!info) {
        info = {};
      }
      info[s] = serializer.toJSON(properties[s]);
    }
    return info;
  }

  var wirtableUIProperties = {
    class: false,
    id: false,
    "fillGradient": false,
    "syncSelectionStyles": false,
    "originalBounds": false,
    "parent": false,
    "font": false,
    "$data": false,
    "$x": false,
    "$y": false
  };

  Q.BaseUI.prototype.toJSON = function(serializer) {
      var json = {};
      for (var name in this) {
        if (name[0] == '_' || (name[0] == '$' && name[1] == '_') || (name.indexOf('$invalidate') == 0) || wirtableUIProperties[name] === false) {
          continue;
        }
        var value = this[name];
        if (value instanceof Function || value == this.class.prototype[name]) {
          continue;
        }
        //wirtableUIProperties[name] = true;

        try {
          json[name] = serializer.toJSON(value);
        } catch (error) {

        }

      }

      return json;
    }
    //new Q.ImageUI().toJSON();
    //new Q.LabelUI().toJSON();
    //Q.log(JSON.stringify(wirtableUIProperties))

  Q.BaseUI.prototype.parseJSON = function(info, serializer) {
    for (var name in info) {
      var v = serializer.parseJSON(info[name]);
      this[name] = v;
    }
  }

  Q.Element.prototype.toJSON = function(serializer) {
    var info = {};
    var outputProperties = ['enableSubNetwork', 'zIndex', 'tooltipType', 'tooltip', 'movable', 'selectable', 'resizable', 'uiClass', 'name', 'parent', 'host'];
    if (this.outputProperties) {
      outputProperties = outputProperties.concat(this.outputProperties);
    }
    exportElementProperties(serializer, outputProperties, info, this);
    if (this.styles) {
      var styles = exportProperties(serializer, this.styles);
      if (styles) {
        info.styles = styles;
      }
    }
    if (this.properties) {
      var properties = exportProperties(serializer, this.properties);
      if (properties) {
        info.properties = properties;
      }
    }
    var bindingUIs = this.bindingUIs;
    if (bindingUIs) {
      var bindingJSONs = [];
      //var binding = {id: ui.id, ui: ui, bindingProperties: bindingProperties};
      bindingUIs.forEach(function(binding) {
        var uiJSON = serializer.toJSON(binding.ui);
        bindingJSONs.push({
          ui: uiJSON,
          bindingProperties: binding.bindingProperties
        })
      })
      info.bindingUIs = bindingJSONs;
    }
    return info;
  }
  Q.Element.prototype.parseJSON = function(info, serializer) {
    if (info.styles) {
      var styles = {};
      for (var n in info.styles) {
        styles[n] = serializer.parseJSON(info.styles[n]);
      }
      this.putStyles(styles, true);
      //delete info.styles;
    }
    if (info.properties) {
      var properties = {};
      for (var n in info.properties) {
        properties[n] = serializer.parseJSON(info.properties[n]);
      }
      this.properties = properties;
    }
    if (info.bindingUIs) {
      info.bindingUIs.forEach(function(binding) {
        var ui = serializer.parseJSON(binding.ui);
        if (!ui) {
          return;
        }
        this.addUI(ui, binding.bindingProperties);

        //var circle = new Q.ImageUI(ui.data);
        //circle.lineWidth = 2;
        //circle.strokeStyle = '#ff9f00';
        //this.addUI(circle);
      }, this)
    }
    for (var n in info) {
      if (n == 'styles' || n == 'properties' || n == 'bindingUIs') {
        continue;
      }
      var v = serializer.parseJSON(info[n]);
      this[n] = v;
    }
  }
  Q.Node.prototype.toJSON = function(serializer) {
    var info = Q.doSuper(this, Q.Node, 'toJSON', arguments);
    exportElementProperties(serializer, ['location', 'size', 'image', 'rotate', 'anchorPosition'], info, this);
    return info;
  }
  Q.Group.prototype.toJSON = function(serializer) {
    var info = Q.doSuper(this, Q.Group, 'toJSON', arguments);
    exportElementProperties(serializer, ['minSize', 'groupType', 'padding', 'groupImage', 'expanded'], info, this);
    return info;
  }
  Q.ShapeNode.prototype.toJSON = function(serializer) {
    var info = Q.doSuper(this, Q.Node, 'toJSON', arguments);
    exportElementProperties(serializer, ['location', 'rotate', 'anchorPosition', 'path'], info, this);
    return info;
  }
  Q.Edge.prototype.toJSON = function(serializer) {
    var info = Q.doSuper(this, Q.Edge, 'toJSON', arguments);
    exportElementProperties(serializer, ['from', 'to', 'edgeType', 'angle', 'bundleEnabled', 'pathSegments'], info, this);
    return info;
  }

  function JSONSerializer(options) {
    if (options) {
      this.withGlobalRefs = options.withGlobalRefs !== false;
    }
    this.reset();
  }

  JSONSerializer.prototype = {
    _refs: null,
    _refValues: null,
    _index: 1,
    root: null,
    reset: function() {
      this._globalRefs = {};
      this._elementRefs = {};
      this._refs = {};
      this._refValues = {};
      this._index = 1;
    },
    getREF: function(id) {
      return this._refs[id];
    },
    clearRef: function() {
      for (var id in this._globalRefs) {
        delete this._globalRefs[id]._value;
      }
      for (var id in this._refValues) {
        delete this._refValues[id]._refId;
      }
      this.reset();
    },
    elementToJSON: function(element) {
      return this._toJSON(element);
    },
    _elementRefs: null,
    _globalRefs: null,
    withGlobalRefs: true,
    toJSON: function(value) {
      if (!(value instanceof Object)) {
        return value;
      }
      if (value instanceof Function && !value._classPath) {
        return null;
      }
      if (!this.withGlobalRefs) {
        return this._toJSON(value);
      }
      if (value instanceof Q.Element) {
        this._elementRefs[value.id] = true;
        return {
          _ref: value.id
        };
      }
      if (value._refId === undefined) {
        var json = this._toJSON(value);
        if (!json) {
          return json;
        }
        //添加引用标记，下次遇到这个对象时，不需要再toJSON，而是直接输出引用，比如{"$ref": 1}
        var id = value._refId = this._index++;
        //将对象暂时存放在_refValues中，以便在导出完成后，删除掉上一步对value增加的_refId属性
        this._refValues[id] = value;
        this._refs[id] = json;
        return json;
      }
      //遇到相同的对象，将对象信息存放到全局map，网元属性只需要存放引用id，比如{"$ref": 1}
      //全局map中存放在g属性中，以id为key，json为value，如下：
      //"refs": {
      //  "1": {
      //    "_classPath": "Q.Position.LEFT_BOTTOM"
      //  }
      //},
      //"datas": [
      //  {
      //    "_className": "Q.Node",
      //    "json": {
      //      "name": "A",
      //      "styles": {
      //        "property": {
      //          "$ref": 1
      //        }
      //      },
      var id = value._refId;
      if (!this._globalRefs[id]) {
        //如果还没有加入到全局引用区，则将json放入到_globalRefs，同时将原来的json变成引用方式
        var json = this._refs[id];
        if (!json) {
          return json;
        }
        var clone = {};
        for (var name in json) {
          clone[name] = json[name];
          delete json[name];
        }
        json.$ref = id;
        this._globalRefs[id] = clone;
      }
      return {
        $ref: id
      };
    },
    _toJSON: function(value) {
      if (value._classPath) {
        return {
          _classPath: value._classPath
        };
      }
      if (!value._className) {
        if (Q.isArray(value)) {
          var json = [];
          value.forEach(function(v) {
            json.push(this.toJSON(v));
          }, this)
          return json;
        } else {
          json = {};
          var prototype;
          if (value.class) {
            prototype = value.class.prototype;
          }
          for (var name in value) {
            var v = value[name];
            if (v instanceof Function || (prototype && v == prototype[name])) {
              continue;
            }
            json[name] = this.toJSON(value[name]);
          }
          return json;
        }

        return value;
      }
      var result = {
        _className: value._className
      };
      if (value.toJSON) {
        result.json = value.toJSON(this);
      } else {
        result.json = value;
      }
      return result;
    },
    jsonToElement: function(json) {
      //如果之前解析的数据中引用到了此节点，此节点其实已经被解析了，这里只需要返回引用就可以了
      if (json._refId !== undefined && json._refId in this._refs) {
        return this._refs[json._refId];
      }
      return this._parseJSON(json);
    },
    parseJSON: function(json) {
      if (!(json instanceof Object)) {
        return json;
      }
      if (!this.withGlobalRefs) {
        return this._parseJSON(json);
      }
      //全局引用
      if (json.$ref !== undefined) {
        //从全局引用中获取json信息
        var gJson = this._globalRefs[json.$ref];
        if (!gJson) {
          return;
        }
        //将json信息解析成对象，并缓存在json的_value属性中
        if (gJson._value === undefined) {
          gJson._value = this.parseJSON(gJson);
        }
        return gJson._value;
      }
      //如果属性为element引用，先从_elementRefs中找到对应element的json信息，然后将此json信息解析成element
      if (json._ref !== undefined) {
        var elementJson = this._elementRefs[json._ref];
        if (!elementJson) {
          return;
        }
        return this.jsonToElement(elementJson);
      }
      ////如果json包含_refId属性，说明这是一个element类型，直接调用jsonToElement，不过应该不会出现
      //if (json._refId !== undefined) {
      //  return this.jsonToElement(json);
      //}
      return this._parseJSON(json);
    },
    _parseJSON: function(json) {
      if (!(json instanceof Object)) {
        return json;
      }
      if (json._classPath) {
        return getByPath(json._classPath);
      }
      if (json._className) {
        var F = getByPath(json._className);
        var v = new F();
        ///防止相互引用导致的问题
        if (this.withGlobalRefs && json._refId !== undefined) {
          this._refs[json._refId] = v;
        }
        if (v && json.json) {
          json = json.json;
          if (v.parseJSON) {
            v.parseJSON(json, this);
          } else {
            for (var n in json) {
              v[n] = json[n];
            }
          }
        }
        return v;
      }
      if (Q.isArray(json)) {
        var result = [];
        json.forEach(function(j) {
          result.push(this.parseJSON(j));
        }, this)
        return result;
      }
      var result = {};
      for (var name in json) {
        result[name] = this.parseJSON(json[name])
      }
      return result;
    }
  }

  //var json = {
  //  refs: {//
  //    1: {},
  //    2: {}
  //  },
  //  datas: [{
  //    type: '',
  //    json: {
  //      parent: {_ref: 1},
  //      image: {_g: 1}
  //    }
  //  },{
  //    type: 'group',
  //    _refId: 1,
  //    json: {}
  //  }, {}],
  //  version: '1.8'
  //}

  function graphModelToJSON(model, filter) {
    var serializer = new JSONSerializer();
    var json = {
      version: '1.8',
      refs: {}
    };
    var datas = [];
    var map = {};
    if (model.currentSubNetwork) {
      var elementJson = serializer.elementToJSON(model.currentSubNetwork);
      if (elementJson) {
        json.currentSubNetwork = {
          _ref: elementJson._refId = model.currentSubNetwork.id
        };
      }
    }
    model.forEach(function(d) {
      if (filter && filter(d) === false) {
        return;
      }
      var elementJson = serializer.elementToJSON(d);
      if (elementJson) {
        datas.push(elementJson);
        map[d.id] = elementJson;
      }
    });
    if (serializer._elementRefs) {
      for (var id in serializer._elementRefs) {
        map[id]._refId = id;
      }
    }
    if (serializer._globalRefs) {
      json.refs = serializer._globalRefs;
    }
    serializer.clearRef();

    json.datas = datas;
    for (var name in json) {
      if (isEmptyObject(json[name])) {
        delete json[name];
      }
    }
    return json;
  }

  Q.GraphModel.prototype.toJSON = function(filter) {
    return graphModelToJSON(this, filter);
  }

  function versionToNumber(version) {
    var index = version.indexOf('.');
    if (index < 0) {
      return parseFloat(version);
    }
    version = version.substring(0, index) + '.' + version.substring(index).replace(/\./g, '');
    return parseFloat(version);
  }

  Q.GraphModel.prototype.parseJSON = function(json, options) {
    var datas = json.datas;
    if (!datas || !(datas.length > 0)) {
      return;
    }
    //旧版本兼容
    if (versionToNumber(json.version) <= 1.7) {
      var serializer = new JSONSerializer1_7(options);
      var datas = json.datas;
      datas.forEach(function(json) {
        var element = serializer.parseJSON(json);
        if (element instanceof Q.Element) {
          this.add(element);
        }
      }, this);

      serializer.reset();
      return;
    }

    var serializer = new JSONSerializer(options, json.g);
    var elementRefs = {};
    datas.forEach(function(info) {
      if (info._refId) {
        elementRefs[info._refId] = info;
      }
    })
    serializer._globalRefs = json.refs || {};
    serializer._elementRefs = elementRefs;

    datas.forEach(function(json) {
      var element = serializer.jsonToElement(json);
      if (element instanceof Q.Element) {
        this.add(element);
      }
    }, this);

    if (json.currentSubNetwork) {
      var currentSubNetwork = serializer.getREF(json.currentSubNetwork._ref);
      if (currentSubNetwork) {
        this.currentSubNetwork = currentSubNetwork;
      }
    }

    serializer.clearRef();
  }

  Q.Graph.prototype.toJSON = Q.Graph.prototype.exportJSON = function(toString, options) {
    options = options || {};
    var json = this.graphModel.toJSON(options.filter);
    if (toString) {
      json = JSON.stringify(json, options.replacer, options.space || '\t')
    }
    return json;
  }
  Q.Graph.prototype.parseJSON = function(json, options) {
    if (Q.isString(json)) {
      json = JSON.parse(json);
    }
    this.graphModel.parseJSON(json, options);
  }

  loadClassPath(Q, 'Q');
  Q.loadClassPath = loadClassPath;

  Q.exportJSON = function(object, toString) {
    try {
      var json = new JSONSerializer({
        withGlobalRefs: false
      }).toJSON(object);
      return toString ? JSON.stringify(json) : json;
    } catch (error) {}
  }
  Q.parseJSON = function(json) {
    try {
      if (Q.isString(json)) {
        json = JSON.parse(json);
      }
      return new JSONSerializer({
        withGlobalRefs: false
      }).parseJSON(json);
    } catch (error) {}
  }
}(Q, window, document);

//svg support

function SVGtoPathSegment(path, svgSegment, prevPoints, matrix) {
  svgSegment = svgSegment.trim();
  var isLowerLetter = svgSegment[0] > "Z";
  var type = svgSegment[0].toUpperCase();

  var points = svgSegment.substring(1);
  if (points) {
    points = points.trim().split(/[\s,]/g);
    var prevX = 0,
      prevY = 0;
    if (points.length && isLowerLetter && prevPoints && prevPoints.length >= 2) {
      prevX = prevPoints[prevPoints.length - 2];
      prevY = prevPoints[prevPoints.length - 1];
    }
    var x, y;
    for (var i = 0, l = points.length; i < l; i++) {
      points[i] = parseFloat(points[i]);
      if (i % 2) {
        y = points[i];
        if (matrix) {
          var p = matrix.translatePoint(x, y);
          points[i - 1] = p.x;
          points[i] = p.y;
        }
        if (prevX || prevY) {
          points[i - 1] += prevX;
          points[i] += prevY;
        }
      } else {
        x = points[i];
      }
    }
  }
  switch (type) {
    case "M":
      path.moveTo(points[0], points[1]);
      break;
    case "L":
      path.lineTo(points[0], points[1]);
      break;
    case "Z":
      path.closePath();
      break;
    case "Q":
      path.quadTo(points[0], points[1], points[2], points[3]);
      break;
    case "C":
      path.curveTo(points[0], points[1], points[2], points[3], points[4], points[5]);
      break;
  }
  return points;
}

var pathSegmentPattern = /[a-z][^a-z]*/ig;

function SVGPathToPath(d, m) {
  var path = new Q.Path();
  var segments = d.match(pathSegmentPattern);
  var points;
  if (m && !m.translatePoint) {
    m.translatePoint = Matrix.prototype.translatePoint;
  }
  Q.forEach(segments, function(segment) {
    points = SVGtoPathSegment(path, segment, points, m);
  });
  return path;
}

function SVGRectToPath(r) {
  var x = parseFloat(r.getAttribute("x"));
  var y = parseFloat(r.getAttribute("y"));
  var w = parseFloat(r.getAttribute("width"));
  var h = parseFloat(r.getAttribute("height"));
  return Q.Shapes.getRect(x, y, w, h);
}

//math

function _toNumbers(matrixString) {
  var index = matrixString.indexOf("(");
  if (index) {
    var lastIndex = matrixString.lastIndexOf(")");
    if (lastIndex < 0) {
      lastIndex = matrixString.length;
    }
    matrixString = matrixString.substring(index + 1, lastIndex);
  }
  var numbers = matrixString.split(",");
  for (var i = 0, l = numbers.length; i < l; i++) {
    numbers[i] = parseFloat(numbers[i]);
  }
  return numbers;
}

/**
 * a c e
 * b d f
 * 0 0 1
 */
var Matrix = function(a, b, c, d, e, f) {
  if (a !== undefined) {
    this.set(a, b, c, d, e, f);
  }
}

Matrix.multiply = function(out, a, b) {
  var a00 = a.a,
    a01 = a.c,
    a02 = a.e,
    a10 = a.b,
    a11 = a.d,
    a12 = a.f,
    b00 = b.a,
    b01 = b.c,
    b02 = b.e,
    b10 = b.b,
    b11 = b.d,
    b12 = b.f;

  if (!out) {
    out = new Matrix();
  }
  out.a = b00 * a00 + b01 * a10;
  out.c = b00 * a01 + b01 * a11;
  out.e = b00 * a02 + b01 * a12 + b02;

  out.b = b10 * a00 + b11 * a10;
  out.d = b10 * a01 + b11 * a11;
  out.f = b10 * a02 + b11 * a12 + b12;
  return out;
}

/**
 * a c e
 * b d f
 * 0 0 1
 * @type
 */
Matrix.prototype = {
  a: 1,
  b: 0,
  c: 0,
  d: 1,
  e: 0,
  f: 0,
  set: function(a, b, c, d, e, f) {
    if (Q.isString(a)) {
      var numbers = _toNumbers(a);
      if (a.indexOf("translate") >= 0) {
        this.e = numbers[0];
        this.f = numbers[1];
      } else {
        this.a = numbers[0] || 1;
        this.b = numbers[1] || 0;
        this.c = numbers[2] || 0;
        this.d = numbers[3] || 1;
        this.e = numbers[4] || 0;
        this.f = numbers[5] || 0;
      }
      return;
    }
    this.a = a;
    this.b = b;
    this.c = c;
    this.d = d;
    this.e = e;
    this.f = f;
  },
  translatePoint: function(x, y) {
    var x2 = x * this.a + y * this.c + this.e;
    var y2 = x * this.b + y * this.d + this.f;
    return {
      x: x2,
      y: y2
    };
  },
  translate: function(dx, dy) {
    if (Q.isString(dx)) {
      var numbers = dx.split(",");
      dx = parseFloat(numbers[0]) || 0;
      dy = parseFloat(numbers[1]) || 0;
    }
    this.e += dx;
    this.f += dy;
  },
  scale: function(sx, sy) {
    if (!sx) {
      return;
    }
    sy == sy || sx;
    this.a *= sx;
    this.b *= sx;
    this.e *= sx;
    this.c *= sy;
    this.d *= sy;
    this.f *= sy;
  },
  rotate: function(angle) {
    var cos = Math.cos(angle);
    var sin = Math.sin(angle);
    this.a *= cos;
    this.b *= sin;
    this.c *= -sin;
    this.d *= cos;
  },
  multiply: function(m, out) {
    return Matrix.multiply(out || this, this, m);
  },
  toString: function() {
    return "matrix(" + formatNumber(this.a) + "," + formatNumber(this.b) + "," + formatNumber(this.c) + "," + formatNumber(this.d) + "," + formatNumber(this.e) + "," + formatNumber(this.f) + ")";
  }
}

function formatNumber(number) {
  return number.toFixed(4);
}

;
(function(Q, $) {

  //Map
  function LoadMapFromJSON(json, map, action, callback, scope) {
    for (var n in json.paths) {
      var path = json.paths[n];
      var shape = map.createShape(path);
      if (action) {
        action.call(scope, shape, "path", path);
      }
    }
    for (var n in json.points) {
      var p = json.points[n];
      var point = map.createPoint(p);
      if (action) {
        action.call(scope, point, "point", p);
      }
    }
    for (var n in json.labels) {
      var label = json.labels[n];
      var text = map.createText(label);
      if (action) {
        action.call(scope, text, "label", label);
      }
    }
    if (callback) {
      callback.call(scope, map);
    }
  }

  var MapChart = function(div) {
    Q.doSuperConstructor(this, MapChart, arguments);
    this.disableRectangleSelection = true;
  }

  MapChart.prototype = {
    _createElement: function(type, name, styles, properties, styles2, properties2) {
      if (!(type.prototype instanceof Q.Element)) {
        throw new Error("Element Must inherit from 'Q.Element' Type");
      }
      var element = new type();
      element.name = name;
      if (properties) {
        for (var n in properties) {
          element[n] = properties[n];
        }
      }
      if (styles) {
        element.putStyles(styles);
      }
      this.graphModel.add(element);
      return element;
    },
    loadJSON: function(json, action, callback, scope) {
      return LoadMapFromJSON(json, this, action, callback, scope);
    },
    createText: function(name, x, y, styles, properties) {
      if (name && name.text) {
        x = name.x || 0;
        y = name.y || 0;
        if (name.matrix) {
          var p = name.matrix.translatePoint(x, y);
          x = p.x;
          y = p.y;
        }
        name = name.text;
      }
      var text = this._createElement(Q.Node, name, styles, properties);
      text.$type = "text";
      text.location = new Q.Point(x, y);
      text.image = null;
      text.setStyle(Q.Styles.LABEL_ANCHOR_POSITION, Q.Position.CENTER_MIDDLE);
      text.setStyle(Q.Styles.LABEL_POSITION, Q.Position.CENTER_MIDDLE);
      return text;
    },
    defaultPointShape: Q.Consts.SHAPE_CIRCLE,
    defaultPointSize: 5,
    createPoint: function(point, name, styles, properties) {
      var shape = point.shape || this.defaultPointShape;
      var size = point.size || this.defaultPointSize;
      var path = Q.Shapes.getShape(shape, size, size);
      var node = this._createElement(Q.Node, name || point.name, styles, properties);
      node.image = path;
      node.$type = "point";
      var x = point.x || 0,
        y = point.y || 0;
      if (point.matrix) {
        var p = point.matrix.translatePoint(x, y);
        x = p.x, y = p.y;
      }
      node.setLocation(x, y);
      return node;
    },
    createShape: function(path, name, styles, properties) {
      if (Q.isString(path)) {
        path = SVGPathToPath(path);
      } else if (path.path) {
        path = SVGPathToPath(path.path, path.matrix);
        if (!name) {
          name = path.name;
        }
      }
      var node = this._createElement(Q.Node, name, styles, properties);
      node.$type = "shape";
      node.image = path;
      node.anchorPosition = null;
      return node;
    },
    defaultMatchType: MATCH_TYPE_FUZZY,
    getElementByName: function(name, matchType) {
      return this._findElementsBy(true, "name", name, matchType);
    },
    findElementsByName: function(name, matchType) {
      return this._findElementsBy(false, "name", name, matchType);
    },
    getElementBy: function(propertyName, propertyValue, matchType) {
      return this._findElementsBy(true, propertyName, propertyValue, matchType);
    },
    findElementsBy: function(propertyName, propertyValue, matchType) {
      return this._findElementsBy(false, propertyName, propertyValue, matchType);
    },
    _findElementsBy: function(getFirst, propertyName, propertyValue, matchType) {
      var matchFunction = getMatchFunction(matchType || this.defaultMatchType, propertyValue);
      if (!getFirst) {
        var result = [];
      }
      var datas = this.graphModel._values;
      for (var i = 0, l = datas.length; i < l; i++) {
        var data = datas[i];
        if (matchFunction(data[propertyName])) {
          if (getFirst) {
            return data;
          }
          result.push(data);
        }
      }
      if (!getFirst) {
        return result;
      }
    },
    addHTMLElement: function(div, x, y, isLogicalLocation) {
      if (isLogicalLocation) {
        var p = this.toCanvas(x, y);
        x = p.x;
        y = p.y;
      }
      div.style.position = "absolute";
      this.html.appendChild(div);
      div.style.left = (x - div.offsetWidth / 2) + "px";
      div.style.top = (y - div.offsetHeight / 2) + "px";
      div.x = x;
      div.y = y;
    },
    removeHTMLElement: function(div) {
      this.html.removeChild(div);
    },
    loadJSONByURL: function(url, action, callback) {
      var scope = this;
      Q.loadJSON(url, function(json) {
        scope.loadJSON(json, action, callback);
      })
    }
  }

  Q.extend(MapChart, Q.Graph);

  var MATCH_TYPE_FUZZY = "fuzzy";
  var MATCH_TYPE_EXACT = "exact";
  var MATCH_TYPE_START = "start";

  function getMatchFunction(matchType, v1) {
    if (!Q.isString(v1) || matchType == MATCH_TYPE_EXACT) {
      return function(v2) {
        return v1 == v2;
      }
    }
    var reg;
    if (matchType == MATCH_TYPE_START) {
      reg = new RegExp("\\b" + v1, "i");
    } else {
      reg = new RegExp(v1, "i");
    }
    return function(v2) {
      return reg.test(v2);
    }
  }

  Q.MapChart = MapChart;
})(Q, jQuery);;
(function(Q, $) {
  //PopupMenu
  function showDivAt(div, x, y) {
    var body = document.documentElement;
    var bounds = new Q.Rect(window.pageXOffset, window.pageYOffset, body.clientWidth - 2, body.clientHeight - 2);
    var width = div.offsetWidth;
    var height = div.offsetHeight;

    if (x + width > bounds.x + bounds.width) {
      x = bounds.x + bounds.width - width;
    }
    if (y + height > bounds.y + bounds.height) {
      y = bounds.y + bounds.height - height;
    }
    if (x < bounds.x) {
      x = bounds.x;
    }
    if (y < bounds.y) {
      y = bounds.y;
    }
    div.style.left = x + 'px';
    div.style.top = y + 'px';
  }

  function isDescendant(parent, child) {
    var node = child.parentNode;
    while (node != null) {
      if (node == parent) {
        return true;
      }
      node = node.parentNode;
    }
    return false;
  }

  var PopupMenu = function(items) {
    this.items = items || [];
  }
  var menuClassName = 'dropdown-menu';
  PopupMenu.Separator = 'divider';

  PopupMenu.prototype = {
    dom: null,
    _invalidateFlag: true,
    add: function(item) {
      this.items.push(item);
      this._invalidateFlag = true;
    },
    addSeparator: function() {
      this.add(PopupMenu.Separator);
    },
    showAt: function(x, y) {
      if (!this.items || !this.items.length) {
        return false;
      }
      if (this._invalidateFlag) {
        this.render();
      }
      this.dom.style.display = "block";
      document.body.appendChild(this.dom);
      showDivAt(this.dom, x, y);
    },
    hide: function() {
      if (this.dom && this.dom.parentNode) {
        this.dom.parentNode.removeChild(this.dom);
      }
    },

    render: function() {
      this._invalidateFlag = false;
      if (!this.dom) {
        this.dom = document.createElement('ul');
        this.dom.setAttribute("role", "menu");
        this.dom.className = menuClassName;
        var startEventName = Q.isTouchSupport ? "touchstart" : "mousedown";

        if (!this.stopEditWhenClickOnWindow) {
          var _this = this;
          this.stopEditWhenClickOnWindow = function(evt) {
            if (isDescendant(_this.html, evt.target)) {
              _this.hide();
            }
          }
        }
        window.addEventListener("mousedown", this.stopEditWhenClickOnWindow, true);
        this.dom.addEventListener(startEventName, function(evt) {
          Q.stopEvent(evt);
        }, false);
      } else {
        this.dom.innerHTML = "";
      }
      for (var i = 0, l = this.items.length; i < l; i++) {
        var item = this.renderItem(this.items[i]);
        this.dom.appendChild(item);
      }
    },
    html2Escape: function(sHtml) {
      return sHtml.replace(/[<>&"]/g, function(c) {
        return {
          '<': '&lt;',
          '>': '&gt;',
          '&': '&amp;',
          '"': '&quot;'
        }[c];
      });
    },
    renderItem: function(menuItem, zIndex) {
      var dom = document.createElement('li');
      dom.setAttribute("role", "presentation");
      if (menuItem == PopupMenu.Separator) {
        dom.className = PopupMenu.Separator;
        dom.innerHTML = " ";
        return dom;
      }
      if (Q.isString(menuItem)) {
        dom.innerHTML = '<a role="menuitem" tabindex="-1" href="#">' + this.html2Escape(menuItem) + '</a>';
        return dom;
      }
      if (menuItem.selected) {
        dom.style.backgroundPosition = '3px 5px';
        dom.style.backgroundRepeat = 'no-repeat';
        dom.style.backgroundImage = "url('data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAABAAAAAQCAYAAAAf8/9hAAAAPklEQVQ4y2P4//8/AyWYYdQA7AYAAZuamlo7ED+H4naQGNEGQDX/R8PtpBjwHIsBz+lqAGVeoDgQR1MiaRgAnxW7Q0QEK0cAAAAASUVORK5CYII=')";
      }
      var a = document.createElement("a");
      a.setAttribute("role", "menuitem");
      a.setAttribute("tabindex", "-1");
      a.setAttribute("href", "javascript:void(0)");
      dom.appendChild(a);

      if (menuItem.html) {
        a.innerHTML = menuItem.html;
      } else {
        var text = menuItem.text || menuItem.name;
        if (text) {
          a.innerHTML = this.html2Escape(text);
        }
      }
      var className = menuItem.className;
      if (className) {
        dom.className = className;
      }
      var call = menuItem.action;
      var self = this;

      var onclick = function(evt) {
        if (call) {
          call.call(menuItem.scope, evt, menuItem);
        }
        if (!Q.isIOS) {
          evt.target.focus();
        }
        setTimeout(function() {
          self.hide();
        }, 100);
      };
      if (Q.isTouchSupport) {
        //            dom.ontouchstart = onclick;
        a.ontouchstart = onclick;
      } else {
        dom.onclick = onclick;
      }
      return dom;
    },
    getMenuItems: function(graph, data, evt) {
      var items = [];
      if (data) {
        var isShapeNode = data instanceof Q.ShapeNode;
        var isGroup = data instanceof Q.Group;
        var isNode = !isShapeNode && data instanceof Q.Node;
        var isEdge = data instanceof Q.Edge;

        items.push({
          text: '编辑名称',
          action: function(evt, item) {
            Q.prompt('输入图元名称', data.name || '', function(name) {
              if (name === null) {
                return;
              }
              data.name = name;
            })
          }
        });
        if (isEdge) {
          var isDashLine = data.getStyle(Q.Styles.EDGE_LINE_DASH) || Q.DefaultStyles[Q.Styles.EDGE_LINE_DASH];
          items.push({
            text: isDashLine ? '实线样式' : '虚线样式',
            action: function(evt, item) {
              data.setStyle(Q.Styles.EDGE_LINE_DASH, isDashLine ? null : [5, 3]);
            }
          });
          items.push({
            text: '连线线宽',
            action: function(evt, item) {
              Q.prompt('输入连线线宽', data.getStyle(Q.Styles.EDGE_WIDTH) || Q.DefaultStyles[Q.Styles.EDGE_WIDTH], function(lineWidth) {
                if (lineWidth === null) {
                  return;
                }
                lineWidth = parseFloat(lineWidth);
                data.setStyle(Q.Styles.EDGE_WIDTH, lineWidth);
              })
            }
          });
          items.push({
            text: '连线颜色',
            action: function(evt, item) {
              Q.prompt('输入连线颜色', data.getStyle(Q.Styles.EDGE_COLOR) || Q.DefaultStyles[Q.Styles.EDGE_COLOR], function(color) {
                if (color === null) {
                  return;
                }
                data.setStyle(Q.Styles.EDGE_COLOR, color);
              })
            }
          });
        } else if (data.parent) {
          var text;
          if (data.parent instanceof Q.Group) {
            text = '脱离分组';
          } else if (data.parent.enableSubNetwork) {
            text = '脱离子网';
          } else {
            text = '脱离父节点'
          }
          items.push({
            text: text,
            action: function() {
              data.parent = data.parent.parent;
            }
          })
        }
        items.push(Q.PopupMenu.Separator);
        items.push({
          text: '置顶显示',
          action: function(evt, item) {
            data.zIndex = 1;
            graph.sendToTop(data);
            graph.invalidate();
          }
        });
        items.push({
          text: '置底显示',
          action: function(evt, item) {
            data.zIndex = -1;
            graph.sendToBottom(data);
            graph.invalidate();
          }
        });
        items.push({
          text: '恢复默认层',
          action: function(evt, item) {
            data.zIndex = 0;
            graph.invalidate();
          }
        });
        items.push(Q.PopupMenu.Separator);
      }
      items.push({
        text: '清空画布',
        action: function() {
          graph.clear();
        }
      })
      items.push(Q.PopupMenu.Separator);

      items.push({
        text: '放大',
        action: function(evt, item) {
          var localXY = graph.globalToLocal(evt);
          graph.zoomIn(localXY.x, localXY.y, true);
        }
      });
      items.push({
        text: '缩小',
        action: function(evt, item) {
          var localXY = graph.globalToLocal(evt);
          graph.zoomOut(localXY.x, localXY.y, true);
        }
      });
      items.push({
        text: '1:1',
        action: function(evt, item) {
          var localXY = graph.globalToLocal(evt);
          graph.scale = 1;
        }
      });
      items.push(Q.PopupMenu.Separator);
      var currentMode = graph.interactionMode;
      var interactons = [{
        text: '平移模式',
        value: Q.Consts.INTERACTION_MODE_DEFAULT
      }, {
        text: '框选模式',
        value: Q.Consts.INTERACTION_MODE_SELECTION
      }];
      for (var i = 0, l = interactons.length; i < l; i++) {
        var mode = interactons[i];
        if (mode.value == currentMode) {
          mode.selected = true;
        }
        mode.action = function(evt, item) {
          graph.interactionMode = item.value;
        };
        items.push(mode)
      }
      items.push(Q.PopupMenu.Separator);
      items.push({
        html: '<a href="http://qunee.com" target="_blank">Qunee' + ' - ' + Q.version + '</a>'
      });
      return items;
    }
  }
  Object.defineProperties(PopupMenu.prototype, {
    items: {
      get: function() {
        return this._items;
      },
      set: function(v) {
        this._items = v;
        this._invalidateFlag = true;
      }
    }
  });

  var _contextmenuListener = {
    onstart: function(evt, graph) {
      graph._popupmenu.hide();
    }
  }

  function getPageXY(evt) {
    if (evt.touches && evt.touches.length) {
      evt = evt.touches[0];
    }
    return {
      x: evt.pageX,
      y: evt.pageY
    };
  }

  function showMenu(evt, graph) {
    var menu = graph.popupmenu;
    var xy = getPageXY(evt);
    var x = xy.x,
      y = xy.y;

    var items = menu.getMenuItems(graph, graph.getElement(evt), evt);

    if (!items) {
      return;
    }
    menu.items = items;
    menu.showAt(x, y);

    Q.stopEvent(evt);
  }
  if (Q.isTouchSupport) {
    _contextmenuListener.onlongpress = function(evt, graph) {
      showMenu(evt, graph);
    }
  }

  Object.defineProperties(Q.Graph.prototype, {
    popupmenu: {
      get: function() {
        return this._popupmenu;
      },
      set: function(v) {
        if (this._popupmenu == v) {
          return;
        }
        this._popupmenu = v;

        if (!this._contextmenuListener) {
          this._contextmenuListener = _contextmenuListener;
          this.addCustomInteraction(this._contextmenuListener);
          this.html.oncontextmenu = function(evt) {
            showMenu(evt, this);
          }.bind(this);
        }
      }
    }
  });
  Q.PopupMenu = PopupMenu;
})(Q, jQuery);
(function(Q, $) {
  function SampleEdgeUI(edge, graph) {
    Q.EdgeUI.apply(this, arguments);
  }
  SampleEdgeUI.prototype = {
    validatePoints: function() {
      this.shape.invalidateData();
      var edge = this.$data,
        path = this.path;
      path.clear();
      var fromAgent = edge.fromAgent;
      var toAgent = edge.toAgent;
      if (!fromAgent || !toAgent) {
        return;
      }
      var fromUI = this.graph.getUI(fromAgent);
      var toUI = this.graph.getUI(toAgent);

      var fromBounds = fromUI.bodyBounds;
      var toBounds = toUI.bodyBounds;

      var p0 = fromBounds.getIntersectionPoint(fromBounds.cx, fromBounds.cy, toBounds.cx, toBounds.cy);
      var p1 = toBounds.getIntersectionPoint(toBounds.cx, toBounds.cy, fromBounds.cx, fromBounds.cy);

      path.moveTo(p0.x, p0.y);
      path.lineTo(p1.x, p1.y);
    }
  }
  Q.extend(SampleEdgeUI, Q.EdgeUI);

  if (Q.loadClassPath) {
    Q.loadClassPath(SampleEdgeUI, "Q.SampleEdgeUI");
  }
  Q.SampleEdgeUI = SampleEdgeUI;
}(Q, jQuery));
(function(Q, $) {
  function createButtonGroup(info, toolbar, scope, vertical, togglable) {
    var buttonGroup = document.createElement("div");
    buttonGroup.className = vertical ? "btn-group-vertical" : "btn-group";
    if (togglable) {
      buttonGroup.setAttribute("data-toggle", "buttons");
    }
    for (var i = 0, l = info.length; i < l; i++) {
      if (!info[i].type && togglable) {
        info[i].type = 'radio';
      }
      buttonGroup.appendChild(createGraphButton(info[i], scope)).info = info[i];
    }
    toolbar.appendChild(buttonGroup);
  }

  function createGraphButton(info, scope) {
    if (info.type == "search") {
      var div = document.createElement("div");
      div.style.display = "inline-block";
      div.style.verticalAlign = "middle";
      div.style.width = '300px';
      div.innerHTML = '<div class="input-group input-group-sm" >\
            <input type="text" class="form-control" placeholder="' + (info.placeholder || '') + '">\
                <span class="input-group-btn">\
                    <div class="btn btn-default" type="button"></div>\
                </span>\
            </div>';
      var input = div.getElementsByTagName("input")[0];
      if (info.id) {
        input.id = info.id;
      }
      var button = $(div).find('.btn')[0];
      if (info.iconClass) {
        var icon = document.createElement('div');
        $(icon).addClass(info.iconClass);
        button.appendChild(icon);
      } else if (info.name) {
        button.appendChild(document.createTextNode(" " + info.name));
      }
      info.input = input;
      if (info.search) {
        var clear = function() {
          info.searchInfo = null;
        }
        var doSearch = function(prov) {
          var value = input.value;
          if (!value) {
            clear();
            return;
          }
          if (!info.searchInfo || info.searchInfo.value != value) {
            var result = info.search(value, info);
            if (!result || !result.length) {
              clear();
              return;
            }
            info.searchInfo = {
              value: value,
              result: result
            };
          }
          doNext(prov);
        }
        var doNext = function(prov) {
          if (!(info.select instanceof Function) || !info.searchInfo || !info.searchInfo.result || !info.searchInfo.result.length) {
            return;
          }
          var searchInfo = info.searchInfo;
          var result = info.searchInfo.result;
          if (result.length == 1) {
            info.select(result[0], 0);
            return;
          }
          if (searchInfo.index === undefined) {
            searchInfo.index = 0;
          } else {
            searchInfo.index += prov ? -1 : 1;
            if (searchInfo.index < 0) {
              searchInfo.index += result.length;
            }
            searchInfo.index %= result.length;
          }
          if (info.select(result[searchInfo.index], searchInfo.index) === false) {
            info.searchInfo = null;
            doSearch();
          };
        }

        $(input).on('input', function(evt) {
          if (info.oninput) {
            info.oninput.call(scope, evt, info);
          }
        })
        input.onkeydown = function(evt) {
          if (evt.keyCode == 27 && input.value) {
            clear();
            input.value = "";
            Q.stopEvent(evt);
            if (info.oninput) {
              info.oninput.call(scope, evt, info);
            }
            return;
          }
          if (evt.keyCode == 13) {
            doSearch(evt.shiftKey);
          }
        }
        button.onclick = function(evt) {
          doSearch();
        }
      }
      return div;
    }
    if (info.type == 'file') {
      var label = document.createElement('span');
      var input = document.createElement('input');
      label.className = 'file-input btn btn-default btn-sm btn-file';
      input.setAttribute('type', 'file');
      input.className = 'btn-file';
      if (info.action) {
        input.onchange = function(evt) {
          var input = $(this),
            files = input.get(0).files;
          label = input.val().replace(/\\/g, '/').replace(/.*\//, '');
          if (files.length) {
            info.action.call(scope, files, label, evt);
          }
        };
      }
      label.appendChild(input);

      if (info.icon) {
        var icon = document.createElement('img');
        icon.src = info.icon;
        label.appendChild(icon);
      } else if (info.iconClass) {
        var icon = document.createElement('div');
        $(icon).addClass(info.iconClass);
        label.appendChild(icon);
      } else if (info.name) {
        label.appendChild(document.createTextNode(" " + info.name));
      }
      if (info.name) {
        label.setAttribute("title", info.name);
      }
      return label;
    }
    if (info.type == "input") {
      var div = document.createElement("div");
      div.style.display = "inline-block";
      div.style.verticalAlign = "middle";
      div.innerHTML = '<div class="input-group input-group-sm" style="width: 150px;">\
            <input type="text" class="form-control">\
                <span class="input-group-btn">\
                    <button class="btn btn-default" type="button"></button>\
                </span>\
            </div>';
      var input = div.getElementsByTagName("input")[0];
      var button = div.getElementsByTagName("button")[0];
      button.innerHTML = info.name;
      info.input = input;
      if (info.action) {
        button.onclick = function(evt) {
          info.action.call(scope || window.graph, evt, info);
        }
      }
      return div;
    } else if (info.type == "select") {
      var div = document.createElement("select");
      div.className = "form-control";
      var options = info.options;
      options.forEach(function(v) {
        var option = document.createElement("option");
        option.innerHTML = v;
        option.value = v;
        div.appendChild(option);
      });
      div.value = info.value;
      if (info.action) {
        div.onValueChange = function(evt) {
          info.action.call(scope || window.graph, evt, info);
        }
      }
      return div;
    }
    if (!info.type) {
      var label = document.createElement("div");
    } else {
      var label = document.createElement("label");
      var button = document.createElement("input");
      info.input = button;
      button.setAttribute('type', info.type);
      label.appendChild(button);
      if (info.selected) {
        button.setAttribute('checked', 'checked');
        if (info.type == 'radio') {
          label.className += "active";
        }
      }
    }
    label.className += "btn btn-default btn-sm";
    if (info.icon) {
      var icon = document.createElement('img');
      icon.src = info.icon;
      label.appendChild(icon);
    } else if (info.iconClass) {
      var icon = document.createElement('div');
      $(icon).addClass(info.iconClass);
      label.appendChild(icon);
    } else if (info.name) {
      label.appendChild(document.createTextNode(" " + info.name));
    }
    if (info.name) {
      label.setAttribute("title", info.name);
    }
    if (info.action) {
      (button || label).onclick = function(evt) {
        info.action.call(scope || window.graph, evt, info);
      }
    }
    return label;
  }

  function createToolbar(graph, toolbar, customButtons) {
    function getGraph() {
      return toolbar.graph;
    }

    toolbar.setGraph = function(graph) {
      var old = this.graph;
      if (old) {
        old.propertyChangeDispatcher.removeListener(onInteractionModeChange, this);
      }
      this.graph = graph;
      updateButtonStatus();
      if (graph) {
        graph.propertyChangeDispatcher.addListener(onInteractionModeChange, this);
      }
    }

    function updateButtonStatus() {
      var g = getGraph();
      var mode = g ? g.interactionMode : null;
      $(toolbar).find('.btn').each(function(index, btn) {
        if (mode && btn.info && btn.info.interactionMode == mode) {
          Q.appendClass(btn, 'active');
        } else {
          Q.removeClass(btn, 'active');
        }
      })
    }

    function onInteractionModeChange(evt) {
      if (evt.kind == 'interactionMode') {
        updateButtonStatus();
      }
    }

    function setInteractionMode(evt, info, interactionProperties) {
      var g = getGraph();
      if (!g) {
        return;
      }
      g.interactionMode = info.value;
      g.interactionProperties = interactionProperties || info;
    }

    var buttons = {
      interactionModes: [{
        name: '默认模式',
        interactionMode: Q.Consts.INTERACTION_MODE_DEFAULT,
        isToolbar: "INTERACTION_MODE_DEFAULT",
        selected: true,
        iconClass: 'q-icon toolbar-default'
      }, {
        name: '框选模式',
        interactionMode: Q.Consts.INTERACTION_MODE_SELECTION,
        isToolbar: "INTERACTION_MODE_SELECTION",
        iconClass: 'q-icon toolbar-rectangle_selection'
      }, {
        name: '浏览模式',
        interactionMode: Q.Consts.INTERACTION_MODE_VIEW,
        isToolbar: "INTERACTION_MODE_VIEW",
        iconClass: 'q-icon toolbar-pan'
      }],
      zoom: [{
        name: '放大',
        iconClass: 'q-icon toolbar-zoomin',
        isToolbar: "ZOOM_IN",
        action: function() {
          getGraph().zoomIn()
        }
      }, {
        name: '缩小',
        iconClass: 'q-icon toolbar-zoomout',
        isToolbar: "ZOOM_OUT",
        action: function() {
          getGraph().zoomOut()
        }
      }, {
        name: '1:1',
        iconClass: 'q-icon toolbar-zoomreset',
        isToolbar: "ZOOM_RESET",
        action: function() {
          getGraph().scale = 1;
        }
      }, {
        name: '纵览',
        iconClass: 'q-icon toolbar-overview',
        isToolbar: "OVERVIEW",
        action: function() {
          getGraph().zoomToOverview()
        }
      }],
      editor: [{
          name: '创建连线',
          interactionMode: Q.Consts.INTERACTION_MODE_CREATE_EDGE,
          isToolbar: "INTERACTION_MODE_CREATE_EDGE",
          iconClass: 'q-icon toolbar-edge'
        },
        //  {
        //   name: '创建曲线',
        //   interactionMode: Q.Consts.INTERACTION_MODE_CREATE_SIMPLE_EDGE,
        //   iconClass: 'q-icon toolbar-edge_flex',
        //   uiClass: FlexEdgeUI
        // },
        {
          name: '创建L型连线',
          interactionMode: Q.Consts.INTERACTION_MODE_CREATE_SIMPLE_EDGE,
          isToolbar: "INTERACTION_MODE_CREATE_HORIZONTAL",
          iconClass: 'q-icon toolbar-edge_VH',
          edgeType: Q.Consts.EDGE_TYPE_VERTICAL_HORIZONTAL
        }, {
          name: '创建多边形',
          interactionMode: Q.Consts.INTERACTION_MODE_CREATE_SHAPE,
          isToolbar: "INTERACTION_MODE_CREATE_SHAPE",
          iconClass: 'q-icon toolbar-polygon'
        }, {
          name: '创建线条',
          interactionMode: Q.Consts.INTERACTION_MODE_CREATE_LINE,
          isToolbar: "INTERACTION_MODE_CREATE_LINE",
          iconClass: 'q-icon toolbar-line'
        }
      ],
      search: {
        name: 'Find',
        placeholder: '名称',
        iconClass: 'q-icon toolbar-search',
        type: 'search',
        id: 'search_input',
        search: function(name, info) {
          var result = [];
          var reg = new RegExp(name, 'i');
          getGraph().forEach(function(e) {

            var data = e.get("data"),
              name = e.name;
            data && data.name ? name = data.name : "";
            if (name && reg.test(name) && e._className !== "Q.Edge") {
              var subNetwork = getGraph().currentSubNetwork;
              //进入子网
              if (subNetwork != null) {

                if (e.parent == subNetwork) {
                  result.push(e.id);
                }
              } else {
                if (e.parent && e.parent.enableSubNetwork) {

                } else {
                  result.push(e.id);
                }
              }
              // result.push(e.id);
            }

          });
          return result;
        },
        select: function(item) {


          item = getGraph().graphModel.getById(item);
          if (!item) {
            return false;
          }

          function resetSelect() {

            this.searchInfo.result = this.search(this.searchInfo.value);
            if (this.searchInfo.result.length) {
              this.select(this.searchInfo.result[0]);
              this.searchInfo.index = 0;
            }
          };
          if (getGraph().currentSubNetwork != null && item.agentNode.parent == null) {
            resetSelect.call(this);
            return false;
          } else {
            //返回子网上层，重新获取检索数据
            if (getGraph().currentSubNetwork == null && item.parent && item.parent.enableSubNetwork) {
              resetSelect.call(this);
              return false;
            }
          }

          getGraph().setSelection(item);
          getGraph().sendToTop(item);
          var bounds = getGraph().getUIBounds(item);
          if (bounds) {
            getGraph().centerTo(bounds.cx, bounds.cy, Math.max(2, getGraph().scale), true);
          }
        }
      },
      exportImage: {
        name: '导出图片',
        iconClass: 'q-icon toolbar-print',
        action: function() {
          Q.showExportPanel(getGraph());
        }
      }
    };

    //delete toolbar
    if (Q.TOOLBARCONFIG) {
      var config = Q.TOOLBARCONFIG;
      for (var i = 0, len = buttons.interactionModes.length; i < len; i++) {
        var mode = buttons.interactionModes[i];
        if (!config[mode.isToolbar]) {
          buttons.interactionModes.splice(i, 1);
          --len;
          --i;
        }
      };
      for (var i = 0, len = buttons.zoom.length; i < len; i++) {
        var mode = buttons.zoom[i];
        if (!config[mode.isToolbar]) {
          buttons.zoom.splice(i, 1);
          --len;
          --i;
        }
      };
      for (var i = 0, len = buttons.editor.length; i < len; i++) {
        var mode = buttons.editor[i];
        if (!config[mode.isToolbar]) {
          buttons.editor.splice(i, 1);
          --len;
          --i;
        }
      };
      if (!config.SEARCH) {
        delete buttons.search;
      }
      if (!config.EXPORTIMAGE) {
        delete buttons.exportImage;
      }
    }
    if (customButtons) {
      for (var n in customButtons) {
        buttons[n] = customButtons[n];
      }
    }

    function createButtons(buttons, toolbar, scope, vertical, togglable) {
      for (var n in buttons) {
        var info = buttons[n];
        if (Q.isArray(info)) {
          info.forEach(function(item) {
            if (item.interactionMode) {
              item.value = item.interactionMode;
              item.action = setInteractionMode;
            }
          })
          createButtonGroup(info, toolbar, scope, vertical, togglable);
          continue;
        }
        if (info.interactionMode) {
          info.value = info.interactionMode;
          info.action = setInteractionMode;
        }
        toolbar.appendChild(createGraphButton(info, scope)).info = info;
      }
    }
    createButtons(buttons, toolbar, this, false, false);

    toolbar.setGraph(graph);
    return toolbar;
  }

  Q.createToolbar = createToolbar;
  Q.createButtonGroup = createButtonGroup;
  Q.createButton = createGraphButton;
  var HierarchicLayouter = function(graph) {
    this.graph = graph;
  }

  HierarchicLayouter.prototype = {
    getLayoutResult: function(params) {
      if (!(params instanceof Object)) {
        if (Q.isNumber(params)) {
          params = {
            xGap: params,
            yGap: params
          }
        } else {
          params = {};
        }
      }
      var xGap = params.xGap || 100;
      var yGap = params.yGap || 100;
      var graph = this.graph;

      var locations = {};
      graph.graphModel.forEachByTopoBreadthFirstSearch(function(v, from, layer, index, sum) {
        xGap = Math.max(xGap, v.name.length * 8);
        if (v._layer === undefined || layer > v._layer) {
          v._layer = layer;
          return;
        }
      });
      var layers = {};
      graph.forEach(function(element) {

        if (element._layer !== undefined && element.visible != false) {

          var nodes = layers[element._layer];
          if (!nodes) {
            nodes = layers[element._layer] = [];
          }
          nodes.push(element);
        }

      });

      var layer = 0;
      while (true) {
        var nodes = layers[layer];
        if (!nodes) {
          break;
        }
        var width = (nodes.length - 1) * xGap;
        var x = -width / 2;
        var y = layer * yGap;
        nodes.forEach(function(node, i) {

          locations[node.id] = {
            node: node,
            x: x + i * xGap,
            y: y
          };
          delete node._layer;
        })
        layer++;
      }

      return locations;
    }
  }
  Q.extend(HierarchicLayouter, Q.Layouter);
  Q.HierarchicLayouter = HierarchicLayouter;
})(Q, jQuery);