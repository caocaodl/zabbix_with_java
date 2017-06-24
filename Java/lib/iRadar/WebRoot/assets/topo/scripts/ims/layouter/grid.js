/**
 * 网格结构
 */
Grid = function() {
	// 单元格
	var cell = new Cell();
	// 行
	var row = new Row();

	row.grid = this;
	// 添加列
	row.addCell(cell);

	this.rows = [row];
	// 开始列
	this.startCell = cell;
	// 列总数
	this.colCount = 1;
	// 行总数
	this.rowCount = this.rows.length;
};

Grid.prototype = {
	addFirstRow : function() {
		var row = new Row();
		row.grid = this;
		for (var i = 0; i < this.rolCount; i++) {
			var cell = new Cell();
			row.addCell(cell);
		}
		this.rows.unshift(row);

		this.rowCount = this.rows.length;
	},
    /**
     * 最后新增一行
     */
	addLastRow : function() {
		var row = new Row();
		row.grid = this;
		for (var i = 0; i < this.colCount; i++) {
			row.addCell(new Cell());
		}
		this.rows.push(row);

		this.rowCount = this.rows.length;
	},
	/**
	 * 在行的末尾添加一个单元格
	 */
	addLastCol : function() {
		for (var i = 0; i < this.rowCount; i++) {
			var row = this.rows[i];

			row.addCell(new Cell());
		}

		this.colCount++;
	},
	/**
	 * 查询某行值为空的列
	 */
	findCellByRowIndex:function(rowIndex){
	  var row=this.rows[rowIndex];
	  //默认找到
	  var bo=true;
	   	for (var j = 0; j < this.colCount; j++) {
				var cell = row.cells[j];
				//不存在
				if (!cell.value) {
					bo=false;
					return row.cells[j];
			           		
				}				
			}
			if(bo){
				this.addLastCol();
				return row.cells[this.colCount-1];
			}
		
	},
	/**
	 * 查询值为空的行
	 */
	findRowByCellIndex:function(cellIndex){
	  //默认找到
	  var bo=true;
	   	for (var j = 0; j < this.rowCount; j++) {
	   		      var row = this.rows[j];
	   		      //每一行固定的列
				  var cell = row.cells[cellIndex];
				//不存在
				if (!cell.value) {
					bo=false;
					return row.cells[cellIndex];
			           		
				}				
			}
			if(bo){
				this.addLastRow();
				return this.rows[this.rowCount-1].cells[cellIndex];
				
			}
		
	},
	/**
	 * 从单元格获取节点
	 * 
	 * @param {}
	 *            elem
	 * @return {}
	 */
	getCellOfItem : function(elem) {
		for (var i = 0; i < this.rowCount; i++) {
			var row = this.rows[i];
			for (var j = 0; j < this.colCount; j++) {
				var cell = row.cells[j];
				if (cell.value == elem) {
					return cell;
				}
			}
		}
		return null;
	},

	pack : function() {
		var changed = false;
		do {
			changed = false;
			for (var i = 0; i < this.rows.length; i++) {
				var row = this.rows[i];
				changed |= row.tryInterleaveWith(row.getPrevRow());
			}
			for (var i = 0; i < this.rows.length; i++) {
				var row = this.rows[i];
				changed |= row.tryInterleaveWith(row.getNextRow());
			}
		} while (changed);
	},

	info : function() {
		var value = '';
		for (var i = 0; i < this.rows.length; i++) {
			var row = this.rows[i];
			for (var j = 0; j < row.cells.length; j++) {
				var cell = row.cells[j];
				var id = '[    ]';
				if (cell.isFilled()) {
					id = cell.value.id;
				} else if (cell.packable === false) {
					id = '[ p  ]';
				}
				value += id;
			}
			value += '\n'
		}
		return value;
	}
};
/**
 * 单元格（列）
 */
Cell = function() {
	this.packable = true;
};

