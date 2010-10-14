package com.f1.custom.util;

import java.io.PrintWriter;
import java.io.StringWriter;

import com.plumtree.openfoundation.util.XPStringUtility;
import com.plumtree.openlog.OpenLogService;
import com.plumtree.openlog.OpenLogger;
import com.plumtree.server.IPTMigrationManager;
import com.plumtree.server.IPTPageInfo;
import com.plumtree.server.IPTPageManager;
import com.plumtree.server.IPTSession;
import com.plumtree.server.IPTSessionInfo;
import com.plumtree.server.PT_ACCESS_LEVELS;
import com.plumtree.server.PT_GLOBALOBJECTS;
import com.plumtree.server.PT_MIGRATION_OBJECT_COLS;
import com.plumtree.taskapi.portalui.TaskAPIUICommon;
import com.plumtree.taskapi.portalui.TaskAPIUICommunity;
import com.plumtree.uiinfrastructure.activityspace.AActivitySpace;
import com.plumtree.uiinfrastructure.statichelpers.ImgSvrURLHelper;

/**
 * 
 * @copyright F1 2010
 * @author prabhud
 */
public class Commons {

	private static OpenLogger log = OpenLogService.GetLogger(
			OpenLogService.GetComponent("Utility"),
			"com.f1.custom.tags.util.Utility");

	/**
	 * This returns an Object array of the object and class id for a given
	 * object whose uuid is passed.
	 * 
	 * @param userSession
	 * @param uuid
	 * @return
	 */
	public static Object[] getClassObjectIDs(IPTSession userSession, String uuid) {
		Object[] classObjectID = ((IPTMigrationManager) userSession
				.OpenGlobalObject(PT_GLOBALOBJECTS.PT_GLOBAL_MIGRATION_MANAGER,
						false)).UUIDToObjectID(uuid);
		return classObjectID;

	}

	/**
	 * This returns the object id for a given object whose uuid is passed.
	 * 
	 * @param userSession
	 * @param uuid
	 * @return
	 */

	public static int getObjectID(IPTSession userSession, String uuid) {
		Object[] classObjectID = getClassObjectIDs(userSession, uuid);
		if (classObjectID != null) {
			return Integer
					.parseInt(classObjectID[PT_MIGRATION_OBJECT_COLS.PT_MOC_OBJECTID]
							.toString());
		}
		return -1;
	}

	/**
	 * This returns the class id for a given object whose uuid is passed.
	 * 
	 * @param userSession
	 * @param uuid
	 * @return
	 */

	public static int getClassID(IPTSession userSession, String uuid) {
		Object[] classObjectID = getClassObjectIDs(userSession, uuid);
		if (classObjectID != null) {
			return Integer
					.parseInt(classObjectID[PT_MIGRATION_OBJECT_COLS.PT_MOC_CLASSID]
							.toString());
		}
		return -1;
	}

	/**
	 * This return the image server url upto the /custom folder
	 * 
	 * @param m_asOwner
	 * @return
	 */
	public static String getImageServerCustomURL(AActivitySpace m_asOwner) {
		String imageServerCustomBaseURL = ImgSvrURLHelper.GetInstance()
				.GetCustomURL(m_asOwner.GetIsSecuredSpace(), "portal");
		StringBuffer strMyImageServerBaseURL = new StringBuffer(
				XPStringUtility.ForceEndsWith(imageServerCustomBaseURL, "/"));

		return strMyImageServerBaseURL.toString();

	}

	/**
	 * This returns the stack trace as a string
	 * 
	 * @param e
	 * @return
	 */
	public static String getStackTraceAsString(Exception e) {
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		pw.print(" [ ");
		pw.print(e.getClass().getName());
		pw.print(" ] ");
		pw.print(e.getMessage());
		e.printStackTrace(pw);
		return sw.toString();
	}

