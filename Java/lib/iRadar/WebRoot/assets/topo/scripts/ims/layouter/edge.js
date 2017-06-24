EdgeLayouter = function(grid, edge, layoutType) {
	this.grid = grid;
	this.edge = edge;

	edge.innerPoints = [];

	this.calculateGlobals();

	if (layoutType == 'uptodown') {

		this.pickUpLayoutForEdge();
	} else if (layoutType == 'lefttoright') {
		this.pickLeftLayoutForEdge();
	} else if (layoutType == 'orthogonal') {

		this.orthogonalLayoutForEdge();
	}

};

EdgeLayouter.prototype = {
	calculateGlobals : function() {
		// 原节点
		this.source = this.edge.source;
		// 对外连接（目标节点）
		this.target = this.edge.target;
		// 原节点(宽度的一半)
		this.sourceRelativeCenterX = this.source.w / 2;
		this.sourceRelativeCenterY = this.source.h / 2;
		// 目标节点(宽度的一半)
		this.targetRelativeCenterX = this.target.w / 2;
		this.targetRelativeCenterY = this.target.h / 2;
		// 原节点的绝对X轴坐标(原坐标+宽度的一半)

		this.sourceAbsoluteCenterX = this.source.x + this.sourceRelativeCenterX;

		this.sourceAbsoluteCenterY = this.source.y + this.sourceRelativeCenterY;
		// 目标节点的绝对Y轴坐标(原坐标+宽度的一半)
		this.targetAbsoluteCenterX = this.target.x + this.targetRelativeCenterX;
		this.targetAbsoluteCenterY = this.target.y + this.targetRelativeCenterY;

		// // 原节点的X轴绝对坐标(原坐标)
		// this.sourceAbsoluteX = this.source.x;
		// this.sourceAbsoluteY = this.source.y;
		// // 原节点的X轴绝对坐标(原坐标+节点宽度)
		// this.sourceAbsoluteX2 = this.source.x + this.source.w;
		// this.sourceAbsoluteY2 = this.source.y + this.source.h;
		//
		// this.targetAbsoluteX = this.target.x;
		// this.targetAbsoluteY = this.target.y;
		//
		// this.targetAbsoluteX2 = this.target.x + this.target.w;
		// this.targetAbsoluteY2 = this.target.y + this.target.h;

		// 查询是否存在连接过来的连线
		this.sourceJoin = this.source.isJoin();
		// 查询是否存在连接出去的连线
		this.sourceSplit = this.source.isSplit();
		this.targetJoin = this.target.isJoin();
		this.targetSplit = this.target.isSplit();

		this.backwards = this.sourceAbsoluteCenterX > this.targetAbsoluteCenterX;
	},
	pickLayoutForEdge : function() {

		// 两个节点在同一列 原节点的绝对X轴坐标(原坐标+宽度的一半)==目标节点的绝对X轴坐标(原坐标+宽度的一半)
		if (this.sourceAbsoluteCenterX == this.targetAbsoluteCenterX) {
			this.setEdgeDirectCenter();
			return;
		} else if (this.sourceAbsoluteCenterY == this.targetAbsoluteCenterY) { // 两个节点在同一行

			if (this.areCellsHorizontalFree()) {
				this.setEdgeDirectCenter();
			} else {
				this.setEdgeAroundTheCorner(true);
			}
			return;
		}
		// 目标节点在原节点后下方（ 原节点X Y轴坐标 小于 目标节点）
		if (this.sourceAbsoluteCenterX <= this.targetAbsoluteCenterX
				&& this.sourceAbsoluteCenterY <= this.targetAbsoluteCenterY) {
			// target is right under
			if (this.sourcejoin && this.sourceSplit) {
				this.setEdgeStepRight();
				return;
			} else if (this.sourceSplit) {
				this.setEdge90DegreeRightUnderAntiClockwise();
				return;
			} else if (this.targetJoin) {
				this.setEdge90DegreeRightUnderClockwise();
				return;
			}
		} else if (this.sourceAbsoluteCenterX <= this.targetAbsoluteCenterX
				&& this.sourceAbsoluteCenterY > this.targetAbsoluteCenterY) {

			// target is right above
			if (this.sourcejoin && this.sourceSplit) {
				this.setEdgeStepRight();
				return;
			} else if (this.sourceSplit) {
				this.setEdge90DegreeRightAboveClockwise();
				return;
			} else if (this.targetJoin) {
				this.setEdge90DegreeRightAboveAntiClockwise();
				return;
			}
		}

		if (this.sourceJoin && sourceSplit && (!this.backwards)) {
			this.setEdgeStepRight();
			return;
		}

		if (this.sourceJoin && sourceSplit) {
			this.setEdgeAroundTheCorner(true);
			return;
		}

		this.setEdgeDirectCenter();
	},
	pickLeftLayoutForEdge : function() {

		// 两个节点在同一列 原节点的绝对X轴坐标(原坐标+宽度的一半)==目标节点的绝对X轴坐标(原坐标+宽度的一半)
		if (this.sourceAbsoluteCenterX == this.targetAbsoluteCenterX) {
			this.setEdgeDirectCenter();
			return;
		} else if (this.sourceAbsoluteCenterY == this.targetAbsoluteCenterY) { // 两个节点在同一行

			if (this.areCellsHorizontalFree()) {
				this.setEdgeDirectCenter();
			} else {
				this.setEdgeAroundTheCorner(true);
			}
			return;
		}

		// 目标节点在原节点后下方（ 原节点X Y轴坐标 小于 目标节点）
		if (this.sourceAbsoluteCenterX <= this.targetAbsoluteCenterX
				&& this.sourceAbsoluteCenterY <= this.targetAbsoluteCenterY) {

			if (this.sourceSplit) {
				this.setEdge90DegreeRightAboveClockwise();
				return;
			} else if (this.targetJoin) {
				this.setEdge90DegreeRightAboveAntiClockwise();
				return;
			}
		} else if (this.sourceAbsoluteCenterX <= this.targetAbsoluteCenterX
				&& this.sourceAbsoluteCenterY > this.targetAbsoluteCenterY) {

			if (this.sourceSplit) {
				this.setEdge90DegreeRightAboveClockwise();
				return;
			} else if (this.targetJoin) {
				this.setEdge90DegreeRightAboveAntiClockwise();
				return;
			}
		}

		this.setEdgeDirectCenter();
	},
	/**
	 * 从上到下布局调用
	 */
	pickUpLayoutForEdge : function() {

		// 两个节点在同一列 原节点的绝对X轴坐标(原坐标+宽度的一半)==目标节点的绝对X轴坐标(原坐标+宽度的一半)
		if (this.sourceAbsoluteCenterX == this.targetAbsoluteCenterX) {
			this.setEdgeDirectCenter();
			return;
		} else if (this.sourceAbsoluteCenterY == this.targetAbsoluteCenterY) { // 两个节点在同一行

			if (this.areCellsHorizontalFree()) {
				this.setEdgeDirectCenter();
			} else {
				this.setEdgeAroundTheCorner(true);
			}
			return;
		}

		// 原点在目标节点左上方或者右下方
		if (this.sourceAbsoluteCenterX <= this.targetAbsoluteCenterX
				&& this.sourceAbsoluteCenterY <= this.targetAbsoluteCenterY) {
			// 不存在外部主动连接过来的节点(说明是首行节点左上方)
			if (this.source.getIncomingLinks().length == 0) {// --

				this.edge.innerPoints = [[this.sourceAbsoluteCenterX,
						this.targetAbsoluteCenterY]];
			} else {// --
				this.edge.innerPoints = [[this.targetAbsoluteCenterX,
						this.sourceAbsoluteCenterY + 10]];
			}

			return;
		} else if (this.sourceAbsoluteCenterX >= this.targetAbsoluteCenterX
				&& this.sourceAbsoluteCenterY <= this.targetAbsoluteCenterY) {// 左下方或者右上方

			// 不存在外部主动连接过来的节点(说明是首行节点右上方)
			if (this.source.getIncomingLinks().length == 0) {
				this.edge.innerPoints = [[this.sourceAbsoluteCenterX,
						this.targetAbsoluteCenterY]];
			} else {// --
				this.edge.innerPoints = [[this.targetAbsoluteCenterX,
						this.sourceAbsoluteCenterY + 10]];
			}
		}

		this.setEdgeDirectCenter();
	},
	orthogonalLayoutForEdge : function() {
		
		if (this.sourceSplit) {
			// this.setEdge90DegreeRightAboveClockwise();

			this.edge.innerPoints = [[this.sourceAbsoluteCenterX,
					this.targetAbsoluteCenterY]];
			return;
		} else if (this.targetJoin) {
			this.setEdge90DegreeRightAboveAntiClockwise();
			return;
		}
		this.edge.innerPoints = [[this.sourceAbsoluteCenterX,
				this.targetAbsoluteCenterY]];
	},
	areCellsHorizontalFree : function() {
		var fromCell = null;
		var toCell = null;
		// 原节点X坐标<目标节点X轴坐标（证明同一行里原节点在目标节点之前）
		if (this.sourceAbsoluteCenterX < this.targetAbsoluteCenterX) {
			// 原节点单元格
			fromCell = this.grid.getCellOfItem(this.source);
			// 目标节点单元格
			toCell = this.grid.getCellOfItem(this.target);
		} else {
			fromCell = this.grid.getCellOfItem(this.target);
			toCell = this.grid.getCellOfItem(this.source);
		}

		fromCell = fromCell.getNextCell();
		while (fromCell != toCell) {
			if (fromCell == null || fromCell.isFilled()) {
				return false;
			}
			fromCell = fromCell.getNextCell();
		}

		return true;
	},
	/**
	 * 两节点在同一列 直线连接
	 */
	setEdgeDirectCenter : function() {

		// 原节点的绝对X轴坐标(原坐标+宽度的一半)
		var boundsMinX = Math.min(this.sourceAbsoluteCenterX,
				this.targetAbsoluteCenterX);
		var boundsMinY = Math.min(this.sourceAbsoluteCenterY,
				this.targetAbsoluteCenterY);
		var boundsMaxX = Math.max(this.sourceAbsoluteCenterX,
				this.targetAbsoluteCenterX);
		var boundsMaxY = Math.max(this.sourceAbsoluteCenterY,
				this.targetAbsoluteCenterY);
	},
	/**
	 * 上方 顺时针90°
	 */
	setEdge90DegreeRightAboveClockwise : function() {
		this.edge.innerPoints = [[this.sourceAbsoluteCenterX + 10,
				this.targetAbsoluteCenterY]];
	},
	/**
	 * 上方 逆时针90°右转
	 */
	setEdge90DegreeRightAboveAntiClockwise : function() {
		this.edge.innerPoints = [[this.targetAbsoluteCenterX,
				this.sourceAbsoluteCenterY]];
	},

	setEdgeAroundTheCorner : function(down) {

		var height = Math.max(this.source.h / 2, this.target.h / 2) + 20;

		if (down) {
			height *= -1;
		}

		this.edge.innerPoints = [
				[this.sourceAbsoluteCenterX,
						this.sourceAbsoluteCenterY + height],
				[this.targetAbsoluteCenterX,
						this.sourceAbsoluteCenterY + height]];
	}
};