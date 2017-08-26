package ro.axonsoft.internship172.web.model;

import java.io.Serializable;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

import com.google.common.base.MoreObjects;

public class VehicleOwnerListForm implements Serializable {
	private static final long serialVersionUID = 1L;

	private Integer page = 1;
	private Integer pageSize = 10;
	private String search;

	@Min(1)
	public Integer getPage() {
		return page;
	}

	public void setPage(final Integer page) {
		this.page = page;
	}

	@Min(1)
	@Max(100)
	public Integer getPageSize() {
		return pageSize;
	}

	public void setPageSize(final Integer pageSize) {
		this.pageSize = pageSize;
	}

	public String getSearch() {
		return search;
	}

	public void setSearch(final String search) {
		this.search = search;
	}

	@Override
	public String toString() {
		return MoreObjects.toStringHelper("").add("page", page).add("pageSize", pageSize).add("search", search)
				.toString();
	}

}