	/**
	 * Returns the next higher access level
	 * 
	 * @param accessLevel
	 * @return
	 */
	public static int getHigherAccessLevel(int accessLevel) {
		switch (accessLevel) {
		case PT_ACCESS_LEVELS.PT_ACCESS_LEVEL_NONE:
			return PT_ACCESS_LEVELS.PT_ACCESS_LEVEL_READ;
		case PT_ACCESS_LEVELS.PT_ACCESS_LEVEL_READ:
			return PT_ACCESS_LEVELS.PT_ACCESS_LEVEL_SELECT;
		case PT_ACCESS_LEVELS.PT_ACCESS_LEVEL_SELECT:
			return PT_ACCESS_LEVELS.PT_ACCESS_LEVEL_EDIT;
		case PT_ACCESS_LEVELS.PT_ACCESS_LEVEL_EDIT:
			return PT_ACCESS_LEVELS.PT_ACCESS_LEVEL_ADMIN;
		case PT_ACCESS_LEVELS.PT_ACCESS_LEVEL_ADMIN:
			return PT_ACCESS_LEVELS.PT_ACCESS_LEVEL_ADMIN;
		default:
			return PT_ACCESS_LEVELS.PT_ACCESS_LEVEL_NONE;
		}
	}

	/**
	 * Is community page accessible based on the requested access level and
	 * comparison operator.
	 * 
	 * @param space
	 * @param comparisonOperator
	 * @param pid
	 * @param accessLevel
	 * @return
	 */
	public static boolean isCommunityPageAccessible(AActivitySpace space,
			String comparisonOperator, int pid, int accessLevel) {
		if (pid == -1) {
			pid = TaskAPIUICommunity.GetCurrentCommunityPageID(space);
		}
		return isVisible(Constants.PAGE, space, comparisonOperator, pid,
				accessLevel);
	}

	private static boolean isVisible(String type, AActivitySpace space,
			String comparisonOperator, int objID, int accessLevel) {
		int higherAccessLevel = getHigherAccessLevel(accessLevel);
		if (Constants.GREATER_THAN_OR_EQUALTO
				.equalsIgnoreCase(comparisonOperator)) {
			return isCommunityOrPageVisible(type, space, objID, accessLevel);
		}

		if (Constants.GREATER_THAN.equalsIgnoreCase(comparisonOperator)) {
			if (accessLevel == PT_ACCESS_LEVELS.PT_ACCESS_LEVEL_ADMIN) {
				return false;// nobody has greater access level than admin
			} else {
				return isCommunityOrPageVisible(type, space, objID,
						higherAccessLevel);
			}
		}

		if (Constants.LESSER_THAN.equalsIgnoreCase(comparisonOperator)) {
			return !(isCommunityOrPageVisible(type, space, objID, accessLevel));
		}
		if (Constants.LESSER_THAN_OR_EQUALTO
				.equalsIgnoreCase(comparisonOperator)) {
			if (accessLevel == PT_ACCESS_LEVELS.PT_ACCESS_LEVEL_ADMIN) {
				return true;// every access level is <= admin
			} else {
				return !(isCommunityOrPageVisible(type, space, objID,
						higherAccessLevel));
			}
		}
		if (Constants.EQUALTO.equalsIgnoreCase(comparisonOperator)) {
			if (accessLevel == PT_ACCESS_LEVELS.PT_ACCESS_LEVEL_ADMIN) {
				return isCommunityOrPageVisible(type, space, objID, accessLevel);
			} else {
				// make sure the user has access only equal to this level and
				// nothing above it
				return (isCommunityOrPageVisible(type, space, objID,
						accessLevel) && !(isCommunityOrPageVisible(type, space,
						objID, higherAccessLevel)));
			}
		}
		if (Constants.NOT_EQUALTO.equalsIgnoreCase(comparisonOperator)) {
			if (accessLevel == PT_ACCESS_LEVELS.PT_ACCESS_LEVEL_ADMIN) {
				return !isCommunityOrPageVisible(type, space, objID,
						accessLevel);
			} else {
				return (!isCommunityOrPageVisible(type, space, objID,
						accessLevel) || (isCommunityOrPageVisible(type, space,
						objID, accessLevel) && isCommunityOrPageVisible(type,
						space, objID, higherAccessLevel)));
			}
		}
		return false;
	}

	/**
	 * 
	 * Is community page visible?
	 * 
	 * @param space
	 * @param pageID
	 * @param accessLevel
	 * @return
	 */
	public static boolean isCommunityPageVisible(AActivitySpace space,
			int pageID, int accessLevel) {
		return checkUserAccessLevelToCurrentCommunityPage(space, pageID,
				accessLevel);
	}

