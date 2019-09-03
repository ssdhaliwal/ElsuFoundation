package filters;

import java.io.*;
import java.util.*;

import javax.xml.bind.annotation.*;

@XmlRootElement
public class FilterGroup implements Serializable {

	private static final long serialVersionUID = -2275147617608726649L;

	private MapExtentType mapExtent;
	private FilterPageType filterPage;
	private List<FilterSet> filterSets;
	private String logic = "OR";

	public FilterGroup() { // needed for JAXB
		initialize();
	}

	public FilterGroup(MapExtentType pMapExtent, FilterSet pFilterSet) {
		initialize();

		setMapExtent(pMapExtent);
		addFilterSets(pFilterSet);
	}

	public FilterGroup(MapExtentType pMapExtent, FilterSet pFilterSet, String pLogic) {
		initialize();

		setMapExtent(pMapExtent);
		addFilterSets(pFilterSet);
		setLogic(pLogic);
	}

	public FilterGroup(MapExtentType pMapExtent, FilterSet pFilterSet, String pOrderByColumns, int pPageSize) {
		initialize();

		setMapExtent(pMapExtent);
		addFilterSets(pFilterSet);
		getFilterPage().setOrderByColumns(pOrderByColumns);
		getFilterPage().setPageSize(pPageSize);
	}

	public FilterGroup(MapExtentType pMapExtent, FilterSet pFilterSet, String pLogic, String pOrderByColumns,
			int pPageSize) {
		initialize();

		setMapExtent(pMapExtent);
		addFilterSets(pFilterSet);
		setLogic(pLogic);
		getFilterPage().setOrderByColumns(pOrderByColumns);
		getFilterPage().setPageSize(pPageSize);
	}

	public FilterGroup(MapExtentType pMapExtent, List<FilterSet> pFilterSet) {
		initialize();

		setMapExtent(pMapExtent);
		setFilterSets(pFilterSet);
	}

	public FilterGroup(MapExtentType pMapExtent, List<FilterSet> pFilterSet, String pLogic) {
		initialize();

		setMapExtent(pMapExtent);
		setFilterSets(pFilterSet);
		setLogic(pLogic);
	}

	public FilterGroup(MapExtentType pMapExtent, List<FilterSet> pFilterSet, String pLogic, String pOrderByColumns,
			int pPageSize) {
		initialize();

		setMapExtent(pMapExtent);
		setFilterSets(pFilterSet);
		setLogic(pLogic);
		getFilterPage().setOrderByColumns(pOrderByColumns);
		getFilterPage().setPageSize(pPageSize);
	}

	public FilterGroup(MapExtentType pMapExtent, List<FilterSet> pFilterSet, String pOrderByColumns, int pPageSize) {
		initialize();

		setMapExtent(pMapExtent);
		setFilterSets(pFilterSet);
		getFilterPage().setOrderByColumns(pOrderByColumns);
		getFilterPage().setPageSize(pPageSize);
	}

	private void initialize() {
		mapExtent = new MapExtentType();
		filterPage = new FilterPageType();
		filterSets = new ArrayList<FilterSet>();
	}

	public MapExtentType getMapExtent() {
		return this.mapExtent;
	}

	public void setMapExtent(MapExtentType pMapExtent) {
		this.mapExtent = pMapExtent;
	}

	public void setMapExtent(MapPointType pSouthWest, MapPointType pNorthEast) {
		this.mapExtent.setSouthWest(pSouthWest);
		this.mapExtent.setNorthEast(pNorthEast);
	}

	public void setMapExtentSouthWest(Double pLongitude, Double pLatitude) {
		this.mapExtent.setSouthWest(pLongitude, pLatitude);
	}

	public void setMapExtentNorthEast(Double pLongitude, Double pLatitude) {
		this.mapExtent.setNorthEast(pLongitude, pLatitude);
	}

	public FilterPageType getFilterPage() {
		return this.filterPage;
	}

