package ca.derekellis.kgtfs

import ca.derekellis.kgtfs.csv.Shape
import java.io.Closeable

public interface ShapesReader : Iterator<Shape>, Closeable
