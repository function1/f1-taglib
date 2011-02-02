package com.f1.custom.tags.standard;


import com.plumtree.openfoundation.util.XPArrayList;
import com.plumtree.portaluiinfrastructure.ptlink.IPTURL;
import com.plumtree.portaluiinfrastructure.tags.ATag;
import com.plumtree.portaluiinfrastructure.tags.Scope;
import com.plumtree.portaluiinfrastructure.tags.metadata.AttributeType;
import com.plumtree.portaluiinfrastructure.tags.metadata.OptionalTagAttribute;
import com.plumtree.server.IPTSession;
import com.plumtree.taskapi.portalui.TaskAPIUICommon;
import com.plumtree.taskapi.portalui.TaskAPIUICommunity;
import com.plumtree.uiinfrastructure.login.LoginHelper;
import com.plumtree.uiinfrastructure.uitasks.IEnvironment;
import com.plumtree.xpshared.htmlelements.HTMLComment;
import com.plumtree.xpshared.htmlelements.HTMLElement;
import com.plumtree.xpshared.htmlelements.HTMLElementCollection;
import com.plumtree.taglib.standard.*;

/**
 * The choose, when and otherwise tags allow you to insert
 * content on a page based on conditional statements of user
 * and group membership. The choose tag denotes the start of a
 * secured content section, and the when tags include a test
 * condition that defines who has access to the enclosed content.
 * The otherwise tags include content that should be displayed
 * as default.
 */
public class F1WhenTag extends WhenTag
{
	// All public static final metadata objects should be initialized
	// in a static initializer so that we can control the order of
	// initialization.
	
	public static final OptionalTagAttribute INT1;
	public static final OptionalTagAttribute INT2;
	public static final OptionalTagAttribute STR1;
	public static final OptionalTagAttribute STR2;
	public static final OptionalTagAttribute ATTRIB_COMMUNITYID;
	public static final OptionalTagAttribute COL;
	
	public static final String EMPTY_STRING = "";
	
	static
	{
		// The TAG member variable needs to be initialized first since it
		// can be used in other tags (i.e. as a RequiredParentTag) that
		// are referenced by member variables of this tag (i.e. as a
		// RelatedChildTag).  This is necessary for tags that have
		// circular references such as a parent / child tag relationship.
			
		INT1 = new OptionalTagAttribute("int1", "integer 1", AttributeType.INT, "-1");
		INT2 = new OptionalTagAttribute("int2", "integer 2", AttributeType.INT, "-2");

		STR1 = new OptionalTagAttribute("str1", "string 1", AttributeType.STRING, "str1");
		STR2 = new OptionalTagAttribute("str2", "string 2", AttributeType.STRING, "str2");
		
		ATTRIB_COMMUNITYID = new OptionalTagAttribute("commid","Object ID of the Community to retrieve Subcommunities from.,",AttributeType.INT, "1");
		
		COL = new OptionalTagAttribute("col", "collection", AttributeType.STRING, "optcollection");
	}

	private static final String ACL_GROUP_STRING_PREFIX = "stringToACLGroup('";
	private static final String ACL_GROUP_STRING_SUFFIX = "').isMember($currentuser)";

	private static final String GUEST_STRING = "isGuest($currentuser)";

	private static final String INT_COMPARE_STRING = "intsequal";
	private static final String STRING_COMPARE_STRING = "stringsequal";
	private static final String STRING_STARTSWITH_STRING = "stringstart";
	private static final String STRING_BLANK_STRING = "isBlank";
	
	private static final String CONTAINS_SUBCOMMUNITIES = "containssubcommunities";
	private static final String CONTAINS_PARENTCOMMUNITIES = "containsparentcommunities";

	private static final String COLLECTION_EMPTY_STRING = "isEmpty";
	private static final String PARAM_EXISTS_STRING = "paramExists"; // param exists in IPTURL
	
