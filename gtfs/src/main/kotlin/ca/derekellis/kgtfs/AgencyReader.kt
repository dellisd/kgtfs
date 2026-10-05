package ca.derekellis.kgtfs

import ca.derekellis.kgtfs.csv.Agency
import java.io.Closeable

public interface AgencyReader : Closeable, Iterator<Agency>
