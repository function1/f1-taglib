package com.f1.custom.tags.data;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;

import com.plumtree.openfoundation.util.XPArrayList;
import com.plumtree.openlog.OpenLogService;
import com.plumtree.openlog.OpenLogger;
import com.plumtree.portaluiinfrastructure.ptlink.IPTURL;
import com.plumtree.portaluiinfrastructure.ptlink.PTURLConstants;
import com.plumtree.portaluiinfrastructure.statichelpers.PTDebugHelpers;
import com.plumtree.portaluiinfrastructure.tags.ATag;
import com.plumtree.portaluiinfrastructure.tags.metadata.AttributeType;
import com.plumtree.portaluiinfrastructure.tags.metadata.OptionalTagAttribute;
import com.plumtree.server.IPTCommunity;
import com.plumtree.server.IPTQueryResult;
import com.plumtree.server.IPTSession;
import com.plumtree.taglib.ptdata.SubCommunitiesDataTag;
import com.plumtree.taskapi.portalui.TaskAPIUICommon;
import com.plumtree.taskapi.portalui.TaskAPIUICommunity;
import com.plumtree.uiinfrastructure.uitasks.IEnvironment;
import com.plumtree.xpshared.htmlelements.HTMLElement;

import com.f1.custom.tags.util.IPTURL_Comparer;


/**
 * Implementation of the SubCommunities PTURL DO Data Tag
 * 
 */
public class MySubCommunitiesDataTag extends SubCommunitiesDataTag
{

	public static final OptionalTagAttribute SORTORDER;
	public static final OptionalTagAttribute SORTBY;

	public static final String IptUrlParamName = "sortOrder";
	public static final String SortOrder_Ascending = "ascending";
	
	static
	{		
		SORTORDER = new OptionalTagAttribute("order", "Sort Order", AttributeType.STRING, SortOrder_Ascending);
		SORTBY = new OptionalTagAttribute("sortby", "Sort-by Property ID", AttributeType.STRING, PTURLConstants.KEY_OBJECTID);
	}