	/**
	 * @see com.plumtree.portaluiinfrastructure.tags.ATag#DisplayTag()
	 */
	public HTMLElement DisplayTag()
	{
		HTMLElement result = new HTMLElementCollection();

		boolean bHasAccess = false;

		// syntax: stringToACLGroup('id1,id2, id3').isMember($currentuser)
		// or
		// syntax: isGuest($currentuser)

		String strTestAttribute = GetTagAttributeAsString(TEST);

		int nACLGroupIndex = strTestAttribute.indexOf(ACL_GROUP_STRING_PREFIX);
		int nGuestIndex = strTestAttribute.indexOf(GUEST_STRING);
		int nIntCompareIndex = strTestAttribute.indexOf(INT_COMPARE_STRING);
		int nStringCompareIndex = strTestAttribute.indexOf(STRING_COMPARE_STRING);
		int nStringStartsWithIndex = strTestAttribute.indexOf(STRING_STARTSWITH_STRING);
		int nStringBlankIndex = strTestAttribute.indexOf(STRING_BLANK_STRING);

		int nContainsSubCommunitiesIndex = strTestAttribute.indexOf(CONTAINS_SUBCOMMUNITIES);
		int nContainsParentCommunitiesIndex = strTestAttribute.indexOf(CONTAINS_PARENTCOMMUNITIES);
		int nIsEmptyCompareIndex = strTestAttribute.indexOf(COLLECTION_EMPTY_STRING);
		int nParamExistsIndex = strTestAttribute.indexOf(PARAM_EXISTS_STRING);
		
		// If they are trying to do a user/group check
		if (-1 != nACLGroupIndex)
		{
			// Get the list of user and group IDs
			String strTestCase = strTestAttribute.substring(nACLGroupIndex + ACL_GROUP_STRING_PREFIX.length(),
				strTestAttribute.indexOf(ACL_GROUP_STRING_SUFFIX));
								
			// first check groups
			bHasAccess = F1WhenTag.CheckAccess(strTestCase, (IPTSession) GetEnvironment().GetUserSession(), result, true);
								
			// next check users
			if (!bHasAccess)
			{
				bHasAccess = F1WhenTag.CheckAccess(strTestCase, (IPTSession) GetEnvironment().GetUserSession(), result, false);
			}
		}
		// If they are trying to do a guest check
		else if (-1 != nGuestIndex)
		{
			// Check if they are guest.
			bHasAccess = LoginHelper.INSTANCE.GetIsGuestUser(GetEnvironment().GetUserSession());
		} 
		// If they are trying to do an integer comparison check
		else if(-1 != nIntCompareIndex ) 
		{
			try {
				int firstInt = GetTagAttributeAsInt(INT1);
				int secondInt = GetTagAttributeAsInt(INT2);

				if( firstInt == secondInt )
					bHasAccess = true;				
			} catch( Exception e ) {
				String strError = "Excecption thrown when doing integer comparison = " + e + ".";
				result.AddInnerHTMLElement(new HTMLComment(strError));				
			}
		} 
		// If they are trying to do an integer comparison check
		else if(-1 != nStringCompareIndex ) 
		{
			try 
			{
				String firstStr = GetTagAttributeAsString(STR1);
				String secondStr = GetTagAttributeAsString(STR2);

				if( firstStr.equals(secondStr) ) bHasAccess = true;				
			} 
			catch( Exception e ) 
			{
				String strError = "Excecption thrown when doing string equality = " + e + ".";
				result.AddInnerHTMLElement(new HTMLComment(strError));				
			}
		} 
		else if(-1 != nStringStartsWithIndex ) 
		{
			try 
			{
				String firstStr = GetTagAttributeAsString(STR1);
				String secondStr = GetTagAttributeAsString(STR2);

				if( firstStr.startsWith(secondStr) ) bHasAccess = true;				
			} 
			catch( Exception e ) 
			{
				String strError = "Excecption thrown when doing string comparison = " + e + ".";
				result.AddInnerHTMLElement(new HTMLComment(strError));				
			}
		}
		else if(-1 != nStringBlankIndex ) 
		{
			try 
			{
				String firstStr = GetTagAttributeAsString(STR1);

				if( firstStr.equals(EMPTY_STRING) ) bHasAccess = true;				
			} 
			catch( Exception e ) 
			{
				String strError = "Excecption thrown checking blank string = " + e + ".";
				result.AddInnerHTMLElement(new HTMLComment(strError));				
			}
		}
		// If they are checking for subcommunities data
		else if(-1 != nContainsSubCommunitiesIndex ) 
		{
			try 
			{
				IEnvironment env = GetEnvironment();	
				int nCommunityID = GetTagAttributeAsInt(ATTRIB_COMMUNITYID);
				XPArrayList urls = TaskAPIUICommunity.GetSubCommununitiesAsURLs(TaskAPIUICommon.GetEnvTypeObject(env), nCommunityID);
				int len = urls.GetSize();
				
				if( len > 0 ) bHasAccess = true;				
			}
			catch( Exception e ) 
			{
					String strError = "Excecption thrown when checking for subcommunities.";
					result.AddInnerHTMLElement(new HTMLComment(strError));				
			}
		}
		
		//	If they are checking for subcommunities data
		else if(-1 != nContainsParentCommunitiesIndex ) 
		{
			try 
			{
				IEnvironment env = GetEnvironment();	
				int nCommunityID = GetTagAttributeAsInt(ATTRIB_COMMUNITYID);
				IPTURL url = TaskAPIUICommunity.GetParentCommunityAsURL(TaskAPIUICommon.GetEnvTypeObject(env), nCommunityID);
				
				if( url != null ) bHasAccess = true;				
			}
			catch( Exception e ) 
			{
					String strError = "Excecption thrown when checking for parent communities.";
					result.AddInnerHTMLElement(new HTMLComment(strError));				
			}
		}
		
		else if(-1 != nIsEmptyCompareIndex ) 
		{
			String strDataKey = GetTagAttributeAsString(COL);
			Object oData = GetStateSharedVariable(strDataKey, Scope.TAG);
			if (null == oData)
			{
				oData = GetStateSharedVariable(strDataKey, Scope.PORTLET_REQUEST);
			}

			if (null == oData)
			{
				bHasAccess = true;
			}
			try
			{
				XPArrayList dataList = (XPArrayList) oData;
			} catch (Exception e)
			{
				bHasAccess = true;
			}			
		}
		else if(-1 != nParamExistsIndex ) 
		{
			try 
			{
				String strParamName = GetTagAttributeAsString(STR1);
				String strDataKey = GetTagAttributeAsString(COL);
				//IPTURL url = TaskAPIUICommunity.GetParentCommunityAsURL(TaskAPIUICommon.GetEnvTypeObject(env), nCommunityID);
				
				IPTURL url = (IPTURL) this.GetStateSharedVariable(strDataKey, Scope.TAG);
				
				if (url != null )
				{
					String paramStr = url.GetData(strParamName);
					if ((null != paramStr)&& (paramStr.length() > 0))
						bHasAccess = true;
				}
			}
			catch( Exception e ) 
			{
					String strError = "Excecption thrown when checking for url params.";
					result.AddInnerHTMLElement(new HTMLComment(strError));				
			}			
		}

		if (bHasAccess)
		{
			// Set that we have access so the otherwise tag won't display.
			SetStateVariable(ChooseTag.HAS_ACCESS_VARIABLE, new Boolean(true), Scope.PORTLET_REQUEST);

			result.AddInnerHTMLElement(ProcessTagBody());
		}

		return result;
	}

	public ATag Create()
	{
		return new F1WhenTag();
	}
}