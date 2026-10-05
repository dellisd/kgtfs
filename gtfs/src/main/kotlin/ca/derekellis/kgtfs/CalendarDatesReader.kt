package ca.derekellis.kgtfs

import ca.derekellis.kgtfs.csv.CalendarDate
import java.io.Closeable

public interface CalendarDatesReader : Iterator<CalendarDate>, Closeable
