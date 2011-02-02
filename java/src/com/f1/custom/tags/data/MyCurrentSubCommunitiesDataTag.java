package com.f1.custom.tags.data;


import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;

import com.plumtree.openfoundation.util.XPArrayList;
import com.plumtree.portaluiinfrastructure.ptlink.IPTURL;
import com.plumtree.portaluiinfrastructure.ptlink.PTURLConstants;
import com.plumtree.portaluiinfrastructure.tags.ATag;
import com.plumtree.portaluiinfrastructure.tags.metadata.AttributeType;
//import com.plumtree.portaluiinfrastructure.tags.metadata.ITagMetaData;
import com.plumtree.portaluiinfrastructure.tags.metadata.OptionalTagAttribute;
//import com.plumtree.portaluiinfrastructure.tags.metadata.TagMetaData;
import com.plumtree.taglib.ptdata.CurrentSubCommunitiesDataTag;
import com.plumtree.taskapi.portalui.TaskAPIUICommon;
import com.plumtree.taskapi.portalui.TaskAPIUICommunity;
import com.plumtree.uiinfrastructure.uitasks.IEnvironment;
import com.plumtree.xpshared.htmlelements.HTMLElement;

/**
 * Implementation of the CurrentSubCommunities PTURL DO Data Tag
 * 
 */
public class MyCurrentSubCommunitiesDataTag extends CurrentSubCommunitiesDataTag
{
	// All public static final metadata objects should be initialized
	// in a static initializer so that we can control the order of
	// initialization.
	public static final OptionalTagAttribute SORTORDER;

	static
	{				
		SORTORDER = new OptionalTagAttribute("order", "Sort Order", AttributeType.STRING, "ascending");
	}

	/**
	 * @see com.plumtree.portaluiinfrastructure.tags.ATag#DisplayTag()
	 */
	public HTMLElement DisplayTag()
	{
		
		String sort = GetTagAttributeAsString(SORTORDER);
		
		IEnvironment env = GetEnvironment();
		
		XPArrayList urls = TaskAPIUICommunity.GetSubCommununitiesAsURLs(TaskAPIUICommon.GetEnvTypeObject(env), TaskAPIUICommunity.GetCurrentCommunityID(TaskAPIUICommon.GetEnvTypeObject(env)));
		
		if(sort.equalsIgnoreCase("ascending"))
		{
			ArrayList list = urls.GetUnderlyingObject();
			
			Collections.sort(list, 
									new Comparator() 
									{
										public int compare(Object x, Object y) 
										{
											String s1 = (String)((IPTURL)x).GetData(PTURLConstants.KEY_TITLE).toLowerCase();
											String s2 = (String)((IPTURL)y).GetData(PTURLConstants.KEY_TITLE).toLowerCase();
											return s1.compareTo(s2);
										}
									}
							);
			
			XPArrayList surls = new XPArrayList();
			
			Iterator i = list.iterator();
			
			while(i.hasNext())
			{
			 surls.Add(i.next());
			}
		
			SetPTURLDataCollection(surls);
		}
		else if(sort.equalsIgnoreCase("descending"))
		{
			ArrayList list = urls.GetUnderlyingObject();
			
			Collections.sort(list, 
									new Comparator() 
									{
										public int compare(Object x, Object y) 
										{
											String s1 = (String)((IPTURL)x).GetData(PTURLConstants.KEY_TITLE).toLowerCase();
											String s2 = (String)((IPTURL)y).GetData(PTURLConstants.KEY_TITLE).toLowerCase();
											return s2.compareTo(s1);
										}
									}
							);
			
			XPArrayList surls = new XPArrayList();
			
			Iterator i = list.iterator();
			
			while(i.hasNext())
			{
			 surls.Add(i.next());
			}
		
			SetPTURLDataCollection(surls);
		}
		else
		{
			SetPTURLDataCollection(urls);
		}
		
		return null;
	}

	/**
	 * @see com.plumtree.portaluiinfrastructure.tags.ATag#Create()
	 */
	public ATag Create()
	{
		return new MyCurrentSubCommunitiesDataTag();
	}
}
