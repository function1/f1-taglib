package com.f1.custom.tags.data;


import com.plumtree.portaluiinfrastructure.ptlink.IPTURL;
import com.plumtree.portaluiinfrastructure.tags.ATag;
import com.plumtree.portaluiinfrastructure.tags.Scope;
import com.plumtree.portaluiinfrastructure.tags.metadata.ITagMetaData;
import com.plumtree.portaluiinfrastructure.tags.metadata.TagMetaData;
import com.plumtree.taglib.ptdata.APTURLDODataTag;
import com.plumtree.taskapi.portalui.TaskAPIUICommon;
import com.plumtree.taskapi.portalui.TaskAPIUICommunity;
import com.plumtree.uiinfrastructure.activityspace.AActivitySpace;
import com.plumtree.uiinfrastructure.uitasks.IEnvironment;
import com.plumtree.xpshared.htmlelements.HTMLComment;
import com.plumtree.xpshared.htmlelements.HTMLElement;

/**
 * Implementation of the CurrentCommunityPages PTURL DO Data Tag
 *
 */
public class CurrentCommunityPageIDDataTag extends APTURLDODataTag
{
	// All public static final metadata objects should be initialized
	// in a static initializer so that we can control the order of
	// initialization.
	public static final ITagMetaData TAG;

	static
	{
		// The TAG member variable needs to be initialized first since it
		// can be used in other tags (i.e. as a RequiredParentTag) that
		// are referenced by member variables of this tag (i.e. as a
		// RelatedChildTag).  This is necessary for tags that have
		// circular references such as a parent / child tag relationship.
		TAG = new TagMetaData("currentcommunitypageiddata",
			COMMON_PRETAGMETADATA_DESC +
			"Gets the current community page id " +
			COMMON_POSTTAGMETADATA_DESC);
	}

	/**
	 * @see com.plumtree.portaluiinfrastructure.tags.ATag#DisplayTag()
	 */
	public HTMLElement DisplayTag()
	{
		IEnvironment env = GetEnvironment();
		AActivitySpace currSpace = TaskAPIUICommon.GetEnvTypeObject(env);
	
		if( currSpace != null ) {			
			int nCurrCommPageID = TaskAPIUICommunity.GetCurrentCommunityPageID(currSpace);
			String sCurrCommPageName = TaskAPIUICommunity.GetCurrentCommunityPageName(currSpace);
			int nCurrCommID = TaskAPIUICommunity.GetCurrentCommunityID(currSpace);
			IPTURL url = TaskAPIUICommunity.CreateCommunityPageCommonOpenerURL(currSpace, nCurrCommID, nCurrCommPageID, sCurrCommPageName);

			Scope tagScope = Scope.GetScope( GetTagAttributeAsString(ATTRIB_SCOPE));
			String strDataReference = GetTagAttributeAsString(ATTRIB_ID);

			SetStateSharedVariable(strDataReference, url, tagScope, false);
		
		} else {
			return new HTMLComment("Unable to determine current community page");
		}

		return null;
	}

	/**
	 * @see com.plumtree.portaluiinfrastructure.tags.ATag#Create()
	 */
	public ATag Create()
	{
		return new CurrentCommunityPageIDDataTag();
	}

}
