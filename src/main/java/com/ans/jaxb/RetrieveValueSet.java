package com.ans.jaxb;

import java.io.Serializable;
import java.util.*;

/**
 * RetrieveValueSetResponse object
 * 
 * @author bensalem Nizar
 */
public class RetrieveValueSet implements Serializable {

	/**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = 2262061124176631777L;
	/**
	 * valueSetOID
	 */
	public String valueSetOID;
	/**
	 * obsolete
	 */
	public String obsolete;

	/**
	 * @return the valueSetOID
	 */
	public String getValueSetOID() {
		return valueSetOID;
	}

	/**
	 * @param valueSetOID the valueSetOID to set
	 */
	public void setValueSetOID(final String valueSetOID) {
		this.valueSetOID = valueSetOID;
	}

	/**
	 * @return the obsolete
	 */
	public String getObsolete() {
		return obsolete;
	}

	/**
	 * @param obsolete the obsolete to set
	 */
	public void setObsolete(final String obsolete) {
		this.obsolete = obsolete;
	}

	@Override
	public int hashCode() {
		return Objects.hash(obsolete, valueSetOID);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		RetrieveValueSet other = (RetrieveValueSet) obj;
		return Objects.equals(obsolete, other.obsolete) && Objects.equals(valueSetOID, other.valueSetOID);
	}

	@Override
	public String toString() {
		return "RetrieveValueSet [valueSetOID=" + valueSetOID + ", obsolete=" + obsolete + "]";
	}
}
