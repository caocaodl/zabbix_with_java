package com.isoft.imon.topo.util;

import java.util.ArrayList;
import java.util.List;

/**
 * 通用分页工具类，该工具类分页指定泛型的List对集合
 * 
 */
public class Page<E> {
	/**
	 * 每页显示的记录数
	 */
	private int pageRecords;
	/**
	 * 总记录数
	 */
	private int totalRecord;
	/**
	 * 分页切割的启始点
	 */
	private int startIndex;
	/**
	 * 分页切割的结束点
	 */
	private int endIndex;
	/**
	 * 总页数
	 */
	private int totalPage;
	/**
	 * 当前页数
	 */
	private int currentPage;
	public void setCurrentPage(int currentPage) {
		this.currentPage = currentPage;
	}

	/**
	 * 总记录集合
	 */
	private List<E> totalList;
	public Page(List<E> totalList) {
		this.totalList = totalList;
	}
	/**
	 * 初始化该分页对象
	 */
	private void init() {
		if (null != totalList) {
			totalRecord = totalList.size();
			if (totalRecord % this.pageRecords == 0) {
				this.totalPage = totalRecord / this.pageRecords;
			} else {
				this.totalPage = totalRecord / this.pageRecords + 1;
			}
		}
	}
	/**
	 * 得到分页后的数据
	 * 
	 * @return 分页数据
	 */
	public List<E> getPage(int currentPage) {
		this.currentPage = currentPage;
		if (currentPage <= 0) {
			this.currentPage = 1;
		}
		if (currentPage >= this.totalPage) {
			this.currentPage = this.totalPage;
		}
		List<E> subList = new ArrayList<E>();
		if (null != this.totalList) {
			subList.addAll(this.totalList.subList(getStartIndex(),
					getEndIndex()));
		}
		return subList;
	}

	/**
	 * 设置每页显示的记录条数,如果不设置则默认为每页显示30条记录
	 * 
	 * @param pageRecords
	 *            每页显示的记录条数(值必需介于10~100之间)
	 */
	public void setPageRecords(int pageRecords) {
		if (pageRecords >= 10 && pageRecords <= 100) {
			this.pageRecords = pageRecords;
			init();
		}
	}

	public int getStartIndex() {
		if (null == this.totalList) {
			return 0;
		}
		this.startIndex = (getCurrentPage() - 1) * this.pageRecords;
		if (startIndex > totalRecord) {
			startIndex = totalRecord;
		}
		if (startIndex < 0) {
			startIndex = 0;
		}
		return startIndex;
	}

	public int getEndIndex() {
		if (null == this.totalList) {
			return 0;
		}
		endIndex = getStartIndex() + this.pageRecords;
		if (endIndex < 0) {
			endIndex = 0;
		}
		if (endIndex < getStartIndex()) {
			endIndex = getStartIndex();
		}
		if (endIndex > this.totalRecord) {
			endIndex = this.totalRecord;
		}
		return endIndex;
	}

	/***
	 * 获取总页数
	 * 
	 * @return
	 */
	public int getTotalPage() {
		return totalPage;
	}

	/**
	 * 获取List集合中的总条数
	 * 
	 * @return
	 */
	public int getTotalRecord() {
		return totalRecord;
	}

	public boolean isEndPage() {
		return this.currentPage == this.totalPage;
	}

	/**
	 * 获取下一页的页数
	 * 
	 * @return 下一页的页数
	 */
	public int getNextPage() {
		int nextPage = this.currentPage + 1;
		if (nextPage > this.totalPage) {
			nextPage = this.totalPage;
		}
		if (nextPage <= 0) {
			nextPage = 1;
		}
		return nextPage;
	}

	/**
	 * 获取上一页的页数
	 * 
	 * @return 上一页的页数
	 */
	public int getPrivyPage() {
		int privyPage = this.currentPage - 1;
		if (privyPage > this.totalPage) {
			privyPage = this.totalPage;
		}
		if (privyPage <= 0) {
			privyPage = 1;
		}

		return privyPage;
	}

	/**
	 * 获取当前页页数
	 * 
	 * @return
	 */
	public int getCurrentPage() {
		return currentPage;
	}
}