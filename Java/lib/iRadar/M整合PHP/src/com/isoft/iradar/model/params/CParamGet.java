package com.isoft.iradar.model.params;

import static com.isoft.iradar.inc.Defines.API_OUTPUT_REFER;

import java.util.HashMap;
import java.util.Map;

public class CParamGet extends CParamWrapper {
	
	public final static String clazzNameFormat = CParamGet.class.getName().replaceFirst("CParam", "%s");

	private static final long serialVersionUID = 1L;
	private Boolean editable;
	private Boolean nopermissions;
	
	// filter
	private Map<String, Object[]> filter;
	private Map<String, String> search;
	private Boolean searchByAny;
	private Boolean startSearch;
	private Boolean excludeSearch;
	private Boolean searchWildcardsEnabled;
	private Long timeFrom;
	private Long timeTill;

	// output
	private Object output = API_OUTPUT_REFER;
	private Integer count;
	private String pattern;
	private String extendPattern;

	private Object countOutput;
	private Object groupCount;
	private Boolean preserveKeys;

	private String[] sortfield;
	private String[] sortorder;

	private Integer limit;
	private Integer limitSelects;

	public boolean getEditable() {
		return editable != null ? editable : false;
	}
	
	public Boolean getEditable(boolean canBeNull) {
		return editable;
	}

	public void setEditable(Boolean editable) {
		this.editable = editable;
	}

	public boolean getNopermissions() {
		return nopermissions != null ? nopermissions : false;
	}

	public void setNopermissions(Boolean nopermissions) {
		this.nopermissions = nopermissions;
	}

	public Map<String,Object[]> getFilter() {
		return filter;
	}

	public void setFilter(Map<String,Object[]> filter) {
		this.filter = filter;
	}
	
	public <T> void setFilter(String criteric, T ... critericValues) {
		if (this.filter == null) {
			this.filter = new HashMap();
		}
		if(critericValues !=null && critericValues.length==0){
			critericValues = null;
		}
		this.filter.put(criteric, critericValues);
	}

	public Map<String, String> getSearch() {
		return search;
	}

	public void setSearch(Map<String,String> search) {
		this.search = search;
	}
	
	public void setSearch(String search, String searchValue) {
		if (this.search == null) {
			this.search = new HashMap();
		}
		this.search.put(search, searchValue);
	}

	public Boolean getSearchByAny() {
		return searchByAny;
	}

	public void setSearchByAny(Boolean searchByAny) {
		this.searchByAny = searchByAny;
	}

	public Boolean getStartSearch() {
		return startSearch;
	}

	public void setStartSearch(Boolean startSearch) {
		this.startSearch = startSearch;
	}

	public Boolean getExcludeSearch() {
		return excludeSearch;
	}

	public void setExcludeSearch(Boolean excludeSearch) {
		this.excludeSearch = excludeSearch;
	}

	public Boolean getSearchWildcardsEnabled() {
		return searchWildcardsEnabled;
	}

	public void setSearchWildcardsEnabled(Boolean searchWildcardsEnabled) {
		this.searchWildcardsEnabled = searchWildcardsEnabled;
	}

	public Object getOutput() {
		return output;
	}

	public void setOutput(Object output) {
		this.output = output;
	}

	public Integer getCount() {
		return count;
	}

	public void setCount(Integer count) {
		this.count = count;
	}

	public String getPattern() {
		return pattern;
	}

	public void setPattern(String pattern) {
		this.pattern = pattern;
	}

	public String getExtendPattern() {
		return extendPattern;
	}

	public void setExtendPattern(String extendPattern) {
		this.extendPattern = extendPattern;
	}

	public Object getCountOutput() {
		return countOutput;
	}

	public void setCountOutput(Object countOutput) {
		this.countOutput = countOutput;
	}

	public Object getGroupCount() {
		return groupCount;
	}

	public void setGroupCount(Object groupCount) {
		this.groupCount = groupCount;
	}
	
	public Boolean getPreserveKeys() {
		return preserveKeys;
	}
	
	public boolean isPreserveKeys() {
		return this.getPreserveKeys()!=null && this.getPreserveKeys();
	}

	public void setPreserveKeys(Boolean preserveKeys) {
		this.preserveKeys = preserveKeys;
	}

	public String[] getSortfield() {
		return sortfield;
	}

	public void setSortfield(String... sortfield) {
		this.sortfield = sortfield;
	}

	public String[] getSortorder() {
		return sortorder;
	}

	public void setSortorder(String... sortorder) {
		if (sortorder != null && sortorder.length > 0) {
			this.sortorder = new String[sortorder.length];
			for(int i=0;i<sortorder.length;i++){
				if(sortorder[i]!=null){
					this.sortorder[i] = sortorder[i].toUpperCase();
				}
			}
		}
	}

	public Integer getLimit() {
		return limit;
	}

	public void setLimit(Integer limit) {
		this.limit = limit;
	}

	public Integer getLimitSelects() {
		return limitSelects;
	}

	public void setLimitSelects(Integer limitSelects) {
		this.limitSelects = limitSelects;
	}

	public Long getTimeFrom() {
		return timeFrom;
	}

	public void setTimeFrom(Long timeFrom) {
		this.timeFrom = timeFrom;
	}

	public Long getTimeTill() {
		return timeTill;
	}

	public void setTimeTill(Long timeTill) {
		this.timeTill = timeTill;
	}

}
