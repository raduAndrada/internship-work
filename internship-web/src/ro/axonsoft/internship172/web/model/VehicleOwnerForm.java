package ro.axonsoft.internship172.web.model;

import java.io.Serializable;

import javax.validation.constraints.Size;

import com.google.common.base.MoreObjects;

public class VehicleOwnerForm implements Serializable {

	private static final long serialVersionUID = 1L;

	private String roIdCard;
	private String regPlate;
	private String issueDate;
	private String comentariu;
	private Long batchId;

	@Size(min = 6, max = 100, message = "{vehicle-owner.field.format}")
	public String getRoIdCard() {
		return roIdCard;
	}

	@Size(min = 3, max = 100, message = "{vehicle-owner.field.format}")
	public String getRegPlate() {
		return regPlate;
	}

	public String getIssueDate() {
		return issueDate;
	}

	public void setIssueDate(String issueDate) {
		this.issueDate = issueDate;
	}

	public String getComentariu() {
		return comentariu;
	}

	public void setComentariu(String comentariu) {
		this.comentariu = comentariu;
	}

	public Long getBatchId() {
		return batchId;
	}

	public void setBatchId(Long batchId) {
		this.batchId = batchId;
	}

	public void setRoIdCard(String roIdCard) {
		this.roIdCard = roIdCard;
	}

	public void setRegPlate(String regPlate) {
		this.regPlate = regPlate;
	}

	@Override
	public String toString() {
		return MoreObjects.toStringHelper("").add("roIdCard", roIdCard).add("regPlate", regPlate)
				.add("issueDate", issueDate).add("comentariu", comentariu).add("batchId", batchId).toString();
	}
}
