package com.f1.custom.tags.data;

import com.plumtree.openfoundation.util.XPArrayList;
import com.plumtree.openfoundation.util.XPConvert;
import com.plumtree.openlog.OpenLogService;
import com.plumtree.openlog.OpenLogger;
import com.plumtree.portaluiinfrastructure.ptlink.IPTURL;
import com.plumtree.portaluiinfrastructure.ptlink.PTURLConstants;
import com.plumtree.portaluiinfrastructure.statichelpers.PTDebugHelpers;
import com.plumtree.portaluiinfrastructure.tags.ATag;
//import com.plumtree.portaluiinfrastructure.tags.metadata.AttributeType;
import com.plumtree.portaluiinfrastructure.tags.metadata.ITagMetaData;
//import com.plumtree.portaluiinfrastructure.tags.metadata.RequiredTagAttribute;
import com.plumtree.portaluiinfrastructure.tags.metadata.TagMetaData;
import com.plumtree.taglib.ptdata.APTURLDODataTag;
import com.plumtree.taskapi.portalui.TaskAPIUICommon;
import com.plumtree.taskapi.portalui.TaskAPIUICommunity;
import com.plumtree.uiinfrastructure.constants.AccessStyles;
import com.plumtree.uiinfrastructure.uitasks.IEnvironment;
import com.plumtree.xpshared.htmlelements.HTMLElement;

public class TopLevelCommunityDataTag extends APTURLDODataTag{
        
        private static OpenLogger log = OpenLogService.GetLogger(
                OpenLogService.GetComponent(PTDebugHelpers.COMPONENT_PORTAL_ADAPTIVETAGS),
                CommunityDataTag.class);

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
                TAG = new TagMetaData("toplevelcommunitydata",
                        "Returns information about the current community, including a URL to the home page and optionally the " +
                        "Community Name and ID, and the Community Page Name and ID. Returns nothing if user is not on a community page.");

        
        }
        
        /**
         * @see com.plumtree.portaluiinfrastructure.tags.ATag#DisplayTag()
         */
        public HTMLElement DisplayTag()
        {
            IEnvironment env = GetEnvironment();
            int toplevelid = TaskAPIUICommunity.GetCurrentCommunityID(TaskAPIUICommon.GetEnvTypeObject(env));
            int parentid = TaskAPIUICommunity.GetCurrentParentCommunityID(TaskAPIUICommon.GetEnvTypeObject(env));
            while (parentid != 0) {
                toplevelid = parentid;
                parentid = TaskAPIUICommunity.GetParentCommunityID(TaskAPIUICommon.GetEnvTypeObject(env),toplevelid);
            }
                        
            XPArrayList list = new XPArrayList(1);
            IPTURL url = TaskAPIUICommunity.CreateCommunityCommonOpenerURL(TaskAPIUICommon.GetEnvTypeObject(env),toplevelid,"test");
            url.SetURLData(PTURLConstants.KEY_OBJECTID, XPConvert.ToString(toplevelid));            
            list.Add(url);
            SetPTURLDataCollection(list);
            
            return null;
        }

        /**
         * @see com.plumtree.portaluiinfrastructure.tags.ATag#Create()
         */
        public ATag Create()
        {
                return new TopLevelCommunityDataTag();
        }
        
        /**
         * 
         * @see com.plumtree.portaluiinfrastructure.tags.ATag#SupportsAccessStyle(com.plumtree.uiinfrastructure.constants.AccessStyles)
         */
        public boolean SupportsAccessStyle(AccessStyles style)
        {
                // This tag does not display any JavaScript, and therefore
                // supports all access styles.
                return true;
        }

    }

