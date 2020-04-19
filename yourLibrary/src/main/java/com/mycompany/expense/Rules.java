package com.mycompany.expense;

/**
 * This class contains methods encoding the rules for how a
 * report in the sample expense report application may be
 * handled by the current user.
 *
 * @author Hans Bergsten, Gefion Software <hans@gefionsoftware.com>
 * @version 1.0
 */
public class Rules {
    /**
     * Returns "true" if the user is the report owner and the report
     * isn't locked.
     */
    public boolean canEdit(String user, boolean isManager, Report report) {
	return report.getOwner().equals(user) && !isLocked(report);
    }

    /**
     * Returns "true" if the user is the report owner and the report
     * isn't locked.
     */
    public boolean canDelete(String user, boolean isManager, Report report) {
	return report.getOwner().equals(user) && !isLocked(report);
    }

    /**
     * Returns "true" if the user is the report owner and the report
     * isn't locked.
     */
    public boolean canSubmit(String user, boolean isManager, Report report) {
	return report.getOwner().equals(user) && !isLocked(report);
    }

    /**
     * Returns "true" if the user is a manager and the report is 
     * "submitted".
     */
    public boolean canAccept(String user, boolean isManager, Report report) {
	return isManager && report.getStatus() == Report.STATUS_SUBMITTED;
    }

    /**
     * Returns "true" if the user is a manager and the report is 
     * "submitted".
     */
    public boolean canReject(String user, boolean isManager, Report report) {
	return isManager && report.getStatus() == Report.STATUS_SUBMITTED;
    }

    /**
     * Returns "true" if the user is a manager or the report owner.
     */
    public boolean canView(String user, boolean isManager, Report report) {
	return isManager || report.getOwner().equals(user);
    }

    /**
     * Returns "true" if the report is either "submitted" or "accepted".
     */
    public boolean isLocked(Report report) {
	return report.getStatus() == Report.STATUS_SUBMITTED ||
	    report.getStatus() == Report.STATUS_ACCEPTED;
    }
}
