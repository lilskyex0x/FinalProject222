package model;

public enum MajorTrack {
	  SOFTWARE_ENGINEERING,
	  DATA_ANALYTICS,
	  NETWORK_SECURITY,
	  E_COMMERCE;

	  public static MajorTrack fromMenuNumber(int n) {
	    switch (n) {
	      case 1:
	        return SOFTWARE_ENGINEERING;
	      case 2:
	        return DATA_ANALYTICS;
	      case 3:
	        return NETWORK_SECURITY;
	      case 4:
	        return E_COMMERCE;
	      default:
	        return null;
	    }
	  }

	  public String displayName() {
	    switch (this) {
	      case SOFTWARE_ENGINEERING:
	        return "Software Engineering";
	      case DATA_ANALYTICS:
	        return "Data Analytics";
	      case NETWORK_SECURITY:
	        return "Network & Security";
	      case E_COMMERCE:
	        return "E-Commerce";
	      default:
	        return name();
	    }
	  }
	}
