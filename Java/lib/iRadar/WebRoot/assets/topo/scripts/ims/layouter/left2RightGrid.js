LeftToRightGridLayouter = function(diagram, sortedIds) {
	this.diagram = diagram;
	this.sortedIds = sortedIds;

};

LeftToRightGridLayouter.prototype = {
	/**
	 * 
	 */
	doLayout : function() {
		// 初始化网格对象
		this.grid = new Grid();

		this.layoutElements();

		this.calcGeometry(this.grid);
		this.writeGeometry(this.grid);

		this.diagram.updateModel();
	},
	/**
	 * 计算节点在网格中的排列
	 */
	layoutElements : function() {

		for (var i = 0; i < this.sortedIds.length; i++) {

			var sortedId = this.sortedIds[i];
			// 根据ID获取节点
			var currentElement = this.diagram.nodeMap[sortedId];
			// 获取主动连接该节点的外部节点集合
			var precedingElements = currentElement.getPrecedingElements();

			var cellOfElement = this.placeElement(currentElement,
					precedingElements);
			// 查看是否有外部节点连接过来

			// 判断对外连接节点是否大于1
			if (currentElement.getOutgoingLinks().length) {
				this.prelayoutSuccessors(currentElement, cellOfElement);
			}
		}
	},
	/**
	 * 排序节点位置
	 * 
	 * @param {}
	 *            currentElement 节点
	 * @param {}
	 *            precedingElements 连接该节点的外部节点集合
	 * @return {}
	 */
	placeElement : function(currentElement, precedingElements) {

		var newCell = null;
		// 开始节点(连接过来的外部节点==0)
		if (precedingElements.length == 0) {
			// 新增一行
			this.grid.addLastRow();
			newCell = this.grid.findRowByCellIndex(0);
		} else {
			var leftCell = null;
			// 判断单元格是否已经存储该节点
			newCell = this.grid.getCellOfItem(currentElement);

		}
		// 单元格赋值
		newCell.value = currentElement;
		return newCell;
	},

	prelayoutSuccessors : function(currentElement, cellOfElement) {
		var outLinks = currentElement.getFollowingElements();

		for (var i = 0; i < outLinks.length; i++) {
			var newCell = null;
			// 对外连接第一个节点是否已经存入单元格
			var outLink = this.grid.getCellOfItem(outLinks[0]);

			// 未存入
			if (!outLink) {
				// 在行的末尾插入新列
				cellOfElement.grid.addLastCol();
				// 新列
				newCell = cellOfElement.getNextCell();

				newCell.value = outLinks[0];
			} else {
				// 检测是否已经存入表格
				newCell = this.grid.getCellOfItem(outLinks[i]);
				// 不存在
				if (!newCell) {
					// 第一个外连接存入的列索引
					var cellIndex = outLink.getColIndex();
					// 获取该行不存在值的单元格

					newCell = this.grid.findRowByCellIndex(cellIndex);
					newCell.value = outLinks[i];
				}
			}

		}

	},

	calcGeometry : function(grid) {
		grid.pack();

		var heightOfRow = [];
		for (var i = 0; i < grid.rowCount; i++) {
			heightOfRow.push(0);
		}
		var widthOfColumn = [];
		for (var i = 0; i < grid.colCount; i++) {
			widthOfColumn.push(0);
		}

		for (var i = 0; i < grid.rowCount; i++) {
			var row = grid.rows[i];

			for (var j = 0; j < grid.colCount; j++) {
				var cell = row.cells[j];
				if (cell.isFilled()) {
					var elem = cell.value;
					widthOfColumn[j] = Math.max(widthOfColumn[j], elem.w + 30);
					heightOfRow[i] = Math.max(heightOfRow[i], elem.h + 30);
				}
			}
		}

		this.heightOfRow = heightOfRow;
		this.widthOfColumn = widthOfColumn;

		this.totalWidth = 0;
		this.totalHeight = 0;
		for (var i = 0; i < grid.colCount; i++) {
			this.totalWidth += widthOfColumn[i];
		}
		for (var i = 0; i < grid.rowCount; i++) {
			this.totalHeight += heightOfRow[i];
		}
		this.grid.totalWidth=this.totalWidth;
		this.grid.totalHeight=this.totalHeight;
	},

	writeGeometry : function(grid) {
		var x = 0;
		var y = 0;

		for (var i = 0; i < grid.rowCount; i++) {
			var row = grid.rows[i];

			var cellHeight = this.heightOfRow[i];

			for (var j = 0; j < grid.colCount; j++) {
				var cell = row.cells[j];

				var cellWidth = this.widthOfColumn[j];

				if (cell.isFilled()) {
					var elem = cell.value;

					var newX = x + cellWidth / 2 - elem.w / 2;
					var newY = y + cellHeight / 2 - elem.h / 2;
					elem.x = newX;
					elem.y = newY;
					// 多个外连接连接到一个节点
					if (elem.isJoin()) {						
						elem.y = cell.grid.totalHeight / 2 - elem.h / 2;
					}
				}
				x += cellWidth;
			}
			x = 0;
			y += cellHeight;
		}
	}
};