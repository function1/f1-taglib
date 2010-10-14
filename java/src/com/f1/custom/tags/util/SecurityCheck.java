package com.f1.custom.tags.util;

import com.f1.custom.util.Commons;
import com.f1.custom.util.Constants;
import com.plumtree.openlog.OpenLogService;
import com.plumtree.openlog.OpenLogger;
import com.plumtree.portaluiinfrastructure.tags.ATag;
import com.plumtree.portaluiinfrastructure.tags.metadata.AttributeType;
import com.plumtree.portaluiinfrastructure.tags.metadata.ITagMetaData;
import com.plumtree.portaluiinfrastructure.tags.metadata.OptionalTagAttribute;
import com.plumtree.portaluiinfrastructure.tags.metadata.RequiredTagAttribute;
import com.plumtree.portaluiinfrastructure.tags.metadata.TagMetaData;
import com.plumtree.server.IPTSession;
import com.plumtree.server.PT_ACCESS_LEVELS;
import com.plumtree.uiinfrastructure.activityspace.AActivitySpace;
import com.plumtree.xpshared.htmlelements.HTMLElement;
import com.plumtree.xpshared.htmlelements.HTMLSpan;

/**
 * 
 * Example Usage: <pt:f1.securitycheck pt:objType="portlet"
 * pt:accessLevel="edit" pt:objId="200" pt:compare="gte"
 * xmlns:pt='http://www.plumtree.com/xmlschemas/ptui/'> Show or dont show this
 * </pt:f1.securitycheck>
 * 
 * @copyright F1 2010
 * @author prabhud
 */
public class SecurityCheck extends ATag {
	public static final ITagMetaData TAG;
	public static final OptionalTagAttribute OBJ_ID;
	public static final RequiredTagAttribute OBJ_TYPE;
	public static final RequiredTagAttribute ACCESS_LEVEL;
	public static final RequiredTagAttribute COMPARISON_OPERATOR;

	private static OpenLogger log = OpenLogService.GetLogger(
			OpenLogService.GetComponent("SecurityCheck"),
			"com.f1.custom.tags.SecurityCheck");

	static {
		TAG = new TagMetaData("securitycheck",
				"Determines if a user has certain level of access.");

		OBJ_ID = new OptionalTagAttribute("objId",
				"ID for the object whose security should be checked.",
				AttributeType.INT, "-1");

		OBJ_TYPE = new RequiredTagAttribute(
				"objType",
				"Type of the object, valid values being portlet, community & page.",
				AttributeType.STRING);

		ACCESS_LEVEL = new RequiredTagAttribute(
				"accessLevel",
				"Access level to check against, valid values being read, select, edit & admin.",
				AttributeType.STRING);

		COMPARISON_OPERATOR = new RequiredTagAttribute(
				"compare",
				"Comparison Operator to compare access level, valid values being gte, gt, lte, lt, eq & ne.",
				AttributeType.STRING);
	}

	public ATag Create() {
		return new SecurityCheck();
	}

	public HTMLElement DisplayTag() {
		int objID = GetTagAttributeAsInt(OBJ_ID);
		String objType = GetTagAttributeAsString(OBJ_TYPE);
		String accessLevel = GetTagAttributeAsString(ACCESS_LEVEL);
		String compare = GetTagAttributeAsString(COMPARISON_OPERATOR);

		log.Debug("object id-" + objID + ":object type-" + objType
				+ ":access level-" + accessLevel + ":compare-" + compare);
		boolean hasAccess = false;
		if (isValidObjectType(objType) && isValidAccessLevel(accessLevel)
				&& isValidComparisonOperator(compare)) {
			log.Debug("valid");
			if ("portlet".equalsIgnoreCase(objType)) {
				hasAccess = Commons.isPortletAccessible(
						(IPTSession) GetEnvironment().GetUserSession(),
						compare, getAccessLevel(accessLevel), objID);
			}
			if ("page".equalsIgnoreCase(objType)) {
				hasAccess = Commons.isCommunityPageAccessible(
						(AActivitySpace) GetEnvironment(), compare, objID,
						getAccessLevel(accessLevel));
			}
			if ("community".equalsIgnoreCase(objType)) {
				hasAccess = Commons.isCommunityAccessible(
						(AActivitySpace) GetEnvironment(), compare, objID,
						getAccessLevel(accessLevel));
			}
		}
		log.Debug("has access=" + hasAccess);
		if (hasAccess) {
			HTMLElement output = new HTMLSpan();
			output.AddInnerHTMLElement(ProcessTagBody());
			return output;
		}
		return null;
	}

	private boolean isValidObjectType(String objType) {
		return Constants.PAGE.equalsIgnoreCase(objType)
				|| Constants.COMMUNITY.equalsIgnoreCase(objType)
				|| Constants.PORTLET.equalsIgnoreCase(objType);
	}

	private boolean isValidAccessLevel(String accessLevel) {
		return Constants.READ.equalsIgnoreCase(accessLevel)
				|| Constants.SELECT.equalsIgnoreCase(accessLevel)
				|| Constants.EDIT.equalsIgnoreCase(accessLevel)
				|| Constants.ADMIN.equalsIgnoreCase(accessLevel);
	}

	private boolean isValidComparisonOperator(String compare) {
		return Constants.GREATER_THAN_OR_EQUALTO.equalsIgnoreCase(compare)
				|| Constants.GREATER_THAN.equalsIgnoreCase(compare)
				|| Constants.LESSER_THAN_OR_EQUALTO.equalsIgnoreCase(compare)
				|| Constants.LESSER_THAN.equalsIgnoreCase(compare)
				|| Constants.EQUALTO.equalsIgnoreCase(compare)
				|| Constants.NOT_EQUALTO.equalsIgnoreCase(compare);
	}

	private int getAccessLevel(String accessLevel) {
		if (Constants.READ.equalsIgnoreCase(accessLevel)) {
			return PT_ACCESS_LEVELS.PT_ACCESS_LEVEL_READ;
		}
		if (Constants.SELECT.equalsIgnoreCase(accessLevel)) {
			return PT_ACCESS_LEVELS.PT_ACCESS_LEVEL_SELECT;
		}
		if (Constants.EDIT.equalsIgnoreCase(accessLevel)) {
			return PT_ACCESS_LEVELS.PT_ACCESS_LEVEL_EDIT;
		}
		if (Constants.ADMIN.equalsIgnoreCase(accessLevel)) {
			return PT_ACCESS_LEVELS.PT_ACCESS_LEVEL_ADMIN;
		}
		return PT_ACCESS_LEVELS.PT_ACCESS_LEVEL_NONE;
	}

}