	public void setOrderByColumns(String pOrderByColumns) {
		getFilterPage().setOrderByColumns(pOrderByColumns);
	}

	public void setPageIndex(int pPageIndex) {
		getFilterPage().setPageIndex(pPageIndex);
	}

	public void setPageSize(int pPageSize) {
		getFilterPage().setPageSize(pPageSize);
	}

	public List<FilterSet> getFilterSets() {
		return this.filterSets;
	}

	public void setFilterSets(List<FilterSet> pFilterSet) {
		this.filterSets = pFilterSet;
	}

	public void addFilterSets(FilterSet pFilterSet) {
		this.filterSets.add(pFilterSet);
	}

	public void addFilter(FilterType pFilter) {
		for (FilterSet fgc : getFilterSets()) {
			fgc.getFilters().add(pFilter);
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
		result.append("mapExtent: " + mapExtent.toString() + ", ");
		result.append("FilterPage: " + filterPage.toString() + ", ");
		result.append("filterSets: " + filterSets.toString() + ", ");
		result.append("logic: " + logic);
		result.append("}");

		return result.toString();
	}

	public String toSQLWhereClause() {
		StringBuilder sb = new StringBuilder();
		Boolean firstTime = true;

		// add the map-extension
		Boolean extentAdded = false;
		if (!getMapExtent().getIgnore()) {
			extentAdded = true;
			Double minLongitude1, maxLongitude1, minLatitude1, maxLatitude1;
			Double minLongitude2, maxLongitude2, minLatitude2, maxLatitude2;

			// no meridian issue
			if ((getMapExtent().getNorthEast().getLongitude() > getMapExtent().getSouthWest().getLongitude())
					&& (getMapExtent().getNorthEast().getLatitude() > getMapExtent().getSouthWest().getLatitude())) {
				minLongitude1 = getMapExtent().getSouthWest().getLongitude();
				minLatitude1 = getMapExtent().getSouthWest().getLatitude();
				maxLongitude1 = getMapExtent().getNorthEast().getLongitude();
				maxLatitude1 = getMapExtent().getNorthEast().getLatitude();

				// get longitude
				sb.append("((longitude BETWEEN " + minLongitude1 + " AND " + maxLongitude1 + ") AND ");
				sb.append("(latitude BETWEEN " + minLatitude1 + " AND " + maxLatitude1 + "))");
			} else if (getMapExtent().getSouthWest().getLongitude() > getMapExtent().getNorthEast().getLongitude()) {
				minLongitude1 = getMapExtent().getSouthWest().getLongitude();
				minLatitude1 = getMapExtent().getSouthWest().getLatitude();
				maxLongitude1 = 180.0;
				maxLatitude1 = getMapExtent().getNorthEast().getLatitude();

				minLongitude2 = -180.0;
				minLatitude2 = getMapExtent().getSouthWest().getLatitude();
				maxLongitude2 = getMapExtent().getNorthEast().getLongitude();
				maxLatitude2 = getMapExtent().getNorthEast().getLatitude();

				// get longitude
				sb.append("( ((longitude BETWEEN " + minLongitude1 + " AND " + maxLongitude1 + ") AND ");
				sb.append("   (latitude BETWEEN " + minLatitude1 + " AND " + maxLatitude1 + ")) OR ");
				sb.append("  ((longitude BETWEEN " + minLongitude2 + " AND " + maxLongitude2 + ") AND ");
				sb.append("   (latitude BETWEEN " + minLatitude2 + " AND " + maxLatitude2 + ")) )");
			}
		}

		// add filter groups
		if (!getFilterSets().isEmpty()) {
			if (extentAdded) {
				sb.append(" AND ");
			}

			sb.append("(");

			for (FilterSet fs : getFilterSets()) {
				sb.append((firstTime ? "" : (" " + getLogic() + " ")) + fs.toSQLWhereClause());
				firstTime = false;
			}

			sb.append(")");
		}

		// return data
		return sb.toString();
	}
}
