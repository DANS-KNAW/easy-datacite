package nl.knaw.dans.common.lang.dataset;

import nl.knaw.dans.common.lang.util.StateUtil;

public enum DatasetState {
    DRAFT, SUBMITTED, PUBLISHED, MAINTENANCE, DELETED;

    public static final StateUtil<DatasetState> UTIL = new StateUtil<DatasetState>(values());

    public static final int MASK_PASSED_SUBMISSION = UTIL.getBitMask(SUBMITTED, PUBLISHED, MAINTENANCE);

    public static boolean isPassedSubmission(DatasetState state) {
        if (state == null)
            return false;
        int mask = 1 << state.ordinal();
        return (MASK_PASSED_SUBMISSION & mask) == mask;
    }
}
