package com.f1.custom.tags.data;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;

import com.plumtree.server.*;
import com.plumtree.openfoundation.util.XPArrayList;
import com.plumtree.openfoundation.util.IXPEnumerator;
import com.plumtree.portaluiinfrastructure.ptlink.IPTURL;
import com.plumtree.portaluiinfrastructure.statichelpers.PTDebugHelpers;
import com.plumtree.portaluiinfrastructure.tags.ATag;
import com.plumtree.portaluiinfrastructure.tags.metadata.AttributeType;
import com.plumtree.portaluiinfrastructure.tags.metadata.ITagMetaData;
import com.plumtree.portaluiinfrastructure.tags.metadata.OptionalTagAttribute;
import com.plumtree.portaluiinfrastructure.tags.metadata.RequiredTagAttribute;
import com.plumtree.taglib.ptdata.APTURLDODataTag;
import com.plumtree.portaluiinfrastructure.tags.metadata.TagMetaData;
import com.plumtree.taskapi.portalui.TaskAPIUICommon;
import com.plumtree.taskapi.portalui.TaskAPIUICommunity;
import com.plumtree.uiinfrastructure.uitasks.IEnvironment;
import com.plumtree.xpshared.htmlelements.HTMLElement;
import com.plumtree.openlog.OpenLogService;
import com.plumtree.openlog.OpenLogger;

import com.f1.custom.tags.util.IPTURL_Comparer;


public class RecursiveCommunityMenuDataTag extends APTURLDODataTag
{
//	 Fields
    private static OpenLogger log = OpenLogService.GetLogger(OpenLogService.GetComponent(PTDebugHelpers.COMPONENT_PORTAL_ADAPTIVETAGS), RecursiveCommunityMenuDataTag.class);
    public static final OptionalTagAttribute PARAM = new OptionalTagAttribute("param", "The name of the new parameter to retrieve for the Object.", AttributeType.STRING, "String");
    public static final OptionalTagAttribute PARAMDATATYPE = new OptionalTagAttribute("paramdatatype", "The field type the sortby field is, 'int' or 'String' are the only acceptable values.", AttributeType.STRING, "String");
    public static final OptionalTagAttribute PARAMID = new OptionalTagAttribute("paramid", "The Object id of the new parameter to retrieve for the Object.", AttributeType.INT, "0");
    public static final ITagMetaData TAG = new TagMetaData("recursivecommunitymenu", "Return a collection of menu items in the order which they need to be displayed.");
    public static final RequiredTagAttribute TOPLEVELMENUS = new RequiredTagAttribute("toplevelmenus", "A comma delimeted list of community ids to set as the top level of the menu.");

    // Methods
    public boolean CommunityHasParent(IEnvironment env, int nCommunityID)
    {
        return (TaskAPIUICommunity.GetParentCommunityAsURL(TaskAPIUICommon.GetEnvTypeObject(env), nCommunityID) != null);
    }

    public ATag Create()
    {
        return new RecursiveCommunityMenuDataTag();
    }