	/**
	 * this function should be present in TaskAPIUICommunity. But unfortunately
	 * it isnt.
	 * 
	 * @param owner
	 * @param pid
	 * @param accessLevel
	 * @return
	 */
	private static boolean checkUserAccessLevelToCurrentCommunityPage(
			AActivitySpace owner, int pid, int accessLevel) {
		try {
			IPTSession ptSession = TaskAPIUICommon.GetPTSession(owner);
			IPTPageInfo ptPageInfo = ((IPTPageManager) ptSession.GetPages())
					.CachedOpenPageInfo(pid, false, false);
			return accessLevel <= ptPageInfo.GetAccessLevel();
		} catch (Exception e) {
			log.Error(getStackTraceAsString(e));
		}
		return false;
	}

	/**
	 * Is community accessible based on the requested access level and
	 * comparison operator.
	 * 
	 * @param space
	 * @param comparisonOperator
	 * @param cid
	 * @param accessLevel
	 * @return
	 */
	public static boolean isCommunityAccessible(AActivitySpace space,
			String comparisonOperator, int cid, int accessLevel) {
		if (cid == -1) {
			cid = TaskAPIUICommunity.GetCurrentCommunityID(space);
		}
		return isVisible(Constants.COMMUNITY, space, comparisonOperator, cid,
				accessLevel);
	}

	/**
	 * 
	 * @param space
	 * @param objID
	 * @param accessLevel
	 * @param objType
	 * @return
	 */
	private static boolean isCommunityOrPageVisible(String objType,
			AActivitySpace space, int objID, int accessLevel) {
		if (Constants.PAGE.equalsIgnoreCase(objType)) {
			return isCommunityPageVisible(space, objID, accessLevel);
		} else if (Constants.COMMUNITY.equalsIgnoreCase(objType)) {
			return isCommunityVisible(space, objID, accessLevel);
		}
		return false;
	}

	/**
	 * Is community visible?
	 * 
	 * @param space
	 * @param cid
	 * @param accessLevel
	 * @return
	 */
	public static boolean isCommunityVisible(AActivitySpace space, int cid,
			int accessLevel) {
		return TaskAPIUICommunity.CheckUserAccessLevelToCommunity(space,
				accessLevel, cid);
	}

	/**
	 * Is portlet accessible to user based on the requested access level and
	 * comparison operator.
	 * 
	 * @param iptSession
	 * @param comparisonOperator
	 * @param accessLevel
	 * @param portletID
	 * @return
	 */
	public static boolean isPortletAccessible(IPTSession iptSession,
			String comparisonOperator, int accessLevel, int portletID) {

		IPTSessionInfo iptSessionInfo = iptSession.GetSessionInfo();
		int userAccessLevel = iptSessionInfo.CheckGadgetAccess(portletID);
		log.Debug("Current access level is " + userAccessLevel);

		if (Constants.GREATER_THAN_OR_EQUALTO
				.equalsIgnoreCase(comparisonOperator)) {
			return userAccessLevel >= accessLevel;
		}
		if (Constants.GREATER_THAN.equalsIgnoreCase(comparisonOperator)) {
			if (accessLevel == PT_ACCESS_LEVELS.PT_ACCESS_LEVEL_ADMIN) {
				return false;// nobody has access level greater than admin
			} else {
				return userAccessLevel > accessLevel;
			}
		}
		if (Constants.LESSER_THAN.equalsIgnoreCase(comparisonOperator)) {
			return userAccessLevel < accessLevel;
		}
		if (Constants.LESSER_THAN_OR_EQUALTO
				.equalsIgnoreCase(comparisonOperator)) {
			if (accessLevel == PT_ACCESS_LEVELS.PT_ACCESS_LEVEL_ADMIN) {
				return true;// everybody's access level is less or equal than
							// ADMIN
			} else {
				return userAccessLevel <= accessLevel;
			}
		}
		if (Constants.EQUALTO.equalsIgnoreCase(comparisonOperator)) {
			return userAccessLevel == accessLevel;
		}
		if (Constants.NOT_EQUALTO.equalsIgnoreCase(comparisonOperator)) {
			return userAccessLevel != accessLevel;
		}
		return false;
	}

}
