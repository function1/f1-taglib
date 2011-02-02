package com.f1.custom.tags.data;

import com.plumtree.openfoundation.util.XPArrayList;
import com.plumtree.portaluiinfrastructure.tags.ATag;
import com.plumtree.portaluiinfrastructure.tags.Scope;
import com.plumtree.portaluiinfrastructure.tags.metadata.ITagMetaData;
import com.plumtree.portaluiinfrastructure.tags.metadata.RequiredTagAttribute;
import com.plumtree.portaluiinfrastructure.tags.metadata.TagMetaData;
import com.plumtree.taglib.ptdata.APTURLDODataTag;
import com.plumtree.xpshared.htmlelements.HTMLComment;
import com.plumtree.xpshared.htmlelements.HTMLElement;

/**
 * Implementation of the CommunityPages PTURL DO Data Tag
 *
 */
public class ClearDataTag extends APTURLDODataTag
{
	// All public static final metadata objects should be initialized
	// in a static initializer so that we can control the order of
	// initialization.
	public static final ITagMetaData TAG;
	public static final RequiredTagAttribute DATA;

	static
	{
		// The TAG member variable needs to be initialized first since it
		// can be used in other tags (i.e. as a RequiredParentTag) that
		// are referenced by member variables of this tag (i.e. as a
		// RelatedChildTag).  This is necessary for tags that have
		// circular references such as a parent / child tag relationship.
		TAG = new TagMetaData("cleardata", COMMON_PRETAGMETADATA_DESC + "Clears a data collection list " + COMMON_POSTTAGMETADATA_DESC);

		DATA = new RequiredTagAttribute("data",
				"The key used to store the XPArrayList data collection as a shread variable in Tag or " +
				"or PortletRequest scope memory.");
	}

	/**
	 * @see com.plumtree.portaluiinfrastructure.tags.ATag#DisplayTag()
	 */
	public HTMLElement DisplayTag()
	{
		String strDataKey = GetTagAttributeAsString(DATA);

		Object oData = GetStateSharedVariable(strDataKey, Scope.TAG);
		if (null == oData)
		{
			oData = GetStateSharedVariable(strDataKey, Scope.PORTLET_REQUEST);
		}

		if (null == oData)
		{
			String strError = "Unable to locate data collection " +
				strDataKey + " in clear tag.";

			return new HTMLComment(strError);
		}

		XPArrayList dataList;

		try
		{
			dataList = (XPArrayList) oData;
		} 
		catch (Exception e)
		{
			return new HTMLComment("Unable to cast data collection " +
				strDataKey + " to an XPArrayList in clear tag: " +
				e.getMessage());
		}

		Scope tagScope = Scope.GetScope( GetTagAttributeAsString(ATTRIB_SCOPE));
		SetStateSharedVariable(strDataKey, null, tagScope, false);

		return null;
	}

	/**
	 * @see com.plumtree.portaluiinfrastructure.tags.ATag#Create()
	 */
	public ATag Create()
	{
		return new ClearDataTag();
	}
}
