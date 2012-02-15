package local.r1j0.bugspot.repository;

import java.util.List;

public interface Repository {

    public static final char TYPE_ADDED = 'A';
    public static final char TYPE_DELETED = 'D';
    public static final char TYPE_MODIFIED = 'M';
    public static final char TYPE_REPLACED = 'R';


    List<LogEntries> checkout(long startRevision);


    List<LogEntries> checkout(long startRevision, long endRevision);

}
