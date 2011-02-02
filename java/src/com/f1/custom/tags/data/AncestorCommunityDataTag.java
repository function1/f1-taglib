package com.f1.custom.tags.data;

import java.util.ArrayList;
import java.util.List;

import com.plumtree.openfoundation.util.XPArrayList;
import com.plumtree.openfoundation.util.XPConvert;
import com.plumtree.openlog.OpenLogService;
import com.plumtree.openlog.OpenLogger;
import com.plumtree.portaluiinfrastructure.ptlink.IPTURL;
import com.plumtree.portaluiinfrastructure.ptlink.PTURLConstants;
import com.plumtree.portaluiinfrastructure.statichelpers.PTDebugHelpers;
import com.plumtree.portaluiinfrastructure.tags.ATag;
import com.plumtree.portaluiinfrastructure.tags.metadata.AttributeType;
import com.plumtree.portaluiinfrastructure.tags.metadata.ITagMetaData;
import com.plumtree.portaluiinfrastructure.tags.metadata.RequiredTagAttribute;
import com.plumtree.portaluiinfrastructure.tags.metadata.TagMetaData;
import com.plumtree.taglib.ptdata.APTURLDODataTag;
import com.plumtree.taskapi.portalui.TaskAPIUICommon;
import com.plumtree.taskapi.portalui.TaskAPIUICommunity;
import com.plumtree.uiinfrastructure.activityspace.AActivitySpace;
import com.plumtree.uiinfrastructure.constants.AccessStyles;
import com.plumtree.uiinfrastructure.uitasks.IEnvironment;
import com.plumtree.xpshared.htmlelements.HTMLElement;

public class AncestorCommunityDataTag extends APTURLDODataTag {

	@SuppressWarnings("unused")
	private static OpenLogger log = OpenLogService.GetLogger(OpenLogService
			.GetComponent(PTDebugHelpers.COMPONENT_PORTAL_ADAPTIVETAGS),
			AncestorCommunityDataTag.class);

	// All public static final metadata objects should be initialized
	// in a static initializer so that we can control the order of
	// initialization.
	public static final ITagMetaData TAG;
	
	public static final RequiredTagAttribute LEVELS_FROM_TOP_ATTRIBUTE;

	static {
		// The TAG member variable needs to be initialized first since it
		// can be used in other tags (i.e. as a RequiredParentTag) that
		// are referenced by member variables of this tag (i.e. as a
		// RelatedChildTag). This is necessary for tags that have
		// circular references such as a parent / child tag relationship.
		TAG = new TagMetaData(
				"ancestorcommunitydata",
				"Returns information about an ancestor to the current community, including a URL to the home page and the "
						+ "Community Name and ID. Returns nothing if user is not on a community page.");

		LEVELS_FROM_TOP_ATTRIBUTE = new RequiredTagAttribute("level", "Levels from the Top", AttributeType.INT);
	}

	/**
	 * @see com.plumtree.portaluiinfrastructure.tags.ATag#DisplayTag()
	 */
	@Override
	public HTMLElement DisplayTag() {
		final int levelFromTop = Math.max(
				GetTagAttributeAsInt(LEVELS_FROM_TOP_ATTRIBUTE), 0) + 1;

		final IEnvironment env = GetEnvironment();
		final AActivitySpace envActivitySpace = TaskAPIUICommon
				.GetEnvTypeObject(env);

		final List<Integer> parentCommunitiesList = new ArrayList<Integer>();
		int parentCommunityId = TaskAPIUICommunity
				.GetCurrentCommunityID(envActivitySpace);
		while ((parentCommunityId = TaskAPIUICommunity.GetParentCommunityID(
				envActivitySpace, parentCommunityId)) != 0) {
			parentCommunitiesList.add(Integer.valueOf(parentCommunityId));
		}

		final int communityId = parentCommunitiesList.get(
				parentCommunitiesList.size() - levelFromTop).intValue();

		final String communityName = TaskAPIUICommunity.GetCommunityName(
				envActivitySpace, communityId);
		final IPTURL url = TaskAPIUICommunity.CreateCommunityCommonOpenerURL(
				envActivitySpace, communityId, communityName);
		url.SetURLData(PTURLConstants.KEY_OBJECTID, XPConvert
				.ToString(communityId));

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
		return new AncestorCommunityDataTag();
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