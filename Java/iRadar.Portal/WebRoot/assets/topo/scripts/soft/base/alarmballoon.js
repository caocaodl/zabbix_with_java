$(function() {
    if (!Q.Element.prototype.initAlarmBalloon) {
        Q.Element.prototype.initAlarmBalloon = function() {
            var alarmUI = new Q.LabelUI();
            alarmUI.position = Q.Position.CENTER_TOP;
            alarmUI.anchorPosition = Q.Position.LEFT_BOTTOM;
            alarmUI.border = 1;
            alarmUI.backgroundGradient = Q.Gradient.LINEAR_GRADIENT_VERTICAL;
            alarmUI.padding = new Q.Insets(2, 5);
            alarmUI.showPointer = true;
            alarmUI.offsetY = -10;
            alarmUI.offsetX = -10;
            alarmUI.rotatable = false;
            alarmUI.alarm=true;
            this._alarmBalloon = alarmUI;
        }
        Q.Element.prototype._checkAlarmBalloon = function() {
            if (!this.alarmLabel || !this.alarmColor) {
                if (this._alarmBalloon) {
                    this.removeUI(this._alarmBalloon);
                }
                return;
            }
            if (!this._alarmBalloon) {
                this.initAlarmBalloon();
            }
            this._alarmBalloon.data = this.alarmLabel;
            this._alarmBalloon.backgroundColor = this.alarmColor;
            if (this.addUI(this._alarmBalloon) === false) {
                this.invalidate();
            }
        }
        Object.defineProperties(Q.Element.prototype, {
            alarmLabel: {
                get: function() {
                    return this._alarmLabel;
                },
                set: function(label) {
                    if (this._alarmLabel == label) {
                        return;
                    }
                    this._alarmLabel = label;
                    this._checkAlarmBalloon();
                }
            },
            alarmColor: {
                get: function() {
                    return this._alarmColor;
                },
                set: function(color) {
                    if (this._alarmColor == color) {
                        return;
                    }
                    this._alarmColor = color;
                    // this.setStyle(Q.Styles.RENDER_COLOR, color);
                    this._checkAlarmBalloon();
                }
            }
        })
    }
   
});