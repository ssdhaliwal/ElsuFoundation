package filters;

import java.io.*;
import java.text.*;
import java.util.*;

import javax.xml.bind.annotation.*;

@XmlRootElement
public class FilterType implements Serializable {

	private static final long serialVersionUID = -1582273408533163404L;

	private String column;
	private String operator;
	private List<String> values;
	private Boolean negate;
	private Boolean numeric;

	private String logic = "OR";
	private List<FilterSet> filterSets;

	public FilterType() { // needed for JAXB
		initialize();
	}

	public FilterType(String pColumn, String pOperator, String pValue, Boolean pNegate, Boolean pNumeric) {
		initialize();

		setColumn(pColumn);
		setOperator(pOperator);
		addValues(pValue);
		setNegate(pNegate);
		setNumeric(pNumeric);
	}

	public FilterType(String pColumn, String pOperator, String pValue, Boolean pNegate, Boolean pNumeric, String pLogic,
			List<FilterSet> pFilterSets) {
		initialize();

		setColumn(pColumn);
		setOperator(pOperator);
		addValues(pValue);
		setNegate(pNegate);
		setNumeric(pNumeric);

		setLogic(pLogic);
		setFilterSets(pFilterSets);
	}

	public FilterType(String pColumn, String pOperator, List<String> pValues, Boolean pNegate, Boolean pNumeric) {
		initialize();

		setColumn(pColumn);
		setOperator(pOperator);
		setValues(pValues);
		setNegate(pNegate);
		setNumeric(pNumeric);
	}

	public FilterType(String pColumn, String pOperator, List<String> pValues, Boolean pNegate, Boolean pNumeric,
			String pLogic, List<FilterSet> pFilterSets) {
		initialize();

		setColumn(pColumn);
		setOperator(pOperator);
		setValues(pValues);
		setNegate(pNegate);
		setNumeric(pNumeric);

		setLogic(pLogic);
		setFilterSets(pFilterSets);
	}

	private void initialize() {
		this.column = "";
		this.operator = "";
		this.values = new ArrayList<String>();
		this.negate = false;
		this.numeric = false;

		filterSets = new ArrayList<FilterSet>();
	}

	public String getColumn() {
		return this.column;
	}

	public void setColumn(String pColumn) {
		this.column = pColumn;
	}

	public String getOperator() {
		return this.operator;
	}

	private String getOperatorSQL() {
		String result = "";

		switch (getOperator().toUpperCase()) {
		case "EQ":
		case "EQT":
		case "EQUAL":
		case "EQUAL TO":
		case "EQUAL_TO":
		case "EQUALTO":
			result = "=";
			break;
		case "LEQ":
		case "LEQT":
		case "LTEQ":
		case "LTEQT":
		case "LE":
		case "LET":
		case "LTE":
		case "LTET":
		case "LESS_THAN_EQUAL":
		case "LESS THAN EQUAL":
		case "LESSTHANEQUAL":
		case "LESS_THAN_EQUAL_TO":
		case "LESS THAN EQUAL TO":
		case "LESSTHANEQUALTO":
			result = "<=";
			break;
		case "GEQ":
		case "GEQT":
		case "GTEQ":
		case "GTEQT":
		case "GE":
		case "GET":
		case "GTE":
		case "GTEt":
		case "GREATER_THAN_EQUAL":
		case "GREATER THAN EQUAL":
		case "GREATERTHANEQUAL":
		case "GREATER_THAN_EQUAL_TO":
		case "GREATER THAN EQUAL TO":
		case "GREATERTHANEQUALTO":
			result = ">=";
			break;
		case "NEQ":
		case "NEQT":
		case "NTEQ":
		case "NTEQT":
		case "NE":
		case "NET":
		case "NTE":
		case "NTET":
		case "NOT_EQUAL_TO":
		case "NOT EQUAL TO":
		case "NOTEQUALTO":
			result = "<>";
			break;
		case "LT":
		case "LESS_THAN":
		case "LESS THAN":
		case "LESSTHAN":
			result = "<";
			break;
		case "GT":
		case "GREATER_THAN":
		case "GREATER THAN":
		case "GREATERTHAN":
			result = ">";
			break;
		default:
			result = getOperator();
			break;
		}

		return result;
	}

