package ca.derekellis.kgtfs

import ca.derekellis.kgtfs.csv.Route
import java.io.Closeable

public interface RoutesReader : Iterator<Route>, Closeable
