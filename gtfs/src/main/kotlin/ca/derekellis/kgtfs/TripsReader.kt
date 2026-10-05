package ca.derekellis.kgtfs

import ca.derekellis.kgtfs.csv.Trip
import java.io.Closeable

public interface TripsReader : Iterator<Trip>, Closeable
