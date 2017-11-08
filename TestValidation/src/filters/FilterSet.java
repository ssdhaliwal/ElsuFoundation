package filters;

import java.io.*;
import java.util.*;

import javax.xml.bind.annotation.*;

@XmlRootElement
public class FilterSet implements Serializable {

	private static final long serialVersionUID = -8944655860156088523L;

	private List<FilterType> filters;
	private String logic = "OR";

	public FilterSet() { // needed for JAXB
		initialize();
	}

	public FilterSet(List<FilterType> pFilters) {
		initialize();

		setFilters(pFilters);
	}

	public FilterSet(FilterType pFilter) {
		initialize();

		addFilters(pFilter);
	}

	public FilterSet(String pColumn, String pOperator, List<String> pValues, Boolean pNegate, Boolean pNumeric) {
		initialize();

		addFilter(pColumn, pOperator, pValues, pNegate, pNumeric);
	}

	public FilterSet(String pColumn, String pOperator, String pValue, Boolean pNegate, Boolean pNumeric) {
		initialize();

		addFilter(pColumn, pOperator, pValue, pNegate, pNumeric);
	}

	private void initialize() {
		filters = new ArrayList<FilterType>();
	}

	public void clearFilters() {
		this.filters.clear();
	}

	public void clearFilters(String pColumn) {
		for (FilterType filter : getFilters()) {
			if (filter.getColumn().equalsIgnoreCase(pColumn)) {
				this.getFilters().remove(filter);
			}
		}
	}

	public List<FilterType> getFilters() {
		return this.filters;
	}

	public void setFilters(List<FilterType> pFilters) {
		this.filters = pFilters;
	}

	public void addFilters(FilterType pFilter) {
		clearFilters(pFilter.getColumn());
		this.filters.add(pFilter);
	}

	public void appendFilters(FilterType pFilter) {
		this.filters.add(pFilter);
	}

	public FilterType addFilter(String pColumn, String pOperator, List<String> pValues, Boolean pNegate,
			Boolean pNumeric) {
		clearFilters(pColumn);
		FilterType result = new FilterType(pColumn, pOperator, pValues, pNegate, pNumeric);

		if (this.filters.add(result)) {
			return result;
		} else {
			return null;
		}
	}

	public FilterType appendFilter(String pColumn, String pOperator, List<String> pValues, Boolean pNegate,
			Boolean pNumeric) {
		FilterType result = new FilterType(pColumn, pOperator, pValues, pNegate, pNumeric);

		if (this.filters.add(result)) {
			return result;
		} else {
			return null;
		}
	}

	public FilterType addFilter(String pColumn, String pOperator, String pValue, Boolean pNegate, Boolean pNumeric) {
		clearFilters(pColumn);
		FilterType result = new FilterType(pColumn, pOperator, pValue, pNegate, pNumeric);

		if (this.filters.add(result)) {
			return result;
		} else {
			return null;
		}
	}

	public FilterType appendFilter(String pColumn, String pOperator, String pValue, Boolean pNegate, Boolean pNumeric) {
		FilterType result = new FilterType(pColumn, pOperator, pValue, pNegate, pNumeric);

		if (this.filters.add(result)) {
			return result;
		} else {
			return null;
		}
	}

	public String getLogic() {
		return logic;
	}

	public void setLogic(String pLogic) {
		this.logic = pLogic;
	}

	@Override
	public String toString() {
		StringBuilder result = new StringBuilder();

		result.append("{");
		result.append("filters:" + filters.toString() + ", ");
		result.append("logic: " + logic);
		result.append("}");

		return result.toString();
	}

	public String toSQLWhereClause() {
		String result = "";
		Boolean firstTime = true;

		if (getFilters().size() > 0) {
			result = "(";

			for (FilterType fi : getFilters()) {
				result += (firstTime ? "" : (" " + getLogic() + " ")) + fi.toSQLWhereClause();
				firstTime = false;
			}

			result += ")";
		}

		return result;
	}
}
