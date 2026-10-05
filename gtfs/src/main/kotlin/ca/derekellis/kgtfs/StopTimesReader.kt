package ca.derekellis.kgtfs

import ca.derekellis.kgtfs.csv.StopTime
import java.io.Closeable

public interface StopTimesReader : Iterator<StopTime>, Closeable
