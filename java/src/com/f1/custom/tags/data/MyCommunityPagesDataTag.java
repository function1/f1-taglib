package com.f1.custom.tags.data;

import com.plumtree.openfoundation.util.XPArrayList;
import com.plumtree.portaluiinfrastructure.ptlink.IPTURL;
import com.plumtree.portaluiinfrastructure.ptlink.PTURLConstants;
import com.plumtree.portaluiinfrastructure.tags.ATag;
import com.plumtree.portaluiinfrastructure.tags.metadata.AttributeType;
import com.plumtree.portaluiinfrastructure.tags.metadata.OptionalTagAttribute;
import com.plumtree.taglib.ptdata.CommunityPagesDataTag;
import com.plumtree.taskapi.portalui.TaskAPIUICommon;
import com.plumtree.taskapi.portalui.TaskAPIUICommunity;
import com.plumtree.uiinfrastructure.uitasks.IEnvironment;
import com.plumtree.xpshared.htmlelements.HTMLElement;

/**
 * Implementation of the CommunityPages PTURL DO Data Tag
 * 
 */
public class MyCommunityPagesDataTag extends CommunityPagesDataTag
{
	public static final OptionalTagAttribute EXCLUDE;

	static
	{
		// The TAG member variable needs to be initialized first since it
		// can be used in other tags (i.e. as a RequiredParentTag) that
		// are referenced by member variables of this tag (i.e. as a
		// RelatedChildTag).  This is necessary for tags that have
		// circular references such as a parent / child tag relationship.
		EXCLUDE = new OptionalTagAttribute("exclude", "Exclude Pages", AttributeType.STRING, null);
	}

	/**
	 * @see com.plumtree.portaluiinfrastructure.tags.ATag#DisplayTag()
	 */
	public HTMLElement DisplayTag()
	{
		
		String exclude = GetTagAttributeAsString(EXCLUDE);
		
		IEnvironment env = GetEnvironment();
		int nCommunityID = GetTagAttributeAsInt(ATTRIB_COMMUNITYID);
		XPArrayList urls = TaskAPIUICommunity.GetCommunityPagesAsURLs(TaskAPIUICommon.GetEnvTypeObject(env), nCommunityID);
		
		if(exclude.equals(null))
		{	
			SetPTURLDataCollection(urls);
		
		}
		else
		{
			//String[] arrExclude = exclude.split(",");
			
			for (int i = 0; i < urls.GetSize(); i++)
			{
				String str1 = (String)((IPTURL)urls.GetElement(i)).GetData(PTURLConstants.KEY_TITLE).toLowerCase();
				
				if(str1.equalsIgnoreCase("home"))
				{
					urls.RemoveAt(i);
				}
			}
			SetPTURLDataCollection(urls);
		}
		
		return null;
	}
	
	/**
	 * @see com.plumtree.portaluiinfrastructure.tags.ATag#Create()
	 */
	public ATag Create()
	{
		return new MyCommunityPagesDataTag();
	}
}
