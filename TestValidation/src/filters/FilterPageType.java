package filters;

import java.io.*;

import javax.xml.bind.annotation.*;
import com.fasterxml.jackson.annotation.*;

@XmlRootElement
public class FilterPageType implements Serializable {

	private static final long serialVersionUID = -1987406642044688454L;

	private String orderByColumns;
	private int pageIndex;
	private int pageSize;

	public FilterPageType() { // needed for JAXB
		initialize();
	}

	public FilterPageType(String pOrderByColumns, int pStartIndex, int pPageSize) {
		setOrderByColumns(pOrderByColumns);
		setPageIndex(pStartIndex);
		setPageSize(pPageSize);
	}

	private void initialize() {
		this.orderByColumns = "";
		this.pageIndex = 0;
		this.pageSize = 0;
	}

	public String getOrderByColumns() {
		return this.orderByColumns;
	}

	public void setOrderByColumns(String pOrderByColumns) {
		this.orderByColumns = pOrderByColumns;
	}

	public int getPageIndex() {
		return this.pageIndex;
	}

	public void setPageIndex(int pPageIndex) {
		this.pageIndex = pPageIndex;
	}

	public int getPageSize() {
		return this.pageSize;
	}

	public void setPageSize(int pPageSize) {
		this.pageSize = pPageSize;
	}

	@JsonIgnore
	public int getPageStartOffset() {
		return ((this.pageIndex - 1) * this.pageSize) + 1;
	}

	@JsonIgnore
	public int getPageStopOffset() {
		return ((this.pageIndex - 1) * this.pageSize) + this.pageSize;
	}

	@Override
	public String toString() {
		StringBuilder result = new StringBuilder();
		
		result.append("{");
		result.append("orderByColumns: [" + orderByColumns.toString() + "], " );
		result.append("pageIndex: " + pageIndex + ", " );
		result.append("pageSize: " + pageSize + ", " );
		result.append("}");
		
		return result.toString();
	}
}