Cell.prototype = {
	isFilled : function() {
		return typeof this.value != 'undefined' && this.value != null;
	},

	isUnpackable : function() {
		return this.isFilled() || (this.packable === false);
	},

	setPackable : function(packable) {
		this.packable = packable;
	},

	getRowIndex : function() {
		return this.row.getIndex();
	},
	/**
	 * 返回节点所在的单元格的索引位置
	 * 
	 * @return {}
	 */
	getColIndex : function() {
		for (var i = 0; i < this.row.cells.length; i++) {
			if (this.row.cells[i] == this) {
				return i;
			}
		}
	},
	/**
	 * 同一行的单元格之后的单元格
	 * 
	 * @return {}
	 */
	after : function() {
		// 该节点所在单元格的位置
		var colIndex = this.getColIndex();
		// 判断是否为一行的最后一个单元格
		if (colIndex == this.row.cells.length - 1) {
			this.grid.addLastCol();
		}

		return this.row.cells[colIndex + 1];

	},

	above : function() {
		var rowIndex = this.getRowIndex();
		var colIndex = this.getColIndex();
		if (rowIndex == 0) {
			this.row.insertRowAbove();
		}
		return this.grid.rows[rowIndex - 1].cells[colIndex];
	},

	beneath : function() {
		var rowIndex = this.getRowIndex();
		var colIndex = this.getColIndex();
		if (rowIndex == this.grid.rowCount - 1) {
			this.row.insertRowBeneath();
		}
		return this.grid.rows[rowIndex + 1].cells[colIndex];
	},
	/**
	 * 获取上个单元格
	 * 
	 * @return {}
	 */
	getPrevCell : function() {
		var index = this.getColIndex();
		return this.row.cells[index - 1];
	},
	/**
	 * 获取下个单元格
	 * 
	 * @return {}
	 */
	getNextCell : function() {
		var index = this.getColIndex();
		return this.row.cells[index + 1];
	}
};
/**
 * 单元格（行）
 */
Row = function() {
	this.cells = [];
};

Row.prototype = {
	/**
	 * 添加单元格(列)
	 * 
	 * @param {}
	 *            cell
	 */
	addCell : function(cell) {
		cell.row = this;
		cell.grid = this.grid;
		this.cells.push(cell);
	},

	getIndex : function() {
		for (var i = 0; i < this.grid.rows.length; i++) {
			if (this.grid.rows[i] == this) {
				return i;
			}
		}
	},
	/**
	 * 在原先行的下方 在新增一行
	 */
	insertRowBeneath : function() {
		var row = new Row();
		row.grid = this.grid;

		for (var i = 0; i < this.grid.colCount; i++) {
			row.addCell(new Cell());
		}

		var rowIndex = this.getIndex();
		var rows = this.grid.rows;

		if (rowIndex == rows.length - 1) {
			rows.push(row);
		} else {
			rows.splice(rowIndex + 1, 0, row);
		}

		this.grid.rowCount = rows.length;
	},
	/**
	 * 在原先行的上面在新增一行
	 */
	insertRowAbove : function() {
		var row = new Row();
		row.grid = this.grid;

		for (var i = 0; i < this.grid.colCount; i++) {
			row.addCell(new Cell());
		}

		var rowIndex = this.getIndex();
		var rows = this.grid.rows;
		// 插入新行
		if (rowIndex == 0) {
			rows.unshift(row);
		} else {
			rows.splice(rowIndex, 0, row);
		}

		this.grid.rowCount = rows.length;
	},

	getPrevRow : function() {
		var index = this.getIndex();
		if (index > 0) {
			return this.grid.rows[index - 1];
		} else {
			return null;
		}
	},

	getNextRow : function() {
		var index = this.getIndex();
		if (index < this.grid.rows.length) {
			return this.grid.rows[index + 1];
		} else {
			return null;
		}
	},

	tryInterleaveWith : function(other) {
		if (!this.isInterleaveWith(other)) {
			return false;
		}

		for (var i = 0; i < this.cells.length; i++) {
			var cell = this.cells[i];
			var otherCell = other.cells[i];

			if (cell.isFilled()) {
				other.cells[i] = cell;
			} else if (cell.isUnpackable()) {
				otherCell.setPackable(false);
			}
		}

		this._remove();

		return true;
	},

	isInterleaveWith : function(other) {
		if (other == null || other == this) {
			return false;
		} else if (other.getPrevRow() != this && other.getNextRow() != this) {
			return false;
		}
		for (var i = 0; i < this.cells.length; i++) {
			var cell = this.cells[i];
			var otherCell = other.cells[i];
			if (cell.isUnpackable() && otherCell.isUnpackable()) {
				return false;
			}
		}
		return true;
	},

	_remove : function() {
		var index = this.getIndex();
		this.grid.rows.splice(index, 1);
		this.grid.rowCount--;
	}
};