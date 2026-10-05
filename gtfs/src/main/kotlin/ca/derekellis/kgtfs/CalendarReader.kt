package ca.derekellis.kgtfs

import ca.derekellis.kgtfs.csv.Calendar
import java.io.Closeable

public interface CalendarReader : Iterator<Calendar>, Closeable
