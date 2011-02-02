package com.f1.custom.tags.util;

import java.util.Comparator;

import com.plumtree.openlog.OpenLogService;
import com.plumtree.openlog.OpenLogger;
import com.plumtree.portaluiinfrastructure.ptlink.IPTURL;
import com.plumtree.portaluiinfrastructure.ptlink.PTURLConstants;
import com.plumtree.portaluiinfrastructure.statichelpers.PTDebugHelpers;

public class IPTURL_Comparer implements Comparator
{
	public static final String SORTBY_TYPE_STR = "string";
	public static final String SORTBY_TYPE_INT = "int";
	
	
	// Fields
    private String dataField;
    private String fieldType;
    private boolean ascending = true;
    
    private static OpenLogger log = OpenLogService.GetLogger(OpenLogService.GetComponent(PTDebugHelpers.COMPONENT_PORTAL_ADAPTIVETAGS),
    														 IPTURL_Comparer.class);

    // Methods
    public IPTURL_Comparer()
    {
        this.dataField = PTURLConstants.KEY_TITLE;
        this.fieldType = SORTBY_TYPE_STR;
    }

    public IPTURL_Comparer(String sortBy, String sortType)
    {
        this.dataField = sortBy;
        this.fieldType = sortType.toLowerCase();
        ascending = true;
    }
    
    public IPTURL_Comparer(String sortBy, String sortType, boolean ascendingOrder)
    {
        this.dataField = sortBy;
        this.fieldType = sortType.toLowerCase();
        ascending = ascendingOrder;
    }

    public int compare(Object x, Object y)
    {
    	int compareCode;
    	
        IPTURL ipturl = (IPTURL) x;
        IPTURL ipturl2 = (IPTURL) y;
        if (this.fieldType.equalsIgnoreCase(SORTBY_TYPE_INT))
        {
        	
            Integer num1 = Integer.valueOf(ipturl.GetData(this.dataField));
            Integer num2 = Integer.valueOf(ipturl2.GetData(this.dataField));
            if (ascending)
            	compareCode = num1.compareTo(num2);
            else
            	compareCode = num2.compareTo(num1);
        }
        else if (this.fieldType.equalsIgnoreCase(SORTBY_TYPE_STR))
        {
        	String s1 = (String)(ipturl).GetData(dataField).toLowerCase();
			String s2 = (String)(ipturl2).GetData(dataField).toLowerCase();
			if (ascending)
				compareCode = s1.compareTo(s2);
			else
				compareCode = s2.compareTo(s1);
        }
        else
        	compareCode = ipturl.GetData(this.dataField).compareTo(ipturl2.GetData(this.dataField));
        
        log.Debug("Comparing  " + dataField + " field in the URL data yielded: " + compareCode);
        
        return compareCode;
    }
}
