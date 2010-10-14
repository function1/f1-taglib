package com.f1.custom.tags.util;

import com.plumtree.portaluiinfrastructure.tags.ATag;
import com.plumtree.portaluiinfrastructure.tags.Scope;
import com.plumtree.portaluiinfrastructure.tags.metadata.AttributeType;
import com.plumtree.portaluiinfrastructure.tags.metadata.ITagMetaData;
import com.plumtree.portaluiinfrastructure.tags.metadata.OptionalTagAttribute;
import com.plumtree.portaluiinfrastructure.tags.metadata.RequiredTagAttribute;
import com.plumtree.portaluiinfrastructure.tags.metadata.TagMetaData;
import com.plumtree.xpshared.htmlelements.HTMLElement;

/**
 * Example Usage: <pt:f1.bcvariable pt:name="test"
 * xmlns:pt='http://www.plumtree.com/xmlschemas/ptui/'> test
 * value</pt:f1.bcvariable>
 * 
 * @copyright F1 2010
 * @author prabhud
 */
public class BodyContentVariable extends ATag {
	public static final ITagMetaData TAG;
	public static final RequiredTagAttribute NAME;
	public static final OptionalTagAttribute SCOPE;

	static {
		TAG = new TagMetaData("bcvariable",
				"Puts the tag's body to the variable specified by the NAME attribute.");

		NAME = new RequiredTagAttribute("name",
				"Name of the variable whose value needs to be set.",
				AttributeType.STRING);
		SCOPE = new OptionalTagAttribute(
				"scope",
				"The scope used to store the variable (default- portlet request).",
				AttributeType.STRING, Scope.PORTLET_REQUEST.toString());

	}

	public ATag Create() {
		return new BodyContentVariable();
	}

	public HTMLElement DisplayTag() {
		SetStateSharedVariable(GetTagAttributeAsString(NAME), ProcessTagBody()
				.GetInnerHTML(),
				Scope.GetScope(GetTagAttributeAsString(SCOPE)), true);
		return null;
	}

}
