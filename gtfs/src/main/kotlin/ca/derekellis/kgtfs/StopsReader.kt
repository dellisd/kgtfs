package ca.derekellis.kgtfs

import ca.derekellis.kgtfs.csv.Stop
import java.io.Closeable

public interface StopsReader : Closeable, Iterator<Stop>
