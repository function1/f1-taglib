package com.f1.custom.tags.data;


import com.plumtree.openfoundation.util.*;
import com.plumtree.openlog.OpenLogService;
import com.plumtree.openlog.OpenLogger;
import com.plumtree.portaluiinfrastructure.ptlink.*;
import com.plumtree.portaluiinfrastructure.statichelpers.PTDebugHelpers;
import com.plumtree.portaluiinfrastructure.tags.*;
import com.plumtree.portaluiinfrastructure.tags.metadata.*;
import com.plumtree.server.IPTQueryResult;
import com.plumtree.server.IPTSession;
import com.plumtree.server.PT_CLASSIDS;
import com.plumtree.server.PT_PROPIDS;
import com.plumtree.taskapi.portalui.*;
import com.plumtree.uiinfrastructure.uitasks.*;
import com.plumtree.xpshared.htmlelements.*;
import com.plumtree.taglib.ptdata.APTURLDODataTag;
import com.plumtree.taskapi.server.TaskAPIServerCommunity;
import com.plumtree.uiinfrastructure.activityspace.AActivitySpace;
import com.plumtree.uiinfrastructure.constants.AccessStyles;

/**
 * Implementation of the CommunityData PTURL DO Data Tag
 *
 */
 public class CommunityDataTag extends APTURLDODataTag
 {
         private static OpenLogger log = OpenLogService.GetLogger(
                 OpenLogService.GetComponent(PTDebugHelpers.COMPONENT_PORTAL_ADAPTIVETAGS),
                 CommunityDataTag.class);

         // All public static final metadata objects should be initialized
         // in a static initializer so that we can control the order of
         // initialization.
         public static final ITagMetaData TAG;
         public static final RequiredTagAttribute ATTRIB_COMMUNITYID;

         static
         {
                 // The TAG member variable needs to be initialized first since it
                 // can be used in other tags (i.e. as a RequiredParentTag) that
                 // are referenced by member variables of this tag (i.e. as a
                 // RelatedChildTag).  This is necessary for tags that have
                 // circular references such as a parent / child tag relationship.
                 TAG = new TagMetaData("communitydata",
                         "Returns information about the current community, including a URL to the home page and optionally the " +
                         "Community Name and ID, and the Community Page Name and ID. Returns nothing if user is not on a community page.");

                 ATTRIB_COMMUNITYID = new RequiredTagAttribute("commid",
                     "The Object ID of the Community to list pages from.",   
                     AttributeType.INT);

         
         }
         
         /**
          * @see com.plumtree.portaluiinfrastructure.tags.ATag#DisplayTag()
          */
         public HTMLElement DisplayTag()
         {
             IEnvironment env = GetEnvironment();        

             int nCommunityID = GetTagAttributeAsInt(ATTRIB_COMMUNITYID);             
             XPArrayList pages = GetCommunityPagesAsURLs(TaskAPIUICommon.GetEnvTypeObject(env), nCommunityID);
             XPArrayList subcommunities = TaskAPIUICommunity.GetSubCommununitiesAsURLs(TaskAPIUICommon.GetEnvTypeObject(env), nCommunityID);             

             // XPArrayList.InsertRange is buggy
             //subcommunities.InsertRange(0,pages);
             //SetPTURLDataCollection(subcommunities);
             
             subcommunities.AddRange(pages);
             SetPTURLDataCollection(subcommunities);
             
             return null;
         }


         
         /**
          * @see com.plumtree.portaluiinfrastructure.tags.ATag#Create()
          */
         public ATag Create()
         {
                 return new CommunityDataTag();
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

    /**
     * @param owner
     * @param nCommunityID
     * @return
     */
    public XPArrayList GetCommunityPagesAsURLs(AActivitySpace owner, int nCommunityID)
     {
         IPTSession session = TaskAPIUICommon.GetPTSession(owner);
          IPTQueryResult qrCommPages = TaskAPIServerCommunity.GetCommunityPages(session, nCommunityID);
          XPArrayList list = new XPArrayList(qrCommPages.RowCount());
          for ( int i  = 0; i < qrCommPages.RowCount(); i++ )
          {
                int nPageID = qrCommPages.ItemAsInt(i, PT_PROPIDS.PT_PROPID_OBJECTID);
                String strPageName  = qrCommPages.ItemAsString(i, PT_PROPIDS.PT_PROPID_NAME);
                IPTURL url;
                url = TaskAPIUICommunity.CreateCommunityPageCommonOpenerURL(owner, nCommunityID, nPageID, strPageName);
                url.SetURLData(PTURLConstants.KEY_CLASSID, XPConvert.ToString(PT_CLASSIDS.PT_PAGE_ID));
                url.SetURLData(PTURLConstants.KEY_OBJECTID, XPConvert.ToString(nPageID));
                list.Add(url);
          }
          return list;
     }
 
 }