	public void setOperator(String pOperator) {
		this.operator = pOperator;
	}

	public void clearValues() {
		this.values.clear();
	}

	public List<String> getValues() {
		return this.values;
	}

	public void setValues(List<String> pValues) {
		this.values = pValues;
	}

	public void addValues(String pValue) {
		this.values.add(pValue);
	}

	public Boolean getNegate() {
		return this.negate;
	}

	public void setNegate(Boolean pNegate) {
		this.negate = pNegate;
	}

	public Boolean getNumeric() {
		return this.numeric;
	}

	public void setNumeric(Boolean pNumeric) {
		this.numeric = pNumeric;
	}

	public String getLogic() {
		return logic;
	}

	public void setLogic(String pLogic) {
		this.logic = pLogic;
	}

	public List<FilterSet> getFilterSets() {
		return this.filterSets;
	}

	public void setFilterSets(List<FilterSet> pFilterSets) {
		this.filterSets = pFilterSets;
	}

	public void addFilterSets(FilterSet pFilterSet) {
		if (this.filterSets == null) {
			this.filterSets = new ArrayList<FilterSet>();
		}

		this.filterSets.add(pFilterSet);
	}

	public void clearFilterSets() {
		if (this.filterSets != null) {
			this.filterSets.clear();
			this.filterSets = null;
		}
	}

	public void removeFilterSets(FilterSet pFilterSet) {
		if (this.filterSets != null) {
			this.filterSets.remove(pFilterSet);

			if (this.filterSets.size() == 0) {
				clearFilterSets();
			}
		}
	}

	@Override
	public String toString() {
		StringBuilder result = new StringBuilder();

		result.append("{");
		result.append("column: " + column.toString() + ", ");
		result.append("operator: " + operator.toString() + ", ");
		result.append("values: " + String.join(",", values) + ", ");
		result.append("negate: " + negate.toString() + ", ");
		result.append("numeric: " + numeric.toString() + ", ");
		result.append("filterSets: " + filterSets.toString() + ", ");
		result.append("logic: " + logic.toString());
		result.append("}");

		return result.toString();
	}

	public String toSQLWhereClause() {
		String result = "";
		Boolean firstTime = true;
		String separator = (getNumeric() ? "" : "'");

		result = "(" + ((getFilterSets().size() > 0) ? "(" : "") + 
				(getNegate() ? "NOT " : "") + getColumn() + " " + getOperatorSQL();

		if (getOperator().equalsIgnoreCase("IN")) {
			result += " (";
			for (String value : getValues()) {
				result += (firstTime ? separator : ", " + separator) + value + separator;
				firstTime = false;
			}
			result += ")";
		} else if (getOperator().equalsIgnoreCase("BETWEEN")) {
			result += " ";
			for (String value : getValues()) {
				result += (firstTime ? separator : " AND " + separator) + value + separator;

				// only two iterations are allowed for BETWEEN
				// extra values will be ignored
				if (!firstTime) {
					break;
				}

				firstTime = false;
			}
		} else {
			for (String value : getValues()) {
				result += " " + separator + value + separator;
				break;
			}
		}

		result += ")";

		// processing for filterset sub-group
		if (getFilterSets().size() > 0) {
			result += (" " + getLogic() + " ");

			firstTime = true;
			for (FilterSet fs : getFilterSets()) {
				result += (firstTime ? "" : (" " + getLogic() + " ")) + fs.toSQLWhereClause();
				firstTime = false;
			}

			result += ")";
		}

		return result;
	}
}
