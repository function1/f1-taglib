package com.f1.custom.tags.data;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;

import com.plumtree.openfoundation.util.XPArrayList;
import com.plumtree.portaluiinfrastructure.ptlink.IPTURL;
import com.plumtree.portaluiinfrastructure.ptlink.PTURLConstants;
import com.plumtree.portaluiinfrastructure.tags.ATag;
import com.plumtree.portaluiinfrastructure.tags.metadata.ITagMetaData;
import com.plumtree.portaluiinfrastructure.tags.metadata.TagMetaData;
import com.plumtree.taglib.ptdata.APTURLDODataTag;
import com.plumtree.taskapi.portalui.TaskAPIUICommon;
import com.plumtree.taskapi.portalui.TaskAPIUICommunity;
import com.plumtree.uiinfrastructure.uitasks.IEnvironment;
import com.plumtree.xpshared.htmlelements.HTMLElement;

/**
 * Implementation of the ParentCommunity PTURL DO Data Tag
 * 
 */
public class BreadcrumbDataTag extends APTURLDODataTag
{

	// All public static final metadata objects should be initialized
	// in a static initializer so that we can control the order of
	// initialization.
	public static final ITagMetaData TAG;
	//public int nCommunityID;
	
	static
	{
		// The TAG member variable needs to be initialized first since it
		// can be used in other tags (i.e. as a RequiredParentTag) that
		// are referenced by member variables of this tag (i.e. as a
		// RelatedChildTag).  This is necessary for tags that have
		// circular references such as a parent / child tag relationship.
		TAG = new TagMetaData("breadcrumbdata",COMMON_PRETAGMETADATA_DESC +	"URL to Parent Community of a " + "specified Community (The Community above the specified Community in the " + "administrative hierarchy. No URL is set if the user " + "does not have access to the Parent Community). " +	COMMON_POSTTAGMETADATA_DESC+"The URL will also set the "+PTURLConstants.KEY_CURRENTMARKER+" DO data component on a Community URL " +  "if currently in that Community. ");
	}
	
	public HTMLElement DisplayTag()
	{
		IEnvironment env = GetEnvironment();
	
		int nCommunityID = TaskAPIUICommunity.GetCurrentCommunityID(TaskAPIUICommon.GetEnvTypeObject(env));
		
		IPTURL curl = TaskAPIUICommunity.GetParentCommunityAsURL(TaskAPIUICommon.GetEnvTypeObject(env), nCommunityID);
		
		ArrayList list = new ArrayList();
		
		if(curl != null)
		{
			while(CommunityHasParent(env,nCommunityID))
			{
				IPTURL nurl = GetParentURLData(env,nCommunityID);
				list.add(nurl);
				nCommunityID = TaskAPIUICommunity.GetParentCommunityID(TaskAPIUICommon.GetEnvTypeObject(env),nCommunityID); 
			}
		}
		else
		{
			SetPTURLDataEntry(curl);
		}
		
		Collections.reverse(list);
		
		XPArrayList rurls = new XPArrayList();
		
		Iterator i = list.iterator();
		
		while(i.hasNext())
		{
		 rurls.Add(i.next());
		}
		
		SetPTURLDataCollection(rurls);
		
		return null;
	}

	public boolean CommunityHasParent(IEnvironment env, int nCommunityID)
	{
		if(TaskAPIUICommunity.GetParentCommunityAsURL(TaskAPIUICommon.GetEnvTypeObject(env), nCommunityID) != null) return true;
		return false;
	}
	
	public IPTURL GetParentURLData(IEnvironment env, int nCommunityID)
	{
		IPTURL url = TaskAPIUICommunity.GetParentCommunityAsURL(TaskAPIUICommon.GetEnvTypeObject(env), nCommunityID);
		return url;
	}
	
	public ATag Create()
	{
		return new BreadcrumbDataTag();
	}

}