	/**
	 * @see com.plumtree.portaluiinfrastructure.tags.ATag#DisplayTag()
	 */
	public HTMLElement DisplayTag()
	{
		log.FunctionBegin("Entering MySubCommunitiesDataTag.DisplayTag()");
		String order = GetTagAttributeAsString(SORTORDER);
		String sortBy = GetTagAttributeAsString(SORTBY);
		boolean ascendingOrder = true;
		
		IEnvironment env = GetEnvironment();
		IPTSession ptSession = TaskAPIUICommon.GetPTSession(TaskAPIUICommon.GetEnvTypeObject(env));

		int nCommunityID = GetTagAttributeAsInt(ATTRIB_COMMUNITYID);
		
		if ( (null != order) && (!order.equalsIgnoreCase(SortOrder_Ascending)) )
			ascendingOrder = false;
		
		XPArrayList subCommUrls = TaskAPIUICommunity.GetSubCommununitiesAsURLs(TaskAPIUICommon.GetEnvTypeObject(env),
																				nCommunityID);
		ArrayList underlyingArray = subCommUrls.GetUnderlyingObject();
		
		int paramPropertyId = -1;
		String sortByParamType;
		if ( (null == sortBy) ||
			 (sortBy.length() < 1) ||
			 (sortBy.equalsIgnoreCase(PTURLConstants.KEY_OBJECTID)) )
		{
			sortBy = PTURLConstants.KEY_OBJECTID;
			sortByParamType = IPTURL_Comparer.SORTBY_TYPE_INT;
		}
		else
		{
			try
		    {
				paramPropertyId = Integer.parseInt(sortBy);
		    	sortByParamType = IPTURL_Comparer.SORTBY_TYPE_INT;
		    }
		    catch (NumberFormatException numEx)
		    {
		    	log.Debug("Parameter 'sortby' value: " + sortBy);
		    	sortByParamType = IPTURL_Comparer.SORTBY_TYPE_STR;
		    }
		    finally
		    {}
		}
		
        try
        {
        	if (-1 != paramPropertyId)
        	{
        		subCommUrls = this.GetExtraParameter(underlyingArray,
            		  								 ptSession,
            		  								 IptUrlParamName,
            		  								 paramPropertyId);
        		sortBy = IptUrlParamName;
        	}
        	StringBuilder debugMsgStr = new StringBuilder("Ordering subcommunities in ");
        	if (ascendingOrder)
        		debugMsgStr.append("ascending");
        	else
        		debugMsgStr.append("descending");
        	debugMsgStr.append(" order.");
        	log.Debug(debugMsgStr.toString());
        	underlyingArray = this.SortCollection(subCommUrls, sortBy, sortByParamType, ascendingOrder);
        }
        catch (Exception ex)
        {
        	log.Error(ex, "Error occured while retrieving additional community properties.");
        }
		
        Iterator arrIterator = underlyingArray.iterator();
        subCommUrls.Clear();
        while (arrIterator.hasNext())
        {
        	subCommUrls.Add((IPTURL)arrIterator.next());
        }
        SetPTURLDataCollection(subCommUrls);

        log.FunctionEnd("Leaving MySubCommunitiesDataTag.DisplayTag()");
		
		return null;
	}
	
	
	/**
	 * @see com.plumtree.portaluiinfrastructure.tags.ATag#Create()
	 */
	public ATag Create()
	{
		return new MySubCommunitiesDataTag();
	}
	
	
	private XPArrayList GetExtraParameter(ArrayList dataList,
										  IPTSession session,
										  String strParamName,
										  int paramId)
    {
        int count = dataList.size();
        IPTURL[] ipturlArray = new IPTURL[count];
        for (int i = 0; i < count; i++)
        {
            IPTURL ipturl = (IPTURL) dataList.get(i);
            String data = ipturl.GetData("objid");
            int nValue = 100;
            IPTCommunity community = (IPTCommunity) session.GetCommunities().Open(Integer.parseInt(data), false);
            IPTQueryResult singlePropertyData = community.GetObjectProperties().GetSinglePropertyData(paramId, -1);
            try
            {
                community.GetName();
                if (singlePropertyData.ItemAsObject(0, 0x4000000) != null)
                {
                    nValue = singlePropertyData.ItemAsInt(0, 0x4000000);
                }
            }
            catch (Exception ex)
            {
            }
            ipturl.AddControlParameter(strParamName, nValue);
            ipturlArray[i] = ipturl;
        }
        XPArrayList list = new XPArrayList();
        for(int n=0; n<ipturlArray.length; n++ )
        {
            list.Add(ipturlArray[n]);
        }
        return list;
    }

    public IPTURL GetParentURLData(IEnvironment env, int nCommunityID)
    {
        return TaskAPIUICommunity.GetParentCommunityAsURL(TaskAPIUICommon.GetEnvTypeObject(env), nCommunityID);
    }

    private ArrayList SortCollection(XPArrayList dataList,
    								String sortByParam,
    								String sortByParamType,
    								boolean sortAscending)
    {
    	// check to see if sortByParamVal a property id
    	
        IPTURL[] arr = new IPTURL[dataList.GetSize()];
        dataList.ToArray(arr);
        
        Arrays.sort(arr, new IPTURL_Comparer(sortByParam, sortByParamType, sortAscending));
        
        ArrayList<IPTURL> urlList = new ArrayList<IPTURL>();
        int arrLen = arr.length;
        for (int x=0; x< arrLen; x++)
        {
        	urlList.add(arr[x]);
        }
        return urlList;
    }
    
    private static OpenLogger log = OpenLogService.GetLogger(OpenLogService.GetComponent(PTDebugHelpers.COMPONENT_PORTAL_ADAPTIVETAGS),
    														MySubCommunitiesDataTag.class);
    

}