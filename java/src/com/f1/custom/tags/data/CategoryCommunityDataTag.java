package com.f1.custom.tags.data;

import com.plumtree.openfoundation.util.XPArrayList;
import com.plumtree.openfoundation.util.XPConvert;
import com.plumtree.openlog.OpenLogService;
import com.plumtree.openlog.OpenLogger;
import com.plumtree.portaluiinfrastructure.ptlink.IPTURL;
import com.plumtree.portaluiinfrastructure.ptlink.PTURLConstants;
import com.plumtree.portaluiinfrastructure.statichelpers.PTDebugHelpers;
import com.plumtree.portaluiinfrastructure.tags.ATag;
import com.plumtree.portaluiinfrastructure.tags.metadata.ITagMetaData;
import com.plumtree.portaluiinfrastructure.tags.metadata.TagMetaData;
import com.plumtree.taglib.ptdata.APTURLDODataTag;
import com.plumtree.taskapi.portalui.TaskAPIUICommon;
import com.plumtree.taskapi.portalui.TaskAPIUICommunity;
import com.plumtree.uiinfrastructure.activityspace.AActivitySpace;
import com.plumtree.uiinfrastructure.constants.AccessStyles;
import com.plumtree.uiinfrastructure.uitasks.IEnvironment;
import com.plumtree.xpshared.htmlelements.HTMLElement;

public class CategoryCommunityDataTag extends APTURLDODataTag
{

	@SuppressWarnings("unused")
	private static OpenLogger log = OpenLogService.GetLogger(OpenLogService
			.GetComponent(PTDebugHelpers.COMPONENT_PORTAL_ADAPTIVETAGS),
			CategoryCommunityDataTag.class);

	// All public static final metadata objects should be initialized
	// in a static initializer so that we can control the order of
	// initialization.
	public static final ITagMetaData TAG;

	static {
		// The TAG member variable needs to be initialized first since it
		// can be used in other tags (i.e. as a RequiredParentTag) that
		// are referenced by member variables of this tag (i.e. as a
		// RelatedChildTag). This is necessary for tags that have
		// circular references such as a parent / child tag relationship.
		TAG = new TagMetaData(
				"categorycommunitydata",
				"Returns information about the current community, including a URL to the home page and optionally the "
						+ "Community Name and ID, and the Community Page Name and ID. Returns nothing if user is not on a community page.");

	}

	/**
	 * @see com.plumtree.portaluiinfrastructure.tags.ATag#DisplayTag()
	 */
	@Override
	public HTMLElement DisplayTag() {
		final IEnvironment env = GetEnvironment();
		final AActivitySpace envActivitySpace = TaskAPIUICommon
				.GetEnvTypeObject(env);
		int categoryid = TaskAPIUICommunity
				.GetCurrentCommunityID(envActivitySpace);
		int parentid = TaskAPIUICommunity
				.GetCurrentParentCommunityID(envActivitySpace);

		int tempid;
		while (parentid != 0) {
			tempid = parentid;
			parentid = TaskAPIUICommunity.GetParentCommunityID(
					envActivitySpace, tempid);
			if (parentid != 0) {
				categoryid = tempid;
			}
		}

		final String communityName = TaskAPIUICommunity.GetCommunityName(
				envActivitySpace, categoryid);
		
		final IPTURL url = TaskAPIUICommunity.CreateCommunityCommonOpenerURL(
				envActivitySpace, categoryid, communityName);
		url.SetURLData(PTURLConstants.KEY_OBJECTID, XPConvert
				.ToString(categoryid));
		
		final XPArrayList urlList = new XPArrayList(1);
		urlList.Add(url);

		SetPTURLDataCollection(urlList);

		return null;
	}

	/**
	 * @see com.plumtree.portaluiinfrastructure.tags.ATag#Create()
	 */
	@Override
	public ATag Create() {
		return new CategoryCommunityDataTag();
	}

	/**
	 * 
	 * @see com.plumtree.portaluiinfrastructure.tags.ATag#SupportsAccessStyle(com.plumtree.uiinfrastructure.constants.AccessStyles)
	 */
	@Override
	public boolean SupportsAccessStyle(AccessStyles style) {
		// This tag does not display any JavaScript, and therefore
		// supports all access styles.
		return true;
	}
}