    public HTMLElement DisplayTag()
    {
        IPTURL ipturl;
        IEnvironment env = this.GetEnvironment();
        IPTSession pTSession = TaskAPIUICommon.GetPTSession(TaskAPIUICommon.GetEnvTypeObject(env));
        String tagAttributeAsString = this.GetTagAttributeAsString(PARAM);
        int tagAttributeAsInt = this.GetTagAttributeAsInt(PARAMID);
        String strSortDataType = this.GetTagAttributeAsString(PARAMDATATYPE);
        boolean flag = true;
        if (((tagAttributeAsString.length() == 0) ||
        		(tagAttributeAsInt == 0)) ||
        		(strSortDataType.length() == 0))
        {
            flag = false;
        }
        ArrayList<Integer> commIdList = new ArrayList<Integer>();
        int currentCommunityID = TaskAPIUICommunity.GetCurrentCommunityID(TaskAPIUICommon.GetEnvTypeObject(env));
        String currentCommunityName = TaskAPIUICommunity.GetCurrentCommunityName(TaskAPIUICommon.GetEnvTypeObject(env));
        commIdList.add(Integer.valueOf(currentCommunityID));
        while (this.CommunityHasParent(env, currentCommunityID))
        {
            currentCommunityID = TaskAPIUICommunity.GetParentCommunityID(TaskAPIUICommon.GetEnvTypeObject(env), currentCommunityID);
            commIdList.add(currentCommunityID);
        }
        String str4 = this.GetTagAttributeAsString(TOPLEVELMENUS);
        String separator = "," ;
        String[] array = str4.split(separator);
        ArrayList arrayList = new ArrayList();
        for (int i=0; i<array.length; i++)
        	arrayList.add(array[i]);
        Collections.reverse(arrayList);
        ArrayList urlList = new ArrayList();
        int arrLength = array.length;
        for (int x=0; x<arrLength; x++)
        {
        	String str5 = array[x];
            currentCommunityID = Integer.parseInt(str5);
            boolean flag2 = false;
            try
            {
                flag2 = pTSession.GetCommunities().IsObjectAccessible(currentCommunityID, pTSession.GetSessionInfo().GetCurrentUserID(), 1);
            }
            catch (Exception ex)
            {
                flag2 = false;
            }
            if (flag2)
            {
                currentCommunityName = TaskAPIUICommunity.GetCommunityName(TaskAPIUICommon.GetEnvTypeObject(env), currentCommunityID);
                ipturl = TaskAPIUICommunity.CreateCommunityCommonOpenerURL(TaskAPIUICommon.GetEnvTypeObject(env), currentCommunityID, currentCommunityName);
                ipturl.AddControlParameter("level", "1");
                ipturl.AddControlParameter("css", "L1");
                urlList.add(ipturl);
            }
        }
        ipturl = null;
        XPArrayList xpList = new XPArrayList();
        while (urlList.size() > 0)
        {
            int index = urlList.size() - 1;
            ipturl = (IPTURL) urlList.get(index);
            urlList.remove(index);
            xpList.Add(ipturl);
            Integer item = Integer.valueOf(ipturl.GetData("objid"));
            int num5 = Integer.parseInt(ipturl.GetData("level"));
            if (commIdList.contains(item))
            {
                try
                {
                    ArrayList underlyingObject = TaskAPIUICommunity.GetSubCommununitiesAsURLs(TaskAPIUICommon.GetEnvTypeObject(env), item).GetUnderlyingObject();
                    if (flag)
                    {
                        try
                        {
                            XPArrayList dataList = this.GetExtraParameter(underlyingObject, pTSession, tagAttributeAsString, tagAttributeAsInt);
                            underlyingObject = this.SortCollection(dataList, tagAttributeAsString, strSortDataType);
                        }
                        catch (Exception ex)
                        {
                        }
                    }
                    Iterator objIter = underlyingObject.iterator();
                    while (objIter.hasNext())
                    {
                    	IPTURL ipturl2 = (IPTURL) objIter.next();
                        ipturl2.AddControlParameter("level", (int) (num5 + 1));
                        ipturl2.AddControlParameter("parentid", item);
                        ipturl2.AddControlParameter("css", "L" + (num5 + 1));
                        urlList.add(ipturl2);
                    }
                }
                catch (Exception ex)
                {
                }
                if (item.equals(TaskAPIUICommunity.GetCurrentCommunityID(TaskAPIUICommon.GetEnvTypeObject(env))))
                {
                	XPArrayList pageUrls = TaskAPIUICommunity.GetCommunityPagesAsURLs(TaskAPIUICommon.GetEnvTypeObject(env), item);
                	IXPEnumerator pageUrlIter = pageUrls.GetEnumerator();
                	while (pageUrlIter.MoveNext())
                    {
                		IPTURL currentUrl = (IPTURL)pageUrlIter.GetCurrent();
                		currentUrl.AddControlParameter("level", (int) (num5 + 1));
                        currentUrl.AddControlParameter("css", "L" + (num5 + 1));
                        xpList.Add(currentUrl);
                    }
                }
            }
        }
        this.SetPTURLDataCollection(xpList);
        return null;
    }

    private XPArrayList GetExtraParameter(ArrayList dataList, IPTSession session, String strParamName, int paramId)
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

    private ArrayList SortCollection(XPArrayList dataList, String strParamName, String strSortDataType)
    {
        IPTURL[] arr = new IPTURL[dataList.GetSize()];
        dataList.ToArray(arr);
        java.util.Arrays.sort(arr, new IPTURL_Comparer(strParamName, strSortDataType));
        ArrayList<IPTURL> urlList = new ArrayList<IPTURL>();
        int arrLen = arr.length;
        for (int x=0; x< arrLen; x++)
        {
        	urlList.add(arr[x]);
        }
        return urlList;
    }

}
