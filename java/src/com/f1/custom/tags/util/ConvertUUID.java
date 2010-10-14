package com.f1.custom.tags.util;

import com.f1.custom.util.Commons;
import com.plumtree.portaluiinfrastructure.tags.ATag;
import com.plumtree.portaluiinfrastructure.tags.Scope;
import com.plumtree.portaluiinfrastructure.tags.metadata.AttributeType;
import com.plumtree.portaluiinfrastructure.tags.metadata.ITagMetaData;
import com.plumtree.portaluiinfrastructure.tags.metadata.OptionalTagAttribute;
import com.plumtree.portaluiinfrastructure.tags.metadata.RequiredTagAttribute;
import com.plumtree.portaluiinfrastructure.tags.metadata.TagMetaData;
import com.plumtree.server.IPTSession;
import com.plumtree.xpshared.htmlelements.HTMLElement;

/**
 * This tag is used to supplement the WCI tags which need obj ids. Given 
 * a uuid, the object id and class ids are stored in variables defined
 * by the objidVarName and classidVarName attributes. If these two
 * aren't defined, objID and classID are used as the variable names.
 * The variables are stored in the portlet's request scope and can be 
 * referenced by the $objID and $classID variables(default).
 *
 * Example Usage: <pt:f1.convertuuid pt:objidVarName="objID"
 * pt:classidVarName="classID" pt:uuid="{23434343434}"
 * xmlns:pt='http://www.plumtree.com/xmlschemas/ptui/' />
 *  
 * @copyright F1 2010
 * @author prabhud
 */
public class ConvertUUID extends ATag {
	public static final ITagMetaData TAG;
	public static final OptionalTagAttribute OBJID_NAME;
	public static final OptionalTagAttribute CLASSID_NAME;
	public static final RequiredTagAttribute UUID;
	public static final OptionalTagAttribute SCOPE;

	static {
		TAG = new TagMetaData("convertuuid",
				"Converts uuids to object and class ids.");

		OBJID_NAME = new OptionalTagAttribute("objidVarName",
				"Name of the object id variable whose value needs to be set.",
				AttributeType.STRING, "objID");
		CLASSID_NAME = new OptionalTagAttribute("classidVarName",
				"Name of the class id variable whose value needs to be set.",
				AttributeType.STRING, "classID");
		UUID = new RequiredTagAttribute("uuid",
				"The uuid of the object whose value needs to be converted.",
				AttributeType.STRING);
		SCOPE = new OptionalTagAttribute(
				"scope",
				"The scope used to store the variable (default- portlet request).",
				AttributeType.STRING, Scope.PORTLET_REQUEST.toString());

	}

	public ATag Create() {
		return new ConvertUUID();
	}

	public HTMLElement DisplayTag() {
		String objID = GetTagAttributeAsString(OBJID_NAME);
		String classID = GetTagAttributeAsString(CLASSID_NAME);
		String uuid = GetTagAttributeAsString(UUID);
		if (uuid != null) {
			SetStateSharedVariable(classID, Commons.getClassID(
					(IPTSession) GetEnvironment().GetUserSession(), uuid),
					Scope.GetScope(GetTagAttributeAsString(SCOPE)), true);
			SetStateSharedVariable(objID, Commons.getObjectID(
					(IPTSession) GetEnvironment().GetUserSession(), uuid),
					Scope.GetScope(GetTagAttributeAsString(SCOPE)), true);
		}
		return null;
	}

}
