package com.f1.custom.tags.ui;

import com.plumtree.openfoundation.web.*;
import com.plumtree.portaluiinfrastructure.htmlconstructs.*;
import com.plumtree.portaluiinfrastructure.ptlink.*;
import com.plumtree.portaluiinfrastructure.tags.*;
import com.plumtree.portaluiinfrastructure.tags.metadata.*;
import com.plumtree.taskapi.portalui.*;
import com.plumtree.uiinfrastructure.activityspace.*;
import com.plumtree.uiinfrastructure.login.*;
import com.plumtree.xpshared.htmlelements.*;

import com.plumtree.taglib.ptui.LoginLinkTag;

/**
 * This tag displays a login/logout link to the Plumtree login page.
 */
public class F1LoginLinkTag extends LoginLinkTag
{
	// All public static final metadata objects should be initialized
	// in a static initializer so that we can control the order of
	// initialization.
	
	public static final OptionalTagAttribute SECURE;

	static
	{
		// The TAG member variable needs to be initialized first since it
		// can be used in other tags (i.e. as a RequiredParentTag) that
		// are referenced by member variables of this tag (i.e. as a
		// RelatedChildTag).  This is necessary for tags that have
		// circular references such as a parent / child tag relationship.
		SECURE = new OptionalTagAttribute("secure",
			"This attribute specifies whether or not to use a secure log off.",
			AttributeType.STRING, "");

	}

	/**
	 * @see com.plumtree.portaluiinfrastructure.tags.ATag#DisplayTag()
	 */
	public HTMLElement DisplayTag()
	{
		
		IPTURL link = TaskAPIUIUser.GetURLForLoginLogoff((AActivitySpace) GetEnvironment());
		// no link to display
		if ( null == link )
		{
			return new HTMLGenericElement("");
		}

		
		String strURL;
		String strStyleID;
		String secure = GetTagAttributeAsString(SECURE);
		
		if (secure.equals("true"))
		{
			strURL = link.GetFullURL();
			strURL = strURL.replaceFirst("http", "https");
		}
		else
		{
			strURL = link.GetFullURL();
		}
		
		if (LoginHelper.INSTANCE.GetIsGuestUser(GetEnvironment().GetUserSession()))
		{
			strStyleID = PTStyleID.PT_LOGIN_LINK;

			String strRedirect = GetTagAttributeAsString(REDIRECT);

			if (!"".equals(strRedirect))
			{
				if (GetTagAttributeAsBoolean(USE_BASE_URL_FOR_REDIRECT))
				{
					strRedirect = TaskAPIUICommon.GetEnvTypeObject(GetEnvironment()).GetCurrentNormalizedBaseURL() +
						strRedirect;
				}

				strURL += LinkConstants.AMPERSAND + ASConstants.REDIRECT_AFTER_LOGIN + LinkConstants.EQUALS +
					XPHttpUtility.UrlEncode(strRedirect, LinkConstants.UTF_8_ENCODING);
			}
		} else
		{
			strStyleID = PTStyleID.PT_LOGOFF_LINK;
		}

		HTMLElement anchor = new HTMLAnchor(strURL);

		// This adds either the inner HTML or the default text to the link.
		AddContentToLink(anchor, link.GetData(PTURLConstants.KEY_TITLE));

		return AddSpanToFinalOutput(anchor, strStyleID);
	}

	public ATag Create()
	{
		return new F1LoginLinkTag();
	}
}