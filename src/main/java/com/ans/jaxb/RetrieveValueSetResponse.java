package com.ans.jaxb;

import java.io.Serializable;
import java.util.Objects;

/**
 * RetrieveValueSetResponse object
 * 
 * @author bensalem Nizar
 */
public class RetrieveValueSetResponse implements Serializable {

	/**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = 2262061124176631777L;
	/**
	 * valueSetOID
	 */
	public String valueSetOID;
	/**
	 * valueSetName
	 */
	public String valueSetName;
	/**
	 * code
	 */
	public String code;
	/**
	 * displayName
	 */
	public String displayName;
	/**
	 * codeSystemName
	 */
	public String codeSystemName;
	/**
	 * codeSystem
	 */
	public String codeSystem;
	/**
	 * dateDebut
	 */
	public String dateDebut;
	/**
	 * dateFin
	 */
	public String dateFin;

	/**
	 * @return the valueSetOID
	 */
	public String getValueSetOID() {
		return valueSetOID;
	}

	/**
	 * @param valueSetOID the valueSetOID to set
	 */
	public void setValueSetOID(String valueSetOID) {
		this.valueSetOID = valueSetOID;
	}

	/**
	 * @return the valueSetName
	 */
	public String getValueSetName() {
		return valueSetName;
	}

	/**
	 * @param valueSetName the valueSetName to set
	 */
	public void setValueSetName(String valueSetName) {
		this.valueSetName = valueSetName;
	}

	/**
	 * @return the code
	 */
	public String getCode() {
		return code;
	}

	/**
	 * @param code the code to set
	 */
	public void setCode(String code) {
		this.code = code;
	}

	/**
	 * @return the displayName
	 */
	public String getDisplayName() {
		return displayName;
	}

	/**
	 * @param displayName the displayName to set
	 */
	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	/**
	 * @return the codeSystemName
	 */
	public String getCodeSystemName() {
		return codeSystemName;
	}

	/**
	 * @param codeSystemName the codeSystemName to set
	 */
	public void setCodeSystemName(String codeSystemName) {
		this.codeSystemName = codeSystemName;
	}

	/**
	 * @return the codeSystem
	 */
	public String getCodeSystem() {
		return codeSystem;
	}

	/**
	 * @param codeSystem the codeSystem to set
	 */
	public void setCodeSystem(String codeSystem) {
		this.codeSystem = codeSystem;
	}

	/**
	 * @return the dateDebut
	 */
	public String getDateDebut() {
		return dateDebut;
	}

	/**
	 * @param dateDebut the dateDebut to set
	 */
	public void setDateDebut(String dateDebut) {
		this.dateDebut = dateDebut;
	}

	/**
	 * @return the dateFin
	 */
	public String getDateFin() {
		return dateFin;
	}

	/**
	 * @param dateFin the dateFin to set
	 */
	public void setDateFin(String dateFin) {
		this.dateFin = dateFin;
	}

	@Override
	public int hashCode() {
		return Objects.hash(code, codeSystem, codeSystemName, dateDebut, dateFin, displayName, valueSetName,
				valueSetOID);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		RetrieveValueSetResponse other = (RetrieveValueSetResponse) obj;
		return Objects.equals(code, other.code) && Objects.equals(codeSystem, other.codeSystem)
				&& Objects.equals(codeSystemName, other.codeSystemName) && Objects.equals(dateDebut, other.dateDebut)
				&& Objects.equals(dateFin, other.dateFin) && Objects.equals(displayName, other.displayName)
				&& Objects.equals(valueSetName, other.valueSetName) && Objects.equals(valueSetOID, other.valueSetOID);
	}

	@Override
	public String toString() {
		return "RetrieveValueSetResponse [valueSetOID=" + valueSetOID + ", valueSetName=" + valueSetName + ", code="
				+ code + ", displayName=" + displayName + ", codeSystemName=" + codeSystemName + ", codeSystem="
				+ codeSystem + ", dateDebut=" + dateDebut + ", dateFin=" + dateFin + "]";
	}



	
	

}
